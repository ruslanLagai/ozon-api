package ru.home.project.ozonapi.service.impl

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerException
import org.openapitools.client.models.OrderStatusType
import org.openapitools.client.models.OrdersStatsOrderDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.client.YandexMarketClient
import ru.home.project.ozonapi.dto.YandexReportResult
import ru.home.project.ozonapi.exception.YandexException
import ru.home.project.ozonapi.service.YandexService
import java.io.File
import java.time.LocalDate
import java.time.Period
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

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

    private val filesToProcess = mapOf(
//        "banners.csv",//баннеры
//        "cpm-boost.csv",//буст продаж с оплатой запоказы
        Pair(ReportType.CrossDoc, "delivery_via_transit_warehouse.csv"),//кросс док
//        "export_from_warehouse.csv", пока нет
//        "express_delivery.csv", //
//        "expropriation.csv", пока нет
//        "extended_service_access.csv",
//        "installment_plan.csv",
        Pair(ReportType.PaidStorage, "paid_storage_after_01-06-22.csv"),
//        "reception_of_surplus.csv",
        Pair(ReportType.Shelf, "shelf.csv"),//Полки
        Pair(ReportType.Utilization, "utilization.csv")
    )

    private val reportProcessor = mapOf(
        Pair(ReportType.CrossDoc) { file: String, response: YandexReportResult ->
            processReport(file, "SERVICE_PRICE_IN_ROUBLES") { value: Double -> response.crossDoc = value }
        },
        Pair(ReportType.PaidStorage) { file: String, response: YandexReportResult ->
            processReport(file, "PAID_STORAGE_IN_ROUBLES") { value: Double -> response.paidStorage = value }
        },
        Pair(ReportType.Shelf) { file: String, response: YandexReportResult ->
            processReport(file, "SERVICE_PRICE_IN_ROUBLES") { value: Double -> response.shelf = value }
        },
        Pair(ReportType.Utilization) { file: String, response: YandexReportResult ->
            processReport(file, "SERVICE_PRICE_IN_ROUBLES") { value: Double -> response.utilization = value }
        }
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

    override fun getReport(from: LocalDate, to: LocalDate) : Pair<YandexReportResult, CompletableFuture<*>> {
        val campaigns = yandexMarketClient.getCampaignList()
        val businessId = campaigns?.map { it.business?.id ?: 0L}?.first() ?: 0L
        val campaignIds = campaigns?.map { it.id ?: 0L } ?: listOf()

        val response = YandexReportResult()
        val future = yandexMarketClient.createReport(businessId, from, to, campaignIds).whenComplete { filePath, ex ->
            if (ex != null) {
               throw ex
            }
            if (filePath.isNullOrEmpty()) {
                throw YandexException("Empty filepath")
            }
            ProcessBuilder()
               .command("mkdir", "/tmp/ozon/")
               .redirectError(ProcessBuilder.Redirect.INHERIT)
               .redirectOutput(ProcessBuilder.Redirect.INHERIT)
               .start()
               .waitFor()

            ProcessBuilder()
               .command("unzip", filePath, "-d", "/tmp/ozon/")
               .redirectError(ProcessBuilder.Redirect.INHERIT)
               .redirectOutput(ProcessBuilder.Redirect.INHERIT)
               .start()
               .waitFor()

            filesToProcess.forEach { (k, v) -> reportProcessor[k]!!.invoke("/tmp/ozon/" + v, response) }

            ProcessBuilder()
               .command("rm", "-r", "/tmp/ozon")
               .start()
               .waitFor()
        }
        return Pair(response, future)
    }

    private fun processReport(file: String, header: String, consumer: Consumer<Double>) {
        val sum = csvReader().readAllWithHeader(File(file))
            .map { it[header] }
            .filter { !it.isNullOrEmpty() }
            .sumOf { it!!.toDouble() }
        consumer.accept(sum)
    }

    enum class ReportType {
        CrossDoc, PaidStorage, Shelf, Utilization
    }
}