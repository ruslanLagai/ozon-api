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
class OzonTotalRevenueCalculationServiceImpl(
    val positionCalculationService: RevenueCalculationService,
    val positionRepository: PositionRepository,
    val ozonService: OzonService
) : TotalRevenueCalculationService {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    companion object {
        val log: Logger = LoggerFactory.getLogger(OzonTotalRevenueCalculationServiceImpl::class.java)
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

        positions.chunked(100)
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

        var promotionInSearch = 0.0
        var externalPromotion = 0.0
        var stencil = 0.0
        var gettingToTop = 0.0
        var specialPlacing = 0.0
        var promotionPerClick = 0.0
        var pushNotifications = 0.0
        var oldMarketing = 0.0
        var premiumSubscription = 0.0
        var deliveryToCustomer = 0.0
        var feedback = 0.0
        var pinFeedback = 0.0
        var bonuses = 0.0
        var returnFromOzonStock = 0.0
        var videoCover = 0.0
        var crossDoc = 0.0
        var realFbsLateDeliveryFee = 0.0
        var storage = 0.0
        var starMembershipCount = 0
        var installmentCount = 0
        var starMembership = 0.0
        var installment = 0.0
        var correction = 0.0
        var sorting = 0.0
        var spoilageSurplus = 0.0
        var ozonPackaging = 0.0
        var destroyFee = 0.0
        var compensationIncome = 0.0
        var courierReturnDelivery = 0.0
        for (transaction in transactions) {
            when (transaction.operationType) {
                OperationType.OperationElectronicServicesPromotionInSearch -> promotionInSearch += transaction.income
                OperationType.OperationPromotionWithCostPerOrder -> promotionInSearch += transaction.income
                OperationType.OperationMarketplaceExternalPromotion -> externalPromotion += transaction.income
                OperationType.OperationElectronicServiceStencil -> stencil += transaction.income
                OperationType.OperationGettingToTheTop -> gettingToTop += transaction.income
                OperationType.OperationOtherElectronicServices -> specialPlacing += transaction.income
                OperationType.OperationMarketplaceCostPerClick -> promotionPerClick += transaction.income
                OperationType.OperationMarketplaceSendingPushNotifications -> pushNotifications += transaction.income
                OperationType.MarketplaceMarketingActionCostOperation -> oldMarketing += transaction.income
                OperationType.OperationMarketplacePremiumSubscribtion -> premiumSubscription += transaction.income
                OperationType.OperationSubscriptionPremium -> premiumSubscription += transaction.income
                OperationType.MarketplaceSellerReexposureDeliveryReturnOperation -> deliveryToCustomer += transaction.income
                OperationType.MarketplaceSaleReviewsOperation -> feedback += transaction.income
                OperationType.OperationPointsForReviews -> feedback += transaction.income
                OperationType.OperationMarketplaceAcceleratedProductReviews -> feedback += transaction.income
                OperationType.OperationMarketPlaceItemPinReview -> pinFeedback += transaction.income
                OperationType.OperationMarketplaceServicePremiumCashbackBonusAccrual -> bonuses += transaction.income
                OperationType.OperationMarketplaceServicePreparingToReturn -> returnFromOzonStock += transaction.income
                OperationType.OperationSellerReturnsCargoAssortmentValid -> returnFromOzonStock += transaction.income
                OperationType.MarketplaceServiceItemVideoCover -> videoCover += transaction.income
                OperationType.OperationMarketplaceCrossDockServiceWriteOff -> crossDoc += transaction.income
                OperationType.MarketplaceServiceItemCrossdocking -> crossDoc += transaction.income
                OperationType.OperationMarketplaceSupplyAdditional -> crossDoc += transaction.income
                OperationType.OperationMarketplaceServiceProcessingNotIdentifiedSurplus -> crossDoc += transaction.income
                OperationType.DefectRateDeliveryDelayNonInt -> realFbsLateDeliveryFee += transaction.income
                OperationType.DefectRateCancellation -> realFbsLateDeliveryFee += transaction.income
                OperationType.OperationMarketplaceServiceStorage -> storage += transaction.income
                OperationType.OperationMarketplaceItemTemporaryStorageRedistribution -> storage += transaction.income
                OperationType.TemporaryStorage -> storage += transaction.income
                OperationType.StarsMembership -> {
                    starMembershipCount += transaction.items.size
                    starMembership += transaction.income
                }
                OperationType.MarketplaceSellerInstallmentOperation -> {
                    installmentCount += transaction.items.size
                    installment += transaction.income
                }
                OperationType.MarketplaceSellerCorrectionOperation -> correction += transaction.income
                OperationType.MarketplaceCorrectionPointOperation -> correction += transaction.income
                OperationType.OperationMarketplaceServiceSupplyInboundCrossZoneAcceptance -> sorting += transaction.income
                OperationType.OperationMarketplaceServiceProcessingSpoilageSurplus -> spoilageSurplus += transaction.income
                OperationType.OperationMarketplacePackageMaterialsProvision -> ozonPackaging += transaction.income
                OperationType.OperationMarketplacePackageRedistribution -> ozonPackaging += transaction.income
                OperationType.OperationMarketplaceServiceStockDisposal -> destroyFee += transaction.income
                OperationType.DisposalReasonFailedToPickupOnTime -> destroyFee += transaction.income
                OperationType.DisposalReasonDamagedPackaging -> destroyFee += transaction.income
                OperationType.DisposalReasonDamagedReturn -> destroyFee += transaction.income
                OperationType.DisposalReasonRezon -> destroyFee += transaction.income
                OperationType.OperationSellerReturnsCargoAssortmentInvalid -> destroyFee += transaction.income
                OperationType.SellerReturnsDeliveryToPickupPoint -> destroyFee += transaction.income
                OperationType.DisposalReasonScattered -> destroyFee += transaction.income
                OperationType.OperationDefectiveWriteOff -> compensationIncome += transaction.income
                OperationType.AccrualConsigDefectiveWriteOff -> compensationIncome += transaction.income
                OperationType.AccrualInternalClaim -> compensationIncome += transaction.income
                OperationType.AccrualConsigWriteOff -> compensationIncome += transaction.income
                OperationType.AccrualWithoutDocs -> compensationIncome += transaction.income
                OperationType.MarketplaceSellerDecompensationItemByTypeDocOperation -> compensationIncome += transaction.income
                OperationType.OperationMarketplaceSellerReturnsGeneral -> courierReturnDelivery += transaction.income


                else -> {
                }
            }
        }
        // Расходы на рекламу
        var marketing = promotionInSearch + stencil + gettingToTop + specialPlacing + promotionPerClick + pushNotifications + externalPromotion
        if (marketing == 0.0) {
            marketing = oldMarketing
        }
        val feedBackTotal = feedback + pinFeedback

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
            .sumOf { transaction -> transaction.items.size }

        // Чистая прибыль
        var totalRevenue = revenueList.sumOf(RevenueResponse::totalRevenue)
        totalRevenue += feedback + pinFeedback + destroyFee + premiumSubscription + marketing + compensationIncome +
                videoCover + correction + spoilageSurplus + courierReturnDelivery + storage +
                starMembership + installment + deliveryToCustomer + realFbsLateDeliveryFee + sorting + bonuses + ozonPackaging
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
                it.gettingToTop = BigDecimal(gettingToTop).setScale(2, RoundingMode.HALF_UP).toDouble()
                it.promotionInSearch = BigDecimal(promotionInSearch).setScale(2, RoundingMode.HALF_UP).toDouble()
                it.specialPlacing = BigDecimal(specialPlacing).setScale(2, RoundingMode.HALF_UP).toDouble()
                it.promotionPerClick = BigDecimal(promotionPerClick).setScale(2, RoundingMode.HALF_UP).toDouble()
                feedbackCosts = BigDecimal(feedback).setScale(2, RoundingMode.HALF_UP).toDouble()
                destroyCosts = destroyFee
                premium = premiumSubscription
                totalRefund = BigDecimal(totalRefundCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                compensation = compensationIncome
                xDoc = BigDecimal(crossDoc).setScale(2, RoundingMode.HALF_UP).toDouble()
                spoilageCosts = spoilageSurplus
                videoCoverCosts = videoCover
                storageCosts = BigDecimal(storage).setScale(2, RoundingMode.HALF_UP).toDouble()
                totalCostPrice = BigDecimal(costPrice).setScale(2, RoundingMode.HALF_UP).toDouble()
                it.starMembership = BigDecimal(starMembership).setScale(2, RoundingMode.HALF_UP).toDouble()
                it.starMembershipCount = starMembershipCount
                it.installmentCount = installmentCount
                it.installment = BigDecimal(installment).setScale(2, RoundingMode.HALF_UP).toDouble()
                stockReturn = BigDecimal(returnFromOzonStock).setScale(2, RoundingMode.HALF_UP).toDouble()
                rfbsDelivery = BigDecimal(deliveryToCustomer).setScale(2, RoundingMode.HALF_UP).toDouble()
                it.sorting = sorting
                it.bonuses = bonuses
                packaging = ozonPackaging
                push = pushNotifications
                it.externalPromotion = BigDecimal(externalPromotion).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
        }

        return revenueList
    }
}