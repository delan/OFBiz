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
 *@created    April 4, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    String productId = request.getParameter("productId");
    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
    Collection productInventoryItems = delegator.findByAnd("InventoryItem", 
            UtilMisc.toMap("productId", productId), 
            UtilMisc.toList("statusId", "quantityOnHand", "serialNumber"));
    if (productInventoryItems != null) pageContext.setAttribute("productInventoryItems", productInventoryItems);
%>

<%if (productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButton">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductContent?productId=<%=productId%></ofbiz:url>" class="tabButton">Content</a>
  <a href="<ofbiz:url>/EditProductGoodIdentifications?productId=<%=productId%></ofbiz:url>" class="tabButton">IDs</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButton">Features</a>
  <a href="<ofbiz:url>/EditProductFacilities?productId=<%=productId%></ofbiz:url>" class="tabButton">Facilities</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">Inventory</a>
  <a href="<ofbiz:url>/EditProductGlAccounts?productId=<%=productId%></ofbiz:url>" class="tabButton">Accounts</a>
  <%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <a href="<ofbiz:url>/QuickAddVariants?productId=<%=productId%></ofbiz:url>" class="tabButton">Variants</a>
  <%}%>
  </div>
<%}%>

<div class="head1">Inventory Items <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
  <a href='/facility/control/EditInventoryItem?productId=<%=productId%>' class="buttontext">[Create New Inventory Item for this Product]</a>
<%}%>
<br>


<%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <div class='head3'>WARNING: This is a Virtual product and generally should not have inventory items associated with it.</div>
<%}%>

<br>
<%if (productId != null){%>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Item&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>Item&nbsp;Type</b></div></td>
    <td><div class="tabletext"><b>Status</b></div></td>
    <td><div class="tabletext"><b>Received</b></div></td>
    <td><div class="tabletext"><b>Expire</b></div></td>
    <td><div class="tabletext"><b>Facility or Container ID</b></div></td>
    <td><div class="tabletext"><b>Lot&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>BinNum</b></div></td>
    <td><div class="tabletext"><b>ATP/QOH or Serial#</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="inventoryItem" property="productInventoryItems">
  <%GenericValue curInventoryItemType = inventoryItem.getRelatedOne("InventoryItemType");%>
  <%GenericValue curStatusItem = inventoryItem.getRelatedOneCache("StatusItem");%>
  <%if (curInventoryItemType != null) pageContext.setAttribute("curInventoryItemType", curInventoryItemType);%>
  <%boolean isQuantity = inventoryItem.get("quantityOnHand") != null && (inventoryItem.get("serialNumber") == null || inventoryItem.getString("serialNumber").length() == 0);%>
  <tr valign="middle">
    <td><a href='/facility/control/EditInventoryItem?inventoryItemId=<ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemId"/>' class="buttontext"><ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemId"/></a></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="curInventoryItemType" field="description"/></div></td>
    <td><div class='tabletext'>&nbsp;<%if (curStatusItem != null) {%><%=curStatusItem.getString("description")%><%} else {%><ofbiz:entityfield attribute="inventoryItem" field="statusId" prefix="[" suffix="]"/><%}%></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="datetimeReceived"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="expireDate"/></div></td>
    <%if (UtilValidate.isNotEmpty(inventoryItem.getString("facilityId")) && UtilValidate.isNotEmpty(inventoryItem.getString("containerId"))) {%>
        <td><div class='tabletext' style='color: red;'>Error: facility (<ofbiz:entityfield attribute="inventoryItem" field="facilityId"/>) 
            AND container (<ofbiz:entityfield attribute="inventoryItem" field="containerId"/>) specified</div></td>
    <%} else if(UtilValidate.isNotEmpty(inventoryItem.getString("facilityId"))) {%>
        <td><span class='tabletext'>F:&nbsp;</span><a href='/facility/control/EditFacility?facilityId=<ofbiz:entityfield attribute="inventoryItem" field="facilityId"/>' class='buttontext'>
            <ofbiz:entityfield attribute="inventoryItem" field="facilityId"/></a></td>
    <%} else if(UtilValidate.isNotEmpty(inventoryItem.getString("containerId"))) {%>
        <td><span class='tabletext'>C:&nbsp;</span><a href='<ofbiz:url>/EditContainer?containerId=<ofbiz:entityfield attribute="inventoryItem" field="containerId"/></ofbiz:url>' class='buttontext'>
            <ofbiz:entityfield attribute="inventoryItem" field="containerId"/></a></td>
    <%} else {%>
        <td>&nbsp;</td>
    <%}%>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="lotId"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="binNumber"/></div></td>
    <%if ("NON_SERIAL_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>
        <td>
        <%-- Don't want to allow this here, manual inventory level adjustments should be logged, etc --%>
        <%-- <FORM method=POST action='<ofbiz:url>/UpdateInventoryItem</ofbiz:url>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemTypeId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="productId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="partyId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="statusId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="facilityId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="containerId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="lotId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="inventoryItem" field="uomId" fullattrs="true"/>>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="inventoryItem" field="availableToPromise" fullattrs="true"/>>
            / <input type=text size='5' <ofbiz:inputvalue entityAttr="inventoryItem" field="quantityOnHand" fullattrs="true"/>>
            <INPUT type=submit value='Set ATP/QOH'>
        </FORM> --%>
            <div class='tabletext'><ofbiz:inputvalue entityAttr="inventoryItem" field="availableToPromise" fullattrs="false"/>
            / <ofbiz:inputvalue entityAttr="inventoryItem" field="quantityOnHand" fullattrs="false"/></div>
        </td>
    <%} else if ("SERIALIZED_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>
            <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="serialNumber"/></div></td>
    <%} else {%>
        <td><div class='tabletext' style='color: red;'>Error: type <ofbiz:entityfield attribute="inventoryItem" field="inventoryItemTypeId"/> unknown, 
            serialNumber (<ofbiz:entityfield attribute="inventoryItem" field="serialNumber"/>) 
            AND quantityOnHand (<ofbiz:entityfield attribute="inventoryItem" field="quantityOnHand"/>) specified</div></td>
        <td>&nbsp;</td>
    <%}%>
    <td>
      <a href='/facility/control/EditInventoryItem?inventoryItemId=<ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemId"/>' class="buttontext">
      [Edit]</a>
    </td>
    <td>
      <a href='<ofbiz:url>/DeleteProductInventoryItem?productId=<%=productId%>&inventoryItemId=<ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemId"/></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
