<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Rev:$
 *@since      3.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if sessionAttributes.lastViewedCategories?exists && sessionAttributes.lastViewedCategories?has_content>
  <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <tr>
      <td width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="center">
              <div class="boxhead">${uiLabelMap.EcommerceLastCategories}...</div>
            </td>
            <td valign="middle" align="right">
              <a href="<@ofbizUrl>/clearLastViewed</@ofbizUrl>" class="lightbuttontextsmall">${uiLabelMap.CommonClear}</a>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td>
              <table width='100%' cellspacing="0" cellpadding="0" border="0">
                <#assign count = 0>
                <#list sessionAttributes.lastViewedCategories as categoryId>
                  <#if count < 9>
                    <#assign category = delegator.findByPrimaryKeyCache("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", categoryId))?if_exists>
                    <#if category?has_content>
                      <tr>
                        <td>
                          <span class="browsecategorytext">-&nbsp;</span>
                          <a href="<@ofbizUrl>/category/~category_id=${categoryId}</@ofbizUrl>" class="browsecategorybutton">${category.description?if_exists}</a>
                        </td>
                      </tr>
                    </#if>
                  </#if>
                  <#assign count = count + 1>
                </#list>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <br>
</#if>