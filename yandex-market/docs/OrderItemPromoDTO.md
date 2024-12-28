
# OrderItemPromoDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | [**OrderPromoType**](OrderPromoType.md) |  | 
**subsidy** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Вознаграждение партнеру от Маркета за товар, проданный в рамках акции.  | 
**discount** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Размер пользовательской скидки в валюте покупателя.  |  [optional]
**shopPromoId** | **kotlin.String** | Идентификатор акции поставщика.  |  [optional]
**marketPromoId** | **kotlin.String** | Идентификатор акции в рамках соглашения на оказание услуг по продвижению сервиса между Маркетом и партнером.  Параметр передается, только если параметр &#x60;type&#x3D;MARKET_DEAL&#x60;.  |  [optional]



