
<%
/**
 *  Title: Inventory Item Attribute Entity
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
<%pageContext.setAttribute("PageName", "ViewInventoryItemAttribute"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String inventoryItemId = request.getParameter("INVENTORY_ITEM_ATTRIBUTE_INVENTORY_ITEM_ID");  
  String name = request.getParameter("INVENTORY_ITEM_ATTRIBUTE_NAME");  


  InventoryItemAttribute inventoryItemAttribute = InventoryItemAttributeHelper.findByPrimaryKey(inventoryItemId, name);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View InventoryItemAttribute</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit InventoryItemAttribute</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: InventoryItemAttribute with (INVENTORY_ITEM_ID, NAME: <%=inventoryItemId%>, <%=name%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindInventoryItemAttribute")%>" class="buttontext">[Find InventoryItemAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemAttribute")%>" class="buttontext">[Create New InventoryItemAttribute]</a>
<%}%>
<%if(inventoryItemAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemAttribute?UPDATE_MODE=DELETE&" + "INVENTORY_ITEM_ATTRIBUTE_INVENTORY_ITEM_ID=" + inventoryItemId + "&" + "INVENTORY_ITEM_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this InventoryItemAttribute]</a>
  <%}%>
<%}%>

<%if(inventoryItemAttribute == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(inventoryItemAttribute == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified InventoryItemAttribute was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemAttribute.getInventoryItemId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemAttribute.getName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>VALUE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemAttribute.getValue())%>
    </td>
  </tr>

<%} //end if inventoryItemAttribute == null %>
</table>
  </div>
<%InventoryItemAttribute inventoryItemAttributeSave = inventoryItemAttribute;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(inventoryItemAttribute == null && (inventoryItemId != null || name != null)){%>
    InventoryItemAttribute with (INVENTORY_ITEM_ID, NAME: <%=inventoryItemId%>, <%=name%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    inventoryItemAttribute = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateInventoryItemAttribute")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(inventoryItemAttribute == null){%>
  <%if(hasCreatePermission){%>
    You may create a InventoryItemAttribute by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>INVENTORY_ITEM_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_ATTRIBUTE_INVENTORY_ITEM_ID" value="<%=UtilFormatOut.checkNull(inventoryItemId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <input class='editInputBox' type="text" size="60" maxlength="60" name="INVENTORY_ITEM_ATTRIBUTE_NAME" value="<%=UtilFormatOut.checkNull(name)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a InventoryItemAttribute (INVENTORY_ITEM_ATTRIBUTE_ADMIN, or INVENTORY_ITEM_ATTRIBUTE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="INVENTORY_ITEM_ATTRIBUTE_INVENTORY_ITEM_ID" value="<%=inventoryItemId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>INVENTORY_ITEM_ID</td>
      <td>
        <b><%=inventoryItemId%></b> (This cannot be changed without re-creating the inventoryItemAttribute.)
      </td>
    </tr>
      <input type="hidden" name="INVENTORY_ITEM_ATTRIBUTE_NAME" value="<%=name%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <b><%=name%></b> (This cannot be changed without re-creating the inventoryItemAttribute.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a InventoryItemAttribute (INVENTORY_ITEM_ATTRIBUTE_ADMIN, or INVENTORY_ITEM_ATTRIBUTE_UPDATE needed).
  <%}%>
<%} //end if inventoryItemAttribute == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>VALUE</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="INVENTORY_ITEM_ATTRIBUTE_VALUE" value="<%if(inventoryItemAttribute!=null){%><%=UtilFormatOut.checkNull(inventoryItemAttribute.getValue())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_ATTRIBUTE_VALUE"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && inventoryItemAttribute == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the inventoryItemAttribute for cases when removed to retain passed form values --%>
<%inventoryItemAttribute = inventoryItemAttributeSave;%>

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
<%if(inventoryItemAttribute != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> InventoryItem</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> InventoryItemTypeAttr</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for InventoryItem, type: one --%>
<%if(inventoryItemAttribute != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
    <%-- InventoryItem inventoryItemRelated = InventoryItemHelper.findByPrimaryKey(inventoryItemAttribute.getInventoryItemId()); --%>
    <%InventoryItem inventoryItemRelated = inventoryItemAttribute.getInventoryItem();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>InventoryItem</b> with (INVENTORY_ITEM_ID: <%=inventoryItemAttribute.getInventoryItemId()%>)
    </div>
    <%if(inventoryItemAttribute.getInventoryItemId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemAttribute.getInventoryItemId())%>" class="buttontext">[View InventoryItem]</a>      
    <%if(inventoryItemRelated == null){%>
      <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemAttribute.getInventoryItemId())%>" class="buttontext">[Create InventoryItem]</a>
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
  

<%-- Start Relation for InventoryItemTypeAttr, type: many --%>
<%if(inventoryItemAttribute != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemTypeAttrHelper.findByName(inventoryItemAttribute.getName())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(inventoryItemAttribute.getInventoryItemTypeAttrs());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItemTypeAttr</b> with (NAME: <%=inventoryItemAttribute.getName()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemTypeAttr?" + "INVENTORY_ITEM_TYPE_ATTR_NAME=" + inventoryItemAttribute.getName())%>" class="buttontext">[Create InventoryItemTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=Name";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + inventoryItemAttribute.getName();%>
    <a href="<%=response.encodeURL(controlPath + "/FindInventoryItemAttribute?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItemTypeAttr]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemTypeAttr?" + "INVENTORY_ITEM_TYPE_ATTR_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeAttrRelated.getInventoryItemTypeId() + "&" + "INVENTORY_ITEM_TYPE_ATTR_NAME=" + inventoryItemTypeAttrRelated.getName() + "&" + "INVENTORY_ITEM_ATTRIBUTE_INVENTORY_ITEM_ID=" + inventoryItemId + "&" + "INVENTORY_ITEM_ATTRIBUTE_NAME=" + name + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (INVENTORY_ITEM_ATTRIBUTE_ADMIN, or INVENTORY_ITEM_ATTRIBUTE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
