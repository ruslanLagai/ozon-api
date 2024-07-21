package ru.home.project.ozonapi.service.impl

import org.apache.commons.lang3.StringUtils
import org.openapitools.client.models.OrderStatsStatusType
import org.openapitools.client.models.OrdersStatsItemStatusType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientException
import ru.home.project.ozonapi.calculator.FinancialDataCalculator
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.dto.response.RevenueResponse
import ru.home.project.ozonapi.entity.MarketType
import ru.home.project.ozonapi.model.PositionFinanceData
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.RevenueCalculationService
import ru.home.project.ozonapi.service.YandexService
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author rlagay
 */
@Service(value = "yandexPositionCalculationService")
class YandexPositionRevenueCalculationServiceImpl(
    val yandexService: YandexService,
    val calculators: List<FinancialDataCalculator>,
    val positionRepository: PositionRepository
) : RevenueCalculationService {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    private val taxPercentage: Double = 0.07

    companion object {
        val log: Logger = LoggerFactory.getLogger(YandexPositionRevenueCalculationServiceImpl::class.java)
    }

    override fun calculateRevenue(request: RevenueRequest): RevenueResponse? {
        if (StringUtils.isBlank(request.name) || request.type != MarketType.Yandex) {
            log.debug("Position name is empty, skipping calculation for position")
            return null
        }

        val response = RevenueResponse(request.name, "", request.artikul, "")

        try {
            if (request.from == null || request.to == null) {
                log.warn("Time period is not specified, request '$request'")
                response.errorMessage = "Отсутствует период для расчета"
                return response
            }

            val positionEntity = request.name?.let { positionRepository.getPositionEntityByName(it) }
            if (positionEntity == null) {
                log.warn("No data from DB for '${request.name}'")
                response.errorMessage = "Отсутствуют данные по товару в БД"
                return response
            }

            val cacheKey = "from_" + request.from.format(formatter) + "_to_" + request.to.format(formatter)
            val allTransactions = request.yandexOrders ?: yandexService.getTransaction(request.from.toLocalDate(), request.to.toLocalDate(), cacheKey)

            val sold = 0

            val transactions = allTransactions
                .filter {
                    val items = it.items
                    !items.isNullOrEmpty() && items.first().shopSku == positionEntity.yandexArtikul
                }

            if (transactions.isEmpty()) {
                log.warn("Not enough transactions for calculating average revenue")
                response.errorMessage = "Отсутствуют доставки за выбранный период"
                return response
            }
            log.debug("Retrieve '{}' transactions from '{}' to '{}'", transactions.size, request.from, request.to)

            var refunds = 0
            var deliveries = 0
            val revenues = transactions.map {
                var refundCosts = 0.0
                val isRefund = it.status == OrderStatsStatusType.RETURNED
                        || it.status == OrderStatsStatusType.CANCELLED_BEFORE_PROCESSING
                        || it.status == OrderStatsStatusType.CANCELLED_IN_DELIVERY
                        || it.status == OrderStatsStatusType.CANCELLED_IN_PROCESSING

                // налог должен считаться тут от цены продажи!
                val income = calculators.sumOf { calculator -> calculator.calculateYandexRevenue(it) }
                val price = calculators.sumOf { calculator -> calculator.calculateYandexPrice(it) }
                val commission = calculators.sumOf { calculator -> calculator.calculateYandexCommission(it) }
                val logistic = calculators.sumOf { calculator -> calculator.calculateYandexDelivery(it) }
                val marketing = calculators.sumOf { calculator -> calculator.calculateYandexMarketing(it) }
                val acquiring = calculators.sumOf { calculator -> calculator.calculateYandexAcquiring(it) }
                val taxes = taxPercentage * price

                if (it.status == OrderStatsStatusType.DELIVERED && it.items != null) {
                    deliveries += it.items!!.sumOf { item -> item.count ?: 0 }
                } else if ((it.status == OrderStatsStatusType.PARTIALLY_RETURNED
                    || it.status == OrderStatsStatusType.PARTIALLY_DELIVERED) && it.items != null) {
                    val deliveredItem = it.items!!.sumOf { item -> item.count ?: 0 }
                    val returned = AtomicInteger()
                    it.items!!.forEach { item ->
                        item.details?.let { detail ->
                            detail.filter { i -> i.itemStatus == OrdersStatsItemStatusType.RETURNED }
                                .forEach { i -> returned.addAndGet(i.itemCount?.toInt() ?: 0) }
                        }
                    }
                    refunds += returned.get()
                    deliveries = deliveries + deliveredItem - returned.get()
                } else if (isRefund) {
                    if (it.items != null && !it.commissions.isNullOrEmpty()) {
                        refunds += it.items!!.sumOf { item -> item.count ?: 0 }
                    }
                    refundCosts = commission + logistic + marketing + acquiring
                }

                PositionFinanceData(revenue = income, taxes = taxes, price = price, commission = commission,
                    logistic = logistic, refund = refundCosts, lastMile = 0.0, marketing = marketing, acquiring = acquiring)
            }.filter { positionFinanceData -> positionFinanceData.revenue != 0.0 || positionFinanceData.refund != 0.0 }

            val tax = revenues.sumOf { item -> if (item.revenue > 0) item.taxes else { 0.0 } }
            var totalRevenue = revenues.sumOf { item -> item.revenue }
            val logisticCosts = revenues.sumOf { item -> item.logistic }
            val commissionCosts = revenues.sumOf { item -> item.commission }
            val marketingCosts = revenues.sumOf { item -> item.marketing }
            val totalPrice = revenues.sumOf { item -> item.price }
            val refundCosts = revenues.sumOf { item -> item.refund }
            val acquiringCosts = revenues.sumOf { item -> item.acquiring }
            var positionCostPrice = 0.0

            var averageRevenue = if (deliveries != 0) { totalRevenue / deliveries } else { 0.0 }

            // пропускаем, если в периоде не было доставок (только отмены)
            if (totalRevenue > 0) {
                val costPrice = positionEntity.costPrice + positionEntity.additionalCost
                positionCostPrice = costPrice * deliveries
                totalRevenue = totalRevenue - costPrice * deliveries
                averageRevenue -= costPrice
                log.debug("Cost price for '${request.name}' '$costPrice'" )
            }

            log.info("Revenue for '${request.name}' '$averageRevenue'" )
            log.info("Total revenue for position '${request.name}' for period is '$averageRevenue'" )

            response.apply {
                yandexId = positionEntity.yandexArtikul
                postingNumber = request.postingNumber
                refundCount = refunds
                taxes = BigDecimal(tax).setScale(2, RoundingMode.HALF_UP).toDouble()
                deliveryItemCount = deliveries
                soldItemsCount = sold
                response.averageRevenue = BigDecimal(averageRevenue).setScale(2, RoundingMode.HALF_UP).toDouble()
                response.totalRevenue = BigDecimal(totalRevenue).setScale(2, RoundingMode.HALF_UP).toDouble()
                logistic = BigDecimal(logisticCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                marketing = BigDecimal(marketingCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                saleCommission = BigDecimal(commissionCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                refund = BigDecimal(refundCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                price = BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP).toDouble()
                costPrice = BigDecimal(positionCostPrice).setScale(2, RoundingMode.HALF_UP).toDouble()
                acquiring = BigDecimal(acquiringCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
            return response
        } catch (e: WebClientException) {
            log.error("Error from Ozon for '${request.name}' from '${request.from}' to '${request.to}'", e)
            response.errorMessage = e.message
            return response
        }
    }
}