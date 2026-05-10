package ru.home.project.ozonapi.calculator

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
}