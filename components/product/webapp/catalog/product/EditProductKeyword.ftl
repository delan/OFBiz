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
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Revision: 1.4 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">${uiLabelMap.ProductKeywords} <span class="head2">${uiLabelMap.CommonFor} <#if product?exists>${(product.productName)?if_exists}</#if>[${uiLabelMap.CommonId}:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewProduct}]</a>
    <#if productId?has_content >
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[${uiLabelMap.ProductProductPage}]</a>
    </#if>
    <br>
    
    <#if productId?exists && product?exists>
        <br>
        <div class="tabletext">${uiLabelMap.ProductNoteKeywordsAutomaticallyCreated}</div>
        
        <TABLE border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
        <TR>
            <TD width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
                <tr>
                <TD align=left>
                    <DIV class="boxhead">${uiLabelMap.ProductAddProductKeyword}:</DIV>
                </TD>
                <TD align=right>
                    <a href="<@ofbizUrl>/EditProduct?productId=${productId?if_exists}</@ofbizUrl>" class="submenutextright">${uiLabelMap.ProductEditProduct}</a>
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
                    <form method="POST" action="<@ofbizUrl>/UpdateProductKeyword</@ofbizUrl>" style="margin: 0;">
                        <input type="hidden" name="UPDATE_MODE" value="CREATE">
                        <input type="hidden" name="PRODUCT_ID" value="${productId?if_exists}">
                        <span class="tabletext">${uiLabelMap.ProductKeyword}: </span><input type="text" size="20" name="KEYWORD" value="" class="inputBox">
                        <span class="tabletext">${uiLabelMap.ProductWeight}: </span><input type="text" size="4" name="relevancyWeight" value="1" class="inputBox">
                        <input type="submit" value="${uiLabelMap.CommonAdd}" style="font-size: x-small;">
                    </form>
                </td>
                </tr>
            </table>
            </TD>
        </TR>
        </TABLE>
        <BR>
        
        <TABLE border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
        <TR>
            <TD width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
            <tr>
                <TD align=left>
                    <DIV class="boxhead">${uiLabelMap.ProductKeywords}</DIV>
                </TD>
                <TD align=right>
                    <a href="<@ofbizUrl>/UpdateProductKeywords?UPDATE_MODE=CREATE&PRODUCT_ID=${productId}</@ofbizUrl>" class="submenutext">${uiLabelMap.ProductReInduceKeywords}</a><a href="<@ofbizUrl>/UpdateProductKeywords?UPDATE_MODE=DELETE&PRODUCT_ID=${productId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.ProductDeleteAllKeywords}</a>
                </td>
            </tr>
            </table>
            </TD>
        </TR>
        <TR>
            <TD width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
            <tr>
                <td valign=top>
                <TABLE width="100%" cellpadding="0" cellspacing="0" border="0">
                <#assign productKeywords = product.getRelated("ProductKeyword")>
                <#if (productKeywords.size() > 0)>
                    <#list productKeywords as productKeyword>
                    <#assign colSize = productKeywords.size()/3 + 1>
                    <#assign kIdx = 0>
                    <tr>                        
                        <#import "java.lang.Number.Long" as Long>
                        <#assign relevancy = productKeyword.relevancyWeight in Long>
                        <td align=right><#if relevancy?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatQuantity(relevancy)}</#if>&nbsp;</td>
                        <td align=left>&nbsp;${(productKeyword.keyword)?if_exists}</td>
                        <td>&nbsp;&nbsp;</td>
                        <td align=left>
                            <a href="<@ofbizUrl>/UpdateProductKeyword?UPDATE_MODE=DELETE&PRODUCT_ID=${productId}&KEYWORD=${(productKeyword.keyword)?if_exists}</@ofbizUrl>" class="buttontext">
                            [${uiLabelMap.CommonDelete}]</a>
                        </td>
                    </tr>
                    <#assign kIdx = kIdx + 1>
                    <#if (kIdx >= colSize)>
                        <#assign colSize = colSize + colSize>
                        </TABLE>
                        </TD>
                        <TD bgcolor="#FFFFFF" valign=top style="border-left: solid #CCCCCC 1px;">
                        <TABLE width="100%" cellpadding="0" cellspacing="0" border="0">      
                    </#if>
                    </#list>
                <#else>
                    <tr>
                    <td colspan="3"><div class="tabletext">${uiLabelMap.ProductNoKeywordsFound}</div></td>
                    </tr>
                </#if>
                </TABLE>
                </td>
            </tr>
            </table>
        </TD>
    </TR>
    </TABLE>        
    <#else>
        <div class="head2">${uiLabelMap.ProductProductNotFoundWithProduct} "${productId?if_exists}"</div>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductViewPermissionError}</h3>
</#if>
