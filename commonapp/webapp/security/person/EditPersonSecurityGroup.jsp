
<%
/**
 *  Title: Security Component - Person Security Group Entity
 *  Description: Defines a permission available to a security group
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
 *@created    Thu May 31 17:02:48 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.commonapp.security.person.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditPersonSecurityGroup"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String username = request.getParameter("PERSON_SECURITY_GROUP_USERNAME");
  String groupId = request.getParameter("PERSON_SECURITY_GROUP_GROUP_ID");

  
  

  PersonSecurityGroup personSecurityGroup = PersonSecurityGroupHelper.findByPrimaryKey(username, groupId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    personSecurityGroup = null;
  }
%>

<a href="<%=response.encodeURL("FindPersonSecurityGroup.jsp")%>" class="buttontext">[Find PersonSecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonSecurityGroup.jsp")%>" class="buttontext">[Create PersonSecurityGroup]</a>
<%}%>
<%if(personSecurityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPersonSecurityGroup.jsp?WEBEVENT=UPDATE_PERSON_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this PersonSecurityGroup]</a>
  <%}%>
<%}%>
<%if(username != null && groupId != null){%>
  <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[View PersonSecurityGroup Details]</a>
<%}%>
<br>

<%if(personSecurityGroup == null && (username != null || groupId != null)){%>
    PersonSecurityGroup with (USERNAME, GROUP_ID: <%=username%>, <%=groupId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditPersonSecurityGroup.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON_SECURITY_GROUP">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(personSecurityGroup == null){%>
  <%if(hasCreatePermission){%>
    You may create a PersonSecurityGroup by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USERNAME</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PERSON_SECURITY_GROUP_USERNAME" value="<%=UtilFormatOut.checkNull(username)%>">
      
      </td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>GROUP_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PERSON_SECURITY_GROUP_GROUP_ID" value="<%=UtilFormatOut.checkNull(groupId)%>">
      
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PersonSecurityGroup (PERSON_SECURITY_GROUP_ADMIN, or PERSON_SECURITY_GROUP_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
    <input type="hidden" name="PERSON_SECURITY_GROUP_USERNAME" value="<%=username%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USERNAME</td>
      <td>
        <b><%=username%></b> (This cannot be changed without re-creating the personSecurityGroup.)
      </td>
    </tr>
    <input type="hidden" name="PERSON_SECURITY_GROUP_GROUP_ID" value="<%=groupId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>GROUP_ID</td>
      <td>
        <b><%=groupId%></b> (This cannot be changed without re-creating the personSecurityGroup.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PersonSecurityGroup (PERSON_SECURITY_GROUP_ADMIN, or PERSON_SECURITY_GROUP_UPDATE needed).
  <%}%>
<%} //end if personSecurityGroup == null %>

<%if(showFields){%>

  

  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindPersonSecurityGroup.jsp")%>" class="buttontext">[Find PersonSecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonSecurityGroup.jsp")%>" class="buttontext">[Create PersonSecurityGroup]</a>
<%}%>
<%if(personSecurityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPersonSecurityGroup.jsp?WEBEVENT=UPDATE_PERSON_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this PersonSecurityGroup]</a>
  <%}%>
<%}%>
<%if(username != null && groupId != null){%>
  <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[View PersonSecurityGroup Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_SECURITY_GROUP_ADMIN, PERSON_SECURITY_GROUP_CREATE, or PERSON_SECURITY_GROUP_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

