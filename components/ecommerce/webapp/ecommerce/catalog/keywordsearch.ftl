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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.4 $
 *@since      2.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<div class='head1'>
    ${uiLabelMap.CommonSearchResultfor} "${requestAttributes.keywordString?if_exists}"
   ${uiLabelMap.CommonWhere} <#if searchOperator?default("OR") == "OR">${uiLabelMap.ProductAnyKeyword}<#else>${uiLabelMap.ProductAnyKeyword}</#if> ${uiLabelMap.CommonMatched}

  <#assign featureIdByType = requestAttributes.featureIdByType>
  <#if requestAttributes.featureIdByType?has_content>
  	${uiLabelMap.CommonAnd} ${uiLabelMap.CommonWhere} 
    <#list featureIdByType.keySet() as productFeatureTypeId>
      <#assign findPftMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureTypeId", productFeatureTypeId)>
      <#assign productFeatureType = delegator.findByPrimaryKeyCache("ProductFeatureType", findPftMap)>
      <#assign findProdFeatMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureId", featureIdByType[productFeatureTypeId])>
      <#assign productFeature = delegator.findByPrimaryKeyCache("ProductFeature", findProdFeatMap)>
      ${productFeatureType.description} = ${productFeature.description}
      <#if productFeatureTypeId_has_next>, ${uiLabelMap.CommonAnd} </#if>
    </#list>
  </#if>
  <#if searchCategory?exists>
   ${uiLabelMap.CommonInThe} ${searchCategory.description} ${uiLabelMap.ProductCategory}
  </#if>
</div>

<#if !requestAttributes.searchProductList?has_content>
  <br><div class='head2'>&nbsp;${uiLabelMap.ProductNoResultsFound}.</div>
</#if>

<#if requestAttributes.searchProductList?has_content>
<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <#if 0 < requestAttributes.viewIndex?int>
          <a href="<@ofbizUrl>/keywordsearch/${prevStr}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
        </#if>
        <#if 0 < requestAttributes.listSize?int>
          <span class="tabletext">${requestAttributes.lowIndex} - ${requestAttributes.highIndex} of ${requestAttributes.listSize}</span>
        </#if>
        <#if requestAttributes.highIndex?int < requestAttributes.listSize?int>      
          | <a href="<@ofbizUrl>/keywordsearch/${nextStr}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
        </#if>
        </b>
      </td>
    </tr>
</table>
</#if>

<#if requestAttributes.searchProductList?has_content>
<center>
  <table width='100%' cellpadding='0' cellspacing='0'>
    <#assign listIndex = requestAttributes.lowIndex>
    <#list requestAttributes.searchProductList as product>
    ${setRequestAttribute("optProductId", product.productId)}
    ${setRequestAttribute("listIndex", listIndex)}
      <tr><td colspan="2"><hr class='sepbar'></td></tr>
      <tr>
        <td>
          ${pages.get("/catalog/productsummary.ftl")}
        </td>
      </tr>
      <#assign listIndex = listIndex + 1>
    </#list>
  </table>
</center>
</#if>

<#if requestAttributes.searchProductList?has_content>
<table border="0" width="100%" cellpadding="2">
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td align=right>
        <b>
        <#if 0 < requestAttributes.viewIndex?int>
          <a href="<@ofbizUrl>/keywordsearch/${prevStr}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
        </#if>
        <#if 0 < requestAttributes.listSize?int>
          <span class="tabletext">${requestAttributes.lowIndex} - ${requestAttributes.highIndex} of ${requestAttributes.listSize}</span>
        </#if>
        <#if requestAttributes.highIndex?int < requestAttributes.listSize?int>      
          | <a href="<@ofbizUrl>/keywordsearch/${nextStr}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
        </#if>
        </b>
      </td>
    </tr>
</table>
</#if>

