
<%
/**
 *  Title: User Login Entity
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
 *@created    Wed Jul 04 01:03:19 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.commonapp.security.login.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditUserLogin"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("USER_LOGIN", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("USER_LOGIN", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("USER_LOGIN", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String userLoginId = request.getParameter("USER_LOGIN_USER_LOGIN_ID");  


  UserLogin userLogin = UserLoginHelper.findByPrimaryKey(userLoginId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    userLogin = null;
  }
%>

<a href="<%=response.encodeURL("FindUserLogin.jsp")%>" class="buttontext">[Find UserLogin]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditUserLogin.jsp")%>" class="buttontext">[Create UserLogin]</a>
<%}%>
<%if(userLogin != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditUserLogin.jsp?WEBEVENT=UPDATE_USER_LOGIN&UPDATE_MODE=DELETE&" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId)%>" class="buttontext">[Delete this UserLogin]</a>
  <%}%>
<%}%>
<%if(userLoginId != null){%>
  <a href="<%=response.encodeURL("ViewUserLogin.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId)%>" class="buttontext">[View UserLogin Details]</a>
<%}%>
<br>

<%if(userLogin == null && (userLoginId != null)){%>
    UserLogin with (USER_LOGIN_ID: <%=userLoginId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditUserLogin.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_USER_LOGIN">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(userLogin == null){%>
  <%if(hasCreatePermission){%>
    You may create a UserLogin by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USER_LOGIN_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="USER_LOGIN_USER_LOGIN_ID" value="<%=UtilFormatOut.checkNull(userLoginId)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a UserLogin (USER_LOGIN_ADMIN, or USER_LOGIN_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="USER_LOGIN_USER_LOGIN_ID" value="<%=userLoginId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USER_LOGIN_ID</td>
      <td>
        <b><%=userLoginId%></b> (This cannot be changed without re-creating the userLogin.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a UserLogin (USER_LOGIN_ADMIN, or USER_LOGIN_UPDATE needed).
  <%}%>
<%} //end if userLogin == null %>

<%if(showFields){%>

  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>PARTY_ID</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="USER_LOGIN_PARTY_ID" value="<%if(userLogin!=null){%><%=UtilFormatOut.checkNull(userLogin.getPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("USER_LOGIN_PARTY_ID"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>CONTACT_MECHANISM_ID</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="USER_LOGIN_CONTACT_MECHANISM_ID" value="<%if(userLogin!=null){%><%=UtilFormatOut.checkNull(userLogin.getContactMechanismId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("USER_LOGIN_CONTACT_MECHANISM_ID"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>CURRENT_USER_ID</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="USER_LOGIN_CURRENT_USER_ID" value="<%if(userLogin!=null){%><%=UtilFormatOut.checkNull(userLogin.getCurrentUserId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("USER_LOGIN_CURRENT_USER_ID"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>CURRENT_PASSWORD</td>
    <td>
    
      <input type="text" size="60" maxlength="60" name="USER_LOGIN_CURRENT_PASSWORD" value="<%if(userLogin!=null){%><%=UtilFormatOut.checkNull(userLogin.getCurrentPassword())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("USER_LOGIN_CURRENT_PASSWORD"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindUserLogin.jsp")%>" class="buttontext">[Find UserLogin]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditUserLogin.jsp")%>" class="buttontext">[Create UserLogin]</a>
<%}%>
<%if(userLogin != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditUserLogin.jsp?WEBEVENT=UPDATE_USER_LOGIN&UPDATE_MODE=DELETE&" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId)%>" class="buttontext">[Delete this UserLogin]</a>
  <%}%>
<%}%>
<%if(userLoginId != null){%>
  <a href="<%=response.encodeURL("ViewUserLogin.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId)%>" class="buttontext">[View UserLogin Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (USER_LOGIN_ADMIN, USER_LOGIN_CREATE, or USER_LOGIN_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

