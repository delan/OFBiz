
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
 *@created    Fri Jul 27 01:37:02 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewPartyClassificationType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String partyClassificationTypeId = request.getParameter("PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID");  


  PartyClassificationType partyClassificationType = PartyClassificationTypeHelper.findByPrimaryKey(partyClassificationTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View PartyClassificationType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit PartyClassificationType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: PartyClassificationType with (PARTY_CLASSIFICATION_TYPE_ID: <%=partyClassificationTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindPartyClassificationType")%>" class="buttontext">[Find PartyClassificationType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassificationType")%>" class="buttontext">[Create New PartyClassificationType]</a>
<%}%>
<%if(partyClassificationType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdatePartyClassificationType?UPDATE_MODE=DELETE&" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationTypeId)%>" class="buttontext">[Delete this PartyClassificationType]</a>
  <%}%>
<%}%>

<%if(partyClassificationType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(partyClassificationType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified PartyClassificationType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_CLASSIFICATION_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassificationType.getPartyClassificationTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassificationType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassificationType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyClassificationType.getDescription())%>
    </td>
  </tr>

<%} //end if partyClassificationType == null %>
</table>
  </div>
<%PartyClassificationType partyClassificationTypeSave = partyClassificationType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(partyClassificationType == null && (partyClassificationTypeId != null)){%>
    PartyClassificationType with (PARTY_CLASSIFICATION_TYPE_ID: <%=partyClassificationTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    partyClassificationType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdatePartyClassificationType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(partyClassificationType == null){%>
  <%if(hasCreatePermission){%>
    You may create a PartyClassificationType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_CLASSIFICATION_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID" value="<%=UtilFormatOut.checkNull(partyClassificationTypeId)%>">
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
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
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

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PARTY_CLASSIFICATION_TYPE_PARENT_TYPE_ID" value="<%if(partyClassificationType!=null){%><%=UtilFormatOut.checkNull(partyClassificationType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_CLASSIFICATION_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="PARTY_CLASSIFICATION_TYPE_HAS_TABLE" value="<%if(partyClassificationType!=null){%><%=UtilFormatOut.checkNull(partyClassificationType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_CLASSIFICATION_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PARTY_CLASSIFICATION_TYPE_DESCRIPTION" value="<%if(partyClassificationType!=null){%><%=UtilFormatOut.checkNull(partyClassificationType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_CLASSIFICATION_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && partyClassificationType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the partyClassificationType for cases when removed to retain passed form values --%>
<%partyClassificationType = partyClassificationTypeSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=3;
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
<%if(partyClassificationType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent PartyClassificationType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child PartyClassificationType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> PartyClassification</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for PartyClassificationType, type: one --%>
<%if(partyClassificationType != null){%>
  <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session)){%>
    <%-- PartyClassificationType partyClassificationTypeRelated = PartyClassificationTypeHelper.findByPrimaryKey(partyClassificationType.getParentTypeId()); --%>
    <%PartyClassificationType partyClassificationTypeRelated = partyClassificationType.getParentPartyClassificationType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>PartyClassificationType</b> with (PARTY_CLASSIFICATION_TYPE_ID: <%=partyClassificationType.getParentTypeId()%>)
    </div>
    <%if(partyClassificationType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassificationType?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationType.getParentTypeId())%>" class="buttontext">[View PartyClassificationType]</a>      
    <%if(partyClassificationTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassificationType?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationType.getParentTypeId())%>" class="buttontext">[Create PartyClassificationType]</a>
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
  

<%-- Start Relation for PartyClassificationType, type: many --%>
<%if(partyClassificationType != null){%>
  <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PartyClassificationTypeHelper.findByParentTypeId(partyClassificationType.getPartyClassificationTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(partyClassificationType.getChildPartyClassificationTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>PartyClassificationType</b> with (PARENT_TYPE_ID: <%=partyClassificationType.getPartyClassificationTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassificationType?" + "PARTY_CLASSIFICATION_TYPE_PARENT_TYPE_ID=" + partyClassificationType.getPartyClassificationTypeId())%>" class="buttontext">[Create PartyClassificationType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyClassificationType.getPartyClassificationTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindPartyClassificationType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyClassificationType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_CLASSIFICATION_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>HAS_TABLE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>DESCRIPTION</nobr></b></div></td>
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
        PartyClassificationType partyClassificationTypeRelated = (PartyClassificationType)relatedIterator.next();
        if(partyClassificationTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getPartyClassificationTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassificationType?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationTypeRelated.getPartyClassificationTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePartyClassificationType?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationTypeRelated.getPartyClassificationTypeId() + "&" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No PartyClassificationTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyClassificationType, type: many --%>
  

<%-- Start Relation for PartyClassification, type: many --%>
<%if(partyClassificationType != null){%>
  <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PartyClassificationHelper.findByPartyClassificationTypeId(partyClassificationType.getPartyClassificationTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(partyClassificationType.getPartyClassifications());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PartyClassification</b> with (PARTY_CLASSIFICATION_TYPE_ID: <%=partyClassificationType.getPartyClassificationTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_CLASSIFICATION", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_CLASSIFICATION", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_CLASSIFICATION", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassification?" + "PARTY_CLASSIFICATION_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationType.getPartyClassificationTypeId())%>" class="buttontext">[Create PartyClassification]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=PartyClassificationTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyClassificationType.getPartyClassificationTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindPartyClassificationType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyClassification]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_CLASSIFICATION_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
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
        PartyClassification partyClassificationRelated = (PartyClassification)relatedIterator.next();
        if(partyClassificationRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyClassificationRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyClassificationRelated.getPartyTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyClassificationRelated.getPartyClassificationTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(partyClassificationRelated != null)
        {
          java.util.Date date = partyClassificationRelated.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(partyClassificationRelated != null)
        {
          java.util.Date date = partyClassificationRelated.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassification?" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyClassificationRelated.getPartyId() + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyClassificationRelated.getPartyTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePartyClassification?" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyClassificationRelated.getPartyId() + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyClassificationRelated.getPartyTypeId() + "&" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassificationTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="7">
<h3>No PartyClassifications Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyClassification, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_CLASSIFICATION_TYPE_ADMIN, or PARTY_CLASSIFICATION_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
