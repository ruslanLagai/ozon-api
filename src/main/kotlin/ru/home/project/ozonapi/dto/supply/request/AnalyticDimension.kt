package ru.home.project.ozonapi.dto.supply.request

/**
 * @author rlagay
 */
enum class AnalyticDimension(val description: String) {
    sku("идентификатор товара"),
    spu("идентификатор товара"),
    day("день"),
    week("неделя"),
    month("месяц"),
    year("год"),
    category1("категория первого уровня"),
    category2("категория второго уровня"),
    category3("категория третьего уровня"),
    category4("категория четвертого уровня"),
    brand("бренд"),
    modelID("модель")
}
