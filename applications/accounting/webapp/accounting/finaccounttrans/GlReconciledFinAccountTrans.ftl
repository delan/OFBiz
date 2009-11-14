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

<form id="glReconciledFinAccountTrans" name="glReconciledFinAccountTransForm" method="post" action="<@ofbizUrl>callReconcileFinAccountTrans?clearAll=Y</@ofbizUrl>">
  <input name="_useRowSubmit" type="hidden" value="Y"/>
  <input name="finAccountId" type="hidden" value="${finAccountId}"/>
  <input name="glReconciliationId" type="hidden" value="${glReconciliationId}"/>
  <#assign previousGlReconciliation = ""/>
  <#if glReconciliationList?has_content>
    <#assign previousGlReconciliation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(glReconciliationList)/>
  </#if>
  <div class="screenlet"> 
    <div class="screenlet-title-bar">
      <span class="label">${uiLabelMap.AccountingCurrentBankReconciliation}</span>
    </div>
    <div class="screenlet-body">
      <a href="<@ofbizUrl>EditFinAccountReconciliations?finAccountId=${finAccountId}&glReconciliationId=${glReconciliationId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonEdit}</a>
      <#assign finAcctTransCondList = delegator.findByAnd("FinAccountTrans", {"glReconciliationId" : glReconciliationId, "statusId" : "FINACT_TRNS_CREATED"})>
      <#if finAcctTransCondList?has_content>
        <a href="javascript:document.CancelBankReconciliationForm.submit();" class="buttontext">${uiLabelMap.AccountingCancelBankReconciliation}</a>
      </#if>
      <#if currentGlReconciliation?has_content>
        <table>
          <tr>
            <td><span class="label">${uiLabelMap.FormFieldTitle_glReconciliationName}</span></td>
            <td>${currentGlReconciliation.glReconciliationName?if_exists}</td>
          </tr>
          <#if currentGlReconciliation.reconciledBalance?exists>
            <tr>
              <td><span class="label">${uiLabelMap.FormFieldTitle_reconciledBalance}</span></td>
              <td>${currentGlReconciliation.reconciledBalance?if_exists}</td>
            </tr>
          </#if>
          <#if currentGlReconciliation.statusId?exists>
            <tr>
              <td><span class="label">${uiLabelMap.CommonStatus}</span></td>
              <#assign currentStatus = currentGlReconciliation.getRelatedOneCache("StatusItem")>  
              <td>${currentStatus.description?if_exists}</td>
            </tr>
          </#if>
          <tr>
            <td><span class="label">${uiLabelMap.FormFieldTitle_reconciledDate}</span></td>
            <td>${currentGlReconciliation.reconciledDate?if_exists}</td>
          </tr>
          <tr>
            <td><span class="label">${uiLabelMap.AccountingOpeningBalance}</span></td>
            <td><@ofbizCurrency amount=currentGlReconciliation  .openingBalance?default('0')/></td>
          </tr>
        </table>
      </#if>
    </div>
  </div>
  <div class="screenlet"> 
    <div class="screenlet-title-bar">
      <span class="label">${uiLabelMap.AccountingPreviousBankReconciliation}</span>
    </div>
    <div class="screenlet-body">
      <#if previousGlReconciliation?has_content>
        <table>
          <tr>
            <td><span class="label">${uiLabelMap.FormFieldTitle_glReconciliationName}</span></td>
            <td>${previousGlReconciliation.glReconciliationName?if_exists}</td>
          </tr>
          <#if previousGlReconciliation.reconciledBalance?exists>
            <tr>
              <td><span class="label">${uiLabelMap.FormFieldTitle_reconciledBalance}</span></td>
              <td>${previousGlReconciliation.reconciledBalance?if_exists}</td>
            </tr>
          </#if>
          <#if previousGlReconciliation.statusId?exists>
            <tr>
              <td><span class="label">${uiLabelMap.CommonStatus}</span></td>
              <#assign previousStatus = previousGlReconciliation.getRelatedOneCache("StatusItem")> 
              <td>${previousStatus.description?if_exists}</td>
            </tr>
          </#if>
          <tr>
            <td><span class="label">${uiLabelMap.FormFieldTitle_reconciledDate}</span></td>
            <td>${previousGlReconciliation.reconciledDate?if_exists}</td>
          </tr>
          <tr>
            <td><span class="label">${uiLabelMap.AccountingOpeningBalance}</span></td>
            <td><@ofbizCurrency amount=previousGlReconciliation.openingBalance?default('0')/></td>
          </tr>
        </table>
      </#if>
    </div>
  </div>
  <div class="screenlet"> 
    <div class="screenlet-title-bar">
      <span class="label">${uiLabelMap.AccountingFinAcctTransAssociatedToGlReconciliation}</span>
    </div>
    <div class="screenlet-body">
      <#if finAccountTransList?has_content>
        <table class="basic-table hover-bar" cellspacing="0">
          <tr class="header-row-2">
            <th>${uiLabelMap.FormFieldTitle_finAccountTransId}</th>
            <th>${uiLabelMap.FormFieldTitle_finAccountTransType}</th>
            <th>${uiLabelMap.PartyParty}</th>
            <th>${uiLabelMap.FormFieldTitle_transactionDate}</th>
            <th>${uiLabelMap.FormFieldTitle_entryDate}</th>
            <th>${uiLabelMap.CommonAmount}</th>
            <th>${uiLabelMap.FormFieldTitle_paymentId}</th>
            <th>${uiLabelMap.OrderPaymentType}</th>
            <th>${uiLabelMap.FormFieldTitle_paymentMethodTypeId}</th>
            <th>${uiLabelMap.CommonStatus}</th>
            <th>${uiLabelMap.CommonComments}</th>
            <#if finAccountTransactions?has_content>
              <th>${uiLabelMap.AccountingRemoveFromGlReconciliation}</th>
              <th>${uiLabelMap.FormFieldTitle_glTransactions}</th>
            </#if>
          </tr>
          <#assign alt_row = false/>
          <#list finAccountTransList as finAccountTrans>
            <input name="finAccountTransId_o_${finAccountTrans_index}" type="hidden" value="${finAccountTrans.finAccountTransId}"/>
            <input name="organizationPartyId_o_${finAccountTrans_index}" type="hidden" value="${defaultOrganizationPartyId}"/>
            <input id="finAccountTransId_${finAccountTrans_index}" name="_rowSubmit_o_${finAccountTrans_index}" type="hidden" value="Y"/>
            <#assign payment = "">
            <#assign payments = "">
            <#assign status = "">
            <#assign paymentType = "">
            <#assign paymentMethodType = "">
            <#assign partyName = "">
            <#if finAccountTrans.paymentId?has_content>
              <#assign payment = delegator.findOne("Payment", {"paymentId" : finAccountTrans.paymentId}, true)>
            </#if>
            <#assign finAccountTransType = delegator.findOne("FinAccountTransType", {"finAccountTransTypeId" : finAccountTrans.finAccountTransTypeId}, true)>
            <#if finAccountTrans.statusId?has_content>
              <#assign status = delegator.findOne("StatusItem", {"statusId" : finAccountTrans.statusId}, true)>
            </#if>
            <#if payment?has_content && payment.paymentTypeId?has_content>
              <#assign paymentType = delegator.findOne("PaymentType", {"paymentTypeId" : payment.paymentTypeId}, true)>
            </#if>
            <#if payment?has_content && payment.paymentMethodTypeId?has_content>
              <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true)>
            </#if>
            <#if finAccountTrans.partyId?has_content>
              <#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : finAccountTrans.partyId}, true))>
            </#if>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td>${finAccountTrans.finAccountTransId?if_exists}</td>
              <td>${finAccountTransType.description?if_exists}</td>
              <td><#if partyName?has_content>${(partyName.firstName)!} ${(partyName.lastName)!} ${(partyName.groupName)!}<a href="/partymgr/control/viewprofile?partyId=${partyName.partyId}">[${(partyName.partyId)!}]</a></#if></td>
              <td>${finAccountTrans.transactionDate?if_exists}</td>
              <td>${finAccountTrans.entryDateId?if_exists}</td>
              <td><@ofbizCurrency amount=finAccountTrans.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td>
                <#if finAccountTrans.paymentId?has_content>
                  <a href="<@ofbizUrl>paymentOverview?paymentId=${finAccountTrans.paymentId}</@ofbizUrl>">${finAccountTrans.paymentId}</a>
                </#if>
              </td>
              <td><#if paymentType?has_content>${paymentType.description?if_exists}</#if></td>
              <td><#if paymentMethodType?has_content>${paymentMethodType.description?if_exists}</#if></td>
              <td><#if status?has_content>${status.description?if_exists}</#if></td>
              <td>${finAccountTrans.comments?if_exists}</td>
              <#if finAccountTrans.statusId == "FINACT_TRNS_CREATED">
                <td align="center"><a href="javascript:document.reomveFinAccountTransAssociation_${finAccountTrans.finAccountTransId}.submit();" class="buttontext">${uiLabelMap.CommonRemove}</a></td>
              <#else>
                <td/>
              </#if>
              <#if finAccountTrans.paymentId?has_content>
                <td align="center">
                  <a id="toggleGlTransactions_${finAccountTrans.finAccountTransId}" href="javascript:void(0)" class="buttontext">${uiLabelMap.FormFieldTitle_glTransactions}</a>
                  <#include "ShowGlTransactions.ftl"/>
                  <script type="text/javascript">
                    new Popup('displayGlTransactions_${finAccountTrans.finAccountTransId}','toggleGlTransactions_${finAccountTrans.finAccountTransId}', {modal: true, position: 'none', trigger: 'click', cursor_margin:0})
                  </script>
                </td>   
              <#else>
                </td>
              </#if>
            </tr>
            <#assign alt_row = !alt_row/>
          </#list>
        </table>
      </#if>
    </div>
    <div class="right">
      <span class="label">${uiLabelMap.AccountingTotalCapital} </span><@ofbizCurrency amount=transactionTotalAmount.grandTotal isoCode=defaultOrganizationPartyCurrencyUomId/> 
      <#if isReconciled == false>
        <input type="submit" value="${uiLabelMap.AccountingReconcile}"/>
      </#if>
    </div>
  </div>
</form>
<form name="CancelBankReconciliationForm" method="post" action="<@ofbizUrl>cancelBankReconciliation</@ofbizUrl>">
  <input name="finAccountId" type="hidden" value="${finAccountId}"/>
  <input name="glReconciliationId" type="hidden" value="${glReconciliationId}"/>
</form>
<#list finAccountTransList as finAccountTrans>
  <form name="reomveFinAccountTransAssociation_${finAccountTrans.finAccountTransId}" method="post" action="<@ofbizUrl>reomveFinAccountTransAssociation</@ofbizUrl>">
    <input name="finAccountTransId" type="hidden" value="${finAccountTrans.finAccountTransId}"/>
    <input name="finAccountId" type="hidden" value="${finAccountTrans.finAccountId}"/>
    <input name="glReconciliationId" type="hidden" value="${glReconciliationId}"/>
  </form>
</#list>
