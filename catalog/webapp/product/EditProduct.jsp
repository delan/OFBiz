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

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<table cellpadding=0 cellspacing=0 border=0 width="100%"><tr><td>&nbsp;&nbsp;</td><td>

<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
  boolean useValues = true;
  if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

  String productId = request.getParameter("PRODUCT_ID");
  GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
  if(product == null) useValues = false;

  Collection categoryCol = delegator.findAll("ProductCategory", UtilMisc.toList("description"));
  Collection productTypeCol = delegator.findAll("ProductType", UtilMisc.toList("description"));

  GenericValue primaryProductCategory = null;
  String primProdCatIdParam = request.getParameter("PRIMARY_PRODUCT_CATEGORY_ID");
  if(product != null && useValues) {
    primaryProductCategory = product.getRelatedOne("PrimaryProductCategory");
  } else if(primProdCatIdParam != null && primProdCatIdParam.length() > 0) {
    primaryProductCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", primProdCatIdParam));
  }

  GenericValue productType = null;
  String productTypeIdParam = request.getParameter("productTypeId");
  if(product != null && useValues) {
    productType = product.getRelatedOne("ProductType");
  } else if(productTypeIdParam != null && productTypeIdParam.length() > 0) {
    productType = delegator.findByPrimaryKey("ProductType", UtilMisc.toMap("productTypeId", productTypeIdParam));
  }

  if("true".equalsIgnoreCase((String)request.getParameter("useValues"))) useValues = true;
%>

<br>
<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
  <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontextdisabled">[Product]</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="buttontext">[Categories]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Keywords]</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Associations]</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Attributes]</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="buttontext">[Features]</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="buttontext">[InventoryItems]</a>
<%}%>

<div class="head1">Product with ID "<%=UtilFormatOut.checkNull(productId)%>"</div>

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
        <input type="text" name="PRODUCT_ID" size="20" maxlength="20" value="<%=productId%>">
      </td>
    </tr>
  <%}else{%>
    <input type=hidden name="UPDATE_MODE" value="CREATE">
    <tr>
      <td align=right><div class="tabletext">Product ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="PRODUCT_ID" size="20" maxlength="20" value="">
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
    <%fieldName = "productTypeId";%><%paramName = "productTypeId";%>
    <td width="26%" align=right><div class="tabletext">Product Type Id</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <%-- <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"> --%>
      <select name="<%=paramName%>" size=1>
        <%if(productType != null) {%>
          <option selected value='<%=productType.getString("productTypeId")%>'><%=productType.getString("description")%> [<%=productType.getString("productTypeId")%>]</option>
        <%}%>
        <option value=''>&nbsp;</option>
        <%Iterator productTypeIter = UtilMisc.toIterator(productTypeCol);%>
        <%while(productTypeIter != null && productTypeIter.hasNext()) {%>
          <%GenericValue nextProductType = (GenericValue) productTypeIter.next();%>
          <option value='<%=nextProductType.getString("productTypeId")%>'><%=nextProductType.getString("description")%> [<%=nextProductType.getString("productTypeId")%>]</option>
        <%}%>
      </select>
    </td>
  </tr>
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
    <%fieldName = "comments";%><%paramName = "COMMENT";%>    
    <td width="26%" align=right><div class="tabletext">Comment</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255"></td>
  </tr>

  <tr>
    <%fieldName = "productName";%><%paramName = "NAME";%>    
    <td width="26%" align=right><div class="tabletext">Name</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%>" size="30" maxlength="60"></td>
  </tr>
  <tr>
    <%fieldName = "description";%><%paramName = "DESCRIPTION";%>    
    <td width="26%" align=right><div class="tabletext">Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><textarea cols="60" rows="4" name="<%=paramName%>" maxlength="255"><%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%></textarea></td>
  </tr>
  <tr>
    <%fieldName = "longDescription";%><%paramName = "LONG_DESCRIPTION";%>    
    <td width="26%" align=right valign=top><div class="tabletext">Long Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><textarea cols="60" rows="6" name="<%=paramName%>" maxlength="2000"><%=UtilFormatOut.checkNull(useValues?product.getString(fieldName):request.getParameter(paramName))%></textarea></td>
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
    <%fieldName = "listPrice";%><%paramName = "LIST_PRICE";%>
    <td width="26%" align=right><div class="tabletext">List Price</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(product.getDouble(fieldName)):request.getParameter(paramName))%>" size="20" maxlength="20"></td>
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
    <td colspan='1' align=right><input type="submit" name="Update" value="Update"></td>
    <td colspan='2'>&nbsp;</td>
  </tr>
</table>
</form>
    <%if (productId != null) {%>
        <br>
        <div class="head2">Duplicate Product</div>
        <%-- <form action="<ofbiz:url>/DuplicateProduct</ofbiz:url>" method=POST style='margin: 0;'>
            <INPUT type=hidden name='productId' value='<%=productId%>'>
            <SPAN class='tabletext'>With New ID:</SPAN>&nbsp;<INPUT type=text size='20' maxlength='20' name='PRODUCT_ID'>&nbsp;
            <INPUT type=submit value='AutoDuplicate'>
        </form> --%>
        <%if (product != null) {%>
            <form action="<ofbiz:url>/EditProduct</ofbiz:url>" method=POST style='margin: 0;'>
                <%-- <INPUT type=hidden name='PRODUCT_ID' value='<%=productId%>'> --%>
                <INPUT type=hidden name='productTypeId' value='<%=UtilFormatOut.checkNull(product.getString("productTypeId"))%>'>
                <INPUT type=hidden name='PRIMARY_PRODUCT_CATEGORY_ID' value='<%=UtilFormatOut.checkNull(product.getString("primaryProductCategoryId"))%>'>
                <INPUT type=hidden name='MANUFACTURER_PARTY_ID' value='<%=UtilFormatOut.checkNull(product.getString("manufacturerPartyId"))%>'>
                <INPUT type=hidden name='INTRODUCTION_DATE' value='<%=UtilFormatOut.checkNull(UtilDateTime.toDateString(product.getDate("introductionDate")))%>'>
                <INPUT type=hidden name='SALES_DISCONTINUATION_DATE' value='<%=UtilFormatOut.checkNull(UtilDateTime.toDateString(product.getDate("salesDiscontinuationDate")))%>'>
                <INPUT type=hidden name='SUPPORT_DISCONTINUATION_DATE' value='<%=UtilFormatOut.checkNull(UtilDateTime.toDateString(product.getDate("supportDiscontinuationDate")))%>'>
                <INPUT type=hidden name='COMMENT' value='<%=UtilFormatOut.checkNull(product.getString("comments"))%>'>
                <INPUT type=hidden name='NAME' value='<%=UtilFormatOut.checkNull(product.getString("productName"))%>'>
                <INPUT type=hidden name='DESCRIPTION' value='<%=UtilFormatOut.checkNull(product.getString("description"))%>'>
                <INPUT type=hidden name='LONG_DESCRIPTION' value='<%=UtilFormatOut.checkNull(product.getString("longDescription"))%>'>
                <INPUT type=hidden name='SMALL_IMAGE_URL' value='<%=UtilFormatOut.checkNull(product.getString("smallImageUrl"))%>'>
                <INPUT type=hidden name='LARGE_IMAGE_URL' value='<%=UtilFormatOut.checkNull(product.getString("largeImageUrl"))%>'>
                <INPUT type=hidden name='LIST_PRICE' value='<%=UtilFormatOut.checkNull(UtilFormatOut.formatQuantity(product.getDouble("listPrice")))%>'>
                <INPUT type=hidden name='DEFAULT_PRICE' value='<%=UtilFormatOut.checkNull(UtilFormatOut.formatQuantity(product.getDouble("defaultPrice")))%>'>
                <INPUT type=hidden name='QUANTITY_UOM_ID' value='<%=UtilFormatOut.checkNull(product.getString("quantityUomId"))%>'>
                <INPUT type=hidden name='QUANTITY_INCLUDED' value='<%=UtilFormatOut.checkNull(UtilFormatOut.formatQuantity(product.getDouble("quantityIncluded")))%>'>
                <INPUT type=hidden name='WEIGHT_UOM_ID' value='<%=UtilFormatOut.checkNull(product.getString("weightUomId"))%>'>
                <INPUT type=hidden name='WEIGHT' value='<%=UtilFormatOut.checkNull(UtilFormatOut.formatQuantity(product.getDouble("weight")))%>'>
                <INPUT type=hidden name='TAXABLE' value='<%=UtilFormatOut.checkNull(product.getString("taxable"))%>'>
                <INPUT type=hidden name='AUTO_CREATE_KEYWORDS' value='<%=UtilFormatOut.checkNull(product.getString("autoCreateKeywords"))%>'>
                <SPAN class='tabletext'>In New Create Form:</SPAN>&nbsp;
                <INPUT type=submit value='FormDuplicate'>
            </form>
        <%}%>
    <%}%>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
</td><td>&nbsp;&nbsp;</td></tr></table>
