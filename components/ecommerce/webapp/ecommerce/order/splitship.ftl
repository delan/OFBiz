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
 *@version    $Rev$
 *@since      3.3
-->

<script language="javascript" type="text/javascript">
<!--
function submitForm(form, mode, value) {
    if (mode == "DN") {
        // done action; payment info
        form.action="<@ofbizUrl>/updateShippingOptions/checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "CS") {
        // continue shopping
        form.action="<@ofbizUrl>/updateShippingOptions/showcart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NA") {
        // new address
        form.action="<@ofbizUrl>/updateShippingOptions/editcontactmech?DONE_PAGE=splitship&preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION</@ofbizUrl>";
        form.submit();
    } else if (mode == "SV") {
        // save option; return to current screen
        form.action="<@ofbizUrl>/updateShippingOptions/splitship</@ofbizUrl>";
        form.submit();
    } else if (mode == "SA") {
        // selected shipping address
        form.action="<@ofbizUrl>/updateShippingAddress/splitship</@ofbizUrl>";
        form.submit();
    }
}
// -->
</script>

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Item Groups</div>
          </td>
          <td valign="middle" align="right">
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td>
            <table width="100%" cellspacing="0" cellpadding="1" border="0">
              <#assign shipGroups = cart.getShipGroups()>
              <#if (shipGroups.size() > 0)>
                <#assign groupIdx = 0>
                <#list shipGroups as group>
                  <#assign shipEstimateWrapper = Static["org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper"].getWrapper(dispatcher, cart, groupIdx)>
                  <#assign carrierShipmentMethods = shipEstimateWrapper.getShippingMethods()>
                  <#assign groupNumber = groupIdx + 1>
                  <form method="POST" action="#" name="editgroupform${groupIdx}" style="margin: 0;">
                    <input type="hidden" name="groupIndex" value="${groupIdx}">
                    <tr>
                      <td>
                        <div class="tabletext"><b>Group ${groupNumber}:</b></div>
                        <#list group.getShipItems() as item>                          
                          <#assign groupItem = group.getShipItemInfo(item)>
                          <div class="tabletext">&nbsp;&nbsp;&nbsp;${item.getName()} - (${groupItem.getItemQuantity()})</div>
                        </#list>
                      </td>
                      <td>
                        <div>
                          <span class='tabletext'>${uiLabelMap.CommonAdd}:</span>
                          <a href="javascript:submitForm(document.editgroupform${groupIdx}, 'NA', '');" class="buttontext">[${uiLabelMap.PartyAddNewAddress}]</a>
                        </div>
                        <div>
                          <#assign selectedContactMechId = cart.getShippingContactMechId(groupIdx)?default("")>
                          <select name="shippingContactMechId" class="selectBox" onchange="javascript:submitForm(document.editgroupform${groupIdx}, 'SA', null);">
                            <option value="">Select Shipping Address</option>
                            <#list shippingContactMechList as shippingContactMech>
                              <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
                              <option value="${shippingAddress.contactMechId}" <#if (shippingAddress.contactMechId == selectedContactMechId)>selected</#if>>${shippingAddress.address1}</option>
                            </#list>
                          </select>
                        </div>
                        <#if cart.getShipmentMethodTypeId(groupIdx)?exists>
                          <#assign selectedShippingMethod = cart.getShipmentMethodTypeId(groupIdx) + "@" + cart.getCarrierPartyId(groupIdx)>
                        <#else>
                          <#assign selectedShippingMethod = "">
                        </#if>
                        <select name="shipmentMethodString" class="selectBox">
                          <option value="">Select Shipping Method</option>
                          <#list carrierShipmentMethods as carrierShipmentMethod>
                            <#assign shippingEst = shipEstimateWrapper.getShippingEstimate(carrierShipmentMethod)?default(-1)>
                            <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
                            <option value="${shippingMethod}" <#if (shippingMethod == selectedShippingMethod)>selected</#if>>
                              <#if carrierShipmentMethod.partyId != "_NA_">
                                ${carrierShipmentMethod.partyId?if_exists}&nbsp;
                              </#if>
                              ${carrierShipmentMethod.description?if_exists}
                              <#if shippingEst?has_content>
                                &nbsp;-&nbsp;
                                <#if (shippingEst > -1)?exists>
                                  <@ofbizCurrency amount=shippingEst isoCode=cart.getCurrency()/>
                                <#else>
                                  Calculated Offline
                                </#if>
                              </#if>
                            </option>
                          </#list>
                        </select>

                        <div class="head2"><b>${uiLabelMap.OrderSpecialInstructions}</b></div>
                        <textarea class='textAreaBox' cols="35" rows="3" wrap="hard" name="shippingInstructions">${cart.getShippingInstructions(groupIdx)?if_exists}</textarea>
                      </td>
                      <td>
                        <div>
                          <select name="maySplit" class="selectBox">
                            <#assign maySplitStr = cart.getMaySplit(groupIdx)?default("")>
                            <option value="">Splitting Preference</option>
                            <option value="false" <#if maySplitStr == "N">selected</#if>>Ship All Items Together</option>
                            <option value="true" <#if maySplitStr == "Y">selected</#if>>Ship Items When Available</option>
                          </select>
                        </div>
                        <div>
                          <select name="isGift" class="selectBox">
                            <#assign isGiftStr = cart.getIsGift(groupIdx)?default("")>
                            <option value="">Is Gift?</option>
                            <option value="false" <#if isGiftStr == "N">selected</#if>>Not A Gift</option>
                            <option value="true" <#if isGiftStr == "Y">selected</#if>>Yes, Is A Gift</option>
                          </select>
                        </div>

                        <div class="head2"><b>${uiLabelMap.OrderGiftMessage}</b></div>
                        <textarea class='textAreaBox' cols="30" rows="3" wrap="hard" name="giftMessage">${cart.getGiftMessage(groupIdx)?if_exists}</textarea>
                      </td>
                      <td><input type="button" class="smallSubmit" value="Save" onclick="javascript:submitForm(document.editgroupform${groupIdx}, 'SV', null);"></td>
                    </tr>
                    <#assign groupIdx = groupIdx + 1>
                    <#if group_has_next>
                      <tr>
                        <td colspan="6"><hr class="sepbar"></td>
                      </tr>
                    </#if>
                  </form>
                </#list>
              <#else>
                <div class="tabletext">No ship groups defined.</div>
              </#if>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<br>
<table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Assign Items</div>
          </td>
          <td valign="middle" align="right">
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td>
            <table width="100%" cellspacing="0" cellpadding="1" border="0">
              <tr>
                <td NOWRAP><div class="tabletext"><b>${uiLabelMap.EcommerceProduct}</b></div></td>
                <td NOWRAP align="center"><div class="tabletext"><b>Total Qty</b></div></td>
                <td NOWRAP>&nbsp;</td>
                <td NOWRAP align="center"><div class="tabletext"><b>Move Qty</b></div></td>
                <td NOWRAP>&nbsp;</td>
                <td NOWRAP>&nbsp;</td>
              </tr>

              <#list cart.items() as cartLine>
                <#assign cartLineIndex = cart.getItemIndex(cartLine)>
                <tr>
                  <form method="POST" action="<@ofbizUrl>/updatesplit</@ofbizUrl>" name="editgroupform" style="margin: 0;">
                    <input type="hidden" name="itemIndex" value="${cartLineIndex}">
                    <td>

                      <div class="tabletext">
                        <#if cartLine.getProductId()?exists>
                          <#-- product item -->
                          <#-- start code to display a small image of the product -->
                          <#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", requestAttributes.locale)?if_exists>
                          <#if !smallImageUrl?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
                          <#if smallImageUrl?has_content>
                            <a href="<@ofbizUrl>/product?product_id=${cartLine.getProductId()}</@ofbizUrl>">
                              <img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" align="left" width="50" class="imageborder" border="0"/>
                            </a>
                          </#if>
                          <#-- end code to display a small image of the product -->
                          <a href="<@ofbizUrl>/product?product_id=${cartLine.getProductId()}</@ofbizUrl>" class="buttontext">${cartLine.getProductId()} -
                          ${cartLine.getName()?if_exists}</a> : ${cartLine.getDescription()?if_exists}

                          <#-- display the registered ship groups and quantity -->
                          <#assign itemShipGroups = cart.getShipGroups(cartLine)>
                          <#list itemShipGroups.entrySet() as group>
                            <div class="tabletext">
                              <#assign groupNumber = group.getKey() + 1>
                              <b>Group - </b>${groupNumber} / <b>Quantity - </b>${group.getValue()}
                            </div>
                          </#list>

                          <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
                          <#assign itemProduct = cartLine.getProduct()>
                          <#assign isStoreInventoryNotRequiredAndNotAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequiredAndAvailable(request, itemProduct, cartLine.getQuantity(), false, false)>
                          <#if isStoreInventoryNotRequiredAndNotAvailable && itemProduct.inventoryMessage?has_content>
                            <b>(${itemProduct.inventoryMessage})</b>
                          </#if>

                        <#else>
                          <#-- this is a non-product item -->
                          <b>${cartLine.getItemTypeDescription()?if_exists}</b> : ${cartLine.getName()?if_exists}
                        </#if>
                      </div>

                    </td>
                    <td align="right">
                      <div class="tabletext">${cartLine.getQuantity()?string.number}&nbsp;&nbsp;&nbsp;</div>
                    </td>
                    <td>
                      <div>&nbsp;</div>
                    </td>
                    <td align="center">
                      <input size="6" class="inputBox" type="text" name="quantity" value="${cartLine.getQuantity()?string.number}">
                    </td>
                    <td>
                      <div>&nbsp;</div>
                    </td>
                    <td>
                      <div class="tabletext">From:
                        <select name="fromGroupIndex" class="selectBox">
                          <#list itemShipGroups.entrySet() as group>
                            <#assign groupNumber = group.getKey() + 1>
                            <option value="${group.getKey()}">Group ${groupNumber}</option>
                          </#list>
                        </select>
                      </div>
                    </td>
                    <td>
                      <div class="tabletext">To:
                        <select name="toGroupIndex" class="selectBox">
                          <#list 0..(cart.getShipGroupSize() - 1) as groupIdx>
                            <#assign groupNumber = groupIdx + 1>
                            <option value="${groupIdx}">Group ${groupNumber}</option>
                          </#list>
                          <option value="-1">New Group</option>
                        </select>
                      </div>
                    </td>
                    <td><input type="submit" class="smallSubmit" value="Submit"></td>
                  </form>
                </tr>
              </#list>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<table width="100%">
  <tr valign="top">
    <td align="left">
      &nbsp;<a href="<@ofbizUrl>/view/showcart</@ofbizUrl>" class="buttontextbig">[${uiLabelMap.OrderBacktoShoppingCart}]</a>
    </td>
    <td align="right">
      <a href="<@ofbizUrl>/view/checkoutpayment</@ofbizUrl>" class="buttontextbig">[${uiLabelMap.CommonContinue}]</a>
    </td>
  </tr>
</table>
