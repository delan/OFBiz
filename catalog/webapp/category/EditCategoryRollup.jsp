<%
/**
 *  Title: Edit Category Page
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
 *@created    Sep 10 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<table cellpadding=0 cellspacing=0 border=0 width="100%"><tr><td>&nbsp;&nbsp;</td><td>
<%if(security.hasEntityPermission("CATALOG", "_VIEW", request.getSession())) {%>
<%
    boolean useValues = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

    String productCategoryId = request.getParameter("showProductCategoryId");
    GenericValue productCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
    Collection parentProductCategoryRollups = null;
    Collection currentProductCategoryRollups = null;
    if(productCategory == null) {
        useValues = false;
    } else {
        parentProductCategoryRollups = productCategory.getRelated("ParentProductCategoryRollup");
        if (parentProductCategoryRollups != null) pageContext.setAttribute("parentProductCategoryRollups", parentProductCategoryRollups);

        currentProductCategoryRollups = productCategory.getRelated("CurrentProductCategoryRollup");
        if (currentProductCategoryRollups != null) pageContext.setAttribute("currentProductCategoryRollups", currentProductCategoryRollups);
    }

    Collection productCategoryCol = delegator.findAll("ProductCategory", null);


%>
<br>
<a href="<ofbiz:url>/EditCategory</ofbiz:url>" class="buttontext">[New Category]</a>
<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <a href="/ecommerce/control/category?category_id=<%=productCategoryId%>" class="buttontext" target='_blank'>[Category Page]</a>
  <a href="<ofbiz:url>/EditCategory?productCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontext">[Category]</a>
  <a href="<ofbiz:url>/EditCategoryRollup?showProductCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontextdisabled">[Rollup]</a>
  <a href="<ofbiz:url>/EditCategoryProducts?productCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontext">[Products]</a>
<%}%>
<div class="head1">Edit Product Category with ID "<%=UtilFormatOut.checkNull(productCategoryId)%>"</div>

<%-- Edit 'ProductCategoryRollup's --%>
<%if (productCategory!=null){%>
<br>
<p class="head2">Category Rollup: Parent Categories</p>

<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Parent Category ID</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:if name="currentProductCategoryRollups" size="0">
  <ofbiz:iterator name="productCategoryRollup" property="currentProductCategoryRollups">
    <%GenericValue curCategory = productCategoryRollup.getRelatedOne("ParentProductCategory");%>
    <tr valign="middle">
      <td><%if (curCategory!=null){%><a href="<ofbiz:url>/EditCategory?productCategoryId=<%=curCategory.getString("productCategoryId")%></ofbiz:url>" class="buttontext"><%=curCategory.getString("productCategoryId")%></a><%}%></td>
      <td><%if (curCategory!=null){%><a href="<ofbiz:url>/EditCategory?productCategoryId=<%=curCategory.getString("productCategoryId")%></ofbiz:url>" class="buttontext"><%=curCategory.getString("description")%></a><%}%>&nbsp;</td>
      <td><div class='tabletext'><ofbiz:inputvalue entityAttr="productCategoryRollup" field="fromDate"/></div></td>
      <td align="center">
        <FORM method=POST action='<ofbiz:url>/updateProductCategoryToCategory</ofbiz:url>'>
            <input type=hidden name='showProductCategoryId' value='<ofbiz:inputvalue entityAttr="productCategoryRollup" field="productCategoryId"/>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRollup" field="productCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRollup" field="parentProductCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRollup" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="productCategoryRollup" field="thruDate" fullattrs="true"/>>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="productCategoryRollup" field="sequenceNum" fullattrs="true"/>>
            <INPUT type=submit value='Update'>
        </FORM>
      </td>
      <td>
        <a href="<ofbiz:url>/removeProductCategoryFromCategory?showProductCategoryId=<%=productCategoryId%>&productCategoryId=<%=productCategoryRollup.getString("productCategoryId")%>&parentProductCategoryId=<%=productCategoryRollup.getString("parentProductCategoryId")%>&fromDate=<%=UtilFormatOut.encodeQueryValue(productCategoryRollup.getTimestamp("fromDate").toString())%></ofbiz:url>" class="buttontext">
        [Delete]</a>
      </td>
    </tr>
  </ofbiz:iterator>
</ofbiz:if>
<ofbiz:unless name="currentProductCategoryRollups" size="0">
  <tr valign="middle">
    <td colspan='5'><DIV class='tabletext'>No Parent Categories found.</DIV></td>
  </tr>
</ofbiz:unless>
</table>

<form method="POST" action="<ofbiz:url>/addProductCategoryToCategory</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="productCategoryId" value="<%=productCategoryId%>">
  <input type="hidden" name="showProductCategoryId" value="<%=productCategoryId%>">
  <div class='tabletext'>Add <b>Parent</b> Category (select Category and enter optional From Date):</div>
    <select name="parentProductCategoryId">
    <%Iterator pit = UtilMisc.toIterator(productCategoryCol);%>
    <%while(pit != null && pit.hasNext()) {%>
      <%GenericValue curCategory = (GenericValue)pit.next();%>
        <%if(!productCategoryId.equals(curCategory.getString("productCategoryId"))){%>
          <option value="<%=curCategory.getString("productCategoryId")%>"><%=curCategory.getString("description")%> [<%=curCategory.getString("productCategoryId")%>]</option>
        <%}%>
    <%}%>
    </select>
  <input type=text size='20' name='fromDate'>
  <input type="submit" value="Add">
</form>
<br>
<hr>
<br>
<p class="head2">Category Rollup: Child Categories</p>

<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Child Category ID</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:if name="parentProductCategoryRollups" size="0">
  <ofbiz:iterator name="productCategoryRollup" property="parentProductCategoryRollups">
    <%GenericValue curCategory = productCategoryRollup.getRelatedOne("CurrentProductCategory");%>
    <tr valign="middle">
      <td><a href="<ofbiz:url>/EditCategory?productCategoryId=<%=curCategory.getString("productCategoryId")%></ofbiz:url>" class="buttontext"><%=curCategory.getString("productCategoryId")%></a></td>
      <td><%if(curCategory!=null){%><a href="<ofbiz:url>/EditCategory?productCategoryId=<%=curCategory.getString("productCategoryId")%></ofbiz:url>" class="buttontext"><%=curCategory.getString("description")%></a><%}%>&nbsp;</td>
      <td><div class='tabletext'><ofbiz:inputvalue entityAttr="productCategoryRollup" field="fromDate"/></div></td>
      <td align="center">
        <FORM method=POST action='<ofbiz:url>/updateProductCategoryToCategory</ofbiz:url>'>
            <input type=hidden name='showProductCategoryId' value='<ofbiz:inputvalue entityAttr="productCategoryRollup" field="productCategoryId"/>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRollup" field="productCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRollup" field="parentProductCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productCategoryRollup" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="productCategoryRollup" field="thruDate" fullattrs="true"/>>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="productCategoryRollup" field="sequenceNum" fullattrs="true"/>>
            <INPUT type=submit value='Update'>
        </FORM>
      </td>
      <td>
        <a href="<ofbiz:url>/removeProductCategoryFromCategory?showProductCategoryId=<%=productCategoryId%>&productCategoryId=<%=productCategoryRollup.getString("productCategoryId")%>&parentProductCategoryId=<%=productCategoryRollup.getString("parentProductCategoryId")%>&fromDate=<%=UtilFormatOut.encodeQueryValue(productCategoryRollup.getTimestamp("fromDate").toString())%></ofbiz:url>" class="buttontext">
        [Delete]</a>
      </td>
    </tr>
  </ofbiz:iterator>
</ofbiz:if>
<ofbiz:unless name="parentProductCategoryRollups" size="0">
  <tr valign="middle">
    <td colspan='5'><DIV class='tabletext'>No Child Categories found.</DIV></td>
  </tr>
</ofbiz:unless>
</table>

<form method="POST" action="<ofbiz:url>/addProductCategoryToCategory</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="showProductCategoryId" value="<%=productCategoryId%>">
  <input type="hidden" name="parentProductCategoryId" value="<%=productCategoryId%>">
  <div class='tabletext'>Add <b>Child</b> Category (select Category and enter optional From Date):</div>
    <select name="productCategoryId">
    <%Iterator cit = UtilMisc.toIterator(productCategoryCol);%>
    <%while (cit != null && cit.hasNext()) {%>
      <%GenericValue curCategory = (GenericValue)cit.next();%>
      <%if (!productCategoryId.equals(curCategory.getString("productCategoryId"))){%>
        <option value="<%=curCategory.getString("productCategoryId")%>"><%=curCategory.getString("description")%> [<%=curCategory.getString("productCategoryId")%>]</option>
      <%}%>
    <%}%>
    </select>
  <input type=text size='20' name='fromDate'>
  <input type="submit" value="Add">
</form>
<%}%>

<%} else {%>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
</td><td>&nbsp;&nbsp;</td></tr></table>
