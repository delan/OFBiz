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

    String productCategoryId = request.getParameter("productCategoryId");
    GenericValue productCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
    GenericValue productCategoryType = null;
    if(productCategory == null) {
        useValues = false;
    } else {
        pageContext.setAttribute("productCategory", productCategory);
        productCategoryType = productCategory.getRelatedOne("ProductCategoryType");
        if (productCategoryType != null) pageContext.setAttribute("productCategoryType", productCategoryType);
    }

    Collection categoryCol = delegator.findAll("ProductCategory", null);

    GenericValue primaryParentCategory = null;
    String primParentCatIdParam = request.getParameter("primaryParentProductCategoryId");
    if(productCategory != null && useValues)  {
        primaryParentCategory = productCategory.getRelatedOne("PrimaryParentProductCategory");
    } else if(primParentCatIdParam != null && primParentCatIdParam.length() > 0) {
        primaryParentCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", primParentCatIdParam));
    }

    //ProductCategoryTypes
    Collection productCategoryTypes = delegator.findAll("ProductCategoryType");
    if (productCategoryTypes != null) pageContext.setAttribute("productCategoryTypes", productCategoryTypes);
%>
<br>
<a href="<ofbiz:url>/EditCategory</ofbiz:url>" class="buttontext">[New Category]</a>
<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <a href="/ecommerce/control/category?category_id=<%=productCategoryId%>" class="buttontext" target='_blank'>[Category Page]</a>
  <a href="<ofbiz:url>/EditCategory?productCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontextdisabled">[Category]</a>
  <a href="<ofbiz:url>/EditCategoryRollup?showProductCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontext">[Rollup]</a>
  <a href="<ofbiz:url>/EditCategoryProducts?productCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontext">[Products]</a>
  <a href="<ofbiz:url>/EditCategoryProdCatalogs?productCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontext">[Catalogs]</a>
<%}%>
<div class="head1">Edit Product Category with ID "<%=UtilFormatOut.checkNull(productCategoryId)%>"</div>
<%if(productCategory == null){%>
  <%if(productCategoryId != null){%>
    <h3>Could not find Product Category with ID "<%=UtilFormatOut.checkNull(productCategoryId)%>".</h3>
    <form action="<ofbiz:url>/createProductCategory</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">Product Category ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="productCategoryId" size="20" maxlength="40" value="<%=productCategoryId%>">
      </td>
    </tr>
  <%}else{%>
    <form action="<ofbiz:url>/createProductCategory</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">Product Category ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="productCategoryId" size="20" maxlength="40" value="">
      </td>
    </tr>
  <%}%>
<%}else{%>
  <form action="<ofbiz:url>/updateProductCategory</ofbiz:url>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="productCategoryId" value="<%=productCategoryId%>">
  <tr>
    <td align=right><div class="tabletext">Product Category ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=productCategoryId%></b> (This cannot be changed without re-creating the cateogry.)
    </td>
  </tr>
<%}%>

      <tr>
        <td width="26%" align=right><div class="tabletext">ProductCategory Type</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <select name="productCategoryTypeId" size=1>
            <option selected value='<ofbiz:inputvalue entityAttr="productCategory" field="productCategoryTypeId"/>'><ofbiz:inputvalue entityAttr="productCategoryType" field="description"/> <ofbiz:entityfield attribute="productCategory" field="productCategoryTypeId" prefix="[" suffix="]"/></option>
            <option value='<ofbiz:inputvalue entityAttr="productCategory" field="productCategoryTypeId"/>'>----</option>
            <ofbiz:iterator name="nextProductCategoryType" property="productCategoryTypes">
              <option value='<ofbiz:inputvalue entityAttr="nextProductCategoryType" field="productCategoryTypeId"/>'><ofbiz:inputvalue entityAttr="nextProductCategoryType" field="description"/> <ofbiz:entityfield attribute="nextProductCategoryType" field="productCategoryTypeId" prefix="[" suffix="]"/></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>

  <%String fieldName; String paramName;%>
  <tr>
    <%fieldName = "description";%><%paramName = "description";%>    
    <td width="26%" align=right><div class="tabletext">Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?productCategory.getString(fieldName):request.getParameter(paramName))%>" size="60" maxlength="60"></td>
  </tr>
  <tr>
    <%fieldName = "longDescription";%><%paramName = "longDescription";%>    
    <td width="26%" align=right valign=top><div class="tabletext">Long Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><textarea cols="60" rows="3" name="<%=paramName%>" maxlength="2000"><%=UtilFormatOut.checkNull(useValues?productCategory.getString(fieldName):request.getParameter(paramName))%></textarea></td>
  </tr>
  <tr>
    <%fieldName = "categoryImageUrl";%><%paramName = "categoryImageUrl";%>    
    <td width="26%" align=right><div class="tabletext">Category Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?productCategory.getString(fieldName):request.getParameter(paramName))%>" size="60" maxlength="250">
      <%if(productCategoryId != null && productCategoryId.length() > 0) {%><p><a href="<ofbiz:url>/UploadCategoryImage?productCategoryId=<%=productCategoryId%></ofbiz:url>" class="buttontext">[Upload Image]</a><%}%>
    </td>
  </tr>

  <tr>
    <td align=right><div class="tabletext">Primary Parent Category ID</div></td>
    <td>&nbsp;</td>
    <td>
      <select name="primaryParentProductCategoryId" size=1>
        <%if(primaryParentCategory != null) {%>
          <option selected value='<%=primaryParentCategory.getString("productCategoryId")%>'><%=primaryParentCategory.getString("description")%> [<%=primaryParentCategory.getString("productCategoryId")%>]</option>
        <%}%>
        <option value=''>&nbsp;</option>
        <%Iterator categoryIter = categoryCol.iterator();%>
        <%while(categoryIter != null && categoryIter.hasNext()) {%>
          <%GenericValue nextCategory=(GenericValue)categoryIter.next();%>
          <%if(nextCategory != null) {%>
            <%if(productCategoryId == null || !productCategoryId.equals(nextCategory.getString("productCategoryId"))){%>
              <option value='<%=nextCategory.getString("productCategoryId")%>'><%=nextCategory.getString("description")%> [<%=nextCategory.getString("productCategoryId")%>]</option>
            <%}%>
          <%}%>
        <%}%>
      </select>
    </td>
  </tr>
  <tr>
    <td colspan='3'>
      <input type="submit" name="Update" value="Update">
    </td>
  </tr>
</table>
</form>
<br>

<%}else{%>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
</td><td>&nbsp;&nbsp;</td></tr></table>
