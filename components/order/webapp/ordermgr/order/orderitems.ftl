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
 *@version    $Revision: 1.2 $
 *@since      2.2
-->

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Order Items</div>
          </td>
          <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED">
          <td valign="middle" align="right">
            <div class="tabletext"><a href="<@ofbizUrl>/changeOrderItemStatus?orderId=${orderId}&statusId=ITEM_CANCELLED&${paramString}</@ofbizUrl>" class="submenutextright">Cancel All Items</a></div>
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
            <table width="100%" border="0" cellpadding="0" cellspacing='0'>
              <tr align=left valign=bottom>
                <td width="30%" align="left"><div class="tableheadtext">Product</div></td>
                <td width="30%" align="left"><div class="tableheadtext">Status</div></td>
                <td width="5%" align="right"><div class="tableheadtext">Quantity</div></td>
                <td width="10%" align="right"><div class="tableheadtext">Unit / List</div></td>
                <td width="10%" align="right"><div class="tableheadtext">Adjustments</div></td>
                <td width="10%" align="right"><div class="tableheadtext">Subtotal</div></td>
                <td width="5%">&nbsp;</td>                
              </tr>
              <#if !orderItemList?has_content>                           
                <tr><td><font color="red">ERROR: Sales Order Lines lookup failed.</font></td></tr>
              <#else>
                <#list orderItemList as orderItem>
                  <tr><td colspan="8"><hr class='sepbar'></td></tr>
                  <tr>
                    <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")>
                    <#assign productId = orderItem.productId?if_exists>
                    <#if productId?exists && productId == "shoppingcart.CommentLine">
                      <td colspan="1" valign="top">    
                        <b><div class="tabletext"> &gt;&gt; ${orderItem.itemDescription}</div></b>
                      </td>
                    <#else>              
                      <td valign="top">
                        <div class="tabletext">
                          <#if productId?exists>
                            ${orderItem.productId?default("N/A")} - ${orderItem.itemDescription?if_exists}
                          <#else>
                            ${orderItemType.description} - ${orderItem.itemDescription?if_exists}
                          </#if>
                        </div>
                        <#if productId?exists>
                          <div class="tabletext">
                            <a href="/catalog/control/EditProduct?productId=${productId}" class="buttontext" target='_blank'>[catalog]</a>
                            <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target='_blank'>[ecommerce]</a>
                          </div>
                        </#if>
                      </td>
                      
                      <#-- now show status details per line item -->
                      <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>                    
                      <td align="left" colspan="1">                                                                                            
                        <div class='tabletext'>Current: ${currentItemStatus.description?default(currentItemStatus.statusId)}</div>  
                        <#assign orderItemStatuses = orderReadHelper.getOrderItemStatuses(orderItem)>
                        <#list orderItemStatuses as orderItemStatus>                     
                          <#assign loopStatusItem = orderItemStatus.getRelatedOne("StatusItem")>                            
                          <div class='tabletext'>
                            ${orderItemStatus.statusDatetime.toString()} : ${loopStatusItem.description?default(orderItemStatus.statusId)}
                          </div>
                        </#list>
                      </td>
                      <td align="center" valign="top" nowrap>
                        <div class="tabletext" nowrap>${orderItem.quantity?string.number}&nbsp;</div>
                      </td>
                      <td align="right" valign="top" nowrap>
                        <div class="tabletext" nowrap>${orderItem.unitPrice?string.currency} / ${orderItem.unitListPrice?string.currency}</div>
                      </td>
                      <td align="right" valign="top" nowrap>
                        <div class="tabletext" nowrap>${Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false)?string.currency}</div>
                      </td>
                      <td align="right" valign="top" nowrap>
                        <div class="tabletext" nowrap>${Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?string.currency}</div>
                      </td>
                      <td>&nbsp;</td>                      
                      <td align="right" valign="top" nowrap>
                        <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && orderItem.statusId != "ITEM_CANCELLED" && orderItem.statusId != "ITEM_COMPLETED">
                          <div class="tabletext"><a href="<@ofbizUrl>/changeOrderItemStatus?orderId=${orderId}&orderItemSeqId=${orderItem.orderItemSeqId}&statusId=ITEM_CANCELLED&${paramString}</@ofbizUrl>" class="buttontext">[Cancel]</a></div>
                        <#else>
                          &nbsp;
                        </#if>
                      </td>                      
                    </#if>
                  </tr>
                  
                  <#-- now show adjustment details per line item -->
                  <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)>
                  <#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
                    <#list orderItemAdjustments as orderItemAdjustment>
                      <#assign adjustmentType = orderItemAdjustment.getRelatedOne("OrderAdjustmentType")>
                      <tr>
                        <td align="right" colspan="2">
                          <div class="tabletext" style='font-size: xx-small;'><b><i>Adjustment</i>:</b> <b>${adjustmentType.description}</b> : ${orderItemAdjustment.description?if_exists} (${orderItemAdjustment.comments?default("")})</div>
                        </td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td align="right">
                          <div class="tabletext" style='font-size: xx-small;'>
                            ${Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderItemAdjustment, orderItem)?string.currency}
                          </div>
                         </td>
                        <td>&nbsp;</td>
                      </tr>
                    </#list>
                  </#if>

                  <#-- now show price info per line item -->
                  <#assign orderItemPriceInfos = orderReadHelper.getOrderItemPriceInfos(orderItem)>
                  <#if orderItemPriceInfos?exists && orderItemPriceInfos?has_content>
                    <tr><td>&nbsp;</td></tr>
                    <#list orderItemPriceInfos as orderItemPriceInfo>                      
                      <tr>
                        <td align="right" colspan="2">
                          <div class="tabletext" style='font-size: xx-small;'><b><i>Price Rule</i>:</b> [${orderItemPriceInfo.productPriceRuleId}:${orderItemPriceInfo.productPriceActionSeqId}] ${orderItemPriceInfo.description?if_exists}</div>
                        </td>
                        <td>&nbsp;</td>
                        <td align="right">
                          <div class="tabletext" style='font-size: xx-small;'>
                            ${orderItemPriceInfo.modifyAmount?string.currency}
                          </div>
                        </td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                    </#list>
                  </#if>

                  <#-- now show inventory reservation info per line item -->
                  <#assign orderItemInventoryReses = orderReadHelper.getOrderItemInventoryReses(orderItem)>                  
                  <#if orderItemInventoryReses?exists && orderItemInventoryReses?has_content>
                    <#list orderItemInventoryReses as orderItemInventoryRes>
                      <tr>
                        <td align="right" colspan="2">
                          <div class="tabletext" style='font-size: xx-small;'>
                            <b><i>Inventory</i>:</b>
                              <a href='/facility/control/EditInventoryItem?inventoryItemId=${orderItemInventoryRes.inventoryItemId}&externalLoginKey=${requestAttributes.externalLoginKey}' class='buttontext' style='font-size: xx-small;'>${orderItemInventoryRes.inventoryItemId}</a>
                          </div>
                        </td>
                        <td align="center">
                          <div class="tabletext" style='font-size: xx-small;'>${orderItemInventoryRes.quantity?string.number}&nbsp;</div>
                        </td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                    </#list>
                  </#if>
                  
                  <#-- now show item issuances per line item -->
                  <#assign itemIssuances = itemIssuancesPerItem.get(orderItem.get("orderItemSeqId"))?if_exists>
                  <#if itemIssuances?has_content>
                    <#list itemIssuances as itemIssuance>
                      <tr>
                        <td align="right" colspan="2">
                          <div class="tabletext" style='font-size: xx-small;'>
                            <b><i>Issued to Shipment Item</i>:</b>
                              <a target='facility' href='/facility/control/ViewShipment?shipmentId=${itemIssuance.shipmentId}&externalLoginKey=${requestAttributes.externalLoginKey}' class='buttontext' style='font-size: xx-small;'>${itemIssuance.shipmentId}</a>:${itemIssuance.shipmentItemSeqId}
                          </div>
                        </td>
                        <td align="center">
                          <div class="tabletext" style='font-size: xx-small;'>${itemIssuance.quantity?string.number}&nbsp;</div>
                        </td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                    </#list>
                  </#if>
                </#list>
              </#if>
              <tr><td colspan="8"><hr class='sepbar'></td></tr>
              <#list orderHeaderAdjustments as orderHeaderAdjustment>
                <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
                <tr>
                  <td align="right" colspan="5">
                    <div class="tabletext"><b>${adjustmentType.description}</b> : ${orderHeaderAdjustment.comments?if_exists}</div>
                  </td>                       
                  <td align="right" nowrap>
                    <div class="tabletext">${Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)?string.currency}</div>
                  </td>
                  <td>&nbsp;</td>                        
                </tr>
              </#list>
              
              <#-- add new adjustment -->
              <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_REJECTED">                              
                <tr>                  
                  <form name="addAdjustmentForm" method="post" action="<@ofbizUrl>/addOrderAdjustment?${paramString}</@ofbizUrl>">
                    <input type="hidden" name="orderId" value="${orderId}">
                    <input type="hidden" name="comments" value="Added manually by '${userLogin.userLoginId}'">
                    <td align="right" colspan="5">
                      <select name="orderAdjustmentTypeId" class="selectBox">
                        <#list orderAdjustmentTypes as type>
                          <option value="${type.orderAdjustmentTypeId}">${type.description?default(type.orderAdjustmentTypeId)}</option>
                        </#list>
                      </select>
                    </td>
                    <td align="right">
                      <input type="text" name="amount" size="6" value="0.00" class="inputBox">
                    </td>
                    <td align="right" colspan="2">
                      <a href="javascript:document.addAdjustmentForm.submit();" class="buttontext">[Add]</a>
                    </td>
                  </form>
                </tr>
              </#if>
              
              <#-- subtotal -->
              <tr><td colspan=1></td><td colspan="8"><hr class='sepbar'></td></tr>
              <tr>
                <td align="right" colspan="5"><div class="tabletext"><b>Items Subtotal</b></div></td>
                <td align="right" nowrap><div class="tabletext">${orderSubTotal?string.currency}</div></td>
              </tr>
              
              <#-- other adjustments -->
              <tr>
                <td align="right" colspan="5"><div class="tabletext"><b>Total Other Order Adjustments</b></div></td>
                <td align="right" nowrap><div class="tabletext">${otherAdjAmount?string.currency}</div></td>
              </tr>
              
              <#-- shipping adjustments -->
              <tr>
                <td align="right" colspan="5"><div class="tabletext"><b>Total Shipping and Handling</b></div></td>
                <td align="right" nowrap><div class="tabletext">${shippingAmount?string.currency}</div></td>
              </tr>  
              
              <#-- tax adjustments -->
              <tr>
                <td align="right" colspan="5"><div class="tabletext"><b>Total Sales Tax</b></div></td>                    
                <td align="right" nowrap><div class="tabletext">${taxAmount?string.currency}</div></td>
              </tr>   
              
              <#-- grand total -->
              <tr>
                <td align="right" colspan="5"><div class="tabletext"><b>Total Due</b></div></td>
                <td align="right" nowrap>
                  <div class="tabletext">${grandTotal?string.currency}</div>
                </td>
              </tr>                       
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>                