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
	String inventoryTransferId = request.getParameter("inventoryTransferId");
	String facilityId = request.getParameter("facilityId"); 
    String inventoryItemId = request.getParameter("inventoryItemId");

	GenericValue inventoryTransfer = null;
	GenericValue facility = null;
	GenericValue inventoryItem = null;
	GenericValue inventoryItemType = null;
	GenericValue inventoryStatus = null;

	if (inventoryTransferId != null) {
		inventoryTransfer = delegator.findByPrimaryKey("InventoryTransfer", UtilMisc.toMap("inventoryTransferId", inventoryTransferId));
		if (inventoryTransfer != null) {
			pageContext.setAttribute("inventoryTransfer", inventoryTransfer);
			if (facilityId == null)
				facilityId = inventoryTransfer.getString("facilityId");
			if (inventoryItemId == null)
				inventoryItemId = inventoryTransfer.getString("inventoryItemId");
		}
	}

	if (facilityId != null)
		facility = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", facilityId));

	String illegalInventoryItem = null;
	if (inventoryItemId != null) {
		inventoryItem = delegator.findByPrimaryKey("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId));
		if (facilityId != null && inventoryItem != null && inventoryItem.get("facilityId") != null && !inventoryItem.getString("facilityId").equals(facilityId)) {
			illegalInventoryItem = "Inventory item not found for this facility.";
			inventoryItem = null;
		}
	    if (inventoryItem != null) {
			pageContext.setAttribute("inventoryItem", inventoryItem); 
			inventoryItemType = inventoryItem.getRelatedOne("InventoryItemType");
		
        	if (inventoryItemType != null) pageContext.setAttribute("inventoryItemType", inventoryItemType);
			if (inventoryItem.getString("statusId") != null) {
				inventoryStatus = inventoryItem.getRelatedOne("StatusItem");
				if (inventoryStatus != null) pageContext.setAttribute("inventoryStatus", inventoryStatus);
			}
		}
	}

    // facilities
    List facilities = delegator.findAll("Facility");
    if (facilities != null) pageContext.setAttribute("facilities", facilities);

	// status items
	if (inventoryTransfer != null && UtilValidate.isNotEmpty(inventoryTransfer.getString("statusId"))) {
		List statusChange = delegator.findByAnd("StatusValidChange", UtilMisc.toMap("statusId", inventoryTransfer.getString("statusId")));	
        if (statusChange != null) {
        	List statusItems = new ArrayList();
        	Iterator statusChangeIter = statusChange.iterator();
        	while (statusChangeIter.hasNext()) {
        		GenericValue curStatusChange = (GenericValue) statusChangeIter.next();
        		GenericValue curStatusItem = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", curStatusChange.get("statusIdTo")));
        		if (curStatusItem != null) statusItems.add(curStatusItem);
        	}
			List statusItem = EntityUtil.orderBy(statusItems, UtilMisc.toList("sequenceId"));
        	pageContext.setAttribute("statusItems", statusItems);
        }
    } else {
    	List statusItems = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "INVENTORY_XFER_STTS"), UtilMisc.toList("sequenceId"));
        if (statusItems != null) pageContext.setAttribute("statusItems", statusItems);
    }	
%>

<% if (illegalInventoryItem != null) { %>
<br><div class='errorMessage'><%=illegalInventoryItem%></div>
<%}%>

<br>
<%if(facilityId != null && facilityId.length() > 0){%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacility?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Facility</a>
    <a href="<ofbiz:url>/EditFacilityGroups?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Groups</a>
    <a href="<ofbiz:url>/FindFacilityLocations?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Locations</a>
    <a href="<ofbiz:url>/EditFacilityRoles?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Roles</a>
    <a href="<ofbiz:url>/EditFacilityInventoryItems?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Inventory&nbsp;Items</a>
    <a href="<ofbiz:url>/ReceiveInventory?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Inventory&nbsp;Receive</a>
    <a href="<ofbiz:url>/FindFacilityTransfers?facilityId=<%=facilityId%></ofbiz:url>" class="tabButtonSelected">Inventory&nbsp;Xfers</a>
  </div>
<%}%>

<div class="head1">Inventory Transfer <span class='head2'>from&nbsp;<%=UtilFormatOut.ifNotEmpty(facility==null?null:facility.getString("facilityName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(facilityId)%>]</span></div>
<a href="<ofbiz:url>/EditFacility</ofbiz:url>" class="buttontext">[New Facility]</a>

<table border='0' cellpadding='2' cellspacing='0'>

<% if (inventoryItem == null) { %>
	<form method="post" action="<ofbiz:url>/TransferInventoryItem</ofbiz:url>" style='margin: 0;'>
	<table border='0' cellpadding='2' cellspacing='0'>
      <tr>
        <td width="25%" align='right'><div class="tabletext">Inventory Item ID</div></td>
        <td width='6%'>&nbsp;</td>
        <td width="25%">
          <input type="text" name="inventoryItemId" size="20" maxlength="20">
          <input type="hidden" name="facilityId" value="<%=facilityId%>">
        </td>
        <td width="50%">
          <input type="submit" value="Get Item">
        </td>
      </tr>
    </table>
    </form>
<% } else { %>
<% if (inventoryTransfer == null) { %>
    <form method="post" action="<ofbiz:url>/CreateInventoryTransfer</ofbiz:url>" name='transferform' style='margin: 0;'>
<% } else { %>
    <form method="post" action="<ofbiz:url>/UpdateInventoryTransfer</ofbiz:url>" name='transferform' style='margin: 0;'>
    <input type="hidden" name="inventoryTransferId" value="<%=inventoryTransferId%>">
<%}%>

<script language='JavaScript'>
    function setNow(field) { eval('document.transferform.' + field + '.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
</script>

	<table border='0' cellpadding='2' cellspacing='0'>
      <input type="hidden" name="inventoryItemId" value="<%=inventoryItemId%>">
      <input type="hidden" name="facilityId" value="<%=facilityId%>">
      <input type="hidden" <ofbiz:inputvalue entityAttr="inventoryItem" field="locationSeqId" fullattrs="true"/>>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%'align='right'><div class="tabletext">InventoryItem ID</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <b><%=inventoryItemId%></b>
        </td>
      </tr>

      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">InventoryItem Type Id</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%' 
          <ofbiz:if name="inventoryItemType">                   
            <div class="tabletext"><ofbiz:inputvalue entityAttr="inventoryItemType" field="description"/></div>
          </ofbiz:if>
        </td>
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Product Id</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'            
            <%if (inventoryItem != null && UtilValidate.isNotEmpty(inventoryItem.getString("productId"))) {%>
                <a href='/catalog/control/EditProduct?productId=<ofbiz:inputvalue entityAttr="inventoryItem" field="productId"/>' class='buttontext'><ofbiz:inputvalue entityAttr="inventoryItem" field="productId"/></a>
            <%}%>
        </td>
      </tr>    
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Status</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
		  <%if (inventoryStatus != null) {%>
            <div class="tabletext"><ofbiz:inputvalue entityAttr="inventoryStatus" field="description"/></div>
          <%} else {%>
		    <div class="tabletext">--</div>
          <%}%>                  
       </td>
      </tr>     
      
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Comments</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <% if (inventoryItem.get("comments") != null) { %>
            <div class="tabletext"><ofbiz:inputvalue entityAttr="inventoryItem" field="comments"/></td></div>
          <%} else {%>
            <div class="tabletext">--</div?
          <%}%>
        </td>
      </tr>
    
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Serial# | ATP/QOH</div></td>
        <td width='6%'>&nbsp;</td>
        <%if (inventoryItem != null && "NON_SERIAL_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>
        <td width='74%'
            <div class="tabletext"><ofbiz:inputvalue entityAttr="inventoryItem" field="availableToPromise"/>&nbsp;
            /&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="quantityOnHand"/></div>
        </td>      
        <%} else if (inventoryItem != null && "SERIALIZED_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>             
        <td width='74%'<div class="tabletext"><ofbiz:inputvalue entityAttr="inventoryItem" field="serialNumber"/></div></td>      
        <%} else if (inventoryItem != null) {%>            
        <td width='74%'<div class='tabletext' style='color: red;'>Error: type <ofbiz:entityfield attribute="inventoryItem" field="inventoryItemTypeId"/> unknown; specify a type.</div></td>
      </tr>
    <%}%>

      <tr>
        <td width='14%'>&nbsp;</td>    
        <td colspan="3"><hr class="sepbar"></td>
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Transfer Status</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <select name="statusId" style='font-size: x-small;'>
            <%GenericValue curStatusItem = inventoryTransfer == null ? null : inventoryTransfer.getRelatedOneCache("StatusItem");%>
            <option value='<ofbiz:inputvalue entityAttr="inventoryTransfer" field="statusId"/>'><%if (curStatusItem != null) {%><%=curStatusItem.getString("description")%><%} else {%><ofbiz:entityfield attribute="inventoryTransfer" field="statusId" prefix="[" suffix="]"/><%}%></option>
            <option value='<ofbiz:inputvalue entityAttr="inventoryTransfer" field="statusId"/>'>----</option>
            <ofbiz:iterator name="statusItem" property="statusItems">
              <option value='<ofbiz:inputvalue entityAttr="statusItem" field="statusId"/>'><ofbiz:inputvalue entityAttr="statusItem" field="description"/></option>
            </ofbiz:iterator>
          </select>
        </td>        
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Transfer Send Date</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <input type="text" <ofbiz:inputvalue entityAttr="inventoryTransfer" field="sendDate" fullattrs="true"/> size="22" style='font-size: x-small;'>
          <a href='#' onclick='setNow("sendDate")' class='buttontext'>[Now]</a>
        </td>
      </tr>
      <% if (inventoryTransfer == null) { %>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">To Facility/Container</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <span class='tabletext'>Select a Facility:</span>
          <select name="facilityIdTo" style='font-size: x-small;'>           
            <ofbiz:iterator name="nextFacility" property="facilities">
              <option value='<ofbiz:inputvalue entityAttr="nextFacility" field="facilityId"/>'><ofbiz:inputvalue entityAttr="nextFacility" field="facilityName"/> <ofbiz:entityfield attribute="nextFacility" field="facilityId" prefix="[" suffix="]"/></option>
            </ofbiz:iterator>
          </select>            
          <br>
          <span class='tabletext'>OR enter a Container ID:</span>
          <input type="text" name="containerIdTo" value='<ofbiz:inputvalue entityAttr="inventoryTransfer" field="containerIdTo"/>' size="20" maxlength="20" style='font-size: x-small;'>
        </td>
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">To Location</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <input type="text" size="20" name="locationSeqIdTo" value='<ofbiz:inputvalue entityAttr="inventoryTransfer" field="locationSeqIdTo"/>' maxlength="20" style='font-size: x-small;'>
        </td>
      </tr>      
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Comments</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <input type="text" name="comments" size="60" maxlength="250" style='font-size: x-small;'>
        </td>
      </tr>  
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Quantity To Transfer</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <%if (inventoryItem != null && "NON_SERIAL_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>
            <input type=text size='5' name='xferQty' value='<ofbiz:inputvalue entityAttr="inventoryItem" field="availableToPromise"/>' style='font-size: x-small;'>
          <%} else if (inventoryItem != null && "SERIALIZED_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>
            <input type="hidden" name="xferQty" value="1">
            <div class="tabletext">1</div>
          <%} else if (inventoryItem != null) {%>
            <div class='tabletext' style='color: red;'>Error: type <ofbiz:entityfield attribute="inventoryItem" field="inventoryItemTypeId"/> unknown; specify a type.</div>
          <%}%>
        </td>
      </tr>
      <% } else { %>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Transfer Receive Date</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <input type="text" <ofbiz:inputvalue entityAttr="inventoryTransfer" field="receiveDate" fullattrs="true"/> size="22" style='font-size: x-small;'>
          <a href='#' onclick='setNow("receiveDate")' class='buttontext'>[Now]</a>
        </td>
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">To Facility/Container</div></td>
        <td width='6%'>&nbsp;</td>
        <% GenericValue fac = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", inventoryTransfer.getString("facilityIdTo"))); %>
        <td width='74%'<div class="tabletext"><%=UtilFormatOut.checkNull(fac.getString("facilityName"))%></div></td>
      </tr>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">To Location</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <input type="text" size="20" name="locationSeqIdTo" value='<ofbiz:inputvalue entityAttr="inventoryTransfer" field="locationSeqIdTo"/>' maxlength="20" style='font-size: x-small;'>
        </td>
      </tr>  
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">Comments</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'
          <input type="text" name="comments" value='<ofbiz:inputvalue entityAttr="inventoryTransfer" field="comments"/>' size="60" maxlength="250" style='font-size: x-small;'>
        </td>
      </tr>      
      <% } %>
      <tr>
        <td colspan='2'>&nbsp;</td>
        <% if (inventoryTransfer == null) { %>
        <td colspan='1' align=left><input type="submit" value="Transfer"></td>
        <% } else { %>
        <td colspan='1' align=left><input type="submit" value="Update"></td>
        <% } %>
      </tr>
    </table>
  </form>
<% } %>

<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("FACILITY_CREATE" or "FACILITY_ADMIN" needed)</h3>
<%}%>
