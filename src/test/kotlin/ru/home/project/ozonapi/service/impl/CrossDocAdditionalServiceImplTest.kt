package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.ChinaStockEntity
import ru.home.project.ozonapi.entity.CostPriceEntity
import ru.home.project.ozonapi.entity.OzonSupplyOrderIdEntity
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.repository.OzonSupplyOrderIdRepository
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class CrossDocAdditionalServiceImplTest {

    private val ozonSupplyOrderIdRepository = mock<OzonSupplyOrderIdRepository>()
    private val service = CrossDocAdditionalService(ozonSupplyOrderIdRepository)

    @Test
    fun `updates cross doc for every cost price entity`() {
        val order = chinaOrder(
            products = listOf(
                chinaProduct(quantity = 2),
                chinaProduct(quantity = 4)
            ),
            costPrices = listOf(
                costPriceEntity(crossDoc = 1.0),
                costPriceEntity(crossDoc = 1.0)
            )
        )
        stubSupply(orderId = 100L, order = order)

        service.updateCostPrice(orderId = 100L, additionalServices = 10.0)

        assertEquals(listOf(2.67, 2.67), order.costPriceEntities.map { it.crossDoc })
        verify(ozonSupplyOrderIdRepository).findByOrderId(100L)
    }

    @Test
    fun `rounds cross doc value to two digits after decimal`() {
        val order = chinaOrder(
            products = listOf(chinaProduct(quantity = 2)),
            costPrices = listOf(costPriceEntity(crossDoc = 0.34))
        )
        stubSupply(orderId = 200L, order = order)

        service.updateCostPrice(orderId = 200L, additionalServices = 0.33)

        assertEquals(0.51, order.costPriceEntities.single().crossDoc)
        verify(ozonSupplyOrderIdRepository).findByOrderId(200L)
    }

    @Test
    fun `does nothing when supply order id is not found`() {
        whenever(ozonSupplyOrderIdRepository.findByOrderId(300L)).thenReturn(Optional.empty())

        assertDoesNotThrow {
            service.updateCostPrice(orderId = 300L, additionalServices = 15.0)
        }

        verify(ozonSupplyOrderIdRepository).findByOrderId(300L)
    }

    private fun stubSupply(orderId: Long, order: ChinaOrderEntity) {
        whenever(ozonSupplyOrderIdRepository.findByOrderId(orderId)).thenReturn(
            Optional.of(OzonSupplyOrderIdEntity(orderId = orderId, chinaOrderEntity = order))
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

    private fun costPriceEntity(crossDoc: Double): CostPriceEntity {
        val id = UUID.randomUUID()
        return CostPriceEntity(
            id = id,
            leftQuantity = 10,
            initialQuantity = 10,
            supplyDate = LocalDate.now(),
            costPrice = 100.0,
            crossDoc = crossDoc,
            fulfilment = 0.0,
            ozonId = "ozon-$id",
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

