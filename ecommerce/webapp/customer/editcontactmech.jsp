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
  String donePage = request.getParameter("DONE_PAGE");
  if(donePage == null || donePage.length() <= 0) donePage="viewprofile";

  String contactMechId = request.getParameter("CONTACT_MECH_ID");
  GenericValue partyContactMech = helper.findByPrimaryKey("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId));
  GenericValue contactMech = helper.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
%>

<p class="head1">Edit Contact Information</p>
<%if(contactMech == null){%>
<p><h3>ERROR: Contact information with ID <%=contactMechId%> not found!</h3></p>
<%}else{%>
  <%if(!Security.hasPermission("USER_ADMIN", session) && partyContactMech == null){%>
  <p><h3>The contact information specified does not belong to you, you may not view or edit it.</h3></p>
  <%}else{%>
<form method="post" action="<%=response.encodeURL(controlPath + "/updatecontactmech/" + donePage)%>" name="editcontactmechform">
<input type=hidden name="<%=HttpRequestConstants.ADDRESS_KEY%>" value="<%=addressIdString%>">

<% String fontColor = "Black"; %>

<table width="90%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Suffix</font></div></td>
    <td width="74%">
      <input type="text" name="USER_SUFFIX" value="<%=UtilFormatOut.checkNull(request.getParameter("USER_SUFFIX"))%>" size="10" maxlength="30">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Address Line 1</font></div></td>
    <td width="74%">
      <input type="text" name="CUSTOMER_ADDRESS1" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_ADDRESS1"))%>" size="30" maxlength="30">
    *</td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Address Line 2</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_ADDRESS2" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_ADDRESS2"))%>" size="30" maxlength="30">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>City</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_CITY" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_CITY"))%>" size="30" maxlength="30">
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>State/Province</font></div></td>
    <td width="74%">
      <select name="CUSTOMER_STATE">
          <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_STATE"))%></option>
          <%@ include file="/includes/states.jsp" %>
      </select>
      * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Zip/Postal Code</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_POSTAL_CODE" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_POSTAL_CODE"))%>" size="12" maxlength="10">
    * </td>
  </tr>
  <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Country</font></div></td>
      <td width="74%">
          <select name="CUSTOMER_COUNTRY" >
            <%@ include file="/includes/countries.jsp" %>
          </select>
      * </td>
  </tr>
  <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Allow Address Solicitation?</font></div></td>
      <td width="74%">
        <select name="CUSTOMER_ADDRESS_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_ADDRESS_ALLOW_SOL"), "Y")%></option>
          <option></option><option>Y</option><option>N</option>
        </select>
      </td>
  </tr>
  <tr>
    <td colspan='2' height='2' bgcolor='#CCCCCC'></td>
  </tr>
  <tr>
    <td colspan='2'>All phone numbers: [Country Code] [Area Code] [Contact Number] [Extension]</td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Home phone<BR>(allow solicitation?)</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_HOME_COUNTRY" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_COUNTRY"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_HOME_AREA" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_AREA"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_HOME_CONTACT" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_CONTACT"))%>" size="15" maxlength="15">
        &nbsp;ext&nbsp;<input type="text" name="CUSTOMER_HOME_EXT" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_EXT"))%>" size="6" maxlength="10">
        <BR>
        <select name="CUSTOMER_HOME_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_ALLOW_SOL"), "Y")%></option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
</table>

  &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttonlink">[Save]</a>
  <%--  <input type="image" value="[Save]" border="0" src="/commerce/images/btn_save.gif"> --%>
</form>
  <%}%>
<%}%>

&nbsp;<a href="<%=response.encodeURL(controlPath + "/authview/" + donePage)%>" class="buttonlink">[Back]</a>

<%@ include file="/includes/footer.jsp" %>











