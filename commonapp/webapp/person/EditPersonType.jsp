
<%
/**
 *  Title: Person Component - Person Type Entity
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
 *@created    Thu May 31 17:02:02 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditPersonType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON_TYPE", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String typeId = request.getParameter("PERSON_TYPE_TYPE_ID");

  

  PersonType personType = PersonTypeHelper.findByPrimaryKey(typeId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    personType = null;
  }
%>

<a href="<%=response.encodeURL("FindPersonType.jsp")%>" class="buttontext">[Find PersonType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonType.jsp")%>" class="buttontext">[Create PersonType]</a>
<%}%>
<%if(personType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPersonType.jsp?WEBEVENT=UPDATE_PERSON_TYPE&UPDATE_MODE=DELETE&" + "PERSON_TYPE_TYPE_ID=" + typeId)%>" class="buttontext">[Delete this PersonType]</a>
  <%}%>
<%}%>
<%if(typeId != null){%>
  <a href="<%=response.encodeURL("ViewPersonType.jsp?" + "PERSON_TYPE_TYPE_ID=" + typeId)%>" class="buttontext">[View PersonType Details]</a>
<%}%>
<br>

<%if(personType == null && (typeId != null)){%>
    PersonType with (TYPE_ID: <%=typeId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditPersonType.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON_TYPE">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(personType == null){%>
  <%if(hasCreatePermission){%>
    You may create a PersonType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>TYPE_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PERSON_TYPE_TYPE_ID" value="<%=UtilFormatOut.checkNull(typeId)%>">
      
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PersonType (PERSON_TYPE_ADMIN, or PERSON_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
    <input type="hidden" name="PERSON_TYPE_TYPE_ID" value="<%=typeId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>TYPE_ID</td>
      <td>
        <b><%=typeId%></b> (This cannot be changed without re-creating the personType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PersonType (PERSON_TYPE_ADMIN, or PERSON_TYPE_UPDATE needed).
  <%}%>
<%} //end if personType == null %>

<%if(showFields){%>

  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>DESCRIPTION</td>
    <td>
    
      <input type="text" size="80" maxlength="255" name="PERSON_TYPE_DESCRIPTION" value="<%if(personType!=null){%><%=UtilFormatOut.checkNull(personType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PERSON_TYPE_DESCRIPTION"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindPersonType.jsp")%>" class="buttontext">[Find PersonType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonType.jsp")%>" class="buttontext">[Create PersonType]</a>
<%}%>
<%if(personType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPersonType.jsp?WEBEVENT=UPDATE_PERSON_TYPE&UPDATE_MODE=DELETE&" + "PERSON_TYPE_TYPE_ID=" + typeId)%>" class="buttontext">[Delete this PersonType]</a>
  <%}%>
<%}%>
<%if(typeId != null){%>
  <a href="<%=response.encodeURL("ViewPersonType.jsp?" + "PERSON_TYPE_TYPE_ID=" + typeId)%>" class="buttontext">[View PersonType Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_TYPE_ADMIN, PERSON_TYPE_CREATE, or PERSON_TYPE_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

