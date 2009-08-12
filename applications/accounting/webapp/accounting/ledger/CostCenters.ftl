<?xml version="1.0" encoding="UTF-8"?>
<!--
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
<#if glAcctgAndAmountPercentageList?has_content && glAccountCategories?has_content>

  <form name="costCenters" id="costCenters" method="post" action="<@ofbizUrl>createGlAcctCatMemFromCostCenters</@ofbizUrl>">
    <input type="hidden" name="_useRowSubmit" value="Y">
    <table class="basic-table hover-bar" cellspacing="0">
      <tr class="header-row">
        <th>${uiLabelMap.FormFieldTitle_glAccountId}</th>
        <th>${uiLabelMap.FormFieldTitle_accountCode}</th>
        <th>${uiLabelMap.FormFieldTitle_accountName}</th>
        <#list glAccountCategories as glAccountCategory>
          <th>${glAccountCategory.description!}</th>
        </#list>
      </tr>
      
      <#list glAcctgAndAmountPercentageList as glAcctgAndAmountPercentage>
        <tr>
          <#assign glAccountOrganizationIndex = glAcctgAndAmountPercentage_index + 1/>
          <td>${glAcctgAndAmountPercentage.glAccountId}</td>
          <td>${glAcctgAndAmountPercentage.accountCode!}</td>
          <td>${glAcctgAndAmountPercentage.accountName!}</td>
          <#list glAccountCategories as glAccountCategory>
            <td>
              <input type="hidden" id="glAccountId_${glAcctgAndAmountPercentage.glAccountId}" name="glAccountId_o_${glAccountOrganizationIndex}${glAccountCategory_index}" value="${glAcctgAndAmountPercentage.glAccountId!}"/>
              <input type="hidden" id="glAccountCategoryId_${glAccountCategory.glAccountCategoryId}_${glAcctgAndAmountPercentage.glAccountId}" name="glAccountCategoryId_o_${glAccountOrganizationIndex}${glAccountCategory_index}" value="${(glAccountCategory.glAccountCategoryId!)}"/>
              
              <#if (glAcctgAndAmountPercentage[glAccountCategory.glAccountCategoryId!])??>
                <input type="text" id="amountPercentage_${glAccountCategory.glAccountCategoryId}_${glAcctgAndAmountPercentage.glAccountId}" name="amountPercentage_o_${glAccountOrganizationIndex}${glAccountCategory_index}" value="${(glAcctgAndAmountPercentage[glAccountCategory.glAccountCategoryId!])!}"/>
              <#else>
                <input type="text" id="amountPercentage_${glAccountCategory.glAccountCategoryId}_${glAcctgAndAmountPercentage.glAccountId}" name="amountPercentage_o_${glAccountOrganizationIndex}${glAccountCategory_index}" value=""/>
              </#if>
              <input name="_rowSubmit_o_${glAccountOrganizationIndex}${glAccountCategory_index}" type="hidden" value="Y"/>
            </td>
          </#list>
        </tr>
      </#list>
    </table>
    <div align="right"><input type="submit" id="costCentersSubmit" value="${uiLabelMap.CommonSubmit}"/></div>
  </form>
<#else>
  <label>${uiLabelMap.AccountingNoRecordFound}</label>
</#if>
