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
 *@version    $Rev:$
 *@since      2.1
-->

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align='left'><div class="boxhead">${requestAttributes.uiLabelMap.OrderHistory}</div></td>
          <#--<td align='right'>
            <a href="<@ofbizUrl>/main</@ofbizUrl>" class="lightbuttontext">[${requestAttributes.uiLabelMap.OrderBackHome}]</a>&nbsp;&nbsp;
          </td>-->
        </tr>
      </table>      
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <table width="100%" cellpadding="1" cellspacing="0" border="0">
              <tr>
                <td width="30%">
                  <div class="tabletext"><b>${requestAttributes.uiLabelMap.CommonDate}</b></div>
                </td>
                <td width="15%">
                  <div class="tabletext"><b><nobr>${requestAttributes.uiLabelMap.OrderOrder} #</nobr></b></div>
                </td>
                <td width="15%">
                  <div class="tabletext"><b>${requestAttributes.uiLabelMap.CommonAmount}</b></div>
                </td>
                <td width="15%">
                  <div class="tabletext"><b>${requestAttributes.uiLabelMap.CommonStatus}</b></div>
                </td>
                <td width="15%"><b></b></td>                
              </tr>
              <#list orderHeaderList as orderHeader>
                <#assign status = orderHeader.getRelatedOneCache("StatusItem")>                               
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                  <td>
                    <div class="tabletext"><nobr>${orderHeader.orderDate.toString()}</nobr></div>
                  </td>
                  <td>
                    <div class="tabletext">${orderHeader.orderId}</div>
                  </td>
                  <td>
                    <div class="tabletext"><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom/></div>
                  </td>
                  <td>
                    <div class="tabletext">${status.description}</div>
                  </td>
                  <td align=right>
                    <a href="<@ofbizUrl>/orderstatus?order_id=${orderHeader.orderId}</@ofbizUrl>" class='buttontext'>[${requestAttributes.uiLabelMap.CommonView}]</a>
                  </td>
                </tr>
              </#list>
              <#if !orderHeaderList?has_content>
                <tr><td colspan="8"><div class='head3'>${requestAttributes.uiLabelMap.OrderNoOrderFound}</div></td></tr>
              </#if>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

