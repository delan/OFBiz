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
 *@version    $Revision: 1.8 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}

    <div class="head1">${uiLabelMap.ProductProduct} <span class='head2'><#if product?exists && product.internalName?has_content>${product.internalName}</#if> <#if productId?exists && productId?has_content>[${uiLabelMap.CommonId}:${productId}]</#if></span></div>

    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewProduct}]</a>
    <#if productId?exists && productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class='buttontext' target='_blank'>[${uiLabelMap.ProductProductPage}]</a>
    </#if>
    <br>
    <br>

    ${productFormWrapper.renderFormString()}

    <#if productId?exists>
        <hr class='sepbar'>
        <div class="head2">${uiLabelMap.ProductDuplicateProduct}</div>
        <#if product?exists>
            <form action="<@ofbizUrl>/DuplicateProduct</@ofbizUrl>" method=POST style='margin: 0;'>
                <INPUT type=hidden name='oldProductId' value='${productId}'>
                <div>
                    <SPAN class='tabletext'>${uiLabelMap.ProductDuplicateRemoveSelectedWithNewId}:</SPAN>
                    <input type="text" class="inputBox" size='20' maxlength='20' name='productId' >&nbsp;<INPUT type=submit class='standardSubmit' value='Go!'>
                </div>
                <div class='tabletext'>
                    <b>${uiLabelMap.CommonDuplicate}:</b>
                    ${uiLabelMap.ProductPrices}&nbsp;<input type='checkbox' class='checkBox' name='duplicatePrices' value='Y' checked/>
                    ${uiLabelMap.CommonId}&nbsp;<input type='checkbox' class='checkBox' name='duplicateIDs' value='Y' checked/>
                    ${uiLabelMap.ProductCategoryMembers}&nbsp;<input type='checkbox' class='checkBox' name='duplicateCategoryMembers' value='Y' checked/>
                    ${uiLabelMap.ProductAssocs}&nbsp;<input type='checkbox' class='checkBox' name='duplicateAssocs' value='Y' checked/>
                    ${uiLabelMap.ProductAttributes}&nbsp;<input type='checkbox' class='checkBox' name='duplicateAttributes' value='Y' checked/>
                    ${uiLabelMap.ProductFeatureAppls}&nbsp;<input type='checkbox' class='checkBox' name='duplicateFeatureAppls' value='Y' checked/>
                    ${uiLabelMap.ProductInventoryItems}&nbsp;<input type='checkbox' class='checkBox' name='duplicateInventoryItems' value='Y' checked/>
                </div>
                <div class='tabletext'>
                    <b>${uiLabelMap.CommonRemove}:</b>
                    ${uiLabelMap.ProductPrices}&nbsp;<input type='checkbox' class='checkBox' name='removePrices' value='Y'/>
                    ${uiLabelMap.CommonId}&nbsp;<input type='checkbox' class='checkBox' name='removeIDs' value='Y'/>
                    ${uiLabelMap.ProductCategoryMembers}&nbsp;<input type='checkbox' class='checkBox' name='removeCategoryMembers' value='Y'/>
                    ${uiLabelMap.ProductAssocs}&nbsp;<input type='checkbox' class='checkBox' name='removeAssocs' value='Y'/>
                    ${uiLabelMap.ProductAttributes}&nbsp;<input type='checkbox' class='checkBox' name='removeAttributes' value='Y'/>
                    ${uiLabelMap.ProductFeatureAppls}&nbsp;<input type='checkbox' class='checkBox' name='removeFeatureAppls' value='Y'/>
                    ${uiLabelMap.ProductInventoryItems}&nbsp;<input type='checkbox' class='checkBox' name='removeInventoryItems' value='Y'/>
                </div>
            </form>
            <br><br>
        </#if>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
