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
//-->
</script>
  
          ${pages.get("/party/viewcustomers.ftl")} <BR>

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
          <TD NOWRAP align="center"><div class="tabletext"><b>Quantity</b></div></TD>
          <#if security.hasEntityPermission("INVENTORY", "_AVAIL", session)>
            <TD NOWRAP align="right"><div class="tabletext"><b>In Stock</b></div></TD>
            <#if security.hasEntityPermission("INVENTORY", "_QUANTITY", session)>
              <TD NOWRAP align="right"><div class="tabletext"><b># Avail</b></div></TD>
            </#if>
          </#if>
          <TD NOWRAP align="right"><div class="tabletext"><b>Unit Price</b></div></TD>
          <TD NOWRAP align="right"><div class="tabletext"><b>Adjustments</b></div></TD>
          <TD NOWRAP align="right"><div class="tabletext"><b>Item Total</b></div></TD>         
        </TR>

        <#assign itemsFromList = false>
        <#list shoppingCart.items() as cartLine>
          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
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
            </td>
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
            <td nowrap align="right">
              <div class="tabletext">
                <#if cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("SALESREP_ORDER", "_SALES_PRICEMOD", session))>
                  ${cartLine.getBasePrice()}
                <#else>
                  <input size="6" class="inputBox" type="text" name="price_${cartLineIndex}" value="${cartLine.getBasePrice()?string("##0.00")}">
                </#if>
              </div>
            </td>
            <td nowrap align="right"><div class="tabletext">${cartLine.getOtherAdjustments()}</div></td>
            <td nowrap align="right"><div class="tabletext">${cartLine.getItemSubTotal()}</div></td>
          </TR>
        </#list>    
    </FORM>

        <#if shoppingCart.getAdjustments()?has_content>
            <tr><td colspan="7"><hr class="sepbar"></td></tr>
              <tr>
                <td colspan="4" nowrap align="right"><div class="tabletext">Sub&nbsp;Total:</div></td>
                <td nowrap align="right"><div class="tabletext">${shoppingCart.getSubTotal()}</div></td>
                <td>&nbsp;</td>
              </tr>
            <#list shoppingCart.getAdjustments() as cartAdjustment>
              <#assign adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType")>
              <tr>
                <td colspan="4" nowrap align="right"><div class="tabletext"><i>Adjustment</i> - ${adjustmentType.description?if_exists}:</div></td>
                <td nowrap align="right"><div class="tabletext">${Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal())}</div></td>
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
            <div class="tabletext"><b>${shoppingCart.getGrandTotal()}</b></div>
          </td>
        </tr> 
        <tr><td colspan="7"><hr class="sepbar"></td></tr>     
        <tr>
                  <td colspan="7" valign="middle" align="right">                     
            <#if (shoppingCartSize > 0)><a href="javascript:document.cartform.submit()"  class="buttontext">[Update]</a>&nbsp;<a href="<@ofbizUrl>/emptycart</@ofbizUrl>"  class="buttontext">[Clear Order]</a>&nbsp;<#if shoppingCart.getOrderType() == "PURCHASE_ORDER"><a href="<@ofbizUrl>/finalizeOrder?finalizeReqCustInfo=false&finalizeReqShipInfo=false&finalizeReqOptions=false&finalizeReqPayInfo=false</@ofbizUrl>"  class="buttontext">[Place Order]</a><#else><a href="<@ofbizUrl>/finalizeOrder</@ofbizUrl>" class="buttontext">[Place Order]</a></#if>
            <#else><span class="buttontext">[Update]</span><a href="<@ofbizUrl>/emptycart</@ofbizUrl> "class="buttontext">[Clear Order]</a><span class="submenutextrightdisabled">[Place Order]</span>
            </#if>
          </td>
        </tr>
        <tr>
          <td colspan="6">&nbsp;</td>
        </tr>
        
      </table>
  <#else>
    <div class="tabletext">No items in order.</div>
  </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<BR/>
<TABLE border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <TR>
    <TD width="100%" colspan="3">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Add an Item</div>
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