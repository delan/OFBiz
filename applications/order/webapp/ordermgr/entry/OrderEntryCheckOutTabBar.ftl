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
 *@author     Jacopo Cappellato
-->

<#if (requestAttributes.uiLabelMap)?exists>
    <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>
<#if stepTitleId?exists>
    <#assign stepTitle = uiLabelMap.get(stepTitleId)>
</#if>
<div class="boxtop">
    <div class="boxhead" align="left">
        <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
            ${uiLabelMap.OrderPurchaseOrder}
        <#else>
            ${uiLabelMap.OrderSalesOrder}
        </#if>
        :&nbsp;${stepTitle?if_exists}
    </div>
    <div class="boxhead" align="right">
        <span class="submenutextdisabled">&nbsp;</span>
        <#list checkoutSteps as checkoutStep>
            <#assign stepUiLabel = uiLabelMap.get(checkoutStep.label)>
            <#if checkoutStep.enabled == "N">
                <span class="submenutextdisabled">${stepUiLabel}</span>
            <#else>
                <a href="<@ofbizUrl>${checkoutStep.uri}</@ofbizUrl>" class="submenutext">${stepUiLabel}</a>
            </#if>
        </#list>
        <span class="submenutextdisabled">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <#if isLastStep == "N">
            <a href="javascript:document.checkoutsetupform.submit();" class="submenutextright">${uiLabelMap.CommonContinue}</a>
        <#else>
            <a href="<@ofbizUrl>/processorder</@ofbizUrl>" class="submenutext">${uiLabelMap.OrderCreateOrder}</a>
        </#if>
    </div>
</div>
