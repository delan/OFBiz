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
 *@author     Olivier.Heintz@nereide.biz
 *@version    $Rev:$
 *@since      3.0
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<div class='tabContainer'>
        <a href="<@ofbizUrl>/FindRouting</@ofbizUrl>" class="${selectedClassMap.findRouting?default(unselectedClassName)}">${uiLabelMap.ManufacturingFindRouting}</a>
        <a href="<@ofbizUrl>/FindRouting?workEffortId=${routingId}&hideFields=Y&lookupFlag=Y</@ofbizUrl>" class="${selectedClassMap.routing?default(unselectedClassName)}">${uiLabelMap.ManufacturingEditRouting}</a>
        <a href="<@ofbizUrl>/EditRoutingProductLink?workEffortId=${routingId}</@ofbizUrl>" class="${selectedClassMap.routingProductLink?default(unselectedClassName)}">${uiLabelMap.ManufacturingEditRoutingProductLink}</a>
        <a href="<@ofbizUrl>//EditRoutingTaskAssoc?workEffortIdFrom=${routingId}</@ofbizUrl>" class="${selectedClassMap.routingTaskAssoc?default(unselectedClassName)}">${uiLabelMap.ManufacturingEditRoutingTaskAssoc}</a>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <a href="<@ofbizUrl>/FindRoutingTask</@ofbizUrl>" class="${selectedClassMap.routingTask?default(unselectedClassName)}">${uiLabelMap.ManufacturingRoutingTask}</a>
        <a href="<@ofbizUrl>/EditMachine</@ofbizUrl>" class="${selectedClassMap.machine?default(unselectedClassName)}">${uiLabelMap.ManufacturingMachine}</a>
        <a href="<@ofbizUrl>/EditMachineGroup</@ofbizUrl>" class="${selectedClassMap.machineGroup?default(unselectedClassName)}">${uiLabelMap.ManufacturingMachineGroup}</a>
</div>
