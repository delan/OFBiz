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

<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %>

<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
  boolean useValues = true;
  if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

  String productId = request.getParameter("PRODUCT_ID");
  GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
  if(product == null) useValues = false;

  Collection categoryCol = delegator.findAll("ProductCategory", null);

  GenericValue primaryProductCategory = null;
  String primProdCatIdParam = request.getParameter("PRIMARY_PRODUCT_CATEGORY_ID");
  if(product != null && useValues)
    primaryProductCategory = product.getRelatedOne("PrimaryProductCategory");
  else if(primProdCatIdParam != null && primProdCatIdParam.length() > 0)
    primaryProductCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", primProdCatIdParam));

  if("true".equalsIgnoreCase((String)request.getParameter("useValues"))) useValues = true;
%>
<br>
<div class="head1">Edit Product with ID "<%=UtilFormatOut.checkNull(productId)%>"</div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[Create New Product]</a>
<%-- <%if(product != null){%><a href="<ofbiz:url>UpdateProduct?UPDATE_MODE=DELETE&PRODUCT_ID=<%=product.getSku()%></ofbiz:url>" class="buttontext">[Delete this Product]</a><%}%> --%>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[View Product Page]</a>
  <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Product]</a>
  <a href="<ofbiz:url>/EditProductCategories?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Category Members]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Keywords]</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Associations]</a>
<%}%>

<form action="<ofbiz:url>/UpdateProduct</ofbiz:url>" method=POST style='margin: 0;'>
<table border='0' cellpadding='2' cellspacing='0'>

<%if(product == null){%>
  <%if(productId != null){%>
    <h3>Could not find product with ID "<%=productId%>".</h3>
    <input type=hidden name="UPDATE_MODE" value="CREATE">
    <tr>
      <td align=right><div class="tabletext">Product ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="PRODUCT_ID" size="20" maxlength="40" value="<%=productId%>">
      </td>
    </tr>
  <%}else{%>
    <input type=hidden name="UPDATE_MODE" value="CREATE">
    <tr>
      <td align=right><div class="tabletext">Product ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="PRODUCT_ID" size="20" maxlength="40" value="">
      </td>
    </tr>
  <%}%>
<%}else{%>
  <input type=hidden name="UPDATE_MODE" value="UPDATE">
  <input type=hidden name="PRODUCT_ID" value="<%=productId%>">
  <tr>
    <td align=right><div class="tabletext">Product ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=productId%></b> (This cannot be changed without re-creating the product.)
    </td>
  </tr>
<%}%>

  <%String fieldName; String paramName;%>
  <tr>
    <%fieldName = "primaryProductCategoryId";%><%paramName = "PRIMARY_PRODUCT_CATEGORY_ID";%>
    <td width="26%" align=right><div class="tabletext">Primary Category Id</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <%-- <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"> --%>
      <select name="<%=paramName%>" size=1>
        <%if(primaryProductCategory != null) {%>
          <option selected value='<%=primaryProductCategory.getString("productCategoryId")%>'><%=primaryProductCategory.getString("description")%> [<%=primaryProductCategory.getString("productCategoryId")%>]</option>
        <%}%>
        <option value=''>&nbsp;</option>
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
    <td width="26%" align=right><div class="tabletext">Manufacturer Party Id</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>

  <tr>
    <%fieldName = "introductionDate";%><%paramName = "INTRODUCTION_DATE";%>    
    <td width="26%" align=right><div class="tabletext">Introduction Date</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilDateTime.toDateString(product.getDate(fieldName)):request.getParameter(paramName))%>" size="10" maxlength="20">(MM/DD/YYYY)</td>
  </tr>
  <tr>
    <%fieldName = "salesDiscontinuationDate";%><%paramName = "SALES_DISCONTINUATION_DATE";%>    
    <td width="26%" align=right><div class="tabletext">Sales Discontinuation Date</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilDateTime.toDateString(product.getDate(fieldName)):request.getParameter(paramName))%>" size="10" maxlength="20">(MM/DD/YYYY)</td>
  </tr>
  <tr>
    <%fieldName = "supportDiscontinuationDate";%><%paramName = "SUPPORT_DISCONTINUATION_DATE";%>    
    <td width="26%" align=right><div class="tabletext">Support Discontinuation Date</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilDateTime.toDateString(product.getDate(fieldName)):request.getParameter(paramName))%>" size="10" maxlength="20">(MM/DD/YYYY)</td>
  </tr>

  <tr>
    <%fieldName = "productName";%><%paramName = "NAME";%>    
    <td width="26%" align=right><div class="tabletext">Name</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="30" maxlength="60"></td>
  </tr>
  <tr>
    <%fieldName = "comments";%><%paramName = "COMMENT";%>    
    <td width="26%" align=right><div class="tabletext">Comment</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255"></td>
  </tr>
  <tr>
    <%fieldName = "description";%><%paramName = "DESCRIPTION";%>    
    <td width="26%" align=right><div class="tabletext">Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255"></td>
  </tr>
  <tr>
    <%fieldName = "longDescription";%><%paramName = "LONG_DESCRIPTION";%>    
    <td width="26%" align=right valign=top><div class="tabletext">Long Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><textarea cols="60" rows="3" name="<%=paramName%>" maxlength="2000"><%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%></textarea></td>
  </tr>

  <tr>
    <%fieldName = "smallImageUrl";%><%paramName = "SMALL_IMAGE_URL";%>    
    <td width="26%" align=right valign=top><div class="tabletext">Small Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255">
      <%if(productId != null && productId.length() > 0) {%><p><a href="<ofbiz:url>/UploadImage?upload_file_type=small&PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Upload Small Image]</a><%}%>
    </td>
  </tr>
  <tr>
    <%fieldName = "largeImageUrl";%><%paramName = "LARGE_IMAGE_URL";%>    
    <td width="26%" align=right valign=top><div class="tabletext">Large Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255">
      <%if(productId != null && productId.length() > 0) {%><p><a href="<ofbiz:url>/UploadImage?upload_file_type=large&PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Upload Large Image]</a><%}%>
    </td>
  </tr>

  <tr>
    <%fieldName = "defaultPrice";%><%paramName = "DEFAULT_PRICE";%>    
    <td width="26%" align=right><div class="tabletext">Default Price</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(product.getDouble(fieldName)):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>

  <tr>
    <%fieldName = "quantityUomId";%><%paramName = "QUANTITY_UOM_ID";%>    
    <td width="26%" align=right><div class="tabletext">Quantity Uom Id</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>
  <tr>
    <%fieldName = "quantityIncluded";%><%paramName = "QUANTITY_INCLUDED";%>    
    <td width="26%" align=right><div class="tabletext">Quantity Included</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(product.getDouble(fieldName)):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>

  <tr>
    <%fieldName = "weightUomId";%><%paramName = "WEIGHT_UOM_ID";%>    
    <td width="26%" align=right><div class="tabletext">Weight Uom Id</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>
  <tr>
    <%fieldName = "weight";%><%paramName = "WEIGHT";%>    
    <td width="26%" align=right><div class="tabletext">Weight</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(product.getDouble(fieldName)):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
  </tr>

  <tr>
    <%fieldName = "taxable";%><%paramName = "TAXABLE";%>    
    <td width="26%" align=right><div class="tabletext">Taxable?</div></td>
    <td>&nbsp;</td>
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
    <%fieldName = "autoCreateKeywords";%><%paramName = "AUTO_CREATE_KEYWORDS";%>
    <td width="26%" align=right><div class="tabletext">Allow Auto Create Keywords?</div></td>
    <td>&nbsp;</td>
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
    <td colspan='3'><input type="submit" name="Update" value="Update"></td>
  </tr>
</table>
</form>

<a href="<ofbiz:url>EditProduct</ofbiz:url>" class="buttontext">[Create New Product]</a>
<%-- <%if(product != null){%><a href="<ofbiz:url>UpdateProduct?UPDATE_MODE=DELETE&PRODUCT_ID=<%=product.getSku()%></ofbiz:url>" class="buttontext">[Delete this Product]</a><%}%> --%>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[View Product Page]</a>
  <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Product]</a>
  <a href="<ofbiz:url>/EditProductCategories?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Category Members]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Keywords]</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Associations]</a>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
