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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Rev$
 *@since      2.2
-->

<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if fixedAsset?has_content>
    <div class='tabContainer'>
        <a href="<@ofbizUrl>/EditFixedAsset?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.EditFixedAsset?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAsset}</a>
<!--   <a href="<@ofbizUrl>/ListFixedAssetRollUp?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.ListFixedAssetRollUp?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAssetRollUp}</a>
        <a href="<@ofbizUrl>/ListFixedAssetParties?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.ListFixedAssetParties?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAssetParties}</a> -->
        <a href="<@ofbizUrl>/ListFixedAssetProducts?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.ListFixedAssetProducts?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAssetProducts}</a>
<!--  <a href="<@ofbizUrl>/ListFixedAssetCalendar?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.ListFixedAssetCalendar?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAssetCalendar}</a> -->
    </div>
</#if>
