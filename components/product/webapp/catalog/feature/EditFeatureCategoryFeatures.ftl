<#--
 *  Copyright (c) 2003-2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Revision: 1.11 $
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>

${pages.get("/feature/FeatureTabBar.ftl")}
<div class="head1">${uiLabelMap.ProductEditFeaturesForFeatureCategory} "${(curProductFeatureCategory.description)?if_exists}"</div>
<a href="<@ofbizUrl>/EditFeature?productFeatureCategoryId=${productFeatureCategoryId?if_exists}</@ofbizUrl>" class="buttontext">[Create New Feature]</a>
<#if productId?has_content>
    <div class="head2">${uiLabelMap.ProductAndApplyFeaturesToProductWithId} "${productId}"</div>
    <div>
        <a href="<@ofbizUrl>/EditProduct?productId=${productId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductReturnToEditProduct}]</a>
        <a href="<@ofbizUrl>/EditProductFeatures?productId=${productId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductReturnToEditProductFeatures}]</a>
    </div>
</#if>

<br>
<p class="head2">${uiLabelMap.ProductProductFeatureMaintenance}</p>
<#if (listSize > 0)>
    <#if productId?has_content>
      <#assign productString = "&productId=" + productId>
    </#if>
    <table border="0" width="100%" cellpadding="2">
        <tr>
        <td align=right>
            <span class="tabletext">
            <b>
            <#if (viewIndex > 0)>
            <a href="<@ofbizUrl>/EditFeatureCategoryFeatures?productFeatureCategoryId=${productFeatureCategoryId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}${productString?if_exists}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
            </#if>
            ${lowIndex+1} - ${highIndex} of ${listSize}
            <#if (listSize > highIndex)>
            | <a href="<@ofbizUrl>/EditFeatureCategoryFeatures?productFeatureCategoryId=${productFeatureCategoryId?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}${productString?if_exists}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
            </#if>
            </b>
            </span>
        </td>
        </tr>
    </table>
</#if>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr class='viewOneTR1'>
    <td><div class="tabletext"><b>${uiLabelMap.CommonId}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.CommonDescription}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.ProductFeatureType}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.ProductFeatureCategory}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.ProductUnitOfMeasureId}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.ProductQuantity}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.ProductAmount}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.ProductIdSeqNum}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.ProductIdCode}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.ProductAbbrev}</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
    <#if productId?has_content>
      </tr>
      <tr class='viewOneTR2'>
        <td><div class="tabletext">&nbsp;</div></td>
        <td><div class="tabletext"><b>${uiLabelMap.ProductApplType}</b></div></td>
        <td><div class="tabletext"><b>${uiLabelMap.CommonFromDate}</b></div></td>
        <td><div class="tabletext"><b>${uiLabelMap.CommonThruDate}</b></div></td>
        <td><div class="tabletext"><b>${uiLabelMap.ProductAmount}</b></div></td>
        <td><div class="tabletext"><b>${uiLabelMap.CommonSequence}</b></div></td>
        <td colspan="5"><div class="tabletext">&nbsp;</div></td>
    </#if>
  </tr>
<#if (listSize > 0)>
<#list productFeatures as productFeature>
  <#assign curProductFeatureType = productFeature.getRelatedOneCache("ProductFeatureType")>
  <tr valign="middle" class='viewOneTR1'>
    <form method='POST' action='<@ofbizUrl>/UpdateProductFeatureInCategory</@ofbizUrl>'>
        <#if productId?has_content><input type="hidden" name="productId" value="${productId}"></#if>
        <input type="hidden" name="productFeatureId" value="${productFeature.productFeatureId}">
      <td><a href="<@ofbizUrl>/EditFeature?productFeatureId=${productFeature.productFeatureId}</@ofbizUrl>" class="buttontext">${productFeature.productFeatureId}</a></td>
      <td><input type="text" class='inputBox' size='15' name="description" value="${productFeature.description}"></td>
      <td><select name='productFeatureTypeId' size=1 class='selectBox'>
        <#if productFeature.productFeatureTypeId?has_content>
          <option value='${productFeature.productFeatureTypeId}'><#if curProductFeatureType?exists>${curProductFeatureType.description}<#else> [${productFeature.productFeatureTypeId}]</#if></option>
          <option value='${productFeature.productFeatureTypeId}'>---</option>
        </#if>
        <#list productFeatureTypes as productFeatureType>
          <option value='${productFeatureType.productFeatureTypeId}'>${productFeatureType.description}</option>
        </#list>
      </select></td>
      <td><select name='productFeatureCategoryId' size=1 class='selectBox'>
        <#if productFeature.productFeatureCategoryId?has_content>
          <#assign curProdFeatCat = productFeature.getRelatedOne("ProductFeatureCategory")>
          <option value='${productFeature.productFeatureCategoryId}'>${(curProdFeatCat.description)?if_exists} [${productFeature.productFeatureCategoryId}]</option>
          <option value='${productFeature.productFeatureCategoryId}'>---</option>
        </#if>
        <#list productFeatureCategories as productFeatureCategory>
          <option value='${productFeatureCategory.productFeatureCategoryId}'>${productFeatureCategory.description} [${productFeatureCategory.productFeatureCategoryId}]</option>
        </#list>
      </select></td>
      <td><input type=text class='inputBox' size='10' name="uomId" value="${productFeature.uomId?if_exists}"></td>
      <td><input type=text class='inputBox' size='5' name="numberSpecified" value="${productFeature.numberSpecified?if_exists}"></td>
      <td><input type=text class='inputBox' size='5' name="defaultAmount" value="${productFeature.defaultAmount?if_exists}"></td>
      <td><input type=text class='inputBox' size='5' name="defaultSequenceNum" value="${productFeature.defaultSequenceNum?if_exists}"></td>
      <td><input type=text class='inputBox' size='5' name="idCode" value="${productFeature.idCode?if_exists}"></td>
      <td><input type=text class='inputBox' size='5' name="abbrev" value="${productFeature.abbrev?if_exists}"></td>
      <td><input type=submit value='Update'></td>
    </form>
    <#if productId?has_content>
      </tr>
      <tr class='viewOneTR2'>
      <form method='POST' action='<@ofbizUrl>/ApplyFeatureToProduct</@ofbizUrl>' name='lineForm${productFeature_index}'>
        <input type=hidden name='productId' value='${productId}'>
        <input type=hidden name="productFeatureId" value="${productFeature.productFeatureId}">
        <td><div class="tabletext">&nbsp;</div></td>
        <td>
          <select name='productFeatureApplTypeId' size=1 class='selectBox'>
            <#list productFeatureApplTypes as productFeatureApplType>
              <option value='${productFeatureApplType.productFeatureApplTypeId}'>${productFeatureApplType.description}</option>
            </#list>
          </select>
        </td>
        <td><input type=text size='25' name='fromDate' class='inputBox'><a href="javascript:call_cal(document.lineForm${productFeature_index}.fromDate, '${nowTimestampString}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a></td>
        <td><input type=text size='25' name='thruDate' class='inputBox'><a href="javascript:call_cal(document.lineForm${productFeature_index}.thruDate, '${nowTimestampString}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a></td>
        <td><input type=text size='6' name='amount' class='inputBox' value='${productFeature.defaultAmount?if_exists}'></td>
        <td><input type=text size='5' name='sequenceNum' class='inputBox' value='${productFeature.defaultSequenceNum?if_exists}'></td>
        <td colspan="5" align="left"><input type=submit value='Apply'></td>
      </form>
    </#if>
  </tr>
</#list>
</#if>
</table>
<br>
<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
