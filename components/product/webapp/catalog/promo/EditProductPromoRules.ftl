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
 *@version    $Revision: 1.18 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>
${pages.get("/promo/PromoTabBar.ftl")}

    <div class="head1">${uiLabelMap.ProductRules} <span class="head2">${uiLabelMap.CommonFor} <#if productPromo?exists>${(productPromo.promoName)?if_exists}</#if> [${uiLabelMap.CommonId}:${productPromoId?if_exists}]</span></div>
    <a href="<@ofbizUrl>/EditProductPromo</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewProductPromo}]</a>
    <br>
    <br>
    <#if productPromoId?exists && productPromo?exists>

        <#-- ======================= Rules ======================== -->

        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td width="10%"><div class="tabletext"><b>${uiLabelMap.ProductRuleId}</b></div></td>
            <td width="80%"><div class="tabletext"><b>${uiLabelMap.ProductRuleName}</b></div></td>
            <td width="10%"><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#list productPromoRules as productPromoRule>
        <#assign productPromoConds = productPromoRule.getRelated("ProductPromoCond")>
        <#assign productPromoActions = productPromoRule.getRelated("ProductPromoAction")>
        <tr valign="middle">
            <td><div class="tabletext"><b>${(productPromoRule.productPromoRuleId)?if_exists}</b></div></td>
            <td align="left">
                <form method="POST" action="<@ofbizUrl>/updateProductPromoRule</@ofbizUrl>">
                    <input type="hidden" name="productPromoId" value="${(productPromoRule.productPromoId)?if_exists}">
                    <input type="hidden" name="productPromoRuleId" value="${(productPromoRule.productPromoRuleId)?if_exists}">
                    <input type="text" size="30" name="ruleName" value="${(productPromoRule.ruleName)?if_exists}" class="inputBox">
                    <input type="submit" value="${uiLabelMap.CommonUpdate}" style="font-size: x-small;">
                </form>
            </td>
            <td align="center">&nbsp;
            <#if (productPromoConds.size() == 0 && productPromoActions.size() == 0)>
                <a href="<@ofbizUrl>/deleteProductPromoRule?productPromoId=${(productPromoRule.productPromoId)?if_exists}&productPromoRuleId=${(productPromoRule.productPromoRuleId)?if_exists}</@ofbizUrl>" class="buttontext">
                [${uiLabelMap.CommonDelete}]</a>
            </#if>
            </td>
        </tr>
        <tr valign="top">
            <td align="right"><div class="tabletext">${uiLabelMap.ProductConditions}:</div></td>
            <td align="left" colspan="2">
                <table border="1" width="100%" cellpadding="2" cellspacing="0">
                <#assign maxCondSeqId = 1>
                <#list productPromoConds as productPromoCond>
                    <tr>
                        <!-- if cur seq id is a number and is greater than max, set new max for input box prefill below -->
                        <#if (productPromoCond.productPromoCondSeqId)?exists>
                            <#assign curCondSeqId = Static["java.lang.Integer"].valueOf(productPromoCond.getString("productPromoCondSeqId"))>
                            <#if (curCondSeqId >= maxCondSeqId)>
                                <#assign maxCondSeqId = curCondSeqId + 1>
                            </#if>
                        </#if>
                        <td><div class="tabletext"><b>${(productPromoCond.productPromoCondSeqId)?if_exists}</b></div></td>
                        <td align="left">
                            <form method=POST action="<@ofbizUrl>/updateProductPromoCond</@ofbizUrl>">
                                <input type=hidden name="productPromoId" value="${(productPromoCond.productPromoId)?if_exists}"/>
                                <input type=hidden name="productPromoRuleId" value="${(productPromoCond.productPromoRuleId)?if_exists}"/>
                                <input type=hidden name="productPromoCondSeqId" value="${(productPromoCond.productPromoCondSeqId)?if_exists}"/>
                                <select name="inputParamEnumId" size=1 class="selectBox">
                                    <#if (productPromoCond.inputParamEnumId)?exists>
                                        <#assign inputParamEnum = productPromoCond.getRelatedOneCache("InputParamEnumeration")>
                                        <option value="${productPromoCond.inputParamEnumId}"><#if inputParamEnum?exists>${(inputParamEnum.description)?if_exists}<#else>[${(productPromoCond.inputParamEnumId)?if_exists}]</#if></option>
                                        <option value="${(productPromoCond.inputParamEnumId)?if_exists}">&nbsp;</option>
                                    <#else>
                                        <option value="">&nbsp;</option>
                                    </#if>
                                    <#list inputParamEnums as inputParamEnum>
                                        <option value="${(inputParamEnum.enumId)?if_exists}">${(inputParamEnum.description)?if_exists}</option>
                                    </#list>
                                </select>
                                <select name="operatorEnumId" size=1 class="selectBox">
                                    <#if (productPromoCond.operatorEnumId)?exists>
                                        <#assign operatorEnum = productPromoCond.getRelatedOneCache("OperatorEnumeration")>
                                        <option value="${(productPromoCond.operatorEnumId)?if_exists}"><#if operatorEnum?exists>${(operatorEnum.description)?if_exists}<#else>[${(productPromoCond.operatorEnumId)?if_exists}]</#if></option>
                                        <option value="${(productPromoCond.operatorEnumId)?if_exists}">&nbsp;</option>
                                    <#else>
                                        <option value="">&nbsp;</option>
                                    </#if>
                                    <#list condOperEnums as condOperEnum>
                                    <option value="${(condOperEnum.enumId)?if_exists}">${(condOperEnum.description)?if_exists}</option>
                                    </#list>
                                </select>
                                <input type=text size="25" name="condValue" value="${(productPromoCond.condValue)?if_exists}" class="inputBox">
                                <input type=submit value="${uiLabelMap.CommonUpdate}" style="font-size: x-small;">
                            </form>
                            <#-- ======================= Categories ======================== -->
                            <div class="tableheadtext">Condition Categories:</div>
                            <#assign condProductPromoCategories = productPromoCond.getRelated("ProductPromoCategory")>
                            <#list condProductPromoCategories as condProductPromoCategory>
                                <#assign condProductCategory = condProductPromoCategory.getRelatedOneCache("ProductCategory")>
                                <#assign condApplEnumeration = condProductPromoCategory.getRelatedOneCache("ApplEnumeration")>
                                <div class="tabletext">
                                    ${(condProductCategory.description)?if_exists} [${condProductPromoCategory.productCategoryId}]
                                    - ${(condApplEnumeration.description)?default(condProductPromoCategory.productPromoApplEnumId)}
                                    - SubCats? ${condProductPromoCategory.includeSubCategories?default("N")}
                                    - And Group: ${condProductPromoCategory.andGroupId}
                                    <a href="<@ofbizUrl>/deleteProductPromoCategory?productPromoId=${(condProductPromoCategory.productPromoId)?if_exists}&productPromoRuleId=${(condProductPromoCategory.productPromoRuleId)?if_exists}&productPromoActionSeqId=${(condProductPromoCategory.productPromoActionSeqId)?if_exists}&productPromoCondSeqId=${(condProductPromoCategory.productPromoCondSeqId)?if_exists}&productCategoryId=${(condProductPromoCategory.productCategoryId)?if_exists}&andGroupId=${(condProductPromoCategory.andGroupId)?if_exists}</@ofbizUrl>" class="buttontext">
                                    [${uiLabelMap.CommonDelete}]</a>
                                </div>
                            </#list>
                            <div class="tabletext">
                                <form method="POST" action="<@ofbizUrl>/createProductPromoCategory</@ofbizUrl>">
                                    <input type="hidden" name="productPromoId" value="${productPromoId}">
                                    <input type="hidden" name="productPromoRuleId" value="${productPromoCond.productPromoRuleId}">
                                    <input type="hidden" name="productPromoActionSeqId" value="_NA_">
                                    <input type="hidden" name="productPromoCondSeqId" value="${productPromoCond.productPromoCondSeqId}">
                                    <select name="productCategoryId" class="selectBox">
                                        <#list productCategories as productCategory>
                                            <option value="${productCategory.productCategoryId}">${productCategory.description}</option>
                                        </#list>
                                    </select>
                                    <select name="productPromoApplEnumId" class="selectBox">
                                        <#list productPromoApplEnums as productPromoApplEnum>
                                            <option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.description}</option>
                                        </#list>
                                    </select>
                                    <select name="includeSubCategories" class="selectBox">
                                        <option value="N">N</option>
                                        <option value="Y">Y</option>
                                    </select>
                                    And Group: <input type="text" size="10" maxlength="20" name="andGroupId" value="_NA_" class="inputBox"/>*
                                    <input type="submit" value="${uiLabelMap.CommonAdd}" style="font-size: x-small;">
                                </form>
                            </div>
                            <#-- ======================= Products ======================== -->
                            <div class="tableheadtext">Condition Products:</div>
                            <#assign condProductPromoProducts = productPromoCond.getRelated("ProductPromoProduct")>
                            <#list condProductPromoProducts as condProductPromoProduct>
                                <#assign condProduct = condProductPromoProduct.getRelatedOneCache("Product")?if_exists>
                                <#assign condApplEnumeration = condProductPromoProduct.getRelatedOneCache("ApplEnumeration")>
                                <div class="tabletext">
                                    ${(condProduct.internalName)?if_exists} [${condProductPromoProduct.productId}]
                                    - ${(condApplEnumeration.description)?default(condProductPromoProduct.productPromoApplEnumId)}
                                    <a href="<@ofbizUrl>/deleteProductPromoProduct?productPromoId=${(condProductPromoProduct.productPromoId)?if_exists}&productPromoRuleId=${(condProductPromoProduct.productPromoRuleId)?if_exists}&productPromoActionSeqId=${(condProductPromoProduct.productPromoActionSeqId)?if_exists}&productPromoCondSeqId=${(condProductPromoProduct.productPromoCondSeqId)?if_exists}&productId=${(condProductPromoProduct.productId)?if_exists}</@ofbizUrl>" class="buttontext">
                                    [${uiLabelMap.CommonDelete}]</a>
                                </div>
                            </#list>
                            <div class="tabletext">
                                <form method="POST" action="<@ofbizUrl>/createProductPromoProduct</@ofbizUrl>">
                                    <input type="hidden" name="productPromoId" value="${productPromoId}">
                                    <input type="hidden" name="productPromoRuleId" value="${productPromoCond.productPromoRuleId}">
                                    <input type="hidden" name="productPromoActionSeqId" value="_NA_">
                                    <input type="hidden" name="productPromoCondSeqId" value="${productPromoCond.productPromoCondSeqId}">
                                    Product ID: <input type="text" size="20" maxlength="20" name="productId" value="" class="inputBox"/>
                                    <select name="productPromoApplEnumId" class="selectBox">
                                        <#list productPromoApplEnums as productPromoApplEnum>
                                            <option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.description}</option>
                                        </#list>
                                    </select>
                                    <input type="submit" value="${uiLabelMap.CommonAdd}" style="font-size: x-small;">
                                </form>
                            </div>
                        </td>
                        <td align="center">
                        <a href="<@ofbizUrl>/deleteProductPromoCond?productPromoId=${(productPromoCond.productPromoId)?if_exists}&productPromoRuleId=${(productPromoCond.productPromoRuleId)?if_exists}&productPromoCondSeqId=${(productPromoCond.productPromoCondSeqId)?if_exists}</@ofbizUrl>" class="buttontext">
                        [${uiLabelMap.CommonDelete}]</a>
                        </td>
                    </tr>
                </#list>
                <tr>
                    <td align="left" colspan="3">
                        <form method=POST action="<@ofbizUrl>/createProductPromoCond</@ofbizUrl>">
                            <input type=hidden name="productPromoId" value="${(productPromoRule.productPromoId)?if_exists}">
                            <input type=hidden name="productPromoRuleId" value="${(productPromoRule.productPromoRuleId)?if_exists}">
                            <span class="tabletext"><b>${uiLabelMap.CommonNew} :</b>&nbsp;</span>
                            <#-- <input type=text size="5" name="productPromoCondSeqId" value="${maxCondSeqId?if_exists}" class="inputBox"> -->
                            <select name="inputParamEnumId" size=1 class="selectBox">
                                <#list inputParamEnums as inputParamEnum>
                                    <option value="${(inputParamEnum.enumId)?if_exists}">${(inputParamEnum.description)?if_exists}</option>
                                </#list>
                            </select>
                            <select name="operatorEnumId" size=1 class="selectBox">
                                <#list condOperEnums as condOperEnum>
                                <option value="${(condOperEnum.enumId)?if_exists}">${(condOperEnum.description)?if_exists}</option>
                                </#list>
                            </select>
                            <input type="text" size="25" name="condValue" class="inputBox">
                            <input type=submit value="${uiLabelMap.CommonCreate}" style="font-size: x-small;">
                        </form>
                    </td>
                </tr>
                </table>
            </td>
        </tr>
        <tr valign="top">
            <td align="right"><div class="tabletext">${uiLabelMap.ProductActions} :</div></td>
            <td align="left" colspan="2">
                <table border="1" width="100%" cellpadding="2" cellspacing="0">
                <#assign maxActionSeqId = 1>
                <#list productPromoActions as productPromoAction>
                    <tr>
                        <!-- if cur seq id is a number and is greater than max, set new max for input box prefill below -->
                        <#if (productPromoAction.productPromoActionSeqId)?exists>
                            <#assign curActionSeqId = Static["java.lang.Integer"].valueOf(productPromoAction.productPromoActionSeqId)>
                            <#if (curActionSeqId >= maxActionSeqId)>
                                <#assign maxActionSeqId = curActionSeqId + 1>
                            </#if>
                        </#if>
                        <td><div class="tabletext"><b>${(productPromoAction.productPromoActionSeqId)?if_exists}</b></div></td>
                        <td align="left">
                            <div class="tabletext">
                            <form method=POST action="<@ofbizUrl>/updateProductPromoAction</@ofbizUrl>">
                                <input type=hidden name="productPromoId" value="${(productPromoAction.productPromoId)?if_exists}">
                                <input type=hidden name="productPromoRuleId" value="${(productPromoAction.productPromoRuleId)?if_exists}">
                                <input type=hidden name="productPromoActionSeqId" value="${(productPromoAction.productPromoActionSeqId)?if_exists}">
                                <select name="productPromoActionEnumId" size=1 class="selectBox">
                                    <#if (productPromoAction.productPromoActionEnumId)?exists>
                                        <#assign productPromoActionCurEnum = productPromoAction.getRelatedOneCache("ActionEnumeration")>
                                        <option value="${(productPromoAction.productPromoActionEnumId)?if_exists}"><#if productPromoActionCurEnum?exists>${(productPromoActionCurEnum.description)?if_exists}<#else>[${(productPromoAction.productPromoActionEnumId)?if_exists}]</#if></option>
                                        <option value="${(productPromoAction.productPromoActionEnumId)?if_exists}">&nbsp;</option>
                                    <#else>
                                        <option value="">&nbsp;</option>
                                    </#if>
                                    <#list productPromoActionEnums as productPromoActionEnum>
                                        <option value="${(productPromoActionEnum.enumId)?if_exists}">${(productPromoActionEnum.description)?if_exists}</option>
                                    </#list>
                                </select>
                                <input type="hidden" name="orderAdjustmentTypeId" value="${(productPromoAction.orderAdjustmentTypeId)?if_exists}">
                                <#-- <select name="orderAdjustmentTypeId" size=1 class="selectBox">
                                    <#if (productPromoAction.orderAdjustmentTypeId)?exists>
                                        <#assign orderAdjustmentType = productPromoAction.getRelatedOneCache("OrderAdjustmentType")>
                                        <option value="${(productPromoAction.orderAdjustmentTypeId)?if_exists}"><#if orderAdjustmentType?exists>${(orderAdjustmentType.description)?if_exists}<#else>[${(productPromoAction.orderAdjustmentTypeId)?if_exists}]</#if></option>
                                        <option value="${(productPromoAction.orderAdjustmentTypeId)?if_exists}">&nbsp;</option>
                                    <#else>
                                        <option value="">&nbsp;</option>
                                    </#if>
                                    <#list orderAdjustmentTypes as orderAdjustmentType>
                                    <option value="${(orderAdjustmentType.orderAdjustmentTypeId)?if_exists}">${(orderAdjustmentType.description)?if_exists}</option>
                                    </#list>
                                </select> -->
                                Quantity:&nbsp;<input type=text size="5" name="quantity" value="${(productPromoAction.quantity)?if_exists}" class="inputBox">
                                Amount:&nbsp;<input type=text size="5" name="amount" value="${(productPromoAction.amount)?if_exists}" class="inputBox">
                                Item:&nbsp;<input type=text size="15" name="productId" value="${(productPromoAction.productId)?if_exists}" class="inputBox">
                                Party:&nbsp;<input type=text size="10" name="partyId" value="${(productPromoAction.partyId)?if_exists}" class="inputBox">
                                <input type=submit value="${uiLabelMap.CommonUpdate}" style="font-size: x-small;">
                            </form>
                            </div>
                            <#-- ======================= Categories ======================== -->
                            <div class="tableheadtext">Action Categories:</div>
                            <#assign actionProductPromoCategories = productPromoAction.getRelated("ProductPromoCategory")>
                            <#list actionProductPromoCategories as actionProductPromoCategory>
                                <#assign actionProductCategory = actionProductPromoCategory.getRelatedOneCache("ProductCategory")>
                                <#assign actionApplEnumeration = actionProductPromoCategory.getRelatedOneCache("ApplEnumeration")>
                                <div class="tabletext">
                                    ${(actionProductCategory.description)?if_exists} [${actionProductPromoCategory.productCategoryId}]
                                    - ${(actionApplEnumeration.description)?default(actionProductPromoCategory.productPromoApplEnumId)}
                                    - SubCats? ${actionProductPromoCategory.includeSubCategories?default("N")}
                                    - And Group: ${actionProductPromoCategory.andGroupId}
                                    <a href="<@ofbizUrl>/deleteProductPromoCategory?productPromoId=${(actionProductPromoCategory.productPromoId)?if_exists}&productPromoRuleId=${(actionProductPromoCategory.productPromoRuleId)?if_exists}&productPromoCondSeqId=${(actionProductPromoCategory.productPromoCondSeqId)?if_exists}&productPromoActionSeqId=${(actionProductPromoCategory.productPromoActionSeqId)?if_exists}&productCategoryId=${(actionProductPromoCategory.productCategoryId)?if_exists}&andGroupId=${(actionProductPromoCategory.andGroupId)?if_exists}</@ofbizUrl>" class="buttontext">
                                    [${uiLabelMap.CommonDelete}]</a>
                                </div>
                            </#list>
                            <div class="tabletext">
                                <form method="POST" action="<@ofbizUrl>/createProductPromoCategory</@ofbizUrl>">
                                    <input type="hidden" name="productPromoId" value="${productPromoId}">
                                    <input type="hidden" name="productPromoRuleId" value="${productPromoAction.productPromoRuleId}">
                                    <input type="hidden" name="productPromoActionSeqId" value="${productPromoAction.productPromoActionSeqId}">
                                    <input type="hidden" name="productPromoCondSeqId" value="_NA_">
                                    <select name="productCategoryId" class="selectBox">
                                        <#list productCategories as productCategory>
                                            <option value="${productCategory.productCategoryId}">${productCategory.description}</option>
                                        </#list>
                                    </select>
                                    <select name="productPromoApplEnumId" class="selectBox">
                                        <#list productPromoApplEnums as productPromoApplEnum>
                                            <option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.description}</option>
                                        </#list>
                                    </select>
                                    <select name="includeSubCategories" class="selectBox">
                                        <option value="N">N</option>
                                        <option value="Y">Y</option>
                                    </select>
                                    And Group: <input type="text" size="10" maxlength="20" name="andGroupId" value="_NA_" class="inputBox"/>*
                                    <input type="submit" value="${uiLabelMap.CommonAdd}" style="font-size: x-small;">
                                </form>
                            </div>
                            <#-- ======================= Products ======================== -->
                            <div class="tableheadtext">Action Products:</div>
                            <#assign actionProductPromoProducts = productPromoAction.getRelated("ProductPromoProduct")>
                            <#list actionProductPromoProducts as actionProductPromoProduct>
                                <#assign actionProduct = actionProductPromoProduct.getRelatedOneCache("Product")?if_exists>
                                <#assign actionApplEnumeration = actionProductPromoProduct.getRelatedOneCache("ApplEnumeration")>
                                <div class="tabletext">
                                    ${(actionProduct.internalName)?if_exists} [${actionProductPromoProduct.productId}]
                                    - ${(actionApplEnumeration.description)?default(actionProductPromoProduct.productPromoApplEnumId)}
                                    <a href="<@ofbizUrl>/deleteProductPromoProduct?productPromoId=${(actionProductPromoProduct.productPromoId)?if_exists}&productPromoRuleId=${(actionProductPromoProduct.productPromoRuleId)?if_exists}&productPromoCondSeqId=${(actionProductPromoProduct.productPromoCondSeqId)?if_exists}&productPromoActionSeqId=${(actionProductPromoProduct.productPromoActionSeqId)?if_exists}&productId=${(actionProductPromoProduct.productId)?if_exists}</@ofbizUrl>" class="buttontext">
                                    [${uiLabelMap.CommonDelete}]</a>
                                </div>
                            </#list>
                            <div class="tabletext">
                                <form method="POST" action="<@ofbizUrl>/createProductPromoProduct</@ofbizUrl>">
                                    <input type="hidden" name="productPromoId" value="${productPromoId}">
                                    <input type="hidden" name="productPromoRuleId" value="${productPromoAction.productPromoRuleId}">
                                    <input type="hidden" name="productPromoActionSeqId" value="${productPromoAction.productPromoActionSeqId}">
                                    <input type="hidden" name="productPromoCondSeqId" value="_NA_">
                                    Product ID: <input type="text" size="20" maxlength="20" name="productId" value="" class="inputBox"/>
                                    <select name="productPromoApplEnumId" class="selectBox">
                                        <#list productPromoApplEnums as productPromoApplEnum>
                                            <option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.description}</option>
                                        </#list>
                                    </select>
                                    <input type="submit" value="${uiLabelMap.CommonAdd}" style="font-size: x-small;">
                                </form>
                            </div>
                        </td>
                        <td align="center">
                        <a href="<@ofbizUrl>/deleteProductPromoAction?productPromoId=${(productPromoAction.productPromoId)?if_exists}&productPromoRuleId=${(productPromoAction.productPromoRuleId)?if_exists}&productPromoActionSeqId=${(productPromoAction.productPromoActionSeqId)?if_exists}</@ofbizUrl>" class="buttontext">
                        [${uiLabelMap.CommonDelete}]</a>
                        </td>
                    </tr>
                </#list>
                <tr>
                    <td align="left" colspan="3">
                        <div class="tabletext">
                        <form method="POST" action="<@ofbizUrl>/createProductPromoAction</@ofbizUrl>">
                            <input type="hidden" name="productPromoId" value="${(productPromoRule.productPromoId)?if_exists}">
                            <input type="hidden" name="productPromoRuleId" value="${(productPromoRule.productPromoRuleId)?if_exists}">
                            <span class="tabletext"><b>${uiLabelMap.CommonNew}:</b>&nbsp;</span>
                            <#-- <input type=text size="5" name="productPromoActionSeqId" value="${maxActionSeqId?if_exists}" class="inputBox"> -->
                            <select name="productPromoActionEnumId" size=1 class="selectBox">
                                <#list productPromoActionEnums as productPromoActionEnum>
                                <option value="${(productPromoActionEnum.enumId)?if_exists}">${(productPromoActionEnum.description)?if_exists}</option>
                                </#list>
                            </select>
                            <input type="hidden" name="orderAdjustmentTypeId" value="PROMOTION_ADJUSTMENT">
                            <#-- <select name="orderAdjustmentTypeId" size=1 class="selectBox">
                                <#list orderAdjustmentTypes as orderAdjustmentType>
                                <option value="${(orderAdjustmentType.orderAdjustmentTypeId)?if_exists}">${(orderAdjustmentType.description)?if_exists}</option>
                                </#list>
                            </select> -->
                            Quantity:&nbsp;<input type=text size="5" name="quantity" class="inputBox">
                            Amount:&nbsp;<input type=text size="5" name="amount" class="inputBox">
                            Item:&nbsp;<input type=text size="15" name="productId" class="inputBox">
                            Party:&nbsp;<input type=text size="10" name="partyId" class="inputBox">
                            <input type=submit value="${uiLabelMap.CommonCreate}" style="font-size: x-small;">
                        </form>
                        </div>
                    </td>
                </tr>
                </table>
            </td>
        </tr>
        </#list>
        </table>
        <div class="tabletext"><b>${uiLabelMap.ProductNoteOnItemId} :</b> ${uiLabelMap.ProductItemIdGiftPurchaseFreeShipping}</div>
        <div class="tabletext"><b>${uiLabelMap.ProductNoteOnPartyId} :</b> ${uiLabelMap.ProductPartyFreeShipping}</div>

        <br>

        <div class="head3">${uiLabelMap.ProductAddPromoRule}:</div>
        <div class="tabletext">
            <form method="POST" action="<@ofbizUrl>/createProductPromoRule</@ofbizUrl>" style="margin: 0;">
                <input type="hidden" name="productPromoId" value="${productPromoId?if_exists}">
                ${uiLabelMap.ProductName} : <input type=text size="30" name="ruleName" class="inputBox">
                <input type="submit" value="${uiLabelMap.CommonAdd}">
            </form>
        </div>

        <br/>

        <#-- ======================= Categories ======================== -->
        <div class="head3">Promotion Categories:</div>
        <#list promoProductPromoCategories as promoProductPromoCategory>
            <#assign promoProductCategory = promoProductPromoCategory.getRelatedOneCache("ProductCategory")>
            <#assign promoApplEnumeration = promoProductPromoCategory.getRelatedOneCache("ApplEnumeration")>
            <div class="tabletext">
                ${(promoProductCategory.description)?if_exists} [${promoProductPromoCategory.productCategoryId}]
                - ${(promoApplEnumeration.description)?default(promoProductPromoCategory.productPromoApplEnumId)}
                - SubCats? ${promoProductPromoCategory.includeSubCategories?default("N")}
                - And Group: ${promoProductPromoCategory.andGroupId}
                <a href="<@ofbizUrl>/deleteProductPromoCategory?productPromoId=${(promoProductPromoCategory.productPromoId)?if_exists}&productPromoRuleId=${(promoProductPromoCategory.productPromoRuleId)?if_exists}&productPromoActionSeqId=${(promoProductPromoCategory.productPromoActionSeqId)?if_exists}&productPromoCondSeqId=${(promoProductPromoCategory.productPromoCondSeqId)?if_exists}&productCategoryId=${(promoProductPromoCategory.productCategoryId)?if_exists}&andGroupId=${(promoProductPromoCategory.andGroupId)?if_exists}</@ofbizUrl>" class="buttontext">
                [${uiLabelMap.CommonDelete}]</a>
            </div>
        </#list>
        <div class="tabletext">
            <form method="POST" action="<@ofbizUrl>/createProductPromoCategory</@ofbizUrl>">
                <input type="hidden" name="productPromoId" value="${productPromoId}">
                <input type="hidden" name="productPromoRuleId" value="_NA_">
                <input type="hidden" name="productPromoActionSeqId" value="_NA_">
                <input type="hidden" name="productPromoCondSeqId" value="_NA_">
                <select name="productCategoryId" class="selectBox">
                    <#list productCategories as productCategory>
                        <option value="${productCategory.productCategoryId}">${productCategory.description}</option>
                    </#list>
                </select>
                <select name="productPromoApplEnumId" class="selectBox">
                    <#list productPromoApplEnums as productPromoApplEnum>
                        <option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.description}</option>
                    </#list>
                </select>
                <select name="includeSubCategories" class="selectBox">
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                </select>
                And Group: <input type="text" size="10" maxlength="20" name="andGroupId" value="_NA_" class="inputBox"/>*
                <input type="submit" value="${uiLabelMap.CommonAdd}" style="font-size: x-small;">
            </form>
        </div>
        <br/>
        <#-- ======================= Products ======================== -->
        <div class="head3">${uiLabelMap.ProductPromotionProducts} :</div>
        <#list promoProductPromoProducts as promoProductPromoProduct>
            <#assign promoProduct = promoProductPromoProduct.getRelatedOneCache("Product")?if_exists>
            <#assign promoApplEnumeration = promoProductPromoProduct.getRelatedOneCache("ApplEnumeration")>
            <div class="tabletext">
                ${(promoProduct.internalName)?if_exists} [${promoProductPromoProduct.productId}]
                - ${(promoApplEnumeration.description)?default(promoProductPromoProduct.productPromoApplEnumId)}
                <a href="<@ofbizUrl>/deleteProductPromoProduct?productPromoId=${(promoProductPromoProduct.productPromoId)?if_exists}&productPromoRuleId=${(promoProductPromoProduct.productPromoRuleId)?if_exists}&productPromoActionSeqId=${(promoProductPromoProduct.productPromoActionSeqId)?if_exists}&productPromoCondSeqId=${(promoProductPromoProduct.productPromoCondSeqId)?if_exists}&productId=${(promoProductPromoProduct.productId)?if_exists}</@ofbizUrl>" class="buttontext">
                [${uiLabelMap.CommonDelete}]</a>
            </div>
        </#list>
        <div class="tabletext">
            <form method="POST" action="<@ofbizUrl>/createProductPromoProduct</@ofbizUrl>">
                <input type="hidden" name="productPromoId" value="${productPromoId}">
                <input type="hidden" name="productPromoRuleId" value="_NA_">
                <input type="hidden" name="productPromoActionSeqId" value="_NA_">
                <input type="hidden" name="productPromoCondSeqId" value="_NA_">
                Product ID: <input type="text" size="20" maxlength="20" name="productId" value="" class="inputBox"/>*
                <select name="productPromoApplEnumId" class="selectBox">
                    <#list productPromoApplEnums as productPromoApplEnum>
                        <option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.description}</option>
                    </#list>
                </select>
                <input type="submit" value="${uiLabelMap.CommonAdd}" style="font-size: x-small;">
            </form>
        </div>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductViewPermissionError}</h3>
</#if>
