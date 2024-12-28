package ru.home.project.ozonapi.service.impl

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import ru.home.project.ozonapi.client.OzonApiClient
import ru.home.project.ozonapi.dto.delivery.Delivery
import ru.home.project.ozonapi.dto.delivery.DeliveryStatus
import ru.home.project.ozonapi.dto.finance.response.RefundData
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.dto.response.AnalyticDataDto
import ru.home.project.ozonapi.dto.supply.request.AnalyticDimension
import ru.home.project.ozonapi.dto.supply.request.AnalyticMetric
import ru.home.project.ozonapi.dto.supply.request.AnalyticOrder
import ru.home.project.ozonapi.dto.supply.request.AnalyticSorting
import ru.home.project.ozonapi.dto.supply.response.AnalyticsData
import ru.home.project.ozonapi.dto.supply.response.SupplyBundleItem
import ru.home.project.ozonapi.model.Product
import ru.home.project.ozonapi.service.OzonService
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * @author rlagay
 */
@Service
@Slf4j
class OzonServiceImpl(
    val ozonApiClient: OzonApiClient
) : OzonService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(OzonServiceImpl::class.java)
    }

    @Cacheable(cacheNames = ["ozon-transactions"], key = "#key")
    override fun getTransaction(from: OffsetDateTime, to: OffsetDateTime, key: String): List<Transaction> {
        val numberOfDays = moreThanMonth(from, to)
        return if (numberOfDays > 31) {
            val transactions = ArrayList<Transaction>()
            for (i: Int in 0..numberOfDays step 30) {
                val rest = numberOfDays - i
                val startPoint = from.plusDays(i.toLong())
                val endPoint = startPoint.plusDays(if (rest > 30) 29L else rest.toLong())
                val part = getTransactions(
                    OffsetDateTime.of(startPoint.toLocalDate().atTime(LocalTime.MIN), ZoneOffset.UTC),
                    OffsetDateTime.of(endPoint.toLocalDate().atTime(LocalTime.MAX), ZoneOffset.UTC)
                )
                transactions.addAll(part)
            }
            transactions
        } else {
            getTransactions(from, to)
        }
    }

    @Cacheable(cacheNames = ["ozon-transactions"], key = "#postingNumber")
    override fun getTransaction(postingNumber: String): List<Transaction> {
        val transactions = ozonApiClient.getTransactions(postingNumber)
        if (CollectionUtils.isEmpty(transactions)) {
            log.info("Empty transactions for '$postingNumber'")
        }
        return transactions.orEmpty()
    }

    @Cacheable(cacheNames = ["ozon-refund"], key = "#postingNumber")
    override fun getRefundData(postingNumber: String): RefundData? {
        val refund = ozonApiClient.getRefundData(postingNumber)
        if (refund == null) {
            log.info("No refund data for {}", postingNumber)
            return null
        }
        return refund
    }

    @Cacheable(cacheNames = ["ozon-supply"], key = "#orderIds")
    override fun getSupplyItemsInOrder(orderIds: List<Int>): List<SupplyBundleItem> {
        if (orderIds.isEmpty()) {
            return listOf()
        }
        val supplyOrders = ozonApiClient.getSupplyOrders(orderIds)
        if (supplyOrders == null) {
            log.info("No supply orders found for {}", orderIds.toTypedArray())
            return listOf()
        }
        val bundleIds = supplyOrders.flatMap { it.supplies!!.map { item -> item.bundleId } }
        val supplyBundles = ozonApiClient.getSupplyOrderBundle(bundleIds)
        if (supplyBundles.isNullOrEmpty()) {
            log.info("No supply items for {}", orderIds.toTypedArray())
            return listOf()
        }
        return supplyBundles
    }

    override fun getSupplyOrders(): List<Int> {
        val supplyOrders = ozonApiClient.getSupplyOrderList()
        if (supplyOrders.isNullOrEmpty()) {
            log.info("No supply orders")
            return listOf()
        }
        return supplyOrders
    }

    @Cacheable(cacheNames = ["ozon-supply"], key = "#cacheKey")
    override fun getStockItems(cacheKey: String): List<Product> {
        val stocks = ozonApiClient.getStocks()
        if (stocks.isEmpty()) {
            log.info("No stock data")
            return listOf()
        }
        return stocks
            .map {
                val fbo = it.stocks.firstOrNull { item -> item.type == "fbo" }
                val fboStock = fbo?.present ?: 0
                val fbs = it.stocks.firstOrNull { item -> item.type == "fbs" }
                val fbsStock = fbs?.present ?: 0
                val discounted = it.stocks.firstOrNull { item -> item.type == "discounted" }?.present ?: 0
                Product(sku = "", artikul = it.offerId, fboStock = fboStock, fbsStock = fbsStock, totalStock = fbsStock + fboStock + discounted)
            }
            .filter { it.totalStock != 0 }
    }

    @Cacheable(cacheNames = ["ozon-delivery"], key = "#status.name()")
    override fun getDeliveryByStatus(status: DeliveryStatus): List<Delivery> {
        val deliveries = ozonApiClient.getDeliveriesByStatus(status)
        if (deliveries.isNullOrEmpty()) {
            log.info("No deliveries")
            return listOf()
        }
        return deliveries
    }

    override fun getAnalyticData(from: LocalDate, to: LocalDate, metrics: List<AnalyticMetric>): ArrayList<AnalyticDataDto> {
        var offset = 0
        var hasNext = true
        val result = ArrayList<AnalyticDataDto>()
        while (hasNext) {
            val supplyOrders = ozonApiClient.getAnalyticData(
                from = from, to = to, metrics = metrics,
                dimensions = listOf(AnalyticDimension.sku),
                offset = offset
            )
            if (supplyOrders == null) {
                log.error("No analytic data")
                return result
            }
            if (supplyOrders.data.size < 1000) {
                hasNext = false
            }
            val analyticDataDto = supplyOrders.data
                .map {
                    val metricMap = HashMap<AnalyticMetric, Int>()
                    for (analyticMetric in metrics) {
                        metricMap[analyticMetric] = it.metrics[metrics.indexOf(analyticMetric)]
                    }
                    AnalyticDataDto(sku = it.dimensions[0].id, name = it.dimensions[0].name, metrics = metricMap)
                }
            result.addAll(analyticDataDto)
            offset++
        }
        return result
    }

    private fun getTransactions(from: OffsetDateTime, to: OffsetDateTime): List<Transaction> {
        val transactions = ArrayList<Transaction>()
        var pageNumber = 1
        do {
            val page = ozonApiClient.getTransactions(from, to, pageNumber)
            page?.let { transactions.addAll(it) }
            pageNumber++
        } while (!page.isNullOrEmpty())
        if (CollectionUtils.isEmpty(transactions)) {
            log.info("Empty transactions from '$from' to '$to'")
        }
        return transactions
    }

    private fun moreThanMonth(from: OffsetDateTime, to: OffsetDateTime): Int {
        return to.dayOfYear - from.dayOfYear
    }
}