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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Revision: 1.2 $
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if shipmentItemDatas?has_content>
  <table width="100%" cellspacing="0" cellpadding="2" border="1">
	<tr>
      <td><div class="tableheadtext">${uiLabelMap.ProductItem}</div></td>
	  <td><div class="tableheadtext">&nbsp;</div></td>
	  <td><div class="tableheadtext">&nbsp;</div></td>
	  <td><div class="tableheadtext">${uiLabelMap.ProductQuantity}</div></td>
	  <td><div class="tableheadtext">&nbsp;</div></td>
	  <td><div class="tableheadtext">&nbsp;</div></td>
	</tr>
    <#list shipmentItemDatas as shipmentItemData>
	  <#assign shipmentItem = shipmentItemData.shipmentItem>
	  <#assign itemIssuances = shipmentItemData.itemIssuances>
	  <#assign shipmentPackageContents = shipmentItemData.shipmentPackageContents>
	  <#assign product = shipmentItemData.product?if_exists>
	  <tr>
	    <td><div class="tabletext">${shipmentItem.shipmentItemSeqId}</div></td>
		<td colspan="2"><div class="tabletext">${(product.productName)?if_exists} [<a href="<@ofbizUrl>/EditProduct?productId=${shipmentItem.productId?if_exists}</@ofbizUrl>" class="buttontext">${shipmentItem.productId?if_exists}</a>]</div></td>
		<td><div class="tabletext">${shipmentItem.quantity?default("&nbsp;")}</div></td>
		<td colspan="2"><div class="tabletext">${shipmentItem.shipmentContentDescription?default("&nbsp;")}</div></td>
	  </tr>
	  <#list itemIssuances as itemIssuance>
		<tr>
		  <td><div class="tabletext">&nbsp;</div></td>
		  <td><div class="tabletext">${uiLabelMap.ProductOrderItem} :<a href="/ordermgr/control/orderview?order_id=${itemIssuance.orderId?if_exists}&externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">${itemIssuance.orderId?if_exists}</a>:${itemIssuance.orderItemSeqId?if_exists}</div></td>
		  <td><div class="tabletext">${uiLabelMap.ProductInventory} :<a href="<@ofbizUrl>/EditInventoryItem?inventoryItemId=${itemIssuance.inventoryItemId?if_exists}</@ofbizUrl>" class="buttontext">${itemIssuance.inventoryItemId?if_exists}</a></div></td>
		  <td><div class="tabletext">${itemIssuance.quantity?if_exists}</div></td>
		  <td><div class="tabletext">${itemIssuance.issuedDateTime?if_exists}</div></td>
		  <td><div class="tabletext">${uiLabelMap.ProductFuturePartyRoleList}</div></td>
		</tr>
  	  </#list>
	  <#list shipmentPackageContents as shipmentPackageContent>
		<tr>
	  	  <td><div class="tabletext">&nbsp;</div></td>
	  	  <td colspan="2"><div class="tabletext">${uiLabelMap.ProductPackage} :${shipmentPackageContent.shipmentPackageSeqId}</div></td>
		  <td><div class="tabletext">${shipmentPackageContent.quantity?if_exists}</div></td>
		  <td colspan="2"><div class="tabletext">&nbsp;</div></td>
		</tr>
	  </#list>
    </#list>
  </table>
</#if>
