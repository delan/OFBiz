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
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>

<% pageContext.setAttribute("PageName", "viewprofile"); %>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%
    ContactMechWorker.getPartyOtherValues(pageContext, userLogin.getString("partyId"), "party", "person", "partyGroup");
    boolean showOld = "true".equals(request.getParameter("SHOW_OLD"));
    pageContext.setAttribute("showOld", new Boolean(showOld));
    ContactMechWorker.getPartyContactMechValueMaps(pageContext, userLogin.getString("partyId"), showOld, "partyContactMechValueMaps");
    ContactMechWorker.getPartyCreditCardInfos(pageContext, userLogin.getString("partyId"), showOld, "creditCardInfos");
%>
<ofbiz:if name="party">
<%-- Main Heading --%>
<table width='100%' cellpadding='0' cellspacing='0' border='0'>
  <tr>
    <td align=left>
      <div class="head1">The Profile of
        <ofbiz:if name="person">
          <ofbiz:entityfield attribute="person" field="personalTitle"/>
          <ofbiz:entityfield attribute="person" field="firstName"/>
          <ofbiz:entityfield attribute="person" field="middleName"/>
          <ofbiz:entityfield attribute="person" field="lastName"/>
          <ofbiz:entityfield attribute="person" field="suffix"/>
        </ofbiz:if>
        <ofbiz:unless name="person">"New User"</ofbiz:unless>
      </div>
    </td>
    <td align=right>
      <ofbiz:if name="showOld">
        <a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="buttontext">[Hide Old]</a>&nbsp;&nbsp;
      </ofbiz:if>
      <ofbiz:unless name="showOld">
        <a href="<ofbiz:url>/viewprofile?SHOW_OLD=true</ofbiz:url>" class="buttontext">[Show Old]</a>&nbsp;&nbsp;
      </ofbiz:unless>
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
            [<ofbiz:if name="person">Update</ofbiz:if><ofbiz:unless name="person">Create</ofbiz:unless>]</a>&nbsp;&nbsp;
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
<ofbiz:if name="person">
  <table width="100%" border="0" cellpadding="0" cellspacing='0'>
    <tr>
      <td align="right" width="10%"><div class="tabletext"><b>Name</b></div></td>
      <td width="5">&nbsp;</td>
      <td align="left" width="90%">
        <div class="tabletext">
          <ofbiz:entityfield attribute="person" field="personalTitle"/>
          <ofbiz:entityfield attribute="person" field="firstName"/>
          <ofbiz:entityfield attribute="person" field="middleName"/>
          <ofbiz:entityfield attribute="person" field="lastName"/>
          <ofbiz:entityfield attribute="person" field="suffix"/>
        </div>
      </td>
    </tr>
    <ofbiz:entityfield attribute="person" field="nickname" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Nickname</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="gender" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Gender</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="birthDate" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Birth Date</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="height" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Height</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="weight" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Weight</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="mothersMaidenName" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Mothers Maiden Name</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="maritalStatus" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Marital Status</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="socialSecurityNumber" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Social Security Number</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="passportNumber" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Passport Number</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="passportExpireDate" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Passport Expire</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="totalYearsWorkExperience" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Years Work</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
    <ofbiz:entityfield attribute="person" field="comments" prefix="<tr><td align=right nowrap><div class='tabletext'><b>Comments</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>" suffix="</div></td></tr>"/>
  </table>
</ofbiz:if>
<ofbiz:unless name="person">
<div class="tabletext">Personal Information Not Found</div>
</ofbiz:unless>
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
  <ofbiz:if name="partyContactMechValueMaps" size="0">
    <table width="100%" border="0" cellpadding="1">
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
              <div class="tabletext">&nbsp;<b><ofbiz:entityfield attribute="contactMechType" field="description"/></b></div>
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
                      <ofbiz:entityfield attribute="partyContactMechPurpose" field="thruDate" prefix="(Expire:" suffix=")"/>
                    </div>
              </ofbiz:iterator>
              <%if ("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {%>
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
                    <ofbiz:entityfield attribute="telecomNumber" field="countryCode"/>
                    <ofbiz:entityfield attribute="telecomNumber" field="areaCode" prefix="" suffix="-"/><ofbiz:entityfield attribute="telecomNumber" field="contactNumber"/>
                    <ofbiz:entityfield attribute="partyContactMech" field="extension" prefix="ext&nbsp;" suffix=""/>
                    <%GenericValue telecomNumber = (GenericValue) pageContext.getAttribute("telecomNumber");%>
                    <%if (telecomNumber != null && (UtilValidate.isEmpty(telecomNumber.getString("countryCode")) || telecomNumber.getString("countryCode").equals("011"))) {%>
                      <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=<%=UtilFormatOut.checkNull(telecomNumber.getString("areaCode"))%>&telephone=<%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                      <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=<%=UtilFormatOut.checkNull(telecomNumber.getString("areaCode"))%>&s=&p=<%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
                    <%}%>
                  </div>
              <%} else if ("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <ofbiz:entityfield attribute="contactMech" field="infoString"/>
                    <a href='mailto:<ofbiz:entityfield attribute="contactMech" field="infoString"/>' class='buttontext'>(send&nbsp;email)</a>
                  </div>
              <%} else if ("WEB_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <ofbiz:entityfield attribute="contactMech" field="infoString"/>
                    <%String openAddress = UtilFormatOut.checkNull(contactMech.getString("infoString"));%>
                    <%if(!openAddress.startsWith("http") && !openAddress.startsWith("HTTP")) openAddress = "http://" + openAddress;%>
                    <a target='_blank' href='<%=openAddress%>' class='buttontext'>(open&nbsp;page&nbsp;in&nbsp;new&nbsp;window)</a>
                  </div>
              <%} else {%>
                  <div class="tabletext">
                    <ofbiz:entityfield attribute="contactMech" field="infoString"/>
                  </div>
              <%}%>
              <div class="tabletext">(Updated:&nbsp;<ofbiz:entityfield attribute="partyContactMech" field="fromDate"/>)</div>
              <ofbiz:entityfield attribute="partyContactMech" field="thruDate" prefix="<div class='tabletext'><b>Delete:&nbsp;" suffix="</b></div>"/>
            </td>
            <td align="center" valign="top" nowrap width="1%"><div class="tabletext"><b>(<ofbiz:entityfield attribute="partyContactMech" field="allowSolicitation"/>)</b></div></td>
            <td width="5">&nbsp;</td>
            <td align="right" valign="top" nowrap width="1%">
              <div><a href='<ofbiz:url>/editcontactmech?contactMechId=<ofbiz:entityfield attribute="contactMech" field="contactMechId"/></ofbiz:url>' class="buttontext">
              [Update]</a>&nbsp;</div>
            </td>
            <td align="right" valign="top" width="1%">
              <div><a href='<ofbiz:url>/deleteContactMech/viewprofile?contactMechId=<ofbiz:entityfield attribute="contactMech" field="contactMechId"/></ofbiz:url>' class="buttontext">
              [Delete]</a>&nbsp;&nbsp;</div>
            </td>
          </tr>
      </ofbiz:iterator>
    </table>
  </ofbiz:if>
  <ofbiz:unless name="partyContactMechValueMaps" size="0">
    <p>No contact information on file.</p><br>
  </ofbiz:unless>
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
        <ofbiz:if name="creditCardInfos" size="0">
          <table width="100%" cellpadding="2" cellspacing="0" border="0">
            <ofbiz:iterator name="creditCardInfo" property="creditCardInfos">
                <tr>
                  <td width="90%" valign="top">
                    <div class="tabletext">
                      <b>
                        <ofbiz:entityfield attribute="creditCardInfo" field="nameOnCard"/> - <%=ContactHelper.formatCreditCard(creditCardInfo)%>
                      </b>
                      (Updated:&nbsp;<ofbiz:entityfield attribute="creditCardInfo" field="fromDate"/>)
                      <ofbiz:entityfield attribute="creditCardInfo" field="thruDate" prefix="(Delete:&nbsp;" suffix=")"/>
                    </div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="right" valign="top" width='1%' nowrap>
                    <div><a href='<ofbiz:url>/editcreditcard?creditCardId=<ofbiz:entityfield attribute="creditCardInfo" field="creditCardId"/></ofbiz:url>' class="buttontext">
                    [Update]</a></div>
                  </td>
                  <td align="right" valign="top" width='1%'>
                    <div><a href='<ofbiz:url>/deleteCreditCardInfo/viewprofile?creditCardId=<ofbiz:entityfield attribute="creditCardInfo" field="creditCardId"/></ofbiz:url>' class="buttontext">
                    [Delete]</a></div>
                  </td>
                </tr>
            </ofbiz:iterator>
          </table>
        </ofbiz:if>
        <ofbiz:unless name="creditCardInfos" size="0">
          <p>No credit card information on file.</p>
        </ofbiz:unless>
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
            <a href="<ofbiz:url>/changepassword</ofbiz:url>" class="lightbuttontext">[Change Password]</a>&nbsp;&nbsp;
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
    <td align="right" valign="top" width="10%" nowrap><div class="tabletext"><b>User Name</b></div></td>
    <td width="5">&nbsp;</td>
    <td align="left" valign="top" width="90%"><div class="tabletext"><ofbiz:entityfield attribute="userLogin" field="userLoginId"/></div></td>
  </tr>
</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</ofbiz:if>
<ofbiz:unless name="party">
    No party found for current user with user name: <ofbiz:entityfield attribute="userLogin" field="userLoginId"/>
</ofbiz:unless>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %> 
