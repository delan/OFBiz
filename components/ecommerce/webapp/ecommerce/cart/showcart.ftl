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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Rev$
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<script language="JavaScript">
<!--
function toggle(e) {
    e.checked = !e.checked;
}
function checkToggle(e) {
    var cform = document.cartform;
    if (e.checked) {
        var len = cform.elements.length;
        var allchecked = true;
        for (var i = 0; i < len; i++) {
            var element = cform.elements[i];
            if (element.name == "selectedItem" && !element.checked) {
                allchecked = false;
            }
            cform.selectAll.checked = allchecked;
        }
    } else {
        cform.selectAll.checked = false;
    }
}
function toggleAll(e) {
    var cform = document.cartform;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];
        if (element.name == "selectedItem" && element.checked != e.checked) {
            toggle(element);
        }
    }
}
function removeSelected() {
    var cform = document.cartform;
    cform.removeSelected.value = true;
    cform.submit();
}
function addToList() {
    var cform = document.cartform;
    cform.action = "<@ofbizUrl>/addBulkToShoppingList</@ofbizUrl>";
    cform.submit();
}
function gwAll(e) {
    var cform = document.cartform;
    var len = cform.elements.length;
    var selectedValue = e.value;
    if (selectedValue == "") {
        return;
    }
    
    var cartSize = ${shoppingCartSize};
    var passed = 0;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];
        var ename = element.name;
        var sname = ename.substring(0,16);
        if (sname == "option^GIFT_WRAP") {
            var options = element.options;
            var olen = options.length;
            var matching = -1;
            for (var x = 0; x < olen; x++) {
                var thisValue = element.options[x].value;
                if (thisValue == selectedValue) {
                    element.selectedIndex = x;
                    passed++;
                }
            }
        }
    }
    if (cartSize > passed && selectedValue != "NO^") {
        alert("Selected Gift Wrap is not avaiable for all items. The items which are available have been selected, the others remain unchanged.");
    }
    cform.submit();
}
//-->
</script>

<script language="JavaScript">
<!--
function setAlternateGwp(field) {
  window.location=field.value;
};
//-->
</script>

<table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.CommonQuickAdd}</div>
          </td>
          <td valign="middle" align="right">
            <#if ((sessionAttributes.lastViewedProducts)?has_content && sessionAttributes.lastViewedProducts?size > 1)>
              <#assign continueLink = "/product?product_id=" + sessionAttributes.lastViewedProducts.get(0)>
            <#else>
              <#assign continueLink = "/main">
            </#if>
            <a href="<@ofbizUrl>${continueLink}</@ofbizUrl>" class="submenutext">${uiLabelMap.EcommerceContinueShopping}</a><#if (shoppingCartSize > 0)><a href="<@ofbizUrl>/checkoutoptions</@ofbizUrl>" class="submenutextright">${uiLabelMap.EcommerceCheckout}</a><#else><span class="submenutextrightdisabled">${uiLabelMap.EcommerceCheckout}</span></#if>
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
            <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="quickaddform" style="margin: 0;">
              <input type="text" class="inputBox" name="add_product_id" value="${requestParameters.add_product_id?if_exists}">
              <input type="text" class="inputBox" size="5" name="quantity" value="${requestParameters.quantity?default("1")}">
              <input type="submit" class="smallSubmit" value="${uiLabelMap.EcommerceAddToCart}">
              <#-- <a href="javascript:document.quickaddform.submit()" class="buttontext"><nobr>[${uiLabelMap.EcommerceAddToCart}]</nobr></a> -->
            </form>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<script language="JavaScript">
<!--
  document.quickaddform.add_product_id.focus();
//-->
</script>
<br/>
<table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.EcommerceShoppingCart}</div>
          </td>
          <td valign="middle" align="right">
            <div class="lightbuttontextdisabled">
              <#--<a href="<@ofbizUrl>/main</@ofbizUrl>" class="lightbuttontext">[${uiLabelMap.EcommerceContinueShopping}]</a>-->
              <#if (shoppingCartSize > 0)>
                <a href="javascript:document.cartform.submit();" class="submenutext">${uiLabelMap.EcommerceRecalculateCart}</a><a href="<@ofbizUrl>/emptycart</@ofbizUrl>" class="submenutext">${uiLabelMap.EcommerceEmptyCart}</a><a href="javascript:removeSelected();" class="submenutextright">${uiLabelMap.EcommerceRemoveSelected}</a>
              <#else>
                <span class="submenutextdisabled">${uiLabelMap.EcommerceRecalculateCart}</span><span class="submenutextdisabled">${uiLabelMap.EcommerceEmptyCart}</span><span class="submenutextrightdisabled">${uiLabelMap.EcommerceRemoveSelected}</span>
              </#if>
            </div>
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
  <#if (shoppingCartSize > 0)>
    <form method="POST" action="<@ofbizUrl>/modifycart</@ofbizUrl>" name="cartform" style="margin: 0;">
      <input type="hidden" name="removeSelected" value="false">
      <table width="100%" cellspacing="0" cellpadding="1" border="0">
        <tr>
          <td NOWRAP>&nbsp;</td>
          <td NOWRAP><div class="tabletext"><b>${uiLabelMap.EcommerceProduct}</b></div></td>
          <#if asslGiftWraps?has_content && showOrderGiftWrap?default("true") == "true">
            <td NOWRAP align="right">
              <select class="selectBox" name="GWALL" onChange="javascript:gwAll(this);">
                <option value="">Gift Wrap All Items</option>
                <option value="NO^">No Gift Wrap</option>
                <#list allgiftWraps as option>
                  <option value="${option.productFeatureId}">${option.description} : ${option.defaultAmount?default(0)?string.currency}</option>
                </#list>
              </select>
          <#else>
            <td NOWRAP>&nbsp;</td>
          </#if>
          <td NOWRAP align="center"><div class="tabletext"><b>${uiLabelMap.CommonQuantity}</b></div></td>
          <td NOWRAP align="right"><div class="tabletext"><b>${uiLabelMap.EcommerceUnitPrice}</b></div></td>
          <td NOWRAP align="right"><div class="tabletext"><b>${uiLabelMap.EcommerceAdjustments}</b></div></td>
          <td NOWRAP align="right"><div class="tabletext"><b>${uiLabelMap.EcommerceItemTotal}</b></div></td>
          <td NOWRAP align="center"><input type="checkbox" name="selectAll" value="0" onclick="javascript:toggleAll(this);"></td>
        </tr>

        <#assign itemsFromList = false>
        <#assign promoItems = false>
        <#list shoppingCart.items() as cartLine>
          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
          <#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures()>
          <#-- show adjustment info -->
          <#list cartLine.getAdjustments() as cartLineAdjustment>
            <!-- cart line ${cartLineIndex} adjustment: ${cartLineAdjustment} -->
          </#list>
          <tr><td>&nbsp;</td><td colspan="6"><hr class="sepbar"></td></tr>
          <tr>
            <td>
                <#if cartLine.getShoppingListId()?exists>
                  <#assign itemsFromList = true>
                  <a href="<@ofbizUrl>/editShoppingList?shoppingListId=${cartLine.getShoppingListId()}</@ofbizUrl>" class="buttontext">L</a>&nbsp;&nbsp;
                <#elseif cartLine.getIsPromo()>
                  <#assign promoItems = true>
                  <a href="<@ofbizUrl>/view/showcart</@ofbizUrl>" class="buttontext">P</a>&nbsp;&nbsp;
                <#else>
                  &nbsp;
                </#if>
            </td>
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
                    <#-- <b>${cartLineIndex}</b> - -->
                    <a href="<@ofbizUrl>/product?product_id=${cartLine.getProductId()}</@ofbizUrl>" class="buttontext">${cartLine.getProductId()} -
                    ${cartLine.getName()?if_exists}</a> : ${cartLine.getDescription()?if_exists}

                    <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
                    <#assign itemProduct = cartLine.getProduct()>
                    <#assign isStoreInventoryRequired = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequired(request, itemProduct)>
                    <#assign isStoreInventoryAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryAvailable(request, cartLine.getProductId(), cartLine.getQuantity())>
                    <#if !isStoreInventoryRequired && !isStoreInventoryAvailable && itemProduct.inventoryMessage?has_content>
                        <b>(${itemProduct.inventoryMessage})</b>
                    </#if>

                  <#else>
                    <#-- this is a non-product item -->
                    <b>${cartLine.getItemTypeDescription()?if_exists}</b> : ${cartLine.getName()?if_exists}
                  </#if>
                </div>
                
                <#if (cartLine.getIsPromo() && cartLine.getAlternativeOptionProductIds()?has_content)>
                  <#-- Show alternate gifts if there are any... -->
                  <div class="tableheadtext">You may also choose one of the following for your gift:</div>
                  <select name="dummyAlternateGwpSelect${cartLineIndex}" onChange="setAlternateGwp(this);">
                  <option value="">- Choose Another Gift -</option>
                  <#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductWorker"].getGwpAlternativeOptionName(delegator, alternativeOptionProductId, requestAttributes.locale)>
                    <option value="<@ofbizUrl>setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&alternateGwpLine=${cartLineIndex}</@ofbizUrl>">${alternativeOptionName?default(alternativeOptionProductId)}</option>
                  </#list>
                  </select>
                  <#-- this is the old way, it lists out the options and is not as nice as the drop-down
                  <#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductWorker"].getGwpAlternativeOptionName(delegator, alternativeOptionProductId, requestAttributes.locale)>
                    <div class="tabletext"><a href="<@ofbizUrl>/setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&alternateGwpLine=${cartLineIndex}</@ofbizUrl>" class="buttontext">Select: ${alternativeOptionName?default(alternativeOptionProductId)}</a></div>
                  </#list>
                  -->
                </#if>
            </td>

            <#-- gift wrap option -->
            <#assign showNoGiftWrapOptions = false>
            <td nowrap align="right">
              <#assign giftWrapOption = lineOptionalFeatures.GIFT_WRAP?if_exists>
              <#assign selectedOption = cartLine.getAdditionalProductFeatureAndAppl("GIFT_WRAP")?if_exists>
              <#if giftWrapOption?has_content>
                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}" onChange="javascript:document.cartform.submit()">
                  <option value="NO^">No Gift Wrap</option>
                  <#list giftWrapOption as option>
                    <option value="${option.productFeatureId}" <#if ((selectedOption.productFeatureId)?exists && selectedOption.productFeatureId == option.productFeatureId)>SELECTED</#if>>${option.description} : ${option.amount?default(0)?string.currency}</option>
                  </#list>
                </select>
              <#elseif showNoGiftWrapOptions>
                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}" onChange="javascript:document.cartform.submit()">
                  <option value="">No Gift Wrap</option>
                </select>
              <#else>
                &nbsp;
              </#if>
            </td>
            <#-- end gift wrap option -->

            <td nowrap align="center">
              <div class="tabletext">
                <#if cartLine.getIsPromo() || cartLine.getShoppingListId()?exists>
                    ${cartLine.getQuantity()?string.number}
                <#else>
                    <input size="6" class="inputBox" type="text" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}">
                </#if>
              </div>
            </td>
            <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=cartLine.getBasePrice() isoCode=shoppingCart.getCurrency()/></div></td>
            <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=cartLine.getOtherAdjustments() isoCode=shoppingCart.getCurrency()/></div></td>
            <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=cartLine.getItemSubTotal() isoCode=shoppingCart.getCurrency()/></div></td>
            <td nowrap align="center"><div class="tabletext"><#if !cartLine.getIsPromo()><input type="checkbox" name="selectedItem" value="${cartLineIndex}" onclick="javascript:checkToggle(this);"><#else>&nbsp;</#if></div></td>
          </tr>
        </#list>

        <#if shoppingCart.getAdjustments()?has_content>
            <tr><td>&nbsp;</td><td colspan="6"><hr class="sepbar"></td></tr>
              <tr>
                <td colspan="5" nowrap align="right"><div class="tabletext">${uiLabelMap.CommonSubTotal}:</div></td>
                <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=shoppingCart.getSubTotal() isoCode=shoppingCart.getCurrency()/></div></td>
                <td>&nbsp;</td>
              </tr>
            <#list shoppingCart.getAdjustments() as cartAdjustment>
              <#assign adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType")>
              <!-- adjustment info: ${cartAdjustment.toString()} -->
              <tr>
                <td colspan="5" nowrap align="right">
                    <div class="tabletext">
                        <i>${uiLabelMap.EcommerceAdjustment}</i> - ${adjustmentType.description?if_exists}
                        <#if cartAdjustment.productPromoId?has_content><a href="<@ofbizUrl>/showPromotionDetails?productPromoId=${cartAdjustment.productPromoId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonDetails}]</a></#if>:
                    </div>
                </td>
                <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) isoCode=shoppingCart.getCurrency()/></div></td>
                <td>&nbsp;</td>
              </tr>
            </#list>
        </#if>

        <tr>
          <td colspan="5" align="right" valign=bottom>
            <div class="tabletext"><b>${uiLabelMap.EcommerceCartTotal}:</b></div>
          </td>
          <td align="right" valign=bottom>
            <hr size=1 class="sepbar">
            <div class="tabletext"><b><@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=shoppingCart.getCurrency()/></b></div>
          </td>
          <td>&nbsp;</td>
        </tr>
        <#if itemsFromList>
        <tr>
          <td valign="bottom" colspan="7"><div class="tabletext">L - ${uiLabelMap.EcommerceItemsfromShopingList}.</td>
        </tr>
        </#if>
        <#if promoItems>
        <tr>
          <td valign="bottom" colspan="7"><div class="tabletext">P - ${uiLabelMap.EcommercePromotionalItems}.</td>
        </tr>
        </#if>
        <#if !itemsFromList && !promoItems>
        <tr>
          <td colspan="7">&nbsp;</td>
        </tr>
        </#if>
        <tr><td>&nbsp;</td><td colspan="6"><hr class="sepbar"></td></tr>
        <tr>
          <td colspan="7" align="right" valign="bottom">
            <div class="tabletext">
              <#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
              <select name="shoppingListId" class="selectBox">
                <#if shoppingLists?has_content>
                  <#list shoppingLists as shoppingList>
                    <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
                  </#list>
                </#if>
                <option value="">---</option>
                <option value="">${uiLabelMap.EcommerceNewShoppingList}</option>
              </select>
              &nbsp;&nbsp;
              <a href="javascript:addToList();" class="buttontext">[${uiLabelMap.EcommerceAddSelectedtoList}]</a>&nbsp;&nbsp;
              <#else>
               ${uiLabelMap.EcommerceYouMust} <a href="<@ofbizUrl>/checkLogin/showcart</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonLogin}</a>
                ${uiLabelMap.EcommerceToAddSelectedItemsToShoppingList}.&nbsp;
              </#if>
            </div>
          </td>
        </tr>
        <tr><td>&nbsp;</td><td colspan="6"><hr class="sepbar"></td></tr>
        <tr>
          <td colspan="7" align="center" valign="bottom">
            <div class="tabletext"><input type="checkbox" onClick="javascript:document.cartform.submit()" name="alwaysShowcart" <#if shoppingCart.viewCartOnAdd()>checked</#if>>&nbsp;${uiLabelMap.EcommerceAlwaysViewCartAfterAddingAnItem}.</div>
          </td>
        </tr>
      </table>
    </form>
  <#else>
    <div class="head2">${uiLabelMap.EcommerceYourShoppingCartEmpty}.</div>
  </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
<#-- Copy link bar to bottom to include a link bar at the bottom too -->
</table>

  <br/>
  <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;Promotion/Coupon Codes</div>
            </td>
            <#--<td valign="middle" align="right">&nbsp;</td>-->
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
          <tr>
            <td>
              <div class="tabletext">
	            <form method="POST" action="<@ofbizUrl>/addpromocode<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="addpromocodeform" style="margin: 0;">
	              <input type="text" class="inputBox" size="15" name="productPromoCodeId" value="">
	              <input type="submit" class="smallSubmit" value="Add Code">
	              <#assign productPromoCodeIds = (shoppingCart.getProductPromoCodesEntered())?if_exists>
	              <#if productPromoCodeIds?has_content>
	                Entered Codes:
	                <#list productPromoCodeIds as productPromoCodeId>
	                  ${productPromoCodeId}
	                </#list>
	              </#if>
	            </form>
	          </div>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>

<#if showPromoText?exists && showPromoText>
  <br/>
  <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.EcommerceSpecialOffers}</div>
            </td>
            <#--<td valign="middle" align="right">&nbsp;</td>-->
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
                  <#-- show promotions text -->
                  <#list productPromos as productPromo>
                    <tr>
                      <td>
                        <div class="tabletext"><a href="<@ofbizUrl>/showPromotionDetails?productPromoId=${productPromo.productPromoId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonDetails}]</a> ${productPromo.promoText?if_exists}</div>
                      </td>
                    </tr>
                    <#if productPromo_has_next>
                      <tr><td><hr class="sepbar"></td></tr>
                    </#if>
                  </#list>
                  <tr><td><hr class="sepbar"></td></tr>
                  <tr>
                    <td>
                      <div class="tabletext"><a href="<@ofbizUrl>/showAllPromotions</@ofbizUrl>" class="buttontext">[${uiLabelMap.EcommerceViewAllPromotions}]</a></div>
                    </td>
                  </tr>
                </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</#if>

<#if associatedProducts?has_content>
  <br/>
  <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.EcommerceYouMightAlsoIntrested}:</div>
            </td>
            <#--<td valign="middle" align="right">&nbsp;</td>-->
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
			      <#-- random complementary products -->
			      <#list associatedProducts as assocProduct>
			        <tr>
			          <td>
			            ${setRequestAttribute("optProduct", assocProduct)}
			            ${setRequestAttribute("listIndex", assocProduct_index)}
			            ${pages.get("/catalog/productsummary.ftl")}
			          </td>
			        </tr>
			        <#if assocProduct_has_next>
			          <tr><td><hr class="sepbar"></td></tr>
			        </#if>
			      </#list>
			    </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</#if>

<#if (shoppingCartSize?default(0) > 0)>
  <br/>
  <#include "/cart/promoUseDetailsInline.ftl"/>
</#if>
