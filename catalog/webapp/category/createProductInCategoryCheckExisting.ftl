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
 *@version    $Revision$
 *@since      2.1
-->

<#if hasPermission>

<div class="head1">Create Product in Category <span class="head2"><#if (productCategory.description)?has_content>"${productCategory.description}"</#if> [ID:${productCategoryId?if_exists}]</span></div>
<#if productCategoryId?has_content>
    <a href="<@ofbizUrl>/EditProductCategory?productCategoryId=${productCategoryId}</@ofbizUrl>" class="buttontext">[Edit ProductCategory]</a>
</#if>

<div class="head1">
    Checking for existing product in category <#if (productCategory.description)?has_content>"${productCategory.description}"</#if> [ID:${productCategoryId?if_exists}]

    <#if productFeatureAndTypeDatas?has_content>
        where 
        <#list productFeatureAndTypeDatas as productFeatureAndTypeData>
            <#assign productFeatureType = productFeatureAndTypeData.productFeatureType>
            <#assign productFeature = productFeatureAndTypeData.productFeature>
            ${productFeatureType.description} = ${productFeature.description}
            <#if productFeatureAndTypeData_has_next>, and </#if>
        </#list>
    </#if>
</div>

<#if products?has_content>
    <#list products as product>
        <div class="tabletext">
            ${product.productName?default("-no name-")} [${product.productId}]
            <a href="<@ofbizUrl>/EditProduct?productId=${product.productId}</@ofbizUrl>" class="buttontext">[This is it]</a>
        </div>
    </#list>
<#else>
    <div class="head3">&nbsp;No existing products found.</div>
</#if>

<form name="createProductInCategoryForm" method="POST" action="<@ofbizUrl>/createProductInCategory</@ofbizUrl>" style="margin: 0;">
    <input type="hidden" name="productCategoryId" value="${productCategoryId}">
    <table border="0" wdith="100%">
        <#list productFeatureAndTypeDatas?if_exists as productFeatureAndTypeData>
            <#assign productFeatureType = productFeatureAndTypeData.productFeatureType>
            <#assign productFeature = productFeatureAndTypeData.productFeature>
            <#assign productFeatureTypeId = productFeatureType.productFeatureTypeId>
            <input type="hidden" name="pft_${productFeatureType.productFeatureTypeId}" value="${productFeature.productFeatureId}"/>
            <tr>
                <td>
                    <div class="tabletext">${productFeatureType.description}</div>
                </td>
                <td>$nbsp;</td>
                <td>
                    <div class="tabletext">
                        ${productFeature.description}
                        <#if requestParameters["pftsel_" + productFeatureTypeId]?exists>
                            <input type="hidden" name="pftsel_${productFeatureTypeId}" value="Y"/>
                            [Selectable]
                        <#else>
                            <input type="hidden" name="pftsel_${productFeatureTypeId}" value="N"/>
                            [Standard]
                        </#if>
                    </div>
                </td>
            </tr>
        </#list>
        <tr>
            <td><div class="tabletext">Product Name:</div></td>
            <td>$nbsp;</td>
            <td>
                <input type="hidden" name="productName" value="${requestParameters.productName?if_exists}"/>
                <div class="tabletext">${requestParameters.productName?if_exists}</div>
            </td>
        </tr>
        <tr>
            <td><div class="tabletext">Short Description:</div></td>
            <td>$nbsp;</td>
            <td>
                <input type="hidden" name="shortDescription" value="${requestParameters.shortDescription?if_exists}"/>
                <div class="tabletext">${requestParameters.shortDescription?if_exists}</div>
            </td>
        </tr>
        <tr>
            <td><div class="tabletext">Default Price:</div></td>
            <td>$nbsp;</td>
            <td>
                <input type="hidden" name="defaultPrice" value="${requestParameters.defaultPrice?if_exists}"/>
                <div class="tabletext">${requestParameters.defaultPrice?if_exists}</div>
            </td>
        </tr>
        <tr>
            <td><div class="tabletext">Average Cost:</div></td>
            <td>$nbsp;</td>
            <td>
                <input type="hidden" name="averageCost" value="${requestParameters.averageCost?if_exists}"/>
                <div class="tabletext">${requestParameters.averageCost?if_exists}</div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="tabletext">
                    New Product ID: <input type="text" name="productId" value=""/>
                    <input type="submit" value="Create New Product"/>
                </div>
            </td>
        </tr>
    </table>
</form>
  
<#else>
    <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
