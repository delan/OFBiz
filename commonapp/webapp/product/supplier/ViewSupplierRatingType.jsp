
<%
/**
 *  Title: Supplier Rating Type Entity
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


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewSupplierRatingType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("SUPPLIER_RATING_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("SUPPLIER_RATING_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SUPPLIER_RATING_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SUPPLIER_RATING_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String supplierRatingTypeId = request.getParameter("SUPPLIER_RATING_TYPE_SUPPLIER_RATING_TYPE_ID");  


  SupplierRatingType supplierRatingType = SupplierRatingTypeHelper.findByPrimaryKey(supplierRatingTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View SupplierRatingType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit SupplierRatingType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: SupplierRatingType with (SUPPLIER_RATING_TYPE_ID: <%=supplierRatingTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindSupplierRatingType")%>" class="buttontext">[Find SupplierRatingType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewSupplierRatingType")%>" class="buttontext">[Create New SupplierRatingType]</a>
<%}%>
<%if(supplierRatingType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateSupplierRatingType?UPDATE_MODE=DELETE&" + "SUPPLIER_RATING_TYPE_SUPPLIER_RATING_TYPE_ID=" + supplierRatingTypeId)%>" class="buttontext">[Delete this SupplierRatingType]</a>
  <%}%>
<%}%>

<%if(supplierRatingType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(supplierRatingType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified SupplierRatingType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SUPPLIER_RATING_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierRatingType.getSupplierRatingTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(supplierRatingType.getDescription())%>
    </td>
  </tr>

<%} //end if supplierRatingType == null %>
</table>
  </div>
<%SupplierRatingType supplierRatingTypeSave = supplierRatingType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(supplierRatingType == null && (supplierRatingTypeId != null)){%>
    SupplierRatingType with (SUPPLIER_RATING_TYPE_ID: <%=supplierRatingTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    supplierRatingType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateSupplierRatingType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(supplierRatingType == null){%>
  <%if(hasCreatePermission){%>
    You may create a SupplierRatingType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>SUPPLIER_RATING_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="SUPPLIER_RATING_TYPE_SUPPLIER_RATING_TYPE_ID" value="<%=UtilFormatOut.checkNull(supplierRatingTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a SupplierRatingType (SUPPLIER_RATING_TYPE_ADMIN, or SUPPLIER_RATING_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="SUPPLIER_RATING_TYPE_SUPPLIER_RATING_TYPE_ID" value="<%=supplierRatingTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>SUPPLIER_RATING_TYPE_ID</td>
      <td>
        <b><%=supplierRatingTypeId%></b> (This cannot be changed without re-creating the supplierRatingType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a SupplierRatingType (SUPPLIER_RATING_TYPE_ADMIN, or SUPPLIER_RATING_TYPE_UPDATE needed).
  <%}%>
<%} //end if supplierRatingType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="SUPPLIER_RATING_TYPE_DESCRIPTION" value="<%if(supplierRatingType!=null){%><%=UtilFormatOut.checkNull(supplierRatingType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("SUPPLIER_RATING_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && supplierRatingType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the supplierRatingType for cases when removed to retain passed form values --%>
<%supplierRatingType = supplierRatingTypeSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=1;
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
<%if(supplierRatingType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("SUPPLIER_PRODUCT", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> SupplierProduct</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for SupplierProduct, type: many --%>
<%if(supplierRatingType != null){%>
  <%if(Security.hasEntityPermission("SUPPLIER_PRODUCT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(SupplierProductHelper.findBySupplierRatingTypeId(supplierRatingType.getSupplierRatingTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(supplierRatingType.getSupplierProducts());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>SupplierProduct</b> with (SUPPLIER_RATING_TYPE_ID: <%=supplierRatingType.getSupplierRatingTypeId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewSupplierProduct?" + "SUPPLIER_PRODUCT_SUPPLIER_RATING_TYPE_ID=" + supplierRatingType.getSupplierRatingTypeId())%>" class="buttontext">[Create SupplierProduct]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=SupplierRatingTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + supplierRatingType.getSupplierRatingTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindSupplierRatingType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find SupplierProduct]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateSupplierProduct?" + "SUPPLIER_PRODUCT_PRODUCT_ID=" + supplierProductRelated.getProductId() + "&" + "SUPPLIER_PRODUCT_PARTY_ID=" + supplierProductRelated.getPartyId() + "&" + "SUPPLIER_RATING_TYPE_SUPPLIER_RATING_TYPE_ID=" + supplierRatingTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  <h3>You do not have permission to view this page (SUPPLIER_RATING_TYPE_ADMIN, or SUPPLIER_RATING_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
