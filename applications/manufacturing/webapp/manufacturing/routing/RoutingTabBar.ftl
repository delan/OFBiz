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
 *@version    $Rev$
 *@since      3.0
-->

<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if routing?exists>
<div class='tabContainer'>
    <a href="<@ofbizUrl>/EditRouting?workEffortId=${routing.workEffortId}</@ofbizUrl>" class="${selectedClassMap.editRouting?default(unselectedClassName)}">${uiLabelMap.ManufacturingEditRouting}</a>
    <a href="<@ofbizUrl>/EditRoutingProductLink?workEffortId=${routing.workEffortId}</@ofbizUrl>" class="${selectedClassMap.routingProductLink?default(unselectedClassName)}">${uiLabelMap.ManufacturingEditRoutingProductLink}</a>
    <a href="<@ofbizUrl>/EditRoutingTaskAssoc?workEffortId=${routing.workEffortId}</@ofbizUrl>" class="${selectedClassMap.routingTaskAssoc?default(unselectedClassName)}">${uiLabelMap.ManufacturingEditRoutingTaskAssoc}</a>
</div>
</#if>