
<%
/**
 *  Title: Supplier Product Entity
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

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewSupplierProduct"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("SUPPLIER_PRODUCT", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("SUPPLIER_PRODUCT", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SUPPLIER_PRODUCT", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SUPPLIER_PRODUCT", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productId = request.getParameter("SUPPLIER_PRODUCT_PRODUCT_ID");  
  String partyId = request.getParameter("SUPPLIER_PRODUCT_PARTY_ID");  


  SupplierProduct supplierProduct = SupplierProductHelper.findByPrimaryKey(productId, partyId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View SupplierProduct</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit SupplierProduct</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: SupplierProduct with (PRODUCT_ID, PARTY_ID: <%=productId%>, <%=partyId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindSupplierProduct")%>" class="buttontext">[Find SupplierProduct]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewSupplierProduct")%>" class="buttontext">[Create New SupplierProduct]</a>
<%}%>
<%if(supplierProduct != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateSupplierProduct?UPDATE_MODE=DELETE&" + "SUPPLIER_PRODUCT_PRODUCT_ID=" + productId + "&" + "SUPPLIER_PRODUCT_PARTY_ID=" + partyId)%>" class="buttontext">[Delete this SupplierProduct]</a>
  <%}%>
<%}%>

<%if(supplierProduct == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(supplierProduct == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified SupplierProduct was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierProduct.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierProduct.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>AVAILABLE_FROM_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(supplierProduct != null)
        {
          java.util.Date date = supplierProduct.getAvailableFromDate();
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
    <td><b>AVAILABLE_THRU_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(supplierProduct != null)
        {
          java.util.Date date = supplierProduct.getAvailableThruDate();
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
    <td><b>SUPPLIER_PREF_ORDER_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierProduct.getSupplierPrefOrderId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SUPPLIER_RATING_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierProduct.getSupplierRatingTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>STANDARD_LEAD_TIME</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(supplierProduct != null)
        {
          java.util.Date date = supplierProduct.getStandardLeadTime();
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
    <td><b>COMMENT</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierProduct.getComment())%>
    </td>
  </tr>

<%} //end if supplierProduct == null %>
</table>
  </div>
<%SupplierProduct supplierProductSave = supplierProduct;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(supplierProduct == null && (productId != null || partyId != null)){%>
    SupplierProduct with (PRODUCT_ID, PARTY_ID: <%=productId%>, <%=partyId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    supplierProduct = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateSupplierProduct")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(supplierProduct == null){%>
  <%if(hasCreatePermission){%>
    You may create a SupplierProduct by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="SUPPLIER_PRODUCT_PRODUCT_ID" value="<%=UtilFormatOut.checkNull(productId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="SUPPLIER_PRODUCT_PARTY_ID" value="<%=UtilFormatOut.checkNull(partyId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a SupplierProduct (SUPPLIER_PRODUCT_ADMIN, or SUPPLIER_PRODUCT_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="SUPPLIER_PRODUCT_PRODUCT_ID" value="<%=productId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ID</td>
      <td>
        <b><%=productId%></b> (This cannot be changed without re-creating the supplierProduct.)
      </td>
    </tr>
      <input type="hidden" name="SUPPLIER_PRODUCT_PARTY_ID" value="<%=partyId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_ID</td>
      <td>
        <b><%=partyId%></b> (This cannot be changed without re-creating the supplierProduct.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a SupplierProduct (SUPPLIER_PRODUCT_ADMIN, or SUPPLIER_PRODUCT_UPDATE needed).
  <%}%>
<%} //end if supplierProduct == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>AVAILABLE_FROM_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(supplierProduct != null)
        {
          java.util.Date date = supplierProduct.getAvailableFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("SUPPLIER_PRODUCT_AVAILABLE_FROM_DATE_DATE");
          timeString = request.getParameter("SUPPLIER_PRODUCT_AVAILABLE_FROM_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="SUPPLIER_PRODUCT_AVAILABLE_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.SUPPLIER_PRODUCT_AVAILABLE_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="SUPPLIER_PRODUCT_AVAILABLE_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>AVAILABLE_THRU_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(supplierProduct != null)
        {
          java.util.Date date = supplierProduct.getAvailableThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("SUPPLIER_PRODUCT_AVAILABLE_THRU_DATE_DATE");
          timeString = request.getParameter("SUPPLIER_PRODUCT_AVAILABLE_THRU_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="SUPPLIER_PRODUCT_AVAILABLE_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.SUPPLIER_PRODUCT_AVAILABLE_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="SUPPLIER_PRODUCT_AVAILABLE_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>SUPPLIER_PREF_ORDER_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="SUPPLIER_PRODUCT_SUPPLIER_PREF_ORDER_ID" value="<%if(supplierProduct!=null){%><%=UtilFormatOut.checkNull(supplierProduct.getSupplierPrefOrderId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("SUPPLIER_PRODUCT_SUPPLIER_PREF_ORDER_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>SUPPLIER_RATING_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="SUPPLIER_PRODUCT_SUPPLIER_RATING_TYPE_ID" value="<%if(supplierProduct!=null){%><%=UtilFormatOut.checkNull(supplierProduct.getSupplierRatingTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("SUPPLIER_PRODUCT_SUPPLIER_RATING_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>STANDARD_LEAD_TIME</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(supplierProduct != null)
        {
          java.util.Date date = supplierProduct.getStandardLeadTime();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("SUPPLIER_PRODUCT_STANDARD_LEAD_TIME_DATE");
          timeString = request.getParameter("SUPPLIER_PRODUCT_STANDARD_LEAD_TIME_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="SUPPLIER_PRODUCT_STANDARD_LEAD_TIME_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.SUPPLIER_PRODUCT_STANDARD_LEAD_TIME_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="SUPPLIER_PRODUCT_STANDARD_LEAD_TIME_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>COMMENT</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="SUPPLIER_PRODUCT_COMMENT" value="<%if(supplierProduct!=null){%><%=UtilFormatOut.checkNull(supplierProduct.getComment())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("SUPPLIER_PRODUCT_COMMENT"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && supplierProduct == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the supplierProduct for cases when removed to retain passed form values --%>
<%supplierProduct = supplierProductSave;%>

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
<%if(supplierProduct != null){%>
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
    <%if(Security.hasEntityPermission("SUPPLIER_PREF_ORDER", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> SupplierPrefOrder</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("SUPPLIER_RATING_TYPE", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> SupplierRatingType</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for Product, type: one --%>
<%if(supplierProduct != null){%>
  <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
    <%-- Product productRelated = ProductHelper.findByPrimaryKey(supplierProduct.getProductId()); --%>
    <%Product productRelated = supplierProduct.getProduct();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Product</b> with (PRODUCT_ID: <%=supplierProduct.getProductId()%>)
    </div>
    <%if(supplierProduct.getProductId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + supplierProduct.getProductId())%>" class="buttontext">[View Product]</a>      
    <%if(productRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + supplierProduct.getProductId())%>" class="buttontext">[Create Product]</a>
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
<%if(supplierProduct != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%-- Party partyRelated = PartyHelper.findByPrimaryKey(supplierProduct.getPartyId()); --%>
    <%Party partyRelated = supplierProduct.getParty();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=supplierProduct.getPartyId()%>)
    </div>
    <%if(supplierProduct.getPartyId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + supplierProduct.getPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + supplierProduct.getPartyId())%>" class="buttontext">[Create Party]</a>
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
  

<%-- Start Relation for SupplierPrefOrder, type: one --%>
<%if(supplierProduct != null){%>
  <%if(Security.hasEntityPermission("SUPPLIER_PREF_ORDER", "_VIEW", session)){%>
    <%-- SupplierPrefOrder supplierPrefOrderRelated = SupplierPrefOrderHelper.findByPrimaryKey(supplierProduct.getSupplierPrefOrderId()); --%>
    <%SupplierPrefOrder supplierPrefOrderRelated = supplierProduct.getSupplierPrefOrder();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>SupplierPrefOrder</b> with (SUPPLIER_PREF_ORDER_ID: <%=supplierProduct.getSupplierPrefOrderId()%>)
    </div>
    <%if(supplierProduct.getSupplierPrefOrderId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewSupplierPrefOrder?" + "SUPPLIER_PREF_ORDER_SUPPLIER_PREF_ORDER_ID=" + supplierProduct.getSupplierPrefOrderId())%>" class="buttontext">[View SupplierPrefOrder]</a>      
    <%if(supplierPrefOrderRelated == null){%>
      <%if(Security.hasEntityPermission("SUPPLIER_PREF_ORDER", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewSupplierPrefOrder?" + "SUPPLIER_PREF_ORDER_SUPPLIER_PREF_ORDER_ID=" + supplierProduct.getSupplierPrefOrderId())%>" class="buttontext">[Create SupplierPrefOrder]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(supplierPrefOrderRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified SupplierPrefOrder was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SUPPLIER_PREF_ORDER_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierPrefOrderRelated.getSupplierPrefOrderId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierPrefOrderRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if supplierPrefOrderRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for SupplierPrefOrder, type: one --%>
  

<%-- Start Relation for SupplierRatingType, type: one --%>
<%if(supplierProduct != null){%>
  <%if(Security.hasEntityPermission("SUPPLIER_RATING_TYPE", "_VIEW", session)){%>
    <%-- SupplierRatingType supplierRatingTypeRelated = SupplierRatingTypeHelper.findByPrimaryKey(supplierProduct.getSupplierRatingTypeId()); --%>
    <%SupplierRatingType supplierRatingTypeRelated = supplierProduct.getSupplierRatingType();%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>SupplierRatingType</b> with (SUPPLIER_RATING_TYPE_ID: <%=supplierProduct.getSupplierRatingTypeId()%>)
    </div>
    <%if(supplierProduct.getSupplierRatingTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewSupplierRatingType?" + "SUPPLIER_RATING_TYPE_SUPPLIER_RATING_TYPE_ID=" + supplierProduct.getSupplierRatingTypeId())%>" class="buttontext">[View SupplierRatingType]</a>      
    <%if(supplierRatingTypeRelated == null){%>
      <%if(Security.hasEntityPermission("SUPPLIER_RATING_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewSupplierRatingType?" + "SUPPLIER_RATING_TYPE_SUPPLIER_RATING_TYPE_ID=" + supplierProduct.getSupplierRatingTypeId())%>" class="buttontext">[Create SupplierRatingType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(supplierRatingTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified SupplierRatingType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SUPPLIER_RATING_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierRatingTypeRelated.getSupplierRatingTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierRatingTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if supplierRatingTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for SupplierRatingType, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (SUPPLIER_PRODUCT_ADMIN, or SUPPLIER_PRODUCT_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
