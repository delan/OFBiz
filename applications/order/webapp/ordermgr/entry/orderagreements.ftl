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
 *@author     Si Chen (schen@graciousstyle.com)
 *@version    1.0
-->

<#if requestAttributes.uiLabelMap?exists>
    <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>

<table border=0 cellspacing='0' cellpadding='0'>
  <tr>
    <td>
      <table border="0" cellspacing="0" cellpadding="2">
         <tr>
            <td valign="middle">
            <span class="tabletext">${uiLabelMap.OrderOrderFor} : </span>
            <#if person?has_content>
                <a href="/partymgr/control/viewprofile?party_id=${partyId}${requestAttributes.externalKeyParam?if_exists}" target="partymgr" class="buttontext">${person.firstName?if_exists}&nbsp;${person.lastName?if_exists}&nbsp;[${person.partyId}]</a>
            <#elseif partyGroup?has_content>
                <a href="/partymgr/control/viewprofile?party_id=${partyId}${requestAttributes.externalKeyParam?if_exists}" target="partymgr" class="buttontext">${partyGroup.groupName?if_exists}&nbsp;[${partyGroup.partyId}]</a>
             <#else>
                   <span class="tabletext">[${uiLabelMap.PartyPartyNotDefined}]</span>
              </#if>
                 - <span class="tabletext"><a href="<@ofbizUrl>/orderentry?updateParty=Y</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonChange}]</a><#if partyId?default("_NA_") == "_NA_"> - <a href="/partymgr/control/findparty?externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">[${uiLabelMap.PartyFindParty}]</a></#if></span>
               </td>
           </tr>
      </table>
      <table border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <#if hasAgreements=='Y'>
        <form method="post" name="agreementForm" action="<@ofbizUrl>/setOrderAgreement</@ofbizUrl>">
        <input type='hidden' name='hasAgreements' value='${hasAgreements}'>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='top' nowrap>
             <div class='tableheadtext'>
                    ${uiLabelMap.OrderSelectAgreement}
             </div>
          </td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext' valign='top'>
                <#list agreements as agreement>
                   <input type='radio' name='agreementId' value='${agreement.agreementId}' >${agreement.agreementId} - ${agreement.description?if_exists} <br />
                </#list>
            </div>
          </td>
        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr><td colspan="3">&nbsp;</td><td align="left"><input type="submit" class="smallSubmit" value="${uiLabelMap.CommonSelectOne}">
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr><td colspan="4"><hr class="sepbar"></td></tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        </form>
        </#if>
        <form method="post" name="agreementForm" action="<@ofbizUrl>/setOrderCurrency</@ofbizUrl>">
         <input type='hidden' name='hasAgreements' value='${hasAgreements}'>
         <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap>
             <div class='tableheadtext'>
                 ${uiLabelMap.OrderSelectCurrency}
             </div>
          </td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext' valign='top'>
                  <select class="selectBox" name="currencyUomId">
                  <option value=""></option>
                    <#list currencies as currency>
                      <option value="${currency.uomId}" <#if (defaultCurrencyUomId?has_content) && (currency.uomId == defaultCurrencyUomId)>selected</#if>>${currency.uomId}</option>
                    </#list>
                  </select>
            </div>
          </td>
        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr><td colspan="3">&nbsp;</td><td align="left"><input type="submit" class="smallSubmit" value="${uiLabelMap.CommonSelectOne}">
       </form>
      </table>
    </td>
  </tr>
</table>
