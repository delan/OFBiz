<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones (jonesde@ofbiz.org) 
 *@version    $Revision: 1.2 $
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if person?exists>
  <p class="head1">${uiLabelMap.CustomerEditPersonalInfo}</p>
    <form method=POST action="<@ofbizUrl>/updatePerson/${donePage}</@ofbizUrl>" name="editpersonform">
<#else>
  <p class="head1">${uiLabelMap.CustomerAddNewPersonalInfo}</p>
    <form method=POST action="<@ofbizUrl>/createPerson/${donePage}</@ofbizUrl>" name="editpersonform">
</#if>

&nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CustomerGoBack}]</a>
&nbsp;<a href="javascript:document.editpersonform.submit()" class="buttontext">[${uiLabelMap.CustomerSave}]</a>

<table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerTitle}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="10" maxlength="30" name="personalTitle" value="${personData.personalTitle?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerFirstName}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="30" name="firstName" value="${personData.firstName?if_exists}"/>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerMiddleInitial}</div></td>
      <td width="74%" align=left>
          <input type="text" class='inputBox' size="4" maxlength="4" name="middleName" value="${personData.middleName?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerLastName} </div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="30" name="lastName" value="${personData.lastName?if_exists}"/>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerSuffix}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="10" maxlength="30" name="suffix" value="${personData.suffix?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerNickName}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="60" name="nickname" value="${personData.nickname?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerGender}</div></td>
      <td width="74%" align=left>
        <select name="gender" class='selectBox'>
          <option>${personData.gender?if_exists}</option>
          <option></option>
          <option>M</option>
          <option>F</option>
        </select>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerBirthDate}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="11" maxlength="20" name="birthDate" value="${personData.birthDate?if_exists}"/>
        (yyyy-MM-dd)
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerHeight}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="60" name="height" value="${personData.height?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerWeight}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="60" name="weight" value="${personData.weight?if_exists}"/>
      </td>
    </tr>

    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerMaidenName}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="60" name="mothersMaidenName" value="${personData.mothersMaidenName?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerMaritalStatus}</div></td>
      <td width="74%" align=left>
        <select name="maritalStatus" class='selectBox'>
          <option>${personData.maritalStatus?if_exists}</option>
          <option></option>
          <option value="S">${uiLabelMap.CustomerSingle}</option>
          <option value="M">${uiLabelMap.CustomerMarried}</option>
          <option value="D">${uiLabelMap.CustomerDivorced}</option>
        </select>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerSocialSecurityNumber}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="60" name="socialSecurityNumber" value="${personData.socialSecurityNumber?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerPassportNumber}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="60" name="passportNumber" value="${personData.passportNumber?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerPassportExpireDate}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="11" maxlength="20" name="passportExpireDate" value="${personData.passportExpireDate?if_exists}"/>
        (yyyy-MM-dd)
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerTotalYearsWorkExperience}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="60" name="totalYearsWorkExperience" value="${personData.totalYearsWorkExperience?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">${uiLabelMap.CustomerComment}</div></td>
      <td width="74%" align=left>
        <input type="text" class='inputBox' size="30" maxlength="60" name="comments" value="${personData.comments?if_exists}"/>
      </td>
    </tr>
</table>
</form>

