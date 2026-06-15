package ru.home.project.ozonapi.service

/**
 * Stores transaction-related cost price records.
 */
interface TransactionCostPriceService{

    /**
     * Обновляет себестоимость товара для заданного списка операций
     */
    fun updateCostPrice(deliveredOperaions: List<String>, sku: String)

    fun updateReturnedOperationCostPrice(returnedOperations: List<String>, sku: String)

}