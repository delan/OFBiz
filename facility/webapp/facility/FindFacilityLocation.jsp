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
	String facilityId = request.getParameter("facilityId");
	String lookup = request.getParameter("look_up");
	String itemId = request.getParameter("inventoryItemId");
	if (itemId != null)
		session.setAttribute("inventoryItemId", itemId);
	
	itemId = (String) session.getAttribute("inventoryItemId");
	GenericValue facility = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", facilityId));
	UtilMisc.parametersToAttributes(request);

	List foundLocations = null;
	if (lookup != null) {
		Map paramMap = UtilMisc.getParameterMap(request);
		paramMap.remove("look_up");
		Iterator i = paramMap.keySet().iterator();
		while (i.hasNext()) {
			Object key = i.next();
			String value = (String) paramMap.get(key);
			if (value == null || value.length() == 0)
				paramMap.remove(key);
		}
		foundLocations = delegator.findByAnd("FacilityLocation", paramMap);
		if (foundLocations != null && foundLocations.size() > 0)
			pageContext.setAttribute("foundLocations", foundLocations);
	}
%>

<br>
<%if(facilityId != null && facilityId.length() > 0){%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacility?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Facility</a>
    <a href="<ofbiz:url>/EditFacilityGroups?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Groups</a>
    <a href="<ofbiz:url>/FindFacilityLocations?facilityId=<%=facilityId%></ofbiz:url>" class="tabButtonSelected">Locations</a>
    <a href="<ofbiz:url>/EditFacilityRoles?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Roles</a>
    <a href="<ofbiz:url>/EditFacilityInventoryItems?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Inventory&nbsp;Items</a>
    <a href="<ofbiz:url>/FindFacilityTransfers?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Inventory&nbsp;Xfers</a>
  </div>
<%}%>

<div class="head1">Find Locations <span class='head2'>for&nbsp;<%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span></div>
<a href="<ofbiz:url>/EditFacility</ofbiz:url>" class="buttontext">[New Facility]</a>
<a href="<ofbiz:url>/EditFacilityLocation?facilityId=<%=facilityId%></ofbiz:url>" class="buttontext">[New Facility Location]</a>


  <form action="<ofbiz:url>/FindFacilityLocations</ofbiz:url>" method='GET' style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
      <% if (facilityId == null) { %>
      <tr>
        <td width="26%" align=right><div class="tabletext">Facility</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" value='<ofbiz:print attribute="facilityId"/>' size="19" maxlength="20"></td>
      </tr>
      <%} else { %>
      <input type="hidden" name="facilityId" value="<%=facilityId%>">
      <%}%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Location SeqID</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" name="locationSeqId" value='<ofbiz:print attribute="locationSeqId"/>' size="19" maxlength="20"></td>
      </tr>
      <tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Area</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" name="areaId" value='<ofbiz:print attribute="areaId"/>' size="19" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Aisle</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" name="aisleId" value='<ofbiz:print attribute="aisleId"/>' size="19" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Section</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" name="sectionId" value='<ofbiz:print attribute="sectionId"/>' size="19" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Level</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" name="levelId" value='<ofbiz:print attribute="levelId"/>' size="19" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Position</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" name="positionId" value='<ofbiz:print attribute="positionId"/>' size="19" maxlength="20"></td>
      </tr>     
      
      <tr>
        <td colspan='2'>&nbsp;</td>
        <td colspan='1' align=left><input type="submit" name="look_up" value="Find"></td>
      </tr>
    </table>
  </form>

  <ofbiz:if name="foundLocations">
    <br>
    <span class="head1">Found:&nbsp;</span><span class="head2"><b><%=foundLocations.size()%></b>&nbsp;Location(s) for&nbsp;<%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span>
    <table border='1' cellpadding='2' cellspacing='0'>
      <tr>
        <td><div class="tabletext"><b>Facility</b></div></td>
        <td><div class="tabletext"><b>LocationSeqID</b></div></td>
        <td><div class="tabletext"><b>Area</b></div></td>
        <td><div class="tabletext"><b>Aisle</b></div></td>
        <td><div class="tabletext"><b>Section</b></div></td>
        <td><div class="tabletext"><b>Level</b></div></td>
        <td><div class="tabletext"><b>Position</b></div></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <% if (itemId != null) {%>
        <td>&nbsp;</td>
        <%}%>
      </tr>
      <ofbiz:iterator name="location" property="foundLocations">
      <tr>
        <td><div class="tabletext"><a href='<ofbiz:url>/EditFacility?facilityId=<ofbiz:inputvalue entityAttr="location" field="facilityId"/></ofbiz:url>' class='buttontext'>&nbsp;<ofbiz:inputvalue entityAttr="location" field="facilityId"/></a></div></td>
        <td><div class='tabletext'>&nbsp;<a href='<ofbiz:url>/EditFacilityLocation?facilityId=<%=facilityId%>&locationSeqId=<ofbiz:inputvalue entityAttr="location" field="locationSeqId"/></ofbiz:url>' class="buttontext"><ofbiz:inputvalue entityAttr="location" field="locationSeqId"/></a></div></td>
        <td><div class="tabletext">&nbsp;<ofbiz:inputvalue entityAttr="location" field="areaId"/></div></td>
        <td><div class="tabletext">&nbsp;<ofbiz:inputvalue entityAttr="location" field="aisleId"/></div></td>
        <td><div class="tabletext">&nbsp;<ofbiz:inputvalue entityAttr="location" field="sectionId"/></div></td>
        <td><div class="tabletext">&nbsp;<ofbiz:inputvalue entityAttr="location" field="levelId"/></div></td>
        <td><div class="tabletext">&nbsp;<ofbiz:inputvalue entityAttr="location" field="positionId"/></div></td>       
        <td>
          <a href='<ofbiz:url>/EditInventoryItem?facilityId=<ofbiz:inputvalue entityAttr="location" field="facilityId"/>&locationSeqId=<ofbiz:inputvalue entityAttr="location" field="locationSeqId"/></ofbiz:url>' class='buttontext'>[New Inventory Item]</a>
        </td>
        <% if (itemId != null) {%>
        <td>
          <a href='<ofbiz:url>/UpdateInventoryItem?inventoryItemId=<%=itemId%>&facilityId=<%=facilityId%>&locationSeqId=<ofbiz:inputvalue entityAttr="location" field="locationSeqId"/></ofbiz:url>' class='buttontext'>[Set Item #<%=itemId%>]</a>
        </td>
        <%}%>   
        <td>          
          <a href='<ofbiz:url>/EditFacilityLocation?facilityId=<ofbiz:inputvalue entityAttr="location" field="facilityId"/>&locationSeqId=<ofbiz:inputvalue entityAttr="location" field="locationSeqId"/></ofbiz:url>' class='buttontext'>[Edit]</a>
        </td>     
      </tr>
      </ofbiz:iterator>
    </table>
  </ofbiz:if>

<%} else {%>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
<%}%>
