package ru.home.project.ozonapi.service.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction
import jakarta.persistence.LockModeType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.data.domain.Sort
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.CostPriceEntity
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.entity.TransactionEntity
import ru.home.project.ozonapi.repository.CostPriceRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TransactionRepository
import ru.home.project.ozonapi.service.OzonService
import java.time.LocalDate
import java.util.UUID

class TransactionCostPriceServiceImplTest {

    private val transactionRepository = mock<TransactionRepository>()
    private val entityManager = mock<EntityManager>()
    private val positionRepository = mock<PositionRepository>()
    private val costPriceRepository = mock<CostPriceRepository>()

    private val service = TransactionCostPriceServiceImpl(
        transactionRepository = transactionRepository,
        positionRepository = positionRepository,
        costPriceRepository = costPriceRepository
    )

    @Test
    fun `should save delivered operations and decrease fifo left quantity`() {
        val costPrice = costPriceEntity(leftQuantity = 3, initialQuantity = 3)
        val position = positionEntity(fifoCostPrice = listOf(costPrice))

        whenever(positionRepository.getPositionEntityByOzonId("ozon-1")).thenReturn(position)

        service.updateCostPrice(
            deliveredOperaions = listOf("operation-1", "operation-2"),
            sku = "ozon-1"
        )
        service.updateReturnedOperationCostPrice(
            returnedOperations = emptyList(),
            sku = "ozon-1"
        )

        val savedTransactionsCaptor = argumentCaptor<Iterable<TransactionEntity>>()
        verify(transactionRepository).saveAll(savedTransactionsCaptor.capture())

        val savedTransactions = savedTransactionsCaptor.firstValue.toList()
        assertEquals(2, savedTransactions.size)
        assertEquals(listOf("operation-1", "operation-2"), savedTransactions.map { it.operationId })
        assertTrue(savedTransactions.all { !it.isFailed })
        assertTrue(savedTransactions.all { it.fifoCostPrice == costPrice })
        assertEquals(1, costPrice.leftQuantity)
        assertEquals(2, costPrice.transactions.size)
    }

    @Test
    fun `should process returned operations across multiple slices and restore fifo quantities`() {
        val firstCostPrice = costPriceEntity(id = UUID.randomUUID(), leftQuantity = 0, initialQuantity = 2)
        val secondCostPrice = costPriceEntity(id = UUID.randomUUID(), leftQuantity = 5, initialQuantity = 6)
        val position = positionEntity(fifoCostPrice = listOf(firstCostPrice, secondCostPrice))
        val returnedOperations = listOf("operation-1", "operation-2", "operation-3")
        whenever(positionRepository.getPositionEntityByOzonId("ozon-1")).thenReturn(position)

        val firstPageRequest = PageRequest.of(0, 50, Sort.by("id"))
        val secondPageRequest = firstPageRequest.next()

        val firstSlice = SliceImpl(
            listOf(
                transactionEntity("operation-1", firstCostPrice),
                transactionEntity("operation-2", firstCostPrice)
            ),
            firstPageRequest,
            true
        )
        val secondSlice = SliceImpl(
            listOf(transactionEntity("operation-3", secondCostPrice)),
            secondPageRequest,
            false
        )

        whenever(transactionRepository.getAllByOperationIdIn(returnedOperations, firstPageRequest)).thenReturn(firstSlice)
        whenever(transactionRepository.getAllByOperationIdIn(returnedOperations, secondPageRequest)).thenReturn(secondSlice)
        whenever(costPriceRepository.getReferenceById(firstCostPrice.id!!)).thenReturn(firstCostPrice)
        whenever(costPriceRepository.getReferenceById(secondCostPrice.id!!)).thenReturn(secondCostPrice)

        service.updateCostPrice(
            deliveredOperaions = emptyList(),
            sku = "ozon-1"
        )
        service.updateReturnedOperationCostPrice(
            returnedOperations = returnedOperations,
            sku = "ozon-1"
        )

        assertEquals(2, firstCostPrice.leftQuantity)
        assertEquals(6, secondCostPrice.leftQuantity)

        verify(transactionRepository).getAllByOperationIdIn(returnedOperations, firstPageRequest)
        verify(transactionRepository).getAllByOperationIdIn(returnedOperations, secondPageRequest)
        verify(transactionRepository).deleteAllByOperationIdIn(returnedOperations)
        val savedCostPricesCaptor = argumentCaptor<Iterable<CostPriceEntity>>()
        verify(costPriceRepository).saveAll(savedCostPricesCaptor.capture())
        assertEquals(
            setOf(firstCostPrice.id, secondCostPrice.id),
            savedCostPricesCaptor.firstValue.map { entity -> entity.id }.toSet()
        )
        assertEquals(
            setOf(6, 2),
            savedCostPricesCaptor.firstValue.map { entity -> entity.leftQuantity }.toSet()
        )
    }

    @Test
    fun `should do nothing when no delivered and returned operations provided`() {
        val position = positionEntity(fifoCostPrice = emptyList())
        whenever(positionRepository.getPositionEntityByOzonId("ozon-1")).thenReturn(position)

        service.updateCostPrice(
            deliveredOperaions = emptyList(),
            sku = "ozon-1"
        )
        service.updateReturnedOperationCostPrice(
            returnedOperations = emptyList(),
            sku = "ozon-1"
        )

        verifyNoInteractions(transactionRepository, costPriceRepository, entityManager)
    }

    private fun positionEntity(fifoCostPrice: List<CostPriceEntity>): PositionEntity =
        PositionEntity(
            id = 1L,
            name = "position-1",
            costPrice = 100.0,
            additionalCost = 10.0,
            ozonId = "ozon-1",
            artikul = "art-1",
            costPriceEntity = fifoCostPrice.toMutableList()
        )

    private fun costPriceEntity(
        id: UUID = UUID.randomUUID(),
        leftQuantity: Int,
        initialQuantity: Int
    ): CostPriceEntity = CostPriceEntity(
        id = id,
        leftQuantity = leftQuantity,
        initialQuantity = initialQuantity,
        supplyDate = LocalDate.now(),
        costPrice = 150.0,
        crossDoc = 5.0,
        fulfilment = 1.0,
        ozonId = "cost-price-$id",
        position = PositionEntity(
            id = 99L,
            name = "linked-position-$id",
            costPrice = 150.0,
            additionalCost = 5.0,
            ozonId = "linked-ozon-$id",
            artikul = "linked-art-$id",
            costPriceEntity = mutableListOf()
        ),
        chinaOrder = ChinaOrderEntity(
            id = 77L,
            supplier = "supplier",
            orderDate = LocalDate.now(),
            stockCost = 1000.0,
            products = arrayListOf()
        ),
        transactions = mutableListOf()
    )

    private fun transactionEntity(operationId: String, costPrice: CostPriceEntity): TransactionEntity =
        TransactionEntity(
            operationId = operationId,
            ozonId = "ozon-$operationId",
            isFailed = false,
            fifoCostPrice = costPrice
        )
}