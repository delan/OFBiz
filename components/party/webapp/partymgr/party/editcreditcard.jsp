<%
/**
 *  Title: Edit Credit Card Page
 *  Description: None
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
 *@created    Sep 1 2001
 *@version    1.0
 */
%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.*, org.ofbiz.party.contact.*" %>
<%@ page import="org.ofbiz.accounting.payment.*" %>
<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<ofbiz:object name="userLogin" property="userLogin" type="org.ofbiz.entity.GenericValue" />

<%
    String partyId = request.getParameter("party_id");
    if (partyId == null) partyId = request.getParameter("partyId");
    if (partyId == null) partyId = (String) request.getAttribute("partyId");

    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) donePage="viewprofile?partyId=" + partyId;    
%>

<%PaymentWorker.getPaymentMethodAndRelated(pageContext, partyId,
    "paymentMethod", "creditCard", "eftAccount", "paymentMethodId", "curContactMechId", "donePage", "tryEntity");%>

<%ContactMechWorker.getCurrentPostalAddress(pageContext, partyId,
    (String) pageContext.getAttribute("curContactMechId"), "curPartyContactMech", "curContactMech", 
    "curPostalAddress", "curPartyContactMechPurposes");%>

<%ContactMechWorker.getPartyPostalAddresses(pageContext, partyId, (String) pageContext.getAttribute("curContactMechId"), "postalAddressInfos");%>

<%if (!security.hasEntityPermission("PAY_INFO", "_VIEW", session) && pageContext.getAttribute("creditCard") != null && pageContext.getAttribute("paymentMethod") != null &&
      !userLogin.getString("partyId").equals(((GenericValue) pageContext.getAttribute("paymentMethod")).getString("partyId"))) {%>
  <p><h3>The credit card specified does not belong to you, you may not view or edit it.</h3></p>
&nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Back]</a>
<%} else {%>
    <ofbiz:unless name="creditCard">
      <p class="head1">Add New Credit Card</p>
      &nbsp;<a href='<ofbiz:url>/authview/<%=donePage%></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
      <%if (security.hasEntityPermission("PAY_INFO", "_CREATE", session)) {%>
        &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[Create]</a>
      <%}%>
      <form method="post" action='<ofbiz:url>/createCreditCard/<%=donePage%></ofbiz:url>' name="editcreditcardform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
    </ofbiz:unless>
    <ofbiz:if name="creditCard">
      <p class="head1">Edit Credit Card</p>
      &nbsp;<a href='<ofbiz:url>/authview/<%=donePage%></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
      <%if (security.hasEntityPermission("PAY_INFO", "_UPDATE", session)) {%>
          &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[Save]</a>
      <%}%>
      <form method="post" action='<ofbiz:url>/updateCreditCard/<%=donePage%></ofbiz:url>' name="editcreditcardform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <input type=hidden name='paymentMethodId' value='<ofbiz:print attribute="paymentMethodId"/>'>
    </ofbiz:if>

    <input type="hidden" name="partyId" value="<%=partyId%>">

    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Name on Card</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="15" maxlength="60" <ofbiz:inputvalue field="firstNameOnCard" entityAttr="creditCard" tryEntityAttr="tryEntity" fullattrs="true"/>>
        &nbsp;
        <input type="text" class="inputBox" size="15" maxlength="60" <ofbiz:inputvalue field="lastNameOnCard" entityAttr="creditCard" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Company Name on Card</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="30" maxlength="60" <ofbiz:inputvalue field="companyNameOnCard" entityAttr="creditCard" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Card Type</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select name="cardType" class="selectBox">
          <option><ofbiz:inputvalue field="cardType" entityAttr="creditCard" tryEntityAttr="tryEntity"/></option>
          <option></option>
          <option>Visa</option>
          <option value='MasterCard'>Master Card</option>
          <option value='AmericanExpress'>American Express</option>
          <option value='DinersClub'>Diners Club</option>
          <option>Discover</option>
          <option>EnRoute</option>
          <option>JCB</option>
        </select>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Card Number</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="20" maxlength="30" <ofbiz:inputvalue field="cardNumber" entityAttr="creditCard" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <%-- Should not be storing this
    <tr>
      <td width="26%" align="right" valign="top"><div class="tabletext">Card Security Code</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="5" maxlength="10" <ofbiz:inputvalue field="cardSecurityCode" entityAttr="creditCard" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    --%>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Expiration Date</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <%String expMonth = "";%>
        <%String expYear = "";%>
        <%if (pageContext.getAttribute("creditCard") != null) {%>
          <%String expDate = ((GenericValue) pageContext.getAttribute("creditCard")).getString("expireDate");%>
          <%if (expDate != null && expDate.indexOf('/') > 0){%>
            <%expMonth = expDate.substring(0,expDate.indexOf('/'));%>
            <%expYear = expDate.substring(expDate.indexOf('/')+1);%>
          <%}%>
        <%}%>
        <select name="expMonth" class="selectBox">
          <option><ofbiz:if name="tryEntity"><%=UtilFormatOut.checkNull(expMonth)%></ofbiz:if><ofbiz:unless name="tryEntity"><%=UtilFormatOut.checkNull(request.getParameter("expMonth"))%></ofbiz:unless></option>
          <option></option>
          <option>01</option>
          <option>02</option>
          <option>03</option>
          <option>04</option>
          <option>05</option>
          <option>06</option>
          <option>07</option>
          <option>08</option>
          <option>09</option>
          <option>10</option>
          <option>11</option>
          <option>12</option>
        </select>
        <select name="expYear" class="selectBox">
          <option><ofbiz:if name="tryEntity"><%=UtilFormatOut.checkNull(expYear)%></ofbiz:if><ofbiz:unless name="tryEntity"><%=UtilFormatOut.checkNull(request.getParameter("expYear"))%></ofbiz:unless></option>
          <option></option>
          <option>2004</option>
          <option>2005</option>
          <option>2006</option>
          <option>2007</option>
          <option>2008</option>
          <option>2009</option>
          <option>2010</option>
          <option>2011</option>
          <option>2012</option>
        </select>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Billing Address</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <%-- Removed because is confusing, can add but would have to come back here with all data populated as before...
        <a href="<ofbiz:url>/editcontactmech</ofbiz:url>" class="buttontext">
          [Create New Address]</a>&nbsp;&nbsp;
        --%>
        <%boolean hasCurrent = false;%>
        <table width="100%" border="0" cellpadding="1">
        <ofbiz:if name="curPostalAddress">
          <%hasCurrent = true;%>
          <tr>
            <td align="right" valign="top" width="1%">
              <INPUT type=radio name='contactMechId' value='<ofbiz:print attribute="curContactMechId"/>' checked>
            </td>
            <td align="left" valign="top" width="80%">
              <div class="tabletext"><b>Use Current Address:</b></div>
              <ofbiz:iterator name="curPartyContactMechPurpose" property="curPartyContactMechPurposes">
                <%GenericValue curContactMechPurposeType = curPartyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType");%>
                <div class="tabletext">
                  <b><%=curContactMechPurposeType.getString("description")%></b>
                  <%if (curPartyContactMechPurpose.get("thruDate") != null) {%>
                    (Expire:<%=UtilDateTime.toDateTimeString(curPartyContactMechPurpose.getTimestamp("thruDate"))%>)
                  <%}%>
                </div>
              </ofbiz:iterator>
              <div class="tabletext">
                <ofbiz:entityfield attribute="curPostalAddress" field="toName" prefix="<b>To:</b> " suffix="<br>"/>
                <ofbiz:entityfield attribute="curPostalAddress" field="attnName" prefix="<b>Attn:</b> " suffix="<br>"/>
                <ofbiz:entityfield attribute="curPostalAddress" field="address1"/><br>
                <ofbiz:entityfield attribute="curPostalAddress" field="address2" prefix="" suffix="<br>"/>
                <ofbiz:entityfield attribute="curPostalAddress" field="city"/>,
                <ofbiz:entityfield attribute="curPostalAddress" field="stateProvinceGeoId"/>
                <ofbiz:entityfield attribute="curPostalAddress" field="postalCode"/>
                <ofbiz:entityfield attribute="curPostalAddress" field="countryGeoId" prefix="<br>" suffix=""/>
              </div>
              <div class="tabletext">(Updated:&nbsp;<ofbiz:entityfield attribute="curPartyContactMech" field="fromDate"/>)</div>
              <ofbiz:entityfield attribute="curPartyContactMech" field="thruDate" prefix="<div class='tabletext'><b>Delete:&nbsp;" suffix="</b></div>"/>
            </td>
          </tr>
        </ofbiz:if>
        <ofbiz:unless name="curPostalAddress">
           <%-- <tr>
            <td align="left" valign="top" colspan='2'>
              <div class="tabletext">No Billing Address Selected</div>
            </td>
          </tr> --%>
        </ofbiz:unless>
          <%-- is confusing
          <tr>
            <td align="left" valign="top" colspan='2'>
              <div class="tabletext"><b>Select a New Billing Address:</b></div>
            </td>
          </tr>
          --%>
          <ofbiz:iterator name="postalAddressInfo" property="postalAddressInfos" type="java.util.Map" expandMap="true">
            <tr>
              <td align="right" valign="top" width="1%">
                <INPUT type=radio name='contactMechId' value='<ofbiz:entityfield attribute="contactMech" field="contactMechId"/>'>
              </td>
              <td align="left" valign="top" width="80%">
                <ofbiz:iterator name="partyContactMechPurpose" property="partyContactMechPurposes">
                    <%GenericValue contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType");%>
                    <div class="tabletext">
                      <b><%=contactMechPurposeType.getString("description")%></b>
                      <ofbiz:entityfield attribute="partyContactMechPurpose" field="thruDate" prefix="(Expire:" suffix=")"/>
                    </div>
                </ofbiz:iterator>
                <div class="tabletext">
                  <ofbiz:entityfield attribute="postalAddress" field="toName" prefix="<b>To:</b> " suffix="<br>"/>
                  <ofbiz:entityfield attribute="postalAddress" field="attnName" prefix="<b>Attn:</b> " suffix="<br>"/>
                  <ofbiz:entityfield attribute="postalAddress" field="address1"/><br>
                  <ofbiz:entityfield attribute="postalAddress" field="address2" prefix="" suffix="<br>"/>
                  <ofbiz:entityfield attribute="postalAddress" field="city"/>,
                  <ofbiz:entityfield attribute="postalAddress" field="stateProvinceGeoId"/>
                  <ofbiz:entityfield attribute="postalAddress" field="postalCode"/>
                  <ofbiz:entityfield attribute="postalAddress" field="countryGeoId" prefix="<br>" suffix=""/>
                </div>
                <div class="tabletext">(Updated:&nbsp;<ofbiz:entityfield attribute="partyContactMech" field="fromDate"/>)</div>
                <ofbiz:entityfield attribute="partyContactMech" field="thruDate" prefix="<div class='tabletext'><b>Delete:&nbsp;" suffix="</b></div>"/>
              </td>
            </tr>
          </ofbiz:iterator>
          <ofbiz:unless name="postalAddressInfos" size="0">
              <ofbiz:unless name="curContactMech">
                  <tr><td colspan='2'><div class="tabletext">No contact information on file.</div></td></tr>
              </ofbiz:unless>
          </ofbiz:unless>
          <tr>
            <td align="right" valigh="top" width="1%">
              <input type="radio" name="contactMechId" value="_NEW_" <%if (!hasCurrent) {%>checked<%}%>>
            </td>
            <td align="left" valign="middle" width="80%">
              <span class="tabletext">Create a new billing address for this credit card.</span>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  </form>

  &nbsp;<a href='<ofbiz:url>/authview/<%=donePage%></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
  <ofbiz:unless name="creditCard">
      <%if (security.hasEntityPermission("PAY_INFO", "_CREATE", session)) {%>
        &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[Create]</a>
      <%}%>
  </ofbiz:unless>
  <ofbiz:if name="creditCard">
      <%if (security.hasEntityPermission("PAY_INFO", "_UPDATE", session)) {%>
          &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[Save]</a>
      <%}%>
  </ofbiz:if>
<%}%>
