
# ReturnDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **kotlin.Long** | Идентификатор возврата. | 
**orderId** | **kotlin.Long** | Номер заказа. | 
**items** | [**kotlin.collections.List&lt;ReturnItemDTO&gt;**](ReturnItemDTO.md) | Список товаров в возврате. | 
**returnType** | [**ReturnType**](ReturnType.md) |  | 
**creationDate** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | Дата создания возврата клиентом.  Формат даты: ISO 8601 со смещением относительно UTC.  |  [optional]
**updateDate** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | Дата обновления возврата.  Формат даты: ISO 8601 со смещением относительно UTC.  |  [optional]
**refundStatus** | [**RefundStatusType**](RefundStatusType.md) |  |  [optional]
**logisticPickupPoint** | [**LogisticPickupPointDTO**](LogisticPickupPointDTO.md) |  |  [optional]
**shipmentRecipientType** | [**RecipientType**](RecipientType.md) |  |  [optional]
**shipmentStatus** | [**ReturnShipmentStatusType**](ReturnShipmentStatusType.md) |  |  [optional]
**refundAmount** | **kotlin.Long** | Сумма возврата. |  [optional]
**fastReturn** | **kotlin.Boolean** | Используется ли опция **Быстрый возврат денег за дешевый брак**.  |  [optional]



