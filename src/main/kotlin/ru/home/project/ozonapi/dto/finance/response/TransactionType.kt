package ru.home.project.ozonapi.dto.finance.response

/**
 * @author rlagay
 */
enum class TransactionType(val description: String) {
    all("все"),
    orders("заказы"),
    returns("возвраты и отмены"),
    services("сервисные сборы"),
    compensation("компенсация"),
    transferDelivery("стоимость доставки"),
    other("прочее")
}