
<%
/**
 *  Title: Cost Component Type Attribute Entity
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
 *@created    Fri Jul 27 01:37:16 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.cost.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewCostComponentTypeAttr"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String costComponentTypeId = request.getParameter("COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID");  
  String name = request.getParameter("COST_COMPONENT_TYPE_ATTR_NAME");  


  CostComponentTypeAttr costComponentTypeAttr = CostComponentTypeAttrHelper.findByPrimaryKey(costComponentTypeId, name);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View CostComponentTypeAttr</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit CostComponentTypeAttr</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: CostComponentTypeAttr with (COST_COMPONENT_TYPE_ID, NAME: <%=costComponentTypeId%>, <%=name%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindCostComponentTypeAttr")%>" class="buttontext">[Find CostComponentTypeAttr]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentTypeAttr")%>" class="buttontext">[Create New CostComponentTypeAttr]</a>
<%}%>
<%if(costComponentTypeAttr != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponentTypeAttr?UPDATE_MODE=DELETE&" + "COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID=" + costComponentTypeId + "&" + "COST_COMPONENT_TYPE_ATTR_NAME=" + name)%>" class="buttontext">[Delete this CostComponentTypeAttr]</a>
  <%}%>
<%}%>

<%if(costComponentTypeAttr == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(costComponentTypeAttr == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified CostComponentTypeAttr was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COST_COMPONENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentTypeAttr.getCostComponentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentTypeAttr.getName())%>
    </td>
  </tr>

<%} //end if costComponentTypeAttr == null %>
</table>
  </div>
<%CostComponentTypeAttr costComponentTypeAttrSave = costComponentTypeAttr;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(costComponentTypeAttr == null && (costComponentTypeId != null || name != null)){%>
    CostComponentTypeAttr with (COST_COMPONENT_TYPE_ID, NAME: <%=costComponentTypeId%>, <%=name%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    costComponentTypeAttr = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateCostComponentTypeAttr")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(costComponentTypeAttr == null){%>
  <%if(hasCreatePermission){%>
    You may create a CostComponentTypeAttr by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>COST_COMPONENT_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID" value="<%=UtilFormatOut.checkNull(costComponentTypeId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <input class='editInputBox' type="text" size="60" maxlength="60" name="COST_COMPONENT_TYPE_ATTR_NAME" value="<%=UtilFormatOut.checkNull(name)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a CostComponentTypeAttr (COST_COMPONENT_TYPE_ATTR_ADMIN, or COST_COMPONENT_TYPE_ATTR_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID" value="<%=costComponentTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>COST_COMPONENT_TYPE_ID</td>
      <td>
        <b><%=costComponentTypeId%></b> (This cannot be changed without re-creating the costComponentTypeAttr.)
      </td>
    </tr>
      <input type="hidden" name="COST_COMPONENT_TYPE_ATTR_NAME" value="<%=name%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <b><%=name%></b> (This cannot be changed without re-creating the costComponentTypeAttr.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a CostComponentTypeAttr (COST_COMPONENT_TYPE_ATTR_ADMIN, or COST_COMPONENT_TYPE_ATTR_UPDATE needed).
  <%}%>
<%} //end if costComponentTypeAttr == null %>

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
<%if((hasUpdatePermission || hasCreatePermission) && costComponentTypeAttr == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the costComponentTypeAttr for cases when removed to retain passed form values --%>
<%costComponentTypeAttr = costComponentTypeAttrSave;%>

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
<%if(costComponentTypeAttr != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> CostComponentType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> CostComponentAttribute</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("COST_COMPONENT", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> CostComponent</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for CostComponentType, type: one --%>
<%if(costComponentTypeAttr != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE", "_VIEW", session)){%>
    <%-- CostComponentType costComponentTypeRelated = CostComponentTypeHelper.findByPrimaryKey(costComponentTypeAttr.getCostComponentTypeId()); --%>
    <%CostComponentType costComponentTypeRelated = costComponentTypeAttr.getCostComponentType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>CostComponentType</b> with (COST_COMPONENT_TYPE_ID: <%=costComponentTypeAttr.getCostComponentTypeId()%>)
    </div>
    <%if(costComponentTypeAttr.getCostComponentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentType?" + "COST_COMPONENT_TYPE_COST_COMPONENT_TYPE_ID=" + costComponentTypeAttr.getCostComponentTypeId())%>" class="buttontext">[View CostComponentType]</a>      
    <%if(costComponentTypeRelated == null){%>
      <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentType?" + "COST_COMPONENT_TYPE_COST_COMPONENT_TYPE_ID=" + costComponentTypeAttr.getCostComponentTypeId())%>" class="buttontext">[Create CostComponentType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(costComponentTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified CostComponentType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COST_COMPONENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentTypeRelated.getCostComponentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if costComponentTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for CostComponentType, type: one --%>
  

<%-- Start Relation for CostComponentAttribute, type: many --%>
<%if(costComponentTypeAttr != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(CostComponentAttributeHelper.findByName(costComponentTypeAttr.getName())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(costComponentTypeAttr.getCostComponentAttributes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>CostComponentAttribute</b> with (NAME: <%=costComponentTypeAttr.getName()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentAttribute?" + "COST_COMPONENT_ATTRIBUTE_NAME=" + costComponentTypeAttr.getName())%>" class="buttontext">[Create CostComponentAttribute]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=Name";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + costComponentTypeAttr.getName();%>
    <a href="<%=response.encodeURL(controlPath + "/FindCostComponentTypeAttr?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find CostComponentAttribute]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>COST_COMPONENT_ID</nobr></b></div></td>
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
        CostComponentAttribute costComponentAttributeRelated = (CostComponentAttribute)relatedIterator.next();
        if(costComponentAttributeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentAttributeRelated.getCostComponentId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentAttributeRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentAttributeRelated.getValue())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentAttribute?" + "COST_COMPONENT_ATTRIBUTE_COST_COMPONENT_ID=" + costComponentAttributeRelated.getCostComponentId() + "&" + "COST_COMPONENT_ATTRIBUTE_NAME=" + costComponentAttributeRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponentAttribute?" + "COST_COMPONENT_ATTRIBUTE_COST_COMPONENT_ID=" + costComponentAttributeRelated.getCostComponentId() + "&" + "COST_COMPONENT_ATTRIBUTE_NAME=" + costComponentAttributeRelated.getName() + "&" + "COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID=" + costComponentTypeId + "&" + "COST_COMPONENT_TYPE_ATTR_NAME=" + name + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No CostComponentAttributes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for CostComponentAttribute, type: many --%>
  

<%-- Start Relation for CostComponent, type: many --%>
<%if(costComponentTypeAttr != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(CostComponentHelper.findByCostComponentTypeId(costComponentTypeAttr.getCostComponentTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(costComponentTypeAttr.getCostComponents());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>CostComponent</b> with (COST_COMPONENT_TYPE_ID: <%=costComponentTypeAttr.getCostComponentTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("COST_COMPONENT", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("COST_COMPONENT", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("COST_COMPONENT", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponent?" + "COST_COMPONENT_COST_COMPONENT_TYPE_ID=" + costComponentTypeAttr.getCostComponentTypeId())%>" class="buttontext">[Create CostComponent]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=CostComponentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + costComponentTypeAttr.getCostComponentTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindCostComponentTypeAttr?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find CostComponent]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>COST_COMPONENT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>COST_COMPONENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GEO_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>COST</nobr></b></div></td>
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
        CostComponent costComponentRelated = (CostComponent)relatedIterator.next();
        if(costComponentRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getCostComponentId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getCostComponentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getGeoId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(costComponentRelated != null)
        {
          java.util.Date date = costComponentRelated.getFromDate();
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
        if(costComponentRelated != null)
        {
          java.util.Date date = costComponentRelated.getThruDate();
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
      <%=UtilFormatOut.formatQuantity(costComponentRelated.getCost())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewCostComponent?" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentRelated.getCostComponentId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponent?" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentRelated.getCostComponentId() + "&" + "COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID=" + costComponentTypeId + "&" + "COST_COMPONENT_TYPE_ATTR_NAME=" + name + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="11">
<h3>No CostComponents Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for CostComponent, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (COST_COMPONENT_TYPE_ATTR_ADMIN, or COST_COMPONENT_TYPE_ATTR_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
