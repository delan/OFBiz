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
 *@created    Sep 10 2001
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    boolean useValues = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

    String productId = request.getParameter("productId");
    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
    if (product == null) useValues = false;
    Collection productCategoryMembers = product.getRelated("ProductCategoryMember", null, UtilMisc.toList("sequenceNum", "productCategoryId"));
    if (productCategoryMembers != null) pageContext.setAttribute("productCategoryMembers", productCategoryMembers);

    Collection categoryCol = delegator.findAll("ProductCategory", UtilMisc.toList("description"));
    if (categoryCol != null) pageContext.setAttribute("categoryCol", categoryCol);

    if ("true".equalsIgnoreCase((String)request.getParameter("useValues"))) useValues = true;
%>

<br>
<%if(productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButton">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButton">Features</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButton">InventoryItems</a>
  <%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <a href="<ofbiz:url>/QuickAddVariants?productId=<%=productId%></ofbiz:url>" class="tabButton">Variants</a>
  <%}%>
  </div>
<%}%>

<div class="head1">Category Members <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
<%}%>
<br>
<br>

<%if(productId!=null && product!=null){%>
<script language='JavaScript'>
    function setLineThruDate(line) { eval('document.lineForm' + line + '.thruDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
</script>
<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Category ID</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence&nbsp;&amp;&nbsp;Quantity</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<%int line = 0;%>
<ofbiz:iterator name="productCategoryMember" property="productCategoryMembers">
  <%line++;%>
  <%GenericValue category = productCategoryMember.getRelatedOne("ProductCategory");%>
  <tr valign="middle">
    <td><a href='<ofbiz:url>/EditCategory?productCategoryId=<ofbiz:inputvalue entityAttr="productCategoryMember" field="productCategoryId"/></ofbiz:url>' class="buttontext"><ofbiz:inputvalue entityAttr="productCategoryMember" field="productCategoryId"/></a></td>
    <td><%if (category!=null) {%><a href='<ofbiz:url>/EditCategory?productCategoryId=<ofbiz:inputvalue entityAttr="productCategoryMember" field="productCategoryId"/></ofbiz:url>' class="buttontext"><%=category.getString("description")%></a><%}%>&nbsp;</td>
    <td>
        <%boolean hasntStarted = false;%>
        <%if (productCategoryMember.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(productCategoryMember.getTimestamp("fromDate"))) { hasntStarted = true; }%>
        <div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>>
                <ofbiz:inputvalue entityAttr="productCategoryMember" field="fromDate"/>
        </div>
    </td>
    <td align="center">
        <%boolean hasExpired = false;%>
        <%if (productCategoryMember.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(productCategoryMember.getTimestamp("thruDate"))) { hasExpired = true; }%>
        <FORM method=POST action='<ofbiz:url>/updateProductToCategory</ofbiz:url>' name='lineForm<%=line%>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryMember" field="productId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryMember" field="productCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryMember" field="fromDate" fullattrs="true"/>>
            <input type=text size='22' <ofbiz:inputvalue entityAttr="productCategoryMember" field="thruDate" fullattrs="true"/><%if (hasExpired) {%> style='color: red;'<%}%>>
            <a href='#' onclick='setLineThruDate("<%=line%>")' class='buttontext'>[Now]</a>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="productCategoryMember" field="sequenceNum" fullattrs="true"/>>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="productCategoryMember" field="quantity" fullattrs="true"/>>
            <INPUT type=submit value='Update'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/removeProductFromCategory?productId=<ofbiz:entityfield attribute="productCategoryMember" field="productId"/>&productCategoryId=<ofbiz:entityfield attribute="productCategoryMember" field="productCategoryId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(productCategoryMember.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/addProductToCategory</ofbiz:url>" style='margin: 0;' name='addProductCategoryMemberForm'>
  <input type="hidden" name="productId" value="<%=productId%>">
  <input type="hidden" name="useValues" value="true">

  <script language='JavaScript'>
      function setPcmFromDate() { document.addProductCategoryMemberForm.fromDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"; }
  </script>
  <div class='head2'>Add ProductCategoryMember (select Category, enter From Date):</div>
  <br>
  <select name="productCategoryId">
  <ofbiz:iterator name="category" property="categoryCol">
    <option value='<ofbiz:entityfield attribute="category" field="productCategoryId"/>'><ofbiz:entityfield attribute="category" field="description"/> [<ofbiz:entityfield attribute="category" field="productCategoryId"/>]</option>
  </ofbiz:iterator>
  </select>
  <a href='#' onclick='setPcmFromDate()' class='buttontext'>[Now]</a> <input type=text size='22' name='fromDate'>
  <input type="submit" value="Add">
</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
