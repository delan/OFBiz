
<%
/**
 *  Title: Facility Type Attribute Entity
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
 *@created    Fri Jul 27 01:37:22 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.storage.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewFacilityTypeAttr"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String facilityTypeId = request.getParameter("FACILITY_TYPE_ATTR_FACILITY_TYPE_ID");  
  String name = request.getParameter("FACILITY_TYPE_ATTR_NAME");  


  FacilityTypeAttr facilityTypeAttr = FacilityTypeAttrHelper.findByPrimaryKey(facilityTypeId, name);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View FacilityTypeAttr</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit FacilityTypeAttr</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: FacilityTypeAttr with (FACILITY_TYPE_ID, NAME: <%=facilityTypeId%>, <%=name%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindFacilityTypeAttr")%>" class="buttontext">[Find FacilityTypeAttr]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewFacilityTypeAttr")%>" class="buttontext">[Create New FacilityTypeAttr]</a>
<%}%>
<%if(facilityTypeAttr != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateFacilityTypeAttr?UPDATE_MODE=DELETE&" + "FACILITY_TYPE_ATTR_FACILITY_TYPE_ID=" + facilityTypeId + "&" + "FACILITY_TYPE_ATTR_NAME=" + name)%>" class="buttontext">[Delete this FacilityTypeAttr]</a>
  <%}%>
<%}%>

<%if(facilityTypeAttr == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(facilityTypeAttr == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified FacilityTypeAttr was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityTypeAttr.getFacilityTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityTypeAttr.getName())%>
    </td>
  </tr>

<%} //end if facilityTypeAttr == null %>
</table>
  </div>
<%FacilityTypeAttr facilityTypeAttrSave = facilityTypeAttr;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(facilityTypeAttr == null && (facilityTypeId != null || name != null)){%>
    FacilityTypeAttr with (FACILITY_TYPE_ID, NAME: <%=facilityTypeId%>, <%=name%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    facilityTypeAttr = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateFacilityTypeAttr")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(facilityTypeAttr == null){%>
  <%if(hasCreatePermission){%>
    You may create a FacilityTypeAttr by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="FACILITY_TYPE_ATTR_FACILITY_TYPE_ID" value="<%=UtilFormatOut.checkNull(facilityTypeId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <input class='editInputBox' type="text" size="60" maxlength="60" name="FACILITY_TYPE_ATTR_NAME" value="<%=UtilFormatOut.checkNull(name)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a FacilityTypeAttr (FACILITY_TYPE_ATTR_ADMIN, or FACILITY_TYPE_ATTR_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="FACILITY_TYPE_ATTR_FACILITY_TYPE_ID" value="<%=facilityTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_TYPE_ID</td>
      <td>
        <b><%=facilityTypeId%></b> (This cannot be changed without re-creating the facilityTypeAttr.)
      </td>
    </tr>
      <input type="hidden" name="FACILITY_TYPE_ATTR_NAME" value="<%=name%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <b><%=name%></b> (This cannot be changed without re-creating the facilityTypeAttr.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a FacilityTypeAttr (FACILITY_TYPE_ATTR_ADMIN, or FACILITY_TYPE_ATTR_UPDATE needed).
  <%}%>
<%} //end if facilityTypeAttr == null %>

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
<%if((hasUpdatePermission || hasCreatePermission) && facilityTypeAttr == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the facilityTypeAttr for cases when removed to retain passed form values --%>
<%facilityTypeAttr = facilityTypeAttrSave;%>

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
<%if(facilityTypeAttr != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> FacilityType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY_ATTRIBUTE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> FacilityAttribute</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> Facility</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for FacilityType, type: one --%>
<%if(facilityTypeAttr != null){%>
  <%if(Security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session)){%>
    <%-- FacilityType facilityTypeRelated = FacilityTypeHelper.findByPrimaryKey(facilityTypeAttr.getFacilityTypeId()); --%>
    <%FacilityType facilityTypeRelated = facilityTypeAttr.getFacilityType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>FacilityType</b> with (FACILITY_TYPE_ID: <%=facilityTypeAttr.getFacilityTypeId()%>)
    </div>
    <%if(facilityTypeAttr.getFacilityTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType?" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityTypeAttr.getFacilityTypeId())%>" class="buttontext">[View FacilityType]</a>      
    <%if(facilityTypeRelated == null){%>
      <%if(Security.hasEntityPermission("FACILITY_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType?" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityTypeAttr.getFacilityTypeId())%>" class="buttontext">[Create FacilityType]</a>
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
  

<%-- Start Relation for FacilityAttribute, type: many --%>
<%if(facilityTypeAttr != null){%>
  <%if(Security.hasEntityPermission("FACILITY_ATTRIBUTE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(FacilityAttributeHelper.findByName(facilityTypeAttr.getName())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facilityTypeAttr.getFacilityAttributes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>FacilityAttribute</b> with (NAME: <%=facilityTypeAttr.getName()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityAttribute?" + "FACILITY_ATTRIBUTE_NAME=" + facilityTypeAttr.getName())%>" class="buttontext">[Create FacilityAttribute]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=Name";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facilityTypeAttr.getName();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacilityTypeAttr?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find FacilityAttribute]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateFacilityAttribute?" + "FACILITY_ATTRIBUTE_FACILITY_ID=" + facilityAttributeRelated.getFacilityId() + "&" + "FACILITY_ATTRIBUTE_NAME=" + facilityAttributeRelated.getName() + "&" + "FACILITY_TYPE_ATTR_FACILITY_TYPE_ID=" + facilityTypeId + "&" + "FACILITY_TYPE_ATTR_NAME=" + name + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  

<%-- Start Relation for Facility, type: many --%>
<%if(facilityTypeAttr != null){%>
  <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(FacilityHelper.findByFacilityTypeId(facilityTypeAttr.getFacilityTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facilityTypeAttr.getFacilitys());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>Facility</b> with (FACILITY_TYPE_ID: <%=facilityTypeAttr.getFacilityTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("FACILITY", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("FACILITY", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("FACILITY", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_TYPE_ID=" + facilityTypeAttr.getFacilityTypeId())%>" class="buttontext">[Create Facility]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=FacilityTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facilityTypeAttr.getFacilityTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacilityTypeAttr?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find Facility]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_NAME</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>SQUARE_FOOTAGE</nobr></b></div></td>
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
        Facility facilityRelated = (Facility)relatedIterator.next();
        if(facilityRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityRelated.getFacilityId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityRelated.getFacilityTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityRelated.getFacilityName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(facilityRelated.getSquareFootage())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + facilityRelated.getFacilityId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateFacility?" + "FACILITY_FACILITY_ID=" + facilityRelated.getFacilityId() + "&" + "FACILITY_TYPE_ATTR_FACILITY_TYPE_ID=" + facilityTypeId + "&" + "FACILITY_TYPE_ATTR_NAME=" + name + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="7">
<h3>No Facilitys Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for Facility, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (FACILITY_TYPE_ATTR_ADMIN, or FACILITY_TYPE_ATTR_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
