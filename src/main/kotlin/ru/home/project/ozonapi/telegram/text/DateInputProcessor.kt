package ru.home.project.ozonapi.telegram.text

import org.apache.commons.lang3.function.TriFunction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.TelegramChatEntity
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
    val totalRevenueCalculationService: TotalRevenueCalculationService,
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
            text = if (chatEntity.positionName == allItems) {
                calculateAllItems(request)
            } else {
                calculateItem(request)
            }
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
        val result = totalRevenueCalculationService.calculateRevenue(allItemsRequest)
        val sb = StringBuilder()

        if (result.isEmpty()) {
            log.warn("Revenue calculation is null, request '${allItemsRequest}'")
            return "Не удалось рассчитать маржинальность"
        }

        result.first().apply {
            val msg = "\uD83D\uDCDD По всем позициям\n" +
                    "Оборот                                 $totalPrice\n" +
                    "Чистая прибыль                 $totalRevenueForAllDeliveredItems\n" +
                    " - Логистика                        $totalLogisticCosts\n" +
                    " - Последняя миля             $totalLastMileCosts\n" +
                    " - Комиссия озон                $totalCommissionCosts\n" +
                    " - Реклама                            $marketingCosts\n" +
                    "   • Трафареты                       $stencil\n" +
                    "   • Продвижение                 $promotionInSearch\n" +
                    " - Возвраты                          $totalRefund\n" +
                    " - Отзывы                             $totalFeedBackCost\n" +
                    "   • Закрепление                   $pinFeedback\n" +
                    "   • Отзывы за балы              $feedbackCosts\n" +
                    " - Утилизация                     $destroyCosts\n" +
                    " - Премиум                          $premium\n" +
                    " - Компенсация                  $compensation\n" +
                    " - Кросс-док                         $xDoc\n" +
                    " - Обработка брака            $spoilageCosts\n" +
                    " - Видеообложка                  $videoCoverCosts\n" +
                    " - Хранение                          $storageCosts\n" +
                    "Доставки                              $totalDeliveryItemCount\n" +
                    "Возвраты                             $totalRefundsCount\n" +
                    "Количество продаж          $soldItemsCount\n" +
                    "Налоги                                 $totalTaxes\n"

            sb.append(msg).append("\n-----------------------------------\n")
        }

        result
            .filter { it.errorMessage == null }
            .filter { it.totalRevenue != 0.0 || it.deliveryItemCount != 0 || it.refundCount != 0 }
            .forEach {
                it.apply {
                    val message = "\uD83D\uDCDD ${it.name}\n" +
//                            "Оборот                        $price\n" +
                            "Чистая прибыль        $totalRevenue\n" +
                            " - Комиссия озон        $saleCommission\n" +
                            " - Логистика                $logistic\n" +
                            " - Последняя миля      $lastMile\n" +
                            " - Возвраты                 $refund\n" +
                            "Средняя прибыль      $averageRevenue\n" +
                            "Доставки                       $deliveryItemCount\n" +
                            "Налог                              $taxes\n" +
                            "Возвраты                      $refundCount\n"
                    sb.append(message).append("\n")
                }
            }
        return sb.toString()
    }

    private fun calculateItem(request: RevenueRequest): String {
        val result = revenueCalculationServices.map { it.calculateRevenue(request) }.firstOrNull(Objects::nonNull)
        val text: String

        if (result == null) {
            log.warn("Revenue calculation is null, request ${request}")
            return "Не удалось рассчитать маржинальность"
        }

        result.apply {
            text = if (result.errorMessage != null) {
                result.errorMessage as String
            } else {
                "\uD83D\uDCDD Расчет прибыли по ${name}\n" +
                        "Оборот                      $price\n" +
                        "Чистая прибыль         $totalRevenue\n" +
                        " - Комиссия озон        $saleCommission\n" +
                        " - Логистика                $logistic\n" +
                        " - Последняя миля      $lastMile\n" +
                        "Средняя прибыль       $averageRevenue\n" +
                        "Доставки                      $deliveryItemCount\n" +
                        "Налог                             $taxes\n" +
                        "Возвраты                      $refundCount\n" +
                        "Продано                        $soldItemsCount\n"
            }
        }
        return text
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