<%--
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
 *@created    Sep 10 2001
 *@version    1.0
--%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("FACILITY", "_VIEW", session)) {%>
<%
	String facilityId = request.getParameter("facilityId");
	String locationSeqId = request.getParameter("locationSeqId");

	session.removeAttribute("inventoryItemId");

    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String inventoryItemId = request.getParameter("inventoryItemId");
    if (UtilValidate.isEmpty(inventoryItemId) && UtilValidate.isNotEmpty((String) request.getAttribute("inventoryItemId"))) {
        inventoryItemId = (String) request.getAttribute("inventoryItemId");
    }
    GenericValue inventoryItem = delegator.findByPrimaryKey("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId));
    GenericValue inventoryItemType = null;
    GenericValue facilityLocation = null;
    GenericValue facility = null;
    if(inventoryItem == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("inventoryItem", inventoryItem);

        inventoryItemType = inventoryItem.getRelatedOne("InventoryItemType");
        if (inventoryItemType != null) pageContext.setAttribute("inventoryItemType", inventoryItemType);

		facilityLocation = inventoryItem.getRelatedOne("FacilityLocation");
		if (facilityLocation != null) pageContext.setAttribute("facilityLocation", facilityLocation);

        facility = inventoryItem.getRelatedOne("Facility");
        if (facility != null) pageContext.setAttribute("facility", facility);
		if (facility != null && facilityId == null) facilityId = facility.getString("facilityId");

        //statuses
        if ("NON_SERIAL_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {
            //do nothing for non-serialized inventory
        } else if ("SERIALIZED_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {
            if (UtilValidate.isNotEmpty(inventoryItem.getString("statusId"))) {
                Collection statusChange = delegator.findByAnd("StatusValidChange",UtilMisc.toMap("statusId",inventoryItem.getString("statusId")));
                if (statusChange != null) {
                    Collection statusItems = new ArrayList();
                    Iterator statusChangeIter = statusChange.iterator();
                    while (statusChangeIter.hasNext()) {
                        GenericValue curStatusChange = (GenericValue) statusChangeIter.next();
                        GenericValue curStatusItem = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", curStatusChange.get("statusIdTo")));
                        if (curStatusItem != null) statusItems.add(curStatusItem);
                    }
                    pageContext.setAttribute("statusItems", statusItems);
                }
            } else {
                //no status id, just get all statusItems
                Collection statusItems = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "INV_SERIALIZED_STTS"));
                if (statusItems != null) pageContext.setAttribute("statusItems", statusItems);
            }
        }
    }

    //inv item types
    Collection inventoryItemTypes = delegator.findAll("InventoryItemType");
    if (inventoryItemTypes != null) pageContext.setAttribute("inventoryItemTypes", inventoryItemTypes);

    //facilities
    Collection facilities = delegator.findAll("Facility");
    if (facilities != null) pageContext.setAttribute("facilities", facilities);

    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));
%>

<br>
<%if(facilityId != null && facilityId.length() > 0){%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacility?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Facility</a>
    <a href="<ofbiz:url>/EditFacilityGroups?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Groups</a>
    <a href="<ofbiz:url>/FindFacilityLocations?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Locations</a>
    <a href="<ofbiz:url>/EditFacilityRoles?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Roles</a>
    <a href="<ofbiz:url>/EditFacilityInventoryItems?facilityId=<%=facilityId%></ofbiz:url>" class="tabButtonSelected">Inventory&nbsp;Items</a>
    <a href="<ofbiz:url>/FindFacilityTransfers?facilityId=<%=facilityId%></ofbiz:url>" class="tabButton">Inventory&nbsp;Xfers</a>
  </div>
<%}%>

<div class="head1">Edit InventoryItem with ID "<%=UtilFormatOut.checkNull(inventoryItemId)%>"</div>
<a href="<ofbiz:url>/EditInventoryItem<%=facilityId==null ? "" : "?facilityId=" + facilityId%></ofbiz:url>" class="buttontext">[New InventoryItem]</a>
<% if (inventoryItemId != null) {%>
<a href='<ofbiz:url>/TransferInventoryItem?inventoryItemId=<%=inventoryItemId%><%=facilityId==null ? "" : "&facilityId=" + facilityId%></ofbiz:url>' class="buttontext">[Transfer Item]</a>
<%}%>


<%if(inventoryItem == null){%>
  <%if(inventoryItemId != null){%>
    <form action="<ofbiz:url>/CreateInventoryItem</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <h3>Could not find inventoryItem with ID "<%=inventoryItemId%>".</h3>
  <%}else{%>
    <form action="<ofbiz:url>/CreateInventoryItem</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
  <%}%>
<%}else{%>
  <form action="<ofbiz:url>/UpdateInventoryItem</ofbiz:url>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="inventoryItemId" value="<%=inventoryItemId%>">
  <tr>
    <td align=right><div class="tabletext">InventoryItem ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=inventoryItemId%></b> (This cannot be changed without re-creating the inventoryItem.)
    </td>
  </tr>
<%}%>
      <tr>
        <td align=right><div class="tabletext">InventoryItem Type Id</div></td>
        <td>&nbsp;</td>
        <td>
          <%-- <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(tryEntity?inventoryItem.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"> --%>
          <select name="inventoryItemTypeId" size=1 style='font-size: x-small;'>
            <option selected value='<ofbiz:inputvalue entityAttr="inventoryItemType" field="inventoryItemTypeId"/>'><ofbiz:inputvalue entityAttr="inventoryItemType" field="description"/> <%--<ofbiz:entityfield attribute="inventoryItemType" field="inventoryItemTypeId" prefix="[" suffix="]"/>--%></option>
            <option value='<ofbiz:inputvalue entityAttr="inventoryItemType" field="inventoryItemTypeId"/>'>----</option>
            <ofbiz:iterator name="nextInventoryItemType" property="inventoryItemTypes">
              <option value='<ofbiz:inputvalue entityAttr="nextInventoryItemType" field="inventoryItemTypeId"/>'><ofbiz:inputvalue entityAttr="nextInventoryItemType" field="description"/> <%--<ofbiz:entityfield attribute="nextInventoryItemType" field="inventoryItemTypeId" prefix="[" suffix="]"/>--%></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Product Id</div></td>
        <td>&nbsp;</td>
        <td>
            <input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="productId" fullattrs="true" tryEntityAttr="tryEntity"/> size="20" maxlength="20" style='font-size: x-small;'>
            <%if (inventoryItem != null && UtilValidate.isNotEmpty(inventoryItem.getString("productId"))) {%>
                <a href='/catalog/control/EditProduct?productId=<ofbiz:inputvalue entityAttr="inventoryItem" field="productId"/>' class='buttontext'>[Edit&nbsp;Product&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="productId"/>]</a>
            <%}%>
        </td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Party Id</div></td>
        <td>&nbsp;</td>
        <td><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="partyId" fullattrs="true" tryEntityAttr="tryEntity"/> size="20" maxlength="20" style='font-size: x-small;'></td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Status</div></td>
        <td>&nbsp;</td>
        <td>
          <select name="statusId" style='font-size: x-small;'>
            <%GenericValue curStatusItem = inventoryItem == null ? null : inventoryItem.getRelatedOneCache("StatusItem");%>
            <option value='<ofbiz:inputvalue entityAttr="inventoryItem" field="statusId"/>'><%if (curStatusItem != null) {%><%=curStatusItem.getString("description")%><%} else {%><ofbiz:entityfield attribute="inventoryItem" field="statusId" prefix="[" suffix="]"/><%}%></option>
            <option value='<ofbiz:inputvalue entityAttr="inventoryItem" field="statusId"/>'>----</option>
            <ofbiz:iterator name="statusItem" property="statusItems">
              <option value='<ofbiz:inputvalue entityAttr="statusItem" field="statusId"/>'><ofbiz:inputvalue entityAttr="statusItem" field="description"/></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Date Received</div></td>
        <td>&nbsp;</td>
        <td><input type="text" size="22" <ofbiz:inputvalue entityAttr="inventoryItem" field="dateReceived" fullattrs="true"/> style='font-size: x-small;'></td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Expire Date</div></td>
        <td>&nbsp;</td>
        <td><input type="text" size="22" <ofbiz:inputvalue entityAttr="inventoryItem" field="expireDate" fullattrs="true"/> style='font-size: x-small;'></td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Facility/Container</div></td>
        <td>&nbsp;</td>
        <td>
            <span class='tabletext'>Select a Facility:</span>
            <select name="facilityId" style='font-size: x-small;'>
              <%if (inventoryItem == null && UtilValidate.isNotEmpty(request.getParameter("facilityId"))) {%>
                  <option value='<%=request.getParameter("facilityId")%>'>[<%=request.getParameter("facilityId")%>]</option>
              <%} else {%>
              <option value='<ofbiz:inputvalue entityAttr="inventoryItem" field="facilityId"/>'><ofbiz:inputvalue entityAttr="facility" field="facilityName"/> <ofbiz:entityfield attribute="inventoryItem" field="facilityId" prefix="[" suffix="]"/></option>
              <%}%>
              <option value='<ofbiz:inputvalue entityAttr="inventoryItem" field="facilityId"/>'>----</option>
              <ofbiz:iterator name="nextFacility" property="facilities">
                <option value='<ofbiz:inputvalue entityAttr="nextFacility" field="facilityId"/>'><ofbiz:inputvalue entityAttr="nextFacility" field="facilityName"/> <ofbiz:entityfield attribute="nextFacility" field="facilityId" prefix="[" suffix="]"/></option>
              </ofbiz:iterator>
            </select>
            <%if (inventoryItem != null && UtilValidate.isNotEmpty(inventoryItem.getString("facilityId"))) {%>
                <a href='<ofbiz:url>/EditFacility?facilityId=<ofbiz:inputvalue entityAttr="inventoryItem" field="facilityId"/></ofbiz:url>' class='buttontext'>[Edit&nbsp;Facility&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="facilityId"/>]</a>
            <%}%>
            <br>
            <span class='tabletext'>OR enter a Container ID:</span>
            <input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="containerId" fullattrs="true"/> size="20" maxlength="20" style='font-size: x-small;'>
         </td>
       </tr>
      <tr>
        <td align='right'><div class='tabletext'>Facility Location</div></td>
        <td>&nbsp;</td>
        <td>
          <ofbiz:if name="facilityLocation">
            <div class="tabletext">
              <b>Area:</b>&nbsp;<ofbiz:inputvalue entityAttr="facilityLocation" field="areaId"/>
              <b>Aisle:</b>&nbsp;<ofbiz:inputvalue entityAttr="facilityLocation" field="aisleId"/>
              <b>Section:</b>&nbsp;<ofbiz:inputvalue entityAttr="facilityLocation" field="sectionId"/>
              <b>Level:</b>&nbsp;<ofbiz:inputvalue entityAttr="facilityLocation" field="levelId"/>
              <b>Position:</b>&nbsp;<ofbiz:inputvalue entityAttr="facilityLocation" field="positionId"/>
            </div>
          </ofbiz:if>
          <ofbiz:if name="inventoryItem">          
            <input type="text" size="20" maxsize="20" <ofbiz:inputvalue entityAttr="inventoryItem" field="locationSeqId" fullattrs="true"/> style='font-size: x-small;'>
            &nbsp;<a href="<ofbiz:url>/FindFacilityLocations?facilityId=<%=facilityId%>&inventoryItemId=<%=inventoryItemId%></ofbiz:url>" class="buttontext">[Find Location]</a>
          </ofbiz:if>
          <ofbiz:unless name="inventoryItem">
            <input type="text" size="20" maxsize="20" name="locationSeqId" value="<%=locationSeqId%>" style='font-size: x-small;'>
          </ofbiz:unless>          
        </td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Lot Id</div></td>
        <td>&nbsp;</td>
        <td><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="lotId" fullattrs="true"/> size="20" maxlength="20" style='font-size: x-small;'></td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Uom Id</div></td>
        <td>&nbsp;</td>
        <td><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="uomId" fullattrs="true"/> size="20" maxlength="20" style='font-size: x-small;'></td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Bin Number</div></td>
        <td>&nbsp;</td>
        <td><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="binNumber" fullattrs="true"/> size="20" maxlength="20" style='font-size: x-small;'></td>
      </tr>
      <tr>
        <td align=right><div class="tabletext">Comments</div></td>
        <td>&nbsp;</td>
        <td><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="comments" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'></td>
      </tr>
    <%if (inventoryItem != null && "NON_SERIAL_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>
      <tr>
        <td align=right><div class="tabletext">Available To Promise / Quantity On Hand</div></td>
        <td>&nbsp;</td>
        <td>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="inventoryItem" field="availableToPromise" fullattrs="true"/> style='font-size: x-small;'>
            / <input type=text size='5' <ofbiz:inputvalue entityAttr="inventoryItem" field="quantityOnHand" fullattrs="true"/> style='font-size: x-small;'>
        </td>
      </tr>
    <%} else if (inventoryItem != null && "SERIALIZED_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>
      <tr>
        <td align=right><div class="tabletext">Serial Number</div></td>
        <td>&nbsp;</td>
        <td><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="serialNumber" fullattrs="true"/> size="30" maxlength="60" style='font-size: x-small;'></td>
      </tr>
    <%} else if (inventoryItem != null) {%>
      <tr>
        <td align=right><div class="tabletext">Serial#/ATP/QOH</div></td>
        <td>&nbsp;</td>
        <td><div class='tabletext' style='color: red;'>Error: type <ofbiz:entityfield attribute="inventoryItem" field="inventoryItemTypeId"/> unknown; specify a type.</div></td>
      </tr>
    <%}%>

  <tr>
    <td colspan='2'>&nbsp;</td>
    <td colspan='5'><input type="submit" name="Update" value="Update" style='font-size: x-small;'></td>
  </tr>
</table>
</form>

<%}else{%>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
<%}%>

