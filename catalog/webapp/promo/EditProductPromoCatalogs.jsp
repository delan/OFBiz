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

    String productPromoId = request.getParameter("productPromoId");
    GenericValue productPromo = delegator.findByPrimaryKey("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId));
    Collection prodCatalogPromoAppls = null;
    if (productPromo == null) {
        tryEntity = false;
    } else {
        prodCatalogPromoAppls = productPromo.getRelated("ProdCatalogPromoAppl", null, UtilMisc.toList("sequenceNum", "productPromoId"));
        if (prodCatalogPromoAppls != null) pageContext.setAttribute("prodCatalogPromoAppls", prodCatalogPromoAppls);
    }

    Collection prodCatalogs = delegator.findAll("ProdCatalog", UtilMisc.toList("catalogName"));
    if (prodCatalogs != null) pageContext.setAttribute("prodCatalogs", prodCatalogs);

    if ("true".equalsIgnoreCase((String)request.getParameter("tryEntity"))) tryEntity = true;
%>

<br>
<%if(productPromoId != null && productPromoId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProductPromo?productPromoId=<%=productPromoId%></ofbiz:url>" class="tabButton">Promo</a>
  <a href="<ofbiz:url>/EditProductPromoRules?productPromoId=<%=productPromoId%></ofbiz:url>" class="tabButton">Rules</a>
  <a href="<ofbiz:url>/EditProductPromoCatalogs?productPromoId=<%=productPromoId%></ofbiz:url>" class="tabButtonSelected">Catalogs</a>
  </div>
<%}%>

<div class="head1">Catalogs <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(productPromo==null?null:productPromo.getString("promoName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productPromoId)%>]</span></div>
<a href="<ofbiz:url>/EditProductPromo</ofbiz:url>" class="buttontext">[New ProductPromo]</a>

<br>
<br>
<%if(productPromoId!=null && productPromo!=null){%>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Catalog Name [ID]</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="prodCatalogPromoAppl" property="prodCatalogPromoAppls">
  <%GenericValue prodCatalog = prodCatalogPromoAppl.getRelatedOne("ProdCatalog");%>
  <tr valign="middle">
    <td><a href='<ofbiz:url>/EditProdCatalog?prodCatalogId=<ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="prodCatalogId"/></ofbiz:url>' class="buttontext"><%if (productPromo!=null) {%><%=prodCatalog.getString("catalogName")%><%}%> [<ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="prodCatalogId"/>]</a></td>
    <%boolean hasntStarted = false;%>
    <%if (prodCatalogPromoAppl.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(prodCatalogPromoAppl.getTimestamp("fromDate"))) { hasntStarted = true; }%>
    <td><div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>><ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="fromDate"/></div></td>
    <td align="center">
        <%boolean hasExpired = false;%>
        <%if (prodCatalogPromoAppl.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(prodCatalogPromoAppl.getTimestamp("thruDate"))) { hasExpired = true; }%>
        <FORM method=POST action='<ofbiz:url>/promo_updateProductPromoToProdCatalog</ofbiz:url>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="prodCatalogId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="productPromoId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="thruDate" fullattrs="true"/> style='font-size: x-small; <%if (hasExpired) {%>color: red;<%}%>'>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="prodCatalogPromoAppl" field="sequenceNum" fullattrs="true"/> style='font-size: x-small;'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/promo_removeProductPromoFromProdCatalog?prodCatalogId=<ofbiz:entityfield attribute="prodCatalogPromoAppl" field="prodCatalogId"/>&productPromoId=<ofbiz:entityfield attribute="prodCatalogPromoAppl" field="productPromoId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(prodCatalogPromoAppl.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/promo_addProductPromoToProdCatalog</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="productPromoId" value="<%=productPromoId%>">
  <input type="hidden" name="tryEntity" value="true">

  <div class='head2'>Add Catalog Promo (select Catalog, enter optional From Date):</div>
  <br>
  <select name="prodCatalogId">
  <ofbiz:iterator name="prodCatalog" property="prodCatalogs">
    <option value='<ofbiz:entityfield attribute="prodCatalog" field="prodCatalogId"/>'><ofbiz:entityfield attribute="prodCatalog" field="catalogName"/> [<ofbiz:entityfield attribute="prodCatalog" field="prodCatalogId"/>]</option>
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
