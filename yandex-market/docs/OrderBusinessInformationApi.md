# OrderBusinessInformationApi

All URIs are relative to *https://api.partner.market.yandex.ru*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getOrderBusinessBuyerInfo**](OrderBusinessInformationApi.md#getOrderBusinessBuyerInfo) | **POST** /campaigns/{campaignId}/orders/{orderId}/business-buyer | Информация о покупателе — юридическом лице
[**getOrderBusinessDocumentsInfo**](OrderBusinessInformationApi.md#getOrderBusinessDocumentsInfo) | **POST** /campaigns/{campaignId}/orders/{orderId}/documents | Информация о документах


<a id="getOrderBusinessBuyerInfo"></a>
# **getOrderBusinessBuyerInfo**
> GetBusinessBuyerInfoResponse getOrderBusinessBuyerInfo(campaignId, orderId)

Информация о покупателе — юридическом лице

{% include notitle [access](../../_auto/method_scopes/getOrderBusinessBuyerInfo.md) %}  Возвращает информацию о покупателе по идентификатору заказа.  {% note info \&quot;Как получить информацию о покупателе, который является физическим лицом\&quot; %}  Воспользуйтесь запросом [GET campaigns/{campaignId}/orders/{orderId}/buyer](../../reference/orders/getOrderBuyerInfo.md).  {% endnote %}  Получить данные можно, только если заказ находится в статусе &#x60;PROCESSING&#x60;, &#x60;DELIVERY&#x60;, &#x60;PICKUP&#x60; или &#x60;DELIVERED&#x60;.  |**⚙️ Лимит:** 3 000 запросов в час| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = OrderBusinessInformationApi()
val campaignId : kotlin.Long = 789 // kotlin.Long | Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val orderId : kotlin.Long = 789 // kotlin.Long | Идентификатор заказа.
try {
    val result : GetBusinessBuyerInfoResponse = apiInstance.getOrderBusinessBuyerInfo(campaignId, orderId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling OrderBusinessInformationApi#getOrderBusinessBuyerInfo")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling OrderBusinessInformationApi#getOrderBusinessBuyerInfo")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaignId** | **kotlin.Long**| Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **orderId** | **kotlin.Long**| Идентификатор заказа. |

### Return type

[**GetBusinessBuyerInfoResponse**](GetBusinessBuyerInfoResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getOrderBusinessDocumentsInfo"></a>
# **getOrderBusinessDocumentsInfo**
> GetBusinessDocumentsInfoResponse getOrderBusinessDocumentsInfo(campaignId, orderId)

Информация о документах

{% include notitle [access](../../_auto/method_scopes/getOrderBusinessDocumentsInfo.md) %}  Возвращает информацию о документах по идентификатору заказа.  Получить данные можно после того, как заказ перейдет в статус &#x60;DELIVERED&#x60;.  |**⚙️ Лимит:** 3 000 запросов в час| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = OrderBusinessInformationApi()
val campaignId : kotlin.Long = 789 // kotlin.Long | Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val orderId : kotlin.Long = 789 // kotlin.Long | Идентификатор заказа.
try {
    val result : GetBusinessDocumentsInfoResponse = apiInstance.getOrderBusinessDocumentsInfo(campaignId, orderId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling OrderBusinessInformationApi#getOrderBusinessDocumentsInfo")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling OrderBusinessInformationApi#getOrderBusinessDocumentsInfo")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaignId** | **kotlin.Long**| Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **orderId** | **kotlin.Long**| Идентификатор заказа. |

### Return type

[**GetBusinessDocumentsInfoResponse**](GetBusinessDocumentsInfoResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

