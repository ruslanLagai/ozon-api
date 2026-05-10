package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import ru.home.project.ozonapi.client.OzonApiClient
import ru.home.project.ozonapi.dto.delivery.DeliveryStatus
import ru.home.project.ozonapi.dto.delivery.response.DeliveryResponse
import ru.home.project.ozonapi.dto.stocks.response.StocksResponse
import ru.home.project.ozonapi.dto.supply.response.GetSupplyOrdersResp
import ru.home.project.ozonapi.dto.supply.response.SupplyBundlesResp
import ru.home.project.ozonapi.dto.supply.response.SupplyOrdersResp
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.OzonSupplyEntity
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
class StocksServiceImplTest {

    private val ozonApiClient = mock<OzonApiClient>()
    private val ozonService = OzonServiceImpl(ozonApiClient)
    private val stockRepository = mock<StockRepository>()
    private val positionRepository = mock<PositionRepository>()
    private val chinaOrdersRepository = mock<ChinaOrdersRepository>()
    private val ozonSupplyRepository = mock<OzonSupplyRepository>()
    private val stocksService = StocksServiceImpl(ozonService, stockRepository, positionRepository, chinaOrdersRepository)

    private val umbrella1 = PositionEntity(1, "Мини зонт черный", 448.92, 11.69, "1135684591", "0000015")
    private val umbrella2 = PositionEntity(2, "Мини зонт лавандовый", 448.92, 11.69, "1134671293", "0000009")
    private val umbrella3 = PositionEntity(3, "Мини зонт голубой", 448.92, 11.69, "1134740183", "0000013")
    private val umbrella4 = PositionEntity(4, "Мини зонт бежевый", 448.92, 11.69, "1134715033", "0000010")
    private val umbrella5 = PositionEntity(5, "Мини зонт розовый", 448.92, 11.69, "1134731178", "0000011")
    private val umbrella6 = PositionEntity(6, "Мини зонт серый", 448.92, 11.69, "1134733705", "0000012")
    private val spongeHolder = PositionEntity(7, "Держатель для губки", 120.0, 0.0, "1111", "0000005", yandexArtikul = "0000005")
    private val hanger = PositionEntity(8, "Крючок", 100.0, 0.0, "2222", "0000029", yandexArtikul = "0000029")
    private val beachBag = PositionEntity(9, "Сумка пляжная", 340.0, 0.0, "1589765459", "0000032", yandexArtikul = "0000032")

    private val stock1 = StockEntity(1, "Мини зонт черный", 4, "1135684591", "0000015", yandexArtikul = "")
    private val stock2 = StockEntity(2, "Мини зонт лавандовый", 0, "1134671293", "0000009", yandexArtikul = "")
    private val stock3 = StockEntity(3, "Мини зонт голубой", 0, "1134740183", "0000013", yandexArtikul = "")
    private val stock4 = StockEntity(4, "Мини зонт бежевый", 2, "1134715033", "0000010", yandexArtikul = "")
    private val orderEntity1 = ChinaOrderEntity(1, "supplier", mass = 34.0, stockCost = 10000.0, products = arrayListOf(), orderDate = LocalDate.now())
    private val orderEntity2 = ChinaOrderEntity(2, "supplier", mass = 34.0, stockCost = 20000.0, products = arrayListOf(), orderDate = LocalDate.now())

    @Test
    fun `test worth calculation - ozon + yandex + stock + order + deliveries - remove ozon supply goods from stock`() {
        val stock1 = StockEntity(1, "Мини зонт черный", 40, "1135684591", "0000015", yandexArtikul = "")
        val stock2 = StockEntity(2, "Мини зонт лавандовый", 40, "1134671293", "0000009", yandexArtikul = "")
        val stock3 = StockEntity(3, "Мини зонт голубой", 0, "1134740183", "0000013", yandexArtikul = "")
        val stock4 = StockEntity(4, "Мини зонт бежевый", 42, "1134715033", "0000010", yandexArtikul = "")

        val positions = listOf(umbrella1, umbrella2, umbrella3, umbrella4, umbrella5, umbrella6, hanger, spongeHolder, beachBag)
        val stocks = listOf(stock1, stock2, stock3, stock4)
        val stocksResp = readResource("stocks/stocks-response.json", StocksResponse::class.java)
        val deliveriesResp = readResource("deliveries/deliveries-response.json", DeliveryResponse::class.java)
        val supplyOrders = readResource("supply/supply-list.json", SupplyOrdersResp::class.java)
        val getSupplyOrders = readResource("supply/get-supply-orders.json", GetSupplyOrdersResp::class.java)
        val supplyBundles = readResource("supply/supply-bundles.json", SupplyBundlesResp::class.java)

        val orders = setOf(orderEntity1, orderEntity2)

        `when`(ozonApiClient.getStocks()).thenReturn(stocksResp.items)
        `when`(ozonApiClient.getSupplyOrderList()).thenReturn(supplyOrders.supplyOrders)
        `when`(ozonApiClient.getSupplyOrders(listOf(28439982, 28439770))).thenReturn(getSupplyOrders.orders)
        `when`(ozonApiClient.getSupplyOrderBundle(listOf("0190c509-5d53-765b-bb12-9e93fe7f2a86", "0190c507-0601-7031-b715-bcaca9670d04")))
            .thenReturn(supplyBundles.items)
        `when`(positionRepository.findAll()).thenReturn(positions)
        `when`(stockRepository.findAll()).thenReturn(stocks)
        `when`(stockRepository.getByOzonId("1135684591")).thenReturn(stock1)
        `when`(stockRepository.getByOzonId("1134671293")).thenReturn(stock2)
        `when`(stockRepository.getByOzonId("1134740183")).thenReturn(stock3)
        `when`(stockRepository.getByOzonId("1134715033")).thenReturn(stock4)

        `when`(ozonSupplyRepository.getOzonSupplyEntityByOrderId(28439982)).thenReturn(null)
        `when`(ozonSupplyRepository.getOzonSupplyEntityByOrderId(28439770)).thenReturn(OzonSupplyEntity(orderId = 28439770, subtracted = false))
        `when`(ozonSupplyRepository.getOzonSupplyEntityByOrderId(28439655)).thenReturn(OzonSupplyEntity(orderId = 28439655, subtracted = true))
        `when`(ozonSupplyRepository.getOzonSupplyEntityByOrderId(28130822)).thenReturn(OzonSupplyEntity(orderId = 28130822, subtracted = true))
        `when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false)).thenReturn(orders)
        `when`(ozonApiClient.getDeliveriesByStatus(DeliveryStatus.delivering)).thenReturn(deliveriesResp.result)

        val result = stocksService.getStocks()

        assertEquals(2, result.orders.size)
        assertEquals(30000.0, result.stocksOnWayWorth)
        assertEquals(183005.7, result.stocksWorth)
        assertEquals(8751.59, result.deliveryWorth)

        assertEquals(24, result.products.size)
        assertEquals(122, result.products["0000009"]!!.totalStock)
        assertEquals(68, result.products["0000010"]!!.totalStock)
        assertEquals(97, result.products["0000012"]!!.totalStock)
        assertEquals(2, result.orders.size)
        assertEquals(4, result.deliveries.size)
        assertEquals(7, result.deliveries["0000009"]!!.totalStock)
        assertEquals(5, result.deliveries["0000010"]!!.totalStock)
        assertEquals(2, result.deliveries["0000015"]!!.totalStock)
        assertEquals(5, result.deliveries["0000012"]!!.totalStock)

    }

    @Test
    fun `test worth calculation - ozon + stock + deliveries`() {
        val positions = listOf(umbrella1, umbrella2, umbrella3, umbrella4, umbrella5, umbrella6)
        val stocks = listOf(stock1, stock2, stock3, stock4)
        val stocksResp = readResource("stocks/stocks-response.json", StocksResponse::class.java)
        val deliveriesResp = readResource("deliveries/deliveries-response.json", DeliveryResponse::class.java)

        `when`(ozonApiClient.getStocks()).thenReturn(stocksResp.items)
        `when`(ozonApiClient.getSupplyOrderList()).thenReturn(listOf())
        `when`(ozonApiClient.getDeliveriesByStatus(DeliveryStatus.delivering)).thenReturn(deliveriesResp.result)

        `when`(positionRepository.findAll()).thenReturn(positions)
        `when`(stockRepository.findAll()).thenReturn(stocks)
        `when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false)).thenReturn(setOf())

        val result = stocksService.getStocks()

        assertEquals(0, result.orders.size)
        assertEquals(0.0, result.stocksOnWayWorth)
        assertEquals(116994.94, result.stocksWorth)
        assertEquals(24, result.products.size)
        assertEquals(8751.59, result.deliveryWorth)
        assertEquals(4, result.deliveries.size)

    }

    @Test
    fun `test worth calculation - ozon + stock + order + deliveries`() {
        val positions = listOf(umbrella1, umbrella2, umbrella3, umbrella4, umbrella5, umbrella6)
        val stocks = listOf(stock1, stock2, stock3, stock4)
        val stocksResp = readResource("stocks/stocks-response.json", StocksResponse::class.java)
        val deliveriesResp = readResource("deliveries/deliveries-response.json", DeliveryResponse::class.java)
        val orders = setOf(orderEntity1, orderEntity2)

        `when`(ozonApiClient.getStocks()).thenReturn(stocksResp.items)
        `when`(ozonApiClient.getSupplyOrderList()).thenReturn(listOf())
        `when`(positionRepository.findAll()).thenReturn(positions)
        `when`(stockRepository.findAll()).thenReturn(stocks)

        `when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false)).thenReturn(orders)
        `when`(ozonApiClient.getDeliveriesByStatus(DeliveryStatus.delivering)).thenReturn(deliveriesResp.result)

        val result = stocksService.getStocks()

        assertEquals(2, result.orders.size)
        assertEquals(30000.0, result.stocksOnWayWorth)
        assertEquals(116994.94, result.stocksWorth)
        assertEquals(24, result.products.size)
        verify(stockRepository, times(0)).updateQuantityByOzonId(anyString(), anyInt())
    }
}