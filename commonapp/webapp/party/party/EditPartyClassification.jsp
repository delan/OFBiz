
<%
/**
 *  Title: Party Classification Entity
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

<%pageContext.setAttribute("PageName", "EditPartyClassification"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String partyId = request.getParameter("PARTY_CLASSIFICATION_PARTY_ID");  
  String partyTypeId = request.getParameter("PARTY_CLASSIFICATION_PARTY_TYPE_ID");  


  PartyClassification partyClassification = PartyClassificationHelper.findByPrimaryKey(partyId, partyTypeId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    partyClassification = null;
  }
%>

<a href="<%=response.encodeURL("FindPartyClassification.jsp")%>" class="buttontext">[Find PartyClassification]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyClassification.jsp")%>" class="buttontext">[Create PartyClassification]</a>
<%}%>
<%if(partyClassification != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyClassification.jsp?WEBEVENT=UPDATE_PARTY_CLASSIFICATION&UPDATE_MODE=DELETE&" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this PartyClassification]</a>
  <%}%>
<%}%>
<%if(partyId != null && partyTypeId != null){%>
  <a href="<%=response.encodeURL("ViewPartyClassification.jsp?" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[View PartyClassification Details]</a>
<%}%>
<br>

<%if(partyClassification == null && (partyId != null || partyTypeId != null)){%>
    PartyClassification with (PARTY_ID, PARTY_TYPE_ID: <%=partyId%>, <%=partyTypeId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditPartyClassification.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_PARTY_CLASSIFICATION">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(partyClassification == null){%>
  <%if(hasCreatePermission){%>
    You may create a PartyClassification by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_PARTY_ID" value="<%=UtilFormatOut.checkNull(partyId)%>">
      

      </td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_TYPE_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_PARTY_TYPE_ID" value="<%=UtilFormatOut.checkNull(partyTypeId)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PartyClassification (PARTY_CLASSIFICATION_ADMIN, or PARTY_CLASSIFICATION_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PARTY_CLASSIFICATION_PARTY_ID" value="<%=partyId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_ID</td>
      <td>
        <b><%=partyId%></b> (This cannot be changed without re-creating the partyClassification.)
      </td>
    </tr>
      <input type="hidden" name="PARTY_CLASSIFICATION_PARTY_TYPE_ID" value="<%=partyTypeId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PARTY_TYPE_ID</td>
      <td>
        <b><%=partyTypeId%></b> (This cannot be changed without re-creating the partyClassification.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PartyClassification (PARTY_CLASSIFICATION_ADMIN, or PARTY_CLASSIFICATION_UPDATE needed).
  <%}%>
<%} //end if partyClassification == null %>

<%if(showFields){%>

  

  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>PARTY_CLASSIFICATION_TYPE_ID</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_PARTY_CLASSIFICATION_TYPE_ID" value="<%if(partyClassification!=null){%><%=UtilFormatOut.checkNull(partyClassification.getPartyClassificationTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_CLASSIFICATION_PARTY_CLASSIFICATION_TYPE_ID"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>FROM_DATE</td>
    <td>
    
      <%{
        String dateString = null;
        String timeString = null;
        if(partyClassification != null)
        {
          java.util.Date date = partyClassification.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PARTY_CLASSIFICATION_FROM_DATE_DATE");
          timeString = request.getParameter("PARTY_CLASSIFICATION_FROM_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input type="text" name="PARTY_CLASSIFICATION_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PARTY_CLASSIFICATION_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input type="text" size="6" maxlength="10" name="PARTY_CLASSIFICATION_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    
    </td>
  </tr>
  

  
  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td>THRU_DATE</td>
    <td>
    
      <%{
        String dateString = null;
        String timeString = null;
        if(partyClassification != null)
        {
          java.util.Date date = partyClassification.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PARTY_CLASSIFICATION_THRU_DATE_DATE");
          timeString = request.getParameter("PARTY_CLASSIFICATION_THRU_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input type="text" name="PARTY_CLASSIFICATION_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PARTY_CLASSIFICATION_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input type="text" size="6" maxlength="10" name="PARTY_CLASSIFICATION_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    
    </td>
  </tr>
  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindPartyClassification.jsp")%>" class="buttontext">[Find PartyClassification]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyClassification.jsp")%>" class="buttontext">[Create PartyClassification]</a>
<%}%>
<%if(partyClassification != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyClassification.jsp?WEBEVENT=UPDATE_PARTY_CLASSIFICATION&UPDATE_MODE=DELETE&" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this PartyClassification]</a>
  <%}%>
<%}%>
<%if(partyId != null && partyTypeId != null){%>
  <a href="<%=response.encodeURL("ViewPartyClassification.jsp?" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[View PartyClassification Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_CLASSIFICATION_ADMIN, PARTY_CLASSIFICATION_CREATE, or PARTY_CLASSIFICATION_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

