<#if hasPermission>
    <#if productCategoryId?has_content>
        <div class="tabContainer">
            <a href="<@ofbizUrl>/EditCategory?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Category</a>
            <a href="<@ofbizUrl>/EditCategoryRollup?showProductCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Rollup</a>
            <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Products</a>
            <a href="<@ofbizUrl>/EditCategoryProdCatalogs?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButtonSelected">Catalogs</a>
            <a href="<@ofbizUrl>/EditCategoryFeatureCats?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">FeatureCats</a>
            <a href="<@ofbizUrl>/EditCategoryParties?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Parties</a>
        </div>
    </#if>
    
    <div class="head1">Catalogs <span class="head2">for <#if productCategory?exists>${(productCategory.description)?if_exists} </#if>[ID:${productCategoryId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[New Category]</a>
    <#if productCategoryId?has_content>
        <a href="/ecommerce/control/category?category_id=${productCategoryId}" class="buttontext" target="_blank">[Category Page]</a>
    </#if>
    <br>
    <br>
    <#if productCategoryId?exists && productCategory?exists>    
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Catalog Name [ID]</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence&nbsp;&amp;&nbsp;Type</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list prodCatalogCategories as prodCatalogCategory>
        <#assign line = line + 1>
        <#assign prodCatalog = prodCatalogCategory.getRelatedOne("ProdCatalog")>
        <#assign curProdCatalogCategoryType = prodCatalogCategory.getRelatedOneCache("ProdCatalogCategoryType")>
        <tr valign="middle">
            <td><a href="<@ofbizUrl>/EditProdCatalog?prodCatalogId=${(prodCatalogCategory.prodCatalogId)?if_exists}</@ofbizUrl>" class="buttontext"><#if prodCatalog?exists>${(prodCatalog.catalogName)?if_exists}</#if> [${(prodCatalogCategory.prodCatalogId)?if_exists}]</a></td>
            <#assign hasntStarted = false>
            <#if (prodCatalogCategory.getTimestamp("fromDate"))?exists && nowTimestamp.before(prodCatalogCategory.getTimestamp("fromDate"))> <#assign hasntStarted = true></#if>
            <td><div class="tabletext"<#if hasntStarted> style="color: red;"</#if>>${(prodCatalogCategory.fromDate)?if_exists}</div></td>
            <td align="center">
                <FORM method=POST action="<@ofbizUrl>/category_updateProductCategoryToProdCatalog</@ofbizUrl>" name="lineForm${line}">
                    <#assign hasExpired = false>
                    <#if (prodCatalogCategory.getTimestamp("thruDate"))?exists && nowTimestamp.after(prodCatalogCategory.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                    <input type=hidden name="prodCatalogId" value="${(prodCatalogCategory.prodCatalogId)?if_exists}">
                    <input type=hidden name="productCategoryId" value="${(prodCatalogCategory.productCategoryId)?if_exists}">
                    <input type=hidden name="fromDate" value="${(prodCatalogCategory.fromDate)?if_exists}">
                    <input type=text size="25" name="thruDate" value="${(prodCatalogCategory.thruDate)?if_exists}" class="inputBox" style="<#if (hasExpired) >color: red;</#if>">
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(prodCatalogCategory.thruDate)?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                    <input type=text size="5" name="sequenceNum" value="${(prodCatalogCategory.sequenceNum)?if_exists}" class="inputBox">
                    <select name="prodCatalogCategoryTypeId" size=1 class="selectBox">
                        <#if (prodCatalogCategory.prodCatalogCategoryTypeId)?exists>
                            <option value="${prodCatalogCategory.prodCatalogCategoryTypeId}"><#if curProdCatalogCategoryType?exists>${(curProdCatalogCategoryType.description)?if_exists}<#else> [${(prodCatalogCategory.prodCatalogCategoryTypeId)}]</#if></option>
                            <option value="${prodCatalogCategory.prodCatalogCategoryTypeId}"></option>
                        <#else>
                            <option value="">&nbsp;</option>
                        </#if>
                        <#list prodCatalogCategoryTypes as prodCatalogCategoryType>
                        <option value="${(prodCatalogCategoryType.prodCatalogCategoryTypeId)?if_exists}">${(prodCatalogCategoryType.description)?if_exists}</option>
                        </#list>
                    </select>
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/category_removeProductCategoryFromProdCatalog?prodCatalogId=${(prodCatalogCategory.prodCatalogId)?if_exists}&productCategoryId=${(prodCatalogCategory.productCategoryId)?if_exists}&fromDate=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(prodCatalogCategory.getTimestamp("fromDate").toString())}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/category_addProductCategoryToProdCatalog</@ofbizUrl>" style="margin: 0;" name="addNewForm">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
        <input type="hidden" name="tryEntity" value="true">
        
        <div class="head2">Add Catalog Product Category (select Category and Type, then enter optional From Date):</div>
        <br>
        <select name="prodCatalogId" class="selectBox">
        <#list prodCatalogs as prodCatalog>
            <option value="${(prodCatalog.prodCatalogId)?if_exists}">${(prodCatalog.catalogName)?if_exists} [${(prodCatalog.prodCatalogId)?if_exists}]</option>
        </#list>
        </select>
            <select name="prodCatalogCategoryTypeId" size=1 class="selectBox">
                <#list prodCatalogCategoryTypes as prodCatalogCategoryType>
                <option value="${(prodCatalogCategoryType.prodCatalogCategoryTypeId)?if_exists}">${(prodCatalogCategoryType.description)?if_exists}</option>
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
