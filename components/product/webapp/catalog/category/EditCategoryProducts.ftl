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
 *@author     Catherine Heintz (catherine.heintz@nereide.biz)
 *@version    $Revision: 1.12 $
 *@since      2.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>
${pages.get("/category/CategoryTabBar.ftl")}
      
    <div class="head1">${uiLabelMap.ProductProducts} <span class="head2">${uiLabelMap.CommonFor}<#if productCategory?exists>${(productCategory.description)?if_exists} [${uiLabelMap.CommonId}:${productCategoryId?if_exists}]</#if></span></div>
    
    <a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewCategory}]</a>
    <#if productCategoryId?has_content>
        <a href="/ecommerce/control/category?category_id=${productCategoryId?if_exists}" class="buttontext" target="_blank">[${uiLabelMap.ProductCategoryPage}]</a>
        <a href="<@ofbizUrl>/createProductInCategoryStart?productCategoryId=${productCategoryId?if_exists}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductCreateProductInCategory}]</a>
    </#if>
    <#if activeOnly>
        <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&activeOnly=false</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductActiveAndInactive}]</a>
    <#else>
        <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&activeOnly=true</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductActiveOnly}]</a>
    </#if>    
    <p>
    <#if productCategoryId?exists && productCategory?exists>
        <p class="head2">${uiLabelMap.ProductProductCategoryMemberMaintenance}</p>
    
        <#if (listSize > 0)>
            <table border="0" width="100%" cellpadding="2">
                <tr>
                <td align=right>
                    <b>
                    <#if (viewIndex > 0)>
                    <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
                    </#if>
                    <#if (listSize > 0)>
                        ${lowIndex+1} - ${highIndex} of ${listSize}
                    </#if>
                    <#if (listSize > highIndex)>
                    | <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
                    </#if>
                    </b>
                </td>
                </tr>
            </table>
        </#if>
        
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>${uiLabelMap.ProductProductNameId}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonFromDateTime}</b></div></td>
            <td align="center"><div class="tabletext"><b>${uiLabelMap.ProductThruDateTimeSequenceQuantity}</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#if (listSize > 0)>
            <#assign line = 0>
            <#list productCategoryMembers[lowIndex..highIndex-1] as productCategoryMember>
            <#assign product = productCategoryMember.getRelatedOne("Product")>
            <#assign hasntStarted = false>
            <#if productCategoryMember.fromDate?exists && nowTimestamp.before(productCategoryMember.getTimestamp("fromDate"))><#assign hasntStarted = true></#if>
            <#assign hasExpired = false>
            <#if productCategoryMember.thruDate?exists && nowTimestamp.after(productCategoryMember.getTimestamp("thruDate"))><#assign hasExpired = true></#if>
            <tr valign="middle">
                <td><a href="<@ofbizUrl>/EditProduct?productId=${(productCategoryMember.productId)?if_exists}</@ofbizUrl>" class="buttontext"><#if product?exists>${(product.internalName)?if_exists}</#if> [${(productCategoryMember.productId)?if_exists}]</a></td>
                <td><div class="tabletext"<#if hasntStarted> style="color: red;"</#if>>${(productCategoryMember.fromDate)?if_exists}</div></td>
                <td align="center">
                    <FORM method=POST action="<@ofbizUrl>/updateCategoryProductMember?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex}</@ofbizUrl>" name="lineForm${line}">
                        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
                        <input type=hidden name="productId" value="${(productCategoryMember.productId)?if_exists}">
                        <input type=hidden name="productCategoryId" value="${(productCategoryMember.productCategoryId)?if_exists}">
                        <input type=hidden name="fromDate" value="${(productCategoryMember.fromDate)?if_exists}">
                        <input type=text size="25" name="thruDate" value="${(productCategoryMember.thruDate)?if_exists}" class="inputBox" <#if hasExpired>style="color: red;"</#if>>
                        <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(productCategoryMember.thruDate)?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                        <input type=text size="5" name="sequenceNum" value="${(productCategoryMember.sequenceNum)?if_exists}" class="inputBox">
                        <input type=text size="5" name="quantity" value="${(productCategoryMember.quantity)?if_exists}" class="inputBox">
                        <INPUT type=submit value="${uiLabelMap.CommonUpdate}" style="font-size: x-small;">
                    </FORM>
                </td>
                <td align="center">
                <a href="<@ofbizUrl>/removeCategoryProductMember?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex}&productId=${(productCategoryMember.productId)?if_exists}&productCategoryId=${(productCategoryMember.productCategoryId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue((productCategoryMember.getTimestamp("fromDate").toString()))}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">
                [${uiLabelMap.CommonDelete}]</a>
                </td>
            </tr>
            <#assign line = line + 1>
            </#list>
        </#if>
        </table>
        
        <#if (listSize > 0)>
            <table border="0" width="100%" cellpadding="2">
                <tr>
                <td align=right>
                    <b>
                    <#if (viewIndex > 0)>
                        <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
                        </#if>
                        <#if (listSize > 0)>
                        ${lowIndex+1} - ${highIndex} of ${listSize}
                        </#if>
                        <#if (listSize > highIndex)>
                        | <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
                    </#if>
                    </b>
                </td>
                </tr>
            </table>
        </#if>
        <br>
        <form method="POST" action="<@ofbizUrl>/addCategoryProductMember</@ofbizUrl>" style="margin: 0;" name="addProductCategoryMemberForm">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">${uiLabelMap.ProductAddProductCategoryMember}:</div>
        <div class="tabletext">
            ${uiLabelMap.ProductProductId}: <input type=text size="20" name="productId" class="inputBox">
            ${uiLabelMap.CommonFromDate}: <input type=text size="22" name="fromDate" class="inputBox">
            <a href="javascript:call_cal(document.addProductCategoryMemberForm.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
            <input type="submit" value="${uiLabelMap.CommonAdd}">
        </div>
        </form>
        
        <br>
        <form method="POST" action="<@ofbizUrl>/copyCategoryProductMembers</@ofbizUrl>" style="margin: 0;" name="copyCategoryProductMembersForm">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">${uiLabelMap.ProductCopyProductCategoryMembersToAnotherCategory}:</div>
        <div class="tabletext">
            ${uiLabelMap.ProductTargetProductCategory}:
            <select name="productCategoryIdTo" class="selectBox">
            <option value=""></option>
            <#list productCategories as productCategoryTo>
                <option value="${(productCategoryTo.productCategoryId)?if_exists}">${(productCategoryTo.description)?if_exists}  [${(productCategoryTo.productCategoryId)?if_exists}]</option>
            </#list>
            </select>
            <br>
            ${uiLabelMap.ProductOptionalFilterWithDate}: <input type=text size="20" name="validDate" class="inputBox">
            <a href="javascript:call_cal(document.copyCategoryProductMembersForm.validDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
            <br>
            ${uiLabelMap.ProductIncludeSubCategories}?
            <select name="recurse" class="selectBox">
                <option>${uiLabelMap.CommonN}</option>
                <option>${uiLabelMap.CommonY}</option>
            </select>
            <input type="submit" value="${uiLabelMap.CommonCopy}">
        </div>
        </form>
        
        <br>
        <form method="POST" action="<@ofbizUrl>/expireAllCategoryProductMembers</@ofbizUrl>" style="margin: 0;" name="expireAllCategoryProductMembersForm">
        <input type="hidden" name="productCategoryId" value="${productCategoryId}?if_exists">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">${uiLabelMap.ProductExpireAllProductMembers}:</div>
        <div class="tabletext">
            ${uiLabelMap.ProductOptionalExpirationDate}: <input type=text size="20" name="thruDate" class="inputBox">
            <a href="javascript:call_cal(document.expireAllCategoryProductMembersForm.thruDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
            &nbsp;&nbsp;<input type="submit" value="${uiLabelMap.CommonExpireAll}">
        </div>
        </form>
        <br>
        <form method="POST" action="<@ofbizUrl>/removeExpiredCategoryProductMembers</@ofbizUrl>" style="margin: 0;" name="removeExpiredCategoryProductMembersForm">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">${uiLabelMap.ProductRemoveExpiredProductMembers}:</div>
        <div class="tabletext">
            ${uiLabelMap.ProductOptionalExpiredBeforeDate}: <input type=text size="20" name="validDate" class="inputBox">
            <a href="javascript:call_cal(document.removeExpiredCategoryProductMembersForm.validDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
            &nbsp;&nbsp;<input type="submit" value="${uiLabelMap.CommonRemoveExpired}">
        </div>
        </form>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
