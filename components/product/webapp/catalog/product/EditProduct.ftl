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
 *@version    $Revision: 1.1 $
 *@since      2.2
-->

<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
        
    <div class="head1">Product <span class='head2'><#if product?exists && product.getString("productName")?has_content>${product.getString("productName")}</#if> <#if productId?exists && productId?has_content>[ID:${productId}]</#if></span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?exists && productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class='buttontext' target='_blank'>[Product Page]</a>
    </#if>
    <br>
    <br>
    
    ${productFormWrapper.renderFormString()}

    <#if productId?exists>
        <hr class='sepbar'>
        <div class="head2">Duplicate Product</div>
        <#if product?exists>
            <form action="<@ofbizUrl>/EditProduct</@ofbizUrl>" method=POST style='margin: 0;'>
                <INPUT type=hidden name='productTypeId' value='product.productTypeId'>
                <INPUT type=hidden name='isVirtual' value='product.isVirtual'>
                <INPUT type=hidden name='isVariant' value='product.isVariant'>
                <INPUT type=hidden name='primaryProductCategoryId' value='product.primaryProductCategoryId'>
                <INPUT type=hidden name='manufacturerPartyId' value='product.manufacturerPartyId'>
                <INPUT type=hidden name='introductionDate' value='product.introductionDate'>
                <INPUT type=hidden name='salesDiscontinuationDate' value='product.salesDiscontinuationDate'>
                <INPUT type=hidden name='supportDiscontinuationDate' value='product.supportDiscontinuationDate'>
                <INPUT type=hidden name='comments' value='product.comments'>
                <INPUT type=hidden name='productname=' value='product.productname'>
                <INPUT type=hidden name='brandname' value='product.brandname'>
                <INPUT type=hidden name='internalname' value='product.internalname'>
                <INPUT type=hidden name='description' value='product.description'>
                <INPUT type=hidden name='longDescription' value='product.longDescription'>
                <INPUT type=hidden name='smallImageUrl' value='product.smallImageUrl'>
                <INPUT type=hidden name='mediumImageUrl' value='product.mediumImageUrl'>
                <INPUT type=hidden name='largeImageUrl' value='product.largeImageUrl'>
                <INPUT type=hidden name='detailImageUrl' value='product.detailImageUrl'>
                <INPUT type=hidden name='quantityUomId' value='product.quantityUomId'>
                <INPUT type=hidden name='quantityIncluded' value='product.quantityIncluded'>
                <INPUT type=hidden name='piecesIncluded' value='product.piecesIncluded'>
                <INPUT type=hidden name='weightUomId' value='product.weightUomId'>
                <INPUT type=hidden name='weight' value='product.weight'>
                <INPUT type=hidden name='taxable' value='product.taxable'>
                <INPUT type=hidden name='taxCategory' value='product.taxCategory'>
                <INPUT type=hidden name='taxVatCode' value='product.taxVatCode'>
                <INPUT type=hidden name='taxDutyCode' value='product.taxDutyCode'>
                <INPUT type=hidden name='chargeShipping' value='product.chargeShipping'>
                <INPUT type=hidden name='autoCreateKeywords' value='product.autoCreateKeywords'>
                <SPAN class='tabletext'>Populate New Form:</SPAN>&nbsp;
                <INPUT type=submit class='standardSubmit' value='NewForm'>
            </form>
            <hr class='sepbar'>
            <form action="<@ofbizUrl>/DuplicateProduct</@ofbizUrl>" method=POST style='margin: 0;'>
                <INPUT type=hidden name='oldProductId' value='${productId}'>
                <div>
                    <SPAN class='tabletext'>Duplicate/Remove Selected With New ID:</SPAN>
                    <input type="text" class="inputBox" size='20' maxlength='20' name='productId' >&nbsp;<INPUT type=submit class='standardSubmit' value='Go!'>
                </div>
                <div class='tabletext'>
                    <b>Duplicate:</b>
                    Prices&nbsp;<input type='checkbox' class='checkBox' name='duplicatePrices' value='Y' checked/>
                    IDs&nbsp;<input type='checkbox' class='checkBox' name='duplicateIDs' value='Y' checked/>
                    CategoryMembers&nbsp;<input type='checkbox' class='checkBox' name='duplicateCategoryMembers' value='Y' checked/>
                    Assocs&nbsp;<input type='checkbox' class='checkBox' name='duplicateAssocs' value='Y' checked/>
                    Attributes&nbsp;<input type='checkbox' class='checkBox' name='duplicateAttributes' value='Y' checked/>
                    FeatureAppls&nbsp;<input type='checkbox' class='checkBox' name='duplicateFeatureAppls' value='Y' checked/>
                    InventoryItems&nbsp;<input type='checkbox' class='checkBox' name='duplicateInventoryItems' value='Y' checked/>
                </div>
                <div class='tabletext'>
                    <b>Remove:</b>
                    Prices&nbsp;<input type='checkbox' class='checkBox' name='removePrices' value='Y'/>
                    IDs&nbsp;<input type='checkbox' class='checkBox' name='removeIDs' value='Y'/>
                    CategoryMembers&nbsp;<input type='checkbox' class='checkBox' name='removeCategoryMembers' value='Y'/>
                    Assocs&nbsp;<input type='checkbox' class='checkBox' name='removeAssocs' value='Y'/>
                    Attributes&nbsp;<input type='checkbox' class='checkBox' name='removeAttributes' value='Y'/>
                    FeatureAppls&nbsp;<input type='checkbox' class='checkBox' name='removeFeatureAppls' value='Y'/>
                    InventoryItems&nbsp;<input type='checkbox' class='checkBox' name='removeInventoryItems' value='Y'/>
                </div>
            </form>
            <br><br>
        </#if>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
