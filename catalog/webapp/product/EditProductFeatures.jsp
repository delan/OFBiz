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
 *@created    April 4, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    String productId = request.getParameter("productId");

    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));

    Collection productFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", 
            UtilMisc.toMap("productId", productId), 
            UtilMisc.toList("sequenceNum", "productFeatureApplTypeId", "productFeatureTypeId", "description"));
    if (productFeatureAndAppls != null) pageContext.setAttribute("productFeatureAndAppls", productFeatureAndAppls);

    Collection productFeatureCategories = delegator.findAll("ProductFeatureCategory", UtilMisc.toList("description"));
    if (productFeatureCategories != null) pageContext.setAttribute("productFeatureCategories", productFeatureCategories);
    Collection productFeatureApplTypes = delegator.findAll("ProductFeatureApplType", UtilMisc.toList("description"));
    if (productFeatureApplTypes != null) pageContext.setAttribute("productFeatureApplTypes", productFeatureApplTypes);

    Collection productFeatureTypes = delegator.findAll("ProductFeatureType", UtilMisc.toList("description"));
    if (productFeatureTypes != null) pageContext.setAttribute("productFeatureTypes", productFeatureTypes);
%>

<br>
<%if (productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButton">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductContent?productId=<%=productId%></ofbiz:url>" class="tabButton">Content</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">Features</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButton">Inventory</a>
  <%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <a href="<ofbiz:url>/QuickAddVariants?productId=<%=productId%></ofbiz:url>" class="tabButton">Variants</a>
  <%}%>
  </div>
<%}%>

<div class="head1">Features <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
<%}%>
<br>
<br>

<%if (productId != null){%>

<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>Type</b></div></td>
    <td><div class="tabletext"><b>Category</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td><div class="tabletext"><b>Thru&nbsp;Date, Sequence, Application&nbsp;Type</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="productFeatureAndAppl" property="productFeatureAndAppls">
  <%GenericValue curProductFeatureApplType = productFeatureAndAppl.getRelatedOneCache("ProductFeatureApplType");%>
  <%GenericValue curProductFeatureCategory = productFeatureAndAppl.getRelatedOneCache("ProductFeatureCategory");%>
  <%if (curProductFeatureCategory != null) pageContext.setAttribute("curProductFeatureCategory", curProductFeatureCategory);%>
  <tr valign="middle">
    <FORM method=POST action='<ofbiz:url>/UpdateFeatureToProductApplication</ofbiz:url>'>
        <input type=hidden <ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="productId" fullattrs="true"/>>
        <input type=hidden <ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="productFeatureId" fullattrs="true"/>>
        <input type=hidden <ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="fromDate" fullattrs="true"/>>
    <td><div class='tabletext'><ofbiz:entityfield attribute="productFeatureAndAppl" field="description"/></div></td>
    <td><div class='tabletext'><ofbiz:entityfield attribute="productFeatureAndAppl" field="productFeatureTypeId"/></div></td>
    <td><a href='<ofbiz:url>/EditFeatureCategoryFeatures?productFeatureCategoryId=<ofbiz:entityfield attribute="productFeatureAndAppl" field="productFeatureCategoryId"/>&productId=<ofbiz:entityfield attribute="productFeatureAndAppl" field="productId"/></ofbiz:url>' class='buttontext'>
        <ofbiz:entityfield attribute="curProductFeatureCategory" field="description"/>
        [<ofbiz:entityfield attribute="productFeatureAndAppl" field="productFeatureCategoryId"/>]</a></td>
    <%boolean hasntStarted = false;%>
    <%if (productFeatureAndAppl.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(productFeatureAndAppl.getTimestamp("fromDate"))) { hasntStarted = true; }%>
    <td><div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>><ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="fromDate"/></div></td>
    <td>
        <%boolean hasExpired = false;%>
        <%if (productFeatureAndAppl.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(productFeatureAndAppl.getTimestamp("thruDate"))) { hasExpired = true; }%>
        <input type=text size='22' <ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="thruDate" fullattrs="true"/> style='font-size: x-small; <%if (hasExpired) {%>color: red;<%}%>'>
        <input type=text size='5' <ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="sequenceNum" fullattrs="true"/> style='font-size: x-small;'>
      <select name='productFeatureApplTypeId' size=1 style='font-size: x-small;'>
        <%if (productFeatureAndAppl.get("productFeatureApplTypeId") != null) {%>
          <option value='<%=productFeatureAndAppl.getString("productFeatureApplTypeId")%>'><%if (curProductFeatureApplType != null) {%><%=UtilFormatOut.checkNull(curProductFeatureApplType.getString("description"))%><%} else {%> [<%=productFeatureAndAppl.getString("productFeatureApplTypeId")%>]<%}%></option>
          <option value='<%=productFeatureAndAppl.getString("productFeatureApplTypeId")%>'>---</option>
        <%}%>
        <ofbiz:iterator name="productFeatureApplType" property="productFeatureApplTypes">
          <option value='<%=productFeatureApplType.getString("productFeatureApplTypeId")%>'><%=productFeatureApplType.getString("description")%> <%--[<%=productFeatureApplType.getString("productFeatureApplTypeId")%>]--%></option>
        </ofbiz:iterator>
      </select>
        <INPUT type=submit value='Update' style='font-size: x-small;'>
    </td>
    </FORM>
    <td>
      <a href='<ofbiz:url>/RemoveFeatureFromProduct?productId=<ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="productId"/>&productFeatureId=<ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="productFeatureId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(productFeatureAndAppl.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/EditFeatureCategoryFeatures</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="productId" value="<%=productId%>">
  <div class='head2'>Add ProductFeature from Category:</div>
  <br>
  <select name='productFeatureCategoryId' size=1 style='font-size: x-small;'>
    <ofbiz:iterator name="productFeatureCategory" property="productFeatureCategories">
      <option value='<%=productFeatureCategory.getString("productFeatureCategoryId")%>'><%=productFeatureCategory.getString("description")%> [<%=productFeatureCategory.getString("productFeatureCategoryId")%>]</option>
    </ofbiz:iterator>
  </select>
  <input type="submit" value="Add" style='font-size: x-small;'>
</form>
<br>
<form method="POST" action="<ofbiz:url>/ApplyFeatureToProductFromTypeAndCode</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="productId" value="<%=productId%>">
  <div class='head2'>Add ProductFeature with Type and ID Code:</div>
  <br>
  <span class='tabletext'>Feature Type: </span><select name='productFeatureTypeId' size=1 style='font-size: x-small;'>
    <ofbiz:iterator name="productFeatureType" property="productFeatureTypes">
      <option value='<%=productFeatureType.getString("productFeatureTypeId")%>'><%=productFeatureType.getString("description")%> <%--[<%=productFeatureType.getString("productFeatureTypeId")%>]--%></option>
    </ofbiz:iterator>
  </select>
  <span class='tabletext'>ID Code: </span><input type=text size='10' name='idCode' value='' style='font-size: x-small;'>
  <br>
  <span class='tabletext'>Feature Application Type: </span><select name='productFeatureApplTypeId' size=1 style='font-size: x-small;'>
    <ofbiz:iterator name="productFeatureApplType" property="productFeatureApplTypes">
      <option value='<%=productFeatureApplType.getString("productFeatureApplTypeId")%>'><%=productFeatureApplType.getString("description")%> <%--[<%=productFeatureApplType.getString("productFeatureApplTypeId")%>]--%></option>
    </ofbiz:iterator>
  </select>
  <br>
  <span class='tabletext'>From: </span><input type=text size='18' name='fromDate' style='font-size: x-small;'>
  <span class='tabletext'>Thru: </span><input type=text size='18' name='thruDate' style='font-size: x-small;'>
  <span class='tabletext'>Sequence: </span><input type=text size='5' name='sequenceNum' style='font-size: x-small;'>
  <input type="submit" value="Add" style='font-size: x-small;'>
</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
