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

    String prodCatalogId = request.getParameter("prodCatalogId");
    GenericValue prodCatalog = delegator.findByPrimaryKey("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
    Collection webSiteCatalogs = null;
    if (prodCatalog == null) {
        tryEntity = false;
    } else {
        webSiteCatalogs = prodCatalog.getRelated("WebSiteCatalog", null, UtilMisc.toList("sequenceNum", "webSiteId"));
        if (webSiteCatalogs != null) pageContext.setAttribute("webSiteCatalogs", webSiteCatalogs);
    }

    Collection webSites = delegator.findAll("WebSite", UtilMisc.toList("siteName"));
    if (webSites != null) pageContext.setAttribute("webSites", webSites);

    if ("true".equalsIgnoreCase((String)request.getParameter("tryEntity"))) tryEntity = true;
%>
<br>

<%if(prodCatalogId != null && prodCatalogId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProdCatalog?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Catalog</a>
  <a href="<ofbiz:url>/EditProdCatalogWebSites?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButtonSelected">WebSites</a>
  <a href="<ofbiz:url>/EditProdCatalogCategories?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProdCatalogPromos?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Promotions</a>
  </div>
<%}%>

<div class="head1">Web Sites <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(prodCatalog==null?null:prodCatalog.getString("catalogName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(prodCatalogId)%>]</span></div>

<a href="<ofbiz:url>/EditProdCatalog</ofbiz:url>" class="buttontext">[New ProdCatalog]</a>
<br>
<br>
<%if(prodCatalogId!=null && prodCatalog!=null){%>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>WebSite ID</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="webSiteCatalog" property="webSiteCatalogs">
  <%GenericValue webSite = webSiteCatalog.getRelatedOne("WebSite");%>
  <tr valign="middle">
    <td><%--<a href='<ofbiz:url>/EditWebSite?webSiteId=<ofbiz:inputvalue entityAttr="webSiteCatalog" field="webSiteId"/></ofbiz:url>' class="buttontext">--%><div class='tabletext'><ofbiz:inputvalue entityAttr="webSiteCatalog" field="webSiteId"/></div><%--</a>--%></td>
    <td><%if (webSite!=null) {%><%--<a href='<ofbiz:url>/EditWebSite?webSiteId=<ofbiz:inputvalue entityAttr="webSiteCatalog" field="webSiteId"/></ofbiz:url>' class="buttontext">--%><div class='tabletext'><%=webSite.getString("siteName")%></div><%--</a>&nbsp;--%><%}%></td>
    <%boolean hasntStarted = false;%>
    <%if (webSiteCatalog.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(webSiteCatalog.getTimestamp("fromDate"))) { hasntStarted = true; }%>
    <td><div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>><ofbiz:inputvalue entityAttr="webSiteCatalog" field="fromDate"/></div></td>
    <td align="center">
        <%boolean hasExpired = false;%>
        <%if (webSiteCatalog.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(webSiteCatalog.getTimestamp("thruDate"))) { hasExpired = true; }%>
        <FORM method=POST action='<ofbiz:url>/updateProdCatalogToWebSite</ofbiz:url>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="webSiteCatalog" field="prodCatalogId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="webSiteCatalog" field="webSiteId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="webSiteCatalog" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="webSiteCatalog" field="thruDate" fullattrs="true"/> style='font-size: x-small; <%if (hasExpired) {%>color: red;<%}%>'>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="webSiteCatalog" field="sequenceNum" fullattrs="true"/> style='font-size: x-small;'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/removeProdCatalogFromWebSite?prodCatalogId=<ofbiz:entityfield attribute="webSiteCatalog" field="prodCatalogId"/>&webSiteId=<ofbiz:entityfield attribute="webSiteCatalog" field="webSiteId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(webSiteCatalog.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/addProdCatalogToWebSite</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="prodCatalogId" value="<%=prodCatalogId%>">
  <input type="hidden" name="tryEntity" value="true">

  <div class='head2'>Add Catalog WebSite (select WebSite, enter optional From Date):</div>
  <br>
  <select name="webSiteId">
  <ofbiz:iterator name="webSite" property="webSites">
    <option value='<ofbiz:entityfield attribute="webSite" field="webSiteId"/>'><ofbiz:entityfield attribute="webSite" field="siteName"/> [<ofbiz:entityfield attribute="webSite" field="webSiteId"/>]</option>
  </ofbiz:iterator>
  </select>
  <input type=text size='20' name='fromDate'>
  <input type="submit" value="Add">
</form>
<br>
<form method="POST" action="<ofbiz:url>/catalog_createWebSite</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="prodCatalogId" value="<%=prodCatalogId%>">
  <input type="hidden" name="tryEntity" value="true">
  <div class='head2'>Add New WebSite:</div>
  <br>
  ID: <input type=text size='20' name='webSiteId'>
  Name: <input type=text size='30' name='siteName'>
  <input type="submit" value="Add">
</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
