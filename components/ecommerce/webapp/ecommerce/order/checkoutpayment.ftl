<#--
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 *@version    $Revision: 1.1 $
 *@since      3.0
-->

<script language="javascript" type="text/javascript">
<!--
function submitForm(form, mode, value) {	
    if (mode == "DN") {	
        // done action; checkout
        form.action="<@ofbizUrl>/checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "CS") {	
        // continue shopping	
        form.action="<@ofbizUrl>/updateCheckoutOptions/showcart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NA") {
        // new address
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EA") {
        // edit address
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcontactmech?DONE_PAGE=checkoutshippingaddress&contactMechId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NC") {
        // new credit card
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EC") {
        // edit credit card
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NE") {
        // new eft account
        form.action="<@ofbizUrl>/updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EE") {
        // edit eft account
        form.action="<@ofbizUrl>/updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    }
}

function toggleBillingAccount(box) {
    var amountName = box.value + "_amount";
    box.checked = true;   
    box.form.elements[amountName].disabled = false;
    
    for (var i = 0; i < box.form.elements[box.name].length; i++) {
        if (!box.form.elements[box.name][i].checked) {           
            box.form.elements[box.form.elements[box.name][i].value + "_amount"].disabled = true;
        }
    }
}
        
// -->
</script>

<#assign cart = context.shoppingCart?if_exists>

<form method="post" name="checkoutInfoForm" style='margin:0;'>
  <input type="hidden" name="checkoutpage" value="payment">
  <table width="100%" border="0" cellpadding='0' cellspacing='0'>
    <tr valign="top" align="left">
      
      <td bgcolor="white" width="1">&nbsp;&nbsp;</td>
      <td height='100%'>
        <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside' style='height: 100%;'>
          <tr>
            <td width='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
                <tr>
                  <td valign=middle align=left>
                    <div class="boxhead">3)&nbsp;How&nbsp;shall&nbsp;you&nbsp;pay?</div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          
          <#-- Payment Method Selection -->
          <tr style='height: 100%;'>
            <td width='100%' valign=top height='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom' style='height: 100%;'>
                <tr>
                  <td valign=top>                
                    <table width="100%" cellpadding="1" cellspacing="0" border="0">
                      <tr><td colspan="2">
                        <span class='tabletext'>Add:</span>
                        <a href="javascript:submitForm(document.checkoutInfoForm, 'NC', '');" class="buttontext">[Credit Card]</a>
                        <a href="javascript:submitForm(document.checkoutInfoForm, 'NE', '');" class="buttontext">[EFT Account]</a>
                      </td></tr>
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_OFFLINE" <#if "EXT_OFFLINE" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">Mail&nbsp;Check/Money Order</span>
                        </td>
                      </tr>
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_COD" <#if "EXT_COD" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">COD</span>
                        </td>
                      </tr>                       
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_WORLDPAY" <#if "EXT_WORLDPAY" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">Pay With WorldPay</span>
                        </td>
                      </tr>    
                      <tr>
                        <td width="1%" nowrap>
                          <input type="radio" name="checkOutPaymentId" value="EXT_PAYPAL" <#if "EXT_PAYPAL" == context.checkOutPaymentId>checked</#if>>
                        </td>
                        <td width="50%" nowrap>
                          <span class="tabletext">Pay With PayPal</span>
                        </td>
                      </tr>    
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>
                                            
                      <#list context.paymentMethodList as paymentMethod>
                        <#if paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
                          <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                          <tr>                 
                            <td width="1%" nowrap>
                              <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if paymentMethod.paymentMethodId == checkOutPaymentId>checked</#if>>
                            </td>
                            <td width="50%" nowrap>
                              <span class="tabletext">CC:&nbsp;${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}</span>
                              <a href="javascript:submitForm(document.checkoutInfoForm, 'EC', '${paymentMethod.paymentMethodId}');" class="buttontext">[Update]</a>
                            </td>
                          </tr>
                        <#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
                          <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
                          <tr>
                            <td width="1%" nowrap>             
                              <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if paymentMethod.paymentMethodId == checkOutPaymentId>checked</#if>>
                            </td>
                            <td width="50%" nowrap>
                              <span class="tabletext">EFT:&nbsp;${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</span>
                              <a href="javascript:submitForm(document.checkoutInfoForm, 'EE', '${paymentMethod.paymentMethodId}');" class="buttontext">[Update]</a>
                            </td>
                          </tr>
                          <tr><td colspan="2"><hr class='sepbar'></td></tr>
                        </#if>
                      </#list>

                      <#-- special billing account functionality to allow use w/ a payment method -->                     
                      <#if billingAccountList?has_content>                        
                        <tr><td colspan="2"><hr class='sepbar'></td></tr>
                        <tr>                          
                          <td width="1%" nowrap>             
                            <input type="radio" name="checkOutPaymentId" value="EXT_BILLACT" <#if "EXT_BILLACT" == checkOutPaymentId>checked</#if>></hr>
                          </td>
                          <td width="50%" nowrap>
                            <span class="tabletext">Pay only with Billing Account</span>                             
                          </td>
                        </tr>
                        <tr><td colspan="2"><hr class='sepbar'></td></tr>
                        <#list billingAccountList as billingAccount>
                          <#assign availableAmount = billingAccount.accountLimit?double - billingAccount.accountBalance?double>                       
                          <tr>
                            <td align="left" valign="top" width="1%" nowrap>                             
                              <input type="radio" onClick="javascript:toggleBillingAccount(this);" name="billingAccountId" value="${billingAccount.billingAccountId}" <#if (billingAccount.billingAccountId == selectedBillingAccount?default(""))>checked</#if>>
                            </td>
                            <td align="left" valign="top" width="99%" nowrap>
                              <div class="tabletext">
                               ${billingAccount.description?default("Bill Account")} #<b>${billingAccount.billingAccountId}</b>&nbsp;(${(availableAmount)?string.currency})<br>
                               <b>Bill Up To:</b> <input type="text" size="5" class="inputBox" name="${billingAccount.billingAccountId}_amount" value="${availableAmount?double?string("##0.00")}" <#if !(billingAccount.billingAccountId == selectedBillingAccount?default(""))>disabled</#if>>
                              </div>
                            </td>
                          </tr>                          
                        </#list>
                        <tr>
                          <td align="left" valign="top" width="1%" nowrap>                             
                            <input type="radio" onClick="javascript:toggleBillingAccount(this);" name="billingAccountId" value="_NA" <#if (selectedBillingAccount?default("") == "N")>checked</#if>>
                            <input type="hidden" name="_NA_amount" value="0.00">
                          </td>
                          <td align="left" valign="top" width="99%" nowrap>
                            <div class="tabletext">No billing account</div>
                           </td>
                        </tr>                        
                      </#if>
                      <#-- end of special billing account functionality -->
                                            
                    </table>                    
                    <#if !paymentMethodList?has_content>                 
                      <div class='tabletext'><b>There are no payment methods on file.</b></div>
                    </#if>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <#-- End Payment Method Selection -->
          
        </table>
      </td>
    </tr>
  </table>
</form>

<table width="100%">
  <tr valign="top">
    <td align="left">
      &nbsp;<a href="javascript:submitForm(document.checkoutInfoForm, 'CS', '');" class="buttontextbig">[Back to Shopping Cart]</a>
    </td>
    <td align="right">
      <a href="javascript:submitForm(document.checkoutInfoForm, 'DN', '');" class="buttontextbig">[Continue to Final Order Review]</a>
    </td>
  </tr>
</table>
