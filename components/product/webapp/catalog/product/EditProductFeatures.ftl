<#--
 *  Copyright (c) 2003-2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Revision: 1.10 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">${uiLabelMap.ProductFeatures} <span class="head2">${uiLabelMap.CommonFor} <#if product?exists>${(product.internalName)?if_exists} </#if> [${uiLabelMap.CommonId}:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewProduct}]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[${uiLabelMap.ProductProductPage}]</a>
    </#if>
    <br>
    <br>
    
    <#if productId?exists>    
        <table border="1" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>${uiLabelMap.CommonId}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonDescription}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ProductType}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ProductCategory}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonFromDate}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ProductThruDateAmountSequenceApplicationType}</b></div></td>
            <td><div class="tabletext">&nbsp;</div></td>
        </tr>
        <#assign line = 0>
        <#list productFeatureAndAppls as productFeatureAndAppl>
            <#assign line = line + 1>
            <#assign curProductFeatureType = productFeatureAndAppl.getRelatedOneCache("ProductFeatureType")>
            <#assign curProductFeatureApplType = productFeatureAndAppl.getRelatedOneCache("ProductFeatureApplType")>
            <#assign curProductFeatureCategory = productFeatureAndAppl.getRelatedOneCache("ProductFeatureCategory")>
<!--            <#if curProductFeatureCategory?exists> pageContext.setAttribute("curProductFeatureCategory", curProductFeatureCategory)</#if>	-->
            <tr valign="middle">
                <form method=POST action="<@ofbizUrl>/UpdateFeatureToProductApplication</@ofbizUrl>" name="lineForm${line}">
                <input type=hidden name="productId" value="${(productFeatureAndAppl.productId)?if_exists}">
                <input type=hidden name="productFeatureId" value="${(productFeatureAndAppl.productFeatureId)?if_exists}">
                <input type=hidden name="fromDate" value="${(productFeatureAndAppl.fromDate)?if_exists}">
                <td><div class="tabletext">${(productFeatureAndAppl.productFeatureId)?if_exists}</div></td>
                <td><div class="tabletext">${(productFeatureAndAppl.description)?if_exists}</div></td>
                <td><div class="tabletext">${(curProductFeatureType.description)?default((productFeatureAndAppl.productFeatureTypeId)?if_exists)}</div></td>
                <td><a href="<@ofbizUrl>/EditFeatureCategoryFeatures?productFeatureCategoryId=${(productFeatureAndAppl.productFeatureCategoryId)?if_exists}&productId=${(productFeatureAndAppl.productId)?if_exists}</@ofbizUrl>" class="buttontext">
                    ${(curProductFeatureCategory.description)?if_exists}
                    [${(productFeatureAndAppl.productFeatureCategoryId)?if_exists}]</a></td>
                <#assign hasntStarted = false>
                <#if (productFeatureAndAppl.getTimestamp("fromDate"))?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().before(productFeatureAndAppl.getTimestamp("fromDate"))> <#assign hasntStarted = true></#if>
                <td><div class='tabletext'<#if hasntStarted> style='color: red;'</#if>>${(productFeatureAndAppl.fromDate)?if_exists}</div></td>
                <td>
                    <#assign hasExpired = false>
                    <#if (productFeatureAndAppl.getTimestamp("thruDate"))?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(productFeatureAndAppl.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                    <input type='text' size='25' name='thruDate' value='${(productFeatureAndAppl.thruDate)?if_exists}' class='inputBox' <#if hasExpired> style='color: red;'</#if>>
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(productFeatureAndAppl.thruDate)?default(nowTimestampString)}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                    <input type=text size='6' name='amount' value='${(productFeatureAndAppl.amount)?if_exists}'  class='inputBox'>
                    <input type=text size='5' name='sequenceNum' value='${(productFeatureAndAppl.sequenceNum)?if_exists}' class='inputBox'>
                <select class='selectBox' name='productFeatureApplTypeId' size=1>
                    <#if (productFeatureAndAppl.productFeatureApplTypeId)?exists>
                        <option value='${(productFeatureAndAppl.productFeatureApplTypeId)?if_exists}'><#if curProductFeatureApplType?exists> ${(curProductFeatureApplType.description)?if_exists} <#else> [${productFeatureAndAppl.productFeatureApplTypeId}]</#if></option>
                        <option value='${productFeatureAndAppl.productFeatureApplTypeId}'> </option>
                    </#if>
                    <#list productFeatureApplTypes as productFeatureApplType>
                        <option value='${(productFeatureApplType.productFeatureApplTypeId)?if_exists}'>${(productFeatureApplType.description)?if_exists} </option>
                    </#list>
                </select>
                    <input type=submit value='Update' style='font-size: x-small;'/>
                </td>
                </form>
                <td>
                <a href='<@ofbizUrl>/RemoveFeatureFromProduct?productId=${(productFeatureAndAppl.productId)?if_exists}&productFeatureId=${(productFeatureAndAppl.productFeatureId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(productFeatureAndAppl.getTimestamp("fromDate").toString())}</@ofbizUrl>' class="buttontext">
                 [${uiLabelMap.CommonDelete}]</a>
                </td>
            </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/EditFeatureCategoryFeatures</@ofbizUrl>" style='margin: 0;'>
        <input type="hidden" name="productId" value="${productId}">
        <div class='head2'>${uiLabelMap.ProductAddProductFeatureFromCategory}:</div>
        <br>
        <select class='selectBox' name='productFeatureCategoryId' size=1>
            <#list productFeatureCategories as productFeatureCategory>
                <option value='${(productFeatureCategory.productFeatureCategoryId)?if_exists}'>${(productFeatureCategory.description)?if_exists} [${(productFeatureCategory.productFeatureCategoryId)?if_exists}]</option>
            </#list>
        </select>
        <input type="submit" value="Add" style='font-size: x-small;'>
        </form>

        <br>

        <form method="POST" action="<@ofbizUrl>/ApplyFeatureToProductFromTypeAndCode</@ofbizUrl>" style='margin: 0;' name='addFeatureByTypeIdCode'>
        <input type="hidden" name="productId" value="${productId}">
        <div class='head2'>${uiLabelMap.ProductAddProductFeatureTypeId}:</div>
        <br>
        <span class='tabletext'>${uiLabelMap.ProductFeatureType}: </span><select class='selectBox' name='productFeatureTypeId' size=1>
            <#list productFeatureTypes as productFeatureType>
            <option value='${(productFeatureType.productFeatureTypeId)?if_exists}'>${(productFeatureType.description)?if_exists} </option>
            </#list>
        </select>
        <span class='tabletext'>${uiLabelMap.CommonIdCode}: </span><input type=text size='10' name='idCode' value='' class='inputBox'>
        <br>
        <span class='tabletext'>${uiLabelMap.ProductFeatureApplicationType}: </span><select class='selectBox' name='productFeatureApplTypeId' size=1>
            <#list productFeatureApplTypes as productFeatureApplType>
            <option value='${(productFeatureApplType.productFeatureApplTypeId)?if_exists}'>${(productFeatureApplType.description)?if_exists} </option>
            </#list>
        </select>
        <br>
        <span class='tabletext'>${uiLabelMap.CommonFrom} : </span><input type=text size='25' name='fromDate' class='inputBox'>
        <a href="javascript:call_cal(document.addFeatureByTypeIdCode.fromDate, '${nowTimestampString}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
        <span class='tabletext'>${uiLabelMap.CommonThru} : </span><input type=text size='25' name='thruDate' class='inputBox'>
        <a href="javascript:call_cal(document.addFeatureByTypeIdCode.thruDate, '${nowTimestampString}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
        <span class='tabletext'>${uiLabelMap.CommonSequence} : </span><input type=text size='5' name='sequenceNum' class='inputBox'>
        <input type="submit" value="Add" style='font-size: x-small;'>
        </form>

        <br>

        <form method="POST" action="<@ofbizUrl>/ApplyFeatureToProduct</@ofbizUrl>" style="margin: 0;" name="addFeatureById">
        <input type="hidden" name="productId" value="${productId}">
        <div class="head2">Add Product Feature with ID:</div>
        <br>
        <span class="tabletext">${uiLabelMap.CommonId}: </span><input type=text size="10" name="productFeatureId" value="" class="inputBox">
        <span class="tabletext">${uiLabelMap.ProductFeatureApplicationType}: </span><select class="selectBox" name="productFeatureApplTypeId" size=1>
            <#list productFeatureApplTypes as productFeatureApplType>
            <option value="${(productFeatureApplType.productFeatureApplTypeId)?if_exists}">${(productFeatureApplType.description)?if_exists} </option>
            </#list>
        </select>
        <br>
        <span class="tabletext">${uiLabelMap.CommonFrom} : </span><input type=text size="25" name="fromDate" class="inputBox">
        <a href="javascript:call_cal(document.addFeatureById.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
        <span class="tabletext">${uiLabelMap.CommonThru} : </span><input type=text size="25" name="thruDate" class="inputBox">
        <a href="javascript:call_cal(document.addFeatureById.thruDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
        <span class="tabletext">${uiLabelMap.CommonSequence} : </span><input type=text size="5" name="sequenceNum" class="inputBox">
        <input type="submit" value="Add" style="font-size: x-small;">
        </form>
    </#if>
    <br>

<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
