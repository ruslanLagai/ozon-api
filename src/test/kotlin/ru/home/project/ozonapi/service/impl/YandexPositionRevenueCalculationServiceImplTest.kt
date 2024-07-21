package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.isNotNull
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.openapitools.client.apis.CampaignsApi
import org.openapitools.client.apis.OrdersApi
import org.openapitools.client.apis.OrdersStatsApi
import org.openapitools.client.apis.ReportsApi
import org.openapitools.client.models.GetCampaignsResponse
import org.openapitools.client.models.GetOrdersResponse
import org.openapitools.client.models.GetOrdersStatsResponse
import org.openapitools.client.models.OrderStatusType
import ru.home.project.ozonapi.calculator.FinancialAmountCalculator
import ru.home.project.ozonapi.client.YandexMarketClient
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.entity.MarketType
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.util.readResourceMoshi
import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
class YandexPositionRevenueCalculationServiceImplTest {

    private val campaignsApi = mock<CampaignsApi>()
    private val ordersApi = mock<OrdersApi>()
    private val ordersStatsApi = mock<OrdersStatsApi>()
    private val reportsApi = mock<ReportsApi>()
    private val yandexMarketClient = YandexMarketClient(ordersApi, ordersStatsApi, campaignsApi, reportsApi)
    private val yandexService = YandexServiceImpl(yandexMarketClient)
    private val financialAmountCalculator = FinancialAmountCalculator()
    private val repository = Mockito.mock(PositionRepository::class.java)

    private val yandexPostRevenueCalculationService = YandexPositionRevenueCalculationServiceImpl(
        yandexService, listOf(financialAmountCalculator), repository
    )
    private val statuses = setOf(
        OrderStatusType.DELIVERED,
        OrderStatusType.CANCELLED,
        OrderStatusType.RETURNED
    )
    val from: LocalDate = LocalDate.now().minusDays(30)
    val to: LocalDate = LocalDate.now()

    @BeforeEach
    fun init() {
        val campaigns = readResourceMoshi("yandex/campaigns/campaigns.json", GetCampaignsResponse::class.java)
        val ordersOne = readResourceMoshi("yandex/orders/orders-page-1.json", GetOrdersResponse::class.java)
        val ordersTwo = readResourceMoshi("yandex/orders/orders-page-2.json", GetOrdersResponse::class.java)
        val ordersThree = readResourceMoshi("yandex/orders/orders-page-3.json", GetOrdersResponse::class.java)
        val ordersFbs = readResourceMoshi("yandex/orders/orders-fbs.json", GetOrdersResponse::class.java)
        val fbyOrdersStats = readResourceMoshi("yandex/orders-stats/fby-stats.json", GetOrdersStatsResponse::class.java)
        val fbyNextPageOrdersStats = readResourceMoshi("yandex/orders-stats/fby-next-page-stats.json", GetOrdersStatsResponse::class.java)
        val fbsOrdersStats = readResourceMoshi("yandex/orders-stats/fbs-stats.json", GetOrdersStatsResponse::class.java)

        Mockito.`when`(campaignsApi.getCampaigns()).thenReturn(campaigns)
        Mockito.`when`(ordersApi.getOrders(campaignId = 66071470L, status = statuses, fromDate = from, toDate = to))
            .thenReturn(ordersOne)
        Mockito.`when`(ordersApi.getOrders(campaignId = 66071470L, status = statuses, fromDate = from, toDate = to, page = 2))
            .thenReturn(ordersTwo)
        Mockito.`when`(ordersApi.getOrders(campaignId = 66071470L, status = statuses, fromDate = from, toDate = to, page = 3))
            .thenReturn(ordersThree)
        Mockito.`when`(ordersApi.getOrders(campaignId = 93726650L, status = statuses, fromDate = from, toDate = to))
            .thenReturn(ordersFbs)
        Mockito.`when`(ordersStatsApi.getOrdersStats(campaignId = Mockito.eq(66071470L), pageToken = isNull(), limit = any(), getOrdersStatsRequest = any()))
            .thenReturn(fbyOrdersStats)
        Mockito.`when`(ordersStatsApi.getOrdersStats(campaignId = Mockito.eq(93726650L), pageToken = isNull(), limit = any(), getOrdersStatsRequest = any()))
            .thenReturn(fbsOrdersStats)
        Mockito.`when`(ordersStatsApi.getOrdersStats(campaignId = any(), pageToken = isNotNull(), limit = any(), getOrdersStatsRequest = any()))
            .thenReturn(fbyNextPageOrdersStats)
    }

    @Test
    fun calculateRevenue() {
        val positionEntity = PositionEntity(1L, "Держатель для губок", 130.0, 0.0, "1134731178", "", "0000005")

        Mockito.`when`(repository.getPositionEntityByName("Держатель для губок")).thenReturn(positionEntity)


        val request = RevenueRequest(name = "Держатель для губок", from = OffsetDateTime.now().minusMonths(1),
            to = OffsetDateTime.now(), type = MarketType.Yandex)
        val response = yandexPostRevenueCalculationService.calculateRevenue(request)

        assertEquals("Держатель для губок", response!!.name)
        assertEquals("0000005", response.yandexId)
        assertEquals(39.23, response.averageRevenue)
        assertEquals(526.08, response.logistic)
        assertEquals(166.4, response.refund)
        assertEquals(349.41, response.saleCommission)
        assertEquals(2695.0, response.price)
        assertEquals(650.0, response.costPrice)
        assertEquals(134.61, response.taxes)
        assertEquals(196.14, response.totalRevenue)
        assertEquals(5, response.deliveryItemCount)
        assertEquals(2, response.refundCount)
    }
}