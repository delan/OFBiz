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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@author     Jean-Luc.Malet@nereide.biz (migration to uiLabelMap)
 *@version    $Rev$
 *@since      2.2
-->

<script language="JavaScript" type="text/javascript">
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
function toggleAll() {
    var cform = document.cartform;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var e = cform.elements[i];   
        if (e.name == "selectedItem") {
            toggle(e);
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
        alert("${uiLabelMap.OrderSelectedGiftNotAvailableForAll}");
    }
    cform.submit();
}
</script>

<div class="screenlet">
    <div class="screenlet-body">
      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td>           
            <form method="post" action="<@ofbizUrl>/additem</@ofbizUrl>" name="quickaddform" style="margin: 0;">
              <table border="0">
                <tr>
                  <td align="right"><div class="tableheadtext">${uiLabelMap.ProductProductId} :</div></td>
                  <td><input type="text" class="inputBox" size="25" name="add_product_id" value="${requestParameters.add_product_id?if_exists}"/>
                    <span class='tabletext'>
                      <a href="javascript:call_fieldlookup2(document.quickaddform.add_product_id,'LookupProduct');">
                        <img src="/images/fieldlookup.gif" width="15" height="14" border="0" alt="Click here For Field Lookup"/>
                      </a>
                    </span>
                  </td>
                </tr>
                <tr>
                  <td align="right"><div class="tableheadtext">${uiLabelMap.OrderQuantity} :</div></td>
                  <td><input type="text" class="inputBox" size="6" name="quantity" value="${requestParameters.quantity?default("1")}"/></td>
                </tr>
                <tr>
                  <td align="right"><div class="tableheadtext">${uiLabelMap.OrderDesiredDeliveryDate} :</div></td>
                  <td>
                    <div class="tabletext">
                      <input type="text" class="inputBox" size="25" maxlength="30" name="itemDesiredDeliveryDate"<#if useAsDefaultDesiredDeliveryDate?exists> value="${defaultDesiredDeliveryDate}"</#if>/>
                      <a href="javascript:call_cal(document.quickaddform.itemDesiredDeliveryDate,'${defaultDesiredDeliveryDate} 00:00:00.0');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="${uiLabelMap.calendar_click_here_for_calendar}"/></a>
                      <input type="checkbox" class="inputBox" name="useAsDefaultDesiredDeliveryDate" value="true"<#if useAsDefaultDesiredDeliveryDate?exists> checked="checked"</#if>/>
                      ${uiLabelMap.OrderUseDefaultDesiredDeliveryDate}
                    </div>
                  </td>
                </tr>
                <tr>
                  <td align="right"><div class="tableheadtext">${uiLabelMap.CommonComment} :</div></td>
                  <td>
                    <div class="tabletext">
                      <input type="text" class="inputBox" size="25" name="itemComment" value="${defaultComment?if_exists}">
                      <input type="checkbox" class="inputBox" name="useAsDefaultComment" value="true" <#if useAsDefaultComment?exists>checked</#if>>
                      ${uiLabelMap.OrderUseDefaultComment}
                    </div>
                  </td>
                </tr>
                <tr>
                  <td></td>
                  <td><input type="submit" class="smallSubmit" value="${uiLabelMap.OrderAddToOrder}"/></td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
        <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
        <tr><td><hr class="sepbar"/></td></tr>
        <tr>
          <td>
            <form method="post" action="<@ofbizUrl>/additem</@ofbizUrl>" name="bulkworkaddform" style="margin: 0;">
                <div class="tableheadtext">
                    ${uiLabelMap.ProductItem}:&nbsp;${uiLabelMap.ProductType}:&nbsp;<select name="add_item_type" class="selectBox"><option value="BULK_ORDER_ITEM">Bulk Item</option><option value="WORK_ORDER_ITEM">${uiLabelMap.ProductWorkItem}</option></select>
                    Category:&nbsp;<select name="add_category_id" class="selectBox">
                      <option></option>
                      <#list productCategoryList as productCategory>
                        <option value="${productCategory.productCategoryId}">${productCategory.description?default("No Description")} [${productCategory.productCategoryId}]</option>
                      </#list>
                    </select>
                </div>
                <div class="tableheadtext">
                    ${uiLabelMap.CommonDescription}:&nbsp;<input type="text" class="inputBox" size="25" name="add_item_description" value="${requestParameters.add_product_id?if_exists}"/>
                    ${uiLabelMap.OrderQuantity}:&nbsp;<input type="text" class="inputBox" size="3" name="quantity" value="${requestParameters.quantity?default("1")}"/>
                    ${uiLabelMap.OrderPrice}:&nbsp;<input type="text" class="inputBox" size="6" name="price" value="${requestParameters.price?if_exists}"/>
                    <input type="submit" class="smallSubmit" value="${uiLabelMap.OrderAddToOrder}"/>
                </div>
            </form>
          </td>
        </tr>  
        </#if>      
      </table>
    </div>
</div>

<!-- Screenlet to add cart to shopping list. The shopping lists are presented in a dropdown box. -->

<#if (shoppingLists?exists) && (shoppingCartSize > 0)>
  <div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">&nbsp;${uiLabelMap.OrderAddOrderToShoppingList}</div>
    </div>
    <div class="screenlet-body">
      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td>           
            <form method="post" name="addBulkToShoppingList" action="<@ofbizUrl>/addBulkToShoppingList</@ofbizUrl>" style='margin: 0;'>
              <#assign index = 0/>
              <#list shoppingCart.items() as cartLine>
                <#if (cartLine.getProductId()?exists) && !cartLine.getIsPromo()>
                  <input type="hidden" name="selectedItem" value="${index}"/>
                </#if>
                <#assign index = index + 1/>
              </#list>
              <table border="0">
                <tr>
                  <td>
                    <div class="tabletext">
                    <select name='shoppingListId' class='selectBox'>
                      <#list shoppingLists as shoppingList>
                        <option value='${shoppingList.shoppingListId}'>${shoppingList.getString("listName")}</option>
                      </#list>
                    </select>
                    <input type="submit" class="smallSubmit" value="${uiLabelMap.EcommerceAddtoShoppingList}"/>
                    </div>
                  </td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>
    </div>
  </div>
</#if>

<script language="JavaScript" type="text/javascript">
  document.quickaddform.add_product_id.focus();
</script>

<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">&nbsp;${uiLabelMap.OrderOrderItems}</div>
    </div>
    <div class="screenlet-body">
  <#if (shoppingCartSize > 0)>
    <form method="post" action="<@ofbizUrl>/modifycart</@ofbizUrl>" name="cartform" style="margin: 0;">
      <input type="hidden" name="removeSelected" value="false"/>
      <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
        <input type="hidden" name="finalizeReqShipInfo" value="false"/>
        <input type="hidden" name="finalizeReqOptions" value="false"/>
        <input type="hidden" name="finalizeReqPayInfo" value="false"/>
        <input type="hidden" name="finalizeReqAdditionalParty" value="false"/>
      </#if>
      <table cellspacing="0" cellpadding="1" border="0">
        <tr>
          <td>&nbsp;</td>
          <td colspan="2">
            <div class="tabletext">
              <b>${uiLabelMap.ProductProduct}</b>
              <#if showOrderGiftWrap?default("true") == "true">
                  <select class="selectBox" name="GWALL" onchange="javascript:gwAll(this);">
                    <option value="">${uiLabelMap.OrderGiftWrapAllItems}</option>
                    <option value="NO^">${uiLabelMap.OrderNoGiftWrap}</option>
                    <#if allgiftWraps?has_content>
                      <#list allgiftWraps as option>
                        <option value="${option.productFeatureId?default("")}">${option.description?default("")} : <@ofbizCurrency amount=option.defaultAmount?default(0) isoCode=currencyUomId/></option>
                      </#list>
                    </#if>
                  </select>
              </#if>
            </div>
          </td>
          <td align="center"><div class="tabletext"><b>${uiLabelMap.OrderQuantity}</b></div></td>
          <td align="right"><div class="tabletext"><b>${uiLabelMap.CommonUnitPrice}</b></div></td>
          <td align="right"><div class="tabletext"><b>${uiLabelMap.OrderAdjustments}</b></div></td>
          <td align="right"><div class="tabletext"><b>${uiLabelMap.OrderItemTotal}</b></div></td>
        </tr>

        <#assign itemsFromList = false>
        <#list shoppingCart.items() as cartLine>
          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
          <#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures()>
          <tr><td colspan="8"><hr class="sepbar"></td></tr>
          <tr valign="top">
            <td>&nbsp;</td>         
            <td>
          <table border="0">
          <tr><td colspan="2">
                <div class="tabletext">                    
                  <#if cartLine.getProductId()?exists>
                    <#-- product item -->
                    <a href="<@ofbizUrl>/product?product_id=${cartLine.getProductId()}</@ofbizUrl>" class="buttontext">${cartLine.getProductId()}</a> -
                    <input size="60" class="inputBox" type="text" name="description_${cartLineIndex}" value="${cartLine.getName()?default("")}"/><br/>
                    <i>${cartLine.getDescription()?if_exists}</i>
                    <#if shoppingCart.getOrderType() != "PURCHASE_ORDER">
                      <#-- only applies to sales orders, not purchase orders
                      <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
                      <#assign itemProduct = cartLine.getProduct()>
                      <#assign isStoreInventoryNotRequiredAndNotAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequiredAndAvailable(request, itemProduct, cartLine.getQuantity(), false, false)>
                      <#if isStoreInventoryNotRequiredAndNotAvailable && itemProduct.inventoryMessage?has_content>
                          <b>(${itemProduct.inventoryMessage})</b>
                      </#if>                                          
                    </#if>   
                  <#else>
                    <#-- this is a non-product item -->
                    <b>${cartLine.getItemTypeDescription()?if_exists}</b> : ${cartLine.getName()?if_exists}
                  </#if>
                    <#-- display the item's features -->
                   <#assign features = "">
                   <#if cartLine.getFeaturesForSupplier(dispatcher,shoppingCart.getPartyId())?has_content>
                       <#assign features = cartLine.getFeaturesForSupplier(dispatcher, shoppingCart.getPartyId())>
                   <#elseif cartLine.getStandardFeatureList()?has_content>
                       <#assign features = cartLine.getStandardFeatureList()>
                   </#if>
                   <#if features?has_content>
                     <br/><i>${uiLabelMap.ProductFeatures}: <#list features as feature>${feature.description?default("")} </#list></i>
                   </#if>
                    <#-- show links to survey response for this item -->
                    <#if cartLine.getAttribute("surveyResponses")?has_content>
                        <br/>Surveys: 
                       <#list cartLine.getAttribute("surveyResponses") as surveyResponseId>
                        <a href="/content/control/ViewSurveyResponse?surveyResponseId=${surveyResponseId}&externalLoginKey=${externalLoginKey}" class="buttontext" style="font-size: xx-small;">${surveyResponseId}</a> 
                       </#list>
                    </#if>
                </div>
            </td></tr>
            <#if cartLine.getRequirementId()?has_content>
                <tr>
                    <td colspan="2" align="left">
                      <div class="tabletext"><b>${uiLabelMap.OrderRequirementId}</b>: ${cartLine.getRequirementId()?if_exists}</div>
                    </td>
                </tr>
            </#if>
            <#if cartLine.getQuoteId()?has_content>
                <#if cartLine.getQuoteItemSeqId()?has_content>
                  <tr>
                    <td colspan="2" align="left">
                      <div class="tabletext"><b>${uiLabelMap.OrderOrderQuoteId}</b>: ${cartLine.getQuoteId()?if_exists} - ${cartLine.getQuoteItemSeqId()?if_exists}</div>
                    </td>
                  </tr>
                </#if>
            </#if>
            <#if cartLine.getItemComment()?has_content>
              <tr><td align="left"><div class="tableheadtext">${uiLabelMap.CommonComment} : </div></td>
                  <td align="left"><div class="tabletext">${cartLine.getItemComment()?if_exists}</div>
              </td></tr>
            </#if>
            <#if cartLine.getDesiredDeliveryDate()?has_content>
              <tr><td align="left"><div class="tableheadtext">${uiLabelMap.OrderDesiredDeliveryDate}: </div></td>
                  <td align="left"><div class="tabletext">${cartLine.getDesiredDeliveryDate()?if_exists}</div>
              </td></tr>
            </#if>
          </table>

                <#if (cartLine.getIsPromo() && cartLine.getAlternativeOptionProductIds()?has_content)>
                  <#-- Show alternate gifts if there are any... -->
                  <div class="tableheadtext">${uiLabelMap.OrderChooseFollowingForGift}:</div>
                  <#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
                    <#assign alternativeOptionProduct = delegator.findByPrimaryKeyCache("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", alternativeOptionProductId))>
                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(alternativeOptionProduct, "PRODUCT_NAME", locale)?if_exists>
                    <div class="tabletext"><a href="<@ofbizUrl>/setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&alternateGwpLine=${cartLineIndex}</@ofbizUrl>" class="buttontext">Select: ${alternativeOptionName?default(alternativeOptionProductId)}</a></div>
                  </#list>
                </#if>
            </td>

            <#-- gift wrap option -->
            <#assign showNoGiftWrapOptions = false>
            <td nowrap align="right">
              <#assign giftWrapOption = lineOptionalFeatures.GIFT_WRAP?if_exists>
              <#assign selectedOption = cartLine.getAdditionalProductFeatureAndAppl("GIFT_WRAP")?if_exists>
              <#if giftWrapOption?has_content>
                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}" onchange="javascript:document.cartform.submit()">
                  <option value="NO^">${uiLabelMap.OrderNoGiftWrap}</option>
                  <#list giftWrapOption as option>
                    <option value="${option.productFeatureId}" <#if ((selectedOption.productFeatureId)?exists && selectedOption.productFeatureId == option.productFeatureId)>SELECTED</#if>>${option.description} : <@ofbizCurrency amount=option.amount?default(0) isoCode=currencyUomId/></option>
                  </#list>
                </select>
              <#elseif showNoGiftWrapOptions>
                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}" onchange="javascript:document.cartform.submit()">
                  <option value="">${uiLabelMap.OrderNoGiftWrap}</option>
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
                    <input size="6" class="inputBox" type="text" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}"/>
                </#if>
              </div>
            </td>
            <td nowrap align="right">
              <div class="tabletext">
                <#if cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
                  <@ofbizCurrency amount=cartLine.getBasePrice() isoCode=currencyUomId/>
                <#else>
                  <input size="6" class="inputBox" type="text" name="price_${cartLineIndex}" value="${cartLine.getBasePrice()?string("##0.00")}"/>
                </#if>
              </div>
            </td>
            <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=cartLine.getOtherAdjustments() isoCode=currencyUomId/></div></td>
            <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=cartLine.getItemSubTotal() isoCode=currencyUomId/></div></td>
          </tr>
        </#list>

        <#if shoppingCart.getAdjustments()?has_content>
            <tr><td colspan="7"><hr class="sepbar"/></td></tr>
              <tr>
                <td colspan="4" nowrap align="right"><div class="tabletext">${uiLabelMap.OrderSubTotal}:</div></td>
                <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=shoppingCart.getSubTotal() isoCode=currencyUomId/></div></td>
                <td>&nbsp;</td>
              </tr>
            <#list shoppingCart.getAdjustments() as cartAdjustment>
              <#assign adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType")>
              <tr>
                <td colspan="4" nowrap align="right">
                  <div class="tabletext">
                    <i>Adjustment</i> - ${adjustmentType.description?if_exists}
                    <#if cartAdjustment.productPromoId?has_content><a href="<@ofbizUrl>/showPromotionDetails?productPromoId=${cartAdjustment.productPromoId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonDetails}</a></#if>:
                  </div>
                </td>
                <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) isoCode=currencyUomId/></div></td>
                <td>&nbsp;</td>
              </tr>
            </#list>
        </#if>
        
        <tr> 
          <td colspan="6" align="right" valign="bottom">
            <div class="tabletext"><b>${uiLabelMap.OrderCartTotal}:</b></div>
          </td>
          <td align="right" valign="bottom">
            <hr class="sepbar"/>
            <div class="tabletext"><b><@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=currencyUomId/></b></div>
          </td>
        </tr>       
        <tr>
          <td colspan="8">&nbsp;</td>
        </tr>      
      </table>    
    </form>
  <#else>
    <div class="tabletext">${uiLabelMap.OrderNoOrderItemsToDisplay}</div>
  </#if>
    </div>
</div>

<!-- Internal cart info: productStoreId=${shoppingCart.getProductStoreId()?if_exists} locale=${shoppingCart.getLocale()?if_exists} currencyUom=${shoppingCart.getCurrency()?if_exists} userLoginId=${(shoppingCart.getUserLogin().getString("userLoginId"))?if_exists} autoUserLogin=${(shoppingCart.getAutoUserLogin().getString("userLoginId"))?if_exists} -->
