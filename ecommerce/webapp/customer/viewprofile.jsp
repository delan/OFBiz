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
  boolean showOld = "true".equals(request.getParameter("SHOW_OLD"))?true:false;
  GenericValue party = userLogin.getRelatedOne("Party");
  if(party != null)
  {
    GenericValue person = party.getRelatedOne("Person");
    Iterator partyContactMechIterator = UtilMisc.toIterator(party.getRelated("PartyContactMech"));
    Iterator creditCardInfoIterator = UtilMisc.toIterator(party.getRelated("CreditCardInfo"));
%>

<%-- Main Heading --%>
<div class="head1"><%=person.getString("firstName")%> <%=person.getString("lastName")%>'s Profile</div>
&nbsp;<br>

<table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td bgcolor="#678475">
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="middle" align="left">
      <p class="head2"><font color="white">&nbsp;Personal Information</font>
    </td>
    <td valign="middle" align="right">
        <a href="<%=response.encodeURL(controlPath + "/editperson")%>" class="lightbuttontext">
        [Update]</a>&nbsp;&nbsp;
    </td>
  </tr>
  </table>
    </td>
  </tr>
  <tr>
    <td bgcolor='white' colspan='2'>
<table width="80%" border="0" cellpadding="1">
  <tr>
    <td align="right" width="15%"><div class="tabletext"><b>Name</b></div></td>
    <td width="5">&nbsp;</td>
    <td align="left">
      <div class="tabletext">
        <%=UtilFormatOut.checkNull(person.getString("personalTitle"))%>
        <%=UtilFormatOut.checkNull(person.getString("firstName"))%>
        <%=UtilFormatOut.checkNull(person.getString("middleName"))%>
        <%=UtilFormatOut.checkNull(person.getString("lastName"))%>
        <%=UtilFormatOut.checkNull(person.getString("suffix"))%>
      </div>
    </td>
  </tr>
</table>
    </td>
  </tr>
</table>
<%-- ============================================================= --%>
<br>
<table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td bgcolor="#678475">
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="middle" align="left">
      <p class="head2"><font color="white">&nbsp;Contact Information</font>
    </td>
    <td valign="middle" align="right">
        <a href="<%=response.encodeURL(controlPath + "/editcontactmech")%>" class="lightbuttontext">
        [Create New]</a>&nbsp;
        <%if(showOld){%>
          <a href="<%=response.encodeURL(controlPath + "/viewprofile")%>" class="lightbuttontext">
          [Hide Old]</a>&nbsp;&nbsp;
        <%}else{%>
          <a href="<%=response.encodeURL(controlPath + "/viewprofile?SHOW_OLD=true")%>" class="lightbuttontext">
          [Show Old]</a>&nbsp;&nbsp;
        <%}%>
    </td>
  </tr>
  </table>
    </td>
  </tr>
  <tr>
    <td bgcolor='white' colspan='2'>
      <%if(partyContactMechIterator != null && partyContactMechIterator.hasNext()){%>
        <table width="100%" border="0" cellpadding="1">
          <tr align=left valign=bottom>
            <th>Contact Type</th>
            <th width="5">&nbsp;</th>
            <th>Information</th>
            <th colspan='2'>Soliciting&nbsp;OK?</th>
            <th>&nbsp;</th>
          </tr>
          <%while(partyContactMechIterator.hasNext())
            {
              GenericValue partyContactMech = (GenericValue)partyContactMechIterator.next();
              GenericValue contactMech = partyContactMech.getRelatedOne("ContactMech");
              GenericValue contactMechType = contactMech.getRelatedOne("ContactMechType");
              Iterator partyContactMechPurposesIter = UtilMisc.toIterator(partyContactMech.getRelated("PartyContactMechPurpose"));
              if(showOld || partyContactMech.get("thruDate") == null || partyContactMech.getTimestamp("thruDate").after(new java.util.Date()))
              {%>
              <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
              <tr>
                <td align="right" valign="top" width="5%">
                  <div class="tabletext">&nbsp;<b><%if(contactMechType!=null){%><%=UtilFormatOut.checkNull(contactMechType.getString("description"))%><%}%></b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="90%">
                  <%while(partyContactMechPurposesIter != null && partyContactMechPurposesIter.hasNext()){%>
                    <%GenericValue partyContactMechPurpose = (GenericValue)partyContactMechPurposesIter.next();%>
                    <%GenericValue contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType");%>
                    <%if(showOld || partyContactMechPurpose.get("thruDate") == null || partyContactMechPurpose.getTimestamp("thruDate").after(new java.util.Date())){%>
                      <div class="tabletext">
                        <b><%=contactMechPurposeType.getString("description")%></b>
                        <%if(showOld && partyContactMechPurpose.get("thruDate") != null){%>
                          (Expire:<%=UtilDateTime.toDateTimeString(partyContactMechPurpose.getTimestamp("thruDate"))%>)
                        <%}%>
                      </div>
                    <%}%>
                  <%}%>
              <%if("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))){%>
                <%GenericValue postalAddress = contactMech.getRelatedOne("PostalAddress");%>
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
              <%}else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))){%>
                <%GenericValue telecomNumber = contactMech.getRelatedOne("TelecomNumber");%>
                  <div class="tabletext">
                    <%=UtilFormatOut.checkNull(telecomNumber.getString("countryCode"))%>
                    <%=UtilFormatOut.ifNotEmpty(telecomNumber.getString("areaCode"), "", "-")%><%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>
                    <%=UtilFormatOut.ifNotEmpty(partyContactMech.getString("extension"), "ext&nbsp;", "")%>
                  </div>
              <%}else if("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))){%>
                  <div class="tabletext">
                    <%=UtilFormatOut.checkNull(contactMech.getString("infoString"))%>
                  </div>
              <%}%>
                  <div class="tabletext">(Created:&nbsp;<%=UtilDateTime.toDateTimeString(partyContactMech.getTimestamp("fromDate"))%>)</div>
                  <%=UtilFormatOut.ifNotEmpty(UtilDateTime.toDateTimeString(partyContactMech.getTimestamp("thruDate")), "<div class=\"tabletext\"><b>Expires/Expired on:&nbsp;", "</b></div>")%>
                </td>
                <td align="center" valign="top" nowrap width="1%"><div class="tabletext"><b>(<%=UtilFormatOut.checkNull(partyContactMech.getString("allowSolicitation"))%>)</b></div></td>
                <td width="5">&nbsp;</td>
                <td align="right" valign="top" nowrap width="1%">
                  <div><a href="<%=response.encodeURL(controlPath + "/editcontactmech?CONTACT_MECH_ID=" + contactMech.getString("contactMechId"))%>" class="buttontext">
                  [Update]</a></div>&nbsp;
                </td>
                <td align="right" valign="top" width="1%">
                  <div><a href="<%=response.encodeURL(controlPath + "/updatecontactmech/viewprofile?UPDATE_MODE=DELETE&CONTACT_MECH_ID=" + contactMech.getString("contactMechId"))%>" class="buttontext">
                  [Delete]</a></div>
                </td>
              </tr>
            <%}%>
          <%}%>
        </table>
      <%}else{%>
        <p>No contact information on file.</p><br>
      <%}%>
    </td>
  </tr>
</table>
<%-- ============================================================= --%>
<br>
<table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td bgcolor="#678475">
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="middle" align="left">
      <p class="head2"><font color="white">&nbsp;Credit Card Information</font>
    </td>
    <td valign="middle" align="right">
      <a href="<%=response.encodeURL(controlPath + "/editcreditcard")%>" class="lightbuttontext">
      [Create New]</a>&nbsp;&nbsp;
    </td>
  </tr>
  </table>
    </td>
  </tr>
  <tr>
    <td bgcolor='white' colspan='2'>
      <table width="80%" border="0" cellpadding="1">
          <tr>
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
                        <a href="<%=response.encodeURL(controlPath + "/editcreditcard?CREDIT_CARD_ID=" + creditCardInfo.getString("creditCardId"))%>" class="buttontext">
                        [Update Card]</a>
                      </td>
                      <td align="right">
                        <a href="<%=response.encodeURL(controlPath + "/updatecreditcard/viewprofile?UPDATE_MODE=DELETE&CREDIT_CARD_ID=" + creditCardInfo.getString("creditCardId"))%>" class="buttontext">
                        [Delete]</a>
                      </td>
                    </tr>
                  <%}//end while loop%>
                </table>
              <%}else{//if paymentIterator%>
                <p>No credit card information on file.</p>
              <%}//if paymentIterator%>
            </td>
          </tr>
      </table>
    </td>
  </tr>
</table>

<br>
<table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td bgcolor="#678475">
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="middle" align="left">
      <p class="head2"><font color="white">&nbsp;User Name & Password</font>
    </td>
    <td valign="middle" align="right">
      <a href="<%=response.encodeURL(controlPath + "/changepassword")%>" class="lightbuttontext">
      [Change Password]</a>&nbsp;&nbsp;
    </td>
  </tr>
  </table>
    </td>
  </tr>
  <tr>
    <td bgcolor='white' colspan='2'>
<table width="80%" border="0" cellpadding="1">
  <tr>
    <td align="right" valign="top" width="15%" nowrap><div class="tabletext"><b>User Name</b></div></td>
    <td width="5">&nbsp;</td>
    <td align="left" valign="top"><div class="tabletext"><%=userLogin.getString("userLoginId")%></div></td>
  </tr>
</table>
  </td>
</tr>
</table>

  <%}else{%>
No party found for current user with user name: <%=userLogin.getString("userLoginId")%>
  <%}%>


<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %> 
