<#if hasPermission>
    <#if productCategoryId?has_content>
        <div class="tabContainer">
            <a href="<@ofbizUrl>/EditCategory?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Category</a>
            <a href="<@ofbizUrl>/EditCategoryRollup?showProductCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Rollup</a>
            <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Products</a>
            <a href="<@ofbizUrl>/EditCategoryProdCatalogs?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Catalogs</a>
            <a href="<@ofbizUrl>/EditCategoryFeatureCats?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButtonSelected">FeatureCats</a>
            <a href="<@ofbizUrl>/EditCategoryParties?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Parties</a>
        </div>
    </#if>
    
    <div class="head1">Catalogs <span class="head2">for <#if productCategory?exists>${(productCategory.description)?if_exists}</#if> [ID:${productCategoryId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[New Category]</a>
    <#if productCategoryId?has_content>
        <a href="/ecommerce/control/category?category_id=${productCategoryId?if_exists}" class="buttontext" target="_blank">[Category Page]</a>
    </#if>
    <p>
    <#if productCategoryId?exists && productCategory?exists>    
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Catalog Name [ID]</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list productFeatureCategoryAppls as productFeatureCategoryAppl>
        <#assign line = line + 1>
        <#assign productFeatureCategory = (productFeatureCategoryAppl.getRelatedOne("ProductFeatureCategory"))?default(null)>
        <tr valign="middle">
            <td><a href="<@ofbizUrl>/EditFeatureCategoryFeatures?productFeatureCategoryId=${(productFeatureCategoryAppl.productFeatureCategoryId)?if_exists}</@ofbizUrl>" class="buttontext"><#if productFeatureCategory?exists>${(productFeatureCategory.description)?if_exists}</#if> [${(productFeatureCategoryAppl.productFeatureCategoryId)?if_exists}]</a></td>
            <#assign hasntStarted = false>
            <#if (productFeatureCategoryAppl.getTimestamp("fromDate"))?exists && nowTimestamp.before(productFeatureCategoryAppl.getTimestamp("fromDate"))> <#assign hasntStarted = true></#if>
            <td><div class="tabletext"<#if hasntStarted> style="color: red;"</#if>>${(productFeatureCategoryAppl.fromDate)?if_exists}</div></td>
            <td align="center">
                <FORM method=POST action="<@ofbizUrl>/updateProductFeatureCategoryAppl</@ofbizUrl>" name="lineForm${line}">
                    <#assign hasExpired = false>
                    <#if (productFeatureCategoryAppl.getTimestamp("thruDate"))?exists && nowTimestamp.after(productFeatureCategoryAppl.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                    <input type=hidden name="productCategoryId" value="${(productFeatureCategoryAppl.productCategoryId)?if_exists}">
                    <input type=hidden name="productFeatureCategoryId" value="${(productFeatureCategoryAppl.productFeatureCategoryId)?if_exists}">
                    <input type=hidden name="fromDate" value="${(productFeatureCategoryAppl.fromDate)?if_exists}">
                    <input type=text size="25" name="thruDate" value="${(productFeatureCategoryAppl.thruDate)?if_exists}" class="inputBox" <#if hasExpired>style="color: red;"</#if>>
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(productFeatureCategoryAppl.thruDate)?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/removeProductFeatureCategoryAppl?productFeatureCategoryId=${(productFeatureCategoryAppl.productFeatureCategoryId)?if_exists}&productCategoryId=${(productFeatureCategoryAppl.productCategoryId)?if_exists}&fromDate=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(productFeatureCategoryAppl.getTimestamp("fromDate").toString())}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createProductFeatureCategoryAppl</@ofbizUrl>" style="margin: 0;" name="addNewForm">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
        <input type="hidden" name="tryEntity" value="true">
        
        <div class="head2">Add Catalog Product Category (select Category and Type, then enter optional From Date):</div>
        <br>
        <select name="productFeatureCategoryId" class="selectBox">
        <#list productFeatureCategories as productFeatureCategory>
            <option value="${(productFeatureCategory.productFeatureCategoryId)?if_exists}">${(productFeatureCategory.description)?if_exists} [${(productFeatureCategory.productFeatureCategoryId)?if_exists}]</option>
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
