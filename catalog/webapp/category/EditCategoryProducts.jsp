<%
/**
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
 *@author     Andy Zeneski
 *@author     David E. Jones
 *@created    March 10, 2002
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
        boolean useValues = true;
        int viewIndex = 0;
        try {
            viewIndex = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_INDEX")).intValue();
        } catch (Exception e) {
            viewIndex = 0;
        }

        int viewSize = 20;
        try {
            viewSize = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_SIZE")).intValue();
        } catch (Exception e) {
            viewSize = 20;
        }

        if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

        String productCategoryId = request.getParameter("productCategoryId");
        GenericValue productCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
        if (productCategory == null) useValues = false;

        Collection productCategoryMembers = productCategory.getRelated("ProductCategoryMember", null, UtilMisc.toList("sequenceNum", "productId"));
        if (productCategoryMembers != null)
            pageContext.setAttribute("productCategoryMembers", productCategoryMembers);

        if ("true".equalsIgnoreCase((String)request.getParameter("useValues"))) useValues = true;

        int highIndex = 0;
        int lowIndex = 0;
        int listSize = 0;
        if (productCategoryMembers != null)
            listSize = productCategoryMembers.size();

        lowIndex = viewIndex * viewSize;
        highIndex = (viewIndex + 1) * viewSize;
        if (listSize < highIndex)
            highIndex = listSize;

    Debug.logInfo("Low Index: " + lowIndex);
    Debug.logInfo("View Size: " + viewSize);
%>
<br>

<a href="<ofbiz:url>/EditCategory</ofbiz:url>" class="buttontext">[New Category]</a>
<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <a href="/ecommerce/control/category?category_id=<%=productCategoryId%>" class="buttontext" target='_blank'>[Category Page]</a>
  <a href="<ofbiz:url>/EditCategory?productCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontext">[Category]</a>
  <a href="<ofbiz:url>/EditCategoryRollup?showProductCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontext">[Rollup]</a>
  <a href="<ofbiz:url>/EditCategoryProducts?productCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontextdisabled">[Products]</a>
  <a href="<ofbiz:url>/EditCategoryProdCatalogs?productCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontext">[Catalogs]</a>
<%}%>

<div class="head1">Edit Category Members for Category
  <%=UtilFormatOut.ifNotEmpty(productCategory==null?null:productCategory.getString("description"),"\"","\"")%>
  with ID "<%=UtilFormatOut.checkNull(productCategoryId)%>"</div>

<br>
<br>
<%-- Edit 'ProductCategoryMember's --%>
<%if(productCategoryId!=null && productCategory!=null){%>
<p class="head2">Product-Category Member Maintenance</p>

<ofbiz:if name="productCategoryMembers" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex > 0){%>
          <a href="<ofbiz:url><%="/EditCategoryProducts?productCategoryId=" + productCategoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize > 0){%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize > highIndex){%>
          | <a href="<ofbiz:url><%="/EditCategoryProducts?productCategoryId=" + productCategoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Product ID</b></div></td>
    <td><div class="tabletext"><b>Product Name</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence&nbsp;&amp;&nbsp;Quantity</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="productCategoryMember" property="productCategoryMembers" offset="<%=lowIndex%>" limit="<%=viewSize%>">
  <%GenericValue product = productCategoryMember.getRelatedOne("Product");%>
  <tr valign="middle">
    <td><a href='<ofbiz:url>/EditProduct?productId=<ofbiz:inputvalue entityAttr="productCategoryMember" field="productId"/></ofbiz:url>' class="buttontext"><ofbiz:inputvalue entityAttr="productCategoryMember" field="productId"/></a></td>
    <td><%if (product!=null) {%><a href='<ofbiz:url>/EditProduct?productId=<ofbiz:inputvalue entityAttr="productCategoryMember" field="productId"/></ofbiz:url>' class="buttontext"><%=product.getString("productName")%></a><%}%>&nbsp;</td>
    <td><div class='tabletext'><ofbiz:inputvalue entityAttr="productCategoryMember" field="fromDate"/></div></td>
    <td align="center">
        <FORM method=POST action='<ofbiz:url>/updateCategoryProductMember?VIEW_SIZE=<%=viewSize%>&VIEW_INDEX=<%=viewIndex%></ofbiz:url>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryMember" field="productId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryMember" field="productCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryMember" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="productCategoryMember" field="thruDate" fullattrs="true"/>>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="productCategoryMember" field="sequenceNum" fullattrs="true"/>>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="productCategoryMember" field="quantity" fullattrs="true"/>>
            <INPUT type=submit value='Update'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/removeCategoryProductMember?VIEW_SIZE=<%=viewSize%>&VIEW_INDEX=<%=viewIndex%>&productId=<ofbiz:entityfield attribute="productCategoryMember" field="productId"/>&productCategoryId=<ofbiz:entityfield attribute="productCategoryMember" field="productCategoryId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(productCategoryMember.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>

<ofbiz:if name="productCategoryMembers" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex > 0){%>
          <a href="<ofbiz:url><%="/EditCategoryProducts?productCategoryId=" + productCategoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize > 0){%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize > highIndex){%>
          | <a href="<ofbiz:url><%="/EditCategoryProducts?productCategoryId=" + productCategoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
