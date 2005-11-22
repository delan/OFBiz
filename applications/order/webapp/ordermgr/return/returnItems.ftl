<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@since      2.2
-->

<div class='tabContainer'>
    <a href="<@ofbizUrl>returnMain?returnId=${returnId?if_exists}</@ofbizUrl>" class="tabButton">Return Header</a>
    <a href="<@ofbizUrl>returnItems?returnId=${returnId?if_exists}</@ofbizUrl>" class="tabButtonSelected">Return Items</a>
    <#if returnHeader?has_content && returnHeader.destinationFacilityId?has_content && returnHeader.statusId == "RETURN_ACCEPTED">
      <a href="/facility/control/ReceiveReturn?facilityId=${returnHeader.destinationFacilityId}&returnId=${returnHeader.returnId?if_exists}${externalKeyParam}" class="tabButton">Receive Return</a>
    </#if>
</div>
<div>
    <a href="<@ofbizUrl>return.pdf?returnId=${returnId?if_exists}</@ofbizUrl>" class="buttontext">PDF</a>
</div>
<#if !requestParameters.orderId?exists>
<table width="100%" border='0' cellpadding='2' cellspacing='0'>
  <tr><td colspan="9"><div class="head3">Item(s) In Return #${returnId}</div></td></tr>
  <tr><td colspan="9"><hr class="sepbar"></td></tr>
  <tr>
    <td><div class="tableheadtext">Order #</div></td>
    <td><div class="tableheadtext">Item #</div></td>
    <td><div class="tableheadtext">Description</div></td>
    <td><div class="tableheadtext">Return Qty</div></td>
    <td><div class="tableheadtext">Return Price</div></td>
    <td><div class="tableheadtext">Reason</div></td>
    <td><div class="tableheadtext">Type</div></td>
    <td><div class="tableheadtext">Response</div></td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="9"><hr class="sepbar"></td></tr>
  <#if returnItems?has_content>
    <#list returnItems as item>
      <#assign orderItem = item.getRelatedOne("OrderItem")?if_exists>
      <#assign orderHeader = item.getRelatedOne("OrderHeader")?if_exists>
      <#assign returnReason = item.getRelatedOne("ReturnReason")?if_exists>
      <#assign returnType = item.getRelatedOne("ReturnType")?if_exists>
      <tr>
        <td><a href="<@ofbizUrl>orderview?order_id=${item.orderId}</@ofbizUrl>" class="buttontext">${item.orderId}</a></td>
        <td><div class="tabletext">${item.orderItemSeqId?default("N/A")}</div></td>
        <td><div class="tabletext">${item.description?default("N/A")}</div></td>
        <td><div class="tabletext">${item.returnQuantity?string.number}</div></td>
        <td><div class="tabletext"><@ofbizCurrency amount=item.returnPrice isoCode=orderHeader.currencyUom/></div></td>
        <td><div class="tabletext">${returnReason.description?default("N/A")}</div></td>
        <td><div class="tabletext">${returnType.description?default("N/A")}</div></td>
        <td>
          <#if returnHeader.statusId == "RETURN_COMPLETED">
            <#assign itemResp = item.getRelatedOne("ReturnItemResponse")?if_exists>
            <#if itemResp?has_content>
              <#if itemResp.paymentId?has_content>
                <div class="tabletext">Payment #<a href="/accounting/control/editPayment?paymentId=${itemResp.paymentId}${externalKeyParam}" class="buttontext">${itemResp.paymentId}</a></div>
              <#elseif itemResp.replacementOrderId?has_content>
                <div class="tabletext">Order #<a href="<@ofbizUrl>orderview?order_id=${itemResp.replacementOrderId}</@ofbizUrl>" class="buttontext">${itemResp.replacementOrderId}</a></div>
              <#elseif itemResp.billingAccountId?has_content>
                <div class="tabletext">Acct #<a href="/accounting/control/EditBillingAccount?billingAccountId=${itemResp.billingAccountId}${externalKeyParam}" class="buttontext">${itemResp.billingAccountId}</a></div>
              </#if>
            <#else>
              <div class="tabletext">None</div>
            </#if>
          <#else>
            <div class="tabletext">N/A</div>
          </#if>
        </td>
        <#if returnHeader.statusId == "RETURN_REQUESTED">
          <td align='right'><a href="<@ofbizUrl>removeReturnItem?returnId=${item.returnId}&returnItemSeqId=${item.returnItemSeqId}</@ofbizUrl>" class="buttontext">Remove</a>
        <#else>
          <td>&nbsp;</td>
        </#if>
      </tr>
    </#list>
  <#else>
    <tr>
      <td colspan="9"><div class="tabletext">No item(s) in return.</div>
    </tr>
  </#if>
</table>
<#if returnHeader.statusId == "RETURN_REQUESTED">
<br/>
<form name="acceptReturn" method="post" action="<@ofbizUrl>/updateReturn</@ofbizUrl>">
  <input type="hidden" name="returnId" value="${returnId}">
  <input type="hidden" name="statusId" value="RETURN_ACCEPTED">
  <input type="hidden" name="currentStatusId" value="${returnHeader.statusId?if_exists}">
  <div class="tabletext" align="right"><input type="submit" value="Accept Return"></div>
</form>
</#if>

<#if returnHeader.statusId == "RETURN_REQUESTED">
<br/>
<form name="returnItems" method="post" action="<@ofbizUrl>returnItems</@ofbizUrl>">
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
        <td colspan="4" nowrap><div class='tableheadtext'>No orders found for partyId: <a href="/partymgr/control/viewprofile?partyId=${returnHeader.fromPartyId?default('_NA_')}" class="buttontext">${returnHeader.fromPartyId?default('[null]')}</a></div></td>
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
<#assign selectAllFormName = "returnItems"/>
<form name="returnItems" method="post" action="<@ofbizUrl>createReturnItems</@ofbizUrl>">
  <input type="hidden" name="returnId" value="${returnId}">
  <input type="hidden" name="_useRowSubmit" value="Y">
  <#include "returnItemInc.ftl"/>
</form>
</#if>
