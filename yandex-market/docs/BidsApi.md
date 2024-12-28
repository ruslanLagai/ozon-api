# BidsApi

All URIs are relative to *https://api.partner.market.yandex.ru*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getBidsInfoForBusiness**](BidsApi.md#getBidsInfoForBusiness) | **POST** /businesses/{businessId}/bids/info | Информация об установленных ставках
[**getBidsRecommendations**](BidsApi.md#getBidsRecommendations) | **POST** /businesses/{businessId}/bids/recommendations | Рекомендованные ставки для заданных товаров
[**putBidsForBusiness**](BidsApi.md#putBidsForBusiness) | **PUT** /businesses/{businessId}/bids | Включение буста продаж и установка ставок
[**putBidsForCampaign**](BidsApi.md#putBidsForCampaign) | **PUT** /campaigns/{campaignId}/bids | Включение буста продаж и установка ставок для магазина


<a id="getBidsInfoForBusiness"></a>
# **getBidsInfoForBusiness**
> GetBidsInfoResponse getBidsInfoForBusiness(businessId, pageToken, limit, getBidsInfoRequest)

Информация об установленных ставках

{% include notitle [access](../../_auto/method_scopes/getBidsInfoForBusiness.md) %}  Возвращает значения ставок для заданных товаров.  {% note warning \&quot;Получить информацию по кампаниям, созданным в кабинете, не получится\&quot; %}  В ответе возвращаются значения только тех ставок, которые вы установили через запрос [PUT businesses/{businessId}/bids](../../reference/bids/putBidsForBusiness.md).  {% endnote %}  В одном запросе может быть максимум 1500 товаров.  |**⚙️ Лимит:** 1000 запросов в минуту| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BidsApi()
val businessId : kotlin.Long = 789 // kotlin.Long | Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val pageToken : kotlin.String = eyBuZXh0SWQ6IDIzNDIgfQ== // kotlin.String | Идентификатор страницы c результатами.  Если параметр не указан, возвращается первая страница.  Рекомендуется передавать значение выходного параметра `nextPageToken`, полученное при последнем запросе.  Если задан `page_token` и в запросе есть параметры `offset`, `page_number` и `page_size`, они игнорируются. 
val limit : kotlin.Int = 20 // kotlin.Int | Количество значений на одной странице. 
val getBidsInfoRequest : GetBidsInfoRequest =  // GetBidsInfoRequest | description
try {
    val result : GetBidsInfoResponse = apiInstance.getBidsInfoForBusiness(businessId, pageToken, limit, getBidsInfoRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BidsApi#getBidsInfoForBusiness")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BidsApi#getBidsInfoForBusiness")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | **kotlin.Long**| Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **pageToken** | **kotlin.String**| Идентификатор страницы c результатами.  Если параметр не указан, возвращается первая страница.  Рекомендуется передавать значение выходного параметра &#x60;nextPageToken&#x60;, полученное при последнем запросе.  Если задан &#x60;page_token&#x60; и в запросе есть параметры &#x60;offset&#x60;, &#x60;page_number&#x60; и &#x60;page_size&#x60;, они игнорируются.  | [optional]
 **limit** | **kotlin.Int**| Количество значений на одной странице.  | [optional]
 **getBidsInfoRequest** | [**GetBidsInfoRequest**](GetBidsInfoRequest.md)| description | [optional]

### Return type

[**GetBidsInfoResponse**](GetBidsInfoResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="getBidsRecommendations"></a>
# **getBidsRecommendations**
> GetBidsRecommendationsResponse getBidsRecommendations(businessId, getBidsRecommendationsRequest)

Рекомендованные ставки для заданных товаров

{% include notitle [access](../../_auto/method_scopes/getBidsRecommendations.md) %}  Возвращает рекомендованные ставки для заданных товаров, что обеспечивает вашим предложениям определенную долю показов, и дополнительные инструменты продвижения.  Для одного товара может возвращаться одна рекомендованная ставка или несколько. Во втором случае разные ставки предназначены для достижения разной доли показов и получения дополнительных инструментов продвижения.  Если товар только добавлен в каталог, но пока не продается, рекомендованной ставки для него не будет.  В одном запросе может быть максимум 1500 товаров.  |**⚙️ Лимит:** 1000 запросов в минуту| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BidsApi()
val businessId : kotlin.Long = 789 // kotlin.Long | Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val getBidsRecommendationsRequest : GetBidsRecommendationsRequest =  // GetBidsRecommendationsRequest | description.
try {
    val result : GetBidsRecommendationsResponse = apiInstance.getBidsRecommendations(businessId, getBidsRecommendationsRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BidsApi#getBidsRecommendations")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BidsApi#getBidsRecommendations")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | **kotlin.Long**| Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **getBidsRecommendationsRequest** | [**GetBidsRecommendationsRequest**](GetBidsRecommendationsRequest.md)| description. |

### Return type

[**GetBidsRecommendationsResponse**](GetBidsRecommendationsResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="putBidsForBusiness"></a>
# **putBidsForBusiness**
> EmptyApiResponse putBidsForBusiness(businessId, putSkuBidsRequest)

Включение буста продаж и установка ставок

{% include notitle [access](../../_auto/method_scopes/putBidsForBusiness.md) %}  Запускает буст продаж — создает и включает кампанию, добавляет в нее товары и назначает на них ставки.  {% cut \&quot;Как в кабинете выглядит кампания, созданная через API\&quot; %}  ![](../../_images/api-boost.png)  {% endcut %}  При первом использовании запроса Маркет: создаст единую на все магазины бизнес-аккаунта кампанию, добавит в нее товары с указанными ставками, включит для них ценовую стратегию и запустит продвижение. Повторное использование запроса позволит обновить ставки на товары в этой кампании или добавить новые. Подробнее о ценовой стратегии читайте в [Справке Маркета для продавцов](https://yandex.ru/support/marketplace/marketing/campaigns.html#price-strategy).  Если товара с указанным SKU нет, он будет проигнорирован. Если в будущем в каталоге появится товар с таким SKU, он автоматически будет добавлен в кампанию с указанной ставкой.  Запрос всегда работает с одной и той же созданной через API кампанией. Если в кабинете удалить ее, при следующем выполнении запроса Маркет создаст новую. Другими кампаниями управлять через API не получится. У созданной через API кампании всегда наибольший приоритет над остальными — изменить его нельзя.  Выполнение запроса включает кампанию и ценовую стратегию, если они были отключены.  Внести другие изменения в созданную через API кампанию можно в кабинете:  * выключить или включить кампанию; * изменить ее название; * выключить или включить ценовую стратегию.  Чтобы остановить продвижение отдельных товаров и удалить их из кампании, передайте для них нулевую ставку в параметре &#x60;bid&#x60;.  Подробнее о том, как работает буст продаж, читайте в [Справке Маркета для продавцов](https://yandex.ru/support/marketplace/marketing/campaigns.html).  {% note info \&quot;Как посмотреть расходы на буст продаж?\&quot; %}  Фактические расходы на буст указаны в отчете по заказам ([POST campaigns/{campaignId}/stats/orders](../../reference/stats/getOrdersStats.md)). Сумма содержится в поле &#x60;bidFee&#x60;.  {% endnote %}  В одном запросе может быть максимум 1500 товаров.  |**⚙️ Лимит:** 1000 запросов в минуту| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BidsApi()
val businessId : kotlin.Long = 789 // kotlin.Long | Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val putSkuBidsRequest : PutSkuBidsRequest =  // PutSkuBidsRequest | description
try {
    val result : EmptyApiResponse = apiInstance.putBidsForBusiness(businessId, putSkuBidsRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BidsApi#putBidsForBusiness")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BidsApi#putBidsForBusiness")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | **kotlin.Long**| Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **putSkuBidsRequest** | [**PutSkuBidsRequest**](PutSkuBidsRequest.md)| description |

### Return type

[**EmptyApiResponse**](EmptyApiResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="putBidsForCampaign"></a>
# **putBidsForCampaign**
> EmptyApiResponse putBidsForCampaign(campaignId, putSkuBidsRequest)

Включение буста продаж и установка ставок для магазина

{% include notitle [access](../../_auto/method_scopes/putBidsForCampaign.md) %}  Запускает буст продаж в указанном магазине — создает и включает кампанию, добавляет в нее товары и назначает на них ставки.  При первом использовании запроса Маркет: создаст кампанию, добавит в нее товары с указанными ставками для заданного магазина, включит для них ценовую стратегию и запустит продвижение. Повторное использование запроса позволит обновить ставки на товары в этой кампании или добавить новые. Подробнее о ценовой стратегии читайте в [Справке Маркета для продавцов](https://yandex.ru/support/marketplace/marketing/campaigns.html#price-strategy).  Если товара с указанным SKU нет, он будет проигнорирован. Если в будущем в каталоге появится товар с таким SKU, он автоматически будет добавлен в кампанию с указанной ставкой.  Запрос всегда работает с одной и той же кампанией, созданной через этот запрос или [PUT businesses/{businessId}/bids](/reference/bids/putBidsForBusiness). Если в кабинете удалить ее, при следующем выполнении запроса Маркет создаст новую. У созданной через API кампании всегда наибольший приоритет над остальными — изменить его нельзя.  Выполнение запроса включает кампанию и ценовую стратегию, если они были отключены.  Внести другие изменения в созданную через API кампанию можно в кабинете:  * выключить или включить кампанию; * изменить ее название; * выключить или включить ценовую стратегию.  Чтобы остановить продвижение отдельных товаров и удалить их из кампании, передайте для них нулевую ставку в параметре &#x60;bid&#x60;.  Подробнее о том, как работает буст продаж, читайте в [Справке Маркета для продавцов](https://yandex.ru/support/marketplace/marketing/campaigns.html).  {% note info \&quot;Как посмотреть расходы на буст продаж?\&quot; %}  Фактические расходы на буст указаны в отчете по заказам ([POST campaigns/{campaignId}/stats/orders](../../reference/stats/getOrdersStats.md)). Сумма содержится в поле &#x60;bidFee&#x60;.  {% endnote %}  В одном запросе может быть максимум 1500 товаров.  |**⚙️ Лимит:** 1000 запросов в минуту| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BidsApi()
val campaignId : kotlin.Long = 789 // kotlin.Long | Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val putSkuBidsRequest : PutSkuBidsRequest =  // PutSkuBidsRequest | description
try {
    val result : EmptyApiResponse = apiInstance.putBidsForCampaign(campaignId, putSkuBidsRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BidsApi#putBidsForCampaign")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BidsApi#putBidsForCampaign")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaignId** | **kotlin.Long**| Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **putSkuBidsRequest** | [**PutSkuBidsRequest**](PutSkuBidsRequest.md)| description |

### Return type

[**EmptyApiResponse**](EmptyApiResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

