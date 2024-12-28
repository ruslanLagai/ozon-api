
# SkuBidRecommendationItemDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**sku** | **kotlin.String** | Ваш SKU — идентификатор товара в вашей системе.  Правила использования SKU:  * У каждого товара SKU должен быть свой.  * Уже заданный SKU нельзя освободить и использовать заново для другого товара. Каждый товар должен получать новый идентификатор, до того никогда не использовавшийся в вашем каталоге.  SKU товара можно изменить в кабинете продавца на Маркете. О том, как это сделать, читайте [в Справке Маркета для продавцов](https://yandex.ru/support2/marketplace/ru/assortment/operations/edit-sku).  [Что такое SKU и как его назначать](https://yandex.ru/support/marketplace/assortment/add/index.html#fields)  | 
**bid** | **kotlin.Int** | Значение ставки. | 
**bidRecommendations** | [**kotlin.collections.List&lt;BidRecommendationItemDTO&gt;**](BidRecommendationItemDTO.md) | Список рекомендованных ставок с соответствующими долями показов и доступными дополнительными инструментами продвижения.  Чем больше ставка, тем большую долю показов она помогает получить и тем больше дополнительных инструментов продвижения доступно.  |  [optional]
**priceRecommendations** | [**kotlin.collections.List&lt;PriceRecommendationItemDTO&gt;**](PriceRecommendationItemDTO.md) | Рекомендованные цены. |  [optional]



