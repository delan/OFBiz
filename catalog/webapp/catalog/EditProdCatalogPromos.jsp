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
    Collection prodCatalogPromoAppls = null;
    if (prodCatalog == null) {
        tryEntity = false;
    } else {
        prodCatalogPromoAppls = prodCatalog.getRelated("ProdCatalogPromoAppl", null, UtilMisc.toList("sequenceNum", "productPromoId"));
        if (prodCatalogPromoAppls != null) pageContext.setAttribute("prodCatalogPromoAppls", prodCatalogPromoAppls);
    }

    Collection productPromos = delegator.findAll("ProductPromo", UtilMisc.toList("promoName"));
    if (productPromos != null) pageContext.setAttribute("productPromos", productPromos);

    if ("true".equalsIgnoreCase((String)request.getParameter("tryEntity"))) tryEntity = true;
%>
<br>

<%if(prodCatalogId != null && prodCatalogId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProdCatalog?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Catalog</a>
  <a href="<ofbiz:url>/EditProdCatalogWebSites?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">WebSites</a>
  <a href="<ofbiz:url>/EditProdCatalogParties?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Parties</a>
  <a href="<ofbiz:url>/EditProdCatalogCategories?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProdCatalogPromos?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButtonSelected">Promotions</a>
  </div>
<%}%>

<div class="head1">Promotions <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(prodCatalog==null?null:prodCatalog.getString("catalogName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(prodCatalogId)%>]</span></div>

<a href="<ofbiz:url>/EditProdCatalog</ofbiz:url>" class="buttontext">[New ProdCatalog]</a>
<br>
<br>
<%if(prodCatalogId!=null && prodCatalog!=null){%>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Promo&nbsp;Name&nbsp;[ID]</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="prodCatalogPromoAppl" property="prodCatalogPromoAppls">
  <%GenericValue productPromo = prodCatalogPromoAppl.getRelatedOne("ProductPromo");%>
  <tr valign="middle">
    <td><a href='<ofbiz:url>/EditProductPromo?productPromoId=<ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="productPromoId"/></ofbiz:url>' class="buttontext"><%if (productPromo!=null) {%><%=productPromo.getString("promoName")%><%}%> [<ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="productPromoId"/>]</a></td>
    <%boolean hasntStarted = false;%>
    <%if (prodCatalogPromoAppl.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(prodCatalogPromoAppl.getTimestamp("fromDate"))) { hasntStarted = true; }%>
    <td><div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>><ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="fromDate"/></div></td>
    <td align="center">
        <%boolean hasExpired = false;%>
        <%if (prodCatalogPromoAppl.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(prodCatalogPromoAppl.getTimestamp("thruDate"))) { hasExpired = true; }%>
        <FORM method=POST action='<ofbiz:url>/updateProductPromoToProdCatalog</ofbiz:url>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="prodCatalogId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="productPromoId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="thruDate" fullattrs="true"/> style='font-size: x-small; <%if (hasExpired) {%>color: red;<%}%>'>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="sequenceNum" fullattrs="true"/> style='font-size: x-small;'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/removeProductPromoFromProdCatalog?prodCatalogId=<ofbiz:entityfield attribute="prodCatalogPromoAppl" field="prodCatalogId"/>&productPromoId=<ofbiz:entityfield attribute="prodCatalogPromoAppl" field="productPromoId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(prodCatalogPromoAppl.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/addProductPromoToProdCatalog</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="prodCatalogId" value="<%=prodCatalogId%>">
  <input type="hidden" name="tryEntity" value="true">

  <div class='head2'>Add Catalog Promo (select Promo, enter optional From Date):</div>
  <br>
  <select name="productPromoId">
  <ofbiz:iterator name="productPromo" property="productPromos">
    <option value='<ofbiz:entityfield attribute="productPromo" field="productPromoId"/>'><ofbiz:entityfield attribute="productPromo" field="promoName"/> [<ofbiz:entityfield attribute="productPromo" field="productPromoId"/>]</option>
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
