package ru.home.project.ozonapi.dto.supply.request

/**
 * @author rlagay
 */
enum class SupplyState(val description: String) {
    DATA_FILLING("заполнение данных"),
    READY_TO_SUPPLY("готова к отгрузке"),
    UNSPECIFIED("не определён"),
    ACCEPTED_AT_SUPPLY_WAREHOUSE("принята на точке отгрузки."),
    IN_TRANSIT("в пути."),
    ACCEPTANCE_AT_STORAGE_WAREHOUSE("приёмка на складе."),
    REPORTS_CONFIRMATION_AWAITING("согласование актов."),
    REPORT_REJECTED("спор."),
    COMPLETED("завершена."),
    REJECTED_AT_SUPPLY_WAREHOUSE("отказано в приёмке."),
    CANCELLED("отменена."),
    OVERDUE("просрочена"),
    UNDEFINED("")
}