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
 *@author     David E. Jones
 *@author     Brad Steiner
 *@version    $Revision$
 *@since      2.2
-->

<#if hasPermission>

${pages.get("/facility/FacilityTabBar.ftl")}
    
    <div class="head1">Inventory Transfers <span class="head2">for&nbsp;<#if facility?exists>${(facility.facilityName)?if_exists}</#if> [ID:${facilityId?if_exists}]</span></div>
    <a href="<@ofbizUrl>/EditFacility</@ofbizUrl>" class="buttontext">[New Facility]</a>
    <#if activeOnly>
        <a href="<@ofbizUrl>/FindFacilityTransfers?facilityId=${facilityId}&activeOnly=false</@ofbizUrl>" class="buttontext">[Active and Inactive]</a>
    <#else>
        <a href="<@ofbizUrl>/FindFacilityTransfers?facilityId=${facilityId}&activeOnly=true</@ofbizUrl>" class="buttontext">[Active Only]</a>
    </#if>
    <a href="<@ofbizUrl>/TransferInventoryItem?facilityId=${facilityId}</@ofbizUrl>" class="buttontext">[Inventory Transfer]</a>
    
    <br>
    <#if (toTransfers.size() > 0)>
        <br>
        <div class="head1">To:<span class="head2">&nbsp;<#if facility?exists>${(facility.facilityName)?if_exists}</#if> [ID:${facilityId?if_exists}]</span></div>
        <table border="1" cellpadding="2" cellspacing="0" width="100%">
            <tr>
            <td><div class="tabletext"><b>Transfer ID</b></div></td>
            <td><div class="tabletext"><b>Item</b></div></td>      
            <td><div class="tabletext"><b>From</b></div></td>
            <td><div class="tabletext"><b>Send Date</b></div></td>
            <td><div class="tabletext"><b>Status</b></div></td>
            <td>&nbsp;</td>
            </tr>
        
            <#list toTransfers as transfer>
            <tr>
            <td><div class="tabletext"><a href="<@ofbizUrl>/EditInventoryTransfer?inventoryTransferId=${(transfer.inventoryTransferId)?if_exists}</@ofbizUrl>" class="buttontext">&nbsp;${(transfer.inventoryTransferId)?if_exists}</a></div></td>
            <td><div class="tabletext"><a href="<@ofbizUrl>/EditInventoryItem?inventoryItemId=${(transfer.inventoryItemId)?if_exists}</@ofbizUrl>" class="buttontext">&nbsp;${(transfer.inventoryItemId)?if_exists}</a></div></td>      
            <td>
                <#assign fac = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", transfer.getString("facilityId")))>
                <div class="tabletext"><a href="<@ofbizUrl>/EditFacility?facilityId=${(transfer.facilityId)?if_exists}</@ofbizUrl>" class="buttontext">&nbsp;<#if fac?exists>${(fac.facilityName)?if_exists}</#if>&nbsp;[${(transfer.facilityId)?if_exists}]</a></div>
            </td>
            <td><div class="tabletext">&nbsp;${(transfer.sendDate)?if_exists}</div></td>
            <td>
                <#assign transferStatus = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", transfer.statusId))>
                <div class="tabletext">&nbsp;${(transferStatus.description)?if_exists}</div>
            </td>
            <td align="center"><div class="tabletext"><a href="<@ofbizUrl>/TransferInventoryItem?inventoryTransferId=${(transfer.inventoryTransferId)?if_exists}</@ofbizUrl>" class="buttontext">[Edit]</a></div></td>
            </tr>
            </#list>
        </table>
    </#if>
    
    <#if (fromTransfers.size() > 0)>
        <br>
        <div class="head1">From:<span class="head2">&nbsp;<#if facility?exists>${(facility.facilityName)?if_exists}</#if> [ID:${facilityId?if_exists}]</span></div>
        <table border="1" cellpadding="2" cellspacing="0" width="100%">
            <tr>
            <td><div class="tabletext"><b>Transfer ID</b></div></td>
            <td><div class="tabletext"><b>Item</b></div></td>      
            <td><div class="tabletext"><b>To</b></div></td>
            <td><div class="tabletext"><b>Send Date</b></div></td>
            <td><div class="tabletext"><b>Status</b></div></td>
            <td>&nbsp;</td>
            </tr>
        
            <#list fromTransfers as transfer>
            <tr>
            <td><div class="tabletext"><a href="<@ofbizUrl>/TransferInventoryItem?inventoryTransferId=${(transfer.inventoryTransferId)?if_exists}</@ofbizUrl>" class="buttontext">&nbsp;${(transfer.inventoryTransferId)?if_exists}</a></div></td>
            <td><div class="tabletext"><a href="<@ofbizUrl>/EditInventoryItem?inventoryItemId=${(transfer.inventoryItemId)?if_exists}</@ofbizUrl>" class="buttontext">&nbsp;${(transfer.inventoryItemId)?if_exists}</a></div></td>      
            <td>
                <#assign fac = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", transfer.getString("facilityIdTo")))>
                <div class="tabletext"><a href="<@ofbizUrl>/EditFacility?facilityId=${(transfer.facilityIdTo)?if_exists}</@ofbizUrl>" class="buttontext">&nbsp;<#if fac?exists>${(fac.facilityName)?if_exists}</#if>&nbsp;[${(transfer.facilityIdTo)?if_exists}]</a></div>
            </td>
            <td><div class="tabletext">&nbsp;${(transfer.sendDate)?if_exists}</div></td>
            <td>
                <#assign transferStatus = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", transfer.statusId))>
                <div class="tabletext">&nbsp;${(transferStatus.description)?if_exists}</div>
            </td>
            <td align="center"><div class="tabletext"><a href="<@ofbizUrl>/TransferInventoryItem?inventoryTransferId=${(transfer.inventoryTransferId)?if_exists}</@ofbizUrl>" class="buttontext">[Edit]</a></div></td>
            </tr>
            </#list>
        </table>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>

