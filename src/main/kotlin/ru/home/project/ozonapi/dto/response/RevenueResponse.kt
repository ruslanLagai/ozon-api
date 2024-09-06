package ru.home.project.ozonapi.dto.response

/**
 * @author rlagay
 */
data class RevenueResponse(val name: String?, var ozonId: String?, val artikul: String?, var yandexId: String? = "") {
    var revenue: Double = 0.0
    var averageRevenue = 0.0
    var logistic = 0.0
    var lastMile = 0.0
    var refund = 0.0
    var totalRefund = 0.0
    var saleCommission = 0.0
    var price = 0.0
    var costPrice = 0.0
    var totalCostPrice = 0.0
    var taxes = 0.0
    var totalRevenue = 0.0
    var deliveryItemCount = 0
    var totalDeliveryItemCount = 0
    var totalRefundsCount = 0
    var totalRevenueForAllDeliveredItems = 0.0
    var totalLastMileCosts = 0.0
    var totalCommissionCosts = 0.0
    var totalLogisticCosts = 0.0
    var totalPrice = 0.0
    var totalTaxes = 0.0
    var postingNumber: String? = null
    var errorMessage: String? = null
    var marketingCosts: Double = 0.0
    var promotionInSearch: Double = 0.0
    var pinFeedback: Double = 0.0
    var totalFeedBackCost: Double = 0.0
    var stencil: Double = 0.0
    var shelf: Double = 0.0
    var feedbackCosts: Double = 0.0
    var destroyCosts: Double = 0.0
    var soldItemsCount: Int = 0
    var refundCount: Int = 0
    var premium: Double = 0.0
    var compensation: Double = 0.0
    var xDoc: Double = 0.0
    var spoilageCosts: Double = 0.0
    var videoCoverCosts = 0.0
    var storageCosts: Double = 0.0
    var marketing: Double = 0.0
    var acquiring: Double = 0.0
    var starMembership: Double = 0.0
    var starMembershipCount: Int = 0
    var installmentCount: Int = 0
    var installment: Double = 0.0
    var stockReturn: Double = 0.0
    var rfbsDelivery: Double = 0.0
}
