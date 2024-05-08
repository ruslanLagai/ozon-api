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
    ACCEPTED_AT_SUPPLY_WAREHOUSE("принята на точке отгрузки."),
    IN_TRANSIT("в пути."),
    ACCEPTANCE_AT_STORAGE_WAREHOUSE("приёмка на складе."),
    REPORTS_CONFIRMATION_AWAITING("согласование актов."),
    REPORT_REJECTED("спор."),
    COMPLETED("завершена."),
    REJECTED_AT_SUPPLY_WAREHOUSE("отказано в приёмке."),
    CANCELLED("отменена."),
    OVERDUE("просрочена")
}