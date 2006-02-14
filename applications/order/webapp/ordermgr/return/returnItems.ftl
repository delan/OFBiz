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

<!-- if we're called with loadOrderItems or createReturn, then orderId would exist -->
<#if !requestParameters.orderId?exists>
<table width="100%" border='0' cellpadding='2' cellspacing='0'>
  <tr><td colspan="10"><div class="head3">${uiLabelMap.OrderItemsReturned} ${uiLabelMap.CommonIn} ${uiLabelMap.OrderOrderReturn} #${returnId}</div></td></tr>
  <tr><td colspan="10"><hr class="sepbar"></td></tr>
  <tr>
    <td><div class="tableheadtext">${uiLabelMap.OrderOrderItems}</div></td>
    <td><div class="tableheadtext">Product Id</div></td>
    <td><div class="tableheadtext">${uiLabelMap.CommonDescription}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.OrderQuantity}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.OrderPrice}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.OrderSubTotal}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.OrderReturnReason}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.OrderItemStatus}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.CommonType}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.OrderReturnResponse}</div></td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="10"><hr class="sepbar"></td></tr>
  <#assign returnTotal = 0.0>
  <#assign rowCount = 0>
  <#assign readOnly = (returnHeader.statusId != "RETURN_REQUESTED")>
  <#if returnItems?has_content>
    <form method="post" action="<@ofbizUrl>updateReturnItems</@ofbizUrl>">
    <#list returnItems as item>
      <#assign orderItem = item.getRelatedOne("OrderItem")?if_exists>
      <#assign orderHeader = item.getRelatedOne("OrderHeader")?if_exists>
      <#assign returnReason = item.getRelatedOne("ReturnReason")?if_exists>
      <#assign returnType = item.getRelatedOne("ReturnType")?if_exists>
      <#assign status = item.getRelatedOne("InventoryStatusItem")?if_exists>
      <#if (item.get("returnQuantity")?exists && item.get("returnPrice")?exists)>
         <#assign returnTotal = returnTotal + item.get("returnQuantity") * item.get("returnPrice") >
         <#assign returnItemSubTotal = item.get("returnQuantity") * item.get("returnPrice") >
      <#else>
         <#assign returnItemSubTotal = null >  <#-- otherwise the last item's might carry over -->
      </#if>

      <tr>
        <td class="tabletext"><a href="<@ofbizUrl>orderview?orderId=${item.orderId}</@ofbizUrl>" class="buttontext">${item.orderId}</a> - ${item.orderItemSeqId?default("N/A")}</td>
          <input name="orderId_o_${rowCount}" value="${item.orderId}" type="hidden">
          <input name="returnId_o_${rowCount}" value="${item.returnId}" type="hidden">
          <input name="returnItemTypeId_o_${rowCount}" value="${item.returnItemTypeId}" type="hidden">
          <input name="returnItemSeqId_o_${rowCount}" value="${item.returnItemSeqId}" type="hidden">
        <td><div class="tabletext">
            <#if item.get("productId")?exists>
                <a href="/catalog/control/EditProductInventoryItems?productId=${item.productId}" class="buttontext">${item.productId}</a>
            <#else>
                N/A
            </#if></div></td>
        <td><div class="tabletext">
            <#if readOnly>
                ${item.description?default("N/A")}            
            <#else>
                <input name="description_o_${rowCount}" value="${item.description}" type="text" class='inputBox' size="15">
            </#if>
            </div></td>
        <td><div class="tabletextright">
            <#if readOnly>
                ${item.returnQuantity?string.number}
            <#else>
                <input name="returnQuantity_o_${rowCount}" value="${item.returnQuantity}" type="text" class='inputBox' size="8" align="right">
            </#if>
            </div></td>
        <td><div class="tabletextright">
            <#if readOnly>
                <@ofbizCurrency amount=item.returnPrice isoCode=orderHeader.currencyUom/>
            <#else>
                <input name="returnPrice_o_${rowCount}" value="${item.returnPrice}" type="text" class='inputBox' size="8" align="right">
            </#if>
            </div></td>
        <td class="tabletextright">
            <#if returnItemSubTotal?exists><@ofbizCurrency amount=returnItemSubTotal isoCode=orderHeader.currencyUom/></#if>
        </td>
        <td><div class="tabletext">
            <#if readOnly>
                ${returnReason.description?default("N/A")}
            <#else>
                <select name="returnReasonId_o_${rowCount}"  class='selectBox'>
                    <#if (returnReason?has_content)>
                        <option value="${returnReason.returnReasonId}">${returnReason.description?if_exists}</option>
                        <option value="${returnReason.returnReasonId}">--</option>
                    </#if>
                    <#list returnReasons as returnReasonItem>
                        <option value="${returnReasonItem.returnReasonId}">${returnReasonItem.description?if_exists}</option>
                    </#list>
                </select>
            </#if>
            </div></td>
        <td><div class="tabletext">
          <#if readOnly>
              ${status.description?default("N/A")}
          <#else>
              <select name="expectedItemStatus_o_${rowCount}"  class='selectBox'>
                  <#if (status?has_content)>
                      <option value="${status.statusId}">${status.description?if_exists}</option>
                      <option value="${status.statusId}">--</option>
                  </#if>
                  <#list itemStatus as returnItemStatus>
                      <option value="${returnItemStatus.statusId}">${returnItemStatus.description?if_exists}</option>
                  </#list>
              </select>
          </#if>
          </div></td>
        <td><div class="tabletext">
            <#if (readOnly)>
                ${returnType.description?default("N/A")}
            <#else>
                <select name="returnTypeId_o_${rowCount}" class="selectBox">
                    <#if (returnType?has_content)>
                        <option value="${returnType.returnTypeId}">${returnType.description?if_exists}</option>
                        <option value="${returnType.returnTypeId}">--</option>
                    </#if>
                    <#list returnTypes as returnTypeItem>
                        <option value="${returnTypeItem.returnTypeId}">${returnTypeItem.description?if_exists}</option>
                    </#list>
                </select>
            </#if></div></td>
        <td>
          <#if returnHeader.statusId == "RETURN_COMPLETED">
            <#assign itemResp = item.getRelatedOne("ReturnItemResponse")?if_exists>
            <#if itemResp?has_content>
              <#if itemResp.paymentId?has_content>
                <div class="tabletext">Payment #<a href="/accounting/control/editPayment?paymentId=${itemResp.paymentId}${externalKeyParam}" class="buttontext">${itemResp.paymentId}</a></div>
              <#elseif itemResp.replacementOrderId?has_content>
                <div class="tabletext">Order #<a href="<@ofbizUrl>orderview?orderId=${itemResp.replacementOrderId}</@ofbizUrl>" class="buttontext">${itemResp.replacementOrderId}</a></div>
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
      <#assign rowCount = rowCount + 1>
    </#list>
        <input type="hidden" name="_rowCount" value="${rowCount}"/>
        
<#if (returnAdjustments?has_content)>                  
    <#assign rowCount = 0>
    <#list returnAdjustments as returnAdjustment>
        <#assign returnHeader = returnAdjustment.getRelatedOne("ReturnHeader")>
        <tr class="tabletext">
            <td class="tabletext" colspan="2">${uiLabelMap.OrderReturnAdjustmentForReturnItem}
            ${returnAdjustment.returnItemSeqId?default("N/A")}</td>
            <td colspan="3"><div class="tabletext">${returnAdjustment.description?default("N/A")}</div></td>
            <#if (!readOnly && !returnAdjustment.returnItemSeqId?has_content)>
              <td>
                 <input type="text" class="inputBox" size="8" name="amount_o_${rowCount}_adj" value="${adj.amount?string("##0.00")}"/>
              </td>
            <#else>
                <td class="tabletextright"><@ofbizCurrency amount=returnAdjustment.amount isoCode=returnHeader.currencyUom/></td>
            </#if>
            <#assign rowCount = rowCount + 1>
            <#assign returnTotal = returnTotal + returnAdjustment.get("amount")>
        </tr>    
    </#list>
          <input name="_rowCount_adj" value="${rowCount}" type="hidden">
    </#if>
    <#-- show the return total -->
    <tr><td colspan="5"></td><td><hr class="sepbar"/></td></tr>
    <tr>
      <td colspan="2">&nbsp;</td>
      <td colspan="3" class="tableheadtext">${uiLabelMap.OrderReturnTotal}</td>
      <td class="tabletextright"><b><@ofbizCurrency amount=returnTotal isoCode=orderHeader.currencyUom/></b></td>
    </tr>
    <#if (!readOnly)>
       <tr>
          
          <input name="returnId" value="${returnHeader.returnId}" type="hidden">
          <td colspan="7" class="tabletext" align="center"><input type="submit" class="bottontext" value="${uiLabelMap.CommonUpdate}"></td>
      </tr>
  </form>
   </#if>        
  <#else>
    <tr>
      <td colspan="9"><div class="tabletext">No item(s) in return.</div></td>
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
<!-- if no requestParameters.orderId exists, then show list of items -->
<#else>
<#assign selectAllFormName = "returnItems"/>
<form name="returnItems" method="post" action="<@ofbizUrl>createReturnItems</@ofbizUrl>">
  <input type="hidden" name="returnId" value="${returnId}">
  <input type="hidden" name="_useRowSubmit" value="Y">
  <#include "returnItemInc.ftl"/>
</form>
    
</#if>
