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

<table cellpadding=0 cellspacing=0 border=0 width="100%"><tr><td>&nbsp;&nbsp;</td><td>

<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String inventoryItemId = request.getParameter("inventoryItemId");
    if (UtilValidate.isEmpty(inventoryItemId) && UtilValidate.isNotEmpty((String) request.getAttribute("inventoryItemId"))) {
        inventoryItemId = (String) request.getAttribute("inventoryItemId");
    }
    GenericValue inventoryItem = delegator.findByPrimaryKey("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId));
    GenericValue inventoryItemType = null;
    GenericValue facility = null;
    if(inventoryItem == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("inventoryItem", inventoryItem);

        inventoryItemType = inventoryItem.getRelatedOne("InventoryItemType");
        if (inventoryItemType != null) pageContext.setAttribute("inventoryItemType", inventoryItemType);

        facility = inventoryItem.getRelatedOne("Facility");
        if (facility != null) pageContext.setAttribute("facility", facility);

        //statuses
        if ("NON_SERIAL_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {
            //do nothing for non-serialized inventory
        } else if ("SERIALIZED_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {
            if (UtilValidate.isNotEmpty(inventoryItem.getString("statusId"))) {
                Collection statusChange = delegator.findByAnd("StatusValidChange",UtilMisc.toMap("statusId",inventoryItem.getString("statusId")));
                if (statusChange != null) {
                    Collection statusItems = null;
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
<a href="<ofbiz:url>/EditInventoryItem</ofbiz:url>" class="buttontext">[New InventoryItem]</a>
<%if(inventoryItemId != null && inventoryItemId.length() > 0){%>
  <a href="<ofbiz:url>/EditInventoryItem?inventoryItemId=<%=inventoryItemId%></ofbiz:url>" class="buttontextdisabled">[InventoryItem]</a>
<%}%>

<div class="head1">Edit InventoryItem with ID "<%=UtilFormatOut.checkNull(inventoryItemId)%>"</div>


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
        <td width="26%" align=right><div class="tabletext">InventoryItem Type Id</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <%-- <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(tryEntity?inventoryItem.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"> --%>
          <select name="inventoryItemTypeId" size=1>
            <option selected value='<ofbiz:inputvalue entityAttr="inventoryItemType" field="inventoryItemTypeId"/>'><ofbiz:inputvalue entityAttr="inventoryItemType" field="description"/> <%--<ofbiz:entityfield attribute="inventoryItemType" field="inventoryItemTypeId" prefix="[" suffix="]"/>--%></option>
            <option value='<ofbiz:inputvalue entityAttr="inventoryItemType" field="inventoryItemTypeId"/>'>----</option>
            <ofbiz:iterator name="nextInventoryItemType" property="inventoryItemTypes">
              <option value='<ofbiz:inputvalue entityAttr="nextInventoryItemType" field="inventoryItemTypeId"/>'><ofbiz:inputvalue entityAttr="nextInventoryItemType" field="description"/> <%--<ofbiz:entityfield attribute="nextInventoryItemType" field="inventoryItemTypeId" prefix="[" suffix="]"/>--%></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Product Id</div></td>
        <td>&nbsp;</td>
        <td width="74%">
            <input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="productId" fullattrs="true" tryEntityAttr="tryEntity"/> size="20" maxlength="20">
            <%if (inventoryItem != null && UtilValidate.isNotEmpty(inventoryItem.getString("productId"))) {%>
                <a href='<ofbiz:url>/EditProduct?productId=<ofbiz:inputvalue entityAttr="inventoryItem" field="productId"/></ofbiz:url>' class='buttontext'>[Edit&nbsp;Product&nbsp;<ofbiz:inputvalue entityAttr="inventoryItem" field="productId"/>]</a>
            <%}%>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Party Id</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="partyId" fullattrs="true" tryEntityAttr="tryEntity"/> size="20" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Status</div></td>
        <td>&nbsp;</td>
        <td width="74%">
           <select name="statusId">
             <option value='<ofbiz:inputvalue entityAttr="inventoryItem" field="statusId"/>'><ofbiz:inputvalue entityAttr="inventoryItem" field="statusId"/></option>
             <option value='<ofbiz:inputvalue entityAttr="inventoryItem" field="statusId"/>'>----</option>
             <ofbiz:iterator name="statusItem" property="statusItems">
               <option value='<ofbiz:inputvalue entityAttr="statusItem" field="statusId"/>'><ofbiz:inputvalue entityAttr="statusItem" field="description"/></option>
             </ofbiz:iterator>
           </select>
         </td>
       </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Date Received</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" size="22" <ofbiz:inputvalue entityAttr="inventoryItem" field="dateReceived" fullattrs="true"/>></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Expire Date</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" size="22" <ofbiz:inputvalue entityAttr="inventoryItem" field="expireDate" fullattrs="true"/>></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Facility/Container</div></td>
        <td>&nbsp;</td>
        <td width="74%">
            Select a Facility:
            <select name="facilityId">
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
            OR enter a Container ID:
            <input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="containerId" fullattrs="true"/> size="20" maxlength="20">
         </td>
       </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Lot Id</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="lotId" fullattrs="true"/> size="20" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Uom Id</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="uomId" fullattrs="true"/> size="20" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Bin Number</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="binNumber" fullattrs="true"/> size="20" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Comments</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="comments" fullattrs="true"/> size="60" maxlength="250"></td>
      </tr>
    <%if (inventoryItem != null && "NON_SERIAL_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Available To Promise / Quantity On Hand</div></td>
        <td>&nbsp;</td>
        <td width="74%">
            <input type=text size='5' <ofbiz:inputvalue entityAttr="inventoryItem" field="availableToPromise" fullattrs="true"/>>
            / <input type=text size='5' <ofbiz:inputvalue entityAttr="inventoryItem" field="quantityOnHand" fullattrs="true"/>>
        </td>
      </tr>
    <%} else if (inventoryItem != null && "SERIALIZED_INV_ITEM".equals(inventoryItem.getString("inventoryItemTypeId"))) {%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Serial Number</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="inventoryItem" field="serialNumber" fullattrs="true"/> size="30" maxlength="60"></td>
      </tr>
    <%} else if (inventoryItem != null) {%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Serial#/ATP/QOH</div></td>
        <td>&nbsp;</td>
        <td width="74%"><div class='tabletext' style='color: red;'>Error: type <ofbiz:entityfield attribute="inventoryItem" field="inventoryItemTypeId"/> unknown; specify a type.</div></td>
      </tr>
    <%}%>

  <tr>
    <td colspan='1' align=right><input type="submit" name="Update" value="Update"></td>
    <td colspan='2'>&nbsp;</td>
  </tr>
</table>
</form>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
</td><td>&nbsp;&nbsp;</td></tr></table>
