
<%
/**
 *  Title: Product Feature Applicability Entity
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
 *@created    Fri Jul 27 01:37:14 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.feature.*" %>

<%@ page import="org.ofbiz.commonapp.product.product.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewProductFeatureAppl"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productId = request.getParameter("PRODUCT_FEATURE_APPL_PRODUCT_ID");  
  String productFeatureId = request.getParameter("PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID");  


  ProductFeatureAppl productFeatureAppl = ProductFeatureApplHelper.findByPrimaryKey(productId, productFeatureId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductFeatureAppl</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductFeatureAppl</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductFeatureAppl with (PRODUCT_ID, PRODUCT_FEATURE_ID: <%=productId%>, <%=productFeatureId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductFeatureAppl")%>" class="buttontext">[Find ProductFeatureAppl]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureAppl")%>" class="buttontext">[Create New ProductFeatureAppl]</a>
<%}%>
<%if(productFeatureAppl != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureAppl?UPDATE_MODE=DELETE&" + "PRODUCT_FEATURE_APPL_PRODUCT_ID=" + productId + "&" + "PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID=" + productFeatureId)%>" class="buttontext">[Delete this ProductFeatureAppl]</a>
  <%}%>
<%}%>

<%if(productFeatureAppl == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productFeatureAppl == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureAppl was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureAppl.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureAppl.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_APPL_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureAppl.getProductFeatureApplTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(productFeatureAppl != null)
        {
          java.util.Date date = productFeatureAppl.getFromDate();
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
        if(productFeatureAppl != null)
        {
          java.util.Date date = productFeatureAppl.getThruDate();
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

<%} //end if productFeatureAppl == null %>
</table>
  </div>
<%ProductFeatureAppl productFeatureApplSave = productFeatureAppl;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productFeatureAppl == null && (productId != null || productFeatureId != null)){%>
    ProductFeatureAppl with (PRODUCT_ID, PRODUCT_FEATURE_ID: <%=productId%>, <%=productFeatureId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productFeatureAppl = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductFeatureAppl")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productFeatureAppl == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductFeatureAppl by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_APPL_PRODUCT_ID" value="<%=UtilFormatOut.checkNull(productId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID" value="<%=UtilFormatOut.checkNull(productFeatureId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductFeatureAppl (PRODUCT_FEATURE_APPL_ADMIN, or PRODUCT_FEATURE_APPL_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_FEATURE_APPL_PRODUCT_ID" value="<%=productId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ID</td>
      <td>
        <b><%=productId%></b> (This cannot be changed without re-creating the productFeatureAppl.)
      </td>
    </tr>
      <input type="hidden" name="PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID" value="<%=productFeatureId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_ID</td>
      <td>
        <b><%=productFeatureId%></b> (This cannot be changed without re-creating the productFeatureAppl.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductFeatureAppl (PRODUCT_FEATURE_APPL_ADMIN, or PRODUCT_FEATURE_APPL_UPDATE needed).
  <%}%>
<%} //end if productFeatureAppl == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_FEATURE_APPL_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_APPL_TYPE_ID" value="<%if(productFeatureAppl!=null){%><%=UtilFormatOut.checkNull(productFeatureAppl.getProductFeatureApplTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_APPL_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FROM_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(productFeatureAppl != null)
        {
          java.util.Date date = productFeatureAppl.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PRODUCT_FEATURE_APPL_FROM_DATE_DATE");
          timeString = request.getParameter("PRODUCT_FEATURE_APPL_FROM_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PRODUCT_FEATURE_APPL_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PRODUCT_FEATURE_APPL_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PRODUCT_FEATURE_APPL_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>THRU_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(productFeatureAppl != null)
        {
          java.util.Date date = productFeatureAppl.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PRODUCT_FEATURE_APPL_THRU_DATE_DATE");
          timeString = request.getParameter("PRODUCT_FEATURE_APPL_THRU_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PRODUCT_FEATURE_APPL_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PRODUCT_FEATURE_APPL_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PRODUCT_FEATURE_APPL_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
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
<%if((hasUpdatePermission || hasCreatePermission) && productFeatureAppl == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productFeatureAppl for cases when removed to retain passed form values --%>
<%productFeatureAppl = productFeatureApplSave;%>

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
<%if(productFeatureAppl != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> ProductFeatureApplType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> Product</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> ProductFeature</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductFeatureApplType, type: one --%>
<%if(productFeatureAppl != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_VIEW", session)){%>
    <%-- ProductFeatureApplType productFeatureApplTypeRelated = ProductFeatureApplTypeHelper.findByPrimaryKey(productFeatureAppl.getProductFeatureApplTypeId()); --%>
    <%ProductFeatureApplType productFeatureApplTypeRelated = productFeatureAppl.getProductFeatureApplType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductFeatureApplType</b> with (PRODUCT_FEATURE_APPL_TYPE_ID: <%=productFeatureAppl.getProductFeatureApplTypeId()%>)
    </div>
    <%if(productFeatureAppl.getProductFeatureApplTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureApplType?" + "PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureAppl.getProductFeatureApplTypeId())%>" class="buttontext">[View ProductFeatureApplType]</a>      
    <%if(productFeatureApplTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureApplType?" + "PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureAppl.getProductFeatureApplTypeId())%>" class="buttontext">[Create ProductFeatureApplType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productFeatureApplTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureApplType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_APPL_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getProductFeatureApplTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productFeatureApplTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureApplType, type: one --%>
  

<%-- Start Relation for Product, type: one --%>
<%if(productFeatureAppl != null){%>
  <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
    <%-- Product productRelated = ProductHelper.findByPrimaryKey(productFeatureAppl.getProductId()); --%>
    <%Product productRelated = productFeatureAppl.getProduct();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Product</b> with (PRODUCT_ID: <%=productFeatureAppl.getProductId()%>)
    </div>
    <%if(productFeatureAppl.getProductId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + productFeatureAppl.getProductId())%>" class="buttontext">[View Product]</a>      
    <%if(productRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + productFeatureAppl.getProductId())%>" class="buttontext">[Create Product]</a>
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
<%if(productFeatureAppl != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
    <%-- ProductFeature productFeatureRelated = ProductFeatureHelper.findByPrimaryKey(productFeatureAppl.getProductFeatureId()); --%>
    <%ProductFeature productFeatureRelated = productFeatureAppl.getProductFeature();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductFeature</b> with (PRODUCT_FEATURE_ID: <%=productFeatureAppl.getProductFeatureId()%>)
    </div>
    <%if(productFeatureAppl.getProductFeatureId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureAppl.getProductFeatureId())%>" class="buttontext">[View ProductFeature]</a>      
    <%if(productFeatureRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureAppl.getProductFeatureId())%>" class="buttontext">[Create ProductFeature]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_FEATURE_APPL_ADMIN, or PRODUCT_FEATURE_APPL_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
