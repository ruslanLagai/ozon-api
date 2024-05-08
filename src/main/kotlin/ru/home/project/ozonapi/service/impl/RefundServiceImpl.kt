package ru.home.project.ozonapi.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.dto.finance.response.OperationType
import ru.home.project.ozonapi.dto.finance.response.RefundData
import ru.home.project.ozonapi.dto.response.RefundResponse
import ru.home.project.ozonapi.dto.response.RefundsByClusterData
import ru.home.project.ozonapi.dto.response.RefundsByClusterResponse
import ru.home.project.ozonapi.model.parse
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.RefundService
import ru.home.project.ozonapi.util.clustersMap
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * @author rlagay
 */
@Service
class RefundServiceImpl(
    val ozonService: OzonService,
    val positionRepository: PositionRepository
) : RefundService {

    private val log: Logger = LoggerFactory.getLogger(RefundServiceImpl::class.java)
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME


    override fun getRefundsForPeriod(from: OffsetDateTime, to: OffsetDateTime, name: String): RefundResponse {
        val position = positionRepository.getPositionEntityByName(name)
        if (position == null) {
            log.error("No position in DB {}", name)
            return RefundResponse(error = "No position in DB", name = name)
        }

        val response = RefundResponse(name = name, sku = position.ozonId)
        val refundData = populateRefundData(from, to, position.ozonId)
        response.apply {
            refundsData.addAll(refundData)
            refundCount = refundData.size
            refundDeliveredCount = refundData.count { it.returnedDate != null && it.statusName.contains("на складе") }
            refundToBeDeliveredCount = refundData.count { it.returnedDate == null }
        }
        return response
    }


    override fun getRefundsForPeriodAndCluster(from: OffsetDateTime, to: OffsetDateTime, cluster: String, name: String): RefundsByClusterResponse {
        val position = positionRepository.getPositionEntityByName(name)
        if (position == null) {
            log.error("No position in DB {}", name)
            return RefundsByClusterResponse(error = "No position in DB", name = name)
        }
        val response = RefundsByClusterResponse(name = name, sku = position.ozonId, cluster = cluster)
        val refundData = populateRefundData(from, to, position.ozonId)
            .map { RefundsByClusterData(name = name, sku = position.ozonId, destination = it.destination, acceptedDate = it.acceptedDate,
                returnedDate = it.returnedDate) }
            .filter {
                val stocks = clustersMap[parse(cluster)]
                stocks!!.any { item -> it.destination.lowercase().contains(item.lowercase()) }
            }

        response.apply {
            data.addAll(refundData)
            refundCount = refundData.size
            refundDeliveredCount = refundData.count { it.returnedDate != null }
            refundToBeDeliveredCount = refundData.count { it.returnedDate == null }
        }
        return response
    }


    private fun populateRefundData(from: OffsetDateTime, to: OffsetDateTime, ozonId: String): List<RefundData> {
        val cacheKey = "from_" + from.format(formatter) + "_to_" + to.format(formatter)
        val postingNumbers = ozonService.getTransaction(from, to, cacheKey)
            .filter { it.operationType == OperationType.OperationItemReturn
                    || it.operationType == OperationType.ClientReturnAgentOperation }
            .filter { it.items.any { item -> item.sku == ozonId } }
            .map { it.posting.postingNumber }
            .toSet()

        val refundData = ArrayList<RefundData>()
        for (postingNumber in postingNumbers) {
            val refund = ozonService.getRefundData(postingNumber)
            if (refund == null) {
                log.warn("No refund data for {}", postingNumber)
                continue
            }
            //test
            if (refund.statusName.lowercase().contains("отмен") || refund.statusName.lowercase().contains("компенсирован")) {
                log.warn("!!!Статус возврата нужно проверить {}", postingNumber)
            }
            refundData.add(refund)
        }
        return refundData
    }
}