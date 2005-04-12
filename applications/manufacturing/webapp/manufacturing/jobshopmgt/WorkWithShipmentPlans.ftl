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
<#if (requestAttributes.uiLabelMap)?exists>
    <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>

<#if shipment?exists>
  <div class="head1">Shipment Plan: ${shipment.shipmentId}</div>
  ${listShipmentPlanForm.renderFormString()}
  <#if workInProgress>
    <br>
    <div><a href="<@ofbizUrl>/ShipmentWorkEffortTasks.pdf?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext" target="_report">[${uiLabelMap.ManufacturingTasksReport}]</a></div>
    <div><a href="<@ofbizUrl>/CuttingListReport.pdf?shipmentId=${shipmentId}</@ofbizUrl>" target="_report" class="buttontext">[${uiLabelMap.ManufacturingCuttingListReport}]</a></div>
  <#else>
    <div><a href="<@ofbizUrl>createProductionRunsForOrder?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CreateProductionRuns}]</a></div>
    <br>
    <div><a href="<@ofbizUrl>/ShipmentPlanStockReport.pdf?shipmentId=${shipmentId}</@ofbizUrl>" target="_report" class="buttontext">[${uiLabelMap.ManufacturingShipmentPlanStockReport}]</a></div>
  </#if>
  <div><a href="<@ofbizUrl>/ShipmentLabel.pdf?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext" target="_report">[${uiLabelMap.ManufacturingPackageLabelsReport}]</a></div>

<#else>
<div class="head1">Shipment Plans</div>
<#if listShipmentPlansForm?has_content>
  ${listShipmentPlansForm.renderFormString()}
</#if>

</#if>

