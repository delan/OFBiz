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
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Revision: 1.7 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>
${pages.get("/facility/FacilityTabBar.ftl")}

<div class="head1">${uiLabelMap.ProductEditInventoryItemWithId} [${inventoryItemId?if_exists}]</div>
<a href="<@ofbizUrl>/EditInventoryItem<#if facilityId?exists>?facilityId=${facilityId}</#if></@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewInventoryItem}]</a>
<#if inventoryItemId?exists>
	<a href="<@ofbizUrl>/TransferInventoryItem?inventoryItemId=${inventoryItemId}<#if facilityId?exists>&facilityId=${facilityId}</#if></@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductTransferItem}]</a>
</#if>

<#if inventoryItem?exists>
  <form action="<@ofbizUrl>/UpdateInventoryItem</@ofbizUrl>" method="POST" style="margin: 0;" name="inventoryItemForm">
  <table border="0" cellpadding="2" cellspacing="0">
  <input type=hidden name="inventoryItemId" value="${inventoryItemId}">
  <tr>
    <td align=right><div class="tabletext">${uiLabelMap.ProductInventoryItemId}</div></td>
    <td>&nbsp;</td>
    <td>
      <b>${inventoryItemId}</b> ${uiLabelMap.ProductNotModificationRecrationInventoryItem}
    </td>
  </tr>
<#else>
  <#if inventoryItemId?exists>
    <form action="<@ofbizUrl>/CreateInventoryItem</@ofbizUrl>" method="POST" style="margin: 0;" name="inventoryItemForm">
    <table border="0" cellpadding="2" cellspacing="0">
    <h3>${uiLabelMap.ProductNotFindInventoryItemWithId} "${inventoryItemId}".</h3>
  <#else>
    <form action="<@ofbizUrl>/CreateInventoryItem</@ofbizUrl>" method="POST" style="margin: 0;" name="inventoryItemForm">
    <table border="0" cellpadding="2" cellspacing="0">
  </#if>
</#if>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductInventoryItemTypeId}</div></td>
        <td>&nbsp;</td>
        <td>
          <select name="inventoryItemTypeId" size=1 class="selectBox">
            <#if inventoryItemType?exists>
                <option selected value="${inventoryItemType.inventoryItemTypeId}">${inventoryItemType.description}</option>
                <option value="${inventoryItemType.inventoryItemTypeId}">----</option>
            </#if>
            <#list inventoryItemTypes as nextInventoryItemType>
              <option value="${nextInventoryItemType.inventoryItemTypeId}">${nextInventoryItemType.description}</option>
            </#list>
          </select>
        </td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductProductId}</div></td>
        <td>&nbsp;</td>
        <td>
            <input type="text" name="productId" value="${inventoryItemData.productId?if_exists}" size="20" maxlength="20" class="inputBox">
            <#if (inventoryItem.productId)?has_content>
                <a href="/catalog/control/EditProduct?productId=${inventoryItem.productId}&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class="buttontext">[Edit&nbsp;Product&nbsp;${inventoryItem.productId}]</a>
            </#if>
        </td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.PartyPartyId}</div></td>
        <td>&nbsp;</td>
        <td><input type="text" name="partyId" value="${inventoryItemData.partyId?if_exists}" size="20" maxlength="20" class="inputBox"></td>
      </tr>
      <#if "SERIALIZED_INV_ITEM" == (inventoryItem.inventoryItemTypeId)?if_exists>
	      <tr>
	        <td align=right><div class="tabletext">${uiLabelMap.ProductStatus}</div></td>
	        <td>&nbsp;</td>
	        <td>
	          <select name="statusId" class="selectBox">
	              <#if (inventoryItem.statusId)?has_content>
		              <option value="${inventoryItem.statusId}">${(curStatusItem.description)?default("[" + inventoryItem.statusId + "]")}</option>
		              <option value="${inventoryItem.statusId}">----</option>
		          </#if>
		          <#if !tryEntity && requestParameters.statusId?has_content>
		              <#assign selectedStatusId = requestParameters.statusId>
		          </#if>
	              <#list statusItems as statusItem>
	                  <option value="${statusItem.statusId}"<#if selectedStatusId?if_exists == statusItem.statusId>${uiLabelMap.ProductSelected}</#if>>${statusItem.description}</option>
	              </#list>
	          </select>
	        </td>
	      </tr>
      </#if>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductDateReceived}</div></td>
        <td>&nbsp;</td>
        <td>
        	<input type="text" size="25" name="datetimeReceived" value="${(inventoryItemData.datetimeReceived.toString())?if_exists}" class="inputBox">
			<a href="javascript:call_cal(document.inventoryItemForm.datetimeReceived, '${(inventoryItemData.datetimeReceived.toString())?default(nowTimestampString)}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
        </td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductExpireDate}</div></td>
        <td>&nbsp;</td>
        <td>
        	<input type="text" size="25" name="expireDate" value="${(inventoryItemData.expireDate.toString())?if_exists}" class="inputBox">
			<a href="javascript:call_cal(document.inventoryItemForm.expireDate, '${(inventoryItemData.expireDate.toString())?default(nowTimestampString)}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
        </td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductFacilityContainer}</div></td>
        <td>&nbsp;</td>
        <td>
            <span class="tabletext">${uiLabelMap.ProductSelectFacility} : </span>
            <select name="facilityId" class="selectBox">
              <#if inventoryItem?exists>
	              <option value="${inventoryItem.facilityId}">${(facility.facilityName)?if_exists} [${inventoryItem.facilityId}]</option>
	              <option value="${inventoryItem.facilityId}">----</option>
	          </#if>
	          <#if !tryEntity && requestParameters.facilityId?has_content>
	              <#assign selectedFacilityId = requestParameters.facilityId>
	          </#if>
              <#list facilities as nextFacility>
                <option value="${nextFacility.facilityId}"<#if selectedFacilityId?if_exists == nextFacility.facilityId> ${uiLabelMap.ProductSelected}</#if>>${nextFacility.facilityName} [${nextFacility.facilityId}]</option>
              </#list>
            </select>
            <#if (inventoryItem.facilityId)?has_content>
                <a href="<@ofbizUrl>/EditFacility?facilityId=${inventoryItem.facilityId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductEditFacility} ${inventoryItem.facilityId}]</a>
            </#if>
            <br>
            <span class="tabletext">${uiLabelMap.ProductOrEnterContainerId} :</span>
            <input type="text" name="containerId" value="${inventoryItemData.containerId?if_exists}" size="20" maxlength="20" class="inputBox"/>
         </td>
       </tr>
      <tr>
        <td align="right"><div class="tabletext">${uiLabelMap.ProductFacilityLocation}</div></td>
        <td>&nbsp;</td>
        <td>
          <#if facilityLocation?exists>
            <div class="tabletext">
              <b>${uiLabelMap.ProductArea} :</b>&nbsp;${facilityLocation.areaId?if_exists}
              <b>${uiLabelMap.ProductAisle} :</b>&nbsp;${facilityLocation.aisleId?if_exists}
              <b>${uiLabelMap.ProductSection} :</b>&nbsp;${facilityLocation.sectionId?if_exists}
              <b>${uiLabelMap.ProductLevel} :</b>&nbsp;${facilityLocation.levelId?if_exists}
              <b>${uiLabelMap.ProductPosition}:</b>&nbsp;${facilityLocation.positionId?if_exists}
            </div>
          </#if>
          <#if inventoryItem?exists>
            <input type="text" size="20" maxsize="20" name="locationSeqId" value="${inventoryItem.locationSeqId?if_exists}" class="inputBox"/>
            &nbsp;<a href="<@ofbizUrl>/FindFacilityLocation?facilityId=${facilityId?if_exists}&inventoryItemId=${inventoryItemId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductFindLocation}]</a>
          <#else>
            <input type="text" size="20" maxsize="20" name="locationSeqId" value="${locationSeqId?if_exists}" class="inputBox">
          </#if>
        </td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductLotId}</div></td>
        <td>&nbsp;</td>
        <td><input type="text" name="lotId" value="${inventoryItemData.lotId?if_exists}" size="20" maxlength="20" class="inputBox"></td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductUomId}</div></td>
        <td>&nbsp;</td>
        <td><input type="text" name="uomId" value="${inventoryItemData.uomId?if_exists}" size="20" maxlength="20" class="inputBox"></td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductBinNumber}</div></td>
        <td>&nbsp;</td>
        <td><input type="text" name="binNumber" value="${inventoryItemData.binNumber?if_exists}" size="20" maxlength="20" class="inputBox"></td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductComments}</div></td>
        <td>&nbsp;</td>
        <td><input type="text" name="comments" value="${inventoryItemData.comments?if_exists}" size="60" maxlength="250" class="inputBox"></td>
      </tr>
    <#if "NON_SERIAL_INV_ITEM" == (inventoryItem.inventoryItemTypeId)?if_exists>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductAvailablePromiseQuantityHand}</div></td>
        <td>&nbsp;</td>
        <td>
        	<div class="tabletext">${inventoryItemData.availableToPromise?if_exists} / ${inventoryItemData.quantityOnHand?if_exists}</div>
        	<div class="tabletext">${uiLabelMap.ProductPhysicalInventoryVariance}</div>
        	<#-- The OLD, more dangerous, and less controlled/tracked way
            <input type=text size="5" name="availableToPromise" value="${inventoryItemData.availableToPromise?if_exists}" class="inputBox">
            / <input type=text size="5" name="quantityOnHand" value="${inventoryItemData.quantityOnHand?if_exists}" class="inputBox">
            -->
        </td>
      </tr>
    <#elseif "SERIALIZED_INV_ITEM" == (inventoryItem.inventoryItemTypeId)?if_exists>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductSerialNumber}</div></td>
        <td>&nbsp;</td>
        <td><input type="text" name="serialNumber" value="${inventoryItemData.serialNumber?if_exists}" size="30" maxlength="60" class="inputBox"></td>
      </tr>
    <#elseif inventoryItem?exists>
      <tr>
        <td align=right><div class="tabletext">${uiLabelMap.ProductSerialAtpQoh}</div></td>
        <td>&nbsp;</td>
        <td><div class="tabletext" style="color: red;">${uiLabelMap.ProductErrorType} [${inventoryItem.inventoryItemTypeId?if_exists}] ${uiLabelMap.ProductUnknownSpecifyType} .</div></td>
      </tr>
    </#if>
  <tr>
    <td colspan="2">&nbsp;</td>
    <td colspan="5"><input type="submit" value="${uiLabelMap.CommonUpdate}" class="smallSubmit"></td>
  </tr>
</table>
</form>

<#if "NON_SERIAL_INV_ITEM" == (inventoryItem.inventoryItemTypeId)?if_exists>
	<hr class="sepbar"/>
	<div class="head2">${uiLabelMap.ProductPhysicalInventoryVariances}</div>
	
	${createPhysicalInventoryAndVarianceWrapper.renderFormString()}
	<br>
	${viewPhysicalInventoryAndVarianceWrapper.renderFormString()}
</#if>

<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>

