
# WarehouseOfferDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**offerId** | **kotlin.String** | Ваш SKU — идентификатор товара в вашей системе.  Правила использования SKU:  * У каждого товара SKU должен быть свой.  * Уже заданный SKU нельзя освободить и использовать заново для другого товара. Каждый товар должен получать новый идентификатор, до того никогда не использовавшийся в вашем каталоге.  SKU товара можно изменить в кабинете продавца на Маркете. О том, как это сделать, читайте [в Справке Маркета для продавцов](https://yandex.ru/support2/marketplace/ru/assortment/operations/edit-sku).  [Что такое SKU и как его назначать](https://yandex.ru/support/marketplace/assortment/add/index.html#fields)  | 
**stocks** | [**kotlin.collections.List&lt;WarehouseStockDTO&gt;**](WarehouseStockDTO.md) | Информация об остатках. | 
**turnoverSummary** | [**TurnoverDTO**](TurnoverDTO.md) |  |  [optional]
**updatedAt** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | Дата и время последнего обновления информации об остатках.  Формат даты и времени: ISO 8601 со смещением относительно UTC. Например, &#x60;2023-11-21T00:42:42+03:00&#x60;.  |  [optional]



