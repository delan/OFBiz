<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Rev:$
 *@since      3.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>
  <#-- the product tabs -->
  ${pages.get("/product/ProductTabBar.ftl")}

  <div class="head1">${uiLabelMap.ProductSuppliers} <span class="head2">${uiLabelMap.CommonFor} <#if product?exists>${(product.productName)?if_exists}</#if>[${uiLabelMap.CommonId}:${productId?if_exists}]</span></div>
  <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewProduct}]</a>
  <#if productId?has_content>
      <a href="/ecommerce/control/product?product_id=${productId}" class='buttontext' target='_blank'>[${uiLabelMap.ProductProductPage}]</a>
  </#if>
  <br>
  <br>

  <#if productId?has_content && product?has_content>
      ${updateSupplierProductForm.renderFormString()}<br>${addSupplierProductForm.renderFormString()}
  </#if>

<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>