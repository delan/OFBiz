
<%
/**
 *  Title: Party Type Entity
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
 *@created    Fri Jul 06 18:25:19 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditPartyType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_TYPE", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String partyTypeId = request.getParameter("PARTY_TYPE_PARTY_TYPE_ID");  


  PartyType partyType = PartyTypeHelper.findByPrimaryKey(partyTypeId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    partyType = null;
  }
%>

<a href="<%=response.encodeURL("FindPartyType.jsp")%>" class="buttontext">[Find PartyType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyType.jsp")%>" class="buttontext">[Create PartyType]</a>
<%}%>
<%if(partyType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyType.jsp?WEBEVENT=UPDATE_PARTY_TYPE&UPDATE_MODE=DELETE&" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this PartyType]</a>
  <%}%>
<%}%>
<%if(partyTypeId != null){%>
  <a href="<%=response.encodeURL("ViewPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[View PartyType Details]</a>
<%}%>
<br>

<%if(partyType == null && (partyTypeId != null)){%>
    PartyType with (PARTY_TYPE_ID: <%=partyTypeId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditPartyType.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_PARTY_TYPE">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(partyType == null){%>
  <%if(hasCreatePermission){%>
    You may create a PartyType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_TYPE_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PARTY_TYPE_PARTY_TYPE_ID" value="<%=UtilFormatOut.checkNull(partyTypeId)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PartyType (PARTY_TYPE_ADMIN, or PARTY_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PARTY_TYPE_PARTY_TYPE_ID" value="<%=partyTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_TYPE_ID</td>
      <td>
        <b><%=partyTypeId%></b> (This cannot be changed without re-creating the partyType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PartyType (PARTY_TYPE_ADMIN, or PARTY_TYPE_UPDATE needed).
  <%}%>
<%} //end if partyType == null %>

<%if(showFields){%>

  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="PARTY_TYPE_PARENT_TYPE_ID" value="<%if(partyType!=null){%><%=UtilFormatOut.checkNull(partyType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_TYPE_PARENT_TYPE_ID"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
    
      <input type="text" size="1" maxlength="1" name="PARTY_TYPE_HAS_TABLE" value="<%if(partyType!=null){%><%=UtilFormatOut.checkNull(partyType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_TYPE_HAS_TABLE"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
    
      <input type="text" size="80" maxlength="255" name="PARTY_TYPE_DESCRIPTION" value="<%if(partyType!=null){%><%=UtilFormatOut.checkNull(partyType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_TYPE_DESCRIPTION"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindPartyType.jsp")%>" class="buttontext">[Find PartyType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyType.jsp")%>" class="buttontext">[Create PartyType]</a>
<%}%>
<%if(partyType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyType.jsp?WEBEVENT=UPDATE_PARTY_TYPE&UPDATE_MODE=DELETE&" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this PartyType]</a>
  <%}%>
<%}%>
<%if(partyTypeId != null){%>
  <a href="<%=response.encodeURL("ViewPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[View PartyType Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_TYPE_ADMIN, PARTY_TYPE_CREATE, or PARTY_TYPE_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

