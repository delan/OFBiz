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

<% pageContext.setAttribute("PageName", "Edit Category"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%if(security.hasEntityPermission("CATALOG", "_VIEW", request.getSession())) {%>
<%
  boolean useValues = true;
  if(request.getAttribute("ERROR_MESSAGE") != null) useValues = false;

  String productCategoryId = request.getParameter("PRODUCT_CATEGORY_ID");
  GenericValue category = helper.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
  if(category == null) useValues = false;

  Collection categoryCol = helper.findAll("ProductCategory", null);

  GenericValue primaryParentCategory = null;
  String primParentCatIdParam = request.getParameter("PRIMARY_PARENT_CATEGORY_ID");
  if(category != null && useValues) 
    primaryParentCategory = category.getRelatedOne("PrimaryParentProductCategory");
  else if(primParentCatIdParam != null && primParentCatIdParam.length() > 0)
    primaryParentCategory = helper.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", primParentCatIdParam));
%>

<div class="head1">Edit Product Category with ID "<%=productCategoryId%>"</div>
<a href="<ofbiz:url>/EditCategory</ofbiz:url>" class="buttontext">[Create New Category]</a>
<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <a href="/ecommerce/control/category?PRODUCT_CATEGORY_ID=<%=productCategoryId%>" class="buttontext">[View Category Page]</a>
<%}%>
<form action="<ofbiz:url>/UpdateCategory</ofbiz:url>" method=POST style='margin: 0;'>
<table border="1">
<%if(category == null){%>
  <%if(productCategoryId != null){%>
    <h3>Could not find Product Category with ID "<%=productCategoryId%>".</h3>
    <input type=hidden name="UPDATE_MODE" value="CREATE">
    <tr>
      <td>Product Category ID</td>
      <td>
        <input type="text" name="PRODUCT_CATEGORY_ID" size="20" maxlength="40" value="<%=productCategoryId%>">
      </td>
    </tr>
  <%}else{%>
    <input type=hidden name="UPDATE_MODE" value="CREATE">
    <tr>
      <td>Product Category ID</td>
      <td>
        <input type="text" name="PRODUCT_CATEGORY_ID" size="20" maxlength="40" value="">
      </td>
    </tr>
  <%}%>
<%}else{%>
  <input type=hidden name="UPDATE_MODE" value="UPDATE">
  <input type=hidden name="PRODUCT_CATEGORY_ID" value="<%=productCategoryId%>">
  <tr>
    <td>Product Category ID</td>
    <td>
      <b><%=productCategoryId%></b> (This cannot be changed without re-creating the cateogry.)
    </td>
  </tr>
<%}%>

  <%String fieldName; String paramName;%>
  <tr>
    <%fieldName = "description";%><%paramName = "DESCRIPTION";%>    
    <td width="26%"><div class="tabletext">Description</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?category.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>
  <tr>
    <%fieldName = "categoryImageUrl";%><%paramName = "CATEGORY_IMAGE_URL";%>    
    <td width="26%"><div class="tabletext">Category Image URL</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?category.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255"></td>
  </tr>

  <tr>
    <td>Primary Parent Category ID</td>
    <td>
      <select name="PRIMARY_PARENT_CATEGORY_ID" size=1>
        <%if(primaryParentCategory != null) {%>
          <option selected value='<%=primaryParentCategory.getString("productCategoryId")%>'><%=primaryParentCategory.getString("description")%> [<%=primaryParentCategory.getString("productCategoryId")%>]</option>
        <%}%>
        <option>&nbsp;</option>
        <%Iterator categoryIter = UtilMisc.toIterator(categoryCol);%>
        <%while(categoryIter != null && categoryIter.hasNext()) {%>
          <%GenericValue nextCategory=(GenericValue)categoryIter.next();%>
          <option value='<%=nextCategory.getString("productCategoryId")%>'><%=nextCategory.getString("description")%> [<%=nextCategory.getString("productCategoryId")%>]</option>
        <%}%>
      </select>
    </td>
  </tr>
  <tr>
    <td colspan='2'>
      <input type="submit" name="Update" value="Update">
    </td>
  </tr>
</table>
</form>
<a href="<ofbiz:url>/EditCategory</ofbiz:url>" class="buttontext">[Create New Category]</a>
<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <a href="/ecommerce/control/category?PRODUCT_CATEGORY_ID=<%=productCategoryId%>" class="buttontext">[View Category Page]</a>
<%}%>
<br>

<%-- Edit 'ProductCategoryRollup's --%>
<%if(category!=null){%>
<hr>
<p class="head2">Category Rollup: Parent Categories</p>

<table border="1" cellpadding='0' cellspacing='0'>
  <tr>
    <td><b>Parent Category ID</b></td>
    <td><b>Description</b></td>
    <td><b>&nbsp;</b></td>
  </tr>
<%Iterator ppcIterator = UtilMisc.toIterator(category.getRelated("CurrentProductCategoryRollup"));%>
<%if(ppcIterator != null && ppcIterator.hasNext()) {%>
  <%while(ppcIterator.hasNext()) {%>
    <%GenericValue productCategoryRollup = (GenericValue)ppcIterator.next();%>
    <%GenericValue curCategory = productCategoryRollup.getRelatedOne("ParentProductCategory");%>
    <tr valign="middle">
      <td><a href="<ofbiz:url>/EditCategory?PRODUCT_CATEGORY_ID=<%=curCategory.getString("productCategoryId")%></ofbiz:url>" class="buttontext"><%=curCategory.getString("productCategoryId")%></a></td>
      <td><%if(curCategory!=null){%><a href="<ofbiz:url>/EditCategory?PRODUCT_CATEGORY_ID=<%=curCategory.getString("productCategoryId")%></ofbiz:url>" class="buttontext"><%=curCategory.getString("description")%></a><%}%>&nbsp;</td>
      <td>
        <a href="<ofbiz:url>/UpdateProductCategoryRollup?UPDATE_MODE=DELETE&PRODUCT_CATEGORY_ID=<%=productCategoryId%>&UPDATE_PRODUCT_CATEGORY_ID=<%=productCategoryRollup.getString("productCategoryId")%>&UPDATE_PARENT_PRODUCT_CATEGORY_ID=<%=productCategoryRollup.getString("parentProductCategoryId")%></ofbiz:url>" class="buttontext">
        [Delete]</a>
      </td>
    </tr>
  <%}%>
<%}else{%>
  <tr valign="middle">
    <td colspan='3'><DIV class='tabletext'>No Parent Categories found.</DIV></td>
  </tr>
<%}%>
</table>

<form method="POST" action="<ofbiz:url>/UpdateProductCategoryRollup</ofbiz:url>">
  <input type="hidden" name="UPDATE_PRODUCT_CATEGORY_ID" value="<%=productCategoryId%>">
  <input type="hidden" name="PRODUCT_CATEGORY_ID" value="<%=productCategoryId%>">
  <input type="hidden" name="UPDATE_MODE" value="CREATE">
  Add Parent Category (enter Category ID):
    <select name="UPDATE_PARENT_PRODUCT_CATEGORY_ID">
    <%Iterator pit = UtilMisc.toIterator(categoryCol);%>
    <%while(pit != null && pit.hasNext()) {%>
      <%GenericValue curCategory = (GenericValue)pit.next();%>
      <option value="<%=curCategory.getString("productCategoryId")%>"><%=curCategory.getString("description")%> [<%=curCategory.getString("productCategoryId")%>]</option>
    <%}%>
    </select>
  <input type="submit" value="Add">
</form>

<hr>
<p class="head2">Category Rollup: Child Categories</p>

<table border="1" cellpadding='0' cellspacing='0'>
  <tr>
    <td><b>Child Category ID</b></td>
    <td><b>Description</b></td>
    <td><b>&nbsp;</b></td>
  </tr>
<%Iterator cpcIterator = UtilMisc.toIterator(category.getRelated("ParentProductCategoryRollup"));%>
<%if(cpcIterator != null && cpcIterator.hasNext()) {%>
  <%while(cpcIterator.hasNext()) {%>
    <%GenericValue productCategoryRollup = (GenericValue)cpcIterator.next();%>
    <%GenericValue curCategory = productCategoryRollup.getRelatedOne("CurrentProductCategory");%>
    <tr valign="middle">
      <td><a href="<ofbiz:url>/EditCategory?PRODUCT_CATEGORY_ID=<%=curCategory.getString("productCategoryId")%></ofbiz:url>" class="buttontext"><%=curCategory.getString("productCategoryId")%></a></td>
      <td><%if(curCategory!=null){%><a href="<ofbiz:url>/EditCategory?PRODUCT_CATEGORY_ID=<%=curCategory.getString("productCategoryId")%></ofbiz:url>" class="buttontext"><%=curCategory.getString("description")%></a><%}%>&nbsp;</td>
      <td>
        <a href="<ofbiz:url>/UpdateProductCategoryRollup?UPDATE_MODE=DELETE&PRODUCT_CATEGORY_ID=<%=productCategoryId%>&UPDATE_PRODUCT_CATEGORY_ID=<%=productCategoryRollup.getString("productCategoryId")%>&UPDATE_PARENT_PRODUCT_CATEGORY_ID=<%=productCategoryRollup.getString("parentProductCategoryId")%></ofbiz:url>" class="buttontext">
        [Delete]</a>
      </td>
    </tr>
  <%}%>
<%}else{%>
  <tr valign="middle">
    <td colspan='3'><DIV class='tabletext'>No Child Categories found.</DIV></td>
  </tr>
<%}%>
</table>

<form method="POST" action="<ofbiz:url>/UpdateProductCategoryRollup</ofbiz:url>">
  <input type="hidden" name="UPDATE_PARENT_PRODUCT_CATEGORY_ID" value="<%=productCategoryId%>">
  <input type="hidden" name="PRODUCT_CATEGORY_ID" value="<%=productCategoryId%>">
  <input type="hidden" name="UPDATE_MODE" value="CREATE">
  Add Child Category (enter Category ID):
    <select name="UPDATE_PRODUCT_CATEGORY_ID">
    <%Iterator cit = UtilMisc.toIterator(categoryCol);%>
    <%while(cit != null && cit.hasNext()) {%>
      <%GenericValue curCategory = (GenericValue)cit.next();%>
      <option value="<%=curCategory.getString("productCategoryId")%>"><%=curCategory.getString("description")%> [<%=curCategory.getString("productCategoryId")%>]</option>
    <%}%>
    </select>
  <input type="submit" value="Add">
</form>
<%}%>

<%}else{%>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
