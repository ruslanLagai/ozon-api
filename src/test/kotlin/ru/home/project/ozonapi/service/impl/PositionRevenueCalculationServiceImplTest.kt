package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import ru.home.project.ozonapi.calculator.FinancialAmountCalculator
import ru.home.project.ozonapi.dto.finance.response.TransactionsResp
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.dto.response.RevenueResponse
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.util.readResource
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
class PositionRevenueCalculationServiceImplTest {

    private val ozonService = Mockito.mock(OzonService::class.java)
    private val repository = Mockito.mock(PositionRepository::class.java)
    private val financialAmountCalculator = FinancialAmountCalculator()

    private val positionRevenueService = PositionRevenueCalculationServiceImpl(ozonService, listOf(financialAmountCalculator), repository)

    @Test
    fun `calculate revenue - no position name`() {
        val request = RevenueRequest("", "art", "")
        val result = positionRevenueService.calculateRevenue(request)
        Assertions.assertNull(result)
    }

    @Test
    fun `calculate revenue - posting number is present`() {
        val request = RevenueRequest("sdf", "art", "dsf")
        val result = positionRevenueService.calculateRevenue(request)
        Assertions.assertNull(result)
    }

    @Test
    fun `calculate revenue - no period`() {
        val request = RevenueRequest("sdf", "art", "")
        val result = positionRevenueService.calculateRevenue(request)
        val expected = RevenueResponse(request.name, "", request.artikul)
        Assertions.assertEquals(expected, result)
        Assertions.assertEquals("Отсутствует период для расчета", result!!.errorMessage)
    }

    @Test
    fun `calculate revenue - no position in DB`() {
        val request = RevenueRequest("sdf", "art", "", to = OffsetDateTime.now(), from = OffsetDateTime.now().minusDays(3))

        val result = positionRevenueService.calculateRevenue(request)
        val expected = RevenueResponse(request.name, "", request.artikul)
        Assertions.assertEquals(expected, result)
        Assertions.assertEquals("Отсутствуют данные по товару в БД", result!!.errorMessage)
    }

    @Test
    fun `calculate revenue - no transactions for calculation`() {
        val request = RevenueRequest("sdf", "art", "", to = OffsetDateTime.now(), from = OffsetDateTime.now().minusDays(3))

        val emptyTransactions = readResource("transactions/position/empty-transactions.json", TransactionsResp::class.java)
            .result.operations
        val positionEntity = PositionEntity(1L, "Держатель для полотенец", 170.0, 10.0, "1", "")

        `when`(ozonService.getTransaction(any(), any(), any())).thenReturn(emptyTransactions)
        `when`(repository.getPositionEntityByName("Держатель для полотенец")).thenReturn(positionEntity)

        val result = positionRevenueService.calculateRevenue(request)
        val expected = RevenueResponse(request.name, "", request.artikul)
        Assertions.assertEquals(expected, result)
        Assertions.assertEquals("Отсутствуют данные по товару в БД", result!!.errorMessage)
    }

    @Test
    fun `calculate revenue - no acquiring in the period`() {
        val request = RevenueRequest("Швабра", "art", "", to = OffsetDateTime.now(), from = OffsetDateTime.now().minusDays(3))

        val transactions = readResource("transactions/position/transactions.json", TransactionsResp::class.java)
            .result.operations
        val acquiring = readResource("transactions/position/acquiring-transaction.json", TransactionsResp::class.java)
            .result.operations
        val positionEntity = PositionEntity(1L, "Швабра", 663.0, 10.0, "1142892245", "")

        `when`(ozonService.getTransaction(any(), any(), any())).thenReturn(transactions)
        `when`(ozonService.getTransaction(anyString())).thenReturn(acquiring)
        `when`(repository.getPositionEntityByName("Швабра")).thenReturn(positionEntity)

        val result = positionRevenueService.calculateRevenue(request)
        val expected = RevenueResponse(request.name, "1142892245", request.artikul)
        Assertions.assertEquals(expected, result)
        Assertions.assertEquals(551.87, result!!.averageRevenue)
        Assertions.assertEquals(1103.73, result.totalRevenue)
        Assertions.assertEquals(207.0, result.taxes)

    //        Assertions.assertEquals(-686.0, result.marketingCosts)
//        Assertions.assertEquals(-144.0, result.feedbackCosts)
//        Assertions.assertEquals(12, result.soldItemsCount)
    }

    @Test
    fun `calculate revenue - acquiring in the period`() {
        val request = RevenueRequest("Швабра", "art", "", to = OffsetDateTime.now(), from = OffsetDateTime.now().minusDays(3))

        val transactions = readResource("transactions/position/transactions-with-acquiring.json", TransactionsResp::class.java)
            .result.operations
        val positionEntity = PositionEntity(1L, "Швабра", 663.0, 10.0, "1142892245", "")

        `when`(ozonService.getTransaction(any(), any(), any())).thenReturn(transactions)
        `when`(repository.getPositionEntityByName("Швабра")).thenReturn(positionEntity)

        val result = positionRevenueService.calculateRevenue(request)
        val expected = RevenueResponse(request.name, "1142892245", request.artikul)
        Assertions.assertEquals(expected, result)
        Assertions.assertEquals(558.8, result!!.averageRevenue)
        Assertions.assertEquals(1117.59, result.totalRevenue)
        Assertions.assertEquals(207.0, result.taxes)

//        Assertions.assertEquals(-686.0, result.marketingCosts)
//        Assertions.assertEquals(-144.0, result.feedbackCosts)
//        Assertions.assertEquals(13, result.soldItemsCount)
    }

    @Test
    fun `calculate revenue with refund & feedback`() {
        val request = RevenueRequest("Зонт", "art", "", to = OffsetDateTime.now(), from = OffsetDateTime.now().minusDays(3))

        val transactions = readResource("transactions/position/transactions.json", TransactionsResp::class.java)
            .result.operations
        val acquiring = readResource("transactions/position/acquiring-transaction.json", TransactionsResp::class.java)
            .result.operations
        val refund = readResource("transactions/position/refund-transaction.json", TransactionsResp::class.java)
            .result.operations
        val positionEntity = PositionEntity(1L, "Зонт", 663.0, 10.0, "1134731178", "")

        `when`(ozonService.getTransaction(any(), any(), any())).thenReturn(transactions)
        `when`(ozonService.getTransaction("35220896-0207")).thenReturn(acquiring)
        `when`(ozonService.getTransaction("35220896-0207-1")).thenReturn(refund)
        `when`(repository.getPositionEntityByName("Зонт")).thenReturn(positionEntity)

        val result = positionRevenueService.calculateRevenue(request)
        val expected = RevenueResponse(request.name, "1134731178", request.artikul)
        Assertions.assertEquals(expected, result)
        Assertions.assertEquals(0.0, result!!.averageRevenue)
        Assertions.assertEquals(0.0, result.taxes)
//        Assertions.assertEquals(-686.0, result.marketingCosts)
//        Assertions.assertEquals(-144.0, result.feedbackCosts)
//        Assertions.assertEquals(12, result.soldItemsCount)
        Assertions.assertEquals(1, result.refundCount)
    }
}