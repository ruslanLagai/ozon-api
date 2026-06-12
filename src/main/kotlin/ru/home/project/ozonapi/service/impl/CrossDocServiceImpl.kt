package ru.home.project.ozonapi.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.dto.supply.request.SupplyState
import ru.home.project.ozonapi.entity.OzonSupplyOrderIdEntity
import ru.home.project.ozonapi.exception.InvalidChineOrderException
import ru.home.project.ozonapi.exception.NoSupplyItemException
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.service.CrossDocService
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.util.ALLOWED_DEFECT_SHARE
import kotlin.math.floor

@Service
class CrossDocServiceImpl(
    private val chinaOrdersRepository: ChinaOrdersRepository,
    private val ozonService: OzonService
) : CrossDocService {

    companion object {
        private val log = LoggerFactory.getLogger(CrossDocServiceImpl::class.java)
    }

    @Transactional
    override fun linkWithOrders(orderId: Long) {
        chinaOrdersRepository.findByIdWithOzonSupplyIds(orderId).ifPresentOrElse( { order ->
            val products = order.products
            val productsMap = HashMap<Long, Int>()
            val ozonSupplyIds = mutableSetOf<Int> ()
            val skusToBeProcessed: Set<Long> = products.map { it.ozonId }.map { it.toLong() }.toSet()

            val states =  listOf(
                SupplyState.ACCEPTED_AT_SUPPLY_WAREHOUSE, SupplyState.IN_TRANSIT,
                SupplyState.ACCEPTANCE_AT_STORAGE_WAREHOUSE, SupplyState.REPORTS_CONFIRMATION_AWAITING,
                SupplyState.REPORT_REJECTED, SupplyState.REJECTED_AT_SUPPLY_WAREHOUSE,
                SupplyState.COMPLETED)

            ozonService.getSupplyOrders(states).stream().forEach {
                Thread.sleep(1000)
                val supplyBundleItems = ozonService.getSupplyItemsInOrder(listOf(it))
                    .filter { item -> skusToBeProcessed.contains(item.sku) }
                if (skusToBeProcessed.containsAll(supplyBundleItems.map { item -> item.sku })) {
                    for (supplyBundleItem in supplyBundleItems) {

                        val sku = supplyBundleItem.sku.toString()
                        var currentQuantity = productsMap.computeIfAbsent(supplyBundleItem.sku, { 0 })
                        val product = products.firstOrNull { product -> product.ozonId == sku }
                        if (product == null) {
                            throw NoSupplyItemException("Продукт с sku ${supplyBundleItem.sku} найден в поставках, но не найден в заказе")
                        }
                        val neededQuantity = product.quantity

                        if (currentQuantity + supplyBundleItem.quantity <= neededQuantity) {
                            if (supplyBundleItem.quantity <= neededQuantity - currentQuantity) {
                                currentQuantity += supplyBundleItem.quantity
                                productsMap[supplyBundleItem.sku] = currentQuantity
                                ozonSupplyIds.add(it)
                            } else {
                                // Слишном много товаров в заявке
                                continue
                            }
                        }
                    }
                }
            }

            // найдены не все sku
            if (!productsMap.keys.containsAll(skusToBeProcessed)) {
                val missed = skusToBeProcessed - productsMap.keys
                throw NoSupplyItemException("Продукты не найдены в поставках, " +
                        "sku: ${missed.map { it.toString() }.joinToString()}")
            }
            // найдены все sku, но не в нужном количестве (допустимое количество брака - 10%)
            for (product in products) {
                val foundQuantity = productsMap[product.ozonId.toLong()] ?: 0
                val minAcceptedQuantity = product.quantity - floor(product.quantity * ALLOWED_DEFECT_SHARE).toInt()
                if (foundQuantity < minAcceptedQuantity) {
                    throw NoSupplyItemException("Продукт с sku ${product.ozonId} найден в поставках, но в недостаточном количестве. " +
                            "Найдено: $foundQuantity, нужно: ${product.quantity}")
                }
            }

            val supplyIds = ozonService.getSupplyOrderIds(ozonSupplyIds)
            val existingIds = order.ozonSupplyOrderIds.map { it.orderId }.toSet()
            val ozonSupplyEntities = supplyIds.stream()
                .filter { !existingIds.contains(it.first) }
                .map { OzonSupplyOrderIdEntity(orderId = it.first, chinaOrderEntity = order, bundleId = it.second) }
                .toList()
            order.ozonSupplyOrderIds.addAll(ozonSupplyEntities)

        }, { throw InvalidChineOrderException("Поставка с id '${orderId}' не найдена") })
    }
}