
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
 *@created    Fri Jul 06 16:51:34 MDT 2001
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

<%boolean hasCreatePermission=Security.hasEntityPermission("SECURITY_GROUP", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SECURITY_GROUP", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SECURITY_GROUP", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String groupId = request.getParameter("SECURITY_GROUP_GROUP_ID");  


  SecurityGroup securityGroup = SecurityGroupHelper.findByPrimaryKey(groupId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    securityGroup = null;
  }
%>

<a href="<%=response.encodeURL("FindSecurityGroup.jsp")%>" class="buttontext">[Find SecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityGroup.jsp")%>" class="buttontext">[Create SecurityGroup]</a>
<%}%>
<%if(securityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityGroup.jsp?WEBEVENT=UPDATE_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this SecurityGroup]</a>
  <%}%>
<%}%>
<%if(groupId != null){%>
  <a href="<%=response.encodeURL("ViewSecurityGroup.jsp?" + "SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[View SecurityGroup Details]</a>
<%}%>
<br>

<%if(securityGroup == null && (groupId != null)){%>
    SecurityGroup with (GROUP_ID: <%=groupId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditSecurityGroup.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_SECURITY_GROUP">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(securityGroup == null){%>
  <%if(hasCreatePermission){%>
    You may create a SecurityGroup by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GROUP_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="SECURITY_GROUP_GROUP_ID" value="<%=UtilFormatOut.checkNull(groupId)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a SecurityGroup (SECURITY_GROUP_ADMIN, or SECURITY_GROUP_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="SECURITY_GROUP_GROUP_ID" value="<%=groupId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GROUP_ID</td>
      <td>
        <b><%=groupId%></b> (This cannot be changed without re-creating the securityGroup.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a SecurityGroup (SECURITY_GROUP_ADMIN, or SECURITY_GROUP_UPDATE needed).
  <%}%>
<%} //end if securityGroup == null %>

<%if(showFields){%>

  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
    
      <input type="text" size="80" maxlength="255" name="SECURITY_GROUP_DESCRIPTION" value="<%if(securityGroup!=null){%><%=UtilFormatOut.checkNull(securityGroup.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("SECURITY_GROUP_DESCRIPTION"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindSecurityGroup.jsp")%>" class="buttontext">[Find SecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityGroup.jsp")%>" class="buttontext">[Create SecurityGroup]</a>
<%}%>
<%if(securityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityGroup.jsp?WEBEVENT=UPDATE_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this SecurityGroup]</a>
  <%}%>
<%}%>
<%if(groupId != null){%>
  <a href="<%=response.encodeURL("ViewSecurityGroup.jsp?" + "SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[View SecurityGroup Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_GROUP_ADMIN, SECURITY_GROUP_CREATE, or SECURITY_GROUP_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

