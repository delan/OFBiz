
<%
/**
 *  Title: Price Component Entity
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
<%@ page import="org.ofbiz.commonapp.product.price.*" %>

<%@ page import="org.ofbiz.commonapp.party.party.*" %>
<%@ page import="org.ofbiz.commonapp.product.product.*" %>
<%@ page import="org.ofbiz.commonapp.product.feature.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.commonapp.common.uom.*" %>
<%@ page import="org.ofbiz.commonapp.common.geo.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewPriceComponent"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRICE_COMPONENT", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRICE_COMPONENT", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRICE_COMPONENT", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String priceComponentId = request.getParameter("PRICE_COMPONENT_PRICE_COMPONENT_ID");  


  PriceComponent priceComponent = PriceComponentHelper.findByPrimaryKey(priceComponentId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View PriceComponent</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit PriceComponent</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: PriceComponent with (PRICE_COMPONENT_ID: <%=priceComponentId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" class="buttontext">[Find PriceComponent]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent")%>" class="buttontext">[Create New PriceComponent]</a>
<%}%>
<%if(priceComponent != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdatePriceComponent?UPDATE_MODE=DELETE&" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentId)%>" class="buttontext">[Delete this PriceComponent]</a>
  <%}%>
<%}%>

<%if(priceComponent == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(priceComponent == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified PriceComponent was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRICE_COMPONENT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getPriceComponentId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRICE_COMPONENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getPriceComponentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getPartyTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>AGREEMENT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getAgreementId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>AGREEMENT_ITEM_SEQ_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getAgreementItemSeqId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getGeoId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SALE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getSaleTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ORDER_VALUE_BREAK_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getOrderValueBreakId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>QUANTITY_BREAK_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getQuantityBreakId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UTILIZATION_UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getUtilizationUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UTILIZATION_QUANTITY</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(priceComponent.getUtilizationQuantity())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(priceComponent != null)
        {
          java.util.Date date = priceComponent.getFromDate();
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
        if(priceComponent != null)
        {
          java.util.Date date = priceComponent.getThruDate();
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
    <td><b>PRICE</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(priceComponent.getPrice())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PERCENT</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(priceComponent.getPercent())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COMMENT</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponent.getComment())%>
    </td>
  </tr>

<%} //end if priceComponent == null %>
</table>
  </div>
<%PriceComponent priceComponentSave = priceComponent;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(priceComponent == null && (priceComponentId != null)){%>
    PriceComponent with (PRICE_COMPONENT_ID: <%=priceComponentId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    priceComponent = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdatePriceComponent")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(priceComponent == null){%>
  <%if(hasCreatePermission){%>
    You may create a PriceComponent by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRICE_COMPONENT_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_PRICE_COMPONENT_ID" value="<%=UtilFormatOut.checkNull(priceComponentId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PriceComponent (PRICE_COMPONENT_ADMIN, or PRICE_COMPONENT_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRICE_COMPONENT_PRICE_COMPONENT_ID" value="<%=priceComponentId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRICE_COMPONENT_ID</td>
      <td>
        <b><%=priceComponentId%></b> (This cannot be changed without re-creating the priceComponent.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PriceComponent (PRICE_COMPONENT_ADMIN, or PRICE_COMPONENT_UPDATE needed).
  <%}%>
<%} //end if priceComponent == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRICE_COMPONENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_PRICE_COMPONENT_TYPE_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getPriceComponentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_PRICE_COMPONENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_PARTY_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_PARTY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_PARTY_TYPE_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getPartyTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_PARTY_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_PRODUCT_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getProductId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_PRODUCT_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_FEATURE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_PRODUCT_FEATURE_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getProductFeatureId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_PRODUCT_FEATURE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_CATEGORY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_PRODUCT_CATEGORY_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getProductCategoryId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_PRODUCT_CATEGORY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>AGREEMENT_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_AGREEMENT_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getAgreementId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_AGREEMENT_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>AGREEMENT_ITEM_SEQ_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_AGREEMENT_ITEM_SEQ_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getAgreementItemSeqId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_AGREEMENT_ITEM_SEQ_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>UOM_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_UOM_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getUomId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_UOM_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>GEO_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_GEO_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getGeoId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_GEO_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>SALE_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_SALE_TYPE_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getSaleTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_SALE_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>ORDER_VALUE_BREAK_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_ORDER_VALUE_BREAK_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getOrderValueBreakId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_ORDER_VALUE_BREAK_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>QUANTITY_BREAK_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_QUANTITY_BREAK_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getQuantityBreakId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_QUANTITY_BREAK_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>UTILIZATION_UOM_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_UTILIZATION_UOM_ID" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getUtilizationUomId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_UTILIZATION_UOM_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>UTILIZATION_QUANTITY</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="PRICE_COMPONENT_UTILIZATION_QUANTITY" value="<%if(priceComponent!=null){%><%=UtilFormatOut.formatQuantity(priceComponent.getUtilizationQuantity())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_UTILIZATION_QUANTITY"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FROM_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(priceComponent != null)
        {
          java.util.Date date = priceComponent.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PRICE_COMPONENT_FROM_DATE_DATE");
          timeString = request.getParameter("PRICE_COMPONENT_FROM_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PRICE_COMPONENT_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PRICE_COMPONENT_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PRICE_COMPONENT_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>THRU_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(priceComponent != null)
        {
          java.util.Date date = priceComponent.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PRICE_COMPONENT_THRU_DATE_DATE");
          timeString = request.getParameter("PRICE_COMPONENT_THRU_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PRICE_COMPONENT_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PRICE_COMPONENT_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PRICE_COMPONENT_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRICE</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="PRICE_COMPONENT_PRICE" value="<%if(priceComponent!=null){%><%=UtilFormatOut.formatQuantity(priceComponent.getPrice())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_PRICE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PERCENT</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="PRICE_COMPONENT_PERCENT" value="<%if(priceComponent!=null){%><%=UtilFormatOut.formatQuantity(priceComponent.getPercent())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_PERCENT"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>COMMENT</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRICE_COMPONENT_COMMENT" value="<%if(priceComponent!=null){%><%=UtilFormatOut.checkNull(priceComponent.getComment())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_COMMENT"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && priceComponent == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the priceComponent for cases when removed to retain passed form values --%>
<%priceComponent = priceComponentSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=14;
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
<%if(priceComponent != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRICE_COMPONENT_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> PriceComponentType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> PriceComponentTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> PriceComponentAttribute</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> Party</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
      <td id=tab5 class=offtab>
        <a href='javascript:ShowTab("tab5")' id=lnk5 class=offlnk> PartyType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
      <td id=tab6 class=offtab>
        <a href='javascript:ShowTab("tab6")' id=lnk6 class=offlnk> Product</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
      <td id=tab7 class=offtab>
        <a href='javascript:ShowTab("tab7")' id=lnk7 class=offlnk> ProductFeature</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
      <td id=tab8 class=offtab>
        <a href='javascript:ShowTab("tab8")' id=lnk8 class=offlnk> ProductCategory</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
      <td id=tab9 class=offtab>
        <a href='javascript:ShowTab("tab9")' id=lnk9 class=offlnk> Uom</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
      <td id=tab10 class=offtab>
        <a href='javascript:ShowTab("tab10")' id=lnk10 class=offlnk> Geo</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("SALE_TYPE", "_VIEW", session)){%>
      <td id=tab11 class=offtab>
        <a href='javascript:ShowTab("tab11")' id=lnk11 class=offlnk> SaleType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("ORDER_VALUE_BREAK", "_VIEW", session)){%>
      <td id=tab12 class=offtab>
        <a href='javascript:ShowTab("tab12")' id=lnk12 class=offlnk> OrderValueBreak</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("QUANTITY_BREAK", "_VIEW", session)){%>
      <td id=tab13 class=offtab>
        <a href='javascript:ShowTab("tab13")' id=lnk13 class=offlnk> QuantityBreak</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
      <td id=tab14 class=offtab>
        <a href='javascript:ShowTab("tab14")' id=lnk14 class=offlnk>Utilization Uom</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for PriceComponentType, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("PRICE_COMPONENT_TYPE", "_VIEW", session)){%>
    <%-- PriceComponentType priceComponentTypeRelated = PriceComponentTypeHelper.findByPrimaryKey(priceComponent.getPriceComponentTypeId()); --%>
    <%PriceComponentType priceComponentTypeRelated = priceComponent.getPriceComponentType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>PriceComponentType</b> with (PRICE_COMPONENT_TYPE_ID: <%=priceComponent.getPriceComponentTypeId()%>)
    </div>
    <%if(priceComponent.getPriceComponentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentType?" + "PRICE_COMPONENT_TYPE_PRICE_COMPONENT_TYPE_ID=" + priceComponent.getPriceComponentTypeId())%>" class="buttontext">[View PriceComponentType]</a>      
    <%if(priceComponentTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRICE_COMPONENT_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentType?" + "PRICE_COMPONENT_TYPE_PRICE_COMPONENT_TYPE_ID=" + priceComponent.getPriceComponentTypeId())%>" class="buttontext">[Create PriceComponentType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(priceComponentTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified PriceComponentType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRICE_COMPONENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentTypeRelated.getPriceComponentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if priceComponentTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for PriceComponentType, type: one --%>
  

<%-- Start Relation for PriceComponentTypeAttr, type: many --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PriceComponentTypeAttrHelper.findByPriceComponentTypeId(priceComponent.getPriceComponentTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(priceComponent.getPriceComponentTypeAttrs());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PriceComponentTypeAttr</b> with (PRICE_COMPONENT_TYPE_ID: <%=priceComponent.getPriceComponentTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentTypeAttr?" + "PRICE_COMPONENT_TYPE_ATTR_PRICE_COMPONENT_TYPE_ID=" + priceComponent.getPriceComponentTypeId())%>" class="buttontext">[Create PriceComponentTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=PriceComponentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + priceComponent.getPriceComponentTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindPriceComponent?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PriceComponentTypeAttr]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRICE_COMPONENT_TYPE_ID</nobr></b></div></td>
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
        PriceComponentTypeAttr priceComponentTypeAttrRelated = (PriceComponentTypeAttr)relatedIterator.next();
        if(priceComponentTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentTypeAttrRelated.getPriceComponentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentTypeAttrRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentTypeAttr?" + "PRICE_COMPONENT_TYPE_ATTR_PRICE_COMPONENT_TYPE_ID=" + priceComponentTypeAttrRelated.getPriceComponentTypeId() + "&" + "PRICE_COMPONENT_TYPE_ATTR_NAME=" + priceComponentTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePriceComponentTypeAttr?" + "PRICE_COMPONENT_TYPE_ATTR_PRICE_COMPONENT_TYPE_ID=" + priceComponentTypeAttrRelated.getPriceComponentTypeId() + "&" + "PRICE_COMPONENT_TYPE_ATTR_NAME=" + priceComponentTypeAttrRelated.getName() + "&" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No PriceComponentTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PriceComponentTypeAttr, type: many --%>
  

<%-- Start Relation for PriceComponentAttribute, type: many --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PriceComponentAttributeHelper.findByPriceComponentId(priceComponent.getPriceComponentId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(priceComponent.getPriceComponentAttributes());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PriceComponentAttribute</b> with (PRICE_COMPONENT_ID: <%=priceComponent.getPriceComponentId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentAttribute?" + "PRICE_COMPONENT_ATTRIBUTE_PRICE_COMPONENT_ID=" + priceComponent.getPriceComponentId())%>" class="buttontext">[Create PriceComponentAttribute]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=PriceComponentId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + priceComponent.getPriceComponentId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindPriceComponent?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PriceComponentAttribute]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRICE_COMPONENT_ID</nobr></b></div></td>
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
        PriceComponentAttribute priceComponentAttributeRelated = (PriceComponentAttribute)relatedIterator.next();
        if(priceComponentAttributeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentAttributeRelated.getPriceComponentId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentAttributeRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentAttributeRelated.getValue())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentAttribute?" + "PRICE_COMPONENT_ATTRIBUTE_PRICE_COMPONENT_ID=" + priceComponentAttributeRelated.getPriceComponentId() + "&" + "PRICE_COMPONENT_ATTRIBUTE_NAME=" + priceComponentAttributeRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePriceComponentAttribute?" + "PRICE_COMPONENT_ATTRIBUTE_PRICE_COMPONENT_ID=" + priceComponentAttributeRelated.getPriceComponentId() + "&" + "PRICE_COMPONENT_ATTRIBUTE_NAME=" + priceComponentAttributeRelated.getName() + "&" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No PriceComponentAttributes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PriceComponentAttribute, type: many --%>
  

<%-- Start Relation for Party, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%-- Party partyRelated = PartyHelper.findByPrimaryKey(priceComponent.getPartyId()); --%>
    <%Party partyRelated = priceComponent.getParty();%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=priceComponent.getPartyId()%>)
    </div>
    <%if(priceComponent.getPartyId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + priceComponent.getPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + priceComponent.getPartyId())%>" class="buttontext">[Create Party]</a>
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
  

<%-- Start Relation for PartyType, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <%-- PartyType partyTypeRelated = PartyTypeHelper.findByPrimaryKey(priceComponent.getPartyTypeId()); --%>
    <%PartyType partyTypeRelated = priceComponent.getPartyType();%>
  <DIV id=area5 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>PartyType</b> with (PARTY_TYPE_ID: <%=priceComponent.getPartyTypeId()%>)
    </div>
    <%if(priceComponent.getPartyTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyType?" + "PARTY_TYPE_PARTY_TYPE_ID=" + priceComponent.getPartyTypeId())%>" class="buttontext">[View PartyType]</a>      
    <%if(partyTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyType?" + "PARTY_TYPE_PARTY_TYPE_ID=" + priceComponent.getPartyTypeId())%>" class="buttontext">[Create PartyType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified PartyType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getPartyTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if partyTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyType, type: one --%>
  

<%-- Start Relation for Product, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
    <%-- Product productRelated = ProductHelper.findByPrimaryKey(priceComponent.getProductId()); --%>
    <%Product productRelated = priceComponent.getProduct();%>
  <DIV id=area6 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Product</b> with (PRODUCT_ID: <%=priceComponent.getProductId()%>)
    </div>
    <%if(priceComponent.getProductId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + priceComponent.getProductId())%>" class="buttontext">[View Product]</a>      
    <%if(productRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + priceComponent.getProductId())%>" class="buttontext">[Create Product]</a>
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
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
    <%-- ProductFeature productFeatureRelated = ProductFeatureHelper.findByPrimaryKey(priceComponent.getProductFeatureId()); --%>
    <%ProductFeature productFeatureRelated = priceComponent.getProductFeature();%>
  <DIV id=area7 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductFeature</b> with (PRODUCT_FEATURE_ID: <%=priceComponent.getProductFeatureId()%>)
    </div>
    <%if(priceComponent.getProductFeatureId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + priceComponent.getProductFeatureId())%>" class="buttontext">[View ProductFeature]</a>      
    <%if(productFeatureRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + priceComponent.getProductFeatureId())%>" class="buttontext">[Create ProductFeature]</a>
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
  

<%-- Start Relation for ProductCategory, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
    <%-- ProductCategory productCategoryRelated = ProductCategoryHelper.findByPrimaryKey(priceComponent.getProductCategoryId()); --%>
    <%ProductCategory productCategoryRelated = priceComponent.getProductCategory();%>
  <DIV id=area8 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductCategory</b> with (PRODUCT_CATEGORY_ID: <%=priceComponent.getProductCategoryId()%>)
    </div>
    <%if(priceComponent.getProductCategoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + priceComponent.getProductCategoryId())%>" class="buttontext">[View ProductCategory]</a>      
    <%if(productCategoryRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + priceComponent.getProductCategoryId())%>" class="buttontext">[Create ProductCategory]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productCategoryRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductCategory was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRelated.getProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productCategoryRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategory, type: one --%>
  

<%-- Start Relation for Uom, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
    <%-- Uom uomRelated = UomHelper.findByPrimaryKey(priceComponent.getUomId()); --%>
    <%Uom uomRelated = priceComponent.getUom();%>
  <DIV id=area9 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Uom</b> with (UOM_ID: <%=priceComponent.getUomId()%>)
    </div>
    <%if(priceComponent.getUomId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + priceComponent.getUomId())%>" class="buttontext">[View Uom]</a>      
    <%if(uomRelated == null){%>
      <%if(Security.hasEntityPermission("UOM", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + priceComponent.getUomId())%>" class="buttontext">[Create Uom]</a>
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
  

<%-- Start Relation for Geo, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("GEO", "_VIEW", session)){%>
    <%-- Geo geoRelated = GeoHelper.findByPrimaryKey(priceComponent.getGeoId()); --%>
    <%Geo geoRelated = priceComponent.getGeo();%>
  <DIV id=area10 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Geo</b> with (GEO_ID: <%=priceComponent.getGeoId()%>)
    </div>
    <%if(priceComponent.getGeoId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + priceComponent.getGeoId())%>" class="buttontext">[View Geo]</a>      
    <%if(geoRelated == null){%>
      <%if(Security.hasEntityPermission("GEO", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeo?" + "GEO_GEO_ID=" + priceComponent.getGeoId())%>" class="buttontext">[Create Geo]</a>
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
  

<%-- Start Relation for SaleType, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("SALE_TYPE", "_VIEW", session)){%>
    <%-- SaleType saleTypeRelated = SaleTypeHelper.findByPrimaryKey(priceComponent.getSaleTypeId()); --%>
    <%SaleType saleTypeRelated = priceComponent.getSaleType();%>
  <DIV id=area11 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>SaleType</b> with (SALE_TYPE_ID: <%=priceComponent.getSaleTypeId()%>)
    </div>
    <%if(priceComponent.getSaleTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewSaleType?" + "SALE_TYPE_SALE_TYPE_ID=" + priceComponent.getSaleTypeId())%>" class="buttontext">[View SaleType]</a>      
    <%if(saleTypeRelated == null){%>
      <%if(Security.hasEntityPermission("SALE_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewSaleType?" + "SALE_TYPE_SALE_TYPE_ID=" + priceComponent.getSaleTypeId())%>" class="buttontext">[Create SaleType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(saleTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified SaleType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SALE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(saleTypeRelated.getSaleTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(saleTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if saleTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for SaleType, type: one --%>
  

<%-- Start Relation for OrderValueBreak, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("ORDER_VALUE_BREAK", "_VIEW", session)){%>
    <%-- OrderValueBreak orderValueBreakRelated = OrderValueBreakHelper.findByPrimaryKey(priceComponent.getOrderValueBreakId()); --%>
    <%OrderValueBreak orderValueBreakRelated = priceComponent.getOrderValueBreak();%>
  <DIV id=area12 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>OrderValueBreak</b> with (ORDER_VALUE_BREAK_ID: <%=priceComponent.getOrderValueBreakId()%>)
    </div>
    <%if(priceComponent.getOrderValueBreakId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewOrderValueBreak?" + "ORDER_VALUE_BREAK_ORDER_VALUE_BREAK_ID=" + priceComponent.getOrderValueBreakId())%>" class="buttontext">[View OrderValueBreak]</a>      
    <%if(orderValueBreakRelated == null){%>
      <%if(Security.hasEntityPermission("ORDER_VALUE_BREAK", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewOrderValueBreak?" + "ORDER_VALUE_BREAK_ORDER_VALUE_BREAK_ID=" + priceComponent.getOrderValueBreakId())%>" class="buttontext">[Create OrderValueBreak]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(orderValueBreakRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified OrderValueBreak was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ORDER_VALUE_BREAK_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(orderValueBreakRelated.getOrderValueBreakId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_AMOUNT</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(orderValueBreakRelated.getFromAmount())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>THRU_AMOUNT</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(orderValueBreakRelated.getThruAmount())%>
    </td>
  </tr>

    <%} //end if orderValueBreakRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for OrderValueBreak, type: one --%>
  

<%-- Start Relation for QuantityBreak, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("QUANTITY_BREAK", "_VIEW", session)){%>
    <%-- QuantityBreak quantityBreakRelated = QuantityBreakHelper.findByPrimaryKey(priceComponent.getQuantityBreakId()); --%>
    <%QuantityBreak quantityBreakRelated = priceComponent.getQuantityBreak();%>
  <DIV id=area13 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>QuantityBreak</b> with (QUANTITY_BREAK_ID: <%=priceComponent.getQuantityBreakId()%>)
    </div>
    <%if(priceComponent.getQuantityBreakId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewQuantityBreak?" + "QUANTITY_BREAK_QUANTITY_BREAK_ID=" + priceComponent.getQuantityBreakId())%>" class="buttontext">[View QuantityBreak]</a>      
    <%if(quantityBreakRelated == null){%>
      <%if(Security.hasEntityPermission("QUANTITY_BREAK", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewQuantityBreak?" + "QUANTITY_BREAK_QUANTITY_BREAK_ID=" + priceComponent.getQuantityBreakId())%>" class="buttontext">[Create QuantityBreak]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(quantityBreakRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified QuantityBreak was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>QUANTITY_BREAK_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(quantityBreakRelated.getQuantityBreakId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_QUANTITY</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(quantityBreakRelated.getFromQuantity())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>THRU_QUANTITY</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(quantityBreakRelated.getThruQuantity())%>
    </td>
  </tr>

    <%} //end if quantityBreakRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for QuantityBreak, type: one --%>
  

<%-- Start Relation for Uom, type: one --%>
<%if(priceComponent != null){%>
  <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
    <%-- Uom uomRelated = UomHelper.findByPrimaryKey(priceComponent.getUtilizationUomId()); --%>
    <%Uom uomRelated = priceComponent.getUtilizationUom();%>
  <DIV id=area14 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Utilization</b> Related Entity: <b>Uom</b> with (UOM_ID: <%=priceComponent.getUtilizationUomId()%>)
    </div>
    <%if(priceComponent.getUtilizationUomId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + priceComponent.getUtilizationUomId())%>" class="buttontext">[View Uom]</a>      
    <%if(uomRelated == null){%>
      <%if(Security.hasEntityPermission("UOM", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + priceComponent.getUtilizationUomId())%>" class="buttontext">[Create Uom]</a>
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
  <h3>You do not have permission to view this page (PRICE_COMPONENT_ADMIN, or PRICE_COMPONENT_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
