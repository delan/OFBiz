
<%
/**
 *  Title: Unit Of Measure Entity
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
 *@created    Fri Jul 27 01:37:00 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.common.uom.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewUom"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("UOM", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("UOM", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("UOM", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("UOM", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String uomId = request.getParameter("UOM_UOM_ID");  


  Uom uom = UomHelper.findByPrimaryKey(uomId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View Uom</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit Uom</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: Uom with (UOM_ID: <%=uomId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindUom")%>" class="buttontext">[Find Uom]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewUom")%>" class="buttontext">[Create New Uom]</a>
<%}%>
<%if(uom != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateUom?UPDATE_MODE=DELETE&" + "UOM_UOM_ID=" + uomId)%>" class="buttontext">[Delete this Uom]</a>
  <%}%>
<%}%>

<%if(uom == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(uom == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified Uom was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uom.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uom.getUomTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ABBREVIATION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uom.getAbbreviation())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uom.getDescription())%>
    </td>
  </tr>

<%} //end if uom == null %>
</table>
  </div>
<%Uom uomSave = uom;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(uom == null && (uomId != null)){%>
    Uom with (UOM_ID: <%=uomId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    uom = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateUom")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(uom == null){%>
  <%if(hasCreatePermission){%>
    You may create a Uom by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>UOM_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="UOM_UOM_ID" value="<%=UtilFormatOut.checkNull(uomId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a Uom (UOM_ADMIN, or UOM_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="UOM_UOM_ID" value="<%=uomId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>UOM_ID</td>
      <td>
        <b><%=uomId%></b> (This cannot be changed without re-creating the uom.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a Uom (UOM_ADMIN, or UOM_UPDATE needed).
  <%}%>
<%} //end if uom == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>UOM_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="UOM_UOM_TYPE_ID" value="<%if(uom!=null){%><%=UtilFormatOut.checkNull(uom.getUomTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("UOM_UOM_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>ABBREVIATION</td>
    <td>
      <input class='editInputBox' type="text" size="60" maxlength="60" name="UOM_ABBREVIATION" value="<%if(uom!=null){%><%=UtilFormatOut.checkNull(uom.getAbbreviation())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("UOM_ABBREVIATION"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="UOM_DESCRIPTION" value="<%if(uom!=null){%><%=UtilFormatOut.checkNull(uom.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("UOM_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && uom == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the uom for cases when removed to retain passed form values --%>
<%uom = uomSave;%>

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
<%if(uom != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("UOM_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> UomType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("UOM_CONVERSION", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Main UomConversion</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("UOM_CONVERSION", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk>ConvTo UomConversion</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for UomType, type: one --%>
<%if(uom != null){%>
  <%if(Security.hasEntityPermission("UOM_TYPE", "_VIEW", session)){%>
    <%-- UomType uomTypeRelated = UomTypeHelper.findByPrimaryKey(uom.getUomTypeId()); --%>
    <%UomType uomTypeRelated = uom.getUomType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>UomType</b> with (UOM_TYPE_ID: <%=uom.getUomTypeId()%>)
    </div>
    <%if(uom.getUomTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUomType?" + "UOM_TYPE_UOM_TYPE_ID=" + uom.getUomTypeId())%>" class="buttontext">[View UomType]</a>      
    <%if(uomTypeRelated == null){%>
      <%if(Security.hasEntityPermission("UOM_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewUomType?" + "UOM_TYPE_UOM_TYPE_ID=" + uom.getUomTypeId())%>" class="buttontext">[Create UomType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(uomTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified UomType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomTypeRelated.getUomTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(uomTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if uomTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for UomType, type: one --%>
  

<%-- Start Relation for UomConversion, type: many --%>
<%if(uom != null){%>
  <%if(Security.hasEntityPermission("UOM_CONVERSION", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(UomConversionHelper.findByUomId(uom.getUomId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(uom.getMainUomConversions());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Main</b> Related Entities: <b>UomConversion</b> with (UOM_ID: <%=uom.getUomId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("UOM_CONVERSION", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("UOM_CONVERSION", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("UOM_CONVERSION", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUomConversion?" + "UOM_CONVERSION_UOM_ID=" + uom.getUomId())%>" class="buttontext">[Create UomConversion]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=UomId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + uom.getUomId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindUom?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find UomConversion]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UOM_ID_TO</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>CONVERSION_FACTOR</nobr></b></div></td>
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
        UomConversion uomConversionRelated = (UomConversion)relatedIterator.next();
        if(uomConversionRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(uomConversionRelated.getUomId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(uomConversionRelated.getUomIdTo())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(uomConversionRelated.getConversionFactor())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewUomConversion?" + "UOM_CONVERSION_UOM_ID=" + uomConversionRelated.getUomId() + "&" + "UOM_CONVERSION_UOM_ID_TO=" + uomConversionRelated.getUomIdTo())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateUomConversion?" + "UOM_CONVERSION_UOM_ID=" + uomConversionRelated.getUomId() + "&" + "UOM_CONVERSION_UOM_ID_TO=" + uomConversionRelated.getUomIdTo() + "&" + "UOM_UOM_ID=" + uomId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No UomConversions Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for UomConversion, type: many --%>
  

<%-- Start Relation for UomConversion, type: many --%>
<%if(uom != null){%>
  <%if(Security.hasEntityPermission("UOM_CONVERSION", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(UomConversionHelper.findByUomIdTo(uom.getUomId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(uom.getConvToUomConversions());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>ConvTo</b> Related Entities: <b>UomConversion</b> with (UOM_ID_TO: <%=uom.getUomId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("UOM_CONVERSION", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("UOM_CONVERSION", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("UOM_CONVERSION", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUomConversion?" + "UOM_CONVERSION_UOM_ID_TO=" + uom.getUomId())%>" class="buttontext">[Create UomConversion]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=UomIdTo";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + uom.getUomId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindUom?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find UomConversion]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UOM_ID_TO</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>CONVERSION_FACTOR</nobr></b></div></td>
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
        UomConversion uomConversionRelated = (UomConversion)relatedIterator.next();
        if(uomConversionRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(uomConversionRelated.getUomId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(uomConversionRelated.getUomIdTo())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(uomConversionRelated.getConversionFactor())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewUomConversion?" + "UOM_CONVERSION_UOM_ID=" + uomConversionRelated.getUomId() + "&" + "UOM_CONVERSION_UOM_ID_TO=" + uomConversionRelated.getUomIdTo())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateUomConversion?" + "UOM_CONVERSION_UOM_ID=" + uomConversionRelated.getUomId() + "&" + "UOM_CONVERSION_UOM_ID_TO=" + uomConversionRelated.getUomIdTo() + "&" + "UOM_UOM_ID=" + uomId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No UomConversions Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for UomConversion, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (UOM_ADMIN, or UOM_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
