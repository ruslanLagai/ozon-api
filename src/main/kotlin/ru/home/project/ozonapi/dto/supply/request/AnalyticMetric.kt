package ru.home.project.ozonapi.dto.supply.request

/**
 * @author rlagay
 */
enum class AnalyticMetric(val description: String) {
    revenue("заказано на сумму"),
    ordered_units("заказано товаров"),
    unknown_metric ("неизвестная метрика"),
    hits_view_search ("показы в поиске и в категории"),
    hits_view_pdp ("показы на карточке товара"),
    hits_view ("всего показов"),
    hits_tocart_search ("в корзину из поиска или категории"),
    hits_tocart_pdp ("в корзину из карточки товара"),
    hits_tocart ("всего добавлено в корзину"),
    session_view_search ("сессии с показом в поиске или в каталоге. Считаются уникальные посетители с просмотром в поиске или каталоге"),
    session_view_pdp ("сессии с показом на карточке товара. Считаются уникальные посетители, которые просмотрели карточку товара"),
    session_view ("всего сессий. Считаются уникальные посетители"),
    conv_tocart_search ("конверсия в корзину из поиска или категории"),
    conv_tocart_pdp ("конверсия в корзину из карточки товара"),
    conv_tocart ("общая конверсия в корзину"),
    returns ("возвращено товаров"),
    cancellations ("отменено товаров"),
    delivered_units ("доставлено товаров"),
    position_category ("позиция в поиске и категории")

}
