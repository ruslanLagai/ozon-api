package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import ru.home.project.ozonapi.client.OzonApiClient
import ru.home.project.ozonapi.dto.delivery.DeliveryStatus
import ru.home.project.ozonapi.dto.delivery.response.DeliveryResponse
import ru.home.project.ozonapi.dto.stocks.response.GetStocksResponse
import ru.home.project.ozonapi.dto.supply.response.SupplyItemsResp
import ru.home.project.ozonapi.dto.supply.response.SupplyOrdersResp
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.entity.StockEntity
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.OzonSupplyRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.StockRepository
import ru.home.project.ozonapi.util.readResource
import java.time.LocalDate

/**
 * @author rlagay
 */
@ExtendWith(MockitoExtension::class)
class StocksServiceImplTest {

    private val ozonApiClient = mock<OzonApiClient>()
    private val ozonService = OzonServiceImpl(ozonApiClient)
    private val stockRepository = mock<StockRepository>()
    private val positionRepository = mock<PositionRepository>()
    private val chinaOrdersRepository = mock<ChinaOrdersRepository>()
    private val ozonSupplyRepository = mock<OzonSupplyRepository>()

    private val stocksService = StocksServiceImpl(ozonService, stockRepository, positionRepository, chinaOrdersRepository, ozonSupplyRepository)

    private val umbrella1 = PositionEntity(1, "Мини зонт черный", 448.92, 11.69, "1135684591", "0000015")
    private val umbrella2 = PositionEntity(2, "Мини зонт лавандовый", 448.92, 11.69, "1134671293", "0000009")
    private val umbrella3 = PositionEntity(3, "Мини зонт голубой", 448.92, 11.69, "1134740183", "0000013")
    private val umbrella4 = PositionEntity(4, "Мини зонт бежевый", 448.92, 11.69, "1134715033", "0000010")
    private val umbrella5 = PositionEntity(5, "Мини зонт розовый", 448.92, 11.69, "1134731178", "0000011")
    private val umbrella6 = PositionEntity(6, "Мини зонт серый", 448.92, 11.69, "1134733705", "0000012")
    private val stock1 = StockEntity(1, "Мини зонт черный", 4, "1135684591", "0000015")
    private val stock2 = StockEntity(2, "Мини зонт лавандовый", 0, "1134671293", "0000009")
    private val stock3 = StockEntity(3, "Мини зонт голубой", 0, "1134740183", "0000013")
    private val stock4 = StockEntity(4, "Мини зонт бежевый", 2, "1134715033", "0000010")
    private val orderEntity1 = ChinaOrderEntity(1, "supplier", mass = 34.0, stockCost = 10000.0, products = listOf(), orderDate = LocalDate.now())
    private val orderEntity2 = ChinaOrderEntity(2, "supplier", mass = 34.0, stockCost = 20000.0, products = listOf(), orderDate = LocalDate.now())

    @Test
    fun `test worth calculation - ozon + stock + order + deliveries - remove ozon supply goods from stock`() {
        val stock1 = StockEntity(1, "Мини зонт черный", 40, "1135684591", "0000015")
        val stock2 = StockEntity(2, "Мини зонт лавандовый", 40, "1134671293", "0000009")
        val stock3 = StockEntity(3, "Мини зонт голубой", 0, "1134740183", "0000013")
        val stock4 = StockEntity(4, "Мини зонт бежевый", 42, "1134715033", "0000010")

        val positions = listOf(umbrella1, umbrella2, umbrella3, umbrella4, umbrella5, umbrella6)
        val stocks = listOf(stock1, stock2, stock3, stock4)
        val stocksResp = readResource("stocks/stocks-response.json", GetStocksResponse::class.java)
        val deliveriesResp = readResource("deliveries/deliveries-response.json", DeliveryResponse::class.java)
        val orders = setOf(orderEntity1, orderEntity2)

        `when`(ozonApiClient.getStocks()).thenReturn(stocksResp.result.items)
        `when`(ozonApiClient.getSupplyOrders()).thenReturn(listOf())
        `when`(positionRepository.findAll()).thenReturn(positions)
        `when`(stockRepository.findAll()).thenReturn(stocks)
        `when`(stockRepository.getByOzonId("1135684591")).thenReturn(stock1)
        `when`(stockRepository.getByOzonId("1134671293")).thenReturn(stock2)
        `when`(stockRepository.getByOzonId("1134740183")).thenReturn(stock3)
        `when`(stockRepository.getByOzonId("1134715033")).thenReturn(stock4)

        `when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false)).thenReturn(orders)
        `when`(ozonApiClient.getDeliveriesByStatus(DeliveryStatus.delivering)).thenReturn(deliveriesResp.result)

        val result = stocksService.getStocks()

        assertEquals(2, result.orders.size)
        assertEquals(30000.0, result.stocksOnWayWorth)
        assertEquals(142328.49, result.stocksWorth)
        assertEquals(8751.59, result.deliveryWorth)
        assertEquals(13, result.products.size)
        verify(stockRepository).updateQuantityByOzonId("1135684591", 3)
        verify(stockRepository).updateQuantityByOzonId("1134671293", 9)
        verify(stockRepository).updateQuantityByOzonId("1134715033", 32)
    }

    @Test
    fun `test worth calculation - ozon + stock + deliveries`() {
        val positions = listOf(umbrella1, umbrella2, umbrella3, umbrella4, umbrella5, umbrella6)
        val stocks = listOf(stock1, stock2, stock3, stock4)
        val stocksResp = readResource("stocks/stocks-response.json", GetStocksResponse::class.java)
        val deliveriesResp = readResource("deliveries/deliveries-response.json", DeliveryResponse::class.java)

        `when`(ozonApiClient.getStocks()).thenReturn(stocksResp.result.items)
        `when`(ozonApiClient.getSupplyOrders()).thenReturn(listOf())
        `when`(ozonApiClient.getDeliveriesByStatus(DeliveryStatus.delivering)).thenReturn(deliveriesResp.result)

        `when`(positionRepository.findAll()).thenReturn(positions)
        `when`(stockRepository.findAll()).thenReturn(stocks)
        `when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false)).thenReturn(setOf())

        val result = stocksService.getStocks()

        assertEquals(0, result.orders.size)
        assertEquals(0.0, result.stocksOnWayWorth)
        assertEquals(88897.73, result.stocksWorth)
        assertEquals(13, result.products.size)
        assertEquals(8751.59, result.deliveryWorth)
        assertEquals(10, result.deliveries.size)

    }

    @Test
    fun `test worth calculation - ozon + stock + order + deliveries`() {
        val positions = listOf(umbrella1, umbrella2, umbrella3, umbrella4, umbrella5, umbrella6)
        val stocks = listOf(stock1, stock2, stock3, stock4)
        val stocksResp = readResource("stocks/stocks-response.json", GetStocksResponse::class.java)
        val deliveriesResp = readResource("deliveries/deliveries-response.json", DeliveryResponse::class.java)
        val orders = setOf(orderEntity1, orderEntity2)

        `when`(ozonApiClient.getStocks()).thenReturn(stocksResp.result.items)
        `when`(ozonApiClient.getSupplyOrders()).thenReturn(listOf())
        `when`(positionRepository.findAll()).thenReturn(positions)
        `when`(stockRepository.findAll()).thenReturn(stocks)

        `when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false)).thenReturn(orders)
        `when`(ozonApiClient.getDeliveriesByStatus(DeliveryStatus.delivering)).thenReturn(deliveriesResp.result)

        val result = stocksService.getStocks()

        assertEquals(2, result.orders.size)
        assertEquals(30000.0, result.stocksOnWayWorth)
        assertEquals(88897.73, result.stocksWorth)
        assertEquals(13, result.products.size)
        verify(stockRepository, times(0)).updateQuantityByOzonId(anyString(), anyInt())
    }
}