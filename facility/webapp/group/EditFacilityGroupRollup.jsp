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

<%if(security.hasEntityPermission("FACILITY", "_VIEW", request.getSession())) {%>
<%
    boolean useValues = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

	String facilityGroupId = request.getParameter("showFacilityGroupId");
	GenericValue facilityGroup = delegator.findByPrimaryKey("FacilityGroup", UtilMisc.toMap("facilityGroupId", facilityGroupId));
	List parentGroupRollups = null;
	List currentGroupRollups = null;

	if (facilityGroup == null) {
		useValues = false;
	} else {

        parentGroupRollups = facilityGroup.getRelated("ParentFacilityGroupRollup");
        if (parentGroupRollups != null) pageContext.setAttribute("parentGroupRollups", parentGroupRollups);

        currentGroupRollups = facilityGroup.getRelated("CurrentFacilityGroupRollup");
        if (currentGroupRollups != null) pageContext.setAttribute("currentGroupRollups", currentGroupRollups);
    }

	List facilityGroups = delegator.findAll("FacilityGroup", UtilMisc.toList("description"));
	if (facilityGroups != null) pageContext.setAttribute("facilityGroups", facilityGroups);
%>

<script language='JavaScript'>
    function setLineThruDateChild(line) { eval('document.lineChildForm' + line + '.thruDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
    function setLineThruDateParent(line) { eval('document.lineParentForm' + line + '.thruDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
</script>

<br>
<%if(facilityGroupId != null && facilityGroupId.length() > 0) {%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacilityGroup?facilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButton">Facility Group</a>
    <a href="<ofbiz:url>/EditFacilityGroupRollup?showFacilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButtonSelected">Rollups</a>
    <a href="<ofbiz:url>/EditFacilityGroupMembers?facilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButton">Facilities</a>
	<a href="<ofbiz:url>/EditFacilityGroupRoles?facilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButton">Roles</a>
  </div>
<%}%>

<div class="head1">Rollups <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(facilityGroup==null?null:facilityGroup.getString("description"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityGroupId)%>]</span></div>
<a href="<ofbiz:url>/EditFacilityGroup</ofbiz:url>" class="buttontext">[New Group]</a>
<br>
<br>

<%-- Edit 'FacilityGroupRollup's --%>
<%if (facilityGroup!=null){%>
<p class="head2">FacilityGroup Rollup: Parent Groups</p>

<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Parent&nbsp;Group&nbsp;[ID]</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:if name="currentGroupRollups" size="0">
  <%int lineParent = 0;%>
  <ofbiz:iterator name="facilityGroupRollup" property="currentGroupRollups">
    <%lineParent++;%>
    <%GenericValue curGroup = facilityGroupRollup.getRelatedOne("ParentFacilityGroup");%>
    <tr valign="middle">
      <td><%if (curGroup!=null){%><a href="<ofbiz:url>/EditFacilityGroup?facilityGroupId=<%=curGroup.getString("facilityGroupId")%></ofbiz:url>" class="buttontext"><%=curGroup.getString("description")%> [<%=curGroup.getString("facilityGroupId")%>]</a><%}%></td>
      <td><div class='tabletext' <%=(facilityGroupRollup.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(facilityGroupRollup.getTimestamp("fromDate")))?"style='color: red;'":""%>><ofbiz:inputvalue entityAttr="facilityGroupRollup" field="fromDate"/></div></td>
      <td align="center">
        <FORM method=POST action='<ofbiz:url>/updateFacilityGroupToGroup</ofbiz:url>' name='lineParentForm<%=lineParent%>'>
            <input type=hidden name='showFacilityGroupId' value='<ofbiz:inputvalue entityAttr="facilityGroupRollup" field="facilityGroupId"/>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="facilityGroupId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="parentFacilityGroupId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="fromDate" fullattrs="true"/>>
            <input type=text size='22' <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="thruDate" fullattrs="true"/> style='font-size: x-small;<%=(facilityGroupRollup.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(facilityGroupRollup.getTimestamp("thruDate")))?" color: red;":""%>'>
            <a href='#' onclick='setLineThruDateParent("<%=lineParent%>")' class='buttontext'>[Now]</a>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="sequenceNum" fullattrs="true"/> style='font-size: x-small;'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
      </td>
      <td>
        <a href="<ofbiz:url>/removeFacilityGroupFromGroup?showFacilityGroupId=<%=facilityGroupId%>&facilityGroupId=<%=facilityGroupRollup.getString("facilityGroupId")%>&parentFacilityGroupId=<%=facilityGroupRollup.getString("parentFacilityGroupId")%>&fromDate=<%=UtilFormatOut.encodeQueryValue(facilityGroupRollup.getTimestamp("fromDate").toString())%></ofbiz:url>" class="buttontext">
        [Delete]</a>
      </td>
    </tr>
  </ofbiz:iterator>
</ofbiz:if>

<ofbiz:unless name="currentGroupRollups" size="0">
  <tr valign="middle">
    <td colspan='5'><DIV class='tabletext'>No Parent Groups found.</DIV></td>
  </tr>
</ofbiz:unless>
</table>
<br>
<form method="POST" action="<ofbiz:url>/addFacilityGroupToGroup</ofbiz:url>" style='margin: 0;' name='addParentForm'>
  <input type="hidden" name="facilityGroupId" value="<%=facilityGroupId%>">
  <input type="hidden" name="showFacilityGroupId" value="<%=facilityGroupId%>">
  <div class='tabletext'>Add <b>Parent</b> Group (select Category and enter From Date):</div>
    <select name="parentFacilityGroupId">
    <%Iterator pit = UtilMisc.toIterator(facilityGroups);%>
    <%while(pit != null && pit.hasNext()) {%>
      <%GenericValue curGroup = (GenericValue)pit.next();%>
        <%if(!facilityGroupId.equals(curGroup.getString("facilityGroupId")) && !"_NA_".equals(curGroup.getString("facilityGroupId"))){%>
          <option value="<%=curGroup.getString("facilityGroupId")%>"><%=curGroup.getString("facilityGroupName")%> [<%=curGroup.getString("facilityGroupId")%>]</option>
        <%}%>
    <%}%>
    </select>
  <script language='JavaScript'>
      function setPctcParentFromDate() { document.addParentForm.fromDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"; }
  </script>
  <a href='#' onclick='setPctcParentFromDate()' class='buttontext'>[Now]</a>
  <input type=text size='22' name='fromDate'>
  <input type="submit" value="Add">
</form>
<br>
<hr>
<br>
<p class="head2">Group Rollup: Child Groups</p>

<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Child&nbsp;Group&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:if name="parentGroupRollups" size="0">
  <%int lineChild = 0;%>
  <ofbiz:iterator name="facilityGroupRollup" property="parentGroupRollups">
    <%lineChild++;%>
    <%GenericValue curGroup = facilityGroupRollup.getRelatedOne("CurrentFacilityGroup");%>
    <tr valign="middle">
      <td><a href="<ofbiz:url>/EditFacilityGroup?facilityGroupId=<%=curGroup.getString("facilityGroupId")%></ofbiz:url>" class="buttontext"><%=curGroup.getString("description")%> [<%=curGroup.getString("facilityGroupId")%>]</a></td>
      <td><div class='tabletext' <%=(facilityGroupRollup.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(facilityGroupRollup.getTimestamp("fromDate")))?"style='color: red;'":""%>><ofbiz:inputvalue entityAttr="facilityGroupRollup" field="fromDate"/></div></td>
      <td align="center">
        <FORM method=POST action='<ofbiz:url>/updateFacilityGroupToGroup</ofbiz:url>' name='lineChildForm<%=lineChild%>'>
            <input type=hidden name='showFacilityGroupId' value='<ofbiz:inputvalue entityAttr="facilityGroupRollup" field="facilityGroupId"/>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="facilityGroupId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="parentFacilityGroupId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="fromDate" fullattrs="true"/>>
            <input type=text size='22' <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="thruDate" fullattrs="true"/> style='font-size: x-small;<%=(facilityGroupRollup.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(facilityGroupRollup.getTimestamp("thruDate")))?" color: red;":""%>'>
            <a href='#' onclick='setLineThruDateChild("<%=lineChild%>")' class='buttontext'>[Now]</a>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="facilityGroupRollup" field="sequenceNum" fullattrs="true"/> style='font-size: x-small;'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
      </td>
      <td>
        <a href="<ofbiz:url>/removeFacilityGroupFromGroup?showFacilityGroupId=<%=facilityGroupId%>&facilityGroupId=<%=facilityGroupRollup.getString("facilityGroupId")%>&parentFacilityGroupId=<%=facilityGroupRollup.getString("parentFacilityGroupId")%>&fromDate=<%=UtilFormatOut.encodeQueryValue(facilityGroupRollup.getTimestamp("fromDate").toString())%></ofbiz:url>" class="buttontext">
        [Delete]</a>
      </td>
    </tr>
  </ofbiz:iterator>
</ofbiz:if>
<ofbiz:unless name="parentGroupRollups" size="0">
  <tr valign="middle">
    <td colspan='5'><DIV class='tabletext'>No Child Groups found.</DIV></td>
  </tr>
</ofbiz:unless>
</table>
<br>
<form method="POST" action="<ofbiz:url>/addFacilityGroupToGroup</ofbiz:url>" style='margin: 0;' name='addChildForm'>
  <input type="hidden" name="showFacilityGroupId" value="<%=facilityGroupId%>">
  <input type="hidden" name="parentFacilityGroupId" value="<%=facilityGroupId%>">
  <div class='tabletext'>Add <b>Child</b> Group (select Group and enter From Date):</div>
    <select name="facilityGroupId">
    <%Iterator cit = UtilMisc.toIterator(facilityGroups);%>
    <%while (cit != null && cit.hasNext()) {%>
      <%GenericValue curGroup = (GenericValue)cit.next();%>
      <%if (!facilityGroupId.equals(curGroup.getString("facilityGroupId")) && !"_NA_".equals(curGroup.getString("facilityGroupId"))){%>
        <option value="<%=curGroup.getString("facilityGroupId")%>"><%=curGroup.getString("facilityGroupName")%> [<%=curGroup.getString("facilityGroupId")%>]</option>
      <%}%>
    <%}%>
    </select>
  <script language='JavaScript'>
      function setPctcChildFromDate() { document.addChildForm.fromDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"; }
  </script>
  <a href='#' onclick='setPctcChildFromDate()' class='buttontext'>[Now]</a>
  <input type=text size='22' name='fromDate'>
  <input type="submit" value="Add">
</form>
<%}%>

<%} else {%>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
