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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision$
 *@since      2.1
-->

<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>
    
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
      <div class='boxhead'><b>Cart&nbsp;Summary</b></div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <table width="100%" border="0" cellpadding="2" cellspacing="0">
              <#if (shoppingCartSize > 0)>
                <tr>
                  <td colspan="3">
                    <a href="<@ofbizUrl>/view/showcart</@ofbizUrl>" class="buttontext">[View&nbsp;Cart]&nbsp;</a><a href="<@ofbizUrl>/checkoutoptions</@ofbizUrl>" class="buttontext">[Checkout]</a>
                  </td>
                </tr>
                <tr>
                  <td valign="bottom"><div class="tabletext"><b>#<b></div></td>
                  <td valign="bottom"><div class="tabletext"><b>Item<b></div></td>
                  <td valign="bottom"><div class="tabletext"><b>Subtotal<b></div></td>
                </tr>
                <#list shoppingCart.items() as cartLine>
                  <tr>
                    <td valign="top"><div class="tabletext" nowrap>${cartLine.getQuantity()?string.number}</div></td>                    
                    <td valign="top">
                      <div><a href="<transform ofbizUrl>/product?product_id=${cartLine.getProductId()}</transform>" class="buttontext">${cartLine.getName()}</a></div>
                    </td>
                    <td align="right" valign="top"><div class="tabletext" nowrap>${cartLine.getItemSubTotal()?string.currency}</div></td>
                  </tr>
                </#list>
                <tr>
                  <td colspan="3" align="right">
                    <div class="tabletext"><b>Total: ${shoppingCart.getGrandTotal()?string.currency}</b></div>
                  </td>
                </tr>
                <tr>
                  <td colspan="3">
                    <a href="<transform ofbizUrl>/view/showcart</transform>" class="buttontext">[View&nbsp;Cart]&nbsp;</a><a href="<transform ofbizUrl>/checkoutoptions</transform>" class="buttontext">[Checkout]</a>
                  </td>
                </tr>
              <#else>
                <tr>
                  <td nowrap colspan="3"><div class="tabletext">Shopping Cart is empty.</div></td>
                </tr>
              </#if>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

