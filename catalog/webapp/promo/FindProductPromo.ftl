<#if hasPermission>
    <div class="head1">Product Promotions List</div>
    <div><a href='<@ofbizUrl>/EditProductPromo</@ofbizUrl>' class="buttontext">[Create New ProductPromo]</a></div>
    <br>
    <table border="1" cellpadding='2' cellspacing='0'>
    <tr>
        <td><div class="tabletext"><b>Promo&nbsp;Name&nbsp;[ID]</b></div></td>
        <td><div class="tabletext"><b>Single&nbsp;Use?</b></div></td>
        <td><div class="tabletext"><b>Promo&nbsp;Text</b></div></td>
        <td><div class="tabletext">&nbsp;</div></td>
    </tr>
    <#list productPromos as productPromo>
    <tr valign="middle">
        <td><div class='tabletext'>&nbsp;<a href='<@ofbizUrl>/EditProductPromo?productPromoId=${(productPromo.productPromoId)?if_exists}</@ofbizUrl>' class="buttontext">${(productPromo.promoName)?if_exists} [${(productPromo.productPromoId)?if_exists}]</a></div></td>
        <td><div class='tabletext'>&nbsp;${(productPromo.singleUse)?if_exists}</div></td>
        <td><div class='tabletext'>&nbsp;${(productPromo.promoText)?if_exists}</div></td>
        <td>
        <a href='<@ofbizUrl>/EditProductPromo?productPromoId=${(productPromo.productPromoId)?if_exists}</@ofbizUrl>' class="buttontext">
        [Edit]</a>
        </td>
    </tr>
    </list>
    </table>
    <br>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
