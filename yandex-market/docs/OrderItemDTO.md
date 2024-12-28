
# OrderItemDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **kotlin.Long** | Идентификатор товара в заказе.  Позволяет идентифицировать товар в рамках данного заказа.  | 
**offerId** | **kotlin.String** | Ваш SKU — идентификатор товара в вашей системе.  Правила использования SKU:  * У каждого товара SKU должен быть свой.  * Уже заданный SKU нельзя освободить и использовать заново для другого товара. Каждый товар должен получать новый идентификатор, до того никогда не использовавшийся в вашем каталоге.  SKU товара можно изменить в кабинете продавца на Маркете. О том, как это сделать, читайте [в Справке Маркета для продавцов](https://yandex.ru/support2/marketplace/ru/assortment/operations/edit-sku).  [Что такое SKU и как его назначать](https://yandex.ru/support/marketplace/assortment/add/index.html#fields)  | 
**offerName** | **kotlin.String** | Название товара. | 
**price** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Цена на товар в валюте заказа без учета вознаграждения партнеру за скидки по промокодам, купонам и акциям (параметр &#x60;subsidies&#x60;).  | 
**buyerPrice** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Цена на товар в валюте покупателя. В цене уже учтены скидки по:  * акциям; * купонам; * промокодам.  | 
**buyerPriceBeforeDiscount** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Стоимость товара в валюте покупателя до применения скидок по:  * акциям; * купонам; * промокодам.  | 
**count** | **kotlin.Int** | Количество единиц товара. | 
**vat** | [**OrderVatType**](OrderVatType.md) |  | 
**priceBeforeDiscount** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | {% note warning \&quot;Этот параметр устарел\&quot; %}  Не используйте его.  {% endnote %}  Стоимость товара в валюте магазина до применения скидок.  |  [optional]
**shopSku** | **kotlin.String** | Ваш SKU — идентификатор товара в вашей системе.  Правила использования SKU:  * У каждого товара SKU должен быть свой.  * Уже заданный SKU нельзя освободить и использовать заново для другого товара. Каждый товар должен получать новый идентификатор, до того никогда не использовавшийся в вашем каталоге.  SKU товара можно изменить в кабинете продавца на Маркете. О том, как это сделать, читайте [в Справке Маркета для продавцов](https://yandex.ru/support2/marketplace/ru/assortment/operations/edit-sku).  [Что такое SKU и как его назначать](https://yandex.ru/support/marketplace/assortment/add/index.html#fields)  |  [optional]
**subsidy** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | {% note warning \&quot;Этот параметр устарел\&quot; %}  Вместо него используйте &#x60;subsidies&#x60;.  {% endnote %}  Общее вознаграждение партнеру за DBS-доставку и все скидки на товар:  * по промокодам; * по купонам; * по баллам Плюса; * по акциям.  |  [optional]
**partnerWarehouseId** | **kotlin.String** | {% note warning \&quot;Этот параметр устарел\&quot; %}  Не используйте его.  {% endnote %}  Идентификатор склада в системе партнера, на который сформирован заказ.  |  [optional]
**promos** | [**kotlin.collections.List&lt;OrderItemPromoDTO&gt;**](OrderItemPromoDTO.md) | Информация о вознаграждениях партнеру за скидки на товар по промокодам, купонам и акциям. |  [optional]
**instances** | [**kotlin.collections.List&lt;OrderItemInstanceDTO&gt;**](OrderItemInstanceDTO.md) | Информация о маркировке единиц товара.  Возвращаются данные для маркировки, переданные в запросе [PUT campaigns/{campaignId}/orders/{orderId}/identifiers](../../reference/orders/provideOrderItemIdentifiers.md).  Если магазин еще не передавал коды для этого заказа, &#x60;instances&#x60; отсутствует.  |  [optional]
**details** | [**kotlin.collections.List&lt;OrderItemDetailDTO&gt;**](OrderItemDetailDTO.md) | Информация об удалении товара из заказа.  |  [optional]
**subsidies** | [**kotlin.collections.List&lt;OrderItemSubsidyDTO&gt;**](OrderItemSubsidyDTO.md) | Список субсидий по типам. |  [optional]
**requiredInstanceTypes** | [**kotlin.collections.List&lt;OrderItemInstanceType&gt;**](OrderItemInstanceType.md) | Список необходимых маркировок товара. |  [optional]
**tags** | [**kotlin.collections.List&lt;OrderItemTagType&gt;**](OrderItemTagType.md) | Признаки товара. |  [optional]



