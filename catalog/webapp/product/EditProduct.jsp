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

<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    URL catalogPropertiesURL = application.getResource("/WEB-INF/catalog.properties");

    boolean tryEntity = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
    boolean isCreate = "true".equals(request.getParameter("isCreate"));

    String productId = request.getParameter("productId");
    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
    if (product == null) tryEntity = false;
    if (isCreate && !tryEntity) product = null;

    Collection categoryCol = delegator.findAll("ProductCategory", UtilMisc.toList("description"));
    Collection productTypeCol = delegator.findAll("ProductType", UtilMisc.toList("description"));

    GenericValue primaryProductCategory = null;
    String primProdCatIdParam = request.getParameter("primaryProductCategoryId");
    if (product != null && tryEntity) {
        primaryProductCategory = product.getRelatedOne("PrimaryProductCategory");
    } else if (primProdCatIdParam != null && primProdCatIdParam.length() > 0) {
        primaryProductCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", primProdCatIdParam));
    }

    GenericValue productType = null;
    String productTypeIdParam = request.getParameter("productTypeId");
    if (product != null && tryEntity) {
        productType = product.getRelatedOne("ProductType");
        pageContext.setAttribute("product", product);
    } else if (productTypeIdParam != null && productTypeIdParam.length() > 0) {
        productType = delegator.findByPrimaryKey("ProductType", UtilMisc.toMap("productTypeId", productTypeIdParam));
    }

    if("true".equalsIgnoreCase((String) request.getParameter("tryEntity"))) tryEntity = true;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));
%>

<SCRIPT language='JavaScript'>
function insertNowTimestamp(field) {
  eval('document.productForm.' + field + '.value="<%=UtilDateTime.nowTimestamp().toString()%>";');
};
function insertImageName(size,ext) {
  eval('document.productForm.' + size + 'ImageUrl.value="<%=UtilProperties.getPropertyValue(catalogPropertiesURL, "image.url.prefix")%>/product.<%=productId%>.' + size + '.' + ext + '";');
};
</SCRIPT>
<br>
<%if(productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductContent?productId=<%=productId%></ofbiz:url>" class="tabButton">Content</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButton">Features</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButton">Inventory</a>
  <%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <a href="<ofbiz:url>/QuickAddVariants?productId=<%=productId%></ofbiz:url>" class="tabButton">Variants</a>
  <%}%>
  </div>
<%}%>

<div class="head1">Product <span class='head2'><%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
<%}%>
<br>
<br>

<%if (product == null) {%>
  <%if (productId != null) {%>
    <h3>Could not find product with ID "<%=productId%>".</h3>
    <form action="<ofbiz:url>/createProduct</ofbiz:url>" method=POST style='margin: 0;' name="productForm">
    <input type=HIDDEN name='isCreate' value='true'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext"><b>Product ID</b></div></td>
      <td>&nbsp;</td>
      <td width="80%" colspan='4'>
        <input type="text" name="productId" size="20" maxlength="20" value="<%=productId%>" style='font-size: x-small;'>
      </td>
    </tr>
  <%} else {%>
    <form action="<ofbiz:url>/createProduct</ofbiz:url>" method=POST style='margin: 0;' name="productForm">
    <input type=HIDDEN name='isCreate' value='true'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext"><b>Product ID</b></div></td>
      <td>&nbsp;</td>
      <td width="80%" colspan='4'>
        <input type="text" name="productId" size="20" maxlength="20" value="" style='font-size: x-small;'>
      </td>
    </tr>
  <%}%>
<%}else{%>
  <form action="<ofbiz:url>/updateProduct</ofbiz:url>" method=POST style='margin: 0;' name="productForm">
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="productId" value="<%=productId%>">
  <tr>
    <td align=right><div class="tabletext"><b>Product ID</b></div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'>
      <b><%=productId%></b> (This cannot be changed without re-creating the product.)
    </td>
  </tr>
<%}%>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Is VIRTUAL Product?</b></div></td>
    <td>&nbsp;</td>
    <td width="30%">
      <SELECT name='isVirtual' style='font-size: x-small;'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='isVirtual' tryEntityAttr="tryEntity" default="N"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>
    <td width="20%" align=right><div class="tabletext"><b>Is VARIANT Product?</b></div></td>
    <td>&nbsp;</td>
    <td width="30%">
      <SELECT name='isVariant' style='font-size: x-small;'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='isVariant' tryEntityAttr="tryEntity" default="N"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>
  </tr>

  <tr>
    <td width="20%" align=right><div class="tabletext">Product Type Id</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'>
      <select name="productTypeId" size=1 style='font-size: x-small;'>
        <%if (productType != null) {%>
          <option selected value='<%=productType.getString("productTypeId")%>'><%=productType.getString("description")%><%-- [<%=productType.getString("productTypeId")%>]--%></option>
          <option value='<%=productType.getString("productTypeId")%>'>----</option>
        <%}%>
        <%Iterator productTypeIter = UtilMisc.toIterator(productTypeCol);%>
        <%while (productTypeIter != null && productTypeIter.hasNext()) {%>
          <%GenericValue nextProductType = (GenericValue) productTypeIter.next();%>
          <option value='<%=nextProductType.getString("productTypeId")%>'><%=nextProductType.getString("description")%><%-- [<%=nextProductType.getString("productTypeId")%>]--%></option>
        <%}%>
      </select>
    </td>
  </tr>
  <tr>
    <td width="20%" align=right><div class="tabletext">Primary Category Id</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'>
      <select name="primaryProductCategoryId" size=1 style='font-size: x-small;'>
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
    <td width="20%" align=right><div class="tabletext">Product Name</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'><input type="text" <ofbiz:inputvalue entityAttr='product' field='productName' tryEntityAttr="tryEntity" fullattrs="true"/> size="30" maxlength="60" style='font-size: x-small;'></td>
  </tr>
  <tr>
    <td width="20%" align=right><div class="tabletext">Internal Name</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'><input type="text" <ofbiz:inputvalue entityAttr='product' field='internalName' tryEntityAttr="tryEntity" fullattrs="true"/> size="30" maxlength="60" style='font-size: x-small;'></td>
  </tr>
  <tr>
    <td width="20%" align=right><div class="tabletext">Brand Name</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'><input type="text" <ofbiz:inputvalue entityAttr='product' field='brandName' tryEntityAttr="tryEntity" fullattrs="true"/> size="30" maxlength="60" style='font-size: x-small;'></td>
  </tr>
  <tr>
    <td width="20%" align=right valign=top><div class="tabletext">Description</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'><textarea cols="60" rows="2" name="description" maxlength="250" style='font-size: small;'><ofbiz:inputvalue entityAttr='product' field='description' tryEntityAttr="tryEntity"/></textarea></td>
  </tr>
  <tr>
    <td width="20%" align=right valign=top><div class="tabletext">Long Description</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'><textarea cols="60" rows="7" name="longDescription" maxlength="2000" style='font-size: small;'><ofbiz:inputvalue entityAttr='product' field='longDescription' tryEntityAttr="tryEntity"/></textarea></td>
  </tr>
  <tr>
    <td width="20%" align=right><div class="tabletext">Comment</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'><input type="text" <ofbiz:inputvalue entityAttr='product' field='comments' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255" style='font-size: x-small;'></td>
  </tr>

  <tr><td colspan='6'><hr class="sepbar"></td></tr>

  <tr>
    <%boolean hasntStarted = false;%>
    <%if (product != null && product.getTimestamp("introductionDate") != null && UtilDateTime.nowTimestamp().before(product.getTimestamp("introductionDate"))) { hasntStarted = true; }%>
    <td width="20%" align=right><div class="tabletext"><b>Intro Date</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='introductionDate' tryEntityAttr="tryEntity" fullattrs="true"/> size="22" maxlength="22" style='font-size: x-small;<%if (hasntStarted) {%> color: red;<%}%>'><a href='#' onclick='insertNowTimestamp("introductionDate")' class='buttontext'>[Now]</a></td>

    <%boolean hasExpiredSales = false;%>
    <%if (product != null && product.getTimestamp("salesDiscontinuationDate") != null && UtilDateTime.nowTimestamp().after(product.getTimestamp("salesDiscontinuationDate"))) { hasExpiredSales = true; }%>
    <td width="20%" align=right><div class="tabletext"><b>Sales Thru Date</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='salesDiscontinuationDate' tryEntityAttr="tryEntity" fullattrs="true"/> size="22" maxlength="22" style='font-size: x-small;<%if (hasExpiredSales) {%> color: red;<%}%>'><a href='#' onclick='insertNowTimestamp("salesDiscontinuationDate")' class='buttontext'>[Now]</a></td>
  </tr>
  <tr>
    <%boolean hasExpiredSupport = false;%>
    <%if (product != null && product.getTimestamp("supportDiscontinuationDate") != null && UtilDateTime.nowTimestamp().after(product.getTimestamp("supportDiscontinuationDate"))) { hasExpiredSupport = true; }%>
    <td width="20%" align=right><div class="tabletext"><b>Support Thru Date</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='supportDiscontinuationDate' tryEntityAttr="tryEntity" fullattrs="true"/> size="22" maxlength="22" style='font-size: x-small;<%if (hasExpiredSupport) {%> color: red;<%}%>'><a href='#' onclick='insertNowTimestamp("supportDiscontinuationDate")' class='buttontext'>[Now]</a></td>

    <td width="20%" align=right><div class="tabletext"><b>OEM Party Id</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='manufacturerPartyId' tryEntityAttr="tryEntity" fullattrs="true"/> size="20" maxlength="20" style='font-size: x-small;'></td>
  </tr>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Require Inventory?</b></div></td>
    <td>&nbsp;</td>
    <td width="30%">
        <SELECT name='requireInventory' style='font-size: x-small;'>
            <%
                String reqInvLabel = "Catalog Default";
                if (product != null) {
                    if ("Y".equals(product.getString("requireInventory"))) {
                        reqInvLabel = "Yes";
                    } else if ("N".equals(product.getString("requireInventory"))) {
                        reqInvLabel = "No";
                    }
                }
            %>
            <OPTION value='<ofbiz:inputvalue entityAttr="product" field="requireInventory" tryEntityAttr="tryEntity"/>'><%=reqInvLabel%></OPTION>
            <OPTION value='<ofbiz:inputvalue entityAttr="product" field="requireInventory" tryEntityAttr="tryEntity"/>'>----</OPTION>
            <OPTION value=''>Catalog Default</OPTION>
            <OPTION value='Y'>Yes</OPTION>
            <OPTION value='N'>No</OPTION>
        </SELECT>
    </td>
    <td width="20%" align=right valign=top><div class="tabletext"><b>Inventory Message</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" size="20" maxlength="250" <ofbiz:inputvalue entityAttr='product' field='inventoryMessage' tryEntityAttr="tryEntity" fullattrs="true"/> style='font-size: x-small;'></td>
  </tr>

  <tr><td colspan='6'><hr class="sepbar"></td></tr>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Weight</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='weight' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20" style='font-size: x-small;'></td>

    <td width="20%" align=right><div class="tabletext"><b>Weight Uom Id</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='weightUomId' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20" style='font-size: x-small;'></td>
  </tr>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Quantity Included</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='quantityIncluded' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20" style='font-size: x-small;'></td>

    <td width="20%" align=right><div class="tabletext"><b>Quantity Uom Id</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='quantityUomId' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20" style='font-size: x-small;'></td>
  </tr>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Pieces Included</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='piecesIncluded' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20" style='font-size: x-small;'></td>

    <td width="20%" align=right><div class="tabletext">&nbsp;</div></td>
    <td>&nbsp;</td>
    <td width="30%">&nbsp;</td>
  </tr>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Taxable?</b></div></td>
    <td>&nbsp;</td>
    <td width="30%">
      <SELECT name='taxable' style='font-size: x-small;'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='taxable' tryEntityAttr="tryEntity" default="Y"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>

    <td width="20%" align=right><div class="tabletext"><b>Tax Category</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='taxCategory' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20" style='font-size: x-small;'></td>
  </tr>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Tax VAT Code</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='taxVatCode' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20" style='font-size: x-small;'></td>

    <td width="20%" align=right><div class="tabletext"><b>Tax Duty Code</b></div></td>
    <td>&nbsp;</td>
    <td width="30%"><input type="text" <ofbiz:inputvalue entityAttr='product' field='taxDutyCode' tryEntityAttr="tryEntity" fullattrs="true"/> size="10" maxlength="20" style='font-size: x-small;'></td>
  </tr>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Charge Shipping?</b></div></td>
    <td>&nbsp;</td>
    <td width="30%">
      <SELECT name='chargeShipping' style='font-size: x-small;'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='chargeShipping' tryEntityAttr="tryEntity" default="Y"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>

    <td width="20%" align=right><div class="tabletext"><b>Allow Auto Create Keywords?</b></div></td>
    <td>&nbsp;</td>
    <td width="30%">
      <SELECT name='autoCreateKeywords' style='font-size: x-small;'>
        <OPTION><ofbiz:inputvalue entityAttr='product' field='autoCreateKeywords' tryEntityAttr="tryEntity" default="Y"/></OPTION>
        <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
      </SELECT>
    </td>
  </tr>

  <tr><td colspan='6'><hr class="sepbar"></td></tr>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Content</b></div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'>
        <div class="tabletext">NOTE: For more content options, use the <a href="<ofbiz:url>/EditProductContent?productId=<%=productId%></ofbiz:url>" class="tabButton">Content</a> tab.</div>
    </td>
  </tr>
  <tr>
    <td width="20%" align=right valign=top><div class="tabletext">Small Image URL</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='smallImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255" style='font-size: x-small;'>
    </td>
  </tr>
  <tr>
    <td width="20%" align=right valign=top><div class="tabletext">Medium Image URL</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='mediumImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255" style='font-size: x-small;'>
    </td>
  </tr>
  <tr>
    <td width="20%" align=right valign=top><div class="tabletext">Large Image URL</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='largeImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255" style='font-size: x-small;'>
    </td>
  </tr>
  <tr>
    <td width="20%" align=right valign=top><div class="tabletext">Detail Image URL</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='detailImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255" style='font-size: x-small;'>
    </td>
  </tr>

  <tr>
    <td width="20%" align=right><div class="tabletext">Detail Template</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4'>
        <input type="text" <ofbiz:inputvalue entityAttr='product' field='detailTemplate' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'>
    </td>
  </tr>

  <tr>
    <td colspan='2'>&nbsp;</td>
    <td><input type="submit" value="Update Product" style='font-size: x-small;'></td>
    <td colspan='3'>&nbsp;</td>
  </tr>

  <tr>
    <td width="20%" align=right><div class="tabletext"><b>Last Modified By:</b></div></td>
    <td>&nbsp;</td>
    <td width="30%">
    	<div class='tabletext'>[<ofbiz:inputvalue entityAttr='product' field='lastModifiedByUserLogin'/>] on <ofbiz:inputvalue entityAttr='product' field='lastModifiedDate'/></div>
    </td>

    <td width="20%" align=right><div class="tabletext"><b>Created By:</b></div></td>
    <td>&nbsp;</td>
    <td width="30%">
    	<div class='tabletext'>[<ofbiz:inputvalue entityAttr='product' field='createdByUserLogin'/>] on <ofbiz:inputvalue entityAttr='product' field='createdDate'/></div>
    </td>
  </tr>
</table>

</form>
    <%if (productId != null) {%>
        <hr class='sepbar'>
        <div class="head2">Duplicate Product</div>
        <%if (product != null) {%>
            <form action="<ofbiz:url>/EditProduct</ofbiz:url>" method=POST style='margin: 0;'>
                <%-- <INPUT type=hidden name='productId' value='<%=productId%>'> --%>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='productTypeId' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='isVirtual' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='isVariant' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='primaryProductCategoryId' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='manufacturerPartyId' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='introductionDate' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='salesDiscontinuationDate' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='supportDiscontinuationDate' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='comments' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='productName' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='internalName' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='description' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='longDescription' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='smallImageUrl' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='mediumImageUrl' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='largeImageUrl' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='detailImageUrl' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='quantityUomId' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='quantityIncluded' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='piecesIncluded' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='weightUomId' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='weight' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='taxable' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='taxCategory' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='taxVatCode' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='taxDutyCode' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='chargeShipping' fullattrs="true"/>>
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='autoCreateKeywords' fullattrs="true"/>>
                <SPAN class='tabletext'>Populate New Form:</SPAN>&nbsp;
                <INPUT type=submit value='NewForm' style='font-size: x-small;'>
            </form>
            <hr class='sepbar'>
            <form action="<ofbiz:url>/DuplicateProduct</ofbiz:url>" method=POST style='margin: 0;'>
                <INPUT type=hidden name='oldProductId' value='<%=productId%>'>
                <div>
                    <SPAN class='tabletext'>Duplicate/Remove Selected With New ID:</SPAN>
                    <INPUT type=text size='20' maxlength='20' name='productId' style='font-size: x-small;'>&nbsp;<INPUT type=submit value='Go!' style='font-size: x-small;'>
                </div>
                <div class='tabletext'>
                    <b>Duplicate:</b>
                    Prices&nbsp;<input type=CHECKBOX name='duplicatePrices' value='Y' checked/>
                    CategoryMembers&nbsp;<input type=CHECKBOX name='duplicateCategoryMembers' value='Y' checked/>
                    Assocs&nbsp;<input type=CHECKBOX name='duplicateAssocs' value='Y' checked/>
                    Attributes&nbsp;<input type=CHECKBOX name='duplicateAttributes' value='Y' checked/>
                    FeatureAppls&nbsp;<input type=CHECKBOX name='duplicateFeatureAppls' value='Y' checked/>
                    InventoryItems&nbsp;<input type=CHECKBOX name='duplicateInventoryItems' value='Y' checked/>
                </div>
                <div class='tabletext'>
                    <b>Remove:</b>
                    Prices&nbsp;<input type=CHECKBOX name='removePrices' value='Y'/>
                    CategoryMembers&nbsp;<input type=CHECKBOX name='removeCategoryMembers' value='Y'/>
                    Assocs&nbsp;<input type=CHECKBOX name='removeAssocs' value='Y'/>
                    Attributes&nbsp;<input type=CHECKBOX name='removeAttributes' value='Y'/>
                    FeatureAppls&nbsp;<input type=CHECKBOX name='removeFeatureAppls' value='Y'/>
                    InventoryItems&nbsp;<input type=CHECKBOX name='removeInventoryItems' value='Y'/>
                </div>
            </form>
            <br><br>
        <%}%>
    <%}%>
<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
