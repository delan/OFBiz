
<%
/**
 *  Title: Security Component - Security Permission Entity
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
 *@created    Wed May 23 02:36:48 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditSecurityPermission"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("SECURITY_PERMISSION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SECURITY_PERMISSION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SECURITY_PERMISSION", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String permissionId = request.getParameter("SECURITY_PERMISSION_PERMISSION_ID");

  

  SecurityPermission securityPermission = SecurityPermissionHelper.findByPrimaryKey(permissionId);
%>

<a href="FindSecurityPermission.jsp" class="buttontext">[Find SecurityPermission]</a>
<%if(hasCreatePermission){%>
  <a href="EditSecurityPermission.jsp" class="buttontext">[Create SecurityPermission]</a>
<%}%>
<%if(securityPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="EditSecurityPermission.jsp?WEBEVENT=UPDATE_SECURITY_PERMISSION&UPDATE_MODE=DELETE&SECURITY_PERMISSION_PERMISSION_ID=<%=permissionId%>" class="buttontext">[Delete this SecurityPermission]</a>
  <%}%>
<%}%>
<%if(permissionId != null){%>
  <a href="ViewSecurityPermission.jsp?SECURITY_PERMISSION_PERMISSION_ID=<%=permissionId%>" class="buttontext">[View SecurityPermission Details]</a>
<%}%>
<br>

<form action="EditSecurityPermission.jsp" method="POST" name="updateForm">
<table cellpadding="2" cellspacing="2" border="0">

<%if(securityPermission == null){%>
  <%if(permissionId != null){%>
    SecurityPermission with (PERMISSION_ID: <%=permissionId%>) not found. 
    <%if(hasCreatePermission){%>
      You may create a SecurityPermission by entering the values you want, and clicking Update.
      <input type="hidden" name="WEBEVENT" value="UPDATE_SECURITY_PERMISSION">
      <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <td>PERMISSION_ID</td>
        <td>
        
          <input type="text" size="60" maxlength="60" name="SECURITY_PERMISSION_PERMISSION_ID" value="<%=UtilFormatOut.checkNull(permissionId)%>">
        
        </td>
      </tr>
    <%}else{%>
      <%showFields=false;%>
      You do not have permission to create a SecurityPermission (SECURITY_PERMISSION_ADMIN, or SECURITY_PERMISSION_CREATE needed).
    <%}%>
  <%}else{%>
    <%if(hasCreatePermission){%>
      <input type="hidden" name="WEBEVENT" value="UPDATE_SECURITY_PERMISSION">
      <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <td>PERMISSION_ID</td>
        <td>
        
          <input type="text" size="60" maxlength="60" name="SECURITY_PERMISSION_PERMISSION_ID" value="">
        
        </td>
      </tr>
    <%}else{%>
      <%showFields=false;%>
      You do not have permission to create a SecurityPermission (SECURITY_PERMISSION_ADMIN, or SECURITY_PERMISSION_CREATE needed).
    <%}%>
  <%} //end if sku == null%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="WEBEVENT" value="UPDATE_SECURITY_PERMISSION">
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
    <input type="hidden" name="SECURITY_PERMISSION_PERMISSION_ID" value="<%=permissionId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PERMISSION_ID</td>
      <td>
        <b><%=permissionId%></b> (This cannot be changed without re-creating the securityPermission.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a SecurityPermission (SECURITY_PERMISSION_ADMIN, or SECURITY_PERMISSION_UPDATE needed).
  <%}%>
<%} //end if securityPermission == null %>

<%if(showFields){%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if(session.getAttribute("ERROR_MESSAGE") != null && lastUpdateMode != null && lastUpdateMode.compareTo("UPDATE") == 0)
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    securityPermission = null;
  }
%>  

  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>DESCRIPTION</td>
    <td>
    
      <input type="text" size="80" maxlength="255" name="SECURITY_PERMISSION_DESCRIPTION" value="<%if(securityPermission!=null){%><%=UtilFormatOut.checkNull(securityPermission.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("SECURITY_PERMISSION_DESCRIPTION"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="FindSecurityPermission.jsp" class="buttontext">[Find SecurityPermission]</a>
<%if(hasCreatePermission){%>
  <a href="EditSecurityPermission.jsp" class="buttontext">[Create SecurityPermission]</a>
<%}%>
<%if(securityPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="EditSecurityPermission.jsp?WEBEVENT=UPDATE_SECURITY_PERMISSION&UPDATE_MODE=DELETE&SECURITY_PERMISSION_PERMISSION_ID=<%=permissionId%>" class="buttontext">[Delete this SecurityPermission]</a>
  <%}%>
<%}%>
<%if(permissionId != null){%>
  <a href="ViewSecurityPermission.jsp?SECURITY_PERMISSION_PERMISSION_ID=<%=permissionId%>" class="buttontext">[View SecurityPermission Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_PERMISSION_ADMIN, SECURITY_PERMISSION_CREATE, or SECURITY_PERMISSION_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
