
<%
/**
 *  Title: Cost Component Attribute Entity
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
 *@created    Fri Jul 27 01:37:15 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.cost.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewCostComponentAttribute"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String costComponentId = request.getParameter("COST_COMPONENT_ATTRIBUTE_COST_COMPONENT_ID");  
  String name = request.getParameter("COST_COMPONENT_ATTRIBUTE_NAME");  


  CostComponentAttribute costComponentAttribute = CostComponentAttributeHelper.findByPrimaryKey(costComponentId, name);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View CostComponentAttribute</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit CostComponentAttribute</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: CostComponentAttribute with (COST_COMPONENT_ID, NAME: <%=costComponentId%>, <%=name%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindCostComponentAttribute")%>" class="buttontext">[Find CostComponentAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentAttribute")%>" class="buttontext">[Create New CostComponentAttribute]</a>
<%}%>
<%if(costComponentAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponentAttribute?UPDATE_MODE=DELETE&" + "COST_COMPONENT_ATTRIBUTE_COST_COMPONENT_ID=" + costComponentId + "&" + "COST_COMPONENT_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this CostComponentAttribute]</a>
  <%}%>
<%}%>

<%if(costComponentAttribute == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(costComponentAttribute == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified CostComponentAttribute was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COST_COMPONENT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentAttribute.getCostComponentId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentAttribute.getName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>VALUE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentAttribute.getValue())%>
    </td>
  </tr>

<%} //end if costComponentAttribute == null %>
</table>
  </div>
<%CostComponentAttribute costComponentAttributeSave = costComponentAttribute;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(costComponentAttribute == null && (costComponentId != null || name != null)){%>
    CostComponentAttribute with (COST_COMPONENT_ID, NAME: <%=costComponentId%>, <%=name%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    costComponentAttribute = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateCostComponentAttribute")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(costComponentAttribute == null){%>
  <%if(hasCreatePermission){%>
    You may create a CostComponentAttribute by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>COST_COMPONENT_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="COST_COMPONENT_ATTRIBUTE_COST_COMPONENT_ID" value="<%=UtilFormatOut.checkNull(costComponentId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <input class='editInputBox' type="text" size="60" maxlength="60" name="COST_COMPONENT_ATTRIBUTE_NAME" value="<%=UtilFormatOut.checkNull(name)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a CostComponentAttribute (COST_COMPONENT_ATTRIBUTE_ADMIN, or COST_COMPONENT_ATTRIBUTE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="COST_COMPONENT_ATTRIBUTE_COST_COMPONENT_ID" value="<%=costComponentId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>COST_COMPONENT_ID</td>
      <td>
        <b><%=costComponentId%></b> (This cannot be changed without re-creating the costComponentAttribute.)
      </td>
    </tr>
      <input type="hidden" name="COST_COMPONENT_ATTRIBUTE_NAME" value="<%=name%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <b><%=name%></b> (This cannot be changed without re-creating the costComponentAttribute.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a CostComponentAttribute (COST_COMPONENT_ATTRIBUTE_ADMIN, or COST_COMPONENT_ATTRIBUTE_UPDATE needed).
  <%}%>
<%} //end if costComponentAttribute == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>VALUE</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="COST_COMPONENT_ATTRIBUTE_VALUE" value="<%if(costComponentAttribute!=null){%><%=UtilFormatOut.checkNull(costComponentAttribute.getValue())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("COST_COMPONENT_ATTRIBUTE_VALUE"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && costComponentAttribute == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the costComponentAttribute for cases when removed to retain passed form values --%>
<%costComponentAttribute = costComponentAttributeSave;%>

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
<%if(costComponentAttribute != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("COST_COMPONENT", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> CostComponent</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> CostComponentTypeAttr</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for CostComponent, type: one --%>
<%if(costComponentAttribute != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT", "_VIEW", session)){%>
    <%-- CostComponent costComponentRelated = CostComponentHelper.findByPrimaryKey(costComponentAttribute.getCostComponentId()); --%>
    <%CostComponent costComponentRelated = costComponentAttribute.getCostComponent();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>CostComponent</b> with (COST_COMPONENT_ID: <%=costComponentAttribute.getCostComponentId()%>)
    </div>
    <%if(costComponentAttribute.getCostComponentId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponent?" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentAttribute.getCostComponentId())%>" class="buttontext">[View CostComponent]</a>      
    <%if(costComponentRelated == null){%>
      <%if(Security.hasEntityPermission("COST_COMPONENT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewCostComponent?" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentAttribute.getCostComponentId())%>" class="buttontext">[Create CostComponent]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(costComponentRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified CostComponent was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COST_COMPONENT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentRelated.getCostComponentId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COST_COMPONENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentRelated.getCostComponentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentRelated.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentRelated.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentRelated.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponentRelated.getGeoId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_DATE</b></td>
    <td>
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
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>THRU_DATE</b></td>
    <td>
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
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COST</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(costComponentRelated.getCost())%>
    </td>
  </tr>

    <%} //end if costComponentRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for CostComponent, type: one --%>
  

<%-- Start Relation for CostComponentTypeAttr, type: many --%>
<%if(costComponentAttribute != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(CostComponentTypeAttrHelper.findByName(costComponentAttribute.getName())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(costComponentAttribute.getCostComponentTypeAttrs());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>CostComponentTypeAttr</b> with (NAME: <%=costComponentAttribute.getName()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentTypeAttr?" + "COST_COMPONENT_TYPE_ATTR_NAME=" + costComponentAttribute.getName())%>" class="buttontext">[Create CostComponentTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=Name";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + costComponentAttribute.getName();%>
    <a href="<%=response.encodeURL(controlPath + "/FindCostComponentAttribute?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find CostComponentTypeAttr]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>COST_COMPONENT_TYPE_ID</nobr></b></div></td>
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
        CostComponentTypeAttr costComponentTypeAttrRelated = (CostComponentTypeAttr)relatedIterator.next();
        if(costComponentTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentTypeAttrRelated.getCostComponentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentTypeAttrRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentTypeAttr?" + "COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID=" + costComponentTypeAttrRelated.getCostComponentTypeId() + "&" + "COST_COMPONENT_TYPE_ATTR_NAME=" + costComponentTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponentTypeAttr?" + "COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID=" + costComponentTypeAttrRelated.getCostComponentTypeId() + "&" + "COST_COMPONENT_TYPE_ATTR_NAME=" + costComponentTypeAttrRelated.getName() + "&" + "COST_COMPONENT_ATTRIBUTE_COST_COMPONENT_ID=" + costComponentId + "&" + "COST_COMPONENT_ATTRIBUTE_NAME=" + name + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No CostComponentTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for CostComponentTypeAttr, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (COST_COMPONENT_ATTRIBUTE_ADMIN, or COST_COMPONENT_ATTRIBUTE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
