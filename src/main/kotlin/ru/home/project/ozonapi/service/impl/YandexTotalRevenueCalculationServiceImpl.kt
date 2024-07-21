package ru.home.project.ozonapi.service.impl

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.dto.finance.response.OperationType
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.dto.response.RevenueResponse
import ru.home.project.ozonapi.entity.MarketType
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.RevenueCalculationService
import ru.home.project.ozonapi.service.TotalRevenueCalculationService
import ru.home.project.ozonapi.service.YandexService
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.format.DateTimeFormatter

/**
 * @author rlagay
 */
@Service
class YandexTotalRevenueCalculationServiceImpl(
    val yandexPositionCalculationService: RevenueCalculationService,
    val positionRepository: PositionRepository,
    val yandexService: YandexService
) : TotalRevenueCalculationService {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    companion object {
        val log: Logger = LoggerFactory.getLogger(YandexTotalRevenueCalculationServiceImpl::class.java)
    }

    override fun calculateRevenue(request: RevenueRequest): List<RevenueResponse> {
        val revenueList = ArrayList<RevenueResponse>()

        if (request.from == null || request.to == null) {
            PositionRevenueCalculationServiceImpl.log.warn("Time period is not specified, request '$request'")
            revenueList.forEach { it.errorMessage = "Отсутствует период для расчета" }
            return revenueList
        }

        val positions = positionRepository.findAll().filter { StringUtils.isNotBlank(it.yandexArtikul) }

        val cacheKey = "from_" + request.from.format(formatter) + "_to_" + request.to.format(formatter)
        val transactions = yandexService.getTransaction(request.from.toLocalDate(), request.to.toLocalDate(), cacheKey)

        positions.chunked(3)
            .parallelStream()
            .forEach { list ->
                list.stream().forEach {
                    val positionRequest = RevenueRequest(name = it.name, artikul = it.yandexArtikul, postingNumber = null,
                        from = request.from, to = request.to, yandexOrders = transactions, type = MarketType.Yandex)
                    val positionResponse = yandexPositionCalculationService.calculateRevenue(positionRequest)
                    positionResponse?.let { resp -> revenueList.add(resp) }
            }
        }
        log.info("Calculated revenue for '${revenueList.size}'")

        // Расходы на рекламу
        val marketing = revenueList.sumOf { it.marketing }

        // Количество проданных товаров за период
        val soldItems = revenueList.sumOf { it.soldItemsCount }

        // Расходы на отзывы
        val feedback = 0.0

        // Начисление по претензиям
        val otherIncome = 0.0
        val pinFeedback = 0.0
        val feedBackTotal = feedback + pinFeedback

        // Расходы на утилизацию
        val destroyFee = 0.0

        // Расходы на кросс док
        val crossDoc = 0.0

        // Расходы на размещение товара
        val storage = 0.0

        // Корректировка
        val correction = 0.0

        // Обработка брака с приемки
        val spoilageSurplus = 0.0

        val compensationIncome = 0.0

        val courierReturnDelivery = 0.0

        // Чистая прибыль
        var totalRevenue = revenueList
            .map(RevenueResponse::totalRevenue)
            .sum()
        totalRevenue += feedback + pinFeedback + destroyFee + marketing + compensationIncome + crossDoc + correction + spoilageSurplus + courierReturnDelivery + storage + otherIncome
        totalRevenue = BigDecimal(totalRevenue).setScale(2, RoundingMode.HALF_UP).toDouble()

        // Всего доставлено
        val totalDeliveries = revenueList.sumOf { it.deliveryItemCount }

        // Всего возвратов
        val totalRefunds = revenueList.sumOf { it.refundCount }

        // Себестоимость проданного товара
        val costPrice = revenueList.sumOf { it.costPrice }

        val totalPrice = revenueList.sumOf { it.price }
        val totalCommission = revenueList.sumOf { it.saleCommission }
        val totalLogistic = revenueList.sumOf { it.logistic }
        val totalRefundCosts = revenueList.sumOf { it.refund }
        val taxes = revenueList.sumOf { it.taxes }

        revenueList.forEach {
            it.apply {
                totalRevenueForAllDeliveredItems = totalRevenue
                it.totalPrice = totalPrice
                totalCommissionCosts = BigDecimal(totalCommission).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalLogisticCosts = BigDecimal(totalLogistic).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalDeliveryItemCount = totalDeliveries
                totalRefundsCount = totalRefunds
                totalTaxes = BigDecimal(taxes).setScale(2, RoundingMode.HALF_UP).toDouble()
                soldItemsCount = soldItems
                marketingCosts = BigDecimal(marketing).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalRefund = BigDecimal(totalRefundCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalCostPrice = BigDecimal(costPrice).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
        }

        return revenueList
    }
}