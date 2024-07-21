package ru.home.project.ozonapi.service.impl

import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerException
import org.openapitools.client.models.OrderStatusType
import org.openapitools.client.models.OrdersStatsOrderDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.client.YandexMarketClient
import ru.home.project.ozonapi.service.YandexService
import java.time.LocalDate
import java.time.Period

/**
 * @author rlagay
 */
@Service
class YandexServiceImpl(
    val yandexMarketClient: YandexMarketClient
) : YandexService {

    private val log: Logger = LoggerFactory.getLogger(YandexServiceImpl::class.java)
    private val statuses = setOf(
        OrderStatusType.DELIVERED,
        OrderStatusType.CANCELLED,
        OrderStatusType.RETURNED
    )

    @Cacheable(cacheNames = ["yandex-transactions"], key = "#key")
    override fun getTransaction(from: LocalDate, to: LocalDate, key: String): List<OrdersStatsOrderDTO> {
        kotlin.runCatching {
            val orders = ArrayList<OrdersStatsOrderDTO>()
            val campaigns = yandexMarketClient.getCampaigns().filter { it != 0L }
            if (campaigns.isEmpty()) {
                log.debug("No campaigns found")
                return listOf()
            }
            campaigns.forEach {
                val period = Period.between(from, to)
                val numberOfDays = period.days + period.months * 31 + period.years * 365
                if (numberOfDays > 30) {
                    for (i in 0..<numberOfDays step 30) {
                        val fromDate = from.plusDays(i.toLong())
                        var toDate = fromDate.plusDays(30)
                        if (toDate.isAfter(to)) {
                            toDate = to
                        }
                        val resp = yandexMarketClient.getOrders(it, statuses, fromDate, toDate)
                        orders.addAll(resp)
                    }
                } else {
                    val resp = yandexMarketClient.getOrders(it, statuses, from, to)
                    orders.addAll(resp)
                }
            }
            return orders
        }.onSuccess {
            log.debug("Successfully received order data")
        }.onFailure {
            when (it) {
                is ClientException -> log.error("Error from yandex market {}, message {}", it.statusCode, it.message)
                is ServerException -> log.error("Error from yandex market {}, message {}, response {}", it.statusCode, it.message, it.response)
            }
            throw it
        }.getOrThrow()
    }


}