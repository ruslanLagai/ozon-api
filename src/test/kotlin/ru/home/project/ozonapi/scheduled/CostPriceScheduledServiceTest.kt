package ru.home.project.ozonapi.scheduled

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.SliceImpl
import ru.home.project.ozonapi.dto.finance.response.Item
import ru.home.project.ozonapi.dto.finance.response.OperationType
import ru.home.project.ozonapi.dto.finance.response.Posting
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.dto.finance.response.TransactionType
import ru.home.project.ozonapi.entity.CostPriceEntity
import ru.home.project.ozonapi.entity.FailedCostPriceTransactionEntity
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.entity.TransactionEntity
import ru.home.project.ozonapi.repository.FailedCostPriceTransactionRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TransactionRepository
import ru.home.project.ozonapi.service.AdditionalServicesForCostPriceService
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.TransactionCostPriceService
import ru.home.project.ozonapi.service.TransactionsService
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.function.Supplier

class CostPriceScheduledServiceTest {

    private val ozonService = mock<OzonService>()
    private val positionRepository = mock<PositionRepository>()
    private val transactionCostPriceService = mock<TransactionCostPriceService>()
    private val transactionsService = mock<TransactionsService>()
    private val failedCostPriceTransactionRepository = mock<FailedCostPriceTransactionRepository>()
    private val crossDocAdditionalService = mock<AdditionalServicesForCostPriceService>()
    private val transactionRepository = mock<TransactionRepository>()

    private val service = CostPriceScheduledService(
        ozonService = ozonService,
        positionRepository = positionRepository,
        transactionCostPriceService = transactionCostPriceService,
        transactionsService = transactionsService,
        failedCostPriceTransactionRepository = failedCostPriceTransactionRepository,
        crossDocAdditionalService = crossDocAdditionalService,
        transactionRepository = transactionRepository
    )

    init {
        whenever(transactionsService.runInTransaction<Unit>(any())).thenAnswer { invocation ->
            invocation.getArgument<Supplier<Unit>>(0).get()
        }
    }

    @Test
    fun `should group delivered and returned operations by sku`() {
        val positionOne = positionEntity(id = 1L, name = "position-1", ozonId = "sku-1", artikul = "art-1")
        val positionTwo = positionEntity(id = 2L, name = "position-2", ozonId = "sku-2", artikul = "art-2")
        val transactions = listOf(
            transaction(operationId = "delivered-1", operationType = OperationType.OperationAgentDeliveredToCustomer, items = listOf(item("sku-1"))),
            transaction(operationId = "returned-1", operationType = OperationType.ClientReturnAgentOperation, items = listOf(item("sku-1"))),
            transaction(operationId = "ignored-1", operationType = OperationType.OperationMarketplaceServiceStorage, items = listOf(item("sku-1"))),
            transaction(operationId = "delivered-2", operationType = OperationType.OperationAgentDeliveredToCustomer, items = listOf(item("sku-2")))
        )
        val existed = listOf(
            TransactionEntity(operationId = "posting-existed-1", ozonId = "sku-1", fifoCostPrice = mock<CostPriceEntity>(), isFailed = false),
            TransactionEntity(operationId = "posting-delivered-2", ozonId = "sku-2", fifoCostPrice = mock<CostPriceEntity>(), isFailed = false),
        )

        whenever(transactionRepository.getAllByOperationIdIn(any(), any()))
            .thenReturn(SliceImpl(existed))
        whenever(ozonService.getTransaction(any(), any(), any())).thenReturn(transactions)
        service.updateTransaction()

        verify(transactionCostPriceService).updateCostPrice(listOf("posting-delivered-1"), listOf("posting-returned-1"), "sku-1")
        verify(transactionCostPriceService).updateCostPrice(emptyList(), emptyList(), "sku-2")
        verify(transactionsService).runInTransaction<Unit>(any())
        verifyNoInteractions(crossDocAdditionalService)
        verifyNoInteractions(failedCostPriceTransactionRepository)
    }

    @Test
    fun `should update cross doc additional service for cross docking operations`() {
        val transactions = listOf(
            transaction(
                operationId = "cross-doc-1",
                operationType = OperationType.OperationMarketplaceCrossDockServiceWriteOff,
                items = listOf(item("sku-1")),
                postingNumber = "123456789",
                income = 321.45
            ),
            transaction(
                operationId = "cross-doc-2",
                operationType = OperationType.OperationMarketplaceSupplyAdditional,
                items = listOf(item("sku-2")),
                postingNumber = "987654321",
                income = 50.0
            )
        )

        whenever(ozonService.getTransaction(any(), any(), any())).thenReturn(transactions)

        service.updateTransaction()

        verify(crossDocAdditionalService).updateCostPrice(123456789L, 321.45)
        verify(crossDocAdditionalService).updateCostPrice(987654321L, 50.0)
        verify(transactionsService).runInTransaction<Unit>(any())
        verifyNoInteractions(positionRepository, transactionCostPriceService, failedCostPriceTransactionRepository)
    }

    @Test
    fun `should save failed cost price transactions for incorrect items`() {
        val transactions = listOf(
            transaction(
                operationId = "invalid-1",
                operationType = OperationType.OperationAgentDeliveredToCustomer,
                items = listOf(item("sku-1", "item-1"), item("sku-2", "item-2"))
            )
        )

        whenever(ozonService.getTransaction(any(), any(), any())).thenReturn(transactions)

        service.updateTransaction()

        val failedCaptor = argumentCaptor<Iterable<FailedCostPriceTransactionEntity>>()
        verify(failedCostPriceTransactionRepository).saveAll(failedCaptor.capture())
        verify(transactionsService).runInTransaction<Unit>(any())
        verifyNoInteractions(positionRepository, transactionCostPriceService)

        val failedEntities = failedCaptor.firstValue.toList()
        assertEquals(2, failedEntities.size)
        assertEquals(listOf("sku-1", "sku-2"), failedEntities.map { it.ozonId })
        assertEquals(listOf("posting-invalid-1", "posting-invalid-1"), failedEntities.map { it.operationId })
        assertEquals(listOf(1, 1), failedEntities.map { it.quantity })
        assertEquals(transactions.first().operationDate, failedEntities.first().operationDate)
    }

    private fun positionEntity(id: Long, name: String, ozonId: String, artikul: String): PositionEntity =
        PositionEntity(
            id = id,
            name = name,
            costPrice = 100.0,
            additionalCost = 10.0,
            ozonId = ozonId,
            artikul = artikul
        )

    private fun item(sku: String, name: String = "item-$sku"): Item =
        Item(name = name, sku = sku)

    private fun transaction(
        operationId: String,
        operationType: OperationType,
        items: List<Item>,
        postingNumber: String = "posting-$operationId",
        income: Double = 0.0
    ): Transaction = Transaction(
        operationId = operationId,
        operationType = operationType,
        operationDate = LocalDateTime.of(2024, 4, 1, 12, 0),
        saleCommission = 0.0,
        income = income,
        price = 0.0,
        type = TransactionType.orders,
        services = emptyList(),
        posting = Posting(date = OffsetDateTime.now().toLocalDateTime(), postingNumber = postingNumber),
        items = items
    )
}

