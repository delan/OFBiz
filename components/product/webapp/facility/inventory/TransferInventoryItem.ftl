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
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Rev:$
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>
    <#if illegalInventoryItem?exists>
        <div class="errorMessage">${illegalInventoryItem}</div>
    </#if>

	${pages.get("/facility/FacilityTabBar.ftl")}

    <div class="head1">${uiLabelMap.ProductInventoryTransfer} <span class="head2">${uiLabelMap.CommonFrom}&nbsp;<#if facility?exists>${(facility.facilityName)?if_exists}</#if> [${uiLabelMap.CommonId}:${facilityId?if_exists}]</span></div>
    <a href="<@ofbizUrl>/EditFacility</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewFacility}]</a>

<#--    <table border="0" cellpadding="2" cellspacing="0">  -->

   <#if !(inventoryItem?exists)>
        <form method="post" action="<@ofbizUrl>/TransferInventoryItem</@ofbizUrl>" style="margin: 0;">
         <table border="0" cellpadding="2" cellspacing="0">
        <tr>
            <td width="25%" align="right"><div class="tabletext">${uiLabelMap.ProductInventoryItemId}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="25%">
            <input type="text" class="inputBox" name="inventoryItemId" size="20" maxlength="20">
            <input type="hidden" name="facilityId" value="${facilityId}">
            </td>
            <td width="50%">
            <input type="submit" value="${uiLabelMap.ProductGetItem}">
            </td>
        </tr>
        </table>
        </form>
    <#else>
       <#if !(inventoryTransfer?exists)>
            <form method="post" action="<@ofbizUrl>/CreateInventoryTransfer</@ofbizUrl>" name="transferform" style="margin: 0;">
        <#else>
            <form method="post" action="<@ofbizUrl>/UpdateInventoryTransfer</@ofbizUrl>" name="transferform" style="margin: 0;">
            <input type="hidden" name="inventoryTransferId" value="${inventoryTransferId?if_exists}">
        </#if>

        <script language="JavaScript">
            function setNow(field) { eval('document.transferform.' + field + '.value="${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()}"'); }
        </script>

        <table border="0" cellpadding="2" cellspacing="0">
        <input type="hidden" name="inventoryItemId" value="${inventoryItemId?if_exists}">
        <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
        <input type="hidden" name="locationSeqId" value="${(inventoryItem.locationSeqId)?if_exists}">
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%"align="right"><div class="tabletext">${uiLabelMap.ProductInventoryItemId}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <b>${inventoryItemId}</b>
            </td>
        </tr>

        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductInventoryItemTypeId}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <#if inventoryItemType?exists>
                <div class="tabletext">${(inventoryItemType.description)?if_exists}</div>
            </#if>
            </td>
        </tr>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductProductId}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
                <#if inventoryItem?exists && (inventoryItem.productId)?exists>
                    <a href="/catalog/control/EditProduct?productId=${(inventoryItem.productId)?if_exists}" class="buttontext">${(inventoryItem.productId)?if_exists}</a>
                </#if>
            </td>
        </tr>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductStatus}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <#if inventoryStatus?exists>
                <div class="tabletext">${(inventoryStatus.description)?if_exists}</div>
            <#else>
                <div class="tabletext">--</div>
            </#if>
            </td>
        </tr>

        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductComments}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <#if (inventoryItem.comments)?exists>
                <div class="tabletext">${inventoryItem.comments}</td></div>
            <#else>
                <div class="tabletext">--</div>
            </#if>
            </td>
        </tr>

        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductSerialAtpQoh}</div></td>
            <td width="6%">&nbsp;</td>
            <#if inventoryItem?exists && inventoryItem.inventoryItemTypeId.equals("NON_SERIAL_INV_ITEM")>
                <td width="74%">
                    <div class="tabletext">${(inventoryItem.availableToPromise)?if_exists}&nbsp;
                    /&nbsp;${(inventoryItem.quantityOnHand)?if_exists}</div>
                </td>
            <#elseif inventoryItem?exists && inventoryItem.inventoryItemTypeId.equals("SERIALIZED_INV_ITEM")>
                <td width="74%"><div class="tabletext">${(inventoryItem.serialNumber)?if_exists}</div></td>
            <#elseif inventoryItem?exists>
                <td width="74%"><div class="tabletext" style="color: red;">${uiLabelMap.ProductErrorType} ${(inventoryItem.inventoryItemTypeId)?if_exists} ${uiLabelMap.ProductUnknownSpecifyType}.</div></td>
            </#if>
        </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td colspan="3"><hr class="sepbar"></td>
    </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="left" nowrap><div class="tabletext">${uiLabelMap.ProductTransferStatus}</div></td>
        <td width="6%">&nbsp;</td>
        <td width="74%">
        <select name="statusId" class="selectBox">
            <#if (inventoryTransfer.getRelatedOneCache("StatusItem"))?exists>
                <#assign curStatusItem = inventoryTransfer.getRelatedOneCache("StatusItem")>
            </#if>
            <option value="${(inventoryTransfer.statusId)?if_exists}">${(curStatusItem.description)?if_exists}</option>
            <#list statusItems as statusItem>
            <option value="${(statusItem.statusId)?if_exists}">${(statusItem.description)?if_exists}</option>
            </#list>
        </select>
        </td>
    </tr>
    <tr>
       <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductTransferSendDate}</div></td>
        <td width="6%">&nbsp;</td>
        <td width="74%">
        <input type="text" name="sendDate" value="${(inventoryTransfer.sendDate)?if_exists}" size="22" class="inputBox">
        <a href="#" onclick="setNow('sendDate')" class="buttontext">[${uiLabelMap.CommonNow}]</a>
        </td>
    </tr>
    <#if !(inventoryTransfer?exists)>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductToFacilityContainer}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <span class="tabletext">${uiLabelMap.ProductSelectFacility}:</span>
            <select name="facilityIdTo" class="selectBox">
                <#list facilities as nextFacility>
                <option value="${(nextFacility.facilityId)?if_exists}">${(nextFacility.facilityName)?if_exists} [${(nextFacility.facilityId)?if_exists}]</option>
                </#list>
            </select>
            <br>
            <span class="tabletext">${uiLabelMap.ProductOrEnterContainerId}:</span>
            <input type="text" name="containerIdTo" value="${(inventoryTransfer.containerIdTo)?if_exists}" size="20" maxlength="20" class="inputBox">
            </td>
        </tr>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductToLocation}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <input type="text" size="20" name="locationSeqIdTo" value="${(inventoryTransfer.locationSeqIdTo)?if_exists}" maxlength="20" class="inputBox">
            </td>
        </tr>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductComments}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <input type="text" name="comments" size="60" maxlength="250" class="inputBox">
            </td>
        </tr>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductQuantityToTransfer}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <#if inventoryItem?exists && inventoryItem.inventoryItemTypeId.equals("NON_SERIAL_INV_ITEM")>
                <input type=text size="5" name="xferQty" value="${(inventoryItem.availableToPromise)?if_exists}" class="inputBox">
            <#elseif inventoryItem?exists && inventoryItem.inventoryItemTypeId.equals("SERIALIZED_INV_ITEM")>
                <input type="hidden" name="xferQty" value="1">
                <div class="tabletext">1</div>
            <#elseif inventoryItem?exists>
                <div class="tabletext" style="color: red;">${uiLabelMap.ProductErrorType} ${(inventoryItem.inventoryItemTypeId)?if_exists} ${uiLabelMap.ProductUnknownSpecifyType}.</div>
            </#if>
            </td>
        </tr>
    <#else>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductTransferReceiveDate}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <input type="text" name="receiveDate" value="${(inventoryTransfer.receiveDate)?if_exists}" size="22" class="inputBox">
            <a href="#" onclick="setNow('receiveDate')" class="buttontext">[${uiLabelMap.CommonNow}]</a>
            </td>
        </tr>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductToFacilityContainer}</div></td>
            <td width="6%">&nbsp;</td>
            <#assign fac = delegator.findByPrimaryKey("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", inventoryTransfer.facilityIdTo))>
            <td width="74%"><div class="tabletext">${(fac.facilityName)?if_exists}</div></td>
        </tr>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductToLocation}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <input type="text" size="20" name="locationSeqIdTo" value="${(inventoryTransfer.locationSeqIdTo)?if_exists}" maxlength="20" class="inputBox">
            </td>
        </tr>
        <tr>
            <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap><div class="tabletext">${uiLabelMap.ProductComments}</div></td>
            <td width="6%">&nbsp;</td>
            <td width="74%">
            <input type="text" name="comments" value="${(inventoryTransfer.comments)?if_exists}" size="60" maxlength="250" class="inputBox">
            </td>
        </tr>
    </#if>
    <tr>
        <td colspan="2">&nbsp;</td>
        <#if !(inventoryTransfer?exists)>
            <td colspan="1" align=left><input type="submit" value="${uiLabelMap.ProductTransfer}"></td>
        <#else>
            <td colspan="1" align=left><input type="submit" value="${uiLabelMap.CommonUpdate}"></td>
        </#if>
    </tr>
    </table>
    </form>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>
