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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Revision: 1.1 $
 *@since      3.0
-->

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
        <td width="20%" align="right"><span class="tableheadtext">Phone Numbers</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left">
          <div class="tabletext"><span class="tableheadtext">Origin:&nbsp;</span>${shipment.originTelecomNumberId?if_exists}&nbsp;<#if originTelecomNumber?has_content>[${originTelecomNumber.countryCode?if_exists}  ${originTelecomNumber.areaCode?if_exists} ${originTelecomNumber.contactNumber?if_exists}]</#if></div>
          <div class="tabletext"><span class="tableheadtext">Destination:&nbsp;</span>${shipment.destinationTelecomNumberId?if_exists}&nbsp;<#if destinationTelecomNumber?has_content>[${destinationTelecomNumber.countryCode?if_exists}  ${destinationTelecomNumber.areaCode?if_exists} ${destinationTelecomNumber.contactNumber?if_exists}]</#if></div>
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
  </#if>