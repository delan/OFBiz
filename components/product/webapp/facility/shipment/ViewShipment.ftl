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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.2 $
 *@since      2.2
-->

<#if hasPermission>
${pages.get("/shipment/ShipmentTabBar.ftl")}

<#if shipmentId?has_content>
    <div><a href="<@ofbizUrl>/ShipmentManifestReport.pdf?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext" target="_blank">Generate Shipment Manifest Report</a></div>
</#if>

<#if shipment?exists>

${pages.get("/shipment/ViewShipmentInfo.ftl")}

<#-- ==================================================================== -->
<br/>
<table width="100%" cellspacing="0" cellpadding="2" border="1">
	<tr>
		<td><div class="tableheadtext">Item#</div></td>
		<td><div class="tableheadtext">&nbsp;</div></td>
		<td><div class="tableheadtext">&nbsp;</div></td>
		<td><div class="tableheadtext">Quantity</div></td>
		<td><div class="tableheadtext">&nbsp;</div></td>
		<td><div class="tableheadtext">&nbsp;</div></td>
	</tr>
<#list shipmentItemDatas as shipmentItemData>
	<#assign shipmentItem = shipmentItemData.shipmentItem>
	<#assign itemIssuances = shipmentItemData.itemIssuances>
	<#assign shipmentPackageContents = shipmentItemData.shipmentPackageContents>
	<#assign product = shipmentItemData.product?if_exists>
	<tr>
		<td><div class="tabletext">${shipmentItem.shipmentItemSeqId}</div></td>
		<td colspan="2"><div class="tabletext">${(product.productName)?if_exists} [<a href="<@ofbizUrl>/EditProduct?productId=${shipmentItem.productId?if_exists}</@ofbizUrl>" class="buttontext">${shipmentItem.productId?if_exists}</a>]</div></td>
		<td><div class="tabletext">${shipmentItem.quantity?default("&nbsp;")}</div></td>
		<td colspan="2"><div class="tabletext">${shipmentItem.shipmentContentDescription?default("&nbsp;")}</div></td>
	</tr>
	<#list itemIssuances as itemIssuance>
		<tr>
			<td><div class="tabletext">&nbsp;</div></td>
			<td><div class="tabletext">OrderItem:<a href="/ordermgr/control/orderview?order_id=${itemIssuance.orderId?if_exists}&externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">${itemIssuance.orderId?if_exists}</a>:${itemIssuance.orderItemSeqId?if_exists}</div></td>
			<td><div class="tabletext">Inventory:<a href="<@ofbizUrl>/EditInventoryItem?inventoryItemId=${itemIssuance.inventoryItemId?if_exists}</@ofbizUrl>" class="buttontext">${itemIssuance.inventoryItemId?if_exists}</a></div></td>
			<td><div class="tabletext">${itemIssuance.quantity?if_exists}</div></td>
			<td><div class="tabletext">${itemIssuance.issuedDateTime?if_exists}</div></td>
			<td><div class="tabletext">Future Party/Role List</div></td>
		</tr>
	</#list>
	<#list shipmentPackageContents as shipmentPackageContent>
		<tr>
			<td><div class="tabletext">&nbsp;</div></td>
			<td colspan="2"><div class="tabletext">Package:${shipmentPackageContent.shipmentPackageSeqId}</div></td>
			<td><div class="tabletext">${shipmentPackageContent.quantity?if_exists}</div></td>
			<td colspan="2"><div class="tabletext">&nbsp;</div></td>
		</tr>
	</#list>
</#list>
</table>

<#-- ==================================================================== -->
<br/>
<table width="100%" cellspacing="0" cellpadding="2" border="1">
    <tr>
        <td><div class="tableheadtext">Package#</div></td>
        <td><div class="tableheadtext">Created</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
    </tr>
<#list shipmentPackageDatas as shipmentPackageData>
    <#assign shipmentPackage = shipmentPackageData.shipmentPackage>
    <#assign shipmentPackageContents = shipmentPackageData.shipmentPackageContents?if_exists>
    <#assign shipmentPackageRouteSegs = shipmentPackageData.shipmentPackageRouteSegs?if_exists>
    <#assign weightUom = shipmentPackageData.weightUom?if_exists>
    <tr>
        <td><div class="tabletext">${shipmentPackage.shipmentPackageSeqId}</div></td>
        <td><div class="tabletext">${(shipmentPackage.dateCreated.toString())?if_exists}</div></td>
        <td><span class="tabletext">Weight: ${shipmentPackage.weight?if_exists}</span></td>
        <td><span class="tabletext">WeightUnit:${weightUom.description?default(shipmentPackage.weightUomId?if_exists)}</span></td>
    </tr>
    <#list shipmentPackageContents as shipmentPackageContent>
        <tr>
            <td><div class="tabletext">&nbsp;</div></td>
            <td><div class="tabletext">Item:${shipmentPackageContent.shipmentItemSeqId}</div></td>
            <td><div class="tabletext">Quantity:${shipmentPackageContent.quantity?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;</div></td>
        </tr>
    </#list>
    <#list shipmentPackageRouteSegs as shipmentPackageRouteSeg>
        <tr>
            <td><div class="tabletext">&nbsp;</div></td>
            <td><div class="tabletext">RouteSegment:${shipmentPackageRouteSeg.shipmentRouteSegmentId}</div></td>
            <td><span class="tabletext">Tracking#: ${shipmentPackageRouteSeg.trackingCode?if_exists}</span></td>
            <td><span class="tabletext">Box#: ${shipmentPackageRouteSeg.boxNumber?if_exists}</span></td>
        </tr>
    </#list>
</#list>
</table>

<#-- ==================================================================== -->
<br/>
<table width="100%" cellspacing="0" cellpadding="2" border="1">
    <tr>
        <td><div class="tableheadtext">Segment#</div></td>
        <td>
            <div class="tableheadtext">Carrier-ShipmentMethod</div>
            <div class="tableheadtext">Origin-Destination Facility</div>
            <div class="tableheadtext">Origin-Destination Address ID</div>
            <div class="tableheadtext">Origin-Destination Phone ID</div>
        </td>
        <td>
            <div class="tableheadtext">Carrier Status</div>
            <div class="tableheadtext">Tracking Number</div>
            <div class="tableheadtext">Estimated (Start-Arrive)</div>
            <div class="tableheadtext">Actual (Start-Arrive)</div>
        </td>
        <td>
        	<div class="tableheadtext">Billing Weight &amp; UOM</div>
        	<div class="tableheadtext">Currency UOM</div>
        	<div class="tableheadtext">Actual Transport</div>
        	<div class="tableheadtext">Actual Services</div>
        	<div class="tableheadtext">Actual Other</div>
        	<div class="tableheadtext">Actual Total</div>
        </td>
    </tr>
<#list shipmentRouteSegmentDatas as shipmentRouteSegmentData>
    <#assign shipmentRouteSegment = shipmentRouteSegmentData.shipmentRouteSegment>
    <#assign shipmentPackageRouteSegs = shipmentRouteSegmentData.shipmentPackageRouteSegs?if_exists>
    <#assign originFacility = shipmentRouteSegmentData.originFacility?if_exists>
    <#assign destFacility = shipmentRouteSegmentData.destFacility?if_exists>
    <#assign shipmentMethodType = shipmentRouteSegmentData.shipmentMethodType?if_exists>
    <#assign carrierPerson = shipmentRouteSegmentData.carrierPerson?if_exists>
    <#assign carrierPartyGroup = shipmentRouteSegmentData.carrierPartyGroup?if_exists>
    <#assign originPostalAddress = shipmentRouteSegmentData.originPostalAddress?if_exists>
    <#assign destPostalAddress = shipmentRouteSegmentData.destPostalAddress?if_exists>
    <#assign originTelecomNumber = shipmentRouteSegmentData.originTelecomNumber?if_exists>
    <#assign destTelecomNumber = shipmentRouteSegmentData.destTelecomNumber?if_exists>
    <#assign carrierServiceStatusItem = shipmentRouteSegmentData.carrierServiceStatusItem?if_exists>
    <#assign currencyUom = shipmentRouteSegmentData.currencyUom?if_exists>
    <#assign billingWeightUom = shipmentRouteSegmentData.billingWeightUom?if_exists>
    <#assign carrierServiceStatusValidChangeToDetails = shipmentRouteSegmentData.carrierServiceStatusValidChangeToDetails?if_exists>
    <tr>
        <td><div class="tabletext">${shipmentRouteSegment.shipmentRouteSegmentId}</div></td>
        <td>
            <span class="tabletext">${(carrierPerson.firstName)?if_exists} ${(carrierPerson.middleName)?if_exists} ${(carrierPerson.lastName)?if_exists} ${(carrierPartyGroup.groupName)?if_exists} [${shipmentRouteSegment.carrierPartyId?if_exists}]</span>
            <span class="tabletext">${shipmentMethodType.description?default(shipmentRouteSegment.shipmentMethodTypeId?if_exists)}</span>
            <br/>
            <span class="tabletext">Origin: ${(originFacility.facilityName)?if_exists} [${originFacility.facilityId?if_exists}]</span>
            <span class="tabletext">Dest: ${(destFacility.facilityName)?if_exists} [${destFacility.facilityId?if_exists}]</span>
            <br/>
            <div class="tabletext">
                Origin: <b>${shipmentRouteSegment.originContactMechId?if_exists}</b>
                <#if originPostalAddress?has_content>[To: ${originPostalAddress.toName?if_exists}, Attn: ${originPostalAddress.attnName?if_exists}, ${originPostalAddress.address1?if_exists}, ${originPostalAddress.address2?if_exists}, ${originPostalAddress.city?if_exists}, ${originPostalAddress.stateProvinceGeoId?if_exists}, ${originPostalAddress.postalCode?if_exists}, ${originPostalAddress.countryGeoId?if_exists}]</#if>
            </div>
            <div class="tabletext">
                Dest: <b>${shipmentRouteSegment.destContactMechId?if_exists}</b>
                <#if destPostalAddress?has_content>[To: ${destPostalAddress.toName?if_exists}, Attn: ${destPostalAddress.attnName?if_exists}, ${destPostalAddress.address1?if_exists}, ${destPostalAddress.address2?if_exists}, ${destPostalAddress.city?if_exists}, ${destPostalAddress.stateProvinceGeoId?if_exists}, ${destPostalAddress.postalCode?if_exists}, ${destPostalAddress.countryGeoId?if_exists}]</#if>
            </div>
            <div class="tabletext">
                Origin: <b>${shipmentRouteSegment.originTelecomNumberId?if_exists}</b>
                <#if originTelecomNumber?has_content>[${originTelecomNumber.countryCode?if_exists}  ${originTelecomNumber.areaCode?if_exists} ${originTelecomNumber.contactNumber?if_exists}]</#if>
            </div>
            <div class="tabletext">
                Dest: <b>${shipmentRouteSegment.destTelecomNumberId?if_exists}</b>
                <#if destTelecomNumber?has_content>[${destTelecomNumber.countryCode?if_exists}  ${destTelecomNumber.areaCode?if_exists} ${destTelecomNumber.contactNumber?if_exists}]</#if>
            </div>
        </td>
        <td>
            
            <div class="tabletext">${(carrierServiceStatus.description)?default("&nbsp;")}</div>
            <div class="tabletext">${shipmentRouteSegment.trackingIdNumber?default("&nbsp;")}</div>
            <div class="tabletext">[${(shipmentRouteSegment.estimatedStartDate.toString())?if_exists} - ${(shipmentRouteSegment.estimatedArrivalDate.toString())?if_exists}]</span>
            <div class="tabletext">[${(shipmentRouteSegment.actualStartDate.toString())?if_exists} - ${(shipmentRouteSegment.actualArrivalDate.toString())?if_exists}]</span>
        </td>
        <td>
        	<div class="tabletext">${shipmentRouteSegment.billingWeight?if_exists} ${(billingWeightUom.description)?if_exists} [${(billingWeightUom.abbreviation)?if_exists}]</div>
        	<div class="tabletext">${(currencyUom.description)?default("&nbsp;")}</div>
        	<div class="tabletext">${(shipmentRouteSegment.actualTransportCost?string.currency)?default("&nbsp;")}</div>
        	<div class="tabletext">${(shipmentRouteSegment.actualServiceCost?string.currency)?default("&nbsp;")}</div>
        	<div class="tabletext">${(shipmentRouteSegment.actualOtherCost?string.currency)?default("&nbsp;")}</div>
        	<div class="tabletext">${(shipmentRouteSegment.actualCost?string.currency)?default("&nbsp;")}</div>
        </td>
    </tr>
    <#list shipmentPackageRouteSegs as shipmentPackageRouteSeg>
        <tr>
            <td><div class="tabletext">&nbsp;</div></td>
            <td><div class="tabletext">Package:${shipmentPackageRouteSeg.shipmentPackageSeqId}</div></td>
            <td><span class="tabletext">Tracking#: ${shipmentPackageRouteSeg.trackingCode?if_exists}</span></td>
            <td><span class="tabletext">Box#: ${shipmentPackageRouteSeg.boxNumber?if_exists}</span></td>
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
