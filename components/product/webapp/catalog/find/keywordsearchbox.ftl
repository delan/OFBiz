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
 *@version    $Revision: 1.3 $
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">${uiLabelMap.ProductSearchProducts}</div>
          </td>
          <td valign=middle align=right>
            <#if isOpen>
                <a href='<@ofbizUrl>/main?SearchProductsState=close</@ofbizUrl>' class='lightbuttontext'>&nbsp;_&nbsp;</a>
            <#else>
                <a href='<@ofbizUrl>/main?SearchProductsState=open</@ofbizUrl>' class='lightbuttontext'>&nbsp;[]&nbsp;</a>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
<#if isOpen>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <form name="keywordsearchform" method="POST" action="<@ofbizUrl>/keywordsearch?VIEW_SIZE=25</@ofbizUrl>" style='margin: 0;'>
              <div class='tabletext'>${uiLabelMap.ProductKeywords}: <input type="text" class="inputBox" name="SEARCH_STRING" size="20" maxlength="50"></div>
              <div class='tabletext'>
                ${uiLabelMap.ProductCategoryId}:
                <select class="selectBox" name="SEARCH_CATEGORY_ID">
                    <#list productCategories as productCategory>
                        <#assign displayDesc = productCategory.description>
                        <#if 18 < displayDesc?length>
                            <#assign displayDesc = displayDesc[0..15] + "...">
                        </#if>
                        <option value='${productCategory.productCategoryId}'>${displayDesc}</option>
                    </#list>
                </select>
              </div>
              <div class='tabletext'>
                ${uiLabelMap.CommonAny}<input type=RADIO name='SEARCH_OPERATOR' value='OR' checked>
                ${uiLabelMap.CommonAll}<input type=RADIO name='SEARCH_OPERATOR' value='AND'>
                &nbsp;<a href="javascript:document.keywordsearchform.submit()" class="buttontext">${uiLabelMap.CommonFind}</a>
              </div>
            </form>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</#if>
</table>
