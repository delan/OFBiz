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
 *@created    May 13 2002
 *@version    1.0
--%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String prodCatalogId = request.getParameter("prodCatalogId");
    GenericValue prodCatalog = delegator.findByPrimaryKey("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
    GenericValue facility = null;
    GenericValue reserveOrderEnum = null;
    if(prodCatalog == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("prodCatalog", prodCatalog);

        facility = prodCatalog.getRelatedOne("Facility");
        if (facility != null) pageContext.setAttribute("facility", facility);

        reserveOrderEnum = prodCatalog.getRelatedOne("ReserveOrderEnumeration");
        if (reserveOrderEnum != null) pageContext.setAttribute("reserveOrderEnum", reserveOrderEnum);
    }

    //facilities
    Collection facilities = delegator.findAll("Facility");
    if (facilities != null) pageContext.setAttribute("facilities", facilities);

    Collection reserveOrderEnums = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "INV_RES_ORDER"));
    if (reserveOrderEnums != null) pageContext.setAttribute("reserveOrderEnums", reserveOrderEnums);
%>

<br>
<%if(prodCatalogId != null && prodCatalogId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProdCatalog?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButtonSelected">Catalog</a>
  <a href="<ofbiz:url>/EditProdCatalogWebSites?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">WebSites</a>
  <a href="<ofbiz:url>/EditProdCatalogCategories?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProdCatalogPromos?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Promotions</a>
  </div>
<%}%>
<div class="head1">Catalog <span class='head2'><%=UtilFormatOut.ifNotEmpty(prodCatalog==null?null:prodCatalog.getString("catalogName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(prodCatalogId)%>]</span></div>
<a href="<ofbiz:url>/EditProdCatalog</ofbiz:url>" class="buttontext">[New ProdCatalog]</a>
<%if (prodCatalog == null) {%>
  <%if (prodCatalogId != null) {%>
    <form action="<ofbiz:url>/CreateProdCatalog</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">ProdCatalog ID</div></td>
      <td>&nbsp;</td>
      <td>
        <h3>Could not find prodCatalog with ID "<%=prodCatalogId%>".</h3><br>
        <input type=text size='20' maxlength='20' name="prodCatalogId" value="<%=UtilFormatOut.checkNull(prodCatalogId)%>">
      </td>
    </tr>
  <%} else {%>
    <form action="<ofbiz:url>/CreateProdCatalog</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">ProdCatalog ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type=text size='20' maxlength='20' name="prodCatalogId" value="<%=UtilFormatOut.checkNull(prodCatalogId)%>">
      </td>
    </tr>
  <%}%>
<%} else {%>
  <form action="<ofbiz:url>/UpdateProdCatalog</ofbiz:url>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="prodCatalogId" value="<%=prodCatalogId%>">
  <tr>
    <td align=right><div class="tabletext">ProdCatalog ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=prodCatalogId%></b> (This cannot be changed without re-creating the prodCatalog.)
    </td>
  </tr>
<%}%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Name</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="prodCatalog" field="catalogName" fullattrs="true"/> size="30" maxlength="60" style='font-size: x-small;'></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Title</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="prodCatalog" field="title" fullattrs="true"/> size="30" maxlength="60" style='font-size: x-small;'></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">SubTitle</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="prodCatalog" field="subtitle" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Inventory Facility</div></td>
        <td>&nbsp;</td>
        <td width="74%">
            <select name="inventoryFacilityId" style='font-size: x-small;'>
              <option value='<ofbiz:inputvalue entityAttr="prodCatalog" field="inventoryFacilityId"/>'><ofbiz:inputvalue entityAttr="facility" field="facilityName"/> <ofbiz:entityfield attribute="prodCatalog" field="inventoryFacilityId" prefix="[" suffix="]"/></option>
              <option value='<ofbiz:inputvalue entityAttr="prodCatalog" field="inventoryFacilityId"/>'>----</option>
              <ofbiz:iterator name="nextFacility" property="facilities">
                <option value='<ofbiz:inputvalue entityAttr="nextFacility" field="facilityId"/>'><ofbiz:inputvalue entityAttr="nextFacility" field="facilityName"/> [<ofbiz:inputvalue entityAttr="nextFacility" field="facilityId"/>]</option>
              </ofbiz:iterator>
            </select>
            <%if (prodCatalog != null && UtilValidate.isNotEmpty(prodCatalog.getString("inventoryFacilityId"))) {%>
                <a href='<ofbiz:url>/EditFacility?facilityId=<ofbiz:inputvalue entityAttr="prodCatalog" field="inventoryFacilityId"/></ofbiz:url>' class='buttontext'>[Edit&nbsp;Facility&nbsp;<ofbiz:inputvalue entityAttr="prodCatalog" field="inventoryFacilityId"/>]</a>
            <%}%>
         </td>
       </tr>

      <tr>
        <td width="26%" align=right><div class="tabletext">One Inventory Facility?</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <SELECT name='oneInventoryFacility' style='font-size: x-small;'>
            <OPTION><ofbiz:inputvalue entityAttr='prodCatalog' field='oneInventoryFacility' default="Y"/></OPTION>
            <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
          </SELECT>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Check Inventory?</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <SELECT name='checkInventory' style='font-size: x-small;'>
            <OPTION><ofbiz:inputvalue entityAttr='prodCatalog' field='checkInventory' default="Y"/></OPTION>
            <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
          </SELECT>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Reserve Inventory?</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <SELECT name='reserveInventory' style='font-size: x-small;'>
            <OPTION><ofbiz:inputvalue entityAttr='prodCatalog' field='reserveInventory' default="Y"/></OPTION>
            <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
          </SELECT>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Reserve Order:</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <select name='reserveOrderEnumId' style='font-size: x-small;'>
              <option value='<ofbiz:inputvalue entityAttr="prodCatalog" field="reserveOrderEnumId"/>'><ofbiz:inputvalue entityAttr="reserveOrderEnum" field="description"/> <%--<ofbiz:entityfield attribute="prodCatalog" field="reserveOrderEnumId" prefix="[" suffix="]"/>--%></option>
              <option value='<ofbiz:inputvalue entityAttr="prodCatalog" field="reserveOrderEnumId"/>'>----</option>
              <ofbiz:iterator name="newReserveOrderEnum" property="reserveOrderEnums">
                <option value='<ofbiz:inputvalue entityAttr="newReserveOrderEnum" field="enumId"/>'><ofbiz:inputvalue entityAttr="newReserveOrderEnum" field="description"/> <%--[<ofbiz:inputvalue entityAttr="newReserveOrderEnum" field="enumId"/>]--%></option>
              </ofbiz:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Require Inventory for Purchase?</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <SELECT name='requireInventory' style='font-size: x-small;'>
            <OPTION><ofbiz:inputvalue entityAttr='prodCatalog' field='requireInventory' default="N"/></OPTION>
            <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
          </SELECT>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Use Quick Add?</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <SELECT name='useQuickAdd' style='font-size: x-small;'>
            <OPTION><ofbiz:inputvalue entityAttr='prodCatalog' field='useQuickAdd' default="N"/></OPTION>
            <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
          </SELECT>
        </td>
      </tr>

      <tr>
        <td width="26%" align=right><div class="tabletext">Content Path Prefix</div></td>
        <td>&nbsp;</td>
        <td width="74%" colspan='4'>
            <input type="text" <ofbiz:inputvalue entityAttr='prodCatalog' field='contentPathPrefix' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'>
            <br><span class='tabletext'>If specified will be prepended to image and other content paths. Should start with a slash but not end with one.</span>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Template Path Prefix</div></td>
        <td>&nbsp;</td>
        <td width="74%" colspan='4'>
            <input type="text" <ofbiz:inputvalue entityAttr='prodCatalog' field='templatePathPrefix' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'>
            <br><span class='tabletext'>If specified will be prepended to template paths. Should start with a slash but not end with one.</span>
        </td>
      </tr>

  <tr>
    <td colspan='2'>&nbsp;</td>
    <td colspan='1' align=left><input type="submit" name="Update" value="Update" style='font-size: x-small;'></td>
  </tr>
</table>
</form>

<%} else {%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
