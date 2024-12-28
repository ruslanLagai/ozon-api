
# OrderDeliveryDTO

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | [**OrderDeliveryType**](OrderDeliveryType.md) |  | 
**serviceName** | **kotlin.String** | Наименование службы доставки. | 
**deliveryPartnerType** | [**OrderDeliveryPartnerType**](OrderDeliveryPartnerType.md) |  | 
**dates** | [**OrderDeliveryDatesDTO**](OrderDeliveryDatesDTO.md) |  | 
**deliveryServiceId** | **kotlin.Long** | Идентификатор службы доставки. | 
**id** | **kotlin.String** | {% note warning \&quot;Этот параметр устарел\&quot; %}  Не используйте его.  {% endnote %}  Идентификатор доставки, присвоенный магазином.  Указывается, только если магазин передал данный идентификатор в ответе на запрос методом &#x60;POST cart&#x60;.  |  [optional]
**price** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | {% note warning \&quot;Этот параметр устарел\&quot; %}  Стоимость доставки смотрите в параметре &#x60;deliveryTotal&#x60;.  {% endnote %}  Стоимость доставки в валюте заказа.  |  [optional]
**courier** | [**OrderCourierDTO**](OrderCourierDTO.md) |  |  [optional]
**region** | [**RegionDTO**](RegionDTO.md) |  |  [optional]
**address** | [**OrderDeliveryAddressDTO**](OrderDeliveryAddressDTO.md) |  |  [optional]
**vat** | [**OrderVatType**](OrderVatType.md) |  |  [optional]
**liftType** | [**OrderLiftType**](OrderLiftType.md) |  |  [optional]
**liftPrice** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Стоимость подъема на этаж. |  [optional]
**outletCode** | **kotlin.String** | Идентификатор пункта самовывоза, присвоенный магазином. |  [optional]
**outletStorageLimitDate** | **kotlin.String** | Формат даты: &#x60;ДД-ММ-ГГГГ&#x60;.  |  [optional]
**dispatchType** | [**OrderDeliveryDispatchType**](OrderDeliveryDispatchType.md) |  |  [optional]
**tracks** | [**kotlin.collections.List&lt;OrderTrackDTO&gt;**](OrderTrackDTO.md) | Информация для отслеживания перемещений посылки. |  [optional]
**shipments** | [**kotlin.collections.List&lt;OrderShipmentDTO&gt;**](OrderShipmentDTO.md) | Информация о посылках. |  [optional]
**estimated** | **kotlin.Boolean** | Приблизительная ли дата доставки. |  [optional]
**eacType** | [**OrderDeliveryEacType**](OrderDeliveryEacType.md) |  |  [optional]
**eacCode** | **kotlin.String** | Код подтверждения ЭАПП (для типа &#x60;MERCHANT_TO_COURIER&#x60;).  |  [optional]



