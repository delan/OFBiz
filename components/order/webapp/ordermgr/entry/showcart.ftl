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
 *@version    $Revision: 1.13 $
 *@since      2.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#assign security = requestAttributes.security>
<#assign externalKeyParam = requestAttributes.externalKeyParam>

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
        alert("Selected Gift Wrap is not avaiable for all items. The items which are available have been selected, the others remain unchanged.");
    }
    cform.submit();
}
//-->
</script>

<TABLE border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;<#if modeStr?exists>${modeStr?cap_first}&nbsp;</#if>Order Entry</div>
          </td>
          <td valign="middle" align="right">                     
            <#if (shoppingCartSize > 0)><a href="javascript:document.cartform.submit()" class="submenutext">Recalculate Order</a><a href="<@ofbizUrl>/emptycart</@ofbizUrl>" class="submenutext">Clear Order</a><#if shoppingCart.getOrderType() == "PURCHASE_ORDER"><a href="<@ofbizUrl>/finalizeOrder?finalizeReqCustInfo=false&finalizeReqShipInfo=false&finalizeReqOptions=false&finalizeReqPayInfo=false</@ofbizUrl>" class="submenutextright">Finalize Order</a><#else><a href="<@ofbizUrl>/finalizeOrder</@ofbizUrl>" class="submenutextright">Finalize Order</a></#if>
            <#else><span class="submenutextdisabled">Recalculate Order</span><a href="<@ofbizUrl>/emptycart</@ofbizUrl>" class="submenutext">Clear Order</a><span class="submenutextrightdisabled">Finalize Order</span>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td>           
            <form method="POST" action="<@ofbizUrl>/additem</@ofbizUrl>" name="quickaddform" style="margin: 0;">              
              <table width="100%" border="0" cellspacing="0" cellpadding="2">
                <tr>
                  <td valign="middle">
                    <span class="tabletext">Order for: </span>
                    <#if person?has_content>
                      <a href="/partymgr/control/viewprofile?party_id=${partyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${person.firstName?if_exists}&nbsp;${person.lastName?if_exists}&nbsp;[${person.partyId}]</a>
                    <#elseif partyGroup?has_content>
                      <a href="/partymgr/control/viewprofile?party_id=${partyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${partyGroup.groupName?if_exists}&nbsp;[${partyGroup.partyId}]</a>
                    <#else>
                      <span class="tabletext">[Party not defined]</span>
                    </#if>
                    - <span class="tabletext"><a href="<@ofbizUrl>/orderentry?updateParty=Y</@ofbizUrl>" class="buttontext">[Change]</a><#if partyId?default("_NA_") == "_NA_"> - <a href="/partymgr/control/findparty?externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">[Find Party]</a></#if></span>
                  </td>
                  <#if security.hasEntityPermission("CATALOG", "_CREATE", session)>
                  <td align="right" valign="middle">
                    <a href="/catalog/control/EditProduct?externalLoginKey=${requestAttributes.externalLoginKey}" target="catalog" class="buttontext">[Create New Product]</a>
                  </td>
                  </#if>
                </tr>              
              </table>
              <div class="tableheadtext">
                Product&nbsp;ID:&nbsp;<input type="text" class="inputBox" size="20" name="add_product_id" value="${requestParameters.add_product_id?if_exists}">
                Quantity:&nbsp;<input type="text" class="inputBox" size="6" name="quantity" value="${requestParameters.quantity?default("1")}">
                <input type="submit" class="smallSubmit" value="Add To Order">
              </div>
            </form>
          </td>
        </tr>
        <#if modeStr?default("sales")?lower_case == "purchase">
        <tr><td><hr class="sepbar"></td></tr>
        <tr>
          <td>
            <form method="POST" action="<@ofbizUrl>/additem</@ofbizUrl>" name="bulkworkaddform" style="margin: 0;">
                <div class="tableheadtext">
                    Item&nbsp;Type:&nbsp;<select name="add_item_type" class="selectBox"><option value="BULK_ORDER_ITEM">Bulk Item</option><option value="WORK_ORDER_ITEM">Work Item</option></select>
                    Category:&nbsp;<select name="add_category_id" class="selectBox">
                      <option></option>
                      <#list productCategoryList as productCategory>
                        <option value="${productCategory.productCategoryId}">${productCategory.description?default("No Description")} [${productCategory.productCategoryId}]</option>
                      </#list>
                    </select>
                </div>
                <div class="tableheadtext">
                    Description:&nbsp;<input type="text" class="inputBox" size="25" name="add_item_description" value="${requestParameters.add_product_id?if_exists}"/>
                    Quantity:&nbsp;<input type="text" class="inputBox" size="3" name="quantity" value="${requestParameters.quantity?default("1")}"/>
                    Price:&nbsp;<input type="text" class="inputBox" size="6" name="price" value="${requestParameters.price?if_exists}"/>
                    <input type="submit" class="smallSubmit" value="Add To Order"/>
                </div>
            </form>
          </td>
        </tr>  
        </#if>      
      </table>
    </TD>
  </TR>
</TABLE>

<script language="JavaScript">
<!--
  document.quickaddform.add_product_id.focus();
//-->
</script>
<BR>
<TABLE border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Order Items</div>
          </td>
          <td valign="middle" align="right">         
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td>          
  <#if (shoppingCartSize > 0)>
    <FORM METHOD="POST" ACTION="<@ofbizUrl>/modifycart</@ofbizUrl>" name="cartform" style="margin: 0;">
      <input type="hidden" name="removeSelected" value="false">
      <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
        <input type="hidden" name="finalizeReqShipInfo" value="false">
        <input type="hidden" name="finalizeReqOptions" value="false">
        <input type="hidden" name="finalizeReqPayInfo" value="false">
      </#if>
      <table width="100%" cellspacing="0" cellpadding="1" border="0">
        <TR> 
          <TD NOWRAP>&nbsp;</TD>
          <TD NOWRAP><div class="tabletext"><b>Product</b></div></TD>
          <#if showOrderGiftWrap?default("true") == "true">
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
          <TD NOWRAP align="center"><div class="tabletext"><b>Quantity</b></div></TD>
          <TD NOWRAP align="right"><div class="tabletext"><b>Unit Price</b></div></TD>
          <TD NOWRAP align="right"><div class="tabletext"><b>Adjustments</b></div></TD>
          <TD NOWRAP align="right"><div class="tabletext"><b>Item Total</b></div></TD>         
        </TR>

        <#assign itemsFromList = false>
        <#list shoppingCart.items() as cartLine>
          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
          <#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures()>
          <tr><td colspan="7"><hr class="sepbar"></td></tr>
          <tr>
            <td>&nbsp;</td>         
            <td>
                <div class="tabletext">                    
                  <#if cartLine.getProductId()?exists>
                    <#-- product item -->
                    <a href="<@ofbizUrl>/product?product_id=${cartLine.getProductId()}</@ofbizUrl>" class="buttontext">${cartLine.getProductId()} - 
                    ${cartLine.getName()?if_exists}</a> : ${cartLine.getDescription()?if_exists}
                    
                    <#if shoppingCart.getOrderType() != "PURCHASE_ORDER">
                      <#-- only applies to sales orders, not purchase orders
                      <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
                      <#assign itemProduct = cartLine.getProduct()>
                      <#assign isStoreInventoryRequired = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequired(request, itemProduct)>
                      <#assign isStoreInventoryAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryAvailable(request, cartLine.getProductId(), cartLine.getQuantity())>
                      <#if !isStoreInventoryRequired && !isStoreInventoryAvailable && itemProduct.inventoryMessage?has_content>
                          <b>(${itemProduct.inventoryMessage})</b>
                      </#if>                                          
                    </#if>   
                                     
                  <#else>
                    <#-- this is a non-product item -->
                    <b>${cartLine.getItemTypeDescription()?if_exists}</b> : ${cartLine.getName()?if_exists}
                  </#if>                    
                </div>
                
                <#if (cartLine.getIsPromo() && cartLine.getAlternativeOptionProductIds()?has_content)>
                  <#-- Show alternate gifts if there are any... -->
                  <div class="tableheadtext">You may also choose one of the following for your gift:</div>
                  <#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
                    <#assign alternativeOptionProduct = delegator.findByPrimaryKeyCache("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", alternativeOptionProductId))>
                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(alternativeOptionProduct, "PRODUCT_NAME", requestAttributes.locale)?if_exists>
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
            <td nowrap align="right">
              <div class="tabletext">
                <#if cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
                  ${cartLine.getBasePrice()?string.currency}
                <#else>
                  <input size="6" class="inputBox" type="text" name="price_${cartLineIndex}" value="${cartLine.getBasePrice()?string("##0.00")}">
                </#if>
              </div>
            </td>
            <td nowrap align="right"><div class="tabletext">${cartLine.getOtherAdjustments()?string.currency}</div></td>
            <td nowrap align="right"><div class="tabletext">${cartLine.getItemSubTotal()?string.currency}</div></td>
          </TR>
        </#list>

        <#if shoppingCart.getAdjustments()?has_content>
            <tr><td colspan="7"><hr class="sepbar"></td></tr>
              <tr>
                <td colspan="4" nowrap align="right"><div class="tabletext">Sub&nbsp;Total:</div></td>
                <td nowrap align="right"><div class="tabletext">${shoppingCart.getSubTotal()?string.currency}</div></td>
                <td>&nbsp;</td>
              </tr>
            <#list shoppingCart.getAdjustments() as cartAdjustment>
              <#assign adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType")>
              <tr>
                <td colspan="4" nowrap align="right">
                  <div class="tabletext">
                    <i>Adjustment</i> - ${adjustmentType.description?if_exists}
                    <#if cartAdjustment.productPromoId?has_content><a href="<@ofbizUrl>/showPromotionDetails?productPromoId=${cartAdjustment.productPromoId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonDetails}]</a></#if>:
                  </div>
                </td>
                <td nowrap align="right"><div class="tabletext">${Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal())?string.currency}</div></td>
                <td>&nbsp;</td>
              </tr>
            </#list>
        </#if>
        
        <tr> 
          <td colspan="5" align="right" valign=bottom>             
            <div class="tabletext"><b>Cart&nbsp;Total:</b></div>
          </td>
          <td align="right" valign=bottom>
            <hr size=1 class="sepbar">
            <div class="tabletext"><b>${shoppingCart.getGrandTotal()?string.currency}</b></div>
          </td>
        </tr>       
        <tr>
          <td colspan="6">&nbsp;</td>
        </tr>      
      </table>    
    </FORM>
  <#else>
    <div class="tabletext">No order items to display.</div>
  </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<#if shoppingCart.getOrderType() == "SALES_ORDER">
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
</#if>

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
  <BR>
  <TABLE border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <TR>
      <TD width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;You might also be interested in:</div>
            </td>
            <#--<td valign="middle" align="right">&nbsp;</td>-->
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD width="100%">
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
            ${pages.get("/entry/catalog/productsummary.ftl")}
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
      </TD>
    </TR>
  </TABLE>
</#if>

<#if (shoppingCartSize?default(0) > 0)>
  <br/>
  <#include "/entry/promoUseDetailsInline.ftl"/>
</#if>

