<#if hasPermission>
    <#if productCategoryId?has_content>
        <div class="tabContainer">
        <a href="<@ofbizUrl>/EditCategory?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Category</a>
        <a href="<@ofbizUrl>/EditCategoryRollup?showProductCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Rollup</a>
        <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Products</a>
        <a href="<@ofbizUrl>/EditCategoryProdCatalogs?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Catalogs</a>
        <a href="<@ofbizUrl>/EditCategoryFeatureCats?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">FeatureCats</a>
        <a href="<@ofbizUrl>/EditCategoryParties?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButtonSelected">Parties</a>
        </div>
    </#if>
    
    <div class="head1">Catalogs <span class="head2">for <#if productCategory?exists>${(productCategory.description)?if_exists}</#if> [ID:${productCategoryId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[New Category]</a>
    <#if productCategoryId?has_content>
        <a href="/ecommerce/control/category?category_id=${productCategoryId}" class="buttontext" target="_blank">[Category Page]</a>
    </#if>
    <br>
    <br>
    <#if productCategoryId?exists && productCategory?exists>    
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
        <td><div class="tabletext"><b>Party ID</b></div></td>
        <td><div class="tabletext"><b>Role</b></div></td>
        <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
        <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
        <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list productCategoryRoles as productCategoryRole>
        <#assign line = line + 1>
        <#assign curRoleType = productCategoryRole.getRelatedOneCache("RoleType")>
        <tr valign="middle">
        <td><a href="/partymgr/control/viewprofile?party_id=${(productCategoryRole.partyId)?if_exists}" target="_blank" class="buttontext">[${(productCategoryRole.partyId)?if_exists}]</a></td>
        <td><div class="tabletext">${(curRoleType.description)?if_exists}</div></td>
        <#assign hasntStarted = false>
        <#if (productCategoryRole.getTimestamp("fromDate"))?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().before(productCategoryRole.getTimestamp("fromDate"))> <#assign hasntStarted = true></#if>
        <td><div class="tabletext"<#if hasntStarted> style="color: red;"</#if>>${(productCategoryRole.fromDate)?if_exists}</div></td>
        <td align="center">
            <FORM method=POST action="<@ofbizUrl>/updatePartyToCategory</@ofbizUrl>" name="lineForm${line}">
                <#assign hasExpired = false>
                <#if (productCategoryRole.getTimestamp("thruDate"))?exists && (Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(productCategoryRole.getTimestamp("thruDate")))> <#assign hasExpired = true></#if>
                <input type=hidden name="productCategoryId" value="${(productCategoryRole.productCategoryId)?if_exists}">
                <input type=hidden name="partyId" value="${(productCategoryRole.partyId)?if_exists}">
                <input type=hidden name="roleTypeId" value="${(productCategoryRole.roleTypeId)?if_exists}">
                <input type=hidden name="fromDate" value="${(productCategoryRole.getTimestamp("fromDate"))?if_exists}">
                <input type=text size="25" name="thruDate" value="${(productCategoryRole. getTimestamp("thruDate"))?if_exists}" class="inputBox" <#if hasExpired> style="color: red;"</#if>>
                <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(productCategoryRole.getTimestamp("thruDate"))?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                <INPUT type=submit value="Update" style="font-size: x-small;">
            </FORM>
        </td>
        <td align="center">
            <a href="<@ofbizUrl>/removePartyFromCategory?productCategoryId=${(productCategoryRole.productCategoryId)?if_exists}&partyId=${(productCategoryRole.partyId)?if_exists}&roleTypeId=${(productCategoryRole.roleTypeId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(productCategoryRole.getTimestamp("fromDate").toString())}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
        </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/addPartyToCategory</@ofbizUrl>" style="margin: 0;" name="addNewForm">
        <input type="hidden" name="productCategoryId" value="${productCategoryId}">
        <input type="hidden" name="tryEntity" value="true">
        
        <div class="head2">Associate Party to Category (enter Party ID, select Type, then enter optional From Date):</div>
        <br>
        <input type="text" class="inputBox" size="20" maxlength="20" name="partyId" value="">
        <select name="roleTypeId" size=1 class="selectBox">
        <#list roleTypes as roleType>
            <option value="${(roleType.roleTypeId)?if_exists}" <#if roleType.roleTypeId.equals("_NA_")> selected</#if>>${(roleType.description)?if_exists}</option>
        </#list>
        </select>
        <input type="text" size="25" name="fromDate" class="inputBox">
        <a href="javascript:call_cal(document.addNewForm.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
        <input type="submit" value="Add">
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
