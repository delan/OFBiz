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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *@author     Olivier.Heintz@nereide.biz
 *@version    $Rev:$
 *@since      3.0
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#assign security = requestAttributes.security>
<#assign unselectedLeftClassName = "headerButtonLeft">
<#assign unselectedRightClassName = "headerButtonRight">
<#assign selectedLeftClassMap = {page.headerItem?default("void") : "headerButtonLeftSelected"}>
<#assign selectedRightClassMap = {page.headerItem?default("void") : "headerButtonRightSelected"}>

<div class="apptitle">&nbsp;${uiLabelMap.ManufacturingManagerApplication}&nbsp;</div>
<div class="row">
  <div class="col"><a href="<@ofbizUrl>/main</@ofbizUrl>" class="${selectedLeftClassMap.main?default(unselectedLeftClassName)}">${uiLabelMap.CommonMain}</a></div>
  <#if security.hasEntityPermission("MANUFACTURING", "_CREATE", session)>
    <div class="col"><a href="<@ofbizUrl>/FindProductionRun</@ofbizUrl>" class="${selectedLeftClassMap.jobshop?default(unselectedLeftClassName)}">${uiLabelMap.ManufacturingJobShop}</a></div>
    <div class="col"><a href="<@ofbizUrl>/FindRouting</@ofbizUrl>" class="${selectedLeftClassMap.routing?default(unselectedLeftClassName)}">${uiLabelMap.ManufacturingRouting}</a></div>
    <div class="col"><a href="<@ofbizUrl>/FindCalendar</@ofbizUrl>" class="${selectedLeftClassMap.calendar?default(unselectedLeftClassName)}">${uiLabelMap.ManufacturingCalendar}</a></div>
    <div class="col"><a href="<@ofbizUrl>/findBom</@ofbizUrl>" class="${selectedLeftClassMap.bom?default(unselectedLeftClassName)}">${uiLabelMap.ManufacturingBillOfMaterials}</a></div>
    <div class="col"><a href="<@ofbizUrl>/FindInventoryEventPlan</@ofbizUrl>" class="${selectedLeftClassMap.mrp?default(unselectedLeftClassName)}">${uiLabelMap.ManufacturingMrp}</a></div>
  </#if>  
  <#if requestAttributes.userLogin?has_content>
    <div class="col-right"><a href="<@ofbizUrl>/logout</@ofbizUrl>" class="${selectedRightClassMap.logout?default(unselectedRightClassName)}">${uiLabelMap.CommonLogout}</a></div>
  <#else>
    <div class="col-right"><a href='<@ofbizUrl>${requestAttributes.checkLoginUrl?if_exists}</@ofbizUrl>' class='${selectedRightClassMap.login?default(unselectedRightClassName)}'>${uiLabelMap.CommonLogin}</a></div>
  </#if>
  <div class="col-fill">&nbsp;</div>
</div>
