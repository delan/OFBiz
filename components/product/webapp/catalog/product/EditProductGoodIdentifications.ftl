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
 *@version    $Rev:$
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">${uiLabelMap.CommonIds} <span class="head2">${uiLabelMap.CommonFor} <#if product?exists>${(product.internalName)?if_exists}</#if> [${uiLabelMap.CommonId}:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewProduct}]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[${uiLabelMap.ProductProductPage}]</a>
    </#if>
    <br>
    <br>
    
    <#if productId?exists && product?exists>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>${uiLabelMap.ProductIdType}</b></div></td>
            <td align="center"><div class="tabletext"><b>${uiLabelMap.ProductIdValue}</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list goodIdentifications as goodIdentification>
        <#assign line = line + 1>
        <#assign goodIdentificationType = goodIdentification.getRelatedOneCache("GoodIdentificationType")>
        <tr valign="middle">
            <td><div class="tabletext"><#if goodIdentificationType?exists>${(goodIdentificationType.description)?if_exists}<#else>[${(goodIdentification.goodIdentificationTypeId)?if_exists}]</#if></div></td>
            <td align="center">
                <FORM method=POST action="<@ofbizUrl>/updateGoodIdentification</@ofbizUrl>" name="lineForm${line}">
                    <input type=hidden name="productId" value="${(goodIdentification.productId)?if_exists}">
                    <input type=hidden name="goodIdentificationTypeId" value="${(goodIdentification.goodIdentificationTypeId)?if_exists}">
                    <input type=text size="20" name="idValue" value="${(goodIdentification.idValue)?if_exists}" class="inputBox">
                    <INPUT type=submit value="${uiLabelMap.CommonUpdate}" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/deleteGoodIdentification?productId=${(goodIdentification.productId)?if_exists}&goodIdentificationTypeId=${(goodIdentification.goodIdentificationTypeId)?if_exists}</@ofbizUrl>" class="buttontext">
            [${uiLabelMap.CommonDelete}]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createGoodIdentification</@ofbizUrl>" style="margin: 0;" name="createGoodIdentificationForm">
            <input type="hidden" name="productId" value="${productId}">
            <input type="hidden" name="useValues" value="true">
        
            <div class="head2">${uiLabelMap.CommonAddId} :</div>
            <div class="tabletext">
                ${uiLabelMap.ProductIdType} :
                <select name="goodIdentificationTypeId" class="selectBox">
                    <#list goodIdentificationTypes as goodIdentificationType>
                        <option value="${(goodIdentificationType.goodIdentificationTypeId)?if_exists}">${(goodIdentificationType.description)?if_exists}</option>
                    </#list>
                </select>
                ${uiLabelMap.ProductIdValue} : <input type=text size="20" name="idValue" class="inputBox">&nbsp;<input type="submit" value="${uiLabelMap.CommonAdd}" style="font-size: x-small;">
            </div>        
        </form>
    </#if>    
<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
