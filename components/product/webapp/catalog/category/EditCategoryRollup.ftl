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
 *@version    $Revision: 1.2 $
 *@since      2.1
-->

<#if hasPermission>

${pages.get("/category/CategoryTabBar.ftl")}

<div class="head1">Rollup <span class="head2"><#if productCategory?has_content>${productCategory.description} </#if>[ID:${productCategoryId?if_exists}]</span></div>

<a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[New Category]</a>
<#if productCategoryId?has_content> 
  <a href="/ecommerce/control/category?category_id=${productCategoryId}" class="buttontext" target="_blank">[Category Page]</a>
</#if>
<br>
<br>

<#if productCategoryId?has_content> 
<p class="head2">Category Rollup: Parent Categories</p>

<table border="1" cellpadding="2" cellspacing="0">
  <tr>
    <td><div class="tabletext"><b>Parent&nbsp;Category&nbsp;[ID]</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<#if currentProductCategoryRollups.size() != 0>
  <#assign lineParent = 0>
    <#list currentProductCategoryRollups as productCategoryRollup>
    <#assign lineParent = lineParent + 1>
    <#assign curCategory = productCategoryRollup.getRelatedOne("ParentProductCategory")>
    <tr valign="middle">
      <td><#if curCategory?has_content><a href="<@ofbizUrl>/EditCategory?productCategoryId=${curCategory.productCategoryId}</@ofbizUrl>" class="buttontext">${curCategory.description?if_exists} [${curCategory.productCategoryId}]</a></#if>
      </td>
      <td><div class="tabletext" style="color: red;">${productCategoryRollup.fromDate}<ofbiz:inputvalue entityAttr="productCategoryRollup" field="fromDate"/></div></td>
      <td align="center">
        <form method=POST action="<@ofbizUrl>/updateProductCategoryToCategory</@ofbizUrl>" name="lineParentForm${lineParent}">
            <input type=hidden name="showProductCategoryId" value="${productCategoryRollup.productCategoryId}">
	    	    
            <input type=hidden name="productCategoryId" value="${productCategoryRollup.productCategoryId}">
            <input type=hidden name="parentProductCategoryId" value="${productCategoryRollup.parentProductCategoryId}">
            <input type=hidden name="fromDate" value="${productCategoryRollup.fromDate}">
            <input type=text size="25" name="thruDate" value="${productCategoryRollup.thruDate?if_exists}" class="inputBox" style="color: red">
            <a href="javascript:call_cal(document.lineParentForm${lineParent}.thruDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
            <input type=text size="5" name="sequenceNum" value="${productCategoryRollup.sequenceNum?if_exists}" class="inputBox">
            <input type=submit value="Update" style="font-size: x-small;">
        </form>
      </td>
      <td>
        <a href="<@ofbizUrl>/removeProductCategoryFromCategory?showProductCategoryId=${productCategoryId}&productCategoryId=${productCategoryRollup.productCategoryId}&parentProductCategoryId=${productCategoryRollup.parentProductCategoryId}&fromDate=${productCategoryRollup.fromDate}</@ofbizUrl>" class="buttontext">
        [Delete]</a>
      </td>
    </tr>
  </#list>
</#if>
<#if currentProductCategoryRollups.size() == 0>
  <tr valign="middle">
    <td colspan="5"><DIV class="tabletext">No Parent Categories found.</DIV></td>
  </tr>
</#if>
</table>
<br>
<form method="POST" action="<@ofbizUrl>/addProductCategoryToCategory</@ofbizUrl>" style="margin: 0;" name="addParentForm">
  <input type="hidden" name="productCategoryId" value="${productCategoryId}">
  <input type="hidden" name="showProductCategoryId" value="${productCategoryId}">
  <div class="tabletext">Add <b>Parent</b> Category (select Category and enter From Date):</div>
    <select name="parentProductCategoryId" class="selectBox">
    
    <#list productCategoryCol as curCategory>
        <#if productCategoryId != curCategory.productCategoryId>
          <option value="${curCategory.productCategoryId}">${curCategory.description?if_exists} [${curCategory.productCategoryId}]</option>
        </#if>
    </#list>
    </select>
  <input type=text size="25" name="fromDate" class="inputBox">
  <a href="javascript:call_cal(document.addParentForm.fromDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
  <input type="submit" value="Add">
</form>
<br>
<hr>
<br>

<p class="head2">Category Rollup: Child Categories</p>

<table border="1" cellpadding="2" cellspacing="0">
  <tr>
    <td><div class="tabletext"><b>Child&nbsp;Category&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<#if parentProductCategoryRollups.size() != 0>
  <#assign lineChild = 0>
   <#list parentProductCategoryRollups as productCategoryRollup>
    <#assign lineChild = lineChild + 1>
    
        <#assign curCategory = productCategoryRollup.getRelatedOne("CurrentProductCategory")>
    <tr valign="middle">
      <td><#if curCategory?has_content><a href="<@ofbizUrl>/EditCategory?productCategoryId=${curCategory.productCategoryId}</@ofbizUrl>" class="buttontext">${curCategory.description?if_exists} [${curCategory.productCategoryId}]</a></#if>
      
      <td><div class="tabletext" style="color: red">${productCategoryRollup.fromDate}</div></td>
      <td align="center">
        <form method=POST action="<@ofbizUrl>/updateProductCategoryToCategory</@ofbizUrl>" name="lineChildForm${lineChild}">
            <input type=hidden name="showProductCategoryId" value="${productCategoryId}">
            <input type=hidden name="productCategoryId" value="${productCategoryRollup.productCategoryId}">
            <input type=hidden name="parentProductCategoryId" value="${productCategoryRollup.parentProductCategoryId}">
            <input type=hidden name="fromDate" value="${productCategoryRollup.fromDate}">
            <input type=text size="25" name="thruDate" value="${productCategoryRollup.thruDate?if_exists}" class="inputBox" style="color: red;">
            <a href="javascript:call_cal(document.lineChildForm${lineChild}.thruDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
            <input type=text size="5" name="sequenceNum" value="${productCategoryRollup.sequenceNum?if_exists}" class="inputBox">
            <input type=submit value="Update" style="font-size: x-small;">
        </form>
      </td>
      <td>
        <a href="<@ofbizUrl>/removeProductCategoryFromCategory?showProductCategoryId=${productCategoryId}&productCategoryId=${productCategoryRollup.productCategoryId}&parentProductCategoryId=${productCategoryRollup.parentProductCategoryId}&fromDate=${productCategoryRollup.fromDate}</@ofbizUrl>" class="buttontext">
        [Delete]</a>
      </td>
    </tr>
  </#list>
</#if>
<#if parentProductCategoryRollups.size() == 0>
  <tr valign="middle">
    <td colspan="5"><div class="tabletext">No Child Categories found.</div></td>
  </tr>
</#if>
</table>
<br>

<form method="POST" action="<@ofbizUrl>/addProductCategoryToCategory</@ofbizUrl>" style="margin: 0;" name="addChildForm">
  <input type="hidden" name="showProductCategoryId" value="${productCategoryId}">
  <input type="hidden" name="parentProductCategoryId" value="${productCategoryId}">
  <div class="tabletext">Add <b>Child</b> Category (select Category and enter From Date):</div>
    <select name="productCategoryId" class="selectBox">
    <#list productCategoryCol as curCategory>
        <#if productCategoryId != curCategory.productCategoryId>
          <option value="${curCategory.productCategoryId}">${curCategory.description?if_exists} [${curCategory.productCategoryId}]</option>
        </#if>
    </#list>
    </select>
  <input type=text size="25" name="fromDate" class="inputBox">
  <a href="javascript:call_cal(document.addChildForm.fromDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
  <input type="submit" value="Add">
</form>
</#if>

<#else>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
