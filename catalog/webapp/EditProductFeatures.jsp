<%
    /**
     *  Title: Edit Product Features Page
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
     *@created    April 4, 2002
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
    String productId = request.getParameter("productId");
    Collection productFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", 
            UtilMisc.toMap("productId", productId), 
            UtilMisc.toList("sequenceNum", "productFeatureApplTypeId", "productFeatureTypeId", "description"));
    if (productFeatureAndAppls != null) pageContext.setAttribute("productFeatureAndAppls", productFeatureAndAppls);

    Collection productFeatureCategories = delegator.findAll("ProductFeatureCategory", UtilMisc.toList("description"));
    if (productFeatureCategories != null) pageContext.setAttribute("productFeatureCategories", productFeatureCategories);
    Collection productFeatureApplTypes = delegator.findAll("ProductFeatureApplType", UtilMisc.toList("description"));
    if (productFeatureApplTypes != null) pageContext.setAttribute("productFeatureApplTypes", productFeatureApplTypes);
%>
<br>

<div class="head1">Edit Features for Product with ID "<%=UtilFormatOut.checkNull(productId)%>"</div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[Create New Product]</a>
<%if (productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[View Product Page]</a>
  <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Product]</a>
  <a href="<ofbiz:url>/EditProductCategories?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Category Members]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Keywords]</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Associations]</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Attributes]</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="buttontextdisabled">[Edit Features]</a>
<%}%>
<br>
<br>
<%if (productId != null){%>
<p class="head2">Product Feature Application Maintenance</p>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>Feature&nbsp;Type</b></div></td>
    <td><div class="tabletext"><b>Feature&nbsp;Category</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td><div class="tabletext"><b>Thru&nbsp;Date</b></div></td>
    <td><div class="tabletext"><b>Sequence</b></div></td>
    <td><div class="tabletext"><b>Application&nbsp;Type</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="productFeatureAndAppl" property="productFeatureAndAppls">
  <%GenericValue curProductFeatureCategory = delegator.findByPrimaryKey("ProductFeatureCategory", UtilMisc.toMap("productFeatureCategoryId", productFeatureAndAppl.get("productFeatureCategoryId")));%>
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
    <td><div class='tabletext'><ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="fromDate"/></div></td>
    <td><input type=text size='18' <ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="thruDate" fullattrs="true"/>></td>
    <td><input type=text size='5' <ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="sequenceNum" fullattrs="true"/>></td>
    <td>
      <select name='productFeatureApplTypeId' size=1>
        <%if (productFeatureAndAppl.get("productFeatureApplTypeId") != null) {%>
          <option value='<%=productFeatureAndAppl.getString("productFeatureApplTypeId")%>'> [<%=productFeatureAndAppl.getString("productFeatureApplTypeId")%>]</option>
          <option value='<%=productFeatureAndAppl.getString("productFeatureApplTypeId")%>'></option>
        <%}%>
        <ofbiz:iterator name="productFeatureApplType" property="productFeatureApplTypes">
          <option value='<%=productFeatureApplType.getString("productFeatureApplTypeId")%>'><%=productFeatureApplType.getString("description")%> [<%=productFeatureApplType.getString("productFeatureApplTypeId")%>]</option>
        </ofbiz:iterator>
      </select>
    </td>
    <td><INPUT type=submit value='Update'></td>
    </FORM>
    <td>
      <a href='<ofbiz:url>/RemoveFeatureFromProduct?productId=<ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="productId"/>&productFeatureId=<ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="productFeatureId"/>&fromDate=<ofbiz:inputvalue entityAttr="productFeatureAndAppl" field="fromDate"/></ofbiz:url>' class="buttontext">
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
  <select name='productFeatureCategoryId' size=1>
    <ofbiz:iterator name="productFeatureCategory" property="productFeatureCategories">
      <option value='<%=productFeatureCategory.getString("productFeatureCategoryId")%>'><%=productFeatureCategory.getString("description")%> [<%=productFeatureCategory.getString("productFeatureCategoryId")%>]</option>
    </ofbiz:iterator>
  </select>
  <input type="submit" value="Add">
</form>
<%}%>
<br>
<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[Create New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[View Product Page]</a>
  <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Product]</a>
  <a href="<ofbiz:url>/EditProductCategories?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Category Members]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Keywords]</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Associations]</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Attributes]</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="buttontextdisabled">[Edit Features]</a>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
