
<%
/**
 *  Title: Facility Type Entity
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
<%pageContext.setAttribute("PageName", "ViewFacilityType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("FACILITY_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("FACILITY_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("FACILITY_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String facilityTypeId = request.getParameter("FACILITY_TYPE_FACILITY_TYPE_ID");  


  FacilityType facilityType = FacilityTypeHelper.findByPrimaryKey(facilityTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View FacilityType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit FacilityType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: FacilityType with (FACILITY_TYPE_ID: <%=facilityTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindFacilityType")%>" class="buttontext">[Find FacilityType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType")%>" class="buttontext">[Create New FacilityType]</a>
<%}%>
<%if(facilityType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateFacilityType?UPDATE_MODE=DELETE&" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityTypeId)%>" class="buttontext">[Delete this FacilityType]</a>
  <%}%>
<%}%>

<%if(facilityType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(facilityType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified FacilityType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityType.getFacilityTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityType.getDescription())%>
    </td>
  </tr>

<%} //end if facilityType == null %>
</table>
  </div>
<%FacilityType facilityTypeSave = facilityType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(facilityType == null && (facilityTypeId != null)){%>
    FacilityType with (FACILITY_TYPE_ID: <%=facilityTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    facilityType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateFacilityType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(facilityType == null){%>
  <%if(hasCreatePermission){%>
    You may create a FacilityType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="FACILITY_TYPE_FACILITY_TYPE_ID" value="<%=UtilFormatOut.checkNull(facilityTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a FacilityType (FACILITY_TYPE_ADMIN, or FACILITY_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="FACILITY_TYPE_FACILITY_TYPE_ID" value="<%=facilityTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>FACILITY_TYPE_ID</td>
      <td>
        <b><%=facilityTypeId%></b> (This cannot be changed without re-creating the facilityType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a FacilityType (FACILITY_TYPE_ADMIN, or FACILITY_TYPE_UPDATE needed).
  <%}%>
<%} //end if facilityType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="FACILITY_TYPE_PARENT_TYPE_ID" value="<%if(facilityType!=null){%><%=UtilFormatOut.checkNull(facilityType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("FACILITY_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="FACILITY_TYPE_HAS_TABLE" value="<%if(facilityType!=null){%><%=UtilFormatOut.checkNull(facilityType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("FACILITY_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="FACILITY_TYPE_DESCRIPTION" value="<%if(facilityType!=null){%><%=UtilFormatOut.checkNull(facilityType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("FACILITY_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && facilityType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the facilityType for cases when removed to retain passed form values --%>
<%facilityType = facilityTypeSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=4;
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
<%if(facilityType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent FacilityType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child FacilityType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> FacilityTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> Facility</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for FacilityType, type: one --%>
<%if(facilityType != null){%>
  <%if(Security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session)){%>
    <%-- FacilityType facilityTypeRelated = FacilityTypeHelper.findByPrimaryKey(facilityType.getParentTypeId()); --%>
    <%FacilityType facilityTypeRelated = facilityType.getParentFacilityType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>FacilityType</b> with (FACILITY_TYPE_ID: <%=facilityType.getParentTypeId()%>)
    </div>
    <%if(facilityType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType?" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityType.getParentTypeId())%>" class="buttontext">[View FacilityType]</a>      
    <%if(facilityTypeRelated == null){%>
      <%if(Security.hasEntityPermission("FACILITY_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType?" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityType.getParentTypeId())%>" class="buttontext">[Create FacilityType]</a>
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
  

<%-- Start Relation for FacilityType, type: many --%>
<%if(facilityType != null){%>
  <%if(Security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(FacilityTypeHelper.findByParentTypeId(facilityType.getFacilityTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facilityType.getChildFacilityTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>FacilityType</b> with (PARENT_TYPE_ID: <%=facilityType.getFacilityTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("FACILITY_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("FACILITY_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("FACILITY_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType?" + "FACILITY_TYPE_PARENT_TYPE_ID=" + facilityType.getFacilityTypeId())%>" class="buttontext">[Create FacilityType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facilityType.getFacilityTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacilityType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find FacilityType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>FACILITY_TYPE_ID</nobr></b></div></td>
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
        FacilityType facilityTypeRelated = (FacilityType)relatedIterator.next();
        if(facilityTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityTypeRelated.getFacilityTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(facilityTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType?" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityTypeRelated.getFacilityTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateFacilityType?" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityTypeRelated.getFacilityTypeId() + "&" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No FacilityTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for FacilityType, type: many --%>
  

<%-- Start Relation for FacilityTypeAttr, type: many --%>
<%if(facilityType != null){%>
  <%if(Security.hasEntityPermission("FACILITY_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(FacilityTypeAttrHelper.findByFacilityTypeId(facilityType.getFacilityTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facilityType.getFacilityTypeAttrs());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>FacilityTypeAttr</b> with (FACILITY_TYPE_ID: <%=facilityType.getFacilityTypeId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewFacilityTypeAttr?" + "FACILITY_TYPE_ATTR_FACILITY_TYPE_ID=" + facilityType.getFacilityTypeId())%>" class="buttontext">[Create FacilityTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=FacilityTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facilityType.getFacilityTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacilityType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find FacilityTypeAttr]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateFacilityTypeAttr?" + "FACILITY_TYPE_ATTR_FACILITY_TYPE_ID=" + facilityTypeAttrRelated.getFacilityTypeId() + "&" + "FACILITY_TYPE_ATTR_NAME=" + facilityTypeAttrRelated.getName() + "&" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  

<%-- Start Relation for Facility, type: many --%>
<%if(facilityType != null){%>
  <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(FacilityHelper.findByFacilityTypeId(facilityType.getFacilityTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(facilityType.getFacilitys());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>Facility</b> with (FACILITY_TYPE_ID: <%=facilityType.getFacilityTypeId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_TYPE_ID=" + facilityType.getFacilityTypeId())%>" class="buttontext">[Create Facility]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=FacilityTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + facilityType.getFacilityTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindFacilityType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find Facility]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateFacility?" + "FACILITY_FACILITY_ID=" + facilityRelated.getFacilityId() + "&" + "FACILITY_TYPE_FACILITY_TYPE_ID=" + facilityTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  <h3>You do not have permission to view this page (FACILITY_TYPE_ADMIN, or FACILITY_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
