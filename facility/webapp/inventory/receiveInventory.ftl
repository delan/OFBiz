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
 *@version    $Revision$
 *@since      2.2
-->

<#if security.hasEntityPermission("FACILITY", "_CREATE", session)>

<#if invalidProductId?exists>
<div class='errorMessage'>${invalidProductId}</div>
</#if>

<#if requestParameters.facilityId?exists>
  <div class='tabContainer'>
    <a href="<@ofbizUrl>/EditFacility?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="tabButton">Facility</a>
    <a href="<@ofbizUrl>/EditFacilityGroups?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="tabButton">Groups</a>
    <a href="<@ofbizUrl>/FindFacilityLocations?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="tabButton">Locations</a>
    <a href="<@ofbizUrl>/EditFacilityRoles?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="tabButton">Roles</a>
    <a href="<@ofbizUrl>/EditFacilityInventoryItems?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="tabButton">Inventory&nbsp;Items</a>
    <a href="<@ofbizUrl>/ReceiveInventory?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="tabButtonSelected">Inventory&nbsp;Receive</a>
    <a href="<@ofbizUrl>/FindFacilityTransfers?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="tabButton">Inventory&nbsp;Xfers</a>
  </div>
</#if>

<div class="head1">Receive Inventory <span class='head2'>into&nbsp;<#if facility?has_content>"${facility.facilityName?default("Not Defined")}"</#if> [ID:${facility.facilityId?if_exists}]</span></div>
<a href="<@ofbizUrl>/EditFacility</@ofbizUrl>" class="buttontext">[New Facility]</a>

<script language='JavaScript'>
    function setNow(field) { eval('document.receiveform.' + field + '.value="${Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().toString()}"'); }
</script>

<div>&nbsp;</div>
<#if !product?has_content && !purchaseOrder?has_content>
    <form name="receiveform" method="post" action="<@ofbizUrl>/ReceiveInventory</@ofbizUrl>" style='margin: 0;'>
    <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}">
	<table border='0' cellpadding='2' cellspacing='0'>
      <tr>        
        <td width="25%" align='right'><div class="tabletext">Purchase Order Number</div></td>
        <td>&nbsp;</td>
        <td width="25%">
          <input type="text" class="inputBox" name="purchaseOrderId" size="20" maxlength="20">          
        </td> 
        <td><div class='tabletext'>&nbsp;(Leave empty for single product receiving)</div></td>
      </tr>
      <tr>        
        <td width="25%" align='right'><div class="tabletext">Product ID</div></td>
        <td>&nbsp;</td>
        <td width="25%">
          <input type="text" class="inputBox" name="productId" size="20" maxlength="20">         
        </td>       
        <td><div class='tabletext'>&nbsp;(Leave empty for entire PO receiving)</div></td>        
      </tr>      
      <tr>
        <td colspan="2">&nbsp;</td>
        <td colspan="2">
          <a href="javascript:document.receiveform.submit();" class="buttontext">Receive Product(s)</a>
        </td>
      </tr>        
    </table>
    </form>
<#else>
	<form method="post" action="<@ofbizUrl>/receiveInventoryProduct</@ofbizUrl>" name='receiveform' style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
      <input type="hidden" name="productId" value="${requestParameters.productId?if_exists}">
      <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}">
      <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}">
      <#if purchaseOrder?has_content>
      <input type="hidden" name="orderId" value="${purchaseOrder.orderId}">
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Purchase Order</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <b>${purchaseOrder.orderId}</b>
        </td>                
      </tr>
      </#if>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Product ID</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <b>${requestParameters.productId?if_exists}</b>
        </td>                
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Product Name</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <div class="tabletext">${product.productName?if_exists}</div>
        </td>                
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Product Description</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <div class="tabletext">${product.description?if_exists}</div>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Item Description</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='itemDescription' size='30' maxlength='60' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Inventory Item <br>(optional will create new if empty)</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='inventoryItemId' size='20' maxlength='20' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Inventory Item Type</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <select name="inventoryItemTypeId" size=1 class="selectBox">  
            <#list inventoryItemTypes as nextInventoryItemType>                      
              <option value='${nextInventoryItemType.inventoryItemTypeId}'>${nextInventoryItemType.description?default(nextInventoryItemType.inventoryItemTypeId)}</option>
            </#list>
          </select>
        </td>                
      </tr>	
      <tr>
        <td colspan="4">&nbsp;</td>
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Date Received</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='datetimeReceived' size='24' value="${Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().toString()}" class="inputBox">
          <!--<a href='#' onclick='setNow("datetimeReceived")' class='buttontext'>[Now]</a>-->
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Facility Location</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='locationSeqId' size='20' maxlength="20" class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Rejected Reason</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <select name="rejectionId" size='1' class='selectBox'>   
            <option></option>    
            <#list rejectReasons as nextRejection>                 
              <option value='${nextRejection.rejectionId}'>${nextRejection.description?default(nextRejection.rejectionId)}</option>
            </#list>
          </select>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Quantity Rejected</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='quantityRejected' size='5' value='0' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Quantity Accepted</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='quantityAccepted' size='5' value='${defaultQuantity?default(1)?string.number}' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td colspan='2'>&nbsp;</td>
        <td colspan='2'><input type="submit" value="Receive"></td>
      </tr>        				
    </table>
    <script language='JavaScript'>
      document.receiveform.quantityAccepted.focus();
    </script>
    </form>
</#if>

<br>
<#else>
  <h3>You do not have permission to view this page. ("FACILITY_CREATE" or "FACILITY_ADMIN" needed)</h3>
</#if>
