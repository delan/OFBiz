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
 *@version    $Revision$
 *@since      2.0
--%>

<%@ page import="java.util.*, org.ofbiz.commonapp.party.party.PartyWorker" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
	String custRequestId = request.getParameter("custRequestId");	
    GenericValue custRequest = null;
    if (custRequestId != null) {
        custRequest = delegator.findByPrimaryKey("CustRequest", UtilMisc.toMap("custRequestId", custRequestId));
        if (custRequest != null) pageContext.setAttribute("custRequest", custRequest);
        List requestRoles = delegator.findByAnd("CustRequestRole", UtilMisc.toMap("custRequestId", custRequestId));
        if (requestRoles != null && requestRoles.size() > 0) pageContext.setAttribute("custRequestRoles", requestRoles);
    }	
    EntityField entityField = new EntityField(pageContext);   	
%>

<BR>
<div class='tabContainer'>
  <a href="<ofbiz:url>/request?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request</a>
  <a href="<ofbiz:url>/requestroles?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButtonSelected">Request Roles</a>
  <a href="<ofbiz:url>/requestitems?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request Items</a>
</div>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Request Roles</div>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <ofbiz:unless name="custRequestRoles">
                  <TR>
                    <TD><div class="tabletext">No roles associated with this customer request.</div></TD>
                  </TR>
                </ofbiz:unless>
                <ofbiz:if name="custRequestRoles">
                  <TR>
                    <TD>
                    <table width="100%" cellpadding="2" cellspacing="0" border="0">
                      <tr>
                        <td><div class="tableheadtext">PartyId</div></td>
                        <td><div class="tableheadtext">Name</div></td>
                        <td><div class="tableheadtext">RoleTypeId</div></td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr>
                        <td colspan='4'><hr class='sepbar'></td>
                      </tr>
                      <ofbiz:iterator name="role" property="custRequestRoles">
                          <tr>
                            <%
                                PartyWorker.getPartyOtherValues(pageContext, role.getString("partyId"), "party", "person", "partyGroup");
                                GenericValue roleType = role.getRelatedOne("RoleType");
                                pageContext.setAttribute("roleType", roleType);
                            %>
                            <td><div class="tabletext"><a href="/partymgr/control/viewprofile?party_id=<%entityField.run("party", "partyId");%>" target="partymgr" class="buttontext"><%entityField.run("party", "partyId");%></a></div></td>
                            <ofbiz:if name="person">
                              <td><div class="tabletext"><%entityField.run("person", "firstName");%>&nbsp;<%entityField.run("person", "lastName");%></div></td>
                            </ofbiz:if>
                            <ofbiz:if name="partyGroup">
                              <td><div class="tabletext"><%entityField.run("partyGroup", "groupName");%></div></td>
                            </ofbiz:if>
                            <td><div class="tabletext"><%=roleType.getString("description")%></div></td>
                            <td align="right"><div class="tabletext"><a href="<ofbiz:url>/removerequestrole?custRequestId=<%=custRequestId%>&partyId=<%=role.getString("partyId")%>&roleTypeId=<%=role.getString("roleTypeId")%></ofbiz:url>" class="buttontext">[Remove]</a></td>
                          </tr>
                        </ofbiz:iterator>
                      </table>
                    </TD>
                  </TR>
                </ofbiz:if>
                <ofbiz:if name="custRequest">
                <TR>
                  <TD><HR class="sepbar"></TD>
                </TR>
                <TR>
                  <TD><div class="head3">Add New:</div</TD>
                </TR>
                <TR>
                  <TD>
                    <form method="post" action="<ofbiz:url>/createrequestrole</ofbiz:url>">
                      <input type="hidden" name="custRequestId" value="<%=custRequestId%>">
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <tr>
                          <td colspan="2">&nbsp;&nbsp;&nbsp;</td>
                          <td align="right"><div class="tableheadtext">Party ID</div></td>
                          <td><input type="text" name="partyId" style="font-size: small;" size="30"></td>
                          <td align="right"><div class="tableheadtext">Role Type ID</div></td>
                          <td>
                            <select name="roleTypeId" style="font-size: small;">
                              <option value="REQ_TAKER">Request Taker</option>
                              <option value="REQ_REQUESTER">Requesting Party</option>
                              <option value="REQ_MANAGER">Request Manager</option>
                            </select>
                          </td>
                          <td align="center"><input type="submit" style="font-size: small;" value="Add"></td>
                          <td colspan="2">&nbsp;&nbsp;&nbsp;</td>
                        </tr>
                      </table>
                    </form>
                  </TD>
                </TR>
                </ofbiz:if>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

