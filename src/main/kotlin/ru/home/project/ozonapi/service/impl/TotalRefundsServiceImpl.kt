package ru.home.project.ozonapi.service.impl

import org.springframework.stereotype.Service
import ru.home.project.ozonapi.dto.response.RefundResponse
import ru.home.project.ozonapi.dto.response.RefundsByClusterResponse
import ru.home.project.ozonapi.dto.response.RefundsByNameResponse
import ru.home.project.ozonapi.dto.response.TotalRefundsResponse
import ru.home.project.ozonapi.model.Cluster
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.RefundService
import ru.home.project.ozonapi.service.TotalRefundsService
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
@Service
class TotalRefundsServiceImpl(
    val refundService: RefundService,
    val positionRepository: PositionRepository
): TotalRefundsService {

    override fun getRefundsData(from: OffsetDateTime, to: OffsetDateTime): TotalRefundsResponse {
        val response = TotalRefundsResponse()
        val refundItems = ArrayList<RefundResponse>()
        val positions = positionRepository.findAll()
        positions.chunked(5)
            .parallelStream()
            .forEach { list ->
                list.stream().forEach {
                    val refund = refundService.getRefundsForPeriod(from, to, it.name)
                    if (refund.refundsData.isNotEmpty()) {
                        refundItems.add(refund)
                    }
                }
            }
        response.apply {
            totalRefundsCount = refundItems.sumOf { it.refundCount }
            totalRefundsDeliveredCount = refundItems.sumOf { it.refundDeliveredCount }
            totalRefundsToBeDeliveredCount = refundItems.sumOf { it.refundToBeDeliveredCount }
            refundsData.addAll(refundItems)

            val refundsList = refundItems.groupBy { it.name }
                .map {
                    RefundsByNameResponse(name = it.key, refundsCount = it.value.sumOf { item -> item.refundCount },
                        refundsDelivered = it.value.sumOf { item -> item.refundDeliveredCount },
                        refundsToBeDelivered = it.value.sumOf { item -> item.refundToBeDeliveredCount })
                }
            refundsByNameData.addAll(refundsList)
        }
        return response
    }

    override fun getRefundsDataByClusters(from: OffsetDateTime, to: OffsetDateTime): TotalRefundsResponse {
        val response = TotalRefundsResponse()
        val refundItems = ArrayList<RefundsByClusterResponse>()
        val positions = positionRepository.findAll()
        positions.chunked(5)
            .parallelStream()
            .forEach { list ->
                list.stream().forEach {
                    Cluster.entries.forEach { cluster ->
                        val refund = refundService.getRefundsForPeriodAndCluster(from, to, cluster.value, it.name)
                        if (refund.data.isNotEmpty()) {
                            refundItems.add(refund)
                        }
                    }
                }
            }
        response.apply {
            totalRefundsCount = refundItems.sumOf { it.refundCount }
            totalRefundsDeliveredCount = refundItems.sumOf { it.refundDeliveredCount }
            totalRefundsToBeDeliveredCount = refundItems.sumOf { it.refundToBeDeliveredCount }
            refundsByClusterData.addAll(refundItems)

            refundItems.groupBy { it.cluster }.forEach { (k, v) ->
                val totalRefunds = TotalRefundsResponse(
                    totalRefundsCount = v.sumOf {  it.refundCount },
                    totalRefundsDeliveredCount = v.sumOf { it.refundDeliveredCount },
                    totalRefundsToBeDeliveredCount = v.sumOf { it.refundToBeDeliveredCount }
                )
                totalRefunds.refundsByClusterData.addAll(v)
                itemByCluster[k] = totalRefunds
            }

        }
        return response
    }

}