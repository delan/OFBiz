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
        Order ID: <input type="text" class='inputBox' size="20" name="orderId" value="${orderId?if_exists}"/>
        <input type="submit" value="Select" class="smallSubmit"/>
    </div>
</form>

<div class="head2">Add Items to Shipment [${shipmentId?if_exists}] from Order [${orderId?if_exists}]</div>
<#if orderId?has_content && !orderHeader?exists>
	<div class="head3" style="color: red;">ERROR: Order with ID [${orderId}] not found.</div>
</#if>
<#if orderHeader?exists>
    <#if orderHeader.orderTypeId == "SALES_ORDER" && shipment.shipmentTypeId != "SALES_SHIPMENT">
        <div class="head3" style="color: red;">WARNING: Order Type is ${(orderType.description)?default(orderHeader.orderTypeId?if_exists)}, but this is NOT a Sales Shipment.</div>
    <#elseif orderHeader.orderTypeId == "PURCHASE_ORDER" && shipment.shipmentTypeId != "PURCHASE_SHIPMENT">
        <div class="head3" style="color: red;">WARNING: Order Type is ${(orderType.description)?default(orderHeader.orderTypeId?if_exists)}, but this is NOT a Purchase Shipment.</div>
    <#else>
        <div class="head3">NOTE: Order Type is ${(orderType.description)?default(orderHeader.orderTypeId?if_exists)}.</div>
    </#if>
    <#if shipment.shipmentTypeId == "SALES_SHIPMENT">
		<div class="head3">Origin Facility is: <#if originFacility?exists>${originFacility.facilityName?if_exists} [${originFacility.facilityId}]<#else><span style="color: red;">Not Set</span></#if></div>
    <#elseif shipment.shipmentTypeId == "PURCHASE_SHIPMENT">
		<div class="head3">Destination Facility is: <#if destinationFacility?exists>${destinationFacility.facilityName?if_exists} [${destinationFacility.facilityId}]<#else><span style="color: red;">Not Set</span></#if></div>
    </#if>
    <#if "ORDER_APPROVED" == orderHeader.statusId || "ORDER_BACKORDERED" == orderHeader.statusId>
        <div class="head3">NOTE: Order Status is ${(orderHeaderStatus.description)?default(orderHeader.statusId?if_exists)}.</div>
    <#elseif "ORDER_COMPLETED" == orderHeader.statusId>
        <div class="head3">NOTE: Order Status is ${(orderHeaderStatus.description)?default(orderHeader.statusId?if_exists)}, there should be no items left to issue from this order.</div>
    <#else>
        <div class="head3" style="color: red;">WARNING: Order Status is ${(orderHeaderStatus.description)?default(orderHeader.statusId?if_exists)}; should generally be Approved before shipping.</div>
    </#if>

    <#assign rowCount = 0>
    <#if isSalesOrder>
        <form action="<@ofbizUrl>/issueOrderItemInventoryResToShipment</@ofbizUrl>" name="selectAllForm">
    <#else>
        <form action="<@ofbizUrl>/issueOrderItemToShipment</@ofbizUrl>" name="selectAllForm">
    </#if>
    <input type="hidden" name="shipmentId" value="${shipmentId}">
    <input type="hidden" name="_useRowSubmit" value="Y">
    <table width="100%" cellpadding="2" cellspacing="0" border="1">
        <tr>
            <td><div class="tableheadtext">Order Item</div></td>
            <td><div class="tableheadtext">Product</div></td>
            <#if isSalesOrder>
                <td><div class="tableheadtext">Items Issued or Reserved</div></td>
                <td><div class="tableheadtext">[Issued + Reserved = Total] = Ordered</div></td>
                <td><div class="tableheadtext">Reserved</div></td>
            <#else>
                <td><div class="tableheadtext">Items Issued</div></td>
                <td><div class="tableheadtext">Issed = Ordered</div></td>
            </#if>
            <td><div class="tableheadtext">Issue</div></td>
            <td align="right">
                <div class="tableheadtext">Submit?</div>
                <div class="tableheadtext">All<input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this);"></div>
            </td>
        </tr>
        <#list orderItemDatas?if_exists as orderItemData>
            <#assign orderItem = orderItemData.orderItem>
            <#assign product = orderItemData.product?if_exists>
            <#assign itemIssuances = orderItemData.itemIssuances>
            <#assign totalQuantityIssued = orderItemData.totalQuantityIssued>
            <#assign orderItemInventoryResDatas = orderItemData.orderItemInventoryResDatas?if_exists>
            <#assign totalQuantityReserved = orderItemData.totalQuantityReserved?if_exists>
            <#assign totalQuantityIssuedAndReserved = orderItemData.totalQuantityIssuedAndReserved?if_exists>
            <tr>
                <td><div class="tabletext">${orderItem.orderItemSeqId}</div></td>
                <td><div class="tabletext">${(product.productName)?if_exists} [${orderItem.productId?default("N/A")}]</div></td>
                <td>
                    <#if itemIssuances?has_content>
                        <#list itemIssuances as itemIssuance>
                            <div class="tabletext"><b>[${itemIssuance.quantity?if_exists}]</b>${itemIssuance.shipmentId?if_exists}:${itemIssuance.shipmentItemSeqId?if_exists} on [${(itemIssuance.issuedDateTime.toString())?if_exists}] by [${(itemIssuance.issuedByUserLoginId)?if_exists}]</div>
                        </#list>
                    <#else>
                        <div class="tabletext">&nbsp;</div>
                    </#if>
                </td>
                <td>
                    <div class="tabletext">
                        <#if isSalesOrder>
                            <#if (totalQuantityIssuedAndReserved != orderItem.quantity)><span style="color: red;"><#else><span></#if>
                                [${totalQuantityIssued} + ${totalQuantityReserved} = ${totalQuantityIssuedAndReserved}]
                                <b>
                                    <#if (totalQuantityIssuedAndReserved > orderItem.quantity)>&gt;<#else><#if (totalQuantityIssuedAndReserved < orderItem.quantity)>&lt;<#else>=</#if></#if>
                                    ${orderItem.quantity}
                                </b>
                            </span>
                        <#else>
                            <#if (totalQuantityIssued > orderItem.quantity)><span style="color: red;"><#else><span></#if>
                                ${totalQuantityIssued}
                                <b>
                                    <#if (totalQuantityIssued > orderItem.quantity)>&gt;<#else><#if (totalQuantityIssued < orderItem.quantity)>&lt;<#else>=</#if></#if>
                                    ${orderItem.quantity}
                                </b>
                            </span>
                        </#if>
                    </div>
                </td>
                <#if isSalesOrder>
                    <td><div class="tabletext">&nbsp;</div></td>
                    <td><div class="tabletext">&nbsp;</div></td>
                <#else>
                    <#assign quantityNotIssued = orderItem.quantity - totalQuantityIssued>
                    <#if (quantityNotIssued > 0)>
                        <td>
                            <input type="hidden" name="shipmentId_o_${rowCount}" value="${shipmentId}"/>
                            <input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}"/>
                            <input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}"/>
                            <input type="text" class='inputBox' size="5" name="quantity_o_${rowCount}" value="${quantityNotIssued}"/>
                        </td>
                        <td align="right">              
                          <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this);">
                        </td>
                        <#assign rowCount = rowCount + 1>   
                    <#else>
                        <td><div class="tabletext">&nbsp;</div></td>
                        <td><div class="tabletext">&nbsp;</div></td>
                    </#if>
                </#if>
            </tr>
            <#if isSalesOrder>
                <#list orderItemInventoryResDatas as orderItemInventoryResData>
                    <#assign orderItemInventoryRes = orderItemInventoryResData.orderItemInventoryRes>
                    <#assign inventoryItem = orderItemInventoryResData.inventoryItem>
                    <#assign inventoryItemFacility = orderItemInventoryResData.inventoryItemFacility>
                    <tr>
                        <td><div class="tabletext">&nbsp;</div></td>
                        <td><div class="tabletext">&nbsp;</div></td>
                        <td>
                            <div class="tabletext">
                                ${orderItemInventoryRes.inventoryItemId}
                                <#if inventoryItem.facilityId?has_content>
                                    <span<#if originFacility?exists && originFacility.facilityId != inventoryItem.facilityId> style="color: red;"</#if>>[${(inventoryItemFacility.facilityName)?default(inventoryItem.facilityId)}]</span>
                                <#else>
                                    <span style="color: red;">[No Facility]</span>
                                </#if>
                            </div>
                        </td>
                        <td><div class="tabletext">&nbsp;</div></td>
                        <td><div class="tabletext">${orderItemInventoryRes.quantity}</div></td>
                        <#if originFacility?exists && originFacility.facilityId == inventoryItem.facilityId?if_exists>
                            <td>
                                <input type="hidden" name="shipmentId_o_${rowCount}" value="${shipmentId}"/>
                                <input type="hidden" name="orderId_o_${rowCount}" value="${orderItemInventoryRes.orderId}"/>
                                <input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItemInventoryRes.orderItemSeqId}"/>
                                <input type="hidden" name="inventoryItemId_o_${rowCount}" value="${orderItemInventoryRes.inventoryItemId}"/>
                                <input type="text" class='inputBox' size="5" name="quantity_o_${rowCount}" value="${orderItemInventoryRes.quantity}"/>
                            </td>
                            <td align="right">              
                              <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this);">
                            </td>
                            <#assign rowCount = rowCount + 1>   
                        <#else>
                            <td><div class="tabletext">Not In Origin Facility</div></td>
                            <td><div class="tabletext">&nbsp;</div></td>
                        </#if>
                    </tr>
                </#list>
            </#if>
        </#list>
        <tr>
            <td colspan="6" align="right"><input type="submit" class="smallSubmit" value="Issue All"/></td>
        </tr>
    </table>
    <input type="hidden" name="_rowCount" value="${rowCount}">
    </form>
    <script language="JavaScript">selectAll();</script>
</#if>

<#else>
  <h3>The Shipment was not found with ID: [${shipmentId?if_exists}]</h3>
</#if>

<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
