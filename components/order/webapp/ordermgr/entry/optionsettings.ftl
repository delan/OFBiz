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
 *@version    $Revision: 1.4 $
 *@since      2.2
-->

<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align='left'>
            <div class='boxhead'>&nbsp;Order Option Settings</div>
          </td> 
          <td nowrap align="right">
            <div class="tabletext">
              <a href="<@ofbizUrl>/setOptions</@ofbizUrl>" class="submenutext">Refresh</a><a href="<@ofbizUrl>/orderentry</@ofbizUrl>" class="submenutext">Items</a><a href="<@ofbizUrl>/setShipping</@ofbizUrl>" class="submenutext">Shipping</a><a href="javascript:document.optsetupform.submit();" class="submenutextright">Continue</a>
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
            <form method="post" action="<@ofbizUrl>/finalizeOrder</@ofbizUrl>" name="optsetupform">
              <input type="hidden" name="finalizeMode" value="options">              
              <table width="100%" cellpadding="1" border="0" cellpadding="0" cellspacing="0">
                <#list carrierShipmentMethodList as carrierShipmentMethod>                    
                <tr>
                  <td width='1%' valign="top" >
                    <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
                    <input type='radio' name='shipping_method' value='${shippingMethod}' <#if shippingMethod == chosenShippingMethod?default("N@A")>checked</#if>>       
                  </td>
                  <td valign="top">                            
                    <div class='tabletext'>                                                 
                      <#if carrierShipmentMethod.partyId != "_NA_">${carrierShipmentMethod.partyId?if_exists}&nbsp;</#if>${carrierShipmentMethod.description?if_exists}
                      <#if cart.getShippingContactMechId()?exists>
                        <#assign shippingEstMap = Static["org.ofbiz.order.shoppingcart.shipping.ShippingEvents"].getShipEstimate(delegator, cart, shippingMethod)>
                        <#if shippingEstMap?has_content && shippingEstMap.shippingTotal?exists>
                          - <@ofbizCurrency amount=shippingEstMap.shippingTotal isoCode=cart.getCurrency()/>
                        </#if>
                      </#if>
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
                    <input type="text" class='inputBox' name="corresponding_po_id" size="15" value='${cart.getPoNumber()?if_exists}'>
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
              </table>
            </form>
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
