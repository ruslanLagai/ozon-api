# FeedCategoriesApi

All URIs are relative to *https://api.partner.market.yandex.ru*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getCampaignFeedCategories**](FeedCategoriesApi.md#getCampaignFeedCategories) | **GET** /campaigns/{campaignId}/feeds/categories | Категории магазина
[**getFeedCategories**](FeedCategoriesApi.md#getFeedCategories) | **GET** /campaigns/{campaignId}/feeds/{feedId}/categories | Категории прайс-листа


<a id="getCampaignFeedCategories"></a>
# **getCampaignFeedCategories**
> GetCampaignCategoriesResponse getCampaignFeedCategories(campaignId, page, pageSize)

Категории магазина

{% note warning \&quot;\&quot; %}  Этот метод устарел. Не используйте его.  {% endnote %}  Возвращает список категорий предложений для магазина по всем прайс-листам этого магазина, размещенным на Маркете. Информация о категориях для отключенных прайс-листов не предоставляется.  В ответе на запрос для каждой категории указывается название, ее идентификатор и идентификатор родительской категории. Список сортируется сначала по возрастанию идентификатора прайс-листа, а затем по возрастанию идентификатора категории. Если категорий много, результаты выдаются постранично.  Для методов &#x60;GET campaigns/{campaignId}/feeds/categories&#x60; и &#x60;GET campaigns/{campaignId}/feeds/{feedId}/categories&#x60; действует групповое ресурсное ограничение. Ограничение вводится на суммарное количество категорий, информация о которых запрошена при помощи этих методов.  |**⚙️ Лимит:** [не более 50 000 запросов в сутки](*rule)| |-|  [//]: &lt;&gt; (rule: Лимит рассчитывается индивидуально и зависит от количества категорий.)   {% note info %}  Количество категорий берется за предыдущий день.  {% endnote %} 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = FeedCategoriesApi()
val campaignId : kotlin.Long = 789 // kotlin.Long | Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val page : kotlin.Int = 56 // kotlin.Int | Номер страницы результатов.  Значение по умолчанию: 1.  Используется вместе с параметром `page_size`.  `page_number` игнорируется, если задан `page_token`, `limit` или `offset`. 
val pageSize : kotlin.Int = 56 // kotlin.Int | Размер страницы.  Используется вместе с параметром `page_number`.  `page_size` игнорируется, если задан `page_token`, `limit` или `offset`. 
try {
    val result : GetCampaignCategoriesResponse = apiInstance.getCampaignFeedCategories(campaignId, page, pageSize)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling FeedCategoriesApi#getCampaignFeedCategories")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling FeedCategoriesApi#getCampaignFeedCategories")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaignId** | **kotlin.Long**| Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **page** | **kotlin.Int**| Номер страницы результатов.  Значение по умолчанию: 1.  Используется вместе с параметром &#x60;page_size&#x60;.  &#x60;page_number&#x60; игнорируется, если задан &#x60;page_token&#x60;, &#x60;limit&#x60; или &#x60;offset&#x60;.  | [optional] [default to 1]
 **pageSize** | **kotlin.Int**| Размер страницы.  Используется вместе с параметром &#x60;page_number&#x60;.  &#x60;page_size&#x60; игнорируется, если задан &#x60;page_token&#x60;, &#x60;limit&#x60; или &#x60;offset&#x60;.  | [optional]

### Return type

[**GetCampaignCategoriesResponse**](GetCampaignCategoriesResponse.md)

### Authorization


Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getFeedCategories"></a>
# **getFeedCategories**
> GetFeedCategoriesResponse getFeedCategories(campaignId, feedId, page, pageSize)

Категории прайс-листа

{% note warning \&quot;\&quot; %}  Этот метод устарел. Не используйте его.  {% endnote %}  Возвращает список категорий предложений из прайс-листа, размещенного на Маркете для заданного магазина. Информация о категориях для отключенных прайс-листов не предоставляется.  В ответе на запрос для каждой категории возвращается ее название, идентификатор и идентификатор родительской категории. Список сортируется по возрастанию идентификатора категории. Если категорий много, результаты выдаются постранично.  Для методов &#x60;GET campaigns/{campaignId}/feeds/categories&#x60; и &#x60;GET campaigns/{campaignId}/feeds/{feedId}/categories&#x60; действует групповое ресурсное ограничение. Ограничение вводится на суммарное количество категорий, информация о которых запрошена при помощи этих методов.  |**⚙️ Лимит:** [не более 50 000 запросов в сутки](*rule)| |-|  [//]: &lt;&gt; (rule: Лимит рассчитывается индивидуально и зависит от количества категорий.)  {% note info %}  Количество категорий берется за предыдущий день.  {% endnote %} 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = FeedCategoriesApi()
val campaignId : kotlin.Long = 789 // kotlin.Long | Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val feedId : kotlin.Long = 789 // kotlin.Long | Идентификатор прайс-листа.
val page : kotlin.Int = 56 // kotlin.Int | Номер страницы результатов.  Значение по умолчанию: 1.  Используется вместе с параметром `page_size`.  `page_number` игнорируется, если задан `page_token`, `limit` или `offset`. 
val pageSize : kotlin.Int = 56 // kotlin.Int | Размер страницы.  Используется вместе с параметром `page_number`.  `page_size` игнорируется, если задан `page_token`, `limit` или `offset`. 
try {
    val result : GetFeedCategoriesResponse = apiInstance.getFeedCategories(campaignId, feedId, page, pageSize)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling FeedCategoriesApi#getFeedCategories")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling FeedCategoriesApi#getFeedCategories")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaignId** | **kotlin.Long**| Идентификатор кампании в API и магазина в кабинете. Каждая кампания в API соответствует магазину в кабинете.  Чтобы узнать идентификаторы своих магазинов, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **feedId** | **kotlin.Long**| Идентификатор прайс-листа. |
 **page** | **kotlin.Int**| Номер страницы результатов.  Значение по умолчанию: 1.  Используется вместе с параметром &#x60;page_size&#x60;.  &#x60;page_number&#x60; игнорируется, если задан &#x60;page_token&#x60;, &#x60;limit&#x60; или &#x60;offset&#x60;.  | [optional] [default to 1]
 **pageSize** | **kotlin.Int**| Размер страницы.  Используется вместе с параметром &#x60;page_number&#x60;.  &#x60;page_size&#x60; игнорируется, если задан &#x60;page_token&#x60;, &#x60;limit&#x60; или &#x60;offset&#x60;.  | [optional]

### Return type

[**GetFeedCategoriesResponse**](GetFeedCategoriesResponse.md)

### Authorization


Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

