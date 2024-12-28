
# ReturnItemDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**shopSku** | **kotlin.String** | Ваш SKU — идентификатор товара в вашей системе.  Правила использования SKU:  * У каждого товара SKU должен быть свой.  * Уже заданный SKU нельзя освободить и использовать заново для другого товара. Каждый товар должен получать новый идентификатор, до того никогда не использовавшийся в вашем каталоге.  SKU товара можно изменить в кабинете продавца на Маркете. О том, как это сделать, читайте [в Справке Маркета для продавцов](https://yandex.ru/support2/marketplace/ru/assortment/operations/edit-sku).  [Что такое SKU и как его назначать](https://yandex.ru/support/marketplace/assortment/add/index.html#fields)  | 
**count** | **kotlin.Long** | Количество единиц товара. | 
**marketSku** | **kotlin.Long** | SKU на Маркете. |  [optional]
**decisions** | [**kotlin.collections.List&lt;ReturnDecisionDTO&gt;**](ReturnDecisionDTO.md) | Список решений по возврату. |  [optional]
**instances** | [**kotlin.collections.List&lt;ReturnInstanceDTO&gt;**](ReturnInstanceDTO.md) | Список логистических позиций возврата. |  [optional]
**tracks** | [**kotlin.collections.List&lt;TrackDTO&gt;**](TrackDTO.md) | Список трек-кодов для почтовых отправлений. |  [optional]



