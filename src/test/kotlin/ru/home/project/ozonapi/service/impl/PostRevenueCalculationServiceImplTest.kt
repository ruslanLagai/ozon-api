package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.web.reactive.function.client.WebClientResponseException
import ru.home.project.ozonapi.calculator.FinancialAmountCalculator
import ru.home.project.ozonapi.dto.finance.response.TransactionsResp
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.dto.response.RevenueResponse
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.util.readResource

/**
 * @author rlagay
 */
class PostRevenueCalculationServiceImplTest {

    private val ozonService = mock(OzonService::class.java)
    private val repository = mock(PositionRepository::class.java)
    private val financialAmountCalculator = FinancialAmountCalculator()

    private val postRevenueService = PostRevenueCalculationServiceImpl(ozonService, listOf(financialAmountCalculator), repository)

    @Test
    fun `calculate revenue no name`() {
        val request = RevenueRequest("", "art", "number")
        val expected = RevenueResponse(request.name, "", request.artikul)
        val result = postRevenueService.calculateRevenue(request)
        assertEquals(expected, result)
        assertEquals("Отсутствует название позиции для расчета", result?.errorMessage ?: "")
    }

    @Test
    fun `calculate revenue delivery & bonuses has same number`() {
        val deliveryAndBonuses = readResource("transactions/transactions-by-posting-number-delivery-with-bonuses.json", TransactionsResp::class.java)
            .result.operations
        val acquiringTransactions = readResource("transactions/transactions-by-posting-number-acquiring.json", TransactionsResp::class.java)
            .result.operations
        val emptyTransactions = readResource("transactions/empty-transactions-response.json", TransactionsResp::class.java)
            .result.operations
        val positionEntity = PositionEntity(1L, "Держатель для полотенец", 170.0, 10.0, "1", "")

        `when`(ozonService.getTransaction(anyString())).thenReturn(emptyTransactions)
        `when`(ozonService.getTransaction("66995153-0218-1")).thenReturn(deliveryAndBonuses)
        `when`(ozonService.getTransaction("66995153-0218")).thenReturn(acquiringTransactions)
        `when`(repository.getPositionEntityByName("Держатель для полотенец")).thenReturn(positionEntity)

        val request = RevenueRequest("Держатель для полотенец", "", "66995153-0218-1")
        val result = postRevenueService.calculateRevenue(request)

        val expected = RevenueResponse(request.name, "1", request.artikul)

        assertEquals(expected, result)
        assertEquals(223.95, result?.revenue ?: 0)
        assertEquals("66995153-0218-1", result?.postingNumber ?: "")
    }

    @Test
    fun `calculate revenue no bonuses`() {
        val delivery = readResource("transactions/transactions-by-posting-number-delivery.json", TransactionsResp::class.java)
            .result.operations
        val acquiringTransactions = readResource("transactions/transactions-by-posting-number-acquiring.json", TransactionsResp::class.java)
            .result.operations
        val emptyTransactions = readResource("transactions/empty-transactions-response.json", TransactionsResp::class.java)
            .result.operations
        val positionEntity = PositionEntity(1L, "Держатель для полотенец", 170.0, 10.0, "1", "")

        `when`(ozonService.getTransaction(anyString())).thenReturn(emptyTransactions)
        `when`(ozonService.getTransaction("66995153-0218-1")).thenReturn(delivery)
        `when`(ozonService.getTransaction("66995153-0218")).thenReturn(acquiringTransactions)
        `when`(repository.getPositionEntityByName("Держатель для полотенец")).thenReturn(positionEntity)

        val request = RevenueRequest("Держатель для полотенец", "", "66995153-0218-1")
        val result = postRevenueService.calculateRevenue(request)

        val expected = RevenueResponse(request.name, "1", request.artikul)

        assertEquals(expected, result)
        assertEquals(225.25, result?.revenue ?: 0.0)
        assertEquals("66995153-0218-1", result?.postingNumber ?: "")
    }

    @Test
    fun `calculate revenue no position in repository`() {
        val delivery = readResource("transactions/transactions-by-posting-number-delivery.json", TransactionsResp::class.java)
            .result.operations
        val acquiringTransactions = readResource("transactions/transactions-by-posting-number-acquiring.json", TransactionsResp::class.java)
            .result.operations
        val emptyTransactions = readResource("transactions/empty-transactions-response.json", TransactionsResp::class.java)
            .result.operations

        `when`(ozonService.getTransaction(anyString())).thenReturn(emptyTransactions)
        `when`(ozonService.getTransaction("66995153-0218-1")).thenReturn(delivery)
        `when`(ozonService.getTransaction("66995153-0218")).thenReturn(acquiringTransactions)
        `when`(repository.getPositionEntityByName("Держатель для полотенец")).thenReturn(null)

        val request = RevenueRequest("Держатель для полотенец", "", "66995153-0218-1")
        val result = postRevenueService.calculateRevenue(request)

        val expected = RevenueResponse(request.name, null, request.artikul)
        expected.postingNumber = "66995153-0218-1"
        expected.revenue = 405.25

        assertEquals(expected, result)
        assertEquals(405.25, result?.revenue ?: 0)
        assertEquals("66995153-0218-1", result?.postingNumber ?: "")
    }

    @Test
    fun `calculate revenue not enough transactions`() {
        val acquiringTransactions = readResource("transactions/transactions-by-posting-number-acquiring.json", TransactionsResp::class.java)
            .result.operations
        val emptyTransactions = readResource("transactions/empty-transactions-response.json", TransactionsResp::class.java)
            .result.operations

        `when`(ozonService.getTransaction(anyString())).thenReturn(emptyTransactions)
        `when`(ozonService.getTransaction("66995153-0218")).thenReturn(acquiringTransactions)
        `when`(repository.getPositionEntityByName("Держатель для полотенец")).thenReturn(null)

        val request = RevenueRequest("Держатель для полотенец", "", "66995153-0218-1")
        val result = postRevenueService.calculateRevenue(request)

        val expected = RevenueResponse(request.name, "", request.artikul)
        expected.errorMessage = "Недостаточно транзакций для расчета, возможно, товар еще не доставлен"

        assertEquals(expected, result)
        verify(ozonService, times(10)).getTransaction(anyString())
    }

    @Test
    fun `calculate revenue ozon exception`() {

        `when`(ozonService.getTransaction(anyString())).thenThrow(WebClientResponseException.create(500, "Internal Server Error", null, null, null))

        val request = RevenueRequest("Держатель для полотенец", "", "66995153-0218-1")
        val result = postRevenueService.calculateRevenue(request)

        val expected = RevenueResponse(request.name, "", request.artikul)
        expected.errorMessage = "Недостаточно транзакций для расчета, возможно, товар еще не доставлен"

        assertEquals(expected, result)
        verify(ozonService, times(1)).getTransaction(anyString())
    }

    @Test
    fun `calculate revenue with refund`() {
        val deliveryAndRefund = readResource("transactions/with-refund/transactions-by-posting-number-delivery.json", TransactionsResp::class.java)
            .result.operations
        val acquiringTransactions = readResource("transactions/with-refund/transactions-by-posting-number-acquiring.json", TransactionsResp::class.java)
            .result.operations
        val emptyTransactions = readResource("transactions/empty-transactions-response.json", TransactionsResp::class.java)
            .result.operations
        val positionEntity = PositionEntity(1L, "Держатель для полотенец", 170.0, 10.0, "1", "")

        `when`(ozonService.getTransaction(anyString())).thenReturn(emptyTransactions)
        `when`(ozonService.getTransaction("31311678-0082-1")).thenReturn(deliveryAndRefund)
        `when`(ozonService.getTransaction("31311678-0082")).thenReturn(acquiringTransactions)
        `when`(repository.getPositionEntityByName("Держатель для полотенец")).thenReturn(positionEntity)

        val request = RevenueRequest("Держатель для полотенец", "", "31311678-0082")
        val result = postRevenueService.calculateRevenue(request)

        val expected = RevenueResponse(request.name, "1", request.artikul)

        assertEquals(expected, result)
        assertEquals(-179.56, result?.revenue ?: 0)
        assertEquals("31311678-0082", result?.postingNumber ?: "")
    }
}