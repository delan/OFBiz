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
 *@author     David E. Jones
 *@created    May 10, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<%try {%>
<%if (security.hasEntityPermission("SECURITY", "_VIEW", session)) {%>
<%
    String userLoginId = request.getParameter("userLoginId");
    GenericValue userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));

	String partyId = null;
    if (userLogin != null) partyId = userLogin.getString("partyId");

    Collection userLoginSecurityGroups = delegator.findByAnd("UserLoginSecurityGroup", 
            UtilMisc.toMap("userLoginId", userLoginId), 
            UtilMisc.toList("groupId"));
    if (userLoginSecurityGroups != null) pageContext.setAttribute("userLoginSecurityGroups", userLoginSecurityGroups);

    Collection securityGroups = delegator.findAll("SecurityGroup", UtilMisc.toList("description", "groupId"));
    if (securityGroups != null) pageContext.setAttribute("securityGroups", securityGroups);

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
    if (userLoginSecurityGroups != null) {
        listSize = userLoginSecurityGroups.size();
    }
    lowIndex = viewIndex * viewSize;
    highIndex = (viewIndex + 1) * viewSize;
    if (listSize < highIndex) {
        highIndex = listSize;
    }
%>

<a href="<ofbiz:url>/viewprofile<%if (partyId != null) {%>?partyId=<%=partyId%><%}%></ofbiz:url>" class="buttontext">[Back To Profile]</a>

<div class="head1">SecurityGroups for UserLogin with ID "<%=UtilFormatOut.checkNull(userLoginId)%>"</div>

<ofbiz:if name="userLoginSecurityGroups" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditUserLoginSecurityGroups?userLoginId=" + userLoginId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditUserLoginSecurityGroups?userLoginId=" + userLoginId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<%if (userLoginId != null){%>
<table border="1" cellpadding='2' cellspacing='0' width='100%'>
  <tr>
    <td><div class="tabletext"><b>SecurityGroup&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td><div class="tabletext"><b>Thru&nbsp;Date</b></div></td>
    <td width='1%'><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="userLoginSecurityGroup" property="userLoginSecurityGroups" offset="<%=lowIndex%>" limit="<%=viewSize%>">
  <tr valign="middle">
    <td><a href='<ofbiz:url>/EditSecurityGroup?groupId=<ofbiz:entityfield attribute="userLoginSecurityGroup" field="groupId"/></ofbiz:url>' class='buttontext'>
        <ofbiz:entityfield attribute="userLoginSecurityGroup" field="groupId"/></a>
    </td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="fromDate"/></div></td>
    <td>
        <form action='<ofbiz:url>/userLogin_updateUserLoginToSecurityGroup</ofbiz:url>' method=POST style='margin: 0;'>
            <input type=hidden name='userLoginId' value='<ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="userLoginId"/>'>
            <input type=hidden name='groupId' value='<ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="groupId"/>'>
            <input type=hidden name='fromDate' value='<ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="fromDate"/>'>
            <input type=text class="inputBox" size='22' <ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="thruDate" fullattrs="true"/>>
            <input type=submit value='Update'>
        </form>
    </td>
    </td>
    <td>
      <a href='<ofbiz:url>/userLogin_removeUserLoginFromSecurityGroup?userLoginId=<ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="userLoginId"/>&groupId=<ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="groupId"/>&fromDate=<ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="fromDate"/></ofbiz:url>' class="buttontext">
      [Remove]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<ofbiz:if name="userLoginSecurityGroups" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditUserLoginSecurityGroups?userLoginId=" + userLoginId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditUserLoginSecurityGroups?userLoginId=" + userLoginId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<br>
<form method="POST" action="<ofbiz:url>/userLogin_addUserLoginToSecurityGroup</ofbiz:url>" style='margin: 0;' name='addUserLoginToSecurityGroupForm'>
  <input type="hidden" name="userLoginId" value="<%=userLoginId%>">
  <input type="hidden" name="useValues" value="true">
  <%-- <input type=hidden name='activeOnly' value='<%=new Boolean(activeOnly).toString()%>'> --%>

  <script language='JavaScript'>
      function setUltsgFromDate() { document.addUserLoginToSecurityGroupForm.fromDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"; }
  </script>
  <div class='head2'>Add Security Group to this UserLogin:</div>
  <div class='tabletext'>
    Security Group ID: <%-- <input type=text size='60' name='groupId'> --%>
      <select name="groupId" class="selectBox">
      <ofbiz:iterator name="securityGroup" property="securityGroups">
        <option value='<ofbiz:entityfield attribute="securityGroup" field="groupId"/>'><ofbiz:entityfield attribute="securityGroup" field="description"/> [<ofbiz:entityfield attribute="securityGroup" field="groupId"/>]</option>
      </ofbiz:iterator>
      </select>
    <br>
    From Date: <a href='#' onclick='setUltsgFromDate()' class='buttontext'>[Now]</a> <input type=text class="inputBox" size='22' name='fromDate'>
    <input type="submit" value="Add">
  </div>
</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("SECURITY_VIEW" or "SECURITY_ADMIN" needed)</h3>
<%}%>
<%} catch (Exception e) { Debug.log(e); throw e; } %>
