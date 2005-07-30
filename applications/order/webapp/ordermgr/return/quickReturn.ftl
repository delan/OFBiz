<#--
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
 *@since      3.0
-->

<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">&nbsp;Return Items</div>
    </div>
    <div class="screenlet-body">
        <#-- DO NOT CHANGE THE NAME OF THIS FORM, it will break the some of the multi-service pattern features -->
        <form name="selectAllForm" method="post" action="<@ofbizUrl>/makeQuickReturn</@ofbizUrl>">
          <input type="hidden" name="_checkGlobalScope" value="Y"/>
          <input type="hidden" name="_useRowSubmit" value="Y"/>
          <input type="hidden" name="fromPartyId" value="${party.partyId}"/>
          <input type="hidden" name="toPartyId" value="${toPartyId?if_exists}"/>
          <input type="hidden" name="order_id" value="${orderId}"/>
          <#if (orderHeader?has_content) && (orderHeader.currencyUom?has_content)>
          <input type="hidden" name="currencyUomId" value="${orderHeader.currencyUom}"/>
          </#if>
          <table border="0" width="100%" cellpadding="2" cellspacing="0">
            <tr>
              <td colspan="7"><div class="head3">Return Item(s) From Order #<a href="<@ofbizUrl>/orderview?order_id=${orderId}</@ofbizUrl>" class="buttontext">${orderId}</div></td>
              <td align="right">
                <span class="tableheadtext">Select All</span>&nbsp;
                <input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this);"/>
              </td>
            </tr>
            <tr>
              <td><div class="tableheadtext">Description</div></td>
              <td><div class="tableheadtext">Order Qty</div></td>
              <td><div class="tableheadtext">Return Qty</div></td>
              <td><div class="tableheadtext">Unit Price</div></td>
              <td><div class="tableheadtext">Return Price*</div></td>
              <td><div class="tableheadtext">Return Reason</div></td>
              <td><div class="tableheadtext">Return Type</div></td>
              <td align="right"><div class="tableheadtext">Include?</div></td>
            </tr>
            <tr><td colspan="8"><hr class="sepbar"></td></tr>
            <#if returnableItems?has_content>
              <#assign rowCount = 0>
              <#list returnableItems.keySet() as orderItem>
                <input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}"/>
                <input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}"/>
                <input type="hidden" name="description_o_${rowCount}" value="${orderItem.itemDescription?if_exists}"/>
                <input type="hidden" name="returnItemType_o_${rowCount}" value="ITEM"/>

                <#-- need some order item information -->
                <#assign orderHeader = orderItem.getRelatedOne("OrderHeader")>
                <#assign itemCount = orderItem.quantity>
                <#assign itemPrice = orderItem.unitPrice>
                <#-- end of order item information -->

                <tr>
                  <td>
                    <div class="tabletext">
                      <#if orderItem.productId?exists>
                      <b>${orderItem.productId}</b>:&nbsp;
                      </#if>
                      ${orderItem.itemDescription}
                    </div>
                  </td>
                  <td align='center'>
                    <div class="tabletext">${orderItem.quantity?string.number}</div>
                  </td>
                  <td>
                    <input type="text" class="inputBox" size="6" name="returnQuantity_o_${rowCount}" value="${returnableItems.get(orderItem).get("returnableQuantity")}"/>
                  </td>
                  <td align='left'>
                    <div class="tabletext"><@ofbizCurrency amount=orderItem.unitPrice isoCode=orderHeader.currencyUom/></div>
                  </td>
                  <td>
                    <input type="text" class="inputBox" size="8" name="returnPrice_o_${rowCount}" value="${returnableItems.get(orderItem).get("returnablePrice")?string("##0.00")}"/>
                  </td>
                  <td>
                    <select name="returnReasonId_o_${rowCount}" class="selectBox">
                      <#list returnReasons as reason>
                      <option value="${reason.returnReasonId}">${reason.description?default(reason.returnReasonId)}</option>
                      </#list>
                    </select>
                  </td>
                  <td>
                    <select name="returnTypeId_o_${rowCount}" class="selectBox">
                      <#list returnTypes as type>
                      <option value="${type.returnTypeId}">${type.description?default(type.returnTypeId)}</option>
                      </#list>
                    </select>
                  </td>
                  <td align="right">
                    <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this);"/>
                  </td>
                </tr>
                <tr><td colspan="8"><hr class="sepbar"></td></tr>
                <#assign rowCount = rowCount + 1>
              </#list>
              <input type="hidden" name="_rowCount" value="${rowCount}"/>
              <tr>
                <td colspan="8"><div class="tableheadtext">Please select a ship from address:</td>
              </tr>
              <tr><td colspan="8"><hr class="sepbar"></td></tr>
              <tr>
                <td colspan="8">
                  <table cellspacing="1" cellpadding="2" width="100%">
                    <#list shippingContactMechList as shippingContactMech>
                      <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
                      <tr>
                        <td align="right" width="1%" valign="top" nowrap>
                          <input type="radio" name="originContactMechId" value="${shippingAddress.contactMechId}"/>
                        </td>
                        <td align="left" width="99%" valign="top" nowrap>
                          <div class="tabletext">
                            <#if shippingAddress.toName?has_content><b>To:</b>&nbsp;${shippingAddress.toName}<br/></#if>
                            <#if shippingAddress.attnName?has_content><b>Attn:</b>&nbsp;${shippingAddress.attnName}<br/></#if>
                            <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br/></#if>
                            <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br/></#if>
                            <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
                            <#if shippingAddress.stateProvinceGeoId?has_content><br/>${shippingAddress.stateProvinceGeoId}</#if>
                            <#if shippingAddress.postalCode?has_content><br/>${shippingAddress.postalCode}</#if>
                            <#if shippingAddress.countryGeoId?has_content><br/>${shippingAddress.countryGeoId}</#if>
                            <#--<a href="<@ofbizUrl>/editcontactmech?DONE_PAGE=checkoutoptions&contactMechId=${shippingAddress.contactMechId}</@ofbizUrl>" class="buttontext">[Update]</a>-->
                          </div>
                        </td>
                      </tr>
                    </#list>
                  </table>
                </td>
              </tr>
              <tr><td colspan="8"><hr class="sepbar"></td></tr>
              <tr>
                <td colspan="8" align="right">
                  <a href="javascript:document.returnItems.submit();" class="buttontext">Return Selected Item(s) </a>
                </td>
              </tr>
            <#else>
              <tr><td colspan="8"><div class="tabletext">No returnable items found for order #${orderId}</div></td></tr>
            </#if>
          </table>
        </form>
    </div>
</div>
    
