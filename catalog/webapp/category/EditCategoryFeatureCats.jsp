<%--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@created    April 10 2003
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    String nowTimestampString = UtilDateTime.nowTimestamp().toString();

    boolean tryEntity = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String productCategoryId = request.getParameter("productCategoryId");
    GenericValue productCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
    if (productCategory == null) tryEntity = false;
    Collection productFeatureCategoryAppls = productCategory.getRelated("ProductFeatureCategoryAppl", null, UtilMisc.toList("productFeatureCategoryId"));
    if (productFeatureCategoryAppls != null) pageContext.setAttribute("productFeatureCategoryAppls", productFeatureCategoryAppls);

    Collection productFeatureCategories = delegator.findAll("ProductFeatureCategory", UtilMisc.toList("description"));
    if (productFeatureCategories != null) pageContext.setAttribute("productFeatureCategories", productFeatureCategories);

    if ("true".equalsIgnoreCase((String)request.getParameter("tryEntity"))) tryEntity = true;
%>

<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditCategory?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Category</a>
    <a href="<ofbiz:url>/EditCategoryRollup?showProductCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Rollup</a>
    <a href="<ofbiz:url>/EditCategoryProducts?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Products</a>
    <a href="<ofbiz:url>/EditCategoryProdCatalogs?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Catalogs</a>
    <a href="<ofbiz:url>/EditCategoryFeatureCats?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButtonSelected'>FeatureCats</a>
    <a href="<ofbiz:url>/EditCategoryParties?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Parties</a>
  </div>
<%}%>

<div class="head1">Catalogs <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(productCategory==null?null:productCategory.getString("description"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productCategoryId)%>]</span></div>

<a href="<ofbiz:url>/EditCategory</ofbiz:url>" class="buttontext">[New Category]</a>
<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <a href="/ecommerce/control/category?category_id=<%=productCategoryId%>" class="buttontext" target='_blank'>[Category Page]</a>
<%}%>
<br>
<br>
<%if(productCategoryId!=null && productCategory!=null){%>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Catalog Name [ID]</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<%int line = 0;%>
<ofbiz:iterator name="productFeatureCategoryAppl" property="productFeatureCategoryAppls">
  <%line++;%>
  <%GenericValue productFeatureCategory = productFeatureCategoryAppl.getRelatedOne("ProductFeatureCategory");%>
  <tr valign="middle">
    <td><a href='<ofbiz:url>/EditFeatureCategoryFeatures?productFeatureCategoryId=<ofbiz:inputvalue entityAttr="productFeatureCategoryAppl" field="productFeatureCategoryId"/></ofbiz:url>' class="buttontext"><%if (productFeatureCategory!=null) {%><%=productFeatureCategory.getString("description")%><%}%> [<ofbiz:inputvalue entityAttr="productFeatureCategoryAppl" field="productFeatureCategoryId"/>]</a></td>
    <%boolean hasntStarted = false;%>
    <%if (productFeatureCategoryAppl.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(productFeatureCategoryAppl.getTimestamp("fromDate"))) { hasntStarted = true; }%>
    <td><div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>><ofbiz:inputvalue entityAttr="productFeatureCategoryAppl" field="fromDate"/></div></td>
    <td align="center">
        <FORM method=POST action='<ofbiz:url>/updateProductFeatureCategoryAppl</ofbiz:url>' name='lineForm<%=line%>'>
            <%boolean hasExpired = false;%>
            <%if (productFeatureCategoryAppl.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(productFeatureCategoryAppl.getTimestamp("thruDate"))) { hasExpired = true; }%>
            <input type=hidden <ofbiz:inputvalue entityAttr="productFeatureCategoryAppl" field="productCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productFeatureCategoryAppl" field="productFeatureCategoryId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productFeatureCategoryAppl" field="fromDate" fullattrs="true"/>>
            <input type=text size='25' <ofbiz:inputvalue entityAttr="productFeatureCategoryAppl" field="thruDate" fullattrs="true"/> class='inputBox' style='<%if (hasExpired) {%>color: red;<%}%>'>
            <a href="javascript:call_cal(document.lineForm<%=line%>.thruDate, '<ofbiz:inputvalue entityAttr="productFeatureCategoryAppl" field="thruDate" default="<%=nowTimestampString%>"/>');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/removeProductFeatureCategoryAppl?productFeatureCategoryId=<ofbiz:entityfield attribute="productFeatureCategoryAppl" field="productFeatureCategoryId"/>&productCategoryId=<ofbiz:entityfield attribute="productFeatureCategoryAppl" field="productCategoryId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(productFeatureCategoryAppl.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/createProductFeatureCategoryAppl</ofbiz:url>" style='margin: 0;' name='addNewForm'>
  <input type="hidden" name="productCategoryId" value="<%=productCategoryId%>">
  <input type="hidden" name="tryEntity" value="true">

  <div class='head2'>Add Catalog Product Category (select Category and Type, then enter optional From Date):</div>
  <br>
  <select name="productFeatureCategoryId" class='selectBox'>
  <ofbiz:iterator name="productFeatureCategory" property="productFeatureCategories">
    <option value='<ofbiz:entityfield attribute="productFeatureCategory" field="productFeatureCategoryId"/>'><ofbiz:entityfield attribute="productFeatureCategory" field="description"/> [<ofbiz:entityfield attribute="productFeatureCategory" field="productFeatureCategoryId"/>]</option>
  </ofbiz:iterator>
  </select>
  <input type=text size='25' name='fromDate' class='inputBox'>
  <a href="javascript:call_cal(document.addNewForm.fromDate, '<%=nowTimestampString%>');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
  <input type="submit" value="Add">
</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
