
<%
/**
 *  Title: Geographic Boundary Association Entity
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
<%pageContext.setAttribute("PageName", "ViewGeoAssoc"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("GEO_ASSOC", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("GEO_ASSOC", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("GEO_ASSOC", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("GEO_ASSOC", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String geoId = request.getParameter("GEO_ASSOC_GEO_ID");  
  String geoIdTo = request.getParameter("GEO_ASSOC_GEO_ID_TO");  


  GeoAssoc geoAssoc = GeoAssocHelper.findByPrimaryKey(geoId, geoIdTo);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View GeoAssoc</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit GeoAssoc</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: GeoAssoc with (GEO_ID, GEO_ID_TO: <%=geoId%>, <%=geoIdTo%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindGeoAssoc")%>" class="buttontext">[Find GeoAssoc]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewGeoAssoc")%>" class="buttontext">[Create New GeoAssoc]</a>
<%}%>
<%if(geoAssoc != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateGeoAssoc?UPDATE_MODE=DELETE&" + "GEO_ASSOC_GEO_ID=" + geoId + "&" + "GEO_ASSOC_GEO_ID_TO=" + geoIdTo)%>" class="buttontext">[Delete this GeoAssoc]</a>
  <%}%>
<%}%>

<%if(geoAssoc == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(geoAssoc == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified GeoAssoc was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoAssoc.getGeoId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID_TO</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoAssoc.getGeoIdTo())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ASSOC_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoAssoc.getGeoAssocTypeId())%>
    </td>
  </tr>

<%} //end if geoAssoc == null %>
</table>
  </div>
<%GeoAssoc geoAssocSave = geoAssoc;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(geoAssoc == null && (geoId != null || geoIdTo != null)){%>
    GeoAssoc with (GEO_ID, GEO_ID_TO: <%=geoId%>, <%=geoIdTo%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    geoAssoc = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateGeoAssoc")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(geoAssoc == null){%>
  <%if(hasCreatePermission){%>
    You may create a GeoAssoc by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GEO_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="GEO_ASSOC_GEO_ID" value="<%=UtilFormatOut.checkNull(geoId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GEO_ID_TO</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="GEO_ASSOC_GEO_ID_TO" value="<%=UtilFormatOut.checkNull(geoIdTo)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a GeoAssoc (GEO_ASSOC_ADMIN, or GEO_ASSOC_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="GEO_ASSOC_GEO_ID" value="<%=geoId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GEO_ID</td>
      <td>
        <b><%=geoId%></b> (This cannot be changed without re-creating the geoAssoc.)
      </td>
    </tr>
      <input type="hidden" name="GEO_ASSOC_GEO_ID_TO" value="<%=geoIdTo%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GEO_ID_TO</td>
      <td>
        <b><%=geoIdTo%></b> (This cannot be changed without re-creating the geoAssoc.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a GeoAssoc (GEO_ASSOC_ADMIN, or GEO_ASSOC_UPDATE needed).
  <%}%>
<%} //end if geoAssoc == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>GEO_ASSOC_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="GEO_ASSOC_GEO_ASSOC_TYPE_ID" value="<%if(geoAssoc!=null){%><%=UtilFormatOut.checkNull(geoAssoc.getGeoAssocTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("GEO_ASSOC_GEO_ASSOC_TYPE_ID"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && geoAssoc == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the geoAssoc for cases when removed to retain passed form values --%>
<%geoAssoc = geoAssocSave;%>

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
<%if(geoAssoc != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Main Geo</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Assoc Geo</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GEO_ASSOC_TYPE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> GeoAssocType</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for Geo, type: one --%>
<%if(geoAssoc != null){%>
  <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
    <%-- Geo geoRelated = GeoHelper.findByPrimaryKey(geoAssoc.getGeoId()); --%>
    <%Geo geoRelated = geoAssoc.getMainGeo();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Main</b> Related Entity: <b>Geo</b> with (GEO_ID: <%=geoAssoc.getGeoId()%>)
    </div>
    <%if(geoAssoc.getGeoId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + geoAssoc.getGeoId())%>" class="buttontext">[View Geo]</a>      
    <%if(geoRelated == null){%>
      <%if(Security.hasEntityPermission("GEO", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + geoAssoc.getGeoId())%>" class="buttontext">[Create Geo]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(geoRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Geo was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getGeoId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getGeoTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_CODE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getGeoCode())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ABBREVIATION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getAbbreviation())%>
    </td>
  </tr>

    <%} //end if geoRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Geo, type: one --%>
  

<%-- Start Relation for Geo, type: one --%>
<%if(geoAssoc != null){%>
  <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
    <%-- Geo geoRelated = GeoHelper.findByPrimaryKey(geoAssoc.getGeoIdTo()); --%>
    <%Geo geoRelated = geoAssoc.getAssocGeo();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Assoc</b> Related Entity: <b>Geo</b> with (GEO_ID: <%=geoAssoc.getGeoIdTo()%>)
    </div>
    <%if(geoAssoc.getGeoIdTo() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + geoAssoc.getGeoIdTo())%>" class="buttontext">[View Geo]</a>      
    <%if(geoRelated == null){%>
      <%if(Security.hasEntityPermission("GEO", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + geoAssoc.getGeoIdTo())%>" class="buttontext">[Create Geo]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(geoRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Geo was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getGeoId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getGeoTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_CODE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getGeoCode())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ABBREVIATION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoRelated.getAbbreviation())%>
    </td>
  </tr>

    <%} //end if geoRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Geo, type: one --%>
  

<%-- Start Relation for GeoAssocType, type: one --%>
<%if(geoAssoc != null){%>
  <%if(Security.hasEntityPermission("GEO_ASSOC_TYPE", "_VIEW", session)){%>
    <%-- GeoAssocType geoAssocTypeRelated = GeoAssocTypeHelper.findByPrimaryKey(geoAssoc.getGeoAssocTypeId()); --%>
    <%GeoAssocType geoAssocTypeRelated = geoAssoc.getGeoAssocType();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>GeoAssocType</b> with (GEO_ASSOC_TYPE_ID: <%=geoAssoc.getGeoAssocTypeId()%>)
    </div>
    <%if(geoAssoc.getGeoAssocTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeoAssocType?" + "GEO_ASSOC_TYPE_GEO_ASSOC_TYPE_ID=" + geoAssoc.getGeoAssocTypeId())%>" class="buttontext">[View GeoAssocType]</a>      
    <%if(geoAssocTypeRelated == null){%>
      <%if(Security.hasEntityPermission("GEO_ASSOC_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeoAssocType?" + "GEO_ASSOC_TYPE_GEO_ASSOC_TYPE_ID=" + geoAssoc.getGeoAssocTypeId())%>" class="buttontext">[Create GeoAssocType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(geoAssocTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified GeoAssocType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ASSOC_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoAssocTypeRelated.getGeoAssocTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(geoAssocTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if geoAssocTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for GeoAssocType, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (GEO_ASSOC_ADMIN, or GEO_ASSOC_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
