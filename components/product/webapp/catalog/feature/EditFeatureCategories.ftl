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
 *@version    $Revision: 1.5 $
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>

${pages.get("/feature/FeatureTabBar.ftl")}
<div class="head1">${uiLabelMap.ProductProductFeatureCategories}</div>

<br>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>${uiLabelMap.CommonId}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.CommonDescription}</b></div></td>
    <td><div class="tabletext"><b>${uiLabelMap.ProductParentCategory}</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>


<#list productFeatureCategories as productFeatureCategory>

  <tr valign="middle">
    <FORM method=POST action='<@ofbizUrl>/UpdateFeatureCategory</@ofbizUrl>'>
    <input type=hidden name="productFeatureCategoryId" value="${productFeatureCategory.productFeatureCategoryId}">
    <td><a href='<@ofbizUrl>/EditFeatureCategoryFeatures?productFeatureCategoryId=${productFeatureCategory.productFeatureCategoryId}</@ofbizUrl>' class="buttontext">${productFeatureCategory.productFeatureCategoryId}</a></td>
    <td><input type=text class='inputBox' size='30' name="description" value="${productFeatureCategory.description?if_exists}"></td>
    <td>
      <select name='parentCategoryId' size=1 class='selectBox'>
        <#assign curProdFeatCat = productFeatureCategory.getRelatedOne("ParentProductFeatureCategory")?if_exists>
        <#if curProdFeatCat?has_content>
          <option value='${curProdFeatCat.productFeatureCategoryId}'>${curProdFeatCat.description?if_exists}</option>
        </#if>
        <option value=''>&nbsp;</option>
          <#list productFeatureCategories as dropDownProductFeatureCategory>
            <option value='${dropDownProductFeatureCategory.productFeatureCategoryId}'>${dropDownProductFeatureCategory.description?if_exists}</option>
          </#list>
      </select>
    </td>
    <td><INPUT type="submit" value="${uiLabelMap.CommonUpdate}"></td>
    <td><a href='<@ofbizUrl>/EditFeatureCategoryFeatures?productFeatureCategoryId=${productFeatureCategory.productFeatureCategoryId}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonEdit}]</a></td>
    </FORM>
  </tr>
</#list>
</table>
<br>

<form method="POST" action="<@ofbizUrl>/CreateFeatureCategory</@ofbizUrl>" style='margin: 0;'>
  <div class='head2'>${uiLabelMap.ProductCreateAProductFeatureCategory}:</div>
  <br>
  <table>
    <tr>
      <td><div class='tabletext'>${uiLabelMap.CommonDescription}:</div></td>
      <td><input type=text class='inputBox' size='30' name='description' value=''></td>
    </tr>
    <tr>
      <td><div class='tabletext'>${uiLabelMap.ProductParentCategory}:</div></td>
      <td><select name='parentCategoryId' size=1 class='selectbox'>
        <option value=''>&nbsp;</option>
        <#list productFeatureCategories as productFeatureCategory>
          <option value='${productFeatureCategory.productFeatureCategoryId}'>${productFeatureCategory.description?if_exists} [${productFeatureCategory.productFeatureCategoryId}]</option>
        </#list>
      </select></td>
    </tr>
    <tr>
      <td colspan='2'><input type="submit" value="${uiLabelMap.CommonCreate}"></td>
    </tr>
  </table>
</form>
<br>

<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>

