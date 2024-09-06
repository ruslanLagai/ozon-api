package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.eq
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.openapitools.client.apis.*
import org.openapitools.client.models.*
import ru.home.project.ozonapi.client.YandexMarketClient
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.util.readResourceMoshi
import ru.home.project.ozonapi.util.yandexFinalStatuses
import java.time.LocalDate

/**
 * @author rlagay
 */
class YandexServiceImplTest {

    private val campaignsApi = mock<CampaignsApi>()
    private val ordersApi = mock<OrdersApi>()
    private val ordersStatsApi = mock<OrdersStatsApi>()
    private val reportsApi = mock<ReportsApi>()
    private val stocksApi = mock<StocksApi>()
    private val positionRepository = mock<PositionRepository>()
    private val yandexMarketClient = YandexMarketClient(ordersApi, ordersStatsApi, campaignsApi, reportsApi, stocksApi)
    private val yandexService = YandexServiceImpl(yandexMarketClient, positionRepository)

    private val statuses = setOf(
        OrderStatusType.DELIVERED,
        OrderStatusType.CANCELLED,
        OrderStatusType.RETURNED
    )

    private val spongeHolder = PositionEntity(7, "Держатель для губки", 120.0, 0.0, "1111", "0000005", "0000005")
    private val hanger = PositionEntity(8, "Крючок", 100.0, 0.0, "2222", "0000029", "0000029")
    private val beachBag = PositionEntity(9, "Сумка пляжная", 340.0, 0.0, "1589765459", "0000032", "0000032")

    @Test
    fun `test get yandex orders`() {
        val from = LocalDate.now().minusDays(25)
        val to = LocalDate.now()

        val campaigns = readResourceMoshi("yandex/campaigns/campaigns.json", GetCampaignsResponse::class.java)
        val ordersOne = readResourceMoshi("yandex/orders/orders-page-1.json", GetOrdersResponse::class.java)
        val ordersTwo = readResourceMoshi("yandex/orders/orders-page-2.json", GetOrdersResponse::class.java)
        val ordersThree = readResourceMoshi("yandex/orders/orders-page-3.json", GetOrdersResponse::class.java)
        val ordersFbs = readResourceMoshi("yandex/orders/orders-fbs.json", GetOrdersResponse::class.java)
        val fbyOrdersStats = readResourceMoshi("yandex/orders-stats/fby-stats.json", GetOrdersStatsResponse::class.java)
        val fbsOrdersStats = readResourceMoshi("yandex/orders-stats/fbs-stats.json", GetOrdersStatsResponse::class.java)
        val nextPageStats = readResourceMoshi("yandex/orders-stats/fby-next-page-stats.json", GetOrdersStatsResponse::class.java)

        `when`(campaignsApi.getCampaigns()).thenReturn(campaigns)
        `when`(ordersApi.getOrders(campaignId = 66071470L, status = statuses, fromDate = from, toDate = to))
            .thenReturn(ordersOne)
        `when`(ordersApi.getOrders(campaignId = 66071470L, status = statuses, fromDate = from, toDate = to, page = 2))
            .thenReturn(ordersTwo)
        `when`(ordersApi.getOrders(campaignId = 66071470L, status = statuses, fromDate = from, toDate = to, page = 3))
            .thenReturn(ordersThree)
        `when`(ordersApi.getOrders(campaignId = 93726650L, status = statuses, fromDate = from, toDate = to))
            .thenReturn(ordersFbs)
        `when`(ordersStatsApi.getOrdersStats(campaignId = eq(66071470L), pageToken = isNull(), limit = any(), getOrdersStatsRequest = any()))
            .thenReturn(fbyOrdersStats)
        `when`(ordersStatsApi.getOrdersStats(campaignId = any(), pageToken = any(), limit = any(), getOrdersStatsRequest = any()))
            .thenReturn(nextPageStats)
        `when`(ordersStatsApi.getOrdersStats(campaignId = eq(93726650L), pageToken = isNull(), limit = any(), getOrdersStatsRequest = any()))
            .thenReturn(fbsOrdersStats)

        val orders = yandexService.getTransaction(from = from, to = to, key = "", yandexFinalStatuses)

        assertEquals(10, orders.size)
    }

    @Test
    fun `test get yandex stocks`() {
        val stocks = readResourceMoshi("yandex/stocks/fby-stocks.json", GetWarehouseStocksResponse::class.java)

        `when`(positionRepository.findAll()).thenReturn(listOf(spongeHolder, hanger, beachBag))
        `when`(stocksApi.getStocks(campaignId = 66071470, limit = 200)).thenReturn(stocks)

        val result = yandexService.getStocks("66071470")

        assertEquals(2, result.size)
        assertEquals(86, result[0].totalStock)
        assertEquals(120.0, result[0].costPrice)
        assertEquals("Держатель для губки", result[0].name)
        assertEquals(26, result[1].totalStock)
        assertEquals(100.0, result[1].costPrice)
        assertEquals("Крючок", result[1].name)
    }
}