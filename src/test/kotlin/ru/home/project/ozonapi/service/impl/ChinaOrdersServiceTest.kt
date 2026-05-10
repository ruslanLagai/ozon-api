package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.ChinaStockEntity
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.entity.StockEntity
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.StockRepository
import java.time.LocalDate
import java.util.*

/**
 * @author rlagay
 */
@ExtendWith(MockitoExtension::class)
class ChinaOrdersServiceTest {

    private val chinaOrdersRepository = mock<ChinaOrdersRepository>()
    private val positionRepository = mock<PositionRepository>()
    private val stockRepository = mock<StockRepository>()
    private val chinaOrdersService = ChinaOrdersService(chinaOrdersRepository, positionRepository, stockRepository)

    @Test
    fun `test add delivery `() {
        val today = LocalDate.now()
        val products = arrayListOf(
            ChinaStockEntity(name = "Зонт 1", quantity = 50, artikul = "000001", ozonId = "1", priceRub = 25.0),
            ChinaStockEntity(name = "Зонт 2", quantity = 40, artikul = "000002", ozonId = "2", priceRub = 75.0)
        )
        val orderEntity = ChinaOrderEntity(supplier = "test", products = ArrayList(products), orderDate = today, stockCost = 100.0)
        val positionEntityOne = PositionEntity(name = "Зонт 1", artikul = "000001", ozonId = "1", additionalCost = 0.0, costPrice = 1.0)
        val positionEntityTwo = PositionEntity(name = "Зонт 2", artikul = "000002", ozonId = "2", additionalCost = 0.0, costPrice = 2.0)
        `when`(chinaOrdersRepository.findById(any())).thenReturn(Optional.of(orderEntity))
        `when`(positionRepository.getPositionEntityByArtikul("000001")).thenReturn(positionEntityOne)
        whenever(positionRepository.getPositionEntityByOzonId("1")).thenReturn(positionEntityOne)
        whenever(positionRepository.getPositionEntityByOzonId("2")).thenReturn(positionEntityTwo)
        `when`(positionRepository.getPositionEntityByArtikul("000002")).thenReturn(positionEntityTwo)
        `when`(stockRepository.getByOzonId("1")).thenReturn(StockEntity(name = "Зонт 1", artikul = "000001", quantity = 20, ozonId = "1", yandexArtikul = ""))

        chinaOrdersService.addDelivery(1, 100.0, 20.0, 0.0)

        val orderCaptor = argumentCaptor<ChinaOrderEntity>()
        verify(chinaOrdersRepository).save(orderCaptor.capture())

        val savedOrder = orderCaptor.firstValue
        val savedCostPrices = savedOrder.costPriceEntities.sortedBy { it.ozonId }

        assertAll(
            { assertEquals("test", savedOrder.supplier) },
            { assertEquals(today, savedOrder.orderDate) },
            { assertTrue(savedOrder.delivered) },
            { assertEquals(today, savedOrder.deliveryDate) },
            { assertEquals(20.0, savedOrder.mass) },
            { assertEquals(0.0, savedOrder.volume) },
            { assertEquals(100.0, savedOrder.deliveryCost) },
            { assertEquals(products, savedOrder.products) },
            { assertEquals(2, savedCostPrices.size) },
            { assertEquals(savedOrder, savedCostPrices[0].chinaOrder) },
            { assertEquals(savedOrder, savedCostPrices[1].chinaOrder) },
            { assertEquals(50, savedCostPrices[0].initialQuantity) },
            { assertEquals(50, savedCostPrices[0].leftQuantity) },
            { assertEquals("1", savedCostPrices[0].ozonId) },
            { assertEquals(2.22, savedCostPrices[0].costPrice) },
            { assertEquals(today, savedCostPrices[0].supplyDate) },
            { assertEquals(0.0, savedCostPrices[0].crossDoc) },
            { assertEquals(0.0, savedCostPrices[0].fulfilment) },
            { assertEquals(positionEntityOne, savedCostPrices[0].position) },
            { assertTrue(savedCostPrices[0].transactions.isEmpty()) },
            { assertNotNull(savedCostPrices[0].transactions) },
            { assertEquals(40, savedCostPrices[1].initialQuantity) },
            { assertEquals(40, savedCostPrices[1].leftQuantity) },
            { assertEquals("2", savedCostPrices[1].ozonId) },
            { assertEquals(2.22, savedCostPrices[1].costPrice) },
            { assertEquals(today, savedCostPrices[1].supplyDate) },
            { assertEquals(0.0, savedCostPrices[1].crossDoc) },
            { assertEquals(0.0, savedCostPrices[1].fulfilment) },
            { assertEquals(positionEntityTwo, savedCostPrices[1].position) },
            { assertTrue(savedCostPrices[1].transactions.isEmpty()) },
            { assertNotNull(savedCostPrices[1].transactions) }
        )

        verify(stockRepository).save(StockEntity(name = "Зонт 2", artikul = "000002", quantity = 40, ozonId = "2", yandexArtikul = ""))
        verify(stockRepository).updateQuantityByOzonId("1", 20 + 50)
    }
}