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
 *@version    $Revision: 1.5 $
 *@since      2.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if productCategory?exists>
<table border="0" width="100%" cellpadding="3">
  <tr>
    <td colspan="2">
      <div class="head1">
        ${productCategory.description?if_exists}
        <#if hasQuantities?exists>
          <form method="POST" action="<@ofbizUrl>/addCategoryDefaults<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="thecategoryform" style='margin: 0;'>
            <input type='hidden' name='add_category_id' value='${productCategory.productCategoryId}'>            
            <#if requestParameters.product_id?exists><input type='hidden' name='product_id' value='${requestParameters.product_id}'></#if>
            <#if requestParameters.category_id?exists><input type='hidden' name='category_id' value='${requestParameters.category_id}'></#if>
            <#if requestParameters.VIEW_INDEX?exists><input type='hidden' name='VIEW_INDEX' value='${requestParameters.VIEW_INDEX}'></#if>
            <#if requestParameters.SEARCH_STRING?exists><input type='hidden' name='SEARCH_STRING' value='${requestParameters.SEARCH_STRING}'></#if>
            <#if requestParameters.SEARCH_CATEGORY_ID?exists><input type='hidden' name='SEARCH_CATEGORY_ID' value='${requestParameters.SEARCH_CATEGORY_ID}'></#if>                                     
            <a href="javascript:document.thecategoryform.submit()" class="buttontext"><nobr>[${uiLabelMap.ProductAddProductsUsingDefaultQuantities}]</nobr></a>
          </form>
        </#if>
        <a href="<@ofbizUrl>/advancedsearch?SEARCH_CATEGORY_ID=${productCategory.productCategoryId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductSearchinCategory}</a>
      </div>
    </td>
  </tr>
  <#if productCategory.categoryImageUrl?exists || productCategory.longDescription?exists>
  <tr>
    <td align="left" valign="top" width="0">
      <div class="tabletext">
        <#if productCategory.categoryImageUrl?exists>
          <img src='<@ofbizContentUrl>${productCategory.categoryImageUrl}</@ofbizContentUrl>' vspace='5' hspace='5' border='1' height='100' align='left'>
        </#if>
        <#if productCategory.longDescription?exists>
          ${productCategory.longDescription}
        </#if>
      </div>
    </td>
  </tr>
  </#if>
</table>
</#if>

<#if productCategoryMembers?exists && 0 < productCategoryMembers.size()>
<table border="0" width="100%" cellpadding="2">
  <tr>
    <td align=right>
      <#-- Start Page Select Drop-Down -->
      <#assign viewIndexMax = Static["java.lang.Math"].ceil(listSize?double / viewSize?double)>
      <select name="pageSelect" class="selectBox" onChange="window.location=this[this.selectedIndex].value;">
        <option value="#">Page ${viewIndex?int + 1} of ${viewIndexMax}</option>
        <#list 1..viewIndexMax as curViewNum>
          <option value="<@ofbizUrl>/category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${curViewNum?int - 1}</@ofbizUrl>">Go to Page ${curViewNum}</option>
        </#list>
      </select>
      <#-- End Page Select Drop-Down -->
      <b>
        <#if 0 < viewIndex?int>
          <a href="<@ofbizUrl>/category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int - 1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
        </#if>
        <#if 0 < listSize?int>
          <span class="tabletext">${lowIndex} - ${highIndex} of ${listSize}</span>
        </#if>
        <#if highIndex?int < listSize?int>
          | <a href="<@ofbizUrl>/category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int + 1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
        </#if>
      </b>
    </td>
  </tr>
</table>

<center>
  <table width='100%' border='0' cellpadding='0' cellspacing='0'>    
    <#assign startIndex = viewSize * viewIndex>
    <#if highIndex < listSize>
      <#assign endIndex = highIndex - 1>
    <#else>
      <#assign endIndex = listSize - 1>
    </#if>
    
    <#list productCategoryMembers[startIndex..endIndex] as productCategoryMember>              
      <tr><td><hr class='sepbar'></td></tr>
      <tr>
        <td>
          ${setRequestAttribute("optProductId", productCategoryMember.productId)} 
          ${setRequestAttribute("productCategoryMember", productCategoryMember)} 
          ${setRequestAttribute("listIndex", productCategoryMember_index)}         
          ${pages.get("/catalog/productsummary.ftl")}
        </td>
      </tr>
    </#list>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
  </table>
</center>

<table border="0" width="100%" cellpadding="2">
  <tr>
    <td align=right>
      <#-- Start Page Select Drop-Down -->
      <#assign viewIndexMax = Static["java.lang.Math"].ceil(listSize?double / viewSize?double)>
      <select name="pageSelect" class="selectBox" onChange="window.location=this[this.selectedIndex].value;">
        <option value="#">Page ${viewIndex?int + 1} of ${viewIndexMax}</option>
        <#list 1..viewIndexMax as curViewNum>
          <option value="<@ofbizUrl>/category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${curViewNum?int - 1}</@ofbizUrl>">Go to Page ${curViewNum}</option>
        </#list>
      </select>
      <#-- End Page Select Drop-Down -->
      <b>
        <#if 0 < viewIndex?int>
          <a href="<@ofbizUrl>/category?category_id=${productCategoryId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex?int - 1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
        </#if>
        <#if 0 < listSize?int>
          <span class="tabletext">${lowIndex} - ${highIndex} of ${listSize}</span>
        </#if>
        <#if highIndex?int < listSize?int>
          | <a href="<@ofbizUrl>/category?category_id=${productCategoryId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex?int + 1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
        </#if>
      </b>
    </td>
  </tr>
</table>

<#else>
<table border="0" width="100%" cellpadding="2">
  <tr>
    <td colspan="2"><hr class='sepbar'></td>
  </tr>
  <tr>
    <td>
      <div class='tabletext'>${uiLabelMap.ProductNoProductsInThisCategory}.</DIV>
    </td>
  </tr>
</table>
</#if>

