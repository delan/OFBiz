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
<%@ page import="org.ofbiz.commonapp.party.contact.ContactHelper" %>

<% pageContext.setAttribute("PageName", "viewprofile"); %>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%
  boolean showOld = "true".equals(request.getParameter("SHOW_OLD"))?true:false;
  GenericValue party = userLogin.getRelatedOne("Party");
  if(party != null)
  {
    Iterator partyContactMechIterator = UtilMisc.toIterator(showOld ? party.getRelated("PartyContactMech") : EntityUtil.filterByDate(party.getRelated("PartyContactMech")));
    Iterator creditCardInfoIterator = UtilMisc.toIterator(showOld ? party.getRelated("CreditCardInfo") : EntityUtil.filterByDate(party.getRelated("CreditCardInfo")));
%>

<%-- Main Heading --%>
<table width='100%' cellpadding='0' cellspacing='0' border='0'>
  <tr>
    <td align=left>
      <div class="head1">The Profile of
        <%if(person != null){%>
          <%=UtilFormatOut.checkNull(person.getString("personalTitle"))%>
          <%=UtilFormatOut.checkNull(person.getString("firstName"))%>
          <%=UtilFormatOut.checkNull(person.getString("middleName"))%>
          <%=UtilFormatOut.checkNull(person.getString("lastName"))%>
          <%=UtilFormatOut.checkNull(person.getString("suffix"))%>
        <%}else{%>
          "New User"
        <%}%>
      </div>
    </td>
    <td align=right>
      <%if(showOld){%>
        <a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="buttontext">
        [Hide Old]</a>&nbsp;&nbsp;
      <%}else{%>
        <a href="<ofbiz:url>/viewprofile?SHOW_OLD=true</ofbiz:url>" class="buttontext">
        [Show Old]</a>&nbsp;&nbsp;
      <%}%>
    </td>
  </tr>
</table>
<br>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Personal Information</div>
          </td>
          <td valign="middle" align="right">
            <a href="<ofbiz:url>/editperson</ofbiz:url>" class="lightbuttontext">
            [<%=(person==null?"Create":"Update")%>]</a>&nbsp;&nbsp;
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
<%if(person != null){%>
  <table width="100%" border="0" cellpadding="0" cellspacing='0'>
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
    <%String preBeforeLabel = "<tr><td align=right width='15%' nowrap><div class='tabletext'><b>";%>
    <%String preAfterLabel = "</b></div></td><td width='5'>&nbsp;</td><td align=left><div class='tabletext'>";%>
    <%String postStr = "</div></td></tr>";%>
    <%=UtilFormatOut.ifNotEmpty(person.getString("nickname"), preBeforeLabel + "Nickname" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(person.getString("gender"), preBeforeLabel + "Gender" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(UtilDateTime.toDateString(person.getDate("birthDate")), preBeforeLabel + "Birth Date" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(UtilFormatOut.formatQuantity(person.getDouble("height")), preBeforeLabel + "Height" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(UtilFormatOut.formatQuantity(person.getDouble("weight")), preBeforeLabel + "Weight" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(person.getString("mothersMaidenName"), preBeforeLabel + "Mothers Maiden Name" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(person.getString("maritalStatus"), preBeforeLabel + "Marital Status" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(person.getString("socialSecurityNumber"), preBeforeLabel + "Social Security Number" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(person.getString("passportNumber"), preBeforeLabel + "Passport Number" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(UtilDateTime.toDateString(person.getDate("passportExpireDate")), preBeforeLabel + "Passport Expire" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(UtilFormatOut.formatQuantity(person.getDouble("totalYearsWorkExperience")), preBeforeLabel + "Years Work" + preAfterLabel, postStr)%>
    <%=UtilFormatOut.ifNotEmpty(person.getString("comment"), preBeforeLabel + "Comment" + preAfterLabel, postStr)%>
  </table>
<%}else{%>
<div class="tabletext">Personal Information Not Found</div>
<%}%>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<%-- ============================================================= --%>
<br>
<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Contact Information</div>
          </td>
          <td valign="middle" align="right">
              <a href="<ofbiz:url>/editcontactmech</ofbiz:url>" class="lightbuttontext">
              [Create New]</a>&nbsp;&nbsp;
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
  <%if(partyContactMechIterator != null && partyContactMechIterator.hasNext()){%>
    <table width="100%" border="0" cellpadding="1">
      <tr align=left valign=bottom>
        <th>Contact&nbsp;Type</th>
        <th width="5">&nbsp;</th>
        <th>Information</th>
        <th colspan='2'>Soliciting&nbsp;OK?</th>
        <th>&nbsp;</th>
      </tr>
      <%while(partyContactMechIterator.hasNext()) {%>
        <%GenericValue partyContactMech = (GenericValue)partyContactMechIterator.next();%>
        <%GenericValue contactMech = partyContactMech.getRelatedOne("ContactMech");%>
        <%GenericValue contactMechType = (contactMech==null?null:contactMech.getRelatedOne("ContactMechType"));%>
        <%Iterator partyContactMechPurposesIter = UtilMisc.toIterator(showOld ? partyContactMech.getRelated("PartyContactMechPurpose") : EntityUtil.filterByDate(partyContactMech.getRelated("PartyContactMechPurpose")));%>
          <tr><td colspan="7"><div style='height: 1; background-color: #999999;'></div></td></tr>
          <tr>
            <td align="right" valign="top" width="15%">
              <div class="tabletext">&nbsp;<b><%if(contactMechType!=null){%><%=UtilFormatOut.checkNull(contactMechType.getString("description"))%><%}%></b></div>
            </td>
            <td width="5">&nbsp;</td>
            <td align="left" valign="top" width="80%">
              <%while(partyContactMechPurposesIter != null && partyContactMechPurposesIter.hasNext()){%>
                <%GenericValue partyContactMechPurpose = (GenericValue)partyContactMechPurposesIter.next();%>
                <%if(partyContactMechPurpose != null) {%>
                  <%GenericValue contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType");%>
                    <div class="tabletext">
                      <%if(contactMechPurposeType != null) {%>
                        <b><%=contactMechPurposeType.getString("description")%></b>
                      <%}else{%>
                        <b>Purpose Type not found with ID: "<%=partyContactMechPurpose.getString("contactMechPurposeTypeId")%>"</b>
                      <%}%>
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
              <%if(UtilValidate.isEmpty(postalAddress.getString("countryGeoId")) || postalAddress.getString("countryGeoId").equals("USA")) {%>
                <%String addr1 = UtilFormatOut.checkNull(postalAddress.getString("address1"));%>
                <%if(addr1.indexOf(' ') > 0) {%>
                  <%String addressNum = addr1.substring(0, addr1.indexOf(' '));%>
                  <%String addressOther = addr1.substring(addr1.indexOf(' ')+1);%>
                  <a target='_blank' href='http://www.whitepages.com/find_person_results.pl?fid=a&s_n=<%=addressNum%>&s_a=<%=addressOther%>&c=<%=UtilFormatOut.checkNull(postalAddress.getString("city"))%>&s=<%=UtilFormatOut.checkNull(postalAddress.getString("stateProvinceGeoId"))%>&x=29&y=18' class='buttontext'>(lookup:whitepages.com)</a>
                <%}%>
              <%}%>
          <%}else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))){%>
            <%GenericValue telecomNumber = contactMech.getRelatedOne("TelecomNumber");%>
              <div class="tabletext">
                <%if(telecomNumber != null) {%>
                  <%=UtilFormatOut.checkNull(telecomNumber.getString("countryCode"))%>
                  <%=UtilFormatOut.ifNotEmpty(telecomNumber.getString("areaCode"), "", "-")%><%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>
                  <%=UtilFormatOut.ifNotEmpty(partyContactMech.getString("extension"), "ext&nbsp;", "")%>
                  <%if(UtilValidate.isEmpty(telecomNumber.getString("countryCode")) || telecomNumber.getString("countryCode").equals("011")) {%>
                    <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=<%=UtilFormatOut.checkNull(telecomNumber.getString("areaCode"))%>&telephone=<%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                    <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=<%=UtilFormatOut.checkNull(telecomNumber.getString("areaCode"))%>&s=&p=<%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
                  <%}%>
                <%}else{%>
                  <div class="tabletext">Phone Number Information Not Found</div>
                <%}%>
              </div>
          <%}else if("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))){%>
              <div class="tabletext">
                <%=UtilFormatOut.checkNull(contactMech.getString("infoString"))%>
                <a href='mailto:<%=UtilFormatOut.checkNull(contactMech.getString("infoString"))%>' class='buttontext'>(send&nbsp;email)</a>
              </div>
          <%}else if("WEB_ADDRESS".equals(contactMech.getString("contactMechTypeId"))){%>
              <div class="tabletext">
                <%=UtilFormatOut.checkNull(contactMech.getString("infoString"))%>
                <%String openAddress = UtilFormatOut.checkNull(contactMech.getString("infoString"));%>
                <%if(!openAddress.startsWith("http") && !openAddress.startsWith("HTTP")) openAddress = "http://" + openAddress;%>
                <a target='_blank' href='<%=openAddress%>' class='buttontext'>(open&nbsp;site&nbsp;in&nbsp;new&nbsp;window)</a>
              </div>
          <%}else{%>
              <div class="tabletext">
                <%=UtilFormatOut.checkNull(contactMech.getString("infoString"))%>
              </div>
          <%}%>
              <div class="tabletext">(Updated:&nbsp;<%=UtilDateTime.toDateTimeString(partyContactMech.getTimestamp("fromDate"))%>)</div>
              <%=UtilFormatOut.ifNotEmpty(UtilDateTime.toDateTimeString(partyContactMech.getTimestamp("thruDate")), "<div class=\"tabletext\"><b>Delete:&nbsp;", "</b></div>")%>
            </td>
            <td align="center" valign="top" nowrap width="1%"><div class="tabletext"><b>(<%=UtilFormatOut.checkNull(partyContactMech.getString("allowSolicitation"))%>)</b></div></td>
            <td width="5">&nbsp;</td>
            <td align="right" valign="top" nowrap width="1%">
              <div><a href="<ofbiz:url><%="/editcontactmech?CONTACT_MECH_ID=" + contactMech.getString("contactMechId")%></ofbiz:url>" class="buttontext">
              [Update]</a></div>&nbsp;
            </td>
            <td align="right" valign="top" width="1%">
              <div><a href="<ofbiz:url><%="/updatecontactmech/viewprofile?UPDATE_MODE=DELETE&CONTACT_MECH_ID=" + contactMech.getString("contactMechId")%></ofbiz:url>" class="buttontext">
              [Delete]</a></div>
            </td>
          </tr>
      <%}%>
    </table>
  <%}else{%>
    <p>No contact information on file.</p><br>
  <%}%>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<%-- ============================================================= --%>
<br>
<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Credit Card Information</div>
          </td>
          <td valign="middle" align="right">
            <a href="<ofbiz:url>/editcreditcard</ofbiz:url>" class="lightbuttontext">
            [Create New]</a>&nbsp;&nbsp;
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="1">
    <tr>
      <td align="left">
        <%if(creditCardInfoIterator != null && creditCardInfoIterator.hasNext()){%>
          <table width="100%" cellpadding="2" cellspacing="0" border="0">
            <%while(creditCardInfoIterator.hasNext()){%>
              <%GenericValue creditCardInfo = (GenericValue)creditCardInfoIterator.next();%>
                <tr>
                  <td width="90%" valign="top">
                    <div class="tabletext">
                      <b>
                        <%=creditCardInfo.getString("nameOnCard")%> - <%=ContactHelper.formatCreditCard(creditCardInfo)%>
                      </b>
                      (Updated:&nbsp;<%=UtilDateTime.toDateTimeString(creditCardInfo.getTimestamp("fromDate"))%>)
                      <%=UtilFormatOut.ifNotEmpty(UtilDateTime.toDateTimeString(creditCardInfo.getTimestamp("thruDate")), "(Delete:&nbsp;", ")")%>
                    </div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="right" valign="top" width='1%' nowrap>
                    <div><a href="<ofbiz:url><%="/editcreditcard?CREDIT_CARD_ID=" + creditCardInfo.getString("creditCardId")%></ofbiz:url>" class="buttontext">
                    [Update]</a></div>
                  </td>
                  <td align="right" valign="top" width='1%'>
                    <div><a href="<ofbiz:url><%="/updatecreditcard/viewprofile?UPDATE_MODE=DELETE&CREDIT_CARD_ID=" + creditCardInfo.getString("creditCardId")%></ofbiz:url>" class="buttontext">
                    [Delete]</a></div>
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
    </TD>
  </TR>
</TABLE>

<br>
<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;User Name & Password</div>
          </td>
          <td valign="middle" align="right">
            <a href="<ofbiz:url>/changepassword</ofbiz:url>" class="lightbuttontext">
            [Change Password]</a>&nbsp;&nbsp;
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
<table width="100%" border="0" cellpadding="1">
  <tr>
    <td align="right" valign="top" width="15%" nowrap><div class="tabletext"><b>User Name</b></div></td>
    <td width="5">&nbsp;</td>
    <td align="left" valign="top"><div class="tabletext"><%=userLogin.getString("userLoginId")%></div></td>
  </tr>
</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

  <%}else{%>
No party found for current user with user name: <%=userLogin.getString("userLoginId")%>
  <%}%>


<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %> 
