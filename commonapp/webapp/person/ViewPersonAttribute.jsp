
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
 *@created    Fri May 25 14:25:29 MDT 2001
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

<%boolean hasViewPermission=Security.hasEntityPermission("PERSON_ATTRIBUTE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON_ATTRIBUTE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON_ATTRIBUTE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON_ATTRIBUTE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String username = request.getParameter("PERSON_ATTRIBUTE_USERNAME");
    String name = request.getParameter("PERSON_ATTRIBUTE_NAME");

  
  

  PersonAttribute personAttribute = PersonAttributeHelper.findByPrimaryKey(username, name);
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
<%if(hasUpdatePermission){%>
  <%if(username != null && name != null){%>
    <a href="<%=response.encodeURL("EditPersonAttribute.jsp?" + "PERSON_ATTRIBUTE_USERNAME=" + username + "&" + "PERSON_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Edit PersonAttribute]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(personAttribute == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified PersonAttribute was not found.</h3></td></tr>
<%}else{%>
  <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON_ATTRIBUTE">
  <input type="hidden" name="UPDATE_MODE" value="UPDATE">

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>USERNAME</td>
    <td>
    
      <%=UtilFormatOut.checkNull(personAttribute.getUsername())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>NAME</td>
    <td>
    
      <%=UtilFormatOut.checkNull(personAttribute.getName())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>VALUE</td>
    <td>
    
      <%=UtilFormatOut.checkNull(personAttribute.getValue())%>
    
    </td>
  </tr>

<%} //end if personAttribute == null %>
</table>

<a href="<%=response.encodeURL("FindPersonAttribute.jsp")%>" class="buttontext">[Find PersonAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonAttribute.jsp")%>" class="buttontext">[Create PersonAttribute]</a>
<%}%>
<%if(personAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPersonAttribute.jsp?WEBEVENT=UPDATE_PERSON_ATTRIBUTE&UPDATE_MODE=DELETE&" + "PERSON_ATTRIBUTE_USERNAME=" + username + "&" + "PERSON_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this PersonAttribute]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(username != null && name != null){%>
    <a href="<%=response.encodeURL("EditPersonAttribute.jsp?" + "PERSON_ATTRIBUTE_USERNAME=" + username + "&" + "PERSON_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Edit PersonAttribute]</a>
  <%}%>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_ATTRIBUTE_ADMIN, or PERSON_ATTRIBUTE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
