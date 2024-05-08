package ru.home.project.ozonapi.service

import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.dto.response.RevenueResponse

/**
 * @author rlagay
 */
interface TotalRevenueCalculationService {

    /**
     * Расчет прибыльности за период по всем позициям
     */
    fun calculateRevenue(request: RevenueRequest): List<RevenueResponse>
}