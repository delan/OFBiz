
<%
/**
 *  Title: Cost Component Entity
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

<%@ page import="org.ofbiz.commonapp.product.product.*" %>
<%@ page import="org.ofbiz.commonapp.product.feature.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>
<%@ page import="org.ofbiz.commonapp.common.geo.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewCostComponent"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("COST_COMPONENT", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("COST_COMPONENT", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("COST_COMPONENT", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("COST_COMPONENT", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String costComponentId = request.getParameter("COST_COMPONENT_COST_COMPONENT_ID");  


  CostComponent costComponent = CostComponentHelper.findByPrimaryKey(costComponentId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View CostComponent</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit CostComponent</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: CostComponent with (COST_COMPONENT_ID: <%=costComponentId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindCostComponent")%>" class="buttontext">[Find CostComponent]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewCostComponent")%>" class="buttontext">[Create New CostComponent]</a>
<%}%>
<%if(costComponent != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponent?UPDATE_MODE=DELETE&" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentId)%>" class="buttontext">[Delete this CostComponent]</a>
  <%}%>
<%}%>

<%if(costComponent == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(costComponent == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified CostComponent was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COST_COMPONENT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponent.getCostComponentId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COST_COMPONENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponent.getCostComponentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponent.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponent.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponent.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(costComponent.getGeoId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(costComponent != null)
        {
          java.util.Date date = costComponent.getFromDate();
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
        if(costComponent != null)
        {
          java.util.Date date = costComponent.getThruDate();
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
      <%=UtilFormatOut.formatQuantity(costComponent.getCost())%>
    </td>
  </tr>

<%} //end if costComponent == null %>
</table>
  </div>
<%CostComponent costComponentSave = costComponent;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(costComponent == null && (costComponentId != null)){%>
    CostComponent with (COST_COMPONENT_ID: <%=costComponentId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    costComponent = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateCostComponent")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(costComponent == null){%>
  <%if(hasCreatePermission){%>
    You may create a CostComponent by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>COST_COMPONENT_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="COST_COMPONENT_COST_COMPONENT_ID" value="<%=UtilFormatOut.checkNull(costComponentId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a CostComponent (COST_COMPONENT_ADMIN, or COST_COMPONENT_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="COST_COMPONENT_COST_COMPONENT_ID" value="<%=costComponentId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>COST_COMPONENT_ID</td>
      <td>
        <b><%=costComponentId%></b> (This cannot be changed without re-creating the costComponent.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a CostComponent (COST_COMPONENT_ADMIN, or COST_COMPONENT_UPDATE needed).
  <%}%>
<%} //end if costComponent == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>COST_COMPONENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="COST_COMPONENT_COST_COMPONENT_TYPE_ID" value="<%if(costComponent!=null){%><%=UtilFormatOut.checkNull(costComponent.getCostComponentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("COST_COMPONENT_COST_COMPONENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="COST_COMPONENT_PRODUCT_ID" value="<%if(costComponent!=null){%><%=UtilFormatOut.checkNull(costComponent.getProductId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("COST_COMPONENT_PRODUCT_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_FEATURE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="COST_COMPONENT_PRODUCT_FEATURE_ID" value="<%if(costComponent!=null){%><%=UtilFormatOut.checkNull(costComponent.getProductFeatureId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("COST_COMPONENT_PRODUCT_FEATURE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="COST_COMPONENT_PARTY_ID" value="<%if(costComponent!=null){%><%=UtilFormatOut.checkNull(costComponent.getPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("COST_COMPONENT_PARTY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>GEO_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="COST_COMPONENT_GEO_ID" value="<%if(costComponent!=null){%><%=UtilFormatOut.checkNull(costComponent.getGeoId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("COST_COMPONENT_GEO_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FROM_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(costComponent != null)
        {
          java.util.Date date = costComponent.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("COST_COMPONENT_FROM_DATE_DATE");
          timeString = request.getParameter("COST_COMPONENT_FROM_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="COST_COMPONENT_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.COST_COMPONENT_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="COST_COMPONENT_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>THRU_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(costComponent != null)
        {
          java.util.Date date = costComponent.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("COST_COMPONENT_THRU_DATE_DATE");
          timeString = request.getParameter("COST_COMPONENT_THRU_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="COST_COMPONENT_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.COST_COMPONENT_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="COST_COMPONENT_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>COST</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="COST_COMPONENT_COST" value="<%if(costComponent!=null){%><%=UtilFormatOut.formatQuantity(costComponent.getCost())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("COST_COMPONENT_COST"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && costComponent == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the costComponent for cases when removed to retain passed form values --%>
<%costComponent = costComponentSave;%>

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
<%if(costComponent != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> CostComponentType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> CostComponentTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> CostComponentAttribute</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> Product</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
      <td id=tab5 class=offtab>
        <a href='javascript:ShowTab("tab5")' id=lnk5 class=offlnk> ProductFeature</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
      <td id=tab6 class=offtab>
        <a href='javascript:ShowTab("tab6")' id=lnk6 class=offlnk> Party</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
      <td id=tab7 class=offtab>
        <a href='javascript:ShowTab("tab7")' id=lnk7 class=offlnk> Geo</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for CostComponentType, type: one --%>
<%if(costComponent != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE", "_VIEW", session)){%>
    <%-- CostComponentType costComponentTypeRelated = CostComponentTypeHelper.findByPrimaryKey(costComponent.getCostComponentTypeId()); --%>
    <%CostComponentType costComponentTypeRelated = costComponent.getCostComponentType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>CostComponentType</b> with (COST_COMPONENT_TYPE_ID: <%=costComponent.getCostComponentTypeId()%>)
    </div>
    <%if(costComponent.getCostComponentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentType?" + "COST_COMPONENT_TYPE_COST_COMPONENT_TYPE_ID=" + costComponent.getCostComponentTypeId())%>" class="buttontext">[View CostComponentType]</a>      
    <%if(costComponentTypeRelated == null){%>
      <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentType?" + "COST_COMPONENT_TYPE_COST_COMPONENT_TYPE_ID=" + costComponent.getCostComponentTypeId())%>" class="buttontext">[Create CostComponentType]</a>
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
  

<%-- Start Relation for CostComponentTypeAttr, type: many --%>
<%if(costComponent != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(CostComponentTypeAttrHelper.findByCostComponentTypeId(costComponent.getCostComponentTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(costComponent.getCostComponentTypeAttrs());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>CostComponentTypeAttr</b> with (COST_COMPONENT_TYPE_ID: <%=costComponent.getCostComponentTypeId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentTypeAttr?" + "COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID=" + costComponent.getCostComponentTypeId())%>" class="buttontext">[Create CostComponentTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=CostComponentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + costComponent.getCostComponentTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindCostComponent?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find CostComponentTypeAttr]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponentTypeAttr?" + "COST_COMPONENT_TYPE_ATTR_COST_COMPONENT_TYPE_ID=" + costComponentTypeAttrRelated.getCostComponentTypeId() + "&" + "COST_COMPONENT_TYPE_ATTR_NAME=" + costComponentTypeAttrRelated.getName() + "&" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  

<%-- Start Relation for CostComponentAttribute, type: many --%>
<%if(costComponent != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(CostComponentAttributeHelper.findByCostComponentId(costComponent.getCostComponentId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(costComponent.getCostComponentAttributes());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>CostComponentAttribute</b> with (COST_COMPONENT_ID: <%=costComponent.getCostComponentId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentAttribute?" + "COST_COMPONENT_ATTRIBUTE_COST_COMPONENT_ID=" + costComponent.getCostComponentId())%>" class="buttontext">[Create CostComponentAttribute]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=CostComponentId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + costComponent.getCostComponentId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindCostComponent?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find CostComponentAttribute]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponentAttribute?" + "COST_COMPONENT_ATTRIBUTE_COST_COMPONENT_ID=" + costComponentAttributeRelated.getCostComponentId() + "&" + "COST_COMPONENT_ATTRIBUTE_NAME=" + costComponentAttributeRelated.getName() + "&" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  

<%-- Start Relation for Product, type: one --%>
<%if(costComponent != null){%>
  <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
    <%-- Product productRelated = ProductHelper.findByPrimaryKey(costComponent.getProductId()); --%>
    <%Product productRelated = costComponent.getProduct();%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Product</b> with (PRODUCT_ID: <%=costComponent.getProductId()%>)
    </div>
    <%if(costComponent.getProductId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + costComponent.getProductId())%>" class="buttontext">[View Product]</a>      
    <%if(productRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + costComponent.getProductId())%>" class="buttontext">[Create Product]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Product was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRIMARY_PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getPrimaryProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>MANUFACTURER_PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getManufacturerPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>QUANTITY_INCLUDED</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(productRelated.getQuantityIncluded())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INTRODUCTION_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(productRelated != null)
        {
          java.util.Date date = productRelated.getIntroductionDate();
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
    <td><b>SALES_DISCONTINUATION_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(productRelated != null)
        {
          java.util.Date date = productRelated.getSalesDiscontinuationDate();
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
    <td><b>SUPPORT_DISCONTINUATION_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(productRelated != null)
        {
          java.util.Date date = productRelated.getSupportDiscontinuationDate();
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
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COMMENT</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getComment())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getDescription())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>LONG_DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getLongDescription())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SMALL_IMAGE_URL</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getSmallImageUrl())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>LARGE_IMAGE_URL</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productRelated.getLargeImageUrl())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DEFAULT_PRICE</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(productRelated.getDefaultPrice())%>
    </td>
  </tr>

    <%} //end if productRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Product, type: one --%>
  

<%-- Start Relation for ProductFeature, type: one --%>
<%if(costComponent != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
    <%-- ProductFeature productFeatureRelated = ProductFeatureHelper.findByPrimaryKey(costComponent.getProductFeatureId()); --%>
    <%ProductFeature productFeatureRelated = costComponent.getProductFeature();%>
  <DIV id=area5 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductFeature</b> with (PRODUCT_FEATURE_ID: <%=costComponent.getProductFeatureId()%>)
    </div>
    <%if(costComponent.getProductFeatureId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + costComponent.getProductFeatureId())%>" class="buttontext">[View ProductFeature]</a>      
    <%if(productFeatureRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + costComponent.getProductFeatureId())%>" class="buttontext">[Create ProductFeature]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productFeatureRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductFeature was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getDescription())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NUMBER_SPECIFIED</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(productFeatureRelated.getNumberSpecified())%>
    </td>
  </tr>

    <%} //end if productFeatureRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeature, type: one --%>
  

<%-- Start Relation for Party, type: one --%>
<%if(costComponent != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%-- Party partyRelated = PartyHelper.findByPrimaryKey(costComponent.getPartyId()); --%>
    <%Party partyRelated = costComponent.getParty();%>
  <DIV id=area6 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=costComponent.getPartyId()%>)
    </div>
    <%if(costComponent.getPartyId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + costComponent.getPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + costComponent.getPartyId())%>" class="buttontext">[Create Party]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Party was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyRelated.getPartyId())%>
    </td>
  </tr>

    <%} //end if partyRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Party, type: one --%>
  

<%-- Start Relation for Geo, type: one --%>
<%if(costComponent != null){%>
  <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
    <%-- Geo geoRelated = GeoHelper.findByPrimaryKey(costComponent.getGeoId()); --%>
    <%Geo geoRelated = costComponent.getGeo();%>
  <DIV id=area7 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Geo</b> with (GEO_ID: <%=costComponent.getGeoId()%>)
    </div>
    <%if(costComponent.getGeoId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + costComponent.getGeoId())%>" class="buttontext">[View Geo]</a>      
    <%if(geoRelated == null){%>
      <%if(Security.hasEntityPermission("GEO", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + costComponent.getGeoId())%>" class="buttontext">[Create Geo]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (COST_COMPONENT_ADMIN, or COST_COMPONENT_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
