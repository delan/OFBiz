<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if security.hasEntityPermission("MANUFACTURING", "_VIEW", session)>
${pages.get("/jobshopmgt/ProductionRunTabBar.ftl")}

<#if shipment?exists>
  <div class="head1">Shipment Plan: ${shipment.shipmentId}</div>
  ${listShipmentPlanForm.renderFormString()}
  <div><a href="/manufacturing/control/createProductionRunsForOrder?shipmentId=${shipmentId}" class="buttontext">[${uiLabelMap.CreateProductionRuns}]</a></div>
<!--
  <div><a href="<@ofbizUrl>/AddItemsFromOrder?selectFromShipmentPlan=Y&shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductShipmentPlanToOrderItems}]</a></div>
  <div><a href="<@ofbizUrl>/ShipmentPlanMancantiReport.pdf?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext" target="new">[Stampa dei mancanti]</a></div>
  <div><a href="<@ofbizUrl>/ShipmentLabel.pdf?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext" target="new">[Stampa Labels]</a></div>
  <div><a href="<@ofbizUrl>/ShipmentWorkEffortTasks.pdf?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext" target="new">[Stampa Tasks]</a></div>
-->
<#else>
<div class="head1">Shipment Plans</div>
<#if listShipmentPlansForm?has_content>
  ${listShipmentPlansForm.renderFormString()}
</#if>

</#if>

<#else>
  <h3>${uiLabelMap.ManufacturingPermissionError}</h3>
</#if>
