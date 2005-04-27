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
 *@since      2.1
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if miniProduct?exists>
    <td>
      <a href="<@ofbizUrl>/product/~product_id=${miniProduct.productId}</@ofbizUrl>" class="buttontext"><img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${miniProduct.smallImageUrl?default("/images/defaultImage.jpg")}</@ofbizContentUrl>" align="left" height="50" class="imageborder" border="0"></a>
    </td>

    <td>
      <div class="tabletext">
        <a href="<@ofbizUrl>/product/~product_id=${miniProduct.productId}</@ofbizUrl>" class="buttontext">${miniProduct.productId} - ${miniProduct.productName}</a>
      </div>
    </td>

    <td>
        <#-- check to see if Inventory is available -->
        <#assign isStoreInventoryAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryAvailable(request, miniProduct.productId, miniProdQuantity?default("1"))>
        
        <#if security.hasEntityPermission("INVENTORY", "_AVAIL", session)> 
          <#if isStoreInventoryAvailable>
            <IMG SRC="/images/checkmark.gif" ALT="In Stock">
          <#else>
            <IMG SRC="/images/crossmark.gif" ALT="Not In Stock">
          </#if>
        </#if>
    </td>

    <td>
    <div class="tabletext">
      <#if (priceResult.price?default(0) > 0 && miniProduct.requireAmount?default("N") == "N")>
        <b><span class="<#if priceResult.isSale>salePrice<#else>normalPrice</#if>"><@ofbizCurrency amount=priceResult.price isoCode=priceResult.currencyUsed/></span></b>
      </#if>
    </div>
    </td>
    
    <td>
      <#if security.hasEntityPermission("WHOLESALE_ORDER", "_CREATE", session)>
        <form method="post" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="${miniProdFormName}" style="margin: 0;">
            <input type="hidden" name="add_product_id" value="${miniProduct.productId}">
            <#if requestParameters.order_id?has_content><input type="hidden" name="order_id" value="${requestParameters.order_id}"></#if>
            <#if requestParameters.product_id?has_content><input type="hidden" name="product_id" value="${requestParameters.product_id}"></#if>
            <#if requestParameters.category_id?has_content><input type="hidden" name="category_id" value="${requestParameters.category_id}"></#if>
            <#if requestParameters.VIEW_INDEX?has_content><input type="hidden" name="VIEW_INDEX" value="${requestParameters.VIEW_INDEX}"></#if>
            <#if requestParameters.VIEW_SIZE?has_content><input type="hidden" name="VIEW_SIZE" value="${requestParameters.VIEW_SIZE}"></#if>
            <input type="hidden" name="clearSearch" value="N">
            <table width="100%">
              <tr>
                <td align="left"><input type="text" class="inputBox" size="5" name="quantity" value="${miniProdQuantity?default("1")}"></td>
                <td align="right"><a href="javascript:document.${miniProdFormName}.submit()" class="buttontext"><nobr>[${uiLabelMap.CommonAdd} ${uiLabelMap.EcommerceToCart}]</nobr></a></td>
              </tr>
            </table>
        </form>
    </#if> 
    </td>
  </tr>
</#if>

