package ru.home.project.ozonapi.service

import ru.home.project.ozonapi.dto.response.StocksResponse

/**
 * @author rlagay
 */
interface StocksService {

    /**
     * Получение данных по товарам:
     *  - на своих складах
     *  - складах озон
     *  - товарах в доставке
     *  - данных по поставкам из Китая
     */
    fun getStocks(): StocksResponse
}