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

	List partyRoleExprs = UtilMisc.toList(new EntityExpr("partyId", EntityOperator.EQUALS, partyId), new EntityExpr("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"));
    List partyRoles = delegator.findByAnd("RoleTypeAndParty", partyRoleExprs, UtilMisc.toList("description"));
    if (partyRoles != null && partyRoles.size() > 0) pageContext.setAttribute("partyRoles", partyRoles);

    Collection roles = delegator.findAll("RoleType", UtilMisc.toList("description", "roleTypeId"));
    if (roles != null) pageContext.setAttribute("roles", roles);

    PartyWorker.getPartyOtherValues(pageContext, partyId, "party", "lookupPerson", "lookupGroup");
%>
<%EntityField entityField = new EntityField(pageContext);%>

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
      <a href="<ofbiz:url>/viewroles</ofbiz:url>" class="tabButtonSelected">Roles</a>
      <a href="<ofbiz:url>/viewrelationships</ofbiz:url>" class="tabButton">Relationships</a>
      </div>
    </td>
  </tr>
</table>

<%-- Party Roles --%>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Member Roles</div>
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
            <ofbiz:if name="partyRoles">
            <table width="100%" border="0" cellpadding="1">
              <ofbiz:iterator name="userRole" property="partyRoles">
              <tr>
                <td align="right" valign="top" width="10%" nowrap><div class="tabletext"><b>Role</b></div></td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="70%"><div class="tabletext"><ofbiz:entityfield attribute="userRole" field="description"/> [<ofbiz:entityfield attribute="userRole" field="roleTypeId"/>]</div></td>
                <%if(security.hasEntityPermission("PARTYMGR", "_DELETE", session)) {%>
                <td align="right" valign="top" width="20%">
                  <a href='<ofbiz:url>/deleterole?partyId=<%=partyId%>&roleTypeId=<ofbiz:entityfield attribute="userRole" field="roleTypeId"/></ofbiz:url>' class="buttontext">[Remove]</a>&nbsp;
                </td>
                <%}%>
              </tr>
              </ofbiz:iterator>
            </table>
            </ofbiz:if>
            <ofbiz:unless name="partyRoles">
              <div class="tabletext">No party roles found.</div>
            </ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <%if(security.hasEntityPermission("PARTYMGR", "_UPDATE", session)) {%>
  <TR>
    <TD width="100%"><hr class="sepbar"></TD>
  </TR>
  <TR>
    <TD width="100%" >
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <form name="addPartyRole" method="post" action="<ofbiz:url>/addrole/viewroles</ofbiz:url>">
        <input type="hidden" name="partyId" value="<%=partyId%>">
        <tr>
          <td align="right" width="75%"><span class="tabletext">&nbsp;Add To Role:&nbsp;</span></td>
          <td>
            <select name="roleTypeId" style="font-size: x-small;">
              <ofbiz:iterator name="role" property="roles">
                <option value='<ofbiz:entityfield attribute="role" field="roleTypeId"/>'><ofbiz:entityfield attribute="role" field="description"/><%-- [<ofbiz:entityfield attribute="role" field="roleTypeId"/>]--%></option>
              </ofbiz:iterator>
            </select>
          </td>
          <td>
            <a href="javascript:document.addPartyRole.submit()" class="buttontext">[Add]</a>&nbsp;&nbsp;
          </td>
        </tr>
        </form>
      </table>
    </TD>
  </TR>
  <%}%>
</TABLE>

<%-- Add role type --%>
<%if(security.hasEntityPermission("PARTYMGR", "_CREATE", session)) {%>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;New Role Type</div>
          </td>
        </tr>
      </table>
    </TD>
    </form>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='1' cellpadding='1' class='boxbottom'>
        <form method="post" action="<ofbiz:url>/createroletype/viewroles</ofbiz:url>" name="createroleform">
        <tr>
          <td width="16%"><div class="tabletext">Role Type ID</div></td>
          <td width="84%">
            <input type="text" name="roleTypeId" size="20" style="font-size: x-small;">*
          </td>
        <tr>
          <td width="16%"><div class="tabletext">Description</div></td>
          <td width="84%">
            <input type="text" name="description" size="30" style="font-size: x-small;">*
            &nbsp;&nbsp;<a href="javascript:document.createroleform.submit()" class="buttontext">[Save]</a>
          </td>
        </tr>
        </form>
      </table>
    </TD>
  </TR>
</TABLE>
<%}%>

<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>