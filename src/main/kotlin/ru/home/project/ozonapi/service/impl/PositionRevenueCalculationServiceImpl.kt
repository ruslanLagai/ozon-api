package ru.home.project.ozonapi.service.impl

import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientException
import ru.home.project.ozonapi.calculator.FinancialDataCalculator
import ru.home.project.ozonapi.dto.finance.response.OperationType
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.dto.response.RevenueResponse
import ru.home.project.ozonapi.entity.MarketType
import ru.home.project.ozonapi.model.PositionFinanceData
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.RevenueCalculationService
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.format.DateTimeFormatter
import java.util.function.Predicate

/**
 * @author rlagay
 */
@Service(value = "positionCalculationService")
@Slf4j
class PositionRevenueCalculationServiceImpl(
    val ozonService: OzonService,
    val calculators: List<FinancialDataCalculator>,
    val positionRepository: PositionRepository
) : RevenueCalculationService {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    private val taxPercentage: Double = 0.07
    private val deliveryPattern = Regex("\\d+-\\d+-\\d+")

    private val deliveryOrRefundPredicate = Predicate<Transaction> {
        it.operationType == OperationType.OperationAgentDeliveredToCustomer
                || it.operationType == OperationType.OperationAgentStornoDeliveredToCustomer
                || it.operationType == OperationType.OperationAgentDeliveredToCustomerCanceled
                || it.operationType == OperationType.ClientReturnAgentOperation
                || it.operationType == OperationType.OperationItemReturn
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(PositionRevenueCalculationServiceImpl::class.java)
    }

    override fun calculateRevenue(request: RevenueRequest): RevenueResponse? {
        if (StringUtils.isBlank(request.name) || request.type != MarketType.Ozon) {
            log.debug("Position name is empty, skipping calculation for position")
            return null
        }
        if (StringUtils.isNoneBlank(request.postingNumber)) {
            log.debug("Skipping as posting number is not blank")
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
            val allTransactions = request.transactions ?: ozonService.getTransaction(request.from, request.to, cacheKey)

            var sold = 0

            val transactions = allTransactions
                .filter { transaction ->
                    val items = transaction.items
                    items.isNotEmpty() && items.first().sku == positionEntity.ozonId
                }
                .onEach { transaction ->
                    if (transaction.operationType == OperationType.MarketplaceRedistributionOfAcquiringOperation
                        && transaction.income < 0) {
                        sold++
                    }
                }
                .groupBy { transaction ->
                    val isDelivery = transaction.posting.postingNumber.matches(Regex("\\d+-\\d+-\\d+"))
                    if (isDelivery) {
                        transaction.posting.postingNumber.substringBeforeLast("-")
                    } else {
                        transaction.posting.postingNumber
                    }
                }
                .filterValues { transactions -> transactions.any { deliveryOrRefundPredicate.test(it) } }

            if (transactions.isEmpty()) {
                log.warn("Not enough transactions for calculating average revenue")
                response.errorMessage = "Отсутствуют доставки за выбранный период"
                return response
            }
            log.debug("Retrieve '{}' transactions from '{}' to '{}'", transactions.size, request.from, request.to)

            var refunds = 0
            var deliveries = 0
            val revenuesMap = transactions.mapValues {
                val transactionList = it.value.toMutableList()
                val key = if (it.key.matches(deliveryPattern)) { it.key.substringBeforeLast("-") } else { it.key }

                // возвраты: если есть возврат, то нужно получить транзакции по отправлению
                val transactionsWithRefunds = ArrayList<Transaction>()
                transactionList
                    .filter { item -> item.operationType == OperationType.ClientReturnAgentOperation
                        || item.operationType == OperationType.OperationItemReturn }
                    .forEach {item ->
                        if (item.operationType == OperationType.OperationMarketplaceServicePremiumCashbackIndividualPoints) {
                            transactionsWithRefunds.add(item)
                        }
                        val postingNumber = item.posting.postingNumber
                        if (transactionsWithRefunds.none { i -> postingNumber == i.posting.postingNumber }) {
                            val withRefund = ozonService.getTransaction(postingNumber)
                            transactionsWithRefunds.addAll(withRefund)
                        }
                    }
                var refundCosts = 0.0
                val isRefund = transactionsWithRefunds.isNotEmpty()
                if (transactionsWithRefunds.isNotEmpty()) {
                    refundCosts = transactionsWithRefunds
                        .filter { transaction -> deliveryOrRefundPredicate.test(transaction) }
                        .sumOf { transaction ->
                            calculators.sumOf { calculator -> calculator.calculateRefund(transaction) }
                        }
                    val returnItem = transactionsWithRefunds.firstOrNull { item -> item.operationType == OperationType.OperationItemReturn
                            || item.operationType == OperationType.ClientReturnAgentOperation}
                    refunds += returnItem?.items?.size ?: 0
                    transactionList.clear()
                    transactionList.addAll(transactionsWithRefunds)
                    transactionsWithRefunds.clear()
                }

                // встречались сбои озона, при которых приходит доставка и тут же отмена начисления
                var operationTypes = it.value.map(Transaction::operationType)
                var isFailedTransaction = operationTypes.contains(OperationType.OperationAgentDeliveredToCustomer)
                        && operationTypes.contains(OperationType.OperationAgentStornoDeliveredToCustomer)

                // нужно получить эквайринг, если отсутствует в списке транзакций
                val isNoAcquiring = transactionList.none { item -> item.operationType == OperationType.MarketplaceRedistributionOfAcquiringOperation }
                if (isNoAcquiring && transactionList.isNotEmpty()) {
                    val withAcquiring = ozonService.getTransaction(key)
                    transactionList.addAll(withAcquiring)
                }

                // встречались недостоверные транзакции при запросе за период: доставка до покупателя с income < 0
                val suspiciousDeliveries = it.value
                    .filter { transaction -> OperationType.OperationAgentDeliveredToCustomer == transaction.operationType }
                    .filter { transaction -> transaction.income < 0 }
                    .toList()
                if (suspiciousDeliveries.isNotEmpty() && !isFailedTransaction) {
                    log.warn("Received delivery transaction with income < 0")
                    suspiciousDeliveries.forEach { delivery ->
                        val posingNumber = delivery.posting.postingNumber
                        val trustedTransactions = ozonService.getTransaction(posingNumber)
                        transactionList.removeIf { transaction -> posingNumber == transaction.posting.postingNumber }
                        transactionList.addAll(trustedTransactions)
                        transactionList.removeIf { transaction ->
                            (OperationType.OperationAgentDeliveredToCustomer == transaction.operationType
                                    || OperationType.OperationAgentStornoDeliveredToCustomer == transaction.operationType)
                            && (transaction.operationDate.isBefore(request.from.toLocalDateTime())
                                    || transaction.operationDate.isAfter(request.to.toLocalDateTime()))
                        }
                        if (transactionList.count { transaction ->
                            transaction.operationType == OperationType.MarketplaceRedistributionOfAcquiringOperation
                        } == transactionList.size) {
                            transactionList.clear()
                        }
                    }
                }

                // встречались сбои озона, при которых приходит доставка и тут же отмена начисления
                val deliveryCancellation = transactionList
                    .filter { item -> item.operationType == OperationType.OperationAgentStornoDeliveredToCustomer }
                    .toList()
                val deliveryOperation = transactionList
                    .filter { item -> item.operationType == OperationType.OperationAgentDeliveredToCustomer }
                    .toList()
                operationTypes = transactionList.map(Transaction::operationType)
                isFailedTransaction = operationTypes.contains(OperationType.OperationAgentDeliveredToCustomer)
                        && operationTypes.contains(OperationType.OperationAgentStornoDeliveredToCustomer)
                        && deliveryOperation.any { deliveryOperationItem ->
                            deliveryCancellation.any { deliveryCancellationItem ->
                                deliveryCancellationItem.operationDate == deliveryOperationItem.operationDate }
                        }

                if (isFailedTransaction) {
                    transactionList.removeIf { item -> suspiciousDeliveries.contains(item) }
                    transactionList.removeIf { item ->
                        StringUtils.equalsIgnoreCase(item.operationType.description, "доставка покупателю — отмена начисления")
                                && suspiciousDeliveries.any { suspiciousDelivery -> suspiciousDelivery.operationDate == item.operationDate }
                    }
                    if (transactionList.none { item -> deliveryOrRefundPredicate.test(item) }) {
                        transactionList.clear()
                    }
                }

                // налог должен считаться тут от цены продажи!
                val income = if (!isRefund) {
                    transactionList
                        .sumOf { transaction ->
                            calculators.sumOf { calculator -> calculator.calculateRevenue(transaction) }
                        }
                } else { 0.0 }
                val price = transactionList
                    .filter { transaction -> transaction.operationType == OperationType.OperationAgentDeliveredToCustomer
                            && (transaction.operationDate.isBefore(request.to.toLocalDateTime()) || transaction.operationDate == request.to.toLocalDateTime())
                            && (transaction.operationDate.isAfter(request.from.toLocalDateTime()) || transaction.operationDate == request.from.toLocalDateTime())
                    }
                    .sumOf { transaction ->
                        calculators.sumOf { calculator -> calculator.calculatePrice(transaction) }
                    }
                val commission = if (!isRefund) {
                    transactionList
                        .filter { transaction -> transaction.operationType == OperationType.OperationAgentDeliveredToCustomer }
                        .sumOf { transaction ->
                            calculators.sumOf { calculator -> calculator.calculateCommission(transaction) }
                        }
                } else { 0.0 }
                val logistic = transactionList
                    .filter { transaction -> transaction.operationType == OperationType.OperationAgentDeliveredToCustomer
                            || transaction.operationType == OperationType.MarketplaceServiceItemServiceFeeRFBS }
                    .sumOf { transaction ->
                        calculators.sumOf { calculator -> calculator.calculateLogistic(transaction) }
                    }
                val lastMile = transactionList
                    .filter { transaction -> transaction.operationType == OperationType.OperationAgentDeliveredToCustomer }
                    .sumOf { transaction ->
                        calculators.sumOf { calculator -> calculator.calculateLastMile(transaction) }
                    }
                val taxes = taxPercentage * transactionList
                    .filter { transaction -> transaction.operationType == OperationType.OperationAgentDeliveredToCustomer }
                    .onEach { transaction ->
                        if (!isRefund) {
                            deliveries += transaction.items.size
                        }
                    }
                    .sumOf { transaction -> transaction.price }
                PositionFinanceData(revenue = income, taxes = taxes, price = price, commission = commission,
                    logistic = logistic, lastMile = lastMile, refund = refundCosts)
            }.filterValues { positionFinanceData -> positionFinanceData.revenue != 0.0
                    || positionFinanceData.refund != 0.0 }

            val tax = revenuesMap.values.sumOf { item -> if (item.revenue > 0) item.taxes else {0.0} }
            var totalRevenue = revenuesMap.values.sumOf { item -> item.revenue }
            val logisticCosts = revenuesMap.values.sumOf { item -> item.logistic }
            val lastMileCosts = revenuesMap.values.sumOf { item -> item.lastMile }
            val commissionCosts = revenuesMap.values.sumOf { item -> item.commission }
            val totalPrice = revenuesMap.values.sumOf { item -> item.price }
            val refundCosts = revenuesMap.values.sumOf { item -> item.refund }
            var positionCostPrice = 0.0

            var averageRevenue = if (deliveries != 0) { (totalRevenue + refundCosts) / deliveries } else { 0.0 }

            // пропускаем, если в периоде не было доставок (только отмены)
            if (totalRevenue > 0) {
                val costPrice = positionEntity.costPrice + positionEntity.additionalCost
                positionCostPrice = costPrice * deliveries
                totalRevenue = totalRevenue - costPrice * deliveries + refundCosts
                averageRevenue -= costPrice
                log.debug("Cost price for '${request.name}' '$costPrice'" )
            }

            log.info("Revenue for '${request.name}' '$averageRevenue'" )
            log.info("Total revenue for position '${request.name}' for period is '$averageRevenue'" )

            response.apply {
                ozonId = positionEntity.ozonId
                postingNumber = request.postingNumber
                refundCount = refunds
                taxes = BigDecimal(tax).setScale(2, RoundingMode.HALF_UP).toDouble()
                deliveryItemCount = deliveries
                soldItemsCount = sold
                response.averageRevenue = BigDecimal(averageRevenue).setScale(2, RoundingMode.HALF_UP).toDouble()
                response.totalRevenue = BigDecimal(totalRevenue).setScale(2, RoundingMode.HALF_UP).toDouble()
                logistic = BigDecimal(logisticCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                lastMile = BigDecimal(lastMileCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                saleCommission = BigDecimal(commissionCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                refund = BigDecimal(refundCosts).setScale(2, RoundingMode.HALF_UP).toDouble()
                price = BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP).toDouble()
                costPrice = BigDecimal(positionCostPrice).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
            return response
        } catch (e: WebClientException) {
            log.error("Error from Ozon for '${request.name}' from '${request.from}' to '${request.to}'", e)
            response.errorMessage = e.message
            return response
        }
    }
}