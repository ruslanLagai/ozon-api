
# OrderBuyerInfoDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | [**OrderBuyerType**](OrderBuyerType.md) |  | 
**id** | **kotlin.String** | Идентификатор покупателя. |  [optional]
**lastName** | **kotlin.String** | Фамилия покупателя. |  [optional]
**firstName** | **kotlin.String** | Имя покупателя. |  [optional]
**middleName** | **kotlin.String** | Отчество покупателя. |  [optional]
**phone** | **kotlin.String** | Подменный номер телефона покупателя. Подробнее о таких номерах читайте [в Справке Маркета для продавцов](https://yandex.ru/support2/marketplace/ru/orders/dbs/call#fake-number).  Формат номера: &#x60;+&lt;код_страны&gt;&lt;код_региона&gt;&lt;номер_телефона&gt;&#x60;.  |  [optional]
**trusted** | **kotlin.Boolean** | Проверенный покупатель.  Если параметр &#x60;trusted&#x60; вернулся со значением &#x60;true&#x60;, Маркет уже проверил покупателя — не звоните ему. Обработайте заказ как обычно и передайте его курьеру или отвезите в ПВЗ.  При необходимости свяжитесь с покупателем в чате. [Как это сделать](../../step-by-step/chats.md)  Подробнее о звонках покупателю читайте [в Справке Маркета для продавцов](https://yandex.ru/support/marketplace/ru/orders/dbs/call).  |  [optional]



