
<%
/**
 *  Title: Security Component - Security Group Entity
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
 *@created    Wed May 23 02:36:23 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.commonapp.security.securitygroup.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditSecurityGroup"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("SECURITY_GROUP", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SECURITY_GROUP", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SECURITY_GROUP", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String groupId = request.getParameter("SECURITY_GROUP_GROUP_ID");

  

  SecurityGroup securityGroup = SecurityGroupHelper.findByPrimaryKey(groupId);
%>

<a href="FindSecurityGroup.jsp" class="buttontext">[Find SecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="EditSecurityGroup.jsp" class="buttontext">[Create SecurityGroup]</a>
<%}%>
<%if(hasDeletePermission){%>
  <%if(securityGroup != null){%>
    <a href="EditSecurityGroup.jsp?WEBEVENT=UPDATE_SECURITY_GROUP&UPDATE_MODE=DELETE&SECURITY_GROUP_GROUP_ID=<%=groupId%>" class="buttontext">[Delete this SecurityGroup]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(groupId != null){%>
    <a href="EditsecurityGroup.jsp?SECURITY_GROUP_GROUP_ID=<%=groupId%>" class="buttontext">[Edit SecurityGroup]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(securityGroup == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified SecurityGroup was not found.</h3></td></tr>
<%}else{%>
  <input type="hidden" name="WEBEVENT" value="UPDATE_SECURITY_GROUP">
  <input type="hidden" name="UPDATE_MODE" value="UPDATE">

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>GROUP_ID</td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroup.getGroupId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>DESCRIPTION</td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroup.getDescription())%>
    
    </td>
  </tr>

<%} //end if securityGroup == null %>
</table>

<a href="FindSecurityGroup.jsp" class="buttontext">[Find SecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="EditSecurityGroup.jsp" class="buttontext">[Create SecurityGroup]</a>
<%}%>
<%if(hasDeletePermission){%>
  <%if(securityGroup != null){%>
    <a href="EditSecurityGroup.jsp?WEBEVENT=UPDATE_SECURITY_GROUP&UPDATE_MODE=DELETE&SECURITY_GROUP_GROUP_ID=<%=groupId%>" class="buttontext">[Delete this SecurityGroup]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(groupId != null){%>
    <a href="EditsecurityGroup.jsp?SECURITY_GROUP_GROUP_ID=<%=groupId%>" class="buttontext">[Edit SecurityGroup]</a>
  <%}%>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_GROUP_ADMIN, or SECURITY_GROUP_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
