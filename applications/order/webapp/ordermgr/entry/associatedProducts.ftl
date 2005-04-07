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
 *@author     Jean-Luc.Malet@nereide.biz (migration to uiLabelMap)
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *@version    $Rev$
 *@since      2.2
-->

<#if requestAttributes.uiLabelMap?exists>
    <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>

<#if (shoppingCart.getOrderType() == "SALES_ORDER")>
    <#assign associatedProducts = Static["org.ofbiz.order.shoppingcart.product.ProductDisplayWorker"].getRandomCartProductAssoc(request, true)?if_exists>
</#if>

<#if associatedProducts?has_content>
  <BR>
  <TABLE border="0" cellspacing="0" cellpadding="0" class="boxoutside">
    <TR>
      <TD>
        <table border="0" cellspacing="0" cellpadding="0" class="boxtop">
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.help_also_interested_in}</div>
            </td>
            <#--<td valign="middle" align="right">&nbsp;</td>-->
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD>
        <table border="0" cellspacing="0" cellpadding="0" class="boxbottom">
          <tr>
            <td>
    <table cellspacing="0" cellpadding="1" border="0">
      <#-- random complementary products -->
      <#list associatedProducts as assocProduct> 
        <tr>
          <td>
            ${setRequestAttribute("optProduct", assocProduct)} 
            ${setRequestAttribute("listIndex", assocProduct_index)}         
            ${screens.render(productsummaryScreen)}
          </td>
        </tr>
        <#if assocProduct_has_next>
          <tr><td><hr class="sepbar"></td></tr>
        </#if>
      </#list>
    </table>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</#if>
