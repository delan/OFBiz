<%--
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
--%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<ofbiz:object name="userLogin" property="userLogin" type="org.ofbiz.core.entity.GenericValue" />  

<%
    GenericValue person = userLogin == null ? null : userLogin.getRelatedOne("Person");
    if (person != null) pageContext.setAttribute("person", person);

    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
    if(person == null) tryEntity = false;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));

    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) donePage="viewprofile";
    pageContext.setAttribute("donePage", donePage);
%>

<br>
<ofbiz:unless name="person">
  <p class="head1">Add New Personal Information</p>
    <FORM method=POST action='<ofbiz:url>/createPerson/<ofbiz:print attribute="donePage"/></ofbiz:url>' name="editpersonform">
</ofbiz:unless>
<ofbiz:if name="person">
  <p class="head1">Edit Personal Information</p>
    <FORM method=POST action='<ofbiz:url>/updatePerson/<ofbiz:print attribute="donePage"/></ofbiz:url>' name="editpersonform">
</ofbiz:if>

&nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
&nbsp;<a href="javascript:document.editpersonform.submit()" class="buttontext">[Save]</a>

<table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">Title</div></td>
      <td width="74%" align=left>
        <input type="text" size="10" maxlength="30" <ofbiz:inputvalue field="personalTitle" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">First name</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="30" <ofbiz:inputvalue field="firstName" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Middle initial</div></td>
      <td width="74%" align=left>
          <input type="text" size="4" maxlength="4" <ofbiz:inputvalue field="middleName" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Last name </div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="30" <ofbiz:inputvalue field="lastName" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Suffix</div></td>
      <td width="74%" align=left>
        <input type="text" size="10" maxlength="30" <ofbiz:inputvalue field="suffix" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Nick Name</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="nickname" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Gender</div></td>
      <td width="74%" align=left>
        <select name="gender">
          <option><ofbiz:inputvalue field="gender" entityAttr="person" tryEntityAttr="tryEntity"/></option>
          <option></option>
          <option>M</option>
          <option>F</option>
        </select>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Birth Date</div></td>
      <td width="74%" align=left>
        <input type="text" size="11" maxlength="20" <ofbiz:inputvalue field="birthDate" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
        (yyyy-MM-dd)
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Height</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="height" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Weight</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="weight" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>

    <tr>
      <td width="26%" align=right><div class="tabletext">Mothers Maiden Name</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="mothersMaidenName" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Marital Status</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="maritalStatus" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Social Security Number</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="socialSecurityNumber" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Passport Number</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="passportNumber" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Passport Expire Date</div></td>
      <td width="74%" align=left>
        <input type="text" size="11" maxlength="20" <ofbiz:inputvalue field="passportExpireDate" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
        (yyyy-MM-dd)
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Total Years Work Experience</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="totalYearsWorkExperience" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Comment</div></td>
      <td width="74%" align=left>
        <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="comments" entityAttr="person" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
</table>
</form>

&nbsp;<a href='<ofbiz:url>/authview/<ofbiz:print attribute="donePage"/></ofbiz:url>' class="buttontext">[Go&nbsp;Back]</a>
&nbsp;<a href="javascript:document.editpersonform.submit()" class="buttontext">[Save]</a>
