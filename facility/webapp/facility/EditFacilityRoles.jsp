<%--
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

<%if (security.hasEntityPermission("FACILITY", "_VIEW", session)) {%>
<%
	String facilityId = request.getParameter("facilityId");
	GenericValue facility = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", facilityId));

	List facilityRoles = delegator.findByAnd("FacilityRole", UtilMisc.toMap("facilityId", facilityId));
	if (facilityRoles != null) pageContext.setAttribute("facilityRoles", facilityRoles);

	List roles = delegator.findAll("RoleType", UtilMisc.toList("description", "roleTypeId"));
    if (roles != null) pageContext.setAttribute("roles", roles);
%>

<br>
<%if(facilityId != null && facilityId.length() > 0){%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacility?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Facility</a>
    <a href="<ofbiz:url>/EditFacilityGroups?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Groups</a>
    <a href="<ofbiz:url>/FindFacilityLocations?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Locations</a>
    <a href="<ofbiz:url>/EditFacilityRoles?facilityId=<%=facilityId%></ofbiz:url>" class="tabButtonSelected">Roles</a>
    <a href="<ofbiz:url>/EditFacilityInventoryItems?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">InventoryItems</a>
  </div>
<%}%>

<div class="head1">Roles <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span></div>
<a href="<ofbiz:url>/EditFacility</ofbiz:url>" class="buttontext">[New Facility]</a>
<br>
<br>

<p class="head2">Facility Role Member Maintenance</p>
<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Party ID</b></div></td>
    <td><div class="tabletext"><b>Role Type ID</b></div></td>  
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>

<ofbiz:iterator name="facilityRole" property="facilityRoles">  
  <tr valign="middle">
    <td><a href='/partymgr/control/viewprofile?party_id=<ofbiz:inputvalue entityAttr="facilityRole" field="partyId"/>' class="buttontext"><ofbiz:inputvalue entityAttr="facilityRole" field="partyId"/></a></td>    
    <td><div class="tabletext"><ofbiz:inputvalue entityAttr="facilityRole" field="roleTypeId"/></div></td>    
    <td align="center">
      <a href='<ofbiz:url>/removePartyFromFacility?facilityId=<ofbiz:entityfield attribute="facilityRole" field="facilityId"/>&partyId=<ofbiz:entityfield attribute="facilityRole" field="partyId"/>&roleTypeId=<ofbiz:entityfield attribute="facilityRole" field="roleTypeId"/></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>

<br>
<form method="POST" action="<ofbiz:url>/addPartyToFacility</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="facilityId" value="<%=facilityId%>">  
  <div class='head2'>Add Facility Party Role:</div>
  <div class='tabletext'>
    Party ID: <input type='text' size='20' name='partyId'>
    Role Type ID:
    <select name='roleTypeId'><option></option>
	  <ofbiz:iterator name="role" property="roles">
	    <option value="<ofbiz:entityfield attribute="role" field="roleTypeId"/>"><ofbiz:entityfield attribute="role" field="description"/></option>
      </ofbiz:iterator>
    </select>
    <input type="submit" value="Add">
  </div>
</form>

<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
<%}%>