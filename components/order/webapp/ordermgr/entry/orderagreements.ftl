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

<#assign uiLabelMap = requestAttributes.uiLabelMap>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">${uiLabelMap.OrderOrderEntry}Currency: ${currencyUomId}</div>
          </td>
          <td valign="middle" align="right">
             <a href="<@ofbizUrl>/emptycart</@ofbizUrl>" class="submenutext">${uiLabelMap.OrderClearOrder}</a>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width="100%" border="0" cellspacing="0" cellpadding="2">
         <tr>
            <td valign="middle">
            <span class="tabletext">${uiLabelMap.OrderOrderFor} : </span>
            <#if person?has_content>
                <a href="/partymgr/control/viewprofile?party_id=${partyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${person.firstName?if_exists}&nbsp;${person.lastName?if_exists}&nbsp;[${person.partyId}]</a>
            <#elseif partyGroup?has_content>
                <a href="/partymgr/control/viewprofile?party_id=${partyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${partyGroup.groupName?if_exists}&nbsp;[${partyGroup.partyId}]</a>
             <#else>
                   <span class="tabletext">[${uiLabelMap.PartyPartyNotDefined}]</span>
              </#if>
                 - <span class="tabletext"><a href="<@ofbizUrl>/orderentry?updateParty=Y</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonChange}]</a><#if partyId?default("_NA_") == "_NA_"> - <a href="/partymgr/control/findparty?externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">[${uiLabelMap.PartyFindParty}]</a></#if></span>
               </td>
           </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <#if agreements?has_content>
        <form method="post" name="agreementForm" action="<@ofbizUrl>/setOrderAgreement</@ofbizUrl>">
        <tr>
          <td width='10%'>&nbsp;</td>
          <td wdith='20%' align='right' valign='top' nowrap>
             <div class='tableheadtext'>
                    ${uiLabelMap.OrderSelectAgreement}
             </div>
          </td>
          <td width='6%'>&nbsp;</td>
          <td width='64%' valign='middle'>
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
         <tr>
          <td width='14%'>&nbsp;</td>
          <td wdith='6%' align='right' valign='middle' nowrap>
             <div class='tableheadtext'>
                 ${uiLabelMap.OrderSelectCurrency}
             </div>
          </td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
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
