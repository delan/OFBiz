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
 *@created    May 13 2002
 *@version    1.0
--%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String productPromoId = request.getParameter("productPromoId");
    GenericValue productPromo = delegator.findByPrimaryKey("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId));
    if(productPromo == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("productPromo", productPromo);
    }
%>

<br>
<%if(productPromoId != null && productPromoId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProductPromo?productPromoId=<%=productPromoId%></ofbiz:url>" class="tabButtonSelected">Promo</a>
  <a href="<ofbiz:url>/EditProductPromoRules?productPromoId=<%=productPromoId%></ofbiz:url>" class="tabButton">Rules</a>
  <a href="<ofbiz:url>/EditProductPromoCatalogs?productPromoId=<%=productPromoId%></ofbiz:url>" class="tabButton">Catalogs</a>
  </div>
<%}%>
<div class="head1">Promotion <span class='head2'><%=UtilFormatOut.ifNotEmpty(productPromo==null?null:productPromo.getString("promoName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productPromoId)%>]</span></div>
<a href="<ofbiz:url>/EditProductPromo</ofbiz:url>" class="buttontext">[New ProductPromo]</a>
<%if (productPromo == null) {%>
  <%if (productPromoId != null) {%>
    <form action="<ofbiz:url>/CreateProductPromo</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">Product Promo ID</div></td>
      <td>&nbsp;</td>
      <td>
        <h3>Could not find productPromo with ID "<%=productPromoId%>".</h3><br>
        <input type=text size='20' maxlength='20' name="productPromoId" value="<%=UtilFormatOut.checkNull(productPromoId)%>">
      </td>
    </tr>
  <%} else {%>
    <form action="<ofbiz:url>/CreateProductPromo</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">Product Promo ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type=text size='20' maxlength='20' name="productPromoId" value="<%=UtilFormatOut.checkNull(productPromoId)%>">
      </td>
    </tr>
  <%}%>
<%} else {%>
  <form action="<ofbiz:url>/UpdateProductPromo</ofbiz:url>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="productPromoId" value="<%=productPromoId%>">
  <tr>
    <td align=right><div class="tabletext">Product Promo ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=productPromoId%></b> (This cannot be changed without re-creating the productPromo.)
    </td>
  </tr>
<%}%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Promo Name</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="productPromo" field="promoName" fullattrs="true"/> size="30" maxlength="60"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Promo Text</div></td>
        <td>&nbsp;</td>
        <td width="74%"><textarea name='promoText' cols='70' rows='5'><ofbiz:inputvalue entityAttr="productPromo" field="promoText"/></textarea></td>
      </tr>

      <tr>
        <td width="26%" align=right><div class="tabletext">Single Use?</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <SELECT name='singleUse'>
            <OPTION><ofbiz:inputvalue entityAttr='productPromo' field='singleUse' default="N"/></OPTION>
            <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
          </SELECT>
        </td>
      </tr>
  <tr>
    <td colspan='2'>&nbsp;</td>
    <td colspan='1' align=left><input type="submit" name="Update" value="Update"></td>
  </tr>
</table>
</form>

<%} else {%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
