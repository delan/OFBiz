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
 *@version    $Revision$
 *@since      2.1
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<div class="head1">${uiLabelMap.ProductAdvancedSearchinCategory}</div>
<br/>
<form name="advtokeywordsearchform" method="post" action="<@ofbizUrl>/keywordsearch</@ofbizUrl>" style="margin: 0;">
  <input type="hidden" name="VIEW_SIZE" value="10">
  <table border="0" wdith="100%">
    <#if searchCategory?has_content>
        <input type="hidden" name="SEARCH_CATEGORY_ID" value="${searchCategoryId?if_exists}">
        <tr>
          <td align="right" valign="middle">
            <div class="tabletext">Category:</div>
          </td>
          <td valign="middle">
            <div class="tabletext">
              <b>"${(searchCategory.description)?if_exists}"</b> Include sub-categories?
              Yes<input type="radio" name="SEARCH_SUB_CATEGORIES" value="Y" checked="checked">
              No<input type="radio" name="SEARCH_SUB_CATEGORIES" value="N">
            </div>
          </td>
        </tr>
    </#if>
    <tr>
      <td align="right" valign="middle">
        <div class="tabletext">${uiLabelMap.ProductKeywords}:</div>
      </td>
      <td valign="middle">
        <div class="tabletext">
          <input type="text" class="inputBox" name="SEARCH_STRING" size="40" value="${requestParameters.SEARCH_STRING?if_exists}">&nbsp;
          ${uiLabelMap.CommonAny}<input type="radio" name="SEARCH_OPERATOR" value="OR" <#if searchOperator == "OR">checked="checked"</#if>>
          ${uiLabelMap.CommonAll}<input type="radio" name="SEARCH_OPERATOR" value="AND" <#if searchOperator == "AND">checked="checked"</#if>>
        </div>
      </td>
    </tr>
    <#list productFeaturesByTypeMap.keySet() as productFeatureTypeId>
      <#assign findPftMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureTypeId", productFeatureTypeId)>
      <#assign productFeatureType = delegator.findByPrimaryKeyCache("ProductFeatureType", findPftMap)>
      <#assign productFeatures = productFeaturesByTypeMap[productFeatureTypeId]>
      <tr>
        <td align="right" valign="middle">
          <div class="tabletext">${(productFeatureType.description)?if_exists}:</div>
        </td>
        <td valign="middle">
          <div class="tabletext">
            <select class="selectBox" name="pft_${productFeatureTypeId}">
              <option value="">- ${uiLabelMap.CommonAny} -</option>
              <#list productFeatures as productFeature>
              <option value="${productFeature.productFeatureId}">${productFeature.description?default(productFeature.productFeatureId)}</option>
              </#list>
            </select>
          </div>
        </td>
      </tr>
    </#list>
    <tr>
      <td align="right" valign="middle">
        <div class="tabletext">${uiLabelMap.ProductSortOrder}:</div>
      </td>
      <td valign="middle">
        <div class="tabletext">
          <select name="sortOrder" class="selectBox">
            <option value="SortKeywordRelevancy">${uiLabelMap.ProductKeywordRelevency}</option>
            <option value="SortProductField:productName">${uiLabelMap.ProductProductName}</option>
          </select>
        </div>
      </td>
    </tr>
    <#if searchConstraintStrings?has_content>
      <tr>
        <td align="right" valign="top">
          <div class="tabletext">${uiLabelMap.ProductLastSearch}:</div>
        </td>
        <td valign="top">
            <#list searchConstraintStrings as searchConstraintString>
                <div class="tabletext">&nbsp;-&nbsp;${searchConstraintString}</div>
            </#list>
            <div class="tabletext">${uiLabelMap.ProductSortedBy}: ${searchSortOrderString}</div>
            <div class="tabletext">
              ${uiLabelMap.ProductNewSearch}<input type="radio" name="clearSearch" value="Y" checked="checked">
              ${uiLabelMap.ProductRefineSearch}<input type="radio" name="clearSearch" value="N">
            </div>
        </td>
      </tr>
    </#if>
    <tr>
      <td>
        <div class="tabletext">
          <a href="javascript:document.advtokeywordsearchform.submit()" class="buttontext">${uiLabelMap.CommonFind}</a>
        </div>
      </td>
    </tr>
  </table>
</form>

