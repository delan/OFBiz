
<%
/**
 *  Title: Person Component - Person Attribute Entity
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
 *@created    Thu May 31 17:01:49 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.commonapp.person.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditPersonAttribute"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON_ATTRIBUTE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON_ATTRIBUTE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON_ATTRIBUTE", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String username = request.getParameter("PERSON_ATTRIBUTE_USERNAME");
  String name = request.getParameter("PERSON_ATTRIBUTE_NAME");

  
  

  PersonAttribute personAttribute = PersonAttributeHelper.findByPrimaryKey(username, name);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    personAttribute = null;
  }
%>

<a href="<%=response.encodeURL("FindPersonAttribute.jsp")%>" class="buttontext">[Find PersonAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonAttribute.jsp")%>" class="buttontext">[Create PersonAttribute]</a>
<%}%>
<%if(personAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPersonAttribute.jsp?WEBEVENT=UPDATE_PERSON_ATTRIBUTE&UPDATE_MODE=DELETE&" + "PERSON_ATTRIBUTE_USERNAME=" + username + "&" + "PERSON_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this PersonAttribute]</a>
  <%}%>
<%}%>
<%if(username != null && name != null){%>
  <a href="<%=response.encodeURL("ViewPersonAttribute.jsp?" + "PERSON_ATTRIBUTE_USERNAME=" + username + "&" + "PERSON_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[View PersonAttribute Details]</a>
<%}%>
<br>

<%if(personAttribute == null && (username != null || name != null)){%>
    PersonAttribute with (USERNAME, NAME: <%=username%>, <%=name%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditPersonAttribute.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON_ATTRIBUTE">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(personAttribute == null){%>
  <%if(hasCreatePermission){%>
    You may create a PersonAttribute by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USERNAME</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PERSON_ATTRIBUTE_USERNAME" value="<%=UtilFormatOut.checkNull(username)%>">
      
      </td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>NAME</td>
      <td>
      
        <input type="text" size="60" maxlength="60" name="PERSON_ATTRIBUTE_NAME" value="<%=UtilFormatOut.checkNull(name)%>">
      
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PersonAttribute (PERSON_ATTRIBUTE_ADMIN, or PERSON_ATTRIBUTE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
    <input type="hidden" name="PERSON_ATTRIBUTE_USERNAME" value="<%=username%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USERNAME</td>
      <td>
        <b><%=username%></b> (This cannot be changed without re-creating the personAttribute.)
      </td>
    </tr>
    <input type="hidden" name="PERSON_ATTRIBUTE_NAME" value="<%=name%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>NAME</td>
      <td>
        <b><%=name%></b> (This cannot be changed without re-creating the personAttribute.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PersonAttribute (PERSON_ATTRIBUTE_ADMIN, or PERSON_ATTRIBUTE_UPDATE needed).
  <%}%>
<%} //end if personAttribute == null %>

<%if(showFields){%>

  

  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>VALUE</td>
    <td>
    
      <input type="text" size="80" maxlength="255" name="PERSON_ATTRIBUTE_VALUE" value="<%if(personAttribute!=null){%><%=UtilFormatOut.checkNull(personAttribute.getValue())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_ATTRIBUTE_VALUE"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindPersonAttribute.jsp")%>" class="buttontext">[Find PersonAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonAttribute.jsp")%>" class="buttontext">[Create PersonAttribute]</a>
<%}%>
<%if(personAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPersonAttribute.jsp?WEBEVENT=UPDATE_PERSON_ATTRIBUTE&UPDATE_MODE=DELETE&" + "PERSON_ATTRIBUTE_USERNAME=" + username + "&" + "PERSON_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this PersonAttribute]</a>
  <%}%>
<%}%>
<%if(username != null && name != null){%>
  <a href="<%=response.encodeURL("ViewPersonAttribute.jsp?" + "PERSON_ATTRIBUTE_USERNAME=" + username + "&" + "PERSON_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[View PersonAttribute Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_ATTRIBUTE_ADMIN, PERSON_ATTRIBUTE_CREATE, or PERSON_ATTRIBUTE_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

