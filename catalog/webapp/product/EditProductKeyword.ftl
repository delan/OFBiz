<#if hasPermission>
    <#if productId?has_content>
        <div class="tabContainer">
        <a href="<@ofbizUrl>/EditProduct?productId=${productId}</@ofbizUrl>" class="tabButton">Product</a>
        <a href="<@ofbizUrl>/EditProductPrices?productId=${productId}</@ofbizUrl>" class="tabButton">Prices</a>
        <a href="<@ofbizUrl>/EditProductContent?productId=${productId}</@ofbizUrl>" class="tabButton">Content</a>
        <a href="<@ofbizUrl>/EditProductGoodIdentifications?productId=${productId}</@ofbizUrl>" class="tabButton">IDs</a>
        <a href="<@ofbizUrl>/EditProductCategories?productId=${productId}</@ofbizUrl>" class="tabButton">Categories</a>
        <a href="<@ofbizUrl>/EditProductKeyword?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButtonSelected">Keywords</a>
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
    
    <div class="head1">Keywords <span class="head2">for <#if product?exists>${(product.productName)?if_exists}</#if>[ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content >
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <br>
    
    <#if productId?exists && product?exists>
        <br>
        <div class="tabletext">NOTE: Keywords are automatically created when product information is changed, but you may manually CREATE or DELETE keywords here as well.</div>
        
        <TABLE border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
        <TR>
            <TD width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
                <tr>
                <TD align=left>
                    <DIV class="boxhead">Add product keyword:</DIV>
                </TD>
                <TD align=right>
                    <a href="<@ofbizUrl>/EditProduct?productId=${productId?if_exists}</@ofbizUrl>" class="lightbuttontext">[Edit Product]</a>
                </td>
                </tr>
            </table>
            </TD>
        </TR>
        <TR>
            <TD width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
                <tr>
                <td>
                    <form method="POST" action="<@ofbizUrl>/UpdateProductKeyword</@ofbizUrl>" style="margin: 0;">
                        <input type="hidden" name="UPDATE_MODE" value="CREATE">
                        <input type="hidden" name="PRODUCT_ID" value="${productId?if_exists}">
                        <span class="tabletext">Keyword: </span><input type="text" size="20" name="KEYWORD" value="" class="inputBox">
                        <span class="tabletext">Weight: </span><input type="text" size="4" name="relevancyWeight" value="1" class="inputBox">
                        <input type="submit" value="Add" style="font-size: x-small;">
                    </form>
                </td>
                </tr>
            </table>
            </TD>
        </TR>
        </TABLE>
        <BR>
        
        <TABLE border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
        <TR>
            <TD width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
            <tr>
                <TD align=left>
                    <DIV class="boxhead">Keywords</DIV>
                </TD>
                <TD align=right>
                    <a href="<@ofbizUrl>/UpdateProductKeywords?UPDATE_MODE=CREATE&PRODUCT_ID=${productId}</@ofbizUrl>" class="lightbuttontext">[Re-induce Keywords]</a>
                    <a href="<@ofbizUrl>/UpdateProductKeywords?UPDATE_MODE=DELETE&PRODUCT_ID=${productId}</@ofbizUrl>" class="lightbuttontext">[Delete All Keywords]</a>
                </td>
            </tr>
            </table>
            </TD>
        </TR>
        <TR>
            <TD width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
            <tr>
                <td valign=top>
                <TABLE width="100%" cellpadding="0" cellspacing="0" border="0">
                <#assign productKeywords = product.getRelated("ProductKeyword")>
                <#if (productKeywords.size() > 0)>
                    <#list productKeywords as productKeyword>
                    <#assign colSize = productKeywords.size()/3 + 1>
                    <#assign kIdx = 0>
                    <tr>                        
                        <#import "java.lang.Number.Long" as Long>
                        <#assign relevancy = productKeyword.relevancyWeight in Long>
                        <td align=right><#if relevancy?exists>${Static["org.ofbiz.core.util.UtilFormatOut"].formatQuantity(relevancy)}</#if>&nbsp;</td>
                        <td align=left>&nbsp;${(productKeyword.keyword)?if_exists}</td>
                        <td>&nbsp;&nbsp;</td>
                        <td align=left>
                            <a href="<@ofbizUrl>/UpdateProductKeyword?UPDATE_MODE=DELETE&PRODUCT_ID=${productId}&KEYWORD=${(productKeyword.keyword)?if_exists}</@ofbizUrl>" class="buttontext">
                            [Delete]</a>
                        </td>
                    </tr>
                    <#assign kIdx = kIdx + 1>
                    <#if (kIdx >= colSize)>
                        <#assign colSize = colSize + colSize>
                        </TABLE>
                        </TD>
                        <TD bgcolor="#FFFFFF" valign=top style="border-left: solid #CCCCCC 1px;">
                        <TABLE width="100%" cellpadding="0" cellspacing="0" border="0">      
                    </#if>
                    </#list>
                <#else>
                    <tr>
                    <td colspan="3"><div class="tabletext">No Keywords Found</div></td>
                    </tr>
                </#if>
                </TABLE>
                </td>
            </tr>
            </table>
        </TD>
    </TR>
    </TABLE>        
    <#else>
        <div class="head2">Product not found with Product ID "${productId?if_exists}"</div>
    </#if>
<#else>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
