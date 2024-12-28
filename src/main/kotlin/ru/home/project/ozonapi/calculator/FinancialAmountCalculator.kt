package ru.home.project.ozonapi.calculator

import org.openapitools.client.models.*
import org.springframework.stereotype.Component
import ru.home.project.ozonapi.dto.finance.response.AdditionalServiceType
import ru.home.project.ozonapi.dto.finance.response.Transaction

/**
 * @author rlagay
 */
@Component
class FinancialAmountCalculator: FinancialDataCalculator {

    override fun calculateRevenue(transaction: Transaction): Double {
        return transaction.income
    }

    override fun calculatePrice(transaction: Transaction): Double {
        return transaction.price
    }

    override fun calculateCommission(transaction: Transaction): Double {
        return transaction.saleCommission
    }

    override fun calculateLogistic(transaction: Transaction): Double {
        return transaction.services
            .filter { it.name == AdditionalServiceType.MarketplaceServiceItemDirectFlowLogisticVDC
                    || it.name == AdditionalServiceType.MarketplaceServiceItemDirectFlowLogistic}
            .sumOf { it.price ?: 0.0 }
    }

    override fun calculateLastMile(transaction: Transaction): Double {
        return transaction.services
            .filter { it.name == AdditionalServiceType.MarketplaceServiceItemDelivToCustomer }
            .sumOf { it.price ?: 0.0 }
    }

    override fun calculateRefund(transaction: Transaction): Double {
        return transaction.income
    }

    override fun calculateYandexRevenue(order: OrdersStatsOrderDTO): Double {
        var payment = 0.0
        val processedPayments = ArrayList<String>()
        order.payments
            .filter { !processedPayments.contains(it.id) }
            .forEach {
                if (OrdersStatsPaymentType.PAYMENT == it.type && it.total != null) {
                    payment += it.total!!.toDouble()
                } else if (OrdersStatsPaymentType.REFUND == it.type && it.total != null) {
                    payment -= it.total!!.toDouble()
                }
                it.id?.let { item -> processedPayments.add(item) }
            }
        var commission = 0.0
        order.commissions.forEach {
            if (it.actual != null) {
                commission += it.actual!!.toDouble()
            }
        }
        var subsidies = 0.0
        order.subsidies?.forEach {
            if (it.operationType == OrdersStatsSubsidyOperationType.ACCRUAL && it.type == OrdersStatsSubsidyType.SUBSIDY) {
                subsidies += it.amount.toDouble()
            }
        }

        return payment + subsidies - commission
    }

    override fun calculateYandexPrice(order: OrdersStatsOrderDTO): Double {
        var payment = 0.0
        val processedPayments = ArrayList<String>()
        order.payments
            .filter { !processedPayments.contains(it.id) }
            .forEach {
                if (OrdersStatsPaymentType.PAYMENT == it.type && it.total != null) {
                    payment += it.total!!.toDouble()
                    it.id?.let { item -> processedPayments.add(item) }
                }
            }
        return payment
    }

    override fun calculateYandexCommission(order: OrdersStatsOrderDTO): Double {
        return order.commissions
            ?.filter { OrdersStatsCommissionType.FEE == it.type }
            ?.filter { it.actual != null }
            ?.sumOf { it.actual!!.toDouble() } ?: 0.0
    }

    override fun calculateYandexDelivery(order: OrdersStatsOrderDTO): Double {
        return order.commissions
            .filter { OrdersStatsCommissionType.DELIVERY_TO_CUSTOMER == it.type
                    || OrdersStatsCommissionType.FULFILLMENT == it.type
                    || OrdersStatsCommissionType.EXPRESS_DELIVERY_TO_CUSTOMER == it.type
                    || OrdersStatsCommissionType.SORTING == it.type
                    || OrdersStatsCommissionType.INTAKE_SORTING == it.type
                    || OrdersStatsCommissionType.RETURNED_ORDERS_STORAGE == it.type
                    || OrdersStatsCommissionType.RETURN_PROCESSING == it.type
            }
            .filter { it.actual != null }
            .sumOf { it.actual!!.toDouble() } ?: 0.0
    }

    override fun calculateYandexMarketing(order: OrdersStatsOrderDTO): Double {
        return order.commissions
            .filter { OrdersStatsCommissionType.AUCTION_PROMOTION == it.type
                    || OrdersStatsCommissionType.LOYALTY_PARTICIPATION_FEE == it.type
            }
            .filter { it.actual != null }
            .sumOf { it.actual!!.toDouble() } ?: 0.0
    }

    override fun calculateYandexAcquiring(order: OrdersStatsOrderDTO): Double {
        return order.commissions
            ?.filter { OrdersStatsCommissionType.PAYMENT_TRANSFER == it.type
                    || OrdersStatsCommissionType.AGENCY == it.type
            }
            ?.filter { it.actual != null }
            ?.sumOf { it.actual!!.toDouble() } ?: 0.0
    }
}