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
 *@version    $Revision: 1.1 $
 *@since      2.2
-->

<script language="JavaScript">
<!--
function toggle(e) {
    e.checked = !e.checked;    
}
function checkToggle(e) {
    var cform = document.returnItems;
    if (e.checked) {      
        var len = cform.elements.length;
        var allchecked = true;
        for (var i = 0; i < len; i++) {
            var element = cform.elements[i];
            var elementName = new java.lang.String(element.name);          
            if (elementName.startsWith("_rowSubmit") && !element.checked) {       
                allchecked = false;
            }
            cform.selectAll.checked = allchecked;            
        }
    } else {
        cform.selectAll.checked = false;
    }
}
function toggleAll(e) {
    var cform = document.returnItems;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];                   
        var eName = new java.lang.String(element.name);                
        if (eName.startsWith("_rowSubmit") && element.checked != e.checked) {
            toggle(element);
        } 
    }     
}
function selectAll() {
    var cform = document.returnItems;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];                   
        var eName = new java.lang.String(element.name);                
        if ((element.name == "selectAll" || eName.startsWith("_rowSubmit")) && !element.checked) {
            toggle(element);
        } 
    }     
}
function removeSelected() {
    var cform = document.returnItems;
    cform.removeSelected.value = true;
    cform.submit();
}
//-->
</script>

<div class='tabContainer'>
    <a href="<@ofbizUrl>/returnMain?returnId=${requestParameters.returnId?if_exists}</@ofbizUrl>" class="tabButton">Return Header</a>  
    <a href="<@ofbizUrl>/returnItems?returnId=${requestParameters.returnId?if_exists}</@ofbizUrl>" class="tabButtonSelected">Return Items</a>  
</div>

<#if !requestParameters.orderId?exists>
<table width="100%" border='0' cellpadding='2' cellspacing='0'>
  <tr><td colspan="8"><div class="head3">Item(s) In Return #${requestParameters.returnId}</div></td></tr>
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
      <#assign orderItem = item.getRelatedOne("OrderItem")>
      <#assign returnReason = item.getRelatedOne("ReturnReason")>
      <#assign returnType = item.getRelatedOne("ReturnType")>
      <tr>
        <td><a href="<@ofbizUrl>/orderview?order_id=${item.orderId}</@ofbizUrl>" class="buttontext">${item.orderId}</a></td>
        <td><div class="tabletext">${item.orderItemSeqId}</div></td>
        <td><div class="tabletext">${orderItem.itemDescription}</div></td>
        <td><div class="tabletext">${item.returnQuantity?string.number}</div></td>
        <td><div class="tabletext">${item.returnPrice?string.currency}</div></td>
        <td><div class="tabletext">${returnReason.description}</div></td>
        <td><div class="tabletext">${returnType.description}</div></td>
        <td align='right'><a href="<@ofbizUrl>/removeReturnItem?returnId=${item.returnId}&returnItemSeqId=${item.returnItemSeqId}</@ofbizUrl>" class="buttontext">Remove</a>
      </tr>
    </#list>
  <#else>
    <tr>
      <td colspan="8"><div class="tabletext">No item(s) in return.</div>
    </tr>
  </#if>
</table>
<br>
<form name="returnItems" method="post" action="<@ofbizUrl>/returnItems</@ofbizUrl>">
  <input type="hidden" name="returnId" value="${requestParameters.returnId}">
  <table border='0' cellpadding='2' cellspacing='0'>
    <tr><td colspan="4"><div class="head3">Return Item(s)</div></td></tr>
    <tr>
      <td width='25%' align='right' nowrap><div class='tableheadtext'>Order ID:</div></td>
      <td>&nbsp;</td>
      <td width='25%'>
        <input type='text' name='orderId' size='20' maxlength='20' class="inputBox">
      </td>
      <td><div class='tabletext'>&nbsp;(Load order items for return)</div></td> 
    </tr>
    <tr>
      <td colspan="2">&nbsp;</td>
      <td colspan="2">
        <a href="javascript:document.returnItems.submit();" class="buttontext">Load Order Items(s)</a>
      </td>
    </tr>
  </table>
</form>
<#else>                            
<form name="returnItems" method="post" action="<@ofbizUrl>/createReturnItems</@ofbizUrl>">
  <input type="hidden" name="returnId" value="${requestParameters.returnId}">
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
      <td><div class="tableheadtext">Order Price</div></td>
      <td><div class="tableheadtext">Return Price*</div></td>
      <td><div class="tableheadtext">Return Reason</div></td>
      <td><div class="tableheadtext">Return Type</div></td>
      <td>&nbsp;</td>  
    </tr>
    <tr><td colspan="8"><hr class="sepbar"></td></tr>
    <#if orderItems?has_content>
      <#assign rowCount = 0>
      <#list orderItems as orderItem>
      <input type="hidden" name="returnId_o_${rowCount}" value="${requestParameters.returnId}">
      <input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}">
      <input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}">
      <#-- need some order item information -->
      <#assign orderHeader = orderItem.getRelatedOne("OrderHeader")>
      <#assign itemCount = orderItem.quantity>
      <#assign itemPrice = orderItem.unitPrice>
      <#assign orh = Static["org.ofbiz.commonapp.order.order.OrderReadHelper"].getHelper(orderHeader)>
      <#assign totalItemTax = orh.getOrderItemTax(orderItem)>
      <#assign itemUnitTax = totalItemTax / itemCount>
      <#assign itemPriceWithTax = itemPrice + itemUnitTax>
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
          <input type="text" class="inputBox" size="6" name="returnQuantity_o_${rowCount}" value="${orderItem.quantity}">
        </td>
        <td align='left'>
          <div class="tabletext">${orderItem.unitPrice?string.currency}</div>
        </td>
        <td>
          <input type="text" class="inputBox" size="8" name="returnPrice_o_${rowCount}" value="${itemPriceWithTax?string("##0.00")}">
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
      <input type="hidden" name="_rowCount" value="${rowCount}">
      <tr>
        <td colspan="7" align="right">
          <a href="javascript:document.returnItems.submit();" class="buttontext">Return Selected Item(s)</a>
        </td>
      </tr>      
    <#else>
      <tr><td colspan="7"><div class="tabletext">No items found for order #${requestParameters.orderId}</div></td></tr>
    </#if>
    <tr>
      <td colspan="7"><div class="tabletext">*Price includes tax</div></td>
  </table>
</form>
</#if>