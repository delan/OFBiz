
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.entity.model.*" %>

<%!
public static String getShipMethodList(GenericDelegator delegator) {
    try {
        Collection c = delegator.findAll("CarrierShipmentMethod", UtilMisc.toList("shipmentMethodTypeId"));
        Iterator i = c.iterator();
        StringBuffer buf = new StringBuffer();
        while ( i.hasNext() ) {
            GenericValue value = (GenericValue) i.next();
            buf.append("<option value=\"");
            buf.append(value.getString("partyId") + "|" + value.getString("shipmentMethodTypeId"));
            buf.append("\">");
            buf.append(value.getString("shipmentMethodTypeId") + " (" + value.getString("partyId") + ")");
            buf.append("</option>");
        }
        return buf.toString();
    }
    catch ( GenericEntityException e ) { }
    return "";
}

public static String getUOMList(GenericDelegator delegator, String uomTypeId) {
    try {
        List exprs = null;
        if (uomTypeId.equals("QUANTITY_MEASURE"))
            exprs = UtilMisc.toList(new EntityExpr("uomTypeId", EntityOperator.NOT_EQUAL, "CURRENCY_MESAURE"));
        else
            exprs = UtilMisc.toList(new EntityExpr("uomTypeId", EntityOperator.EQUALS, uomTypeId));
        Collection c = delegator.findByAnd("Uom", exprs, UtilMisc.toList("abbreviation"));
        Iterator i = c.iterator();
        StringBuffer buf = new StringBuffer();
        while ( i.hasNext() ) {
            GenericValue value = (GenericValue) i.next();
            buf.append("<option value=\"");
            buf.append(value.getString("uomId"));
            buf.append("\">");
            buf.append(value.getString("abbreviation"));
            buf.append("</option>");
        }
        return buf.toString();
    }
    catch ( GenericEntityException e ) { }
    return "";
}

public static String getGeoList(GenericDelegator delegator) {
    try {
        Collection c = delegator.findAll("Geo", UtilMisc.toList("geoId"));
        Iterator i = c.iterator();
        StringBuffer buf = new StringBuffer();
        while ( i.hasNext() ) {
            GenericValue value = (GenericValue) i.next();
            buf.append("<option value=\"");
            buf.append(value.getString("geoId"));
            buf.append("\">");
            buf.append(value.getString("geoId"));
            buf.append("</option>");
        }
        return buf.toString();
    }
    catch ( GenericEntityException e ) { }
    return "";
}

%>