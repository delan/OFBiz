<%
/**
 *  Title: Edit EFT Account Page
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
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*, org.ofbiz.commonapp.party.contact.*" %>
<%@ page import="org.ofbiz.commonapp.accounting.payment.*" %>
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<ofbiz:object name="userLogin" property="userLogin" type="org.ofbiz.core.entity.GenericValue" />  

<%PaymentWorker.getPaymentMethodAndRelated(pageContext, userLogin.getString("partyId"), 
    "paymentMethod", "creditCard", "eftAccount", "paymentMethodId", "curContactMechId", "donePage", "tryEntity");%>

<%
    GenericValue efta = (GenericValue) pageContext.getAttribute("eftAccount");
    System.out.println("EFT Account: " + efta);
    System.out.println("Try Entity: " + pageContext.getAttribute("tryEntity"));
%>

<%ContactMechWorker.getCurrentPostalAddress(pageContext, userLogin.getString("partyId"), 
    (String) pageContext.getAttribute("curContactMechId"), "curPartyContactMech", "curContactMech", 
    "curPostalAddress", "curPartyContactMechPurposes");%>

<%ContactMechWorker.getPartyPostalAddresses(pageContext, userLogin.getString("partyId"), (String) pageContext.getAttribute("curContactMechId"), "postalAddressInfos");%>

<br>
<%if (!security.hasEntityPermission("PARTYMGR", "_VIEW", session) && pageContext.getAttribute("eftAccount") != null && pageContext.getAttribute("paymentMethod") != null &&
      !userLogin.getString("partyId").equals(((GenericValue) pageContext.getAttribute("paymentMethod")).getString("partyId"))) {%>
  <p><h3>The EFT Account specified does not belong to you, you may not view or edit it.</h3></p>
&nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Back]</a>
<%} else {%>
    <ofbiz:unless name="eftAccount">
      <p class="head1">Add New EFT Account</p>
      &nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
      &nbsp;<a href="javascript:document.editeftaccountform.submit()" class="buttontext">[Save]</a>
      <form method="post" action='<ofbiz:url>/createEftAccount?DONE_PAGE=<ofbiz:print attribute="donePage"/></ofbiz:url>' name="editeftaccountform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
    </ofbiz:unless>
    <ofbiz:if name="eftAccount">
      <p class="head1">Edit EFT Account</p>
      &nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
      &nbsp;<a href="javascript:document.editeftaccountform.submit()" class="buttontext">[Save]</a>
      <form method="post" action='<ofbiz:url>/updateEftAccount?DONE_PAGE=<ofbiz:print attribute="donePage"/></ofbiz:url>' name="editeftaccountform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <input type=hidden name='paymentMethodId' value='<ofbiz:print attribute="paymentMethodId"/>'>
    </ofbiz:if>

    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Name on Account</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="nameOnAccount" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Company Name on Account</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="companyNameOnAccount" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Bank Name</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="bankName" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Routing Number</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="10" maxlength="30" <ofbiz:inputvalue field="routingNumber" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Account Type</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select name="accountType">
          <option><ofbiz:inputvalue field="accountType" entityAttr="eftAccount" tryEntityAttr="tryEntity"/></option>
          <option></option>
          <option>Checking</option>
          <option>Savings</option>
        </select>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Account Number</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="20" maxlength="40" <ofbiz:inputvalue field="accountNumber" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
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
        <table width="100%" border="0" cellpadding="1">
        <ofbiz:if name="curPostalAddress">
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
        </table>
      </td>
    </tr>
  </table>
  </form>

  &nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
  &nbsp;<a href="javascript:document.editeftaccountform.submit()" class="buttontext">[Save]</a>
<%}%>
