
<%
/**
 *  Title: Geographic Boundary Entity
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
 *@created    Fri Jul 27 01:36:59 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.common.geo.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewGeo"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("GEO", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("GEO", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("GEO", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("GEO", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String geoId = request.getParameter("GEO_GEO_ID");  


  Geo geo = GeoHelper.findByPrimaryKey(geoId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View Geo</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit Geo</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: Geo with (GEO_ID: <%=geoId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindGeo")%>" class="buttontext">[Find Geo]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewGeo")%>" class="buttontext">[Create New Geo]</a>
<%}%>
<%if(geo != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateGeo?UPDATE_MODE=DELETE&" + "GEO_GEO_ID=" + geoId)%>" class="buttontext">[Delete this Geo]</a>
  <%}%>
<%}%>

<%if(geo == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(geo == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified Geo was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geo.getGeoId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geo.getGeoTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geo.getName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_CODE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geo.getGeoCode())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ABBREVIATION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geo.getAbbreviation())%>
    </td>
  </tr>

<%} //end if geo == null %>
</table>
  </div>
<%Geo geoSave = geo;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(geo == null && (geoId != null)){%>
    Geo with (GEO_ID: <%=geoId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    geo = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateGeo")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(geo == null){%>
  <%if(hasCreatePermission){%>
    You may create a Geo by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GEO_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="GEO_GEO_ID" value="<%=UtilFormatOut.checkNull(geoId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a Geo (GEO_ADMIN, or GEO_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="GEO_GEO_ID" value="<%=geoId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GEO_ID</td>
      <td>
        <b><%=geoId%></b> (This cannot be changed without re-creating the geo.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a Geo (GEO_ADMIN, or GEO_UPDATE needed).
  <%}%>
<%} //end if geo == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>GEO_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="GEO_GEO_TYPE_ID" value="<%if(geo!=null){%><%=UtilFormatOut.checkNull(geo.getGeoTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("GEO_GEO_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>NAME</td>
    <td>
      <input class='editInputBox' type="text" size="60" maxlength="60" name="GEO_NAME" value="<%if(geo!=null){%><%=UtilFormatOut.checkNull(geo.getName())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("GEO_NAME"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>GEO_CODE</td>
    <td>
      <input class='editInputBox' type="text" size="60" maxlength="60" name="GEO_GEO_CODE" value="<%if(geo!=null){%><%=UtilFormatOut.checkNull(geo.getGeoCode())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("GEO_GEO_CODE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>ABBREVIATION</td>
    <td>
      <input class='editInputBox' type="text" size="60" maxlength="60" name="GEO_ABBREVIATION" value="<%if(geo!=null){%><%=UtilFormatOut.checkNull(geo.getAbbreviation())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("GEO_ABBREVIATION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && geo == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the geo for cases when removed to retain passed form values --%>
<%geo = geoSave;%>

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
<%if(geo != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("GEO_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> GeoType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GEO_ASSOC", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Main GeoAssoc</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GEO_ASSOC", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk>Assoc GeoAssoc</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for GeoType, type: one --%>
<%if(geo != null){%>
  <%if(Security.hasEntityPermission("GEO_TYPE", "_VIEW", session)){%>
    <%-- GeoType geoTypeRelated = GeoTypeHelper.findByPrimaryKey(geo.getGeoTypeId()); --%>
    <%GeoType geoTypeRelated = geo.getGeoType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>GeoType</b> with (GEO_TYPE_ID: <%=geo.getGeoTypeId()%>)
    </div>
    <%if(geo.getGeoTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeoType?" + "GEO_TYPE_GEO_TYPE_ID=" + geo.getGeoTypeId())%>" class="buttontext">[View GeoType]</a>      
    <%if(geoTypeRelated == null){%>
      <%if(Security.hasEntityPermission("GEO_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeoType?" + "GEO_TYPE_GEO_TYPE_ID=" + geo.getGeoTypeId())%>" class="buttontext">[Create GeoType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(geoTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified GeoType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoTypeRelated.getGeoTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if geoTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for GeoType, type: one --%>
  

<%-- Start Relation for GeoAssoc, type: many --%>
<%if(geo != null){%>
  <%if(Security.hasEntityPermission("GEO_ASSOC", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(GeoAssocHelper.findByGeoId(geo.getGeoId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(geo.getMainGeoAssocs());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Main</b> Related Entities: <b>GeoAssoc</b> with (GEO_ID: <%=geo.getGeoId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("GEO_ASSOC", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("GEO_ASSOC", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("GEO_ASSOC", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeoAssoc?" + "GEO_ASSOC_GEO_ID=" + geo.getGeoId())%>" class="buttontext">[Create GeoAssoc]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=GeoId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + geo.getGeoId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindGeo?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find GeoAssoc]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>GEO_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GEO_ID_TO</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GEO_ASSOC_TYPE_ID</nobr></b></div></td>
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
        GeoAssoc geoAssocRelated = (GeoAssoc)relatedIterator.next();
        if(geoAssocRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(geoAssocRelated.getGeoId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(geoAssocRelated.getGeoIdTo())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(geoAssocRelated.getGeoAssocTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeoAssoc?" + "GEO_ASSOC_GEO_ID=" + geoAssocRelated.getGeoId() + "&" + "GEO_ASSOC_GEO_ID_TO=" + geoAssocRelated.getGeoIdTo())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateGeoAssoc?" + "GEO_ASSOC_GEO_ID=" + geoAssocRelated.getGeoId() + "&" + "GEO_ASSOC_GEO_ID_TO=" + geoAssocRelated.getGeoIdTo() + "&" + "GEO_GEO_ID=" + geoId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No GeoAssocs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for GeoAssoc, type: many --%>
  

<%-- Start Relation for GeoAssoc, type: many --%>
<%if(geo != null){%>
  <%if(Security.hasEntityPermission("GEO_ASSOC", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(GeoAssocHelper.findByGeoIdTo(geo.getGeoId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(geo.getAssocGeoAssocs());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Assoc</b> Related Entities: <b>GeoAssoc</b> with (GEO_ID_TO: <%=geo.getGeoId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("GEO_ASSOC", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("GEO_ASSOC", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("GEO_ASSOC", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeoAssoc?" + "GEO_ASSOC_GEO_ID_TO=" + geo.getGeoId())%>" class="buttontext">[Create GeoAssoc]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=GeoIdTo";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + geo.getGeoId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindGeo?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find GeoAssoc]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>GEO_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GEO_ID_TO</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GEO_ASSOC_TYPE_ID</nobr></b></div></td>
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
        GeoAssoc geoAssocRelated = (GeoAssoc)relatedIterator.next();
        if(geoAssocRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(geoAssocRelated.getGeoId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(geoAssocRelated.getGeoIdTo())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(geoAssocRelated.getGeoAssocTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeoAssoc?" + "GEO_ASSOC_GEO_ID=" + geoAssocRelated.getGeoId() + "&" + "GEO_ASSOC_GEO_ID_TO=" + geoAssocRelated.getGeoIdTo())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateGeoAssoc?" + "GEO_ASSOC_GEO_ID=" + geoAssocRelated.getGeoId() + "&" + "GEO_ASSOC_GEO_ID_TO=" + geoAssocRelated.getGeoIdTo() + "&" + "GEO_GEO_ID=" + geoId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No GeoAssocs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for GeoAssoc, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (GEO_ADMIN, or GEO_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
