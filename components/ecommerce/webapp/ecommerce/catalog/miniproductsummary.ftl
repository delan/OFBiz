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
 *@version    $Revision: 1.1 $
 *@since      2.1
-->

<#if miniProduct?exists>
    <a href='<@ofbizUrl>/product/~product_id=${miniProduct.productId}</@ofbizUrl>' class='buttontext'>${miniProduct.productName}</a>
    <div class='tabletext'><b>${miniProduct.productId}, 
        <span class='<#if priceResult.isSale>salePrice<#else>normalPrice</#if>'>${priceResult.price?string.currency}</span></b></div>
        
    <#if (miniProduct.introductionDate?exists) && (nowTimeLong < miniProduct.introductionDate.getTime())>
        <#-- check to see if introductionDate hasn't passed yet -->
        <div class='tabletext' style='color: red;'>Not Yet Available</div>
    
    <#elseif (miniProduct.salesDiscontinuationDate?exists) && (nowTimeLong > miniProduct.salesDiscontinuationDate.getTime())>
        <#-- check to see if salesDiscontinuationDate has passed -->
        <div class='tabletext' style='color: red;'>No Longer Available</div>
    
    <#elseif miniProduct.isVirtual?default("N") == "Y">
        <a href='<@ofbizUrl>/product/<#if requestParameters.category_id?exists>~category_id=${requestParameters.category_id}/</#if>~product_id=${miniProduct.productId}</@ofbizUrl>' class="buttontext"><nobr>[Choose Variation...]</nobr></a>
    
    <#else>
        <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="${miniProdFormName}" style='margin: 0;'>
            <input type='hidden' name="add_product_id" value='${miniProduct.productId}'>
            <input type='hidden' name="quantity" value="${miniProdQuantity?default("1")}">
            <#if requestParameters.order_id?has_content><input type='hidden' name='order_id' value='${requestParameters.order_id}'></#if>
            <#if requestParameters.product_id?has_content><input type='hidden' name='product_id' value='${requestParameters.product_id}'></#if>
            <#if requestParameters.category_id?has_content><input type='hidden' name='category_id' value='${requestParameters.category_id}'></#if>
            <#if requestParameters.VIEW_INDEX?has_content><input type='hidden' name='VIEW_INDEX' value='${requestParameters.VIEW_INDEX}'></#if>
            <#if requestParameters.SEARCH_STRING?has_content><input type='hidden' name='SEARCH_STRING' value='${requestParameters.SEARCH_STRING}'></#if>
            <#if requestParameters.SEARCH_CATEGORY_ID?has_content><input type='hidden' name='SEARCH_CATEGORY_ID' value='${requestParameters.SEARCH_CATEGORY_ID}'></#if>
            <a href="javascript:document.${miniProdFormName}.submit()" class="buttontext"><nobr>[Add ${miniProdQuantity} to Cart]</nobr></a>
        </form>
    </#if>
</#if>

