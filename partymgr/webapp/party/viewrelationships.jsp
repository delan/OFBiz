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
 * @author     David E. Jones
 * @author     Andy Zeneski
 * @created    July 12, 2002
 * @version    1.0
--%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*, org.ofbiz.commonapp.party.party.*" %>
<%@ page import="org.ofbiz.commonapp.accounting.payment.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>

<%
    String partyId = request.getParameter("party_id");
    if (partyId == null) partyId = (String) request.getAttribute("partyId");
    if (partyId == null) partyId = (String) request.getSession().getAttribute("partyId");
    else request.getSession().setAttribute("partyId", partyId);

    Collection roleTypes = delegator.findAll("RoleType", UtilMisc.toList("description", "roleTypeId"));
    if (roleTypes != null) pageContext.setAttribute("roleTypes", roleTypes);

    Collection relateTypes = delegator.findAll("PartyRelationshipType", UtilMisc.toList("description", "partyRelationshipTypeId"));
    if (relateTypes != null) pageContext.setAttribute("relateTypes", relateTypes);

    Collection partyRelationships = delegator.findByOr("PartyRelationship", UtilMisc.toList(new EntityExpr("partyIdTo", EntityOperator.EQUALS, partyId), new EntityExpr("partyIdFrom", EntityOperator.EQUALS, partyId)));
    if (partyRelationships != null && partyRelationships.size() > 0) pageContext.setAttribute("partyRelationships", partyRelationships);

    PartyWorker.getPartyOtherValues(pageContext, partyId, "party", "lookupPerson", "lookupGroup");
%>
<%EntityField entityField = new EntityField(pageContext);%>
<%InputValue inputValue = new InputValue(pageContext);%>

<script language='JavaScript'>
    function setNowFromDate(formName) { eval('document.' + formName + '.fromDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
</script>

<%-- Main Heading --%>
<br>
<table width='100%' cellpadding='0' cellspacing='0' border='0'>
  <tr>
    <td align='left'>
      <div class="head1">The Profile of
        <ofbiz:if name="lookupPerson">
          <%entityField.run("lookupPerson", "personalTitle");%>
          <%entityField.run("lookupPerson", "firstName");%>
          <%entityField.run("lookupPerson", "middleName");%>
          <%entityField.run("lookupPerson", "lastName");%>
          <%entityField.run("lookupPerson", "suffix");%>
        </ofbiz:if>
        <ofbiz:unless name="lookupPerson">
          <ofbiz:if name="lookupGroup">
            <%entityField.run("lookupGroup", "groupName");%>
          </ofbiz:if>
          <ofbiz:unless name="lookupGroup">"New User"</ofbiz:unless>
       </ofbiz:unless>
      </div>
    </td>
    <td align='right'>
	  <div class='tabContainer'>
      <a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="tabButton">Profile</a>
      <a href="<ofbiz:url>/viewroles</ofbiz:url>" class="tabButton">Roles</a>
      <a href="<ofbiz:url>/viewrelationships</ofbiz:url>" class="tabButtonSelected">Relationships</a>
      </div>
    </td>
  </tr>
</table>

<%-- Party Relationships --%>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Party Relationships</div>
          </td>
        </tr>
      </table>
    </TD>
    </form>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <ofbiz:if name="partyRelationships">
            <table width="100%" border="1" cellpadding="1" cellspacing="0">
              <tr>
                <td><div class="tabletext"><b>&nbsp;Description</b></div></td>
                <td><div class="tabletext"><b>&nbsp;From Date</b></div></td>
                <td><div class="tabletext"><b>&nbsp;Thru Date</b></div></td>
                <td><div class="tabletext"><b>&nbsp;Status</b></div></td>
                <td>&nbsp;</td>
              </tr>
              <ofbiz:iterator name="partyRelationship" property="partyRelationships">
              	  <%GenericValue partyRelationshipType = partyRelationship.getRelatedOneCache("PartyRelationshipType");%>
              	  <%if (partyRelationshipType != null) pageContext.setAttribute("partyRelationshipType", partyRelationshipType);%>
              	  <%GenericValue roleTypeTo = partyRelationship.getRelatedOneCache("ToRoleType");%>
              	  <%if (roleTypeTo != null) pageContext.setAttribute("roleTypeTo", roleTypeTo);%>
              	  <%GenericValue roleTypeFrom = partyRelationship.getRelatedOneCache("FromRoleType");%>
              	  <%if (roleTypeFrom != null) pageContext.setAttribute("roleTypeFrom", roleTypeFrom);%>
	              <tr>
	                <td><div class="tabletext">
	                	party <b><%entityField.run("partyRelationship", "partyIdTo");%></b>
	                	<%if (!"_NA_".equals(partyRelationship.getString("roleTypeIdTo"))) {%>
		                	in role <b><%entityField.run("roleTypeTo", "description");%></b>
		                <%}%>
	                	is a <b><%entityField.run("partyRelationshipType", "partyRelationshipName");%></b>
	                	of party <b><%entityField.run("partyRelationship", "partyIdFrom");%></b>
	                	<%if (!"_NA_".equals(partyRelationship.getString("roleTypeIdFrom"))) {%>
		                	in role <b><%entityField.run("roleTypeFrom", "description");%></b>
		                <%}%>
	                </div></td>
	                <td><div class="tabletext">&nbsp;<%inputValue.run("fromDate", "partyRelationship");%></div></td>
	                <td><div class="tabletext">&nbsp;<%inputValue.run("thruDate", "partyRelationship");%></div></td>
	                <td><div class="tabletext">&nbsp;<%entityField.run("partyRelationship", "statusId");%></div></td>
	                <td align="right">
	                  &nbsp;<a href='<ofbiz:url>/deletePartyRelationship?partyIdTo=<%inputValue.run("partyIdTo", "partyRelationship");%>&roleTypeIdTo=<%inputValue.run("roleTypeIdTo", "partyRelationship");%>&roleTypeIdFrom=<%inputValue.run("roleTypeIdFrom", "partyRelationship");%>&partyIdFrom=<%inputValue.run("partyIdFrom", "partyRelationship");%>&fromDate=<%=UtilFormatOut.encodeQueryValue(partyRelationship.getTimestamp("fromDate").toString())%></ofbiz:url>' class="buttontext">[Remove]</a>&nbsp
	                </td>
	              </tr>
              </ofbiz:iterator>
            </table>
            </ofbiz:if>
            <ofbiz:unless name="partyRelationships">
              <div class="tabletext">No relationships found.</div>
            </ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width="100%"><hr class="sepbar"></TD>
  </TR>
  <TR>
    <TD width="100%" >
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <form name="addPartyRelationshipTo" method="post" action="<ofbiz:url>/createPartyRelationship</ofbiz:url>">
        <input type="hidden" name="partyIdFrom" value="<%=partyId%>">
        <tr>
          <td>
          	<div class="tabletext" style="font-weight: bold;">
	            The party with ID <input type="text" size="20" name="partyIdTo" style="font-size: x-small;">
	            in the role of <select name="roleTypeIdTo" style="font-size: x-small;">
	              <ofbiz:iterator name="roleType" property="roleTypes">
	                <option <%=("_NA_".equals(roleType.getString("roleTypeId"))) ? "SELECTED" : ""%> value='<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>'><ofbiz:entityfield attribute="roleType" field="description"/><%-- [<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>]--%></option>
	              </ofbiz:iterator>
	            </select>
	            is a <select name="partyRelationshipTypeId" style="font-size: x-small;">
	              <ofbiz:iterator name="relateType" property="relateTypes">
	                <option value='<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>'><ofbiz:entityfield attribute="relateType" field="partyRelationshipName"/><%-- [<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>]--%></option>
	              </ofbiz:iterator>
	            </select>
	            of the current party in the role of
	            <select name="roleTypeIdFrom" style="font-size: x-small;">
	              <ofbiz:iterator name="roleType" property="roleTypes">
	                <option <%=("_NA_".equals(roleType.getString("roleTypeId"))) ? "SELECTED" : ""%> value='<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>'><ofbiz:entityfield attribute="roleType" field="description"/><%-- [<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>]--%></option>
	              </ofbiz:iterator>
	            </select>
	            from <input type="text" size="24" name="fromDate" style="font-size: x-small;">&nbsp;<a href="javascript:setNowFromDate('addPartyRelationshipTo')" class="buttontext">[Now]</a>
	            thru <input type="text" size="24" name="thruDate" style="font-size: x-small;">
	        </div>
          </td>
          <td>
            <a href="javascript:document.addPartyRelationshipTo.submit()" class="buttontext">[Add]</a>&nbsp;&nbsp;
          </td>
        </tr>
        <tr>
         <td colspan="2"><span class="tabletext">Comments:&nbsp;&nbsp;</span><input type="text" size="60" name="comments" style="font-size: x-small;"></td>
        </tr>
        </form>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width="100%"><hr class="sepbar"></TD>
  </TR>
  <TR>
    <TD width="100%" >
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <form name="addPartyRelationshipFrom" method="post" action="<ofbiz:url>/createPartyRelationship</ofbiz:url>">
        <input type="hidden" name="partyIdTo" value="<%=partyId%>">
        <tr>
          <td>
          	<div class="tabletext" style="font-weight: bold;">
          		The current party in the role of
	            <select name="roleTypeIdTo" style="font-size: x-small;">
	              <ofbiz:iterator name="roleType" property="roleTypes">
	                <option <%=("_NA_".equals(roleType.getString("roleTypeId"))) ? "SELECTED" : ""%> value='<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>'><ofbiz:entityfield attribute="roleType" field="description"/><%-- [<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>]--%></option>
	              </ofbiz:iterator>
	            </select>
	            is a 
	            <select name="partyRelationshipTypeId" style="font-size: x-small;">
	              <ofbiz:iterator name="relateType" property="relateTypes">
	                <option value='<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>'><ofbiz:entityfield attribute="relateType" field="partyRelationshipName"/><%-- [<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>]--%></option>
	              </ofbiz:iterator>
	            </select>
	            of the party with ID
	            <input type="text" size="20" name="partyIdFrom" style="font-size: x-small;">
	            in the role of 
	            <select name="roleTypeIdFrom" style="font-size: x-small;">
	              <ofbiz:iterator name="roleType" property="roleTypes">
	                <option <%=("_NA_".equals(roleType.getString("roleTypeId"))) ? "SELECTED" : ""%> value='<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>'><ofbiz:entityfield attribute="roleType" field="description"/><%-- [<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>]--%></option>
	              </ofbiz:iterator>
	            </select>
	            from <input type="text" size="24" name="fromDate" style="font-size: x-small;">&nbsp;<a href="javascript:setNowFromDate('addPartyRelationshipFrom')" class="buttontext">[Now]</a>&nbsp;
	            thru <input type="text" size="24" name="thruDate" style="font-size: x-small;">
	        </div>
          </td>
          <td>
            <a href="javascript:document.addPartyRelationshipFrom.submit()" class="buttontext">[Add]</a>&nbsp;&nbsp;
          </td>
        </tr>
        <tr>
         <td colspan="2"><span class="tabletext">Comments:&nbsp;&nbsp;</span><input type="text" size="60" name="comments" style="font-size: x-small;"></td>
        </tr>
        </form>
      </table>
    </TD>
  </TR>
</TABLE>

<%-- Add relationship type --%>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;New Relationship Type</div>
          </td>
        </tr>
      </table>
    </TD>
    </form>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='1' cellpadding='1' class='boxbottom'>
        <form method="post" action="<ofbiz:url>/addrelationshiptype/viewrelationships</ofbiz:url>" name="createrelatetypeform">
        <tr>
          <td width="16%"><div class="tabletext">Relationship Type ID</div></td>
          <td width="84%">
            <input type="text" name="partyRelationshipTypeId" size="20" style="font-size: x-small;">
          *</td>
        </tr>
        <tr>
          <td width="16%"><div class="tabletext">Relationship Name</div></td>
          <td width="84%">
            <input type="text" name="partyRelationshipName" size="20" style="font-size: x-small;">
          *</td>
        </tr>
        <tr>
          <td width="16%"><div class="tabletext">Description</div></td>
          <td width="84%">
            <input type="text" name="description" size="60" style="font-size: x-small;">
          </td>
        </tr>
        <tr>
          <td width="16%"><div class="tabletext">Parent Type</div></td>
          <td width="84%">
            <select name="parentTypeId" style="font-size: x-small;">
              <option value=''></option>
              <ofbiz:iterator name="relateType" property="relateTypes">
                <option value='<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>'><ofbiz:entityfield attribute="relateType" field="partyRelationshipName"/><%-- [<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>]--%></option>
              </ofbiz:iterator>
            </select>
          </td>
        </tr>
        <tr>
          <td width="16%"><div class="tabletext">Has Table</div></td>
          <td width="84%">
            <select name="hasTable" style="font-size: x-small;">
            <option value='N'>No</option>
            <option value='Y'>Yes</option>
            </select>
          </td>
        </tr
        <tr>
          <td width="16%"><div class="tabletext">Valid From RoleType</div></td>
          <td width="84%">
            <select name="roleTypeIdValidFrom" style="font-size: x-small;">
            <option value=''></option>
              <ofbiz:iterator name="roleType" property="roleTypes">
                <option value='<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>'><ofbiz:entityfield attribute="roleType" field="description"/><%-- [<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>]--%></option>
              </ofbiz:iterator>
            </select>
          </td>
        </tr>
        <tr>
          <td width="16%"><div class="tabletext">Valid To RoleType</div></td>
          <td width="84%">
            <select name="roleTypeIdValidTo" style="font-size: x-small;">
            <option value=''></option>
              <ofbiz:iterator name="roleType" property="roleTypes">
                <option value='<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>'><ofbiz:entityfield attribute="roleType" field="description"/><%-- [<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>]--%></option>
              </ofbiz:iterator>
            </select>
            &nbsp;&nbsp;<a href="javascript:document.createrelatetypeform.submit()" class="buttontext">[Save]</a>
          </td>
        </tr>
        </form>
      </table>
    </TD>
  </TR>
</TABLE>

<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
