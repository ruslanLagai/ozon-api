package ru.home.project.ozonapi.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.dto.finance.response.OperationType
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.dto.response.RevenueResponse
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.RevenueCalculationService
import ru.home.project.ozonapi.service.TotalRevenueCalculationService
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.format.DateTimeFormatter

/**
 * @author rlagay
 */
@Service
class TotalRevenueCalculationServiceImpl(
    val positionCalculationService: RevenueCalculationService,
    val positionRepository: PositionRepository,
    val ozonService: OzonService
) : TotalRevenueCalculationService {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    companion object {
        val log: Logger = LoggerFactory.getLogger(TotalRevenueCalculationServiceImpl::class.java)
    }

    override fun calculateRevenue(request: RevenueRequest): List<RevenueResponse> {
        val revenueList = ArrayList<RevenueResponse>()

        if (request.from == null || request.to == null) {
            PositionRevenueCalculationServiceImpl.log.warn("Time period is not specified, request '$request'")
            revenueList.forEach { it.errorMessage = "Отсутствует период для расчета" }
            return revenueList
        }

        val positions = positionRepository.findAll()

        val cacheKey = "from_" + request.from.format(formatter) + "_to_" + request.to.format(formatter)
        val transactions = ozonService.getTransaction(request.from, request.to, cacheKey)

        positions.chunked(5)
            .parallelStream()
            .forEach { list ->
                list.stream().forEach {
                    val positionRequest = RevenueRequest(name = it.name, artikul = it.artikul, postingNumber = null,
                        from = request.from, to = request.to, transactions = ArrayList<Transaction>(transactions))
                    val positionResponse = positionCalculationService.calculateRevenue(positionRequest)
                    positionResponse?.let { resp -> revenueList.add(resp) }
            }
        }
        log.info("Calculated revenue for '${revenueList.size}'")

        // Расходы на рекламу
        val promotionInSearch = transactions
            .filter { transaction -> transaction.operationType == OperationType.OperationElectronicServicesPromotionInSearch }
            .sumOf { transaction -> transaction.income }
        val stencil = transactions
            .filter { transaction -> transaction.operationType == OperationType.OperationElectronicServiceStencil }
            .sumOf { transaction -> transaction.income }
        var marketing = promotionInSearch + stencil

        if (marketing == 0.0) {
            marketing = transactions
                .filter { transaction -> transaction.operationType == OperationType.MarketplaceMarketingActionCostOperation }
                .sumOf { transaction -> transaction.income }
        }


        // Расходы на подписку
        val premiumSubscription = transactions
            .filter { transaction -> transaction.operationType == OperationType.OperationMarketplacePremiumSubscribtion }
            .sumOf { transaction -> transaction.income }

        // Количество проданных товаров за период
        val transactionsWithRefund = transactions
            .filter { transaction -> transaction.operationType == OperationType.MarketplaceRedistributionOfAcquiringOperation }
            .groupBy { transaction -> transaction.posting.postingNumber }
            .filter { (_, v) -> v.size > 1 }
        val soldItems = transactions
            .asSequence()
            .filter { transaction -> transaction.operationType == OperationType.MarketplaceRedistributionOfAcquiringOperation }
            .filter { transaction -> !transactionsWithRefund.containsKey(transaction.posting.postingNumber) }
            .filter { transaction -> transaction.income < 0 }
            .map { transaction -> transaction.items.size }
            .sum()

        // Расходы на отзывы
        val feedback = transactions
            .filter { transaction -> transaction.operationType == OperationType.MarketplaceSaleReviewsOperation }
            .sumOf { transaction -> transaction.income }
        val pinFeedback = transactions
            .filter { transaction -> transaction.operationType == OperationType.OperationMarketPlaceItemPinReview }
            .sumOf { transaction -> transaction.income }
        val feedBackTotal = feedback + pinFeedback

        // Расходы на утилизацию
        val destroyFee = transactions
            .filter {transaction -> transaction.operationType == OperationType.OperationMarketplaceServiceStockDisposal }
            .sumOf { transaction -> transaction.income }

        // Расходы на видеообложку
        val videoCover = transactions
            .filter {transaction -> transaction.operationType == OperationType.MarketplaceServiceItemVideoCover }
            .sumOf { transaction -> transaction.income }

        // Расходы на кросс док
        val crossDoc = transactions
            .filter { transaction -> transaction.operationType == OperationType.OperationMarketplaceCrossDockServiceWriteOff
                    || transaction.operationType == OperationType.MarketplaceServiceItemCrossdocking
                    || transaction.operationType == OperationType.OperationMarketplaceSupplyAdditional }
            .sumOf { transaction -> transaction.income }

        // Расходы на размещение товара
        val storage = transactions
            .filter { transaction -> transaction.operationType == OperationType.OperationMarketplaceServiceStorage }
            .sumOf { transaction -> transaction.income }

        // Корректировка
        val correction = transactions
            .filter { transaction -> transaction.operationType == OperationType.MarketplaceSellerCorrectionOperation }
            .sumOf { transaction -> transaction.income }

        // Обработка брака с приемки
        val spoilageSurplus = transactions
            .filter { transaction -> transaction.operationType == OperationType.OperationMarketplaceServiceProcessingSpoilageSurplus }
            .sumOf { transaction -> transaction.income }

        val compensationIncome = transactions.filter {
            it.operationType == OperationType.OperationDefectiveWriteOff || it.operationType == OperationType.AccrualConsigDefectiveWriteOff
        }.sumOf { it.income }

        val courierReturnDelivery = transactions.filter {
            it.operationType == OperationType.OperationMarketplaceSellerReturnsGeneral
        }.sumOf { it.income }

        // Чистая прибыль
        var totalRevenue = revenueList
            .map(RevenueResponse::totalRevenue)
            .sum()
        totalRevenue += feedback + pinFeedback + destroyFee + premiumSubscription + marketing + compensationIncome + crossDoc + videoCover + correction + spoilageSurplus + courierReturnDelivery + storage
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
        val totalLastMile = revenueList.sumOf { it.lastMile }
        val totalRefundCosts = revenueList.sumOf { it.refund }
        val taxes = revenueList.sumOf { it.taxes }

        revenueList.forEach {
            it.apply {
                totalRevenueForAllDeliveredItems = totalRevenue
                it.totalPrice = totalPrice
                totalCommissionCosts = BigDecimal(totalCommission).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalLastMileCosts = BigDecimal(totalLastMile).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalLogisticCosts = BigDecimal(totalLogistic).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalDeliveryItemCount = totalDeliveries
                totalRefundsCount = totalRefunds
                totalTaxes = BigDecimal(taxes).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalFeedBackCost = feedBackTotal
                it.pinFeedback = pinFeedback
                soldItemsCount = soldItems
                marketingCosts = BigDecimal(marketing).setScale(2, RoundingMode.HALF_UP).toDouble()
                it.stencil = BigDecimal(stencil).setScale(2, RoundingMode.HALF_UP).toDouble()
                it.promotionInSearch = BigDecimal(promotionInSearch).setScale(2, RoundingMode.HALF_UP).toDouble()
                feedbackCosts = feedback
                destroyCosts = destroyFee
                premium = premiumSubscription
                totalRefund = BigDecimal(totalRefundCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                compensation = compensationIncome
                xDoc = BigDecimal(crossDoc).setScale(2, RoundingMode.HALF_UP).toDouble()
                spoilageCosts = spoilageSurplus
                videoCoverCosts = videoCover
                storageCosts = BigDecimal(storage).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalCostPrice = BigDecimal(costPrice).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
        }

        return revenueList
    }
}