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
 *@version    $Revision$
 *@since      2.2
-->

<#if hasPermission>

<div class="head1">Product Feature Categories</div>

<br>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>ID</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>Parent&nbsp;Category</b></div></td>
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
    <td><INPUT type=submit value='Update'></td>
    <td><a href='<@ofbizUrl>/EditFeatureCategoryFeatures?productFeatureCategoryId=${productFeatureCategory.productFeatureCategoryId}</@ofbizUrl>' class="buttontext">[Edit]</a></td>
    </FORM>
  </tr>
</#list>
</table>
<br>

<form method="POST" action="<@ofbizUrl>/CreateFeatureCategory</@ofbizUrl>" style='margin: 0;'>
  <div class='head2'>Create a Product Feature Category:</div>
  <br>
  <table>
    <tr>
      <td><div class='tabletext'>Description:</div></td>
      <td><input type=text class='inputBox' size='30' name='description' value=''></td>
    </tr>
    <tr>
      <td><div class='tabletext'>Parent Category:</div></td>
      <td><select name='parentCategoryId' size=1 class='selectbox'>
        <option value=''>&nbsp;</option>
        <#list productFeatureCategories as productFeatureCategory>
          <option value='${productFeatureCategory.productFeatureCategoryId}'>${productFeatureCategory.description?if_exists} [${productFeatureCategory.productFeatureCategoryId}]</option>
        </#list>
      </select></td>
    </tr>
    <tr>
      <td colspan='2'><input type="submit" value="Create"></td>
    </tr>
  </table>
</form>
<br>

<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>

