
<%
/**
 *  Title: Inventory Item Type Entity
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


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewInventoryItemType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String inventoryItemTypeId = request.getParameter("INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID");  


  InventoryItemType inventoryItemType = InventoryItemTypeHelper.findByPrimaryKey(inventoryItemTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View InventoryItemType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit InventoryItemType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: InventoryItemType with (INVENTORY_ITEM_TYPE_ID: <%=inventoryItemTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindInventoryItemType")%>" class="buttontext">[Find InventoryItemType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemType")%>" class="buttontext">[Create New InventoryItemType]</a>
<%}%>
<%if(inventoryItemType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemType?UPDATE_MODE=DELETE&" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeId)%>" class="buttontext">[Delete this InventoryItemType]</a>
  <%}%>
<%}%>

<%if(inventoryItemType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(inventoryItemType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified InventoryItemType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>INVENTORY_ITEM_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemType.getInventoryItemTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(inventoryItemType.getDescription())%>
    </td>
  </tr>

<%} //end if inventoryItemType == null %>
</table>
  </div>
<%InventoryItemType inventoryItemTypeSave = inventoryItemType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(inventoryItemType == null && (inventoryItemTypeId != null)){%>
    InventoryItemType with (INVENTORY_ITEM_TYPE_ID: <%=inventoryItemTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    inventoryItemType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateInventoryItemType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(inventoryItemType == null){%>
  <%if(hasCreatePermission){%>
    You may create a InventoryItemType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>INVENTORY_ITEM_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID" value="<%=UtilFormatOut.checkNull(inventoryItemTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a InventoryItemType (INVENTORY_ITEM_TYPE_ADMIN, or INVENTORY_ITEM_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID" value="<%=inventoryItemTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>INVENTORY_ITEM_TYPE_ID</td>
      <td>
        <b><%=inventoryItemTypeId%></b> (This cannot be changed without re-creating the inventoryItemType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a InventoryItemType (INVENTORY_ITEM_TYPE_ADMIN, or INVENTORY_ITEM_TYPE_UPDATE needed).
  <%}%>
<%} //end if inventoryItemType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="INVENTORY_ITEM_TYPE_PARENT_TYPE_ID" value="<%if(inventoryItemType!=null){%><%=UtilFormatOut.checkNull(inventoryItemType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="INVENTORY_ITEM_TYPE_HAS_TABLE" value="<%if(inventoryItemType!=null){%><%=UtilFormatOut.checkNull(inventoryItemType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="INVENTORY_ITEM_TYPE_DESCRIPTION" value="<%if(inventoryItemType!=null){%><%=UtilFormatOut.checkNull(inventoryItemType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("INVENTORY_ITEM_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && inventoryItemType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the inventoryItemType for cases when removed to retain passed form values --%>
<%inventoryItemType = inventoryItemTypeSave;%>

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
<%if(inventoryItemType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent InventoryItemType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child InventoryItemType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> InventoryItemTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> InventoryItem</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for InventoryItemType, type: one --%>
<%if(inventoryItemType != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_VIEW", session)){%>
    <%-- InventoryItemType inventoryItemTypeRelated = InventoryItemTypeHelper.findByPrimaryKey(inventoryItemType.getParentTypeId()); --%>
    <%InventoryItemType inventoryItemTypeRelated = inventoryItemType.getParentInventoryItemType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>InventoryItemType</b> with (INVENTORY_ITEM_TYPE_ID: <%=inventoryItemType.getParentTypeId()%>)
    </div>
    <%if(inventoryItemType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemType?" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItemType.getParentTypeId())%>" class="buttontext">[View InventoryItemType]</a>      
    <%if(inventoryItemTypeRelated == null){%>
      <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemType?" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItemType.getParentTypeId())%>" class="buttontext">[Create InventoryItemType]</a>
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
  

<%-- Start Relation for InventoryItemType, type: many --%>
<%if(inventoryItemType != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemTypeHelper.findByParentTypeId(inventoryItemType.getInventoryItemTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(inventoryItemType.getChildInventoryItemTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>InventoryItemType</b> with (PARENT_TYPE_ID: <%=inventoryItemType.getInventoryItemTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemType?" + "INVENTORY_ITEM_TYPE_PARENT_TYPE_ID=" + inventoryItemType.getInventoryItemTypeId())%>" class="buttontext">[Create InventoryItemType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + inventoryItemType.getInventoryItemTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindInventoryItemType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItemType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>INVENTORY_ITEM_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>HAS_TABLE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>DESCRIPTION</nobr></b></div></td>
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
        InventoryItemType inventoryItemTypeRelated = (InventoryItemType)relatedIterator.next();
        if(inventoryItemTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemTypeRelated.getInventoryItemTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(inventoryItemTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemType?" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeRelated.getInventoryItemTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemType?" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeRelated.getInventoryItemTypeId() + "&" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No InventoryItemTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for InventoryItemType, type: many --%>
  

<%-- Start Relation for InventoryItemTypeAttr, type: many --%>
<%if(inventoryItemType != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemTypeAttrHelper.findByInventoryItemTypeId(inventoryItemType.getInventoryItemTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(inventoryItemType.getInventoryItemTypeAttrs());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItemTypeAttr</b> with (INVENTORY_ITEM_TYPE_ID: <%=inventoryItemType.getInventoryItemTypeId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemTypeAttr?" + "INVENTORY_ITEM_TYPE_ATTR_INVENTORY_ITEM_TYPE_ID=" + inventoryItemType.getInventoryItemTypeId())%>" class="buttontext">[Create InventoryItemTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=InventoryItemTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + inventoryItemType.getInventoryItemTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindInventoryItemType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItemTypeAttr]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemTypeAttr?" + "INVENTORY_ITEM_TYPE_ATTR_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeAttrRelated.getInventoryItemTypeId() + "&" + "INVENTORY_ITEM_TYPE_ATTR_NAME=" + inventoryItemTypeAttrRelated.getName() + "&" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  

<%-- Start Relation for InventoryItem, type: many --%>
<%if(inventoryItemType != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemHelper.findByInventoryItemTypeId(inventoryItemType.getInventoryItemTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(inventoryItemType.getInventoryItems());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItem</b> with (INVENTORY_ITEM_TYPE_ID: <%=inventoryItemType.getInventoryItemTypeId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_TYPE_ID=" + inventoryItemType.getInventoryItemTypeId())%>" class="buttontext">[Create InventoryItem]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=InventoryItemTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + inventoryItemType.getInventoryItemTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindInventoryItemType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItem]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItem?" + "INVENTORY_ITEM_INVENTORY_ITEM_ID=" + inventoryItemRelated.getInventoryItemId() + "&" + "INVENTORY_ITEM_TYPE_INVENTORY_ITEM_TYPE_ID=" + inventoryItemTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (INVENTORY_ITEM_TYPE_ADMIN, or INVENTORY_ITEM_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
