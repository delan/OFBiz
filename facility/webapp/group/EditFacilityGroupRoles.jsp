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
	String facilityGroupId = request.getParameter("facilityGroupId");
	GenericValue facilityGroup = delegator.findByPrimaryKey("FacilityGroup", UtilMisc.toMap("facilityGroupId", facilityGroupId));

	List facilityRoles = delegator.findByAnd("FacilityGroupRole", UtilMisc.toMap("facilityGroupId", facilityGroupId));
	if (facilityRoles != null) pageContext.setAttribute("facilityRoles", facilityRoles);

	List roles = delegator.findAll("RoleType", UtilMisc.toList("description", "roleTypeId"));
    if (roles != null) pageContext.setAttribute("roles", roles);
%>

<br>
<%@ include file="/includes/facilityGroupMenu.jsp" %>

<div class="head1">Roles <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(facilityGroup==null?null:facilityGroup.getString("description"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityGroupId)%>]</span></div>
<a href="<ofbiz:url>/EditFacilityGroup</ofbiz:url>" class="buttontext">[New Group]</a>
<br>
<br>

<p class="head2">FacilityGroup Role Member Maintenance</p>
<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Party ID</b></div></td>
    <td><div class="tabletext"><b>Role Type ID</b></div></td>  
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>

<ofbiz:iterator name="facilityGroupRole" property="facilityRoles">  
  <tr valign="middle">
    <td><a href='/partymgr/control/viewprofile?partyId=<ofbiz:inputvalue entityAttr="facilityGroupRole" field="partyId"/>' class="buttontext"><ofbiz:inputvalue entityAttr="facilityGroupRole" field="partyId"/></a></td>    
    <td><div class="tabletext"><ofbiz:inputvalue entityAttr="facilityGroupRole" field="roleTypeId"/></div></td>    
    <td align="center">
      <a href='<ofbiz:url>/removePartyFromFacilityGroup?facilityGroupId=<ofbiz:entityfield attribute="facilityGroupRole" field="facilityGroupId"/>&partyId=<ofbiz:entityfield attribute="facilityGroupRole" field="partyId"/>&roleTypeId=<ofbiz:entityfield attribute="facilityGroupRole" field="roleTypeId"/></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>

<br>
<form method="POST" action="<ofbiz:url>/addPartyToFacilityGroup</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="facilityGroupId" value="<%=facilityGroupId%>">  
  <div class='head2'>Add FacilityGroup Party Role:</div>
  <div class='tabletext'>
    Party ID: <input type='text' size='20' name='partyId'>
    From Date:
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