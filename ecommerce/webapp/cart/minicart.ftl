
<#assign shoppingCartSize = requestAttributes.shoppingCartSize>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
      <div class='boxhead'><b>Cart&nbsp;Summary</b></div>
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
<table width="100%" border="0" cellpadding="2" cellspacing="0">
<#if (shoppingCartSize > 0)>
    <tr>
      <td colspan="3">
        <a href="<transform ofbizUrl>/view/showcart</transform>" class="buttontext">[View&nbsp;Cart]&nbsp;</a><a href="<transform ofbizUrl>/checkoutoptions</transform>" class="buttontext">[Checkout]</a>
      </td>
    </tr>
    <tr>
      <td valign="bottom"><div class="tabletext"><b>#<b></div></td>
      <td valign="bottom"><div class="tabletext"><b>Item<b></div></td>
      <td valign="bottom"><div class="tabletext"><b>Subtotal<b></div></td>
    </tr>
    <#list requestAttributes.shoppingCartLines as cartLine>
      <tr>
        <td valign="top"><div class="tabletext" nowrap>${cartLine.getQuantity()?string.number}</div>
        </td>
        <td valign="top">
          <div><a href="<transform ofbizUrl>/product?product_id=${cartLine.getProductId()}</transform>" class="buttontext">
          ${cartLine.getName()}</a></div>
        </td>
        <td align="right" valign="top"><div class="tabletext" nowrap>${cartLine.getItemSubTotal()?string.currency}</div>
        </td>
      </tr>
    </#list>
    <tr>
      <td colspan="3" align="right">
        <div class="tabletext"><b>Total: ${requestAttributes.shoppingCartGrandTotal?string.currency}</b></div>
      </td>
    </tr>
    <tr>
      <td colspan="3">
        <a href="<transform ofbizUrl>/view/showcart</transform>" class="buttontext">[View&nbsp;Cart]&nbsp;</a><a href="<transform ofbizUrl>/checkoutoptions</transform>" class="buttontext">[Checkout]</a>
      </td>
    </tr>
<#else>
    <tr>
      <td nowrap colspan="3"><div class="tabletext">Shopping Cart is empty.</div></td>
    </tr>
</#if>
</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

