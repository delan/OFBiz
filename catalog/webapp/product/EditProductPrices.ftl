<#if hasPermission> 
    <#if productId?has_content>
        <div class='tabContainer'>
        <a href="<@ofbizUrl>/EditProduct?productId=${productId}</@ofbizUrl>" class="tabButton">Product</a>
        <a href="<@ofbizUrl>/EditProductPrices?productId=${productId}</@ofbizUrl>" class="tabButtonSelected">Prices</a>
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
        <#if product?exists && product.getString("isVirtual").equals("Y")>
            <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="tabButton">Variants</a>
        </#if>
        </div>
    </#if>
    
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
        <#assign currencyUom = productPrice.getRelatedOneCache("CurrencyUom")>
        <#assign productPriceType = productPrice.getRelatedOneCache("ProductPriceType")>
        <#assign facilityGroup = productPrice.getRelatedOneCache("FacilityGroup")>
        <tr valign="middle">
            <td><div class='tabletext'><#if productPriceType?has_content> ${(productPriceType.description)?if_exists} <#else> [${productPrice.productPriceTypeId}]</#if></div></td>
            <td><div class='tabletext'><#if currencyUom?has_content> ${currencyUom.description?if_exists} [${productPrice.currencyUomId}]</#if></div></td>
            <td><div class='tabletext'><#if facilityGroup.facilityGroupId?has_content> ${facilityGroup.facilityGroupName?if_exists} <#else> [${productPrice.facilityGroupId}]</#if></div></td>
            <td>
                <#assign hasntStarted = false>
                <#if productPrice.getTimestamp("fromDate")?has_content && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().before(productPrice.getTimestamp("fromDate"))> <#assign hasntStarted = true> </#if>
                <div class='tabletext' <#if hasntStarted> style='color: red;'</#if>>
                        ${productPrice.getTimestamp("fromDate")}
                </div>
            </td>
            <td align="center">
                <#assign hasExpired = false>
                <#if productPrice.getTimestamp("thruDate")?has_content && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().after(productPrice.getTimestamp("thruDate"))> <#assign hasExpired = true> </#if>
                <form method=POST action='<@ofbizUrl>/updateProductPrice</@ofbizUrl>' name='lineForm${line}'>
                    <input type=hidden name='productId' value='${productPrice.productId}'>
                    <input type=hidden name='productPriceTypeId' value='${productPrice.productPriceTypeId}'>
                    <input type=hidden name='currencyUomId' value='${productPrice.currencyUomId?default(defaultCurrencyUomId)}'>
                    <input type=hidden name='facilityGroupId' value='${productPrice.facilityGroupId}'>
                    <input type=hidden name='fromDate' value='${productPrice.fromDate}'>
                    <input type='text' class='inputBox' size='25' name='thruDate' value='<#if productPrice.thruDate?has_content>${productPrice.thruDate?if_exists}</#if>' <#if hasExpired> style='color: red;'</#if>>
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(productPrice.thruDate.toString())?default(nowTimestampString)}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                    <input type='text' class='inputBox' size='8' name='price' value='${productPrice.price}'>
                    <INPUT type='submit' value='Update' style='font-size: x-small;'>
                </form>
            </td>
            <td><div class='tabletext'>[${productPrice.lastModifiedByUserLogin}] on ${productPrice.lastModifiedDate}</div></td>
            <td align="center">
            <a href='<@ofbizUrl>/deleteProductPrice?productId=${productPrice.productId}&productPriceTypeId=${productPrice.productPriceTypeId}&currencyUomId=${productPrice.currencyUomId}&facilityGroupId=${productPrice.facilityGroupId}&fromDate=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(productPrice.getTimestamp("fromDate").toString())}</@ofbizUrl>' class="buttontext">
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
                <a href="javascript:call_cal(document.createProductPriceForm.fromDate, '${Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().toString()}');"> <img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                Price: <input type='text' size='8' name='price' class='inputBox'>&nbsp;<input type="submit" value="Add" style='font-size: x-small;'>
            </div>        
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
