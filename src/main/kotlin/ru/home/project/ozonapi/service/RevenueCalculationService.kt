package ru.home.project.ozonapi.service

import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.dto.response.RevenueResponse

/**
 * @author rlagay
 */
interface RevenueCalculationService {

    /**
     * Расчет транзакций по
     */
    fun calculateRevenue(request: RevenueRequest): RevenueResponse?
}