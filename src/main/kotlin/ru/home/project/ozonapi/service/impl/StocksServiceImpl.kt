package ru.home.project.ozonapi.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.dto.delivery.Delivery
import ru.home.project.ozonapi.dto.delivery.DeliveryStatus
import ru.home.project.ozonapi.dto.response.StocksResponse
import ru.home.project.ozonapi.dto.supply.response.SupplyItem
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.OzonSupplyEntity
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.exception.InvalidStocksException
import ru.home.project.ozonapi.exception.NoPositionsException
import ru.home.project.ozonapi.model.Product
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.OzonSupplyRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.StockRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.StocksService
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author rlagay
 */
@Service
class StocksServiceImpl(
    val ozonService: OzonService,
    val stockRepository: StockRepository,
    val positionRepository: PositionRepository,
    val chinaOrdersRepository: ChinaOrdersRepository,
    val ozonSupplyRepository: OzonSupplyRepository
) : StocksService {

    private val log: Logger = LoggerFactory.getLogger(StocksServiceImpl::class.java)

    private val statuses = setOf(DeliveryStatus.delivering, DeliveryStatus.awaiting_deliver, DeliveryStatus.awaiting_packaging)

    override fun getStocks(): StocksResponse {
        val positions = positionRepository.findAll()
        if (positions.isNullOrEmpty()) {
            log.error("No positions found")
            throw NoPositionsException()
        }

        val stocks = getProductStocks(positions)
        var stocksWorth = stocks.values.sumOf { it.totalStock * (it.costPrice + it.addCost) }
        stocksWorth = BigDecimal(stocksWorth).setScale(2, RoundingMode.HALF_UP).toDouble()

        val orders = getOrders()
        var stocksOnWayWorth = orders.second.sumOf { it.stockCost }
        stocksOnWayWorth = BigDecimal(stocksOnWayWorth).setScale(2, RoundingMode.HALF_UP).toDouble()

        val deliveries = getOrdersInOzonDelivery(positions)
        var deliveriesWorth = deliveries.sumOf { it.totalStock * (it.costPrice + it.addCost) }
        deliveriesWorth = BigDecimal(deliveriesWorth).setScale(2, RoundingMode.HALF_UP).toDouble()
        val deliveriesByArtikul = mergeProducts(deliveries, positions)

        return StocksResponse(products = stocks, stocksWorth = stocksWorth, stocksOnWayWorth = stocksOnWayWorth,
            orders = orders.second, error = orders.first, deliveryWorth = deliveriesWorth, deliveries = deliveriesByArtikul)
    }

    /**
     * Получение товаров в доставке
     */
    private fun getOrdersInOzonDelivery(positions: List<PositionEntity>): List<Product> {
        val deliveries = ArrayList<Delivery>()
        statuses.forEach {
            deliveries.addAll(ozonService.getDeliveryByStatus(it))
        }

        if (deliveries.isEmpty()) {
            return listOf()
        }
        val products = ArrayList<Product>()
        deliveries.forEach {
            it.products.forEach { deliveryProduct ->
                val position = positions.firstOrNull { position -> position.ozonId == deliveryProduct.sku }
                if (position == null) {
                    log.warn("Position is not found for delivery, sku {}", deliveryProduct.sku)
                } else {
                    val costPrice = position.costPrice
                    val addCosts = position.additionalCost
                    val product = Product(costPrice = costPrice, addCost = addCosts, totalStock = deliveryProduct.quantity, name = deliveryProduct.name,
                        artikul = deliveryProduct.artikul, sku = deliveryProduct.sku)
                    products.add(product)
                }
            }
        }
        return products
    }

    /**
     * Получение поставок из Китая
     */
    private fun getOrders(): Pair<String, Set<ChinaOrderEntity>> {
        var error = ""
        val orders = chinaOrdersRepository.getChinaOrderEntityByDelivered(false)
        if (orders.isEmpty()) {
            log.debug("No orders found")
            return Pair(error, setOf())
        }
        if (orders.any { it.stockCost == 0.0 }) {
            log.warn("No goods price")
            error = "Не заполнены данные по стоимости товаров в поставке из Китая"
        }
        return Pair(error, orders)

    }

    /**
     * Получение остатков товаров, включая товары в пути на склад озон (кросс док)
     */
    private fun getProductStocks(positions: List<PositionEntity>): Map<String, Product> {
        val stocks = ArrayList<Product>()

        // Остатки на озоне
        ozonService.getStockItems("key")
            .filter { it.totalStock != 0 }
            .forEach { stocks.add(it) }

        // Ограничение!! в поставке должны быть одна поставка (через ВРЦ не создавать!)
        var subtracted = false
        var orderId = 0
        ozonService.getSupplyOrders()
            .asSequence()
            .map {
                val supply = ozonSupplyRepository.getOzonSupplyEntityByOrderId(it.orderId)
                orderId = supply?.orderId ?: it.orderId
                if (supply != null && supply.subtracted) {
                    subtracted = true
                }
                if (!subtracted) {
                    // отмечаем поставку как учтенную
                    val supplyEntity = ozonSupplyRepository.getOzonSupplyEntityByOrderId(orderId)
                    if (supplyEntity != null) {
                        ozonSupplyRepository.updateByOrderId(orderId)
                    } else {
                        ozonSupplyRepository.save(OzonSupplyEntity(orderId = orderId, subtracted = true))
                    }
                }
                ozonService.getSupplyItemsInOrder(it.orderId)
            }
            .flatMap { it.asSequence() }
            .map {
                if (!subtracted) {
                    subtractFromStock(it)
                }
                Product(name = it.name, sku = it.sku.toString(), artikul = it.artikul, totalStock = it.quantity)
            }
            .filter { it.totalStock != 0 }
            .forEach { stocks.add(it) }

        // Остатки
        stockRepository.findAll()
            .filter { it.quantity != 0 }
            .map { Product(sku = it.ozonId, artikul = it.artikul, name = it.name, totalStock = it.quantity) }
            .forEach { stocks.add(it) }

        return mergeProducts(stocks, positions)
    }

    private fun subtractFromStock(supplyItem: SupplyItem) {
        val entity = stockRepository.getByOzonId(supplyItem.sku.toString())
        if (entity == null) {
            log.warn("Product in ozon supply is absent in own stock. TO BE CHECKED, {}!", supplyItem.name)
        } else {
            val quantity = entity.quantity - supplyItem.quantity
            if (quantity < 0) {
                throw InvalidStocksException("Quantity in own stock will be < 0")
            }
            stockRepository.updateQuantityByOzonId(supplyItem.sku.toString(), quantity)
        }
    }

    private fun mergeProducts(products: List<Product>, positions: List<PositionEntity>) =
        products.groupBy { it.artikul }
            .filter { it.value.isNotEmpty() }
            .mapValues {
                val position = positions.firstOrNull { item -> item.artikul == it.key }
                val name = position?.name ?: ""
                val artikul = it.key
                val sku = position?.ozonId ?: ""
                val totalStock = it.value.sumOf { item -> item.totalStock }
                val fboStock = it.value.sumOf { item -> item.fboStock }
                val fbsStock = it.value.sumOf { item -> item.fbsStock }
                val costPrice = position?.costPrice ?: 0.0
                val addCosts = position?.additionalCost ?: 0.0

                Product(
                    name = name, sku = sku, artikul = artikul, totalStock = totalStock, fboStock = fboStock,
                    fbsStock = fbsStock, costPrice = costPrice, addCost = addCosts
                )
            }

}