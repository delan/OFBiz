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
 *@version    $Revision: 1.1 $
 *@since      2.1
-->

<div class='head1'>Advanced Search in Category: ${searchCategory.description}</div>
<br>
<form name="advtokeywordsearchform" method="POST" action="<@ofbizUrl>/keywordsearch</@ofbizUrl>" style='margin: 0;'>
  <input type='hidden' name="VIEW_SIZE" value="10">
  <input type='hidden' name="SEARCH_CATEGORY_ID" value="${searchCategoryId}">
  <table border="0" wdith="100%">
    <tr>
      <td>
        <div class='tabletext'>Keywords:</div>
      </td>
      <td>
        <div class='tabletext'>
          <input type='text' class='inputBox' name="SEARCH_STRING" size="40" value="${requestParameters.SEARCH_STRING?if_exists}">&nbsp;
          Any<input type='RADIO' name='SEARCH_OPERATOR' value='OR' <#if searchOperator == "OR">checked</#if>>
          All<input type='RADIO' name='SEARCH_OPERATOR' value='AND' <#if searchOperator == "AND">checked</#if>>
        </div>
      </td>
    </tr>   
    <#list productFeaturesByTypeMap.keySet() as productFeatureTypeId>
      <#assign findPftMap = Static["org.ofbiz.core.util.UtilMisc"].toMap("productFeatureTypeId", productFeatureTypeId)>
      <#assign productFeatureType = delegator.findByPrimaryKeyCache("ProductFeatureType", findPftMap)>
      <#assign productFeatures = productFeaturesByTypeMap[productFeatureTypeId]>
      <tr>
        <td>
          <div class='tabletext'>${productFeatureType.description}:</div>
        </td>
        <td>
          <div class='tabletext'>
            <select class="selectBox" name="pft_${productFeatureTypeId}">
              <option value="">- any -</option>
              <#list productFeatures as productFeature>
              <option value="${productFeature.productFeatureId}">${productFeature.description}</option>
              </#list>
            </select>
          </div>
        </td>
      </tr>
    </#list>
    <tr>
      <td>
        <div class='tabletext'>
          <a href="javascript:document.advtokeywordsearchform.submit()" class="buttontext">Find</a>
        </div>
      </td>
    </tr>
  </table>
</form>

