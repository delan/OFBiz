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
<table width="100%" cellspacing="0" cellpadding="2" border="1">
	<tr>
		<td><div class="tableheadtext">Item#</div></td>
		<td><div class="tableheadtext">&nbsp;</div></td>
		<td><div class="tableheadtext">&nbsp;</div></td>
		<td><div class="tableheadtext">Quantity</div></td>
		<td><div class="tableheadtext">&nbsp;</div></td>
		<td><div class="tableheadtext">&nbsp;</div></td>
		<td><div class="tableheadtext">&nbsp;</div></td>
	</tr>
<#list shipmentItemDatas as shipmentItemData>
	<#assign shipmentItem = shipmentItemData.shipmentItem>
	<#assign itemIssuances = shipmentItemData.itemIssuances>
	<#assign shipmentPackageContents = shipmentItemData.shipmentPackageContents>
	<#assign product = shipmentItemData.product?if_exists>
	<#assign totalQuantityPackaged = shipmentItemData.totalQuantityPackaged>
	<#assign totalQuantityToPackage = shipmentItemData.totalQuantityToPackage>
	<tr>
		<td><div class="tabletext">${shipmentItem.shipmentItemSeqId}</div></td>
		<td colspan="2"><div class="tabletext">${(product.productName)?if_exists} [<a href="<@ofbizUrl>/EditProduct?productId=${shipmentItem.productId?if_exists}</@ofbizUrl>" class="buttontext">${shipmentItem.productId?if_exists}</a>]</div></td>
		<td><div class="tabletext">${shipmentItem.quantity?default("&nbsp;")}</div></td>
		<td colspan="2"><div class="tabletext">${shipmentItem.shipmentContentDescription?default("&nbsp;")}</div></td>
		<td><div class="tabletext"><a href="<@ofbizUrl>/deleteShipmentItem?shipmentId=${shipmentId}&shipmentItemSeqId=${shipmentItem.shipmentItemSeqId}</@ofbizUrl>" class="buttontext">Delete</a></div></td>
	</tr>
	<#list itemIssuances as itemIssuance>
		<tr>
			<td><div class="tabletext">&nbsp;</div></td>
			<td><div class="tabletext">OrderItem:<a href="/ordermgr/control/orderview?order_id=${itemIssuance.orderId?if_exists}" class="buttontext">${itemIssuance.orderId?if_exists}</a>:${itemIssuance.orderItemSeqId?if_exists}</div></td>
			<td><div class="tabletext">Inventory:<a href="<@ofbizUrl>/EditInventoryItem?inventoryItemId=${itemIssuance.inventoryItemId?if_exists}</@ofbizUrl>" class="buttontext">${itemIssuance.inventoryItemId?if_exists}</a></div></td>
			<td><div class="tabletext">${itemIssuance.quantity?if_exists}</div></td>
			<td><div class="tabletext">${itemIssuance.issuedDateTime?if_exists}</div></td>
			<td><div class="tabletext">Future Party/Role List</div></td>
			<td><div class="tabletext"><a href="<@ofbizUrl>/deleteShipmentItemIssuance?shipmentId=${shipmentId}&itemIssuanceId=${itemIssuance.itemIssuanceId}</@ofbizUrl>" class="buttontext">Delete</a></div></td>
		</tr>
	</#list>
	<#list shipmentPackageContents as shipmentPackageContent>
		<tr>
			<td><div class="tabletext">&nbsp;</div></td>
			<td colspan="2"><div class="tabletext">Package:${shipmentPackageContent.shipmentPackageSeqId}</div></td>
			<td><div class="tabletext">${shipmentPackageContent.quantity?if_exists}</div></td>
			<td colspan="2"><div class="tabletext">&nbsp;</div></td>
			<td><div class="tabletext"><a href="<@ofbizUrl>/deleteShipmentItemPackageContent?shipmentId=${shipmentId}&shipmentItemSeqId=${shipmentPackageContent.shipmentItemSeqId}&shipmentPackageSeqId=${shipmentPackageContent.shipmentPackageSeqId}</@ofbizUrl>" class="buttontext">Delete</a></div></td>
		</tr>
	</#list>
    <#if (totalQuantityToPackage > 0)>
        <tr>
            <form action="<@ofbizUrl>/createShipmentItemPackageContent</@ofbizUrl>" name="createShipmentPackageContentForm${shipmentItemData_index}">
            <input type="hidden" name="shipmentId" value="${shipmentId}"/>
            <input type="hidden" name="shipmentItemSeqId" value="${shipmentItem.shipmentItemSeqId}"/>
            <td><div class="tabletext">&nbsp;</div></td>
            <td colspan="2">
                <div class="tabletext">Add to Package:
                <select name="shipmentPackageSeqId" class="selectBox">
                    <#list shipmentPackages as shipmentPackage>
                        <option>${shipmentPackage.shipmentPackageSeqId}</option>
                    </#list>
                    <option>New</option>
                </select>
                </div>
            </td>
            <td>
                <div class="tabletext">
                    <input name="quantity" size="5" value="${totalQuantityToPackage}" class="inputBox"/>
                    <a href="javascript:document.createShipmentPackageContentForm${shipmentItemData_index}.submit()" class="buttontext">Add</a>
                </div>
            </td>
            <td colspan="2"><div class="tabletext">&nbsp;</div></td>
            <td>&nbsp;</td>
            </form>
        </tr>
    </#if>
</#list>
<tr>
	<form action="<@ofbizUrl>/createShipmentItem</@ofbizUrl>" name="createShipmentItemForm">
	<input type="hidden" name="shipmentId" value="${shipmentId}"/>
	<td><div class="tabletext">New Item:</div></td>
	<td colspan="2"><div class="tabletext">ProductID:<input name="productId" size="15" maxlength="20" class="inputBox"/></div></td>
	<td><div class="tabletext"><input name="quantity" size="5" value="0" class="inputBox"/></div></td>
	<td colspan="2"><div class="tabletext">Description:<input name="shipmentContentDescription" size="30" maxlength="255" class="inputBox"/></div></td>
	<td><a href="javascript:document.createShipmentItemForm.submit()" class="buttontext">Create</a></td>
	</form>
</tr>
</table>
<#else>
  <h3>The Shipment was not found with ID: [${shipmentId?if_exists}]</h3>
</#if>

<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
