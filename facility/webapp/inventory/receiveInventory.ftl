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

<script language="JavaScript">
<!--
function toggle(e) {
    e.checked = !e.checked;    
}
function checkToggle(e) {
    var cform = document.receiveform;
    if (e.checked) {      
        var len = cform.elements.length;
        var allchecked = true;
        for (var i = 0; i < len; i++) {
            var element = cform.elements[i];
            var elementName = new java.lang.String(element.name);          
            if (elementName.startsWith("_rowSubmit") && !element.checked) {       
                allchecked = false;
            }
            cform.selectAll.checked = allchecked;            
        }
    } else {
        cform.selectAll.checked = false;
    }
}
function toggleAll(e) {
    var cform = document.receiveform;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];                   
        var eName = new java.lang.String(element.name);                
        if (eName.startsWith("_rowSubmit") && element.checked != e.checked) {
            toggle(element);
        } 
    }     
}
function selectAll() {
    var cform = document.receiveform;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];                   
        var eName = new java.lang.String(element.name);                
        if ((element.name == "selectAll" || eName.startsWith("_rowSubmit")) && !element.checked) {
            toggle(element);
        } 
    }     
}
function removeSelected() {
    var cform = document.receiveform;
    cform.removeSelected.value = true;
    cform.submit();
}
//-->
</script>

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
<#if requestParameters.initialSelected?exists && product?has_content>
  <form method="post" action="<@ofbizUrl>/receiveInventoryProduct</@ofbizUrl>" name='receiveform' style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
      <#-- general request fields -->
      <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}">   
      <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}">   
      <#-- special service fields -->
      <input type="hidden" name="productId|0" value="${requestParameters.productId?if_exists}">
      <input type="hidden" name="facilityId|0" value="${requestParameters.facilityId?if_exists}">      
      <input type="hidden" name="_rowCount" value="1">
      <#if purchaseOrder?has_content>
      <input type="hidden" name="orderId|0" value="${purchaseOrder.orderId}">
      <input type="hidden" name="orderItemSeqId|0" value="${firstOrderItem.orderItemSeqId}">
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Purchase Order</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <b>${purchaseOrder.orderId}</b>&nbsp;/&nbsp;<b>${firstOrderItem.orderItemSeqId}</b>
          <#if 1 < purchaseOrderItemsSize>
            <span class='tabletext'>(Multiple order items for this product - ${purchaseOrderItemsSize}:1 Item:Product)</span>
          <#else>
            <span class='tabletext'>(Single order item for this product - 1:1 Item:Product)<span>
          </#if>
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
          <div class="tabletext"><a href="/catalog/control/EditProduct?productId=${product.productId}${requestAttributes.externalKeyParam?if_exists}" target="catalog" class="buttontext">${product.productName?if_exists}</a></div>
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
          <input type='text' name='itemDescription|0' size='30' maxlength='60' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Inventory Item <br>(optional will create new if empty)</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='inventoryItemId|0' size='20' maxlength='20' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Inventory Item Type</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <select name="inventoryItemTypeId|0" size=1 class="selectBox">  
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
          <input type='text' name='datetimeReceived|0' size='24' value="${Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().toString()}" class="inputBox">
          <!--<a href='#' onclick='setNow("datetimeReceived")' class='buttontext'>[Now]</a>-->
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Facility Location</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='locationSeqId|0' size='20' maxlength="20" class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Rejected Reason</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <select name="rejectionId|0" size='1' class='selectBox'>   
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
          <input type='text' name='quantityRejected|0' size='5' value='0' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Quantity Accepted</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='quantityAccepted|0' size='5' value='${defaultQuantity?default(1)?string.number}' class="inputBox">
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
<#elseif requestParameters.initialSelected?exists && purchaseOrder?has_content>
  <form method="post" action="<@ofbizUrl>/receiveInventoryProduct</@ofbizUrl>" name='receiveform' style='margin: 0;'>
    <#-- general request fields -->
    <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}">   
    <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}">
    <input type="hidden" name="_useRowSubmit" value="Y">
    <#assign now = Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().toString()>
    <#assign rowCount = 0>     
    <table width="100%" border='0' cellpadding='2' cellspacing='0'>
      <#if !purchaseOrderItems?exists || purchaseOrderItemsSize == 0>
        <tr>
          <td colspan="2"><div class="tableheadtext">There are no items in the PO to receive.</div></td>
        </tr>
      <#else>
        <tr>
          <td>
            <div class="head3">Receive Purchase Order #${purchaseOrder.orderId}</div>
          </td>
          <td align="right">
            <input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this);">
          </td>
        </tr>
               
        <#list purchaseOrderItems as orderItem>
          <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")>
          <input type="hidden" name="orderId|${rowCount}" value="${orderItem.orderId}">
          <input type="hidden" name="orderItemSeqId|${rowCount}" value="${orderItem.orderItemSeqId}"> 
          <input type="hidden" name="facilityId|${rowCount}" value="${requestParameters.facilityId?if_exists}">       
          <input type="hidden" name="datetimeReceived|${rowCount}" value="${now}">        
          <tr>
            <td colspan="2"><hr class="sepbar"></td>
          </tr>                 
          <tr>
            <td>
              <table width="100%" border='0' cellpadding='2' cellspacing='0'>
                <tr>
                  <#if orderItem.productId?exists>
                    <#assign product = orderItem.getRelatedOneCache("Product")>  
                    <input type="hidden" name="productId|${rowCount}" value="${product.productId}">                      
                    <td width="45%">
                      <div class="tabletext">
                        ${orderItem.orderItemSeqId}:&nbsp;<a href="/catalog/control/EditProduct?productId=${product.productId}${requestAttributes.externalKeyParam?if_exists}" target="catalog" class="buttontext">${product.productId}&nbsp;-&nbsp;${product.productName?if_exists}</a> : ${product.description?if_exists}
                      </div>                       
                    </td>
                  <#else>
                    <td width="45%">
                      <div class="tabletext">
                        <b>${orderItemType.description}</b> : ${orderItem.itemDescription?if_exists}&nbsp;&nbsp;
                        <input type="text" class="inputBox" size="12" name="productId|${rowCount}">
                        <a href="/catalog/control/EditProduct?externalLoginKey=${requestAttributes.externalLoginKey}" target="catalog" class="buttontext">Create Product</a>
                      </div>
                    </td>
                  </#if>
                  <td align="right">
                    <div class="tableheadtext">Location:</div>
                  </td>
                  <td align="right">
                    <input type="text" class="inputBox" name="locationSeqId|${rowCount}" size="12">
                  </td>
                  <td align="right">
                    <div class="tableheadtext">Qty Received:</div>
                  </td>
                  <td align="right">
                    <input type="text" class="inputBox" name="quantityAccepted|${rowCount}" size="6" value="${orderItem.quantity?string.number}">
                  </td>                                                      
                </tr>
                <tr>
                  <td width="45%">
                    <span class="tableheadtext">Inventory Item Type:</span>&nbsp;&nbsp;
                    <select name="inventoryItemTypeId|${rowCount}" size='1' class="selectBox">  
                      <#list inventoryItemTypes as nextInventoryItemType>                      
                      <option value='${nextInventoryItemType.inventoryItemTypeId}'>${nextInventoryItemType.description?default(nextInventoryItemType.inventoryItemTypeId)}</option>
                      </#list>
                    </select>                    
                  </td>                    
                  <td align="right">
                    <div class="tableheadtext">Rejection Reason:</div>
                  </td>
                  <td align="right">
                    <select name="rejectionId|${rowCount}" size='1' class='selectBox'>   
                      <option></option>    
                      <#list rejectReasons as nextRejection>                 
                      <option value='${nextRejection.rejectionId}'>${nextRejection.description?default(nextRejection.rejectionId)}</option>
                      </#list>
                    </select>
                  </td>
                  <td align="right">
                    <div class="tableheadtext">Qty Rejected:</div>
                  </td>
                  <td align="right">
                    <input type="text" class="inputBox" name="quantityRejected|${rowCount}" size="6">
                  </td>
                </tr>
              </table>
            </td>
            <td align="right">              
              <input type="checkbox" name="_rowSubmit|${rowCount}" value="Y" onclick="javascript:checkToggle(this);">
            </td>
          </tr>          
          <#assign rowCount = rowCount + 1>
        </#list> 
        <tr>
          <td colspan="2">
            <hr class="sepbar">
          </td>
        </tr>
        <tr>
          <td colspan="2" align="right">
            <a href="javascript:document.receiveform.submit();" class="buttontext">Receive Selected Product(s)</a>
          </td>
        </tr>     
      </#if>
    </table>
    <input type="hidden" name="_rowCount" value="${rowCount}">
  </form>
  <script language="JavaScript">selectAll();</script>
<#else>
  <form name="receiveform" method="post" action="<@ofbizUrl>/ReceiveInventory</@ofbizUrl>" style='margin: 0;'>
    <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}">
    <input type="hidden" name="initialSelected" value="Y">
	<table border='0' cellpadding='2' cellspacing='0'>
      <tr>        
        <td width="25%" align='right'><div class="tabletext">Purchase Order Number</div></td>
        <td>&nbsp;</td>
        <td width="25%">
          <input type="text" class="inputBox" name="purchaseOrderId" size="20" maxlength="20" value="${requestParameters.purchaseOrderId?if_exists}">          
        </td> 
        <td><div class='tabletext'>&nbsp;(Leave empty for single product receiving)</div></td>
      </tr>
      <tr>        
        <td width="25%" align='right'><div class="tabletext">Product ID</div></td>
        <td>&nbsp;</td>
        <td width="25%">
          <input type="text" class="inputBox" name="productId" size="20" maxlength="20" value="${requestParameters.productId?if_exists}">         
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
</#if>

<br>
<#else>
  <h3>You do not have permission to view this page. ("FACILITY_CREATE" or "FACILITY_ADMIN" needed)</h3>
</#if>
