
<%
/**
 *  Title: Facility Entity
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
 *@created    Fri Jul 27 01:37:21 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.storage.*" %>

<%@ page import="org.ofbiz.commonapp.product.inventory.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewFacility"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("FACILITY", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("FACILITY", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("FACILITY", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("FACILITY", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String facilityId = request.getParameter("FACILITY_FACILITY_ID");  


  Facility facility = FacilityHelper.findByPrimaryKey(facilityId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View Facility</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit Facility</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: Facility with (FACILITY_ID: <%=facilityId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindFacility")%>" class="buttontext">[Find Facility]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewFacility")%>" class="buttontext">[Create New Facility]</a>
<%}%>
<%if(facility != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateFacility?UPDATE_MODE=DELETE&" + "FACILITY_FACILITY_ID=" + facilityId)%>" class="buttontext">[Delete this Facility]</a>
  <%}%>
<%}%>

<%if(facility == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(facility == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified Facility was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facility.getFacilityId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facility.getFacilityTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facility.getFacilityName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SQUARE_FOOTAGE</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(facility.getSquareFootage())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facility.getDescription())%>
    </td>
  </tr>

<%} //end if facility == null %>
</table>
  </div>
<%Facility facilitySave = facility;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(facility == null && (facilityId != null)){%>
    Facility with (FACILITY_ID: <%=facilityId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    facility = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateFacility")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(facility == null){%>
  <%if(hasCreatePermission){%>
    You may create a Facility by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="FACILITY_FACILITY_ID" value="<%=UtilFormatOut.checkNull(facilityId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a Facility (FACILITY_ADMIN, or FACILITY_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="FACILITY_FACILITY_ID" value="<%=facilityId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_ID</td>
      <td>
        <b><%=facilityId%></b> (This cannot be changed without re-creating the facility.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a Facility (FACILITY_ADMIN, or FACILITY_UPDATE needed).
  <%}%>
<%} //end if facility == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FACILITY_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="FACILITY_FACILITY_TYPE_ID" value="<%if(facility!=null){%><%=UtilFormatOut.checkNull(facility.getFacilityTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("FACILITY_FACILITY_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FACILITY_NAME</td>
    <td>
      <input class='editInputBox' type="text" size="60" maxlength="60" name="FACILITY_FACILITY_NAME" value="<%if(facility!=null){%><%=UtilFormatOut.checkNull(facility.getFacilityName())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("FACILITY_FACILITY_NAME"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>SQUARE_FOOTAGE</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="FACILITY_SQUARE_FOOTAGE" value="<%if(facility!=null){%><%=UtilFormatOut.formatQuantity(facility.getSquareFootage())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("FACILITY_SQUARE_FOOTAGE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="FACILITY_DESCRIPTION" value="<%if(facility!=null){%><%=UtilFormatOut.checkNull(facility.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("FACILITY_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && facility == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the facility for cases when removed to retain passed form values --%>
<%facility = facilitySave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=7;
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
<%if(facility != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> FacilityType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> FacilityTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY_ATTRIBUTE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> FacilityAttribute</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> InventoryItem</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("CONTAINER", "_VIEW", session)){%>
      <td id=tab5 class=offtab>
        <a href='javascript:ShowTab("tab5")' id=lnk5 class=offlnk> Container</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_VIEW", session)){%>
      <td id=tab6 class=offtab>
        <a href='javascript:ShowTab("tab6")' id=lnk6 class=offlnk> FacilityContactMechanism</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_FACILITY", "_VIEW", session)){%>
      <td id=tab7 class=offtab>
        <a href='javascript:ShowTab("tab7")' id=lnk7 class=offlnk> PartyFacility</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for FacilityType, type: one --%>
<%if(facility != null){%>
  <%if(Security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session)){%>
    <%-- FacilityType facilityTypeRelated = FacilityTypeHelper.findByPrimaryKey(facility.getFacilityTypeId()); --%>
    <%FacilityType facilityTypeRelated = facility.getFacilityType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>FacilityType</b> with (FACILITY_TYPE_ID: <%=facility.getFacilityTypeId()%>)
    </div>
    <%if(facility.getFacilityTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType?" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facility.getFacilityTypeId())%>" class="buttontext">[View FacilityType]</a>      
    <%if(facilityTypeRelated == null){%>
      <%if(Security.hasEntityPermission("FACILITY_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType?" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facility.getFacilityTypeId())%>" class="buttontext">[Create FacilityType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(facilityTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified FacilityType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityTypeRelated.getFacilityTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if facilityTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for FacilityType, type: one --%>
  

<%-- Start Relation for FacilityTypeAttr, type: many --%>
<%if(facility != null){%>
  <%if(Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(FacilityTypeAttrHelper.findByFacilityTypeId(facility.getFacilityTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facility.getFacilityTypeAttrs());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>FacilityTypeAttr</b> with (FACILITY_TYPE_ID: <%=facility.getFacilityTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityTypeAttr?" + "FACILITY_TYPE_ATTR_FACILITY_TYPE_ID=" + facility.getFacilityTypeId())%>" class="buttontext">[Create FacilityTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=FacilityTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facility.getFacilityTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacility?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find FacilityTypeAttr]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>FACILITY_TYPE_ID</nobr></b></div></td>
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
        FacilityTypeAttr facilityTypeAttrRelated = (FacilityTypeAttr)relatedIterator.next();
        if(facilityTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityTypeAttrRelated.getFacilityTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityTypeAttrRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacilityTypeAttr?" + "FACILITY_TYPE_ATTR_FACILITY_TYPE_ID=" + facilityTypeAttrRelated.getFacilityTypeId() + "&" + "FACILITY_TYPE_ATTR_NAME=" + facilityTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateFacilityTypeAttr?" + "FACILITY_TYPE_ATTR_FACILITY_TYPE_ID=" + facilityTypeAttrRelated.getFacilityTypeId() + "&" + "FACILITY_TYPE_ATTR_NAME=" + facilityTypeAttrRelated.getName() + "&" + "FACILITY_FACILITY_ID=" + facilityId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No FacilityTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for FacilityTypeAttr, type: many --%>
  

<%-- Start Relation for FacilityAttribute, type: many --%>
<%if(facility != null){%>
  <%if(Security.hasEntityPermission("FACILITY_ATTRIBUTE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(FacilityAttributeHelper.findByFacilityId(facility.getFacilityId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facility.getFacilityAttributes());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>FacilityAttribute</b> with (FACILITY_ID: <%=facility.getFacilityId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("FACILITY_ATTRIBUTE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("FACILITY_ATTRIBUTE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("FACILITY_ATTRIBUTE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityAttribute?" + "FACILITY_ATTRIBUTE_FACILITY_ID=" + facility.getFacilityId())%>" class="buttontext">[Create FacilityAttribute]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=FacilityId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facility.getFacilityId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacility?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find FacilityAttribute]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
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
        FacilityAttribute facilityAttributeRelated = (FacilityAttribute)relatedIterator.next();
        if(facilityAttributeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityAttributeRelated.getFacilityId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityAttributeRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityAttributeRelated.getValue())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacilityAttribute?" + "FACILITY_ATTRIBUTE_FACILITY_ID=" + facilityAttributeRelated.getFacilityId() + "&" + "FACILITY_ATTRIBUTE_NAME=" + facilityAttributeRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateFacilityAttribute?" + "FACILITY_ATTRIBUTE_FACILITY_ID=" + facilityAttributeRelated.getFacilityId() + "&" + "FACILITY_ATTRIBUTE_NAME=" + facilityAttributeRelated.getName() + "&" + "FACILITY_FACILITY_ID=" + facilityId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No FacilityAttributes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for FacilityAttribute, type: many --%>
  

<%-- Start Relation for InventoryItem, type: many --%>
<%if(facility != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemHelper.findByFacilityId(facility.getFacilityId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facility.getInventoryItems());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItem</b> with (FACILITY_ID: <%=facility.getFacilityId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("INVENTORY_ITEM", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("INVENTORY_ITEM", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("INVENTORY_ITEM", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_FACILITY_ID=" + facility.getFacilityId())%>" class="buttontext">[Create InventoryItem]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=FacilityId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facility.getFacilityId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacility?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItem]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>INVENTORY_ITEM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>INVENTORY_ITEM_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>STATUS_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>CONTAINER_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>LOT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>QUANTITY_ON_HAND</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>SERIAL_NUMBER</nobr></b></div></td>
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
        InventoryItem inventoryItemRelated = (InventoryItem)relatedIterator.next();
        if(inventoryItemRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getInventoryItemId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getInventoryItemTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getStatusTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getFacilityId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getContainerId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getLotId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getUomId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(inventoryItemRelated.getQuantityOnHand())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getSerialNumber())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemRelated.getInventoryItemId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemRelated.getInventoryItemId() + "&" + "FACILITY_FACILITY_ID=" + facilityId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="13">
<h3>No InventoryItems Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for InventoryItem, type: many --%>
  

<%-- Start Relation for Container, type: many --%>
<%if(facility != null){%>
  <%if(Security.hasEntityPermission("CONTAINER", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ContainerHelper.findByFacilityId(facility.getFacilityId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facility.getContainers());%>
  <DIV id=area5 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>Container</b> with (FACILITY_ID: <%=facility.getFacilityId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("CONTAINER", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("CONTAINER", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("CONTAINER", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewContainer?" + "CONTAINER_FACILITY_ID=" + facility.getFacilityId())%>" class="buttontext">[Create Container]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=FacilityId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facility.getFacilityId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacility?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find Container]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>CONTAINER_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>CONTAINER_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
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
        Container containerRelated = (Container)relatedIterator.next();
        if(containerRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(containerRelated.getContainerId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(containerRelated.getContainerTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(containerRelated.getFacilityId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewContainer?" + "CONTAINER_CONTAINER_ID=" + containerRelated.getContainerId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateContainer?" + "CONTAINER_CONTAINER_ID=" + containerRelated.getContainerId() + "&" + "FACILITY_FACILITY_ID=" + facilityId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No Containers Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for Container, type: many --%>
  

<%-- Start Relation for FacilityContactMechanism, type: many --%>
<%if(facility != null){%>
  <%if(Security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(FacilityContactMechanismHelper.findByFacilityId(facility.getFacilityId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facility.getFacilityContactMechanisms());%>
  <DIV id=area6 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>FacilityContactMechanism</b> with (FACILITY_ID: <%=facility.getFacilityId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityContactMechanism?" + "FACILITY_CONTACT_MECHANISM_FACILITY_ID=" + facility.getFacilityId())%>" class="buttontext">[Create FacilityContactMechanism]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=FacilityId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facility.getFacilityId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacility?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find FacilityContactMechanism]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>CONTACT_MECHANISM_ID</nobr></b></div></td>
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
        FacilityContactMechanism facilityContactMechanismRelated = (FacilityContactMechanism)relatedIterator.next();
        if(facilityContactMechanismRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityContactMechanismRelated.getFacilityId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityContactMechanismRelated.getContactMechanismId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacilityContactMechanism?" + "FACILITY_CONTACT_MECHANISM_FACILITY_ID=" + facilityContactMechanismRelated.getFacilityId() + "&" + "FACILITY_CONTACT_MECHANISM_CONTACT_MECHANISM_ID=" + facilityContactMechanismRelated.getContactMechanismId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateFacilityContactMechanism?" + "FACILITY_CONTACT_MECHANISM_FACILITY_ID=" + facilityContactMechanismRelated.getFacilityId() + "&" + "FACILITY_CONTACT_MECHANISM_CONTACT_MECHANISM_ID=" + facilityContactMechanismRelated.getContactMechanismId() + "&" + "FACILITY_FACILITY_ID=" + facilityId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No FacilityContactMechanisms Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for FacilityContactMechanism, type: many --%>
  

<%-- Start Relation for PartyFacility, type: many --%>
<%if(facility != null){%>
  <%if(Security.hasEntityPermission("PARTY_FACILITY", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PartyFacilityHelper.findByFacilityId(facility.getFacilityId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facility.getPartyFacilitys());%>
  <DIV id=area7 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PartyFacility</b> with (FACILITY_ID: <%=facility.getFacilityId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_FACILITY", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_FACILITY", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_FACILITY", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyFacility?" + "PARTY_FACILITY_FACILITY_ID=" + facility.getFacilityId())%>" class="buttontext">[Create PartyFacility]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=FacilityId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facility.getFacilityId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacility?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyFacility]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_ROLE_TYPE_ID</nobr></b></div></td>
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
        PartyFacility partyFacilityRelated = (PartyFacility)relatedIterator.next();
        if(partyFacilityRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyFacilityRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyFacilityRelated.getFacilityId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyFacilityRelated.getFacilityRoleTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyFacility?" + "PARTY_FACILITY_PARTY_ID=" + partyFacilityRelated.getPartyId() + "&" + "PARTY_FACILITY_FACILITY_ID=" + partyFacilityRelated.getFacilityId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePartyFacility?" + "PARTY_FACILITY_PARTY_ID=" + partyFacilityRelated.getPartyId() + "&" + "PARTY_FACILITY_FACILITY_ID=" + partyFacilityRelated.getFacilityId() + "&" + "FACILITY_FACILITY_ID=" + facilityId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No PartyFacilitys Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyFacility, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (FACILITY_ADMIN, or FACILITY_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
