<%
/**
 *  Title: Edit Product Page
 *  Description: None
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
 */
%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String prodCatalogId = request.getParameter("prodCatalogId");
    GenericValue prodCatalog = delegator.findByPrimaryKey("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
    if (prodCatalog == null) tryEntity = false;
    Collection prodCatalogCategories = prodCatalog.getRelated("ProdCatalogCategory", null, UtilMisc.toList("prodCatalogCategoryTypeId", "sequenceNum", "productCategoryId"));
    if (prodCatalogCategories != null) pageContext.setAttribute("prodCatalogCategories", prodCatalogCategories);

    Collection productCategories = delegator.findAll("ProductCategory", UtilMisc.toList("description"));
    if (productCategories != null) pageContext.setAttribute("productCategories", productCategories);

    Collection prodCatalogCategoryTypes = delegator.findAll("ProdCatalogCategoryType", UtilMisc.toList("description"));
    if (prodCatalogCategoryTypes != null) pageContext.setAttribute("prodCatalogCategoryTypes", prodCatalogCategoryTypes);

    if ("true".equalsIgnoreCase((String)request.getParameter("tryEntity"))) tryEntity = true;
%>
<br>

<a href="<ofbiz:url>/EditProdCatalog</ofbiz:url>" class="buttontext">[New ProdCatalog]</a>
<%if(prodCatalogId != null && prodCatalogId.length() > 0){%>
  <a href="<ofbiz:url>/EditProdCatalog?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="buttontext">[Catalog]</a>
  <a href="<ofbiz:url>/EditProdCatalogWebSites?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="buttontext">[WebSites]</a>
  <a href="<ofbiz:url>/EditProdCatalogCategories?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="buttontextdisabled">[Categories]</a>
  <a href="<ofbiz:url>/EditProdCatalogPromos?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="buttontext">[Promotions]</a>
<%}%>

<div class="head1">Product Categories for Product Catalog
  <%=UtilFormatOut.ifNotEmpty(prodCatalog==null?null:prodCatalog.getString("catalogName"),"\"","\"")%> 
  with ID "<%=UtilFormatOut.checkNull(prodCatalogId)%>"</div>

<br>
<br>
<%if(prodCatalogId!=null && prodCatalog!=null){%>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Category ID</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence&nbsp;&amp;&nbsp;Type</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="prodCatalogCategory" property="prodCatalogCategories">
  <%GenericValue productCategory = prodCatalogCategory.getRelatedOne("ProductCategory");%>
  <tr valign="middle">
    <td><a href='<ofbiz:url>/EditCategory?productCategoryId=<ofbiz:inputvalue entityAttr="prodCatalogCategory" field="productCategoryId"/></ofbiz:url>' class="buttontext"><ofbiz:inputvalue entityAttr="prodCatalogCategory" field="productCategoryId"/></a></td>
    <td><%if (productCategory!=null) {%><a href='<ofbiz:url>/EditCategory?productCategoryId=<ofbiz:inputvalue entityAttr="prodCatalogCategory" field="productCategoryId"/></ofbiz:url>' class="buttontext"><%=productCategory.getString("description")%></a><%}%>&nbsp;</td>
    <td><div class='tabletext'><ofbiz:inputvalue entityAttr="prodCatalogCategory" field="fromDate"/></div></td>
    <td align="center">
        <FORM method=POST action='<ofbiz:url>/updateProductCategoryToProdCatalog</ofbiz:url>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="prodCatalogId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="productCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="thruDate" fullattrs="true"/>>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="prodCatalogCategory" field="sequenceNum" fullattrs="true"/>>
            <select name='prodCatalogCategoryTypeId' size=1>
                <%if (prodCatalogCategory.get("prodCatalogCategoryTypeId") != null) {%>
                  <option value='<%=prodCatalogCategory.getString("prodCatalogCategoryTypeId")%>'> [<%=prodCatalogCategory.getString("prodCatalogCategoryTypeId")%>]</option>
                  <option value='<%=prodCatalogCategory.getString("prodCatalogCategoryTypeId")%>'>&nbsp;</option>
                <%} else {%>
                  <option value=''>&nbsp;</option>
                <%}%>
                <ofbiz:iterator name="prodCatalogCategoryType" property="prodCatalogCategoryTypes">
                  <option value='<%=prodCatalogCategoryType.getString("prodCatalogCategoryTypeId")%>'><%=prodCatalogCategoryType.getString("description")%> [<%=prodCatalogCategoryType.getString("prodCatalogCategoryTypeId")%>]</option>
                </ofbiz:iterator>
            </select>
            <INPUT type=submit value='Update'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/removeProductCategoryFromProdCatalog?prodCatalogId=<ofbiz:entityfield attribute="prodCatalogCategory" field="prodCatalogId"/>&productCategoryId=<ofbiz:entityfield attribute="prodCatalogCategory" field="productCategoryId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(prodCatalogCategory.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/addProductCategoryToProdCatalog</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="prodCatalogId" value="<%=prodCatalogId%>">
  <input type="hidden" name="tryEntity" value="true">

  <div class='head2'>Add Catalog Product Category (select Category and Type, then enter optional From Date):</div>
  <br>
  <select name="productCategoryId">
  <ofbiz:iterator name="productCategory" property="productCategories">
    <option value='<ofbiz:entityfield attribute="productCategory" field="productCategoryId"/>'><ofbiz:entityfield attribute="productCategory" field="description"/> [<ofbiz:entityfield attribute="productCategory" field="productCategoryId"/>]</option>
  </ofbiz:iterator>
  </select>
    <select name='prodCatalogCategoryTypeId' size=1>
        <option value=''>&nbsp;</option>
        <ofbiz:iterator name="prodCatalogCategoryType" property="prodCatalogCategoryTypes">
          <option value='<%=prodCatalogCategoryType.getString("prodCatalogCategoryTypeId")%>'><%=prodCatalogCategoryType.getString("description")%> [<%=prodCatalogCategoryType.getString("prodCatalogCategoryTypeId")%>]</option>
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
