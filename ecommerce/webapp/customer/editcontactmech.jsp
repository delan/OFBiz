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
  boolean useValues = true;
  if(request.getAttribute("ERROR_MESSAGE") != null) useValues = false;

  String donePage = request.getParameter("DONE_PAGE");
  if(donePage == null || donePage.length() <= 0) donePage="viewprofile";

  String contactMechId = request.getParameter("CONTACT_MECH_ID");
  if(contactMechId == null) contactMechId = (String)request.getAttribute("CONTACT_MECH_ID");

  GenericValue partyContactMech = helper.findByPrimaryKey("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId));
  GenericValue contactMech = helper.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
%>

<%if(contactMech == null){%>
<p class="head1">Create New Contact Information</p>

<form method="post" action="<%=response.encodeURL(controlPath + "/updatecontactmech?UPDATE_MODE=CREATE&DONE_PAGE=" + donePage)%>" name="createcontactmechform">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%"><div class="tabletext">Select Contact Type:</div></td>
      <td width="74%">
        <select name="CONTACT_MECH_TYPE_ID">
          <%Iterator contactMechTypes = UtilMisc.toIterator(helper.findByAnd("ContactMechType", null, null));%>
          <%while(contactMechTypes != null && contactMechTypes.hasNext()){%>
            <%GenericValue contactMechType = (GenericValue)contactMechTypes.next();%>
            <option value='<%=contactMechType.getString("contactMechTypeId")%>'><%=contactMechType.getString("description")%></option>
          <%}%>
        </select>&nbsp;<a href="javascript:document.createcontactmechform.submit()" class="buttontext">[Create]</a>
      </td>
    </tr>
  </table>
</form>
<%-- <p><h3>ERROR: Contact information with ID "<%=UtilFormatOut.checkNull(contactMechId)%>" not found!</h3></p> --%>
<%}else{%>
<p class="head1">Edit Contact Information</p>
  <%if(!security.hasPermission("USER_ADMIN", session) && partyContactMech == null){%>
  <p><h3>The contact information specified does not belong to you, you may not view or edit it.</h3></p>
  <%}else{%>
  <form method="post" action="<%=response.encodeURL(controlPath + "/updatecontactmech/" + donePage)%>" name="editcontactmechform">
  <input type=hidden name="CONTACT_MECH_ID" value="<%=contactMechId%>">
  <input type=hidden name="UPDATE_MODE" value="UPDATE">

  <table width="90%" border="0" cellpadding="2" cellspacing="0">
  <%if("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))){%>
    <%GenericValue postalAddress = contactMech.getRelatedOne("PostalAddress");%>
    <tr>
      <td width="26%"><div class="tabletext">To Name</div></td>
      <td width="74%">
        <input type="text" name="CM_TO_NAME" value="<%=UtilFormatOut.checkNull(useValues?postalAddress.getString("toName"):request.getParameter("CM_TO_NAME"))%>" size="30" maxlength="60">
      *</td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Attention Name</div></td>
      <td width="74%">
        <input type="text" name="CM_ATTN_NAME" value="<%=UtilFormatOut.checkNull(useValues?postalAddress.getString("attnName"):request.getParameter("CM_ATTN_NAME"))%>" size="30" maxlength="60">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Address Line 1</div></td>
      <td width="74%">
        <input type="text" name="CM_ADDRESS1" value="<%=UtilFormatOut.checkNull(useValues?postalAddress.getString("address1"):request.getParameter("CM_ADDRESS1"))%>" size="30" maxlength="30">
      *</td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Address Line 2</div></td>
      <td width="74%">
          <input type="text" name="CM_ADDRESS2" value="<%=UtilFormatOut.checkNull(useValues?postalAddress.getString("address2"):request.getParameter("CM_ADDRESS2"))%>" size="30" maxlength="30">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">City</div></td>
      <td width="74%">
          <input type="text" name="CM_CITY" value="<%=UtilFormatOut.checkNull(useValues?postalAddress.getString("city"):request.getParameter("CM_CITY"))%>" size="30" maxlength="30">
      * </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">State/Province</div></td>
      <td width="74%">
        <select name="CM_STATE">
          <option><%=UtilFormatOut.checkNull(useValues?postalAddress.getString("stateProvinceGeoId"):request.getParameter("CM_STATE"))%></option>
          <option></option>
          <%@ include file="/includes/states.jsp" %>
        </select>
      *</td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Zip/Postal Code</div></td>
      <td width="74%">
        <input type="text" name="CM_POSTAL_CODE" value="<%=UtilFormatOut.checkNull(useValues?postalAddress.getString("postalCode"):request.getParameter("CM_POSTAL_CODE"))%>" size="12" maxlength="10">
      *</td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Country</div></td>
      <td width="74%">
        <select name="CM_COUNTRY" >
          <option><%=UtilFormatOut.checkNull(useValues?postalAddress.getString("countryGeoId"):request.getParameter("CM_COUNTRY"))%></option>
          <option></option>
          <%@ include file="/includes/countries.jsp" %>
        </select>
      *</td>
    </tr>
  <%}else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))){%>
    <%GenericValue telecomNumber = contactMech.getRelatedOne("TelecomNumber");%>
    <tr>
      <td colspan='2'>All phone numbers: [Country Code] [Area Code] [Contact Number] [Extension]</td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Phone Number</div></td>
      <td width="74%">
          <input type="text" name="CM_COUNTRY_CODE" value="<%=UtilFormatOut.checkNull(useValues?telecomNumber.getString("countryCode"):request.getParameter("CM_COUNTRY_CODE"))%>" size="4" maxlength="10">
          -&nbsp;<input type="text" name="CM_AREA_CODE" value="<%=UtilFormatOut.checkNull(useValues?telecomNumber.getString("areaCode"):request.getParameter("CM_AREA_CODE"))%>" size="4" maxlength="10">
          -&nbsp;<input type="text" name="CM_CONTACT_NUMBER" value="<%=UtilFormatOut.checkNull(useValues?telecomNumber.getString("contactNumber"):request.getParameter("CM_CONTACT_NUMBER"))%>" size="15" maxlength="15">
          &nbsp;ext&nbsp;<input type="text" name="CM_EXTENSION" value="<%=UtilFormatOut.checkNull(useValues?telecomNumber.getString("extension"):request.getParameter("CM_EXTENSION"))%>" size="6" maxlength="10">
      </td>
    </tr>
  <%}else if("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))){%>
    <tr>
      <td width="26%"><div class="tabletext">Email address</div></td>
      <td width="74%">
          <input type="text" name="CM_INFO_STRING" value="<%=UtilFormatOut.checkNull(useValues?contactMech.getString("infoString"):request.getParameter("CM_INFO_STRING"))%>" size="40" maxlength="80"> *
      </td>
    </tr>
  <%}%>
    <tr>
      <td width="26%"><div class="tabletext">Allow Solicitation?</div></td>
      <td width="74%">
        <select name="CM_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(useValues?contactMech.getString("allowSolicitation"):request.getParameter("CM_ADDRESS_ALLOW_SOL"), "Y")%></option>
          <option></option><option>Y</option><option>N</option>
        </select>
      </td>
    </tr>
  </table>

    &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttontext">[Save]</a>
    <%--  <input type="image" value="[Save]" border="0" src="/commerce/images/btn_save.gif"> --%>
  </form>
  <%}%>
<%}%>

&nbsp;<a href="<%=response.encodeURL(controlPath + "/authview/" + donePage)%>" class="buttontext">[Back]</a>

<%@ include file="/includes/footer.jsp" %>
