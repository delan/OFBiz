<#--
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 *@version    $Revision: 1.3 $
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>

<div class='insideHeaderText'>
<#if (shoppingCartSize > 0)>
  ${uiLabelMap.CartCartHas} <b>${shoppingCart.getTotalQuantity()}</b> ${uiLabelMap.CartCartHas}, <b>${shoppingCart.getGrandTotal()?string.currency}</b>
<#else>
  ${uiLabelMap.CartShoppingCartEmpty}</b>
</#if>
  &nbsp;&nbsp;
</div>
<div class='insideHeaderDisabled'>
  <a href="<transform ofbizUrl>/view/showcart</transform>" class="insideHeaderLink">[${uiLabelMap.CartViewCart}]</a>
  <#if (shoppingCartSize > 0)>
    <a href="<transform ofbizUrl>/quickcheckout</transform>" class="insideHeaderLink">[${uiLabelMap.CartCheckout}]</a>
  <#else>
    [${uiLabelMap.CartCheckout}]
  </#if>
  &nbsp;&nbsp;
</div>
