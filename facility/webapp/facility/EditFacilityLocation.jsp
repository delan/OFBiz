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
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String facilityId = request.getParameter("facilityId");
	String locationSeqId = request.getParameter("locationSeqId");
	GenericValue facility = null;
	GenericValue facilityLocation = null;

    if (UtilValidate.isEmpty(facilityId) && UtilValidate.isNotEmpty((String) request.getAttribute("facilityId"))) {
        facilityId = (String) request.getAttribute("facilityId");
    }
	if (UtilValidate.isEmpty(locationSeqId) && UtilValidate.isNotEmpty((String) request.getAttribute("locationSeqId"))) {
        locationSeqId = (String) request.getAttribute("locationSeqId");
    }

	if (facilityId != null && locationSeqId != null) {
	    facilityLocation = delegator.findByPrimaryKey("FacilityLocation", 
				UtilMisc.toMap("facilityId", facilityId, "locationSeqId", locationSeqId));
	}
	if (facilityId != null) {
		facility = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", facilityId));
	}
    
    if (facilityLocation == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("facilityLocation", facilityLocation);       
    }   
%>

<br>
<%if(facilityId != null && facilityId.length() > 0){%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacility?facilityId=<%=facilityId%></ofbiz:url>" class="tabButtonSelected">Facility</a>
    <a href="<ofbiz:url>/EditFacilityGroups?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Groups</a>
    <a href="<ofbiz:url>/FindFacilityLocations?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Locations</a>
    <a href="<ofbiz:url>/EditFacilityRoles?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Roles</a>
    <a href="<ofbiz:url>/EditFacilityInventoryItems?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">InventoryItems</a>
  </div>
<%}%>

<div class="head1">Location <span class='head2'>for&nbsp;<%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span></div>
<a href="<ofbiz:url>/EditFacilityLocation</ofbiz:url>" class="buttontext">[New Facility Location]</a>
<%if (facilityId != null && locationSeqId != null) {%>
<a href="<ofbiz:url>/EditInventoryItem?facilityId=<%=facilityId%>&locationSeqId=<%=locationSeqId%></ofbiz:url>" class="buttontext">[New Inventory Item]</a>
<%}%>

<%if (facilityId != null && facilityLocation == null) {%> 
    <form action="<ofbiz:url>/CreateFacilityLocation</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
	<input type='hidden' name="facilityId" value="<%=facilityId%>">  
<%} else if (facilityLocation != null) {%>
  <form action="<ofbiz:url>/UpdateFacilityLocation</ofbiz:url>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type='hidden' name="facilityId" value="<%=facilityId%>">
  <input type='hidden' name="locationSeqId" value="<%=locationSeqId%>">
  <tr>
    <td align=right><div class="tabletext">Facility ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=facilityId%></b>
    </td>
  </tr>
  <tr>
    <td align='right'><div class='tabletext'>Location SeqID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=locationSeqId%></b>
    </td>
  </tr>
<%} else { %>
  <div class="head1">Cannot create location without facilityId</div>
<%}%>

<% if (facilityId != null) { %>      
      <tr>
        <td width="26%" align=right><div class="tabletext">Area</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facilityLocation" field="areaId" fullattrs="true"/> size="19" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Aisle</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facilityLocation" field="aisleId" fullattrs="true"/> size="19" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Section</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facilityLocation" field="sectionId" fullattrs="true"/> size="19" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Level</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facilityLocation" field="levelId" fullattrs="true"/> size="19" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Position</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facilityLocation" field="positionId" fullattrs="true"/> size="19" maxlength="20"></td>
      </tr>
   
      <tr>
        <td colspan='2'>&nbsp;</td>
        <td colspan='1' align=left><input type="submit" value="Update"></td>
      </tr>
</table>
</form>
<%}%>

<%} else {%>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
<%}%>
