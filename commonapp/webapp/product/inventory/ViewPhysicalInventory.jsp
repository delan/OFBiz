
<%
/**
 *  Title: Physical Inventory Entity
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
<%pageContext.setAttribute("PageName", "ViewPhysicalInventory"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PHYSICAL_INVENTORY", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PHYSICAL_INVENTORY", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PHYSICAL_INVENTORY", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PHYSICAL_INVENTORY", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String physicalInventoryId = request.getParameter("PHYSICAL_INVENTORY_PHYSICAL_INVENTORY_ID");  


  PhysicalInventory physicalInventory = PhysicalInventoryHelper.findByPrimaryKey(physicalInventoryId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View PhysicalInventory</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit PhysicalInventory</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: PhysicalInventory with (PHYSICAL_INVENTORY_ID: <%=physicalInventoryId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindPhysicalInventory")%>" class="buttontext">[Find PhysicalInventory]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewPhysicalInventory")%>" class="buttontext">[Create New PhysicalInventory]</a>
<%}%>
<%if(physicalInventory != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdatePhysicalInventory?UPDATE_MODE=DELETE&" + "PHYSICAL_INVENTORY_PHYSICAL_INVENTORY_ID=" + physicalInventoryId)%>" class="buttontext">[Delete this PhysicalInventory]</a>
  <%}%>
<%}%>

<%if(physicalInventory == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(physicalInventory == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified PhysicalInventory was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PHYSICAL_INVENTORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(physicalInventory.getPhysicalInventoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(physicalInventory != null)
        {
          java.util.Date date = physicalInventory.getDate();
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
      <%=UtilFormatOut.checkNull(physicalInventory.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COMMENT</b></td>
    <td>
      <%=UtilFormatOut.checkNull(physicalInventory.getComment())%>
    </td>
  </tr>

<%} //end if physicalInventory == null %>
</table>
  </div>
<%PhysicalInventory physicalInventorySave = physicalInventory;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(physicalInventory == null && (physicalInventoryId != null)){%>
    PhysicalInventory with (PHYSICAL_INVENTORY_ID: <%=physicalInventoryId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    physicalInventory = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdatePhysicalInventory")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(physicalInventory == null){%>
  <%if(hasCreatePermission){%>
    You may create a PhysicalInventory by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PHYSICAL_INVENTORY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PHYSICAL_INVENTORY_PHYSICAL_INVENTORY_ID" value="<%=UtilFormatOut.checkNull(physicalInventoryId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PhysicalInventory (PHYSICAL_INVENTORY_ADMIN, or PHYSICAL_INVENTORY_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PHYSICAL_INVENTORY_PHYSICAL_INVENTORY_ID" value="<%=physicalInventoryId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PHYSICAL_INVENTORY_ID</td>
      <td>
        <b><%=physicalInventoryId%></b> (This cannot be changed without re-creating the physicalInventory.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PhysicalInventory (PHYSICAL_INVENTORY_ADMIN, or PHYSICAL_INVENTORY_UPDATE needed).
  <%}%>
<%} //end if physicalInventory == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(physicalInventory != null)
        {
          java.util.Date date = physicalInventory.getDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("PHYSICAL_INVENTORY_DATE_DATE");
          timeString = request.getParameter("PHYSICAL_INVENTORY_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="PHYSICAL_INVENTORY_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.PHYSICAL_INVENTORY_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="PHYSICAL_INVENTORY_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PHYSICAL_INVENTORY_PARTY_ID" value="<%if(physicalInventory!=null){%><%=UtilFormatOut.checkNull(physicalInventory.getPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PHYSICAL_INVENTORY_PARTY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>COMMENT</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PHYSICAL_INVENTORY_COMMENT" value="<%if(physicalInventory!=null){%><%=UtilFormatOut.checkNull(physicalInventory.getComment())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PHYSICAL_INVENTORY_COMMENT"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && physicalInventory == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the physicalInventory for cases when removed to retain passed form values --%>
<%physicalInventory = physicalInventorySave;%>

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
<%if(physicalInventory != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> InventoryItemVariance</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for InventoryItemVariance, type: many --%>
<%if(physicalInventory != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemVarianceHelper.findByPhysicalInventoryId(physicalInventory.getPhysicalInventoryId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(physicalInventory.getInventoryItemVariances());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItemVariance</b> with (PHYSICAL_INVENTORY_ID: <%=physicalInventory.getPhysicalInventoryId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemVariance?" + "INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID=" + physicalInventory.getPhysicalInventoryId())%>" class="buttontext">[Create InventoryItemVariance]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=PhysicalInventoryId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + physicalInventory.getPhysicalInventoryId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindPhysicalInventory?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItemVariance]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemVariance?" + "INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID=" + inventoryItemVarianceRelated.getInventoryItemId() + "&" + "INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID=" + inventoryItemVarianceRelated.getPhysicalInventoryId() + "&" + "PHYSICAL_INVENTORY_PHYSICAL_INVENTORY_ID=" + physicalInventoryId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  <h3>You do not have permission to view this page (PHYSICAL_INVENTORY_ADMIN, or PHYSICAL_INVENTORY_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
