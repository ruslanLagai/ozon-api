package ru.home.project.ozonapi.service.impl

import org.springframework.stereotype.Service
import ru.home.project.ozonapi.service.AnalyticService
import ru.home.project.ozonapi.service.OzonService

/**
 * @author rlagay
 */
@Service
class AnalyticServiceImpl(
    val ozonService: OzonService
) : AnalyticService {

    fun updateViewAndConversionAnalytic() {

    }

}