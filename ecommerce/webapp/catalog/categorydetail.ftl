<#--
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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

<#-- variable setup -->
<#assign productCategory = requestAttributes.productCategory?if_exists>
<#assign productCategoryMembers = requestAttributes.productCategoryMembers?if_exists>
<#-- end variable setup -->

<#if productCategory?exists>
<table border="0" width="100%" cellpadding="3">
  <tr>
    <td colspan="2">
      <div class="head1">
        ${productCategory.description?if_exists}
        <#if requestAttributes.hasQuantities?exists>
          <form method="POST" action="<@ofbizUrl>/addCategoryDefaults<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="thecategoryform" style='margin: 0;'>
            <input type='hidden' name='add_category_id' value='${productCategory.productCategoryId}'>            
            <#if requestParameters.product_id?exists><input type='hidden' name='product_id' value='${requestParameters.product_id}'></#if>
            <#if requestParameters.category_id?exists><input type='hidden' name='category_id' value='${requestParameters.category_id}'></#if>
            <#if requestParameters.VIEW_INDEX?exists><input type='hidden' name='VIEW_INDEX' value='${requestParameters.VIEW_INDEX}'></#if>
            <#if requestParameters.SEARCH_STRING?exists><input type='hidden' name='SEARCH_STRING' value='${requestParameters.SEARCH_STRING}'></#if>
            <#if requestParameters.SEARCH_CATEGORY_ID?exists><input type='hidden' name='SEARCH_CATEGORY_ID' value='${requestParameters.SEARCH_CATEGORY_ID}'></#if>                                     
            <a href="javascript:document.thecategoryform.submit()" class="buttontext"><nobr>[Add Products in this Category to the Cart using Default Quantities]</nobr></a>
          </form>
        </#if>
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

<#if 0 < productCategoryMembers?size>
<table border="0" width="100%" cellpadding="2">
  <tr>
    <td align=right>
      <b>
        <#if 0 < requestAttributes.viewIndex?int>
          <a href="<@ofbizUrl>/category?category_id=${requestAttributes.productCategoryId}&VIEW_SIZE=${requestAttributes.viewSize}&VIEW_INDEX=${requestAttributes.viewIndex?int - 1}</@ofbizUrl>" class="buttontext">[Previous]</a> |
        </#if>
        <#if 0 < requestAttributes.listSize?int>
          <span class="tabletext">${requestAttributes.lowIndex} - ${requestAttributes.highIndex} of ${requestAttributes.listSize}</span>
        </#if>
        <#if requestAttributes.highIndex?int < requestAttributes.listSize?int>
          | <a href="<@ofbizUrl>/category?category_id=${requestAttributes.productCategoryId}&VIEW_SIZE=${requestAttributes.viewSize}&VIEW_INDEX=${requestAttributes.viewIndex?int + 1}</@ofbizUrl>" class="buttontext">[Next]</a> |
        </#if>
      </b>
    </td>
  </tr>
</table>

<center>
  <table width='100%' border='0' cellpadding='0' cellspacing='0'>
    <#list productCategoryMembers as productCategoryMember>              
      <tr><td><hr class='sepbar'></td></tr>
      <tr>
        <td>
          ${setRequestAttribute("optProductId", productCategoryMember.productId)} 
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
      <b>
        <#if 0 < requestAttributes.viewIndex?int>
          <a href="<@ofbizUrl>/category?category_id=${requestAttributes.productCategoryId}&VIEW_SIZE=${requestAttributes.viewSize}&VIEW_INDEX=${requestAttributes.viewIndex?int - 1}</@ofbizUrl>" class="buttontext">[Previous]</a> |
        </#if>
        <#if 0 < requestAttributes.listSize?int>
          <span class="tabletext">${requestAttributes.lowIndex} - ${requestAttributes.highIndex} of ${requestAttributes.listSize}</span>
        </#if>
        <#if requestAttributes.highIndex?int < requestAttributes.listSize?int>
          | <a href="<@ofbizUrl>/category?category_id=${requestAttributes.productCategoryId}&VIEW_SIZE=${requestAttributes.viewSize}&VIEW_INDEX=${requestAttributes.viewIndex?int + 1}</@ofbizUrl>" class="buttontext">[Next]</a> |
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
      <div class='tabletext'>There are no products in this category.</DIV>
    </td>
  </tr>
</table>
</#if>

