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
 *@author     David E. Jones
 *@version    1.0
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#if getUsername>
<script language="JavaScript">
 <!--
     lastFocusedName = null;
     function setLastFocused(formElement) {
         lastFocusedName = formElement.name;
     }
     function clickUsername() {
         if (document.forms["newuserform"].elements["UNUSEEMAIL"].checked) {
             if (lastFocusedName == "UNUSEEMAIL") {
                 document.forms["newuserform"].elements["PASSWORD"].focus();
             } else if (lastFocusedName == "PASSWORD") {
                 document.forms["newuserform"].elements["UNUSEEMAIL"].focus();
             } else {
                 document.forms["newuserform"].elements["PASSWORD"].focus();
             }
         }
     }
     function changeEmail() {
         if (document.forms["newuserform"].elements["UNUSEEMAIL"].checked) {
             document.forms["newuserform"].elements["USERNAME"].value=document.forms["newuserform"].elements["CUSTOMER_EMAIL"].value;
         }
     }
     function setEmailUsername() {
         if (document.forms["newuserform"].elements["UNUSEEMAIL"].checked) {
             document.forms["newuserform"].elements["USERNAME"].value=document.forms["newuserform"].elements["CUSTOMER_EMAIL"].value;
             // don't disable, make the browser not submit the field: document.forms["newuserform"].elements["USERNAME"].disabled=true;
         } else {
             document.forms["newuserform"].elements["USERNAME"].value='';
             // document.forms["newuserform"].elements["USERNAME"].disabled=false;
         }
     }
 //-->
</script>
</#if>

<p class="head1">${uiLabelMap.PartyRequestNewAccount}</p>
<br/>
<p class='tabletext'>${uiLabelMap.PartyAlreadyHaveAccount}, <a href='<@ofbizUrl>/checkLogin/main</@ofbizUrl>' class='buttontext'>${uiLabelMap.CommonLoginHere}</a>.</p>

<#macro fieldErrors fieldName>
  <#if requestAttributes.errorMsgListReq?has_content>
    <#assign fieldMessages = Static["org.ofbiz.base.util.MessageString"].getMessagesForField(fieldName, true, requestAttributes.errorMsgListReq)>
    <ul>
      <#list fieldMessages as errorMsg>
        <li class="errorMessage">${errorMsg}</li>
      </#list>
    </ul>
  </#if>
</#macro>
<#macro fieldErrorsMulti fieldName1 fieldName2 fieldName3 fieldName4>
  <#if requestAttributes.errorMsgListReq?has_content>
    <#assign fieldMessages = Static["org.ofbiz.base.util.MessageString"].getMessagesForField(fieldName1, fieldName2, fieldName3, fieldName4, true, requestAttributes.errorMsgListReq)>
    <ul>
      <#list fieldMessages as errorMsg>
        <li class="errorMessage">${errorMsg}</li>
      </#list>
    </ul>
  </#if>
</#macro>

<form method="post" action="<@ofbizUrl>/createcustomer${previousParams}</@ofbizUrl>" name="newuserform" style='margin:0;'>
<input type="hidden" name="productStoreId" value="${productStoreId}">
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class='boxhead'>&nbsp;${uiLabelMap.PartyNameAndShippingAddress}</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.CommonTitle}</div></td>
    <td width="74%">
      <@fieldErrors fieldName="USER_TITLE"/>
      <input type="text" class='inputBox' name="USER_TITLE" value="${requestParameters.USER_TITLE?if_exists}" size="10" maxlength="30">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyFirstName}</div></td>
    <td width="74%">
      <@fieldErrors fieldName="USER_FIRST_NAME"/>
      <input type="text" class='inputBox' name="USER_FIRST_NAME" value="${requestParameters.USER_FIRST_NAME?if_exists}" size="30" maxlength="30">
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyMiddleInitial}</div></td>
    <td width="74%">
      <@fieldErrors fieldName="USER_MIDDLE_NAME"/>
      <input type="text" class='inputBox' name="USER_MIDDLE_NAME" value="${requestParameters.USER_MIDDLE_NAME?if_exists}" size="4" maxlength="4">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyLastName} </div></td>
    <td width="74%">
      <@fieldErrors fieldName="USER_LAST_NAME"/>
      <input type="text" class='inputBox' name="USER_LAST_NAME" value="${requestParameters.USER_LAST_NAME?if_exists}" size="30" maxlength="30">
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartySuffix}</div></td>
    <td width="74%">
      <@fieldErrors fieldName="USER_SUFFIX"/>
      <input type="text" class='inputBox' name="USER_SUFFIX" value="${requestParameters.USER_SUFFIX?if_exists}" size="10" maxlength="30">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyAddressLine1}</div></td>
    <td width="74%">
      <@fieldErrors fieldName="CUSTOMER_ADDRESS1"/>
      <input type="text" class='inputBox' name="CUSTOMER_ADDRESS1" value="${requestParameters.CUSTOMER_ADDRESS1?if_exists}" size="30" maxlength="30">
    *</td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyAddressLine2}</div></td>
    <td width="74%">
      <@fieldErrors fieldName="CUSTOMER_ADDRESS2"/>
      <input type="text" class='inputBox' name="CUSTOMER_ADDRESS2" value="${requestParameters.CUSTOMER_ADDRESS2?if_exists}" size="30" maxlength="30">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyCity}</div></td>
    <td width="74%">
      <@fieldErrors fieldName="CUSTOMER_CITY"/>
      <input type="text" class='inputBox' name="CUSTOMER_CITY" value="${requestParameters.CUSTOMER_CITY?if_exists}" size="30" maxlength="30">
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyState}</div></td>
    <td width="74%">
      <@fieldErrors fieldName="CUSTOMER_STATE"/>
      <select name="CUSTOMER_STATE" class='selectBox'>
          <#if requestParameters.CUSTOMER_STATE?exists><option value='${requestParameters.CUSTOMER_STATE}'>${selectedStateName?default(requestParameters.CUSTOMER_STATE)}</option></#if>
          <option value="">${uiLabelMap.PartyNoState}</option>          
          <#include "../includes/states.ftl">
      </select>
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyZipCode}</div></td>
    <td width="74%">
      <@fieldErrors fieldName="CUSTOMER_POSTAL_CODE"/>
      <input type="text" class='inputBox' name="CUSTOMER_POSTAL_CODE" value="${requestParameters.CUSTOMER_POSTAL_CODE?if_exists}" size="12" maxlength="10">
    * </td>
  </tr>
  <tr>
      <td width="26%"><div class="tabletext">${uiLabelMap.PartyCountry}</div></td>
      <td width="74%">
      <@fieldErrors fieldName="CUSTOMER_COUNTRY"/>
          <select name="CUSTOMER_COUNTRY" class='selectBox'>
            <#if requestParameters.CUSTOMER_COUNTRY?exists><option value='${requestParameters.CUSTOMER_COUNTRY}'>${selectedCountryName?default(requestParameters.CUSTOMER_COUNTRY)}</option></#if>
            <#include "../includes/countries.ftl">
          </select>
      * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyAllowAddressSolicitation}?</div></td>
    <td width="74%">
      <select name="CUSTOMER_ADDRESS_ALLOW_SOL" class='selectBox'>
        <option>${requestParameters.CUSTOMER_ADDRESS_ALLOW_SOL?default("Y")}</option>
        <option></option><option>Y</option><option>N</option>
      </select>
    </td>
  </tr>
</table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<br/>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class='boxhead'>&nbsp;${uiLabelMap.PartyPhoneNumbers}</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
<table width="100%">
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyAllPhoneNumbers}:</div></td>
    <td width="74%"><div class="tabletext">[${uiLabelMap.PartyCountry}] [${uiLabelMap.PartyAreaCode}] [${uiLabelMap.PartyContactNumber}] [${uiLabelMap.PartyExtension}]</div></td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyHomePhone}<BR>(${uiLabelMap.PartyAllowSolicitation}?)</div></td>
    <td width="74%">
      <@fieldErrorsMulti fieldName1="CUSTOMER_HOME_COUNTRY" fieldName2="CUSTOMER_HOME_AREA" fieldName3="CUSTOMER_HOME_CONTACT" fieldName4="CUSTOMER_HOME_EXT"/>
        <input type="text" class='inputBox' name="CUSTOMER_HOME_COUNTRY" value="${requestParameters.CUSTOMER_HOME_COUNTRY?if_exists}" size="4" maxlength="10">
        -&nbsp;<input type="text" class='inputBox' name="CUSTOMER_HOME_AREA" value="${requestParameters.CUSTOMER_HOME_AREA?if_exists}" size="4" maxlength="10">
        -&nbsp;<input type="text" class='inputBox' name="CUSTOMER_HOME_CONTACT" value="${requestParameters.CUSTOMER_HOME_CONTACT?if_exists}" size="15" maxlength="15">
        &nbsp;ext&nbsp;<input type="text" class='inputBox' name="CUSTOMER_HOME_EXT" value="${requestParameters.CUSTOMER_HOME_EXT?if_exists}" size="6" maxlength="10">
        <BR>
        <select name="CUSTOMER_HOME_ALLOW_SOL" class='selectBox'>
          <option>${requestParameters.CUSTOMER_HOME_ALLOW_SOL?default("Y")}</option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyBusinessPhone}<BR>(${uiLabelMap.PartyAllowSolicitation}?)</div></td>
    <td width="74%">
      <@fieldErrorsMulti fieldName1="CUSTOMER_WORK_COUNTRY" fieldName2="CUSTOMER_WORK_AREA" fieldName3="CUSTOMER_WORK_CONTACT" fieldName4="CUSTOMER_WORK_EXT"/>
        <input type="text" class='inputBox' name="CUSTOMER_WORK_COUNTRY" value="${requestParameters.CUSTOMER_WORK_COUNTRY?if_exists}" size="4" maxlength="10">
        -&nbsp;<input type="text" class='inputBox' name="CUSTOMER_WORK_AREA" value="${requestParameters.CUSTOMER_WORK_AREA?if_exists}" size="4" maxlength="10">
        -&nbsp;<input type="text" class='inputBox' name="CUSTOMER_WORK_CONTACT" value="${requestParameters.CUSTOMER_WORK_CONTACT?if_exists}" size="15" maxlength="15">
        &nbsp;ext&nbsp;<input type="text" class='inputBox' name="CUSTOMER_WORK_EXT" value="${requestParameters.CUSTOMER_WORK_EXT?if_exists}" size="6" maxlength="10">
        <BR>
        <select name="CUSTOMER_WORK_ALLOW_SOL" class='selectBox'>
          <option>${requestParameters.CUSTOMER_WORK_ALLOW_SOL?default("Y")}</option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyFaxNumber}<BR>(${uiLabelMap.PartyAllowSolicitation}?)</div></td>
    <td width="74%">
      <@fieldErrorsMulti fieldName1="CUSTOMER_FAX_COUNTRY" fieldName2="CUSTOMER_FAX_AREA" fieldName3="CUSTOMER_FAX_CONTACT" fieldName4=""/>
        <input type="text" class='inputBox' name="CUSTOMER_FAX_COUNTRY" value="${requestParameters.CUSTOMER_FAX_COUNTRY?if_exists}" size="4" maxlength="10">
        -&nbsp;<input type="text" class='inputBox' name="CUSTOMER_FAX_AREA" value="${requestParameters.CUSTOMER_FAX_AREA?if_exists}" size="4" maxlength="10">
        -&nbsp;<input type="text" class='inputBox' name="CUSTOMER_FAX_CONTACT" value="${requestParameters.CUSTOMER_FAX_CONTACT?if_exists}" size="15" maxlength="15">
        <BR>
        <select name="CUSTOMER_FAX_ALLOW_SOL" class='selectBox'>
          <option>${requestParameters.CUSTOMER_FAX_ALLOW_SOL?default("Y")}</option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyMobilePhone}<BR>(${uiLabelMap.PartyAllowSolicitation}?)</div></td>
    <td width="74%">
      <@fieldErrorsMulti fieldName1="CUSTOMER_MOBILE_COUNTRY" fieldName2="CUSTOMER_MOBILE_AREA" fieldName3="CUSTOMER_MOBILE_CONTACT" fieldName4=""/>
        <input type="text" class='inputBox' name="CUSTOMER_MOBILE_COUNTRY" value="${requestParameters.CUSTOMER_MOBILE_COUNTRY?if_exists}" size="4" maxlength="10">
        -&nbsp;<input type="text" class='inputBox' name="CUSTOMER_MOBILE_AREA" value="${requestParameters.CUSTOMER_MOBILE_AREA?if_exists}" size="4" maxlength="10">
        -&nbsp;<input type="text" class='inputBox' name="CUSTOMER_MOBILE_CONTACT" value="${requestParameters.CUSTOMER_MOBILE_CONTACT?if_exists}" size="15" maxlength="15">
        <BR>
        <select name="CUSTOMER_MOBILE_ALLOW_SOL" class='selectBox'>
          <option>${requestParameters.CUSTOMER_MOBILE_ALLOW_SOL?default("Y")}</option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
</table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<br/>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class='boxhead'>&nbsp;${uiLabelMap.PartyEmailAddress}</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
<table width="100%">
  <tr>
    <td width="26%"><div class="tabletext">${uiLabelMap.PartyEmailAddress}<BR>(${uiLabelMap.PartyAllowSolicitation}?)</div></td>
    <td width="74%">
      <@fieldErrors fieldName="CUSTOMER_EMAIL"/>
        <input type="text" class='inputBox' name="CUSTOMER_EMAIL" value="${requestParameters.CUSTOMER_EMAIL?if_exists}" size="60" maxlength="255" onChange="changeEmail()" onkeyup="changeEmail()"> *
        <br/>
        <select name="CUSTOMER_EMAIL_ALLOW_SOL" class='selectBox'>
          <option>${requestParameters.CUSTOMER_EMAIL_ALLOW_SOL?default("Y")}</option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
<#--
  <tr>
    <td width="26%">
        <div class="tabletext">Order Email addresses (comma separated)</div>
    </td>
    <td width="74%">
        <input type="text" name="CUSTOMER_ORDER_EMAIL" value="${requestParameters.CUSTOMER_ORDER_EMAIL?if_exists}" size="40" maxlength="80">
    </td>
  </tr>
-->
</table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<br/>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class='boxhead'>&nbsp;<#if getUsername>${uiLabelMap.CommonUsername} & </#if>${uiLabelMap.CommonPassword}</div>
          </td>          
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <#if getUsername>
    <tr>
      <td width="26%"><div class="tabletext">${uiLabelMap.CommonUsername}</div></td>
      <td width="74%">
        <@fieldErrors fieldName="USERNAME"/>
        <div class="tabletext">Use Email Address: <input type="CHECKBOX" name="UNUSEEMAIL" value="on" onClick="setEmailUsername();" onFocus="setLastFocused(this);"/></div>
        <div><input type="text" class='inputBox' name="USERNAME" value="${requestParameters.USERNAME?if_exists}" size="20" maxlength="50" onFocus="clickUsername();" onChange="changeEmail();"/> *</div>
     </td>
    </tr>
    </#if>
    <#if createAllowPassword>
      <tr>
        <td width="26%">
          <div class="tabletext">${uiLabelMap.CommonPassword}</div>
        </td>
        <td width="74%">
          <@fieldErrors fieldName="PASSWORD"/>
          <input type="password" class='inputBox' name="PASSWORD" value="" size="20" maxlength="50" onFocus="setLastFocused(this);">
        * </td>
      </tr>
      <tr>
        <td width="26%">
          <div class="tabletext">${uiLabelMap.PartyRepeatPassword}</div>
        </td>
        <td width="74%">
          <@fieldErrors fieldName="CONFIRM_PASSWORD"/>
          <input type="password" class='inputBox' name="CONFIRM_PASSWORD" value="" size="20" maxlength="50">
        * </td>
      </tr>
      <tr>
        <td width="26%">
          <div class="tabletext">${uiLabelMap.PartyPasswordHint}</div>
        </td>
        <td width="74%">
          <@fieldErrors fieldName="PASSWORD_HINT"/>
          <input type="text" class='inputBox' name="PASSWORD_HINT" value="${requestParameters.PASSWORD_HINT?if_exists}" size="40" maxlength="100">
        </td>
      </tr>
    <#else>
      <tr>
        <td width="26%">
          <div class="tabletext">${uiLabelMap.CommonPassword}</div>
        </td>
        <td>
         <div class="commentary">${uiLabelMap.PartyRecievePasswordByEmail}.</div>
        </td>
      </tr>
    </#if>
  </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:document.newuserform.submit();">
</form>

<br/><div class="commentary">${uiLabelMap.CommonFieldsMarkedAreRequired}</div>

&nbsp;&nbsp;<a href="<@ofbizUrl>/checkLogin/main</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonBack}]</a>
&nbsp;&nbsp;<a href="javascript:document.newuserform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
<br/>
<br/>
