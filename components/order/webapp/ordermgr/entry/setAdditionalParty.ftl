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
 *@author     Peter Goron (peter.goron@nereide.biz)
 *@author     Jean-Luc.Malet@nereide.biz (migration to uiLabelMap)
 *@version    $Rev: 3227 $
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#-- ==================== Party Selection dialog box ========================= -->
<table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PartyAdditionalPartyEntry}</div>
	  </td>
          <td nowrap align="right">
            <div class="tabletext">
              <#--<a href="<@ofbizUrl>/setAdditionalParty</@ofbizUrl>" class="submenutext">${uiLabelMapRefresh</a>-->
              <a href="<@ofbizUrl>/orderentry</@ofbizUrl>" class="submenutext">${uiLabelMap.OrderOrderItems}</a>
              <a href="<@ofbizUrl>/setShipping</@ofbizUrl>" class="submenutext">${uiLabelMap.FacilityShipping}</a>
              <a href="<@ofbizUrl>/setOptions</@ofbizUrl>" class="submenutext">${uiLabelMap.CommonOptions}</a>
              <a href="<@ofbizUrl>/setBilling</@ofbizUrl>" class="submenutext">${uiLabelMap.AccountingPayment}</a>
              <a href="<@ofbizUrl>/finalizeOrder?finalizeReqAdditionalParty=false</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonContinue}</a>
            </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  
  <form method="post" action="<@ofbizUrl>/setAdditionalParty</@ofbizUrl>" name="quickAddPartyForm">

  <tr>
    <td><div class="tableheadtext">1) ${uiLabelMap.OrderSelectPartyToOrder} :</div></td>
  </tr>
  <tr>
    <td width="100%">
      <table border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td align="right">
            <input type="radio" name="additionalPartyType" value="Person" onclick="<#if additionalPartyType?exists>javascript:document.quickAddPartyForm.additionalPartyId.value='';</#if>document.quickAddPartyForm.submit()"<#if (additionalPartyType?exists && additionalPartyType == "Person")> checked</#if>>
	  </td>
          <td>
            <div class="tabletext">${uiLabelMap.CommonPerson}</div>
          </td>
        </tr>
        <tr>
          <td align="right">
            <input type="radio" name="additionalPartyType" value="Group" onclick="<#if additionalPartyType?exists>javascript:document.quickAddPartyForm.additionalPartyId.value='';</#if>document.quickAddPartyForm.submit()"<#if additionalPartyType?exists && additionalPartyType == "Group"> checked</#if>>
          </td>
          <td>
            <div class="tabletext">${uiLabelMap.CommonGroup}</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>

  <tr>
    <td>&nbsp;</td>
  </tr>

  <#if additionalPartyType?exists && additionalPartyType != "">
    <#if additionalPartyType == "Person">
      <#assign lookupPartyView="lookupPerson">
    <#else>
      <#assign lookupPartyView="lookupPartyGroup">
    </#if>
  <tr>
    <td><div class="tableheadtext">2) ${uiLabelMap.PartyFindParty} :</div></td>
  </tr>

  <tr>
    <td width="100%">
      <table border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td>
            <div class="tableheadtext">${uiLabelMap.CommonIdentifier} :</div>
          </td>
          <td>
            <input type="text" class="inputBox" name="additionalPartyId" value="${additionalPartyId?if_exists}" onchange="javascript:document.quickAddPartyForm.submit()">
          </td>
          <td>
            <a href="javascript:document.quickAddPartyForm.additionalPartyId.focus();call_fieldlookup2(document.quickAddPartyForm.additionalPartyId, '${lookupPartyView}');"><img src="/content/images/fieldlookup.gif" width="16" height="16" border="0" alt="Lookup"></a>
          </td>
          <td>
            &nbsp;<a href="javascript:document.quickAddPartyForm.submit()" class="buttontext">[${uiLabelMap.CommonApply}]</a>
          </td>
        </tr>
      </table>
    </td>
  </tr>

  <tr>
    <td>&nbsp;</td>
  </tr>

  </form>

  <#if roles?has_content>
  <tr>
    <td><div class="tableheadtext">3) ${uiLabelMap.PartySelectRoleForParty} :</div></td>
  </tr>
 
  <tr>
    <td width="100%">
      <table border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <form method="post" action="<@ofbizUrl>/addAdditionalParty</@ofbizUrl>">
        <tr>
          <td>&nbsp;</td>
          <td>
            <select name="additionalRoleTypeId" size="5" multiple>
              <#list roles as role>
              <option value="${role.roleTypeId}" class="tabletext">${role.description}</option>
              </#list>
            </select>
          </td>
          <td>&nbsp;</td>
          <td align="left">
            <input type="hidden" name="additionalPartyId" value="${additionalPartyId}">
            <input type="submit" class="smallSubmit" value="Add">
          </td>
        </tr>
        </form>
      </table>
    </td>
  </tr>
  </#if> <#-- roles?has_content -->
  <#else>
  </form>
  </#if> <#-- additionalPartyType?has_content -->
</table>
<br>

${pages.get("/entry/additionalPartyListing.ftl")}
