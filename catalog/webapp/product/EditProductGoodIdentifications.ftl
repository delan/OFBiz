<#if hasPermission>
    <#if productId?has_content>
        <div class="tabContainer">
        <a href="<@ofbizUrl>/EditProduct?productId=${productId}</@ofbizUrl>" class="tabButton">Product</a>
        <a href="<@ofbizUrl>/EditProductPrices?productId=${productId}</@ofbizUrl>" class="tabButton">Prices</a>
        <a href="<@ofbizUrl>/EditProductContent?productId=${productId}</@ofbizUrl>" class="tabButton">Content</a>
        <a href="<@ofbizUrl>/EditProductGoodIdentifications?productId=${productId}</@ofbizUrl>" class="tabButtonSelected">IDs</a>
        <a href="<@ofbizUrl>/EditProductCategories?productId=${productId}</@ofbizUrl>" class="tabButton">Categories</a>
        <a href="<@ofbizUrl>/EditProductKeyword?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Keywords</a>
        <a href="<@ofbizUrl>/EditProductAssoc?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Associations</a>
        <a href="<@ofbizUrl>/EditProductAttributes?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Attributes</a>
        <a href="<@ofbizUrl>/EditProductFeatures?productId=${productId}</@ofbizUrl>" class="tabButton">Features</a>
        <a href="<@ofbizUrl>/EditProductFacilities?productId=${productId}</@ofbizUrl>" class="tabButton">Facilities</a>
        <a href="<@ofbizUrl>/EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="tabButton">Inventory</a>
        <a href="<@ofbizUrl>/EditProductGlAccounts?productId=${productId}</@ofbizUrl>" class="tabButton">Accounts</a>
        <#if product?exists && product.isVirtual.equals("Y")>
            <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="tabButton">Variants</a>
        </#if>
        </div>
    </#if>
    
    <div class="head1">IDs <span class="head2">for <#if product?exists>${(product.productName)?if_exists}</#if> [ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <br>
    <br>
    
    <#if productId?exists && product?exists>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>ID&nbsp;Type</b></div></td>
            <td align="center"><div class="tabletext"><b>ID&nbsp;Value</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list goodIdentifications as goodIdentification>
        <#assign line = line + 1>
        <#assign goodIdentificationType = goodIdentification.getRelatedOneCache("GoodIdentificationType")>
        <tr valign="middle">
            <td><div class="tabletext"><#if goodIdentificationType?exists>${(goodIdentificationType.description)?if_exists}<#else>[${(goodIdentification.goodIdentificationTypeId)?if_exists}]</#if></div></td>
            <td align="center">
                <FORM method=POST action="<@ofbizUrl>/updateGoodIdentification</@ofbizUrl>" name="lineForm${line}">
                    <input type=hidden name="productId" value="${(goodIdentification.productId)?if_exists}">
                    <input type=hidden name="goodIdentificationTypeId" value="${(goodIdentification.goodIdentificationTypeId)?if_exists}">
                    <input type=text size="20" name="idValue" value="${(goodIdentification.idValue)?if_exists}" class="inputBox">
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/deleteGoodIdentification?productId=${(goodIdentification.productId)?if_exists}&goodIdentificationTypeId=${(goodIdentification.goodIdentificationTypeId)?if_exists}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createGoodIdentification</@ofbizUrl>" style="margin: 0;" name="createGoodIdentificationForm">
            <input type="hidden" name="productId" value="${productId}">
            <input type="hidden" name="useValues" value="true">
        
            <div class="head2">Add ID:</div>
            <div class="tabletext">
                ID Type:
                <select name="goodIdentificationTypeId" class="selectBox">
                    <#list goodIdentificationTypes as goodIdentificationType>
                        <option value="${(goodIdentificationType.goodIdentificationTypeId)?if_exists}">${(goodIdentificationType.description)?if_exists}</option>
                    </#list>
                </select>
                ID Value: <input type=text size="20" name="idValue" class="inputBox">&nbsp;<input type="submit" value="Add" style="font-size: x-small;">
            </div>        
        </form>
    </#if>    
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
