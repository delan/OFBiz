<#--

Copyright 2001-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->
<#if requestAttributes.uiLabelMap?exists>
<#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if shipmentId?has_content>
  <div class='tabContainer'>
    <a href="<@ofbizUrl>ViewShipment?shipmentId=${shipmentId}</@ofbizUrl>" class="${selectedClassMap.ViewShipment?default(unselectedClassName)}">${uiLabelMap.CommonView}</a>
    <a href="<@ofbizUrl>EditShipment?shipmentId=${shipmentId}</@ofbizUrl>" class="${selectedClassMap.EditShipment?default(unselectedClassName)}">${uiLabelMap.CommonEdit}</a>
    <#if shipment.shipmentTypeId?exists && shipment.shipmentTypeId='SALES_SHIPMENT'>
      <a href="<@ofbizUrl>EditShipmentPlan?shipmentId=${shipmentId}</@ofbizUrl>" class="${selectedClassMap.EditShipmentPlan?default(unselectedClassName)}">${uiLabelMap.ProductShipmentPlan}</a>
    </#if>
    <a href="<@ofbizUrl>AddItemsFromOrder?shipmentId=${shipmentId}</@ofbizUrl>" class="${selectedClassMap.AddItemsFromOrder?default(unselectedClassName)}">${uiLabelMap.ProductOrderItems}</a>
    <#if shipment.shipmentTypeId?exists && shipment.shipmentTypeId='PURCHASE_SHIPMENT'>
      <#if shipment.destinationFacilityId?exists>
        <a href="<@ofbizUrl>ReceiveInventory?shipmentId=${shipmentId}&facilityId=${shipment.destinationFacilityId?if_exists}<#if shipment.primaryOrderId?exists>&purchaseOrderId=${shipment.primaryOrderId}</#if></@ofbizUrl>" class="${selectedClassMap.ReceiveInventory?default(unselectedClassName)}">${uiLabelMap.ProductReceiveInventory}</a>
      </#if>
    <#else>
      <a href="<@ofbizUrl>EditShipmentItems?shipmentId=${shipmentId}</@ofbizUrl>" class="${selectedClassMap.EditShipmentItems?default(unselectedClassName)}">${uiLabelMap.ProductItems}</a>
      <a href="<@ofbizUrl>EditShipmentPackages?shipmentId=${shipmentId}</@ofbizUrl>" class="${selectedClassMap.EditShipmentPackages?default(unselectedClassName)}">${uiLabelMap.ProductPackages}</a>
      <a href="<@ofbizUrl>EditShipmentRouteSegments?shipmentId=${shipmentId}</@ofbizUrl>" class="${selectedClassMap.EditShipmentRouteSegments?default(unselectedClassName)}">${uiLabelMap.ProductRouteSegments}</a>
    </#if>
  </div>
</#if>
