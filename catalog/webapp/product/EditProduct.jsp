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
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String productId = request.getParameter("productId");
    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
    if(product == null) tryEntity = false;

    Collection categoryCol = delegator.findAll("ProductCategory", UtilMisc.toList("description"));
    Collection productTypeCol = delegator.findAll("ProductType", UtilMisc.toList("description"));

    GenericValue primaryProductCategory = null;
    String primProdCatIdParam = request.getParameter("primaryProductCategoryId");
    if(product != null && tryEntity) {
        primaryProductCategory = product.getRelatedOne("PrimaryProductCategory");
    } else if(primProdCatIdParam != null && primProdCatIdParam.length() > 0) {
        primaryProductCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", primProdCatIdParam));
    }

    GenericValue productType = null;
    String productTypeIdParam = request.getParameter("productTypeId");
    if(product != null && tryEntity) {
        productType = product.getRelatedOne("ProductType");
        pageContext.setAttribute("product", product);
    } else if(productTypeIdParam != null && productTypeIdParam.length() > 0) {
        productType = delegator.findByPrimaryKey("ProductType", UtilMisc.toMap("productTypeId", productTypeIdParam));
    }

    if("true".equalsIgnoreCase((String)request.getParameter("tryEntity"))) tryEntity = true;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));
%>

<br>
<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="buttontextdisabled">[Product]</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="buttontext">[Categories]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Keywords]</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Associations]</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Attributes]</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="buttontext">[Features]</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="buttontext">[InventoryItems]</a>
<%}%>

<div class="head1">Product with ID "<%=UtilFormatOut.checkNull(productId)%>"</div>

<%if(product == null){%>
  <%if(productId != null){%>
    <h3>Could not find product with ID "<%=productId%>".</h3>
    <form action="<ofbiz:url>/createProduct</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext"><b>Product ID</b></div></td>
      <td>&nbsp;</td>
      <td width="74%" colspan='5'>
        <input type="text" name="productId" size="20" maxlength="20" value="<%=productId%>">
      </td>
    </tr>
  <%}else{%>
    <form action="<ofbiz:url>/createProduct</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext"><b>Product ID</b></div></td>
      <td>&nbsp;</td>
      <td width="74%" colspan='5'>
        <input type="text" name="productId" size="20" maxlength="20" value="">
      </td>
    </tr>
  <%}%>
<%}else{%>
  <form action="<ofbiz:url>/updateProduct</ofbiz:url>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="productId" value="<%=productId%>">
  <tr>
    <td align=right><div class="tabletext"><b>Product ID</b></div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'>
      <b><%=productId%></b> (This cannot be changed without re-creating the product.)
    </td>
  </tr>
<%}%>

  <tr>
    <td width="26%" align=right><div class="tabletext"><b>Is VIRTUAL Product?</b></div></td>
    <td>&nbsp;</td>
    <td width="24%">
      <SELECT name='autoCreateKeywords'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='isVirtual' tryEntityAttr="tryEntity" default="N"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>
    <td width="26%" align=right><div class="tabletext"><b>Is VARIANT Product?</b></div></td>
    <td>&nbsp;</td>
    <td width="24%">
      <SELECT name='autoCreateKeywords'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='isVariant' tryEntityAttr="tryEntity" default="N"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>
  </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext">Product Type Id</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'>
      <select name="productTypeId" size=1>
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
    <td width="26%" align=right><div class="tabletext">Primary Category Id</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'>
      <select name="primaryProductCategoryId" size=1>
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
    <td width="26%" align=right><div class="tabletext">Manufacturer Party Id</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'><input type="text" <ofbiz:inputvalue entityAttr='product' field='manufacturerPartyId' tryEntityAttr="tryEntity" fullattrs="true"/> size="20" maxlength="20"></td>
  </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext">Introduction Date</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'><input type="text" <ofbiz:inputvalue entityAttr='product' field='introductionDate' tryEntityAttr="tryEntity" fullattrs="true"/> size="20" maxlength="20">(yyyy-mm-dd hh:MM:ss)</td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Sales Discontinuation Date</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'><input type="text" <ofbiz:inputvalue entityAttr='product' field='salesDiscontinuationDate' tryEntityAttr="tryEntity" fullattrs="true"/> size="20" maxlength="20">(yyyy-mm-dd hh:MM:ss)</td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Support Discontinuation Date</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'><input type="text" <ofbiz:inputvalue entityAttr='product' field='supportDiscontinuationDate' tryEntityAttr="tryEntity" fullattrs="true"/> size="20" maxlength="20">(yyyy-mm-dd hh:MM:ss)</td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Comment</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'><input type="text" <ofbiz:inputvalue entityAttr='product' field='comments' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255"></td>
  </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext">Product Name</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'><input type="text" <ofbiz:inputvalue entityAttr='product' field='productName' tryEntityAttr="tryEntity" fullattrs="true"/> size="30" maxlength="60"></td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Internal Name</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'><input type="text" <ofbiz:inputvalue entityAttr='product' field='internalName' tryEntityAttr="tryEntity" fullattrs="true"/> size="30" maxlength="60"></td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Description</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'><textarea cols="60" rows="2" name="description" maxlength="255"><ofbiz:inputvalue entityAttr='product' field='description' tryEntityAttr="tryEntity"/></textarea></td>
  </tr>
  <tr>
    <td width="26%" align=right valign=top><div class="tabletext">Long Description</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'><textarea cols="60" rows="5" name="longDescription" maxlength="2000"><ofbiz:inputvalue entityAttr='product' field='longDescription' tryEntityAttr="tryEntity"/></textarea></td>
  </tr>

  <tr>
    <td width="26%" align=right valign=top><div class="tabletext">Small Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='smallImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255">
      <%if(productId != null && productId.length() > 0) {%><p><a href="<ofbiz:url>/UploadImage?upload_file_type=small&PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Upload Small Image]</a><%}%>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right valign=top><div class="tabletext">Medium Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='mediumImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255">
      <%-- <%if(productId != null && productId.length() > 0) {%><p><a href="<ofbiz:url>/UploadImage?upload_file_type=small&PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Upload Small Image]</a><%}%> --%>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right valign=top><div class="tabletext">Large Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='largeImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255">
      <%if(productId != null && productId.length() > 0) {%><p><a href="<ofbiz:url>/UploadImage?upload_file_type=large&PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Upload Large Image]</a><%}%>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right valign=top><div class="tabletext">Detail Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='5'>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='detailImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255">
      <%-- <%if(productId != null && productId.length() > 0) {%><p><a href="<ofbiz:url>/UploadImage?upload_file_type=small&PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Upload Small Image]</a><%}%> --%>
    </td>
  </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext"><b>List Price</b></div></td>
    <td>&nbsp;</td>
    <td width="24%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='listPrice' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20"></td>
    
    <td width="26%" align=right><div class="tabletext"><b>Taxable?</b></div></td>
    <td>&nbsp;</td>
    <td width="24%">
      <SELECT name='taxable'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='taxable' tryEntityAttr="tryEntity"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext"><b>Default Price</b></div></td>
    <td>&nbsp;</td>
    <td width="24%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='defaultPrice' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20"></td>
    
    <td width="26%" align=right><div class="tabletext"><b>Charge Shipping?</b></div></td>
    <td>&nbsp;</td>
    <td width="24%">
      <SELECT name='chargeShipping'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='chargeShipping' tryEntityAttr="tryEntity"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">&nbsp;</div></td>
    <td>&nbsp;</td>
    <td width="24%">&nbsp;</td>

    <td width="26%" align=right><div class="tabletext"><b>Allow Auto Create Keywords?</b></div></td>
    <td>&nbsp;</td>
    <td width="24%">
      <SELECT name='autoCreateKeywords'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='autoCreateKeywords' tryEntityAttr="tryEntity"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>
  </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext"><b>Quantity Uom Id</b></div></td>
    <td>&nbsp;</td>
    <td width="24%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='quantityUomId' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20"></td>

    <td width="26%" align=right><div class="tabletext"><b>Quantity Included</b></div></td>
    <td>&nbsp;</td>
    <td width="24%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='quantityIncluded' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20"></td>
  </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext"><b>Weight Uom Id</b></div></td>
    <td>&nbsp;</td>
    <td width="24%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='weightUomId' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20"></td>

    <td width="26%" align=right><div class="tabletext"><b>Weight</b></div></td>
    <td>&nbsp;</td>
    <td width="24%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='weight' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20"></td>
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
            <SPAN class='tabletext'>With New ID:</SPAN>&nbsp;<INPUT type=text size='20' maxlength='20' name='productId'>&nbsp;
            <INPUT type=submit value='AutoDuplicate'>
        </form> --%>
        <%if (product != null) {%>
            <form action="<ofbiz:url>/EditProduct</ofbiz:url>" method=POST style='margin: 0;'>
                <%-- <INPUT type=hidden name='productId' value='<%=productId%>'> --%>
                <INPUT type=hidden name='productTypeId' value='<%=UtilFormatOut.checkNull(product.getString("productTypeId"))%>'>
                <INPUT type=hidden name='isVirtual' value='<%=UtilFormatOut.checkNull(product.getString("isVirtual"))%>'>
                <INPUT type=hidden name='isVariant' value='<%=UtilFormatOut.checkNull(product.getString("isVariant"))%>'>
                <INPUT type=hidden name='primaryProductCategoryId' value='<%=UtilFormatOut.checkNull(product.getString("primaryProductCategoryId"))%>'>
                <INPUT type=hidden name='manufacturerPartyId' value='<%=UtilFormatOut.checkNull(product.getString("manufacturerPartyId"))%>'>
                <INPUT type=hidden name='introductionDate' value='<%=UtilFormatOut.checkNull(UtilDateTime.toDateString(product.getDate("introductionDate")))%>'>
                <INPUT type=hidden name='salesDiscontinuationDate' value='<%=UtilFormatOut.checkNull(UtilDateTime.toDateString(product.getDate("salesDiscontinuationDate")))%>'>
                <INPUT type=hidden name='supportDiscontinuationDate' value='<%=UtilFormatOut.checkNull(UtilDateTime.toDateString(product.getDate("supportDiscontinuationDate")))%>'>
                <INPUT type=hidden name='comments' value='<%=UtilFormatOut.checkNull(product.getString("comments"))%>'>
                <INPUT type=hidden name='productName' value='<%=UtilFormatOut.checkNull(product.getString("productName"))%>'>
                <INPUT type=hidden name='internalName' value='<%=UtilFormatOut.checkNull(product.getString("internalName"))%>'>
                <INPUT type=hidden name='description' value='<%=UtilFormatOut.checkNull(product.getString("description"))%>'>
                <INPUT type=hidden name='longDescription' value='<%=UtilFormatOut.checkNull(product.getString("longDescription"))%>'>
                <INPUT type=hidden name='smallImageUrl' value='<%=UtilFormatOut.checkNull(product.getString("smallImageUrl"))%>'>
                <INPUT type=hidden name='mediumImageUrl' value='<%=UtilFormatOut.checkNull(product.getString("smallImageUrl"))%>'>
                <INPUT type=hidden name='largeImageUrl' value='<%=UtilFormatOut.checkNull(product.getString("largeImageUrl"))%>'>
                <INPUT type=hidden name='detailImageUrl' value='<%=UtilFormatOut.checkNull(product.getString("smallImageUrl"))%>'>
                <INPUT type=hidden name='listPrice' value='<%=UtilFormatOut.checkNull(UtilFormatOut.formatQuantity(product.getDouble("listPrice")))%>'>
                <INPUT type=hidden name='defaultPrice' value='<%=UtilFormatOut.checkNull(UtilFormatOut.formatQuantity(product.getDouble("defaultPrice")))%>'>
                <INPUT type=hidden name='quantityUomId' value='<%=UtilFormatOut.checkNull(product.getString("quantityUomId"))%>'>
                <INPUT type=hidden name='quantityIncluded' value='<%=UtilFormatOut.checkNull(UtilFormatOut.formatQuantity(product.getDouble("quantityIncluded")))%>'>
                <INPUT type=hidden name='weightUomId' value='<%=UtilFormatOut.checkNull(product.getString("weightUomId"))%>'>
                <INPUT type=hidden name='weight' value='<%=UtilFormatOut.checkNull(UtilFormatOut.formatQuantity(product.getDouble("weight")))%>'>
                <INPUT type=hidden name='taxable' value='<%=UtilFormatOut.checkNull(product.getString("taxable"))%>'>
                <INPUT type=hidden name='autoCreateKeywords' value='<%=UtilFormatOut.checkNull(product.getString("autoCreateKeywords"))%>'>
                <SPAN class='tabletext'>In New Create Form:</SPAN>&nbsp;
                <INPUT type=submit value='FormDuplicate'>
            </form>
        <%}%>
    <%}%>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
</td><td>&nbsp;&nbsp;</td></tr></table>
