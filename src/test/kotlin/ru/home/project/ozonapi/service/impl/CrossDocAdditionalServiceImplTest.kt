package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.home.project.ozonapi.dto.supply.response.SupplyBundleItem
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.ChinaStockEntity
import ru.home.project.ozonapi.entity.CostPriceEntity
import ru.home.project.ozonapi.entity.CrossDocTransactionEntity
import ru.home.project.ozonapi.entity.OzonSupplyOrderIdEntity
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.repository.CrossDocTransactionEntityRepository
import ru.home.project.ozonapi.repository.OzonSupplyOrderIdRepository
import ru.home.project.ozonapi.service.OzonService
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class CrossDocAdditionalServiceImplTest {

    private val ozonSupplyOrderIdRepository = mock<OzonSupplyOrderIdRepository>()
    private val ozonService = mock<OzonService>()
    private val crossDocTransactionEntityRepository = mock<CrossDocTransactionEntityRepository>()
    private val service = CrossDocAdditionalService(ozonSupplyOrderIdRepository, ozonService, crossDocTransactionEntityRepository)

    @Test
    fun `updates cross doc for every cost price entity`() {
        val order1 = chinaOrder(
            products = listOf(
                chinaProduct(quantity = 1),
                chinaProduct(quantity = 3)
            ),
            costPrices = listOf(
                costPriceEntity(crossDoc = 1.0, "1"),
                costPriceEntity(crossDoc = 1.0, "2")
            )
        )
        val order2 = chinaOrder(
            products = listOf(
                chinaProduct(quantity = 1),
                chinaProduct(quantity = 1)
            ),
            costPrices = listOf(
                costPriceEntity(crossDoc = 1.0, "1"),
                costPriceEntity(crossDoc = 1.0, "2")
            )
        )
        whenever(ozonService.getSupplyItemsByBundleIds(setOf("bundleId-1", "bundleId-2"))).thenReturn(
            listOf(
                SupplyBundleItem(iconPath = null, sku = 1L, artikul = "", name = "", quantity = 4),
                SupplyBundleItem(iconPath = null, sku = 2L, artikul = "", name = "", quantity = 2)
            )
        )
        whenever(ozonSupplyOrderIdRepository.findByOrderId(100L)).thenReturn(
            listOf(
                OzonSupplyOrderIdEntity(orderId = 100L, chinaOrderEntity = order1, bundleId = "bundleId-1"),
                OzonSupplyOrderIdEntity(orderId = 100L, chinaOrderEntity = order2, bundleId = "bundleId-2"))
        )
        whenever(crossDocTransactionEntityRepository.findByOrderId("100")).thenReturn(Optional.empty())

        service.updateCostPrice(orderId = 100L, additionalServices = 10.0)

        assertEquals(listOf(2.67, 2.67), order1.costPriceEntities.map { it.crossDoc })
        assertEquals(listOf(2.67, 2.67), order2.costPriceEntities.map { it.crossDoc })

        verify(ozonSupplyOrderIdRepository).findByOrderId(100L)
        verify(crossDocTransactionEntityRepository).save(
            CrossDocTransactionEntity(
                orderId = "100"
            )
        )
    }

    @Test
    fun `rounds cross doc value to two digits after decimal`() {
        val order = chinaOrder(
            products = listOf(chinaProduct(quantity = 2)),
            costPrices = listOf(costPriceEntity(crossDoc = 0.34, id = "1"))
        )
        stubSupply(orderId = 200L, order = order)
        whenever(ozonService.getSupplyItemsByBundleIds(setOf(""))).thenReturn(
            listOf(
                SupplyBundleItem(iconPath = null, sku = 1L, artikul = "", name = "", quantity = 2)
            )
        )

        service.updateCostPrice(orderId = 200L, additionalServices = 0.33)

        assertEquals(0.51, order.costPriceEntities.single().crossDoc)
        verify(ozonSupplyOrderIdRepository).findByOrderId(200L)
    }

    @Test
    fun `does nothing when supply order id is not found`() {
        whenever(ozonSupplyOrderIdRepository.findByOrderId(300L)).thenReturn(emptyList())

        assertDoesNotThrow {
            service.updateCostPrice(orderId = 300L, additionalServices = 15.0)
        }

        verify(ozonSupplyOrderIdRepository).findByOrderId(300L)
    }

    private fun stubSupply(orderId: Long, order: ChinaOrderEntity) {
        whenever(ozonSupplyOrderIdRepository.findByOrderId(orderId)).thenReturn(
            listOf(OzonSupplyOrderIdEntity(orderId = orderId, chinaOrderEntity = order, bundleId = ""))
        )
    }

    private fun chinaOrder(
        products: List<ChinaStockEntity>,
        costPrices: List<CostPriceEntity>
    ): ChinaOrderEntity {
        val order = ChinaOrderEntity(
            id = 1L,
            supplier = "supplier",
            orderDate = LocalDate.now(),
            stockCost = 1000.0,
            products = products.toMutableList(),
            costPriceEntities = costPrices.toMutableList(),
            ozonSupplyOrderIds = mutableSetOf()
        )
        order.products.forEach { it.chinaOrderEntity = order }
        order.costPriceEntities.forEach { it.chinaOrder = order }
        return order
    }

    private fun chinaProduct(quantity: Int): ChinaStockEntity =
        ChinaStockEntity(
            name = "product-$quantity",
            quantity = quantity,
            ozonId = "ozon-$quantity",
            artikul = "art-$quantity",
            priceRub = 100.0
        )

    private fun costPriceEntity(crossDoc: Double, id: String): CostPriceEntity {
        return CostPriceEntity(
            id = UUID.randomUUID(),
            leftQuantity = 10,
            initialQuantity = 10,
            supplyDate = LocalDate.now(),
            costPrice = 100.0,
            crossDoc = crossDoc,
            fulfilment = 0.0,
            ozonId = id,
            position = PositionEntity(
                id = 10L,
                name = "position-$id",
                costPrice = 100.0,
                additionalCost = 0.0,
                ozonId = "position-ozon-$id",
                artikul = "art-$id",
                costPriceEntity = mutableListOf()
            ),
            chinaOrder = ChinaOrderEntity(
                id = 99L,
                supplier = "placeholder",
                orderDate = LocalDate.now(),
                stockCost = 0.0,
                products = mutableListOf()
            ),
            transactions = mutableListOf()
        )
    }
}

