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
 *@version    $Revision$
 *@since      2.2
-->

<#if hasPermission>
    <#if productId?has_content>
        <div class="tabContainer">
        <a href="<@ofbizUrl>/EditProduct?productId=${productId}</@ofbizUrl>" class="tabButton">Product</a>
        <a href="<@ofbizUrl>/EditProductPrices?productId=${productId}</@ofbizUrl>" class="tabButton">Prices</a>
        <a href="<@ofbizUrl>/EditProductContent?productId=${productId}</@ofbizUrl>" class="tabButton">Content</a>
        <a href="<@ofbizUrl>/EditProductGoodIdentifications?productId=${productId}</@ofbizUrl>" class="tabButton">IDs</a>
        <a href="<@ofbizUrl>/EditProductCategories?productId=${productId}</@ofbizUrl>" class="tabButton">Categories</a>
        <a href="<@ofbizUrl>/EditProductKeyword?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Keywords</a>
        <a href="<@ofbizUrl>/EditProductAssoc?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Associations</a>
        <a href="<@ofbizUrl>/EditProductAttributes?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Attributes</a>
        <a href="<@ofbizUrl>/EditProductFeatures?productId=${productId}</@ofbizUrl>" class="tabButton">Features</a>
        <a href="<@ofbizUrl>/EditProductFacilities?productId=${productId}</@ofbizUrl>" class="tabButton">Facilities</a>
        <a href="<@ofbizUrl>/EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="tabButton">Inventory</a>
        <a href="<@ofbizUrl>/EditProductGlAccounts?productId=${productId}</@ofbizUrl>" class="tabButton">Accounts</a>
        <#if product?exists && product.isVirtual?if_exists == "Y">
            <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="tabButtonSelected">Variants</a>
        </#if>
        </div>
    </#if>
    
    <div class="head1">Quick Add Variants <span class="head2">for ${(product.productName)?if_exists} [ID:${productId?if_exists}]</span></div>
    
    <#if product?exists && !(product.isVirtual.equals("Y"))>
        WARNING: This product is not a virtual product, variants will not generally be used.
    </#if>
    
    <br>
    <#if productFeatureAndAppls.size() > 0>
        <table border="1" cellpadding="2" cellspacing="0">
	        <tr>
	            <#list featureTypes as featureType>
	                <td><div class="tabletext"><b>${featureType}</b></div></td>
	            </#list>
	            <td><div class="tabletext"><b>New Product ID and Create!</b></div></td>
	            <td><div class="tabletext"><b>Existing Variant IDs:</b></div></td>
	        </tr>
	        
	        <#list featureCombinationInfos as featureCombinationInfo>
	        	<#assign curProductFeatureAndAppls = featureCombinationInfo.curProductFeatureAndAppls>
	        	<#assign existingVariantProductIds = featureCombinationInfo.existingVariantProductIds>
			    <tr valign="middle">
			        <FORM method=POST action="<@ofbizUrl>/QuickAddChosenVariant</@ofbizUrl>">
			            <input type=hidden name="productId" value="${productId}">
			            <input type=hidden name="featureTypeSize" value="${featureTypeSize}">
			            
			            <#list curProductFeatureAndAppls as productFeatureAndAppl>
			                <td>
			                    <div class="tabletext">${productFeatureAndAppl.description?if_exists}</div>
			                    <input type=hidden name="feature_${productFeatureAndAppl_index}" value="${productFeatureAndAppl.productFeatureId?if_exists}">
			                </td>
			            </#list>
			            <td>
			                <input type=text size="20" maxlength="20" name="variantProductId">
			                <INPUT type=submit value="Create!">
			            </td>
			            <td>
			                <div class="tabletext">&nbsp;
			                <#list existingVariantProductIds as existingVariantProductId>
			                	[<a href="<@ofbizUrl>/EditProduct?productId=${productAssoc.productIdTo}</@ofbizUrl>" class="buttontext">${productAssoc.productIdTo}</a>] &nbsp;
			                </#list>
			                </div>
			            </td>
			        </FORM>
			    </tr>
			</#list>
		</table>
	<#else>
	    <div class="tabletext"><b>No selectable features found. Please create some and try again.</b></div>
	</#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
