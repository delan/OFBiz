
<%
/**
 *  Title: Person Component - Person Person Type Entity
 *  Description: Maps a Person to a Person Type; necessary so a person can be of multiple types.
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
 *@created    Wed May 23 12:53:19 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditPersonPersonType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON_PERSON_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON_PERSON_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON_PERSON_TYPE", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String username = request.getParameter("PERSON_PERSON_TYPE_USERNAME");
    String typeId = request.getParameter("PERSON_PERSON_TYPE_TYPE_ID");

  
  

  PersonPersonType personPersonType = PersonPersonTypeHelper.findByPrimaryKey(username, typeId);
%>

<a href="FindPersonPersonType.jsp" class="buttontext">[Find PersonPersonType]</a>
<%if(hasCreatePermission){%>
  <a href="EditPersonPersonType.jsp" class="buttontext">[Create PersonPersonType]</a>
<%}%>
<%if(personPersonType != null){%>
  <%if(hasDeletePermission){%>
    <a href="EditPersonPersonType.jsp?WEBEVENT=UPDATE_PERSON_PERSON_TYPE&UPDATE_MODE=DELETE&PERSON_PERSON_TYPE_USERNAME=<%=username%>&PERSON_PERSON_TYPE_TYPE_ID=<%=typeId%>" class="buttontext">[Delete this PersonPersonType]</a>
  <%}%>
<%}%>
<%if(username != null && typeId != null){%>
  <a href="ViewPersonPersonType.jsp?PERSON_PERSON_TYPE_USERNAME=<%=username%>&PERSON_PERSON_TYPE_TYPE_ID=<%=typeId%>" class="buttontext">[View PersonPersonType Details]</a>
<%}%>
<br>

<form action="EditPersonPersonType.jsp" method="POST" name="updateForm">
<table cellpadding="2" cellspacing="2" border="0">

<%if(personPersonType == null){%>
  <%if(username != null || typeId != null){%>
    PersonPersonType with (USERNAME, TYPE_ID: <%=username%>, <%=typeId%>) not found. 
    <%if(hasCreatePermission){%>
      You may create a PersonPersonType by entering the values you want, and clicking Update.
      <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON_PERSON_TYPE">
      <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <td>USERNAME</td>
        <td>
        
          <input type="text" size="20" maxlength="20" name="PERSON_PERSON_TYPE_USERNAME" value="<%=UtilFormatOut.checkNull(username)%>">
        
        </td>
      </tr>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <td>TYPE_ID</td>
        <td>
        
          <input type="text" size="40" maxlength="40" name="PERSON_PERSON_TYPE_TYPE_ID" value="<%=UtilFormatOut.checkNull(typeId)%>">
        
        </td>
      </tr>
    <%}else{%>
      <%showFields=false;%>
      You do not have permission to create a PersonPersonType (PERSON_PERSON_TYPE_ADMIN, or PERSON_PERSON_TYPE_CREATE needed).
    <%}%>
  <%}else{%>
    <%if(hasCreatePermission){%>
      <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON_PERSON_TYPE">
      <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <td>USERNAME</td>
        <td>
        
          <input type="text" size="20" maxlength="20" name="PERSON_PERSON_TYPE_USERNAME" value="">
        
        </td>
      </tr>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <td>TYPE_ID</td>
        <td>
        
          <input type="text" size="40" maxlength="40" name="PERSON_PERSON_TYPE_TYPE_ID" value="">
        
        </td>
      </tr>
    <%}else{%>
      <%showFields=false;%>
      You do not have permission to create a PersonPersonType (PERSON_PERSON_TYPE_ADMIN, or PERSON_PERSON_TYPE_CREATE needed).
    <%}%>
  <%} //end if sku == null%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON_PERSON_TYPE">
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
    <input type="hidden" name="PERSON_PERSON_TYPE_USERNAME" value="<%=username%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USERNAME</td>
      <td>
        <b><%=username%></b> (This cannot be changed without re-creating the personPersonType.)
      </td>
    </tr>
    <input type="hidden" name="PERSON_PERSON_TYPE_TYPE_ID" value="<%=typeId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>TYPE_ID</td>
      <td>
        <b><%=typeId%></b> (This cannot be changed without re-creating the personPersonType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PersonPersonType (PERSON_PERSON_TYPE_ADMIN, or PERSON_PERSON_TYPE_UPDATE needed).
  <%}%>
<%} //end if personPersonType == null %>

<%if(showFields){%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if(session.getAttribute("ERROR_MESSAGE") != null && lastUpdateMode != null && lastUpdateMode.compareTo("UPDATE") == 0)
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    personPersonType = null;
  }
%>  

  

  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="FindPersonPersonType.jsp" class="buttontext">[Find PersonPersonType]</a>
<%if(hasCreatePermission){%>
  <a href="EditPersonPersonType.jsp" class="buttontext">[Create PersonPersonType]</a>
<%}%>
<%if(personPersonType != null){%>
  <%if(hasDeletePermission){%>
    <a href="EditPersonPersonType.jsp?WEBEVENT=UPDATE_PERSON_PERSON_TYPE&UPDATE_MODE=DELETE&PERSON_PERSON_TYPE_USERNAME=<%=username%>&PERSON_PERSON_TYPE_TYPE_ID=<%=typeId%>" class="buttontext">[Delete this PersonPersonType]</a>
  <%}%>
<%}%>
<%if(username != null && typeId != null){%>
  <a href="ViewPersonPersonType.jsp?PERSON_PERSON_TYPE_USERNAME=<%=username%>&PERSON_PERSON_TYPE_TYPE_ID=<%=typeId%>" class="buttontext">[View PersonPersonType Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_PERSON_TYPE_ADMIN, PERSON_PERSON_TYPE_CREATE, or PERSON_PERSON_TYPE_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
