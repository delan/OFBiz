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
 *@version    $Revision: 1.1 $
 *@since      2.2
-->

<#if hasPermission>

${pages.get("/facility/FacilityTabBar.ftl")}
    
    <div class="head1">Inventory Items <span class="head2">for <#if facility?exists>${(facility.facilityName)?if_exists}</#if> [ID:${facilityId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditFacility</@ofbizUrl>" class="buttontext">[New Facility]</a>
    <a href="<@ofbizUrl>/EditInventoryItem?facilityId=${facilityId}</@ofbizUrl>" class="buttontext">
    [Create New Inventory Item for this Facility]</a>
    
    <#if facilityInventoryItems?exists && (facilityInventoryItems.size() > 0)>
        <table border="0" width="100%" cellpadding="2">
            <tr>
            <td align=right>
                <b>
                <#if (viewIndex > 0)>
                <a href="<@ofbizUrl>/EditFacilityInventoryItems?facilityId=${facilityId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[Previous]</a> |
                </#if>
                <#if (listSize > 0)>
                    ${lowIndex+1} - ${highIndex+1} of ${listSize}
                </#if>
                <#if (listSize > highIndex+1)>
                | <a href="<@ofbizUrl>/EditFacilityInventoryItems?facilityId=${facilityId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[Next]</a>
                </#if>
                </b>
            </td>
            </tr>
        </table>
    </#if>
    <#if facilityId?exists>
        <table border="1" cellpadding="2" cellspacing="0" width="100%">
        <tr>
            <td><div class="tabletext"><b>Item&nbsp;ID</b></div></td>
            <td><div class="tabletext"><b>Item&nbsp;Type</b></div></td>
            <td><div class="tabletext"><b>Status</b></div></td>
            <td><div class="tabletext"><b>Received</b></div></td>
            <td><div class="tabletext"><b>Expire</b></div></td>
            <td><div class="tabletext"><b>Product ID</b></div></td>
            <td><div class="tabletext"><b>Location</b></div></td>
            <td><div class="tabletext"><b>Lot&nbsp;ID</b></div></td>
            <td><div class="tabletext"><b>BinNum</b></div></td>
            <td><div class="tabletext"><b>ATP/QOH or Serial#</b></div></td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </tr>
        <#list facilityInventoryItems[lowIndex..highIndex] as inventoryItem>
        <#assign curInventoryItemType = inventoryItem.getRelatedOne("InventoryItemType")>
        <#assign isQuantity = false>
        <#if (inventoryItem.quantityOnHand)?exists && (!(inventoryItem.serialNumber)?exists || (inventoryItem.serialNumber.length() == 0))><#assign isQuantity=true></#if>
        <tr valign="middle">
            <td><div class="tabletext">&nbsp;<a href="<@ofbizUrl>/EditInventoryItem?inventoryItemId=${(inventoryItem.inventoryItemId)?if_exists}&facilityId=${facilityId}</@ofbizUrl>" class="buttontext">${(inventoryItem.inventoryItemId)?if_exists}</a></div></td>
            <td><div class="tabletext">&nbsp;${(curInventoryItemType.description)?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;${(inventoryItem.statusId)?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;${(inventoryItem.datetimeReceived)?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;${(inventoryItem.expireDate)?if_exists}</div></td>
            <td><a href="/catalog/control/EditProduct?productId=${(inventoryItem.productId)?if_exists}" class="buttontext">
                ${(inventoryItem.productId)?if_exists}</a></td>
            <td><div class="tabletext">&nbsp;<a href="<@ofbizUrl>/EditFacilityLocation?facilityId=${facilityId}&locationSeqId=${(inventoryItem.locationSeqId)?if_exists}</@ofbizUrl>" class="buttontext">${(inventoryItem.locationSeqId)?if_exists}</a></div></td>
            <td><div class="tabletext">&nbsp;${(inventoryItem.lotId)?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;${(inventoryItem.binNumber)?if_exists}</div></td>   
            <#if (inventoryItem.inventoryItemTypeId)?exists && (inventoryItem.inventoryItemTypeId.equals("NON_SERIAL_INV_ITEM"))>
                <td>       
                    <div class="tabletext">${(inventoryItem.availableToPromise)?if_exists}
                    / ${(inventoryItem.quantityOnHand)?if_exists}</div>
                </td>
            <#elseif (inventoryItem.inventoryItemTypeId)?exists && inventoryItem.inventoryItemTypeId.equals("SERIALIZED_INV_ITEM")>
                    <td><div class="tabletext">&nbsp;${(inventoryItem.serialNumber)?if_exists}</div></td>
            <#else>
                <td><div class="tabletext" style="color: red;">Error: type ${(inventoryItem.inventoryItemTypeId)?if_exists} unknown, serialNumber (${(inventoryItem.serialNumber)?if_exists}) AND quantityOnHand (${(inventoryItem.quantityOnHand)?if_exists}) specified</div></td>
                <td>&nbsp;</td>
            </#if>
            <td>
            <a href="<@ofbizUrl>/EditInventoryItem?facilityId=${facilityId}&inventoryItemId=${(inventoryItem.inventoryItemId)?if_exists}&facilityId=${facilityId}</@ofbizUrl>" class="buttontext">
            [Edit]</a>
            </td>
            <td>
            <a href="<@ofbizUrl>/TransferInventoryItem?facilityId=${facilityId}&inventoryItemId=${(inventoryItem.inventoryItemId)?if_exists}&facilityId=${facilityId}</@ofbizUrl>" class="buttontext">
            [Transfer]</a>
            </td>
            <td>
            <a href="<@ofbizUrl>/DeleteFacilityInventoryItem?facilityId=${facilityId}&inventoryItemId=${(inventoryItem.inventoryItemId)?if_exists}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <#if (facilityInventoryItems.size() > 0)>
        <table border="0" width="100%" cellpadding="2">
            <tr>
            <td align=right>
                <b>
                <#if (viewIndex > 0)>
                <a href="<@ofbizUrl>/EditFacilityInventoryItems?facilityId=${facilityId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[Previous]</a> |
                </#if>
                <#if (listSize > 0)>
                ${lowIndex+1} - ${highIndex+1} of ${listSize}
                </#if>
                <#if (listSize > highIndex+1)>
                | <a href="<@ofbizUrl>/EditFacilityInventoryItems?facilityId=${facilityId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[Next]</a>
                </#if>
                </b>
            </td>
            </tr>
        </table>
        </#if>
        <br>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
