
<%
/**
 *  Title: Party Type Attribute Entity
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
 *@created    Wed Jul 04 01:03:18 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.commonapp.party.party.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditPartyTypeAttr"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_TYPE_ATTR", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_TYPE_ATTR", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_TYPE_ATTR", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String partyTypeId = request.getParameter("PARTY_TYPE_ATTR_PARTY_TYPE_ID");  
  String name = request.getParameter("PARTY_TYPE_ATTR_NAME");  


  PartyTypeAttr partyTypeAttr = PartyTypeAttrHelper.findByPrimaryKey(partyTypeId, name);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    partyTypeAttr = null;
  }
%>

<a href="<%=response.encodeURL("FindPartyTypeAttr.jsp")%>" class="buttontext">[Find PartyTypeAttr]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyTypeAttr.jsp")%>" class="buttontext">[Create PartyTypeAttr]</a>
<%}%>
<%if(partyTypeAttr != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyTypeAttr.jsp?WEBEVENT=UPDATE_PARTY_TYPE_ATTR&UPDATE_MODE=DELETE&" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeId + "&" + "PARTY_TYPE_ATTR_NAME=" + name)%>" class="buttontext">[Delete this PartyTypeAttr]</a>
  <%}%>
<%}%>
<%if(partyTypeId != null && name != null){%>
  <a href="<%=response.encodeURL("ViewPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeId + "&" + "PARTY_TYPE_ATTR_NAME=" + name)%>" class="buttontext">[View PartyTypeAttr Details]</a>
<%}%>
<br>

<%if(partyTypeAttr == null && (partyTypeId != null || name != null)){%>
    PartyTypeAttr with (PARTY_TYPE_ID, NAME: <%=partyTypeId%>, <%=name%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditPartyTypeAttr.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_PARTY_TYPE_ATTR">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(partyTypeAttr == null){%>
  <%if(hasCreatePermission){%>
    You may create a PartyTypeAttr by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_TYPE_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PARTY_TYPE_ATTR_PARTY_TYPE_ID" value="<%=UtilFormatOut.checkNull(partyTypeId)%>">
      

      </td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>NAME</td>
      <td>
      
        <input type="text" size="60" maxlength="60" name="PARTY_TYPE_ATTR_NAME" value="<%=UtilFormatOut.checkNull(name)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PartyTypeAttr (PARTY_TYPE_ATTR_ADMIN, or PARTY_TYPE_ATTR_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PARTY_TYPE_ATTR_PARTY_TYPE_ID" value="<%=partyTypeId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_TYPE_ID</td>
      <td>
        <b><%=partyTypeId%></b> (This cannot be changed without re-creating the partyTypeAttr.)
      </td>
    </tr>
      <input type="hidden" name="PARTY_TYPE_ATTR_NAME" value="<%=name%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>NAME</td>
      <td>
        <b><%=name%></b> (This cannot be changed without re-creating the partyTypeAttr.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PartyTypeAttr (PARTY_TYPE_ATTR_ADMIN, or PARTY_TYPE_ATTR_UPDATE needed).
  <%}%>
<%} //end if partyTypeAttr == null %>

<%if(showFields){%>

  

  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindPartyTypeAttr.jsp")%>" class="buttontext">[Find PartyTypeAttr]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyTypeAttr.jsp")%>" class="buttontext">[Create PartyTypeAttr]</a>
<%}%>
<%if(partyTypeAttr != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyTypeAttr.jsp?WEBEVENT=UPDATE_PARTY_TYPE_ATTR&UPDATE_MODE=DELETE&" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeId + "&" + "PARTY_TYPE_ATTR_NAME=" + name)%>" class="buttontext">[Delete this PartyTypeAttr]</a>
  <%}%>
<%}%>
<%if(partyTypeId != null && name != null){%>
  <a href="<%=response.encodeURL("ViewPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeId + "&" + "PARTY_TYPE_ATTR_NAME=" + name)%>" class="buttontext">[View PartyTypeAttr Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_TYPE_ATTR_ADMIN, PARTY_TYPE_ATTR_CREATE, or PARTY_TYPE_ATTR_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

