<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones (jonesde@ofbiz.org) 
 *@version    $Rev$
-->
<#if partyTaxAuthInfoAndDetailList?exists>
    <#list partyTaxAuthInfoAndDetailList as partyTaxAuthInfoAndDetail>
        <div class="tabletext">
            <a href="<@ofbizUrl>deleteCustomerTaxAuthInfo?partyId=${partyId}&amp;taxAuthPartyId=${partyTaxAuthInfoAndDetail.taxAuthPartyId}&amp;taxAuthGeoId=${partyTaxAuthInfoAndDetail.taxAuthGeoId}&amp;fromDate=${partyTaxAuthInfoAndDetail.fromDate}</@ofbizUrl>" class="buttontext">X</a>
            [${partyTaxAuthInfoAndDetail.geoCode}] ${partyTaxAuthInfoAndDetail.geoName} (${partyTaxAuthInfoAndDetail.groupName}): ${uiLabelMap.PartyTaxId} [${partyTaxAuthInfoAndDetail.partyTaxId?default("N/A")}], ${uiLabelMap.PartyTaxIsExempt} [${partyTaxAuthInfoAndDetail.isExempt?default("N")}]
        </div>
    </#list>
    <div>
        <span class="tableheadtext">${uiLabelMap.PartyTaxAddInfo}:</span>
        <select name="taxAuthPartyGeoIds" class="selectBox">
          <option></option>
          <#list taxAuthorityAndDetailList as taxAuthorityAndDetail>
            <option value="${taxAuthorityAndDetail.taxAuthPartyId}::${taxAuthorityAndDetail.taxAuthGeoId}">[${taxAuthorityAndDetail.geoCode}] ${taxAuthorityAndDetail.geoName} (${taxAuthorityAndDetail.groupName?if_exists})</option>
          </#list>
        </select>
        <span class="tabletext">${uiLabelMap.CommonId}: </span><input type="text" name="partyTaxId" class="inputBox" size="12" maxlength="40"/>

        <#if productStore.showTaxIsExempt?default("Y") == "Y">
        <span class="tabletext">${uiLabelMap.PartyTaxIsExempt} </span><input type="checkbox" name="isExempt" class="inputBox" value="Y"/>
        <#else/>
        <input type="hidden" name="isExempt" value="N"/>
        </#if>
    </div>
</#if>
