package ru.home.project.ozonapi.service.impl

import org.springframework.stereotype.Service
import ru.home.project.ozonapi.dto.supply.request.AnalyticMetric
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.AnalyticService
import ru.home.project.ozonapi.service.OzonService
import java.time.LocalDate

/**
 * @author rlagay
 */
@Service
class AnalyticServiceImpl(
    val ozonService: OzonService,
    val positionRepository: PositionRepository
) : AnalyticService {

    fun updateViewAndConversionAnalytic() {
        val analyticData = ozonService.getAnalyticData(
            from = LocalDate.now(),
            to = LocalDate.now(),
            metrics = listOf(AnalyticMetric.hits_view_search, AnalyticMetric.hits_view,
                AnalyticMetric.hits_view_pdp, AnalyticMetric.hits_tocart_pdp, AnalyticMetric.hits_tocart)
        )
    }

}