# OrdersStatsApi

All URIs are relative to *https://api.partner.market.yandex.ru*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getOrdersStats**](OrdersStatsApi.md#getOrdersStats) | **POST** /campaigns/{campaignId}/stats/orders | Детальная информация по заказам


<a id="getOrdersStats"></a>
# **getOrdersStats**
> GetOrdersStatsResponse getOrdersStats(campaignId, pageToken, limit, getOrdersStatsRequest)

Детальная информация по заказам

{% include notitle [access](../../_auto/method_scopes/getOrdersStats.md) %}  {% note info \&quot;Раньше генерация детальной информации по заказам называлась «Отчет по заказам»\&quot; %}  Сейчас это новый отчет. [Подробнее об отчете](../../reference/reports/generateUnitedOrdersReport.md)  {% endnote %}  Возвращает информацию по заказам на Маркете, в которых есть ваши товары. С помощью нее вы можете собрать статистику по вашим заказам и узнать, например, какие из товаров чаще всего возвращаются покупателями, какие, наоборот, пользуются большим спросом, какая комиссия начисляется за заказы и т. п.  {% note tip \&quot;Информация по созданным или обновленным заказам может появиться с задержкой до 40 минут\&quot; %}  Чтобы получить данные без задержки, используйте [метод получения информации о заказах](../../reference/orders/getOrders.md).  {% endnote %}  В одном запросе можно получить информацию не более чем по 200 заказам.  |**⚙️ Лимит:** 1 000 000 заказов в час| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = OrdersStatsApi()
val campaignId : kotlin.Long = 789 // kotlin.Long | Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val pageToken : kotlin.String = eyBuZXh0SWQ6IDIzNDIgfQ== // kotlin.String | Идентификатор страницы c результатами.  Если параметр не указан, возвращается первая страница.  Рекомендуется передавать значение выходного параметра `nextPageToken`, полученное при последнем запросе.  Если задан `page_token` и в запросе есть параметры `offset`, `page_number` и `page_size`, они игнорируются. 
val limit : kotlin.Int = 20 // kotlin.Int | Количество значений на одной странице. 
val getOrdersStatsRequest : GetOrdersStatsRequest =  // GetOrdersStatsRequest | 
try {
    val result : GetOrdersStatsResponse = apiInstance.getOrdersStats(campaignId, pageToken, limit, getOrdersStatsRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling OrdersStatsApi#getOrdersStats")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling OrdersStatsApi#getOrdersStats")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaignId** | **kotlin.Long**| Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **pageToken** | **kotlin.String**| Идентификатор страницы c результатами.  Если параметр не указан, возвращается первая страница.  Рекомендуется передавать значение выходного параметра &#x60;nextPageToken&#x60;, полученное при последнем запросе.  Если задан &#x60;page_token&#x60; и в запросе есть параметры &#x60;offset&#x60;, &#x60;page_number&#x60; и &#x60;page_size&#x60;, они игнорируются.  | [optional]
 **limit** | **kotlin.Int**| Количество значений на одной странице.  | [optional]
 **getOrdersStatsRequest** | [**GetOrdersStatsRequest**](GetOrdersStatsRequest.md)|  | [optional]

### Return type

[**GetOrdersStatsResponse**](GetOrdersStatsResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

