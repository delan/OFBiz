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
 *@author     Brad Steiner (bsteiner@thehungersite.com)
 *@version    $Revision$
 *@since      2.2
-->

<#if hasPermission>
    <#if externalLoginKey?exists><#assign externalKeyParam = "&externalLoginKey="><#assign externalKeyParam = externalKeyParam + externalLoginKey><#else><#assign externalKeyParam = ""></#if>       

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">Inventory Items <span class="head2">for <#if product?exists>${(product.productName)?if_exists} </#if> [ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
        <a href="/facility/control/EditInventoryItem?productId=${productId}${externalKeyParam}" class="buttontext">[Create New Inventory Item for this Product]</a>
    </#if>
    <br>
        
    <#if product?exists && product.isVirtual.equals("Y")>
        <div class="head3">WARNING: This is a Virtual product and generally should not have inventory items associated with it.</div>
    </#if>
    
    <br>
    <#if productId?exists>
        <table border="1" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Item&nbsp;ID</b></div></td>
            <td><div class="tabletext"><b>Item&nbsp;Type</b></div></td>
            <td><div class="tabletext"><b>Status</b></div></td>
            <td><div class="tabletext"><b>Received</b></div></td>
            <td><div class="tabletext"><b>Expire</b></div></td>
            <td><div class="tabletext"><b>Facility or Container ID</b></div></td>
            <td><div class="tabletext"><b>Lot&nbsp;ID</b></div></td>
            <td><div class="tabletext"><b>BinNum</b></div></td>
            <td><div class="tabletext"><b>ATP/QOH or Serial#</b></div></td>
            <td><div class="tabletext">&nbsp;</div></td>
            <td><div class="tabletext">&nbsp;</div></td>
        </tr>
        <#list productInventoryItems as inventoryItem>
        <#assign curInventoryItemType = inventoryItem.getRelatedOne("InventoryItemType")>
        <#if inventoryItem.inventoryItemTypeId?exists && inventoryItem.inventoryItemTypeId.equals("SERIALIZED_INV_ITEM")>
            <#assign curStatusItem = inventoryItem.getRelatedOneCache("StatusItem")>
        </#if>
        <#if curInventoryItemType?exists> 
            <!-- context.setAttribute("curInventoryItemType", curInventoryItemType) -->                
            <tr valign="middle">
                <td><a href="/facility/control/EditInventoryItem?inventoryItemId=${(inventoryItem.inventoryItemId)?if_exists}${externalKeyParam}" class="buttontext">${(inventoryItem.inventoryItemId)?if_exists}</a></td>
                <td><div class="tabletext">&nbsp;${(curInventoryItemType.description)?if_exists}</div></td>
                <td><div class="tabletext">&nbsp;<#if curStatusItem?exists>${(curStatusItem.description)?if_exists}<#elseif inventoryItem.statusId?exists>[${inventoryItem.statusId}]</#if></div></td>
                <td><div class="tabletext">&nbsp;${(inventoryItem.datetimeReceived)?if_exists}</div></td>
                <td><div class="tabletext">&nbsp;${(inventoryItem.expireDate)?if_exists}</div></td>
                <#if inventoryItem.facilityId?exists && inventoryItem.containerId?exists>
                    <td><div class="tabletext" style="color: red;">Error: facility (${inventoryItem.facilityId}) 
                        AND container (${inventoryItem.containerId}) specified</div></td>
                <#elseif inventoryItem.facilityId?exists>
                    <td><span class="tabletext">F:&nbsp;</span><a href="/facility/control/EditFacility?facilityId=${inventoryItem.facilityId}${externalKeyParam}" class="buttontext">
                        ${inventoryItem.facilityId}</a></td>
                <#elseif (inventoryItem.containerId)?exists>
                    <td><span class="tabletext">C:&nbsp;</span><a href="<@ofbizUrl>/EditContainer?containerId=${inventoryItem.containerId }</@ofbizUrl>" class="buttontext">
                        ${inventoryItem.containerId}</a></td>
                <#else>
                    <td>&nbsp;</td>
                </#if>
                <td><div class="tabletext">&nbsp;${(inventoryItem.lotId)?if_exists}</div></td>
                <td><div class="tabletext">&nbsp;${(inventoryItem.binNumber)?if_exists}</div></td>
                <#if inventoryItem.inventoryItemTypeId?exists && inventoryItem.inventoryItemTypeId.equals("NON_SERIAL_INV_ITEM")>
                    <td>
                    <!-- Don"t want to allow this here, manual inventory level adjustments should be logged, etc -->
                    <!-- <FORM method=POST action="<@ofbizUrl>/UpdateInventoryItem</@ofbizUrl>">
                        <input type=hidden name="inventoryItemId" value="${inventoryItem.inventoryItemId}">
                        <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemTypeId" fullattrs="true"/>>
                        <input type=hidden name="productId" value="${(inventoryItem.productId)?if_exists}">
                        <input type=hidden name="partyId" value="$({inventoryItem.partyId)?if_exists}">
                        <input type=hidden name="statusId" value="$({inventoryItem.statusI)?if_exists}">
                        <input type=hidden name="facilityId" value="$({inventoryItem.facilityId)?if_exists}">
                        <input type=hidden name="containerId" value="$({inventoryItem.containerId)?if_exists}">
                        <input type=hidden name="lotId" value="$({inventoryItem.lotId)?if_exists}">
                        <input type=hidden name="UomId" value="$({inventoryItem.UomId)?if_exists}">
                        <input type=text size="5" name="availableToPromise" value="${(inventoryItem.availableToPromise)?if_exists}">
                        / <input type=text size="5" name="quantityOnHand" value="${(inventoryItem.quantityOnHand)?if_exists}">
                        <INPUT type=submit value="Set ATP/QOH">
                    </FORM> -->
                        <div class="tabletext">${(inventoryItem.availableToPromise)?default("NA")}
                        / ${(inventoryItem.quantityOnHand)?default("NA")}</div>
                    </td>
                <#elseif inventoryItem.inventoryItemTypeId.equals("SERIALIZED_INV_ITEM")>
                    <td><div class="tabletext">&nbsp;${(inventoryItem.serialNumber)?if_exists}</div></td>
                <#else>
                    <td><div class="tabletext" style="color: red;">Error: type ${(inventoryItem.inventoryItemTypeId)?if_exists} unknown, serialNumber (${(inventoryItem.serialNumber)?if_exists}) 
                        AND quantityOnHand (${(inventoryItem.quantityOnHand)?if_exists} specified</div></td>
                    <td>&nbsp;</td>
                </#if>
                <td>
                <a href="/facility/control/EditInventoryItem?inventoryItemId=${(inventoryItem.inventoryItemId)?if_exists}${externalKeyParam}" class="buttontext">
                [Edit]</a>
                </td>
                <td>
                <a href="<@ofbizUrl>/DeleteProductInventoryItem?productId=${productId}&inventoryItemId=${(inventoryItem.inventoryItemId)?if_exists}</@ofbizUrl>" class="buttontext">
                [Delete]</a>
                </td>
            </tr>
        </#if>
        </#list>
        </table>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
