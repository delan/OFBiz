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

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    String defaultCurrencyUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default");
    if (UtilValidate.isEmpty(defaultCurrencyUomId)) defaultCurrencyUomId = "USD";

    boolean useValues = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

    String productId = request.getParameter("productId");
    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
    if (product == null) useValues = false;
    Collection productPrices = product.getRelated("ProductPrice", null, UtilMisc.toList("facilityGroupId", "productPriceTypeId", "currencyUomId", "fromDate"));
    if (productPrices != null) pageContext.setAttribute("productPrices", productPrices);

    Collection productPriceTypes = delegator.findAllCache("ProductPriceType", UtilMisc.toList("description"));
    if (productPriceTypes != null) pageContext.setAttribute("productPriceTypes", productPriceTypes);

    Collection currencyUoms = delegator.findByAndCache("Uom", UtilMisc.toMap("uomTypeId", "CURRENCY_MEASURE"), UtilMisc.toList("description"));
    if (currencyUoms != null) pageContext.setAttribute("currencyUoms", currencyUoms);

    Collection facilityGroups = delegator.findByAndCache("FacilityGroup", null, UtilMisc.toList("facilityGroupName"));
    if (facilityGroups != null) pageContext.setAttribute("facilityGroups", facilityGroups);

    if ("true".equalsIgnoreCase((String)request.getParameter("useValues"))) useValues = true;
%>

<br>
<%if(productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButton">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">Prices</a>
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

<div class="head1">Prices <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
<%}%>
<br>
<br>

<%if(productId!=null && product!=null){%>
<script language='JavaScript'>
    function setLineThruDate(line) { eval('document.lineForm' + line + '.thruDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
</script>
<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Price&nbsp;Type</b></div></td>
    <td><div class="tabletext"><b>Currency</b></div></td>
    <td><div class="tabletext"><b>Facility Group</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Price</b></div></td>
    <td><div class="tabletext"><b>Last Modified By</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<%int line = 0;%>
<ofbiz:iterator name="productPrice" property="productPrices">
  <%line++;%>
  <%GenericValue currencyUom = productPrice.getRelatedOneCache("CurrencyUom");%>
  <%GenericValue productPriceType = productPrice.getRelatedOneCache("ProductPriceType");%>
  <%GenericValue facilityGroup = productPrice.getRelatedOneCache("FacilityGroup");%>
  <tr valign="middle">
    <td><div class='tabletext'><%if (productPriceType != null) {%><%=productPriceType.getString("description")%><%} else {%>[<ofbiz:inputvalue entityAttr="productPrice" field="productPriceTypeId"/>]<%}%></div></td>
    <td><div class='tabletext'><%if (currencyUom != null) {%><%=currencyUom.getString("description")%><%}%> [<ofbiz:inputvalue entityAttr="productPrice" field="currencyUomId"/>]</div></td>
    <td><div class='tabletext'><%if (facilityGroup != null) {%><%=facilityGroup.getString("facilityGroupName")%><%} else {%>[<ofbiz:inputvalue entityAttr="productPrice" field="facilityGroupId"/>]<%}%></div></td>
    <td>
        <%boolean hasntStarted = false;%>
        <%if (productPrice.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(productPrice.getTimestamp("fromDate"))) { hasntStarted = true; }%>
        <div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>>
                <ofbiz:inputvalue entityAttr="productPrice" field="fromDate"/>
        </div>
    </td>
    <td align="center">
        <%boolean hasExpired = false;%>
        <%if (productPrice.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(productPrice.getTimestamp("thruDate"))) { hasExpired = true; }%>
        <FORM method=POST action='<ofbiz:url>/updateProductPrice</ofbiz:url>' name='lineForm<%=line%>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="productPrice" field="productId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productPrice" field="productPriceTypeId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productPrice" field="currencyUomId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productPrice" field="facilityGroupId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productPrice" field="fromDate" fullattrs="true"/>>
            <input type=text size='22' <ofbiz:inputvalue entityAttr="productPrice" field="thruDate" fullattrs="true"/> style='font-size: x-small;<%if (hasExpired) {%> color: red;<%}%>'>
            <a href='#' onclick='setLineThruDate("<%=line%>")' class='buttontext'>[Now]</a>
            <input type=text size='8' <ofbiz:inputvalue entityAttr="productPrice" field="price" fullattrs="true"/> style='font-size: x-small;'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td><div class='tabletext'>[<ofbiz:inputvalue entityAttr='productPrice' field='lastModifiedByUserLogin'/>] on <ofbiz:inputvalue entityAttr='productPrice' field='lastModifiedDate'/></div></td>
    <td align="center">
      <a href='<ofbiz:url>/deleteProductPrice?productId=<ofbiz:entityfield attribute="productPrice" field="productId"/>&productPriceTypeId=<ofbiz:entityfield attribute="productPrice" field="productPriceTypeId"/>&currencyUomId=<ofbiz:entityfield attribute="productPrice" field="currencyUomId"/>&facilityGroupId=<ofbiz:entityfield attribute="productPrice" field="facilityGroupId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(productPrice.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/createProductPrice</ofbiz:url>" style='margin: 0;' name='createProductPriceForm'>
    <input type="hidden" name="productId" value="<%=productId%>">
    <input type="hidden" name="useValues" value="true">

    <script language='JavaScript'>
      function setPpcFromDate() { document.createProductPriceForm.fromDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"; }
    </script>
    <div class='head2'>Add Price:</div>
    <div class='tabletext'>
        Price Type:
        <select name="productPriceTypeId" style='font-size: x-small;'>
            <ofbiz:iterator name="productPriceType" property="productPriceTypes">
                <option value='<ofbiz:entityfield attribute="productPriceType" field="productPriceTypeId"/>'><ofbiz:entityfield attribute="productPriceType" field="description"/><%--[<ofbiz:entityfield attribute="productPriceType" field="productPriceTypeId"/>]--%></option>
            </ofbiz:iterator>
        </select>
        Currency:
        <select name="currencyUomId" style='font-size: x-small;'>
            <ofbiz:iterator name="currencyUom" property="currencyUoms">
                <%boolean isDefault = defaultCurrencyUomId.equals(currencyUom.getString("uomId"));%>
                <option value='<ofbiz:entityfield attribute="currencyUom" field="uomId"/>' <%if (isDefault) {%>selected<%}%>><ofbiz:entityfield attribute="currencyUom" field="description"/> [<ofbiz:entityfield attribute="currencyUom" field="uomId"/>]</option>
            </ofbiz:iterator>
        </select>
        Facility Group:
        <select name="facilityGroupId" style='font-size: x-small;'>
            <ofbiz:iterator name="facilityGroup" property="facilityGroups">
                <%boolean isDefault = "_NA_".equals(facilityGroup.getString("facilityGroupId"));%>
                <option value='<ofbiz:entityfield attribute="facilityGroup" field="facilityGroupId"/>' <%if (isDefault) {%>selected<%}%>><ofbiz:entityfield attribute="facilityGroup" field="facilityGroupName"/><%-- [<ofbiz:entityfield attribute="facilityGroup" field="facilityGroupId"/>]--%></option>
            </ofbiz:iterator>
        </select>
    </div>
    <div class='tabletext'>
        From Date: <a href='#' onclick='setPpcFromDate()' class='buttontext'>[Now]</a> <input type=text size='22' name='fromDate' style='font-size: x-small;'>
        Price: <input type=text size='8' name='price' style='font-size: x-small;'>&nbsp;<input type="submit" value="Add" style='font-size: x-small;'>
    </div>

</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
