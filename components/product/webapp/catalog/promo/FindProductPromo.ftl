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
 *@author     Brad Steiner (bsteiner@thehungersite.com)
 *@version    $Revision: 1.1 $
 *@since      2.2
-->

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
