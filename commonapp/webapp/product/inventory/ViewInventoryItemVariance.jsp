
<%
/**
 *  Title: Inventory Item Variance Entity
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
 *@created    Fri Jul 27 01:37:20 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.inventory.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewInventoryItemVariance"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String inventoryItemId = request.getParameter("INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID");  
  String physicalInventoryId = request.getParameter("INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID");  


  InventoryItemVariance inventoryItemVariance = InventoryItemVarianceHelper.findByPrimaryKey(inventoryItemId, physicalInventoryId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View InventoryItemVariance</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit InventoryItemVariance</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: InventoryItemVariance with (INVENTORY_ITEM_ID, PHYSICAL_INVENTORY_ID: <%=inventoryItemId%>, <%=physicalInventoryId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindInventoryItemVariance")%>" class="buttontext">[Find InventoryItemVariance]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemVariance")%>" class="buttontext">[Create New InventoryItemVariance]</a>
<%}%>
<%if(inventoryItemVariance != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemVariance?UPDATE_MODE=DELETE&" + "INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID=" + inventoryItemId + "&" + "INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID=" + physicalInventoryId)%>" class="buttontext">[Delete this InventoryItemVariance]</a>
  <%}%>
<%}%>

<%if(inventoryItemVariance == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(inventoryItemVariance == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified InventoryItemVariance was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemVariance.getInventoryItemId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PHYSICAL_INVENTORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemVariance.getPhysicalInventoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>VARIANCE_REASON_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemVariance.getVarianceReasonId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>QUANTITY</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(inventoryItemVariance.getQuantity())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COMMENT</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemVariance.getComment())%>
    </td>
  </tr>

<%} //end if inventoryItemVariance == null %>
</table>
  </div>
<%InventoryItemVariance inventoryItemVarianceSave = inventoryItemVariance;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(inventoryItemVariance == null && (inventoryItemId != null || physicalInventoryId != null)){%>
    InventoryItemVariance with (INVENTORY_ITEM_ID, PHYSICAL_INVENTORY_ID: <%=inventoryItemId%>, <%=physicalInventoryId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    inventoryItemVariance = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateInventoryItemVariance")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(inventoryItemVariance == null){%>
  <%if(hasCreatePermission){%>
    You may create a InventoryItemVariance by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>INVENTORY_ITEM_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID" value="<%=UtilFormatOut.checkNull(inventoryItemId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PHYSICAL_INVENTORY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID" value="<%=UtilFormatOut.checkNull(physicalInventoryId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a InventoryItemVariance (INVENTORY_ITEM_VARIANCE_ADMIN, or INVENTORY_ITEM_VARIANCE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID" value="<%=inventoryItemId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>INVENTORY_ITEM_ID</td>
      <td>
        <b><%=inventoryItemId%></b> (This cannot be changed without re-creating the inventoryItemVariance.)
      </td>
    </tr>
      <input type="hidden" name="INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID" value="<%=physicalInventoryId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PHYSICAL_INVENTORY_ID</td>
      <td>
        <b><%=physicalInventoryId%></b> (This cannot be changed without re-creating the inventoryItemVariance.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a InventoryItemVariance (INVENTORY_ITEM_VARIANCE_ADMIN, or INVENTORY_ITEM_VARIANCE_UPDATE needed).
  <%}%>
<%} //end if inventoryItemVariance == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>VARIANCE_REASON_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_VARIANCE_VARIANCE_REASON_ID" value="<%if(inventoryItemVariance!=null){%><%=UtilFormatOut.checkNull(inventoryItemVariance.getVarianceReasonId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_VARIANCE_VARIANCE_REASON_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>QUANTITY</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="INVENTORY_ITEM_VARIANCE_QUANTITY" value="<%if(inventoryItemVariance!=null){%><%=UtilFormatOut.formatQuantity(inventoryItemVariance.getQuantity())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_VARIANCE_QUANTITY"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>COMMENT</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="INVENTORY_ITEM_VARIANCE_COMMENT" value="<%if(inventoryItemVariance!=null){%><%=UtilFormatOut.checkNull(inventoryItemVariance.getComment())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_VARIANCE_COMMENT"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && inventoryItemVariance == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the inventoryItemVariance for cases when removed to retain passed form values --%>
<%inventoryItemVariance = inventoryItemVarianceSave;%>

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
<%if(inventoryItemVariance != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("VARIANCE_REASON", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> VarianceReason</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PHYSICAL_INVENTORY", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> PhysicalInventory</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> InventoryItem</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> ItemVarianceAcctgTrans</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for VarianceReason, type: one --%>
<%if(inventoryItemVariance != null){%>
  <%if(Security.hasEntityPermission("VARIANCE_REASON", "_VIEW", session)){%>
    <%-- VarianceReason varianceReasonRelated = VarianceReasonHelper.findByPrimaryKey(inventoryItemVariance.getVarianceReasonId()); --%>
    <%VarianceReason varianceReasonRelated = inventoryItemVariance.getVarianceReason();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>VarianceReason</b> with (VARIANCE_REASON_ID: <%=inventoryItemVariance.getVarianceReasonId()%>)
    </div>
    <%if(inventoryItemVariance.getVarianceReasonId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewVarianceReason?" + "VARIANCE_REASON_VARIANCE_REASON_ID=" + inventoryItemVariance.getVarianceReasonId())%>" class="buttontext">[View VarianceReason]</a>      
    <%if(varianceReasonRelated == null){%>
      <%if(Security.hasEntityPermission("VARIANCE_REASON", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewVarianceReason?" + "VARIANCE_REASON_VARIANCE_REASON_ID=" + inventoryItemVariance.getVarianceReasonId())%>" class="buttontext">[Create VarianceReason]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(varianceReasonRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified VarianceReason was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>VARIANCE_REASON_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(varianceReasonRelated.getVarianceReasonId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(varianceReasonRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if varianceReasonRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for VarianceReason, type: one --%>
  

<%-- Start Relation for PhysicalInventory, type: one --%>
<%if(inventoryItemVariance != null){%>
  <%if(Security.hasEntityPermission("PHYSICAL_INVENTORY", "_VIEW", session)){%>
    <%-- PhysicalInventory physicalInventoryRelated = PhysicalInventoryHelper.findByPrimaryKey(inventoryItemVariance.getPhysicalInventoryId()); --%>
    <%PhysicalInventory physicalInventoryRelated = inventoryItemVariance.getPhysicalInventory();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>PhysicalInventory</b> with (PHYSICAL_INVENTORY_ID: <%=inventoryItemVariance.getPhysicalInventoryId()%>)
    </div>
    <%if(inventoryItemVariance.getPhysicalInventoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPhysicalInventory?" + "PHYSICAL_INVENTORY_PHYSICAL_INVENTORY_ID=" + inventoryItemVariance.getPhysicalInventoryId())%>" class="buttontext">[View PhysicalInventory]</a>      
    <%if(physicalInventoryRelated == null){%>
      <%if(Security.hasEntityPermission("PHYSICAL_INVENTORY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewPhysicalInventory?" + "PHYSICAL_INVENTORY_PHYSICAL_INVENTORY_ID=" + inventoryItemVariance.getPhysicalInventoryId())%>" class="buttontext">[Create PhysicalInventory]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(physicalInventoryRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified PhysicalInventory was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PHYSICAL_INVENTORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(physicalInventoryRelated.getPhysicalInventoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(physicalInventoryRelated != null)
        {
          java.util.Date date = physicalInventoryRelated.getDate();
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
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(physicalInventoryRelated.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COMMENT</b></td>
    <td>
      <%=UtilFormatOut.checkNull(physicalInventoryRelated.getComment())%>
    </td>
  </tr>

    <%} //end if physicalInventoryRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for PhysicalInventory, type: one --%>
  

<%-- Start Relation for InventoryItem, type: one --%>
<%if(inventoryItemVariance != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
    <%-- InventoryItem inventoryItemRelated = InventoryItemHelper.findByPrimaryKey(inventoryItemVariance.getInventoryItemId()); --%>
    <%InventoryItem inventoryItemRelated = inventoryItemVariance.getInventoryItem();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>InventoryItem</b> with (INVENTORY_ITEM_ID: <%=inventoryItemVariance.getInventoryItemId()%>)
    </div>
    <%if(inventoryItemVariance.getInventoryItemId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemVariance.getInventoryItemId())%>" class="buttontext">[View InventoryItem]</a>      
    <%if(inventoryItemRelated == null){%>
      <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemVariance.getInventoryItemId())%>" class="buttontext">[Create InventoryItem]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(inventoryItemRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified InventoryItem was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getInventoryItemId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getInventoryItemTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>STATUS_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getStatusTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getFacilityId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTAINER_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getContainerId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>LOT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getLotId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>QUANTITY_ON_HAND</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(inventoryItemRelated.getQuantityOnHand())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SERIAL_NUMBER</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemRelated.getSerialNumber())%>
    </td>
  </tr>

    <%} //end if inventoryItemRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for InventoryItem, type: one --%>
  

<%-- Start Relation for ItemVarianceAcctgTrans, type: many --%>
<%if(inventoryItemVariance != null){%>
  <%if(Security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ItemVarianceAcctgTransHelper.findByInventoryItemIdAndPhysicalInventoryId(inventoryItemVariance.getInventoryItemId(), inventoryItemVariance.getPhysicalInventoryId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(inventoryItemVariance.getItemVarianceAcctgTranss());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ItemVarianceAcctgTrans</b> with (INVENTORY_ITEM_ID, PHYSICAL_INVENTORY_ID: <%=inventoryItemVariance.getInventoryItemId()%>, <%=inventoryItemVariance.getPhysicalInventoryId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewItemVarianceAcctgTrans?" + "ITEM_VARIANCE_ACCTG_TRANS_INVENTORY_ITEM_ID=" + inventoryItemVariance.getInventoryItemId() + "&" + "ITEM_VARIANCE_ACCTG_TRANS_PHYSICAL_INVENTORY_ID=" + inventoryItemVariance.getPhysicalInventoryId())%>" class="buttontext">[Create ItemVarianceAcctgTrans]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=InventoryItemIdAndPhysicalInventoryId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + inventoryItemVariance.getInventoryItemId<%curFindString = curFindString + "&SEARCH_PARAMETER2=" + inventoryItemVariance.getPhysicalInventoryId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindInventoryItemVariance?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ItemVarianceAcctgTrans]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>ACCTG_TRANS_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>INVENTORY_ITEM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PHYSICAL_INVENTORY_ID</nobr></b></div></td>
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
        ItemVarianceAcctgTrans itemVarianceAcctgTransRelated = (ItemVarianceAcctgTrans)relatedIterator.next();
        if(itemVarianceAcctgTransRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(itemVarianceAcctgTransRelated.getAcctgTransId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(itemVarianceAcctgTransRelated.getInventoryItemId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(itemVarianceAcctgTransRelated.getPhysicalInventoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewItemVarianceAcctgTrans?" + "ITEM_VARIANCE_ACCTG_TRANS_ACCTG_TRANS_ID=" + itemVarianceAcctgTransRelated.getAcctgTransId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateItemVarianceAcctgTrans?" + "ITEM_VARIANCE_ACCTG_TRANS_ACCTG_TRANS_ID=" + itemVarianceAcctgTransRelated.getAcctgTransId() + "&" + "INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID=" + inventoryItemId + "&" + "INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID=" + physicalInventoryId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No ItemVarianceAcctgTranss Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ItemVarianceAcctgTrans, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (INVENTORY_ITEM_VARIANCE_ADMIN, or INVENTORY_ITEM_VARIANCE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
