<%
/**
 *  Title: Login Page
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
 *@created    May 22 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<% pageContext.setAttribute("PageName", "viewprofile"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%
  GenericValue party = userLogin.getRelatedOne("Party");
  
  if(party != null)
  {
    GenericValue person = party.getRelatedOne("Person");

    Collection partyContactMechs = party.getRelated("PartyContactMech");
    Iterator partyContactMechIterator = partyContactMechs.iterator();
    Collection creditCards = party.getRelated("CreditCardInfo");
    Iterator creditCardInfoIterator = creditCards.iterator();
%>

<%-- Main Heading --%>
<div class="head1"><%=person.getString("firstName")%> <%=person.getString("lastName")%>'s Profile</div>
&nbsp;<br>

<table width="100%" border="0" bgcolor="#678475" cellpadding="0" cellspacing="0">
<tr bgcolor="#678475">
    <td bgcolor="#678475" valign="middle" align="left">
      <p class="head2"><font color="white">&nbsp;Personal Info</font>
    </td>
    <td bgcolor="#678475" valign="middle" align="right">
        <a href="editprofile.jsp" class="lightbuttonlink">
        [Update]&nbsp;&nbsp;</a>
    </td>
</tr>
</table>
<table width="80%" border="0" cellpadding="1">
    <tr>
        <td align="right" valign="top" width="5%"><div class="tabletext"><b>Name</b></div></td>
        <td width="5">&nbsp;</td>
        <td align="left">
          <div class="tabletext">
            <%=UtilFormatOut.checkNull(person.getString("firstName"))%> <%=UtilFormatOut.checkNull(person.getString("middleName"))%> <%=UtilFormatOut.checkNull(person.getString("lastName"))%>
          </div>
        </td>
    </tr>
<%-- ============================================================= --%>
    <tr>
      <td colspan="4" height="1" bgcolor="#899ABC">&nbsp;</td>
    </tr>
    <tr>
      <td align="right" valign="top" width="5%" nowrap>
        <div class="tabletext"><b>Contact Information</b></div>
        <div class="tabletext">
          <a href="profilenewaddress.jsp" class="buttonlink">
          [Add Contact Information]&nbsp;&nbsp;</a>
        </div>
      </td>
      <td width="5">&nbsp;</td>
      <td align="left">
        <%if(partyContactMechIterator != null && partyContactMechIterator.hasNext()){%>
          <table width="100%" border="0" cellpadding="1">
            <%
              while(partyContactMechIterator.hasNext())
              {
                GenericValue partyContactMech = (GenericValue)partyContactMechIterator.next();
                GenericValue contactMech = partyContactMech.getRelatedOne("ContactMech");
                if("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId")))
                {
                  GenericValue postalAddress = contactMech.getRelatedOne("PostalAddress");
                  %>
                <tr>
                  <td align="left" valign="top" width="60%">
                    <div class="tabletext">
                      <%=UtilFormatOut.checkNull(postalAddress.getString("address1"))%><br>
                      <%if(postalAddress.getString("address2") != null && postalAddress.getString("address2").length() != 0){%> <%= postalAddress.getString("address2") %><br> <%}%>
                      <%=UtilFormatOut.checkNull(postalAddress.getString("city"))%><br>
                      <%=UtilFormatOut.checkNull(postalAddress.getString("stateProvinceGeoId"))%> <%=UtilFormatOut.checkNull(postalAddress.getString("postalCode"))%> <%=UtilFormatOut.checkNull(postalAddress.getString("countryGeoId"))%><br>
                    </div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="right" valign="top" nowrap width="1%">
                    <div><a href="editaddress.jsp?CONTACT_MECH_ID=<%=postalAddress.getString("contactMechId")%>" class="buttonlink">
                    [Update]</a></div>&nbsp;
                  </td>
                  <td align="right" valign="top" width="1%">
                    <div><a href="/delete_postal_address/viewprofile?CONTACT_MECH_ID=<%=postalAddress.getString("contactMechId")%>" class="buttonlink">
                    [Delete]</a></div>
                  </td>
                </tr>
                  <%
                }
                else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId")))
                {
                  GenericValue telecomNumber = contactMech.getRelatedOne("TelecomNumber");
                  %>
                <tr>
                  <td align="left" valign="top" width="60%">
                    <div class="tabletext">
                      <%=UtilFormatOut.checkNull(telecomNumber.getString("countryCode"))%> <%=UtilFormatOut.checkNull(telecomNumber.getString("areaCode"))%>-<%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%><br>
                    </div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="right" valign="top" nowrap width="1%">
                    <div><a href="editaddress.jsp?CONTACT_MECH_ID=<%=telecomNumber.getString("contactMechId")%>" class="buttonlink">
                    [Update]</a></div>&nbsp;
                  </td>
                  <td align="right" valign="top" width="1%">
                    <div><a href="/delete_telecom_number/viewprofile?CONTACT_MECH_ID=<%=telecomNumber.getString("contactMechId")%>" class="buttonlink">
                    [Delete]</a></div>
                  </td>
                </tr>
                  <%
                }
            %>
                <%if(partyContactMechIterator.hasNext()){%>
                  <tr><td colspan="4" height="1" bgcolor="#899ABC">&nbsp;</td></tr>
                <%}%>
              <%}%>
          </table>
        <%}else{%>
          <p>No contact information on file.</p><br>
        <%}%>
      </td>
    </tr>
<%-- ============================================================= --%>
    <tr>
      <td colspan="4" height="1" bgcolor="#899ABC">&nbsp;</td>
    </tr>
    <tr>
      <td align="right" valign="top" width="5%" nowrap>
        <div class="tabletext"><b>Credit Cards</b></div>
        <div class="tabletext">
          <a href="profilenewcc.jsp" class="buttonlink">
          [Add Card]&nbsp;&nbsp;</a>
        </div>
      </td>
      <td width="5">&nbsp;</td>
      <td align="left">
        <%if(creditCardInfoIterator != null && creditCardInfoIterator.hasNext()){%>
          <p>Select an account on file to update or delete.</p>
          <table width="100%" cellpadding="2" cellspacing="0" border="0">
            <%while(creditCardInfoIterator.hasNext()){%>
              <%GenericValue creditCardInfo = (GenericValue)creditCardInfoIterator.next();%>
              <tr>
                <td width="55%">
                  <div class="tabletext">
                    <b>
                      <%=creditCardInfo.getString("nameOnCard")%> <%=creditCardInfo.getString("cardType")%>
                      <%if(creditCardInfo.getString("cardNumber") != null && creditCardInfo.getString("cardNumber").length() > 4) {%>
                        <%=creditCardInfo.getString("cardNumber").substring(creditCardInfo.getString("cardNumber").length()-4)%>
                      <%}%>
                      <%=creditCardInfo.getString("expireDate")%>
                    </b>
                  </div>
                <td>
                <td align="center">
                  <a href="editcc.jsp?CREDIT_CARD_ID=<%=creditCardInfo.getString("creditCardId")%>" class="buttonlink">
                  [Update Card]</a>
                </td>
                <td align="right">
                  <a href="/delete_credit_card_info/viewprofile?CREDIT_CARD_ID=<%=creditCardInfo.getString("creditCardId")%>" class="buttonlink">
                  [Delete]</a>
                </td>
              </tr>
            <%}//end while loop%>
          </table>
        <%}else{//if paymentIterator%>
          <p>No credit card information on file.</p><br>
        <%}//if paymentIterator%>
      </td>
    </tr>
<%-- ============================================================= --%>

</table>
<table width="100%" border="0" bgcolor="#678475" cellpadding="0" cellspacing="0">
<tr bgcolor="#678475">
  <td bgcolor="#678475" valign="middle" align="left">
    <div class="head2"><font color="white">&nbsp;User Name & Password</font></div>
  </td>
  <td bgcolor="#678475" valign="middle" align="right">
    <a href="changepassword.jsp" class="lightbuttonlink">
    [Update]&nbsp;&nbsp;</a>
  </td>
</tr>
</table>

<table width="80%" border="0" cellpadding="1">
  <tr>
    <td align="right" valign="top" width="15%" nowrap><div class="tabletext"><b>User Name</b></div></td>
    <td width="5">&nbsp;</td>
    <td align="left" valign="top"><div class="tabletext"><%=userLogin.getString("userLoginId")%></div></td>
  </tr>
</table>
  <%}else{%>
No party found for current user with user name: <%=userLogin.getString("userLoginId")%>
  <%}%>


<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
