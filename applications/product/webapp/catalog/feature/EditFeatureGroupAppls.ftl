<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Rev$
 *@since      3.0
-->

<div class="head1">${uiLabelMap.ProductEditFeatureGroupAppls}</div>

<br/>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tableheadtext">${uiLabelMap.CommonId}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.ProductFeature}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.CommonType}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.ProductFeatureCategory}</div></td>
    <td><div class="tableheadtext">&nbsp;</div></td>
  </tr>

  <#list featureGroupAppls as groupAppl>
    <#assign feature = groupAppl.getRelatedOne("ProductFeature")>
    <#assign type = feature.getRelatedOne("ProductFeatureType")>
    <#assign category = feature.getRelatedOne("ProductFeatureCategory")>
    <tr>
      <td><div class="tabletext">${feature.productFeatureId}</div></td>
      <td><div class="tabletext">${feature.description?if_exists}</div></td>
      <td><div class="tabletext">${type.description}</div></td>
      <td><div class="tabletext">${category.description} (${category.productFeatureCategoryId})</div></td>
      <td><a href="<@ofbizUrl>RemoveProductFeatureGroupAppl?productFeatureGroupId=${groupAppl.productFeatureGroupId}&productFeatureId=${groupAppl.productFeatureId}&fromDate=${groupAppl.fromDate?string}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonRemove}]</a>
    </tr>
  </#list>
</table>

<br/>
<form method="post" action="<@ofbizUrl>CreateProductFeatureGroupAppl</@ofbizUrl>" style='margin: 0;'>
  <input type="hidden" name="productFeatureGroupId" value="${requestParameters.productFeatureGroupId}">
  <div class='head2'>${uiLabelMap.ProductQuickApplyFeature}:</div>
  <br/>
  <table>
    <tr>
      <td><div class='tabletext'>${uiLabelMap.ProductFeatureId}:</div></td>
      <td><input type="text" class='inputBox' size='30' name='productFeatureId' value=''></td>
    </tr>
    <tr>
      <td colspan='2'><input type="submit" value="${uiLabelMap.CommonCreate}"></td>
    </tr>
  </table>
</form>

<br/>
<#if !productFeatures?has_content>
  <form method="post" action="<@ofbizUrl>EditFeatureGroupAppls</@ofbizUrl>" style='margin: 0;'>
    <input type="hidden" name="productFeatureGroupId" value="${requestParameters.productFeatureGroupId}">
    <div class='head2'>${uiLabelMap.ProductApplyFeaturesFromCategory}:</div>
    <br/>
    <table>
      <tr>
        <td><div class='tabletext'>${uiLabelMap.ProductFeatureCategory}:</div></td>
        <td>
          <select class="selectBox" name="productFeatureCategoryId">
            <#list productFeatureCategories as category>
              <option value="${category.productFeatureCategoryId}">${category.description}</option>
            </#list>
          </select>
        </td>
      </tr>
      <tr>
        <td colspan='2'><input type="submit" value="${uiLabelMap.CommonContinue}"></td>
      </tr>
    </table>
  </form>
<#else>


  <div class='head2'${uiLabelMap.ProductApplyFeaturesFromCategory}:</div>
  <br/>
  <table border="1" cellpadding='2' cellspacing='0'>
    <tr>
      <td><div class='tableheadtext'>${uiLabelMap.CommonId}</div></td>
      <td><div class='tableheadtext'>${uiLabelMap.CommonType}</div></td>
      <td><div class='tableheadtext'>${uiLabelMap.CommonDescription}</div></td>
      <td><div class='tableheadtext'>${uiLabelMap.CommonIdCode}</div></td>
      <td><div class='tableheadtext'>${uiLabelMap.ProductAbbrev}</div></td>
      <td><div class='tableheadtext'>&nbsp;</div></td>
    </tr>

    <#list productFeatures as feature>
      <#assign type = feature.getRelatedOne("ProductFeatureType")>
      <form method="post" action="<@ofbizUrl>CreateProductFeatureGroupAppl</@ofbizUrl>" style='margin: 0;'>
        <input type="hidden" name="productFeatureGroupId" value="${requestParameters.productFeatureGroupId}">
        <input type="hidden" name="productFeatureCategoryId" value="${requestParameters.productFeatureCategoryId}">
        <input type="hidden" name="productFeatureId" value="${feature.productFeatureId}">
        <tr>
          <td><div class="tabletext">${feature.productFeatureId}</div></td>
          <td><div class="tabletext">${type.description}</div></td>
          <td><div class="tabletext">${feature.description?if_exists}</div></td>
          <td><div class="tabletext">${feature.idCode?if_exists}</div></td>
          <td><div class="tabletext">${feature.abbrev?if_exists}</div></td>
          <td><input type="submit" value="${uiLabelMap.CommonApply}">
        </tr>
      </form>
    </#list>
  </table>
  <a href="<@ofbizUrl>EditFeatureGroupAppls?productFeatureGroupId=${requestParameters.productFeatureGroupId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductFeatureFinishedWithCategories}]</a>
</#if>
