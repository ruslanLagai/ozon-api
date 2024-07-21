package ru.home.project.ozonapi.util

import ru.home.project.ozonapi.dto.response.RevenueResponse
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * @author rlagay
 */

fun produceOzonAllItemsMessage(result: List<RevenueResponse>): String {
    val sb = StringBuilder()

    result.first().apply {
        val msg = "\uD83D\uDCDD По всем позициям\n" +
                "Оборот                                 $totalPrice\n" +
                "Чистая прибыль                 $totalRevenueForAllDeliveredItems\n" +
                "Себестоимость                   $totalCostPrice\n" +
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
                        "Чистая прибыль        $totalRevenue\n" +
                        "Себестоимость         $costPrice\n" +
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

fun produceYandexAllItemsMessage(result: List<RevenueResponse>): String {
    val sb = StringBuilder()

    result.first().apply {
        val msg = "\uD83D\uDCDD По всем позициям\n" +
                "Оборот                                 $totalPrice\n" +
                "Чистая прибыль                 $totalRevenueForAllDeliveredItems\n" +
                "Себестоимость                   $totalCostPrice\n" +
                " - Логистика                        $totalLogisticCosts\n" +
                " - Комиссия яндекс                $totalCommissionCosts\n" +
                " - Реклама                            $marketingCosts\n" +
                " - Возвраты                          $totalRefund\n" +
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
                        "Чистая прибыль        $totalRevenue\n" +
                        "Себестоимость         $costPrice\n" +
                        " - Комиссия яндекс        $saleCommission\n" +
                        " - Логистика                $logistic\n" +
                        " - Реклама                  $refund\n" +
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

fun produceOzonItemMessage(result: RevenueResponse): String {
    val text: String

    result.apply {
        text = if (result.errorMessage != null) {
            result.errorMessage as String
        } else {
            "\uD83D\uDCDD Расчет прибыли по ${name}\n" +
                    "Оборот                      $price\n" +
                    "Себестоимость         $costPrice\n" +
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

fun produceYandexItemMessage(result: RevenueResponse): String {
    val text: String

    result.apply {
        text = if (result.errorMessage != null) {
            result.errorMessage as String
        } else {
            "\uD83D\uDCDD Расчет прибыли по ${name}\n" +
                    "Оборот                      $price\n" +
                    "Себестоимость         $costPrice\n" +
                    "Чистая прибыль         $totalRevenue\n" +
                    " - Комиссия яндекс        $saleCommission\n" +
                    " - Логистика                $logistic\n" +
                    " - Реклама            $lastMile\n" +
                    "Средняя прибыль       $averageRevenue\n" +
                    "Доставки                      $deliveryItemCount\n" +
                    "Налог                             $taxes\n" +
                    "Возвраты                      $refundCount\n" +
                    "Продано                        $soldItemsCount\n"
        }
    }
    return text
}