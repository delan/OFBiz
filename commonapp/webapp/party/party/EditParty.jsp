
<%
/**
 *  Title: Party Entity
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
 *@created    Wed Jul 04 01:03:12 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditParty"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String partyId = request.getParameter("PARTY_PARTY_ID");  


  Party party = PartyHelper.findByPrimaryKey(partyId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    party = null;
  }
%>

<a href="<%=response.encodeURL("FindParty.jsp")%>" class="buttontext">[Find Party]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditParty.jsp")%>" class="buttontext">[Create Party]</a>
<%}%>
<%if(party != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditParty.jsp?WEBEVENT=UPDATE_PARTY&UPDATE_MODE=DELETE&" + "PARTY_PARTY_ID=" + partyId)%>" class="buttontext">[Delete this Party]</a>
  <%}%>
<%}%>
<%if(partyId != null){%>
  <a href="<%=response.encodeURL("ViewParty.jsp?" + "PARTY_PARTY_ID=" + partyId)%>" class="buttontext">[View Party Details]</a>
<%}%>
<br>

<%if(party == null && (partyId != null)){%>
    Party with (PARTY_ID: <%=partyId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditParty.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_PARTY">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(party == null){%>
  <%if(hasCreatePermission){%>
    You may create a Party by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PARTY_PARTY_ID" value="<%=UtilFormatOut.checkNull(partyId)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a Party (PARTY_ADMIN, or PARTY_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PARTY_PARTY_ID" value="<%=partyId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_ID</td>
      <td>
        <b><%=partyId%></b> (This cannot be changed without re-creating the party.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a Party (PARTY_ADMIN, or PARTY_UPDATE needed).
  <%}%>
<%} //end if party == null %>

<%if(showFields){%>

  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindParty.jsp")%>" class="buttontext">[Find Party]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditParty.jsp")%>" class="buttontext">[Create Party]</a>
<%}%>
<%if(party != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditParty.jsp?WEBEVENT=UPDATE_PARTY&UPDATE_MODE=DELETE&" + "PARTY_PARTY_ID=" + partyId)%>" class="buttontext">[Delete this Party]</a>
  <%}%>
<%}%>
<%if(partyId != null){%>
  <a href="<%=response.encodeURL("ViewParty.jsp?" + "PARTY_PARTY_ID=" + partyId)%>" class="buttontext">[View Party Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_ADMIN, PARTY_CREATE, or PARTY_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

