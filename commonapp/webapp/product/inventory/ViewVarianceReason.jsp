
<%
/**
 *  Title: Variance Reason Entity
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
<%pageContext.setAttribute("PageName", "ViewVarianceReason"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("VARIANCE_REASON", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("VARIANCE_REASON", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("VARIANCE_REASON", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("VARIANCE_REASON", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String varianceReasonId = request.getParameter("VARIANCE_REASON_VARIANCE_REASON_ID");  


  VarianceReason varianceReason = VarianceReasonHelper.findByPrimaryKey(varianceReasonId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View VarianceReason</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit VarianceReason</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: VarianceReason with (VARIANCE_REASON_ID: <%=varianceReasonId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindVarianceReason")%>" class="buttontext">[Find VarianceReason]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewVarianceReason")%>" class="buttontext">[Create New VarianceReason]</a>
<%}%>
<%if(varianceReason != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateVarianceReason?UPDATE_MODE=DELETE&" + "VARIANCE_REASON_VARIANCE_REASON_ID=" + varianceReasonId)%>" class="buttontext">[Delete this VarianceReason]</a>
  <%}%>
<%}%>

<%if(varianceReason == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(varianceReason == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified VarianceReason was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>VARIANCE_REASON_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(varianceReason.getVarianceReasonId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(varianceReason.getDescription())%>
    </td>
  </tr>

<%} //end if varianceReason == null %>
</table>
  </div>
<%VarianceReason varianceReasonSave = varianceReason;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(varianceReason == null && (varianceReasonId != null)){%>
    VarianceReason with (VARIANCE_REASON_ID: <%=varianceReasonId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    varianceReason = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateVarianceReason")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(varianceReason == null){%>
  <%if(hasCreatePermission){%>
    You may create a VarianceReason by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>VARIANCE_REASON_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="VARIANCE_REASON_VARIANCE_REASON_ID" value="<%=UtilFormatOut.checkNull(varianceReasonId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a VarianceReason (VARIANCE_REASON_ADMIN, or VARIANCE_REASON_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="VARIANCE_REASON_VARIANCE_REASON_ID" value="<%=varianceReasonId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>VARIANCE_REASON_ID</td>
      <td>
        <b><%=varianceReasonId%></b> (This cannot be changed without re-creating the varianceReason.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a VarianceReason (VARIANCE_REASON_ADMIN, or VARIANCE_REASON_UPDATE needed).
  <%}%>
<%} //end if varianceReason == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="VARIANCE_REASON_DESCRIPTION" value="<%if(varianceReason!=null){%><%=UtilFormatOut.checkNull(varianceReason.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("VARIANCE_REASON_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && varianceReason == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the varianceReason for cases when removed to retain passed form values --%>
<%varianceReason = varianceReasonSave;%>

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
<%if(varianceReason != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> InventoryItemVariance</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for InventoryItemVariance, type: many --%>
<%if(varianceReason != null){%>
  <%if(Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(InventoryItemVarianceHelper.findByVarianceReasonId(varianceReason.getVarianceReasonId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(varianceReason.getInventoryItemVariances());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>InventoryItemVariance</b> with (VARIANCE_REASON_ID: <%=varianceReason.getVarianceReasonId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemVariance?" + "INVENTORY_ITEM_VARIANCE_VARIANCE_REASON_ID=" + varianceReason.getVarianceReasonId())%>" class="buttontext">[Create InventoryItemVariance]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=VarianceReasonId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + varianceReason.getVarianceReasonId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindVarianceReason?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find InventoryItemVariance]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateInventoryItemVariance?" + "INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID=" + inventoryItemVarianceRelated.getInventoryItemId() + "&" + "INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID=" + inventoryItemVarianceRelated.getPhysicalInventoryId() + "&" + "VARIANCE_REASON_VARIANCE_REASON_ID=" + varianceReasonId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  <h3>You do not have permission to view this page (VARIANCE_REASON_ADMIN, or VARIANCE_REASON_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
