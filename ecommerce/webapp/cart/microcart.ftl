
<#assign shoppingCartSize = requestAttributes.shoppingCartSize>

<div class='insideHeaderText'>
<#if (shoppingCartSize > 0)>
  Cart has <b>${shoppingCartSize}</b> items, <b>${requestAttributes.shoppingCartGrandTotal?string.currency}</b>
<#else>
  Shopping Cart is <b>Empty</b>
</#if>
  &nbsp;&nbsp;
</div>
<div class='insideHeaderDisabled'>
  <a href="<transform ofbizUrl>/view/showcart</transform>" class="insideHeaderLink">[View&nbsp;Cart]</a>
  <#if (shoppingCartSize > 0)>
    <a href="<transform ofbizUrl>/checkoutoptions</transform>" class="insideHeaderLink">[Checkout]</a>
  <#else>
    [Checkout]
  </#if>
  &nbsp;&nbsp;
</div>
