<#if hasPermission>
    <#if prodCatalogId?has_content>
        <div class="tabContainer">
        <a href="<@ofbizUrl>/EditProdCatalog?prodCatalogId=${prodCatalogId}</@ofbizUrl>" class="tabButton">Catalog</a>
        <a href="<@ofbizUrl>/EditProdCatalogWebSites?prodCatalogId=${prodCatalogId}</@ofbizUrl>" class="tabButton">WebSites</a>
        <a href="<@ofbizUrl>/EditProdCatalogParties?prodCatalogId=${prodCatalogId}</@ofbizUrl>" class="tabButton">Parties</a>
        <a href="<@ofbizUrl>/EditProdCatalogCategories?prodCatalogId=${prodCatalogId}</@ofbizUrl>" class="tabButton">Categories</a>
        <a href="<@ofbizUrl>/EditProdCatalogPromos?prodCatalogId=${prodCatalogId}</@ofbizUrl>" class="tabButtonSelected">Promotions</a>
        </div>
    </#if>
    
    <div class="head1">Promotions <span class="head2">for <#if productCatalog?exists>${(productCatalog.catalogName)?if_exists}</#if> [ID:${prodCatalogId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProdCatalog</@ofbizUrl>" class="buttontext">[New ProdCatalog]</a>
    <p>
    <#if prodCatalogId?exists && prodCatalog?exists>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Promo&nbsp;Name&nbsp;[ID]</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list prodCatalogPromoAppls as prodCatalogPromoAppl>
        <#assign line = line+1>
        <#assign productPromo = prodCatalogPromoAppl.getRelatedOne("ProductPromo")>
        <tr valign="middle">
            <td><a href="<@ofbizUrl>/EditProductPromo?productPromoId=${(prodCatalogPromoAppl.productPromoId)?if_exists}</@ofbizUrl>" class="buttontext"><#if productPromo?exists>${(productPromo.promoName)?if_exists}</#if> [${(prodCatalogPromoAppl.productPromoId)?if_exists}]</a></td>
            <#assign hasntStarted = false>
            <#if prodCatalogPromoAppl.getTimestamp("fromDate")?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().before(prodCatalogPromoAppl.getTimestamp("fromDate"))> <#assign hasntStarted = true> </#if>
            <td><div class="tabletext" <#if hasntStarted> style="color: red;"</#if> >${prodCatalogPromoAppl.getTimestamp("fromDate").toString()}</div></td>
            <td align="center">
                <#assign hasExpired = false>
                <#if prodCatalogPromoAppl.getTimestamp("thruDate")?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().after(prodCatalogPromoAppl.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                <FORM method=POST action="<@ofbizUrl>/updateProductPromoToProdCatalog</@ofbizUrl>" name="lineForm${line}">
                    <input type=hidden name="prodCatalogId" value="${(prodCatalogPromoAppl.prodCatalogId)?if_exists}">
                    <input type=hidden name="productPromoId" value="${(prodCatalogPromoAppl.productPromoId)?if_exists}">
                    <input type=hidden name="fromDate" value="${(prodCatalogPromoAppl.fromDate)?if_exists}">
                    <input type=text size="25" name="thruDate" value="${(prodCatalogPromoAppl.thruDate)?if_exists}" class="inputBox" style="<#if (hasExpired) >color: red;</#if>">
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(prodCatalogPromoAppl.thruDate)?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                    <input type=text size="5" name="sequenceNum" value="${(prodCatalogPromoAppl.sequenceNum)?if_exists}" class="inputBox">
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/removeProductPromoFromProdCatalog?prodCatalogId=${(prodCatalogPromoAppl.prodCatalogId)?if_exists}&productPromoId=${(prodCatalogPromoAppl.productPromoId)?if_exists}&fromDate=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(prodCatalogPromoAppl.getTimestamp("fromDate").toString())}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/addProductPromoToProdCatalog</@ofbizUrl>" style="margin: 0;" name="addNewForm">
        <input type="hidden" name="prodCatalogId" value="${prodCatalogId?if_exists}">
        <input type="hidden" name="tryEntity" value="true">
        
        <div class="head2">Add Catalog Promo (select Promo, enter optional From Date):</div>
        <br>
        <select name="productPromoId" class="selectBox">
        <#list productPromos as productPromo>
            <option value="${(productPromo.productPromoId)?if_exists}">${(productPromo.promoName)?if_exists} [${(productPromo.productPromoId)?if_exists}]</option>
        </#list>
        </select>
        <input type=text size="25" name="fromDate" class="inputBox">
        <a href="javascript:call_cal(document.addNewForm.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
        <input type="submit" value="Add">
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
