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
<script language="JavaScript">
<!-- //
function lookupShipments() {
    shipmentIdValue = document.lookupShipmentForm.shipmentId.value;
    if (shipmentIdValue.length > 1) {
        document.lookupShipmentForm.action = "<@ofbizUrl>/ViewShipment</@ofbizUrl>";
    } else {
        document.lookupShipmentForm.action = "<@ofbizUrl>/FindShipment</@ofbizUrl>";
    }
    document.lookupShipmentForm.submit();
}
// -->
</script>

<form method="post" name="lookupShipmentForm" action="<@ofbizUrl>/FindShipment</@ofbizUrl>">
<input type="hidden" name="lookupFlag" value="Y">
<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td><div class="boxhead">Find Orders</div></td>
          <td align="right">
            <div class="tabletext">
              <a href="<@ofbizUrl>/EditShipment</@ofbizUrl>" class="submenutext">New Shipment</a>
              <a href="javascript:lookupShipments();" class="submenutext">Lookup Shipment(s)</a>
            </div>
          </td>
        </tr>
      </table>
      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
        <tr>
          <td align="center" width="100%">
            <table border="0" cellspacing="0" cellpadding="2">
              <tr>
                <td width="25%" align="right"><div class="tableheadtext">Shipment ID:</div></td>
                <td width="5%">&nbsp;</td>
                <td><input type="text" class="inputBox" name="shipmentId"></td>
              </tr>             
              <tr>
                <td width="25%" align="right"><div class="tableheadtext">Shipment Type:</div></td>
                <td width="5%">&nbsp;</td>
                <td>
                  <select name="shipmentTypeId" class="selectBox">
                    <#if currentShipmentType?has_content>
                    <option value="${currentShipmentType.shipmentTypeId}">${currentShipmentType.description}</option>
                    <option value="${currentShipmentType.shipmentTypeId}">---</option>
                    </#if>
                    <option value="">Any Shipment Type</option>                
                    <#list shipmentTypes as shipmentType>
                      <option value="${shipmentType.shipmentTypeId}">${shipmentType.description}</option>
                    </#list>
                  </select>
                </td>
              </tr>                                           
              <tr>
                <td width="25%" align="right"><div class="tableheadtext">Origin Facility:</div></td>
                <td width="5%">&nbsp;</td>
                <td>
                  <select name="originFacilityId" class="selectBox">
                    <#if currentOriginFacility?has_content>
                    <option value="${currentOriginFacility.facilityId}">${currentOriginFacility.facilityName} [${currentOriginFacility.facilityId}]</option>
                    <option value="${currentOriginFacility.facilityId}">---</option>
                    </#if>
                    <option value="">Any Facility</option>                
                    <#list facilities as facility>
                      <option value="${facility.facilityId}">${facility.facilityName} [${facility.facilityId}]</option>
                    </#list>
                  </select>
                </td>
              </tr>                                           
              <tr>
                <td width="25%" align="right"><div class="tableheadtext">Destination Facility:</div></td>
                <td width="5%">&nbsp;</td>
                <td>
                  <select name="destinationFacilityId" class="selectBox">
                    <#if currentDestinationFacility?has_content>
                    <option value="${currentDestinationFacility.facilityId}">${currentDestinationFacility.facilityName} [${currentDestinationFacility.facilityId}]</option>
                    <option value="${currentDestinationFacility.facilityId}">---</option>
                    </#if>
                    <option value="">Any Facility</option>                
                    <#list facilities as facility>
                      <option value="${facility.facilityId}">${facility.facilityName} [${facility.facilityId}]</option>
                    </#list>
                  </select>
                </td>
              </tr>                                           
              <tr>
                <td width="25%" align="right"><div class="tableheadtext">Status:</div></td>
                <td width="5%">&nbsp;</td>
                <td>
                  <select name="statusId" class="selectBox"> 
                    <#if currentStatus?has_content>
                    <option value="${currentStatus.statusId}">${currentStatus.description}</option>
                    <option value="${currentStatus.statusId}">---</option>
                    </#if>                                     
                    <option value="">Any Shipment Status</option>                   
                    <#list shipmentStatuses as shipmentStatus>
                      <option value="${shipmentStatus.statusId}">${shipmentStatus.description}</option>
                    </#list>
                  </select>
                </td>
              </tr>                            
              <tr>
                <td width="25%" align="right">
                  <div class="tableheadtext">Date Filter:</div>
                </td>
                <td width="5%">&nbsp;</td>
                <td>
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>                      
                      <td>                        
                        <input type="text" size="25" class="inputBox" name="minDate" value="${requestParameters.minDate?if_exists}">
                        <a href="javascript:call_cal(document.lookupShipmentForm.minDate, '${fromDateStr}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                        <span class="tabletext">From</span>
                      </td>
                    </tr>
                    <tr>                  
                      <td>                       
                        <input type="text" size="25" class="inputBox" name="maxDate" value="${requestParameters.maxDate?if_exists}">
                        <a href="javascript:call_cal(document.lookupShipmentForm.maxDate, '${thruDateStr}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>                   
                        <span class="tabletext">Thru</span>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</form> 


<#if shipmentList?exists>
<br>
<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td width="50%"><div class="boxhead">Shipments Found</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <#if 0 < shipmentList?size>             
                <#if 0 < viewIndex>
                  <a href="<@ofbizUrl>/FindShipment?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}${paramList}</@ofbizUrl>" class="submenutext">Previous</a>
                <#else>
                  <span class="submenutextdisabled">Previous</span>
                </#if>
                <#if 0 < listSize>
                  <span class="submenutextinfo">${lowIndex+1} - ${highIndex} of ${listSize}</span>
                </#if>
                <#if highIndex < listSize>
                  <a href="<@ofbizUrl>/FindShipment?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}${paramList}</@ofbizUrl>" class="submenutextright">Next</a>
                <#else>
                  <span class="submenutextrightdisabled">Next</span>
                </#if>
              </#if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
        <tr>
          <td width="5%" align="left"><div class="tableheadtext">ShipmentID</div></td>
          <td width="15%" align="left"><div class="tableheadtext">Type</div></td>
          <td width="10%" align="left"><div class="tableheadtext">Status</div></td>
          <td width="25%" align="left"><div class="tableheadtext">Origin Facility</div></td>
          <td width="25%" align="left"><div class="tableheadtext">Dest. Facility</div></td>
          <td width="15%" align="left"><div class="tableheadtext">Ship Date</div></td>
          <td width="5%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan="10"><hr class="sepbar"></td>
        </tr>
        <#if shipmentList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list shipmentList[lowIndex..highIndex-1] as shipment>
            <#assign originFacility = shipment.getRelatedOneCache("OriginFacility")?if_exists>
            <#assign destinationFacility = shipment.getRelatedOneCache("DestinationFacility")?if_exists>
            <#assign statusItem = shipment.getRelatedOneCache("StatusItem")>
            <#assign shipmentType = shipment.getRelatedOneCache("ShipmentType")>
            <tr class="${rowClass}">
              <td><a href="<@ofbizUrl>/ViewShipment?shipmentId=${shipment.shipmentId}</@ofbizUrl>" class="buttontext">${shipment.shipmentId}</a></td>
              <td><div class="tabletext">${shipmentType.description?default(shipmentType.shipmentTypeId?default(""))}</div></td>
              <td><div class="tabletext">${statusItem.description?default(statusItem.statusId?default("N/A"))}</div></td>
              <td><div class="tabletext">${(originFacility.facilityName)?if_exists} [${shipment.originFacilityId?if_exists}]</div></td>
              <td><div class="tabletext">${(destinationFacility.facilityName)?if_exists} [${shipment.destinationFacilityId?if_exists}]</div></td>
              <td><div class="tabletext"><nobr>${(shipment.estimatedShipDate.toString())?if_exists}</nobr></div></td>
              <td align="right">
                <a href="<@ofbizUrl>/ViewShipment?shipmentId=${shipment.shipmentId}</@ofbizUrl>" class="buttontext">View</a>
              </td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "viewManyTR2">
              <#assign rowClass = "viewManyTR1">
            <#else>
              <#assign rowClass = "viewManyTR2">
            </#if>
          </#list>          
        <#else>
          <tr>
            <td colspan="4"><div class="head3">No shipments found.</div></td>
          </tr>        
        </#if>
      </table>
    </td>
  </tr>
</table>
</#if>

<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
