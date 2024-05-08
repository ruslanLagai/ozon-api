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
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.RevenueCalculationService

/**
 * @author rlagay
 */
@Service
@Slf4j
class PostRevenueCalculationServiceImpl(
    val ozonService: OzonService,
    val calculators: List<FinancialDataCalculator>,
    val positionRepository: PositionRepository
) : RevenueCalculationService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(PostRevenueCalculationServiceImpl::class.java)
    }

    override fun calculateRevenue(request: RevenueRequest): RevenueResponse? {
        if (StringUtils.isBlank(request.postingNumber)) {
            log.debug("Posting number is empty, skipping calculation by posting number")
            return null
        }
        if (request.from != null || request.to != null) {
            return null
        }

        val response = RevenueResponse(request.name, "", request.artikul)

        try {
            if (StringUtils.isBlank(request.name)) {
                log.warn("Name is empty")
                response.errorMessage = "Отсутствует название позиции для расчета"
                return response
            }

            val isDelivery = request.postingNumber!!.matches(Regex("\\d{1,}-\\d{1,}-\\d{1}"))
            val transactions = if (isDelivery) { getTransactionsByDelivery(request) } else { getTransactionsByAcquiring(request) }

            if (transactions.size <= 1) {
                log.warn("Недостаточно транзакций для '${request.postingNumber}', получено транзакций '${transactions.size}'")
                response.errorMessage = "Недостаточно транзакций для расчета, возможно, товар еще не доставлен"
                return response
            }

            var revenue = transactions.filter { transaction ->
                StringUtils.startsWith(transaction.posting.postingNumber, request.postingNumber!!.substringBeforeLast("-"))
            }.sumOf { transaction ->
                calculators.sumOf { calculator -> calculator.calculateRevenue(transaction) }
            }

            val positionEntity = request.name?.let { positionRepository.getPositionEntityByName(it) }
            if (positionEntity == null) {
                log.warn("Отсутствует данные по себестоимости товара '${request.name}'")
            }

            val noRefund = transactions.none { transaction ->
                transaction.operationType == OperationType.ClientReturnAgentOperation
                        || transaction.operationType == OperationType.OperationItemReturn
            }
            if (noRefund) {
                val costPrice = (positionEntity?.costPrice ?: 0.0) + (positionEntity?.additionalCost ?: 0.0)
                revenue -= costPrice

                log.debug("Себестоимость для '${request.postingNumber}' '$costPrice'" )
            }

            log.info("Чистая прибыль для '${request.postingNumber}' '$revenue'" )

            response.apply {
                ozonId = positionEntity?.ozonId
                postingNumber = request.postingNumber
                response.revenue = revenue
            }
            return response
        } catch (e: WebClientException) {
            log.error("Error from Ozon, posting number '${request.postingNumber}'", e)
            response.errorMessage = e.message
            return response
        }
    }

    private fun getTransactionsByDelivery(
        request: RevenueRequest
    ): List<Transaction> {
        val transactions = ArrayList<Transaction>()
        request.postingNumber?.let {
            //отправление
            val delivery = ozonService.getTransaction(it)
            transactions.addAll(delivery)

            //эквайринг
            val acquiring = ozonService.getTransaction(it.substringBeforeLast("-"))
            transactions.addAll(acquiring)

            //бонусы продавца
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
                .filter { item -> it.substringAfterLast("-") != item }
                .takeWhile { transactions.size < 6 }
                .map { item -> ozonService.getTransaction(it.substringBeforeLast("-") + item) }
                .forEach { list -> transactions.addAll(list) }
        }
        return transactions
    }

    private fun getTransactionsByAcquiring(
        request: RevenueRequest,
    ): List<Transaction> {
        val transactions = ArrayList<Transaction>()
        request.postingNumber?.let {
            //эквайринг
            val transaction = ozonService.getTransaction(it)
            transactions.addAll(transaction)

            //бонусы, отправление продавца
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
                .takeWhile { transactions.size < 6 }
                .map { item -> ozonService.getTransaction("$it-$item") }
                .forEach { list -> transactions.addAll(list) }
        }
        return transactions
    }
}