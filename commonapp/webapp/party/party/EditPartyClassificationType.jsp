
<%
/**
 *  Title: Party Classification Type Entity
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
 *@created    Wed Jul 04 01:03:16 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditPartyClassificationType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String partyClassificationTypeId = request.getParameter("PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID");  


  PartyClassificationType partyClassificationType = PartyClassificationTypeHelper.findByPrimaryKey(partyClassificationTypeId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    partyClassificationType = null;
  }
%>

<a href="<%=response.encodeURL("FindPartyClassificationType.jsp")%>" class="buttontext">[Find PartyClassificationType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyClassificationType.jsp")%>" class="buttontext">[Create PartyClassificationType]</a>
<%}%>
<%if(partyClassificationType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyClassificationType.jsp?WEBEVENT=UPDATE_PARTY_CLASSIFICATION_TYPE&UPDATE_MODE=DELETE&" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationTypeId)%>" class="buttontext">[Delete this PartyClassificationType]</a>
  <%}%>
<%}%>
<%if(partyClassificationTypeId != null){%>
  <a href="<%=response.encodeURL("ViewPartyClassificationType.jsp?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationTypeId)%>" class="buttontext">[View PartyClassificationType Details]</a>
<%}%>
<br>

<%if(partyClassificationType == null && (partyClassificationTypeId != null)){%>
    PartyClassificationType with (PARTY_CLASSIFICATION_TYPE_ID: <%=partyClassificationTypeId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditPartyClassificationType.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_PARTY_CLASSIFICATION_TYPE">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(partyClassificationType == null){%>
  <%if(hasCreatePermission){%>
    You may create a PartyClassificationType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_CLASSIFICATION_TYPE_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID" value="<%=UtilFormatOut.checkNull(partyClassificationTypeId)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PartyClassificationType (PARTY_CLASSIFICATION_TYPE_ADMIN, or PARTY_CLASSIFICATION_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID" value="<%=partyClassificationTypeId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_CLASSIFICATION_TYPE_ID</td>
      <td>
        <b><%=partyClassificationTypeId%></b> (This cannot be changed without re-creating the partyClassificationType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PartyClassificationType (PARTY_CLASSIFICATION_TYPE_ADMIN, or PARTY_CLASSIFICATION_TYPE_UPDATE needed).
  <%}%>
<%} //end if partyClassificationType == null %>

<%if(showFields){%>

  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>PARENT_TYPE_ID</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_TYPE_PARENT_TYPE_ID" value="<%if(partyClassificationType!=null){%><%=UtilFormatOut.checkNull(partyClassificationType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_CLASSIFICATION_TYPE_PARENT_TYPE_ID"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>HAS_TABLE</td>
    <td>
    
      <input type="text" size="0" maxlength="0" name="PARTY_CLASSIFICATION_TYPE_HAS_TABLE" value="<%if(partyClassificationType!=null){%><%=UtilFormatOut.checkNull(partyClassificationType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_CLASSIFICATION_TYPE_HAS_TABLE"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>DESCRIPTION</td>
    <td>
    
      <input type="text" size="80" maxlength="255" name="PARTY_CLASSIFICATION_TYPE_DESCRIPTION" value="<%if(partyClassificationType!=null){%><%=UtilFormatOut.checkNull(partyClassificationType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_CLASSIFICATION_TYPE_DESCRIPTION"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindPartyClassificationType.jsp")%>" class="buttontext">[Find PartyClassificationType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyClassificationType.jsp")%>" class="buttontext">[Create PartyClassificationType]</a>
<%}%>
<%if(partyClassificationType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyClassificationType.jsp?WEBEVENT=UPDATE_PARTY_CLASSIFICATION_TYPE&UPDATE_MODE=DELETE&" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationTypeId)%>" class="buttontext">[Delete this PartyClassificationType]</a>
  <%}%>
<%}%>
<%if(partyClassificationTypeId != null){%>
  <a href="<%=response.encodeURL("ViewPartyClassificationType.jsp?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationTypeId)%>" class="buttontext">[View PartyClassificationType Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_CLASSIFICATION_TYPE_ADMIN, PARTY_CLASSIFICATION_TYPE_CREATE, or PARTY_CLASSIFICATION_TYPE_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

