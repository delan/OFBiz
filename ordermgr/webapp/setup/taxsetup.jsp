<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<% pageContext.setAttribute("PageName", "Main Page"); %>

<div class='tabContainer'>
  <%if(security.hasEntityPermission("SHIPRATE", "_VIEW", session)) {%>
  <a href="<ofbiz:url>/shipsetup</ofbiz:url>" class='tabButton'>Ship&nbsp;Rate&nbsp;Setup</a>
  <%}%>
  <%if(security.hasEntityPermission("TAXRATE", "_VIEW", session)) {%>
  <a href="<ofbiz:url>/taxsetup</ofbiz:url>" class='tabButtonSelected'>Tax&nbsp;Rate&nbsp;Setup</a>
  <%}%>
  <%if(security.hasEntityPermission("PAYPROC", "_VIEW", session)) {%>
  <a href="<ofbiz:url>/paysetup</ofbiz:url>" class='tabButton'>Payment&nbsp;Setup</a>
  <%}%>
</div>

<%if(security.hasEntityPermission("TAXRATE", "_VIEW", session)) {%>

<TABLE border=0 width='100%' cellpadding='0' cellspacing=0 class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxtop'>
        <tr>
          <TD align=left width='90%' >
            <div class='boxhead'>&nbsp;Tax Rate Editor</div>
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
          <!-- Inside the box -->
<%
    List taxItems = delegator.findAll("SimpleSalesTaxLookup", UtilMisc.toList("stateProvinceGeoId", "taxCategory", "-fromDate"));
    pageContext.setAttribute("taxItems", taxItems);
    String viewStr = new String();
%>

    <p>
    <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="viewOneTR1">
      <td nowrap><div class="tableheadtext">Country GeoId</td>
      <td nowrap><div class="tableheadtext">State GeoId</div></td>
      <td nowrap><div class="tableheadtext">Tax Category</div></td>
      <td nowrap><div class="tableheadtext">Min Purchase</div></td>
      <td nowrap><div class="tableheadtext">Tax Shipping</div></td>
      <td nowrap><div class="tableheadtext">Description</div></td>
      <td nowrap><div class="tableheadtext">Tax Rate</div></td>
      <td nowrap><div class="tableheadtext">From-Date</div></td>
      <!--<td nowrap><div class="tablehead">Thru-Date</div></td>-->
      <td nowrap><div class="tabletext">&nbsp;</div></td>
    </tr>

    <ofbiz:iterator name="taxItem" property="taxItems">
    <tr class="<%= viewStr = viewStr == "viewManyTR1" ? "viewManyTR2" : "viewManyTR1" %>">
      <td><div class="tabletext"><ofbiz:entityfield attribute="taxItem" field="countryGeoId"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="taxItem" field="stateProvinceGeoId"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="taxItem" field="taxCategory"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="taxItem" field="minPurchase"/></div></td></div></td>      
      <td><div class="tabletext"><ofbiz:entityfield attribute="taxItem" field="taxShipping"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="taxItem" field="description"/></div></td></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="taxItem" field="salesTaxPercentage"/></div></td>
      <td><div class="tabletext"><ofbiz:entityfield attribute="taxItem" field="fromDate"/></div></td>
      <!--<td><div class="tabletext">&nbsp;</div></td>-->
      <%if(security.hasEntityPermission("TAXRATE", "_DELETE", session)) {%>
      <td><div class="tabletext"><a href="<ofbiz:url>/removetaxrate?countryGeoId=<ofbiz:entityfield attribute="taxItem" field="countryGeoId"/>&stateProvinceGeoId=<ofbiz:entityfield attribute="taxItem" field="stateProvinceGeoId"/>&taxCategory=<%=taxItem.getString("taxCategory")%>&minPurchase=<%=taxItem.getString("minPurchase")%>&fromDate=<%=taxItem.getString("fromDate")%></ofbiz:url>" class="buttontext">[Remove]</a></div></td>
      <%} else {%>
      <td>&nbsp;</td>
      <%}%>
    </tr>
    </ofbiz:iterator>

    <%
        List stateGeos = delegator.findByAndCache("Geo", UtilMisc.toMap("geoTypeId", "STATE"), UtilMisc.toList("geoId"));
        pageContext.setAttribute("stateGeos", stateGeos);
    %>

    <%if(security.hasEntityPermission("TAXRATE", "_CREATE", session)) {%>
    <form name="addrate" action="<ofbiz:url>/createtaxrate</ofbiz:url>">
    <tr bgcolor="#CCCCCC">
      <td>
        <select name="countryGeoId" class="selectBox">
        <option value="_NA_">All</option>
        <%@ include file="/includes/countries.jsp" %>
        </select>
      </td>          	
      <td>
        <select name="stateProvinceGeoId" class="selectBox">
        <option value="_NA_">All</option>
        <%@ include file="/includes/states.jsp" %>
        </select>
      </td>      
      <td><input type="text" size="20" name="taxCategory" class="inputBox"></td>      
      <td><input type="text" size="10" name="minPurchase" class="inputBox" value="0.00"></td>      
      <td>
        <select name="taxShipping" class="selectBox">
          <option value="N">No</option>
          <option value="Y">Yes</option>
        </select>
      </td>
      <td><input type="text" size="20" name="description" class="inputBox"></td>
      <td><input type="text" size="10" name="salesTaxPercentage" class="inputBox"></td>
      <td><input type="text" name="fromDate" class="inputBox"></td>
      <!--<td><input type="text" name="thruDate" class="inputBox"></td>-->
      <td><div class="tabletext"><a href="javascript:document.addrate.submit();" class="buttontext">[Add]</a></div></td>
    </tr>
    </form>
    <%}%>

    </table>
    </p>

          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<%}else{%>
  <br>
  <h3>You do not have permission to view this page. ("TAXRATE_VIEW" or "TAXRATE_ADMIN" needed)</h3>
<%}%>

