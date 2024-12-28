# ModelsApi

All URIs are relative to *https://api.partner.market.yandex.ru*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getModel**](ModelsApi.md#getModel) | **GET** /models/{modelId} | Информация об одной модели
[**getModelOffers**](ModelsApi.md#getModelOffers) | **GET** /models/{modelId}/offers | Список предложений для одной модели
[**getModels**](ModelsApi.md#getModels) | **POST** /models | Информация о нескольких моделях
[**getModelsOffers**](ModelsApi.md#getModelsOffers) | **POST** /models/offers | Список предложений для нескольких моделей
[**searchModels**](ModelsApi.md#searchModels) | **GET** /models | Поиск модели товара


<a id="getModel"></a>
# **getModel**
> GetModelsResponse getModel(modelId, regionId, currency)

Информация об одной модели

{% include notitle [access](../../_auto/method_scopes/getModel.md) %}  Возвращает информацию о модели товара.  Для методов &#x60;GET models&#x60;, &#x60;GET models/{modelId}&#x60; и &#x60;POST models&#x60; действует групповое ресурсное ограничение. Ограничение вводится на суммарное количество моделей, информация о которых запрошена при помощи этих методов.  |**⚙️ Лимит:** 100 000 моделей в час| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ModelsApi()
val modelId : kotlin.Long = 789 // kotlin.Long | Идентификатор модели товара.
val regionId : kotlin.Long = 789 // kotlin.Long | Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md). 
val currency : CurrencyType =  // CurrencyType | Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * `BYN` — белорусский рубль.  * `KZT` — казахстанский тенге.  * `RUR` — российский рубль.  * `UAH` — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина). 
try {
    val result : GetModelsResponse = apiInstance.getModel(modelId, regionId, currency)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ModelsApi#getModel")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ModelsApi#getModel")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **modelId** | **kotlin.Long**| Идентификатор модели товара. |
 **regionId** | **kotlin.Long**| Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md).  |
 **currency** | [**CurrencyType**](.md)| Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * &#x60;BYN&#x60; — белорусский рубль.  * &#x60;KZT&#x60; — казахстанский тенге.  * &#x60;RUR&#x60; — российский рубль.  * &#x60;UAH&#x60; — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина).  | [optional] [enum: RUR, USD, EUR, UAH, AUD, GBP, BYR, BYN, DKK, ISK, KZT, CAD, CNY, NOK, XDR, SGD, TRY, SEK, CHF, JPY, AZN, ALL, DZD, AOA, ARS, AMD, AFN, BHD, BGN, BOB, BWP, BND, BRL, BIF, HUF, VEF, KPW, VND, GMD, GHS, GNF, HKD, GEL, AED, EGP, ZMK, ILS, INR, IDR, JOD, IQD, IRR, YER, QAR, KES, KGS, COP, CDF, CRC, KWD, CUP, LAK, LVL, SLL, LBP, LYD, SZL, LTL, MUR, MRO, MKD, MWK, MGA, MYR, MAD, MXN, MZN, MDL, MNT, NPR, NGN, NIO, NZD, OMR, PKR, PYG, PEN, PLN, KHR, SAR, RON, SCR, SYP, SKK, SOS, SDG, SRD, TJS, THB, TWD, BDT, TZS, TND, TMM, UGX, UZS, UYU, PHP, DJF, XAF, XOF, HRK, CZK, CLP, LKR, EEK, ETB, RSD, ZAR, KRW, NAD, TL, UE]

### Return type

[**GetModelsResponse**](GetModelsResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getModelOffers"></a>
# **getModelOffers**
> GetModelsOffersResponse getModelOffers(modelId, regionId, currency, orderByPrice, count, page)

Список предложений для одной модели

{% include notitle [access](../../_auto/method_scopes/getModelOffers.md) %}  Возвращает информацию о первых десяти предложениях, расположенных на карточке модели.  Предложения выдаются для определенного региона и располагаются в том же порядке, в котором они показываются в выдаче Маркета на карточке модели.  Для групповых моделей метод не поддерживается. Идентификатор групповой модели игнорируется.  Для методов &#x60;GET models/{modelId}/offers&#x60; и &#x60;POST models/offers&#x60; действует групповое ресурсное ограничение. Ограничение вводится на суммарное количество моделей, информация о которых запрошена при помощи этих методов.  |**⚙️ Лимит:** 100 000 предложений в час| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ModelsApi()
val modelId : kotlin.Long = 789 // kotlin.Long | Идентификатор модели товара.
val regionId : kotlin.Long = 789 // kotlin.Long | Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md). 
val currency : CurrencyType =  // CurrencyType | Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * `BYN` — белорусский рубль.  * `KZT` — казахстанский тенге.  * `RUR` — российский рубль.  * `UAH` — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина). 
val orderByPrice : SortOrderType =  // SortOrderType | Направление сортировки по цене.  Возможные значения: * `ASC` — сортировка по возрастанию. * `DESC` — сортировка по убыванию.  Значение по умолчанию: предложения выводятся в произвольном порядке. 
val count : kotlin.Int = 56 // kotlin.Int | Количество предложений на странице ответа.
val page : kotlin.Int = 56 // kotlin.Int | {% note warning \"Если в методе есть `page_token`\" %}  Используйте его вместо параметра `page`.  [Подробнее о типах пагинации и их использовании](../../concepts/pagination.md)  {% endnote %}  Номер страницы результатов.  Используется вместе с параметром `page_size`.  `page_number` игнорируется, если задан `page_token`, `limit` или `offset`. 
try {
    val result : GetModelsOffersResponse = apiInstance.getModelOffers(modelId, regionId, currency, orderByPrice, count, page)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ModelsApi#getModelOffers")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ModelsApi#getModelOffers")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **modelId** | **kotlin.Long**| Идентификатор модели товара. |
 **regionId** | **kotlin.Long**| Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md).  |
 **currency** | [**CurrencyType**](.md)| Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * &#x60;BYN&#x60; — белорусский рубль.  * &#x60;KZT&#x60; — казахстанский тенге.  * &#x60;RUR&#x60; — российский рубль.  * &#x60;UAH&#x60; — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина).  | [optional] [enum: RUR, USD, EUR, UAH, AUD, GBP, BYR, BYN, DKK, ISK, KZT, CAD, CNY, NOK, XDR, SGD, TRY, SEK, CHF, JPY, AZN, ALL, DZD, AOA, ARS, AMD, AFN, BHD, BGN, BOB, BWP, BND, BRL, BIF, HUF, VEF, KPW, VND, GMD, GHS, GNF, HKD, GEL, AED, EGP, ZMK, ILS, INR, IDR, JOD, IQD, IRR, YER, QAR, KES, KGS, COP, CDF, CRC, KWD, CUP, LAK, LVL, SLL, LBP, LYD, SZL, LTL, MUR, MRO, MKD, MWK, MGA, MYR, MAD, MXN, MZN, MDL, MNT, NPR, NGN, NIO, NZD, OMR, PKR, PYG, PEN, PLN, KHR, SAR, RON, SCR, SYP, SKK, SOS, SDG, SRD, TJS, THB, TWD, BDT, TZS, TND, TMM, UGX, UZS, UYU, PHP, DJF, XAF, XOF, HRK, CZK, CLP, LKR, EEK, ETB, RSD, ZAR, KRW, NAD, TL, UE]
 **orderByPrice** | [**SortOrderType**](.md)| Направление сортировки по цене.  Возможные значения: * &#x60;ASC&#x60; — сортировка по возрастанию. * &#x60;DESC&#x60; — сортировка по убыванию.  Значение по умолчанию: предложения выводятся в произвольном порядке.  | [optional] [enum: ASC, DESC]
 **count** | **kotlin.Int**| Количество предложений на странице ответа. | [optional] [default to 10]
 **page** | **kotlin.Int**| {% note warning \&quot;Если в методе есть &#x60;page_token&#x60;\&quot; %}  Используйте его вместо параметра &#x60;page&#x60;.  [Подробнее о типах пагинации и их использовании](../../concepts/pagination.md)  {% endnote %}  Номер страницы результатов.  Используется вместе с параметром &#x60;page_size&#x60;.  &#x60;page_number&#x60; игнорируется, если задан &#x60;page_token&#x60;, &#x60;limit&#x60; или &#x60;offset&#x60;.  | [optional] [default to 1]

### Return type

[**GetModelsOffersResponse**](GetModelsOffersResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getModels"></a>
# **getModels**
> GetModelsResponse getModels(regionId, getModelsRequest, currency)

Информация о нескольких моделях

{% include notitle [access](../../_auto/method_scopes/getModels.md) %}  Возвращает информацию о моделях товаров.  В одном запросе можно получить информацию не более чем о 100 моделях.  Для методов &#x60;GET models&#x60;, &#x60;GET models/{modelId}&#x60; и &#x60;POST models&#x60; действует групповое ресурсное ограничение. Ограничение вводится на суммарное количество моделей, информация о которых запрошена при помощи этих методов.  |**⚙️ Лимит:** 100 000 моделей в час| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ModelsApi()
val regionId : kotlin.Long = 789 // kotlin.Long | Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md). 
val getModelsRequest : GetModelsRequest =  // GetModelsRequest | 
val currency : CurrencyType =  // CurrencyType | Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * `BYN` — белорусский рубль.  * `KZT` — казахстанский тенге.  * `RUR` — российский рубль.  * `UAH` — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина). 
try {
    val result : GetModelsResponse = apiInstance.getModels(regionId, getModelsRequest, currency)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ModelsApi#getModels")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ModelsApi#getModels")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **regionId** | **kotlin.Long**| Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md).  |
 **getModelsRequest** | [**GetModelsRequest**](GetModelsRequest.md)|  |
 **currency** | [**CurrencyType**](.md)| Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * &#x60;BYN&#x60; — белорусский рубль.  * &#x60;KZT&#x60; — казахстанский тенге.  * &#x60;RUR&#x60; — российский рубль.  * &#x60;UAH&#x60; — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина).  | [optional] [enum: RUR, USD, EUR, UAH, AUD, GBP, BYR, BYN, DKK, ISK, KZT, CAD, CNY, NOK, XDR, SGD, TRY, SEK, CHF, JPY, AZN, ALL, DZD, AOA, ARS, AMD, AFN, BHD, BGN, BOB, BWP, BND, BRL, BIF, HUF, VEF, KPW, VND, GMD, GHS, GNF, HKD, GEL, AED, EGP, ZMK, ILS, INR, IDR, JOD, IQD, IRR, YER, QAR, KES, KGS, COP, CDF, CRC, KWD, CUP, LAK, LVL, SLL, LBP, LYD, SZL, LTL, MUR, MRO, MKD, MWK, MGA, MYR, MAD, MXN, MZN, MDL, MNT, NPR, NGN, NIO, NZD, OMR, PKR, PYG, PEN, PLN, KHR, SAR, RON, SCR, SYP, SKK, SOS, SDG, SRD, TJS, THB, TWD, BDT, TZS, TND, TMM, UGX, UZS, UYU, PHP, DJF, XAF, XOF, HRK, CZK, CLP, LKR, EEK, ETB, RSD, ZAR, KRW, NAD, TL, UE]

### Return type

[**GetModelsResponse**](GetModelsResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="getModelsOffers"></a>
# **getModelsOffers**
> GetModelsOffersResponse getModelsOffers(regionId, getModelsRequest, currency, orderByPrice)

Список предложений для нескольких моделей

{% include notitle [access](../../_auto/method_scopes/getModelsOffers.md) %}  Возвращает информацию о первых десяти предложениях, расположенных на карточках моделей, идентификаторы которых указаны в запросе.  Предложения выдаются для определенного региона и располагаются в том же порядке, в котором они показываются в выдаче Маркета на карточке модели.  Для групповых моделей выдача предложений не поддерживается. Идентификаторы групповых моделей игнорируются.  В одном запросе можно получить информацию о предложениях не более чем для 100 моделей.  Для методов &#x60;GET models/{modelId}/offers&#x60; и &#x60;POST models/offers&#x60; действует групповое ресурсное ограничение. Ограничение вводится на суммарное количество моделей, информация о которых запрошена при помощи этих методов.  |**⚙️ Лимит:** 100 000 предложений в час| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ModelsApi()
val regionId : kotlin.Long = 789 // kotlin.Long | Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md). 
val getModelsRequest : GetModelsRequest =  // GetModelsRequest | 
val currency : CurrencyType =  // CurrencyType | Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * `BYN` — белорусский рубль.  * `KZT` — казахстанский тенге.  * `RUR` — российский рубль.  * `UAH` — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина). 
val orderByPrice : SortOrderType =  // SortOrderType | Направление сортировки по цене.  Возможные значения: * `ASC` — сортировка по возрастанию. * `DESC` — сортировка по убыванию.  Значение по умолчанию: предложения выводятся в произвольном порядке. 
try {
    val result : GetModelsOffersResponse = apiInstance.getModelsOffers(regionId, getModelsRequest, currency, orderByPrice)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ModelsApi#getModelsOffers")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ModelsApi#getModelsOffers")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **regionId** | **kotlin.Long**| Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md).  |
 **getModelsRequest** | [**GetModelsRequest**](GetModelsRequest.md)|  |
 **currency** | [**CurrencyType**](.md)| Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * &#x60;BYN&#x60; — белорусский рубль.  * &#x60;KZT&#x60; — казахстанский тенге.  * &#x60;RUR&#x60; — российский рубль.  * &#x60;UAH&#x60; — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина).  | [optional] [enum: RUR, USD, EUR, UAH, AUD, GBP, BYR, BYN, DKK, ISK, KZT, CAD, CNY, NOK, XDR, SGD, TRY, SEK, CHF, JPY, AZN, ALL, DZD, AOA, ARS, AMD, AFN, BHD, BGN, BOB, BWP, BND, BRL, BIF, HUF, VEF, KPW, VND, GMD, GHS, GNF, HKD, GEL, AED, EGP, ZMK, ILS, INR, IDR, JOD, IQD, IRR, YER, QAR, KES, KGS, COP, CDF, CRC, KWD, CUP, LAK, LVL, SLL, LBP, LYD, SZL, LTL, MUR, MRO, MKD, MWK, MGA, MYR, MAD, MXN, MZN, MDL, MNT, NPR, NGN, NIO, NZD, OMR, PKR, PYG, PEN, PLN, KHR, SAR, RON, SCR, SYP, SKK, SOS, SDG, SRD, TJS, THB, TWD, BDT, TZS, TND, TMM, UGX, UZS, UYU, PHP, DJF, XAF, XOF, HRK, CZK, CLP, LKR, EEK, ETB, RSD, ZAR, KRW, NAD, TL, UE]
 **orderByPrice** | [**SortOrderType**](.md)| Направление сортировки по цене.  Возможные значения: * &#x60;ASC&#x60; — сортировка по возрастанию. * &#x60;DESC&#x60; — сортировка по убыванию.  Значение по умолчанию: предложения выводятся в произвольном порядке.  | [optional] [enum: ASC, DESC]

### Return type

[**GetModelsOffersResponse**](GetModelsOffersResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="searchModels"></a>
# **searchModels**
> SearchModelsResponse searchModels(query, regionId, currency, page, pageSize)

Поиск модели товара

{% include notitle [access](../../_auto/method_scopes/searchModels.md) %}  Возвращает информацию о моделях, удовлетворяющих заданным в запросе условиям поиска.  В одном запросе можно получить информацию не более чем о 100 моделях.  Для методов &#x60;GET models&#x60;, &#x60;GET models/{modelId}&#x60; и &#x60;POST models&#x60; действует групповое ресурсное ограничение. Ограничение вводится на суммарное количество моделей, информация о которых запрошена при помощи этих методов.  |**⚙️ Лимит:** 100 000 моделей в час| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ModelsApi()
val query : kotlin.String = query_example // kotlin.String | Поисковый запрос по названию модели товара.
val regionId : kotlin.Long = 789 // kotlin.Long | Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md). 
val currency : CurrencyType =  // CurrencyType | Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * `BYN` — белорусский рубль.  * `KZT` — казахстанский тенге.  * `RUR` — российский рубль.  * `UAH` — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина). 
val page : kotlin.Int = 56 // kotlin.Int | {% note warning \"Если в методе есть `page_token`\" %}  Используйте его вместо параметра `page`.  [Подробнее о типах пагинации и их использовании](../../concepts/pagination.md)  {% endnote %}  Номер страницы результатов.  Используется вместе с параметром `page_size`.  `page_number` игнорируется, если задан `page_token`, `limit` или `offset`. 
val pageSize : kotlin.Int = 56 // kotlin.Int | Размер страницы.  Используется вместе с параметром `page_number`.  `page_size` игнорируется, если задан `page_token`, `limit` или `offset`. 
try {
    val result : SearchModelsResponse = apiInstance.searchModels(query, regionId, currency, page, pageSize)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ModelsApi#searchModels")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ModelsApi#searchModels")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **query** | **kotlin.String**| Поисковый запрос по названию модели товара. |
 **regionId** | **kotlin.Long**| Идентификатор региона.  Идентификатор региона можно получить c помощью запроса [GET regions](../../reference/regions/searchRegionsByName.md).  |
 **currency** | [**CurrencyType**](.md)| Валюта, в которой отображаются цены предложений на страницах с результатами поиска.  Возможные значения:  * &#x60;BYN&#x60; — белорусский рубль.  * &#x60;KZT&#x60; — казахстанский тенге.  * &#x60;RUR&#x60; — российский рубль.  * &#x60;UAH&#x60; — украинская гривна.  Значение по умолчанию: используется национальная валюта магазина (национальная валюта страны происхождения магазина).  | [optional] [enum: RUR, USD, EUR, UAH, AUD, GBP, BYR, BYN, DKK, ISK, KZT, CAD, CNY, NOK, XDR, SGD, TRY, SEK, CHF, JPY, AZN, ALL, DZD, AOA, ARS, AMD, AFN, BHD, BGN, BOB, BWP, BND, BRL, BIF, HUF, VEF, KPW, VND, GMD, GHS, GNF, HKD, GEL, AED, EGP, ZMK, ILS, INR, IDR, JOD, IQD, IRR, YER, QAR, KES, KGS, COP, CDF, CRC, KWD, CUP, LAK, LVL, SLL, LBP, LYD, SZL, LTL, MUR, MRO, MKD, MWK, MGA, MYR, MAD, MXN, MZN, MDL, MNT, NPR, NGN, NIO, NZD, OMR, PKR, PYG, PEN, PLN, KHR, SAR, RON, SCR, SYP, SKK, SOS, SDG, SRD, TJS, THB, TWD, BDT, TZS, TND, TMM, UGX, UZS, UYU, PHP, DJF, XAF, XOF, HRK, CZK, CLP, LKR, EEK, ETB, RSD, ZAR, KRW, NAD, TL, UE]
 **page** | **kotlin.Int**| {% note warning \&quot;Если в методе есть &#x60;page_token&#x60;\&quot; %}  Используйте его вместо параметра &#x60;page&#x60;.  [Подробнее о типах пагинации и их использовании](../../concepts/pagination.md)  {% endnote %}  Номер страницы результатов.  Используется вместе с параметром &#x60;page_size&#x60;.  &#x60;page_number&#x60; игнорируется, если задан &#x60;page_token&#x60;, &#x60;limit&#x60; или &#x60;offset&#x60;.  | [optional] [default to 1]
 **pageSize** | **kotlin.Int**| Размер страницы.  Используется вместе с параметром &#x60;page_number&#x60;.  &#x60;page_size&#x60; игнорируется, если задан &#x60;page_token&#x60;, &#x60;limit&#x60; или &#x60;offset&#x60;.  | [optional]

### Return type

[**SearchModelsResponse**](SearchModelsResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

