
<%
/**
 *  Title: Facility Contact Mechanism Entity
 *  Description: Data Type Of: Contact Mechanism
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
 *@created    Fri Jul 27 01:37:22 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.storage.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewFacilityContactMechanism"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String facilityId = request.getParameter("FACILITY_CONTACT_MECHANISM_FACILITY_ID");  
  String contactMechanismId = request.getParameter("FACILITY_CONTACT_MECHANISM_CONTACT_MECHANISM_ID");  


  FacilityContactMechanism facilityContactMechanism = FacilityContactMechanismHelper.findByPrimaryKey(facilityId, contactMechanismId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View FacilityContactMechanism</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit FacilityContactMechanism</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: FacilityContactMechanism with (FACILITY_ID, CONTACT_MECHANISM_ID: <%=facilityId%>, <%=contactMechanismId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindFacilityContactMechanism")%>" class="buttontext">[Find FacilityContactMechanism]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewFacilityContactMechanism")%>" class="buttontext">[Create New FacilityContactMechanism]</a>
<%}%>
<%if(facilityContactMechanism != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateFacilityContactMechanism?UPDATE_MODE=DELETE&" + "FACILITY_CONTACT_MECHANISM_FACILITY_ID=" + facilityId + "&" + "FACILITY_CONTACT_MECHANISM_CONTACT_MECHANISM_ID=" + contactMechanismId)%>" class="buttontext">[Delete this FacilityContactMechanism]</a>
  <%}%>
<%}%>

<%if(facilityContactMechanism == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(facilityContactMechanism == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified FacilityContactMechanism was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityContactMechanism.getFacilityId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTACT_MECHANISM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityContactMechanism.getContactMechanismId())%>
    </td>
  </tr>

<%} //end if facilityContactMechanism == null %>
</table>
  </div>
<%FacilityContactMechanism facilityContactMechanismSave = facilityContactMechanism;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(facilityContactMechanism == null && (facilityId != null || contactMechanismId != null)){%>
    FacilityContactMechanism with (FACILITY_ID, CONTACT_MECHANISM_ID: <%=facilityId%>, <%=contactMechanismId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    facilityContactMechanism = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateFacilityContactMechanism")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(facilityContactMechanism == null){%>
  <%if(hasCreatePermission){%>
    You may create a FacilityContactMechanism by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="FACILITY_CONTACT_MECHANISM_FACILITY_ID" value="<%=UtilFormatOut.checkNull(facilityId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>CONTACT_MECHANISM_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="FACILITY_CONTACT_MECHANISM_CONTACT_MECHANISM_ID" value="<%=UtilFormatOut.checkNull(contactMechanismId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a FacilityContactMechanism (FACILITY_CONTACT_MECHANISM_ADMIN, or FACILITY_CONTACT_MECHANISM_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="FACILITY_CONTACT_MECHANISM_FACILITY_ID" value="<%=facilityId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_ID</td>
      <td>
        <b><%=facilityId%></b> (This cannot be changed without re-creating the facilityContactMechanism.)
      </td>
    </tr>
      <input type="hidden" name="FACILITY_CONTACT_MECHANISM_CONTACT_MECHANISM_ID" value="<%=contactMechanismId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>CONTACT_MECHANISM_ID</td>
      <td>
        <b><%=contactMechanismId%></b> (This cannot be changed without re-creating the facilityContactMechanism.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a FacilityContactMechanism (FACILITY_CONTACT_MECHANISM_ADMIN, or FACILITY_CONTACT_MECHANISM_UPDATE needed).
  <%}%>
<%} //end if facilityContactMechanism == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>
  </div>
<%}%>
</div>
<%if((hasUpdatePermission || hasCreatePermission) && facilityContactMechanism == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the facilityContactMechanism for cases when removed to retain passed form values --%>
<%facilityContactMechanism = facilityContactMechanismSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=1;
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
<%if(facilityContactMechanism != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> Facility</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for Facility, type: one --%>
<%if(facilityContactMechanism != null){%>
  <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
    <%-- Facility facilityRelated = FacilityHelper.findByPrimaryKey(facilityContactMechanism.getFacilityId()); --%>
    <%Facility facilityRelated = facilityContactMechanism.getFacility();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Facility</b> with (FACILITY_ID: <%=facilityContactMechanism.getFacilityId()%>)
    </div>
    <%if(facilityContactMechanism.getFacilityId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + facilityContactMechanism.getFacilityId())%>" class="buttontext">[View Facility]</a>      
    <%if(facilityRelated == null){%>
      <%if(Security.hasEntityPermission("FACILITY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + facilityContactMechanism.getFacilityId())%>" class="buttontext">[Create Facility]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (FACILITY_CONTACT_MECHANISM_ADMIN, or FACILITY_CONTACT_MECHANISM_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
