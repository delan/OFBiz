<#--
 *  Copyright (c) 2001, 2002, 2003 The Open For Business Project - www.ofbiz.org
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
 *@since      2.1
-->

<#assign reorderProductResults = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getQuickReorderProducts(request)?if_exists>
<#assign reorderProducts = reorderProductResults.products?if_exists> 
<#assign reorderQuantities = reorderProductResults.quantities?if_exists> 

<#if reorderProducts?has_content>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="center">
              <div class="boxhead">Quick&nbsp;Reorder...</div>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td>
                <table width='100%' cellspacing="0" cellpadding="0" border="0">
      <#list reorderProducts as miniProduct> 
        <tr>
          <td>
            ${setRequestAttribute("miniProdQuantity", reorderQuantities.get(miniProduct.productId))}
            ${setRequestAttribute("miniProdFormName", "theminireorderprod" + miniProduct_index + "form")}
            ${setRequestAttribute("miniProduct", miniProduct)}
            ${pages.get("/catalog/miniproductsummary.ftl")}
          </td>
        </tr>
        <#if miniProduct_has_next>
          <tr><td><hr class='sepbar'></td></tr>
        </#if>
      </#list>
                </table>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</#if>

