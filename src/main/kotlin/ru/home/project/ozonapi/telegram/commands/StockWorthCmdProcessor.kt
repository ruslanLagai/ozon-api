package ru.home.project.ozonapi.telegram.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
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
            builder.append("- товар на складе            '${stocks.stocksWorth}'\n")
            if (stocks.products.isNotEmpty()) {
                stocks.products.forEach{
                    builder.append("  • '${it.value.name}'           '${it.value.totalStock}'")
                }
            }

            builder.append("- товар в доставке           '${stocks.deliveryWorth}\n")
            if (stocks.deliveries.isNotEmpty()) {
                stocks.deliveries.forEach{
                    builder.append("  • '${it.value.name}'           '${it.value.totalStock}'")
                }
            }

            builder.append("- заказы из Китая            '${stocks.stocksOnWayWorth}'")
            if (stocks.orders.isNotEmpty()) {
                stocks.orders.forEach {
                    builder.append("  • '${it.number ?: it.supplier}'           '${it.stockCost}'")
                }
            }
            msg.text = builder.toString()
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