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
<%@ page import="org.ofbiz.core.widgetimpl.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    String prodCatalogId = request.getParameter("prodCatalogId");
    GenericValue prodCatalog = delegator.findByPrimaryKey("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
    List prodCatalogRoles = null;
    List prodCatalogRoleDatas = new LinkedList();
    if (prodCatalog != null) {
        prodCatalogRoles = prodCatalog.getRelated("ProdCatalogRole", null, UtilMisc.toList("sequenceNum", "partyId"));
        Iterator prodCatalogRoleIter = prodCatalogRoles.iterator();
        while (prodCatalogRoleIter.hasNext()) {
        	GenericValue prodCatalogRole = (GenericValue) prodCatalogRoleIter.next();
        	Map prodCatalogRoleData = new HashMap();
        	prodCatalogRoleData.put("prodCatalogRole", prodCatalogRole);
        	prodCatalogRoleData.put("person", prodCatalogRole.getRelatedOne("Person"));
        	prodCatalogRoleData.put("partyGroup", prodCatalogRole.getRelatedOne("PartyGroup"));
        	prodCatalogRoleData.put("roleType", prodCatalogRole.getRelatedOneCache("RoleType"));
        	prodCatalogRoleDatas.add(prodCatalogRoleData);
        }
    }

    HtmlFormWrapper updateProdCatalogToPartyWrapper = new HtmlFormWrapper("/catalog/ProdCatalogForms.xml", "UpdateProdCatalogToParty", request, response);
    updateProdCatalogToPartyWrapper.putInContext("prodCatalogRoleDatas", prodCatalogRoleDatas);

    HtmlFormWrapper addProdCatalogToPartyWrapper = new HtmlFormWrapper("/catalog/ProdCatalogForms.xml", "AddProdCatalogToParty", request, response);
    addProdCatalogToPartyWrapper.putInContext("prodCatalog", prodCatalog);
%>

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
<%=updateProdCatalogToPartyWrapper.renderFormString()%>
<br>
<%=addProdCatalogToPartyWrapper.renderFormString()%>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
