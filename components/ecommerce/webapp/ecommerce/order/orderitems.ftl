<#--
 *  Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Rev$
 *@since      2.1
-->

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp; ${requestAttributes.uiLabelMap.OrderOrderItems}</div>
          </td>
          <#if maySelectItems?default(false)>
            <td valign="middle" align="right" nowrap>
              <a href='javascript:document.addCommonToCartForm.add_all.value="true";document.addCommonToCartForm.submit()' class="submenutext">${requestAttributes.uiLabelMap.EcommerceAddAlltoCart}</a><a href='javascript:document.addCommonToCartForm.add_all.value="false";document.addCommonToCartForm.submit()' class="submenutextright">${requestAttributes.uiLabelMap.EcommerceAddCheckedToCart}</a>
            </td>
          </#if>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <table width="100%" border="0" cellpadding="0">
              <tr align='left' valign='bottom'>
                <td width="35%" align="left"><span class="tableheadtext"><b>${requestAttributes.uiLabelMap.EcommerceProduct}</b></span></td>               
                <#if maySelectItems?default(false)>
                <td width="10%" align="right"><span class="tableheadtext"><b>${requestAttributes.uiLabelMap.OrderQtyOrdered}</b></span></td>
                <td width="10%" align="right"><span class="tableheadtext"><b>${requestAttributes.uiLabelMap.OrderQtyShipped}</b></span></td>
                <td width="10%" align="right"><span class="tableheadtext"><b>${requestAttributes.uiLabelMap.OrderQtyCanceled}</b></span></td>
                <#else>
                <td width="10%" align="right">&nbsp;</td>
                <td width="10%" align="right">&nbsp;</td>
                <td width="10%" align="right"><span class="tableheadtext"><b>${requestAttributes.uiLabelMap.OrderQtyOrdered}</b></span></td>
                </#if>
                <td width="10%" align="right"><span class="tableheadtext"><b>${requestAttributes.uiLabelMap.EcommerceUnitPrice}</b></span></td>
                <td width="10%" align="right"><span class="tableheadtext"><b>${requestAttributes.uiLabelMap.OrderAdjustments}</b></span></td>
                <td width="10%" align="right"><span class="tableheadtext"><b>${requestAttributes.uiLabelMap.CommonSubtotal}</b></span></td>
                <#if maySelectItems?default(false)>
                <td width="5%" align="right">&nbsp;</td>
                </#if>
              </tr>
              <#list orderItems as orderItem>
                <tr><td colspan="10"><hr class='sepbar'></td></tr>
                <tr>
                  <#if orderItem.productId == "_?_">
                    <td colspan="1" valign="top">
                      <b><div class="tabletext"> &gt;&gt; ${orderItem.itemDescription}</div></b>
                    </td>
                  <#else>
                    <td valign="top">
                      <div class="tabletext">
                        <a href="<@ofbizUrl>/product?product_id=${orderItem.productId}</@ofbizUrl>" class="buttontext">${orderItem.productId} - ${orderItem.itemDescription}</a>
                      </div>
                      <#if maySelectItems?default(false)>
                        <#assign returns = orderItem.getRelated("ReturnItem")?if_exists>
                        <#if returns?has_content>
                          <#list returns as return>
                            <#assign returnHeader = return.getRelatedOne("ReturnHeader")>
                            <#if returnHeader.statusId != "RETURN_CANCELLED">
                              <#if returnHeader.statusId == "RETURN_REQUESTED" || returnHeader.statusId == "RETURN_APPROVED">
                                <#assign displayState = "Return Pending">
                              <#else>
                                <#assign displayState = "Returned">
                              </#if>
                              <div class='tabletext'><font color="red"><b>${displayState}</b></font> (#${return.returnId})</div>
                            </#if>
                          </#list>
                        </#if>
                      </#if>
                    </td>
                    <#if !maySelectItems?default(false)>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    </#if>
                    <td align="right" valign="top">
                      <div class="tabletext">${orderItem.quantity?string.number}</div>                        
                    </td>
                    <#if maySelectItems?default(false)>
                    <td align="right" valign="top">
                      <#assign shippedQty = localOrderReadHelper.getItemShippedQuantity(orderItem)>
                      <div class="tabletext" nowrap>${shippedQty?default(0)?string.number}</div>
                    </td>
                    <td align="right" valign="top">
                      <#assign canceledQty = localOrderReadHelper.getItemCanceledQuantity(orderItem)>
                      <div class="tabletext" nowrap>${canceledQty?default(0)?string.number}</div>
                    </td>
                    </#if>
                    <td align="right" valign="top">
                      <div class="tabletext" nowrap><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></div>
                    </td>
                    <td align="right" valign="top">
                      <div class="tabletext" nowrap><@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem) isoCode=currencyUomId/></div>
                    </td>
                    <td align="right" valign="top" nowrap>
                      <div class="tabletext"><@ofbizCurrency amount=localOrderReadHelper.getOrderItemTotal(orderItem) isoCode=currencyUomId/></div>
                    </td>                    
                    <#if maySelectItems?default(false)>
                      <td>&nbsp;</td>
                      <#if (orderHeader.statusId != "ORDER_SENT" && orderItem.statusId != "ITEM_COMPLETED" && orderItem.statusId != "ITEM_CANCELLED")>
                        <td><a href="<@ofbizUrl>/cancelOrderItem?order_id=${orderItem.orderId}&item_seq=${orderItem.orderItemSeqId}</@ofbizUrl>" class="buttontext">${requestAttributes.uiLabelMap.CommonCancel}</a></td>
                      <#else>
                        <td>&nbsp;</td>
                      </#if>
                      <td>
                        <input name="item_id" value="${orderItem.orderItemSeqId}" type="checkbox">
                      </td>
                    </#if>
                  </#if>
                </tr>

                <#-- now show adjustment details per line item -->
                <#assign itemAdjustments = localOrderReadHelper.getOrderItemAdjustments(orderItem)>
                <#list itemAdjustments as orderItemAdjustment>
                  <tr>
                    <td align="right">
                      <div class="tabletext" style='font-size: xx-small;'>
                        <b><i>${requestAttributes.uiLabelMap.EcommerceAdjustment}</i>:</b> <b>${localOrderReadHelper.getAdjustmentType(orderItemAdjustment)}</b>&nbsp;
                        <#if orderItemAdjustment.description?has_content>: ${orderItemAdjustment.description}</#if>
                      </div>
                    </td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td align="right">
                      <div class="tabletext" style='font-size: xx-small;'><@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment) isoCode=currencyUomId/></div>
                    </td>
                    <td>&nbsp;</td>
                    <#if maySelectItems?default(false)><td>&nbsp;</td></#if>
                  </tr>
                </#list>

                <#-- show the order item ship group info -->
                <#assign orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc")?if_exists>
                <#if orderItemShipGroupAssocs?has_content>
                  <#list orderItemShipGroupAssocs as shipGroupAssoc>
                    <#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup")>
                    <#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress")>
                    <tr>
                      <td align="right">
                        <div class="tabletext" style="font-size: xx-small;"><b><i>Ship Group</i>:</b> [${shipGroup.shipGroupSeqId}] ${shipGroupAddress.address1}</div>
                      </td>
                      <td align="right">
                        <div class="tabletext" style="font-size: xx-small;">${shipGroupAssoc.quantity?string.number}</div>
                      </td>
                      <td>&nbsp;</td>
                      <td>&nbsp;</td>
                      <td>&nbsp;</td>
                      <td>&nbsp;</td>
                      <td>&nbsp;</td>
                    </tr>
                  </#list>
                </#if>

               </#list>
               <#if orderItems?size == 0 || !orderItems?has_content>
                 <tr><td><font color="red">${requestAttributes.uiLabelMap.OrderSalesOrderLookupFailed}.</font></td></tr>
               </#if>

              <tr><td colspan="10"><hr class='sepbar'></td></tr>
              <tr>
                <td align="right" colspan="6"><div class="tabletext"><b>${requestAttributes.uiLabelMap.CommonSubtotal}</b></div></td>
                <td align="right" nowrap><div class="tabletext"><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></div></td>
              </tr>
              <#list headerAdjustmentsToShow as orderHeaderAdjustment>
                <tr>
                  <td align="right" colspan="6"><div class="tabletext"><b>${localOrderReadHelper.getAdjustmentType(orderHeaderAdjustment)}</b></div></td>
                  <td align="right" nowrap><div class="tabletext"><@ofbizCurrency amount=localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId/></div></td>
                </tr>
              </#list>
              <tr>
                <td align="right" colspan="6"><div class="tabletext"><b>${requestAttributes.uiLabelMap.OrderShippingAndHandling}</b></div></td>
                <td align="right" nowrap><div class="tabletext"><@ofbizCurrency amount=orderShippingTotal isoCode=currencyUomId/></div></td>
              </tr>
              <tr>
                <td align="right" colspan="6"><div class="tabletext"><b>${requestAttributes.uiLabelMap.OrderSalesTax}</b></div></td>
                <td align="right" nowrap><div class="tabletext"><@ofbizCurrency amount=orderTaxTotal isoCode=currencyUomId/></div></td>
              </tr>

              <tr><td colspan=2></td><td colspan="9"><hr class='sepbar'></td></tr>
              <tr>
                <td align="right" colspan="6"><div class="tabletext"><b>${requestAttributes.uiLabelMap.OrderGrandTotal}</b></div></td>
                <td align="right" nowrap>
                  <div class="tabletext"><@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/></div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
