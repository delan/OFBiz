
<%
/**
 *  Title: Person Component - Person Type Attribute Entity
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
 *@created    Wed May 23 12:53:05 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditPersonTypeAttribute"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String typeId = request.getParameter("PERSON_TYPE_ATTRIBUTE_TYPE_ID");
    String name = request.getParameter("PERSON_TYPE_ATTRIBUTE_NAME");

  
  

  PersonTypeAttribute personTypeAttribute = PersonTypeAttributeHelper.findByPrimaryKey(typeId, name);
%>

<a href="FindPersonTypeAttribute.jsp" class="buttontext">[Find PersonTypeAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="EditPersonTypeAttribute.jsp" class="buttontext">[Create PersonTypeAttribute]</a>
<%}%>
<%if(hasDeletePermission){%>
  <%if(personTypeAttribute != null){%>
    <a href="EditPersonTypeAttribute.jsp?WEBEVENT=UPDATE_PERSON_TYPE_ATTRIBUTE&UPDATE_MODE=DELETE&PERSON_TYPE_ATTRIBUTE_TYPE_ID=<%=typeId%>&PERSON_TYPE_ATTRIBUTE_NAME=<%=name%>" class="buttontext">[Delete this PersonTypeAttribute]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(typeId != null && name != null){%>
    <a href="EditpersonTypeAttribute.jsp?PERSON_TYPE_ATTRIBUTE_TYPE_ID=<%=typeId%>&PERSON_TYPE_ATTRIBUTE_NAME=<%=name%>" class="buttontext">[Edit PersonTypeAttribute]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(personTypeAttribute == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified PersonTypeAttribute was not found.</h3></td></tr>
<%}else{%>
  <input type="hidden" name="WEBEVENT" value="UPDATE_PERSON_TYPE_ATTRIBUTE">
  <input type="hidden" name="UPDATE_MODE" value="UPDATE">

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>TYPE_ID</td>
    <td>
    
      <%=UtilFormatOut.checkNull(personTypeAttribute.getTypeId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>NAME</td>
    <td>
    
      <%=UtilFormatOut.checkNull(personTypeAttribute.getName())%>
    
    </td>
  </tr>

<%} //end if personTypeAttribute == null %>
</table>

<a href="FindPersonTypeAttribute.jsp" class="buttontext">[Find PersonTypeAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="EditPersonTypeAttribute.jsp" class="buttontext">[Create PersonTypeAttribute]</a>
<%}%>
<%if(hasDeletePermission){%>
  <%if(personTypeAttribute != null){%>
    <a href="EditPersonTypeAttribute.jsp?WEBEVENT=UPDATE_PERSON_TYPE_ATTRIBUTE&UPDATE_MODE=DELETE&PERSON_TYPE_ATTRIBUTE_TYPE_ID=<%=typeId%>&PERSON_TYPE_ATTRIBUTE_NAME=<%=name%>" class="buttontext">[Delete this PersonTypeAttribute]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(typeId != null && name != null){%>
    <a href="EditpersonTypeAttribute.jsp?PERSON_TYPE_ATTRIBUTE_TYPE_ID=<%=typeId%>&PERSON_TYPE_ATTRIBUTE_NAME=<%=name%>" class="buttontext">[Edit PersonTypeAttribute]</a>
  <%}%>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_TYPE_ATTRIBUTE_ADMIN, or PERSON_TYPE_ATTRIBUTE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
