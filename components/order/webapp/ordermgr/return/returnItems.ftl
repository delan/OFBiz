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
 *@version    $Revision: 1.10 $
 *@since      2.2
-->

<div class='tabContainer'>
    <a href="<@ofbizUrl>/returnMain?returnId=${returnId?if_exists}</@ofbizUrl>" class="tabButton">Return Header</a>
    <a href="<@ofbizUrl>/returnItems?returnId=${returnId?if_exists}</@ofbizUrl>" class="tabButtonSelected">Return Items</a>
    <#if returnHeader?has_content && returnHeader.destinationFacilityId?has_content && returnHeader.statusId == "RETURN_ACCEPTED">
      <a href="/facility/control/ReceiveReturn?facilityId=${returnHeader.destinationFacilityId}&returnId=${returnHeader.returnId?if_exists}${requestAttributes.externalKeyParam}" class="tabButton">Receive Return</a>
    </#if>
</div>

<#if !requestParameters.orderId?exists>
<table width="100%" border='0' cellpadding='2' cellspacing='0'>
  <tr><td colspan="8"><div class="head3">Item(s) In Return #${returnId}</div></td></tr>
  <tr><td colspan="8"><hr class="sepbar"></td></tr>
  <tr>
    <td><div class="tableheadtext">Order #</div></td>
    <td><div class="tableheadtext">Item #</div></td>
    <td><div class="tableheadtext">Description</div></td>
    <td><div class="tableheadtext">Return Qty</div></td>
    <td><div class="tableheadtext">Return Price</div></td>
    <td><div class="tableheadtext">Reason</div></td>
    <td><div class="tableheadtext">Type</div></td> 
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="8"><hr class="sepbar"></td></tr> 
  <#if returnItems?has_content>
    <#list returnItems as item>
      <#assign orderItem = item.getRelatedOne("OrderItem")?if_exists>
      <#assign returnReason = item.getRelatedOne("ReturnReason")?if_exists>
      <#assign returnType = item.getRelatedOne("ReturnType")?if_exists>
      <tr>
        <td><a href="<@ofbizUrl>/orderview?order_id=${item.orderId}</@ofbizUrl>" class="buttontext">${item.orderId}</a></td>
        <td><div class="tabletext">${item.orderItemSeqId?default("N/A")}</div></td>
        <td><div class="tabletext">${item.description?default("N/A")}</div></td>
        <td><div class="tabletext">${item.returnQuantity?string.number}</div></td>
        <td><div class="tabletext">${item.returnPrice?string.currency}</div></td>
        <td><div class="tabletext">${returnReason.description?default("N/A")}</div></td>
        <td><div class="tabletext">${returnType.description?default("N/A")}</div></td>
        <#if returnHeader.statusId == "RETURN_REQUESTED">
          <td align='right'><a href="<@ofbizUrl>/removeReturnItem?returnId=${item.returnId}&returnItemSeqId=${item.returnItemSeqId}</@ofbizUrl>" class="buttontext">Remove</a>
        <#else>
          <td>&nbsp;</td>
        </#if>
      </tr>
    </#list>
  <#else>
    <tr>
      <td colspan="8"><div class="tabletext">No item(s) in return.</div>
    </tr>
  </#if>
</table>
<br>
<#if returnHeader.statusId == "RETURN_REQUESTED">
<form name="returnItems" method="post" action="<@ofbizUrl>/returnItems</@ofbizUrl>">
  <input type="hidden" name="returnId" value="${returnId}">
  <table border='0' cellpadding='2' cellspacing='0'>
    <tr><td colspan="4"><div class="head3">Return Item(s)</div></td></tr>
    <#if partyOrders?has_content>
      <tr>      
        <td width='25%' align='right' nowrap><div class='tableheadtext'>Order ID:</div></td>
        <td>&nbsp;</td>
        <td width='25%'>        
          <select name="orderId" class="selectBox">
            <#list partyOrders as order>
              <option>${order.orderId}</option>
            </#list>
          </select>
        </td>
        <td><div class='tabletext'>&nbsp;(Load order items for return)</div></td> 
      </tr>
    <#else>
      <tr>
        <td colspan="4" nowrap><div class='tableheadtext'>No orders found for partyId: <a href="/partymgr/control/viewprofile?party_id=${returnHeader.fromPartyId?default('_NA_')}${requestAttributes.externalKeyParam}" class="buttontext">${returnHeader.fromPartyId?default('[null]')}</a></div></td>
      </tr>
      <tr>
        <td width='25%' align='right' nowrap><div class='tableheadtext'>Order ID:</div></td>
        <td>&nbsp;</td>
        <td width='25%'>               
          <input type='text' name='orderId' size='20' maxlength='20' class="inputBox">
        </td>
        <td><div class='tabletext'>&nbsp;(Load order items for return)</div></td> 
      </tr>
    </#if>      
    <tr>
      <td colspan="2">&nbsp;</td>
      <td colspan="2">
        <a href="javascript:document.returnItems.submit();" class="buttontext">Load Order Items(s)</a>
      </td>
    </tr>
  </table>
</form>
</#if>
<#else>                            
<form name="returnItems" method="post" action="<@ofbizUrl>/createReturnItems</@ofbizUrl>">
  <input type="hidden" name="returnId" value="${returnId}">
  <input type="hidden" name="_useRowSubmit" value="Y">
  <table border='0' width='100%' cellpadding='2' cellspacing='0'>
    <tr>
      <td colspan="8"><div class="head3">Return Item(s) From Order #${requestParameters.orderId}</div></td>
      <td align="right">
        <span class="tableheadtext">Select All</span>&nbsp;
        <input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this);">
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
      <td>&nbsp;</td>  
    </tr>
    <tr><td colspan="8"><hr class="sepbar"></td></tr>
    <#if returnableItems?has_content>
      <#assign rowCount = 0>
      <#list returnableItems.keySet() as orderItem>     
      <input type="hidden" name="returnItemType_o_${rowCount}" value="ITEM">
      <input type="hidden" name="returnId_o_${rowCount}" value="${returnId}">
      <input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}">
      <input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}">
      <input type="hidden" name="description_o_${rowCount}" value="${orderItem.itemDescription?if_exists}">
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
          <input type="text" class="inputBox" size="6" name="returnQuantity_o_${rowCount}" value="${returnableItems.get(orderItem).get("returnableQuantity")}">
        </td>
        <td align='left'>
          <div class="tabletext">${orderItem.unitPrice?string.currency}</div>
        </td>
        <td>
          <input type="text" class="inputBox" size="8" name="returnPrice_o_${rowCount}" value="${returnableItems.get(orderItem).get("returnablePrice")?string("##0.00")}">
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
      <tr><td colspan="8"><hr class="sepbar"></td></tr>  
      <#assign rowCount = rowCount + 1>        
      </#list>

      <#-- shipping 'refund' -->
      <input type="hidden" name="returnItemType_o_${rowCount}" value="ADJUSTMENT">
      <input type="hidden" name="returnId_o_${rowCount}" value="${returnId}">
      <input type="hidden" name="orderId_o_${rowCount}" value="${requestParameters.orderId}">    
      <input type="hidden" name="returnQuantity_o_${rowCount}" value="1">      
      <input type="hidden" name="description_o_${rowCount}" value="Shipping Adjustment">
      <tr>
        <td><div class='tabletext'>Shipping Amount</div></td>
        <td align='center'><div class='tabletext'>-</div></td>
        <td align='center'><div class='tabletext'>-</div></td>
        <td><div class='tabletext'>${shippingAmount?string.currency}</div></td>
        <td>
          <input type="text" class="inputBox" size="8" name="returnPrice_o_${rowCount}" value="${shippingAmount?string("##0.00")}">
        </td>
        <td align="center"><div class='tabletext'>-</div></td>
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
      <tr><td colspan="8"><hr class="sepbar"></td></tr>  
      <input type="hidden" name="_rowCount" value="${rowCount+1}">
      <tr>
        <td colspan="7" align="right">
          <a href="javascript:document.returnItems.submit();" class="buttontext">Return Selected Item(s)</a>
        </td>
      </tr>      
    <#else>
      <tr><td colspan="7"><div class="tabletext">No items found for order #${requestParameters.orderId}</div></td></tr>
    </#if>
    <tr>
      <td colspan="7"><div class="tabletext">*Price includes tax & adjustments</div></td>
  </table>
</form>
</#if>