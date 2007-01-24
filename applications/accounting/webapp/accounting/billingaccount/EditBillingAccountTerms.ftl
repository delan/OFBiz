<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<div class="head1">${uiLabelMap.PageTitleEditBillingAccountTerms} - ${uiLabelMap.AccountingAccountId}: ${billingAccount.billingAccountId}</div>

<br/>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td><div class="tableheadtext">${uiLabelMap.PartyTerm}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.CommonValue}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.CommonUom}</div></td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="5"><hr class="sepbar"></td></tr>
  <#if !billingAccountTerms?exists || billingAccountTerms?size == 0>
    <tr>
      <td colspan="5"><div class="tabletext">${uiLabelMap.AccountingNoBillingAccountTerm}</div></td>
    </tr>
  <#else>
    <#list billingAccountTerms as term>
    <#assign termType = term.getRelatedOne("TermType")>
    <#if term.uomId?exists>
      <#assign uom = term.getRelatedOne("Uom")>
    </#if>
    <tr>
      <td><div class="tabletext">${(termType.get("description",locale))?if_exists}</div></td>
      <td><div class="tabletext">${term.termValue?if_exists}</div></td>
      <td><div class="tabletext"><#if uom?has_content>${uom.get("description",locale)?if_exists}<#else>&nbsp;</#if></div></td>
      <td align="right">  
        <a href="<@ofbizUrl>EditBillingAccountTerms?billingAccountId=${term.billingAccountId}&billingAccountTermId=${term.billingAccountTermId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonEdit}]</a>&nbsp;
        <a href="<@ofbizUrl>removeBillingAccountTerm?billingAccountId=${term.billingAccountId}&billingAccountTermId=${term.billingAccountTermId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonRemove}]</a> 
      </td>
    </tr>
    </#list>
  </#if>
</table>

<br/>
<#if billingAccountTerm?has_content>
    <div class="head1">${uiLabelMap.PageTitleEditBillingAccountTerms}</div>
    <br/>
    <form name="billingform" method="post" action="<@ofbizUrl>updateBillingAccountTerm</@ofbizUrl>">
      <input type="hidden" name="billingAccountTermId" value="${billingAccountTerm.billingAccountTermId}">
<#else>
    <div class="head1">${uiLabelMap.AccountingCreateBillingAccountTerm}</div>
    <br/>
    <form name="billingform" method="post" action="<@ofbizUrl>createBillingAccountTerm</@ofbizUrl>">
</#if>
  <input type="hidden" name="billingAccountId" value="${billingAccount.billingAccountId}">
  <table width="90%" border="0" cellpadding="2" cellspacing="0"> 
    <tr>
      <td width="26%" align="right" valign="top"><div class="tabletext">${uiLabelMap.PartyTermType}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select class="selectBox" name="termTypeId">
          <#list termTypes as termType>
          <option value="${termType.termTypeId}" <#if termData?has_content && termData.termTypeId?default("") == termType.termTypeId>SELECTED</#if>>${(termType.get("description",locale))?if_exists}</option>
          </#list>
        </select>
      *</td>
    </tr>  
    <tr>
      <td width="26%" align="right" valign="top"><div class="tabletext">${uiLabelMap.CommonUom}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select class="selectBox" name="uomId">
          <option></option>
          <#list uoms as uom>
          <option value="${uom.uomId}" <#if termData?has_content && termData.uomId?default("") == uom.uomId>SELECTED</#if>>${uom.get("description",locale)?if_exists}</option>
          </#list>
        </select>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right" valign="top"><div class="tabletext">${uiLabelMap.PartyTermValue}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="10" name="termValue" value="${termData.termValue?if_exists}">
      *</td>
    </tr>         
    <tr>
      <td width="26%" align="right" valign="top">
        <input type="submit" value="${uiLabelMap.CommonSave}" class="smallSubmit">
      </td>
      <td width="5">&nbsp;</td>
      <td width="74%">&nbsp;</td>
    </tr>
  </table>
</form>

