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
        <a href="<@ofbizUrl>/EditProductFacilities?productId=${productId}</@ofbizUrl>" class="tabButtonSelected">Facilities</a>
        <a href="<@ofbizUrl>/EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="tabButton">Inventory</a>
        <a href="<@ofbizUrl>/EditProductGlAccounts?productId=${productId}</@ofbizUrl>" class="tabButton">Accounts</a>
        <#if product?exists && product.isVirtual.equals("Y")>
            <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="tabButton">Variants</a>
        </#if>
        </div>
    </#if>
    
    <div class="head1">Facilities <span class="head2">for <#if product?exists>${(product.productName)?if_exists}</#if>[ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?exists>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <p>    
    <#if productId?exists && product?exists>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Facility</b></div></td>
            <td align="center"><div class="tabletext"><b>Minimum&nbsp;Stock&nbsp;&amp;&nbsp;Reorder&nbsp;Quantity&nbsp;&amp;&nbsp;Days To Ship</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list productFacilities as productFacility>
        <#assign line = line+1>
        <#assign facility = productFacility.getRelatedOneCache("Facility")>
        <tr valign="middle">
            <td><div class="tabletext"><#if facility?exists>${facility.facilityName}<#else>[${productFacility.facilityId}]</#if></div></td>
            <td align="center">
                <FORM method=POST action="<@ofbizUrl>/updateProductFacility</@ofbizUrl>" name="lineForm${line}">
                    <input type=hidden name="productId" value="${(productFacility.productId)?if_exists}">
                    <input type=hidden name="facilityId" value="${(productFacility.facilityId)?if_exists}">
                    <input type=text size="10" name="minimumStock" value="${(productFacility.minimumStock)?if_exists}" class="inputBox">
                    <input type=text size="10" name="reorderQuantity" value="${(productFacility.reorderQuantity)?if_exists}" class="inputBox">
                    <input type=text size="10" name="daysToShip" value="${(productFacility.daysToShip)?if_exists}" class="inputBox">
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/deleteProductFacility?productId=${(productFacility.productId)?if_exists}&facilityId=${(productFacility.facilityId)?if_exists}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createProductFacility</@ofbizUrl>" style="margin: 0;" name="createProductFacilityForm">
            <input type="hidden" name="productId" value="${productId?if_exists}">
            <input type="hidden" name="useValues" value="true">
        
            <div class="head2">Add Facility:</div>
            <div class="tabletext">
                Facility:
                <select name="facilityId" class="selectBox">
                    <#list facilities as facility>
                        <option value="${(facility.facilityId)?if_exists}">${(facility.facilityName)?if_exists}</option>
                    </#list>
                </select>
                Minimum&nbsp;Stock:&nbsp;<input type=text size="10" name="minimumStock" class="inputBox">
                Reorder&nbsp;Quantity:&nbsp;<input type=text size="10" name="reorderQuantity" class="inputBox">
                Days&nbsp;To&nbsp;Ship:&nbsp;<input type=text size="10" name="daysToShip" class="inputBox">
                <input type="submit" value="Add" style="font-size: x-small;">
            </div>
        
        </form>
    </#if>    
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
