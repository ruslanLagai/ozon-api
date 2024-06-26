package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import ru.home.project.ozonapi.client.OzonApiClient
import ru.home.project.ozonapi.dto.finance.response.TransactionsResp
import ru.home.project.ozonapi.dto.stocks.response.GetStocksResponse
import ru.home.project.ozonapi.util.readResource
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * @author rlagay
 */
class OzonServiceImplTest {

    private val client = mock(OzonApiClient::class.java)
    private val service = OzonServiceImpl(client)

    @Test
    fun `get transactions response`() {
        val date = OffsetDateTime.now()
        val response = readResource("transactions/transactions-response.json", TransactionsResp::class.java)
        `when`(client.getTransactions(date, date, 1)).thenReturn(response.result.operations)

        val result = service.getTransaction(date, date, "")

        assertEquals(735, result.size)
    }

    @Test
    fun `get empty transactions`() {
        val response = readResource("transactions/empty-transactions-response.json", TransactionsResp::class.java)
        `when`(client.getTransactions(OffsetDateTime.now(), OffsetDateTime.now(), 1))
            .thenReturn(response.result.operations)

        val result = service.getTransaction(OffsetDateTime.now(), OffsetDateTime.now(), "")

        assertEquals(0, result.size)
    }

    @Test
    fun `get transactions by posting number`() {
        val response = readResource("transactions/transactions-by-posting-number.json", TransactionsResp::class.java)
        `when`(client.getTransactions("123")).thenReturn(response.result.operations)

        val result = service.getTransaction("123")

        assertEquals(2, result.size)
    }

    @Test
    fun `get transactions more then month response`() {

        val to = OffsetDateTime.of(2023, 11, 27, 1, 0, 0, 0,  ZoneOffset.UTC)
        val from = to.minusMonths(2).minusDays(2)

        val firstFrom = OffsetDateTime.of(LocalDate.of(2023, 9, 25).atTime(LocalTime.MIN), ZoneOffset.UTC)
        val firstTo = OffsetDateTime.of(LocalDate.of(2023, 10, 24).atTime(LocalTime.MAX), ZoneOffset.UTC)
        val secondFrom = OffsetDateTime.of(LocalDate.of(2023, 10, 25).atTime(LocalTime.MIN),  ZoneOffset.UTC)
        val secondTo = OffsetDateTime.of(LocalDate.of(2023, 11, 23).atTime(LocalTime.MAX), ZoneOffset.UTC)
        val thirdFrom = OffsetDateTime.of(LocalDate.of(2023, 11, 24).atTime(LocalTime.MIN),  ZoneOffset.UTC)
        val thirdTo = OffsetDateTime.of(LocalDate.of(2023, 11, 27).atTime(LocalTime.MAX), ZoneOffset.UTC)

        val response = readResource("transactions/transactions-response.json", TransactionsResp::class.java)
        val empty = readResource("transactions/empty-transactions-response.json", TransactionsResp::class.java)
        `when`(client.getTransactions(firstFrom, firstTo, 1)).thenReturn(response.result.operations)
        `when`(client.getTransactions(secondFrom, secondTo, 1)).thenReturn(empty.result.operations)
        `when`(client.getTransactions(thirdFrom, thirdTo, 1)).thenReturn(empty.result.operations)

        val result = service.getTransaction(from, to, "")

        assertEquals(735, result.size)
        verify(client, times(4)).getTransactions(any(), any(), anyInt())
    }

    @Test
    fun `get stocks`() {
        val response = readResource("stocks/stocks-response.json", GetStocksResponse::class.java)
        `when`(client.getStocks()).thenReturn(response.result.items)

        val result = service.getStockItems("123")

        assertEquals(13, result.size)
    }
}