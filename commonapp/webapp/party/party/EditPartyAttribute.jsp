
<%
/**
 *  Title: Party Attribute Entity
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
 *@created    Fri Jul 06 16:51:32 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditPartyAttribute"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_ATTRIBUTE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_ATTRIBUTE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_ATTRIBUTE", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String partyId = request.getParameter("PARTY_ATTRIBUTE_PARTY_ID");  
  String name = request.getParameter("PARTY_ATTRIBUTE_NAME");  


  PartyAttribute partyAttribute = PartyAttributeHelper.findByPrimaryKey(partyId, name);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    partyAttribute = null;
  }
%>

<a href="<%=response.encodeURL("FindPartyAttribute.jsp")%>" class="buttontext">[Find PartyAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyAttribute.jsp")%>" class="buttontext">[Create PartyAttribute]</a>
<%}%>
<%if(partyAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyAttribute.jsp?WEBEVENT=UPDATE_PARTY_ATTRIBUTE&UPDATE_MODE=DELETE&" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyId + "&" + "PARTY_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this PartyAttribute]</a>
  <%}%>
<%}%>
<%if(partyId != null && name != null){%>
  <a href="<%=response.encodeURL("ViewPartyAttribute.jsp?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyId + "&" + "PARTY_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[View PartyAttribute Details]</a>
<%}%>
<br>

<%if(partyAttribute == null && (partyId != null || name != null)){%>
    PartyAttribute with (PARTY_ID, NAME: <%=partyId%>, <%=name%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditPartyAttribute.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_PARTY_ATTRIBUTE">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(partyAttribute == null){%>
  <%if(hasCreatePermission){%>
    You may create a PartyAttribute by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PARTY_ATTRIBUTE_PARTY_ID" value="<%=UtilFormatOut.checkNull(partyId)%>">
      

      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
      
        <input type="text" size="60" maxlength="60" name="PARTY_ATTRIBUTE_NAME" value="<%=UtilFormatOut.checkNull(name)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PartyAttribute (PARTY_ATTRIBUTE_ADMIN, or PARTY_ATTRIBUTE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PARTY_ATTRIBUTE_PARTY_ID" value="<%=partyId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_ID</td>
      <td>
        <b><%=partyId%></b> (This cannot be changed without re-creating the partyAttribute.)
      </td>
    </tr>
      <input type="hidden" name="PARTY_ATTRIBUTE_NAME" value="<%=name%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <b><%=name%></b> (This cannot be changed without re-creating the partyAttribute.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PartyAttribute (PARTY_ATTRIBUTE_ADMIN, or PARTY_ATTRIBUTE_UPDATE needed).
  <%}%>
<%} //end if partyAttribute == null %>

<%if(showFields){%>

  

  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>VALUE</td>
    <td>
    
      <input type="text" size="80" maxlength="255" name="PARTY_ATTRIBUTE_VALUE" value="<%if(partyAttribute!=null){%><%=UtilFormatOut.checkNull(partyAttribute.getValue())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_ATTRIBUTE_VALUE"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindPartyAttribute.jsp")%>" class="buttontext">[Find PartyAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyAttribute.jsp")%>" class="buttontext">[Create PartyAttribute]</a>
<%}%>
<%if(partyAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyAttribute.jsp?WEBEVENT=UPDATE_PARTY_ATTRIBUTE&UPDATE_MODE=DELETE&" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyId + "&" + "PARTY_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this PartyAttribute]</a>
  <%}%>
<%}%>
<%if(partyId != null && name != null){%>
  <a href="<%=response.encodeURL("ViewPartyAttribute.jsp?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyId + "&" + "PARTY_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[View PartyAttribute Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_ATTRIBUTE_ADMIN, PARTY_ATTRIBUTE_CREATE, or PARTY_ATTRIBUTE_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

