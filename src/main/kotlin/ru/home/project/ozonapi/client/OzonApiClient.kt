package ru.home.project.ozonapi.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriBuilder
import reactor.util.retry.Retry
import ru.home.project.ozonapi.dto.delivery.Delivery
import ru.home.project.ozonapi.dto.delivery.DeliveryStatus
import ru.home.project.ozonapi.dto.delivery.request.DeliveryRequest
import ru.home.project.ozonapi.dto.delivery.response.DeliveryResponse
import ru.home.project.ozonapi.dto.finance.request.Date
import ru.home.project.ozonapi.dto.finance.request.Filter
import ru.home.project.ozonapi.dto.finance.request.RefundRequest
import ru.home.project.ozonapi.dto.finance.request.TransactionsRequest
import ru.home.project.ozonapi.dto.finance.response.RefundData
import ru.home.project.ozonapi.dto.finance.response.RefundResp
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.dto.finance.response.TransactionsResp
import ru.home.project.ozonapi.dto.stocks.request.GetStocksRequest
import ru.home.project.ozonapi.dto.stocks.request.StocksFilter
import ru.home.project.ozonapi.dto.stocks.response.GetStocksResponse
import ru.home.project.ozonapi.dto.stocks.response.StocksResultItem
import ru.home.project.ozonapi.dto.supply.request.*
import ru.home.project.ozonapi.dto.supply.response.*
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
@Component
class OzonApiClient(
    private val ozonClient: WebClient
) {

    fun getTransactions(from: OffsetDateTime?, to: OffsetDateTime?, page: Int): List<Transaction>? {
        val date = Date(from, to)
        val filter = Filter(date = date)
        return getTransactions(filter, page)
    }

    fun getTransactions(postingNumber: String): List<Transaction>? {
        val filter = Filter(postingNumber = postingNumber)
        return getTransactions(filter, 1)
    }

    fun getRefundData(postingNumber: String): RefundData? {
        val filter = Filter(postingNumber = postingNumber, operationType = null, transactionType = null, date = null)
        val request = RefundRequest(filter = filter)
        return ozonClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v3/returns/company/fbo")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<RefundResp>()
            .cache(Duration.ofSeconds(5))
            .mapNotNull { resp ->
                val returns = resp.returns
                if (!returns.isNullOrEmpty()) {
                    returns[0]
                } else {
                    null
                }
            }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()
    }

    fun getStocks(): List<StocksResultItem> {
        val filter = StocksFilter()
        val request = GetStocksRequest(filter = filter)
        val items = ArrayList<StocksResultItem>()

        var hasNext = true
        while (hasNext) {
            val response = ozonClient.post()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder
                        .path("/v3/product/info/stocks")
                        .build()
                }
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono<GetStocksResponse>()
                .cache(Duration.ofSeconds(5))
                .mapNotNull { resp -> resp.result }
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block()
            if (response == null || response.total < 100) {
                hasNext = false
            }
            response?.let { items.addAll(it.items) }
        }
        return items
    }

    fun getSupplyOrderList(): List<Int>? {
        val request = SupplyOrdersRequest(filter = SupplyOrdersFilter(
            states =  listOf(
                SupplyState.ORDER_STATE_ACCEPTED_AT_SUPPLY_WAREHOUSE, SupplyState.ORDER_STATE_IN_TRANSIT,
                SupplyState.ORDER_STATE_ACCEPTANCE_AT_STORAGE_WAREHOUSE, SupplyState.ORDER_STATE_REPORTS_CONFIRMATION_AWAITING,
                SupplyState.ORDER_STATE_REPORT_REJECTED, SupplyState.ORDER_STATE_REJECTED_AT_SUPPLY_WAREHOUSE)),
            paging = SupplyOrdersPaging()
        )
        return ozonClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v2/supply-order/list")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<SupplyOrdersResp>()
            .cache(Duration.ofSeconds(5))
            .mapNotNull { resp -> resp.supplyOrders }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()
    }

    fun getSupplyOrders(orderIds: List<Int>): List<SupplyOrderItem>? {
        val request = SupplyOrderItemsRequest(orderIds = orderIds.map { it.toString() })
        return ozonClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v2/supply-order/get")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<GetSupplyOrdersResp>()
            .cache(Duration.ofSeconds(5))
            .mapNotNull { resp -> resp.orders.ifEmpty { null } }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()
    }

    fun getSupplyOrderBundle(bundleIds: List<String>): List<SupplyBundleItem>? {
        val request = SupplyBundleRequest(bundleIds = bundleIds)
        return ozonClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v1/supply-order/bundle")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<SupplyBundlesResp>()
            .cache(Duration.ofSeconds(5))
            .mapNotNull { resp -> resp.items.ifEmpty { null } }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()
    }

    fun getDeliveriesByStatus(status: DeliveryStatus): List<Delivery>? {
        val filter = ru.home.project.ozonapi.dto.delivery.request.Filter(
            status = status, since = OffsetDateTime.now().minusMonths(1), to = OffsetDateTime.now())
        val request = DeliveryRequest(filter = filter)
        return ozonClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v2/posting/fbo/list")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<DeliveryResponse>()
            .cache(Duration.ofSeconds(5))
            .mapNotNull { resp -> resp.result.ifEmpty { null } }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()
    }

    fun getAnalyticData(from: LocalDate, to: LocalDate, metrics: List<AnalyticMetric>,
                        dimensions: List<AnalyticDimension>, filters: List<AnalyticFilter>? = null,
                        sort: List<AnalyticSorting>? = null, offset: Int): AnalyticsDataResult? {
        val request = AnalyticsDataRequest(
            from = from, to = to, metrics = metrics, dimension = dimensions, filters = filters,
            sort = sort, offset = offset
        )
        return ozonClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v1/analytics/data")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<AnalyticsDataResp>()
            .cache(Duration.ofSeconds(5))
            .mapNotNull { resp -> resp.result }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()
    }

    private fun getTransactions(filter: Filter, page: Int): List<Transaction>? {
        val request = TransactionsRequest(filter = filter, page = page)

        return ozonClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v3/finance/transaction/list")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<TransactionsResp>()
            .cache(Duration.ofSeconds(5))
            .mapNotNull { resp -> resp.result.operations }
            .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(2)))
            .block()
    }
}