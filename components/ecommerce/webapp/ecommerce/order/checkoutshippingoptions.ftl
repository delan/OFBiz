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
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "EA") {
        // edit address
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcontactmech?DONE_PAGE=checkoutshippingaddress&contactMechId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NC") {
        // new credit card
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "EC") {
        // edit credit card
        form.action="<@ofbizUrl>/updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutoptions&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NE") {
        // new eft account
        form.action="<@ofbizUrl>/updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "EE") {
        // edit eft account
        form.action="<@ofbizUrl>/updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutoptions&paymentMethodId="+value+"</@ofbizUrl>";
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
  <input type="hidden" name="checkoutpage" value="shippingoptions">
  <table width="100%" border="0" cellpadding='0' cellspacing='0'>
    <tr valign="top" align="left">
      <td height='100%'>
        <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
          <tr>
            <td width='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
                <tr>
                  <td valign=middle align=left>
                    <div class="boxhead">2)&nbsp;How&nbsp;shall&nbsp;we&nbsp;ship&nbsp;it?</div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr style='height: 100%;'>
            <td width='100%' valign=top height='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom' style='height: 100%;'>
                <tr>
                  <td>
                    <table width='100%' cellpadding='1' border='0' cellpadding='0' cellspacing='0'>
                      <#list context.carrierShipmentMethodList as carrierShipmentMethod>
                        <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
                        <tr>
                          <td width='1%' valign="top" >                            
                            <input type='radio' name='shipping_method' value='${shippingMethod}' <#if shippingMethod == context.chosenShippingMethod?default("N@A")>checked</#if>>       
                          </td>
                          <td valign="top">                            
                            <div class='tabletext'>
                              <#if cart.getShippingContactMechId()?exists>
                                <#assign shippingEstMap = Static["org.ofbiz.order.shoppingcart.shipping.ShippingEvents"].getShipEstimate(delegator, cart, shippingMethod)>
                                <#if shippingEstMap?has_content && shippingEstMap.shippingTotal?exists>
                                  <#assign shippingEstimate = " - " + shippingEstMap.shippingTotal?string.currency>
                                <#else>
                                  <#assign shippingEstimate = " - Calculated Offline">
                                </#if>                              
                              </#if>
                              <#if carrierShipmentMethod.partyId != "_NA_">${carrierShipmentMethod.partyId?if_exists}&nbsp;</#if>${carrierShipmentMethod.description?if_exists}${shippingEstimate?if_exists}
                            </div>                           
                          </td>
                        </tr>                        
                      </#list>
                      <#if !carrierShipmentMethodList?exists || carrierShipmentMethodList?size == 0>                     
                        <tr>
                          <td width='1%' valign="top">
                            <input type='radio' name='shipping_method' value="Default" checked>
                          </td>
                          <td valign="top">
                            <div class='tabletext'>Use Default: No other shipping methods available.</div>
                          </td>
                        </tr>
                      </#if>
                      <tr><td colspan='2'><hr class='sepbar'></td></tr>
                      <tr>
                        <td colspan='2'>
                          <div class="head2"><b>Ship all at once, or 'as available'?</b></div>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top">
                          <input type='radio' <#if !cart.getMaySplit()?default(false)>checked</#if> name='may_split' value='false'>
                        </td>
                        <td valign="top">
                          <div class="tabletext">Please wait until the entire order is ready before shipping.</div>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top">
                          <input <#if cart.getMaySplit()?default(false)>checked</#if> type='radio' name='may_split' value='true'>
                        </td>
                        <td valign="top">
                          <div class="tabletext">Please ship items I ordered as they become available (you may incur additional shipping charges).</div>
                        </td>
                      </tr>
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>
                      <tr>
                        <td colspan="2">
                          <div class="head2"><b>Special Instructions</b></div>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <textarea class='textAreaBox' cols="30" rows="3" name="shipping_instructions">${cart.getShippingInstructions()?if_exists}</textarea>
                        </td>
                      </tr>
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>       
                      <tr>
                        <td colspan="2">
                          <span class="head2"><b>PO Number</b></span>&nbsp;
                          <#if cart.getPoNumber()?exists && cart.getPoNumber() != "(none)">
                            <#assign currentPoNumber = cart.getPoNumber()>
                          </#if>
                          <input type="text" class='inputBox' name="corresponding_po_id" size="15" value='${currentPoNumber?if_exists}'>
                        </td>
                      </tr>                                                           
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>                      
                      <tr>
                        <td colspan="2">
                          <div>
                            <span class="head2"><b>Is This a Gift?</b></span>
                            <input type='radio' <#if cart.getIsGift()?default(false)>checked</#if> name='is_gift' value='true'><span class='tabletext'>Yes</span>
                            <input type='radio' <#if !cart.getIsGift()?default(false)>checked</#if> name='is_gift' value='false'><span class='tabletext'>No</span>
                          </div>
                        </td>
                      </tr>
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>
                      <tr>
                        <td colspan="2">
                          <div class="head2"><b>Gift Message</b></div>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <textarea class='textAreaBox' cols="30" rows="3" name="gift_message">${cart.getGiftMessage()?if_exists}</textarea>
                        </td>
                      </tr>
                      <tr><td colspan="2"><hr class='sepbar'></td></tr>
                      <tr>
                        <td colspan="2">
                          <div class="head2"><b>Email Addresses</b></div>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <div class="tabletext">Your order will be sent to the following email addresses:</div>
                          <div class="tabletext">
                            <b>
                              <#list context.emailList as email>
                                ${email.infoString?if_exists}<#if email_has_next>,</#if>
                              </#list>
                            </b>
                          </div>
                          <div class="tabletext">Your may update these in your <a href="<@ofbizUrl>/viewprofile?DONE_PAGE=checkoutoptions</@ofbizUrl>" class="buttontext">profile</a>.</div>
                          <br>
                          <div class="tabletext">You may add other comma separated email addresses here that will be used only for the current order:</div>
                          <input type="text" class='inputBox' size="30" name="order_additional_emails" value='${cart.getOrderAdditionalEmails()?if_exists}'>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
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
      <a href="javascript:submitForm(document.checkoutInfoForm, 'DN', '');" class="buttontextbig">[Next]</a>
    </td>
  </tr>
</table>
