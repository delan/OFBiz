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
 *@version    $Revision: 1.3 $
 *@since      2.2
-->

<#if hasPermission> 

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">Prices <span class='head2'>for ${(product.productName)?if_exists}&nbsp;<#if product.productId?has_content>[ID:${product.getString("productId")}]</#if></span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
    <a href="/ecommerce/control/product?product_id=${productId}" class='buttontext' target='_blank'>[Product Page]</a>
    </#if>
    <br>
    <br>
    
    <#if productId?has_content>
        <table border="1" width="100%" cellpadding='2' cellspacing='0'>
        <tr>
            <td><div class="tabletext"><b>Price&nbsp;Type</b></div></td>
            <td><div class="tabletext"><b>Currency</b></div></td>
            <td><div class="tabletext"><b>Facility Group</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Price</b></div></td>
            <td><div class="tabletext"><b>Last Modified By</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list productPrices as productPrice>
        <#assign line = line + 1>
        <#assign currencyUom = productPrice.getRelatedOneCache("CurrencyUom")?if_exists>
        <#assign productPriceType = productPrice.getRelatedOneCache("ProductPriceType")?if_exists>
        <#assign facilityGroup = productPrice.getRelatedOneCache("FacilityGroup")?if_exists>
        <tr valign="middle">
            <td><div class='tabletext'><#if productPriceType?has_content>${(productPriceType.description)?if_exists}<#else>[${productPrice.productPriceTypeId}]</#if></div></td>
            <td><div class='tabletext'><#if currencyUom?has_content>${currencyUom.description?if_exists} [${productPrice.currencyUomId}]</#if></div></td>
            <td><div class='tabletext'><#if (facilityGroup.facilityGroupId)?has_content>${facilityGroup.facilityGroupName?if_exists}<#else>[${productPrice.facilityGroupId}]</#if></div></td>
            <td>
                <#assign hasntStarted = false>
                <#if productPrice.fromDate?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().before(productPrice.fromDate)><#assign hasntStarted = true></#if>
                <div class='tabletext' <#if hasntStarted> style='color: red;'</#if>>
                        ${productPrice.getTimestamp("fromDate")}
                </div>
            </td>
            <td align="center">
                <#assign hasExpired = false>
                <#if productPrice.thruDate?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(productPrice.thruDate)><#assign hasExpired = true></#if>
                <form method=POST action='<@ofbizUrl>/updateProductPrice</@ofbizUrl>' name='lineForm${line}'>
                    <input type=hidden name='productId' value='${productPrice.productId}'>
                    <input type=hidden name='productPriceTypeId' value='${productPrice.productPriceTypeId}'>
                    <input type=hidden name='currencyUomId' value='${productPrice.currencyUomId?default(defaultCurrencyUomId)}'>
                    <input type=hidden name='facilityGroupId' value='${productPrice.facilityGroupId}'>
                    <input type=hidden name='fromDate' value='${productPrice.fromDate}'>
                    <input type='text' class='inputBox' size='25' name='thruDate' value='<#if productPrice.thruDate?has_content>${(productPrice.thruDate.toString())?if_exists}</#if>' <#if hasExpired> style='color: red;'</#if>>
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(productPrice.thruDate.toString())?default(nowTimestampString)}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                    <input type='text' class='inputBox' size='8' name='price' value='${productPrice.price?if_exists}'>
                    <INPUT type='submit' value='Update' style='font-size: x-small;'>
                </form>
            </td>
            <td><div class='tabletext'>[${productPrice.lastModifiedByUserLogin?if_exists}] on ${productPrice.lastModifiedDate?if_exists}</div></td>
            <td align="center">
            <a href='<@ofbizUrl>/deleteProductPrice?productId=${productPrice.productId}&productPriceTypeId=${productPrice.productPriceTypeId}&currencyUomId=${productPrice.currencyUomId}&facilityGroupId=${productPrice.facilityGroupId}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(productPrice.getTimestamp("fromDate").toString())}</@ofbizUrl>' class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createProductPrice</@ofbizUrl>" style='margin: 0;' name='createProductPriceForm'>
            <input type="hidden" name="productId" value="${productId}">
            <input type="hidden" name="useValues" value="true">
        
            <div class='head2'>Add Price:</div>
            <div class='tabletext'>
                Price Type:
                <select name="productPriceTypeId" class='selectBox'>
                    <#list productPriceTypes as productPriceType>
                        <option value='${productPriceType.productPriceTypeId}'> ${productPriceType.description}</option>
                    </#list>
                </select>
                Currency:
                <select name="currencyUomId" class='selectBox'>
                    <#list currencyUoms as currencyUom>
                        <#assign isDefault = defaultCurrencyUomId.equals(currencyUom.getString("uomId"))>
                        <option value='${currencyUom.uomId}' <#if isDefault>selected</#if>> ${currencyUom.description} [${currencyUom.uomId}]</option>
                    </#list>
                </select>
                Facility Group:
                <select name="facilityGroupId" class='selectBox'>
                    <#list facilityGroups as facilityGroup>
                        <#assign isDefault = facilityGroup.facilityGroupId.equals("_NA_")>
                        <option value='${facilityGroup.facilityGroupId}' <#if isDefault>selected</#if>>${facilityGroup.facilityGroupName}</option>
                    </#list>
                </select>
            </div>
            <div class='tabletext'>
                From Date: <input type='text' size='25' name='fromDate' class='inputBox' value='${(productPrice.fromDate.toString())?if_exists}'>
                <a href="javascript:call_cal(document.createProductPriceForm.fromDate, '${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()}');"> <img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                Price: <input type='text' size='8' name='price' class='inputBox'>&nbsp;<input type="submit" value="Add" style='font-size: x-small;'>
            </div>        
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
