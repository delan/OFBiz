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
<%@ page import="org.ofbiz.core.entity.*, org.ofbiz.commonapp.party.contact.*" %>

<% pageContext.setAttribute("PageName", "Edit Contact Mechanism"); %>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%ContactMechWorker.getContactMechAndRelated(pageContext, "contactMech", "contactMechId", "partyContactMech", "partyContactMechPurposes",
    "contactMechTypeId", "contactMechType", "purposeTypes", "postalAddress", "telecomNumber", "requestName", "tryEntity");%>

<%if (!security.hasPermission("USER_ADMIN", session) && pageContext.getAttribute("partyContactMech") == null && pageContext.getAttribute("contactMech") != null){%>
  <p><h3>The contact information specified does not belong to you, you may not view or edit it.</h3></p>
  &nbsp;<a href="<ofbiz:url>/authview/"<ofbiz:print attribute="donePage"/></ofbiz:url>" class="buttontext">[Back]</a>
<%} else {%>

  <ofbiz:unless name="contactMech">
    <%-- When creating a new contact mech, first select the type, then actually create --%>
    <%if (request.getParameter("preContactMechTypeId") == null) {%>
    <p class="head1">Create New Contact Information</p>
    <form method="post" action='<ofbiz:url>/editcontactmech?DONE_PAGE=<ofbiz:print attribute="donePage"/></ofbiz:url>' name="createcontactmechform">
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <tr>
          <td width="26%"><div class="tabletext">Select Contact Type:</div></td>
          <td width="74%">
            <select name="preContactMechTypeId">
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
    <%}%>
  </ofbiz:unless>

  <ofbiz:if name="contactMechTypeId">
    <ofbiz:unless name="contactMech">
      <p class="head1">Create New Contact Information</p>
    &nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Done/Cancel]</a>
    &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttontext">[Save]</a>
      <%String cmNewPurposeTypeId = request.getParameter("contactMechPurposeTypeId");%>
      <%if (cmNewPurposeTypeId != null){%>
        <%GenericValue contactMechPurposeType = delegator.findByPrimaryKey("ContactMechPurposeType", UtilMisc.toMap("contactMechPurposeTypeId", cmNewPurposeTypeId));%>
        <%if (contactMechPurposeType != null){%>
        <div>(Note: this new contact information will have the purpose <b>"<%=UtilFormatOut.checkNull(contactMechPurposeType.getString("description"))%>"</b>)</div>
        <%} else { cmNewPurposeTypeId = null; }%>
      <%}%>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <form method="post" action='<ofbiz:url>/<ofbiz:print attribute="requestName"/></ofbiz:url>' name="editcontactmechform">
        <input type=hidden name='DONE_PAGE' value='<ofbiz:print attribute="donePage"/>'>
        <input type=hidden name='contactMechTypeId' value='<ofbiz:print attribute="contactMechTypeId"/>'>
        <%=UtilFormatOut.ifNotEmpty(cmNewPurposeTypeId, "<input type='hidden' name='contactMechPurposeTypeId' value='", "'>")%>
    </ofbiz:unless>
    <ofbiz:if name="contactMech">
      <p class="head1">Edit Contact Information</p>
    &nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class='buttontext'>[Done/Cancel]</a>
    &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttontext">[Save]</a>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Contact Purposes</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <table border='0' cellspacing='1' bgcolor='black'>
              <ofbiz:iterator name="partyContactMechPurpose" property="partyContactMechPurposes">
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
                  <td bgcolor='white'><div><a href='<ofbiz:url>/deletePartyContactMechPurpose?contactMechId=<ofbiz:print attribute="contactMechId"/>&contactMechPurposeTypeId=<%=partyContactMechPurpose.getString("contactMechPurposeTypeId")%>&fromDate=<%=UtilFormatOut.encodeQueryValue(partyContactMechPurpose.getTimestamp("fromDate").toString())%>&DONE_PAGE=<ofbiz:print attribute="donePage"/>&useValues=true</ofbiz:url>' class='buttontext'>&nbsp;Delete&nbsp;</a></div></td>
                </tr>
              </ofbiz:iterator>
              <ofbiz:if name="purposeTypes" size="0">
              <tr>
                <form method=POST action='<ofbiz:url>/createPartyContactMechPurpose?contactMechId=<ofbiz:print attribute="contactMechId"/>&DONE_PAGE=<ofbiz:print attribute="donePage"/>&useValues=true</ofbiz:url>' name='newpurposeform'>
                  <td bgcolor='white'>
                    <SELECT name='contactMechPurposeTypeId'>
                      <OPTION></OPTION>
                      <ofbiz:iterator name="contactMechPurposeType" property="purposeTypes">
                        <OPTION value='<ofbiz:entityfield attribute="contactMechPurposeType" field="contactMechPurposeTypeId"/>'><ofbiz:entityfield attribute="contactMechPurposeType" field="description"/></OPTION>
                      </ofbiz:iterator>
                    </SELECT>
                  </td>
                </form>
                <td bgcolor='white'><div><a href='javascript:document.newpurposeform.submit()' class='buttontext'>&nbsp;Add&nbsp;Purpose&nbsp;</a></div></td>
              </tr>
              </ofbiz:if>
            </table>
          </td>
        </tr>
        <form method="post" action='<ofbiz:url>/<ofbiz:print attribute="requestName"/></ofbiz:url>' name="editcontactmechform">
        <input type=hidden name="DONE_PAGE" value='<ofbiz:print attribute="donePage"/>'>
        <input type=hidden name="contactMechId" value='<ofbiz:print attribute="contactMechId"/>'>
    </ofbiz:if>

  <%if ("POSTAL_ADDRESS".equals(pageContext.getAttribute("contactMechTypeId"))) {%>
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
        <select name="stateProvinceGeoId">
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
        <select name="countryGeoId" >
          <option><ofbiz:inputvalue field="countryGeoId" entityAttr="postalAddress" tryEntityAttr="tryEntity"/></option>
          <option></option>
          <%@ include file="/includes/countries.jsp" %>
        </select>
      *</td>
    </tr>
  <%} else if("TELECOM_NUMBER".equals(pageContext.getAttribute("contactMechTypeId"))) {%>
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
  <%} else if("EMAIL_ADDRESS".equals(pageContext.getAttribute("contactMechTypeId"))) {%>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">Email address</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
          <input type="text" size="60" maxlength="255" <ofbiz:inputvalue field="infoString" param="emailAddress" entityAttr="contactMech" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
  <%} else {%>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext"><ofbiz:entityfield attribute="contactMechType" field="description"/></div></td>
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

    &nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Done/Cancel]</a>
    &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttontext">[Save]</a>
  </ofbiz:if>
  <ofbiz:unless name="contactMechTypeId">
    &nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Done/Cancel]</a>
  </ofbiz:unless>
<%}%>


<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
