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
 *@version    $Revision: 1.5 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">${uiLabelMap.ProductQuickAddVariants} <span class="head2">${uiLabelMap.CommonFor} ${(product.internalName)?if_exists} [${uiLabelMap.CommonId}:${productId?if_exists}]</span></div>
    
    <#if (product.isVirtual)?if_exists != "Y">
        ${uiLabelMap.ProductWarningProductNotVirtual}
    </#if>
    
    <br>
    <#if (productFeatureAndAppls.size() > 0)>
        <table border="1" cellpadding="2" cellspacing="0">
	        <tr>
	            <#list featureTypes as featureType>
	                <td><div class="tabletext"><b>${featureType}</b></div></td>
	            </#list>
	            <td><div class="tabletext"><b>${uiLabelMap.ProductNewProductCreate} !</b></div></td>
	            <td><div class="tabletext"><b>${uiLabelMap.ProductExistingVariant} :</b></div></td>
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
			                <INPUT type='submit' class='smallSubmit' value="${uiLabelMap.CommonCreate} !">
			            </td>
			            <td>
			                <div class="tabletext">&nbsp;
			                <#list existingVariantProductIds as existingVariantProductId>
			                	[<a href="<@ofbizUrl>/EditProduct?productId=${existingVariantProductId}</@ofbizUrl>" class="buttontext">${existingVariantProductId}</a>] &nbsp;
			                </#list>
			                </div>
			            </td>
			        </FORM>
			    </tr>
			</#list>
		</table>
	<#else>
	    <div class="tabletext"><b>${uiLabelMap.ProductNoSelectableFeaturesFound}</b></div>
	</#if>
<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
