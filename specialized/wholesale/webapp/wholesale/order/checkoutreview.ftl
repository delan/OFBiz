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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision$
 *@since      2.1
-->

<script language="JavaScript" type="text/javascript">
<!--
    var clicked = 0;
    function processOrder() {
        if (clicked == 0) {
            clicked++;
            window.location.replace("<@ofbizUrl>/processorder</@ofbizUrl>");
        } else {
            alert("You order is being processed, this may take a moment.");
        }
    }
// -->
</script>

<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<p class="head1">${uiLabelMap.OrderFinalCheckoutReview}</p>
<p>${uiLabelMap.OrderDemoFrontNote}.</p>

<#if cart?exists && 0 < cart.size()>
  <#if pages?exists>${pages.get("/order/orderheader.ftl")}</#if>
  <#if screens?exists>${screens.render("component://wholesale/widget/OrderScreens.xml#orderheader")}</#if>
  <br/>
 <#if pages?exists>${pages.get("/order/orderitems.ftl")}</#if>
 <#if screens?exists>${screens.render("component://wholesale/widget/OrderScreens.xml#orderitems")}</#if>
  <table border="0" cellpadding="1" width="100%">
   <tr>
      <td colspan="4" align="left">
        <a href="<@ofbizUrl>/${requestParameters.BACK_PAGE?default("setBilling")}</@ofbizUrl>" class="buttontextbig">[${uiLabelMap.CommonBackToOptions}]</a>
      </td>
      <td align="right">
        <a href="javascript:processOrder();" class="buttontextbig">[${uiLabelMap.OrderSubmitOrder}]&nbsp;</a>
      </td>
    </tr>
  </table>
<#else>
  <h3>${uiLabelMap.OrderErrorShoppingCartEmpty}.</h3>
</#if>
