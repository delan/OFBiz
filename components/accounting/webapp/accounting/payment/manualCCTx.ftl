<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.1 $
 *@since      3.0
-->

<div class="head1">Manual Credit Card Transaction</div>
<br>

<form name="manualCcForm" method="post" action="<@ofbizUrl>/view/manualCcPay</@ofbizUrl>">
  <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td width="26%" align=right valign=middle><div class="tableheadtext">Transaction Type</div></td>
      <td width="5">&nbsp;</td>
      <td width='74%'>
        <#assign txType = requestParameters.transactionType?if_exists>
        <#if txType?has_content>
          <div class="tabletext">${txType}</div>
        <#else>
          <select name="transactionType" class="selectBox" onclick="javascript:document.manualCcForm.submit();">
            <option>Authorization</option>
            <option>Settlement</option>
            <option>Credit</option>
          </select>
        </#if>
      </td>
    </tr>

    <#-- reference number -->
    <#if txType?default("") == "Credit" || txType?default("") == "Settlement">
      <tr><td colspan="3"><hr class="sepbar"></td></tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Reference Number</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" class="inputBox" size="30" maxlength="60" name="referenceNum">
        *</td>
      </tr>
    </#if>

    <#-- manual credit card information -->
    <#if txType?default("") == "Credit" || txType?default("") == "Authorization">
      <#if txType?default("") == "Credit">
        <tr><td colspan="3"><hr class="sepbar"></td></tr>
      </#if>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Name on Card</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" class="inputBox" size="30" maxlength="60" name="nameOnCard" value="${(creditCard.nameOnCard)?if_exists}">
        *</td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Company Name on Card</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" class="inputBox" size="30" maxlength="60" name="companyNameOnCard" value="${(creditCard.companyNameOnCard)?if_exists}">
        </td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Card Type</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <select name="cardType" class="selectBox">
            <#if ((creditCard.cardType)?exists)>
              <option>${creditCard.cardType}</option>
              <option value="${creditCard.cardType}">---</option>
            </#if>
            ${pages.get("/includes/cctypes.ftl")}
          </select>
        *</td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Card Number</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" class="inputBox" size="20" maxlength="30" name="cardNumber" value="${(creditCard.cardNumber)?if_exists}">
        *</td>
      </tr>
      <#--<tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Card Security Code</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" size="5" maxlength="10" name="cardSecurityCode" value="">
        </td>
      </tr>-->
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Expiration Date</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <#assign expMonth = "">
          <#assign expYear = "">
          <#if creditCard?exists && creditCard.expireDate?exists>
            <#assign expDate = creditCard.expireDate>
            <#if (expDate?exists && expDate.indexOf("/") > 0)>
              <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
              <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
            </#if>
          </#if>
          <select name="expMonth" class='selectBox'>
            <#if creditCard?has_content && expMonth?has_content>
              <#assign ccExprMonth = expMonth>
            <#else>
              <#assign ccExprMonth = requestParameters.expMonth?if_exists>
            </#if>
            <#if ccExprMonth?has_content>
              <option value="${ccExprMonth?if_exists}">${ccExprMonth?if_exists}</option>
            </#if>
            ${pages.get("/includes/ccmonths.ftl")}
          </select>
          <select name="expYear" class='selectBox'>
            <#if creditCard?has_content && expYear?has_content>
              <#assign ccExprYear = expYear>
            <#else>
              <#assign ccExprYear = requestParameters.expYear?if_exists>
            </#if>
            <#if ccExprYear?has_content>
              <option value="${ccExprYear?if_exists}">${ccExprYear?if_exists}</option>
            </#if>
            ${pages.get("/includes/ccyears.ftl")}
          </select>
        *</td>
      </tr>
      <tr><td colspan="3"><hr class="sepbar"></td></tr>

      <#-- credit card address -->
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Bill-To Address1</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" class="inputBox" size="30" maxlength="30" name="address1" value="${(postalFields.address1)?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
        *</td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Bill-To Address2</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" class="inputBox" size="30" maxlength="30" name="address2" value="${(postalFields.address2)?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">City</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" class="inputBox" size="30" maxlength="30" name="city" value="${(postalFields.city)?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
        *</td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">State/Province</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <select name="stateProvinceGeoId" class="selectBox" <#if requestParameters.useShipAddr?exists>disabled</#if>>
            <#if (postalFields.stateProvinceGeoId)?exists>
              <option>${postalFields.stateProvinceGeoId}</option>
              <option value="${postalFields.stateProvinceGeoId}">---</option>
            <#else>
              <option value="">No State</option>
            </#if>
            ${pages.get("/includes/states.ftl")}
          </select>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Postal Code</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" class="inputBox" size="12" maxlength="10" name="postalCode" value="${(postalFields.postalCode)?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
        *</td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Country</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <select name="countryGeoId" class="selectBox" <#if requestParameters.useShipAddr?exists>disabled</#if>>
            <#if (postalFields.countryGeoId)?exists>
              <option>${postalFields.countryGeoId}</option>
              <option value="${postalFields.countryGeoId}">---</option>
            </#if>
            ${pages.get("/includes/countries.ftl")}
          </select>
        *</td>
      </tr>

    </#if>
    <#if txType?has_content>
      <tr><td colspan="3"><hr class="sepbar"></td></tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Amount</div></td>
        <td width="5">&nbsp;</td>
        <td width="74%">
          <input type="text" class="inputBox" size="20" maxlength="30" name="amount">
        *</td>
      </tr>
    </#if>

    <#-- submit button -->
    <tr>
      <td width="26%" align=right valign=middle>&nbsp;</td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="submit" value="Submit">
      </td>
    </tr>
  </table>
</form>
