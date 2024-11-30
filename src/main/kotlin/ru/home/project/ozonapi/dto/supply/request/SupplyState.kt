package ru.home.project.ozonapi.dto.supply.request

/**
 * @author rlagay
 */
enum class SupplyState(val description: String) {
    DRAFT("черновик заявки. Только для заявок с вРЦ."),
    SUPPLY_VARIANTS_ARRANGING("подбор вариантов отгрузки. Только для заявок с вРЦ."),
    HAS_NO_SUPPLY_VARIANTS_ARCHIVE("нет вариантов отгрузки. Заявка в архиве. Только для заявок с вРЦ."),
    HAS_NO_SUPPLY_VARIANTS_NEW("нет вариантов отгрузки. Только для заявок с вРЦ."),
    SUPPLY_VARIANT_CONFIRMATION("согласование отгрузки. Только для заявок с вРЦ."),
    TIMESLOT_BOOKING("бронирование времени."),
    DATA_FILLING("заполнение данных."),
    READY_TO_SUPPLY("готова к отгрузке."),
    ORDER_STATE_ACCEPTED_AT_SUPPLY_WAREHOUSE("принята на точке отгрузки."),
    ORDER_STATE_IN_TRANSIT("в пути."),
    ORDER_STATE_ACCEPTANCE_AT_STORAGE_WAREHOUSE("приёмка на складе."),
    ORDER_STATE_REPORTS_CONFIRMATION_AWAITING("согласование актов."),
    ORDER_STATE_REPORT_REJECTED("спор."),
    ORDER_STATE_COMPLETED("завершена."),
    ORDER_STATE_REJECTED_AT_SUPPLY_WAREHOUSE("отказано в приёмке."),
    ORDER_STATE_CANCELLED("отменена."),
    OVERDUE("просрочена")
}