
# GenerateGoodsMovementReportRequest

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**campaignId** | **kotlin.Long** | Идентификатор кампании. | 
**dateFrom** | [**java.time.LocalDate**](java.time.LocalDate.md) | Начало периода, включительно. Формат даты: &#x60;ГГГГ-ММ-ДД&#x60;.  | 
**dateTo** | [**java.time.LocalDate**](java.time.LocalDate.md) | Конец периода, включительно. Формат даты: &#x60;ГГГГ-ММ-ДД&#x60;.  | 
**shopSku** | **kotlin.String** | Ваш SKU — идентификатор товара в вашей системе.  Правила использования SKU:  * У каждого товара SKU должен быть свой.  * Уже заданный SKU нельзя освободить и использовать заново для другого товара. Каждый товар должен получать новый идентификатор, до того никогда не использовавшийся в вашем каталоге.  SKU товара можно изменить в кабинете продавца на Маркете. О том, как это сделать, читайте [в Справке Маркета для продавцов](https://yandex.ru/support2/marketplace/ru/assortment/operations/edit-sku).  [Что такое SKU и как его назначать](https://yandex.ru/support/marketplace/assortment/add/index.html#fields)  |  [optional]



