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
 *@version    $Revision: 1.6 $
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
//-->
</script>
            
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.CommonQuickAdd}</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/main</@ofbizUrl>" class="submenutext">${uiLabelMap.EcommerceContinueShopping}</a><#if (shoppingCartSize > 0)><a href="<@ofbizUrl>/quickcheckout</@ofbizUrl>" class="submenutextright">${uiLabelMap.EcommerceCheckout}</a><#else><span class="submenutextrightdisabled">${uiLabelMap.EcommerceCheckout}</span></#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="quickaddform" style='margin: 0;'>
              <input type='text' class='inputBox' name="add_product_id" value="${requestParameters.add_product_id?if_exists}">
              <input type='text' class='inputBox' size="5" name="quantity" value="${requestParameters.quantity?default("1")}">
              <input type='submit' class='smallSubmit' value="${uiLabelMap.EcommerceAddtoCart}">
              <#-- <a href="javascript:document.quickaddform.submit()" class="buttontext"><nobr>[${uiLabelMap.EcommerceAddtoCart}]</nobr></a> -->
            </form>
          </td>
        </tr>
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
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.EcommerceShoppingCart}</div>
          </td>
          <td valign="middle" align="right">
            <div class='lightbuttontextdisabled'>
              <#--<a href="<@ofbizUrl>/main</@ofbizUrl>" class="lightbuttontext">[${uiLabelMap.EcommerceContinueShopping}]</a>-->
              <#if (shoppingCartSize > 0)>
                <a href="javascript:document.cartform.submit()" class="submenutext">${uiLabelMap.EcommerceRecalculateCart}</a><a href="<@ofbizUrl>/emptycart</@ofbizUrl>" class="submenutext">${uiLabelMap.EcommerceEmptyCart}</a><a href="javascript:removeSelected();" class="submenutextright">${uiLabelMap.EcommerceRemoveSelected}</a>
              <#else>
                <span class="submenutextdisabled">${uiLabelMap.EcommerceRecalculateCart}</span><span class="submenutextdisabled">${uiLabelMap.EcommerceEmptyCart}</span><span class="submenutextrightdisabled">${uiLabelMap.EcommerceRemoveSelected}</span>
              </#if>
            </div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <#if (shoppingCartSize > 0)>
    <FORM METHOD="POST" ACTION="<@ofbizUrl>/modifycart</@ofbizUrl>" name='cartform' style='margin: 0;'>
      <input type="hidden" name="removeSelected" value="false">
      <table width='100%' cellspacing="0" cellpadding="1" border="0">
        <TR> 
          <TD NOWRAP>&nbsp;</TD>
          <TD NOWRAP><div class='tabletext'><b>${uiLabelMap.EcommerceProduct}</b></div></TD>
          <TD NOWRAP align='center'><div class='tabletext'><b>${uiLabelMap.CommonQuantity}</b></div></TD>
          <TD NOWRAP align='right'><div class='tabletext'><b>${uiLabelMap.EcommerceUnitPrice}</b></div></TD>
          <TD NOWRAP align='right'><div class='tabletext'><b>${uiLabelMap.EcommerceAdjustments}</b></div></TD>
          <TD NOWRAP align='right'><div class='tabletext'><b>${uiLabelMap.EcommerceItemTotal}</b></div></TD>
          <TD NOWRAP align='center'><input type='checkbox' name='selectAll' value='0' onclick="javascript:toggleAll(this);"></TD>
        </TR>

        <#assign itemsFromList = false>
        <#assign promoItems = false>
        <#list shoppingCart.items() as cartLine>
          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
          <tr><td colspan="7"><hr class='sepbar'></td></tr>
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
                <div class='tabletext'>
                  <#if cartLine.getProductId()?exists>
                    <#-- product item -->
                    <#-- <b>${cartLineIndex}</b> - -->
                    <a href='<@ofbizUrl>/product?product_id=${cartLine.getProductId()}</@ofbizUrl>' class='buttontext'>${cartLine.getProductId()} - 
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
            </td>
            <td nowrap align="center">
              <div class='tabletext'>
                <#if cartLine.getIsPromo() || cartLine.getShoppingListId()?exists>
                    ${cartLine.getQuantity()?string.number}
                <#else>
                    <input size="6" class='inputBox' type="text" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}">
                </#if>
              </div>
            </td>
            <td nowrap align="right"><div class='tabletext'>${cartLine.getBasePrice()?string.currency}</div></TD>
            <td nowrap align="right"><div class='tabletext'>${cartLine.getOtherAdjustments()?string.currency}</div></TD>
            <td nowrap align="right"><div class='tabletext'>${cartLine.getItemSubTotal()?string.currency}</div></TD>
            <td nowrap align="center"><div class='tabletext'><#if !cartLine.getIsPromo()><input type="checkbox" name="selectedItem" value="${cartLineIndex}" onclick="javascript:checkToggle(this);"><#else>&nbsp;</#if></div></TD>
          </TR>
        </#list>

        <#if shoppingCart.getAdjustments()?has_content>
            <tr><td colspan="7"><hr class='sepbar'></td></tr>
              <tr>
                <td colspan="5" nowrap align="right"><div class='tabletext'>${uiLabelMap.CommonSubTotal}:</div></td>
                <td nowrap align="right"><div class='tabletext'>${shoppingCart.getSubTotal()?string.currency}</div></td>
                <td>&nbsp;</td>
              </tr>
            <#list shoppingCart.getAdjustments() as cartAdjustment>
              <#assign adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType")>
              <tr>
                <td colspan="5" nowrap align="right"><div class='tabletext'><i>${uiLabelMap.EcommerceAdjustment}</i> - ${adjustmentType.description?if_exists}:</div></td>
                <td nowrap align="right"><div class='tabletext'>${Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal())?string.currency}</div></td>
                <td>&nbsp;</td>
              </tr>
            </#list>
        </#if>
        
        <tr> 
          <td colspan="5" align="right" valign=bottom>             
            <div class='tabletext'><b>${uiLabelMap.EcommerceCartTotal}:</b></div>
          </td>
          <td align="right" valign=bottom>
            <hr size=1 class='sepbar'>
            <div class='tabletext'><b>${shoppingCart.getGrandTotal()?string.currency}</b></div>
          </td>
        </tr>
        <#if itemsFromList>
        <tr>
          <td valign="bottom" colspan="6"><div class="tabletext">L - ${uiLabelMap.EcommerceItemsfromShopingList}.</td>
        </tr>
        </#if>
        <#if promoItems>
        <tr>
          <td valign="bottom" colspan="6"><div class="tabletext">P - ${uiLabelMap.EcommercePromotionalItems}.</td>
        </tr>
        </#if>        
        <#if !itemsFromList && !promoItems>
        <tr>
          <td colspan="6">&nbsp;</td>
        </tr>
        </#if>
        <tr>
          <td colspan="6"><hr class="sepbar"></td>
        </tr>
        <tr>          
          <td colspan="6" align="right" valign="bottom">
            <div class="tabletext">
              <#if sessionAttributes.userLogin?has_content>
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
               ${uiLabelMap.EcommerceYouMust}<a href="<@ofbizUrl>/checkLogin/showcart</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonLogin}</a>
                ${uiLabelMap.EcommerceToAddSelectedItemsToShoppingList}.&nbsp;
              </#if>           
            </div>
          </td>
        </tr>       
        <tr>
          <td colspan="6"><hr class="sepbar"></td>
        </tr>        
        <tr>
          <td colspan="6" align="center" valign="bottom">
            <div class="tabletext"><input type="checkbox" onChange="javascript:document.cartform.submit()" name="alwaysShowcart" <#if shoppingCart.viewCartOnAdd()>checked</#if>>&nbsp;Always view cart after adding an item.</div>
          </td>
        </tr>        
      </table>    
    </FORM>
  <#else>
    <div class='head2'>${uiLabelMap.EcommerceYourShoppingCartEmpty}.</div>
  </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
<#-- Un-comment this to include a link bar at the bottom too 
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td>
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td valign="middle" align="left">
            &nbsp;
          </td>
          <td valign="middle" align="right">
            <div class='lightbuttontextdisabled'>
              <a href="<@ofbizUrl>/main</@ofbizUrl>" class="lightbuttontext">[Continue&nbsp;Shopping]</a>
              <#if (shoppingCartSize > 0)>
                <a href="javascript:document.cartform.submit()" class="lightbuttontext">[Recalculate&nbsp;Cart]</a>
                <a href="<@ofbizUrl>/emptycart</@ofbizUrl>" class="lightbuttontext">[Empty&nbsp;Cart]</a>
                <a href="<@ofbizUrl>/quickcheckout</@ofbizUrl>" class="lightbuttontext">[Checkout]</a>
              <#else>
                [Recalculate&nbsp;Cart] [Empty&nbsp;Cart] [Checkout]
              </#if>
            </div>
          </td>
        </tr>
      </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
-->
</TABLE>

<#if showPromoText>
  <BR>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.EcommerceSpecialOffers}</div>
            </td>
            <#--<td valign="middle" align="right">&nbsp;</td>-->
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td>
                <table width='100%' cellspacing="0" cellpadding="1" border="0">
                  <#-- show promotions text -->
                  <#list productPromos as productPromo>
                    <#if productPromo.promoText?has_content>
                        <tr>
                          <td>
                            <div class='tabletext'>${productPromo.promoText}</div>
                          </td>
                        </tr>
                        <#if productPromo_has_next>
                          <tr><td><hr class='sepbar'></td></tr>
                        </#if>
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

<#if associatedProducts?has_content>
  <BR>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.EcommerceYouMightAlsoIntrested}:</div>
            </td>
            <#--<td valign="middle" align="right">&nbsp;</td>-->
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td>
    <table width='100%' cellspacing="0" cellpadding="1" border="0">
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
          <tr><td><hr class='sepbar'></td></tr>
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
