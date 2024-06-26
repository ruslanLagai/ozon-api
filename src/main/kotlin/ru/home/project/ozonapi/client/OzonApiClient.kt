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
import ru.home.project.ozonapi.dto.supply.request.SupplyItemsRequest
import ru.home.project.ozonapi.dto.supply.request.SupplyOrdersRequest
import ru.home.project.ozonapi.dto.supply.request.SupplyState
import ru.home.project.ozonapi.dto.supply.response.SupplyItem
import ru.home.project.ozonapi.dto.supply.response.SupplyItemsResp
import ru.home.project.ozonapi.dto.supply.response.SupplyOrderItem
import ru.home.project.ozonapi.dto.supply.response.SupplyOrdersResp
import java.time.Duration
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

    fun getSupplyOrders(): List<SupplyOrderItem>? {
        val request = SupplyOrdersRequest(page = 1, size = 100, states = listOf(
            SupplyState.ACCEPTED_AT_SUPPLY_WAREHOUSE, SupplyState.IN_TRANSIT,
            SupplyState.ACCEPTANCE_AT_STORAGE_WAREHOUSE, SupplyState.REPORTS_CONFIRMATION_AWAITING,
            SupplyState.REPORT_REJECTED, SupplyState.REJECTED_AT_SUPPLY_WAREHOUSE
        ))
        return ozonClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v1/supply-order/list")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<SupplyOrdersResp>()
            .cache(Duration.ofSeconds(5))
            .mapNotNull { resp -> resp.result.ifEmpty { null } }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()
    }

    fun getSupplyItems(orderId: Int): List<SupplyItem>? {
        val request = SupplyItemsRequest(page = 1, size = 100, orderId = orderId)
        return ozonClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v1/supply-order/items")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<SupplyItemsResp>()
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
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()
    }
}