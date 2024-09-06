package ru.home.project.ozonapi.telegram.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.exception.InvalidStocksException
import ru.home.project.ozonapi.service.StocksService

/**
 * @author rlagay
 */
@Component
class StockWorthCmdProcessor(
    val stocksService: StocksService
): CmdProcessor {

    private val log: Logger = LoggerFactory.getLogger(StockWorthCmdProcessor::class.java)

    override fun processCmd(command: String, update: Update): SendMessage? {
        try {
            val stocks = stocksService.getStocks()
            val msg = SendMessage()
            msg.chatId = update.message?.chatId.toString()
            val builder = StringBuilder()
            builder.append("Стоимость товара:\n")
            builder.append("- товар на складе            ${stocks.stocksWorth}\n")
            if (stocks.products.isNotEmpty()) {
                stocks.products.forEach{
                    val number = when (it.value.totalStock.toString().length) {
                        1 -> "   ${it.value.totalStock}"
                        2 -> "  ${it.value.totalStock}"
                        3 -> " ${it.value.totalStock}"
                        4 -> it.value.totalStock
                        else -> it.value.totalStock
                    }
                    builder.append("  • $number - ${it.value.name}").append("\n")
                }
            }
            builder.append("\n")

            builder.append("- товар в доставке Озон           ${stocks.deliveryWorth}\n")
            if (stocks.deliveries.isNotEmpty()) {
                stocks.deliveries.forEach{
                    val number = when (it.value.totalStock.toString().length) {
                        1 -> "   ${it.value.totalStock}"
                        2 -> "  ${it.value.totalStock}"
                        3 -> " ${it.value.totalStock}"
                        4 -> it.value.totalStock
                        else -> it.value.totalStock
                    }
                    builder.append("  • $number - ${it.value.name}").append("\n")
                }
            }
            builder.append("\n")

            builder.append("- товар в доставке Яндекс           ${stocks.yandexDeliveryWorth}\n")
            if (stocks.yandexDeliveries.isNotEmpty()) {
                stocks.yandexDeliveries.forEach{
                    val number = when (it.value.totalStock.toString().length) {
                        1 -> "   ${it.value.totalStock}"
                        2 -> "  ${it.value.totalStock}"
                        3 -> " ${it.value.totalStock}"
                        4 -> it.value.totalStock
                        else -> it.value.totalStock
                    }
                    builder.append("  • $number - ${it.value.name}").append("\n")
                }
            }
            builder.append("\n")

            builder.append("- заказы из Китая            ${stocks.stocksOnWayWorth}\n")
            if (stocks.orders.isNotEmpty()) {
                stocks.orders.forEach {

                    builder.append("  • ${it.stockCost} - ").append(it.supplier)
                    if (!it.number.isNullOrEmpty()) {
                        builder.append(" №${it.number}")
                    }
                    builder.append(" от " + it.orderDate).append("\n")
                }
            }
            builder.append("\n")
            msg.text = builder.toString()
            return msg
        } catch (e: InvalidStocksException) {
            log.error("Failed to calculate stock worth", e)
            val msg = SendMessage()
            msg.chatId = update.message?.chatId.toString()
            msg.text = "Ошибка при расчете остатков, необходимо проверить/исправить остатки"
            return msg
        } catch (e: Exception) {
            log.error("Failed to calculate stock worth", e)
            val msg = SendMessage()
            msg.chatId = update.message?.chatId.toString()
            msg.text = "Не удалось рассчитать стоимость остатков"
            return msg
        }
    }
}