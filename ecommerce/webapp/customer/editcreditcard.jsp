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

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<% pageContext.setAttribute("PageName", "editcreditcard"); %>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%
  boolean useValues = true;
  if(request.getAttribute("ERROR_MESSAGE") != null) useValues = false;

  String donePage = request.getParameter("DONE_PAGE");
  if(donePage == null || donePage.length() <= 0) donePage="viewprofile";

  String creditCardId = request.getParameter("CREDIT_CARD_ID");
  if(request.getAttribute("CREDIT_CARD_ID") != null) creditCardId = (String)request.getAttribute("CREDIT_CARD_ID");

  Iterator partyContactMechIterator = UtilMisc.toIterator(EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId")), null)));
  GenericValue creditCard = delegator.findByPrimaryKey("CreditCardInfo", UtilMisc.toMap("creditCardId", creditCardId));
%>

<%if(!security.hasPermission("USER_ADMIN", session) && creditCard != null && 
     !userLogin.getString("partyId").equals(creditCard.getString("partyId"))){%>
  <p><h3>The credit card specified does not belong to you, you may not view or edit it.</h3></p>
&nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Back]</a>
<%}else{%>
    <%if(creditCard == null){%>
      <%useValues = false;%>
      <p class="head1">Add New Credit Card</p>
      &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Done/Cancel]</a>
      &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[Save]</a>
      <form method="post" action="<ofbiz:url>/updatecreditcard?DONE_PAGE=<%=donePage%></ofbiz:url>" name="editcreditcardform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <input type=hidden name="UPDATE_MODE" value="CREATE">
    <%}else{%>
      <p class="head1">Edit Credit Card</p>
      &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Done/Cancel]</a>
      &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[Save]</a>
      <form method="post" action="<ofbiz:url>/updatecreditcard?DONE_PAGE=<%=donePage%></ofbiz:url>" name="editcreditcardform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <input type=hidden name="CREDIT_CARD_ID" value="<%=creditCardId%>">
        <input type=hidden name="UPDATE_MODE" value="UPDATE">
    <%}%>

    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Name on Card</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" name="CC_NAME_ON_CARD" value="<%=UtilFormatOut.checkNull(useValues?creditCard.getString("nameOnCard"):request.getParameter("CC_NAME_ON_CARD"))%>" size="30" maxlength="60">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Company Name on Card</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" name="CC_COMPANY_NAME_ON_CARD" value="<%=UtilFormatOut.checkNull(useValues?creditCard.getString("companyNameOnCard"):request.getParameter("CC_COMPANY_NAME_ON_CARD"))%>" size="30" maxlength="60">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Card Type</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select name="CC_CARD_TYPE">
          <option><%=UtilFormatOut.checkNull(useValues?creditCard.getString("cardType"):request.getParameter("CC_CARD_TYPE"))%></option>
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
        <input type="text" name="CC_CARD_NUMBER" value="<%=UtilFormatOut.checkNull(useValues?creditCard.getString("cardNumber"):request.getParameter("CC_CARD_NUMBER"))%>" size="20" maxlength="30">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Card Security Code</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" name="CC_CARD_SECURITY_CODE" value="<%=UtilFormatOut.checkNull(useValues?creditCard.getString("cardSecurityCode"):request.getParameter("CC_CARD_SECURITY_CODE"))%>" size="5" maxlength="10">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Expiration Date</div></td>        
      <td width="5">&nbsp;</td>
      <td width="74%">
        <%String expMonth = "";%>
        <%String expYear = "";%>
        <%if(creditCard != null){%>
          <%String expDate = creditCard.getString("expireDate");%>
          <%if(expDate != null && expDate.indexOf('/') > 0){%>
            <%expMonth = expDate.substring(0,expDate.indexOf('/'));%>
            <%expYear = expDate.substring(expDate.indexOf('/')+1);%>
          <%}%>
        <%}%>
        <select name="CC_EXPIRE_DATE_MONTH">
          <option><%=UtilFormatOut.checkNull(useValues?expMonth:request.getParameter("CC_EXPIRE_DATE_MONTH"))%></option>
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
        <select name="CC_EXPIRE_DATE_YEAR">
          <option><%=UtilFormatOut.checkNull(useValues?expYear:request.getParameter("CC_EXPIRE_DATE_YEAR"))%></option>
          <option></option>
          <option>2001</option>
          <option>2002</option>
          <option>2003</option>
          <option>2004</option>
          <option>2005</option>
          <option>2006</option>
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
        <table width="100%" border="0" cellpadding="1">
        <%
          String curContactMechId = UtilFormatOut.checkNull(useValues?creditCard.getString("contactMechId"):request.getParameter("CC_CONTACT_MECH_ID"));
          Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", curContactMechId), null));
          GenericValue curPartyContactMech = EntityUtil.getFirst(partyContactMechs);

          GenericValue curContactMech = curPartyContactMech!=null?curPartyContactMech.getRelatedOne("ContactMech"):null;
          GenericValue curPostalAddress = curContactMech!=null?curContactMech.getRelatedOne("PostalAddress"):null;
        %>
        <%if(curPostalAddress != null){%>
          <%Iterator curPartyContactMechPurposesIter = UtilMisc.toIterator(EntityUtil.filterByDate(curPartyContactMech.getRelated("PartyContactMechPurpose")));%>
          <tr>
            <td align="right" valign="top" width="1%">
              <INPUT type=radio name='CC_CONTACT_MECH_ID' value='<%=curContactMech.getString("contactMechId")%>' checked>
            </td>
            <td align="left" valign="top" width="80%">
              <div class="tabletext"><b>Use Current Address:</b></div>
              <%while(curPartyContactMechPurposesIter != null && curPartyContactMechPurposesIter.hasNext()){%>
                <%GenericValue curPartyContactMechPurpose = (GenericValue)curPartyContactMechPurposesIter.next();%>
                <%GenericValue curContactMechPurposeType = curPartyContactMechPurpose.getRelatedOne("ContactMechPurposeType");%>
                  <div class="tabletext">
                    <b><%=curContactMechPurposeType.getString("description")%></b>
                    <%if(curPartyContactMechPurpose.get("thruDate") != null){%>
                      (Expire:<%=UtilDateTime.toDateTimeString(curPartyContactMechPurpose.getTimestamp("thruDate"))%>)
                    <%}%>
                  </div>
              <%}%>
              <div class="tabletext">
                <%=UtilFormatOut.ifNotEmpty(curPostalAddress.getString("toName"), "<b>To:</b> ", "<br>")%>
                <%=UtilFormatOut.ifNotEmpty(curPostalAddress.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
                <%=UtilFormatOut.checkNull(curPostalAddress.getString("address1"))%><br>
                <%=UtilFormatOut.ifNotEmpty(curPostalAddress.getString("address2"), "", "<br>")%>
                <%=UtilFormatOut.checkNull(curPostalAddress.getString("city"))%>, 
                <%=UtilFormatOut.checkNull(curPostalAddress.getString("stateProvinceGeoId"))%> 
                <%=UtilFormatOut.checkNull(curPostalAddress.getString("postalCode"))%>
                <%=UtilFormatOut.ifNotEmpty(curPostalAddress.getString("countryGeoId"),"<br>","")%>
              </div>
              <div class="tabletext">(Updated:&nbsp;<%=UtilDateTime.toDateTimeString(curPartyContactMech.getTimestamp("fromDate"))%>)</div>
              <%=UtilFormatOut.ifNotEmpty(UtilDateTime.toDateTimeString(curPartyContactMech.getTimestamp("thruDate")), "<div class=\"tabletext\"><b>Delete:&nbsp;", "</b></div>")%>
            </td>
          </tr>
        <%}else{%>
           <%-- <tr>
            <td align="left" valign="top" colspan='2'>
              <div class="tabletext">No Billing Address Selected</div>
            </td>
          </tr> --%>
        <%}%>
          <%-- is confusing
          <tr>
            <td align="left" valign="top" colspan='2'>
              <div class="tabletext"><b>Select a New Billing Address:</b></div>
            </td>
          </tr>
          --%>
          <%if(partyContactMechIterator != null && partyContactMechIterator.hasNext()){%>
            <%while(partyContactMechIterator.hasNext()) {%>
              <%GenericValue partyContactMech = (GenericValue)partyContactMechIterator.next();%>
              <%GenericValue contactMech = partyContactMech.getRelatedOne("ContactMech");%>
              <%Iterator partyContactMechPurposesIter = UtilMisc.toIterator(EntityUtil.filterByDate(partyContactMech.getRelated("PartyContactMechPurpose")));%>
                <%if("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId")) && !contactMech.getString("contactMechId").equals(curContactMechId)){%>
                  <%GenericValue postalAddress = contactMech.getRelatedOne("PostalAddress");%>
                <tr>
                  <td align="right" valign="top" width="1%">
                    <INPUT type=radio name='CC_CONTACT_MECH_ID' value='<%=contactMech.getString("contactMechId")%>'>
                  </td>
                  <td align="left" valign="top" width="80%">
                    <%while(partyContactMechPurposesIter != null && partyContactMechPurposesIter.hasNext()){%>
                        <%GenericValue partyContactMechPurpose = (GenericValue)partyContactMechPurposesIter.next();%>
                        <%GenericValue contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType");%>
                        <div class="tabletext">
                          <b><%=contactMechPurposeType.getString("description")%></b>
                          <%if(partyContactMechPurpose.get("thruDate") != null){%>
                            (Expire:<%=UtilDateTime.toDateTimeString(partyContactMechPurpose.getTimestamp("thruDate"))%>)
                          <%}%>
                        </div>
                    <%}%>
                    <div class="tabletext">
                      <%=UtilFormatOut.ifNotEmpty(postalAddress.getString("toName"), "<b>To:</b> ", "<br>")%>
                      <%=UtilFormatOut.ifNotEmpty(postalAddress.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
                      <%=UtilFormatOut.checkNull(postalAddress.getString("address1"))%><br>
                      <%=UtilFormatOut.ifNotEmpty(postalAddress.getString("address2"), "", "<br>")%>
                      <%=UtilFormatOut.checkNull(postalAddress.getString("city"))%>, 
                      <%=UtilFormatOut.checkNull(postalAddress.getString("stateProvinceGeoId"))%> 
                      <%=UtilFormatOut.checkNull(postalAddress.getString("postalCode"))%>
                      <%=UtilFormatOut.ifNotEmpty(postalAddress.getString("countryGeoId"),"<br>","")%>
                    </div>
                    <div class="tabletext">(Updated:&nbsp;<%=UtilDateTime.toDateTimeString(partyContactMech.getTimestamp("fromDate"))%>)</div>
                    <%=UtilFormatOut.ifNotEmpty(UtilDateTime.toDateTimeString(partyContactMech.getTimestamp("thruDate")), "<div class=\"tabletext\"><b>Delete:&nbsp;", "</b></div>")%>
                  </td>
                </tr>
                <%}%>
            <%}%>
          <%}else{%>
            <p>No contact information on file.</p><br>
          <%}%>
        </table>
      </td>
    </tr>
  </table>
  </form>

  &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Done/Cancel]</a>
  &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[Save]</a>
<%}%>


<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
