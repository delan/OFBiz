
<%
/**
 *  Title: Party Facility Entity
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
 *@created    Fri Jul 27 01:37:23 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.storage.*" %>

<%@ page import="org.ofbiz.commonapp.party.party.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewPartyFacility"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PARTY_FACILITY", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_FACILITY", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_FACILITY", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_FACILITY", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String partyId = request.getParameter("PARTY_FACILITY_PARTY_ID");  
  String facilityId = request.getParameter("PARTY_FACILITY_FACILITY_ID");  


  PartyFacility partyFacility = PartyFacilityHelper.findByPrimaryKey(partyId, facilityId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View PartyFacility</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit PartyFacility</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: PartyFacility with (PARTY_ID, FACILITY_ID: <%=partyId%>, <%=facilityId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindPartyFacility")%>" class="buttontext">[Find PartyFacility]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewPartyFacility")%>" class="buttontext">[Create New PartyFacility]</a>
<%}%>
<%if(partyFacility != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdatePartyFacility?UPDATE_MODE=DELETE&" + "PARTY_FACILITY_PARTY_ID=" + partyId + "&" + "PARTY_FACILITY_FACILITY_ID=" + facilityId)%>" class="buttontext">[Delete this PartyFacility]</a>
  <%}%>
<%}%>

<%if(partyFacility == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(partyFacility == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified PartyFacility was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyFacility.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyFacility.getFacilityId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ROLE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyFacility.getFacilityRoleTypeId())%>
    </td>
  </tr>

<%} //end if partyFacility == null %>
</table>
  </div>
<%PartyFacility partyFacilitySave = partyFacility;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(partyFacility == null && (partyId != null || facilityId != null)){%>
    PartyFacility with (PARTY_ID, FACILITY_ID: <%=partyId%>, <%=facilityId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    partyFacility = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdatePartyFacility")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(partyFacility == null){%>
  <%if(hasCreatePermission){%>
    You may create a PartyFacility by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PARTY_FACILITY_PARTY_ID" value="<%=UtilFormatOut.checkNull(partyId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PARTY_FACILITY_FACILITY_ID" value="<%=UtilFormatOut.checkNull(facilityId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PartyFacility (PARTY_FACILITY_ADMIN, or PARTY_FACILITY_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PARTY_FACILITY_PARTY_ID" value="<%=partyId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_ID</td>
      <td>
        <b><%=partyId%></b> (This cannot be changed without re-creating the partyFacility.)
      </td>
    </tr>
      <input type="hidden" name="PARTY_FACILITY_FACILITY_ID" value="<%=facilityId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_ID</td>
      <td>
        <b><%=facilityId%></b> (This cannot be changed without re-creating the partyFacility.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PartyFacility (PARTY_FACILITY_ADMIN, or PARTY_FACILITY_UPDATE needed).
  <%}%>
<%} //end if partyFacility == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FACILITY_ROLE_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PARTY_FACILITY_FACILITY_ROLE_TYPE_ID" value="<%if(partyFacility!=null){%><%=UtilFormatOut.checkNull(partyFacility.getFacilityRoleTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PARTY_FACILITY_FACILITY_ROLE_TYPE_ID"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && partyFacility == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the partyFacility for cases when removed to retain passed form values --%>
<%partyFacility = partyFacilitySave;%>

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
<%if(partyFacility != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> Party</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> Facility</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY_ROLE_TYPE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> FacilityRoleType</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for Party, type: one --%>
<%if(partyFacility != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%-- Party partyRelated = PartyHelper.findByPrimaryKey(partyFacility.getPartyId()); --%>
    <%Party partyRelated = partyFacility.getParty();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=partyFacility.getPartyId()%>)
    </div>
    <%if(partyFacility.getPartyId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + partyFacility.getPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + partyFacility.getPartyId())%>" class="buttontext">[Create Party]</a>
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
  

<%-- Start Relation for Facility, type: one --%>
<%if(partyFacility != null){%>
  <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
    <%-- Facility facilityRelated = FacilityHelper.findByPrimaryKey(partyFacility.getFacilityId()); --%>
    <%Facility facilityRelated = partyFacility.getFacility();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Facility</b> with (FACILITY_ID: <%=partyFacility.getFacilityId()%>)
    </div>
    <%if(partyFacility.getFacilityId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + partyFacility.getFacilityId())%>" class="buttontext">[View Facility]</a>      
    <%if(facilityRelated == null){%>
      <%if(Security.hasEntityPermission("FACILITY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + partyFacility.getFacilityId())%>" class="buttontext">[Create Facility]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(facilityRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Facility was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRelated.getFacilityId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRelated.getFacilityTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRelated.getFacilityName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SQUARE_FOOTAGE</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(facilityRelated.getSquareFootage())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if facilityRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Facility, type: one --%>
  

<%-- Start Relation for FacilityRoleType, type: one --%>
<%if(partyFacility != null){%>
  <%if(Security.hasEntityPermission("FACILITY_ROLE_TYPE", "_VIEW", session)){%>
    <%-- FacilityRoleType facilityRoleTypeRelated = FacilityRoleTypeHelper.findByPrimaryKey(partyFacility.getFacilityRoleTypeId()); --%>
    <%FacilityRoleType facilityRoleTypeRelated = partyFacility.getFacilityRoleType();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>FacilityRoleType</b> with (FACILITY_ROLE_TYPE_ID: <%=partyFacility.getFacilityRoleTypeId()%>)
    </div>
    <%if(partyFacility.getFacilityRoleTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityRoleType?" + "FACILITY_ROLE_TYPE_FACILITY_ROLE_TYPE_ID=" + partyFacility.getFacilityRoleTypeId())%>" class="buttontext">[View FacilityRoleType]</a>      
    <%if(facilityRoleTypeRelated == null){%>
      <%if(Security.hasEntityPermission("FACILITY_ROLE_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacilityRoleType?" + "FACILITY_ROLE_TYPE_FACILITY_ROLE_TYPE_ID=" + partyFacility.getFacilityRoleTypeId())%>" class="buttontext">[Create FacilityRoleType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(facilityRoleTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified FacilityRoleType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ROLE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRoleTypeRelated.getFacilityRoleTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRoleTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if facilityRoleTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for FacilityRoleType, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_FACILITY_ADMIN, or PARTY_FACILITY_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
