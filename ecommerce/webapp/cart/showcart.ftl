
<#-- Get the Cart and Prepate Size -->
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?exists>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>

<#-- Get Cart Associated Products Data -->
<#assign associatedProducts = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getRandomCartProductAssoc(request)?if_exists>

<#-- Get Promo Text Data -->
<#assign productPromos = Static["org.ofbiz.commonapp.product.promo.ProductPromoWorker"].getCatalogProductPromos(delegator, request)>
<#-- Make sure that at least one promo has non-empty promoText -->
<#assign showPromoText = false>
<#list productPromos as productPromo>
    <#if productPromo.promoText?has_content><#assign showPromoText = true></#if>
</#list>

<#assign contentPathPrefix = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getContentPathPrefix(request)>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Quick Add</div>
          </td>
          <td valign="middle" align="right">
            <div class='lightbuttontextdisabled'>
              <a href="<@ofbizUrl>/main</@ofbizUrl>" class="lightbuttontext">[Continue&nbsp;Shopping]</a>
              <#if (shoppingCartSize > 0)>
                <a href="<@ofbizUrl>/checkoutoptions</@ofbizUrl>" class="lightbuttontext">[Checkout]</a>
              <#else>
                [Checkout]
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
            <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="quickaddform" style='margin: 0;'>
              <input type='text' class='inputBox' name="add_product_id" value="${requestParameters.add_product_id?if_exists}">
              <input type='text' class='inputBox' size="5" name="quantity" value="${requestParameters.quantity?default("1")}">
              <input type='submit' value="Add To Cart">
              <#-- <a href="javascript:document.quickaddform.submit()" class="buttontext"><nobr>[Add to Cart]</nobr></a> -->
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
            <div class="boxhead">&nbsp;Shopping Cart</div>
          </td>
          <td valign="middle" align="right">
            <div class='lightbuttontextdisabled'>
              <a href="<@ofbizUrl>/main</@ofbizUrl>" class="lightbuttontext">[Continue&nbsp;Shopping]</a>
              <#if (shoppingCartSize > 0)>
                <a href="javascript:document.cartform.submit()" class="lightbuttontext">[Recalculate&nbsp;Cart]</a>
                <a href="<@ofbizUrl>/emptycart</@ofbizUrl>" class="lightbuttontext">[Empty&nbsp;Cart]</a>
                <a href="<@ofbizUrl>/checkoutoptions</@ofbizUrl>" class="lightbuttontext">[Checkout]</a>
              <#else>
                [Recalculate&nbsp;Cart] [Empty&nbsp;Cart] [Checkout]
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
      <table width='100%' cellspacing="0" cellpadding="1" border="0">
        <TR> 
          <TD NOWRAP><div class='tabletext'><b>Product</b></div></TD>
          <TD NOWRAP align=center><div class='tabletext'><b>Quantity</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Unit Price</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Adjustments</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Item Total</b></div></TD>
          <TD NOWRAP align=center><div class='tabletext'><b>Remove</b></div></TD>
        </TR>

        <#list shoppingCart.items() as cartLine>
          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
          <tr><td colspan="7"><hr class='sepbar'></td></tr>
          <tr>
            <td>
                <div class='tabletext'>
                    <#-- <b>${cartLineIndex}</b> - -->
                    <a href='<@ofbizUrl>/product?product_id=${cartLine.getProductId()}</@ofbizUrl>' class='buttontext'>${cartLine.getProductId()} - 
                    ${cartLine.getName()?if_exists}</a> : ${cartLine.getDescription()?if_exists}

                    <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
                    <#assign itemProduct = cartLine.getProduct()>
                    <#assign isCatalogInventoryRequired = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].isCatalogInventoryRequired(request, itemProduct)>
                    <#assign isCatalogInventoryAvailable = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].isCatalogInventoryAvailable(request, cartLine.getProductId(), cartLine.getQuantity())>
                    <#if !isCatalogInventoryRequired && !isCatalogInventoryAvailable && itemProduct.inventoryMessage?has_content>
                        <b>(${itemProduct.inventoryMessage})</b>
                    </#if>
                </div>
            </td>
            <td nowrap align="center">
              <div class='tabletext'>
                <#if cartLine.getIsPromo()>
                    ${cartLine.getQuantity()?string.number}
                <#else>
                    <input size="6" class='inputBox' type="text" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}">
                </#if>
              </div>
            </td>
            <td nowrap align="right"><div class='tabletext'>${cartLine.getBasePrice()?string.currency}</div></TD>
            <td nowrap align="right"><div class='tabletext'>${cartLine.getOtherAdjustments()?string.currency}</div></TD>
            <td nowrap align="right"><div class='tabletext'>${cartLine.getItemSubTotal()?string.currency}</div></TD>
            <td nowrap align="center"><div class='tabletext'><#if !cartLine.getIsPromo()><input type="checkbox" name="delete_${cartLineIndex}" value="0"><#else>&nbsp;</#if></div></TD>
          </TR>
        </#list>

        <#if shoppingCart.getAdjustments()?has_content>
            <tr><td colspan="7"><hr class='sepbar'></td></tr>
              <tr>
                <td colspan="4" nowrap align="right"><div class='tabletext'>Sub&nbsp;Total:</div></td>
                <td nowrap align="right"><div class='tabletext'>${shoppingCart.getSubTotal()?string.currency}</div></td>
                <td>&nbsp;</td>
              </tr>
            <#list shoppingCart.getAdjustments() as cartAdjustment>
              <#assign adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType")>
              <tr>
                <td colspan="4" nowrap align="right"><div class='tabletext'><i>Adjustment</i> - ${adjustmentType.description?if_exists}:</div></td>
                <td nowrap align="right"><div class='tabletext'>${Static["org.ofbiz.commonapp.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal())?string.currency}</div></td>
                <td>&nbsp;</td>
              </tr>
            </#list>
        </#if>
<#--
        <TR>
          <TD COLSPAN="3" ALIGN="right"><div class='tabletext'>Sales tax:</div></TD>
          <TD ALIGN="right"><div class='tabletext'>$0.00</div></TD>
        </TR> 
-->

        <tr> 
          <td colspan="4" align="right" valign=bottom> 
             <#-- <hr size=1> -->
            <div class='tabletext'><b>Cart&nbsp;Total:</b></div>
          </td>
          <td align="right" valign=bottom>
            <hr size=1 class='sepbar'>
            <div class='tabletext'><b>${shoppingCart.getGrandTotal()?string.currency}</b></div>
          </td>
        </tr>
      </table>
    </FORM>
<#--
      <CENTER>
        <input type="checkbox" name="always_showcart" <#if shoppingCart.viewCartOnAdd()>checked</#if>>&nbsp;Always view cart after adding an item.
    	<br><br>
        <input type="submit" value="Update Cart">
      </CENTER>
-->
  <#else>
    <div class='head2'>Your shopping cart is empty.</div>
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
                <a href="<@ofbizUrl>/checkoutoptions</@ofbizUrl>" class="lightbuttontext">[Checkout]</a>
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
              <div class="boxhead">&nbsp;Special Offers</div>
            </td>
            <td valign="middle" align="right">&nbsp;</td>
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
              <div class="boxhead">&nbsp;You might also be interested in:</div>
            </td>
            <td valign="middle" align="right">&nbsp;</td>
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
