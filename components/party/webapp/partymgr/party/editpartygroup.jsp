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
 *@version    $Rev:$
 *@since      2.0
--%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.entity.*, org.ofbiz.entity.util.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>

<%
        String create = request.getParameter("create_new");
        String partyId = null;
        if (create == null) {
            partyId = request.getParameter("party_id");
            if (partyId == null) partyId = request.getParameter("partyId");
            if (partyId == null) partyId = (String) request.getAttribute("partyId");
        }

        if (partyId != null) {
            GenericValue party = delegator.findByPrimaryKeyCache("Party", UtilMisc.toMap("partyId", partyId));
            GenericValue lookupGroup = party.getRelatedOneCache("PartyGroup");
            if (lookupGroup != null) pageContext.setAttribute("lookupGroup", lookupGroup);
            GenericValue lookupPartyType = party.getRelatedOneCache("PartyType");
            if (lookupPartyType != null) pageContext.setAttribute("lookupPartyType", lookupPartyType);

            boolean tryEntity = true;
            if(request.getAttribute("_ERROR_MESSAGE_") != null) tryEntity = false;
            if(lookupGroup == null)
               tryEntity = false;
            pageContext.setAttribute("tryEntity", new Boolean(tryEntity));
        }

        String donePage = request.getParameter("DONE_PAGE");    
        if(donePage == null || donePage.length() <= 0) donePage="viewprofile";
        if (partyId != null) donePage = donePage + "?partyId=" + partyId;
        pageContext.setAttribute("donePage", donePage);
%>

<ofbiz:unless name="lookupGroup">
  <p class="head1">Add New Group Information</p>
    <FORM method=POST action='<ofbiz:url>/createPartyGroup/<ofbiz:print attribute="donePage"/></ofbiz:url>' name="editgroupform">
    <%
        // remove session attribute "partyId" in case PartyGroup can not be created und returns to same page with "partyId" set
        request.getSession().removeAttribute("partyId");
    %>
</ofbiz:unless>
<ofbiz:if name="lookupGroup">
  <p class="head1">Edit Group Information</p>
    <FORM method=POST action='<ofbiz:url>/updatePartyGroup/<ofbiz:print attribute="donePage"/></ofbiz:url>' name="editgroupform">
</ofbiz:if>

<% if (partyId != null) { %>
<input type="hidden" name="partyId" value="<%=partyId%>">
<% } %>

&nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
&nbsp;<a href="javascript:document.editgroupform.submit()" class="buttontext">[Save]</a>

<table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">Group name</div></td>
      <td width="74%" align=left>
        <input type="text" class="inputBox" size="30" maxlength="30" <ofbiz:inputvalue field="groupName" entityAttr="lookupGroup" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Party Group (Sub-) Type</div></td>
      <td width="74%" align=left>
        <select name="partyTypeId" class="selectBox">
          <ofbiz:if name="lookupGroup">
              <option value='<ofbiz:entityfield attribute="lookupPartyType" field="partyTypeId"/>' selected><ofbiz:entityfield attribute="lookupPartyType" field="description"/></option>
              <option value='<ofbiz:entityfield attribute="lookupPartyType" field="partyTypeId"/>'>-----</option>
          </ofbiz:if>
          <option value='PARTY_GROUP'>Party Group</option>
          <%
                // determine all Subtypes of Partygroup-Type
                GenericValue partyGroupType = delegator.findByPrimaryKey("PartyType", UtilMisc.toMap("partyTypeId", "PARTY_GROUP"));
                List partyGroupSubTypes = EntityTypeUtil.getDescendantTypes(partyGroupType);
                pageContext.setAttribute("partyGroupSubTypes", partyGroupSubTypes);
          %>
          <ofbiz:iterator name="partyGroupSubType" property="partyGroupSubTypes">
            <option value='<ofbiz:entityfield attribute="partyGroupSubType" field="partyTypeId"/>'><ofbiz:entityfield attribute="partyGroupSubType" field="description"/></option>
          </ofbiz:iterator>
        </select>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Federal Tax Number</div></td>
      <td width="74%" align=left>
        <input type="text" class="inputBox" size="30" maxlength="60" <ofbiz:inputvalue field="federalTaxId" entityAttr="lookupGroup" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Comment</div></td>
      <td width="74%" align=left>
        <input type="text" class="inputBox" size="30" maxlength="60" <ofbiz:inputvalue field="comments" entityAttr="lookupGroup" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
</table>
</form>

&nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
&nbsp;<a href="javascript:document.editgroupform.submit()" class="buttontext">[Save]</a>
<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
