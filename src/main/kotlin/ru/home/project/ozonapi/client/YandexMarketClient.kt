package ru.home.project.ozonapi.client

import org.apache.commons.io.FileUtils
import org.openapitools.client.apis.CampaignsApi
import org.openapitools.client.apis.OrdersApi
import org.openapitools.client.apis.OrdersStatsApi
import org.openapitools.client.apis.ReportsApi
import org.openapitools.client.models.*
import org.springframework.stereotype.Component
import ru.home.project.ozonapi.exception.YandexException
import java.io.File
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.Executors

/**
 * @author rlagay
 */
@Component
class YandexMarketClient(
    val ordersApi: OrdersApi,
    val ordersStatsApi: OrdersStatsApi,
    val campaignsApi: CampaignsApi,
    val reportsApi: ReportsApi
) {

    private val executorService = Executors.newFixedThreadPool(2)

    /**
     * Get campaigns
     */
    fun getCampaigns() : List<Long> {
        return campaignsApi.getCampaigns().campaigns?.map { it.id ?: 0L} ?: listOf()
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
     * Get report
     */
    fun createReport(businessId: Long, from: LocalDate, to: LocalDate, campaigns: List<Long>): String {

        val path = executorService.submit {
            var link: String? = null
            for (i in 0..2) {
                link = getReport(businessId, from, to, campaigns)
                if (link != null) {
                    break
                }
                Thread.sleep(1000)
            }
            if (link != null) {
                val date = LocalDateTime.now()
                FileUtils.copyURLToFile(URI.create(link).toURL(), File("/tmp/yandex-report-$date.csv"))
            }
            return@submit
        }.get()
        return ""
    }

    /**
     * Get report
     */
    fun getReport(businessId: Long, from: LocalDate, to: LocalDate, campaigns: List<Long>) : String? {
        val request = GenerateUnitedMarketplaceServicesReportRequest(
            businessId = businessId,
            dateFrom = from,
            dateTo = to,
            placementPrograms = listOf(PlacementType.FBY, PlacementType.FBS),
            campaignIds = campaigns
        )
        val response = reportsApi.generateUnitedMarketplaceServicesReport(request, ReportFormatType.CSV)
        if (response.status != ApiResponseStatusType.OK || response.result == null) {
            throw YandexException(msg = "Failed to create report, status: " + response.status!!.name)
        }
        val report = reportsApi.getReportInfo(response.result!!.reportId)
        if (report.status != ApiResponseStatusType.OK || report.result == null) {
            throw YandexException(msg = "Failed to create report, status: " + response.status!!.name)
        }
        val status = report.result!!.status
        return if (status == ReportStatusType.DONE || status == ReportStatusType.FAILED) { report.result!!.file } else { null }
    }
}