<%--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     David E. Jones
 *@created    May 20 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String productCategoryId = request.getParameter("productCategoryId");
    GenericValue productCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
    if (productCategory == null) tryEntity = false;
    Collection prodCatalogCategories = productCategory.getRelated("ProductCategoryRole", null, UtilMisc.toList("roleTypeId", "sequenceNum", "partyId"));
    if (prodCatalogCategories != null) pageContext.setAttribute("prodCatalogCategories", prodCatalogCategories);

    Collection roleTypes = delegator.findAll("RoleType", UtilMisc.toList("description"));
    if (roleTypes != null) pageContext.setAttribute("roleTypes", roleTypes);

    if ("true".equalsIgnoreCase((String)request.getParameter("tryEntity"))) tryEntity = true;
%>
<br>

<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditCategory?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Category</a>
    <a href="<ofbiz:url>/EditCategoryRollup?showProductCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Rollup</a>
    <a href="<ofbiz:url>/EditCategoryProducts?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Products</a>
    <a href="<ofbiz:url>/EditCategoryProdCatalogs?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Catalogs</a>
    <a href="<ofbiz:url>/EditCategoryParties?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButtonSelected'>Parties</a>
  </div>
<%}%>

<div class="head1">Catalogs <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(productCategory==null?null:productCategory.getString("description"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productCategoryId)%>]</span></div>

<a href="<ofbiz:url>/EditCategory</ofbiz:url>" class="buttontext">[New Category]</a>
<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <a href="/ecommerce/control/category?category_id=<%=productCategoryId%>" class="buttontext" target='_blank'>[Category Page]</a>
<%}%>
<br>
<br>
<%if(productCategoryId!=null && productCategory!=null){%>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Party ID</b></div></td>
    <td><div class="tabletext"><b>Role</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="productCategoryRole" property="prodCatalogCategories">
  <%GenericValue curRoleType = productCategoryRole.getRelatedOneCache("RoleType");%>
  <%if (curRoleType != null) pageContext.setAttribute("curRoleType", curRoleType);%>
  <tr valign="middle">
    <td><a href='/partymgr/control/viewprofile?party_id=<ofbiz:inputvalue entityAttr="productCategoryRole" field="partyId"/>' target="_blank" class="buttontext">[<ofbiz:inputvalue entityAttr="productCategoryRole" field="partyId"/>]</a></td>
    <td><div class='tabletext'><ofbiz:inputvalue entityAttr="curRoleType" field="description"/></div></td>
    <%boolean hasntStarted = false;%>
    <%if (productCategoryRole.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(productCategoryRole.getTimestamp("fromDate"))) { hasntStarted = true; }%>
    <td><div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>><ofbiz:inputvalue entityAttr="productCategoryRole" field="fromDate"/></div></td>
    <td align="center">
        <FORM method=POST action='<ofbiz:url>/updatePartyToCategory</ofbiz:url>'>
            <%boolean hasExpired = false;%>
            <%if (productCategoryRole.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(productCategoryRole.getTimestamp("thruDate"))) { hasExpired = true; }%>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRole" field="productCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRole" field="partyId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRole" field="roleTypeId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRole" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="productCategoryRole" field="thruDate" fullattrs="true"/> style='font-size: x-small; <%if (hasExpired) {%>color: red;<%}%>'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/removePartyFromCategory?productCategoryId=<ofbiz:entityfield attribute="productCategoryRole" field="productCategoryId"/>&partyId=<ofbiz:entityfield attribute="productCategoryRole" field="partyId"/>&roleTypeId=<ofbiz:entityfield attribute="productCategoryRole" field="roleTypeId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(productCategoryRole.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/addPartyToCategory</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="productCategoryId" value="<%=productCategoryId%>">
  <input type="hidden" name="tryEntity" value="true">

  <div class='head2'>Associate Party to Category (enter Party ID, select Type, then enter optional From Date):</div>
  <br>
  <input type="text" size="20" maxlength="20" name="partyId" value="">
  <select name='roleTypeId' size=1>
    <%-- <option value=''>&nbsp;</option> --%>
    <ofbiz:iterator name="roleType" property="roleTypes">
      <option value='<%=roleType.getString("roleTypeId")%>'<%if ("_NA_".equals(roleType.getString("roleTypeId"))) {%> selected<%}%>><%=roleType.getString("description")%><%-- [<%=roleType.getString("roleTypeId")%>]--%></option>
    </ofbiz:iterator>
  </select>
  <input type=text size='20' name='fromDate'>
  <input type="submit" value="Add">
</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
