<%--
 *  Description: None
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski 
 *@version    $Revision$
 *@since      2.0
--%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("FACILITY", "_VIEW", session)) {%>

<%
    //default this to true, ie only show active
    boolean activeOnly = !"false".equals(request.getParameter("activeOnly"));

    boolean useValues = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

	String facilityId = request.getParameter("facilityId"); 
    GenericValue facility = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", facilityId));

	// get the 'to' this facility transfers
	List exprsTo = null;
	if (activeOnly)
		exprsTo = UtilMisc.toList(new EntityExpr("facilityIdTo", EntityOperator.EQUALS, facilityId), new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "IXF_COMPLETE"), new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "IXF_CANCELLED"));
	else
        exprsTo = UtilMisc.toList(new EntityExpr("facilityIdTo", EntityOperator.EQUALS, facilityId));
	List toTransfers = delegator.findByAnd("InventoryTransfer", exprsTo, UtilMisc.toList("sendDate"));
	if (toTransfers != null) pageContext.setAttribute("toTransfers", toTransfers);

	// get the 'from' this facility transfers
	List exprsFrom = null;
	if (activeOnly)
		exprsFrom = UtilMisc.toList(new EntityExpr("facilityId", EntityOperator.EQUALS, facilityId), new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "IXF_COMPLETE"), new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "IXF_CANCELLED"));
    else
		exprsFrom = UtilMisc.toList(new EntityExpr("facilityId", EntityOperator.EQUALS, facilityId));
	List fromTransfers = delegator.findByAnd("InventoryTransfer", exprsFrom, UtilMisc.toList("sendDate"));
	if (fromTransfers != null) pageContext.setAttribute("fromTransfers", fromTransfers);
%>

<br>
<%if(facilityId != null && facilityId.length() > 0){%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacility?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Facility</a>
    <a href="<ofbiz:url>/EditFacilityGroups?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Groups</a>
    <a href="<ofbiz:url>/FindFacilityLocations?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Locations</a>
    <a href="<ofbiz:url>/EditFacilityRoles?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Roles</a>
    <a href="<ofbiz:url>/EditFacilityInventoryItems?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Inventory&nbsp;Items</a>
    <a href="<ofbiz:url>/FindFacilityTransfers?facilityId=<%=facilityId%></ofbiz:url>" class="tabButtonSelected">Inventory&nbsp;Xfers</a>
  </div>
<%}%>

<div class="head1">Inventory Transfers <span class='head2'>for&nbsp;<%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span></div>
<a href="<ofbiz:url>/EditFacility</ofbiz:url>" class="buttontext">[New Facility]</a>
<%if (activeOnly) {%>
  <a href="<ofbiz:url>/FindFacilityTransfers?facilityId=<%=facilityId%>&activeOnly=false</ofbiz:url>" class="buttontext">[Active and Inactive]</a>
<%} else {%>
  <a href="<ofbiz:url>/FindFacilityTransfers?facilityId=<%=facilityId%>&activeOnly=true</ofbiz:url>" class="buttontext">[Active Only]</a>
<%}%>
<a href="<ofbiz:url>/TransferInventoryItem?facilityId=<%=facilityId%></ofbiz:url>" class="buttontext">[Inventory Transfer]</a>

<br>
<ofbiz:if name="toTransfers" size="0">
  <br>
  <div class="head1">To:<span class='head2'>&nbsp;<%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span></div>
  <table border="1" cellpadding='2' cellspacing='0' width='100%'>
    <tr>
      <td><div class="tabletext"><b>Transfer ID</b></div></td>
      <td><div class="tabletext"><b>Item</b></div></td>      
      <td><div class="tabletext"><b>From</b></div></td>
      <td><div class="tabletext"><b>Send Date</b></div></td>
      <td><div class="tabletext"><b>Status</b></div></td>
      <td>&nbsp;</td>
    </tr>
   
    <ofbiz:iterator name="transfer" property="toTransfers">
    <tr>
      <td><div class="tabletext"><a href='<ofbiz:url>/EditInventoryTransfer?inventoryTransferId=<ofbiz:inputvalue entityAttr="transfer" field="inventoryTransferId"/></ofbiz:url>' class='buttontext'>&nbsp;<ofbiz:inputvalue entityAttr="transfer" field="inventoryTransferId"/></a></div></td>
      <td><div class="tabletext"><a href='<ofbiz:url>/EditInventoryItem?inventoryItemId=<ofbiz:inputvalue entityAttr="transfer" field="inventoryItemId"/></ofbiz:url>' class='buttontext'>&nbsp;<ofbiz:inputvalue entityAttr="transfer" field="inventoryItemId"/></a></div></td>      
      <td>
        <% GenericValue fac = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", transfer.getString("facilityId"))); %>
        <div class="tabletext"><a href='<ofbiz:url>/EditFacility?facilityId=<ofbiz:inputvalue entityAttr="transfer" field="facilityId"/></ofbiz:url>' class='buttontext'>&nbsp;<%if (fac != null) {%><%=UtilFormatOut.checkNull(fac.getString("facilityName"))%><%}%>&nbsp;[<ofbiz:inputvalue entityAttr="transfer" field="facilityId"/>]</a></div>
      </td>
      <td><div class="tabletext">&nbsp;<ofbiz:inputvalue entityAttr="transfer" field="sendDate"/></div></td>
      <td>
        <% GenericValue stat = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", transfer.getString("statusId"))); %>
        <% if (stat != null) pageContext.setAttribute("transferStatus", stat); %>
        <div class="tabletext">&nbsp;<ofbiz:inputvalue entityAttr="transferStatus" field="description"/></div>
      </td>
      <td align="center"><div class="tabletext"><a href='<ofbiz:url>/TransferInventoryItem?inventoryTransferId=<ofbiz:inputvalue entityAttr="transfer" field="inventoryTransferId"/></ofbiz:url>' class='buttontext'>[Edit]</a></div></td>
    </tr>
    </ofbiz:iterator>
  </table>
</ofbiz:if>

<ofbiz:if name="fromTransfers" size="0">
  <br>
  <div class="head1">From:<span class='head2'>&nbsp;<%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span></div>
  <table border="1" cellpadding='2' cellspacing='0' width='100%'>
    <tr>
      <td><div class="tabletext"><b>Transfer ID</b></div></td>
      <td><div class="tabletext"><b>Item</b></div></td>      
      <td><div class="tabletext"><b>To</b></div></td>
      <td><div class="tabletext"><b>Send Date</b></div></td>
      <td><div class="tabletext"><b>Status</b></div></td>
      <td>&nbsp;</td>
    </tr>
   
    <ofbiz:iterator name="transfer" property="fromTransfers">
    <tr>
      <td><div class="tabletext"><a href='<ofbiz:url>/TransferInventoryItem?inventoryTransferId=<ofbiz:inputvalue entityAttr="transfer" field="inventoryTransferId"/></ofbiz:url>' class='buttontext'>&nbsp;<ofbiz:inputvalue entityAttr="transfer" field="inventoryTransferId"/></a></div></td>
      <td><div class="tabletext"><a href='<ofbiz:url>/EditInventoryItem?inventoryItemId=<ofbiz:inputvalue entityAttr="transfer" field="inventoryItemId"/></ofbiz:url>' class='buttontext'>&nbsp;<ofbiz:inputvalue entityAttr="transfer" field="inventoryItemId"/></a></div></td>      
      <td>
        <% GenericValue fac = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", transfer.getString("facilityIdTo"))); %>
        <div class="tabletext"><a href='<ofbiz:url>/EditFacility?facilityId=<ofbiz:inputvalue entityAttr="transfer" field="facilityIdTo"/></ofbiz:url>' class='buttontext'>&nbsp;<%if (fac != null) {%><%=UtilFormatOut.checkNull(fac.getString("facilityName"))%><%}%>&nbsp;[<ofbiz:inputvalue entityAttr="transfer" field="facilityIdTo"/>]</a></div>
      </td>
      <td><div class="tabletext">&nbsp;<ofbiz:inputvalue entityAttr="transfer" field="sendDate"/></div></td>
      <td>
        <% GenericValue stat = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", transfer.getString("statusId"))); %>
        <% if (stat != null) pageContext.setAttribute("transferStatus", stat); %>
        <div class="tabletext">&nbsp;<ofbiz:inputvalue entityAttr="transferStatus" field="description"/></div>
      </td>
      <td align="center"><div class="tabletext"><a href='<ofbiz:url>/TransferInventoryItem?inventoryTransferId=<ofbiz:inputvalue entityAttr="transfer" field="inventoryTransferId"/></ofbiz:url>' class='buttontext'>[Edit]</a></div></td>
    </tr>
    </ofbiz:iterator>
  </table>
</ofbiz:if>
     
<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
<%}%>
