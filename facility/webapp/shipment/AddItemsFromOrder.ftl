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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Revision$
 *@since      2.2
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
${pages.get("/shipment/ShipmentTabBar.ftl")}

<#if shipment?exists>

<form action="<@ofbizUrl>/AddItemsFromOrder</@ofbizUrl>">
	<input type="hidden" name="shipmentId" value="${shipmentId}"/>
	<div class="tabletext">
        Order ID: <input type="text" size="20" name="orderId" value="${orderId?if_exists}"/>
        <input type="submit" value="Select" class="standardButton"/>
    </div>
</form>

<div class="head2">Add Items to Shipment [${shipmentId?if_exists}] from Order [${orderId?if_exists}]</div>
<div class="head3">Origin Facility is: <#if originFacility?exists>${originFacility.facilityName?if_exists} [${originFacility.facilityId}]<#else><span style="color: red;">Not Set</span></#if></div>
<#if orderId?has_content && !orderHeader?exists>
    <div class="head3" style="color: red;">ERROR: Order with ID [${orderId}] not found.</div>
</#if>
<#if orderHeader?exists>
    <#if "ORDER_APPROVED" == orderHeader.statusId || "ORDER_BACKORDERED" == orderHeader.statusId>
        <div class="head3">NOTE: Order Status is ${(orderHeaderStatus.description)?default(orderHeader.statusId?if_exists)}.</div>
    <#else>
        <div class="head3" style="color: red;">WARNING: Order Status is ${(orderHeaderStatus.description)?default(orderHeader.statusId?if_exists)}; should generally be Approved or Backordered before shipping.</div>
    </#if>
</#if>
<table width="100%" cellpadding="2" cellspacing="0" border="1">
    <tr>
        <td><div class="tableheadtext">OrderItem</div></td>
        <td><div class="tableheadtext">InventoryItem</div></td>
        <td><div class="tableheadtext">Product</div></td>
        <td><div class="tableheadtext">Facility</div></td>
        <td><div class="tableheadtext">Res&nbsp;Status</div></td>
        <td><div class="tableheadtext">Reserved</div></td>
        <td><div class="tableheadtext">Issued</div></td>
        <td><div class="tableheadtext">ShipmentItems</div></td>
        <td><div class="tableheadtext">Issue</div></td>
    </tr>
    <#list orderItemDatas?if_exists as orderItemData>
        <#assign orderItem = orderItemData.orderItem>
        <#assign orderItemInventoryResDatas = orderItemData.orderItemInventoryResDatas>
        <#assign product = orderItemData.product>
        
        <#list orderItemInventoryResDatas as orderItemInventoryResData>
            <#assign orderItemInventoryRes = orderItemInventoryResData.orderItemInventoryRes>
            <#assign resStatus = orderItemInventoryResData.resStatus>
            <#assign inventoryItem = orderItemInventoryResData.inventoryItem>
            <#assign itemIssuances = orderItemInventoryResData.itemIssuances>
            <#assign totalQuantityIssued = orderItemInventoryResData.totalQuantityIssued>
            <#assign quantityLeftToIssue = orderItemInventoryResData.quantityLeftToIssue>
            <tr>
                <td><div class="tabletext">${orderItemInventoryRes.orderItemSeqId}</div></td>
                <td><div class="tabletext">${orderItemInventoryRes.inventoryItemId}</div></td>
                <td><div class="tabletext">${(product.productName)?if_exists} [${orderItem.productId?if_exists}]</div></td>
                <td>
                    <#if inventoryItem.facilityId?has_content>
                        <div class="tabletext"<#if originFacility?exists && originFacility.facilityId != inventoryItem.facilityId> style="color: red;"</#if>>${inventoryItem.facilityId}</div>
                    <#else>
                        <div class="tabletext" style="color: red;">No Facility</div>
                    </#if>
                </td>
                <td><div class="tabletext">${resStatus.description?default(orderItemInventoryRes.statusId?default("&nbsp;"))}</div></td>
                <td><div class="tabletext">${orderItemInventoryRes.quantity}</div></td>
                <td><div class="tabletext">${totalQuantityIssued}</div></td>
                <td>
                    <#if itemIssuances?has_content>
                        <#list itemIssuances as itemIssuance>
                            <div class="tabletext">${itemIssuance.shipmentId?if_exists}:${itemIssuance.shipmentItemSeqId?if_exists}-${itemIssuance.quantity?if_exists}-${(itemIssuance.issuedDateTime.toString())?if_exists}</div>
                        </#list>
                    <#else>
                        <div class="tabletext">&nbsp;</div>
                    </#if>
                </td>
                <td>
                    <#if (quantityLeftToIssue > 0)>
                        <#if "OIIR_CANCELLED" != orderItemInventoryRes.statusId>
                            <#if originFacility?exists && originFacility.facilityId == inventoryItem.facilityId?if_exists>
                                <form action="<@ofbizUrl>/issueOrderItemInventoryResToShipment</@ofbizUrl>" name="addOrderItemToShipmentForm${orderItemData_index}${orderItemInventoryResData_index}">
                                    <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                                    <input type="hidden" name="orderId" value="${orderItemInventoryRes.orderId}"/>
                                    <input type="hidden" name="orderItemSeqId" value="${orderItemInventoryRes.orderItemSeqId}"/>
                                    <input type="hidden" name="inventoryItemId" value="${orderItemInventoryRes.inventoryItemId}"/>
                                    <input type="text" size="5" name="quantity" value="${quantityLeftToIssue}"/>
                                    <a href="javascript:document.addOrderItemToShipmentForm${orderItemData_index}${orderItemInventoryResData_index}.submit();" class="buttontext">Issue</a>
                                </form>
                            <#else>
                                <div class="tabletext">Not In Origin Facility</div>
                            </#if>
                        <#else>
                            <div class="tabletext">Reservation Canceled</div>
                        </#if>
                    <#else>
                        <div class="tabletext">All Issued</div>
                    </#if>
                </td>
            </tr>
        </#list>
    </#list>
</table>

<#else>
  <h3>The Shipment was not found with ID: [${shipmentId?if_exists}]</h3>
</#if>

<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
