
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
 *@created    Fri May 25 14:29:47 MDT 2001
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

<%boolean hasViewPermission=Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String username = request.getParameter("PERSON_SECURITY_GROUP_USERNAME");
    String groupId = request.getParameter("PERSON_SECURITY_GROUP_GROUP_ID");

  
  

  PersonSecurityGroup personSecurityGroup = PersonSecurityGroupHelper.findByPrimaryKey(username, groupId);
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
<%if(hasUpdatePermission){%>
  <%if(username != null && groupId != null){%>
    <a href="<%=response.encodeURL("EditPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Edit PersonSecurityGroup]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(personSecurityGroup == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified PersonSecurityGroup was not found.</h3></td></tr>
<%}else{%>
  <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON_SECURITY_GROUP">
  <input type="hidden" name="UPDATE_MODE" value="UPDATE">

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>USERNAME</td>
    <td>
    
      <%=UtilFormatOut.checkNull(personSecurityGroup.getUsername())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>GROUP_ID</td>
    <td>
    
      <%=UtilFormatOut.checkNull(personSecurityGroup.getGroupId())%>
    
    </td>
  </tr>

<%} //end if personSecurityGroup == null %>
</table>

<a href="<%=response.encodeURL("FindPersonSecurityGroup.jsp")%>" class="buttontext">[Find PersonSecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonSecurityGroup.jsp")%>" class="buttontext">[Create PersonSecurityGroup]</a>
<%}%>
<%if(personSecurityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPersonSecurityGroup.jsp?WEBEVENT=UPDATE_PERSON_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this PersonSecurityGroup]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(username != null && groupId != null){%>
    <a href="<%=response.encodeURL("EditPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Edit PersonSecurityGroup]</a>
  <%}%>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_SECURITY_GROUP_ADMIN, or PERSON_SECURITY_GROUP_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
