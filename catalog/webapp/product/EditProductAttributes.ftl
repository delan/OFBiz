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
        <a href="<@ofbizUrl>/EditProductAttributes?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButtonSelected">Attributes</a>
        <a href="<@ofbizUrl>/EditProductFeatures?productId=${productId}</@ofbizUrl>" class="tabButton">Features</a>
        <a href="<@ofbizUrl>/EditProductFacilities?productId=${productId}</@ofbizUrl>" class="tabButton">Facilities</a>
        <a href="<@ofbizUrl>/EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="tabButton">Inventory</a>
        <a href="<@ofbizUrl>/EditProductGlAccounts?productId=${productId}</@ofbizUrl>" class="tabButton">Accounts</a>
        <#if product?exists && product.isVirtual.equals("Y")>
            <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="tabButton">Variants</a>
        </#if>
        </div>
    </#if>
    
    <div class="head1">Attributes <span class="head2">for <#if product?exists>${(product.productName)?if_exists}</#if> [ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
    <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <p>    
    <#if productId?exists && product?exists>
        <table border="1" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Name</b></div></td>
            <td><div class="tabletext"><b>Value, Type</b></div></td>
        </tr>
        <#list productAttributes as productAttribute>
        <tr valign="middle">
            <td><div class="tabletext">${(productAttribute.attrName)?if_exists}</div></td>
            <td>
                <FORM method=POST action="<@ofbizUrl>/UpdateProductAttribute?UPDATE_MODE=UPDATE</@ofbizUrl>">
                    <input type=hidden name="PRODUCT_ID" value="${(productAttribute.productId)?if_exists}">
                    <input type=hidden name="ATTRIBUTE_NAME" value="${(productAttribute.attrName)?if_exists}">
                    <input type="text" class="inputBox" size="50" name="ATTRIBUTE_VALUE" value="${(productAttribute.attrValue)?if_exists}">
                    <input type="text" class="inputBox" size="15" name="ATTRIBUTE_TYPE" value="${(productAttribute.attrType)?if_exists}">
                    <INPUT type=submit value="Update">
                </FORM>
            </td>
            <td>
            <a href="<@ofbizUrl>/UpdateProductAttribute?UPDATE_MODE=DELETE&PRODUCT_ID=${(productAttribute.productId)?if_exists}&ATTRIBUTE_NAME=${(productAttribute.attrName)?if_exists}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/UpdateProductAttribute</@ofbizUrl>" style="margin: 0;">
        <input type="hidden" name="PRODUCT_ID" value="${productId}">
        <input type="hidden" name="UPDATE_MODE" value="CREATE">
        <input type="hidden" name="useValues" value="true">
        
        <div class="head2">Add ProductAttribute (enter Name, Value and Type):</div>
        <br>
        <input type="text" class="inputBox" name="ATTRIBUTE_NAME" size="15">&nbsp;
        <input type="text" class="inputBox" name="ATTRIBUTE_VALUE" size="50">&nbsp;
        <input type="text" class="inputBox" name="ATTRIBUTE_TYPE" size="15">&nbsp;
        <input type="submit" value="Add">
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
