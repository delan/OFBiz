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
 *@author     Britton LaRoche (britton_laroche@hotmail.com)
 *@author     Britton LaRoche
 *@created    January 12, 2003
 *@version    1.0
--%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*, org.ofbiz.commonapp.party.contact.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>

<%
    Collection orderReport = null;
    // search by status info
    String groupName = request.getParameter("groupName");
    // search by date info
    String fromDate = request.getParameter("fromDate");
    String toDate = request.getParameter("toDate");

    String lookupErrorMessage = null;

    String pageParamString = "";
    if (groupName != null) {
        if (fromDate != null || toDate != null) {
        // lookup by date
        if (fromDate.length() != 0 || toDate.length() != 0) {
            List exprs = new ArrayList();
            try {
                if (fromDate != null && fromDate.length() > 8) {
                    fromDate = fromDate.trim();
                    if (fromDate.length() < 14)
                        fromDate = fromDate + " " + "00:00:00.000";
                    exprs.add(new EntityExpr("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)));
                }
                if (toDate != null && toDate.length() > 8) {
                    toDate = toDate.trim();
                    if (toDate.length() < 14)
                        toDate = toDate + " " + "23:59:59.999";
                    exprs.add(new EntityExpr("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(toDate, "Timestamp", null, null)));
                }
                orderHeaderList = delegator.findByAnd("OrderReportView", exprs, UtilMisc.toList("-orderDate"));
            } catch (Exception e) { Debug.logError(e); }
        } else {
            lookupErrorMessage = "Both 'fromDate' and 'toDate' cannot be empty.";
        }
    }
    
    Iterator orderList = UtilMisc.toIterator(orderHeader.getRelated("OrderShipmentPreference"));
    if(orderShipmentPreferences != null && orderShipmentPreferences.hasNext()) {
        shipmentPreference = (GenericValue)orderShipmentPreferences.next();
        carrierPartyId = shipmentPreference.getString("carrierPartyId");
        shipmentMethodTypeId = shipmentPreference.getString("shipmentMethodTypeId");
        shippingInstructions = shipmentPreference.getString("shippingInstructions");
        trackingNumber = shipmentPreference.getString("trackingNumber");
        maySplit = shipmentPreference.getBoolean("maySplit");
        giftMessage = shipmentPreference.getString("giftMessage");
        isGift = shipmentPreference.getBoolean("isGift");
    }

<%}else{%>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
<%}%>