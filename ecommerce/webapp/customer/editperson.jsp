<%
/**
 *  Title: Edit Person Page
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

<% pageContext.setAttribute("PageName", "Edit Person"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%
  boolean useValues = true;
  if(request.getAttribute("ERROR_MESSAGE") != null) useValues = false;

  String donePage = request.getParameter("DONE_PAGE");
  if(donePage == null || donePage.length() <= 0) donePage="viewprofile";

  GenericValue person = userLogin.getRelatedOne("Person");
%>
    <%if(person == null){%>
      <%useValues = false;%>
      <p class="head1">Add New Personal Information</p>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <form method="post" action="<%=response.encodeURL(controlPath + "/updateperson/" + donePage)%>" name="editpersonform">
        <input type=hidden name="UPDATE_MODE" value="CREATE">
    <%}else{%>
      <p class="head1">Edit Personal Information</p>
        <form method="post" action="<%=response.encodeURL(controlPath + "/updateperson/" + donePage)%>" name="editpersonform">
        <input type=hidden name="UPDATE_MODE" value="UPDATE">
    <%}%>

    <tr>
      <td width="26%"><div class="tabletext">Title</div></td>
      <td width="74%">
        <input type="text" name="PERSON_TITLE" value="<%=UtilFormatOut.checkNull(useValues?person.getString("personalTitle"):request.getParameter("PERSON_TITLE"))%>" size="10" maxlength="30">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">First name</div></td>
      <td width="74%">
        <input type="text" name="PERSON_FIRST_NAME" value="<%=UtilFormatOut.checkNull(useValues?person.getString("firstName"):request.getParameter("PERSON_FIRST_NAME"))%>" size="30" maxlength="30">
      *</td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Middle initial</div></td>
      <td width="74%">
          <input type="text" name="PERSON_MIDDLE_NAME" value="<%=UtilFormatOut.checkNull(useValues?person.getString("middleName"):request.getParameter("PERSON_MIDDLE_NAME"))%>" size="4" maxlength="4">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Last name </div></td>
      <td width="74%">
        <input type="text" name="PERSON_LAST_NAME" value="<%=UtilFormatOut.checkNull(useValues?person.getString("lastName"):request.getParameter("PERSON_LAST_NAME"))%>" size="30" maxlength="30">
      *</td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Suffix</div></td>
      <td width="74%">
        <input type="text" name="PERSON_SUFFIX" value="<%=UtilFormatOut.checkNull(useValues?person.getString("suffix"):request.getParameter("PERSON_SUFFIX"))%>" size="10" maxlength="30">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Nick Name</div></td>
      <td width="74%">
        <input type="text" name="PERSON_NICKNAME" value="<%=UtilFormatOut.checkNull(useValues?person.getString("nickname"):request.getParameter("PERSON_NICKNAME"))%>" size="30" maxlength="60">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Gender</div></td>
      <td width="74%">
        <select name="PERSON_GENDER">
          <option><%=UtilFormatOut.checkNull(useValues?person.getString("gender"):request.getParameter("PERSON_GENDER"))%></option>
          <option></option>
          <option>M</option>
          <option>F</option>
        </select>
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Birth Date</div></td>
      <td width="74%">
        <input type="text" name="PERSON_BIRTH_DATE" value="<%=UtilFormatOut.checkNull(useValues?UtilDateTime.toDateString(person.getTimestamp("birthDate")):request.getParameter("PERSON_BIRTH_DATE"))%>" size="11" maxlength="20">
        (MM/DD/YYYY)
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Height</div></td>
      <td width="74%">
        <input type="text" name="PERSON_HEIGHT" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(person.getDouble("height")):request.getParameter("PERSON_HEIGHT"))%>" size="30" maxlength="60">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Weight</div></td>
      <td width="74%">
        <input type="text" name="PERSON_WEIGHT" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(person.getDouble("weight")):request.getParameter("PERSON_WEIGHT"))%>" size="30" maxlength="60">
      </td>
    </tr>

    <tr>
      <td width="26%"><div class="tabletext">Mothers Maiden Name</div></td>
      <td width="74%">
        <input type="text" name="PERSON_MOTHERS_MAIDEN_NAME" value="<%=UtilFormatOut.checkNull(useValues?person.getString("mothersMaidenName"):request.getParameter("PERSON_MOTHERS_MAIDEN_NAME"))%>" size="30" maxlength="60">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Marital Status</div></td>
      <td width="74%">
        <input type="text" name="PERSON_MARITAL_STATUS" value="<%=UtilFormatOut.checkNull(useValues?person.getString("maritalStatus"):request.getParameter("PERSON_MARITAL_STATUS"))%>" size="30" maxlength="60">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Social Security Number</div></td>
      <td width="74%">
        <input type="text" name="PERSON_SOCIAL_SECURITY_NUMBER" value="<%=UtilFormatOut.checkNull(useValues?person.getString("socialSecurityNumber"):request.getParameter("PERSON_SOCIAL_SECURITY_NUMBER"))%>" size="30" maxlength="60">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Passport Number</div></td>
      <td width="74%">
        <input type="text" name="PERSON_PASSPORT_NUMBER" value="<%=UtilFormatOut.checkNull(useValues?person.getString("passportNumber"):request.getParameter("PERSON_PASSPORT_NUMBER"))%>" size="30" maxlength="60">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Passport Expire Date</div></td>
      <td width="74%">
        <input type="text" name="PERSON_PASSPORT_EXPIRE_DATE" value="<%=UtilFormatOut.checkNull(useValues?UtilDateTime.toDateString(person.getTimestamp("passportExpireDate")):request.getParameter("PERSON_PASSPORT_EXPIRE_DATE"))%>" size="11" maxlength="20">
        (MM/DD/YYYY)
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Total Years Work Experience</div></td>
      <td width="74%">
        <input type="text" name="PERSON_TOTAL_YEARS_WORK_EXPERIENCE" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(person.getDouble("totalYearsWorkExperience")):request.getParameter("PERSON_TOTAL_YEARS_WORK_EXPERIENCE"))%>" size="30" maxlength="60">
      </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext">Comment</div></td>
      <td width="74%">
        <input type="text" name="PERSON_COMMENT" value="<%=UtilFormatOut.checkNull(useValues?person.getString("comment"):request.getParameter("PERSON_COMMENT"))%>" size="30" maxlength="60">
      </td>
    </tr>

  </form>
  </table>

    &nbsp;<a href="<%=response.encodeURL(controlPath + "/authview/" + donePage)%>" class="buttontext">[Done]</a>
    &nbsp;<a href="javascript:document.editpersonform.submit()" class="buttontext">[Save]</a>
    <%--  <input type="image" value="[Save]" border="0" src="/commerce/images/btn_save.gif"> --%>

<%@ include file="/includes/footer.jsp" %>
