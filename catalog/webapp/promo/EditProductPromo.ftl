<#if hasPermission>
    <#if productPromoId?exists>
        <div class='tabContainer'>
        <a href="<@ofbizUrl>/EditProductPromo?productPromoId=${productPromoId}</@ofbizUrl>" class="tabButtonSelected">Promo</a>
        <a href="<@ofbizUrl>/EditProductPromoRules?productPromoId=${productPromoId}</@ofbizUrl>" class="tabButton">Rules</a>
        <a href="<@ofbizUrl>/EditProductPromoCatalogs?productPromoId=${productPromoId}</@ofbizUrl>" class="tabButton">Catalogs</a>
        </div>
    </#if>   
    <div class="head1">Promotion&nbsp;<span class='head2'><#if productPromo?exists>${(productPromo.promoName)?if_exists}</#if>[${(productPromo.productPromoId)?if_exists}]</span></div>
    <a href="<@ofbizUrl>/EditProductPromo</@ofbizUrl>" class="buttontext">[New ProductPromo]</a>
    <#if !(productPromo?exists)>
        <#if productPromoId?exists>
            <form action="<@ofbizUrl>/CreateProductPromo</@ofbizUrl>" method=POST style='margin: 0;'>
            <table border='0' cellpadding='2' cellspacing='0'>
            <tr>
            <td align=right><div class="tabletext">Product Promo ID</div></td>
            <td>&nbsp;</td>
            <td>
                <h3>Could not find productPromo with ID "${productPromoId?if_exists}".</h3><br>
                <input type=text size='20' maxlength='20' class='inputBox' name="productPromoId" value="${productPromoId?if_exists}">
            </td>
            </tr>
        <#else>	
            <form action="<@ofbizUrl>/CreateProductPromo</@ofbizUrl>" method=POST style='margin: 0;'>
            <table border='0' cellpadding='2' cellspacing='0'>
            <tr>
            <td align=right><div class="tabletext">Product Promo ID</div></td>
            <td>&nbsp;</td>
            <td>
                <input type=text size='20' maxlength='20' class='inputBox' name="productPromoId" value="">
            </td>
            </tr>
        </#if>	
    <#else>
        <form action="<@ofbizUrl>/UpdateProductPromo</@ofbizUrl>" method=POST style='margin: 0;'>
        <table border='0' cellpadding='2' cellspacing='0'>
        <input type=hidden name="productPromoId" value="${productPromoId?if_exists}">
        <tr>
            <td align=right><div class="tabletext">Product Promo ID</div></td>
            <td>&nbsp;</td>
            <td>
            <b>${productPromoId}</b> (This cannot be changed without re-creating the productPromo.)
            </td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Promo Code</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" name="promoCode" class='inputBox' value="${(productPromo.promoCode)?if_exists}" size="30" maxlength="60"></td>
        </tr>
        </#if>
        <tr>
            <td width="26%" align=right><div class="tabletext">Promo Name</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" name="promoName" class='inputBox' value="${(productPromo.promoName)?if_exists}" size="30" maxlength="60"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Promo Text</div></td>
            <td>&nbsp;</td>
            <td width="74%"><textarea name='promoText' class='textAreaBox' cols='70' rows='5' value="<#if productPromo?exists>${(productPromo.promoText)?if_exists}</#if>"></textarea></td>
        </tr>
    
        <tr>
            <td width="26%" align=right><div class="tabletext">Single Use?</div></td>
            <td>&nbsp;</td>
            <td width="74%">
            <SELECT name='singleUse' class='selectBox'>
                <OPTION><#if productPromo?exists>${(productPromo.singleUse)?default("N")}<#else>N</#if></OPTION>
                <OPTION>&nbsp;</OPTION><OPTION>Y</OPTION><OPTION>N</OPTION>
            </SELECT>
            </td>
        </tr>
    <tr>
        <td colspan='2'>&nbsp;</td>
        <td colspan='1' align=left><input type="submit" name="Update" value="Update"></td>
    </tr>
    </table>
    </form>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>