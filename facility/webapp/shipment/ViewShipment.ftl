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
 *@version    $Revision$
 *@since      2.2
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
${pages.get("/shipment/ShipmentTabBar.ftl")}

<#if shipment?exists>

<table border="0" cellpadding="2" cellspacing="0">
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Shipment Type</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left"><span class="tabletext">${(shipmentType.description)?default(shipment.shipmentTypeId?if_exists)}</span></td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Status</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left"><span class="tabletext">${(statusItem.description)?default(shipment.statusId?if_exists)}</span></td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Primary Order Id</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left"><span class="tabletext">${shipment.primaryOrderId?if_exists}</span></td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Estimated Dates</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left">
            <span class="tabletext">
                <span class="tableheadtext">Ready:&nbsp;</span>${(shipment.estimatedReadyDate.toString())?if_exists}
                <span class="tableheadtext">Ship:&nbsp;</span>${(shipment.estimatedShipDate.toString())?if_exists}
                <span class="tableheadtext">Arrival:&nbsp;</span>${(shipment.estimatedArrivalDate.toString())?if_exists}
            </span>
        </td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Latest Cancel Date</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left"><span class="tabletext">${(shipment.latestCancelDate.toString())?if_exists}</span></td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Estimated Ship Cost</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left"><span class="tabletext">${(shipment.estimatedShipCost?string.currency)?if_exists}</span></td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Handling Instructions</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left"><span class="tabletext">${shipment.handlingInstructions?if_exists}</span></td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Facilities</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left">
            <div class="tabletext"><span class="tableheadtext">Origin:&nbsp;</span>${(originFacility.facilityName)?if_exists}&nbsp;[${(shipment.originFacilityId?if_exists)}]</div>
            <div class="tabletext"><span class="tableheadtext">Destination:&nbsp;</span>${(destinationFacility.facilityName)?if_exists}&nbsp;[${(shipment.destinationFacilityId?if_exists)}]</div>
        </td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Parties</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left">
            <span class="tabletext">
                <span class="tableheadtext">To:&nbsp;</span>${(toPerson.firstName)?if_exists} ${(toPerson.middleName)?if_exists} ${(toPerson.lastName)?if_exists} ${(toPartyGroup.groupName)?if_exists} [${shipment.partyIdTo?if_exists}]
                <span class="tableheadtext">From:&nbsp;</span>${(fromPerson.firstName)?if_exists} ${(fromPerson.middleName)?if_exists} ${(fromPerson.lastName)?if_exists} ${(fromPartyGroup.groupName)?if_exists} [${shipment.partyIdFrom?if_exists}]
            </span>
        </td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Addresses</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left">
            <div class="tabletext"><span class="tableheadtext">Origin:&nbsp;</span>${shipment.originContactMechId?if_exists}&nbsp;<#if originPostalAddress?has_content>[To: ${originPostalAddress.toName?if_exists}, Attn: ${originPostalAddress.attnName?if_exists}, ${originPostalAddress.address1?if_exists}, ${originPostalAddress.address2?if_exists}, ${originPostalAddress.city?if_exists}, ${originPostalAddress.stateProvinceGeoId?if_exists}, ${originPostalAddress.postalCode?if_exists}, ${originPostalAddress.countryGeoId?if_exists}]</#if></div>
            <div class="tabletext"><span class="tableheadtext">Destination:&nbsp;</span>${shipment.destinationContactMechId?if_exists}&nbsp;<#if destinationPostalAddress?has_content>[To: ${destinationPostalAddress.toName?if_exists}, Attn: ${destinationPostalAddress.attnName?if_exists}, ${destinationPostalAddress.address1?if_exists}, ${destinationPostalAddress.address2?if_exists}, ${destinationPostalAddress.city?if_exists}, ${destinationPostalAddress.stateProvinceGeoId?if_exists}, ${destinationPostalAddress.postalCode?if_exists}, ${destinationPostalAddress.countryGeoId?if_exists}]</#if></div>
        </td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Created</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left">
            <div class="tabletext">by [${shipment.createdByUserLogin?if_exists}] on ${(shipment.createdDate.toString())?if_exists}</div>
        </td>
    </tr>
    <tr>
        <td width="20%" align="right"><span class="tableheadtext">Last Updated</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left">
            <div class="tabletext">by [${shipment.lastModifiedByUserLogin?if_exists}] on ${(shipment.lastModifiedDate.toString())?if_exists}</div>
        </td>
    </tr>
</table>

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
			<td><div class="tabletext">OrderItem:<a href="/ordermgr/control/vieworder?order_id=${itemIssuance.orderId?if_exists}" class="buttontext">${itemIssuance.orderId?if_exists}</a>:${itemIssuance.orderItemSeqId?if_exists}</div></td>
			<td><div class="tabletext"><a href="<@ofbizUrl>/EditInventoryItem?inventoryItemId=${itemIssuance.inventoryItemId?if_exists}</@ofbizUrl>" class="buttontext">${itemIssuance.inventoryItemId?if_exists}</a></div></td>
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
        </td>
        <td>
            <div class="tableheadtext">Estimated (Start-Arrive)</div>
            <div class="tableheadtext">Actual (Start-Arrive)</div>
        </td>
        <td><div class="tableheadtext">Actual Cost</div></td>
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
        </td>
        <td>
            <div class="tabletext">[${(shipmentRouteSegment.estimatedStartDate.toString())?if_exists} - ${(shipmentRouteSegment.estimatedArrivalDate.toString())?if_exists}]</span>
            <div class="tabletext">[${(shipmentRouteSegment.actualStartDate.toString())?if_exists} - ${(shipmentRouteSegment.actualArrivalDate.toString())?if_exists}]</span>
        </td>
        <td><div class="tabletext">${(shipmentRouteSegment.actualCost?string.currency)?default("&nbsp;")}</div></td>
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
