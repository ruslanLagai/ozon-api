package ru.home.project.ozonapi.dto.delivery

/**
 * @author rlagay
 */
enum class DeliveryStatus(val description: String) {
    awaiting_packaging("ожидает упаковки"),
    awaiting_deliver("ожидает отгрузки"),
    delivering("доставляется"),
    delivered("доставлено"),
    cancelled("отменено")
}