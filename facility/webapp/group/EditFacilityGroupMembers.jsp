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
    //default this to true, ie only show active
    boolean activeOnly = !"false".equals(request.getParameter("activeOnly"));

    boolean useValues = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

	String facilityGroupId = request.getParameter("facilityGroupId");
	GenericValue facilityGroup = delegator.findByPrimaryKey("FacilityGroup", UtilMisc.toMap("facilityGroupId", facilityGroupId));
	if (facilityGroup == null) useValues = false;

	List facilityGroupMembers = facilityGroup.getRelated("FacilityGroupMember", null, UtilMisc.toList("sequenceNum", "facilityId"));
	if (activeOnly) {
		facilityGroupMembers = EntityUtil.filterByDate(facilityGroupMembers, true);
	}
	if (facilityGroupMembers != null) {
		pageContext.setAttribute("facilityGroupMembers", facilityGroupMembers);
	}

   	List facilityGroups = delegator.findAll("FacilityGroup", UtilMisc.toList("description"));
	if (facilityGroups != null) pageContext.setAttribute("facilityGroups", facilityGroups);

    if ("true".equalsIgnoreCase((String)request.getParameter("useValues"))) useValues = true;

    int viewIndex = 0;
    int viewSize = 20;
    int highIndex = 0;
    int lowIndex = 0;
    int listSize = 0;

    try {
        viewIndex = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_INDEX")).intValue();
    } catch (Exception e) {
        viewIndex = 0;
    }
    try {
        viewSize = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_SIZE")).intValue();
    } catch (Exception e) {
        viewSize = 20;
    }
    if (facilityGroupMembers != null) {
        listSize = facilityGroupMembers.size();
    }
    lowIndex = viewIndex * viewSize;
    highIndex = (viewIndex + 1) * viewSize;
    if (listSize < highIndex) {
        highIndex = listSize;
    }    
%>
<br>
<%@ include file="/includes/facilityGroupMenu.jsp" %>

<div class="head1">Facilities <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(facilityGroup==null?null:facilityGroup.getString("description"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityGroupId)%>]</span></div>
<a href="<ofbiz:url>/EditFacilityGroup</ofbiz:url>" class="buttontext">[New Group]</a>
<%if (activeOnly) {%>
  <a href="<ofbiz:url>/EditFacilityGroupMembers?facilityGroupId=<%=facilityGroupId%>&activeOnly=false</ofbiz:url>" class="buttontext">[Active and Inactive]</a>
<%} else {%>
  <a href="<ofbiz:url>/EditFacilityGroupMembers?facilityGroupId=<%=facilityGroupId%>&activeOnly=true</ofbiz:url>" class="buttontext">[Active Only]</a>
<%}%>
<br>
<br>

<%-- Edit 'ProductCategoryMember's --%>
<%if(facilityGroupId!=null && facilityGroup!=null){%>
<p class="head2">Facility-Group Member Maintenance</p>

<ofbiz:if name="facilityGroupMembers" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditCategoryProducts?facilityGroupId=" + facilityGroupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%>&activeOnly=<%=new Boolean(activeOnly).toString()%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditCategoryProducts?facilityGroupId=" + facilityGroupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%>&activeOnly=<%=new Boolean(activeOnly).toString()%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>

<script language='JavaScript'>
    function setLineThruDate(line) { eval('document.lineForm' + line + '.thruDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
</script>
<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Facility Name [ID]</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time&nbsp;&amp;&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<%int line = 0;%>
<ofbiz:iterator name="facilityGroupMember" property="facilityGroupMembers" offset="<%=lowIndex%>" limit="<%=viewSize%>">
  <%line++;%>
  <%GenericValue facility = facilityGroupMember.getRelatedOne("Facility");%>
  <tr valign="middle">
    <td><a href='<ofbiz:url>/EditFacility?facilityId=<ofbiz:inputvalue entityAttr="facilityGroupMember" field="facilityId"/></ofbiz:url>' class="buttontext"><%if (facility!=null) {%><%=facility.getString("facilityName")%><%}%> [<ofbiz:inputvalue entityAttr="facilityGroupMember" field="facilityId"/>]</a></td>
    <td>
        <%boolean hasntStarted = false;%>
        <%if (facilityGroupMember.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(facilityGroupMember.getTimestamp("fromDate"))) { hasntStarted = true; }%>
        <div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>>
                <ofbiz:inputvalue entityAttr="facilityGroupMember" field="fromDate"/>
        </div>
    </td>
    <td align="center">
        <%boolean hasExpired = false;%>
        <%if (facilityGroupMember.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(facilityGroupMember.getTimestamp("thruDate"))) { hasExpired = true; }%>
        <FORM method=POST action='<ofbiz:url>/updateCategoryProductMember?VIEW_SIZE=<%=viewSize%>&VIEW_INDEX=<%=viewIndex%></ofbiz:url>' name='lineForm<%=line%>'>
            <input type=hidden name='activeOnly' value='<%=new Boolean(activeOnly).toString()%>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="facilityGroupMember" field="facilityId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="facilityGroupMember" field="facilityGroupId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="facilityGroupMember" field="fromDate" fullattrs="true"/>>
            <input type=text size='22' <ofbiz:inputvalue entityAttr="facilityGroupMember" field="thruDate" fullattrs="true"/> style='font-size: x-small; <%if (hasExpired) {%>color: red;<%}%>'>
            <a href='#' onclick='setLineThruDate("<%=line%>")' class='buttontext'>[Now]</a>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="facilityGroupMember" field="sequenceNum" fullattrs="true"/> style='font-size: x-small;'>           
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/removeFacilityFromGroup?VIEW_SIZE=<%=viewSize%>&VIEW_INDEX=<%=viewIndex%>&facilityId=<ofbiz:entityfield attribute="facilityGroupMember" field="facilityId"/>&facilityGroupId=<ofbiz:entityfield attribute="facilityGroupMember" field="facilityGroupId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(facilityGroupMember.getTimestamp("fromDate").toString())%>&activeOnly=<%=new Boolean(activeOnly).toString()%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>

<ofbiz:if name="facilityGroupMembers" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditCategoryProducts?facilityGroupId=" + facilityGroupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%>&activeOnly=<%=new Boolean(activeOnly).toString()%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditCategoryProducts?facilityGroupId=" + facilityGroupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%>&activeOnly=<%=new Boolean(activeOnly).toString()%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>

<br>
<form method="POST" action="<ofbiz:url>/addFacilityToGroup</ofbiz:url>" style='margin: 0;' name='addFacilityGroupMemberForm'>
  <input type="hidden" name="facilityGroupId" value="<%=facilityGroupId%>">
  <input type="hidden" name="useValues" value="true">
  <input type=hidden name='activeOnly' value='<%=new Boolean(activeOnly).toString()%>'>

  <script language='JavaScript'>
      function setPcmFromDate() { document.addFacilityGroupMemberForm.fromDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"; }
  </script>
  <div class='head2'>Add FacilityGroupMember:</div>
  <div class='tabletext'>
    Facility ID: <input type=text size='20' name='facilityId'>
    From Date: <a href='#' onclick='setPcmFromDate()' class='buttontext'>[Now]</a> <input type=text size='22' name='fromDate'>
    <input type="submit" value="Add">
  </div>
</form>

<%-- TO DO IMPLEMENT THIS
<br>
<form method="POST" action="<ofbiz:url>/expireAllFacilityGroupMembers</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="facilityGroupId" value="<%=facilityGroupId%>">
  <input type="hidden" name="useValues" value="true">
  <input type=hidden name='activeOnly' value='<%=new Boolean(activeOnly).toString()%>'>

  <div class='head2'>Expire All Facility Members:</div>
  <div class='tabletext'>
    Optional Expiration Date: <input type=text size='20' name='thruDate'>
    <input type="submit" value="Expire All">
  </div>
</form>
<br>
<form method="POST" action="<ofbiz:url>/removeExpiredFacilityGroupMembers</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="facilityGroupId" value="<%=facilityGroupId%>">
  <input type="hidden" name="useValues" value="true">
  <input type=hidden name='activeOnly' value='<%=new Boolean(activeOnly).toString()%>'>

  <div class='head2'>Remove Expired Facility Members:</div>
  <div class='tabletext'>
    Optional Expired Before Date: <input type=text size='20' name='validDate'>
    <input type="submit" value="Remove Expired">
  </div>
</form>
--%>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
<%}%>
