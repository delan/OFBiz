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

<%@ page import="java.util.*, java.io.*, java.net.URL" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("CATALOG", "_VIEW", request.getSession())) {%>
<%
    URL catalogPropertiesURL = application.getResource("/WEB-INF/catalog.properties");

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

    Collection categoryCol = delegator.findAll("ProductCategory", UtilMisc.toList("description"));

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
<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditCategory?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButtonSelected'>Category</a>
    <a href="<ofbiz:url>/EditCategoryRollup?showProductCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Rollup</a>
    <a href="<ofbiz:url>/EditCategoryProducts?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Products</a>
    <a href="<ofbiz:url>/EditCategoryProdCatalogs?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Catalogs</a>
    <a href="<ofbiz:url>/EditCategoryParties?productCategoryId=<%=productCategoryId%></ofbiz:url>" class='tabButton'>Parties</a>
  </div>
<%}%>
<div class="head1">Category <span class='head2'><%=UtilFormatOut.ifNotEmpty(productCategory==null?null:productCategory.getString("description"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productCategoryId)%>]</span></div>
<a href="<ofbiz:url>/EditCategory</ofbiz:url>" class="buttontext">[New Category]</a>
<%if(productCategoryId != null && productCategoryId.length() > 0) {%>
  <a href="/ecommerce/control/category?category_id=<%=productCategoryId%>" class="buttontext" target='_blank'>[Category Page]</a>
<%}%>
<br>
<br>

<%if(productCategory == null){%>
  <%if(productCategoryId != null){%>
    <h3>Could not find Product Category with ID "<%=UtilFormatOut.checkNull(productCategoryId)%>".</h3>
    <form action="<ofbiz:url>/createProductCategory</ofbiz:url>" method=POST style='margin: 0;' name='productCategoryForm'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">Product Category ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="productCategoryId" size="20" maxlength="40" value="<%=productCategoryId%>" style='font-size: x-small;'>
      </td>
    </tr>
  <%}else{%>
    <form action="<ofbiz:url>/createProductCategory</ofbiz:url>" method=POST style='margin: 0;' name='productCategoryForm'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">Product Category ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="productCategoryId" size="20" maxlength="40" value="" style='font-size: x-small;'>
      </td>
    </tr>
  <%}%>
<%}else{%>
  <form action="<ofbiz:url>/updateProductCategory</ofbiz:url>" method=POST style='margin: 0;' name='productCategoryForm'>
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
          <select name="productCategoryTypeId" size=1 style='font-size: x-small;'>
            <option selected value='<ofbiz:inputvalue entityAttr="productCategory" field="productCategoryTypeId"/>'><ofbiz:inputvalue entityAttr="productCategoryType" field="description"/><%-- <ofbiz:entityfield attribute="productCategory" field="productCategoryTypeId" prefix="[" suffix="]"/>--%></option>
            <option value='<ofbiz:inputvalue entityAttr="productCategory" field="productCategoryTypeId"/>'>----</option>
            <ofbiz:iterator name="nextProductCategoryType" property="productCategoryTypes">
              <option value='<ofbiz:inputvalue entityAttr="nextProductCategoryType" field="productCategoryTypeId"/>'><ofbiz:inputvalue entityAttr="nextProductCategoryType" field="description"/><%-- <ofbiz:entityfield attribute="nextProductCategoryType" field="productCategoryTypeId" prefix="[" suffix="]"/>--%></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext">Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr='productCategory' field='description' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="60" style='font-size: x-small;'></td>
  </tr>
  <tr>
    <td width="26%" align=right valign=top><div class="tabletext">Long Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><textarea cols="60" rows="3" name="longDescription" maxlength="2000" style='font-size: small;'><ofbiz:inputvalue entityAttr='productCategory' field='longDescription' tryEntityAttr="tryEntity"/></textarea></td>
  </tr>

<%if (UtilValidate.isNotEmpty(productCategoryId)) {%>
    <SCRIPT language='JavaScript'>
    function insertImageName(type,ext) {
      eval('document.forms.productCategoryForm.' + type + 'ImageUrl.value="<%=UtilProperties.getPropertyValue(catalogPropertiesURL, "image.url.prefix")%>/category.<%=productCategoryId%>.' + type + '.' + ext + '";');
    };
    </SCRIPT>
<%}%>
  <tr>
    <td width="26%" align=right><div class="tabletext">Category Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" <ofbiz:inputvalue entityAttr='productCategory' field='categoryImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'>
      <%if (UtilValidate.isNotEmpty(productCategoryId)) {%>
        <div>
          <a href="<ofbiz:url>/UploadCategoryImage?productCategoryId=<%=productCategoryId%>&upload_file_type=category</ofbiz:url>" class="buttontext">[Upload Category Image]</a>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('category', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('category', 'gif');" class="buttontext">[.gif]</a>
        </div>
      <%}%>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Link One Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" <ofbiz:inputvalue entityAttr='productCategory' field='linkOneImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'>
      <%if (UtilValidate.isNotEmpty(productCategoryId)) {%>
        <div>
          <a href="<ofbiz:url>/UploadCategoryImage?productCategoryId=<%=productCategoryId%>&upload_file_type=linkOne</ofbiz:url>" class="buttontext">[Upload Link One Image]</a>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('linkOne', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('linkOne', 'gif');" class="buttontext">[.gif]</a>
        </div>
      <%}%>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Link Two Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" <ofbiz:inputvalue entityAttr='productCategory' field='linkTwoImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'>
      <%if (UtilValidate.isNotEmpty(productCategoryId)) {%>
        <div>
          <a href="<ofbiz:url>/UploadCategoryImage?productCategoryId=<%=productCategoryId%>&upload_file_type=linkTwo</ofbiz:url>" class="buttontext">[Upload Link Two Image]</a>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('linkTwo', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('linkTwo', 'gif');" class="buttontext">[.gif]</a>
        </div>
      <%}%>
    </td>
  </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext">Detail Template</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='4'>
        <input type="text" <ofbiz:inputvalue entityAttr='productCategory' field='detailTemplate' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'>
        <br><span class='tabletext'>Defaults to "/catalog/categorydetail.jsp"</span>
    </td>
  </tr>

  <tr>
    <td align=right><div class="tabletext">Primary Parent Category ID</div></td>
    <td>&nbsp;</td>
    <td>
      <select name="primaryParentProductCategoryId" size=1 style='font-size: x-small;'>
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
    <td colspan='2'>&nbsp;</td>
    <td><input type="submit" name="Update" value="Update" style='font-size: x-small;'></td>
  </tr>
</table>
</form>
<br>

<%}else{%>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
