<%--
 *  Description: None
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski 
 *@version    $Revision$
 *@since      2.0
--%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("FACILITY", "_CREATE", session)) {%>

<%
	String facilityId = request.getParameter("facilityId"); 
	String productId = request.getParameter("productId");
	GenericValue facility = null;
	GenericValue product = null;

	if (facilityId != null)
		facility = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", facilityId));

	if (productId != null)
		product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));

	String invalidProductId = null;
	if (productId != null && product == null)
		invalidProductId = "No product found with product ID: [" + productId + "]";

	// reject reasons
	List rejectReasons = delegator.findAll("RejectionReason");
	if (rejectReasons != null) pageContext.setAttribute("rejectReasons", rejectReasons);

    // inv item types
    List inventoryItemTypes = delegator.findAll("InventoryItemType");
    if (inventoryItemTypes != null) pageContext.setAttribute("inventoryItemTypes", inventoryItemTypes);

    // facilities
    List facilities = delegator.findAll("Facility");
    if (facilities != null) pageContext.setAttribute("facilities", facilities);
	

%>

<% if (invalidProductId != null) { %>
<br><div class='errorMessage'><%=invalidProductId%></div>
<%}%>

<br>
<%if(facilityId != null && facilityId.length() > 0){%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacility?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Facility</a>
    <a href="<ofbiz:url>/EditFacilityGroups?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Groups</a>
    <a href="<ofbiz:url>/FindFacilityLocations?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Locations</a>
    <a href="<ofbiz:url>/EditFacilityRoles?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Roles</a>
    <a href="<ofbiz:url>/EditFacilityInventoryItems?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Inventory&nbsp;Items</a>
    <a href="<ofbiz:url>/ReceiveInventory?facilityId=<%=facilityId%></ofbiz:url>" class="tabButtonSelected">Inventory&nbsp;Receive</a>
    <a href="<ofbiz:url>/FindFacilityTransfers?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Inventory&nbsp;Xfers</a>
  </div>
<%}%>

<div class="head1">Receive Inventory <span class='head2'>into&nbsp;<%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span></div>
<a href="<ofbiz:url>/EditFacility</ofbiz:url>" class="buttontext">[New Facility]</a>

<script language='JavaScript'>
    function setNow(field) { eval('document.receiveform.' + field + '.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
</script>

<table border='0' cellpadding='2' cellspacing='0'>

<% if (product == null) { %>
	<form method="post" action="<ofbiz:url>/ReceiveInventory</ofbiz:url>" style='margin: 0;'>
	<table border='0' cellpadding='2' cellspacing='0'>
      <tr>
        <td width="25%" align='right'><div class="tabletext">Product ID</div></td>
        <td>&nbsp;</td>
        <td width="25%">
          <input type="text" name="productId" size="20" maxlength="20">
          <input type="hidden" name="facilityId" value="<%=facilityId%>">
        </td>
        <td width="50%">
          <input type="submit" value="Get Product">
        </td>
      </tr>
    </table>
    </form>
<% } else { %>
	<form method="post" action="<ofbiz:url>/receiveInventoryProduct</ofbiz:url>" name='receiveform' style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
      <input type="hidden" name="productId" value="<%=productId%>">
      <input type="hidden" name="facilityId" value="<%=facilityId%>">     
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Product ID</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <b><%=productId%></b>
        </td>                
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Product Name</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <div class="tabletext"><%=UtilFormatOut.checkNull(product.getString("productName"))%></div>
        </td>                
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Product Description</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <div class="tabletext"><%=UtilFormatOut.checkNull(product.getString("description"))%></div>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Item Description</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='itemDescription' size='30' maxlength='60' style='font-size: x-small;'>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Inventory Item <br>(optional will create new if empty)</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='inventoryItemId' size='20' maxlength='20' style='font-size: x-small;'>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Inventory Item Type</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <select name="inventoryItemTypeId" size=1 style='font-size: x-small;'>                        
            <ofbiz:iterator name="nextInventoryItemType" property="inventoryItemTypes">
              <option value='<ofbiz:inputvalue entityAttr="nextInventoryItemType" field="inventoryItemTypeId"/>'><ofbiz:inputvalue entityAttr="nextInventoryItemType" field="description"/></option>
            </ofbiz:iterator>
          </select>
        </td>                
      </tr>	
      <tr>
        <td colspan="4">&nbsp;</td>
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Date Received</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='datetimeReceived' size='22' value="<%=UtilDateTime.nowTimestamp()%>" style='font-size: x-small;'>
          <!--<a href='#' onclick='setNow("datetimeReceived")' class='buttontext'>[Now]</a>-->
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Facility Location</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='locationSeqId' size='20' maxlength="20" style='font-size: x-small;'>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Rejected Reason</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <select name="rejectionId" size=1 style='font-size: x-small;'>   
            <option></option>                     
            <ofbiz:iterator name="nextRejection" property="rejectReasons">
              <option value='<ofbiz:inputvalue entityAttr="nextRejection" field="rejectionId"/>'><ofbiz:inputvalue entityAttr="nextRejection" field="description"/></option>
            </ofbiz:iterator>
          </select>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Quantity Rejected</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='quantityRejected' size='5' value='0' style='font-size: x-small;'>
        </td>                
      </tr>	
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Quantity Accepted</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type='text' name='quantityAccepted' size='5' value='1' style='font-size: x-small;'>
        </td>                
      </tr>	
      <tr>
        <td colspan='2'>&nbsp;</td>
        <td colspan='2'><input type="submit" value="Receive"></td>
      </tr>        				
    </table>
    <script language='JavaScript'>
      document.receiveform.quantityAccepted.focus();
    </script>
    </form>
<% } %>

<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("FACILITY_CREATE" or "FACILITY_ADMIN" needed)</h3>
<%}%>
