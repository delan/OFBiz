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
 *@version    $Rev:$
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if security.hasEntityPermission("FACILITY", "_CREATE", session)>

<#if invalidProductId?exists>
<div class='errorMessage'>${invalidProductId}</div>
</#if>

${pages.get("/facility/FacilityTabBar.ftl")}

<div class="head1">${uiLabelMap.ProductReceiveInventory} <span class='head2'>into&nbsp;<#if facility?has_content>"${facility.facilityName?default("Not Defined")}"</#if> [${uiLabelMap.CommonId} :${facility.facilityId?if_exists}]</span></div>
<a href="<@ofbizUrl>/EditFacility</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewFacility}]</a>

<script language='JavaScript'>
    function setNow(field) { eval('document.selectAllForm.' + field + '.value="${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()}"'); }
</script>

<div>&nbsp;</div>

<#-- Receiving Results -->
<#if receivedItems?has_content>
  <table width="100%" border='0' cellpadding='2' cellspacing='0'>
    <tr><td colspan="7"><div class="head3">${uiLabelMap.ProductReceiptPurchaseOrder} #${purchaseOrder.orderId}</div></td></tr>
    <tr><td colspan="7"><hr class="sepbar"></td></tr>
    <tr>
      <td><div class="tableheadtext">${uiLabelMap.ProductReceipt}#</div></td>
      <td><div class="tableheadtext">${uiLabelMap.CommonDate}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.ProductPo} #</div></td>
      <td><div class="tableheadtext">${uiLabelMap.ProductLine} #</div></td>
      <td><div class="tableheadtext">${uiLabelMap.ProductProductId}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.CommonRejected}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.CommonAccepted}</div></td>
    </tr>
    <tr><td colspan="7"><hr class="sepbar"></td></tr>
    <#list receivedItems as item>
      <tr>
        <td><div class="tabletext">${item.receiptId}</div></td>
        <td><div class="tabletext">${item.getString("datetimeReceived").toString()}</div></td>
        <td><div class="tabletext">${item.orderId}</div></td>
        <td><div class="tabletext">${item.orderItemSeqId}</div></td>
        <td><div class="tabletext">${item.productId?default("Not Found")}</div></td>
        <td><div class="tabletext">${item.quantityRejected?default(0)?string.number}</div></td>
        <td><div class="tabletext">${item.quantityAccepted?string.number}</div></td>
      </tr>
    </#list>
    <tr><td colspan="7"><hr class="sepbar"></td></tr>
  </table>
  <br>
</#if>

<#-- Single Product Receiving -->
<#if requestParameters.initialSelected?exists && product?has_content>
  <form method="post" action="<@ofbizUrl>/receiveInventoryProduct</@ofbizUrl>" name='selectAllForm' style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
      <#-- general request fields -->
      <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}">   
      <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}">   
      <#-- special service fields -->
      <input type="hidden" name="productId_o_0" value="${requestParameters.productId?if_exists}">
      <input type="hidden" name="facilityId_o_0" value="${requestParameters.facilityId?if_exists}">      
      <input type="hidden" name="_rowCount" value="1">
      <#if purchaseOrder?has_content>
      <input type="hidden" name="orderId_o_0" value="${purchaseOrder.orderId}">
      <input type="hidden" name="orderItemSeqId_o_0" value="${firstOrderItem.orderItemSeqId}">
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductPurchaseOrder}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <b>${purchaseOrder.orderId}</b>&nbsp;/&nbsp;<b>${firstOrderItem.orderItemSeqId}</b>
          <#if 1 < purchaseOrderItemsSize>
            <span class='tabletext'>(${uiLabelMap.ProductMultipleOrderItemsProduct} - ${purchaseOrderItemsSize}:1 ${uiLabelMap.ProductItemProduct})</span>
          <#else>
            <span class='tabletext'>(${uiLabelMap.ProductSingleOrderItemProduct} - 1:1 ${uiLabelMap.ProductItemProduct})<span>
          </#if>
        </td>                
      </tr>
      </#if>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductProductId}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <b>${requestParameters.productId?if_exists}</b>
        </td>                
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductProductName}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <div class="tabletext"><a href="/catalog/control/EditProduct?productId=${product.productId}${requestAttributes.externalKeyParam?if_exists}" target="catalog" class="buttontext">${product.internalName?if_exists}</a></div>
        </td>                
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductProductDescription}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <div class="tabletext">${product.description?if_exists}</div>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductItemDescription}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='itemDescription_o_0' size='30' maxlength='60' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductInventoryItem} <br>(${uiLabelMap.ProductOptionalCreateNew})</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='inventoryItemId_o_0' size='20' maxlength='20' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductInventoryItemType} </div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <select name="inventoryItemTypeId_o_0" size=1 class="selectBox">  
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
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductDateReceived}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='datetimeReceived_o_0' size='24' value="${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()}" class="inputBox">
          <#-- <a href='#' onclick='setNow("datetimeReceived")' class='buttontext'>[Now]</a> -->
        </td>                
      </tr>	
      
      <#-- facility location(s) -->
      <#assign facilityLocations = (product.getRelatedByAnd("ProductFacilityLocation", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId)))?if_exists>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductFacilityLocation}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <#if facilityLocations?has_content>            
            <select name='locationSeqId_o_0' class='selectBox'>
              <#list facilityLocations as productFacilityLocation>
                <#assign facility = productFacilityLocation.getRelatedOneCache("Facility")>
                <#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation")?if_exists>
                <#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOneCache("TypeEnumeration"))?if_exists>
                <option value="${productFacilityLocation.locationSeqId}"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?exists>(${facilityLocationTypeEnum.description})</#if>[${productFacilityLocation.locationSeqId}]</option>                              
              </#list>
              <option value="">${uiLabelMap.ProductNoLocation}</option>
            </select>
          <#else>
            <input type='text' name='locationSeqId_o_0' size='20' maxlength="20" class="inputBox">
          </#if>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductRejectedReason}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <select name="rejectionId_o_0" size='1' class='selectBox'>   
            <option></option>    
            <#list rejectReasons as nextRejection>                 
              <option value='${nextRejection.rejectionId}'>${nextRejection.description?default(nextRejection.rejectionId)}</option>
            </#list>
          </select>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductQuantityRejected}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='quantityRejected_o_0' size='5' value='0' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.ProductQuantityAccepted}</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='quantityAccepted_o_0' size='5' value='${defaultQuantity?default(1)?string.number}' class="inputBox">
        </td>                
      </tr>	
      <tr>
        <td colspan='2'>&nbsp;</td>
        <td colspan='2'><input type="submit" value="${uiLabelMap.CommonReceive}"></td>
      </tr>        				
    </table>
    <script language='JavaScript'>
      document.selectAllForm.quantityAccepted.focus();
    </script>
  </form>
  
<#-- Select Shipment Screen -->
<#elseif requestParameters.initialSelected?exists && !requestParameters.shipmentId?exists && shipments?has_content>
  <form method="post" action="<@ofbizUrl>/ReceiveInventory</@ofbizUrl>" name='selectAllForm' style='margin: 0;'>
    <#-- general request fields -->
    <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}">   
    <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}">
    <input type="hidden" name="initialSelected" value="Y">
    <table width="100%" border='0' cellpadding='2' cellspacing='0'>
      <tr>
        <td>
          <div class="head3">${uiLabelMap.ProductSelectShipmentReceive}</div>
        </td>
      </tr>
      <#list shipments as shipment>
        <#assign originFacility = shipment.getRelatedOneCache("OriginFacility")?if_exists>
        <#assign destinationFacility = shipment.getRelatedOneCache("DestinationFacility")?if_exists>
        <#assign statusItem = shipment.getRelatedOneCache("StatusItem")>
        <#assign shipmentType = shipment.getRelatedOneCache("ShipmentType")>      
        <#assign shipmentDate = shipment.estimatedArrivalDate?if_exists>       
        <tr>
          <td><hr class="sepbar"></td>
        </tr> 
        <tr>
          <td>
            <table width="100%" border='0' cellpadding='2' cellspacing='0'>
              <tr>
                <td width="5%" nowrap><input type="radio" name="shipmentId" value="${shipment.shipmentId}"></td>
                <td width="5%" nowrap><div class="tabletext">${shipment.shipmentId}</div></td>
                <td><div class="tabletext">${shipmentType.description?default(shipmentType.shipmentTypeId?default(""))}</div></td>
                <td><div class="tabletext">${statusItem.description?default(statusItem.statusId?default("N/A"))}</div></td>
                <td><div class="tabletext">${(originFacility.facilityName)?if_exists} [${shipment.originFacilityId?if_exists}]</div></td>
                <td><div class="tabletext">${(destinationFacility.facilityName)?if_exists} [${shipment.destinationFacilityId?if_exists}]</div></td>
                <td><div class="tabletext"><nobr>${(shipment.estimatedArrivalDate.toString())?if_exists}</nobr></div></td>                                                          
              </tr>              
            </table>
          </td>
        </tr>
      </#list>
      <tr>
        <td><hr class="sepbar"></td>
      </tr>
      <tr>
        <td>
          <table width="100%" border='0' cellpadding='2' cellspacing='0'>
            <tr>
              <td width="5%" nowrap><input type="radio" name="shipmentId" value="_NA_"></td>
              <td width="5%" nowrap><div class="tabletext">${uiLabelMap.ProductNoSpecificShipment}</div></td>
              <td colspan="5"></td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td>&nbsp;<a href="javascript:document.selectAllForm.submit();" class="buttontext">${uiLabelMap.ProductReceiveSelectedShipment}</a></td>
      </tr>
    </table>
  </form>
  
<#-- Multi-Item PO Receiving -->
<#elseif requestParameters.initialSelected?exists && purchaseOrder?has_content>
  <form method="post" action="<@ofbizUrl>/receiveInventoryProduct</@ofbizUrl>" name='selectAllForm' style='margin: 0;'>
    <#-- general request fields -->
    <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}">   
    <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}">
    <input type="hidden" name="initialSelected" value="Y">
    <input type="hidden" name="_useRowSubmit" value="Y">
    <#assign now = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()>
    <#assign rowCount = 0>     
    <table width="100%" border='0' cellpadding='2' cellspacing='0'>
      <#if !purchaseOrderItems?exists || purchaseOrderItemsSize == 0>
        <tr>
          <td colspan="2"><div class="tableheadtext">${uiLabelMap.ProductNoItemsPoReceive}.</div></td>
        </tr>
      <#else>
        <tr>
          <td>
            <div class="head3">${uiLabelMap.ProductReceivePurchaseOrder} #${purchaseOrder.orderId}</div>
          </td>
          <td align="right">
            <span class="tableheadtext">${uiLabelMap.CommonSelectAll}</span>&nbsp;
            <input type="checkbox" name="selectAll" value="${uiLabelMap.CommonY}" onclick="javascript:toggleAll(this);">
          </td>            
        </tr>               
        <#list purchaseOrderItems as orderItem>
          <#assign defaultQuantity = orderItem.quantity - receivedQuantities[orderItem.orderItemSeqId]?double>
          <#if shipment?has_content>
          <#assign defaultQuantity = shippedQuantities[orderItem.orderItemSeqId]?double - receivedQuantities[orderItem.orderItemSeqId]?double>
          </#if>
          <#if 0 < defaultQuantity>
          <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")>
          <input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}">
          <input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}"> 
          <input type="hidden" name="facilityId_o_${rowCount}" value="${requestParameters.facilityId?if_exists}">       
          <input type="hidden" name="datetimeReceived_o_${rowCount}" value="${now}">        
          <tr>
            <td colspan="2"><hr class="sepbar"></td>
          </tr>                 
          <tr>
            <td>
              <table width="100%" border='0' cellpadding='2' cellspacing='0'>
                <tr>
                  <#if orderItem.productId?exists>
                    <#assign product = orderItem.getRelatedOneCache("Product")>  
                    <input type="hidden" name="productId_o_${rowCount}" value="${product.productId}">                      
                    <td width="45%">
                      <div class="tabletext">
                        ${orderItem.orderItemSeqId}:&nbsp;<a href="/catalog/control/EditProduct?productId=${product.productId}${requestAttributes.externalKeyParam?if_exists}" target="catalog" class="buttontext">${product.productId}&nbsp;-&nbsp;${product.internalName?if_exists}</a> : ${product.description?if_exists}
                      </div>                       
                    </td>
                  <#else>
                    <td width="45%">
                      <div class="tabletext">
                        <b>${orderItemType.description}</b> : ${orderItem.itemDescription?if_exists}&nbsp;&nbsp;
                        <input type="text" class="inputBox" size="12" name="productId_o_${rowCount}">
                        <a href="/catalog/control/EditProduct?externalLoginKey=${requestAttributes.externalLoginKey}" target="catalog" class="buttontext">${uiLabelMap.ProductCreateProduct}</a>
                      </div>
                    </td>
                  </#if>
                  <td align="right">
                    <div class="tableheadtext">${uiLabelMap.ProductLocation}:</div>
                  </td>
                  <#-- location(s) -->
                  <td align="right">
                    <#assign facilityLocations = (orderItem.getRelatedByAnd("ProductFacilityLocation", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId)))?if_exists>
                    <#if facilityLocations?has_content>
                      <select name="locationSeqId_o_${rowCount}" class="selectBox">
                        <#list facilityLocations as productFacilityLocation>
                          <#assign facility = productFacilityLocation.getRelatedOneCache("Facility")>
                          <#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation")?if_exists>
                          <#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOneCache("TypeEnumeration"))?if_exists>
                          <option value="${productFacilityLocation.locationSeqId}"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?exists>(${facilityLocationTypeEnum.description})</#if>[${productFacilityLocation.locationSeqId}]</option>
                        </#list>
                        <option value="">${uiLabelMap.ProductNoLocation}</option>
                      </select>
                    <#else>
                      <input type="text" class="inputBox" name="locationSeqId_o_${rowCount}" size="12">
                    </#if>
                  </td>
                  <td align="right">
                    <div class="tableheadtext">${uiLabelMap.ProductQtyReceived} :</div>
                  </td>
                  <td align="right">                    
                    <input type="text" class="inputBox" name="quantityAccepted_o_${rowCount}" size="6" value="${defaultQuantity?string.number}">
                  </td>                                                      
                </tr>
                <tr>
                  <td width="45%">
                    <span class="tableheadtext">${uiLabelMap.ProductInventoryItemType} :</span>&nbsp;&nbsp;
                    <select name="inventoryItemTypeId_o_${rowCount}" size='1' class="selectBox">  
                      <#list inventoryItemTypes as nextInventoryItemType>                      
                      <option value='${nextInventoryItemType.inventoryItemTypeId}'>${nextInventoryItemType.description?default(nextInventoryItemType.inventoryItemTypeId)}</option>
                      </#list>
                    </select>                    
                  </td>                    
                  <td align="right">
                    <div class="tableheadtext">${uiLabelMap.ProductRejectionReason} :</div>
                  </td>
                  <td align="right">
                    <select name="rejectionId_o_${rowCount}" size='1' class='selectBox'>   
                      <option></option>    
                      <#list rejectReasons as nextRejection>                 
                      <option value='${nextRejection.rejectionId}'>${nextRejection.description?default(nextRejection.rejectionId)}</option>
                      </#list>
                    </select>
                  </td>
                  <td align="right">
                    <div class="tableheadtext">${uiLabelMap.ProductQtyRejected} :</div>
                  </td>
                  <td align="right">
                    <input type="text" class="inputBox" name="quantityRejected_o_${rowCount}" value="0" size="6">
                  </td>
                </tr>
              </table>
            </td>
            <td align="right">              
              <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="${uiLabelMap.CommonY}" onclick="javascript:checkToggle(this);">
            </td>
          </tr>          
          <#assign rowCount = rowCount + 1>
          </#if>
        </#list> 
        <tr>
          <td colspan="2">
            <hr class="sepbar">
          </td>
        </tr>
        <#if rowCount == 0>
          <tr>
            <td colspan="2">
              <div class="tabletext">${uiLabelMap.ProductNoItemsPo} #${purchaseOrder.orderId} ${uiLabelMap.ProductToReceive}.</div>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <a href="<@ofbizUrl>/ReceiveInventory?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductReturnToReceiving}</a>
            </td>
          </tr>          
        <#else>        
          <tr>
            <td colspan="2" align="right">
              <a href="javascript:document.selectAllForm.submit();" class="buttontext">${uiLabelMap.ProductReceiveSelectedProduct}</a>
            </td>
          </tr>
        </#if>
      </#if>      
    </table>
    <input type="hidden" name="_rowCount" value="${rowCount}">
  </form>
  <script language="JavaScript">selectAll();</script>
  
<#-- Initial Screen -->
<#else>
  <form name="selectAllForm" method="post" action="<@ofbizUrl>/ReceiveInventory</@ofbizUrl>" style='margin: 0;'>
    <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}">
    <input type="hidden" name="initialSelected" value="Y">
	<table border='0' cellpadding='2' cellspacing='0'>
	  <tr><td colspan="4"><div class="head3">${uiLabelMap.ProductReceiveItem}</div></td></tr>
      <tr>        
        <td width="25%" align='right'><div class="tabletext">${uiLabelMap.ProductPurchaseOrderNumber}</div></td>
        <td>&nbsp;</td>
        <td width="25%">
          <input type="text" class="inputBox" name="purchaseOrderId" size="20" maxlength="20" value="${requestParameters.purchaseOrderId?if_exists}">          
        </td> 
        <td><div class='tabletext'>&nbsp;(${uiLabelMap.ProductLeaveSingleProductReceiving})</div></td>
      </tr>
      <tr>        
        <td width="25%" align='right'><div class="tabletext">${uiLabelMap.ProductProductId}</div></td>
        <td>&nbsp;</td>
        <td width="25%">
          <input type="text" class="inputBox" name="productId" size="20" maxlength="20" value="${requestParameters.productId?if_exists}">         
        </td>       
        <td><div class='tabletext'>&nbsp;(${uiLabelMap.ProductLeaveEntirePoReceiving})</div></td>        
      </tr>      
      <tr>
        <td colspan="2">&nbsp;</td>
        <td colspan="2">
          <a href="javascript:document.selectAllForm.submit();" class="buttontext">${uiLabelMap.ProductReceiveProduct}</a>
        </td>
      </tr>        
    </table>
  </form>
</#if>

<br>
<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>
