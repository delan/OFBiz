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

<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>

<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td width="50%"><div class="boxhead">Inventory Items to Pick</div></td>
          <td width="50%"><div class="boxhead" align=right>&nbsp;</div></td>
        </tr>
      </table>
      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
        <tr>
          <td width="20%" align="left"><div class="tableheadtext">Location</div></td>
          <td width="20%" align="left"><div class="tableheadtext">Product [ID]</div></td>
          <td width="10%" align="left"><div class="tableheadtext">Quantity</div></td>
          <td width="50%" align="left"><div class="tableheadtext">OrderItems</div></td>
        </tr>
        <tr>
          <td colspan="10"><hr class="sepbar"></td>
        </tr>
        <#if inventoryItemInfoList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list inventoryItemInfoList as inventoryItemInfo>
            <#-- inventoryItemInfoList: List of Maps with inventoryItem, facilityLocation, orderItems, product, quantity -->
            <#assign inventoryItem = inventoryItemInfo.inventoryItem>
            <#assign facilityLocation = inventoryItemInfo.facilityLocation?if_exists>
            <#assign orderItems = inventoryItemInfo.orderItems>
            <#assign product = inventoryItemInfo.product>
            <#assign quantity = inventoryItemInfo.quantity>
            <tr class="${rowClass}">
              <td>
                <#if facilityLocation?has_content>
                  <div class="tabletext">${facilityLocation.facilityId}:${facilityLocation.areaId?if_exists}-${facilityLocation.aisleId?if_exists}-${facilityLocation.sectionId?if_exists}-${facilityLocation.levelId?if_exists}-${facilityLocation.positionId?if_exists}</div>
                <#else>
                  <div class="tabletext">&nbsp;</div>
                </#if>
              </td>
              <td>
                <#if product?has_content>
                  <div class="tabletext">${product.productName} [${product.productId}]</div>
                <#else>
                  <div class="tabletext">&nbsp;</div>
                </#if>
              </td>
              <td>
                  <div class="tabletext">${quantity}</div>
              </td>
              <td>
              	<#list orderItems as orderItem>
                  <div class="tabletext">${orderItem.orderId}:${orderItem.orderItemSeqId}-${orderItem.quantity}</div>
                </#list>
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
            <td colspan="4"><div class="head3">No inventory found to pick.</div></td>
          </tr>        
        </#if>
        <#if lookupErrorMessage?exists>
          <tr>
            <td colspan="4"><div class="head3">${lookupErrorMessage}</div></td>
          </tr>
        </#if>
      </table>
    </td>
  </tr>
</table>
        
<#else>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
</#if>
