<%
    /**
     *  Title: Edit Facility Inventory Items Page
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
     *@created    May 10, 2002
     *@version    1.0
     */
%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    String facilityId = request.getParameter("facilityId");
    Collection facilityInventoryItems = delegator.findByAnd("InventoryItem", 
            UtilMisc.toMap("facilityId", facilityId), 
            UtilMisc.toList("statusId", "quantityOnHand", "serialNumber"));
    if (facilityInventoryItems != null) pageContext.setAttribute("facilityInventoryItems", facilityInventoryItems);
%>
<br>

<a href="<ofbiz:url>/EditFacility</ofbiz:url>" class="buttontext">[New Facility]</a>
<%if(facilityId != null && facilityId.length() > 0){%>
  <a href="<ofbiz:url>/EditFacility?facilityId=<%=facilityId%></ofbiz:url>" class="buttontext">[Facility]</a>
  <a href="<ofbiz:url>/EditFacilityInventoryItems?facilityId=<%=facilityId%></ofbiz:url>" class="buttontextdisabled">[InventoryItems]</a>
<%}%>

<div class="head1">Inventory Items for Facility with ID "<%=UtilFormatOut.checkNull(facilityId)%>"</div>

<br>
<br>
<%if (facilityId != null){%>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Item&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>Item&nbsp;Type</b></div></td>
    <td><div class="tabletext"><b>Status</b></div></td>
    <td><div class="tabletext"><b>Product ID</b></div></td>
    <td><div class="tabletext"><b>Lot&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>ATP/QOH or Serial#</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="inventoryItem" property="facilityInventoryItems">
  <%GenericValue curInventoryItemType = inventoryItem.getRelatedOne("InventoryItemType");%>
  <%if (curInventoryItemType != null) pageContext.setAttribute("curInventoryItemType", curInventoryItemType);%>
  <%boolean isQuantity = inventoryItem.get("quantityOnHand") != null && (inventoryItem.get("serialNumber") == null || inventoryItem.getString("serialNumber").length() == 0);%>
  <tr valign="middle">
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemId"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="curInventoryItemType" field="description"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="statusId"/></div></td>
    <td><a href='<ofbiz:url>/EditProduct?productId=<ofbiz:entityfield attribute="inventoryItem" field="productId"/></ofbiz:url>' class='buttontext'>
        <ofbiz:entityfield attribute="inventoryItem" field="productId"/></a></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="lotId"/></div></td>
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
      <a href='<ofbiz:url>/EditInventoryItem?inventoryItemId=<ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemId"/></ofbiz:url>' class="buttontext">
      [Edit]</a>
    </td>
    <td>
      <a href='<ofbiz:url>/DeleteFacilityInventoryItem?inventoryItemId=<ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemId"/></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<a href='<ofbiz:url>/EditInventoryItem?facilityId=<%=facilityId%></ofbiz:url>' class="buttontext">
[Create New Inventory Item for this Facility]</a>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
