package ru.home.project.ozonapi.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.entity.CostPriceEntity
import ru.home.project.ozonapi.entity.TransactionEntity
import ru.home.project.ozonapi.repository.CostPriceRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TransactionRepository
import ru.home.project.ozonapi.service.TransactionCostPriceService
import java.util.*

/**
 * Stores transaction-related cost price records.
 */
@Service
class TransactionCostPriceServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val positionRepository: PositionRepository,
    private val costPriceRepository: CostPriceRepository
) : TransactionCostPriceService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(TransactionCostPriceServiceImpl::class.java)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateCostPrice(deliveredOperaions: List<String>, returnedOperations: List<String>, sku: String) {
        val position = positionRepository.getPositionEntityByOzonId(sku)

        if (deliveredOperaions.isNotEmpty()) {
            position.costPriceEntity
                .filter { it.fulfilment != 0.0 }
                .filter { it.crossDoc != 0.0 }
                .sortedByDescending { it.supplyDate }
                .let {
                    val current = it.firstOrNull { costPriceEntity -> costPriceEntity.leftQuantity > 0 }
                    if (current == null) {
                        log.warn("No costPriceEntity with leftQuantity > 0 for ${position.ozonId}")
                        return@let
                    }

                    var diff = current.leftQuantity - deliveredOperaions.size
                    if (diff >= 0) {
                        val transactionEntities = deliveredOperaions.map { operationId ->
                            TransactionEntity(
                                operationId = operationId,
                                ozonId = position.ozonId,
                                fifoCostPrice = current,
                                isFailed = false
                            )
                        }
                        current.transactions.addAll(transactionEntities)
                        transactionRepository.saveAll(transactionEntities)
                        current.leftQuantity = diff
                    } else {
                        var delivered = deliveredOperaions.size
                        val transactionEntities = ArrayList<TransactionEntity>()
                        var fifoCostPrice = current
                        var initialIndex = 0

                        while (delivered > 0) {
                            diff = delivered - fifoCostPrice!!.leftQuantity
                            val toIndex =  if (diff > 0) fifoCostPrice.leftQuantity else delivered
                            deliveredOperaions.subList(initialIndex, toIndex)
                                .map { operationId ->
                                    TransactionEntity(
                                        operationId = operationId,
                                        ozonId = position.ozonId,
                                        fifoCostPrice = fifoCostPrice,
                                        isFailed = false
                                    )
                                }
                                .plusElement(transactionEntities)
                            fifoCostPrice.leftQuantity = 0
                            fifoCostPrice = it.firstOrNull { costPriceEntity -> costPriceEntity.leftQuantity > 0 }

                            // failed transactions with prev cost price
                            if (fifoCostPrice == null) {
                                log.warn("Failed to find next costPriceEntity with leftQuantity > 0 for ${position.artikul}")

                                deliveredOperaions.subList(initialIndex, toIndex)
                                    .map { operationId ->
                                        TransactionEntity(
                                            operationId = operationId,
                                            ozonId = position.ozonId,
                                            fifoCostPrice = current,
                                            isFailed = true
                                        )
                                    }
                                    .onEach { item -> transactionEntities.add(item) }
                                log.warn("Marked  ${transactionEntities.size} transactions for ${position.artikul}")
                                current.transactions.addAll(transactionEntities)
                                break
                            } else {
                                fifoCostPrice.transactions.addAll(transactionEntities)
                            }
                            delivered -= diff
                            initialIndex += diff
                        }
                        transactionRepository.saveAll(transactionEntities)
                    }
            }
        }

        if (returnedOperations.isNotEmpty()) {
            position.costPriceEntity.let {

                val fifoCostPriceMap = HashMap<UUID, CostPriceEntity>()
                var hasNext = true
                var pageRequest = PageRequest.of(0, 50, Sort.by("id"))
                while (hasNext) {
                    val page = transactionRepository.getAllByOperationIdIn(returnedOperations, pageRequest)
                    hasNext = page.hasNext()
                    page.forEach { transactionEntity ->
                        val fifoCostPriceId = transactionEntity.fifoCostPrice.id
                        val fifoCostPrice = fifoCostPriceMap.getOrPut(
                            fifoCostPriceId!!, { costPriceRepository.getReferenceById(fifoCostPriceId) }
                        )
                        fifoCostPrice.leftQuantity += 1
                    }
                    pageRequest = pageRequest.next()
                }
                transactionRepository.deleteAllByOperationIdIn(returnedOperations)
                costPriceRepository.saveAll(fifoCostPriceMap.values)
            }
            positionRepository.save(position)
        }
    }
}