# BusinessOfferMappingsApi

All URIs are relative to *https://api.partner.market.yandex.ru*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addOffersToArchive**](BusinessOfferMappingsApi.md#addOffersToArchive) | **POST** /businesses/{businessId}/offer-mappings/archive | Добавление товаров в архив
[**deleteOffers**](BusinessOfferMappingsApi.md#deleteOffers) | **POST** /businesses/{businessId}/offer-mappings/delete | Удаление товаров из каталога
[**deleteOffersFromArchive**](BusinessOfferMappingsApi.md#deleteOffersFromArchive) | **POST** /businesses/{businessId}/offer-mappings/unarchive | Удаление товаров из архива
[**getOfferMappings**](BusinessOfferMappingsApi.md#getOfferMappings) | **POST** /businesses/{businessId}/offer-mappings | Информация о товарах в каталоге
[**getSuggestedOfferMappings**](BusinessOfferMappingsApi.md#getSuggestedOfferMappings) | **POST** /businesses/{businessId}/offer-mappings/suggestions | Просмотр карточек на Маркете, которые подходят вашим товарам
[**updateOfferMappings**](BusinessOfferMappingsApi.md#updateOfferMappings) | **POST** /businesses/{businessId}/offer-mappings/update | Добавление товаров в каталог и изменение информации о них


<a id="addOffersToArchive"></a>
# **addOffersToArchive**
> AddOffersToArchiveResponse addOffersToArchive(businessId, addOffersToArchiveRequest)

Добавление товаров в архив

{% include notitle [access](../../_auto/method_scopes/addOffersToArchive.md) %}  Помещает товары в архив. Товары, помещенные в архив, скрыты с витрины во всех магазинах кабинета.  {% note warning \&quot;В архив нельзя отправить товар, который хранится на складе Маркета\&quot; %}  Вначале такой товар нужно распродать или вывезти.  {% endnote %}  |**⚙️ Лимит:** 5000 товаров в минуту, не более 200 товаров в одном запросе| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BusinessOfferMappingsApi()
val businessId : kotlin.Long = 789 // kotlin.Long | Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val addOffersToArchiveRequest : AddOffersToArchiveRequest =  // AddOffersToArchiveRequest | 
try {
    val result : AddOffersToArchiveResponse = apiInstance.addOffersToArchive(businessId, addOffersToArchiveRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BusinessOfferMappingsApi#addOffersToArchive")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BusinessOfferMappingsApi#addOffersToArchive")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | **kotlin.Long**| Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **addOffersToArchiveRequest** | [**AddOffersToArchiveRequest**](AddOffersToArchiveRequest.md)|  |

### Return type

[**AddOffersToArchiveResponse**](AddOffersToArchiveResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="deleteOffers"></a>
# **deleteOffers**
> DeleteOffersResponse deleteOffers(businessId, deleteOffersRequest)

Удаление товаров из каталога

{% include notitle [access](../../_auto/method_scopes/deleteOffers.md) %}  Удаляет товары из каталога.  |**⚙️ Лимит:** 5000 товаров в минуту, не более 200 товаров в одном запросе| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BusinessOfferMappingsApi()
val businessId : kotlin.Long = 789 // kotlin.Long | Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val deleteOffersRequest : DeleteOffersRequest =  // DeleteOffersRequest | 
try {
    val result : DeleteOffersResponse = apiInstance.deleteOffers(businessId, deleteOffersRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BusinessOfferMappingsApi#deleteOffers")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BusinessOfferMappingsApi#deleteOffers")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | **kotlin.Long**| Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **deleteOffersRequest** | [**DeleteOffersRequest**](DeleteOffersRequest.md)|  |

### Return type

[**DeleteOffersResponse**](DeleteOffersResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="deleteOffersFromArchive"></a>
# **deleteOffersFromArchive**
> DeleteOffersFromArchiveResponse deleteOffersFromArchive(businessId, deleteOffersFromArchiveRequest)

Удаление товаров из архива

{% include notitle [access](../../_auto/method_scopes/deleteOffersFromArchive.md) %}  Восстанавливает товары из архива.  |**⚙️ Лимит:** 5000 товаров в минуту, не более 200 товаров в одном запросе| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BusinessOfferMappingsApi()
val businessId : kotlin.Long = 789 // kotlin.Long | Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val deleteOffersFromArchiveRequest : DeleteOffersFromArchiveRequest =  // DeleteOffersFromArchiveRequest | 
try {
    val result : DeleteOffersFromArchiveResponse = apiInstance.deleteOffersFromArchive(businessId, deleteOffersFromArchiveRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BusinessOfferMappingsApi#deleteOffersFromArchive")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BusinessOfferMappingsApi#deleteOffersFromArchive")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | **kotlin.Long**| Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **deleteOffersFromArchiveRequest** | [**DeleteOffersFromArchiveRequest**](DeleteOffersFromArchiveRequest.md)|  |

### Return type

[**DeleteOffersFromArchiveResponse**](DeleteOffersFromArchiveResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="getOfferMappings"></a>
# **getOfferMappings**
> GetOfferMappingsResponse getOfferMappings(businessId, pageToken, limit, language, getOfferMappingsRequest)

Информация о товарах в каталоге

{% include notitle [access](../../_auto/method_scopes/getOfferMappings.md) %}  Возвращает список товаров в каталоге, их категории на Маркете и характеристики каждого товара.  Можно использовать тремя способами: * задать список интересующих SKU; * задать фильтр — в этом случае результаты возвращаются постранично; * не передавать тело запроса, чтобы получить список всех товаров в каталоге.  |**⚙️ Лимит:** 600 запросов в минуту, не более 200 товаров в одном запросе| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BusinessOfferMappingsApi()
val businessId : kotlin.Long = 789 // kotlin.Long | Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val pageToken : kotlin.String = eyBuZXh0SWQ6IDIzNDIgfQ== // kotlin.String | Идентификатор страницы c результатами.  Если параметр не указан, возвращается первая страница.  Рекомендуется передавать значение выходного параметра `nextPageToken`, полученное при последнем запросе.  Если задан `page_token` и в запросе есть параметры `offset`, `page_number` и `page_size`, они игнорируются. 
val limit : kotlin.Int = 20 // kotlin.Int | Количество значений на одной странице. 
val language : CatalogLanguageType =  // CatalogLanguageType | Язык, на котором принимаются и возвращаются значения в параметрах `name` и `description`.  Значение по умолчанию: `RU`. 
val getOfferMappingsRequest : GetOfferMappingsRequest =  // GetOfferMappingsRequest | 
try {
    val result : GetOfferMappingsResponse = apiInstance.getOfferMappings(businessId, pageToken, limit, language, getOfferMappingsRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BusinessOfferMappingsApi#getOfferMappings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BusinessOfferMappingsApi#getOfferMappings")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | **kotlin.Long**| Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **pageToken** | **kotlin.String**| Идентификатор страницы c результатами.  Если параметр не указан, возвращается первая страница.  Рекомендуется передавать значение выходного параметра &#x60;nextPageToken&#x60;, полученное при последнем запросе.  Если задан &#x60;page_token&#x60; и в запросе есть параметры &#x60;offset&#x60;, &#x60;page_number&#x60; и &#x60;page_size&#x60;, они игнорируются.  | [optional]
 **limit** | **kotlin.Int**| Количество значений на одной странице.  | [optional]
 **language** | [**CatalogLanguageType**](.md)| Язык, на котором принимаются и возвращаются значения в параметрах &#x60;name&#x60; и &#x60;description&#x60;.  Значение по умолчанию: &#x60;RU&#x60;.  | [optional] [enum: RU, UZ]
 **getOfferMappingsRequest** | [**GetOfferMappingsRequest**](GetOfferMappingsRequest.md)|  | [optional]

### Return type

[**GetOfferMappingsResponse**](GetOfferMappingsResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="getSuggestedOfferMappings"></a>
# **getSuggestedOfferMappings**
> GetSuggestedOfferMappingsResponse getSuggestedOfferMappings(businessId, getSuggestedOfferMappingsRequest)

Просмотр карточек на Маркете, которые подходят вашим товарам

{% include notitle [access](../../_auto/method_scopes/getSuggestedOfferMappings.md) %}  Возвращает идентификаторы карточек на Маркете, которые соответствуют товарам с заданными параметрами.  Пользоваться этим запросом необязательно: он просто помогает заранее убедиться, что Маркет верно определяет карточки по предоставленным вами данным.  **Как пользоваться запросом**  1. Передайте Маркету список товаров, которые нужно проверить. 2. В ответ вы получите список SKU на Маркете с расшифровкой: названием, идентификатором модели, категорией. 3. Если расшифровки мало, вы можете открыть карточку. Для этого перейдите по ссылке вида &#x60;https://market.yandex.ru/product/&lt;marketModelId&gt;?sku&#x3D;&lt;marketSku&gt;&#x60;. 4. Если карточка соответствует товару, значит его можно добавлять в каталог с теми данными, что вы указали. Если карточка определилась неправильно — проверьте данные о товаре. Возможно, их нужно уточнить или дополнить. Кроме того, на этапе добавления товара вы можете указать &#x60;marketSKU&#x60;, который ему подходит по вашему мнению.  {% note info \&quot;Как определить &#x60;marketSku&#x60; товара, найденного на Маркете?\&quot; %}  Он есть в адресе страницы товара — расположен после &#x60;sku&#x3D;&#x60;.  Например, &#x60;https://market.yandex.ru/product--yandex-kniga/484830016?sku&#x3D;484830016&#x60;  {% endnote %}  |**⚙️ Лимит:** 100 000 товаров в час| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BusinessOfferMappingsApi()
val businessId : kotlin.Long = 789 // kotlin.Long | Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val getSuggestedOfferMappingsRequest : GetSuggestedOfferMappingsRequest =  // GetSuggestedOfferMappingsRequest | 
try {
    val result : GetSuggestedOfferMappingsResponse = apiInstance.getSuggestedOfferMappings(businessId, getSuggestedOfferMappingsRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BusinessOfferMappingsApi#getSuggestedOfferMappings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BusinessOfferMappingsApi#getSuggestedOfferMappings")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | **kotlin.Long**| Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **getSuggestedOfferMappingsRequest** | [**GetSuggestedOfferMappingsRequest**](GetSuggestedOfferMappingsRequest.md)|  | [optional]

### Return type

[**GetSuggestedOfferMappingsResponse**](GetSuggestedOfferMappingsResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="updateOfferMappings"></a>
# **updateOfferMappings**
> UpdateOfferMappingsResponse updateOfferMappings(businessId, updateOfferMappingsRequest, language)

Добавление товаров в каталог и изменение информации о них

{% include notitle [access](../../_auto/method_scopes/updateOfferMappings.md) %}  Добавляет товары в каталог, передает их категории на Маркете и характеристики, необходимые для этих категории. Также редактирует информацию об уже имеющихся товарах.  Список категорий Маркета можно получить с помощью запроса [POST categories/tree](../../reference/categories/getCategoriesTree.md), а характеристики товаров по категориям с помощью [POST category/{categoryId}/parameters](../../reference/content/getCategoryContentParameters.md).  Чтобы **добавить новый товар**, передайте его с новым идентификатором, который раньше никогда не использовался в каталоге. Старайтесь сразу передать как можно больше информации — она потребуется Маркету для подбора подходящей карточки или создания новой. Если известно, какой карточке на Маркете соответствует товар, можно сразу указать идентификатор этой карточки (**SKU на Маркете**) в поле &#x60;marketSKU&#x60;.  Для **новых товаров** обязательно укажите параметры: &#x60;offerId&#x60;, &#x60;name&#x60;, &#x60;marketCategoryId&#x60;, &#x60;pictures&#x60;, &#x60;vendor&#x60;, &#x60;description&#x60;.  Чтобы **отредактировать информацию о товаре**, передайте новые данные, указав в &#x60;offerId&#x60; соответствующий **ваш SKU**. Поля, в которых ничего не меняется, можно не передавать.  Чтобы **удалить характеристики**, которые заданы в параметрах с типом &#x60;string&#x60;, передайте пустое значение.  Параметр &#x60;offerId&#x60; должен быть **уникальным** для всех товаров, которые вы передаете.  {% note warning \&quot;Правила использования SKU\&quot; %}  * У каждого товара SKU должен быть свой.  * Уже заданный SKU нельзя освободить и использовать заново для другого товара. Каждый товар должен получать новый идентификатор, до того никогда не использовавшийся в вашем каталоге.  SKU товара можно изменить в кабинете продавца на Маркете. О том, как это сделать, читайте [в Справке Маркета для продавцов](https://yandex.ru/support2/marketplace/ru/assortment/operations/edit-sku).  {% endnote %}  {% note info \&quot;Данные в каталоге обновляются не мгновенно\&quot; %}  Это занимает до нескольких минут.  {% endnote %}  |**⚙️ Лимит:** 5000 товаров в минуту, не более 500 товаров в одном запросе| |-| 

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BusinessOfferMappingsApi()
val businessId : kotlin.Long = 789 // kotlin.Long | Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html) 
val updateOfferMappingsRequest : UpdateOfferMappingsRequest =  // UpdateOfferMappingsRequest | 
val language : CatalogLanguageType =  // CatalogLanguageType | Язык, на котором принимаются и возвращаются значения в параметрах `name` и `description`.  Значение по умолчанию: `RU`. 
try {
    val result : UpdateOfferMappingsResponse = apiInstance.updateOfferMappings(businessId, updateOfferMappingsRequest, language)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BusinessOfferMappingsApi#updateOfferMappings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BusinessOfferMappingsApi#updateOfferMappings")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **businessId** | **kotlin.Long**| Идентификатор кабинета. Чтобы узнать идентификатор, воспользуйтесь запросом [GET campaigns](../../reference/campaigns/getCampaigns.md#businessdto).  ℹ️ [Что такое кабинет и магазин на Маркете](https://yandex.ru/support/marketplace/account/introduction.html)  |
 **updateOfferMappingsRequest** | [**UpdateOfferMappingsRequest**](UpdateOfferMappingsRequest.md)|  |
 **language** | [**CatalogLanguageType**](.md)| Язык, на котором принимаются и возвращаются значения в параметрах &#x60;name&#x60; и &#x60;description&#x60;.  Значение по умолчанию: &#x60;RU&#x60;.  | [optional] [enum: RU, UZ]

### Return type

[**UpdateOfferMappingsResponse**](UpdateOfferMappingsResponse.md)

### Authorization


Configure ApiKey:
    ApiClient.apiKey["Api-Key"] = ""
    ApiClient.apiKeyPrefix["Api-Key"] = ""
Configure OAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

