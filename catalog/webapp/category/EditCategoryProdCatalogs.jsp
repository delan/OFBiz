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
    Collection prodCatalogCategories = productCategory.getRelated("ProdCatalogCategory", null, UtilMisc.toList("prodCatalogCategoryTypeId", "sequenceNum", "prodCatalogId"));
    if (prodCatalogCategories != null) pageContext.setAttribute("prodCatalogCategories", prodCatalogCategories);

    Collection prodCatalogs = delegator.findAll("ProdCatalog", UtilMisc.toList("catalogName"));
    if (prodCatalogs != null) pageContext.setAttribute("prodCatalogs", prodCatalogs);

    Collection prodCatalogCategoryTypes = delegator.findAll("ProdCatalogCategoryType", UtilMisc.toList("description"));
    if (prodCatalogCategoryTypes != null) pageContext.setAttribute("prodCatalogCategoryTypes", prodCatalogCategoryTypes);

    if ("true".equalsIgnoreCase((String)request.getParameter("tryEntity"))) tryEntity = true;
%>
<br>

<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditCategory?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Category</a>
    <a href="<ofbiz:url>/EditCategoryRollup?showProductCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Rollup</a>
    <a href="<ofbiz:url>/EditCategoryProducts?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Products</a>
    <a href="<ofbiz:url>/EditCategoryProdCatalogs?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButtonSelected'>Catalogs</a>
    <a href="<ofbiz:url>/EditCategoryParties?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Parties</a>
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
    <td><div class="tabletext"><b>Catalog Name [ID]</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence&nbsp;&amp;&nbsp;Type</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="prodCatalogCategory" property="prodCatalogCategories">
  <%GenericValue prodCatalog = prodCatalogCategory.getRelatedOne("ProdCatalog");%>
  <%GenericValue curProdCatalogCategoryType = prodCatalogCategory.getRelatedOneCache("ProdCatalogCategoryType");%>
  <tr valign="middle">
    <td><a href='<ofbiz:url>/EditProdCatalog?prodCatalogId=<ofbiz:inputvalue entityAttr="prodCatalogCategory" field="prodCatalogId"/></ofbiz:url>' class="buttontext"><%if (prodCatalog!=null) {%><%=prodCatalog.getString("catalogName")%><%}%> [<ofbiz:inputvalue entityAttr="prodCatalogCategory" field="prodCatalogId"/>]</a></td>
    <%boolean hasntStarted = false;%>
    <%if (prodCatalogCategory.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(prodCatalogCategory.getTimestamp("fromDate"))) { hasntStarted = true; }%>
    <td><div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>><ofbiz:inputvalue entityAttr="prodCatalogCategory" field="fromDate"/></div></td>
    <td align="center">
        <FORM method=POST action='<ofbiz:url>/category_updateProductCategoryToProdCatalog</ofbiz:url>'>
            <%boolean hasExpired = false;%>
            <%if (prodCatalogCategory.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(prodCatalogCategory.getTimestamp("thruDate"))) { hasExpired = true; }%>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="prodCatalogId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="productCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="thruDate" fullattrs="true"/> style='font-size: x-small; <%if (hasExpired) {%>color: red;<%}%>'>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="sequenceNum" fullattrs="true"/> style='font-size: x-small;'>
            <select name='prodCatalogCategoryTypeId' size=1 style='font-size: x-small;'>
                <%if (prodCatalogCategory.get("prodCatalogCategoryTypeId") != null) {%>
                  <option value='<%=prodCatalogCategory.getString("prodCatalogCategoryTypeId")%>'><%if (curProdCatalogCategoryType != null) {%><%=UtilFormatOut.checkNull(curProdCatalogCategoryType.getString("description"))%><%} else {%> [<%=prodCatalogCategory.getString("prodCatalogCategoryTypeId")%>]<%}%></option>
                  <option value='<%=prodCatalogCategory.getString("prodCatalogCategoryTypeId")%>'>----</option>
                <%} else {%>
                  <option value=''>&nbsp;</option>
                <%}%>
                <ofbiz:iterator name="prodCatalogCategoryType" property="prodCatalogCategoryTypes">
                  <option value='<%=prodCatalogCategoryType.getString("prodCatalogCategoryTypeId")%>'><%=prodCatalogCategoryType.getString("description")%><%-- [<%=prodCatalogCategoryType.getString("prodCatalogCategoryTypeId")%>]--%></option>
                </ofbiz:iterator>
            </select>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/category_removeProductCategoryFromProdCatalog?prodCatalogId=<ofbiz:entityfield attribute="prodCatalogCategory" field="prodCatalogId"/>&productCategoryId=<ofbiz:entityfield attribute="prodCatalogCategory" field="productCategoryId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(prodCatalogCategory.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/category_addProductCategoryToProdCatalog</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="productCategoryId" value="<%=productCategoryId%>">
  <input type="hidden" name="tryEntity" value="true">

  <div class='head2'>Add Catalog Product Category (select Category and Type, then enter optional From Date):</div>
  <br>
  <select name="prodCatalogId">
  <ofbiz:iterator name="prodCatalog" property="prodCatalogs">
    <option value='<ofbiz:entityfield attribute="prodCatalog" field="prodCatalogId"/>'><ofbiz:entityfield attribute="prodCatalog" field="catalogName"/> [<ofbiz:entityfield attribute="prodCatalog" field="prodCatalogId"/>]</option>
  </ofbiz:iterator>
  </select>
    <select name='prodCatalogCategoryTypeId' size=1>
        <%-- <option value=''>&nbsp;</option> --%>
        <ofbiz:iterator name="prodCatalogCategoryType" property="prodCatalogCategoryTypes">
          <option value='<%=prodCatalogCategoryType.getString("prodCatalogCategoryTypeId")%>'><%=prodCatalogCategoryType.getString("description")%><%-- [<%=prodCatalogCategoryType.getString("prodCatalogCategoryTypeId")%>]--%></option>
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
