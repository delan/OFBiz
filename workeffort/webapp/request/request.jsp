<%--
/**
 *  Title: Edit Request Page
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
 *@created    July 31, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*,
                 org.ofbiz.commonapp.party.party.PartyWorker" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
    // check for a requestId and get the requst object
    String custRequestId = request.getParameter("custRequestId");
    GenericValue custRequest = null;
    if (custRequestId == null) custRequestId = (String) request.getSession().getAttribute("custRequestId");
    if (custRequestId != null) {
        custRequest = delegator.findByPrimaryKey("CustRequest", UtilMisc.toMap("custRequestId", custRequestId));
        if (custRequest != null) pageContext.setAttribute("custRequest", custRequest);
    }
    Collection custRequestTypes = delegator.findAllCache("CustRequestType", UtilMisc.toList("description"));
    pageContext.setAttribute("custRequestTypes", custRequestTypes);
    EntityField entityField = new EntityField(pageContext);
%>

<script language='JavaScript'>
  function setCustRequestDate() { document.custRequestForm.custRequestDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"; }
</script>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Request Detail</div>
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
                <TR>
                  <TD>
                    <ofbiz:unless name="custRequest">
                      <form method="post" action="<ofbiz:url>/createrequest</ofbiz:url>" name="custRequestForm">
                    </ofbiz:unless>
                    <ofbiz:if name="custRequest">
                      <form method="post" action="<ofbiz:url>/updaterequest</ofbiz:url>" name="custRequestForm">
                        <input type="hidden" name="custRequestId" value="<%=custRequestId%>">
                    </ofbiz:if>
                    <table width="100%" cellpadding="2" cellspacing="0" border="0">
                      <tr>
                        <td align="right">Request Date</td>
                        <td><input type="text" style="font-size: small;" size="23" <ofbiz:inputvalue entityAttr="custRequest" field="custRequestDate" fullattrs="true"/>> <a href='#' onclick='setCustRequestDate()' class='buttontext'>[Now]</a></td>
                      </tr>
                      <tr>
                        <td align="right">Response Required Date</td>
                        <td><input type="text" style="font-size: small;" size="23" <ofbiz:inputvalue entityAttr="custRequest" field="responseRequiredDate" fullattrs="true"/>></td>
                      </tr>
                      <tr>
                        <td align="right">RequestType</td>
                        <td>
                          <select name="custRequestTypeId" style="font-size: small;">
                            <ofbiz:iterator name="custRequestType" property="custRequestTypes">
                              <%if (custRequest != null && custRequest.getString("custRequestTypeId").equals(custRequestType.getString("custRequestTypeId"))) {%>
                                <option SELECTED value="<%=custRequestType.getString("custRequestTypeId")%>"><%=custRequestType.getString("description")%></option>
                              <%} else {%>
                                <option value="<%=custRequestType.getString("custRequestTypeId")%>"><%=custRequestType.getString("description")%></option>
                              <%}%>
                            </ofbiz:iterator>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td align="right">Name</td>
                        <td><input type="text" style="font-size: small;" size="50" <ofbiz:inputvalue entityAttr="custRequest" field="custRequestName" fullattrs="true"/>></td>
                      </tr>
                      <tr>
                        <td align="right">Description</td>
                        <td><input type="text" style="font-size: small;" size="50"  <ofbiz:inputvalue entityAttr="custRequest" field="description" fullattrs="true"/>></td>
                      </tr>

                      <ofbiz:unless name="custRequest">
                      <tr>
                        <td align="right">Requesting Party</td>
                        <td><input type="text" name="requestPartyId" style="font-size: small;" size="50"></td>
                      </tr>
                      </ofbiz:unless>

                      <tr>
                        <ofbiz:unless name="custRequest">
                          <td align="right"><input type="submit" style="font-size: small;" value="Create"></td>
                        </ofbiz:unless>
                        <ofbiz:if name="custRequest">
                          <td align="right"><input type="submit" style="font-size: small;" value="Update"></td>
                        </ofbiz:if>
                        <td>&nbsp</td>
                      </tr>
                    </table>
                    </form>
                  </TD>
                </TR>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<%
    // get all the associated roles if available.
    if (custRequestId != null) {
        Collection requestRoles = delegator.findByAnd("CustRequestRole", UtilMisc.toMap("custRequestId", custRequestId));
        if (requestRoles != null && requestRoles.size() > 0) pageContext.setAttribute("custRequestRoles", requestRoles);
    }
%>
<BR>
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
                      <ofbiz:iterator name="role" property="custRequestRoles">
                          <tr>
                            <%
                                PartyWorker.getPartyOtherValues(pageContext, role.getString("partyId"), "party", "person", "partyGroup");
                                GenericValue roleType = role.getRelatedOne("RoleType");
                                pageContext.setAttribute("roleType", roleType);
                            %>
                            <td align="right"><div class="tabletext"><%entityField.run("party", "partyId");%></div></td>
                            <ofbiz:if name="person">
                              <td><div class="tabletext"><%entityField.run("person", "firstName");%>&nbsp;<%entityField.run("person", "lastName");%></div></td>
                            </ofbiz:if>
                            <ofbiz:if name="partyGroup">
                              <td><div class="tabletext"><%entityField.run("partyGroup", "groupName");%></div></td>
                            </ofbiz:if>
                            <td><div class="tabletext"><%=roleType.getString("description")%></div></td>
                            <td align="right"><div class="tabletext"><a href="#" class="buttontext">[Remove]</a></td>
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
                  <TD><div class="head2">Add New:</div</TD>
                </TR>
                <TR>
                  <TD>
                    <form method="post" action="<ofbiz:url>/createrequestrole</ofbiz:url>">
                      <input type="hidden" name="custRequestId" value="<%=custRequestId%>">
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <tr>
                          <td colspan="2">&nbsp;&nbsp;&nbsp;</td>
                          <td align="right">Party ID</td>
                          <td><input type="text" name="partyId" style="font-size: small;" size="30"></td>
                          <td align="right">Role Type ID</td>
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

<%
    // get all the request items.
    int nextSeqId = 1;
    if (custRequestId != null) {
        Collection requestItems = delegator.findByAnd("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId));
        if (requestItems != null && requestItems.size() > 0) {
            pageContext.setAttribute("custRequestItems", requestItems);
            nextSeqId = requestItems.size() + 1;
        }
    }
%>
<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Request Items</div>
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
                <ofbiz:unless name="custRequestItems">
                  <TR>
                    <TD><div class="tabletext">No items created.</div></TD>
                  </TR>
                </ofbiz:unless>
                <ofbiz:if name="custRequestItems">
                <TR>
                  <TD>
                    <table width="100%" cellpadding="2" cellspacing="0" border="0">
                      <ofbiz:iterator name="item" property="custRequestItems">
                          <tr>
                            <td align="right"><div class="tabletext"><%=item.getString("custRequestItemSeqId")%></div></td>
                            <td><div class="tabletext"><%=item.getString("description")%></div></td>
                            <td><div class="tabletext"><%=item.getString("requiredByDate")%></div></td>
                            <td align="right"><div class="tabletext"><a href="#" class="buttontext">[Edit]</a></td>
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
                  <TD><div class="head2">Create New:</div</TD>
                </TR>
                <TR>
                  <TD>
                    <form method="post" action="<ofbiz:url>/createrequestitem</ofbiz:url>">
                      <input type="hidden" name="custRequestId" value="<%=custRequestId%>">
                      <input type="hidden" name="custRequestItemSeqId" value="<%=nextSeqId%>">
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <tr>
                          <td align="right">Response Required Date</td>
                          <td><input type="text" name="requiredByDate" style="font-size: small;" size="30"></td>
                        </tr>
                        <tr>
                          <td align="right">Max Amount</td>
                          <td><input type="text" name="maximumAmount" style="font-size: small;" size="10" value=""></td>
                        </tr>
                        <tr>
                          <td align="right">Quantity</td>
                          <td><input type="text" name="quantity" style="font-size: small;" size="6" value=""></td>
                        </tr>
                        <tr>
                          <td align="right">Description</td>
                          <td><input type="text" name="description" style="font-size: small;" size="50"></td>
                        </tr>
                        <tr>
                          <td align="right">Story</td>
                          <td><textarea name="story" style="font-size: small;" cols="60" rows="20"></textarea></td>
                        </tr>
                        <tr>
                          <td align="right"><input type="submit" style="font-size: small;" value="Create"></td>
                          <td>&nbsp;</td>
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
