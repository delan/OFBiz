<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#macro displayReturnAdjustment returnAdjustment adjEditable>
    <#assign returnHeader = returnAdjustment.getRelatedOne("ReturnHeader")>
    <#assign adjReturnType = returnAdjustment.getRelatedOne("ReturnType")?if_exists>
    <#if (adjEditable)>
        <input type="hidden" name="_rowSubmit_o_${rowCount}" value="Y" />
        <input type="hidden" name="returnAdjustmentId_o_${rowCount}" value="${returnAdjustment.returnAdjustmentId}" />
    </#if>
    <tr>
        <td colspan="2">&nbsp;</td>
        <td colspan="3" align="right"><span class="label">${returnAdjustment.get("description",locale)?default("N/A")}</span>
            <#if returnAdjustment.comments?has_content>: ${returnAdjustment.comments}</#if>
        </div></td>
        <#if (adjEditable)>
           <td align="right">
              <input type="text" size="8" name="amount_o_${rowCount}" value="${returnAdjustment.amount?default(0)?string("##0.00")}"/>
           </td>
        <#else>
           <td align="right"><@ofbizCurrency amount=returnAdjustment.amount?default(0) isoCode=returnHeader.currencyUomId/></td>
        </#if>
        <td colspan="2">&nbsp;</td>
        <td><div>
           <#if (!adjEditable)>
               ${adjReturnType.description?default("${uiLabelMap.CommonNA}")}
           <#else>
               <select name="returnTypeId_o_${rowCount}">
                  <#if (adjReturnType?has_content)>
                    <option value="${adjReturnType.returnTypeId}">${adjReturnType.get("description",locale)?if_exists}</option>
                    <option value="${adjReturnType.returnTypeId}">--</option>
                  </#if>
                  <#list returnTypes as returnTypeItem>
                    <option value="${returnTypeItem.returnTypeId}">${returnTypeItem.get("description",locale)?if_exists}</option>
                  </#list>
                </select>
          </#if>
          </div>
       </td>
       <#if (adjEditable)>
       <td align='right'><a href="<@ofbizUrl>removeReturnAdjustment?returnAdjustmentId=${returnAdjustment.returnAdjustmentId}&returnId=${returnAdjustment.returnId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonRemove}</a></td>
       <#else>
       <td>&nbsp;</td>
       </#if>
        <#if (adjEditable)>
          <#assign rowCount = rowCount + 1>
          <#assign returnTotal = returnTotal + returnAdjustment.amount?default(0)>
       </#if>
    </tr>    
</#macro>

    <#if returnHeader?has_content>
      <#if returnHeader.destinationFacilityId?has_content && returnHeader.statusId == "RETURN_ACCEPTED" && returnHeader.returnHeaderTypeId == "CUSTOMER_RETURN">
        <#list returnShipmentIds as returnShipmentId>
          <a href="/facility/control/ViewShipment?shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}" class="buttontext">${uiLabelMap.ProductShipmentId} ${returnShipmentId.shipmentId}</a>
          <a href="/facility/control/ReceiveReturn?facilityId=${returnHeader.destinationFacilityId}&returnId=${returnHeader.returnId?if_exists}&shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}" class="buttontext">${uiLabelMap.OrderReceiveReturn}</a>
        </#list>
      <#elseif returnHeader.statusId == "SUP_RETURN_ACCEPTED" && returnHeader.returnHeaderTypeId == "VENDOR_RETURN">
         <#if returnShipmentIds?has_content>
           <#list returnShipmentIds as returnShipmentId>
             <a href="/facility/control/ViewShipment?shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}" class="buttontext">${uiLabelMap.ProductShipmentId} ${returnShipmentId.shipmentId}</a>
           </#list>         
         <#else> 
           <a href="/facility/control/EditShipment?primaryReturnId=${returnHeader.returnId}&partyIdTo=${toPartyId}&statusId=SHIPMENT_INPUT&shipmentTypeId=PURCHASE_RETURN" class="buttontext">${uiLabelMap.OrderCreateReturnShipment}</a>
         </#if>  
      </#if> 
    </#if>

<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">${uiLabelMap.PageTitleReturnItems}</li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body">
<!-- if we're called with loadOrderItems or createReturn, then orderId would exist -->
<#if !requestParameters.orderId?exists>
        <table cellspacing="0" class="basic-table">
          <#assign readOnly = (returnHeader.statusId != "RETURN_REQUESTED")>
            
          <tr><td colspan="10"><h3>${uiLabelMap.OrderOrderReturn} #${returnId}</h3></td></tr>
        
          <#-- information about orders and amount refunded/credited on past returns -->
          <#if orh?exists>
          <tr><td colspan="10">
              <table cellspacing="0" class="basic-table">
                <tr>
                  <td class="label" width="25%">${uiLabelMap.OrderOrderTotal}</td>
                  <td><@ofbizCurrency amount=orh.getOrderGrandTotal() isoCode=orh.getCurrency()/></td>
                </tr>  
                <tr>
                  <td class="label" width="25%">${uiLabelMap.OrderAmountAlreadyCredited}</td>
                  <td><@ofbizCurrency amount=orh.getReturnedCreditTotalWithBillingAccountBd() isoCode=orh.getCurrency()/></td>
                </tr>  
                <tr>
                  <td class="label" width="25%">${uiLabelMap.OrderAmountAlreadyRefunded}</td>
                  <td><@ofbizCurrency amount=orh.getReturnedRefundTotalWithBillingAccountBd() isoCode=orh.getCurrency()/></td>
                </tr>  
              </table>  
          </td></tr>
          </#if>
          <tr><td colspan="10"><br/></td></tr>
          <tr class="header-row">
            <td>${uiLabelMap.OrderOrderItems}</td>
            <td>${uiLabelMap.ProductProduct}</td>
            <td>${uiLabelMap.CommonDescription}</td>
            <td>${uiLabelMap.OrderQuantity}</td>
            <td>${uiLabelMap.OrderPrice}</td>
            <td>${uiLabelMap.OrderSubTotal}</td>
            <td>${uiLabelMap.OrderReturnReason}</td>
            <td>${uiLabelMap.OrderItemStatus}</td>
            <td>${uiLabelMap.CommonType}</td>
            <#if (readOnly)>
            <td>${uiLabelMap.OrderReturnResponse}</td>    
            </#if>    
            <td>&nbsp;</td>
          </tr>
          <#assign returnTotal = 0.0>
          <#assign rowCount = 0>
          <form method="post" action="<@ofbizUrl>updateReturnItems</@ofbizUrl>">
          <input type="hidden" name="_useRowSubmit" value="Y">      
          <#if returnItems?has_content>
            <#assign alt_row = false>
            <#list returnItems as item>
              <#assign orderItem = item.getRelatedOne("OrderItem")?if_exists>
              <#assign orderHeader = item.getRelatedOne("OrderHeader")?if_exists>
              <#assign returnReason = item.getRelatedOne("ReturnReason")?if_exists>
              <#assign returnType = item.getRelatedOne("ReturnType")?if_exists>
              <#assign status = item.getRelatedOne("InventoryStatusItem")?if_exists>
              <#assign shipmentReceipts = item.getRelated("ShipmentReceipt")?if_exists>
              <#if (item.get("returnQuantity")?exists && item.get("returnPrice")?exists)>
                 <#assign returnTotal = returnTotal + item.get("returnQuantity") * item.get("returnPrice") >
                 <#assign returnItemSubTotal = item.get("returnQuantity") * item.get("returnPrice") >
              <#else>
                 <#assign returnItemSubTotal = null >  <#-- otherwise the last item's might carry over -->
              </#if>
              <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                <td><a href="<@ofbizUrl>orderview?orderId=${item.orderId}</@ofbizUrl>" class="buttontext">${item.orderId}</a> - ${item.orderItemSeqId?default("N/A")}</td>
                  <input name="orderId_o_${rowCount}" value="${item.orderId}" type="hidden">
                  <input name="returnId_o_${rowCount}" value="${item.returnId}" type="hidden">
                  <input name="returnItemTypeId_o_${rowCount}" value="${item.returnItemTypeId}" type="hidden">
                  <input name="returnItemSeqId_o_${rowCount}" value="${item.returnItemSeqId}" type="hidden">
                  <input type="hidden" name="_rowSubmit_o_${rowCount}" value="Y" />
                <td><div>
                    <#if item.get("productId")?exists>
                        <a href="/catalog/control/EditProductInventoryItems?productId=${item.productId}" class="buttontext">${item.productId}</a>
                    <#else>
                        N/A
                    </#if></div></td>
                <td><div>
                    <#if readOnly>
                        ${item.description?default("N/A")}            
                    <#else>
                        <input name="description_o_${rowCount}" value="${item.description?if_exists}" type="text" size="15">
                    </#if>
                    </div></td>
                <td><div>
                    <#if readOnly>
                        ${item.returnQuantity?string.number} 
                    <#else>
                        <input name="returnQuantity_o_${rowCount}" value="${item.returnQuantity}" type="text" size="8" align="right">
                    </#if>
                    <#if item.receivedQuantity?exists>
                    <br/>Received: ${item.receivedQuantity}
                        <#list shipmentReceipts?if_exists as shipmentReceipt>
                            <br/>Qty: ${shipmentReceipt.quantityAccepted}, ${shipmentReceipt.datetimeReceived}, <a href="/facility/control/EditInventoryItem?inventoryItemId=${shipmentReceipt.inventoryItemId}" class="buttontext">${shipmentReceipt.inventoryItemId}</a>
                        </#list>
                    </#if>
                    </div></td>
                <td><div>
                    <#if readOnly>
                        <@ofbizCurrency amount=item.returnPrice isoCode=orderHeader.currencyUom/>
                    <#else>
                        <input name="returnPrice_o_${rowCount}" value="${item.returnPrice}" type="text" size="8" align="right">
                    </#if>
                    </div></td>
                <td>
                    <#if returnItemSubTotal?exists><@ofbizCurrency amount=returnItemSubTotal isoCode=orderHeader.currencyUom/></#if>
                </td>
                <td><div>
                    <#if readOnly>
                        ${returnReason.get("description",locale)?default("N/A")}
                    <#else>
                        <select name="returnReasonId_o_${rowCount}">
                            <#if (returnReason?has_content)>
                                <option value="${returnReason.returnReasonId}">${returnReason.get("description",locale)?if_exists}</option>
                                <option value="${returnReason.returnReasonId}">--</option>
                            </#if>
                            <#list returnReasons as returnReasonItem>
                                <option value="${returnReasonItem.returnReasonId}">${returnReasonItem.get("description",locale)?if_exists}</option>
                            </#list>
                        </select>
                    </#if>
                    </div></td>
                <td><div>
                  <#if readOnly>
                      <#if status?has_content>
                      ${status.get("description",locale)}
                      <#else>
                      N/A
                      </#if>
                  <#else>
                      <select name="expectedItemStatus_o_${rowCount}">
                          <#if (status?has_content)>
                              <option value="${status.statusId}">${status.get("description",locale)?if_exists}</option>
                              <option value="${status.statusId}">--</option>
                          </#if>
                          <#list itemStatus as returnItemStatus>
                              <option value="${returnItemStatus.statusId}">${returnItemStatus.get("description",locale)?if_exists}</option>
                          </#list>
                      </select>
                  </#if>
                  </div></td>
                <td><div>
                    <#if (readOnly)>
                        ${returnType.get("description",locale)?default("N/A")}
                    <#else>
                        <select name="returnTypeId_o_${rowCount}">
                            <#if (returnType?has_content)>
                                <option value="${returnType.returnTypeId}">${returnType.get("description",locale)?if_exists}</option>
                                <option value="${returnType.returnTypeId}">--</option>
                            </#if>
                            <#list returnTypes as returnTypeItem>
                                <option value="${returnTypeItem.returnTypeId}">${returnTypeItem.get("description",locale)?if_exists}</option>
                            </#list>
                        </select>
                    </#if></div></td>
                <#if (readOnly)>
                  <td>
                  <#if returnHeader.statusId == "RETURN_COMPLETED">
                    <#assign itemResp = item.getRelatedOne("ReturnItemResponse")?if_exists>
                    <#if itemResp?has_content>
                      <#if itemResp.paymentId?has_content>
                        <div>${uiLabelMap.AccountingPayment} #<a href="/accounting/control/editPayment?paymentId=${itemResp.paymentId}${externalKeyParam}" class="buttontext">${itemResp.paymentId}</a></div>
                      <#elseif itemResp.replacementOrderId?has_content>
                        <div>${uiLabelMap.OrderOrder} #<a href="<@ofbizUrl>orderview?orderId=${itemResp.replacementOrderId}</@ofbizUrl>" class="buttontext">${itemResp.replacementOrderId}</a></div>
                      <#elseif itemResp.billingAccountId?has_content>
                        <div>${uiLabelMap.AccountingAccountId} #<a href="/accounting/control/EditBillingAccount?billingAccountId=${itemResp.billingAccountId}${externalKeyParam}" class="buttontext">${itemResp.billingAccountId}</a></div>
                      </#if>
                    <#else>
                      <div>${uiLabelMap.CommonNone}</div>
                    </#if>
                  <#else>
                    <div>${uiLabelMap.CommonNA}</div>
                  </#if>
                </td>                  
                </#if>
                <#if returnHeader.statusId == "RETURN_REQUESTED">
                  <td align='right'><a href="<@ofbizUrl>removeReturnItem?returnId=${item.returnId}&returnItemSeqId=${item.returnItemSeqId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonRemove}</a>
                <#else>
                  <td>&nbsp;</td>
                </#if>
              </tr>
              <#assign rowCount = rowCount + 1>
              <#assign returnItemAdjustments = item.getRelated("ReturnAdjustment")>
              <#if (returnItemAdjustments?has_content)>
                  <#list returnItemAdjustments as returnItemAdjustment>
                     <@displayReturnAdjustment returnAdjustment=returnItemAdjustment adjEditable=false/>  <#-- adjustments of return items should never be editable -->
                  </#list>
              </#if>
              <#-- toggle the row color -->
              <#assign alt_row = !alt_row>
            </#list>
        <#else>
            <tr>
              <td colspan="9"><div>${uiLabelMap.OrderNoReturnItemsFound}</div></td>
            </tr>
        </#if>
            <tr><td colspan="10"><hr></td></tr>
        <#-- these are general return adjustments not associated with a particular item (itemSeqId = "_NA_" -->
        <#if (returnAdjustments?has_content)>
            <#list returnAdjustments as returnAdjustment>
                <#assign adjEditable = !readOnly> <#-- they are editable if the rest of the return items are -->
                <@displayReturnAdjustment returnAdjustment=returnAdjustment adjEditable=adjEditable/>
            </#list>
            </#if>
            <#-- show the return total -->    
            <tr><td colspan="5"></td><td><hr/></td></tr>
            <tr>
              <td colspan="2">&nbsp;</td>
              <td colspan="3" class="label">${uiLabelMap.OrderReturnTotal}</td>
              <td align="right"><@ofbizCurrency amount=returnTotal isoCode=returnHeader.currencyUomId/></td>
            </tr>
            <#if (!readOnly) && (rowCount > 0)>
               <tr>          
                  <input name="returnId" value="${returnHeader.returnId}" type="hidden">
                  <input name="_rowCount" value="${rowCount}" type="hidden">
                  <td colspan="6" align="right"><input type="submit" class="bottontext" value="${uiLabelMap.CommonUpdate}"></td>
              </tr>
           </#if>
           <tr><td colspan="10"><hr></td></tr> 
        </form>
        
        </table>
        <#if (returnHeader.statusId == "RETURN_REQUESTED") && (rowCount > 0)>
        <br/>
        <form name="acceptReturn" method="post" action="<@ofbizUrl>/updateReturn</@ofbizUrl>">
          <input type="hidden" name="returnId" value="${returnId}">
          <input type="hidden" name="statusId" value="RETURN_ACCEPTED">
          <div align="right"><input type="submit" value="${uiLabelMap.OrderReturnAccept}"></div>
        </form>
        </#if>
        
        <#if returnHeader.statusId == "RETURN_REQUESTED">
        <br/>
        <form name="returnItems" method="post" action="<@ofbizUrl>returnItems</@ofbizUrl>">
          <input type="hidden" name="returnId" value="${returnId}">
          <table border='0' cellpadding='2' cellspacing='0'>
            <tr><td colspan="4"><h3>${uiLabelMap.OrderReturnItems}</h3></td></tr>
            <#if partyOrders?has_content>
              <tr>      
                <td width='25%' align='right' nowrap class="label">${uiLabelMap.OrderOrderId}</td>
                <td>&nbsp;</td>
                <td width='25%'>        
                  <select name="orderId">
                    <#list partyOrders as order>
                      <option>${order.orderId}</option>
                    </#list>
                  </select>
                </td>
                <td><div class="tooltip">${uiLabelMap.OrderReturnLoadItems}</div></td> 
              </tr>
            <#else>
              <tr>
                <td colspan="4" nowrap><div>${uiLabelMap.OrderNoOrderFoundForParty}: <a href="${customerDetailLink}${returnHeader.fromPartyId?default('_NA_')}" class="buttontext">${returnHeader.fromPartyId?default('[null]')}</a></div></td>
              </tr>
              <tr>
                <td width='25%' align='right' nowrap><div>${uiLabelMap.OrderOrderId}</div></td>
                <td>&nbsp;</td>
                <td width='25%'>               
                  <input type='text' name='orderId' size='20' maxlength='20'>
                </td>
                <td><div class="tooltip">${uiLabelMap.OrderReturnLoadItems}</div></td> 
              </tr>
            </#if>      
            <tr>
              <td colspan="2">&nbsp;</td>
              <td colspan="2">
                <a href="javascript:document.returnItems.submit();" class="buttontext">${uiLabelMap.OrderReturnLoadItems}</a>
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
    </div>
</div>