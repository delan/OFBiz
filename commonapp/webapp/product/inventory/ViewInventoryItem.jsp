
<%
/**
 *  Title: Inventory Item Entity
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
 *@created    Fri Jul 27 01:37:19 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.inventory.*" %>

<%@ page import="org.ofbiz.commonapp.product.product.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>
<%@ page import="org.ofbiz.commonapp.common.status.*" %>
<%@ page import="org.ofbiz.commonapp.product.storage.*" %>
<%@ page import="org.ofbiz.commonapp.common.uom.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewInventoryItem"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("INVENTORY_ITEM", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("INVENTORY_ITEM", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("INVENTORY_ITEM", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String inventoryItemId = request.getParameter("INVENTORY_ITEM_INVENTORY_ITEM_ID");  


  InventoryItem inventoryItem = InventoryItemHelper.findByPrimaryKey(inventoryItemId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View InventoryItem</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit InventoryItem</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: InventoryItem with (INVENTORY_ITEM_ID: <%=inventoryItemId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindInventoryItem")%>" class="buttontext">[Find InventoryItem]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem")%>" class="buttontext">[Create New InventoryItem]</a>
<%}%>
<%if(inventoryItem != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItem?UPDATE_MODE=DELETE&" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemId)%>" class="buttontext">[Delete this InventoryItem]</a>
  <%}%>
<%}%>

<%if(inventoryItem == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(inventoryItem == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified InventoryItem was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getInventoryItemId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getInventoryItemTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>STATUS_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getStatusTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getFacilityId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTAINER_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getContainerId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>LOT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getLotId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>QUANTITY_ON_HAND</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(inventoryItem.getQuantityOnHand())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SERIAL_NUMBER</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItem.getSerialNumber())%>
    </td>
  </tr>

<%} //end if inventoryItem == null %>
</table>
  </div>
<%InventoryItem inventoryItemSave = inventoryItem;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(inventoryItem == null && (inventoryItemId != null)){%>
    InventoryItem with (INVENTORY_ITEM_ID: <%=inventoryItemId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    inventoryItem = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateInventoryItem")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(inventoryItem == null){%>
  <%if(hasCreatePermission){%>
    You may create a InventoryItem by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>INVENTORY_ITEM_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_INVENTORY_ITEM_ID" value="<%=UtilFormatOut.checkNull(inventoryItemId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a InventoryItem (INVENTORY_ITEM_ADMIN, or INVENTORY_ITEM_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="INVENTORY_ITEM_INVENTORY_ITEM_ID" value="<%=inventoryItemId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>INVENTORY_ITEM_ID</td>
      <td>
        <b><%=inventoryItemId%></b> (This cannot be changed without re-creating the inventoryItem.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a InventoryItem (INVENTORY_ITEM_ADMIN, or INVENTORY_ITEM_UPDATE needed).
  <%}%>
<%} //end if inventoryItem == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>INVENTORY_ITEM_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_INVENTORY_ITEM_TYPE_ID" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.checkNull(inventoryItem.getInventoryItemTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_INVENTORY_ITEM_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_PRODUCT_ID" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.checkNull(inventoryItem.getProductId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_PRODUCT_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_PARTY_ID" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.checkNull(inventoryItem.getPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_PARTY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>STATUS_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_STATUS_TYPE_ID" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.checkNull(inventoryItem.getStatusTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_STATUS_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FACILITY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_FACILITY_ID" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.checkNull(inventoryItem.getFacilityId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_FACILITY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>CONTAINER_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_CONTAINER_ID" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.checkNull(inventoryItem.getContainerId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_CONTAINER_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>LOT_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_LOT_ID" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.checkNull(inventoryItem.getLotId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_LOT_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>UOM_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_UOM_ID" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.checkNull(inventoryItem.getUomId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_UOM_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>QUANTITY_ON_HAND</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="INVENTORY_ITEM_QUANTITY_ON_HAND" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.formatQuantity(inventoryItem.getQuantityOnHand())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_QUANTITY_ON_HAND"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>SERIAL_NUMBER</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="INVENTORY_ITEM_SERIAL_NUMBER" value="<%if(inventoryItem!=null){%><%=UtilFormatOut.checkNull(inventoryItem.getSerialNumber())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_SERIAL_NUMBER"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && inventoryItem == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the inventoryItem for cases when removed to retain passed form values --%>
<%inventoryItem = inventoryItemSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=11;
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
<%if(inventoryItem != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> InventoryItemType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> InventoryItemTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> InventoryItemAttribute</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> Product</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
      <td id=tab5 class=offtab>
        <a href='javascript:ShowTab("tab5")' id=lnk5 class=offlnk> Party</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("STATUS_TYPE", "_VIEW", session)){%>
      <td id=tab6 class=offtab>
        <a href='javascript:ShowTab("tab6")' id=lnk6 class=offlnk> StatusType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
      <td id=tab7 class=offtab>
        <a href='javascript:ShowTab("tab7")' id=lnk7 class=offlnk> Facility</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("CONTAINER", "_VIEW", session)){%>
      <td id=tab8 class=offtab>
        <a href='javascript:ShowTab("tab8")' id=lnk8 class=offlnk> Container</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("LOT", "_VIEW", session)){%>
      <td id=tab9 class=offtab>
        <a href='javascript:ShowTab("tab9")' id=lnk9 class=offlnk> Lot</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
      <td id=tab10 class=offtab>
        <a href='javascript:ShowTab("tab10")' id=lnk10 class=offlnk> Uom</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session)){%>
      <td id=tab11 class=offtab>
        <a href='javascript:ShowTab("tab11")' id=lnk11 class=offlnk> InventoryItemVariance</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for InventoryItemType, type: one --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_VIEW", session)){%>
    <%-- InventoryItemType inventoryItemTypeRelated = InventoryItemTypeHelper.findByPrimaryKey(inventoryItem.getInventoryItemTypeId()); --%>
    <%InventoryItemType inventoryItemTypeRelated = inventoryItem.getInventoryItemType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>InventoryItemType</b> with (INVENTORY_ITEM_TYPE_ID: <%=inventoryItem.getInventoryItemTypeId()%>)
    </div>
    <%if(inventoryItem.getInventoryItemTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemType?" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItem.getInventoryItemTypeId())%>" class="buttontext">[View InventoryItemType]</a>      
    <%if(inventoryItemTypeRelated == null){%>
      <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemType?" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItem.getInventoryItemTypeId())%>" class="buttontext">[Create InventoryItemType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(inventoryItemTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified InventoryItemType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemTypeRelated.getInventoryItemTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if inventoryItemTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for InventoryItemType, type: one --%>
  

<%-- Start Relation for InventoryItemTypeAttr, type: many --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemTypeAttrHelper.findByInventoryItemTypeId(inventoryItem.getInventoryItemTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(inventoryItem.getInventoryItemTypeAttrs());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItemTypeAttr</b> with (INVENTORY_ITEM_TYPE_ID: <%=inventoryItem.getInventoryItemTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemTypeAttr?" + "INVENTORY_ITEM_TYPE_ATTR_INVENTORY_ITEM_TYPE_ID=" + inventoryItem.getInventoryItemTypeId())%>" class="buttontext">[Create InventoryItemTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=InventoryItemTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + inventoryItem.getInventoryItemTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindInventoryItem?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItemTypeAttr]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>INVENTORY_ITEM_TYPE_ID</nobr></b></div></td>
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
        InventoryItemTypeAttr inventoryItemTypeAttrRelated = (InventoryItemTypeAttr)relatedIterator.next();
        if(inventoryItemTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemTypeAttrRelated.getInventoryItemTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemTypeAttrRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemTypeAttr?" + "INVENTORY_ITEM_TYPE_ATTR_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeAttrRelated.getInventoryItemTypeId() + "&" + "INVENTORY_ITEM_TYPE_ATTR_NAME=" + inventoryItemTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemTypeAttr?" + "INVENTORY_ITEM_TYPE_ATTR_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeAttrRelated.getInventoryItemTypeId() + "&" + "INVENTORY_ITEM_TYPE_ATTR_NAME=" + inventoryItemTypeAttrRelated.getName() + "&" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No InventoryItemTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for InventoryItemTypeAttr, type: many --%>
  

<%-- Start Relation for InventoryItemAttribute, type: many --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemAttributeHelper.findByInventoryItemId(inventoryItem.getInventoryItemId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(inventoryItem.getInventoryItemAttributes());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItemAttribute</b> with (INVENTORY_ITEM_ID: <%=inventoryItem.getInventoryItemId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemAttribute?" + "INVENTORY_ITEM_ATTRIBUTE_INVENTORY_ITEM_ID=" + inventoryItem.getInventoryItemId())%>" class="buttontext">[Create InventoryItemAttribute]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=InventoryItemId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + inventoryItem.getInventoryItemId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindInventoryItem?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItemAttribute]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>INVENTORY_ITEM_ID</nobr></b></div></td>
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
        InventoryItemAttribute inventoryItemAttributeRelated = (InventoryItemAttribute)relatedIterator.next();
        if(inventoryItemAttributeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemAttributeRelated.getInventoryItemId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemAttributeRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemAttributeRelated.getValue())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemAttribute?" + "INVENTORY_ITEM_ATTRIBUTE_INVENTORY_ITEM_ID=" + inventoryItemAttributeRelated.getInventoryItemId() + "&" + "INVENTORY_ITEM_ATTRIBUTE_NAME=" + inventoryItemAttributeRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemAttribute?" + "INVENTORY_ITEM_ATTRIBUTE_INVENTORY_ITEM_ID=" + inventoryItemAttributeRelated.getInventoryItemId() + "&" + "INVENTORY_ITEM_ATTRIBUTE_NAME=" + inventoryItemAttributeRelated.getName() + "&" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No InventoryItemAttributes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for InventoryItemAttribute, type: many --%>
  

<%-- Start Relation for Product, type: one --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
    <%-- Product productRelated = ProductHelper.findByPrimaryKey(inventoryItem.getProductId()); --%>
    <%Product productRelated = inventoryItem.getProduct();%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Product</b> with (PRODUCT_ID: <%=inventoryItem.getProductId()%>)
    </div>
    <%if(inventoryItem.getProductId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + inventoryItem.getProductId())%>" class="buttontext">[View Product]</a>      
    <%if(productRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProduct?" + "PRODUCT_PRODUCT_ID=" + inventoryItem.getProductId())%>" class="buttontext">[Create Product]</a>
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
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%-- Party partyRelated = PartyHelper.findByPrimaryKey(inventoryItem.getPartyId()); --%>
    <%Party partyRelated = inventoryItem.getParty();%>
  <DIV id=area5 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=inventoryItem.getPartyId()%>)
    </div>
    <%if(inventoryItem.getPartyId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + inventoryItem.getPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + inventoryItem.getPartyId())%>" class="buttontext">[Create Party]</a>
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
  

<%-- Start Relation for StatusType, type: one --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("STATUS_TYPE", "_VIEW", session)){%>
    <%-- StatusType statusTypeRelated = StatusTypeHelper.findByPrimaryKey(inventoryItem.getStatusTypeId()); --%>
    <%StatusType statusTypeRelated = inventoryItem.getStatusType();%>
  <DIV id=area6 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>StatusType</b> with (STATUS_TYPE_ID: <%=inventoryItem.getStatusTypeId()%>)
    </div>
    <%if(inventoryItem.getStatusTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewStatusType?" + "STATUS_TYPE_STATUS_TYPE_ID=" + inventoryItem.getStatusTypeId())%>" class="buttontext">[View StatusType]</a>      
    <%if(statusTypeRelated == null){%>
      <%if(Security.hasEntityPermission("STATUS_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewStatusType?" + "STATUS_TYPE_STATUS_TYPE_ID=" + inventoryItem.getStatusTypeId())%>" class="buttontext">[Create StatusType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(statusTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified StatusType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>STATUS_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(statusTypeRelated.getStatusTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(statusTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(statusTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(statusTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if statusTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for StatusType, type: one --%>
  

<%-- Start Relation for Facility, type: one --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
    <%-- Facility facilityRelated = FacilityHelper.findByPrimaryKey(inventoryItem.getFacilityId()); --%>
    <%Facility facilityRelated = inventoryItem.getFacility();%>
  <DIV id=area7 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Facility</b> with (FACILITY_ID: <%=inventoryItem.getFacilityId()%>)
    </div>
    <%if(inventoryItem.getFacilityId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + inventoryItem.getFacilityId())%>" class="buttontext">[View Facility]</a>      
    <%if(facilityRelated == null){%>
      <%if(Security.hasEntityPermission("FACILITY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + inventoryItem.getFacilityId())%>" class="buttontext">[Create Facility]</a>
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
  

<%-- Start Relation for Container, type: one --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("CONTAINER", "_VIEW", session)){%>
    <%-- Container containerRelated = ContainerHelper.findByPrimaryKey(inventoryItem.getContainerId()); --%>
    <%Container containerRelated = inventoryItem.getContainer();%>
  <DIV id=area8 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Container</b> with (CONTAINER_ID: <%=inventoryItem.getContainerId()%>)
    </div>
    <%if(inventoryItem.getContainerId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewContainer?" + "CONTAINER_CONTAINER_ID=" + inventoryItem.getContainerId())%>" class="buttontext">[View Container]</a>      
    <%if(containerRelated == null){%>
      <%if(Security.hasEntityPermission("CONTAINER", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewContainer?" + "CONTAINER_CONTAINER_ID=" + inventoryItem.getContainerId())%>" class="buttontext">[Create Container]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(containerRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Container was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTAINER_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(containerRelated.getContainerId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTAINER_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(containerRelated.getContainerTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(containerRelated.getFacilityId())%>
    </td>
  </tr>

    <%} //end if containerRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Container, type: one --%>
  

<%-- Start Relation for Lot, type: one --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("LOT", "_VIEW", session)){%>
    <%-- Lot lotRelated = LotHelper.findByPrimaryKey(inventoryItem.getLotId()); --%>
    <%Lot lotRelated = inventoryItem.getLot();%>
  <DIV id=area9 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Lot</b> with (LOT_ID: <%=inventoryItem.getLotId()%>)
    </div>
    <%if(inventoryItem.getLotId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewLot?" + "LOT_LOT_ID=" + inventoryItem.getLotId())%>" class="buttontext">[View Lot]</a>      
    <%if(lotRelated == null){%>
      <%if(Security.hasEntityPermission("LOT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewLot?" + "LOT_LOT_ID=" + inventoryItem.getLotId())%>" class="buttontext">[Create Lot]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(lotRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Lot was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>LOT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(lotRelated.getLotId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CREATION_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(lotRelated != null)
        {
          java.util.Date date = lotRelated.getCreationDate();
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
    <td><b>QUANTITY</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(lotRelated.getQuantity())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>EXPIRATION_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(lotRelated != null)
        {
          java.util.Date date = lotRelated.getExpirationDate();
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

    <%} //end if lotRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Lot, type: one --%>
  

<%-- Start Relation for Uom, type: one --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("UOM", "_VIEW", session)){%>
    <%-- Uom uomRelated = UomHelper.findByPrimaryKey(inventoryItem.getUomId()); --%>
    <%Uom uomRelated = inventoryItem.getUom();%>
  <DIV id=area10 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Uom</b> with (UOM_ID: <%=inventoryItem.getUomId()%>)
    </div>
    <%if(inventoryItem.getUomId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + inventoryItem.getUomId())%>" class="buttontext">[View Uom]</a>      
    <%if(uomRelated == null){%>
      <%if(Security.hasEntityPermission("UOM", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewUom?" + "UOM_UOM_ID=" + inventoryItem.getUomId())%>" class="buttontext">[Create Uom]</a>
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
  

<%-- Start Relation for InventoryItemVariance, type: many --%>
<%if(inventoryItem != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemVarianceHelper.findByInventoryItemId(inventoryItem.getInventoryItemId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(inventoryItem.getInventoryItemVariances());%>
  <DIV id=area11 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItemVariance</b> with (INVENTORY_ITEM_ID: <%=inventoryItem.getInventoryItemId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemVariance?" + "INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID=" + inventoryItem.getInventoryItemId())%>" class="buttontext">[Create InventoryItemVariance]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=InventoryItemId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + inventoryItem.getInventoryItemId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindInventoryItem?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItemVariance]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>INVENTORY_ITEM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PHYSICAL_INVENTORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>VARIANCE_REASON_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>QUANTITY</nobr></b></div></td>
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
        InventoryItemVariance inventoryItemVarianceRelated = (InventoryItemVariance)relatedIterator.next();
        if(inventoryItemVarianceRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemVarianceRelated.getInventoryItemId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemVarianceRelated.getPhysicalInventoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemVarianceRelated.getVarianceReasonId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(inventoryItemVarianceRelated.getQuantity())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemVarianceRelated.getComment())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemVariance?" + "INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID=" + inventoryItemVarianceRelated.getInventoryItemId() + "&" + "INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID=" + inventoryItemVarianceRelated.getPhysicalInventoryId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemVariance?" + "INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID=" + inventoryItemVarianceRelated.getInventoryItemId() + "&" + "INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID=" + inventoryItemVarianceRelated.getPhysicalInventoryId() + "&" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="7">
<h3>No InventoryItemVariances Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for InventoryItemVariance, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (INVENTORY_ITEM_ADMIN, or INVENTORY_ITEM_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
