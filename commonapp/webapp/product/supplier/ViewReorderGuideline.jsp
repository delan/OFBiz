
<%
/**
 *  Title: Reorder Guideline Entity
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
 *@created    Fri Jul 27 01:37:23 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.supplier.*" %>

<%@ page import="org.ofbiz.commonapp.product.product.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>
<%@ page import="org.ofbiz.commonapp.product.storage.*" %>
<%@ page import="org.ofbiz.commonapp.common.geo.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewReorderGuideline"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("REORDER_GUIDELINE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("REORDER_GUIDELINE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("REORDER_GUIDELINE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("REORDER_GUIDELINE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String reorderGuidelineId = request.getParameter("REORDER_GUIDELINE_REORDER_GUIDELINE_ID");  


  ReorderGuideline reorderGuideline = ReorderGuidelineHelper.findByPrimaryKey(reorderGuidelineId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ReorderGuideline</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ReorderGuideline</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ReorderGuideline with (REORDER_GUIDELINE_ID: <%=reorderGuidelineId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" class="buttontext">[Find ReorderGuideline]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewReorderGuideline")%>" class="buttontext">[Create New ReorderGuideline]</a>
<%}%>
<%if(reorderGuideline != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateReorderGuideline?UPDATE_MODE=DELETE&" + "REORDER_GUIDELINE_REORDER_GUIDELINE_ID=" + reorderGuidelineId)%>" class="buttontext">[Delete this ReorderGuideline]</a>
  <%}%>
<%}%>

<%if(reorderGuideline == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(reorderGuideline == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ReorderGuideline was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>REORDER_GUIDELINE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(reorderGuideline.getReorderGuidelineId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(reorderGuideline.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(reorderGuideline.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ROLE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(reorderGuideline.getRoleTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(reorderGuideline.getFacilityId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(reorderGuideline.getGeoId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(reorderGuideline != null)
        {
          java.util.Date date = reorderGuideline.getFromDate();
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
        if(reorderGuideline != null)
        {
          java.util.Date date = reorderGuideline.getThruDate();
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
    <td><b>REORDER_QUANTITY</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(reorderGuideline.getReorderQuantity())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>REORDER_LEVEL</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(reorderGuideline.getReorderLevel())%>
    </td>
  </tr>

<%} //end if reorderGuideline == null %>
</table>
  </div>
<%ReorderGuideline reorderGuidelineSave = reorderGuideline;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(reorderGuideline == null && (reorderGuidelineId != null)){%>
    ReorderGuideline with (REORDER_GUIDELINE_ID: <%=reorderGuidelineId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    reorderGuideline = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateReorderGuideline")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(reorderGuideline == null){%>
  <%if(hasCreatePermission){%>
    You may create a ReorderGuideline by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>REORDER_GUIDELINE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="REORDER_GUIDELINE_REORDER_GUIDELINE_ID" value="<%=UtilFormatOut.checkNull(reorderGuidelineId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ReorderGuideline (REORDER_GUIDELINE_ADMIN, or REORDER_GUIDELINE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="REORDER_GUIDELINE_REORDER_GUIDELINE_ID" value="<%=reorderGuidelineId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>REORDER_GUIDELINE_ID</td>
      <td>
        <b><%=reorderGuidelineId%></b> (This cannot be changed without re-creating the reorderGuideline.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ReorderGuideline (REORDER_GUIDELINE_ADMIN, or REORDER_GUIDELINE_UPDATE needed).
  <%}%>
<%} //end if reorderGuideline == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="REORDER_GUIDELINE_PRODUCT_ID" value="<%if(reorderGuideline!=null){%><%=UtilFormatOut.checkNull(reorderGuideline.getProductId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("REORDER_GUIDELINE_PRODUCT_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="REORDER_GUIDELINE_PARTY_ID" value="<%if(reorderGuideline!=null){%><%=UtilFormatOut.checkNull(reorderGuideline.getPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("REORDER_GUIDELINE_PARTY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>ROLE_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="REORDER_GUIDELINE_ROLE_TYPE_ID" value="<%if(reorderGuideline!=null){%><%=UtilFormatOut.checkNull(reorderGuideline.getRoleTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("REORDER_GUIDELINE_ROLE_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FACILITY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="REORDER_GUIDELINE_FACILITY_ID" value="<%if(reorderGuideline!=null){%><%=UtilFormatOut.checkNull(reorderGuideline.getFacilityId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("REORDER_GUIDELINE_FACILITY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>GEO_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="REORDER_GUIDELINE_GEO_ID" value="<%if(reorderGuideline!=null){%><%=UtilFormatOut.checkNull(reorderGuideline.getGeoId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("REORDER_GUIDELINE_GEO_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FROM_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(reorderGuideline != null)
        {
          java.util.Date date = reorderGuideline.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("REORDER_GUIDELINE_FROM_DATE_DATE");
          timeString = request.getParameter("REORDER_GUIDELINE_FROM_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="REORDER_GUIDELINE_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.REORDER_GUIDELINE_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="REORDER_GUIDELINE_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>THRU_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(reorderGuideline != null)
        {
          java.util.Date date = reorderGuideline.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("REORDER_GUIDELINE_THRU_DATE_DATE");
          timeString = request.getParameter("REORDER_GUIDELINE_THRU_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="REORDER_GUIDELINE_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.REORDER_GUIDELINE_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="REORDER_GUIDELINE_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>REORDER_QUANTITY</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="REORDER_GUIDELINE_REORDER_QUANTITY" value="<%if(reorderGuideline!=null){%><%=UtilFormatOut.formatQuantity(reorderGuideline.getReorderQuantity())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("REORDER_GUIDELINE_REORDER_QUANTITY"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>REORDER_LEVEL</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="REORDER_GUIDELINE_REORDER_LEVEL" value="<%if(reorderGuideline!=null){%><%=UtilFormatOut.formatQuantity(reorderGuideline.getReorderLevel())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("REORDER_GUIDELINE_REORDER_LEVEL"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && reorderGuideline == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the reorderGuideline for cases when removed to retain passed form values --%>
<%reorderGuideline = reorderGuidelineSave;%>

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
<%if(reorderGuideline != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> Product</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> Party</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> Facility</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> Geo</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for Product, type: one --%>
<%if(reorderGuideline != null){%>
  <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
    <%-- Product productRelated = ProductHelper.findByPrimaryKey(reorderGuideline.getProductId()); --%>
    <%Product productRelated = reorderGuideline.getProduct();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Product</b> with (PRODUCT_ID: <%=reorderGuideline.getProductId()%>)
    </div>
    <%if(reorderGuideline.getProductId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + reorderGuideline.getProductId())%>" class="buttontext">[View Product]</a>      
    <%if(productRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + reorderGuideline.getProductId())%>" class="buttontext">[Create Product]</a>
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
  

<%-- Start Relation for Party, type: one --%>
<%if(reorderGuideline != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%-- Party partyRelated = PartyHelper.findByPrimaryKey(reorderGuideline.getPartyId()); --%>
    <%Party partyRelated = reorderGuideline.getParty();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=reorderGuideline.getPartyId()%>)
    </div>
    <%if(reorderGuideline.getPartyId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + reorderGuideline.getPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + reorderGuideline.getPartyId())%>" class="buttontext">[Create Party]</a>
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
  

<%-- Start Relation for Facility, type: one --%>
<%if(reorderGuideline != null){%>
  <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
    <%-- Facility facilityRelated = FacilityHelper.findByPrimaryKey(reorderGuideline.getFacilityId()); --%>
    <%Facility facilityRelated = reorderGuideline.getFacility();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Facility</b> with (FACILITY_ID: <%=reorderGuideline.getFacilityId()%>)
    </div>
    <%if(reorderGuideline.getFacilityId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + reorderGuideline.getFacilityId())%>" class="buttontext">[View Facility]</a>      
    <%if(facilityRelated == null){%>
      <%if(Security.hasEntityPermission("FACILITY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + reorderGuideline.getFacilityId())%>" class="buttontext">[Create Facility]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(facilityRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Facility was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRelated.getFacilityId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRelated.getFacilityTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRelated.getFacilityName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SQUARE_FOOTAGE</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(facilityRelated.getSquareFootage())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(facilityRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if facilityRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Facility, type: one --%>
  

<%-- Start Relation for Geo, type: one --%>
<%if(reorderGuideline != null){%>
  <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
    <%-- Geo geoRelated = GeoHelper.findByPrimaryKey(reorderGuideline.getGeoId()); --%>
    <%Geo geoRelated = reorderGuideline.getGeo();%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Geo</b> with (GEO_ID: <%=reorderGuideline.getGeoId()%>)
    </div>
    <%if(reorderGuideline.getGeoId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + reorderGuideline.getGeoId())%>" class="buttontext">[View Geo]</a>      
    <%if(geoRelated == null){%>
      <%if(Security.hasEntityPermission("GEO", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + reorderGuideline.getGeoId())%>" class="buttontext">[Create Geo]</a>
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
  <h3>You do not have permission to view this page (REORDER_GUIDELINE_ADMIN, or REORDER_GUIDELINE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
