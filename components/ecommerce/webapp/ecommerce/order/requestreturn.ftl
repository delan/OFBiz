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
 *@version    $Revision: 1.3 $
 *@since      3.0
-->
<@assign uiLabelMap = requestAttributes.uiLabelMap>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.OrderReturnItems}</div>
          </td>
          <#if maySelectItems?default(false)>
            <td valign="middle" align="right" nowrap>
              <a href='javascript:document.addOrderToCartForm.add_all.value="true";document.addOrderToCartForm.submit()' class="submenutext">${uiLabelMap.OrderAddAlltoCart}</a><a href='javascript:document.addOrderToCartForm.add_all.value="false";document.addOrderToCartForm.submit()' class="submenutextright">${uiLabelMap.OrderAddCheckedToCart}</a>
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
                     
<form name="returnItems" method="post" action="<@ofbizUrl>/requestReturn</@ofbizUrl>">
  <#--<input type="hidden" name="returnId" value="${requestParameters.returnId}">-->
  <input type="hidden" name="_checkGlobalScope" value="Y">
  <input type="hidden" name="_useRowSubmit" value="Y">
  <input type="hidden" name="fromPartyId" value="${party.partyId}">
  <input type="hidden" name="order_id" value="${orderId}">  
  <table border='0' width='100%' cellpadding='2' cellspacing='0'>
    <tr>
      <td colspan="5"><div class="head3">${uiLabelMap.OrderReturnItemsFromOrder} #<a href="<@ofbizUrl>/orderstatus?order_id=${orderId}</@ofbizUrl>" class="buttontext">${orderId}</div></td>
      <td align="right">
        <span class="tableheadtext">${uiLabelMap.OrderSelectAll}</span>&nbsp;
        <input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this);">
      </td>      
    </tr>
    <tr>
      <td><div class="tableheadtext">${uiLabelMap.OrderDescription}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.OrderQuantity}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.OrderPrice}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.OrderReason}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.OrderRequestedResponse}</div></td>
      <td>&nbsp;</td>  
    </tr>
    <tr><td colspan="6"><hr class="sepbar"></td></tr>
    <#if returnableItems?has_content>
      <#assign rowCount = 0>
      <#list returnableItems.keySet() as orderItem>
      <#--<input type="hidden" name="returnId_o_${rowCount}" value="${requestParameters.returnId}">-->            
      <input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}">
      <input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}">
      <input type="hidden" name="description_o_${rowCount}" value="${orderItem.itemDescription?if_exists}">
      <input type="hidden" name="returnItemType_o_${rowCount}" value="ITEM">
      <#-- need some order item information -->
      <#assign orderHeader = orderItem.getRelatedOne("OrderHeader")>
      <#assign itemCount = orderItem.quantity>
      <#assign itemPrice = orderItem.unitPrice>
      <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
      <#assign totalItemTax = orh.getOrderItemTax(orderItem)>
      <#if 0 < itemCount && 0 < totalItemTax>
        <#assign itemUnitTax = totalItemTax / itemCount>
      <#else>
        <#assign itemUnitTax = 0>
      </#if>
      <#assign itemPriceWithTax = itemPrice + itemUnitTax>
      <#-- end of order item information -->
      <tr>       
        <td>
          <div class="tabletext">
            <#if orderItem.productId?exists>
            &nbsp;<a href="<@ofbizUrl>/product?product_id=${orderItem.productId}</@ofbizUrl>" class="buttontext">${orderItem.productId}</a>
            </#if>
            ${orderItem.itemDescription}
          </div>
        </td>               
        <td>
          <input type="text" class="inputBox" size="6" name="returnQuantity_o_${rowCount}" value="${returnableItems.get(orderItem)}">
        </td>
        <td align='left'>
          <div class="tabletext">${orderItem.unitPrice?string.currency}</div>
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
          <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this);">
        </td>        
      </tr>     
      <tr><td colspan="6"><hr class="sepbar"></td></tr>  
      <#assign rowCount = rowCount + 1>        
      </#list>
      <input type="hidden" name="_rowCount" value="${rowCount}">
      <tr>
        <td colspan="6"><div class='tableheadtext'>${uiLabelMap.OrderSelectShipFromAddress}:</td>
      </tr>
      <tr><td colspan="6"><hr class='sepbar'></td></tr>
      <tr>
        <td colspan="6">
          <table cellspacing="1" cellpadding="2" width="100%">
            <#list context.shippingContactMechList as shippingContactMech>
              <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
              <tr>
                <td align="right" width="1%" valign="top" nowrap>
                  <input type="radio" name="originContactMechId" value="${shippingAddress.contactMechId}">        
                </td>
                <td align="left" width="99%" valign="top" nowrap>
                  <div class="tabletext">
                    <#if shippingAddress.toName?has_content><b>${uiLabelMap.OrderTo}:</b>&nbsp;${shippingAddress.toName}<br></#if>
                    <#if shippingAddress.attnName?has_content><b>${uiLabelMap.OrderAttn}:</b>&nbsp;${shippingAddress.attnName}<br></#if>
                    <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br></#if>
                    <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br></#if>
                    <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
                    <#if shippingAddress.stateProvinceGeoId?has_content><br>${shippingAddress.stateProvinceGeoId}</#if>
                    <#if shippingAddress.postalCode?has_content><br>${shippingAddress.postalCode}</#if>
                    <#if shippingAddress.countryGeoId?has_content><br>${shippingAddress.countryGeoId}</#if>                                                            
                    <a href="<@ofbizUrl>/editcontactmech?DONE_PAGE=checkoutoptions&contactMechId=${shippingAddress.contactMechId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.OrderUpdate}]</a>
                  </div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr><td colspan="6"><hr class='sepbar'></td></tr>
      </#list>      
      <tr>
        <td colspan="6" align="right">
          <a href="javascript:document.returnItems.submit();" class="buttontext">${uiLabelMap.OrderReturnSelectedItems}</a>
        </td>
      </tr>      
    <#else>
      <tr><td colspan="6"><div class="tabletext">No returnable items found for order #${orderId}</div></td></tr>
      <tr><td colspan="6"><div class="tabletext">${uiLabelMap.OrderNoReturnableItems} #${orderId}</div></td></tr>
    </#if>    
  </table>
</form>

          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
