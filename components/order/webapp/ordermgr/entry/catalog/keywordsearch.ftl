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
 *@version    $Revision: 1.6 $
 *@since      2.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<div class="head1">Product Search, <span class="head2">you searched for:</span></div>
<#list searchConstraintStrings as searchConstraintString>
    <div class="tabletext">&nbsp;<a href="<@ofbizUrl>/keywordsearch?removeConstraint=${searchConstraintString_index}&clearSearch=N</@ofbizUrl>" class="buttontext">[X]</a>&nbsp;${searchConstraintString}</div>
</#list>
<div class="tabletext">Sorted by: ${searchSortOrderString}</div>
<div class="tabletext"><a href="<@ofbizUrl>/advancedsearch?SEARCH_CATEGORY_ID=${(reqeustParameters.SEARCH_CATEGORY_ID)?if_exists}</@ofbizUrl>" class="buttontext">[Refine Search]</a></div>

<#if !productIds?has_content>
  <br><div class="head2">&nbsp;${uiLabelMap.ProductNoResultsFound}.</div>
</#if>

<#if productIds?has_content>
<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <#if 0 < viewIndex?int>
          <a href="<@ofbizUrl>/keywordsearch/~VIEW_INDEX=${viewIndex-1}/~VIEW_SIZE=${viewSize}/~clearSearch=N</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
        </#if>
        <#if 0 < listSize?int>
          <span class="tabletext">${lowIndex+1} - ${highIndex} of ${listSize}</span>
        </#if>
        <#if highIndex?int < listSize?int>
          | <a href="<@ofbizUrl>/keywordsearch/~VIEW_INDEX=${viewIndex+1}/~VIEW_SIZE=${viewSize}/~clearSearch=N</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
        </#if>
        </b>
      </td>
    </tr>
</table>
</#if>

<#if productIds?has_content>
    <#if sessionAttributes.shoppingCart?exists && sessionAttributes.shoppingCart.isPurchaseOrder()>
        <center>
          <form method="post" action="<@ofbizUrl>/addtocartbulk</@ofbizUrl>" name="bulkaddform" style='margin: 0;'>
              <#if requestParameters.category_id?has_content><input type="hidden" name="category_id" value="${requestParameters.category_id}"></#if>
              <#if requestParameters.VIEW_INDEX?has_content><input type="hidden" name="VIEW_INDEX" value="${requestParameters.VIEW_INDEX}"></#if>
              <#if requestParameters.VIEW_SIZE?has_content><input type="hidden" name="VIEW_SIZE" value="${requestParameters.VIEW_SIZE}"></#if>
              <input type="hidden" name="clearSearch" value="N">
            <div class="tabletext" align="right">
              <a href="javascript:document.bulkaddform.submit()" class="buttontext"><nobr>[${uiLabelMap.EcommerceAddAlltoCart}]</nobr></a>
            </div>     
            <table border='1' width='100%' cellpadding='2' cellspacing='0'>      
              <#list productIds as productId> <#-- note that there is no boundary range because that is being done before the list is put in the content -->
                <tr>
                    ${setRequestAttribute("optProductId", productId)}
                    ${pages.get("/entry/catalog/quickaddsummary.ftl")}
                </tr>        
              </#list> 
            </table>
            <div class="tabletext" align="right">
              <a href="javascript:document.bulkaddform.submit()" class="buttontext"><nobr>[${uiLabelMap.EcommerceAddAlltoCart}]</nobr></a>
            </div>      
          </form>
        </center>
    <#else>
        <center>
          <table width="100%" cellpadding="0" cellspacing="0">
            <#assign listIndex = lowIndex>
            <#list productIds as productId> <#-- note that there is no boundary range because that is being done before the list is put in the content -->
              ${setRequestAttribute("optProductId", productId)}
              ${setRequestAttribute("listIndex", productId_index)}
              <tr><td colspan="2"><hr class="sepbar"></td></tr>
              <tr>
                <td>
                  ${pages.get("/entry/catalog/productsummary.ftl")}
                </td>
              </tr>
            </#list>
          </table>
        </center>
    </#if>
</#if>

<#if productIds?has_content>
<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <#if 0 < viewIndex?int>
          <a href="<@ofbizUrl>/keywordsearch/~VIEW_INDEX=${viewIndex-1}/~VIEW_SIZE=${viewSize}/~clearSearch=N</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
        </#if>
        <#if 0 < listSize?int>
          <span class="tabletext">${lowIndex+1} - ${highIndex} of ${listSize}</span>
        </#if>
        <#if highIndex?int < listSize?int>
          | <a href="<@ofbizUrl>/keywordsearch/~VIEW_INDEX=${viewIndex+1}/~VIEW_SIZE=${viewSize}/~clearSearch=N</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
        </#if>
        </b>
      </td>
    </tr>
</table>
</#if>

