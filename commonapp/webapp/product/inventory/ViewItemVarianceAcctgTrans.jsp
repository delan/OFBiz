
<%
/**
 *  Title: Item Variance Accounting Transaction Entity
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
 *@created    Fri Jul 27 01:37:21 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.inventory.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewItemVarianceAcctgTrans"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String acctgTransId = request.getParameter("ITEM_VARIANCE_ACCTG_TRANS_ACCTG_TRANS_ID");  


  ItemVarianceAcctgTrans itemVarianceAcctgTrans = ItemVarianceAcctgTransHelper.findByPrimaryKey(acctgTransId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ItemVarianceAcctgTrans</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ItemVarianceAcctgTrans</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ItemVarianceAcctgTrans with (ACCTG_TRANS_ID: <%=acctgTransId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindItemVarianceAcctgTrans")%>" class="buttontext">[Find ItemVarianceAcctgTrans]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewItemVarianceAcctgTrans")%>" class="buttontext">[Create New ItemVarianceAcctgTrans]</a>
<%}%>
<%if(itemVarianceAcctgTrans != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateItemVarianceAcctgTrans?UPDATE_MODE=DELETE&" + "ITEM_VARIANCE_ACCTG_TRANS_ACCTG_TRANS_ID=" + acctgTransId)%>" class="buttontext">[Delete this ItemVarianceAcctgTrans]</a>
  <%}%>
<%}%>

<%if(itemVarianceAcctgTrans == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(itemVarianceAcctgTrans == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ItemVarianceAcctgTrans was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ACCTG_TRANS_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(itemVarianceAcctgTrans.getAcctgTransId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(itemVarianceAcctgTrans.getInventoryItemId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PHYSICAL_INVENTORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(itemVarianceAcctgTrans.getPhysicalInventoryId())%>
    </td>
  </tr>

<%} //end if itemVarianceAcctgTrans == null %>
</table>
  </div>
<%ItemVarianceAcctgTrans itemVarianceAcctgTransSave = itemVarianceAcctgTrans;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(itemVarianceAcctgTrans == null && (acctgTransId != null)){%>
    ItemVarianceAcctgTrans with (ACCTG_TRANS_ID: <%=acctgTransId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    itemVarianceAcctgTrans = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateItemVarianceAcctgTrans")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(itemVarianceAcctgTrans == null){%>
  <%if(hasCreatePermission){%>
    You may create a ItemVarianceAcctgTrans by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>ACCTG_TRANS_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="ITEM_VARIANCE_ACCTG_TRANS_ACCTG_TRANS_ID" value="<%=UtilFormatOut.checkNull(acctgTransId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ItemVarianceAcctgTrans (ITEM_VARIANCE_ACCTG_TRANS_ADMIN, or ITEM_VARIANCE_ACCTG_TRANS_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="ITEM_VARIANCE_ACCTG_TRANS_ACCTG_TRANS_ID" value="<%=acctgTransId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>ACCTG_TRANS_ID</td>
      <td>
        <b><%=acctgTransId%></b> (This cannot be changed without re-creating the itemVarianceAcctgTrans.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ItemVarianceAcctgTrans (ITEM_VARIANCE_ACCTG_TRANS_ADMIN, or ITEM_VARIANCE_ACCTG_TRANS_UPDATE needed).
  <%}%>
<%} //end if itemVarianceAcctgTrans == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>INVENTORY_ITEM_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="ITEM_VARIANCE_ACCTG_TRANS_INVENTORY_ITEM_ID" value="<%if(itemVarianceAcctgTrans!=null){%><%=UtilFormatOut.checkNull(itemVarianceAcctgTrans.getInventoryItemId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("ITEM_VARIANCE_ACCTG_TRANS_INVENTORY_ITEM_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PHYSICAL_INVENTORY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="ITEM_VARIANCE_ACCTG_TRANS_PHYSICAL_INVENTORY_ID" value="<%if(itemVarianceAcctgTrans!=null){%><%=UtilFormatOut.checkNull(itemVarianceAcctgTrans.getPhysicalInventoryId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("ITEM_VARIANCE_ACCTG_TRANS_PHYSICAL_INVENTORY_ID"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && itemVarianceAcctgTrans == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the itemVarianceAcctgTrans for cases when removed to retain passed form values --%>
<%itemVarianceAcctgTrans = itemVarianceAcctgTransSave;%>

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
<%if(itemVarianceAcctgTrans != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PHYSICAL_INVENTORY", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> PhysicalInventory</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> InventoryItem</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> InventoryItemVariance</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for PhysicalInventory, type: one --%>
<%if(itemVarianceAcctgTrans != null){%>
  <%if(Security.hasEntityPermission("PHYSICAL_INVENTORY", "_VIEW", session)){%>
    <%-- PhysicalInventory physicalInventoryRelated = PhysicalInventoryHelper.findByPrimaryKey(itemVarianceAcctgTrans.getPhysicalInventoryId()); --%>
    <%PhysicalInventory physicalInventoryRelated = itemVarianceAcctgTrans.getPhysicalInventory();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>PhysicalInventory</b> with (PHYSICAL_INVENTORY_ID: <%=itemVarianceAcctgTrans.getPhysicalInventoryId()%>)
    </div>
    <%if(itemVarianceAcctgTrans.getPhysicalInventoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPhysicalInventory?" + "PHYSICAL_INVENTORY_PHYSICAL_INVENTORY_ID=" + itemVarianceAcctgTrans.getPhysicalInventoryId())%>" class="buttontext">[View PhysicalInventory]</a>      
    <%if(physicalInventoryRelated == null){%>
      <%if(Security.hasEntityPermission("PHYSICAL_INVENTORY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewPhysicalInventory?" + "PHYSICAL_INVENTORY_PHYSICAL_INVENTORY_ID=" + itemVarianceAcctgTrans.getPhysicalInventoryId())%>" class="buttontext">[Create PhysicalInventory]</a>
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
<%if(itemVarianceAcctgTrans != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
    <%-- InventoryItem inventoryItemRelated = InventoryItemHelper.findByPrimaryKey(itemVarianceAcctgTrans.getInventoryItemId()); --%>
    <%InventoryItem inventoryItemRelated = itemVarianceAcctgTrans.getInventoryItem();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>InventoryItem</b> with (INVENTORY_ITEM_ID: <%=itemVarianceAcctgTrans.getInventoryItemId()%>)
    </div>
    <%if(itemVarianceAcctgTrans.getInventoryItemId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + itemVarianceAcctgTrans.getInventoryItemId())%>" class="buttontext">[View InventoryItem]</a>      
    <%if(inventoryItemRelated == null){%>
      <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + itemVarianceAcctgTrans.getInventoryItemId())%>" class="buttontext">[Create InventoryItem]</a>
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
  

<%-- Start Relation for InventoryItemVariance, type: one --%>
<%if(itemVarianceAcctgTrans != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session)){%>
    <%-- InventoryItemVariance inventoryItemVarianceRelated = InventoryItemVarianceHelper.findByPrimaryKey(itemVarianceAcctgTrans.getInventoryItemId(), itemVarianceAcctgTrans.getPhysicalInventoryId()); --%>
    <%InventoryItemVariance inventoryItemVarianceRelated = itemVarianceAcctgTrans.getInventoryItemVariance();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>InventoryItemVariance</b> with (INVENTORY_ITEM_ID, PHYSICAL_INVENTORY_ID: <%=itemVarianceAcctgTrans.getInventoryItemId()%>, <%=itemVarianceAcctgTrans.getPhysicalInventoryId()%>)
    </div>
    <%if(itemVarianceAcctgTrans.getInventoryItemId() != null && itemVarianceAcctgTrans.getPhysicalInventoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemVariance?" + "INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID=" + itemVarianceAcctgTrans.getInventoryItemId() + "&" + "INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID=" + itemVarianceAcctgTrans.getPhysicalInventoryId())%>" class="buttontext">[View InventoryItemVariance]</a>      
    <%if(inventoryItemVarianceRelated == null){%>
      <%if(Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemVariance?" + "INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID=" + itemVarianceAcctgTrans.getInventoryItemId() + "&" + "INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID=" + itemVarianceAcctgTrans.getPhysicalInventoryId())%>" class="buttontext">[Create InventoryItemVariance]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(inventoryItemVarianceRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified InventoryItemVariance was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemVarianceRelated.getInventoryItemId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PHYSICAL_INVENTORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemVarianceRelated.getPhysicalInventoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>VARIANCE_REASON_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemVarianceRelated.getVarianceReasonId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>QUANTITY</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(inventoryItemVarianceRelated.getQuantity())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COMMENT</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemVarianceRelated.getComment())%>
    </td>
  </tr>

    <%} //end if inventoryItemVarianceRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for InventoryItemVariance, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (ITEM_VARIANCE_ACCTG_TRANS_ADMIN, or ITEM_VARIANCE_ACCTG_TRANS_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
