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
 *@version    $Rev:$
 *@since      2.1
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<script language="JavaScript">
 <!--
     function changeCategory() {
         document.forms["keywordsearchform"].elements["SEARCH_CATEGORY_ID"].value=document.forms["advancedsearchform"].elements["DUMMYCAT"].value;
         document.forms["advancedsearchform"].elements["SEARCH_CATEGORY_ID"].value=document.forms["advancedsearchform"].elements["DUMMYCAT"].value;
     }
 //-->
 </script>

<div class="screenlet">
    <div class="screenlet-header">
        <div class="simple-right-small">
            <#if isOpen>
                <a href="<@ofbizUrl>/main?SearchProductsState=close</@ofbizUrl>" class="lightbuttontext">&nbsp;_&nbsp;</a>
            <#else>
                <a href="<@ofbizUrl>/main?SearchProductsState=open</@ofbizUrl>" class="lightbuttontext">&nbsp;[]&nbsp;</a>
            </#if>
        </div>
        <div class="boxhead">${uiLabelMap.ProductSearchProducts}</div>
    </div>
<#if isOpen>
    <div class="screenlet-body">
        <div>
            <form name="keywordsearchform" method="POST" action="<@ofbizUrl>/keywordsearch?VIEW_SIZE=25</@ofbizUrl>" style="margin: 0;">
              <div class="tabletext">${uiLabelMap.ProductKeywords}: <input type="text" class="inputBox" name="SEARCH_STRING" size="20" maxlength="50" value="${requestParameters.SEARCH_STRING?if_exists}"></div>
              <div class="tabletext">
                ${uiLabelMap.ProductCategoryId}: <input type="text" class="inputBox" name="SEARCH_CATEGORY_ID" size="20" maxlength="20" value="${requestParameters.SEARCH_CATEGORY_ID?if_exists}">
              </div>
              <div class="tabletext">
                ${uiLabelMap.CommonAny}<input type="RADIO" name="SEARCH_OPERATOR" value="OR" checked/>
                ${uiLabelMap.CommonAll}<input type="RADIO" name="SEARCH_OPERATOR" value="AND"/>
                &nbsp;<a href="javascript:document.keywordsearchform.submit()" class="buttontext">${uiLabelMap.CommonFind}</a>
              </div>
            </form>
        </div>
        <div>
            <form name="advancedsearchform" method="POST" action="<@ofbizUrl>/advancedsearch</@ofbizUrl>" style="margin: 0;">
              <div class="tabletext">
                ${uiLabelMap.ProductCategoryId}: <input type="text" class="inputBox" name="SEARCH_CATEGORY_ID" size="20" maxlength="20" value="${requestParameters.SEARCH_CATEGORY_ID?if_exists}">
              </div>
              <div class="tabletext">
                <a href="javascript:document.advancedsearchform.submit()" class="buttontext">${uiLabelMap.ProductAdvancedSearch}</a>
              </div>
                <select class="selectBox" name="DUMMYCAT" onChange="changeCategory()" style="width: 200px;">
                    <option value="">- Select a Category -</option>
                    <#list productCategories as productCategory>
                        <#assign displayDesc = productCategory.description?default("No Description")>
                        <#if 18 < displayDesc?length>
                            <#assign displayDesc = displayDesc[0..15] + "...">
                        </#if>
                        <option value="${productCategory.productCategoryId}">${displayDesc} [${productCategory.productCategoryId}]</option>
                    </#list>
                </select>
            </form>
        </div>
    </div>
</#if>
</div>

