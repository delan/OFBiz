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
 *@version    $Revision$
 *@since      2.2
-->

<script language="javascript">
<!-- //
function shipBillAddr() {
    if (document.billsetupform.useShipAddr.checked) {
        window.location.replace("/ordermgr/control/setBilling?createNew=Y&finalizeMode=payment&paymentMethodType=${paymentMethodType?if_exists}&useShipAddr=Y");
    } else { 
        window.location.replace("/ordermgr/control/setBilling?createNew=Y&finalizeMode=payment&paymentMethodType=${paymentMethodType?if_exists}");
    }
}
function makeExpDate() {
    document.billsetupform.expireDate.value = document.billsetupform.expMonth.value + "/" + document.billsetupform.expYear.value;
}
// -->
</script>

<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align='left'>
            <div class='boxhead'>&nbsp;Order Entry Payment Settings</div>
          </td> 
          <td nowrap align="right">
            <div class="tabletext">
              <a href="<@ofbizUrl>/setBilling</@ofbizUrl>" class="submenutext">Refresh</a><a href="<@ofbizUrl>/orderentry</@ofbizUrl>" class="submenutext">Items</a><a href="<@ofbizUrl>/setShipping</@ofbizUrl>" class="submenutext">Shipping</a><a href="<@ofbizUrl>/setOptions</@ofbizUrl>" class="submenutext">Options</a><a href="javascript:document.billsetupform.submit();" class="submenutextright">Continue</a>
            </div>
          </td>         
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>  
            <#if (paymentMethodList?has_content || billingAccountList?has_content) && !requestParameters.createNew?exists>
              <#-- initial screen when we have a associated party -->
              <form method="post" action="<@ofbizUrl>/finalizeOrder</@ofbizUrl>" name="billsetupform">
                <input type="hidden" name="finalizeMode" value="payment">
                <table width="100%" cellpadding="1" cellspacing="0" border="0">
                  <tr>
                    <td colspan="2">    
                      <a href="<@ofbizUrl>/setBilling?createNew=Y</@ofbizUrl>" class="buttontext">[Create New]</a>
                    </td>
                  </tr>
                  <tr><td colspan="3"><hr class='sepbar'></td></tr>                      
                  <tr>
                    <td width="1%" nowrap>
                      <input type="radio" name="checkOutPaymentId" value="EXT_OFFLINE" <#if checkOutPaymentId?exists && checkOutPaymentId == "EXT_OFFLINE">CHECKED</#if>>
                    </td>
                    <td colpan="2" width="50%" nowrap>
                      <span class="tabletext">Offline:&nbsp;Check/Money Order</span>
                    </td>
                  </tr>
                  <tr><td colspan="3"><hr class='sepbar'></td></tr>
                  <tr>
                    <td width="1%" nowrap>
                      <input type="radio" name="checkOutPaymentId" value="OFFLINE_PAYMENT" <#if checkOutPaymentId?exists && checkOutPaymentId == "OFFLINE_PAYMENT">CHECKED</#if>>
                    </td>
                    <td colpan="2" width="50%" nowrap>
                      <span class="tabletext">Payment already received</span>
                    </td>
                  </tr>
                  <tr><td colspan="3"><hr class='sepbar'></td></tr>                  
                  <#if billingAccountList?has_content>                                                         
                    <#list billingAccountList as billingAccount>                          
                      <tr>
                        <td align="left" valign="top" width="1%" nowrap>                             
                          <input type="radio" name="checkOutPaymentId" value="EXT_BILLACT|${billingAccount.billingAccountId?if_exists}" <#if ((cart.getGrandTotal()?double + billingAccount.accountBalance?double) > billingAccount.accountLimit?double)>disabled<#elseif selectedBillingAccountId?default("") == billingAccount.billingAccountId>checked</#if>>
                        </td>
                        <td align="left" valign="top" width="99%" nowrap>
                          <div class="tabletext">
                             Bill Account #<b>${billingAccount.billingAccountId}</b>&nbsp;(${(billingAccount.accountLimit?double - billingAccount.accountBalance)?string.currency})<br>
                             ${billingAccount.description?if_exists} 
                          </div> 
                        </td>
                      </tr>
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>
                    </#list>
                  </#if>                                    
                  <#if paymentMethodList?has_content>
                    <#list paymentMethodList as paymentMethod>
                      <#if paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
                        <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                        <tr>                 
                          <td width="1%" nowrap>
                            <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if checkOutPaymentId?exists && paymentMethod.paymentMethodId == checkOutPaymentId>checked</#if>>
                          </td>
                          <td width="50%" nowrap>
                            <span class="tabletext">CC:&nbsp;${Static["org.ofbiz.commonapp.party.contact.ContactHelper"].formatCreditCard(creditCard)}</span>                            
                          </td>
                          <td align="right"><a href="/partymgr/control/editcreditcard?party_id=${party.partyId}&paymentMethodId=${paymentMethod.paymentMethodId}" target="_blank" class="buttontext">[Update]</a></td>
                        </tr>
                      <#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
                        <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
                        <tr>
                          <td width="1%" nowrap>             
                            <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if checkOutPaymentId?exists && paymentMethod.paymentMethodId == checkOutPaymentId>checked</#if>>
                          </td>
                          <td width="50%" nowrap>
                            <span class="tabletext">EFT:&nbsp;${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</span>                            
                          </td>
                          <td align="right"><a href="/partymgr/control/editeftaccount?party_id=${party.partyId}&paymentMethodId=${paymentMethod.paymentMethodId}" target="_blank" class="buttontext">[Update]</a></td>
                        </tr>
                        <tr><td colspan="2"><hr class='sepbar'></td></tr>
                      </#if>
                    </#list>  
                  <#else>
                    <div class='tabletext'><b>There are no payment methods on file.</b></div>                                                     
                  </#if>
                </table>
              </form>  
            <#elseif paymentMethodType?exists || finalizeMode?default("") == "payment">
              <#-- after initial screen; show detailed screens for selected type -->
              <#if paymentMethodType == "CC">
                <#if postalAddress?has_content>
                  <form method="post" action="<@ofbizUrl>/updateCreditCardAndPostalAddress</@ofbizUrl>" name="billsetupform">
                    <input type="hidden" name="paymentMethodId" value="${creditCard.paymentMethodId?if_exists}">
                    <input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}">
                <#elseif requestParameters.useShipAddr?exists>
                  <form method="post" action="<@ofbizUrl>/createCreditCard</@ofbizUrl>" name="billsetupform">
                <#else>
                  <form method="post" action="<@ofbizUrl>/createCreditCardAndPostalAddress</@ofbizUrl>" name="billsetupform">
                </#if>
              </#if>
              <#if paymentMethodType == "EFT">
                <#if postalAddress?has_content>                  
                  <form method="post" action="<@ofbizUrl>/updateEftAndPostalAddress</@ofbizUrl>" name="billsetupform">
                    <input type="hidden" name="paymentMethodId" value="${eftAccount.paymentMethodId?if_exists}">
                    <input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}">
                <#elseif requestParameters.useShipAddr?exists>
                  <form method="post" action="<@ofbizUrl>/createEftAccount</@ofbizUrl>" name="billsetupform">
                <#else>                    
                  <form method="post" action="<@ofbizUrl>/createEftAndPostalAddress</@ofbizUrl>" name="billsetupform">
                </#if>        
              </#if>
              
              <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS">
              <input type="hidden" name="partyId" value="${sessionAttributes.orderPartyId}">
              <input type="hidden" name="paymentMethodType" value="${paymentMethodType}">
              <input type="hidden" name="finalizeMode" value="payment">
              <input type="hidden" name="createNew" value="Y">
              <#if requestParameters.useShipAddr?exists>
                <input type="hidden" name="contactMechId" value="${postalFields.contactMechId}">
              </#if>
                
              <table width="100%" border="0" cellpadding="1" cellspacing="0">
                <#if cart.getShippingContactMechId()?exists>
                <tr>
                  <td width="26%" align="right"= valign="top">
                    <input type="checkbox" name="useShipAddr" value="Y" onClick="javascript:shipBillAddr();" <#if requestParameters.useShipAddr?exists>checked</#if>>
                  </td>
                  <td colspan="2" align="left" valign="center">
                    <div class="tabletext">Billing address is the same as the shipping address</div>
                  </td>
                </tr>
                <tr>
                  <td colspan="3"><hr class="sepbar"></td>
                </tr>
                </#if>
                
                <#if person?exists && person?has_content>
                  <#assign toName = "">
                  <#if person.personalTitle?has_content><#assign toName = person.personalTitle + " "></#if>
                  <#assign toName = toName + person.firstName + " ">
                  <#if person.middleName?has_content><#assign toName = toName + person.middleName + " "></#if>
                  <#assign toName = toName + person.lastName>
                  <#if person.suffix?has_content><#assign toName = toName + " " + person.suffix></#if>
                <#else>
                  <#assign toName = postalFields.toName?default("")>
                </#if>
                
                <#-- generic address information -->
                <tr>
                  <td width="26%" align=right valign=top><div class="tabletext">To Name</div></td>
                  <td width="5">&nbsp;</td>
                  <td width="74%">
                    <input type="text" class="inputBox" size="30" maxlength="60" name="toName" value="${toName}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
                  </td>
                </tr>
                <tr>
                  <td width="26%" align=right valign=top><div class="tabletext">Attention Name</div></td>
                  <td width="5">&nbsp;</td>
                  <td width="74%">
                    <input type="text" class="inputBox" size="30" maxlength="60" name="attnName" value="${postalFields.attnName?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
                  </td>
                </tr>
                <tr>
                  <td width="26%" align=right valign=top><div class="tabletext">Address Line 1</div></td>
                  <td width="5">&nbsp;</td>
                  <td width="74%">
                    <input type="text" class="inputBox" size="30" maxlength="30" name="address1" value="${postalFields.address1?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
                  *</td>
                </tr>
                <tr>
                  <td width="26%" align=right valign=top><div class="tabletext">Address Line 2</div></td>
                  <td width="5">&nbsp;</td>
                  <td width="74%">
                    <input type="text" class="inputBox" size="30" maxlength="30" name="address2" value="${postalFields.address2?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
                  </td>
                </tr>
                <tr>
                  <td width="26%" align=right valign=top><div class="tabletext">City</div></td>
                  <td width="5">&nbsp;</td>
                  <td width="74%">
                    <input type="text" class="inputBox" size="30" maxlength="30" name="city" value="${postalFields.city?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
                  *</td>
                </tr>
                <tr>
                  <td width="26%" align=right valign=top><div class="tabletext">State/Province</div></td>
                  <td width="5">&nbsp;</td>
                  <td width="74%">
                    <select name="stateProvinceGeoId" class="selectBox" <#if requestParameters.useShipAddr?exists>disabled</#if>>
                      <#if postalFields.stateProvinceGeoId?exists>
                      <option>${postalFields.stateProvinceGeoId}</option>
                      <option value="${postalFields.stateProvinceGeoId}">---</option>
                      </#if>
                      <option value=""></option>
                      ${pages.get("/includes/states.ftl")}
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="26%" align=right valign=top><div class="tabletext">Zip/Postal Code</div></td>
                  <td width="5">&nbsp;</td>
                  <td width="74%">
                    <input type="text" class="inputBox" size="12" maxlength="10" name="postalCode" value="${postalFields.postalCode?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
                  *</td>
                </tr>
                <tr>
                  <td width="26%" align=right valign=top><div class="tabletext">Country</div></td>
                  <td width="5">&nbsp;</td>
                  <td width="74%">
                    <select name="countryGeoId" class="selectBox" <#if requestParameters.useShipAddr?exists>disabled</#if>>
                      <#if postalFields.countryGeoId?exists>
                      <option>${postalFields.countryGeoId}</option>
                      <option value="${postalFields.countryGeoId}">---</option>
                      </#if>
                      ${pages.get("/includes/countries.ftl")}
                    </select>
                  *</td>
                </tr> 
                
                <#-- credit card fields -->
	            <#if paymentMethodType == "CC">
	              <#if !creditCard?has_content>
	                <#assign creditCard = requestParameters>
	              </#if>
	              <input type='hidden' name='expireDate' value='${creditCard.expireDate?if_exists}'>
                  <tr>
                    <td colspan="3"><hr class="sepbar"></td>
                  </tr>
  	              <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Name on Card</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="nameOnCard" value="${creditCard.nameOnCard?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Company Name on Card</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="companyNameOnCard" value="${creditCard.companyNameOnCard?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Card Type</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <select name="cardType" class="selectBox">
                        <#if creditCard.cartType?exists>
                        <option>${creditCard.cardType}</option>
                        <option value="${creditCard.cardType}">---</option>
                        </#if>
                        <option>Visa</option>
                        <option value='MasterCard'>Master Card</option>
                        <option value='AmericanExpress'>American Express</option>
                        <option value='DinersClub'>Diners Club</option>
                        <option>Discover</option>
                        <option>EnRoute</option>
                        <option>JCB</option>
                      </select>
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Card Number</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="20" maxlength="30" name="cardNumber" value="${creditCard.cardNumber?if_exists}">
                    *</td>
                  </tr>
                  <#--<tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Card Security Code</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" size="5" maxlength="10" name="cardSecurityCode" value="">
                    </td>
                  </tr>-->
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Expiration Date</div></td>        
                    <td width="5">&nbsp;</td>                    
                    <td width="74%">
                      <#assign expMonth = "">
                      <#assign expYear = "">
                      <#if creditCard?exists && creditCard.expDate?exists>
                        <#assign expDate = creditCard.expireDate>
                        <#if (expDate?exists && expDate.indexOf("/") > 0)>
                          <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
                          <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
                        </#if>
                      </#if>
                      <select name="expMonth" class='selectBox' onChange="javascript:makeExpDate();">                        
                        <option><#if creditCard?has_content && expMonth?has_content>${expMonth?if_exists}<#else>${requestParameters.expMonth?if_exists}</#if></option>
                        <option></option>
                        <option>01</option>
                        <option>02</option>
                        <option>03</option>
                        <option>04</option>
                        <option>05</option>
                        <option>06</option>
                        <option>07</option>
                        <option>08</option>
                        <option>09</option>
                        <option>10</option>
                        <option>11</option>
                        <option>12</option>
                      </select>
                      <select name="expYear" class='selectBox' onChange="javascript:makeExpDate();">
                        <option><#if creditCard?has_content && expYear?has_content>${expYear?if_exists}<#else>${requestParameters.expYear?if_exists}</#if></option>
                        <option></option>          
                        <option>2003</option>
                        <option>2004</option>
                        <option>2005</option>
                        <option>2006</option>
                        <option>2007</option>
                        <option>2008</option>
                      </select>
                    *</td>                                                       
                  </tr>
  	            </#if>  
  	            
  	            <#-- eft fields -->
  	            <#if paymentMethodType =="EFT">
  	              <#if !eftAccount?has_content>
  	                <#assign eftAccount = requestParameters>
  	              </#if>
  	              <tr>
                    <td colspan="3"><hr class="sepbar"></td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Name on Account</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="nameOnAccount" value="${eftAccount.nameOnAccount?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Company Name on Account</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="companyNameOnAccount" value="${eftAccount.companyNameOnAccount?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Bank Name</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="bankName" value="${eftAccount.bankName?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Routing Number</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="10" maxlength="30" name="routingNumber" value="${eftAccount.routingNumber?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Account Type</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <select name="accountType" class='selectBox'>
                        <option>${eftAccount.accountType?if_exists}</option>
                        <option></option>
                        <option>Checking</option>
                        <option>Savings</option>
                      </select>
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Account Number</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="20" maxlength="40" name="accountNumber" value="${eftAccount.accountNumber?if_exists}">
                    *</td>
                  </tr>
                </#if>  	                                               
              </table>                    	                                                                     
            <#else>
              <#-- initial screen show a list of options -->
              <form method="post" action="<@ofbizUrl>/finalizeOrder</@ofbizUrl>" name="billsetupform">
                <input type="hidden" name="finalizeMode" value="payoption">
                <input type="hidden" name="createNew" value="Y">                
                <table width="100%" border="0" cellpadding="1" cellspacing="0"> 
                  <#if !requestParameters.createNew?exists>                                    
                  <tr>
                    <td width='1%' nowrap><input type="radio" name="paymentMethodType" value="offline" <#if paymentMethodType?exists && paymentMethodType == "offline">checked</#if>
                    <td width='50%'nowrap><div class="tabletext">Offline Payment: Check/Money Order</div></td>
                  </tr>
                  <tr><td colspan="2"><hr class='sepbar'></td></tr>
                  <tr>
                    <td width='1%' nowrap><input type="radio" name="paymentMethodType" value="offline_payment"></td>
                    <td width='50%' nowrap><div class="tabletext">Payment already received</div></td>                                       
                  </tr>
                  <tr><td colspan="2"><hr class='sepbar'></td></tr>
                  </#if>
                  <tr>
                    <td width='1%' nowrap><input type="radio" name="paymentMethodType" value="CC">  
                    <td width='50%' nowrap><div class="tabletext">Credit Card: Visa/Mastercard/Amex/Discover</div></td>                    
                  </tr>
                  <tr><td colspan="2"><hr class='sepbar'></td></tr>
                  <tr>
                    <td width='1%' nowrap><input type="radio" name="paymentMethodType" value="EFT">
                    <td width='50%' nowrap><div class="tabletext">EFT Account: AHC/Electronic Check</div></td>                    
                  </tr>                  
                </table>
              </form>
            </#if>                                    
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
         
<br>
<#else>
  <h3>You do not have permission to view this page. ("ORDERMGR_CREATE" or "ORDERMGR_ADMIN" needed)</h3>
</#if>