
<%
/**
 *  Title: Unit Of Measure Conversion Type Entity
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
 *@created    Fri Jul 27 01:37:01 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.common.uom.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewUomConversion"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("UOM_CONVERSION", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("UOM_CONVERSION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("UOM_CONVERSION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("UOM_CONVERSION", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String uomId = request.getParameter("UOM_CONVERSION_UOM_ID");  
  String uomIdTo = request.getParameter("UOM_CONVERSION_UOM_ID_TO");  


  UomConversion uomConversion = UomConversionHelper.findByPrimaryKey(uomId, uomIdTo);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View UomConversion</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit UomConversion</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: UomConversion with (UOM_ID, UOM_ID_TO: <%=uomId%>, <%=uomIdTo%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindUomConversion")%>" class="buttontext">[Find UomConversion]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewUomConversion")%>" class="buttontext">[Create New UomConversion]</a>
<%}%>
<%if(uomConversion != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateUomConversion?UPDATE_MODE=DELETE&" + "UOM_CONVERSION_UOM_ID=" + uomId + "&" + "UOM_CONVERSION_UOM_ID_TO=" + uomIdTo)%>" class="buttontext">[Delete this UomConversion]</a>
  <%}%>
<%}%>

<%if(uomConversion == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(uomConversion == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified UomConversion was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomConversion.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID_TO</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomConversion.getUomIdTo())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONVERSION_FACTOR</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(uomConversion.getConversionFactor())%>
    </td>
  </tr>

<%} //end if uomConversion == null %>
</table>
  </div>
<%UomConversion uomConversionSave = uomConversion;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(uomConversion == null && (uomId != null || uomIdTo != null)){%>
    UomConversion with (UOM_ID, UOM_ID_TO: <%=uomId%>, <%=uomIdTo%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    uomConversion = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateUomConversion")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(uomConversion == null){%>
  <%if(hasCreatePermission){%>
    You may create a UomConversion by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>UOM_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="UOM_CONVERSION_UOM_ID" value="<%=UtilFormatOut.checkNull(uomId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>UOM_ID_TO</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="UOM_CONVERSION_UOM_ID_TO" value="<%=UtilFormatOut.checkNull(uomIdTo)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a UomConversion (UOM_CONVERSION_ADMIN, or UOM_CONVERSION_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="UOM_CONVERSION_UOM_ID" value="<%=uomId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>UOM_ID</td>
      <td>
        <b><%=uomId%></b> (This cannot be changed without re-creating the uomConversion.)
      </td>
    </tr>
      <input type="hidden" name="UOM_CONVERSION_UOM_ID_TO" value="<%=uomIdTo%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>UOM_ID_TO</td>
      <td>
        <b><%=uomIdTo%></b> (This cannot be changed without re-creating the uomConversion.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a UomConversion (UOM_CONVERSION_ADMIN, or UOM_CONVERSION_UPDATE needed).
  <%}%>
<%} //end if uomConversion == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>CONVERSION_FACTOR</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="UOM_CONVERSION_CONVERSION_FACTOR" value="<%if(uomConversion!=null){%><%=UtilFormatOut.formatQuantity(uomConversion.getConversionFactor())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("UOM_CONVERSION_CONVERSION_FACTOR"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && uomConversion == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the uomConversion for cases when removed to retain passed form values --%>
<%uomConversion = uomConversionSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=2;
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
<%if(uomConversion != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Main Uom</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>ConvTo Uom</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for Uom, type: one --%>
<%if(uomConversion != null){%>
  <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
    <%-- Uom uomRelated = UomHelper.findByPrimaryKey(uomConversion.getUomId()); --%>
    <%Uom uomRelated = uomConversion.getMainUom();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Main</b> Related Entity: <b>Uom</b> with (UOM_ID: <%=uomConversion.getUomId()%>)
    </div>
    <%if(uomConversion.getUomId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + uomConversion.getUomId())%>" class="buttontext">[View Uom]</a>      
    <%if(uomRelated == null){%>
      <%if(Security.hasEntityPermission("UOM", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + uomConversion.getUomId())%>" class="buttontext">[Create Uom]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(uomRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Uom was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomRelated.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomRelated.getUomTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ABBREVIATION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomRelated.getAbbreviation())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if uomRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Uom, type: one --%>
  

<%-- Start Relation for Uom, type: one --%>
<%if(uomConversion != null){%>
  <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
    <%-- Uom uomRelated = UomHelper.findByPrimaryKey(uomConversion.getUomIdTo()); --%>
    <%Uom uomRelated = uomConversion.getConvToUom();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>ConvTo</b> Related Entity: <b>Uom</b> with (UOM_ID: <%=uomConversion.getUomIdTo()%>)
    </div>
    <%if(uomConversion.getUomIdTo() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + uomConversion.getUomIdTo())%>" class="buttontext">[View Uom]</a>      
    <%if(uomRelated == null){%>
      <%if(Security.hasEntityPermission("UOM", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + uomConversion.getUomIdTo())%>" class="buttontext">[Create Uom]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(uomRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Uom was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomRelated.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomRelated.getUomTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ABBREVIATION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomRelated.getAbbreviation())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if uomRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Uom, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (UOM_CONVERSION_ADMIN, or UOM_CONVERSION_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
