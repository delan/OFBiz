
<%
/**
 *  Title: Person Component - Person Entity
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
 *@created    Tue May 22 23:57:27 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditPerson"); %>

<%@ include file="/includes/header.jsp" %> 
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PERSON", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String username = request.getParameter("PERSON_USERNAME");

  

  Person person = PersonHelper.findByPrimaryKey(username);
%>

<a href="FindPerson.jsp" class="buttontext">[Find Person]</a>
<%if(hasCreatePermission){%>
  <a href="EditPerson.jsp" class="buttontext">[Create Person]</a>
<%}%>
<%if(hasDeletePermission){%>
  <%if(person != null){%>
    <a href="EditPerson.jsp?WEBEVENT=UPDATE_PERSON&UPDATE_MODE=DELETE&PERSON_USERNAME=<%=username%>" class="buttontext">[Delete this Person]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(username != null){%>
    <a href="Editperson.jsp?PERSON_USERNAME=<%=username%>" class="buttontext">[Edit Person]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(person == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified Person was not found.</h3></td></tr>
<%}else{%>
  <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON">
  <input type="hidden" name="UPDATE_MODE" value="UPDATE">

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>USERNAME</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getUsername())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>PASSWORD</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getPassword())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>FIRST_NAME</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getFirstName())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>MIDDLE_NAME</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getMiddleName())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>LAST_NAME</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getLastName())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>TITLE</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getTitle())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>SUFFIX</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getSuffix())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>HOME_PHONE</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getHomePhone())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>WORK_PHONE</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getWorkPhone())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>FAX</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getFax())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>EMAIL</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getEmail())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>HOME_STREET1</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getHomeStreet1())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>HOME_STREET2</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getHomeStreet2())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>HOME_CITY</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getHomeCity())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>HOME_COUNTY</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getHomeCounty())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>HOME_STATE</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getHomeState())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>HOME_COUNTRY</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getHomeCountry())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>HOME_POSTAL_CODE</td>
    <td>
    
      <%=UtilFormatOut.checkNull(person.getHomePostalCode())%>
    
    </td>
  </tr>

<%} //end if person == null %>
</table>

<a href="FindPerson.jsp" class="buttontext">[Find Person]</a>
<%if(hasCreatePermission){%>
  <a href="EditPerson.jsp" class="buttontext">[Create Person]</a>
<%}%>
<%if(hasDeletePermission){%>
  <%if(person != null){%>
    <a href="EditPerson.jsp?WEBEVENT=UPDATE_PERSON&UPDATE_MODE=DELETE&PERSON_USERNAME=<%=username%>" class="buttontext">[Delete this Person]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(username != null){%>
    <a href="Editperson.jsp?PERSON_USERNAME=<%=username%>" class="buttontext">[Edit Person]</a>
  <%}%>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_ADMIN, or PERSON_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
