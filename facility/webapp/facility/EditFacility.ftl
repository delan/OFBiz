<#--
 *  Description: None
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones
 *@created    May 10 2002
 *@version    1.0
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

<#if facility?exists && facilityId?has_content>
  <div class='tabContainer'>
    <a href="<@ofbizUrl>/EditFacility?facilityId=${facilityId}</@ofbizUrl>" class="tabButtonSelected">Facility</a>
    <a href="<@ofbizUrl>/EditFacilityGroups?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Groups</a>
    <a href="<@ofbizUrl>/FindFacilityLocations?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Locations</a>
    <a href="<@ofbizUrl>/EditFacilityRoles?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Roles</a>
    <a href="<@ofbizUrl>/EditFacilityInventoryItems?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Inventory&nbsp;Items</a>
    <a href="<@ofbizUrl>/ReceiveInventory?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Inventory&nbsp;Receive</a>
    <a href="<@ofbizUrl>/FindFacilityTransfers?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Inventory&nbsp;Xfers</a>
    <a href="<@ofbizUrl>/ReceiveReturn?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Receive Return</a>
    <a href="<@ofbizUrl>/PicklistOptions?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Picklist</a>
    <a href="<@ofbizUrl>/FindShipment?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Shipments</a>
  </div>
</#if>

<div class="head1">Facility <span class='head2'>${facility.facilityName?if_exists} [ID:${facilityId?if_exists}]</span></div>
<a href="<@ofbizUrl>/EditFacility</@ofbizUrl>" class="buttontext">[New Facility]</a>
<#if facilityId?has_content>
	<a href="/workeffort/control/month?facilityId=${facilityId}&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class="buttontext">[View Calendar]</a>
</#if>

<#if facility?exists && facilityId?has_content>
  <form action="<@ofbizUrl>/UpdateFacility</@ofbizUrl>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="facilityId" value="${facilityId?if_exists}">
  <tr>
    <td align=right><div class="tabletext">Facility ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b>${facilityId?if_exists}</b> (This cannot be changed without re-creating the facility.)
    </td>
  </tr>
<#else>
  <form action="<@ofbizUrl>/CreateFacility</@ofbizUrl>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <#if facilityId?exists>
    <h3>Could not find facility with ID "${facilityId?if_exists}".</h3>
  </#if>
</#if>
  <tr>
    <td width="26%" align=right><div class="tabletext">Facility Type Id</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <select name="facilityTypeId" size=1 class='selectBox'>
        <option selected value='${facilityType.facilityTypeId?if_exists}'>${facilityType.description?if_exists}</option>
        <option value='${facilityType.facilityTypeId?if_exists}'>----</option>
        <#list facilityTypes as nextFacilityType>
          <option value='${nextFacilityType.facilityTypeId?if_exists}'>${nextFacilityType.description?if_exists}</option>
        </#list>
      </select>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Name</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" class="inputBox" name="facilityName" value="${facility.facilityName?if_exists}" size="30" maxlength="60"></td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Square Footage</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" class="inputBox" name="squareFootage" value="${facility.squareFootage?if_exists}" size="10" maxlength="20"></td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" class="inputBox" name="description" value="${facility.description?if_exists}" size="60" maxlength="250"></td>
  </tr>

  <tr>
    <td colspan='2'>&nbsp;</td>
    <td colspan='1' align=left><input type="submit" name="Update" value="Update"></td>
  </tr>
</table>
</form>

<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>

