
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
 *@created    Mon May 28 21:59:02 MDT 2001
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

<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String username = request.getParameter("PERSON_USERNAME");

  

  Person person = PersonHelper.findByPrimaryKey(username);
%>

<a href="<%=response.encodeURL("FindPerson.jsp")%>" class="buttontext">[Find Person]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPerson.jsp")%>" class="buttontext">[Create Person]</a>
<%}%>
<%if(person != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPerson.jsp?WEBEVENT=UPDATE_PERSON&UPDATE_MODE=DELETE&" + "PERSON_USERNAME=" + username)%>" class="buttontext">[Delete this Person]</a>
  <%}%>
<%}%>
<%if(username != null){%>
  <a href="<%=response.encodeURL("ViewPerson.jsp?" + "PERSON_USERNAME=" + username)%>" class="buttontext">[View Person Details]</a>
<%}%>
<br>

<form action="<%=response.encodeURL("EditPerson.jsp")%>" method="POST" name="updateForm">
<table cellpadding="2" cellspacing="2" border="0">

<%if(person == null){%>
  <%if(username != null){%>
    Person with (USERNAME: <%=username%>) not found. 
    <%if(hasCreatePermission){%>
      You may create a Person by entering the values you want, and clicking Update.
      <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON">
      <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <td>USERNAME</td>
        <td>
        
          <input type="text" size="20" maxlength="20" name="PERSON_USERNAME" value="<%=UtilFormatOut.checkNull(username)%>">
        
        </td>
      </tr>
    <%}else{%>
      <%showFields=false;%>
      You do not have permission to create a Person (PERSON_ADMIN, or PERSON_CREATE needed).
    <%}%>
  <%}else{%>
    <%if(hasCreatePermission){%>
      <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON">
      <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <td>USERNAME</td>
        <td>
        
          <input type="text" size="20" maxlength="20" name="PERSON_USERNAME" value="">
        
        </td>
      </tr>
    <%}else{%>
      <%showFields=false;%>
      You do not have permission to create a Person (PERSON_ADMIN, or PERSON_CREATE needed).
    <%}%>
  <%} //end if sku == null%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON">
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
    <input type="hidden" name="PERSON_USERNAME" value="<%=username%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USERNAME</td>
      <td>
        <b><%=username%></b> (This cannot be changed without re-creating the person.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a Person (PERSON_ADMIN, or PERSON_UPDATE needed).
  <%}%>
<%} //end if person == null %>

<%if(showFields){%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if(session.getAttribute("ERROR_MESSAGE") != null && lastUpdateMode != null && lastUpdateMode.compareTo("UPDATE") == 0)
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    person = null;
  }
%>  

  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>PASSWORD</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="PERSON_PASSWORD" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getPassword())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_PASSWORD"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>FIRST_NAME</td>
    <td>
    
      <input type="text" size="40" maxlength="40" name="PERSON_FIRST_NAME" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getFirstName())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_FIRST_NAME"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>MIDDLE_NAME</td>
    <td>
    
      <input type="text" size="40" maxlength="40" name="PERSON_MIDDLE_NAME" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getMiddleName())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_MIDDLE_NAME"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>LAST_NAME</td>
    <td>
    
      <input type="text" size="40" maxlength="40" name="PERSON_LAST_NAME" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getLastName())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_LAST_NAME"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>TITLE</td>
    <td>
    
      <input type="text" size="10" maxlength="10" name="PERSON_TITLE" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getTitle())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_TITLE"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>SUFFIX</td>
    <td>
    
      <input type="text" size="10" maxlength="10" name="PERSON_SUFFIX" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getSuffix())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_SUFFIX"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>HOME_PHONE</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="PERSON_HOME_PHONE" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getHomePhone())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_HOME_PHONE"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>WORK_PHONE</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="PERSON_WORK_PHONE" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getWorkPhone())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_WORK_PHONE"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>FAX</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="PERSON_FAX" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getFax())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_FAX"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>EMAIL</td>
    <td>
    
      <input type="text" size="80" maxlength="255" name="PERSON_EMAIL" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getEmail())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_EMAIL"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>HOME_STREET1</td>
    <td>
    
      <input type="text" size="80" maxlength="100" name="PERSON_HOME_STREET1" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getHomeStreet1())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_HOME_STREET1"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>HOME_STREET2</td>
    <td>
    
      <input type="text" size="80" maxlength="100" name="PERSON_HOME_STREET2" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getHomeStreet2())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_HOME_STREET2"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>HOME_CITY</td>
    <td>
    
      <input type="text" size="60" maxlength="60" name="PERSON_HOME_CITY" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getHomeCity())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_HOME_CITY"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>HOME_COUNTY</td>
    <td>
    
      <input type="text" size="60" maxlength="60" name="PERSON_HOME_COUNTY" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getHomeCounty())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_HOME_COUNTY"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>HOME_STATE</td>
    <td>
    
      <input type="text" size="60" maxlength="60" name="PERSON_HOME_STATE" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getHomeState())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_HOME_STATE"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>HOME_COUNTRY</td>
    <td>
    
      <input type="text" size="60" maxlength="60" name="PERSON_HOME_COUNTRY" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getHomeCountry())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_HOME_COUNTRY"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>HOME_POSTAL_CODE</td>
    <td>
    
      <input type="text" size="60" maxlength="60" name="PERSON_HOME_POSTAL_CODE" value="<%if(person!=null){%><%=UtilFormatOut.checkNull(person.getHomePostalCode())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_HOME_POSTAL_CODE"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindPerson.jsp")%>" class="buttontext">[Find Person]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPerson.jsp")%>" class="buttontext">[Create Person]</a>
<%}%>
<%if(person != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPerson.jsp?WEBEVENT=UPDATE_PERSON&UPDATE_MODE=DELETE&" + "PERSON_USERNAME=" + username)%>" class="buttontext">[Delete this Person]</a>
  <%}%>
<%}%>
<%if(username != null){%>
  <a href="<%=response.encodeURL("ViewPerson.jsp?" + "PERSON_USERNAME=" + username)%>" class="buttontext">[View Person Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_ADMIN, PERSON_CREATE, or PERSON_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

