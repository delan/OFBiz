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
        <a href="<@ofbizUrl>/EditProductFacilities?productId=${productId}</@ofbizUrl>" class="tabButton">Facilities</a>
        <a href="<@ofbizUrl>/EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="tabButton">Inventory</a>
        <a href="<@ofbizUrl>/EditProductGlAccounts?productId=${productId}</@ofbizUrl>" class="tabButtonSelected">Accounts</a>
        <#if product?exists && product.isVirtual.equals("Y")>
            <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="tabButton">Variants</a>
        </#if>
        </div>
    </#if>
    
    <div class="head1">GL Accounts <span class="head2">for <#if product?exists>${(product.productName)?if_exists}</#if> [ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <p>    
    <#if productId?exists && product?exists>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Account&nbsp;Type</b></div></td>
            <td align="center"><div class="tabletext"><b>GL&nbsp;Account</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list productGlAccounts as productGlAccount>
        <#assign line = line + 1>
        <#assign productGlAccountType = productGlAccount.getRelatedOneCache("ProductGlAccountType")>
        <#assign curGlAccount = productGlAccount.getRelatedOneCache("GlAccount")>
        <tr valign="middle">
            <td><div class="tabletext"><#if productGlAccountType?exists>${(productGlAccountType.description)?if_exists}<#else>[${(productGlAccount.productGlAccountTypeId)?if_exists}]</#if></div></td>
            <td align="center">
                <FORM method=POST action="<@ofbizUrl>/updateProductGlAccount</@ofbizUrl>" name="lineForm${line}">
                    <input type=hidden name="productId" value="${(productGlAccount.productId)?if_exists}">
                    <input type=hidden name="productGlAccountTypeId" value="${(productGlAccount.productGlAccountTypeId)?if_exists}">
                    <select class="selectBox" name="glAccountId">
                        <#if curGlAccount?exists>
                            <option value="${(curGlAccount.glAccountId)?if_exists}">${(curGlAccount.accountCode)?if_exists} ${(curGlAccount.accountName)?if_exists}</option>
                            <option value="${(curGlAccount.glAccountId)?if_exists}"></option>
                        </#if>
                        <#list glAccounts as glAccount>
                            <option value="${(glAccount.glAccountId)?if_exists}">${(glAccount.accountCode)?if_exists} ${(glAccount.accountName)?if_exists}</option>
                        </#list>
                    </select>
                    <input type=text size="20" name="glAccountId" value="${(productGlAccount.idValue)?if_exists}" class="inputBox">
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/deleteProductGlAccount?productId=${(productGlAccount.productId)?if_exists}&productGlAccountTypeId=${(productGlAccount.productGlAccountTypeId)?if_exists}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createProductGlAccount</@ofbizUrl>" style="margin: 0;" name="createProductGlAccountForm">
            <input type="hidden" name="productId" value="${productId}">
            <input type="hidden" name="useValues" value="true">
        
            <div class="head2">Add GL Account:</div>
            <div class="tabletext">
                Account Type:
                <select name="productGlAccountTypeId" class="selectBox">
                    <#list productGlAccountTypes as productGlAccountType>
                        <option value="${(productGlAccountType.productGlAccountTypeId)?if_exists}">${(productGlAccountType.description)?if_exists}</option>
                    </#list>
                </select>
                GL Account: 
                <select name="glAccountId" class="inputBox">
                    <#list glAccounts as glAccount>
                        <option value="${(glAccount.glAccountId)?if_exists}">${(glAccount.accountCode)?if_exists} ${(glAccount.accountName)?if_exists}</option>
                    </#list>
                </select>
                <input type="submit" value="Add" style="font-size: x-small;">
            </div>        
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
