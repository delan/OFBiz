<%--
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
 *@author     David E. Jones
 *@created    Dec 19 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String prodCatalogId = request.getParameter("prodCatalogId");
    GenericValue prodCatalog = delegator.findByPrimaryKey("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
    Collection partyCatalogs = null;
    if (prodCatalog == null) {
        tryEntity = false;
    } else {
        partyCatalogs = prodCatalog.getRelated("PartyCatalog", null, UtilMisc.toList("sequenceNum", "partyId"));
        if (partyCatalogs != null) pageContext.setAttribute("partyCatalogs", partyCatalogs);
    }

    if ("true".equalsIgnoreCase((String)request.getParameter("tryEntity"))) tryEntity = true;
%>
<br>

<%if(prodCatalogId != null && prodCatalogId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProdCatalog?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Catalog</a>
  <a href="<ofbiz:url>/EditProdCatalogWebSites?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">WebSites</a>
  <a href="<ofbiz:url>/EditProdCatalogParties?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButtonSelected">Parties</a>
  <a href="<ofbiz:url>/EditProdCatalogCategories?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProdCatalogPromos?prodCatalogId=<%=prodCatalogId%></ofbiz:url>" class="tabButton">Promotions</a>
  </div>
<%}%>

<div class="head1">Parties <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(prodCatalog==null?null:prodCatalog.getString("catalogName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(prodCatalogId)%>]</span></div>

<a href="<ofbiz:url>/EditProdCatalog</ofbiz:url>" class="buttontext">[New ProdCatalog]</a>
<br>
<br>
<%if (prodCatalogId != null && prodCatalog != null) {%>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Party ID</b></div></td>
    <td><div class="tabletext"><b>Name</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="partyCatalog" property="partyCatalogs">
  <%PartyWorker.getPartyOtherValues(pageContext, partyCatalog.getString("partyId"), "party", "lookupPerson", "lookupGroup");%>
  <%-- GenericValue party = partyCatalog.getRelatedOne("Party"); --%>
  <tr valign="middle">
    <td><a href='/partymgr/control/viewprofile?partyId=<ofbiz:inputvalue entityAttr="partyCatalog" field="partyId"/>' class="buttontext" target="partymgr">[<ofbiz:inputvalue entityAttr="partyCatalog" field="partyId"/>]</a></td>
    <td>
        <%--<a href='<ofbiz:url>/EditParty?partyId=<ofbiz:inputvalue entityAttr="partyCatalog" field="partyId"/></ofbiz:url>' class="buttontext">--%>
        <div class='tabletext'>
            <ofbiz:if name="lookupPerson">
              <ofbiz:inputvalue entityAttr="lookupPerson" field="personalTitle"/>
              <ofbiz:inputvalue entityAttr="lookupPerson" field="firstName"/>
              <ofbiz:inputvalue entityAttr="lookupPerson" field="middleName"/>
              <ofbiz:inputvalue entityAttr="lookupPerson" field="lastName"/>
              <ofbiz:inputvalue entityAttr="lookupPerson" field="suffix"/>
            </ofbiz:if>
            <ofbiz:unless name="lookupPerson">
              <ofbiz:if name="lookupGroup">
                <ofbiz:inputvalue entityAttr="lookupGroup" field="groupName"/>
              </ofbiz:if>
              <ofbiz:unless name="lookupGroup">"Unnamed Party"</ofbiz:unless>
            </ofbiz:unless>
        </div>
        <%--</a>&nbsp;--%>
    </td>
    <%boolean hasntStarted = false;%>
    <%if (partyCatalog.getTimestamp("fromDate") != null && UtilDateTime.nowTimestamp().before(partyCatalog.getTimestamp("fromDate"))) { hasntStarted = true; }%>
    <td><div class='tabletext'<%if (hasntStarted) {%> style='color: red;'<%}%>><ofbiz:inputvalue entityAttr="partyCatalog" field="fromDate"/></div></td>
    <td align="center">
        <%boolean hasExpired = false;%>
        <%if (partyCatalog.getTimestamp("thruDate") != null && UtilDateTime.nowTimestamp().after(partyCatalog.getTimestamp("thruDate"))) { hasExpired = true; }%>
        <FORM method=POST action='<ofbiz:url>/updateProdCatalogToParty</ofbiz:url>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="partyCatalog" field="prodCatalogId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="partyCatalog" field="partyId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="partyCatalog" field="fromDate" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="partyCatalog" field="thruDate" fullattrs="true"/> style='font-size: x-small; <%if (hasExpired) {%>color: red;<%}%>'>
            <input type=text size='5' <ofbiz:inputvalue entityAttr="partyCatalog" field="sequenceNum" fullattrs="true"/> style='font-size: x-small;'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/removeProdCatalogFromParty?prodCatalogId=<ofbiz:entityfield attribute="partyCatalog" field="prodCatalogId"/>&partyId=<ofbiz:entityfield attribute="partyCatalog" field="partyId"/>&fromDate=<%=UtilFormatOut.encodeQueryValue(partyCatalog.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/addProdCatalogToParty</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="prodCatalogId" value="<%=prodCatalogId%>">
  <input type="hidden" name="tryEntity" value="true">

  <div class='head2'>Add Catalog for Party:</div>
  <br>
  <div class='tabletext'>
    Party&nbsp;ID:&nbsp;<input type=text size='15' name='partyId'>
    From&nbsp;Date:&nbsp;<input type=text size='22' name='fromDate'>
    <input type="submit" value="Add">
  </div>
</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
