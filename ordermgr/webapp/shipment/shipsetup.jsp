<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<% pageContext.setAttribute("PageName", "Main Page"); %>

<%@ include file="shipsetuphelper.jsp" %>

<%if(security.hasEntityPermission("SHIPRATE", "_VIEW", session)) {%>

<BR>
<TABLE border=0 width='100%' cellpadding='0' cellspacing=0 class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxtop'>
        <tr>
          <TD align=left width='90%' >
            <div class='boxhead'>&nbsp;Shipping Rate Editor</div>
          </TD>
          <TD align=right width='10%'>&nbsp;</TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
        <tr>
          <td>
          <br>
          <!-- Inside the box -->
<%
    ArrayList orderByList = new ArrayList();
    orderByList.add("GeoIdFrom");
    orderByList.add("shipmentMethodTypeId");
    orderByList.add("GeoIdTo");

    Collection estimates = delegator.findAll("ShipmentCostEstimate",orderByList);
    Iterator estit = estimates.iterator();
    String viewStr = new String();
%>

    <p>
      <b>Shipping Rate Chart:</b>
    <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="viewOneTR1">
      <td nowrap><div class="tabletext"><b>Method</b></div></td>
      <td nowrap><div class="tabletext"><b>From</b></div></td>
      <td nowrap><div class="tabletext"><b>To</b></div></td>
      <td nowrap><div class="tabletext"><b>Party</b></div></td>
      <td nowrap><div class="tabletext"><b>Role</b></div></td>
      <td nowrap><div class="tabletext"><b>Min-Max (w)</b></div></td>
      <td nowrap><div class="tabletext"><b>WeightAmt</b></div></td>
      <td nowrap><div class="tabletext"><b>Min-Max (q)</b></div></td>
      <td nowrap><div class="tabletext"><b>QtyAmt</b></div></td>
      <td nowrap><div class="tabletext"><b>Min-Max (p)</b></div></td>
      <td nowrap><div class="tabletext"><b>PriceAmt</b></div></td>
      <td nowrap><div class="tabletext"><b>Base%</b></div></td>
      <td nowrap><div class="tabletext"><b>BasePrc</b></div></td>
      <td nowrap><div class="tabletext"><b>ItemPrc</b></div></td>
      <td nowrap><div class="tabletext"><b>&nbsp;</b></div></td>
    </tr>
<%
    while ( estit.hasNext() ) {
        GenericValue est = (GenericValue) estit.next();
        pageContext.setAttribute("est",est);
        GenericValue wv = est.getRelatedOne("WeightQuantityBreak");
        if ( wv != null )
            pageContext.setAttribute("wv",wv);
        GenericValue qv  = est.getRelatedOne("QuantityQuantityBreak");
        if ( qv != null )
            pageContext.setAttribute("qv",qv);
        GenericValue pv  = est.getRelatedOne("PriceQuantityBreak");
        if ( pv != null )
            pageContext.setAttribute("pv",pv);
%>
    <tr class="<%= viewStr = viewStr == "viewManyTR1" ? "viewManyTR2" : "viewManyTR1" %>">
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="shipmentMethodTypeId"/>&nbsp;(<ofbiz:entityfield attribute="est" field="carrierPartyId"/>)</div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="geoIdFrom"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="geoIdTo"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="partyId"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="roleTypeId"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="wv" field="fromQuantity"/>-<ofbiz:entityfield attribute="wv" field="thruQuantity"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="weightUnitPrice"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="qv" field="fromQuantity"/>-<ofbiz:entityfield attribute="qv" field="thruQuantity"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="quantityUnitPrice"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="pv" field="fromQuantity"/>-<ofbiz:entityfield attribute="pv" field="thruQuantity"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="priceUnitPrice"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="orderPricePercent"/>%</div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="orderFlatPrice"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="est" field="orderItemFlatPrice"/></div></td>
      <%if(security.hasEntityPermission("SHIPRATE", "_DELETE", session)) {%>
      <td><div class="tabletext"><a href="<ofbiz:url>/removeshipestimate?shipmentCostEstimateId=<ofbiz:entityfield attribute="est" field="shipmentCostEstimateId"/></ofbiz:url>" class="buttontext">[Remove]</a></div></td>
      <%} else {%>
      <td>&nbsp;</td>
      <%}%>
    </tr>
<%
       wv = null;
       qv = null;
       pv = null;
       pageContext.removeAttribute("est");
       pageContext.removeAttribute("wv");
       pageContext.removeAttribute("qv");
       pageContext.removeAttribute("pv");
%>
<%  }     %>
    </table>
    </p>

    <%if(security.hasEntityPermission("SHIPRATE", "_CREATE", session)) {%>
    <br>
    <form name="addform" method="post" action="<ofbiz:url>/createshipestimate</ofbiz:url>">
    <p>
      <b>Add New Record:</b>
      <br>
      <span class="info">
        Base Info; Specify GeoID / Party ID; No GeoID means anywhere NOT already defined.
      </span>
      <TABLE width="100%" border="0" class="edittable">
      <tr>
        <td width="100%">
          <table width="100%" cellpadding="2" cellspacing="2" border="0">
            <tr class="viewOneTR1">
              <td nowrap><div class="tabletext"><b>Ship Method</b><%--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" class="lightbuttontext">[Add]</a>--%></div></td>
              <td nowrap><div class="tabletext"><b>FromGeo</b></div></td>
              <td nowrap><div class="tabletext"><b>ToGeo</b><%--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" class="lightbuttontext">[Add]</a>--%></div></td>
              <td nowrap><div class="tabletext"><b>PartyID</b></div></td>
              <td nowrap><div class="tabletext"><b>RoleTypeID</b></div></td>
            </tr>
            <tr class="viewManyTR1">
              <td><div class="tabletext"><select name="shipMethod"><%= getShipMethodList(delegator) %></select></div></td>
              <td><div class="tabletext"><select name="fromGeo"><option value="">None</option><%= getGeoList(delegator) %></select></div></td>
              <td><div class="tabletext"><select name="toGeo"><option value="">None</option><%= getGeoList(delegator) %></select></div></td>
              <td><div class="tabletext"><input type="text" name="party" size="6"></div></td>
              <td><div class="tabletext"><input type="text" name="role" size="6"></div></td>
            </tr>
          </table>
        </td>
      </tr>
      </TABLE>
      <br>

      <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>

      <span class="info">
        Flat Rate Info; Will be added to the shipping calculation.
      </span>
      <TABLE width="100%" border="0" class="edittable">
      <tr>
        <td width="100%">
          <table width="100%" cellpadding="2" cellspacing="2" border="0">
            <tr class="viewOneTR1">
              <td nowrap><div class="tabletext"><b>FlatBasePercent</b></div></td>
              <td nowrap><div class="tabletext"><b>FlatBasePrice</b></div></td>
              <td nowrap><div class="tabletext"><b>FlatItemPrice</b></div></td>
            </tr>
            <tr class="viewManyTR2">
              <td><div class="tabletext"><input type="text" name="flatPercent" value="0" size="5">&nbsp;%</div></td>
              <td><div class="tabletext"><input type="text" name="flatPrice" value="0.00" size="5"></div></td>
              <td><div class="tabletext"><input type="text" name="flatItemPrice" value="0.00" size="5"></div></td>
            </tr>
          </table>
        </td>
      </tr>
      </TABLE>
      <br>
      <span class="info">
        Unit Span Info; Units must fall in between the span to qualify. An empty span will effect only units which do not have a matching span.
      </span>
      <TABLE width="100%" border="0" class="edittable">
      <tr>
        <td width="100%">
          <table width="100%" cellpadding="2" cellspacing="2" border="0">
            <tr class="viewOneTR1">
              <td nowrap><div class="tabletext"><b>Min - Max (Weight)</b></div></td>
              <td nowrap><div class="tabletext"><b>WeightUOM</b></div></td>
              <td nowrap><div class="tabletext"><b>UnitWeightAmt</b></div></td>
            </tr>
            <tr class="viewManyTR1">
              <td><div class="tabletext"><input type="text" name="wmin" size="4"> - <input type="text" name="wmax" size="4"></div></td>
              <td><div class="tabletext"><select name="wuom"><= getUomList(delegator, "WEIGHT_MEASURE") %></select></div></td>
              <td><div class="tabletext"><input type="text" name="wprice" size="5"></div></td>
            </tr>
            <tr class="viewOneTR1">
              <td nowrap><div class="tabletext"><b>Min - Max (Qty)</b></div></td>
              <td nowrap><div class="tabletext"><b>QtyUOM</b></div></td>
              <td nowrap><div class="tabletext"><b>UnitQtyAmt</b></div></td>
            </tr>
            <tr class="viewManyTR2">
              <td><div class="tabletext"><input type="text" name="qmin" size="4"> - <input type="text" name="qmax" size="4"></div></td>
              <td><div class="tabletext"><select name="quom"><= getUomList(delegator, "QUANTITY_MEASURE") %></select></div></td>
              <td><div class="tabletext"><input type="text" name="qprice" size="5"></div></td>
            </tr>
            <tr class="viewOneTR1">
              <td nowrap><div class="tabletext"><b>Min - Max (Price)</b></div></td>
              <td nowrap><div class="tabletext"><b>&nbsp;</b></div></td>
              <td nowrap><div class="tabletext"><b>UnitPriceAmt</b></div></td>
            </tr>
            <tr class="viewManyTR1">
              <td><div class="tabletext"><input type="text" name="pmin" size="4"> - <input type="text" name="pmax" size="4"></div></td>
              <td><div class="tabletext">&nbsp;</div></td>
              <td><div class="tabletext"><input type="text" name="pprice" size="5"></div></td>
            </tr>
          </table>
        </td>
      </tr>
      </TABLE>
      <br>
      <a href="javascript:document.addform.submit();" class="buttontext">[Add/Save]</a>
    </p>
    </form>
    <br>
    <%}%>

          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<%}else{%>
  <br>
  <h3>You do not have permission to view this page. ("SHIPRATE_VIEW" or "SHIPRATE_ADMIN" needed)</h3>
<%}%>

