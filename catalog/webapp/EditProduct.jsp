<%
/**
 *  Title: Edit Product Page
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

<%pageContext.setAttribute("PageName", "Edit Product");%>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
  boolean useValues = true;
  if(request.getAttribute("ERROR_MESSAGE") != null) useValues = false;

  String productId = request.getParameter("PRODUCT_ID");
  GenericValue product = helper.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
  if(product == null) useValues = false;

  Collection categoryCol = helper.findAll("ProductCategory", null);

  GenericValue primaryProductCategory = null;
  String primProdCatIdParam = request.getParameter("PRIMARY_PRODUCT_CATEGORY_ID");
  if(product != null && useValues)
    primaryProductCategory = product.getRelatedOne("PrimaryProductCategory");
  else if(primProdCatIdParam != null && primProdCatIdParam.length() > 0)
    primaryProductCategory = helper.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", primProdCatIdParam));
%>

<div class="head1">Edit Product with ID "<%=productId%>"</div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[Create New Product]</a>
<%-- <%if(product != null){%><a href="<ofbiz:url>UpdateProduct?UPDATE_MODE=DELETE&PRODUCT_ID=<%=product.getSku()%></ofbiz:url>" class="buttontext">[Delete this Product]</a><%}%> --%>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?PRODUCT_ID=<%=productId%>" class="buttontext">[View Product Page]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Keywords]</a>
<%}%>

<form action="<ofbiz:url>/UpdateProduct</ofbiz:url>" method=POST style='margin: 0;'>
<table border="1">

<%if(product == null){%>
  <%if(productId != null){%>
    <h3>Could not find product with ID "<%=productId%>".</h3>
    <input type=hidden name="UPDATE_MODE" value="CREATE">
    <tr>
      <td>Product ID</td>
      <td>
        <input type="text" name="PRODUCT_ID" size="20" maxlength="40" value="<%=productId%>">
      </td>
    </tr>
  <%}else{%>
    <input type=hidden name="UPDATE_MODE" value="CREATE">
    <tr>
      <td>Product ID</td>
      <td>
        <input type="text" name="PRODUCT_ID" size="20" maxlength="40" value="">
      </td>
    </tr>
  <%}%>
<%}else{%>
  <input type=hidden name="UPDATE_MODE" value="UPDATE">
  <input type=hidden name="PRODUCT_ID" value="<%=productId%>">
  <tr>
    <td>Product ID</td>
    <td>
      <b><%=productId%></b> (This cannot be changed without re-creating the product.)
    </td>
  </tr>
<%}%>

  <%String fieldName; String paramName;%>
  <tr>
    <%fieldName = "primaryProductCategoryId";%><%paramName = "PRIMARY_PRODUCT_CATEGORY_ID";%>
    <td width="26%"><div class="tabletext">Primary Category Id</div></td>
    <td width="74%">
      <%-- <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"> --%>
      <select name="<%=paramName%>" size=1>
        <%if(primaryProductCategory != null) {%>
          <option selected value='<%=primaryProductCategory.getString("productCategoryId")%>'><%=primaryProductCategory.getString("description")%> [<%=primaryProductCategory.getString("productCategoryId")%>]</option>
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
    <%fieldName = "manufacturerPartyId";%><%paramName = "MANUFACTURER_PARTY_ID";%>    
    <td width="26%"><div class="tabletext">Manufacturer Party Id</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>

  <tr>
    <%fieldName = "introductionDate";%><%paramName = "INTRODUCTION_DATE";%>    
    <td width="26%"><div class="tabletext">Introduction Date</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilDateTime.toDateString(product.getDate(fieldName)):request.getParameter(paramName))%>" size="10" maxlength="20">(MM/DD/YYYY)</td>
  </tr>
  <tr>
    <%fieldName = "salesDiscontinuationDate";%><%paramName = "SALES_DISCONTINUATION_DATE";%>    
    <td width="26%"><div class="tabletext">Sales Discontinuation Date</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilDateTime.toDateString(product.getDate(fieldName)):request.getParameter(paramName))%>" size="10" maxlength="20">(MM/DD/YYYY)</td>
  </tr>
  <tr>
    <%fieldName = "supportDiscontinuationDateStr";%><%paramName = "SUPPORT_DISCONTINUATION_DATE";%>    
    <td width="26%"><div class="tabletext">Support Discontinuation Date</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilDateTime.toDateString(product.getDate(fieldName)):request.getParameter(paramName))%>" size="10" maxlength="20">(MM/DD/YYYY)</td>
  </tr>

  <tr>
    <%fieldName = "name";%><%paramName = "NAME";%>    
    <td width="26%"><div class="tabletext">Name</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="30" maxlength="60"></td>
  </tr>
  <tr>
    <%fieldName = "comment";%><%paramName = "COMMENT";%>    
    <td width="26%"><div class="tabletext">Comment</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255"></td>
  </tr>
  <tr>
    <%fieldName = "description";%><%paramName = "DESCRIPTION";%>    
    <td width="26%"><div class="tabletext">Description</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255"></td>
  </tr>
  <tr>
    <%fieldName = "longDescription";%><%paramName = "LONG_DESCRIPTION";%>    
    <td width="26%">Long Description</td>
    <td width="74%"><textarea cols="60" rows="3" name="<%=paramName%>" maxlength="2000"><%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%></textarea></td>
  </tr>

  <tr>
    <%fieldName = "smallImageUrl";%><%paramName = "SMALL_IMAGE_URL";%>    
    <td width="26%"><div class="tabletext">Small Image URL</div></td>
    <td width="74%">
      <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255">
      <p>
      <a href="<ofbiz:url>/UploadImage?upload_file_type=small&PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Upload Small Image]</a>
    </td>
  </tr>
  <tr>
    <%fieldName = "largeImageUrl";%><%paramName = "LARGE_IMAGE_URL";%>    
    <td width="26%"><div class="tabletext">Large Image URL</div></td>
    <td width="74%">
      <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255">
      <p>
      <a href="<ofbiz:url>/UploadImage?upload_file_type=large&PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Upload Large Image]</a>
    </td>
  </tr>

  <tr>
    <%fieldName = "defaultPrice";%><%paramName = "DEFAULT_PRICE";%>    
    <td width="26%"><div class="tabletext">Default Price</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(product.getDouble(fieldName)):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>

  <tr>
    <%fieldName = "quantityUomId";%><%paramName = "QUANTITY_UOM_ID";%>    
    <td width="26%"><div class="tabletext">Quantity Uom Id</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>
  <tr>
    <%fieldName = "quantityIncluded";%><%paramName = "QUANTITY_INCLUDED";%>    
    <td width="26%"><div class="tabletext">Quantity Included</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(product.getDouble(fieldName)):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>

  <tr>
    <%fieldName = "weightUomId";%><%paramName = "WEIGHT_UOM_ID";%>    
    <td width="26%"><div class="tabletext">Weight Uom Id</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>
  <tr>
    <%fieldName = "weight";%><%paramName = "WEIGHT";%>    
    <td width="26%"><div class="tabletext">Weight</div></td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(product.getDouble(fieldName)):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>

  <tr>
    <%fieldName = "taxable";%><%paramName = "TAXABLE";%>    
    <td width="26%"><div class="tabletext">Taxable?</div></td>
    <td width="74%">
      <SELECT name='<%=paramName%>'>
        <OPTION><%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>
        <OPTION>&nbsp;</OPTION>
        <OPTION>Y</OPTION>
        <OPTION>N</OPTION>
      </SELECT>
    </td>
  </tr>
  <tr>
    <%fieldName = "showInSearch";%><%paramName = "SHOW_IN_SEARCH";%>
    <td width="26%"><div class="tabletext">Show In Search?</div></td>
    <td width="74%">
      <SELECT name='<%=paramName%>'>
        <OPTION><%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>
        <OPTION>&nbsp;</OPTION>
        <OPTION>Y</OPTION>
        <OPTION>N</OPTION>
      </SELECT>
    </td>
  </tr>

  <tr>
    <td colspan='2'><input type="submit" name="Update" value="Update"></td>
  </tr>
</table>
</form>

<a href="<ofbiz:url>EditProduct</ofbiz:url>" class="buttontext">[Create New Product]</a>
<%-- <%if(product != null){%><a href="<ofbiz:url>UpdateProduct?UPDATE_MODE=DELETE&PRODUCT_ID=<%=product.getSku()%></ofbiz:url>" class="buttontext">[Delete this Product]</a><%}%> --%>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?PRODUCT_ID=<%=productId%>" class="buttontext">[View Product Details Page]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Keywords]</a>
<%}%>
<br>

<%-- Edit 'ProductCategory's --%>
<%if(productId!=null && product!=null){%>
<hr>
<p class="head2">Product-Category Maintenance</p>

<table border="1" cellpadding='0' cellspacing='0'>
  <tr>
    <td><b>Category ID</b></td>
    <td><b>Description</b></td>
    <td><b></b></td>
  </tr>
<%Iterator pcIterator = UtilMisc.toIterator(product.getRelated("ProductCategoryMember"));%>
<%while(pcIterator.hasNext()) {%>
  <%GenericValue productCategoryMember = (GenericValue)pcIterator.next();%>
  <%GenericValue category = productCategoryMember.getRelatedOne("ProductCategory");%>
  <tr valign="middle">
    <td><a href="/ecommerce/control/category?PRODUCT_CATEGORY_ID=<%=productCategoryMember.getString("categoryId")%>" class="buttontext"><%=productCategoryMember.getString("categoryId")%></a></td>
    <td><%if(category!=null){%><a href="/ecommerce/control/category?PRODUCT_CATEGORY_ID=<%=productCategoryMember.getString("categoryId")%>" class="buttontext"><%=category.getString("description")%></a><%}%>&nbsp;</td>
    <td>
      <a href="<ofbiz:url>/UpdateProductCategoryMember?PRODUCT_ID=<%=productId%>&PRODUCT_CATEGORY_ID=<%=productCategoryMember.getString("categoryId")%></ofbiz:url>" class="buttontext">
      [Delete]</a>
    </td>
  </tr>
<%}%>
</table>

<form method="POST" action="<ofbiz:url>/UpdateProductCategoryMember</ofbiz:url>">
  <input type="hidden" name="PRODUCT_ID" value="<%=productId%>">
  Add ProductCategoryMember (enter Category ID):
    <select name="PRODUCT_CATEGORY_ID">
    <%Iterator it = UtilMisc.toIterator(categoryCol);%>
    <%while(it != null && it.hasNext()) {%>
      <%GenericValue category = (GenericValue)it.next();%>
      <option value="<%=category.getString("productCategoryId")%>"><%=category.getString("description")%> [<%=category.getString("productCategoryId")%>]</option>
    <%}%>
    </select>
  <input type="submit" value="Add">
</form>
<%}%>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
