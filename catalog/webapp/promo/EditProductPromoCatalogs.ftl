<#if hasPermission>
    <#if productPromoId?has_content>
        <div class="tabContainer">
        <a href="<@ofbizUrl>/EditProductPromo?productPromoId=${productPromoId}</@ofbizUrl>" class="tabButton">Promo</a>
        <a href="<@ofbizUrl>/EditProductPromoRules?productPromoId=${productPromoId}</@ofbizUrl>" class="tabButton">Rules</a>
        <a href="<@ofbizUrl>/EditProductPromoCatalogs?productPromoId=${productPromoId}</@ofbizUrl>" class="tabButtonSelected">Catalogs</a>
        </div>
   </#if>
    
    <div class="head1">Catalogs <span class="head2">for <#if productPromo?exists>${(productPromo.promoName)?if_exists}</#if> [ID:${productPromoId?if_exists}]</span></div>
    <a href="<@ofbizUrl>/EditProductPromo</@ofbizUrl>" class="buttontext">[New ProductPromo]</a>
    
    <br>
    <br>
    <#if productPromoId?exists && productPromo?exists>   
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Catalog Name [ID]</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>        
        <#list prodCatalogPromoAppls as prodCatalogPromoAppl>
        <#assign line = line + 1>
        <#assign prodCatalog = prodCatalogPromoAppl.getRelatedOne("ProdCatalog")>
        <tr valign="middle">
            <td><a href="<@ofbizUrl>/EditProdCatalog?prodCatalogId=${(prodCatalogPromoAppl.prodCatalogId)?if_exists}</@ofbizUrl>" class="buttontext"><#if productPromo?exists>${(prodCatalog.catalogName)?if_exists}</#if>[${(prodCatalogPromoAppl.prodCatalogId)?if_exists}]</a></td>
            <#assign hasntStarted = false>
            <#if (prodCatalogPromoAppl.getTimestamp("fromDate"))?exists && nowTimestamp.before(prodCatalogPromoAppl.getTimestamp("fromDate"))> <#assign hasntStarted = true></#if>
            <td><div class="tabletext" <#if hasntStarted>style="color: red;"></#if>${(prodCatalogPromoAppl.fromDate)?if_exists}</div></td>
            <td align="center">
                <#assign hasExpired = false>
                <#if (prodCatalogPromoAppl.getTimestamp("thruDate"))?exists && nowTimestamp.after(prodCatalogPromoAppl.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                <FORM method=POST action="<@ofbizUrl>/promo_updateProductPromoToProdCatalog</@ofbizUrl>" name="lineForm${line}">
                    <input type=hidden name="prodCatalogId" value="${(prodCatalogPromoAppl.prodCatalogId)?if_exists}">
                    <input type=hidden name="productPromoId" value="${(prodCatalogPromoAppl.productPromoId)?if_exists}">
                    <input type=hidden name="fromDate" value="${(prodCatalogPromoAppl.fromDate)?if_exists}">
                    <input type=text size="20" name="thruDate" value="${(prodCatalogPromoAppl.thruDate)?if_exists}" class="inputBox" <#if hasExpired>style="color: red;"></#if>
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${nowTimestamp.toString()}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                    <input type=text size="5" name="sequenceNum" value="${(prodCatalogPromoAppl.sequenceNum)?if_exists}" class="inputBox">
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/promo_removeProductPromoFromProdCatalog?prodCatalogId=${(prodCatalogPromoAppl.prodCatalogId)?if_exists}&productPromoId=${(prodCatalogPromoAppl.productPromoId)?if_exists}&fromDate=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(prodCatalogPromoAppl.getTimestamp("fromDate").toString())}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/promo_addProductPromoToProdCatalog</@ofbizUrl>" name="addProductPromoToCatalog" style="margin: 0;">
        <input type="hidden" name="productPromoId" value="${productPromoId}">
        <input type="hidden" name="tryEntity" value="true">
        
        <div class="head2">Add Catalog Promo (select Catalog, enter optional From Date):</div>
        <br>
        <select name="prodCatalogId" class="selectBox">
        <#list prodCatalogs as prodCatalog>
            <option value="${(prodCatalog.prodCatalogId)?if_exists}">${(prodCatalog.catalogName)?if_exists} [${(prodCatalog.prodCatalogId)?if_exists}]</option>
        </#list>
        </select>
        <input type=text size="20" name="fromDate" class="inputBox">
        <a href="javascript:call_cal(document.addProductPromoToCatalog.fromDate, '${nowTimestamp.toString()}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>        
        <input type="submit" value="Add">
        </form>
   </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
