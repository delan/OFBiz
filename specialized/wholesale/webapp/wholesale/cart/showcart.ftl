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
 *@version    $Revision$
 *@since      2.1
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
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
//-->
</script>
<table border="0" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width='100%' border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.EcommerceShoppingCart}</div>
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
  <#if (shoppingCartSize > 0)>
    <form method="POST" action="<@ofbizUrl>/modifycart</@ofbizUrl>" name="cartform" style="margin: 0;">
      <input type="hidden" name="removeSelected" value="false">
      <input type="hidden" name="alwaysShowcart" value="checked">
      <input type="hidden" name="selectedItem" value="">
      <input type="hidden" name="selectAll" value="">
      <table width="100%" cellspacing="0" cellpadding="1" border="0">
        <tr>
          <td NOWRAP>&nbsp;</td>
          <td NOWRAP><div class="tabletext"><b>${uiLabelMap.EcommerceProduct}</b></div></td>
          <td NOWRAP>&nbsp;</td>
          <td NOWRAP align="center"><div class="tabletext"><b>${uiLabelMap.CommonQuantity}</b></div></td>
          <#if security.hasEntityPermission("INVENTORY", "_AVAIL", session)>
            <TD NOWRAP align="right"><div class="tabletext"><b>In Stock</b></div></TD>
            <#if security.hasEntityPermission("INVENTORY", "_QUANTITY", session)>
              <TD NOWRAP align="right"><div class="tabletext"><b># AVAIL</b></div></TD>
            </#if>
          </#if>
          <td NOWRAP align="right"><div class="tabletext"><b>${uiLabelMap.EcommerceUnitPrice}</b></div></td>
          <td NOWRAP align="right"><div class="tabletext"><b>${uiLabelMap.EcommerceAdjustments}</b></div></td>
          <td NOWRAP align="right"><div class="tabletext"><b>${uiLabelMap.EcommerceItemTotal}</b></div></td>
          <td NOWRAP align="center"></td>
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
        
          <#if security.hasEntityPermission("INVENTORY", "_AVAIL", session)> 
            <tr><td>&nbsp;</td>
            <#if security.hasEntityPermission("INVENTORY", "_QUANTITY", session)>
              <td colspan="8"><hr class="sepbar"></td>
            <#else>
              <td colspan="7"><hr class="sepbar"></td>
            </#if>
            </tr>
          <#else><tr><td>&nbsp;</td><td colspan="6"><hr class="sepbar"></td></tr>
          </#if>
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
                    <#-- <b>${cartLineIndex}</b> - -->
                    <table width="100%">
                      <tr>
                        <td valign=middle width="35%"><a href="<@ofbizUrl>/product?product_id=${cartLine.getProductId()}</@ofbizUrl>">
                          <img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${cartLine.getProduct().smallImageUrl?default("/images/defaultImage.jpg")}</@ofbizContentUrl>" valign="bottom" height="50" class="imageborder" border="0"></a>
                        </td>
                        <td valign=middle align=left>
                          	<a href="<@ofbizUrl>/product?product_id=${cartLine.getProductId()}</@ofbizUrl>" class="buttontext">${cartLine.getProductId()} -
                      			${cartLine.getName()?if_exists} <#--<#if cartLine.getProductFeatures()?has_content> - ${cartLine.getProductFeatures()} </#if>  --></a>
                   		</td>
                      </tr>
                    </table>
                    <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
                    <#assign itemProduct = cartLine.getProduct()>
                    <#assign isStoreInventoryRequired = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequired(request, itemProduct)>
<#--                    <#assign isStoreInventoryAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryAvailable(request, cartLine.getProductId(), cartLine.getQuantity())>-->
<#--                    <#if !isStoreInventoryRequired && !isStoreInventoryAvailable && itemProduct.inventoryMessage?has_content> 
                        <b>(${itemProduct.inventoryMessage})</b>
-->                   </#if>
<#--
                  <#else>
-->                    <#-- this is a non-product item -->
<#--                    <b>${cartLine.getItemTypeDescription()?if_exists}</b> : ${cartLine.getName()?if_exists}
                  </#if> 
-->                </div>
            </td>

            <#-- gift wrap option -->
            <#assign showNoGiftWrapOptions = false>
            <td nowrap align="right">
              <#assign giftWrapOption = lineOptionalFeatures.GIFT_WRAP?if_exists>
              <#assign selectedOption = cartLine.getAdditionalProductFeatureAndAppl("GIFT_WRAP")?if_exists>
              <#if giftWrapOption?has_content>
                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}">
                  <option value="">No Gift Wrap</option>
                  <#list giftWrapOption as option>
                    <option value="${option.productFeatureId}" <#if ((selectedOption.productFeatureId)?exists && selectedOption.productFeatureId == option.productFeatureId)>SELECTED</#if>>${option.description} : ${option.amount?string.currency}</option>
                  </#list>
                </select>
              <#elseif showNoGiftWrapOptions>
                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}">
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
            
          <#if security.hasEntityPermission("INVENTORY", "_AVAIL", session)> 
            <td nowrap align="right">
              <div class="tabletext">
                <#if isStoreInventoryAvailable>
                    <IMG SRC="/images/checkmark.gif" ALT="In Stock">
                <#else>
                    <IMG SRC="/images/crossmark.gif" ALT="Not In Stock">
                </#if>
              </div>
            </td>
            <#if security.hasEntityPermission("INVENTORY", "_QUANTITY", session)>
            <td nowrap align="right">
              <div class="tabletext">
                ${availableToPromiseMap.get(cartLine.getProductId())?string.number}
              </div>
            </td>
            </#if>
          </#if>      
            
            <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=cartLine.getBasePrice() isoCode=shoppingCart.getCurrency()/></div></td>
            <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=cartLine.getOtherAdjustments() isoCode=shoppingCart.getCurrency()/></div></td>
            <td nowrap align="right"><div class="tabletext"><@ofbizCurrency amount=cartLine.getItemSubTotal() isoCode=shoppingCart.getCurrency()/></div></td>
            <td nowrap align="center"><div class="tabletext"><#if !cartLine.getIsPromo()><a href="javascript:removeItem(${cartLineIndex});" class="buttontext">[Remove]</a><#else>&nbsp;</#if></div></td>	
          </tr>
        </#list>
        <#if shoppingCart.getAdjustments()?has_content>
        
          <#if security.hasEntityPermission("INVENTORY", "_AVAIL", session)> 
            <tr><td>&nbsp;</td>
            <#if security.hasEntityPermission("INVENTORY", "_QUANTITY", session)>
              <td colspan="8"><hr class="sepbar"></td>
            <#else>
              <td colspan="7"><hr class="sepbar"></td>
            </#if>
            </tr>
          <#else><tr><td>&nbsp;</td><td colspan="6"><hr class="sepbar"></td></tr>
          </#if>      
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
          <td colspan="6" align="right" valign=bottom>
            <div class="tabletext"><b>${uiLabelMap.EcommerceCartTotal}:</b></div>
          </td>
          <td align="right" valign=bottom>
            <hr size=1 class="sepbar">
            <div class="tabletext"><b><@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=shoppingCart.getCurrency()/></b></div>
          </td>
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
          <#if security.hasEntityPermission("INVENTORY", "_AVAIL", session)> 
            <tr><td>&nbsp;</td>
            <#if security.hasEntityPermission("INVENTORY", "_QUANTITY", session)>
              <td colspan="8"><hr class="sepbar"></td>
            <#else>
              <td colspan="7"><hr class="sepbar"></td>
            </#if>
            </tr>
          <#else><tr><td>&nbsp;</td><td colspan="6"><hr class="sepbar"></td></tr>
          </#if> 
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
                <option value="">${uiLabelMap.WholesaleNewShoppingList}</option>
              </select>
              &nbsp;&nbsp;
              <a href="javascript:addToList();" class="buttontext">[${uiLabelMap.EcommerceAddSelectedtoSavedItems}]</a>&nbsp;&nbsp;
              </#if>
            </div>
          </td>
        </tr>
    </form>

        <tr>
          <td colspan="4" align="right" valign="bottom">&nbsp;</td>
          <td colspan="3" align="right" valign="bottom">
          <table width="100%">
            <tr>
          		<td align="right" valign="bottom">
            		<div class="tabletext">
              			<a href="<@ofbizUrl>/main</@ofbizUrl>" class="buttontext">[continue browsing]</a>
            		</div>
         		</td>
          		<td align="right" valign="bottom">
            		<div class="tabletext">
             			<a href="javascript:document.cartform.submit()" class="buttontext">[update order]</a>
            		</div>
         		</td>
          		<td align="right" valign="bottom">
            		<div class="tabletext">
              			<a href="<@ofbizUrl>/quickcheckout</@ofbizUrl>" class="buttontext">[place order]</a>
            		</div>
          		</td>
          	</tr>
          </table>
          </td>
        </tr>
      </table>
  <#else>
    <div class="tabletext">${uiLabelMap.EcommerceYourShoppingCartEmpty}.</div><br>
    <a href="<@ofbizUrl>/main</@ofbizUrl>" class="buttontext">[continue browsing]</a>
  </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
<#-- Copy link bar to bottom to include a link bar at the bottom too -->
</table>
<#-- -->
<br/>
<TABLE border="0" cellspacing="0" cellpadding="0" class="boxoutside">
  <TR>
    <TD width="100%" colspan="3">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PageTitleQuickAdd}</div>
          </td>
          <td valign="middle" align="right">         
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
          <TD NOWRAP width="50%"><div class="tabletext"><b>Product Id</b></div></TD>
          <TD NOWRAP width="50%" colspan=2><div class="tabletext"><b>Quantity</b></div></TD>
   </TR>
   <tr><td colspan="3"><hr class="sepbar"></td></tr>

   <tr>
     <form method="POST" action="<@ofbizUrl>/additem</@ofbizUrl>" name="quickaddform" style="margin: 0;">         
          <td width="50%">
              <div class="tableheadtext">
                <input type="text" class="inputBox" size="15" name="add_product_id" value="${requestParameters.add_product_id?if_exists}">
              </div>
          </td>
          <td width="25%">
              <div class="tableheadtext">
                <input type="text" class="inputBox" size="3" name="quantity" value="${requestParameters.quantity?default("1")}">
           </td>
          <td width="25%">
			[<a href="javascript:document.quickaddform.submit()"  class="buttontext">Add to Order</a>]
          </td>
         </form>
        </tr> 
</TABLE>

  <table border="0" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
      <td width="100%">
        <table width='100%'border="0" cellspacing="0" cellpadding="0" class="boxtop">
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
        <table width='100%' border="0" cellspacing="0" cellpadding="0" class="boxbottom">
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

 
<#-- shows details of promotions applied to order
<#if (shoppingCartSize?default(0) > 0)>
  <br/>
  <#include "/cart/promoUseDetailsInline.ftl"/>
</#if>
--->