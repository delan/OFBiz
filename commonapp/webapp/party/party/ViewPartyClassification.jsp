
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
 *@created    Fri Jul 27 01:37:02 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewPartyClassification"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String partyId = request.getParameter("PARTY_CLASSIFICATION_PARTY_ID");  
  String partyTypeId = request.getParameter("PARTY_CLASSIFICATION_PARTY_TYPE_ID");  


  PartyClassification partyClassification = PartyClassificationHelper.findByPrimaryKey(partyId, partyTypeId);
%>

<br>
<SCRIPT language='JavaScript'>  
function ShowViewTab(lname) 
{
    document.all.viewtab.className = (lname == 'view') ? 'ontab' : 'offtab';
    document.all.viewlnk.className = (lname == 'view') ? 'onlnk' : 'offlnk';
    document.all.viewarea.style.visibility = (lname == 'view') ? 'visible' : 'hidden';

    document.all.edittab.className = (lname == 'edit') ? 'ontab' : 'offtab';
    document.all.editlnk.className = (lname == 'edit') ? 'onlnk' : 'offlnk';
    document.all.editarea.style.visibility = (lname == 'edit') ? 'visible' : 'hidden';
}
</SCRIPT>
<table cellpadding='0' cellspacing='0'><tr>  
  <td id=viewtab class=ontab>
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View PartyClassification</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit PartyClassification</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: PartyClassification with (PARTY_ID, PARTY_TYPE_ID: <%=partyId%>, <%=partyTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindPartyClassification")%>" class="buttontext">[Find PartyClassification]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassification")%>" class="buttontext">[Create New PartyClassification]</a>
<%}%>
<%if(partyClassification != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdatePartyClassification?UPDATE_MODE=DELETE&" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this PartyClassification]</a>
  <%}%>
<%}%>

<%if(partyClassification == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(partyClassification == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified PartyClassification was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassification.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassification.getPartyTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_CLASSIFICATION_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassification.getPartyClassificationTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_DATE</b></td>
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
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>THRU_DATE</b></td>
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
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
    </td>
  </tr>

<%} //end if partyClassification == null %>
</table>
  </div>
<%PartyClassification partyClassificationSave = partyClassification;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(partyClassification == null && (partyId != null || partyTypeId != null)){%>
    PartyClassification with (PARTY_ID, PARTY_TYPE_ID: <%=partyId%>, <%=partyTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    partyClassification = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdatePartyClassification")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(partyClassification == null){%>
  <%if(hasCreatePermission){%>
    You may create a PartyClassification by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_PARTY_ID" value="<%=UtilFormatOut.checkNull(partyId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_PARTY_TYPE_ID" value="<%=UtilFormatOut.checkNull(partyTypeId)%>">
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
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_ID</td>
      <td>
        <b><%=partyId%></b> (This cannot be changed without re-creating the partyClassification.)
      </td>
    </tr>
      <input type="hidden" name="PARTY_CLASSIFICATION_PARTY_TYPE_ID" value="<%=partyTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
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

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_CLASSIFICATION_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_PARTY_CLASSIFICATION_TYPE_ID" value="<%if(partyClassification!=null){%><%=UtilFormatOut.checkNull(partyClassification.getPartyClassificationTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_CLASSIFICATION_PARTY_CLASSIFICATION_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
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
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PARTY_CLASSIFICATION_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PARTY_CLASSIFICATION_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PARTY_CLASSIFICATION_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
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
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PARTY_CLASSIFICATION_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PARTY_CLASSIFICATION_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PARTY_CLASSIFICATION_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>
  </div>
<%}%>
</div>
<%if((hasUpdatePermission || hasCreatePermission) && partyClassification == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the partyClassification for cases when removed to retain passed form values --%>
<%partyClassification = partyClassificationSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=5;
function ShowTab(lname) 
{
  for(inc=1; inc <= numTabs; inc++)
  {
    document.all['tab' + inc].className = (lname == 'tab' + inc) ? 'ontab' : 'offtab';
    document.all['lnk' + inc].className = (lname == 'tab' + inc) ? 'onlnk' : 'offlnk';
    document.all['area' + inc].style.visibility = (lname == 'tab' + inc) ? 'visible' : 'hidden';
  }
}
</SCRIPT>
<%if(partyClassification != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> Party</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> PartyType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> PartyClassificationType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> PartyTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_ATTRIBUTE", "_VIEW", session)){%>
      <td id=tab5 class=offtab>
        <a href='javascript:ShowTab("tab5")' id=lnk5 class=offlnk> PartyAttribute</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for Party, type: one --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%-- Party partyRelated = PartyHelper.findByPrimaryKey(partyClassification.getPartyId()); --%>
    <%Party partyRelated = partyClassification.getParty();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=partyClassification.getPartyId()%>)
    </div>
    <%if(partyClassification.getPartyId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + partyClassification.getPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + partyClassification.getPartyId())%>" class="buttontext">[Create Party]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Party was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyRelated.getPartyId())%>
    </td>
  </tr>

    <%} //end if partyRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Party, type: one --%>
  

<%-- Start Relation for PartyType, type: one --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <%-- PartyType partyTypeRelated = PartyTypeHelper.findByPrimaryKey(partyClassification.getPartyTypeId()); --%>
    <%PartyType partyTypeRelated = partyClassification.getPartyType();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>PartyType</b> with (PARTY_TYPE_ID: <%=partyClassification.getPartyTypeId()%>)
    </div>
    <%if(partyClassification.getPartyTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyType?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyClassification.getPartyTypeId())%>" class="buttontext">[View PartyType]</a>      
    <%if(partyTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyType?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyClassification.getPartyTypeId())%>" class="buttontext">[Create PartyType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified PartyType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getPartyTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if partyTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyType, type: one --%>
  

<%-- Start Relation for PartyClassificationType, type: one --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session)){%>
    <%-- PartyClassificationType partyClassificationTypeRelated = PartyClassificationTypeHelper.findByPrimaryKey(partyClassification.getPartyClassificationTypeId()); --%>
    <%PartyClassificationType partyClassificationTypeRelated = partyClassification.getPartyClassificationType();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>PartyClassificationType</b> with (PARTY_CLASSIFICATION_TYPE_ID: <%=partyClassification.getPartyClassificationTypeId()%>)
    </div>
    <%if(partyClassification.getPartyClassificationTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassificationType?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassification.getPartyClassificationTypeId())%>" class="buttontext">[View PartyClassificationType]</a>      
    <%if(partyClassificationTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassificationType?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassification.getPartyClassificationTypeId())%>" class="buttontext">[Create PartyClassificationType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyClassificationTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified PartyClassificationType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_CLASSIFICATION_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getPartyClassificationTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if partyClassificationTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyClassificationType, type: one --%>
  

<%-- Start Relation for PartyTypeAttr, type: many --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PartyTypeAttrHelper.findByPartyTypeId(partyClassification.getPartyTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(partyClassification.getPartyTypeAttrs());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PartyTypeAttr</b> with (PARTY_TYPE_ID: <%=partyClassification.getPartyTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyTypeAttr?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyClassification.getPartyTypeId())%>" class="buttontext">[Create PartyTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=PartyTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyClassification.getPartyTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindPartyClassification?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyTypeAttr]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>NAME</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(relatedDeletePerm){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
    <%
     int relatedLoopCount = 0;
     if(relatedIterator != null && relatedIterator.hasNext())
     {
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        relatedLoopCount++; //if(relatedLoopCount > 10) break;
        PartyTypeAttr partyTypeAttrRelated = (PartyTypeAttr)relatedIterator.next();
        if(partyTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyTypeAttrRelated.getPartyTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyTypeAttrRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyTypeAttr?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeAttrRelated.getPartyTypeId() + "&" + "PARTY_TYPE_ATTR_NAME=" + partyTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePartyTypeAttr?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeAttrRelated.getPartyTypeId() + "&" + "PARTY_TYPE_ATTR_NAME=" + partyTypeAttrRelated.getName() + "&" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No PartyTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyTypeAttr, type: many --%>
  

<%-- Start Relation for PartyAttribute, type: many --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY_ATTRIBUTE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PartyAttributeHelper.findByPartyId(partyClassification.getPartyId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(partyClassification.getPartyAttributes());%>
  <DIV id=area5 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PartyAttribute</b> with (PARTY_ID: <%=partyClassification.getPartyId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_ATTRIBUTE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_ATTRIBUTE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_ATTRIBUTE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyAttribute?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyClassification.getPartyId())%>" class="buttontext">[Create PartyAttribute]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=PartyId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyClassification.getPartyId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindPartyClassification?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyAttribute]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>NAME</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>VALUE</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(relatedDeletePerm){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
    <%
     int relatedLoopCount = 0;
     if(relatedIterator != null && relatedIterator.hasNext())
     {
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        relatedLoopCount++; //if(relatedLoopCount > 10) break;
        PartyAttribute partyAttributeRelated = (PartyAttribute)relatedIterator.next();
        if(partyAttributeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyAttributeRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyAttributeRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyAttributeRelated.getValue())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyAttribute?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyAttributeRelated.getPartyId() + "&" + "PARTY_ATTRIBUTE_NAME=" + partyAttributeRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePartyAttribute?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyAttributeRelated.getPartyId() + "&" + "PARTY_ATTRIBUTE_NAME=" + partyAttributeRelated.getName() + "&" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No PartyAttributes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyAttribute, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_CLASSIFICATION_ADMIN, or PARTY_CLASSIFICATION_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
