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
 *@author     thierry.grauss@etu.univ-tours.fr (migration to uiLabelMap)
 *@version    $Rev$
 *@since      2.2
-->


<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

${pages.get("/facility/FacilityTabBar.ftl")}

<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td width="50%"><div class="boxhead">${uiLabelMap.ProductInventoryItemsToPick}</div></td>
          <td width="50%"><div class="boxhead" align=right>&nbsp;</div></td>
        </tr>
      </table>
      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
        <tr>
          <td width="10%" align="left"><div class="tableheadtext">${uiLabelMap.ProductLocation}</div></td>
          <td width="30%" align="left"><div class="tableheadtext">${uiLabelMap.ProductProductId}</div></td>
          <td width="10%" align="left"><div class="tableheadtext">${uiLabelMap.ProductToPick}</div></td>
          <td width="20%" align="left"><div class="tableheadtext">${uiLabelMap.OrderOrderItems}</div></td>
          <td width="10%" align="left"><div class="tableheadtext">${uiLabelMap.ProductInventoryItems}</div></td>
          <#--
          <td width="10%" align="left"><div class="tableheadtext">On Hand</div></td>
          <td width="10%" align="left"><div class="tableheadtext">Available</div></td>
          -->
        </tr>
        <tr>
          <td colspan="10"><hr class="sepbar"></td>
        </tr>
        <#if facilityLocationInfoList?has_content || inventoryItemInfoList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#-- facilityLocationInfoList: facilityLocation, productInfoList (product, quantity, inventoryItemList, orderItemList) -->
          <#list facilityLocationInfoList as facilityLocationInfo>
              <#assign facilityLocation = facilityLocationInfo.facilityLocation>
              <#assign productInfoList = facilityLocationInfo.productInfoList>
              <#list productInfoList as productInfo>
                <#assign product = productInfo.product>
                <#assign quantity = productInfo.quantity>
                <#assign inventoryItemList = productInfo.inventoryItemList>
                <#assign orderItemList = productInfo.orderItemList>
                <tr class="${rowClass}">
                  <td valign="top">
                    <div class="tabletext">${facilityLocation.facilityId}:${facilityLocation.areaId?if_exists}-${facilityLocation.aisleId?if_exists}-${facilityLocation.sectionId?if_exists}-${facilityLocation.levelId?if_exists}-${facilityLocation.positionId?if_exists}</div>
                  </td>
                  <td valign="top">
                    <#if product?has_content>
                      <div class="tabletext">${product.internalName} [${product.productId}]</div>
                    <#else>
                      <div class="tabletext">&nbsp;</div>
                    </#if>
                  </td>
                  <td valign="top">
                      <div class="tabletext">${quantity}</div>
                  </td>
                  <td valign="top">
                    <#list orderItemList as orderItem>
                      <div class="tabletext">${orderItem.orderId}:${orderItem.orderItemSeqId}-${orderItem.quantity}</div>
                    </#list>
                  </td>
                  <td valign="top">
                    <#list inventoryItemList as inventoryItem>
                      <div class="tabletext">${inventoryItem.inventoryItemId}:${inventoryItem.binNumber?if_exists}</div>
                    </#list>
                  </td>
                  <#--
                  <td valign="top">
                      <div class="tabletext">&nbsp;</div>
                  </td>
                  <td valign="top">
                      <div class="tabletext">&nbsp;</div>
                  </td>
                  -->
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "viewManyTR2">
                  <#assign rowClass = "viewManyTR1">
                <#else>
                  <#assign rowClass = "viewManyTR2">
                </#if>
              </#list>
          </#list>
          <#list inventoryItemInfoList as inventoryItemInfo>
            <#-- inventoryItemInfoList: List of Maps with inventoryItem, facilityLocation, orderItems, product, quantity, statusItem -->
            <#-- for this list, only display for inventoryItems with no location since those with locations will be displayed above -->
            <#if !inventoryItemInfo.facilityLocation?exists>
                <#assign inventoryItem = inventoryItemInfo.inventoryItem>
                <#assign facilityLocation = inventoryItemInfo.facilityLocation?if_exists>
                <#assign orderItems = inventoryItemInfo.orderItems>
                <#assign product = inventoryItemInfo.product>
                <#assign quantity = inventoryItemInfo.quantity>
                <#assign statusItem = inventoryItemInfo.statusItem?if_exists>
                <tr class="${rowClass}">
                  <td valign="top">
                    <#if facilityLocation?has_content>
                      <div class="tabletext">${facilityLocation.facilityId}:${facilityLocation.areaId?if_exists}-${facilityLocation.aisleId?if_exists}-${facilityLocation.sectionId?if_exists}-${facilityLocation.levelId?if_exists}-${facilityLocation.positionId?if_exists}</div>
                    <#else>
                      <div class="tabletext">&nbsp;</div>
                    </#if>
                  </td>
                  <td valign="top">
                    <#if product?has_content>
                      <div class="tabletext">${product.internalName} [${product.productId}]</div>
                    <#else>
                      <div class="tabletext">&nbsp;</div>
                    </#if>
                  </td>
                  <td valign="top">
                      <div class="tabletext">${quantity}</div>
                  </td>
                  <td valign="top">
                    <#list orderItems as orderItem>
                      <div class="tabletext">${orderItem.orderId}:${orderItem.orderItemSeqId}-${orderItem.quantity}</div>
                    </#list>
                  </td>
                  <td valign="top">
                    <div class="tabletext">${inventoryItem.inventoryItemId}:${inventoryItem.binNumber?if_exists}</div>
                  </td>
                  <#--
                  <#if inventoryItem.inventoryItemTypeId == "NON_SERIAL_INV_ITEM">
                    <td valign="top">
                      <div class="tabletext">${inventoryItem.quantityOnHandTotal?if_exists}</div>
                    </td>
                    <td valign="top">
                      <div class="tabletext">${inventoryItem.availableToPromiseTotal?if_exists}</div>
                    </td>
                  <#else>
                    <td colspan="2" valign="top">
                      <#if statusItem?exists>
                        <div class="tabletext">${statusItem.description}</div>
                      <#else>
                        <div class="tabletext">${inventoryItem.statusId?if_exists}</div>
                      </#if>
                    </td>
                  </#if>
                  -->
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "viewManyTR2">
                  <#assign rowClass = "viewManyTR1">
                <#else>
                  <#assign rowClass = "viewManyTR2">
                </#if>
              </#if>
          </#list>
        <#else>
          <tr>
            <td colspan="4"><div class="head3">${uiLabelMap.ProductNoInventoryFoundToPick}.</div></td>
          </tr>        
        </#if>
      </table>
    </td>
  </tr>
</table>
<br>
<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td width="50%"><div class="boxhead">${uiLabelMap.OrderOrdersToPack}</div></td>
          <td width="50%"><div class="boxhead" align=right>&nbsp;</div></td>
        </tr>
      </table>
      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
        <tr>
          <td width="10%" align="left"><div class="tableheadtext">${uiLabelMap.OrderOrderItem}</div></td>
          <td width="40%" align="left"><div class="tableheadtext">${uiLabelMap.ProductProductId}</div></td>
          <td width="20%" align="left"><div class="tableheadtext">${uiLabelMap.ProductQuantity}</div></td>
          <td width="20%" align="left"><div class="tableheadtext">${uiLabelMap.ProductInventoryAvailNotAvail}</div></td>
        </tr>
        <tr>
          <td colspan="10"><hr class="sepbar"></td>
        </tr>
        <#if orderHeaderInfoList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list orderHeaderInfoList as orderHeaderInfo>
    	    <#-- orderHeaderInfoList: List of Maps with orderHeader and orderItemInfoList which is List of Maps with orderItem, product and orderItemInventoryResList -->
            <#assign orderHeader = orderHeaderInfo.orderHeader>
            <#assign orderItemInfoList = orderHeaderInfo.orderItemInfoList>
            <#list orderItemInfoList as orderItemInfo>
              <#assign orderItem = orderItemInfo.orderItem>
              <#assign product = orderItemInfo.product>
              <#assign orderItemInventoryResList = orderItemInfo.orderItemInventoryResList>
              <tr class="${rowClass}">
                <td valign="top">
                  <div class="tabletext"><b>${orderHeaderInfo_index+1}</b>-${orderItem.orderId}:${orderItem.orderItemSeqId}</div>
                </td>
                <td valign="top">
                  <#if product?has_content>
                    <div class="tabletext">${product.internalName} [${product.productId}]</div>
                  <#else>
                    <div class="tabletext">&nbsp;</div>
                  </#if>
                </td>
                <td valign="top">
                  <div class="tabletext">${orderItem.quantity}</div>
                </td>
                <td valign="top">
                  <#list orderItemInventoryResList as orderItemInventoryRes>
                    <div class="tabletext">${orderItemInventoryRes.inventoryItemId}:${orderItemInventoryRes.quantity}:${orderItemInventoryRes.quantityNotAvailable?if_exists}</div>
                  </#list>
                </td>
              </tr>
            </#list>
              <#-- toggle the row color -->
              <#if rowClass == "viewManyTR2">
                <#assign rowClass = "viewManyTR1">
              <#else>
                <#assign rowClass = "viewManyTR2">
              </#if>        
          </#list>          
        <#else>
          <tr>
            <td colspan="4"><div class="head3">${uiLabelMap.ProductNoInventoryFoundToPick}.</div></td>
          </tr>        
        </#if>
      </table>
    </td>
  </tr>
</table>

<#if insufficientQohList?has_content || wrongQuantityReservedList?has_content>
<br>
<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td width="50%"><div class="boxhead">${uiLabelMap.ProductPickPackWarnings}</div></td>
          <td width="50%"><div class="boxhead" align=right>&nbsp;</div></td>
        </tr>
      </table>
      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
    	<#-- wrongQuantityReservedList: List of Maps with reservedQuantity and orderItem -->
    	<#-- insufficientQohList: List of Maps with inventoryItem and quantityNeeded -->
        <#assign rowClass = "viewManyTR2">
          <#list insufficientQohList?if_exists as insufficientQoh>
            <#assign inventoryItem = insufficientQoh.inventoryItem>
            <#assign quantityNeeded = insufficientQoh.quantityNeeded>
              <tr class="${rowClass}">
                <td>
                  <div class="tabletext">Inventory item with ID [${inventoryItem.inventoryItemId}] has ${inventoryItem.quantityOnHand?if_exists} on hand but needs ${quantityNeeded} for a full pick.</div>
                </td>
              </tr>
              <#-- toggle the row color -->
              <#if rowClass == "viewManyTR2">
                <#assign rowClass = "viewManyTR1">
              <#else>
                <#assign rowClass = "viewManyTR2">
              </#if>        
          </#list>          
          <#list wrongQuantityReservedList?if_exists as wrongQuantityReserved>
            <#assign orderItem = wrongQuantityReserved.orderItem>
            <#assign reservedQuantity = wrongQuantityReserved.reservedQuantity>
            <#assign issuedQuantity = wrongQuantityReserved.issuedQuantity>
            <#assign reservedIssuedQuantity = wrongQuantityReserved.reservedIssuedQuantity>
              <tr class="${rowClass}">
                <td>
                  <div class="tabletext">Order Item ${orderItem.orderId}:${orderItem.orderItemSeqId} is for ${orderItem.quantity} of product ID [${orderItem.productId}] but ${reservedQuantity} was reserved and ${issuedQuantity} has been issued. The total reserved and issued is ${reservedIssuedQuantity} which does not equal: ${orderItem.quantity} the order item quantity.</div>
                </td>
              </tr>
              <#-- toggle the row color -->
              <#if rowClass == "viewManyTR2">
                <#assign rowClass = "viewManyTR1">
              <#else>
                <#assign rowClass = "viewManyTR2">
              </#if>        
          </#list>          
      </table>
    </td>
  </tr>
</table>
</#if>

<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>
