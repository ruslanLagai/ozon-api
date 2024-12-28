
# ShipmentDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **kotlin.Long** | Идентификатор отгрузки. | 
**planIntervalFrom** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | Начало планового интервала отгрузки.  Формат даты: ISO 8601 со смещением относительно UTC.  | 
**planIntervalTo** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | Конец планового интервала отгрузки.  Формат даты: ISO 8601 со смещением относительно UTC.  | 
**orderIds** | **kotlin.collections.Set&lt;kotlin.Long&gt;** | Идентификаторы заказов в отгрузке. | 
**draftCount** | **kotlin.Int** | Количество заказов, которое Маркет запланировал к отгрузке. | 
**plannedCount** | **kotlin.Int** | Количество заказов, которое Маркет подтвердил к отгрузке. | 
**factCount** | **kotlin.Int** | Количество заказов, принятых в сортировочном центре или пункте приема. | 
**availableActions** | [**kotlin.collections.Set&lt;ShipmentActionType&gt;**](ShipmentActionType.md) | Доступные действия над отгрузкой. | 
**shipmentType** | [**ShipmentType**](ShipmentType.md) |  |  [optional]
**warehouse** | [**PartnerShipmentWarehouseDTO**](PartnerShipmentWarehouseDTO.md) |  |  [optional]
**warehouseTo** | [**PartnerShipmentWarehouseDTO**](PartnerShipmentWarehouseDTO.md) |  |  [optional]
**externalId** | **kotlin.String** | Идентификатор отгрузки в вашей системе. Если вы еще не передавали идентификатор, вернется идентификатор из параметра &#x60;id&#x60;. |  [optional]
**deliveryService** | [**DeliveryServiceDTO**](DeliveryServiceDTO.md) |  |  [optional]
**palletsCount** | [**PalletsCountDTO**](PalletsCountDTO.md) |  |  [optional]
**currentStatus** | [**ShipmentStatusChangeDTO**](ShipmentStatusChangeDTO.md) |  |  [optional]



