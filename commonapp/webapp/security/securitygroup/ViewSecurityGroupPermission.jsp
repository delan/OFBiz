
<%
/**
 *  Title: Security Component - Security Group Permission Entity
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
 *@created    Fri Jun 29 12:51:09 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.securitygroup.*" %>


<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditSecurityGroupPermission"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String groupId = request.getParameter("SECURITY_GROUP_PERMISSION_GROUP_ID");  
  String permissionId = request.getParameter("SECURITY_GROUP_PERMISSION_PERMISSION_ID");  

  
  

  SecurityGroupPermission securityGroupPermission = SecurityGroupPermissionHelper.findByPrimaryKey(groupId, permissionId);
%>

<b><u>View Entity: SecurityGroupPermission with (GROUP_ID, PERMISSION_ID: <%=groupId%>, <%=permissionId%>).</u></b>
<br>
<a href="<%=response.encodeURL("FindSecurityGroupPermission.jsp")%>" class="buttontext">[Find SecurityGroupPermission]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp")%>" class="buttontext">[Create SecurityGroupPermission]</a>
<%}%>
<%if(securityGroupPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp?WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Delete this SecurityGroupPermission]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(groupId != null && permissionId != null){%>
    <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Edit SecurityGroupPermission]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(securityGroupPermission == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified SecurityGroupPermission was not found.</h3></td></tr>
<%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>GROUP_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroupPermission.getGroupId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PERMISSION_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroupPermission.getPermissionId())%>
    
    </td>
  </tr>

<%} //end if securityGroupPermission == null %>
</table>

<a href="<%=response.encodeURL("FindSecurityGroupPermission.jsp")%>" class="buttontext">[Find SecurityGroupPermission]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp")%>" class="buttontext">[Create SecurityGroupPermission]</a>
<%}%>
<%if(securityGroupPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp?WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Delete this SecurityGroupPermission]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(groupId != null && permissionId != null){%>
    <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Edit SecurityGroupPermission]</a>
  <%}%>
<%}%>

  
  
  
<%-- Start Relation for SecurityGroup, type: one --%>
<%if(securityGroupPermission != null){%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session)){%>
    <%SecurityGroup securityGroup = SecurityGroupHelper.findByPrimaryKey(securityGroupPermission.getGroupId());%>
    <hr>
    <b>Related Entity: SecurityGroup with (GROUP_ID: <%=securityGroupPermission.getGroupId()%>)</b>
    <br>
    <%if(securityGroupPermission.getGroupId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/ViewSecurityGroup.jsp?" + "SECURITY_GROUP_GROUP_ID=" + securityGroupPermission.getGroupId())%>" class="buttontext">[View SecurityGroup Details]</a>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(securityGroup == null){%>
    <tr bgcolor="<%=rowColor1%>"><td><h3>Specified SecurityGroup was not found.</h3></td></tr>
    <%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>GROUP_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroup.getGroupId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>DESCRIPTION</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroup.getDescription())%>
    
    </td>
  </tr>

    <%} //end if securityGroup == null %>
    </table>
  <%}%>
<%}%>
<%-- End Relation for SecurityGroup, type: one --%>
  

  
  
  
<%-- Start Relation for SecurityPermission, type: one --%>
<%if(securityGroupPermission != null){%>
  <%if(Security.hasEntityPermission("SECURITY_PERMISSION", "_VIEW", session)){%>
    <%SecurityPermission securityPermission = SecurityPermissionHelper.findByPrimaryKey(securityGroupPermission.getPermissionId());%>
    <hr>
    <b>Related Entity: SecurityPermission with (PERMISSION_ID: <%=securityGroupPermission.getPermissionId()%>)</b>
    <br>
    <%if(securityGroupPermission.getPermissionId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/ViewSecurityPermission.jsp?" + "SECURITY_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[View SecurityPermission Details]</a>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(securityPermission == null){%>
    <tr bgcolor="<%=rowColor1%>"><td><h3>Specified SecurityPermission was not found.</h3></td></tr>
    <%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PERMISSION_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityPermission.getPermissionId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>DESCRIPTION</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityPermission.getDescription())%>
    
    </td>
  </tr>

    <%} //end if securityPermission == null %>
    </table>
  <%}%>
<%}%>
<%-- End Relation for SecurityPermission, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_GROUP_PERMISSION_ADMIN, or SECURITY_GROUP_PERMISSION_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
