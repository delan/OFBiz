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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.1 $
 *@since      3.0
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if canNotView>
  <p><h3>${uiLabelMap.AccountingCardInfoNotBelongToYou}.</h3></p>
&nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonBack}]</a>
<#else>
    <#if !giftCard?exists>
      <p class="head1">${uiLabelMap.AccountingAddNewGiftCard}</p>
      &nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
      &nbsp;<a href="javascript:document.editgiftcardform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
      <form method="post" action='<@ofbizUrl>/createGiftCard?DONE_PAGE=${donePage}</@ofbizUrl>' name="editgiftcardform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <#else>
      <p class="head1">${uiLabelMap.AccountingEditCreditCard}</p>
      &nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
      &nbsp;<a href="javascript:document.editgiftcardform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
      <form method="post" action='<@ofbizUrl>/updateGiftCard?DONE_PAGE=${donePage}</@ofbizUrl>' name="editgiftcardform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <input type='hidden' name='paymentMethodId' value='${paymentMethodId}'>
    </#if>

    <tr>
      <td width="26%" align=right valign='top'><div class="tabletext">Physical Number</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <#if giftCardData?has_content && giftCardData.physicalNumber?has_content>
          <#assign pcardNumberDisplay = "">
          <#assign pcardNumber = giftCardData.physicalNumber?if_exists>
          <#if pcardNumber?has_content>
            <#assign psize = pcardNumber?length - 4>
            <#if 0 < psize>
              <#list 0 .. psize-1 as foo>
                <#assign pcardNumberDisplay = pcardNumberDisplay + "*">
              </#list>
              <#assign pcardNumberDisplay = pcardNumberDisplay + pcardNumber[psize .. psize + 3]>
            <#else>
              <#assign pcardNumberDisplay = pcardNumber>
            </#if>
          </#if>
        </#if>
        <input type="text" class="inputBox" size="20" maxlength="60" name="physicalNumber" value="${pcardNumberDisplay?if_exists}">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign='top'><div class="tabletext">Physical PIN</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="password" class="inputBox" size="10" maxlength="60" name="physicalPin" value="${giftCardData.physicalPin?if_exists}">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign='top'><div class="tabletext">Virtual Number</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <#if giftCardData?has_content && giftCardData.virtualNumber?has_content>
          <#assign vcardNumberDisplay = "">
          <#assign vcardNumber = giftCardData.virtualNumber?if_exists>
          <#if vcardNumber?has_content>
            <#assign vsize = vcardNumber?length - 4>
            <#if 0 < vsize>
              <#list 0 .. vsize-1 as foo>
                <#assign vcardNumberDisplay = vcardNumberDisplay + "*">
              </#list>
              <#assign vcardNumberDisplay = vcardNumberDisplay + vcardNumber[vsize .. vsize + 3]>
            <#else>
              <#assign vcardNumberDisplay = vcardNumber>
            </#if>
          </#if>
        </#if>
        <input type="text" class="inputBox" size="20" maxlength="60" name="virtualNumber" value="${vcardNumberDisplay?if_exists}">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign='top'><div class="tabletext">Virtual PIN</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="password" class="inputBox" size="10" maxlength="60" name="virtualPin" value="${giftCardData.virtualPin?if_exists}">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign='top'><div class="tabletext">Expiration Date</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <#assign expMonth = "">
        <#assign expYear = "">
        <#if giftCardData?exists && giftCardData.expireDate?exists>
          <#assign expDate = giftCard.expireDate>
          <#if (expDate?exists && expDate.indexOf("/") > 0)>
            <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
            <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
          </#if>
        </#if>
        <select name="expMonth" class='selectBox' onChange="javascript:makeExpDate();">
          <#if giftCardData?has_content && expMonth?has_content>
            <#assign ccExprMonth = expMonth>
          <#else>
            <#assign ccExprMonth = requestParameters.expMonth?if_exists>
          </#if>
          <#if ccExprMonth?has_content>
            <option value="${ccExprMonth?if_exists}">${ccExprMonth?if_exists}</option>
          </#if>
          ${pages.get("/includes/ccmonths.ftl")}
        </select>
        <select name="expYear" class='selectBox' onChange="javascript:makeExpDate();">
          <#if giftCard?has_content && expYear?has_content>
            <#assign ccExprYear = expYear>
          <#else>
            <#assign ccExprYear = requestParameters.expYear?if_exists>
          </#if>
          <#if ccExprYear?has_content>
            <option value="${ccExprYear?if_exists}">${ccExprYear?if_exists}</option>
          </#if>
          ${pages.get("/includes/ccyears.ftl")}
        </select>
      </td>
    </tr>
  </table>
  </form>

  &nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
  &nbsp;<a href="javascript:document.editgiftcardform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
</#if>
