package ru.home.project.ozonapi.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.dto.request.ProductRequest
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.ChinaStockEntity
import ru.home.project.ozonapi.entity.CostPriceEntity
import ru.home.project.ozonapi.entity.StockEntity
import ru.home.project.ozonapi.exception.NoOrderException
import ru.home.project.ozonapi.exception.NoPositionsException
import ru.home.project.ozonapi.exception.NoProductsException
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.StockRepository
import ru.home.project.ozonapi.service.OrdersService
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import kotlin.jvm.optionals.getOrNull

/**
 * @author rlagay
 */
@Service
class ChinaOrdersService(
    val chinaOrdersRepository: ChinaOrdersRepository,
    val positionRepository: PositionRepository,
    val stockRepository: StockRepository
) : OrdersService {

    private val log: Logger = LoggerFactory.getLogger(ChinaOrdersService::class.java)

    override fun saveNewOrder(supplier: String, stockWorthRub: Double, number: String?, products: List<ProductRequest>) {
        val stockEntities = ArrayList<ChinaStockEntity>()
        products.forEach {
            val position = positionRepository.getPositionEntityByArtikul(it.artikul)
            if (position == null) {
                log.warn("No position found for {}", it.artikul)
                throw NoPositionsException()
            }
            val stockItem = ChinaStockEntity(name = position.name, quantity = it.quantity, ozonId = position.ozonId,
                artikul = it.artikul, priceRub = it.price ?: 0.0)
            stockEntities.add(stockItem)
        }

        val entity = ChinaOrderEntity(supplier = supplier, stockCost = stockWorthRub, number = number, products = stockEntities,
            orderDate = LocalDate.now())
        chinaOrdersRepository.save(entity)
    }

    @Transactional
    override fun addDelivery(orderId: Long, deliveryCost: Double, mass: Double, volume: Double?) {
        val orderEntity = chinaOrdersRepository.findById(orderId).getOrNull() ?: throw NoOrderException("Не найдена поставка с id '${orderId}'")

        orderEntity.apply {
            orderEntity.mass = mass
            orderEntity.volume = volume ?: 0.0
            delivered = true
            deliveryDate = LocalDate.now()
            orderEntity.deliveryCost = deliveryCost
        }
        chinaOrdersRepository.save(orderEntity)
        if (orderEntity.products.isEmpty()) {
            log.warn("China order has no product data")
            throw NoProductsException("No products in china order '${orderEntity.id}'")
        }

        val products = orderEntity.products
        val productsCount = products.sumOf { it.quantity }
        val stockCost = orderEntity.stockCost
        // При добавлении данных о доставке, сохраняем записи о себестоимости для каждой позиции в поставке.
        products.forEach {
            val costPriceEntity = CostPriceEntity(
                initialQuantity = it.quantity,
                leftQuantity = it.quantity,
                ozonId = it.ozonId,
                supplyDate = LocalDate.now(),
                costPrice = BigDecimal.valueOf((deliveryCost + stockCost) / productsCount).setScale(2, RoundingMode.HALF_UP).toDouble(),
                crossDoc = 0.0,
                fulfilment = 0.0,
                position = positionRepository.getPositionEntityByOzonId(it.ozonId),
                chinaOrder = orderEntity,
                transactions = ArrayList()
            )
            orderEntity.costPriceEntities.add(costPriceEntity)
        }

        products.map {
            val position = positionRepository.getPositionEntityByArtikul(it.artikul)
            if (position == null) {
                log.warn("No position found for {}", it.artikul)
                throw NoPositionsException()
            }
            StockEntity(name = position.name, artikul = position.artikul, ozonId = position.ozonId, quantity = it.quantity, yandexArtikul = position.yandexArtikul)
        }.forEach {
            val existed = stockRepository.getByOzonId(it.ozonId)
            if (existed == null) {
                stockRepository.save(it)
            } else {
                stockRepository.updateQuantityByOzonId(it.ozonId, it.quantity + existed.quantity)
            }
        }
    }
}