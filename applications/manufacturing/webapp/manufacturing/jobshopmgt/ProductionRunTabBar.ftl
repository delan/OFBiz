<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 *@author     Olivier.Heintz@nereide.biz
 *@version    $Rev$
 *@since      3.0
-->

<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<div class='tabContainer'>
    <a href="<@ofbizUrl>WorkWithShipmentPlans</@ofbizUrl>" class="${selectedClassMap.WorkWithShipmentPlans?default(unselectedClassName)}">${uiLabelMap.ManufacturingWorkWithShipmentPlans}</a>
    <a href="<@ofbizUrl>FindProductionRun</@ofbizUrl>" class="${selectedClassMap.find?default(unselectedClassName)}">${uiLabelMap.ManufacturingFindProductionRun}</a>
    <a href="<@ofbizUrl>CreateProductionRun</@ofbizUrl>" class="${selectedClassMap.create?default(unselectedClassName)}">${uiLabelMap.ManufacturingCreateProductionRun}</a>
    <#if productionRun?has_content>
        <#if productionRun.getString("currentStatusId") == "PRUN_CREATED">
        <a href="<@ofbizUrl>EditProductionRun?productionRunId=${productionRunId}</@ofbizUrl>" class="${selectedClassMap.edit?default(unselectedClassName)}">${uiLabelMap.ManufacturingEditProductionRun}</a>
        <#else>
        <a href="<@ofbizUrl>ProductionRunDeclaration?productionRunId=${productionRunId}</@ofbizUrl>" class="${selectedClassMap.declaration?default(unselectedClassName)}">${uiLabelMap.ManufacturingProductionRunDeclaration}</a>
        </#if>
    </#if>
    <a href="<@ofbizUrl>ManufacturingReports</@ofbizUrl>" class="${selectedClassMap.ManufacturingReports?default(unselectedClassName)}">${uiLabelMap.ManufacturingReports}</a>
</div>
