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

    String facilityGroupId = request.getParameter("facilityGroupId");
    if (UtilValidate.isEmpty(facilityGroupId) && UtilValidate.isNotEmpty((String) request.getAttribute("facilityGroupId"))) {
        facilityGroupId = (String) request.getAttribute("facilityGroupId");
    }
    GenericValue facilityGroup = delegator.findByPrimaryKey("FacilityGroup", UtilMisc.toMap("facilityGroupId", facilityGroupId));
    GenericValue facilityGroupType = null;
    if(facilityGroup == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("facilityGroup", facilityGroup);

        facilityGroupType = facilityGroup.getRelatedOne("FacilityGroupType");
        if (facilityGroupType != null) pageContext.setAttribute("facilityGroupType", facilityGroupType);
    }

    // Facility Group types
    List facilityGroupTypes = delegator.findAll("FacilityGroupType");
    if (facilityGroupTypes != null) pageContext.setAttribute("facilityGroupTypes", facilityGroupTypes);

    // Facility Groups
    List facilityGroups = delegator.findAll("FacilityGroup");
    if (facilityGroups != null) pageContext.setAttribute("facilityGroups", facilityGroups);
%>

<br>
<%if(facilityGroupId != null && facilityGroupId.length() > 0) {%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacilityGroup?facilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButtonSelected">Facility Group</a>
    <a href="<ofbiz:url>/EditFacilityGroupRollup?showFacilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButton">Rollups</a>
    <a href="<ofbiz:url>/EditFacilityGroupMembers?facilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButton">Facilities</a>
	<a href="<ofbiz:url>/EditFacilityGroupRoles?facilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButton">Roles</a>
  </div>
<%}%>

<div class="head1">Facility Group<span class='head2'>&nbsp;<%=UtilFormatOut.ifNotEmpty(facilityGroup==null?null:facilityGroup.getString("facilityGroupName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityGroupId)%>]</span></div>
<a href="<ofbiz:url>/EditFacilityGroup</ofbiz:url>" class="buttontext">[New Group]</a>

<%if (facilityGroup == null) {%>
  <%if (facilityGroupId != null) {%>
    <form action="<ofbiz:url>/CreateFacilityGroup</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <h3>Could not find facility with ID "<%=facilityGroupId%>".</h3>
  <%} else {%>
    <form action="<ofbiz:url>/CreateFacilityGroup</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
  <%}%>
<%} else {%>
  <form action="<ofbiz:url>/UpdateFacilityGroup</ofbiz:url>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="facilityGroupId" value="<%=facilityGroupId%>">
  <tr>
    <td align=right><div class="tabletext">Facility Group ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=facilityGroupId%></b> (This cannot be changed without re-creating the facility group.)
    </td>
  </tr>
<%}%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Facility Group Type</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <select name="facilityGroupTypeId" size=1>
            <option selected value='<ofbiz:inputvalue entityAttr="facilityGroupType" field="facilityGroupTypeId"/>'><ofbiz:inputvalue entityAttr="facilityGroupType" field="description"/></option>
            <option value='<ofbiz:inputvalue entityAttr="facilityGroupType" field="facilityGroupTypeId"/>'>----</option>
            <ofbiz:iterator name="nextFacilityGroupType" property="facilityGroupTypes">
              <option value='<ofbiz:inputvalue entityAttr="nextFacilityGroupType" field="facilityGroupTypeId"/>'><ofbiz:inputvalue entityAttr="nextFacilityGroupType" field="description"/></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>

      <tr>
        <td width="26%" align=right><div class="tabletext">Primary Parent Group</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <select name="primaryParentGroupId" size=1>
            <%               
               GenericValue currentPrimaryParent = null;
               if (facilityGroup != null)
                   currentPrimaryParent = facilityGroup.getRelatedOne("PrimaryParentFacilityGroup");
            %>
            <%if(currentPrimaryParent != null) pageContext.setAttribute("currentPrimaryParent", currentPrimaryParent);%>
            <option selected value='<ofbiz:inputvalue entityAttr="facilityGroup" field="primaryParentGroupId"/>'><ofbiz:inputvalue entityAttr="currentPrimaryParent" field="description"/></option>
            <option value='<ofbiz:inputvalue entityAttr="facilityGroup" field="primaryParentGroupId"/>'>----</option>
            <ofbiz:iterator name="nextFacilityGroup" property="facilityGroups">
              <%if (!nextFacilityGroup.getString("facilityGroupId").equals("_NA_")) { %>
              <option value='<ofbiz:inputvalue entityAttr="nextFacilityGroup" field="facilityGroupId"/>'><ofbiz:inputvalue entityAttr="nextFacilityGroup" field="facilityGroupName"/></option>
              <%}%>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>

      <tr>
        <td width="26%" align=right><div class="tabletext">Name</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facilityGroup" field="facilityGroupName" fullattrs="true"/> size="30" maxlength="60"></td>
      </tr>    
      <tr>
        <td width="26%" align=right><div class="tabletext">Description</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facilityGroup" field="description" fullattrs="true"/> size="60" maxlength="250"></td>
      </tr>

  <tr>
    <td colspan='2'>&nbsp;</td>
    <td colspan='1' align=left><input type="submit" name="Update" value="Update"></td>
  </tr>
</table>
</form>

<%} else {%>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
<%}%>
<%-- </td><td>&nbsp;&nbsp;</td></tr></table> --%>
