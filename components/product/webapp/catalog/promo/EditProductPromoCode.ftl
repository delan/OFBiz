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
 *@version    $Revision: 1.2 $
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>
${pages.get("/promo/PromoTabBar.ftl")}
    <div class="head1">Promotion Code</div>
    <div>
        <a href="<@ofbizUrl>/EditProductPromoCode?productPromoId=${productPromoId?if_exists}</@ofbizUrl>" class="buttontext">[New Promotion Code]</a>
    </div>

    ${productPromoCodeFormWrapper.renderFormString()}

    <br/>
    <#if productPromoCode?exists>
        <#if productPromoCode.requireEmailOrParty?if_exists == "N">
            <div class="tableheadtext">NOTE: The Require Email Or Party flag is set to N (No), so any email addresses or parties listed here will be ignored. To require an email address or party from these lists, set the flag to Y (Yes).</div>
        </#if>
        <div class="head3">Promo Code Emails</div>
        <#list productPromoCodeEmails as productPromoCodeEmail>
            <div class="tabletext"><a href="<@ofbizUrl>/deleteProductPromoCodeEmail?productPromoCodeId=${productPromoCodeEmail.productPromoCodeId}&emailAddress=${productPromoCodeEmail.emailAddress}</@ofbizUrl>" class="buttontext">[X]</a>&nbsp;${productPromoCodeEmail.emailAddress}</div>
        </#list>
        <div class="tabletext">
            <form method="POST" action="<@ofbizUrl>/createProductPromoCodeEmail</@ofbizUrl>" style="margin: 0;">
                <input type="hidden" name="productPromoCodeId" value="${productPromoCodeId?if_exists}"/>
                Add Email: <input type="text" size="40" name="emailAddress" class="inputBox">
                <input type="submit" value="${uiLabelMap.CommonAdd}">
            </form>
        </div>

        <div class="head3">Promo Code Parties</div>
        <#list productPromoCodeParties as productPromoCodeParty>
            <div class="tabletext"><a href="<@ofbizUrl>/deleteProductPromoCodeParty?productPromoCodeId=${productPromoCodeParty.productPromoCodeId}&partyId=${productPromoCodeParty.partyId}</@ofbizUrl>" class="buttontext">[X]</a>&nbsp;${productPromoCodeParty.partyId}</div>
        </#list>
        <div class="tabletext">
            <form method="POST" action="<@ofbizUrl>/createProductPromoCodeParty</@ofbizUrl>" style="margin: 0;">
                <input type="hidden" name="productPromoCodeId" value="${productPromoCodeId?if_exists}"/>
                Add Party ID: <input type="text" size="10" name="partyId" class="inputBox">
                <input type="submit" value="${uiLabelMap.CommonAdd}">
            </form>
        </div>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductViewPermissionError}</h3>
</#if>
