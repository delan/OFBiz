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
 *@version    $Revision$
 *@since      2.2
-->

<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">GL Accounts <span class="head2">for <#if product?exists>${(product.productName)?if_exists}</#if> [ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <p>    
    <#if productId?exists && product?exists>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Account&nbsp;Type</b></div></td>
            <td align="center"><div class="tabletext"><b>GL&nbsp;Account</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list productGlAccounts as productGlAccount>
        <#assign line = line + 1>
        <#assign productGlAccountType = productGlAccount.getRelatedOneCache("ProductGlAccountType")>
        <#assign curGlAccount = productGlAccount.getRelatedOneCache("GlAccount")>
        <tr valign="middle">
            <td><div class="tabletext"><#if productGlAccountType?exists>${(productGlAccountType.description)?if_exists}<#else>[${(productGlAccount.productGlAccountTypeId)?if_exists}]</#if></div></td>
            <td align="center">
                <FORM method=POST action="<@ofbizUrl>/updateProductGlAccount</@ofbizUrl>" name="lineForm${line}">
                    <input type=hidden name="productId" value="${(productGlAccount.productId)?if_exists}">
                    <input type=hidden name="productGlAccountTypeId" value="${(productGlAccount.productGlAccountTypeId)?if_exists}">
                    <select class="selectBox" name="glAccountId">
                        <#if curGlAccount?exists>
                            <option value="${(curGlAccount.glAccountId)?if_exists}">${(curGlAccount.accountCode)?if_exists} ${(curGlAccount.accountName)?if_exists}</option>
                            <option value="${(curGlAccount.glAccountId)?if_exists}"></option>
                        </#if>
                        <#list glAccounts as glAccount>
                            <option value="${(glAccount.glAccountId)?if_exists}">${(glAccount.accountCode)?if_exists} ${(glAccount.accountName)?if_exists}</option>
                        </#list>
                    </select>
                    <input type=text size="20" name="glAccountId" value="${(productGlAccount.idValue)?if_exists}" class="inputBox">
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/deleteProductGlAccount?productId=${(productGlAccount.productId)?if_exists}&productGlAccountTypeId=${(productGlAccount.productGlAccountTypeId)?if_exists}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createProductGlAccount</@ofbizUrl>" style="margin: 0;" name="createProductGlAccountForm">
            <input type="hidden" name="productId" value="${productId}">
            <input type="hidden" name="useValues" value="true">
        
            <div class="head2">Add GL Account:</div>
            <div class="tabletext">
                Account Type:
                <select name="productGlAccountTypeId" class="selectBox">
                    <#list productGlAccountTypes as productGlAccountType>
                        <option value="${(productGlAccountType.productGlAccountTypeId)?if_exists}">${(productGlAccountType.description)?if_exists}</option>
                    </#list>
                </select>
                GL Account: 
                <select name="glAccountId" class="inputBox">
                    <#list glAccounts as glAccount>
                        <option value="${(glAccount.glAccountId)?if_exists}">${(glAccount.accountCode)?if_exists} ${(glAccount.accountName)?if_exists}</option>
                    </#list>
                </select>
                <input type="submit" value="Add" style="font-size: x-small;">
            </div>        
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
