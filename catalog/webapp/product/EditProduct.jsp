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
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*, org.ofbiz.core.widgetimpl.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    String productId = request.getParameter("productId");
    if (UtilValidate.isEmpty(productId)) productId = (String) request.getAttribute("productId");
    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));

	// NOTE: there was a problem with doing creates with an ID that already exists, the existing pruduct popped up instead, which shouldn't happen here but if it does then we may have to do the whole "isCreate" parameter idea
    HtmlFormWrapper productFormWrapper = new HtmlFormWrapper("/product/ProductForms.xml", "EditProduct", request, response);
    productFormWrapper.putInContext("product", product);
    productFormWrapper.putInContext("productId", productId);
%>

<%if(productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductContent?productId=<%=productId%></ofbiz:url>" class="tabButton">Content</a>
  <a href="<ofbiz:url>/EditProductGoodIdentifications?productId=<%=productId%></ofbiz:url>" class="tabButton">IDs</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButton">Features</a>
  <a href="<ofbiz:url>/EditProductFacilities?productId=<%=productId%></ofbiz:url>" class="tabButton">Facilities</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButton">Inventory</a>
  <a href="<ofbiz:url>/EditProductGlAccounts?productId=<%=productId%></ofbiz:url>" class="tabButton">Accounts</a>
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

<%=productFormWrapper.renderFormString()%>

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
                <INPUT type=hidden <ofbiz:inputvalue entityAttr='product' field='brandName' fullattrs="true"/>>
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
                <INPUT type=submit class='standardSubmit' value='NewForm'>
            </form>
            <hr class='sepbar'>
            <form action="<ofbiz:url>/DuplicateProduct</ofbiz:url>" method=POST style='margin: 0;'>
                <INPUT type=hidden name='oldProductId' value='<%=productId%>'>
                <div>
                    <SPAN class='tabletext'>Duplicate/Remove Selected With New ID:</SPAN>
                    <input type="text" class="inputBox" size='20' maxlength='20' name='productId' >&nbsp;<INPUT type=submit class='standardSubmit' value='Go!'>
                </div>
                <div class='tabletext'>
                    <b>Duplicate:</b>
                    Prices&nbsp;<input type='checkbox' class='checkBox' name='duplicatePrices' value='Y' checked/>
                    IDs&nbsp;<input type='checkbox' class='checkBox' name='duplicateIDs' value='Y' checked/>
                    CategoryMembers&nbsp;<input type='checkbox' class='checkBox' name='duplicateCategoryMembers' value='Y' checked/>
                    Assocs&nbsp;<input type='checkbox' class='checkBox' name='duplicateAssocs' value='Y' checked/>
                    Attributes&nbsp;<input type='checkbox' class='checkBox' name='duplicateAttributes' value='Y' checked/>
                    FeatureAppls&nbsp;<input type='checkbox' class='checkBox' name='duplicateFeatureAppls' value='Y' checked/>
                    InventoryItems&nbsp;<input type='checkbox' class='checkBox' name='duplicateInventoryItems' value='Y' checked/>
                </div>
                <div class='tabletext'>
                    <b>Remove:</b>
                    Prices&nbsp;<input type='checkbox' class='checkBox' name='removePrices' value='Y'/>
                    IDs&nbsp;<input type='checkbox' class='checkBox' name='removeIDs' value='Y'/>
                    CategoryMembers&nbsp;<input type='checkbox' class='checkBox' name='removeCategoryMembers' value='Y'/>
                    Assocs&nbsp;<input type='checkbox' class='checkBox' name='removeAssocs' value='Y'/>
                    Attributes&nbsp;<input type='checkbox' class='checkBox' name='removeAttributes' value='Y'/>
                    FeatureAppls&nbsp;<input type='checkbox' class='checkBox' name='removeFeatureAppls' value='Y'/>
                    InventoryItems&nbsp;<input type='checkbox' class='checkBox' name='removeInventoryItems' value='Y'/>
                </div>
            </form>
            <br><br>
        <%}%>
    <%}%>
<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
