
<%
/**
 *  Title: Container Entity
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
<%@ page import="org.ofbiz.commonapp.product.storage.*" %>

<%@ page import="org.ofbiz.commonapp.product.inventory.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewContainer"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("CONTAINER", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("CONTAINER", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("CONTAINER", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("CONTAINER", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String containerId = request.getParameter("CONTAINER_CONTAINER_ID");  


  Container container = ContainerHelper.findByPrimaryKey(containerId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View Container</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit Container</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: Container with (CONTAINER_ID: <%=containerId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindContainer")%>" class="buttontext">[Find Container]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewContainer")%>" class="buttontext">[Create New Container]</a>
<%}%>
<%if(container != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateContainer?UPDATE_MODE=DELETE&" + "CONTAINER_CONTAINER_ID=" + containerId)%>" class="buttontext">[Delete this Container]</a>
  <%}%>
<%}%>

<%if(container == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(container == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified Container was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTAINER_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(container.getContainerId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTAINER_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(container.getContainerTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FACILITY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(container.getFacilityId())%>
    </td>
  </tr>

<%} //end if container == null %>
</table>
  </div>
<%Container containerSave = container;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(container == null && (containerId != null)){%>
    Container with (CONTAINER_ID: <%=containerId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    container = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateContainer")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(container == null){%>
  <%if(hasCreatePermission){%>
    You may create a Container by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>CONTAINER_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="CONTAINER_CONTAINER_ID" value="<%=UtilFormatOut.checkNull(containerId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a Container (CONTAINER_ADMIN, or CONTAINER_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="CONTAINER_CONTAINER_ID" value="<%=containerId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>CONTAINER_ID</td>
      <td>
        <b><%=containerId%></b> (This cannot be changed without re-creating the container.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a Container (CONTAINER_ADMIN, or CONTAINER_UPDATE needed).
  <%}%>
<%} //end if container == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>CONTAINER_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="CONTAINER_CONTAINER_TYPE_ID" value="<%if(container!=null){%><%=UtilFormatOut.checkNull(container.getContainerTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("CONTAINER_CONTAINER_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FACILITY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="CONTAINER_FACILITY_ID" value="<%if(container!=null){%><%=UtilFormatOut.checkNull(container.getFacilityId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("CONTAINER_FACILITY_ID"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && container == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the container for cases when removed to retain passed form values --%>
<%container = containerSave;%>

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
<%if(container != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("CONTAINER_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> ContainerType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> InventoryItem</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> Facility</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ContainerType, type: one --%>
<%if(container != null){%>
  <%if(Security.hasEntityPermission("CONTAINER_TYPE", "_VIEW", session)){%>
    <%-- ContainerType containerTypeRelated = ContainerTypeHelper.findByPrimaryKey(container.getContainerTypeId()); --%>
    <%ContainerType containerTypeRelated = container.getContainerType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ContainerType</b> with (CONTAINER_TYPE_ID: <%=container.getContainerTypeId()%>)
    </div>
    <%if(container.getContainerTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewContainerType?" + "CONTAINER_TYPE_CONTAINER_TYPE_ID=" + container.getContainerTypeId())%>" class="buttontext">[View ContainerType]</a>      
    <%if(containerTypeRelated == null){%>
      <%if(Security.hasEntityPermission("CONTAINER_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewContainerType?" + "CONTAINER_TYPE_CONTAINER_TYPE_ID=" + container.getContainerTypeId())%>" class="buttontext">[Create ContainerType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(containerTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ContainerType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTAINER_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(containerTypeRelated.getContainerTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(containerTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if containerTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ContainerType, type: one --%>
  

<%-- Start Relation for InventoryItem, type: many --%>
<%if(container != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemHelper.findByContainerId(container.getContainerId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(container.getInventoryItems());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItem</b> with (CONTAINER_ID: <%=container.getContainerId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_CONTAINER_ID=" + container.getContainerId())%>" class="buttontext">[Create InventoryItem]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ContainerId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + container.getContainerId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindContainer?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItem]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemRelated.getInventoryItemId() + "&" + "CONTAINER_CONTAINER_ID=" + containerId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  

<%-- Start Relation for Facility, type: one --%>
<%if(container != null){%>
  <%if(Security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
    <%-- Facility facilityRelated = FacilityHelper.findByPrimaryKey(container.getFacilityId()); --%>
    <%Facility facilityRelated = container.getFacility();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Facility</b> with (FACILITY_ID: <%=container.getFacilityId()%>)
    </div>
    <%if(container.getFacilityId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + container.getFacilityId())%>" class="buttontext">[View Facility]</a>      
    <%if(facilityRelated == null){%>
      <%if(Security.hasEntityPermission("FACILITY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewFacility?" + "FACILITY_FACILITY_ID=" + container.getFacilityId())%>" class="buttontext">[Create Facility]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (CONTAINER_ADMIN, or CONTAINER_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
