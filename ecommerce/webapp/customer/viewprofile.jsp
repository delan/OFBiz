
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<% pageContext.setAttribute("PageName", "viewprofile"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%
  GenericValue userLogin = (GenericValue)session.getAttribute(SiteDefs.USER_LOGIN);
  GenericValue party = userLogin.getRelatedOne("Party");
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
        <td width="5"><img src="/commerce/images/shim.gif" width="5" height="5"></td>
        <td align="left">
          <div class="tabletext">
            <%=CommonUtil.checkNull(person.getString("firstName"))%> <%=CommonUtil.checkNull(person.getString("middleName"))%> <%=CommonUtil.checkNull(person.getString("lastName"))%>
          </div>
        </td>
    </tr>
<%-- ============================================================= --%>
    <tr>
      <td colspan="4" height="1" bgcolor="#899ABC"><img src="/commerce/images/shim.gif" width="200" height="1"></td>
    </tr>
    <tr>
      <td align="right" valign="top" width="5%" nowrap>
        <div class="tabletext"><b>Contact Information</b></div>
        <div class="tabletext">
          <a href="profilenewaddress.jsp" class="buttonlink">
          [Add Contact Information]&nbsp;&nbsp;</a>
        </div>
      </td>
      <td width="5"><img src="/commerce/images/shim.gif" width="5" height="5"></td>
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
                      <%=postalAddress.getString("address1")%><br>
                      <%if(postalAddress.getString("address2") != null && postalAddress.getString("address2").length() != 0){%> <%= postalAddress.getString("address2") %><br> <%}%>
                      <%=postalAddress.getString("city")%><br>
                      <%=postalAddress.getString("stateProvinceGeoId")%> <%=postalAddress.getString("postalCode")%> <%=postalAddress.getString("countryGeoId")%><br>
                    </div>
                  </td>
                  <td width="5"><img src="/commerce/images/shim.gif" width="5" height="5"></td>
                  <td align="right" valign="top" nowrap width="1%">
                    <div><a href="profileeditaddress.jsp?<%=HttpRequestConstants.ADDRESS_KEY%>=<%=postalAddress.getAddressId()%>" class="buttonlink">
                    [Update]</a></div>&nbsp;
                  </td>
                  <td align="right" valign="top" width="1%">
                    <%if(postalAddress.getAddressId().intValue() != custAddress.getAddressId().intValue()){%>
                      <div><a href="viewprofile.jsp?<%=HttpRequestConstants.EVENT%>=<%=EventConstants.DELETE_SHIPPING_ADDRESS%>&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=postalAddress.getAddressId()%>" class="buttonlink">
                      [Delete]</a></div>
                    <%}else{%>
                      <div class="tabletext"><b>Primary Address</b></div>
                    <%}%>
                  </td>
                </tr>
                  <%
                }
                else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId")))
                {
                  GenericValue telecomNumber = contactMech.getRelatedOne("TelecomNumber");
                }
            %>
                <%if(partyContactMechIterator.hasNext()){%>
                  <tr><td colspan="4" height="1" bgcolor="#899ABC"><img src="/commerce/images/shim.gif" width="200" height="1"></td></tr>
                <%}%>
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
      <td colspan="4" height="1" bgcolor="#899ABC"><img src="/commerce/images/shim.gif" width="200" height="1"></td>
    </tr>
    <tr>
      <td align="right" valign="top" width="5%" nowrap>
        <div class="tabletext"><b>Credit Cards</b></div>
        <div class="tabletext">
          <a href="profilenewcc.jsp" class="buttonlink">
          [Add Card]&nbsp;&nbsp;</a>
        </div>
      </td>
      <td width="5"><img src="/commerce/images/shim.gif" width="5" height="5"></td>
      <td align="left">
        <%if(paymentIterator != null && paymentIterator.hasNext()){%>
          <p>Select an account on file to update or delete.</p>
          <table width="100%" cellpadding="2" cellspacing="0" border="0">
            <%while(paymentIterator.hasNext()){%>
              <%CustomerPayment customerPayment = (CustomerPayment)paymentIterator.next();%>
              <tr>
                <td width="55%">
                  <div class="tabletext">
                    <b>
                      <%=customerPayment.getCardType()%>
                      <%if(customerPayment.getCardNumber() != null && customerPayment.getCardNumber().length() > 4) {%>
                        <%=customerPayment.getCardNumber().substring(customerPayment.getCardNumber().length()-4)%>
                      <%}%>
                      <%=customerPayment.getExpireDate()%>
                    </b>
                  </div>
                <td>
                <td align="center">
                  <a href="profileeditcc.jsp?<%=HttpRequestConstants.CUSTOMER_PAYMENT_ID%>=<%=customerPayment.getPaymentId()%>" class="buttonlink">
                  [Update Card]</a>
                </td>
                <td align="right">
                  <a href="viewprofile.jsp?<%=HttpRequestConstants.EVENT%>=<%=EventConstants.DELETE_CUSTOMER_PAYMENT%>&<%=HttpRequestConstants.CUSTOMER_PAYMENT_ID%>=<%=customerPayment.getPaymentId()%>" class="buttonlink">
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
    <td width="5"><img src="/commerce/images/shim.gif" width="5" height="5"></td>
    <td align="left" valign="top"><div class="tabletext"><%=userLogin.getString("userLoginId")%></div></td>
  </tr>
</table>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>


