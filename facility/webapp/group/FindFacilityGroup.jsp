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
    //facilities
    Collection groups = delegator.findAll("FacilityGroup");
    if (groups != null) pageContext.setAttribute("facilityGroups", groups);
%>
<br>

<div class="head1">Facility Group List</div>

<div><a href='<ofbiz:url>/EditFacilityGroup</ofbiz:url>' class="buttontext">[New Group]</a></div>
<br>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Facility&nbsp;Group&nbsp;Name&nbsp;[ID]</b></div></td>
    <td><div class="tabletext"><b>Facility&nbsp;Group&nbsp;Type</b></div></td>   
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="facilityGroup" property="facilityGroups">
  <%if (!facilityGroup.getString("facilityGroupId").equals("_NA_")) { %>
  <%GenericValue facilityGroupType = facilityGroup.getRelatedOne("FacilityGroupType");%>
  <%if (facilityGroupType != null) pageContext.setAttribute("facilityGroupType", facilityGroupType);%>
  <tr valign="middle">
    <td><div class='tabletext'>&nbsp;<a href='<ofbiz:url>/EditFacilityGroup?facilityGroupId=<ofbiz:inputvalue entityAttr="facilityGroup" field="facilityGroupId"/></ofbiz:url>' class="buttontext"><ofbiz:inputvalue entityAttr="facilityGroup" field="facilityGroupName"/> [<ofbiz:inputvalue entityAttr="facilityGroup" field="facilityGroupId"/>]</a></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="facilityGroupType" field="description"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="facilityGroup" field="description"/></div></td>
    <td>
      <a href='<ofbiz:url>/EditFacilityGroup?facilityGroupId=<ofbiz:inputvalue entityAttr="facilityGroup" field="facilityGroupId"/></ofbiz:url>' class="buttontext">
      [Edit]</a>
    </td>
  <%}%>
  </tr>
</ofbiz:iterator>
</table>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
<%}%>
