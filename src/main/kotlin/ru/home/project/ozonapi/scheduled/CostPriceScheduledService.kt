package ru.home.project.ozonapi.scheduled

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.dto.finance.response.OperationType
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.entity.FailedCostPriceTransactionEntity
import ru.home.project.ozonapi.repository.FailedCostPriceTransactionRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.AdditionalServicesForCostPriceService
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.TransactionCostPriceService
import ru.home.project.ozonapi.service.TransactionsService
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Predicate

/**
 *
 * По расписанию запускается, запрашивает поставки из озона, если поставка новая вычитает товары из остатков
 *
 * @author rlagay
 */
@Service
class CostPriceScheduledService(
    val ozonService: OzonService,
    val positionRepository: PositionRepository,
    val transactionCostPriceService: TransactionCostPriceService,
    val transactionsService: TransactionsService,
    val failedCostPriceTransactionRepository: FailedCostPriceTransactionRepository,
    val crossDocAdditionalService: AdditionalServicesForCostPriceService
) {

    private val deliveryOrRefundPredicate = Predicate<Transaction> {
        it.operationType == OperationType.OperationAgentDeliveredToCustomer || it.operationType == OperationType.ClientReturnAgentOperation
    }

    private val log: Logger = LoggerFactory.getLogger(CostPriceScheduledService::class.java)

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

    @Scheduled(cron = "\${service.ozon.transactions.cron}")
    @Transactional
    fun updateTransaction() {
        val from = OffsetDateTime.now().minusDays(14)
        val to = OffsetDateTime.now()

        // if transaction is incorrect, save to failed table
        val incorrectItems = ArrayList<Transaction>()
        val cacheKey = "from_" + from.minusDays(14).format(formatter) + "_to_" + to.format(formatter)
        ozonService.getTransaction(from, to, cacheKey)
            .filter { deliveryOrRefundPredicate.test(it) }
            .filter {
                if (it.items.size != 1) {
                    log.warn("Transaction with more than 1 item: {}, operationId ${it.operationId}", it)
                    incorrectItems.add(it)
                }
                it.items.size == 1
            }
            .groupBy {
                it.items.first().sku
            }
            .forEach { (sku, transactions) ->
                val delivered = ArrayList<String>()
                val cancelled = ArrayList<String>()
                for (transaction in transactions) {
                    val operationType = transaction.operationType
                    when (operationType) {
                        OperationType.OperationAgentDeliveredToCustomer -> delivered.add(transaction.posting.postingNumber)
                        OperationType.ClientReturnAgentOperation -> cancelled.add(transaction.posting.postingNumber)
                        else -> {}
                    }
                }

                transactionCostPriceService.updateCostPrice(deliveredOperaions = delivered, returnedOperations = cancelled, sku = sku)
            }

        ozonService.getTransaction(from, to, cacheKey)
            .filter { it.operationType == OperationType.OperationMarketplaceCrossDockServiceWriteOff
                    || it.operationType == OperationType.MarketplaceServiceItemCrossdocking
                    || it.operationType == OperationType.OperationMarketplaceSupplyAdditional
                    || it.operationType == OperationType.OperationMarketplaceServiceProcessingNotIdentifiedSurplus }
            .forEach { transaction ->
                crossDocAdditionalService.updateCostPrice(transaction.posting.postingNumber.toLong(), transaction.income)
            }

        transactionsService.runInTransaction {
            incorrectItems.map {
                it.items.map { item ->
                    FailedCostPriceTransactionEntity(ozonId = item.sku, operationId = it.operationId, quantity = 1, operationDate = it.operationDate)
                }.toList()
            }
            .forEach { failedCostPriceTransactionRepository.saveAll(it) }
        }
    }
}