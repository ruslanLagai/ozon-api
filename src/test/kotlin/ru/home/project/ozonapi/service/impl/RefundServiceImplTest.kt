package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import ru.home.project.ozonapi.dto.finance.response.RefundResp
import ru.home.project.ozonapi.dto.finance.response.TransactionsResp
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.model.Cluster
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.util.readResource
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
@ExtendWith(MockitoExtension::class)
class RefundServiceImplTest {

    @Mock
    private lateinit var positionRepository: PositionRepository

    @Mock
    private lateinit var ozonService: OzonService

    @InjectMocks
    private lateinit var refundService: RefundServiceImpl

    @Test
    fun `test processing refund data for item`() {
        val name = "Вешалка для одежды, набор плечиков с нескользящим покрытием и вырезом под ворот, 10 штук, синие"
        Mockito.`when`(positionRepository.getPositionEntityByName(name))
            .thenReturn(PositionEntity(name = name, costPrice = 1.0, additionalCost = 1.0, ozonId = "1368970935", artikul = "12"))

        val transactions = readResource("refund/transactions-response.json", TransactionsResp::class.java)
            .result.operations
        Mockito.`when`(ozonService.getTransaction(any(), any(), any())).thenReturn(transactions)

        Mockito.verify(ozonService, times(0)).getRefundData(any())

        val result = refundService.getRefundsForPeriod(OffsetDateTime.now(), OffsetDateTime.now(), name)

        assertNull(result.error)
        assertEquals(0, result.refundCount)
        assertEquals(0, result.refundDeliveredCount)
        assertEquals(0, result.refundToBeDeliveredCount)
        assertEquals(name, result.name)
        assertEquals("1368970935", result.sku)
        assertEquals(0, result.refundsData.size)
    }


    @Test
    fun `test no refunds for item`() {
        val name = "Вешалка для одежды, набор плечиков с нескользящим покрытием и вырезом под ворот, 10 штук, синие"
        Mockito.`when`(positionRepository.getPositionEntityByName(name))
            .thenReturn(PositionEntity(name = name, costPrice = 1.0, additionalCost = 1.0, ozonId = "1368971009", artikul = "12"))

        val transactions = readResource("refund/transactions-response.json", TransactionsResp::class.java)
            .result.operations
        Mockito.`when`(ozonService.getTransaction(any(), any(), any())).thenReturn(transactions)

        val refundOne = readResource("refund/refund-status-on-stock-1.json", RefundResp::class.java).returns!![0]
        val refundTwo = readResource("refund/refund-status-on-stock-2.json", RefundResp::class.java).returns!![0]

        Mockito.`when`(ozonService.getRefundData("35982762-0410-1")).thenReturn(refundOne)
        Mockito.`when`(ozonService.getRefundData("35982762-0414-1")).thenReturn(refundTwo)

        val result = refundService.getRefundsForPeriod(OffsetDateTime.now(), OffsetDateTime.now(), name)

        assertNull(result.error)
        assertEquals(2, result.refundCount)
        assertEquals(2, result.refundDeliveredCount)
        assertEquals(0, result.refundToBeDeliveredCount)
        assertEquals(name, result.name)
        assertEquals("1368971009", result.sku)
        assertEquals(2, result.refundsData.size)
    }

    @Test
    fun `test processing refund data by cluster`() {
        val name = "зонт мини карманный, механический, складной маленький, розовый"
        Mockito.`when`(positionRepository.getPositionEntityByName(name))
            .thenReturn(PositionEntity(name = name, costPrice = 1.0, additionalCost = 1.0, ozonId = "1134731178", artikul = "12"))

        val transactions = readResource("refund/transactions-response.json", TransactionsResp::class.java)
            .result.operations
        Mockito.`when`(ozonService.getTransaction(any(), any(), any())).thenReturn(transactions)

        val refundOne = readResource("refund/refund-by-cluster-1.json", RefundResp::class.java).returns!![0]
        val refundTwo = readResource("refund/refund-by-cluster-2.json", RefundResp::class.java).returns!![0]
        val refundThree = readResource("refund/refund-by-cluster-3.json", RefundResp::class.java).returns!![0]
        val refundFour = readResource("refund/refund-by-cluster-4.json", RefundResp::class.java).returns!![0]
        val refundFive = readResource("refund/refund-by-cluster-5.json", RefundResp::class.java).returns!![0]

        Mockito.`when`(ozonService.getRefundData("16212139-0157-2")).thenReturn(refundOne)
        Mockito.`when`(ozonService.getRefundData("80966184-0007-1")).thenReturn(refundTwo)
        Mockito.`when`(ozonService.getRefundData("88125387-0286-1")).thenReturn(refundThree)
        Mockito.`when`(ozonService.getRefundData("49234487-0281-1")).thenReturn(refundFour)
        Mockito.`when`(ozonService.getRefundData("46951727-0242-1")).thenReturn(refundFive)


        val result = refundService.getRefundsForPeriodAndCluster(OffsetDateTime.now(), OffsetDateTime.now(), Cluster.SPB.value, name)

        assertNull(result.error)
        assertEquals(name, result.name)
        assertEquals(Cluster.SPB.value, result.cluster)
        assertEquals("1134731178", result.sku)
        assertEquals(3, result.data.size)
    }
}