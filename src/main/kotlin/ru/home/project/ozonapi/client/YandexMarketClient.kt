package ru.home.project.ozonapi.client

import org.apache.commons.io.FileUtils
import org.openapitools.client.apis.*
import org.openapitools.client.models.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.home.project.ozonapi.exception.YandexException
import java.io.File
import java.net.URI
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

/**
 * @author rlagay
 */
@Component
class YandexMarketClient(
    val ordersApi: OrdersApi,
    val ordersStatsApi: OrdersStatsApi,
    val campaignsApi: CampaignsApi,
    val reportsApi: ReportsApi,
    val stocksApi: StocksApi
) {

    val log: Logger = LoggerFactory.getLogger(YandexMarketClient::class.java)

    /**
     * Get campaigns
     */
    fun getCampaigns() : List<Long> {
        return campaignsApi.getCampaigns().campaigns?.map { it.id ?: 0L} ?: listOf()
    }

    /**
     * Get campaigns
     */
    fun getCampaignList() : List<CampaignDTO>? {
        return campaignsApi.getCampaigns().campaigns
    }

    /**
     * Get stocks
     */
    fun getFbyStocks(campaign: Long): List<WarehouseOffersDTO>? {
        return stocksApi.getStocks(campaignId = campaign, limit = 200).result?.warehouses
    }

    /**
     * Get orders data
     */
    fun getOrders(campaign: Long, statuses: Set<OrderStatusType>, from: LocalDate, to: LocalDate): List<OrdersStatsOrderDTO> {
        val orderIds = HashSet<Long>()
        val firstOrdersPage = ordersApi.getOrders(campaignId = campaign, status = statuses, fromDate = from, toDate = to)
        firstOrdersPage.orders?.forEach { orderIds.add(it.id ?: 0) }

        val pagesCount = firstOrdersPage.pager?.pagesCount ?: 0
        if (pagesCount > (firstOrdersPage.pager?.currentPage ?: 0)) {
            for (i: Int in 2..pagesCount) {
                ordersApi.getOrders(campaignId = campaign, status = statuses, fromDate = from, toDate = to, page = i).orders
                    ?.forEach { orderIds.add(it.id ?: 0) }
            }
        }

        if (orderIds.isEmpty()) {
            return listOf()
        }
        val request = GetOrdersStatsRequest(orders = orderIds.toList())
        val orderStats = HashSet<OrdersStatsOrderDTO>()
        val ordersStatsResponse = ordersStatsApi.getOrdersStats(campaignId = campaign, limit = 100, getOrdersStatsRequest = request)
        val orders = ordersStatsResponse.result?.orders ?: listOf()
        orderStats.addAll(orders)

        var nextPageId = ordersStatsResponse.result?.paging?.nextPageToken
        nextPageId.let {
            while (!nextPageId.isNullOrEmpty()) {
                val nextPage = ordersStatsApi.getOrdersStats(campaignId = campaign, limit = 100, pageToken = nextPageId, getOrdersStatsRequest = request)
                nextPageId = nextPage.result?.paging?.nextPageToken
                orderStats.addAll(nextPage.result?.orders ?: listOf())
            }
        }
        return ArrayList<OrdersStatsOrderDTO>(orderStats)
    }

    /**
     * Get Orders
     */
    fun getOrdersWithoutStats(campaign: Long, statuses: Set<OrderStatusType>, from: LocalDate, to: LocalDate): List<OrderDTO> {
        val orders = ArrayList<OrderDTO>()
        val firstOrdersPage = ordersApi.getOrders(campaignId = campaign, status = statuses, fromDate = from, toDate = to)
        firstOrdersPage.orders?.forEach { orders.add(it) }

        val pagesCount = firstOrdersPage.pager?.pagesCount ?: 0
        if (pagesCount > (firstOrdersPage.pager?.currentPage ?: 0)) {
            for (i: Int in 2..pagesCount) {
                ordersApi.getOrders(campaignId = campaign, status = statuses, fromDate = from, toDate = to, page = i).orders
                    ?.forEach { orders.add(it) }
            }
        }
        return orders
    }

    /**
     * Get report
     */
    fun createReport(businessId: Long, from: LocalDate, to: LocalDate, campaigns: List<Long>) : CompletableFuture<String> {
        var reportId: String
        var filePath = ""
        return CompletableFuture.supplyAsync {
            val request = GenerateUnitedMarketplaceServicesReportRequest(
                businessId = businessId,
                dateFrom = from,
                dateTo = if (to.isAfter(LocalDate.now())) { LocalDate.now() } else { to },
                placementPrograms = listOf(PlacementType.FBY, PlacementType.FBS),
                campaignIds = campaigns
            )
            val response = reportsApi.generateUnitedMarketplaceServicesReport(request, ReportFormatType.CSV)
            if (response.status != ApiResponseStatusType.OK || response.result == null) {
                throw YandexException(msg = "Failed to create report, status: " + response.status!!.name)
            }
            reportId = response.result!!.reportId

            Thread.sleep(3000)

            val file = getReport(reportId)
            if (file != null) {
                val date = LocalDateTime.now()
                filePath = "/tmp/ozon/yandex-report-$date.zip"
                FileUtils.copyURLToFile(URI.create(file).toURL(), File(filePath))
                Thread.sleep(1000)
            }
            filePath
        }
    }

    /**
     * create Stocks report
     */
    fun createStocksReport(campaign: Long, from: LocalDate, to: LocalDate) : String {
        val request = GenerateGoodsMovementReportRequest(campaignId = campaign, dateFrom = from, dateTo = to)
        val response = reportsApi.generateGoodsMovementReport(request, ReportFormatType.CSV)
        if (response.status != ApiResponseStatusType.OK || response.result == null) {
            throw YandexException(msg = "Failed to create report, status: " + response.status!!.name)
        }

        val reportId = response.result!!.reportId
        Thread.sleep(5000)
        val file = getReport(reportId)
        if (file == null) {
            throw YandexException(msg = "Failed to retrieve report, filePath is null, reportId: ${reportId}")
        }
        val date = LocalDateTime.now()
        val filePath = "/tmp/ozon/yandex-goods-movement-report-$date.csv"
        FileUtils.copyURLToFile(URI.create(file).toURL(), File(filePath))
        Thread.sleep(1000)
        return filePath
    }


    private fun getReport(reportId: String): String? {
        var report = reportsApi.getReportInfo(reportId)
        if (report.result!!.status != ReportStatusType.DONE && report.result!!.status != ReportStatusType.FAILED) {
            for (i in 0..2) {
                log.info("Retrying to get report, current status is {}", report.result!!.status)
                report = reportsApi.getReportInfo(reportId)
                Thread.sleep(Duration.ofSeconds(1 + i.toLong() * i.toLong()))
                if (report.result!!.status == ReportStatusType.DONE) {
                    break
                }
            }
        }

        val status = report.result!!.status
        if (report.status != ApiResponseStatusType.OK) {
            throw YandexException(msg = "Failed to create report, status: " + report.status!!.name)
        }

        if (status != ReportStatusType.DONE) {
            throw YandexException(msg = "Failed to create report, status is not DONE: " + report.result!!.status)
        }
        return report.result!!.file
    }
}