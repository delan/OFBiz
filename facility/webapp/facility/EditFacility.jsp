<%--
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
 *@created    May 10 2002
 *@version    1.0
--%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%-- <table cellpadding=0 cellspacing=0 border=0 width="100%"><tr><td>&nbsp;&nbsp;</td><td> --%>

<%if(security.hasEntityPermission("FACILITY", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String facilityId = request.getParameter("facilityId");
    if (UtilValidate.isEmpty(facilityId) && UtilValidate.isNotEmpty((String) request.getAttribute("facilityId"))) {
        facilityId = (String) request.getAttribute("facilityId");
    }
    GenericValue facility = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", facilityId));
    GenericValue facilityType = null;
    if(facility == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("facility", facility);

        facilityType = facility.getRelatedOne("FacilityType");
        if (facilityType != null) pageContext.setAttribute("facilityType", facilityType);
    }

    //Facility types
    Collection facilityTypes = delegator.findAll("FacilityType");
    if (facilityTypes != null) pageContext.setAttribute("facilityTypes", facilityTypes);
%>

<br>
<%if(facilityId != null && facilityId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditFacility?facilityId=<%=facilityId%></ofbiz:url>" class="tabButtonSelected">Facility</a>
  <a href="<ofbiz:url>/EditFacilityInventoryItems?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">InventoryItems</a>
  </div>
<%}%>

<div class="head1">Facility <span class='head2'><%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span></div>
<a href="<ofbiz:url>/EditFacility</ofbiz:url>" class="buttontext">[New Facility]</a>

<%if (facility == null) {%>
  <%if (facilityId != null) {%>
    <form action="<ofbiz:url>/CreateFacility</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <h3>Could not find facility with ID "<%=facilityId%>".</h3>
  <%} else {%>
    <form action="<ofbiz:url>/CreateFacility</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
  <%}%>
<%} else {%>
  <form action="<ofbiz:url>/UpdateFacility</ofbiz:url>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="facilityId" value="<%=facilityId%>">
  <tr>
    <td align=right><div class="tabletext">Facility ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=facilityId%></b> (This cannot be changed without re-creating the facility.)
    </td>
  </tr>
<%}%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Facility Type Id</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <%-- <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(tryEntity?facility.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"> --%>
          <select name="facilityTypeId" size=1>
            <option selected value='<ofbiz:inputvalue entityAttr="facilityType" field="facilityTypeId"/>'><ofbiz:inputvalue entityAttr="facilityType" field="description"/> <%--<ofbiz:entityfield attribute="facilityType" field="facilityTypeId" prefix="[" suffix="]"/>--%></option>
            <option value='<ofbiz:inputvalue entityAttr="facilityType" field="facilityTypeId"/>'>----</option>
            <ofbiz:iterator name="nextFacilityType" property="facilityTypes">
              <option value='<ofbiz:inputvalue entityAttr="nextFacilityType" field="facilityTypeId"/>'><ofbiz:inputvalue entityAttr="nextFacilityType" field="description"/> <%--<ofbiz:entityfield attribute="nextFacilityType" field="facilityTypeId" prefix="[" suffix="]"/>--%></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Name</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facility" field="facilityName" fullattrs="true"/> size="30" maxlength="60"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Square Footage</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facility" field="squareFootage" fullattrs="true"/> size="10" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Description</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="facility" field="description" fullattrs="true"/> size="60" maxlength="250"></td>
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
