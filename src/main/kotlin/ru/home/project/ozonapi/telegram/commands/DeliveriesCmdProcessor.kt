package ru.home.project.ozonapi.telegram.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.repository.ChinaOrdersRepository

/**
 * @author rlagay
 */
@Component
class DeliveriesCmdProcessor(
    val chinaOrdersRepository: ChinaOrdersRepository
): CmdProcessor {

    private val log: Logger = LoggerFactory.getLogger(DeliveriesCmdProcessor::class.java)

    override fun processCmd(command: String, update: Update): SendMessage? {
        val msg = SendMessage()
        val sb = StringBuilder().append("Поставки в пути\n")
        msg.text = ""
        msg.chatId = update.message?.chatId.toString()
        try {
            val orders = chinaOrdersRepository.getChinaOrderEntityByDelivered(false)
            orders.forEach {
                val builder = StringBuilder().append(it.supplier)
                if (it.number != null) {
                    builder.append(" №${it.number}")
                }
                builder.append(" от " + it.orderDate + " на сумму " + it.stockCost)
                it.products?.forEach {product ->
                    builder.append("\n").append("  • ").append(product.name).append(" - ")
                        .append(product.quantity).append(" штук")
                }
                val text = builder.toString()
                sb.append(text).append("\n\n")
            }
            msg.text = if (orders.isEmpty()) { "Поставки в пути отсутствуют" } else { sb.toString() }
        } catch (e: Exception) {
            msg.text = "Не удалось получить список поставок"
            log.error("Failed to retrieve orders", e)
        }
        return msg
    }
}