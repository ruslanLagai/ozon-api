package ru.home.project.ozonapi.service.impl

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.apache.commons.lang3.StringUtils
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerException
import org.openapitools.client.models.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.client.YandexMarketClient
import ru.home.project.ozonapi.dto.YandexReportResult
import ru.home.project.ozonapi.exception.YandexException
import ru.home.project.ozonapi.model.Product
import ru.home.project.ozonapi.repository.PositionRepository
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
    val yandexMarketClient: YandexMarketClient,
    val positionRepository: PositionRepository
) : YandexService {

    private val log: Logger = LoggerFactory.getLogger(YandexServiceImpl::class.java)


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
        Pair(ReportType.ReceptionSurplus, "reception_of_surplus.csv"),
        Pair(ReportType.Shelf, "shelf.csv"),//Полки
        Pair(ReportType.Utilization, "utilization.csv")
    )

    private val reportProcessor = mapOf(
        Pair(ReportType.CrossDoc) { file: String, response: YandexReportResult ->
            processReport(file, "SERVICE_PRICE") { value: Double -> response.crossDoc += value }
        },
        Pair(ReportType.PaidStorage) { file: String, response: YandexReportResult ->
            processReport(file, "PAID_STORAGE") { value: Double -> response.paidStorage = value }
        },
        Pair(ReportType.Shelf) { file: String, response: YandexReportResult ->
            processReport(file, "SERVICE_PRICE") { value: Double -> response.shelf = value }
        },
        Pair(ReportType.Utilization) { file: String, response: YandexReportResult ->
            processReport(file, "SERVICE_PRICE") { value: Double -> response.utilization = value }
        },
        Pair(ReportType.ReceptionSurplus) { file: String, response: YandexReportResult ->
            processReport(file, "SERVICE_PRICE") { value: Double -> response.crossDoc += value }
        }
    )


    override fun getCampaigns() : List<CampaignDTO>? {
        return yandexMarketClient.getCampaignList()
    }

    /**
     * Get stocks from yandex
     */
    @Cacheable(cacheNames = ["yandex-stocks"], key = "#fboCampaignId")
    override fun getStocks(fboCampaignId: String) : List<Product> {
        val positions = positionRepository.findAll().filter { StringUtils.isNotBlank(it.yandexArtikul) }
        if (positions.isEmpty()) {
            log.info("No positions with yandex artikul...skipping")
            return listOf()
        }

        if (fboCampaignId.isEmpty()) {
            throw YandexException("Failed to get campaign id")
        }
//        val warehouses = yandexMarketClient.getWarehouses()
        val stocks = yandexMarketClient.getFbyStocks(fboCampaignId.toLong())
        if (stocks == null) {
            log.warn("No stocks in yandex market warehouses, campaignId {}", fboCampaignId)
            return listOf()
        }

        return stocks.flatMap { it.offers }
            .groupBy { it.offerId }
            .mapValues {
                val yandexArtikul = it.key
                it.value
                    .flatMap { item -> item.stocks ?: listOf() }
                    .filter { item -> item.type == WarehouseStockType.FIT }
                    .map { item ->
                        val position = positions.firstOrNull { positionEntity -> positionEntity.yandexArtikul == yandexArtikul }
                        if (position == null) {
                            log.warn("No position found for artikul {}", yandexArtikul)
                            null
                        } else {
                            Product(price = position.costPrice, costPrice = position.costPrice, addCost = position.additionalCost,
                                artikul = position.artikul, sku = position.ozonId, yandexArtikul = yandexArtikul, totalStock = item.count.toInt(),
                                name = position.name, fboStock = item.count.toInt())
                        }
                    }
            }
            .filter { it.value.isNotEmpty() }
            .filter { it.value.none { item -> item == null } }
            .mapValues {
                val first = it.value.filterNotNull().first()
                val count = it.value.filterNotNull().sumOf { item -> item.totalStock }
                val fby = it.value.filterNotNull().sumOf { item -> item.fboStock }

                Product(price = first.price, costPrice = first.costPrice, addCost = first.addCost,
                    artikul = first.artikul, sku = first.sku, yandexArtikul = it.key, totalStock = count,
                    name = first.name, fboStock = fby)
            }.values.toList()
    }

    @Cacheable(cacheNames = ["yandex-transactions"], key = "#key")
    override fun getTransaction(from: LocalDate, to: LocalDate, key: String, statuses: Set<OrderStatusType>): List<OrdersStatsOrderDTO> {
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

    override fun getOrders(from: LocalDate, to: LocalDate, statuses: Set<OrderStatusType>, campaignId: Long): List<OrderDTO> {
        kotlin.runCatching {
            val orders = ArrayList<OrderDTO>()
            val period = Period.between(from, to)
            val numberOfDays = period.days + period.months * 31 + period.years * 365
            if (numberOfDays > 30) {
                for (i in 0..<numberOfDays step 30) {
                    val fromDate = from.plusDays(i.toLong())
                    var toDate = fromDate.plusDays(30)
                    if (toDate.isAfter(to)) {
                        toDate = to
                    }
                    val resp = yandexMarketClient.getOrdersWithoutStats(campaignId, statuses, fromDate, toDate)
                    orders.addAll(resp)
                }
            } else {
                val resp = yandexMarketClient.getOrdersWithoutStats(campaignId, statuses, from, to)
                orders.addAll(resp)
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

    override fun getStocksReport(from: LocalDate, to: LocalDate, campaignId: Long) : HashMap<String, Int> {
        val filePath = yandexMarketClient.createStocksReport(campaign = campaignId, from = from, to = to)
        ProcessBuilder()
            .command("mkdir", "/tmp/ozon-stocks-report/")
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()

        ProcessBuilder()
            .command("unzip", filePath, "-d", "/tmp/ozon-stocks-report/")
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()

        val result = processStocksReport("/tmp/ozon-stocks-report/movement_by_sku.csv")

        ProcessBuilder()
            .command("rm", "-r", "/tmp/ozon-stocks-report")
            .start()
            .waitFor()
        return result
    }

    private fun processReport(file: String, header: String, consumer: Consumer<Double>) {
        val sum = csvReader().readAllWithHeader(File(file))
            .map { it[header] }
            .filter { !it.isNullOrEmpty() }
            .sumOf { it!!.toDouble() }
        consumer.accept(sum)
    }

    private fun processStocksReport(file: String) : HashMap<String, Int> {
        val stocksMap = HashMap<String, Int>()
        csvReader().readAllWithHeader(File(file))
            .map {
                val artikul = it["SHOP_SKU"]
                val income = it["SHIPMENTS_INCOME"]
                Pair(artikul, income) }
            .filter { it.first != null && it.second != null }
            .groupBy { it.first }
            .forEach {
                val total = it.value.map { item -> item.second }
                    .filter { item -> !item.isNullOrEmpty() }
                    .sumOf { item -> item!!.toInt() }
                stocksMap[it.key!!] = total
            }
        return stocksMap
    }

    enum class ReportType {
        CrossDoc, PaidStorage, Shelf, Utilization, ReceptionSurplus
    }
}