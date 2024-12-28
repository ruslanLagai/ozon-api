
# OrderDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **kotlin.Long** | Идентификатор заказа. | 
**status** | [**OrderStatusType**](OrderStatusType.md) |  | 
**substatus** | [**OrderSubstatusType**](OrderSubstatusType.md) |  | 
**creationDate** | **kotlin.String** |  | 
**currency** | [**CurrencyType**](CurrencyType.md) |  | 
**itemsTotal** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Платеж покупателя.  | 
**deliveryTotal** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Стоимость доставки.  | 
**buyerItemsTotalBeforeDiscount** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Стоимость всех товаров в заказе в валюте покупателя без учета стоимости доставки и до применения скидок по:  * акциям; * купонам; * промокодам.  | 
**paymentType** | [**OrderPaymentType**](OrderPaymentType.md) |  | 
**paymentMethod** | [**OrderPaymentMethodType**](OrderPaymentMethodType.md) |  | 
**fake** | **kotlin.Boolean** | Тип заказа:  * &#x60;false&#x60; — настоящий заказ покупателя.  * &#x60;true&#x60; — [тестовый](../../concepts/sandbox.md) заказ Маркета.  | 
**items** | [**kotlin.collections.List&lt;OrderItemDTO&gt;**](OrderItemDTO.md) | Список товаров в заказе. | 
**delivery** | [**OrderDeliveryDTO**](OrderDeliveryDTO.md) |  | 
**buyer** | [**OrderBuyerDTO**](OrderBuyerDTO.md) |  | 
**taxSystem** | [**OrderTaxSystemType**](OrderTaxSystemType.md) |  | 
**updatedAt** | **kotlin.String** |  |  [optional]
**buyerItemsTotal** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | {% note warning \&quot;Этот параметр устарел\&quot; %}  Не используйте его.  {% endnote %}  Стоимость всех товаров в заказе в валюте покупателя после применения скидок и без учета стоимости доставки.  |  [optional]
**buyerTotal** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | {% note warning \&quot;Этот параметр устарел\&quot; %}  Не используйте его.  {% endnote %}  Стоимость всех товаров в заказе в валюте покупателя после применения скидок и с учетом стоимости доставки.  |  [optional]
**buyerTotalBeforeDiscount** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | {% note warning \&quot;Этот параметр устарел\&quot; %}  Не используйте его.  {% endnote %}  Стоимость всех товаров в заказе в валюте покупателя до применения скидок и с учетом стоимости доставки (&#x60;buyerItemsTotalBeforeDiscount&#x60; + стоимость доставки).  |  [optional]
**subsidies** | [**kotlin.collections.List&lt;OrderSubsidyDTO&gt;**](OrderSubsidyDTO.md) | Список субсидий по типам. |  [optional]
**notes** | **kotlin.String** | Комментарий к заказу. |  [optional]
**cancelRequested** | **kotlin.Boolean** | **Только для модели DBS**  Запрошена ли отмена.  |  [optional]
**expiryDate** | **kotlin.String** |  |  [optional]



