
<%
/**
 *  Title: Good Identification Entity
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
 *@created    Fri Jul 27 01:37:09 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.product.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewGoodIdentification"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("GOOD_IDENTIFICATION", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("GOOD_IDENTIFICATION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("GOOD_IDENTIFICATION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("GOOD_IDENTIFICATION", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String goodIdentificationTypeId = request.getParameter("GOOD_IDENTIFICATION_GOOD_IDENTIFICATION_TYPE_ID");  
  String productId = request.getParameter("GOOD_IDENTIFICATION_PRODUCT_ID");  


  GoodIdentification goodIdentification = GoodIdentificationHelper.findByPrimaryKey(goodIdentificationTypeId, productId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View GoodIdentification</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit GoodIdentification</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: GoodIdentification with (GOOD_IDENTIFICATION_TYPE_ID, PRODUCT_ID: <%=goodIdentificationTypeId%>, <%=productId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindGoodIdentification")%>" class="buttontext">[Find GoodIdentification]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentification")%>" class="buttontext">[Create New GoodIdentification]</a>
<%}%>
<%if(goodIdentification != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateGoodIdentification?UPDATE_MODE=DELETE&" + "GOOD_IDENTIFICATION_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationTypeId + "&" + "GOOD_IDENTIFICATION_PRODUCT_ID=" + productId)%>" class="buttontext">[Delete this GoodIdentification]</a>
  <%}%>
<%}%>

<%if(goodIdentification == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(goodIdentification == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified GoodIdentification was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GOOD_IDENTIFICATION_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentification.getGoodIdentificationTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentification.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ID_VALUE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentification.getIdValue())%>
    </td>
  </tr>

<%} //end if goodIdentification == null %>
</table>
  </div>
<%GoodIdentification goodIdentificationSave = goodIdentification;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(goodIdentification == null && (goodIdentificationTypeId != null || productId != null)){%>
    GoodIdentification with (GOOD_IDENTIFICATION_TYPE_ID, PRODUCT_ID: <%=goodIdentificationTypeId%>, <%=productId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    goodIdentification = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateGoodIdentification")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(goodIdentification == null){%>
  <%if(hasCreatePermission){%>
    You may create a GoodIdentification by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GOOD_IDENTIFICATION_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="GOOD_IDENTIFICATION_GOOD_IDENTIFICATION_TYPE_ID" value="<%=UtilFormatOut.checkNull(goodIdentificationTypeId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="GOOD_IDENTIFICATION_PRODUCT_ID" value="<%=UtilFormatOut.checkNull(productId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a GoodIdentification (GOOD_IDENTIFICATION_ADMIN, or GOOD_IDENTIFICATION_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="GOOD_IDENTIFICATION_GOOD_IDENTIFICATION_TYPE_ID" value="<%=goodIdentificationTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GOOD_IDENTIFICATION_TYPE_ID</td>
      <td>
        <b><%=goodIdentificationTypeId%></b> (This cannot be changed without re-creating the goodIdentification.)
      </td>
    </tr>
      <input type="hidden" name="GOOD_IDENTIFICATION_PRODUCT_ID" value="<%=productId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ID</td>
      <td>
        <b><%=productId%></b> (This cannot be changed without re-creating the goodIdentification.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a GoodIdentification (GOOD_IDENTIFICATION_ADMIN, or GOOD_IDENTIFICATION_UPDATE needed).
  <%}%>
<%} //end if goodIdentification == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>ID_VALUE</td>
    <td>
      <input class='editInputBox' type="text" size="60" maxlength="60" name="GOOD_IDENTIFICATION_ID_VALUE" value="<%if(goodIdentification!=null){%><%=UtilFormatOut.checkNull(goodIdentification.getIdValue())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("GOOD_IDENTIFICATION_ID_VALUE"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && goodIdentification == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the goodIdentification for cases when removed to retain passed form values --%>
<%goodIdentification = goodIdentificationSave;%>

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
<%if(goodIdentification != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> GoodIdentificationType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> Product</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for GoodIdentificationType, type: one --%>
<%if(goodIdentification != null){%>
  <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_VIEW", session)){%>
    <%-- GoodIdentificationType goodIdentificationTypeRelated = GoodIdentificationTypeHelper.findByPrimaryKey(goodIdentification.getGoodIdentificationTypeId()); --%>
    <%GoodIdentificationType goodIdentificationTypeRelated = goodIdentification.getGoodIdentificationType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>GoodIdentificationType</b> with (GOOD_IDENTIFICATION_TYPE_ID: <%=goodIdentification.getGoodIdentificationTypeId()%>)
    </div>
    <%if(goodIdentification.getGoodIdentificationTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentificationType?" + "GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentification.getGoodIdentificationTypeId())%>" class="buttontext">[View GoodIdentificationType]</a>      
    <%if(goodIdentificationTypeRelated == null){%>
      <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentificationType?" + "GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentification.getGoodIdentificationTypeId())%>" class="buttontext">[Create GoodIdentificationType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(goodIdentificationTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified GoodIdentificationType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GOOD_IDENTIFICATION_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getGoodIdentificationTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if goodIdentificationTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for GoodIdentificationType, type: one --%>
  

<%-- Start Relation for Product, type: one --%>
<%if(goodIdentification != null){%>
  <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
    <%-- Product productRelated = ProductHelper.findByPrimaryKey(goodIdentification.getProductId()); --%>
    <%Product productRelated = goodIdentification.getProduct();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Product</b> with (PRODUCT_ID: <%=goodIdentification.getProductId()%>)
    </div>
    <%if(goodIdentification.getProductId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + goodIdentification.getProductId())%>" class="buttontext">[View Product]</a>      
    <%if(productRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + goodIdentification.getProductId())%>" class="buttontext">[Create Product]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (GOOD_IDENTIFICATION_ADMIN, or GOOD_IDENTIFICATION_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
