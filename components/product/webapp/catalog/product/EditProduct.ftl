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
 *@author     Brad Steiner (bsteiner@thehungersite.com)
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Revision: 1.11 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}

    <div class="head1">${uiLabelMap.ProductProduct} <span class='head2'><#if product?exists && product.internalName?has_content>${product.internalName}</#if> <#if productId?has_content>[${uiLabelMap.CommonId}:${productId}]</#if></span></div>

    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewProduct}]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class='buttontext' target='_blank'>[${uiLabelMap.ProductProductPage}]</a>
        <a href="/ordermgr/control/findorders?productId=${productId}&orderTypeId=PURCHASE_ORDER&lookupFlag=Y&hideFields=Y&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class='buttontext' target='_blank'>[Purchase Orders]</a>
        <a href="/ordermgr/control/findorders?productId=${productId}&orderTypeId=SALES_ORDER&lookupFlag=Y&hideFields=Y&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class='buttontext' target='_blank'>[Sales Orders]</a>
    </#if>
    <br>
    <br>

    ${productFormWrapper.renderFormString()}

    ${pages.get("/product/EditProductDupForm.ftl")}

<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
