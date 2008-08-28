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

<div id="billToServerError" class="errorMessage"></div>
<form id="editBillToPostalAddress" name="editBillToPostalAddress" method="post" action="<@ofbizUrl></@ofbizUrl>">
  <div>
    <input type="hidden" name="setBillingPurpose" value="Y"/>
    <input type="hidden" name="contactMechId" value="${parameters.billToContactMechId?if_exists}"/>
    <#assign productStoreId = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStoreId(request)/>
    <input type="hidden" name="productStoreId" value="${productStoreId?if_exists}"/>
    <div class="form-row">
      <label>${uiLabelMap.PartyAddressLine1}*</label>
      <span>
        <input type="text" class="left required" name="address1" id="billToAddress1" value="${parameters.billToAddress1?if_exists}" size="30" maxlength="30">
        <span id="advice-required-billToAddress1" style="display: none" class="errorMessage">(required)</span>
      </span>
    </div>
    <div class="form-row">
      <label>${uiLabelMap.PartyAddressLine2}</label>
      <span>
        <input type="text" class="left" name="address2" value="${parameters.billToAddress2?if_exists}" size="30" maxlength="30">    
      </span>
    </div>
    <div class="form-row">
      <label>${uiLabelMap.PartyCity}*</label>
      <span>
        <input type="text" class="left required" name="city" id="billToCity" value="${parameters.billToCity?if_exists}" size="30" maxlength="30">
        <span id="advice-required-billToCity" style="display: none" class="errorMessage">(required)</span>
      </span>
    </div>
    <div class="form-row">
      <label>${uiLabelMap.PartyZipCode}*</label>
      <span>
        <input type="text" class="left required" name="postalCode" id="billToPostalCode" value="${parameters.billToPostalCode?if_exists}" size="12" maxlength="10">
        <span id="advice-required-billToPostalCode" style="display: none" class="errorMessage">(required)</span>
      </span>
    </div>
    <div class="form-row">
      <label>${uiLabelMap.PartyState}*</label>
      <span>
        <select name="stateProvinceGeoId" id="billToStateProvinceGeoId" class="left required" style="width: 70%">
          <#if parameters.billToStateProvinceGeoId?exists>
            <option value='${parameters.billToStateProvinceGeoId}'>${parameters.billToStateProvinceGeo?default(parameters.billToStateProvinceGeoId)}</option>
          </#if>
          <option value="">${uiLabelMap.PartyNoState}</option>
          ${screens.render("component://common/widget/CommonScreens.xml#states")}
        </select>
        <span id="advice-required-billToStateProvinceGeoId" style="display: none" class="errorMessage">(required)</span>
      </span>
    </div>
    <div class="form-row">
      <label>${uiLabelMap.PartyCountry}*</label>
      <span>
        <select name="countryGeoId" id="billToCountryGeoId" class="left required" style="width: 70%">
          <#if parameters.billToCountryGeoId?exists>
            <option value='${parameters.billToCountryGeoId}'>${parameters.billToCountryProvinceGeo?default(parameters.billToCountryGeoId)}</option>
          </#if>
          ${screens.render("component://common/widget/CommonScreens.xml#countries")}
        </select>
        <span id="advice-required-billToCountryGeoId" style="display: none" class="errorMessage">(required)</span>
      </span>
    </div>
    <#if billToTelecomNumber?has_content>
      <div class="form-row">
        <div class="field-label">
          <label for="phoneNumber_${billToTelecomNumber.contactMechId}">${uiLabelMap.PartyPhoneNumber}*</label>
        </div>
        <div>
          <input type="hidden" name="phoneContactMechId" value="${billToTelecomNumber.contactMechId?if_exists}"/>
          <input type="text" name="countryCode" id="countryCode_${billToTelecomNumber.contactMechId}" class="required" value="${billToTelecomNumber.countryCode?if_exists}" size="3" maxlength="3"/>
          - <input type="text" name="areaCode" id="areaCode_${billToTelecomNumber.contactMechId}" class="required" value="${billToTelecomNumber.areaCode?if_exists}" size="3" maxlength="3"/>
          - <input type="text" name="contactNumber" id="contactNumber_${billToTelecomNumber.contactMechId}" class="required" value="${contactNumber?default("${billToTelecomNumber.contactNumber?if_exists}")}" size="6" maxlength="7"/>
          - <input type="text" name="extension" id="extension_${billToTelecomNumber.contactMechId}" value="${extension?default("${billToExtension?if_exists}")}" size="3" maxlength="3"/>
        </div>
      </div>
    </#if>
    <div class="form-row">
      <b>${uiLabelMap.EcommerceMyDefaultShippingAddress}</b>
      <input type="checkbox" name="setShippingPurpose" value="Y" <#if setShippingPurpose?exists>checked</#if>/>
    </div>
    <div class="form-row">
      <a name="submitEditBillToPostalAddress" id="submitEditBillToPostalAddress" class="buttontext" onclick="updatePartyBillToPostalAddress('submitEditBillToPostalAddress')">${uiLabelMap.CommonSubmit}</a>
      <form action="">
        <input class="popup_closebox buttontext" type="button" value="${uiLabelMap.CommonClose}"/>
      </form>
    </div>
  </div>
</form>
