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
 *@version    $Revision$
 *@since      2.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if productCategoryMembers?exists && 0 < productCategoryMembers.size()>
<center>
  <table width='100%' border='0' cellpadding='2' cellspacing='0' class='boxoutside'>
    <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">${productCategory.description?if_exists}</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
    <#assign startIndex = viewSize * viewIndex>
    <#if highIndex < listSize>
      <#assign endIndex = highIndex - 1>
    <#else>
      <#assign endIndex = listSize - 1>
    </#if>
    
    <#list productCategoryMembers[startIndex..endIndex] as productCategoryMember> 
      <tr>
        <td>
          ${setRequestAttribute("optProductId", productCategoryMember.productId)} 
          ${setRequestAttribute("productCategoryMember", productCategoryMember)} 
          ${setRequestAttribute("listIndex", productCategoryMember_index)}
          ${setRequestAttribute("miniProdFormName", "theminireorderprod" + productCategoryMember_index + "form")}
          ${setRequestAttribute("miniProdQuantity", 1)}       
          ${pages.get("/catalog/productshortsum.ftl")}
        </td>
      </tr>             
      <tr><td><hr class='sepbar'></td></tr>
    </#list>
  </table>
</center>
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

