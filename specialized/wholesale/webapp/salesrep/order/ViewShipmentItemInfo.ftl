<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     Si Chen (sichen@sinfoniasolutions.com)
 *@version    $Revision$
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if shipmentItemDatas?has_content>
  <table width="100%" cellspacing="0" cellpadding="2" border="1">
	<tr>
      <td width=7%><div class="tableheadtext">${uiLabelMap.ProductItem}</div></td>
	  <td><div class="tableheadtext">${uiLabelMap.ProductItemDescription}</div></td>
	  <td width=7%><div class="tableheadtext">${uiLabelMap.ProductQuantity}</div></td>
	  <td><div class="tableheadtext">&nbsp;</div></td>
	</tr>
    <#list shipmentItemDatas as shipmentItemData>
	  <#assign shipmentItem = shipmentItemData.shipmentItem>
	  <#assign itemIssuances = shipmentItemData.itemIssuances>
	  <#assign shipmentPackageContents = shipmentItemData.shipmentPackageContents>
	  <#assign product = shipmentItemData.product?if_exists>
	  <tr>
	    <td><div class="tabletext">${shipmentItem.shipmentItemSeqId}</div></td>
		<td><div class="tabletext">${(product.productName)?if_exists} [${shipmentItem.productId?if_exists}]</div></td>
		<td><div class="tabletext">${shipmentItem.quantity?default("&nbsp;")}</div></td>
		<td>
		  <table border="0">
	        <#list shipmentPackageContents as shipmentPackageContent>
		      <tr>
	  	        <td><div class="tabletext">
	  	  	      ${uiLabelMap.ProductPackage} : ${shipmentPackageContent.shipmentPackageSeqId}
	  	        </div></td>
		        <td><div class="tabletext">${uiLabelMap.ProductQuantity} : ${shipmentPackageContent.quantity?if_exists}</div></td>
		      </tr>
	         </#list>
	       </table>
	    </td>
	  </tr>
    </#list>
  </table>
</#if>
