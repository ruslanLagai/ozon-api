
# OrderDigitalItemDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **kotlin.Long** | Идентификатор товара в заказе.  Он приходит в ответе на запрос [GET campaigns/{campaignId}/orders/{orderId}](../../reference/orders/getOrder.md) — параметр &#x60;id&#x60; в &#x60;items&#x60;.  | 
**slip** | **kotlin.String** | Инструкция по активации. | 
**activateTill** | [**java.time.LocalDate**](java.time.LocalDate.md) | Дата, до которой нужно активировать ключи. Если ключи действуют бессрочно, укажите любую дату в отдаленном будущем.  Формат даты: &#x60;ГГГГ-ММ-ДД&#x60;.  | 
**code** | **kotlin.String** | {% note warning \&quot;Этот параметр устарел\&quot; %}  Вместо него используйте &#x60;codes&#x60;. Совместное использование обоих параметров приведет к ошибке.  {% endnote %}  Сам ключ.  |  [optional]
**codes** | **kotlin.collections.Set&lt;kotlin.String&gt;** | Ключи, относящиеся к товару. |  [optional]



