package ru.home.project.ozonapi.telegram.text

import org.apache.commons.lang3.function.TriFunction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.dto.response.RevenueResponse
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.MarketType
import ru.home.project.ozonapi.entity.TelegramChatEntity
import ru.home.project.ozonapi.exception.OzonException
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.service.RevenueCalculationService
import ru.home.project.ozonapi.service.TotalRefundsService
import ru.home.project.ozonapi.service.TotalRevenueCalculationService
import ru.home.project.ozonapi.util.*
import java.time.OffsetDateTime
import java.util.*
import java.util.function.BiFunction
import java.util.regex.Pattern

/**
 * @author rlagay
 */
@Component
class DateInputProcessor(
    val telegramChatRepository: TelegramChatRepository,
    val revenueCalculationServices: List<RevenueCalculationService>,
    val ozonTotalRevenueCalculationServiceImpl: TotalRevenueCalculationService,
    val totalRefundsService: TotalRefundsService
): TextInputProcessor {


    companion object {
        val log: Logger = LoggerFactory.getLogger(DateInputProcessor::class.java)
    }

    private val datePattern: Pattern = Pattern.compile("\\d{1,2}.\\d{2}.\\d{2,4}\\s?-\\s?\\d{1,2}.\\d{2}.\\d{2,4}")
    private val predefinedDates: Set<String> = setOf(lastDayDate, lastTwoDaysDate, forCurrentMonth, forCurrentWeek)

    private val dateProcessor = mapOf<ActionType, TriFunction<Update, String, TelegramChatEntity, SendMessage?>>(
        Pair(ActionType.Revenue, TriFunction { update, input, chat -> processRevenue(update, input, chat) }),
        Pair(ActionType.Refund, TriFunction { update, input, chat -> processRefund(update, input, chat) })
    )

    private val marketProccessor = mapOf<MarketType, BiFunction<TelegramChatEntity, RevenueRequest, String>>(
        Pair(MarketType.Ozon, BiFunction { chat, request -> calculateOzon(chat, request) })
    )

    override fun processInput(input: String, update: Update): SendMessage? {
        val isDateInput = predefinedDates.contains(input) ||  datePattern.matcher(input).matches()
        if (isDateInput) {
            val chatId = update.message.chatId
            val chat = telegramChatRepository.getByChatIdAndState(chatId, true)
            return if (chat != null && dateProcessor.containsKey(chat.action)) {
                dateProcessor[chat.action]!!.apply(update, input, chat)
            } else {
                null
            }
        }
        return null
    }

    private fun processRevenue(update: Update, input: String, chatEntity: TelegramChatEntity): SendMessage? {
        val text: String
        val sendMessage = SendMessage()

        sendMessage.chatId = update.message?.chatId.toString()
        if (update.message == null || update.message.chatId == null) {
            return null
        }
        val fromDate = input.substringBefore("-")
        val toDate = input.substringAfter("-")
        val chatId = update.message.chatId

        try {
            val to = parseToDate(toDate)
            val from = parseFromDate(fromDate)

            val request = RevenueRequest(name = chatEntity.positionName, to = to, from = from)
            text = marketProccessor[chatEntity.market]!!.apply(chatEntity, request)
            val msg = SendMessage()
            msg.chatId = update.message?.chatId.toString()
            msg.text = text
            return msg
        } catch (e: Exception) {
            log.error(e.message, e)
            throw e
        } finally {
            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.Revenue)
        }
    }

    private fun calculateOzon(
        chatEntity: TelegramChatEntity,
        request: RevenueRequest
    ) = if (chatEntity.positionName == allItems) {
        calculateAllItems(request)
    } else {
        calculateItem(request)
    }

    private fun processRefund(update: Update, input: String, chatEntity: TelegramChatEntity): SendMessage? {
        val text: String
        val sendMessage = SendMessage()

        sendMessage.chatId = update.message?.chatId.toString()
        if (update.message == null || update.message.chatId == null) {
            return null
        }
        val fromDate = input.substringBefore("-")
        val toDate = input.substringAfter("-")
        val chatId = update.message.chatId

        try {
            val to = parseToDate(toDate)
            val from = parseFromDate(fromDate)

            text = if (chatEntity.positionName == refundsByCluster) {
                processRefundByCluster(from, to)
            } else {
                processRefund(from, to)
            }
            val msg = SendMessage()
            msg.chatId = update.message?.chatId.toString()
            msg.text = text
            return msg
        } catch (e: Exception) {
            log.error(e.message, e)
            throw e
        } finally {
            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.Refund)
        }
    }

    private fun calculateAllItems(request: RevenueRequest): String {
        val allItemsRequest = RevenueRequest(to = request.to, from = request.from, name = null)
        var result: List<RevenueResponse>
        var text = ""
        kotlin.runCatching {
            result = ozonTotalRevenueCalculationServiceImpl.calculateRevenue(allItemsRequest)
            if (result.isEmpty()) {
                log.warn("Revenue calculation is null, request '${allItemsRequest}'")
                text = "Не удалось рассчитать маржинальность"
            }
            text = produceOzonAllItemsMessage(result)
        }.onFailure {
            text = when (it) {
                is OzonException -> "Ошибка от апи Озон, попробуйте позже"
                else -> "Не удалось рассчитать маржинальность, попробуйте еще раз"
            }
            log.error("Error is: {}", it.message, it)
        }
        return text
    }

    private fun calculateItem(request: RevenueRequest): String {
        val result = revenueCalculationServices.map { it.calculateRevenue(request) }.firstOrNull(Objects::nonNull)

        if (result == null) {
            log.warn("Revenue calculation is null, request ${request}")
            return "Не удалось рассчитать маржинальность"
        }
        return produceOzonItemMessage(result)
    }

    private fun processRefundByCluster(from: OffsetDateTime, to: OffsetDateTime): String {
        val result = totalRefundsService.getRefundsDataByClusters(from, to)
        val sb = StringBuilder()
        result.apply {
            val msg = "\uD83D\uDCDD Статистика по возвратам:\n" +
                    "Количество возвратов   $totalRefundsCount\n" +
                    " - в доставке                       $totalRefundsToBeDeliveredCount\n" +
                    " - на складе                        $totalRefundsDeliveredCount\n"
            sb.append(msg).append("\n")
        }
        result.itemByCluster
            .forEach {
                val msg = "\uD83D\uDCDD ${it.key}:\n" +
                        "Количество возвратов   ${it.value.totalRefundsCount}\n" +
                        " - в доставке                      ${it.value.totalRefundsToBeDeliveredCount}\n" +
                        " - на складе                       ${it.value.totalRefundsDeliveredCount}\n"
                val msgInDelivery = if (it.value.totalRefundsToBeDeliveredCount != 0) {
                    val s = StringBuilder().append("• В доставке:\n")
                    it.value.refundsByClusterData
                        .filter { item -> item.refundToBeDeliveredCount != 0 }
                        .forEach { item -> s.append("- ").append(item.refundToBeDeliveredCount).append(" ").append(item.name).append("\n") }
                    s.toString()
                } else { "" }
                val msgDelivered = if (it.value.totalRefundsDeliveredCount != 0) {
                    val s = StringBuilder().append("• Прибыли на склад:\n")
                    it.value.refundsByClusterData
                        .filter { item -> item.refundDeliveredCount != 0 }
                        .forEach { item -> s.append("- ").append(item.refundDeliveredCount).append(" ").append(item.name).append("\n") }
                    s.toString()
                } else { "" }
                sb.append(msg).append(msgInDelivery).append(msgDelivered).append("\n")
            }
        return sb.toString()
    }

    private fun processRefund(from: OffsetDateTime, to: OffsetDateTime): String {
        val result = totalRefundsService.getRefundsData(from, to)
        val sb = StringBuilder()
        result.apply {
            val msg = "\uD83D\uDCDD Статистика по возвратам:\n" +
                    "Количество возвратов   $totalRefundsCount\n" +
                    " - в доставке                      $totalRefundsToBeDeliveredCount\n" +
                    " - на складе                       $totalRefundsDeliveredCount\n"
            sb.append(msg).append("\n")
        }
        result.refundsByNameData
            .filter { it.refundsCount != 0 }
            .forEach {
                val msg = "\uD83D\uDCDD ${it.name}:\n" +
                        "Количество возвратов   ${it.refundsCount}\n" +
                        " - в доставке                      ${it.refundsToBeDelivered}\n" +
                        " - на складе                       ${it.refundsDelivered}\n"
                sb.append(msg).append("\n")
            }
        return sb.toString()
    }
}