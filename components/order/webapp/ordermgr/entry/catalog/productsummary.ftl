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
 *@version    $Revision: 1.8 $
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if requestAttributes.product?exists>
<#-- variable setup -->
<#assign product = requestAttributes.product>
<#assign productContentWrapper = requestAttributes.productContentWrapper>
<#assign price = requestAttributes.priceMap>
<#assign targetRequestName = "product">
<#if requestAttributes.targetRequestName?has_content>
    <#assign targetRequestName = requestAttributes.targetRequestName>
</#if>
<#assign productContentWrapper = requestAttributes.productContentWrapper>
<#assign smallImageUrl = productContentWrapper.get("SMALL_IMAGE_URL")?if_exists>
<#if !smallImageUrl?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
<#-- end variable setup -->

  <table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
      <td valign="top">
          <a href="<@ofbizUrl>/${targetRequestName}/<#if requestAttributes.categoryId?exists>~category_id=${requestAttributes.categoryId}/</#if>~product_id=${product.productId}</@ofbizUrl>">
            <img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${product.smallImageUrl?default("/images/defaultImage.jpg")}</@ofbizContentUrl>" align="left" height="50" class="imageborder" border="0">
          </a>
      </td>
      <td align="left" valign="top" width="100%">
          <div class="tabletext">
            <a href="<@ofbizUrl>/${targetRequestName}/<#if requestAttributes.categoryId?exists>~category_id=${requestAttributes.categoryId}/</#if>~product_id=${product.productId}</@ofbizUrl>" class="buttontext">${productContentWrapper.get("PRODUCT_NAME")?if_exists}</a>
          </div>
          <div class="tabletext">${productContentWrapper.get("DESCRIPTION")?if_exists}<#if daysToShip?exists>&nbsp;-&nbsp;${uiLabelMap.ProductUsuallyShipsIn} <b>${daysToShip}</b> ${uiLabelMap.CommonDays}!</#if></div>
          <div class="tabletext">
            <nobr>
              <b>${product.productId?if_exists}</b>,
                <#if price.competitivePrice?exists && price.price?exists && price.price?double < price.competitivePrice?double>
                  ${uiLabelMap.ProductCompareAtPrice}: <span class='basePrice'><@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed/></span>
                </#if>
                <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
                  ${uiLabelMap.ProductListPrice}: <span class="basePrice"><@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed/></span>
                </#if>
                <b>
                  <#if price.isSale>
                    <span class="salePrice">${uiLabelMap.EcommerceOnSale}!</span>
                  </#if>
                  ${uiLabelMap.EcommerceYourPrice}: <#if "Y" = product.isVirtual?if_exists> from </#if><span class="<#if price.isSale>salePrice<#else>normalPrice</#if>"><@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></span>
                </b>
            </nobr>
          </div>
      </td>
      <td valign=center align=right>
          <#-- check to see if introductionDate hasn't passed yet -->
          <#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
            <div class="tabletext" style="color: red;">${uiLabelMap.ProductNotYetAvailable}</div>
          <#-- check to see if salesDiscontinuationDate has passed -->
          <#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)>
            <div class="tabletext" style="color: red;">${uiLabelMap.ProductNoLongerAvailable}</div>
          <#-- check to see if the product is a virtual product -->
          <#elseif product.isVirtual?exists && product.isVirtual == "Y">
            <a href="<@ofbizUrl>/product?<#if requestAttributes.categoryId?exists>category_id=${requestAttributes.categoryId}&</#if>product_id=${product.productId}</@ofbizUrl>" class="buttontext"><nobr>[${uiLabelMap.EcommerceChooseVariations}...]</nobr></a>
          <#else>
            <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}form" style="margin: 0;">
              <input type="hidden" name="add_product_id" value="${product.productId}">
              <input type="text" class="inputBox" size="5" name="quantity" value="1">
              <#if requestParameters.product_id?has_content><input type="hidden" name="product_id" value="${requestParameters.product_id}"></#if>
              <#if requestParameters.category_id?has_content><input type="hidden" name="category_id" value="${requestParameters.category_id}"></#if>
              <#if requestParameters.productPromoId?has_content><input type="hidden" name="productPromoId" value="${requestParameters.productPromoId}"></#if>
              <#if requestParameters.VIEW_INDEX?has_content><input type="hidden" name="VIEW_INDEX" value="${requestParameters.VIEW_INDEX}"></#if>
              <#if requestParameters.VIEW_SIZE?has_content><input type="hidden" name="VIEW_SIZE" value="${requestParameters.VIEW_SIZE}"></#if>
              <input type="hidden" name="clearSearch" value="N">
              <br><a href="javascript:document.the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}form.submit()" class="buttontext"><nobr>[${uiLabelMap.EcommerceAddtoCart}]</nobr></a>
            </form>
            
            <#if requestAttributes.productCategoryMember?exists>
                <#assign prodCatMem = requestAttributes.productCategoryMember>
                <#if prodCatMem?exists && prodCatMem.quantity?exists && 0.00 < prodCatMem.quantity?double>
                <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}defaultform" style="margin: 0;">
                  <input type="hidden" name="add_product_id" value="${prodCatMem.productId?if_exists}">
                  <input type="hidden" name="quantity" value="${prodCatMem.quantity?if_exists}">
                  <#if requestParameters.product_id?has_content><input type="hidden" name="product_id" value="${requestParameters.product_id}"></#if>
                  <#if requestParameters.category_id?has_content><input type="hidden" name="category_id" value="${requestParameters.category_id}"></#if>
                  <#if requestParameters.productPromoId?has_content><input type="hidden" name="productPromoId" value="${requestParameters.productPromoId}"></#if>
                  <#if requestParameters.VIEW_INDEX?has_content><input type="hidden" name="VIEW_INDEX" value="${requestParameters.VIEW_INDEX}"></#if>
                  <#if requestParameters.VIEW_SIZE?has_content><input type="hidden" name="VIEW_SIZE" value="${requestParameters.VIEW_SIZE}"></#if>
                  <input type="hidden" name="clearSearch" value="N">
                  <a href="javascript:document.the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}defaultform.submit()" class="buttontext"><nobr>[${uiLabelMap.CommonAddDefault}(${prodCatMem.quantity?string.number}) ${uiLabelMap.EcommerceToCart}]</nobr></a>
                </form>
              </#if>
            </#if>
          </#if>
      </td>
    </tr>
  </table>
<#else>
&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br>
</#if>
