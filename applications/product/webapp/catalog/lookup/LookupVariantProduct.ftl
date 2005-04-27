<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     Jacopo Cappellato (tiz@sastau.it)
-->

<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
    
<br/>
<div class="tabletext">[${product.productId}] ${product.internalName}</div>
<br/>
<#if searchFeatures?has_content>
    <table border="1" cellpadding="2" cellspacing="0">
        <form method="post" action="<@ofbizUrl>/LookupVariantProduct</@ofbizUrl>" name="selectAllForm">
        <input type="hidden" name="productId" value="${product.productId}">
        <#list searchFeatures as searchFeature>
            <tr>
                <td><div class="tabletext"><b>${searchFeature.featureType}</b></div></td>
                <td><div class="tabletext">
                    <select name="${searchFeature.featureType}" class="selectBox">
                    <#assign features = searchFeature.features>
                    <option value=""></option>
                    <#list features as feature>
                        <#if searchFeature.selectedFeatureId?has_content && searchFeature.selectedFeatureId == feature.productFeatureId>
                            <option value="${feature.productFeatureId}" selected>${feature.description}</option>
                        <#else>
                            <option value="${feature.productFeatureId}">${feature.description}</option>
                        </#if>
                    </#list>
                    </select>
                    </div>
                </td>
            </tr>
        </#list>
        <tr><td colspan=2><input type="submit" value="Search" class="smallSubmit"></td></tr>
        </form>
    </table>
</#if>
<br/>
<#if variants?exists>
    <table border="1" cellpadding="2" cellspacing="0">
        <#list variants as variant>
            <tr>
                <td><a class="buttontext" href="javascript:set_value('${variant}')">${variant}</a></td>
            </tr>
        </#list>
    </table>
</#if>
<#if productFeatureIds?exists>
    <table border="1" cellpadding="2" cellspacing="0">
        <form method="post" action="<@ofbizUrl>/LookupVariantProduct</@ofbizUrl>" name="createNewVariant">
        <input type="hidden" name="productId" value="${product.productId}">
        <input type="hidden" name="productFeatureIds" value="${productFeatureIds}">
        <input type="text" name="productVariantId" value="${productVariantId}" class="inputBox">
        <input type="submit" value="Quick Add Variant" class="smallSubmit">
        </form>
    </table>
</#if>