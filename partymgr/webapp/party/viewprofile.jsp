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
 * @created    May 22 2001
 * @version    1.0
 */
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

    List userLogins = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));
    if (userLogins != null && userLogins.size() > 0) pageContext.setAttribute("userLogins", userLogins);

    List partyRoles = delegator.findByAnd("RoleTypeAndParty", UtilMisc.toMap("partyId", partyId));
    if (partyRoles != null && partyRoles.size() > 0) pageContext.setAttribute("partyRoles", partyRoles);

    List roles = delegator.findAll("RoleType", UtilMisc.toList("description", "roleTypeId"));
    if (roles != null) pageContext.setAttribute("roles", roles);

    List relateTypes = delegator.findAll("PartyRelationshipType", UtilMisc.toList("description", "partyRelationshipTypeId"));
    if (relateTypes != null) pageContext.setAttribute("relateTypes", relateTypes);

    List notes = delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId), UtilMisc.toList("-noteDateTime"));
    if (notes != null && notes.size() > 0) pageContext.setAttribute("notes", notes);

    GenericValue avsOverride = delegator.findByPrimaryKey("PartyIcsAvsOverride", UtilMisc.toMap("partyId", partyId));
    if (avsOverride != null) pageContext.setAttribute("avsOverride", avsOverride);

    PartyWorker.getPartyOtherValues(pageContext, partyId, "party", "lookupPerson", "lookupGroup");
    boolean showOld = "true".equals(request.getParameter("SHOW_OLD"));
    pageContext.setAttribute("showOld", new Boolean(showOld));
    ContactMechWorker.getPartyContactMechValueMaps(pageContext, partyId, showOld, "partyContactMechValueMaps");
    PaymentWorker.getPartyPaymentMethodValueMaps(pageContext, partyId, showOld, "paymentMethodValueMaps");
%>
<%EntityField entityField = new EntityField(pageContext);%>

<script language='JavaScript'>
    function setNowFromDate(formName) { eval('document.' + formName + '.fromDate.value="<%=UtilDateTime.nowTimestamp().toString()%>"'); }
</script>

<br>
<ofbiz:if name="party">
<%-- Main Heading --%>
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
      <a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="tabButtonSelected">Profile</a>
      <a href="<ofbiz:url>/viewroles</ofbiz:url>" class="tabButton">Roles</a>
      <a href="<ofbiz:url>/viewrelationships</ofbiz:url>" class="tabButton">Relationships</a>
      </div>

      <ofbiz:if name="showOld">
        <a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="buttontext">[Hide Old]</a>&nbsp;&nbsp;
      </ofbiz:if>
      <ofbiz:unless name="showOld">
        <a href="<ofbiz:url>/viewprofile?SHOW_OLD=true</ofbiz:url>" class="buttontext">[Show Old]</a>&nbsp;&nbsp;
      </ofbiz:unless>
      <% if(security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)) { %>
        <a href="/ordermgr/control/orderlist?partyId=<%=partyId%>" target="ordermgr" class="buttontext">[Orders]</a>&nbsp;&nbsp;
      <% } %>
      <% if(security.hasEntityPermission("ORDERMGR", "_CREATE", session)) { %>
        <a href="/ordermgr/control/salesentry?partyId=<%=partyId%>" target="ordermgr" class="buttontext">[New Order]</a>&nbsp;&nbsp;
      <% } %>      
    </td>
  </tr>
</table>
<br>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <ofbiz:if name="lookupPerson">
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;Personal Information</div>
            </td>
            <%if(security.hasEntityPermission("PARTYMGR", "_UPDATE", session)) {%>
            <td valign="middle" align="right">
              <a href="<ofbiz:url>/editperson</ofbiz:url>" class="lightbuttontext">
              [<ofbiz:if name="lookupPerson">Update</ofbiz:if>]</a>&nbsp;&nbsp;
            </td>
            <%}%>
          </ofbiz:if>
          <ofbiz:if name="lookupGroup">
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;Party Group Information</div>
            </td>
            <%if(security.hasEntityPermission("PARTYMGR", "_UPDATE", session)) {%>
            <td valign="middle" align="right">
              <a href="<ofbiz:url>/editpartygroup</ofbiz:url>" class="lightbuttontext">
              [<ofbiz:if name="lookupGroup">Update</ofbiz:if>]</a>&nbsp;&nbsp;
            </td>
            <%}%>
          </ofbiz:if>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
<ofbiz:if name="lookupPerson">
  <table width="100%" border="0" cellpadding="0" cellspacing='0'>
    <tr>
      <td align="right" width="10%"><div class="tabletext"><b>Name</b></div></td>
      <td width="5">&nbsp;</td>
      <td align="left" width="90%">
        <div class="tabletext">
          <%entityField.run("lookupPerson", "personalTitle");%>
          <%entityField.run("lookupPerson", "firstName");%>
          <%entityField.run("lookupPerson", "middleName");%>
          <%entityField.run("lookupPerson", "lastName");%>
          <%entityField.run("lookupPerson", "suffix");%>
        </div>
      </td>
    </tr>
    <%entityField.run("lookupPerson", "nickname", "<tr><td align=right nowrap><div class='tabletext'><b>Nickname</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "gender", "<tr><td align=right nowrap><div class='tabletext'><b>Gender</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "birthDate", "<tr><td align=right nowrap><div class='tabletext'><b>Birth Date</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "height", "<tr><td align=right nowrap><div class='tabletext'><b>Height</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "weight", "<tr><td align=right nowrap><div class='tabletext'><b>Weight</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "mothersMaidenName", "<tr><td align=right nowrap><div class='tabletext'><b>Mothers Maiden Name</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "maritalStatus", "<tr><td align=right nowrap><div class='tabletext'><b>Marital Status</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "socialSecurityNumber", "<tr><td align=right nowrap><div class='tabletext'><b>Social Security Number</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "passportNumber", "<tr><td align=right nowrap><div class='tabletext'><b>Passport Number</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "passportExpireDate", "<tr><td align=right nowrap><div class='tabletext'><b>Passport Expire</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "totalYearsWorkExperience", "<tr><td align=right nowrap><div class='tabletext'><b>Years Work</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
    <%entityField.run("lookupPerson", "comments", "<tr><td align=right nowrap><div class='tabletext'><b>Comments</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>", "</div></td></tr>");%>
  </table>
</ofbiz:if>
<ofbiz:unless name="lookupPerson">
  <ofbiz:if name="lookupGroup">
    <div class="tabletext"><%entityField.run("lookupGroup", "groupName");%></div>
  </ofbiz:if>
  <ofbiz:unless name="lookupGroup">
    <div class="tabletext">Information Not Found</div>
  </ofbiz:unless>
</ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<%-- ============================================================= --%>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Contact Information</div>
          </td>
          <%if(security.hasEntityPermission("PARTYMGR", "_CREATE", session)) {%>
          <td valign="middle" align="right">
              <a href="<ofbiz:url>/editcontactmech</ofbiz:url>" class="lightbuttontext">
              [Create New]</a>&nbsp;&nbsp;
          </td>
          <%}%>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <ofbiz:if name="partyContactMechValueMaps" size="0">
    <table width="100%" border="0" cellpadding="0">
      <tr align=left valign=bottom>
        <th>Contact&nbsp;Type</th>
        <th width="5">&nbsp;</th>
        <th>Information</th>
        <th colspan='2'>Soliciting&nbsp;OK?</th>
        <th>&nbsp;</th>
      </tr>
      <ofbiz:iterator name="partyContactMechValueMap" property="partyContactMechValueMaps" type="java.util.Map" expandMap="true">
        <%GenericValue contactMech = (GenericValue) pageContext.getAttribute("contactMech");%>
          <tr><td colspan="7"><hr class='sepbar'></td></tr>
          <tr>
            <td align="right" valign="top" width="10%">
              <div class="tabletext">&nbsp;<b><%entityField.run("contactMechType", "description");%></b></div>
            </td>
            <td width="5">&nbsp;</td>
            <td align="left" valign="top" width="80%">
              <ofbiz:iterator name="partyContactMechPurpose" property="partyContactMechPurposes">
                  <%GenericValue contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType");%>
                    <div class="tabletext">
                      <%if (contactMechPurposeType != null) {%>
                        <b><%=contactMechPurposeType.getString("description")%></b>
                      <%} else {%>
                        <b>Purpose Type not found with ID: "<%=partyContactMechPurpose.getString("contactMechPurposeTypeId")%>"</b>
                      <%}%>
                      <%entityField.run("partyContactMechPurpose", "thruDate", "(Expire:", ")");%>
                    </div>
              </ofbiz:iterator>
              <%if ("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <%entityField.run("postalAddress", "toName", "<b>To:</b> ", "<br>");%>
                    <%entityField.run("postalAddress", "attnName", "<b>Attn:</b> ", "<br>");%>
                    <%entityField.run("postalAddress", "address1");%><br>
                    <%entityField.run("postalAddress", "address2", "", "<br>");%>
                    <%entityField.run("postalAddress", "city");%>,
                    <%entityField.run("postalAddress", "stateProvinceGeoId");%>
                    <%entityField.run("postalAddress", "postalCode");%>
                    <%entityField.run("postalAddress", "countryGeoId", "<br>", "");%>
                  </div>
                  <%GenericValue postalAddress = (GenericValue) pageContext.getAttribute("postalAddress");%>
                  <%if (postalAddress != null && (UtilValidate.isEmpty(postalAddress.getString("countryGeoId")) || postalAddress.getString("countryGeoId").equals("USA"))) {%>
                    <%String addr1 = UtilFormatOut.checkNull(postalAddress.getString("address1"));%>
                    <%if (addr1.indexOf(' ') > 0) {%>
                      <%String addressNum = addr1.substring(0, addr1.indexOf(' '));%>
                      <%String addressOther = addr1.substring(addr1.indexOf(' ')+1);%>
                      <a target='_blank' href='http://www.whitepages.com/find_person_results.pl?fid=a&s_n=<%=addressNum%>&s_a=<%=addressOther%>&c=<%=UtilFormatOut.checkNull(postalAddress.getString("city"))%>&s=<%=UtilFormatOut.checkNull(postalAddress.getString("stateProvinceGeoId"))%>&x=29&y=18' class='buttontext'>(lookup:whitepages.com)</a>
                    <%}%>
                  <%}%>
              <%} else if ("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <%entityField.run("telecomNumber", "countryCode");%>
                    <%entityField.run("telecomNumber", "areaCode", "", "-");%><%entityField.run("telecomNumber", "contactNumber");%>
                    <%entityField.run("partyContactMech", "extension", "ext&nbsp;", "");%>
                    <%GenericValue telecomNumber = (GenericValue) pageContext.getAttribute("telecomNumber");%>
                    <%if (telecomNumber != null && (UtilValidate.isEmpty(telecomNumber.getString("countryCode")) || telecomNumber.getString("countryCode").equals("011"))) {%>
                      <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=<%=UtilFormatOut.checkNull(telecomNumber.getString("areaCode"))%>&telephone=<%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                      <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=<%=UtilFormatOut.checkNull(telecomNumber.getString("areaCode"))%>&s=&p=<%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
                    <%}%>
                  </div>
              <%} else if ("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <%entityField.run("contactMech", "infoString");%>
                    <a href='mailto:<%entityField.run("contactMech", "infoString");%>' class='buttontext'>(send&nbsp;email)</a>
                  </div>
              <%} else if ("WEB_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <%entityField.run("contactMech", "infoString");%>
                    <%String openAddress = UtilFormatOut.checkNull(contactMech.getString("infoString"));%>
                    <%if(!openAddress.startsWith("http") && !openAddress.startsWith("HTTP")) openAddress = "http://" + openAddress;%>
                    <a target='_blank' href='<%=openAddress%>' class='buttontext'>(open&nbsp;page&nbsp;in&nbsp;new&nbsp;window)</a>
                  </div>
              <%} else {%>
                  <div class="tabletext">
                    <%entityField.run("contactMech", "infoString");%>
                  </div>
              <%}%>
              <div class="tabletext">(Updated:&nbsp;<%entityField.run("partyContactMech", "fromDate");%>)</div>
              <%entityField.run("partyContactMech", "thruDate", "<div class='tabletext'><b>Effective Thru:&nbsp;", "</b></div>");%>
            </td>
            <td align="center" valign="top" nowrap width="1%"><div class="tabletext"><b>(<%entityField.run("partyContactMech", "allowSolicitation");%>)</b></div></td>
            <td width="5">&nbsp;</td>
            <%if(security.hasEntityPermission("PARTYMGR", "_UPDATE", session)) {%>
            <td align="right" valign="top" nowrap width="1%">
              <div><a href='<ofbiz:url>/editcontactmech?contactMechId=<%entityField.run("contactMech", "contactMechId");%></ofbiz:url>' class="buttontext">
              [Update]</a>&nbsp;</div>
            </td>
            <%}%>
            <%if(security.hasEntityPermission("PARTYMGR", "_DELETE", session)) {%>
            <td align="right" valign="top" width="1%">
              <div><a href='<ofbiz:url>/deleteContactMech/viewprofile?contactMechId=<%entityField.run("contactMech", "contactMechId");%>&partyId=<%=partyId%></ofbiz:url>' class="buttontext">
              [Expire]</a>&nbsp;&nbsp;</div>
            </td>
            <%}%>
          </tr>
      </ofbiz:iterator>
    </table>
  </ofbiz:if>
  <ofbiz:unless name="partyContactMechValueMaps" size="0">
    <div class="tabletext">No contact information on file.</div>
  </ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<%-- ============================================================= --%>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Payment Method Information</div>
          </td>
          <td valign="middle" align="right">&nbsp;
              <%if (security.hasEntityPermission("PAY_INFO", "_CREATE", session)) {%>
                <a href="<ofbiz:url>/editcreditcard</ofbiz:url>" class="lightbuttontext">
                [Create New Credit Card]</a>&nbsp;
                <a href="<ofbiz:url>/editeftaccount</ofbiz:url>" class="lightbuttontext">
                [Create New EFT Account]</a>&nbsp;&nbsp;
              <%}%>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
              <ofbiz:if name="paymentMethodValueMaps" size="0">
              <table width="100%" border="0" cellpadding="1">
                <tr>
                  <td align="left">
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <ofbiz:iterator name="paymentMethodValueMap" property="paymentMethodValueMaps" type="java.util.Map" expandMap="true">
                            <%GenericValue paymentMethod = (GenericValue) pageContext.getAttribute("paymentMethod");%>
                            <tr>
                              <%if ("CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {%>
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        Credit Card: <%entityField.run("creditCard", "nameOnCard");%> - 
                                        <%if (security.hasEntityPermission("PAY_INFO", "_VIEW", session)) {%>
                                            <%EntityField.run("creditCard", "cardType", pageContext);%>
                                            <%EntityField.run("creditCard", "cardNumber", pageContext);%>
                                            <%EntityField.run("creditCard", "expireDate", pageContext);%>
                                        <%} else {%>
                                            <%=ContactHelper.formatCreditCard((GenericValue) pageContext.getAttribute("creditCard"))%>
                                        <%}%>
                                      </b>
                                      (Updated:&nbsp;<%entityField.run("paymentMethod", "fromDate");%>)
                                      <%entityField.run("paymentMethod", "thruDate", "<b>(Effective Thru:&nbsp;", ")</b>");%>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <%if (security.hasEntityPermission("PAY_INFO", "_UPDATE", session)) {%>
                                        <div><a href='<ofbiz:url>/editcreditcard?paymentMethodId=<%entityField.run("paymentMethod", "paymentMethodId");%></ofbiz:url>' class="buttontext">
                                        [Update]</a></div>
                                    <%}%>
                                  </td>
                              <%} else if ("EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {%>
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        EFT Account: <%entityField.run("eftAccount", "nameOnAccount");%> - <%entityField.run("eftAccount", "bankName", "Bank: ", "");%> <%entityField.run("eftAccount", "accountNumber", "Account #: ", "");%>
                                      </b>
                                      (Updated:&nbsp;<%entityField.run("paymentMethod", "fromDate");%>)
                                      <%entityField.run("paymentMethod", "thruDate", "<b>(Effective Thru:&nbsp;", ")</b>");%>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <%if (security.hasEntityPermission("PAY_INFO", "_UPDATE", session)) {%>
                                        <div><a href='<ofbiz:url>/editeftaccount?paymentMethodId=<%entityField.run("paymentMethod", "paymentMethodId");%></ofbiz:url>' class="buttontext">
                                        [Update]</a></div>
                                    <%}%>
                                  </td>
                              <%}%>
                              <td align="right" valign="top" width='1%'>
                                <%if (security.hasEntityPermission("PAY_INFO", "_DELETE", session)) {%>
                                    <div><a href='<ofbiz:url>/deletePaymentMethod/viewprofile?paymentMethodId=<%entityField.run("paymentMethod", "paymentMethodId");%></ofbiz:url>' class="buttontext">
                                    [Expire]</a></div>
                                <%}%>
                              </td>
                            </tr>
                        </ofbiz:iterator>
                      </table>

                  </td>
                </tr>
              </table>
              </ofbiz:if>
              <ofbiz:unless name="paymentMethodValueMaps" size="0">
                <div class="tabletext">No payment method information on file.</div>
              </ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Cybersource AVS Override</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td>
              <ofbiz:if name="avsOverride">
                <div class="tabletext"><b>AVS String:</b>&nbsp;<%=avsOverride.getString("avsDeclineString")%></div>
              </ofbiz:if>
              <ofbiz:unless name="avsOverride">
                <div class="tabletext"><b>AVS String:</b>&nbsp;Global</div>
              </ofbiz:unless>
          </td>
          <%if(security.hasEntityPermission("PARTYMGR", "_UPDATE", session)) {%>
          <td align="right" valign="top" width="1%">
            <a href="<ofbiz:url>/editAvsOverride</ofbiz:url>" class="buttontext">[Edit]</a>
          </td>          
          <ofbiz:if name="avsOverride">
            <td align="right" valign="top" width="1%">
              <a href="<ofbiz:url>/resetAvsOverride?partyId=<%=avsOverride.getString("partyId")%></ofbiz:url>" class="buttontext">[Reset]</a>
            </td>
          </ofbiz:if>
          <%}%>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;User Name(s)</div>
          </td>
          <%if(security.hasEntityPermission("PARTYMGR", "_CREATE", session)) {%>
          <td valign="middle" align="right">&nbsp;
            <a href="<ofbiz:url>/createnewlogin</ofbiz:url>" class="lightbuttontext">[Create New]</a>&nbsp;&nbsp;
          </td>
          <%}%>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <ofbiz:if name="userLogins">
            <table width="100%" border="0" cellpadding="1">
              <ofbiz:iterator name="userUserLogin" property="userLogins">
              <tr>
                <td align="right" valign="top" width="10%" nowrap><div class="tabletext"><b>User Name</b></div></td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="40%"><div class="tabletext"><%entityField.run("userUserLogin", "userLoginId");%></div></td>
                <td align="left" valign="top" width="30%">
                  <div class="tabletext">
                    <%
                       String enabled = "ENABLED";
                       GenericValue thisUL = null;
                       try {
                           thisUL = (GenericValue) pageContext.findAttribute("userUserLogin");
                       } catch (Exception e) {}
                       if (thisUL != null && thisUL.get("enabled") != null && thisUL.getString("enabled").equals("N")) {
                           enabled = "DISABLED - " + thisUL.getString("disabledDateTime");
                       }
                    %>
                    <%=enabled%>
                  </div>
                </td>
                <td align="right" valign="top" width="20%">
                  <%if(security.hasEntityPermission("PARTYMGR", "_CREATE", session)) {%>
                      <a href="<ofbiz:url>/editlogin?userlogin_id=<%entityField.run("userUserLogin", "userLoginId");%></ofbiz:url>" class="buttontext">[Edit]</a>&nbsp;
                  <%}%>
                  <%if (security.hasEntityPermission("SECURITY", "_VIEW", session)) {%>
                      <a href="<ofbiz:url>/EditUserLoginSecurityGroups?userLoginId=<%entityField.run("userUserLogin", "userLoginId");%></ofbiz:url>" class="buttontext">[SecurityGroups]</a>&nbsp;
                  <%}%>
                </td>
              </tr>
              </ofbiz:iterator>
            </table>
            </ofbiz:if>
            <ofbiz:unless name="userLogins">
              <div class="tabletext">No UserLogin(s) found for this party.</div>
            </ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<%-- Party Notes --%>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Notes</div>
          </td>
          <%if(security.hasEntityPermission("PARTYMGR", "_NOTE", session)) {%>
          <td valign="middle" align="right">&nbsp;
            <a href="<ofbiz:url>/createnewnote</ofbiz:url>" class="lightbuttontext">[Create New]</a>&nbsp;&nbsp;
          </td>
          <%}%>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <ofbiz:if name="notes">
            <table width="100%" border="0" cellpadding="1">
              <ofbiz:iterator name="noteRef" property="notes">
                <tr>
                  <td align="left" valign="top" width="35%">
                    <div class="tabletext">&nbsp;<b>By: </b><ofbiz:entityfield attribute="noteRef" field="firstName"/>&nbsp;<ofbiz:entityfield attribute="noteRef" field="lastName"/></div>
                    <div class="tabletext">&nbsp;<b>At: </b><ofbiz:entityfield attribute="noteRef" field="noteDateTime"/></div>
                  </td>
                  <td align="left" valign="top" width="65%">
                    <div class="tabletext"><ofbiz:entityfield attribute="noteRef" field="noteInfo"/></div>
                  </td>
                </tr>
                <ofbiz:iteratorHasNext>
                  <tr><td colspan="2"><hr class="sepbar"></td></tr>
                </ofbiz:iteratorHasNext>
              </ofbiz:iterator>
            </table>
            </ofbiz:if>
            <ofbiz:unless name="notes">
              <div class="tabletext">No notes for this party.</div>
            </ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

</ofbiz:if>
<ofbiz:unless name="party">
    No party found with the partyId of: <%=partyId%>
</ofbiz:unless>
<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
