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
 *@version    $Revision: 1.2 $
 *@since      2.2
-->

<#if returnHeader?exists>
<div class='tabContainer'>
    <a href="<@ofbizUrl>/returnMain?returnId=${returnId?if_exists}</@ofbizUrl>" class="tabButtonSelected">Return Header</a>  
    <a href="<@ofbizUrl>/returnItems?returnId=${returnId?if_exists}<#if requestParameters.orderId?exists>&orderId=${requestParameters.orderId}</#if></@ofbizUrl>" class="tabButton">Return Items</a>
</div>
</#if>

<#if returnHeader?exists>
<form name="returnhead" method="post" action="<@ofbizUrl>/updateReturn</@ofbizUrl>">
<input type="hidden" name="returnId" value="${returnHeader.returnId}">
<#else>
<form name="returnhead" method="post" action="<@ofbizUrl>/createReturn</@ofbizUrl>">
</#if>

<table border='0' cellpadding='2' cellspacing='0'>
  <#if returnHeader?exists>
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Return ID:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <b>${returnHeader.returnId}</b>
    </td>                
  </tr>
  </#if>
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Entry Date:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <#if returnInfo.entryDate?exists>
        <#assign entryDate = returnInfo.get("entryDate").toString()>
      </#if>
      <input type='text' class='inputBox' size='25' name='entryDate' value='${entryDate?if_exists}'>
      <a href="javascript:call_cal(document.returnhead.entryDate, '');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
    </td>                
  </tr>
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Return From Party:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <input type='text' class='inputBox' size='20' name='fromPartyId' value='${returnInfo.fromPartyId?if_exists}'>
    </td>                
  </tr>
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Return To Facility:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <select name='destinationFacilityId' class='selectBox'>
        <#if currentFacility?exists>
          <option value="${currentFacility.facilityId}">${currentFacility.facilityName?default(currentFacility.facilityId)}</option>
          <option value="${currentFacility.facilityId}">---</option>
        </#if>
        <option value="">No Facility</option>
        <#list facilityList as facility>
          <option value="${facility.facilityId}">${facility.facilityName?default(facility.facilityId)}</option>
        </#list>
    </td>                
  </tr>  
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Billing Account:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <#if billingAccountList?has_content>
        <select name='billingAccountId' class='selectBox'>
          <#if currentAccount?exists>
            <option value="${currentAccount.billingAccountId}">${currentAccount.billingAccountId}: ${currentAccount.description?if_exists}</option>
            <option value="${currentAccount.billingAccountId}">---</option>
          </#if>
          <option value="">No Account</option>
          <#list billingAccountList as ba>
            <option value="${ba.billingAccountId}">${ba.billingAccountId}: ${ba.description?if_exists}</option>
          </#list>
        </select>
      <#else>
        <input type='text' class='inputBox' size='20' name='billingAccountId'>
      </#if>
    </td>                
  </tr>     
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Return Status:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <select name="statusId" class="selectBox">
        <#if currentStatus?exists>
        <option value="${currentStatus.statusId}">${currentStatus.description}</option>
        <option value="${currentStatus.statusId}">---</option>
        </#if>
        <#list returnStatus as status>
          <option value="${status.statusId}">${status.description}</option>
        </#list>
      </select>
    </td>                
  </tr>   
  <#if returnHeader?has_content>    
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' valign='top' nowrap><div class="tabletext">Return From Address:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <#if postalAddresses?has_content>
          <#list postalAddresses as postalAddressInfo>
            <#assign postalAddress = postalAddressInfo.postalAddress>           
            <div class="tabletext">
              <input type='radio' name="originContactMechId" value="${postalAddress.contactMechId}" <#if returnHeader.originContactMechId?default("") == postalAddress.contactMechId>checked</#if>>
              <#if postalAddress.toName?has_content><b>To:</b>&nbsp;${postalAddress.toName}<br></#if>
              <#if postalAddress.attnName?has_content>&nbsp;&nbsp;&nbsp;&nbsp;<b>Attn:</b>&nbsp;${postalAddress.attnName}<br></#if>
              <#if postalAddress.address1?has_content>&nbsp;&nbsp;&nbsp;&nbsp;${postalAddress.address1}<br></#if>
              <#if postalAddress.address2?has_content>&nbsp;&nbsp;&nbsp;&nbsp;${postalAddress.address2}<br></#if>
              <#if postalAddress.city?has_content>&nbsp;&nbsp;&nbsp;&nbsp;${postalAddress.city}</#if>
              <#if postalAddress.stateProvinceGeoId?has_content><br>&nbsp;&nbsp;&nbsp;&nbsp;${postalAddress.stateProvinceGeoId}</#if>
              <#if postalAddress.postalCode?has_content><br>&nbsp;&nbsp;&nbsp;&nbsp;${postalAddress.postalCode}</#if>
              <#if postalAddress.countryGeoId?has_content><br>&nbsp;&nbsp;&nbsp;&nbsp;${postalAddress.countryGeoId}</#if>                                                                                     
            </div>
          </#list>
        </#if>          
        <div class='tabletext'><input type='radio' name="originContactMechId" value="">No Address</div>
      </td>                
    </tr>     
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%'>&nbsp;</td>
      <td width='6%'>&nbsp;</td>   
      <td width='74%'>
        <input type="submit" class="standardButton" value="Update">      
      </td>
    </tr>     
  <#else>  
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%'>&nbsp;</td>
    <td width='6%'>&nbsp;</td>   
    <td width='74%'>
      <input type="submit" class="standardButton" value="Create New">      
    </td>
  </tr>     
  </#if>
</table>