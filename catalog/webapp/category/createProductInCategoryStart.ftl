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

<form name="createProductInCategoryCheckExistingForm" method="POST" action="<@ofbizUrl>/createProductInCategoryCheckExisting</@ofbizUrl>" style="margin: 0;">
    <input type="hidden" name="productCategoryId" value="${productCategoryId}">
    <table border="0" wdith="100%">
        <#list productFeaturesByTypeMap.keySet() as productFeatureTypeId>
            <#assign findPftMap = Static["org.ofbiz.core.util.UtilMisc"].toMap("productFeatureTypeId", productFeatureTypeId)>
            <#assign productFeatureType = delegator.findByPrimaryKeyCache("ProductFeatureType", findPftMap)>
            <#assign productFeatures = productFeaturesByTypeMap[productFeatureTypeId]>
            <tr>
                <td>
                    <div class="tabletext">${productFeatureType.description}:</div>
                </td>
                <td>$nbsp;</td>
                <td>
                    <div class="tabletext">
                        <select class="selectBox" name="pft_${productFeatureTypeId}">
                            <option value="">- none -</option>
                            <#list productFeatures as productFeature>
                                <option value="${productFeature.productFeatureId}">${productFeature.description}</option>
                            </#list>
                        </select>
                        <input type="checkbox" name="pftsel_${productFeatureTypeId}"/>Selectable
                    </div>
                </td>
            </tr>
        </#list>
        <tr>
            <td><div class="tabletext">Product Name:</div></td>
            <td>$nbsp;</td>
            <td><input type="text" name="productName" size="30" maxlength="60" class="inputBox"/></td>
        </tr>
        <tr>
            <td><div class="tabletext">Short Description:</div></td>
            <td>$nbsp;</td>
            <td><input type="text" name="shortDescription" size="60" maxlength="250" class="inputBox"/></td>
        </tr>
        <tr>
            <td><div class="tabletext">Default Price:</div></td>
            <td>$nbsp;</td>
            <td><input type="text" name="defaultPrice" size="8" class="inputBox"/></td>
        </tr>
        <tr>
            <td><div class="tabletext">Average Cost:</div></td>
            <td>$nbsp;</td>
            <td><input type="text" name="averageCost" size="8" class="inputBox"/></td>
        </tr>
        <tr>
            <td colspan="3">
                <a href="javascript:document.createProductInCategoryCheckExistingForm.submit()" class="buttontext">Check Existing</a>
            </td>
        </tr>
    </table>
</form>

<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
