<%
/**
 *  Title: View Relationships Page
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
 * @author     Andy Zeneski
 * @created    July 12, 2002
 * @version    1.0
 */
%>

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

    Collection roles = delegator.findAll("RoleType", UtilMisc.toList("description", "roleTypeId"));
    if (roles != null) pageContext.setAttribute("roles", roles);

    Collection relateTypes = delegator.findAll("PartyRelationshipType", UtilMisc.toList("description", "partyRelationshipTypeId"));
    if (relateTypes != null) pageContext.setAttribute("relateTypes", relateTypes);

    Collection relationships = delegator.findByOr("PartyRelationship", UtilMisc.toList(new EntityExpr("partyIdTo", EntityOperator.EQUALS, partyId), new EntityExpr("partyIdFrom", EntityOperator.EQUALS, partyId)));
    if (relationships != null && relationships.size() > 0) pageContext.setAttribute("relationships", relationships);
    System.out.println("Relationships::"+relationships);

    PartyWorker.getPartyOtherValues(pageContext, partyId, "party", "person", "partyGroup");
%>
<%EntityField entityField = new EntityField(pageContext);%>

<script language='JavaScript'>
    function setNowFromDate(formName) { eval('document.' + formName + '.fromDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
</script>

<%-- Main Heading --%>
<br>
<table width='100%' cellpadding='0' cellspacing='0' border='0'>
  <tr>
    <td align='left'>
      <div class="head1">The Profile of
        <ofbiz:if name="person">
          <%entityField.run("person", "personalTitle");%>
          <%entityField.run("person", "firstName");%>
          <%entityField.run("person", "middleName");%>
          <%entityField.run("person", "lastName");%>
          <%entityField.run("person", "suffix");%>
        </ofbiz:if>
        <ofbiz:unless name="person">
          <ofbiz:if name="partyGroup">
            <%entityField.run("partyGroup", "groupName");%>
          </ofbiz:if>
          <ofbiz:unless name="partyGroup">"New User"</ofbiz:unless>
       </ofbiz:unless>
      </div>
    </td>
    <td align='right'>
      <a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="buttontext">[Profile]</a>&nbsp;&nbsp;
      <a href="<ofbiz:url>/viewroles</ofbiz:url>" class="buttontext">[Roles]</a>&nbsp;&nbsp;
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
            <ofbiz:if name="relationships">
            <table width="100%" border="1" cellpadding="1" cellspacing="0">
              <tr>
                <td><div class="tabletext"><b>&nbsp;Type<b></div></td>
                <td><div class="tabletext"><b>&nbsp;Party To</b></div></td>
                <td><div class="tabletext"><b>&nbsp;Role To</b></div></td>
                <td><div class="tabletext"><b>&nbsp;Party From</b></div></td>
                <td><div class="tabletext"><b>&nbsp;Role From</b></div></td>
                <td><div class="tabletext"><b>&nbsp;From Date</b></div></td>
                <td><div class="tabletext"><b>&nbsp;Thru Date</b></div></td>
                <td><div class="tabletext"><b>&nbsp;Status</b></div></td>
                <td>&nbsp;</td>
              </tr>
              <ofbiz:iterator name="userRelate" property="relationships">
              <tr>
                <td valign="top"><div class="tabletext">&nbsp;[<%entityField.run("userRelate", "partyRelationshipTypeId");%>]</div></td>
                <td valign="top"><div class="tabletext">&nbsp;<%entityField.run("userRelate", "partyIdTo");%></div></td>
                <td valign="top"><div class="tabletext">&nbsp;<%entityField.run("userRelate", "roleTypeIdTo");%></div></td>
                <td valign="top"><div class="tabletext">&nbsp;<%entityField.run("userRelate", "partyIdFrom");%></div></td>
                <td valign="top"><div class="tabletext">&nbsp;<%entityField.run("userRelate", "roleTypeIdFrom");%></div></td>
                <td valign="top"><div class="tabletext">&nbsp;<%entityField.run("userRelate", "fromDate");%></div></td>
                <td valign="top"><div class="tabletext">&nbsp;<%entityField.run("userRelate", "thruDate");%></div></td>
                <td valign="top"><div class="tabletext">&nbsp;<%entityField.run("userRelate", "statusId");%></div></td>
                <td align="right" valign="top">
                  &nbsp;<a href='<ofbiz:url>/</ofbiz:url>' class="buttontext">[Remove]</a>&nbsp
                </td>
              </tr>
              </ofbiz:iterator>
            </table>
            </ofbiz:if>
            <ofbiz:unless name="relationships">
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
        <form name="addPartyRelationshipTo" method="post" action="<ofbiz:url>/createrelationship/viewrelationships</ofbiz:url>">
        <input type="hidden" name="partyIdFrom" value="<%=partyId%>">
        <tr>
          <td width="15%">&nbsp;</td>
          <td colspan="2"><span class="tabletext"><b>RoleTypeIdFrom / PartyIdTo / RoleTypeIdTo / RelationshipType / FromDate</b></span></td>
        </tr>
        <tr>
          <td width="15%"><span class="tabletext">&nbsp;New Relationship To:&nbsp;&nbsp;</span></td>
          <td>
            <select name="roleTypeIdFrom" style="font-size: x-small;">
              <ofbiz:iterator name="role" property="roles">
                <option <%=(role.get("roleTypeId") != null && role.getString("roleTypeId").equals("_NA_")) ? "SELECTED" : ""%> value='<ofbiz:entityfield attribute="role" field="roleTypeId"/>'><ofbiz:entityfield attribute="role" field="description"/> [<ofbiz:entityfield attribute="role" field="roleTypeId"/>]</option>
              </ofbiz:iterator>
            </select>
            <input type="text" size="20" name="partyIdTo" style="font-size: x-small;">
            <select name="roleTypeIdTo" style="font-size: x-small;">
              <ofbiz:iterator name="role" property="roles">
                <option <%=(role.get("roleTypeId") != null && role.getString("roleTypeId").equals("_NA_")) ? "SELECTED" : ""%> value='<ofbiz:entityfield attribute="role" field="roleTypeId"/>'><ofbiz:entityfield attribute="role" field="description"/> [<ofbiz:entityfield attribute="role" field="roleTypeId"/>]</option>
              </ofbiz:iterator>
            </select>
            <select name="partyRelationshipTypeId" style="font-size: x-small;">
              <ofbiz:iterator name="relateType" property="relateTypes">
                <option value='<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>'><ofbiz:entityfield attribute="relateType" field="description"/> [<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>]</option>
              </ofbiz:iterator>
            </select>
            <input type="text" size="22" name="fromDate" style="font-size: x-small;">&nbsp;<a href="javascript:setNowFromDate('addPartyRelationshipTo')" class="buttontext">[Now]</a>&nbsp;
          </td>
          <td>
            <a href="javascript:document.addPartyRelationshipTo.submit()" class="buttontext">[Add]</a>&nbsp;&nbsp;
          </td>
        </tr>
        <tr>
         <td width="15%">&nbsp;</td>
         <td colspan="2"><span class="tabletext">Comments:&nbsp;&nbsp;</span><input type="text" size="60" name="comments" style="font-size: x-small;"></td>
        </tr>
        </form>
      </table>
    </TD>
  </TR>
  <TR>
    <TD>&nbsp;</TD>
  </TR>
  <TR>
    <TD width="100%" >
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <form name="addPartyRelationshipFrom" method="post" action="<ofbiz:url>/createrelationship/viewrelationships</ofbiz:url>">
        <input type="hidden" name="partyIdTo" value="<%=partyId%>">
        <tr>
          <td width="15%">&nbsp;</td>
          <td colspan="2"><span class="tabletext"><b>RoleTypeIdTo / PartyIdFrom / RoleTypeIdFrom / RelationshipType / FromDate</b></span></td>
        </tr>
        <tr>
          <td width="15%"><span class="tabletext">&nbsp;New Relationship From:&nbsp;&nbsp;</span></td>
          <td>
            <select name="roleTypeIdTo" style="font-size: x-small;">
              <ofbiz:iterator name="role" property="roles">
                <option <%=(role.get("roleTypeId") != null && role.getString("roleTypeId").equals("_NA_")) ? "SELECTED" : ""%> value='<ofbiz:entityfield attribute="role" field="roleTypeId"/>'><ofbiz:entityfield attribute="role" field="description"/> [<ofbiz:entityfield attribute="role" field="roleTypeId"/>]</option>
              </ofbiz:iterator>
            </select>
            <input type="text" size="20" name="partyIdFrom" style="font-size: x-small;">
            <select name="roleTypeIdFrom" style="font-size: x-small;">
              <ofbiz:iterator name="role" property="roles">
                <option <%=(role.get("roleTypeId") != null && role.getString("roleTypeId").equals("_NA_")) ? "SELECTED" : ""%> value='<ofbiz:entityfield attribute="role" field="roleTypeId"/>'><ofbiz:entityfield attribute="role" field="description"/> [<ofbiz:entityfield attribute="role" field="roleTypeId"/>]</option>
              </ofbiz:iterator>
            </select>
            <select name="partyRelationshipTypeId" style="font-size: x-small;">
              <ofbiz:iterator name="relateType" property="relateTypes">
                <option value='<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>'><ofbiz:entityfield attribute="relateType" field="description"/> [<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>]</option>
              </ofbiz:iterator>
            </select>
            <input type="text" size="22" name="fromDate" style="font-size: x-small;">&nbsp;<a href="javascript:setNowFromDate('addPartyRelationshipFrom')" class="buttontext">[Now]</a>&nbsp;
          </td>
          <td>
            <a href="javascript:document.addPartyRelationshipFrom.submit()" class="buttontext">[Add]</a>&nbsp;&nbsp;
          </td>
        </tr>
        <tr>
         <td width="15%">&nbsp;</td>
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
          </td>
        </tr>
        <tr>
          <td width="16%"><div class="tabletext">Description</div></td>
          <td width="84%">
            <input type="text" name="description" size="30" style="font-size: x-small;">
          </td>
        </tr>
        <tr>
          <td width="16%"><div class="tabletext">Parent Type</div></td>
          <td width="84%">
            <select name="parentTypeId" style="font-size: x-small;">
              <option value=''></option>
              <ofbiz:iterator name="relateType" property="relateTypes">
                <option value='<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>'><ofbiz:entityfield attribute="relateType" field="description"/> [<ofbiz:entityfield attribute="relateType" field="partyRelationshipTypeId"/>]</option>
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
              <ofbiz:iterator name="role" property="roles">
                <option value='<ofbiz:entityfield attribute="role" field="roleTypeId"/>'><ofbiz:entityfield attribute="role" field="description"/> [<ofbiz:entityfield attribute="role" field="roleTypeId"/>]</option>
              </ofbiz:iterator>
            </select>
          </td>
        </tr>
        <tr>
          <td width="16%"><div class="tabletext">Valid To RoleType</div></td>
          <td width="84%">
            <select name="roleTypeIdValidTo" style="font-size: x-small;">
            <option value=''></option>
              <ofbiz:iterator name="role" property="roles">
                <option value='<ofbiz:entityfield attribute="role" field="roleTypeId"/>'><ofbiz:entityfield attribute="role" field="description"/> [<ofbiz:entityfield attribute="role" field="roleTypeId"/>]</option>
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
