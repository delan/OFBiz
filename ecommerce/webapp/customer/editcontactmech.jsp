<%
/**
 *  Title: Edit Contact Mechanism Page
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
 *@created    Aug 22 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<% pageContext.setAttribute("PageName", "Edit Contact Mechanism"); %>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
    if("true".equals(request.getParameter("tryEntity"))) tryEntity = true;

    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) donePage="viewprofile";

    String contactMechId = request.getParameter("contactMechId");
    if(request.getAttribute("contactMechId") != null)
        contactMechId = (String) request.getAttribute("contactMechId");

    //try to find a PartyContactMech with a valid date range
    Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId)));
    GenericValue partyContactMech = EntityUtil.getFirst(partyContactMechs);
    if (partyContactMech != null) {
        request.setAttribute("partyContactMech", partyContactMech);
    }

    GenericValue contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
    String contactMechTypeId = null;
    if (contactMech != null) {
        pageContext.setAttribute("contactMech", contactMech);
        contactMechTypeId = contactMech.getString("contactMechTypeId");
    }
    if (contactMechTypeId != null)
        pageContext.setAttribute("contactMechTypeId", contactMechTypeId);
    
    String requestName;
    if (contactMech == null) {
        //create
        if ("POSTAL_ADDRESS".equals(contactMechTypeId)) {
            requestName = "createPostalAddress";
        } else if ("TELECOM_NUMBER".equals(contactMechTypeId)) {
            requestName = "createTelecomNumber";
        } else if ("EMAIL_ADDRESS".equals(contactMechTypeId)) {
            requestName = "createEmailAddress";
        } else {
            requestName = "createContactMech";
        }
    } else {
        //update
        if ("POSTAL_ADDRESS".equals(contactMechTypeId)) {
            requestName = "updatePostalAddress";
        } else if ("TELECOM_NUMBER".equals(contactMechTypeId)) {
            requestName = "updateTelecomNumber";
        } else if ("EMAIL_ADDRESS".equals(contactMechTypeId)) {
            requestName = "updateEmailAddress";
        } else {
            requestName = "updateContactMech";
        }
    }

    
    if ("POSTAL_ADDRESS".equals(contactMechTypeId)) {
        GenericValue postalAddress = null;
        if (contactMech != null) postalAddress = contactMech.getRelatedOne("PostalAddress");
        if (postalAddress == null) tryEntity = false;
        if (postalAddress != null) pageContext.setAttribute("postalAddress", postalAddress);
    } else if ("TELECOM_NUMBER".equals(contactMechTypeId)) {
        GenericValue telecomNumber = null;
        if (contactMech != null) telecomNumber = contactMech.getRelatedOne("TelecomNumber");
        if (telecomNumber == null) tryEntity = false;
        if (telecomNumber != null) pageContext.setAttribute("telecomNumber", telecomNumber);
    }

    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));
%>

<%if (!security.hasPermission("USER_ADMIN", session) && partyContactMech == null && contactMech != null){%>
  <p><h3>The contact information specified does not belong to you, you may not view or edit it.</h3></p>
  &nbsp;<a href="<ofbiz:url><%="/authview/" + donePage%></ofbiz:url>" class="buttontext">[Back]</a>
<%} else {%>

  <%if (contactMech == null) {%>
    <%if (request.getParameter("contactMechTypeId") == null) {%>
    <p class="head1">Create New Contact Information</p>
    <form method="post" action="<ofbiz:url>/editcontactmech?DONE_PAGE=<%=donePage%></ofbiz:url>" name="createcontactmechform">
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <tr>
          <td width="26%"><div class="tabletext">Select Contact Type:</div></td>
          <td width="74%">
            <select name="contactMechTypeId">
              <%Iterator contactMechTypes = UtilMisc.toIterator(delegator.findAll("ContactMechType", null));%>
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
      <%contactMechTypeId = request.getParameter("contactMechTypeId");%>
      <%pageContext.setAttribute("tryEntity", new Boolean(false));%>
    <%}%>
  <%}%>

  <%if (contactMechTypeId != null) {%>
    <%if (contactMech == null) {%>
      <p class="head1">Create New Contact Information</p>
    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Done/Cancel]</a>
    &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttontext">[Save]</a>
      <%String cmNewPurposeTypeId = request.getParameter("contactMechPurposeTypeId");%>
      <%if (cmNewPurposeTypeId != null){%>
        <%GenericValue contactMechPurposeType = delegator.findByPrimaryKey("ContactMechPurposeType", UtilMisc.toMap("contactMechPurposeTypeId", cmNewPurposeTypeId));%>
        <%if (contactMechPurposeType != null){%>
        <div>(Note: this new contact information will have the purpose <b>"<%=UtilFormatOut.checkNull(contactMechPurposeType.getString("description"))%>"</b>)</div>
        <%} else { cmNewPurposeTypeId = null; }%>
      <%}%>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <%-- <form method="post" action="<%=response.encodeURL(controlPath + "/updatecontactmech/" + donePage)%>" name="editcontactmechform"> --%>
        <form method="post" action="<ofbiz:url>/<%=requestName%></ofbiz:url>" name="editcontactmechform">
        <input type=hidden name="DONE_PAGE" value="<%=donePage%>">
        <input type=hidden name="contactMechTypeId" value="<%=contactMechTypeId%>">
        <%=UtilFormatOut.ifNotEmpty(cmNewPurposeTypeId, "<input type='hidden' name='contactMechPurposeTypeId' value='", "'>")%>
    <%} else {%>
      <p class="head1">Edit Contact Information</p>
    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Done/Cancel]</a>
    &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttontext">[Save]</a>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Contact Purposes</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <table border='0' cellspacing='1' bgcolor='black'>
              <%Iterator partyContactMechPurposesIter = UtilMisc.toIterator(EntityUtil.filterByDate(partyContactMech.getRelated("PartyContactMechPurpose")));%>
              <%while (partyContactMechPurposesIter != null && partyContactMechPurposesIter.hasNext()){%>
                <%GenericValue partyContactMechPurpose = (GenericValue)partyContactMechPurposesIter.next();%>
                <%if (partyContactMechPurpose != null) {%>
                    <%GenericValue contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType");%>
                    <tr>
                      <td bgcolor='white'>
                        <div class="tabletext">&nbsp;
                          <%if(contactMechPurposeType != null) {%>
                            <b><%=contactMechPurposeType.getString("description")%></b>
                          <%}else{%>
                            <b>Purpose Type not found with ID: "<%=partyContactMechPurpose.getString("contactMechPurposeTypeId")%>"</b>
                          <%}%>
                          (Since:<%=UtilDateTime.toDateString(partyContactMechPurpose.getTimestamp("fromDate"))%>)
                          <%=UtilFormatOut.ifNotEmpty(UtilDateTime.toDateTimeString(partyContactMechPurpose.getTimestamp("thruDate")), "(Expires:", ")")%>
                        &nbsp;</div></td>
                      <td bgcolor='white'><div><a href='<ofbiz:url><%="/deletePartyContactMechPurpose?contactMechId=" + contactMechId + "&contactMechPurposeTypeId=" + partyContactMechPurpose.getString("contactMechPurposeTypeId") + "&fromDate=" + UtilFormatOut.encodeQueryValue(partyContactMechPurpose.getTimestamp("fromDate").toString()) + "&DONE_PAGE=" + donePage + "&useValues=true"%></ofbiz:url>' class='buttontext'>&nbsp;Delete&nbsp;</a></div></td>
                    </tr>
                <%}%>
              <%}%>
              <%Iterator purposeTypes = UtilMisc.toIterator(delegator.findByAnd("ContactMechTypePurpose", UtilMisc.toMap("contactMechTypeId", contactMechTypeId), null));%>
              <%if(purposeTypes != null && purposeTypes.hasNext()){%>
              <tr>
                <form method=POST action='<ofbiz:url><%="/createPartyContactMechPurpose?contactMechId=" + contactMechId + "&DONE_PAGE=" + donePage + "&useValues=true"%></ofbiz:url>' name='newpurposeform'>
                  <td bgcolor='white'>
                    <SELECT name='contactMechPurposeTypeId'>
                      <OPTION></OPTION>
                      <%while(purposeTypes != null && purposeTypes.hasNext()){%>
                        <%GenericValue contactMechTypePurpose = (GenericValue)purposeTypes.next();%>
                        <%GenericValue contactMechPurposeType = contactMechTypePurpose.getRelatedOne("ContactMechPurposeType");%>
                        <%if(contactMechPurposeType != null){%>
                          <OPTION value='<%=contactMechPurposeType.getString("contactMechPurposeTypeId")%>'><%=contactMechPurposeType.getString("description")%></OPTION>
                        <%}%>
                      <%}%>
                    </SELECT>
                  </td>
                </form>
                <td bgcolor='white'><div><a href='javascript:document.newpurposeform.submit()' class='buttontext'>&nbsp;Add&nbsp;Purpose&nbsp;</a></div></td>
              </tr>
              <%}%>
            </table>
          </td>
        </tr>
        <%-- <form method="post" action="<%=response.encodeURL(controlPath + "/updatecontactmech/" + donePage)%>" name="editcontactmechform"> --%>
        <form method="post" action="<ofbiz:url>/<%=requestName%></ofbiz:url>" name="editcontactmechform">
        <input type=hidden name="DONE_PAGE" value="<%=donePage%>">
        <input type=hidden name="contactMechId" value="<%=contactMechId%>">
    <%}%>

  <%if ("POSTAL_ADDRESS".equals(contactMechTypeId)) {%>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">To Name</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="toName" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Attention Name</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="attnName" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Address Line 1</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="30" maxlength="30" <ofbiz:inputvalue field="address1" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Address Line 2</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
          <input type="text" size="30" maxlength="30" <ofbiz:inputvalue field="address2" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">City</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
          <input type="text" size="30" maxlength="30" <ofbiz:inputvalue field="city" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">State/Province</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select name="CM_STATE">
          <option><ofbiz:inputvalue field="stateProvinceGeoId" entityAttr="postalAddress" tryEntityAttr="tryEntity"/></option>
          <option></option>
          <%@ include file="/includes/states.jsp" %>
        </select>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Zip/Postal Code</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="12" maxlength="10" <ofbiz:inputvalue field="postalCode" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Country</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select name="CM_COUNTRY" >
          <option><ofbiz:inputvalue field="countryGeoId" entityAttr="postalAddress" tryEntityAttr="tryEntity"/></option>
          <option></option>
          <%@ include file="/includes/countries.jsp" %>
        </select>
      *</td>
    </tr>
  <%} else if("TELECOM_NUMBER".equals(contactMechTypeId)) {%>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Phone Number</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" size="4" maxlength="10" <ofbiz:inputvalue field="countryCode" entityAttr="telecomNumber" tryEntityAttr="tryEntity" fullattrs="true"/>">
        -&nbsp;<input type="text" size="4" maxlength="10" <ofbiz:inputvalue field="areaCode" entityAttr="telecomNumber" tryEntityAttr="tryEntity" fullattrs="true"/>>
        -&nbsp;<input type="text" size="15" maxlength="15" <ofbiz:inputvalue field="contactNumber" entityAttr="telecomNumber" tryEntityAttr="tryEntity" fullattrs="true"/>>
        &nbsp;ext&nbsp;<input type="text" size="6" maxlength="10" <ofbiz:inputvalue field="extension" entityAttr="partyContactMech" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext"></div></td>
      <td width="5">&nbsp;</td>
      <td><div class="tabletext">[Country Code] [Area Code] [Contact Number] [Extension]</div></td>
    </tr>
  <%} else if("EMAIL_ADDRESS".equals(contactMechTypeId)) {%>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Email address</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
          <input type="text" size="60" maxlength="255" <ofbiz:inputvalue field="infoString" param="emailAddress" entityAttr="contactMech" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
  <%} else {%>
    <%GenericValue curContactMechType = delegator.findByPrimaryKey("ContactMechType", UtilMisc.toMap("contactMechTypeId", contactMechTypeId));%>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext"><%=curContactMechType.getString("description")%></div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
          <input type="text" size="60" maxlength="255" <ofbiz:inputvalue field="infoString" entityAttr="contactMech" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
  <%}%>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Allow Solicitation?</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select name="allowSolicitation">
          <option><ofbiz:inputvalue field="allowSolicitation" entityAttr="partyContactMech" tryEntityAttr="tryEntity"/></option>
          <option></option><option>Y</option><option>N</option>
        </select>
      </td>
    </tr>
  </form>
  </table>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Done/Cancel]</a>
    &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttontext">[Save]</a>
  <%}else{%>
    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Done/Cancel]</a>
  <%}%>
<%}%>


<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
