package ru.home.project.ozonapi.scheduled

import org.hibernate.StaleObjectStateException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Example
import org.springframework.data.domain.Pageable
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.dto.finance.response.OperationType
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.entity.FailedCostPriceTransactionEntity
import ru.home.project.ozonapi.repository.CrossDocTransactionEntityRepository
import ru.home.project.ozonapi.repository.FailedCostPriceTransactionRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TransactionRepository
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
    val crossDocAdditionalService: AdditionalServicesForCostPriceService,
    val transactionRepository: TransactionRepository,
    val crossDocTransactionEntityRepository: CrossDocTransactionEntityRepository
) {

    private val deliveryOrRefundPredicate = Predicate<Transaction> {
        it.operationType == OperationType.OperationAgentDeliveredToCustomer || it.operationType == OperationType.ClientReturnAgentOperation
    }

    private val log: Logger = LoggerFactory.getLogger(CostPriceScheduledService::class.java)

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

    /**
     * Retries [block] when it fails with [ObjectOptimisticLockingFailureException], since
     * CostPriceEntity rows may be concurrently updated (e.g. delivery and return processing
     * for the same sku), causing @Version conflicts on commit of the REQUIRES_NEW transactions.
     */
    private fun <T> retryOnOptimisticLock(operationName: String, maxAttempts: Int = 3, block: () -> T): T {
        var attempt = 1
        while (true) {
            try {
                return block()
            } catch (ex: ObjectOptimisticLockingFailureException) {
                if (attempt >= maxAttempts) {
                    log.error("$operationName failed after $attempt attempts due to optimistic locking conflict", ex)
                    throw ex
                }
                log.warn("$operationName hit optimistic locking conflict on attempt $attempt, retrying", ex)
                attempt++
            } catch (ex: StaleObjectStateException) {
                log.warn("$operationName failed due to stale object state exception, this may indicate a serious issue with concurrent updates", ex)
                if (attempt >= maxAttempts) {
                    log.error("$operationName failed after $attempt attempts due to optimistic locking conflict", ex)
                    throw ex
                }
                log.warn("$operationName hit optimistic locking conflict on attempt $attempt, retrying", ex)
                attempt++
            }
        }
    }

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
                    log.warn("Transaction with more than 1 item: {}, operationId ${it.operationId}, posting number ${it.posting.postingNumber}", it)
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

                var existed = transactionRepository.getAllByOperationIdIn(delivered, Pageable.ofSize(50).withPage(0))
                var pageNum = 1
                val toRemove = mutableSetOf<String>()
                toRemove.addAll(existed.content.map { it.operationId })
                while (existed.hasNext()) {
                    existed = transactionRepository.getAllByOperationIdIn(delivered, Pageable.ofSize(50).withPage(pageNum))
                    pageNum++
                    toRemove.addAll(existed.content.map { it.operationId })
                }
                log.info("To be removed: ${toRemove.joinToString()}")
                delivered.removeAll(toRemove)

                log.info("Delivered: ${delivered.joinToString()}")

                retryOnOptimisticLock("updateCostPrice for sku=$sku") {
                    transactionCostPriceService.updateCostPrice(deliveredOperaions = delivered, sku = sku)
                }
                retryOnOptimisticLock("updateReturnedOperationCostPrice for sku=$sku") {
                    transactionCostPriceService.updateReturnedOperationCostPrice(returnedOperations = cancelled, sku = sku)
                }
            }

        ozonService.getTransaction(from, to, cacheKey)
            .filter { it.operationType == OperationType.OperationMarketplaceCrossDockServiceWriteOff
                    || it.operationType == OperationType.MarketplaceServiceItemCrossdocking
                    || it.operationType == OperationType.OperationMarketplaceSupplyAdditional
                    || it.operationType == OperationType.OperationMarketplaceServiceProcessingNotIdentifiedSurplus
            }
            .filter { crossDocTransactionEntityRepository.findByOrderId(it.posting.postingNumber).isEmpty }
            .forEach { transaction ->
                retryOnOptimisticLock("update cross doc for ${transaction.posting.postingNumber}") {
                    crossDocAdditionalService.updateCostPrice(transaction.posting.postingNumber.toLong(), transaction.income)
                }
            }

        retryOnOptimisticLock("updateCostPrice for failed transactions") {
            transactionsService.runInTransaction {
                incorrectItems.map {
                    it.items.map { item ->
                        FailedCostPriceTransactionEntity(ozonId = item.sku, operationId = it.posting.postingNumber, quantity = 1, operationDate = it.operationDate)
                    }.toList()
                }.forEach { failedCostPriceTransactionRepository.saveAll(it) }
            }
        }

    }
}