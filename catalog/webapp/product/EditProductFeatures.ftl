<#if hasPermission>
    <#if productId?has_content>
        <div class='tabContainer'>
        <a href="<@ofbizUrl>/EditProduct?productId=${productId}</@ofbizUrl>" class="tabButton">Product</a>
        <a href="<@ofbizUrl>/EditProductPrices?productId=${productId}</@ofbizUrl>" class="tabButton">Prices</a>
        <a href="<@ofbizUrl>/EditProductContent?productId=${productId}</@ofbizUrl>" class="tabButton">Content</a>
        <a href="<@ofbizUrl>/EditProductGoodIdentifications?productId=${productId}</@ofbizUrl>" class="tabButton">IDs</a>
        <a href="<@ofbizUrl>/EditProductCategories?productId=${productId}</@ofbizUrl>" class="tabButton">Categories</a>
        <a href="<@ofbizUrl>/EditProductKeyword?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Keywords</a>
        <a href="<@ofbizUrl>/EditProductAssoc?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Associations</a>
        <a href="<@ofbizUrl>/EditProductAttributes?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Attributes</a>
        <a href="<@ofbizUrl>/EditProductFeatures?productId=${productId}</@ofbizUrl>" class="tabButtonSelected">Features</a>
        <a href="<@ofbizUrl>/EditProductFacilities?productId=${productId}</@ofbizUrl>" class="tabButton">Facilities</a>
        <a href="<@ofbizUrl>/EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="tabButton">Inventory</a>
        <a href="<@ofbizUrl>/EditProductGlAccounts?productId=${productId}</@ofbizUrl>" class="tabButton">Accounts</a>
        <#if product?exists && product.getString("isVirtual").equals("Y")>
            <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="tabButton">Variants</a>
        </#if>
        </div>
    </#if>
    
    <div class="head1">Features <span class='head2'>for <#if product?exists>${(product.productName)?if_exists} </#if> [ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class='buttontext' target='_blank'>[Product Page]</a>
    </#if>
    <br>
    <br>
    
    <#if productId?exists>    
        <table border="1" cellpadding='2' cellspacing='0'>
        <tr>
            <td><div class="tabletext"><b>Description</b></div></td>
            <td><div class="tabletext"><b>Type</b></div></td>
            <td><div class="tabletext"><b>Category</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
            <td><div class="tabletext"><b>Thru&nbsp;Date, Amount, Sequence, Application&nbsp;Type</b></div></td>
            <td><div class="tabletext">&nbsp;</div></td>
        </tr>
        <#assign line = 0>
        <#list productFeatureAndAppls as productFeatureAndAppl>
            <#assign line = line + 1>
            <#assign curProductFeatureApplType = productFeatureAndAppl.getRelatedOneCache("ProductFeatureApplType")>
            <#assign curProductFeatureCategory = productFeatureAndAppl.getRelatedOneCache("ProductFeatureCategory")>
<!--            <#if curProductFeatureCategory?exists> pageContext.setAttribute("curProductFeatureCategory", curProductFeatureCategory)</#if>	-->
            <tr valign="middle">
                <form method=POST action='<@ofbizUrl>/UpdateFeatureToProductApplication</@ofbizUrl>' name='lineForm${line}'>
                <input type=hidden name="productId" value="${(productFeatureAndAppl.productId)?if_exists}">
                <input type=hidden value="${(productFeatureAndAppl.productFeatureId)?if_exists}">
                <input type=hidden name="fromDate" value="${(productFeatureAndAppl.fromDate)?if_exists}">
                <td><div class='tabletext'>${(productFeatureAndAppl.description)?if_exists}></div></td>
                <td><div class='tabletext'>${(productFeatureAndAppl.productFeatureTypeId)?if_exists}</div></td>                
                <td><a href='<@ofbizUrl>/EditFeatureCategoryFeatures?productFeatureCategoryId=${(productFeatureAndAppl.productFeatureCategoryId)?if_exists}&productId=${(productFeatureAndAppl.productId)?if_exists}</@ofbizUrl>' class='buttontext'>
                    ${(curProductFeatureCategory.description)?if_exists}
                    [${(productFeatureAndAppl.productFeatureCategoryId)?if_exists}]</a></td>
                <#assign hasntStarted = false>
                <#if (productFeatureAndAppl.getTimestamp("fromDate"))?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().before(productFeatureAndAppl.getTimestamp("fromDate"))> <#assign hasntStarted = true></#if>
                <td><div class='tabletext'<#if hasntStarted> style='color: red;'</#if>>${(productFeatureAndAppl.fromDate)?if_exists}></div></td>
                <td>
                    <#assign hasExpired = false>
                    <#if (productFeatureAndAppl.getTimestamp("thruDate"))?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().after(productFeatureAndAppl.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                    <input type='text' size='25' name='thruDate' value='${(productFeatureAndApp.thruDate)?if_exists}' class='inputBox' <#if hasExpired> style='color: red;'</#if>>
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(productFeatureAndAppl.thruDate)?default(nowTimestampString)}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                    <input type=text size='6' name='amount' value='${(productFeatureAndAppl.amount)?if_exists}'  class='inputBox'>
                    <input type=text size='5' name='sequenceNum' value='${(productFeatureAndAppl.sequenceNum)?if_exists}' class='inputBox'>
                <select class='selectBox' name='productFeatureApplTypeId' size=1>
                    <#if (productFeatureAndAppl.productFeatureApplTypeId)?exists>
                        <option value='${(productFeatureAndAppl.productFeatureApplTypeId)?if_exists}'><#if curProductFeatureApplTyp?exists> ${(curProductFeatureApplType.description)?if_exists} <#else> [${productFeatureAndAppl.productFeatureApplTypeId}]</#if></option>
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
                <a href='<@ofbizUrl>/RemoveFeatureFromProduct?productId=${(productFeatureAndAppl.productId)?if_exists}>&productFeatureId=${(productFeatureAndAppl.productFeatureId)?if_exists}>&fromDate=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(productFeatureAndAppl.getTimestamp("fromDate").toString())})</@ofbizUrl>' class="buttontext">
                [Delete]</a>
                </td>
            </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/EditFeatureCategoryFeatures</@ofbizUrl>" style='margin: 0;'>
        <input type="hidden" name="productId" value="${productId}">
        <div class='head2'>Add ProductFeature from Category:</div>
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
        <div class='head2'>Add ProductFeature with Type and ID Code:</div>
        <br>
        <span class='tabletext'>Feature Type: </span><select class='selectBox' name='productFeatureTypeId' size=1>
            <#list productFeatureTypes as productFeatureType>
            <option value='${(productFeatureType.productFeatureTypeId)?if_exists}'>${(productFeatureType.description)?if_exists} </option>
            </#list>
        </select>
        <span class='tabletext'>ID Code: </span><input type=text size='10' name='idCode' value='' class='inputBox'>
        <br>
        <span class='tabletext'>Feature Application Type: </span><select class='selectBox' name='productFeatureApplTypeId' size=1>
            <#list productFeatureApplTypes as productFeatureApplType>
            <option value='${(productFeatureApplType.productFeatureApplTypeId)?if_exists}'>${(productFeatureApplType.description)?if_exists} </option>
            </#list>
        </select>
        <br>
        <span class='tabletext'>From: </span><input type=text size='25' name='fromDate' class='inputBox'>
        <a href="javascript:call_cal(document.addFeatureByTypeIdCode.fromDate, '${nowTimestampString}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
        <span class='tabletext'>Thru: </span><input type=text size='25' name='thruDate' class='inputBox'>
        <a href="javascript:call_cal(document.addFeatureByTypeIdCode.thruDate, '${nowTimestampString}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
        <span class='tabletext'>Sequence: </span><input type=text size='5' name='sequenceNum' class='inputBox'>
        <input type="submit" value="Add" style='font-size: x-small;'>
        </form>
    </#if>
    <br>

<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
