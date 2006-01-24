<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tbody><tr><td colspan="2" valign="top">
<div id="logo" style="z-index: 0;"><img src="/shop/html/images/logomontage.jpg" alt="Ghillies Choice" height="111" width="780"></div>
<div id="rightTop" style="z-index: 10;">
<!--cart viewer -->

 <div class="cartBox" style="z-index: 11;">
  <div class="dropNav" style="z-index: 12;">
	    <ul id="navDD"><li>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>

<#if (shoppingCartSize > 0)>
			<a href="<@ofbizUrl>showcart</@ofbizUrl>">
            View Cart,${shoppingCart.getTotalQuantity()}
            <#if shoppingCart.getTotalQuantity() == 1>${uiLabelMap.EcommerceItem}<#else/>${uiLabelMap.EcommerceItems}</#if>,
            <@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=shoppingCart.getCurrency()/>
            </a>
<#else>
            ${uiLabelMap.EcommerceShoppingCartEmpty}
</#if>
<#--
     <ul>
      <li><a href="http://www.ghillieschoice.com/">1 Fly</a></li>
      <li><a href="http://www.ghillieschoice.com/">Summary: $9</a></li>
     </ul>
-->    </li>
    </ul>
  </div>
 </div>
</div>
</td>
</tr>
<tr><td valign="top" width="574">
<!-- navigation row -->

<table border="0" cellpadding="0" cellspacing="0" width="574">
<tbody><tr>
<td><div id="navHolder" style="z-index: 1;"><img src="/shop/html/images/left_nav.jpg" alt="Ghillies Choice" height="18" width="219"><a href="/shop/main"><img src="/shop/html/images/about.gif" alt="About Ghillies Choice" border="0" height="18" width="70"></a><a href="#"><img src="/shop/html/images/terms.gif" alt="About Ghillies Choice" border="0" height="18" width="138"></a><a href="#"><img src="/shop/html/images/contact.gif" alt="About Ghillies Choice" border="0" height="18" width="84"></a><a href="#"><img src="/shop/html/images/links.gif" alt="About Ghillies Choice" border="0" height="18" width="63"></a></div>
</td>
</tr>
</tbody></table>

</td>
<td class="rightTile" valign="top" width="100%"><img src="/shop/html/images/nav_right.gif" alt="About Ghillies Choice" border="0" height="18" width="326"></td>
</tr>
</tbody></table>
<!--  end top -->
