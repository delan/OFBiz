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
 *@version    $Revision: 1.3 $
 *@since      2.1
-->

<#if hasPermission>
${pages.get("/category/CategoryTabBar.ftl")}

    <div class="head1">Products <span class="head2">for <#if productCategory?exists>${(productCategory.description)?if_exists} [ID:${productCategoryId?if_exists}]</#if></span></div>
    
    <a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[New Category]</a>
    <#if productCategoryId?has_content>
        <a href="/ecommerce/control/category?category_id=${productCategoryId?if_exists}" class="buttontext" target="_blank">[Category Page]</a>
        <a href="<@ofbizUrl>/createProductInCategoryStart?productCategoryId=${productCategoryId?if_exists}</@ofbizUrl>" class="buttontext">[Create Product In Category]</a>
    </#if>
    <#if activeOnly>
        <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&activeOnly=false</@ofbizUrl>" class="buttontext">[Active and Inactive]</a>
    <#else>
        <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&activeOnly=true</@ofbizUrl>" class="buttontext">[Active Only]</a>
    </#if>    
    <p>
    <#if productCategoryId?exists && productCategory?exists>
        <p class="head2">Product-Category Member Maintenance</p>
    
        <#if (listSize > 0)>
            <table border="0" width="100%" cellpadding="2">
                <tr>
                <td align=right>
                    <b>
                    <#if (viewIndex > 0)>
                    <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[Previous]</a> |
                    </#if>
                    <#if (listSize > 0)>
                        ${lowIndex+1} - ${highIndex+1} of ${listSize}
                    </#if>
                    <#if (listSize > (highIndex+1))>
                    | <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[Next]</a>
                    </#if>
                    </b>
                </td>
                </tr>
            </table>
        </#if>
        
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Product Name [ID]</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence&nbsp;&amp;&nbsp;Quantity</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#if (listSize > 0)>
            <#assign line = 0>
            <#list productCategoryMembers[lowIndex..highIndex] as productCategoryMember> 
            <#assign product = productCategoryMember.getRelatedOne("Product")>
            <tr valign="middle">
                <td><a href="<@ofbizUrl>/EditProduct?productId=${(productCategoryMember.productId)?if_exists}</@ofbizUrl>" class="buttontext"><#if product?exists>${(product.productName)?if_exists}</#if> [${(productCategoryMember.productId)?if_exists}]</a></td>
                <td>
                    <#assign hasntStarted = false>
                    <#if (productCategoryMember.getTimestamp("fromDate"))?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().before(productCategoryMember.getTimestamp("fromDate"))> <#assign hasntStarted = true> </#if>
                    <div class="tabletext"<#if hasntStarted> style="color: red;"</#if>>
                    ${(productCategoryMember.fromDate)?if_exists}
                    </div>
                </td>
                <td align="center">
                    <#assign hasExpired = false>
                    <#if (productCategoryMember.getTimestamp("thruDate"))?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(productCategoryMember.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                    <FORM method=POST action="<@ofbizUrl>/updateCategoryProductMember?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex}</@ofbizUrl>" name="lineForm${line}">
                        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
                        <input type=hidden name="productId" value="${(productCategoryMember.productId)?if_exists}">
                        <input type=hidden name="productCategoryId" value="${(productCategoryMember.productCategoryId)?if_exists}">
                        <input type=hidden name="fromDate" value="${(productCategoryMember.fromDate)?if_exists}">
                        <input type=text size="25" name="thruDate" value="${(productCategoryMember.thruDate)?if_exists}" class="inputBox" <#if hasExpired>style="color: red;"</#if>>
                        <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(productCategoryMember.thruDate)?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                        <input type=text size="5" name="sequenceNum" value="${(productCategoryMember.sequenceNum)?if_exists}" class="inputBox">
                        <input type=text size="5" name="quantity" value="${(productCategoryMember.quantity)?if_exists}" class="inputBox">
                        <INPUT type=submit value="Update" style="font-size: x-small;">
                    </FORM>
                </td>
                <td align="center">
                <a href="<@ofbizUrl>/removeCategoryProductMember?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex}&productId=${(productCategoryMember.productId)?if_exists}&productCategoryId=${(productCategoryMember.productCategoryId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue((productCategoryMember.getTimestamp("fromDate").toString()))}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">
                [Delete]</a>
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
                        <a href="<@ofbizUrl>"/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[Previous]</a> |
                        </#if>
                        <#if (listSize > 0)>
                        ${lowIndex+1} - ${highIndex+1} of ${listSize}
                        </#if>
                        <#if (listSize > (highIndex+1))>
                        | <a href="<@ofbizUrl>"/EditCategoryProducts?productCategoryId=${productCategoryId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[Next]</a>
                    </#if>
                    </b>
                </td>
                </tr>
            </table>
        </#if>
        <br>
        <form method="POST" action="<@ofbizUrl>/addCategoryProductMember</@ofbizUrl>" style="margin: 0;" name="addProductCategoryMemberForm">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
        <input type="hidden" name="useValues" value="true">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">Add ProductCategoryMember:</div>
        <div class="tabletext">
            Product ID: <input type=text size="20" name="productId" class="inputBox">
            From Date: <input type=text size="22" name="fromDate" class="inputBox">
            <a href="javascript:call_cal(document.addProductCategoryMemberForm.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
            <input type="submit" value="Add">
        </div>
        </form>
        
        <br>
        <form method="POST" action="<@ofbizUrl>/copyCategoryProductMembers</@ofbizUrl>" style="margin: 0;">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
        <input type="hidden" name="useValues" value="true">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">Copy ProductCategoryMembers to Another Category:</div>
        <div class="tabletext">
            Product Category:
            <select name="productCategoryIdTo" class="selectBox">
            <option value=""></option>
            <#list productCategories as productCategoryTo>
                <option value="${(productCategoryTo.productCategoryId)?if_exists}">${(productCategoryTo.description)?if_exists}  [${(productCategoryTo.productCategoryId)?if_exists}]</option>
            </#list>
            </select>
            <br>
            Optional Filter With Date: <input type=text size="20" name="validDate" class="inputBox">
            <br>
            Include Sub-Categories?
            <select name="recurse" class="selectBox">
                <option>N</option>
                <option>Y</option>
            </select>
            <input type="submit" value="Copy">
        </div>
        </form>
        
        <br>
        <form method="POST" action="<@ofbizUrl>/expireAllCategoryProductMembers</@ofbizUrl>" style="margin: 0;">
        <input type="hidden" name="productCategoryId" value="${productCategoryId}?if_exists">
        <input type="hidden" name="useValues" value="true">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">Expire All Product Members:</div>
        <div class="tabletext">
            Optional Expiration Date: <input type=text size="20" name="thruDate" class="inputBox">
            <input type="submit" value="Expire All">
        </div>
        </form>
        <br>
        <form method="POST" action="<@ofbizUrl>/removeExpiredCategoryProductMembers</@ofbizUrl>" style="margin: 0;">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
        <input type="hidden" name="useValues" value="true">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">Remove Expired Product Members:</div>
        <div class="tabletext">
            Optional Expired Before Date: <input type=text size="20" name="validDate" class="inputBox">
            <input type="submit" value="Remove Expired">
        </div>
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
