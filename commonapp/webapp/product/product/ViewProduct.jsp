
<%
/**
 *  Title: Product Entity
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
 *@created    Fri Jul 27 01:37:04 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.product.*" %>

<%@ page import="org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>
<%@ page import="org.ofbiz.commonapp.common.uom.*" %>
<%@ page import="org.ofbiz.commonapp.product.feature.*" %>
<%@ page import="org.ofbiz.commonapp.product.cost.*" %>
<%@ page import="org.ofbiz.commonapp.product.price.*" %>
<%@ page import="org.ofbiz.commonapp.product.inventory.*" %>
<%@ page import="org.ofbiz.commonapp.product.supplier.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewProduct"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productId = request.getParameter("PRODUCT_PRODUCT_ID");  


  Product product = ProductHelper.findByPrimaryKey(productId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View Product</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit Product</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: Product with (PRODUCT_ID: <%=productId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProduct")%>" class="buttontext">[Find Product]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProduct")%>" class="buttontext">[Create New Product]</a>
<%}%>
<%if(product != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProduct?UPDATE_MODE=DELETE&" + "PRODUCT_PRODUCT_ID=" + productId)%>" class="buttontext">[Delete this Product]</a>
  <%}%>
<%}%>

<%if(product == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(product == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified Product was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(product.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRIMARY_PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(product.getPrimaryProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>MANUFACTURER_PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(product.getManufacturerPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(product.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>QUANTITY_INCLUDED</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(product.getQuantityIncluded())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INTRODUCTION_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(product != null)
        {
          java.util.Date date = product.getIntroductionDate();
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
        if(product != null)
        {
          java.util.Date date = product.getSalesDiscontinuationDate();
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
        if(product != null)
        {
          java.util.Date date = product.getSupportDiscontinuationDate();
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
      <%=UtilFormatOut.checkNull(product.getName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COMMENT</b></td>
    <td>
      <%=UtilFormatOut.checkNull(product.getComment())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(product.getDescription())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>LONG_DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(product.getLongDescription())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SMALL_IMAGE_URL</b></td>
    <td>
      <%=UtilFormatOut.checkNull(product.getSmallImageUrl())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>LARGE_IMAGE_URL</b></td>
    <td>
      <%=UtilFormatOut.checkNull(product.getLargeImageUrl())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DEFAULT_PRICE</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(product.getDefaultPrice())%>
    </td>
  </tr>

<%} //end if product == null %>
</table>
  </div>
<%Product productSave = product;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(product == null && (productId != null)){%>
    Product with (PRODUCT_ID: <%=productId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    product = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProduct")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(product == null){%>
  <%if(hasCreatePermission){%>
    You may create a Product by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_PRODUCT_ID" value="<%=UtilFormatOut.checkNull(productId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a Product (PRODUCT_ADMIN, or PRODUCT_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_PRODUCT_ID" value="<%=productId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ID</td>
      <td>
        <b><%=productId%></b> (This cannot be changed without re-creating the product.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a Product (PRODUCT_ADMIN, or PRODUCT_UPDATE needed).
  <%}%>
<%} //end if product == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRIMARY_PRODUCT_CATEGORY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_PRIMARY_PRODUCT_CATEGORY_ID" value="<%if(product!=null){%><%=UtilFormatOut.checkNull(product.getPrimaryProductCategoryId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_PRIMARY_PRODUCT_CATEGORY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>MANUFACTURER_PARTY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_MANUFACTURER_PARTY_ID" value="<%if(product!=null){%><%=UtilFormatOut.checkNull(product.getManufacturerPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_MANUFACTURER_PARTY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>UOM_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_UOM_ID" value="<%if(product!=null){%><%=UtilFormatOut.checkNull(product.getUomId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_UOM_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>QUANTITY_INCLUDED</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="PRODUCT_QUANTITY_INCLUDED" value="<%if(product!=null){%><%=UtilFormatOut.formatQuantity(product.getQuantityIncluded())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_QUANTITY_INCLUDED"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>INTRODUCTION_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(product != null)
        {
          java.util.Date date = product.getIntroductionDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PRODUCT_INTRODUCTION_DATE_DATE");
          timeString = request.getParameter("PRODUCT_INTRODUCTION_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PRODUCT_INTRODUCTION_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PRODUCT_INTRODUCTION_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PRODUCT_INTRODUCTION_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>SALES_DISCONTINUATION_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(product != null)
        {
          java.util.Date date = product.getSalesDiscontinuationDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PRODUCT_SALES_DISCONTINUATION_DATE_DATE");
          timeString = request.getParameter("PRODUCT_SALES_DISCONTINUATION_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PRODUCT_SALES_DISCONTINUATION_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PRODUCT_SALES_DISCONTINUATION_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PRODUCT_SALES_DISCONTINUATION_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>SUPPORT_DISCONTINUATION_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(product != null)
        {
          java.util.Date date = product.getSupportDiscontinuationDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PRODUCT_SUPPORT_DISCONTINUATION_DATE_DATE");
          timeString = request.getParameter("PRODUCT_SUPPORT_DISCONTINUATION_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PRODUCT_SUPPORT_DISCONTINUATION_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PRODUCT_SUPPORT_DISCONTINUATION_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PRODUCT_SUPPORT_DISCONTINUATION_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>NAME</td>
    <td>
      <input class='editInputBox' type="text" size="60" maxlength="60" name="PRODUCT_NAME" value="<%if(product!=null){%><%=UtilFormatOut.checkNull(product.getName())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_NAME"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>COMMENT</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_COMMENT" value="<%if(product!=null){%><%=UtilFormatOut.checkNull(product.getComment())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_COMMENT"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_DESCRIPTION" value="<%if(product!=null){%><%=UtilFormatOut.checkNull(product.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_DESCRIPTION"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>LONG_DESCRIPTION</td>
    <td>
      <textarea cols="60" rows="3" maxlength="5000" name="PRODUCT_LONG_DESCRIPTION"><%if(product!=null){%><%=UtilFormatOut.checkNull(product.getLongDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_LONG_DESCRIPTION"))%><%}%></textarea>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>SMALL_IMAGE_URL</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_SMALL_IMAGE_URL" value="<%if(product!=null){%><%=UtilFormatOut.checkNull(product.getSmallImageUrl())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_SMALL_IMAGE_URL"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>LARGE_IMAGE_URL</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_LARGE_IMAGE_URL" value="<%if(product!=null){%><%=UtilFormatOut.checkNull(product.getLargeImageUrl())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_LARGE_IMAGE_URL"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DEFAULT_PRICE</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="PRODUCT_DEFAULT_PRICE" value="<%if(product!=null){%><%=UtilFormatOut.formatQuantity(product.getDefaultPrice())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_DEFAULT_PRICE"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && product == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the product for cases when removed to retain passed form values --%>
<%product = productSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=15;
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
<%if(product != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_CLASS", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> ProductClass</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> ProductAttribute</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk>Primary ProductCategory</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_MEMBER", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> ProductCategoryMember</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
      <td id=tab5 class=offtab>
        <a href='javascript:ShowTab("tab5")' id=lnk5 class=offlnk>Manufacturer Party</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
      <td id=tab6 class=offtab>
        <a href='javascript:ShowTab("tab6")' id=lnk6 class=offlnk>Quantity Uom</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION", "_VIEW", session)){%>
      <td id=tab7 class=offtab>
        <a href='javascript:ShowTab("tab7")' id=lnk7 class=offlnk> GoodIdentification</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_DATA_OBJECT", "_VIEW", session)){%>
      <td id=tab8 class=offtab>
        <a href='javascript:ShowTab("tab8")' id=lnk8 class=offlnk> ProductDataObject</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_ASSOC", "_VIEW", session)){%>
      <td id=tab9 class=offtab>
        <a href='javascript:ShowTab("tab9")' id=lnk9 class=offlnk>Main ProductAssoc</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_ASSOC", "_VIEW", session)){%>
      <td id=tab10 class=offtab>
        <a href='javascript:ShowTab("tab10")' id=lnk10 class=offlnk>Assoc ProductAssoc</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_VIEW", session)){%>
      <td id=tab11 class=offtab>
        <a href='javascript:ShowTab("tab11")' id=lnk11 class=offlnk> ProductFeatureAppl</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("COST_COMPONENT", "_VIEW", session)){%>
      <td id=tab12 class=offtab>
        <a href='javascript:ShowTab("tab12")' id=lnk12 class=offlnk> CostComponent</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session)){%>
      <td id=tab13 class=offtab>
        <a href='javascript:ShowTab("tab13")' id=lnk13 class=offlnk> PriceComponent</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
      <td id=tab14 class=offtab>
        <a href='javascript:ShowTab("tab14")' id=lnk14 class=offlnk> InventoryItem</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("SUPPLIER_PRODUCT", "_VIEW", session)){%>
      <td id=tab15 class=offtab>
        <a href='javascript:ShowTab("tab15")' id=lnk15 class=offlnk> SupplierProduct</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductClass, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CLASS", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductClassHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getProductClasss());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductClass</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_CLASS", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_CLASS", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_CLASS", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductClass?" + "PRODUCT_CLASS_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create ProductClass]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductClass]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
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
        ProductClass productClassRelated = (ProductClass)relatedIterator.next();
        if(productClassRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productClassRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productClassRelated.getProductTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(productClassRelated != null)
        {
          java.util.Date date = productClassRelated.getFromDate();
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
        if(productClassRelated != null)
        {
          java.util.Date date = productClassRelated.getThruDate();
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
        <a href="<%=response.encodeURL(controlPath + "/ViewProductClass?" + "PRODUCT_CLASS_PRODUCT_ID=" + productClassRelated.getProductId() + "&" + "PRODUCT_CLASS_PRODUCT_TYPE_ID=" + productClassRelated.getProductTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductClass?" + "PRODUCT_CLASS_PRODUCT_ID=" + productClassRelated.getProductId() + "&" + "PRODUCT_CLASS_PRODUCT_TYPE_ID=" + productClassRelated.getProductTypeId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductClasss Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductClass, type: many --%>
  

<%-- Start Relation for ProductAttribute, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductAttributeHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getProductAttributes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductAttribute</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductAttribute?" + "PRODUCT_ATTRIBUTE_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create ProductAttribute]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductAttribute]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
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
        ProductAttribute productAttributeRelated = (ProductAttribute)relatedIterator.next();
        if(productAttributeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAttributeRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAttributeRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAttributeRelated.getValue())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductAttribute?" + "PRODUCT_ATTRIBUTE_PRODUCT_ID=" + productAttributeRelated.getProductId() + "&" + "PRODUCT_ATTRIBUTE_NAME=" + productAttributeRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductAttribute?" + "PRODUCT_ATTRIBUTE_PRODUCT_ID=" + productAttributeRelated.getProductId() + "&" + "PRODUCT_ATTRIBUTE_NAME=" + productAttributeRelated.getName() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No ProductAttributes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductAttribute, type: many --%>
  

<%-- Start Relation for ProductCategory, type: one --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
    <%-- ProductCategory productCategoryRelated = ProductCategoryHelper.findByPrimaryKey(product.getPrimaryProductCategoryId()); --%>
    <%ProductCategory productCategoryRelated = product.getPrimaryProductCategory();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Primary</b> Related Entity: <b>ProductCategory</b> with (PRODUCT_CATEGORY_ID: <%=product.getPrimaryProductCategoryId()%>)
    </div>
    <%if(product.getPrimaryProductCategoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + product.getPrimaryProductCategoryId())%>" class="buttontext">[View ProductCategory]</a>      
    <%if(productCategoryRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + product.getPrimaryProductCategoryId())%>" class="buttontext">[Create ProductCategory]</a>
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
  

<%-- Start Relation for ProductCategoryMember, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_MEMBER", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductCategoryMemberHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getProductCategoryMembers());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductCategoryMember</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_MEMBER", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_MEMBER", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_MEMBER", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryMember?" + "PRODUCT_CATEGORY_MEMBER_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create ProductCategoryMember]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductCategoryMember]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRIMARY_FLAG</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>COMMENT</nobr></b></div></td>
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
        ProductCategoryMember productCategoryMemberRelated = (ProductCategoryMember)relatedIterator.next();
        if(productCategoryMemberRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryMemberRelated.getProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryMemberRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(productCategoryMemberRelated != null)
        {
          java.util.Date date = productCategoryMemberRelated.getFromDate();
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
        if(productCategoryMemberRelated != null)
        {
          java.util.Date date = productCategoryMemberRelated.getThruDate();
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
      <%=UtilFormatOut.checkNull(productCategoryMemberRelated.getPrimaryFlag())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryMemberRelated.getComment())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryMember?" + "PRODUCT_CATEGORY_MEMBER_PRODUCT_CATEGORY_ID=" + productCategoryMemberRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_MEMBER_PRODUCT_ID=" + productCategoryMemberRelated.getProductId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductCategoryMember?" + "PRODUCT_CATEGORY_MEMBER_PRODUCT_CATEGORY_ID=" + productCategoryMemberRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_MEMBER_PRODUCT_ID=" + productCategoryMemberRelated.getProductId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="8">
<h3>No ProductCategoryMembers Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategoryMember, type: many --%>
  

<%-- Start Relation for Party, type: one --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%-- Party partyRelated = PartyHelper.findByPrimaryKey(product.getManufacturerPartyId()); --%>
    <%Party partyRelated = product.getManufacturerParty();%>
  <DIV id=area5 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Manufacturer</b> Related Entity: <b>Party</b> with (PARTY_ID: <%=product.getManufacturerPartyId()%>)
    </div>
    <%if(product.getManufacturerPartyId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + product.getManufacturerPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + product.getManufacturerPartyId())%>" class="buttontext">[Create Party]</a>
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
  

<%-- Start Relation for Uom, type: one --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
    <%-- Uom uomRelated = UomHelper.findByPrimaryKey(product.getUomId()); --%>
    <%Uom uomRelated = product.getQuantityUom();%>
  <DIV id=area6 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Quantity</b> Related Entity: <b>Uom</b> with (UOM_ID: <%=product.getUomId()%>)
    </div>
    <%if(product.getUomId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + product.getUomId())%>" class="buttontext">[View Uom]</a>      
    <%if(uomRelated == null){%>
      <%if(Security.hasEntityPermission("UOM", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + product.getUomId())%>" class="buttontext">[Create Uom]</a>
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
  

<%-- Start Relation for GoodIdentification, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(GoodIdentificationHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getGoodIdentifications());%>
  <DIV id=area7 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>GoodIdentification</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("GOOD_IDENTIFICATION", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("GOOD_IDENTIFICATION", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("GOOD_IDENTIFICATION", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentification?" + "GOOD_IDENTIFICATION_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create GoodIdentification]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find GoodIdentification]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>GOOD_IDENTIFICATION_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>ID_VALUE</nobr></b></div></td>
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
        GoodIdentification goodIdentificationRelated = (GoodIdentification)relatedIterator.next();
        if(goodIdentificationRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationRelated.getGoodIdentificationTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationRelated.getIdValue())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentification?" + "GOOD_IDENTIFICATION_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationRelated.getGoodIdentificationTypeId() + "&" + "GOOD_IDENTIFICATION_PRODUCT_ID=" + goodIdentificationRelated.getProductId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateGoodIdentification?" + "GOOD_IDENTIFICATION_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationRelated.getGoodIdentificationTypeId() + "&" + "GOOD_IDENTIFICATION_PRODUCT_ID=" + goodIdentificationRelated.getProductId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No GoodIdentifications Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for GoodIdentification, type: many --%>
  

<%-- Start Relation for ProductDataObject, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_DATA_OBJECT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductDataObjectHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getProductDataObjects());%>
  <DIV id=area8 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductDataObject</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_DATA_OBJECT", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_DATA_OBJECT", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_DATA_OBJECT", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductDataObject?" + "PRODUCT_DATA_OBJECT_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create ProductDataObject]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductDataObject]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>DATA_OBJECT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
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
        ProductDataObject productDataObjectRelated = (ProductDataObject)relatedIterator.next();
        if(productDataObjectRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productDataObjectRelated.getDataObjectId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productDataObjectRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductDataObject?" + "PRODUCT_DATA_OBJECT_DATA_OBJECT_ID=" + productDataObjectRelated.getDataObjectId() + "&" + "PRODUCT_DATA_OBJECT_PRODUCT_ID=" + productDataObjectRelated.getProductId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductDataObject?" + "PRODUCT_DATA_OBJECT_DATA_OBJECT_ID=" + productDataObjectRelated.getDataObjectId() + "&" + "PRODUCT_DATA_OBJECT_PRODUCT_ID=" + productDataObjectRelated.getProductId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No ProductDataObjects Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductDataObject, type: many --%>
  

<%-- Start Relation for ProductAssoc, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_ASSOC", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductAssocHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getMainProductAssocs());%>
  <DIV id=area9 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Main</b> Related Entities: <b>ProductAssoc</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_ASSOC", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_ASSOC", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_ASSOC", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductAssoc?" + "PRODUCT_ASSOC_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create ProductAssoc]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductAssoc]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID_TO</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ASSOC_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>REASON</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>QUANTITY</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>INSTRUCTION</nobr></b></div></td>
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
        ProductAssoc productAssocRelated = (ProductAssoc)relatedIterator.next();
        if(productAssocRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getProductIdTo())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getProductAssocTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(productAssocRelated != null)
        {
          java.util.Date date = productAssocRelated.getFromDate();
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
        if(productAssocRelated != null)
        {
          java.util.Date date = productAssocRelated.getThruDate();
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
      <%=UtilFormatOut.checkNull(productAssocRelated.getReason())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(productAssocRelated.getQuantity())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getInstruction())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductAssoc?" + "PRODUCT_ASSOC_PRODUCT_ID=" + productAssocRelated.getProductId() + "&" + "PRODUCT_ASSOC_PRODUCT_ID_TO=" + productAssocRelated.getProductIdTo() + "&" + "PRODUCT_ASSOC_PRODUCT_ASSOC_TYPE_ID=" + productAssocRelated.getProductAssocTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductAssoc?" + "PRODUCT_ASSOC_PRODUCT_ID=" + productAssocRelated.getProductId() + "&" + "PRODUCT_ASSOC_PRODUCT_ID_TO=" + productAssocRelated.getProductIdTo() + "&" + "PRODUCT_ASSOC_PRODUCT_ASSOC_TYPE_ID=" + productAssocRelated.getProductAssocTypeId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="10">
<h3>No ProductAssocs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductAssoc, type: many --%>
  

<%-- Start Relation for ProductAssoc, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_ASSOC", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductAssocHelper.findByProductIdTo(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getAssocProductAssocs());%>
  <DIV id=area10 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Assoc</b> Related Entities: <b>ProductAssoc</b> with (PRODUCT_ID_TO: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_ASSOC", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_ASSOC", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_ASSOC", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductAssoc?" + "PRODUCT_ASSOC_PRODUCT_ID_TO=" + product.getProductId())%>" class="buttontext">[Create ProductAssoc]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductIdTo";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductAssoc]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID_TO</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ASSOC_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>REASON</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>QUANTITY</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>INSTRUCTION</nobr></b></div></td>
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
        ProductAssoc productAssocRelated = (ProductAssoc)relatedIterator.next();
        if(productAssocRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getProductIdTo())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getProductAssocTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(productAssocRelated != null)
        {
          java.util.Date date = productAssocRelated.getFromDate();
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
        if(productAssocRelated != null)
        {
          java.util.Date date = productAssocRelated.getThruDate();
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
      <%=UtilFormatOut.checkNull(productAssocRelated.getReason())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(productAssocRelated.getQuantity())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getInstruction())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductAssoc?" + "PRODUCT_ASSOC_PRODUCT_ID=" + productAssocRelated.getProductId() + "&" + "PRODUCT_ASSOC_PRODUCT_ID_TO=" + productAssocRelated.getProductIdTo() + "&" + "PRODUCT_ASSOC_PRODUCT_ASSOC_TYPE_ID=" + productAssocRelated.getProductAssocTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductAssoc?" + "PRODUCT_ASSOC_PRODUCT_ID=" + productAssocRelated.getProductId() + "&" + "PRODUCT_ASSOC_PRODUCT_ID_TO=" + productAssocRelated.getProductIdTo() + "&" + "PRODUCT_ASSOC_PRODUCT_ASSOC_TYPE_ID=" + productAssocRelated.getProductAssocTypeId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="10">
<h3>No ProductAssocs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductAssoc, type: many --%>
  

<%-- Start Relation for ProductFeatureAppl, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductFeatureApplHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getProductFeatureAppls());%>
  <DIV id=area11 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductFeatureAppl</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureAppl?" + "PRODUCT_FEATURE_APPL_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create ProductFeatureAppl]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductFeatureAppl]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_APPL_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
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
        ProductFeatureAppl productFeatureApplRelated = (ProductFeatureAppl)relatedIterator.next();
        if(productFeatureApplRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplRelated.getProductFeatureApplTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(productFeatureApplRelated != null)
        {
          java.util.Date date = productFeatureApplRelated.getFromDate();
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
        if(productFeatureApplRelated != null)
        {
          java.util.Date date = productFeatureApplRelated.getThruDate();
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
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureAppl?" + "PRODUCT_FEATURE_APPL_PRODUCT_ID=" + productFeatureApplRelated.getProductId() + "&" + "PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID=" + productFeatureApplRelated.getProductFeatureId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureAppl?" + "PRODUCT_FEATURE_APPL_PRODUCT_ID=" + productFeatureApplRelated.getProductId() + "&" + "PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID=" + productFeatureApplRelated.getProductFeatureId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="7">
<h3>No ProductFeatureAppls Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureAppl, type: many --%>
  

<%-- Start Relation for CostComponent, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(CostComponentHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getCostComponents());%>
  <DIV id=area12 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>CostComponent</b> with (PRODUCT_ID: <%=product.getProductId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponent?" + "COST_COMPONENT_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create CostComponent]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find CostComponent]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponent?" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentRelated.getCostComponentId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  

<%-- Start Relation for PriceComponent, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PriceComponentHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getPriceComponents());%>
  <DIV id=area13 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PriceComponent</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRICE_COMPONENT", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRICE_COMPONENT", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRICE_COMPONENT", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent?" + "PRICE_COMPONENT_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create PriceComponent]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PriceComponent]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRICE_COMPONENT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRICE_COMPONENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>AGREEMENT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>AGREEMENT_ITEM_SEQ_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GEO_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>SALE_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>ORDER_VALUE_BREAK_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>QUANTITY_BREAK_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UTILIZATION_UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UTILIZATION_QUANTITY</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRICE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PERCENT</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>COMMENT</nobr></b></div></td>
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
        PriceComponent priceComponentRelated = (PriceComponent)relatedIterator.next();
        if(priceComponentRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPriceComponentId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPriceComponentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPartyTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getAgreementId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getAgreementItemSeqId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getUomId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getGeoId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getSaleTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getOrderValueBreakId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getQuantityBreakId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getUtilizationUomId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(priceComponentRelated.getUtilizationQuantity())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(priceComponentRelated != null)
        {
          java.util.Date date = priceComponentRelated.getFromDate();
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
        if(priceComponentRelated != null)
        {
          java.util.Date date = priceComponentRelated.getThruDate();
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
      <%=UtilFormatOut.formatQuantity(priceComponentRelated.getPrice())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(priceComponentRelated.getPercent())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getComment())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent?" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentRelated.getPriceComponentId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePriceComponent?" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentRelated.getPriceComponentId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="23">
<h3>No PriceComponents Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PriceComponent, type: many --%>
  

<%-- Start Relation for InventoryItem, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getInventoryItems());%>
  <DIV id=area14 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItem</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("INVENTORY_ITEM", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("INVENTORY_ITEM", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("INVENTORY_ITEM", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create InventoryItem]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItem]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>INVENTORY_ITEM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>INVENTORY_ITEM_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>STATUS_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>CONTAINER_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>LOT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>QUANTITY_ON_HAND</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>SERIAL_NUMBER</nobr></b></div></td>
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
        InventoryItem inventoryItemRelated = (InventoryItem)relatedIterator.next();
        if(inventoryItemRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getInventoryItemId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getInventoryItemTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getStatusTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getFacilityId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getContainerId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getLotId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getUomId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(inventoryItemRelated.getQuantityOnHand())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getSerialNumber())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemRelated.getInventoryItemId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemRelated.getInventoryItemId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="13">
<h3>No InventoryItems Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for InventoryItem, type: many --%>
  

<%-- Start Relation for SupplierProduct, type: many --%>
<%if(product != null){%>
  <%if(Security.hasEntityPermission("SUPPLIER_PRODUCT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(SupplierProductHelper.findByProductId(product.getProductId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(product.getSupplierProducts());%>
  <DIV id=area15 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>SupplierProduct</b> with (PRODUCT_ID: <%=product.getProductId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("SUPPLIER_PRODUCT", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("SUPPLIER_PRODUCT", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("SUPPLIER_PRODUCT", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewSupplierProduct?" + "SUPPLIER_PRODUCT_PRODUCT_ID=" + product.getProductId())%>" class="buttontext">[Create SupplierProduct]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + product.getProductId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProduct?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find SupplierProduct]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>AVAILABLE_FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>AVAILABLE_THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>SUPPLIER_PREF_ORDER_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>SUPPLIER_RATING_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>STANDARD_LEAD_TIME</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>COMMENT</nobr></b></div></td>
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
        SupplierProduct supplierProductRelated = (SupplierProduct)relatedIterator.next();
        if(supplierProductRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(supplierProductRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(supplierProductRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(supplierProductRelated != null)
        {
          java.util.Date date = supplierProductRelated.getAvailableFromDate();
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
        if(supplierProductRelated != null)
        {
          java.util.Date date = supplierProductRelated.getAvailableThruDate();
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
      <%=UtilFormatOut.checkNull(supplierProductRelated.getSupplierPrefOrderId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(supplierProductRelated.getSupplierRatingTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(supplierProductRelated != null)
        {
          java.util.Date date = supplierProductRelated.getStandardLeadTime();
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
      <%=UtilFormatOut.checkNull(supplierProductRelated.getComment())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewSupplierProduct?" + "SUPPLIER_PRODUCT_PRODUCT_ID=" + supplierProductRelated.getProductId() + "&" + "SUPPLIER_PRODUCT_PARTY_ID=" + supplierProductRelated.getPartyId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateSupplierProduct?" + "SUPPLIER_PRODUCT_PRODUCT_ID=" + supplierProductRelated.getProductId() + "&" + "SUPPLIER_PRODUCT_PARTY_ID=" + supplierProductRelated.getPartyId() + "&" + "PRODUCT_PRODUCT_ID=" + productId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="10">
<h3>No SupplierProducts Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for SupplierProduct, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_ADMIN, or PRODUCT_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
