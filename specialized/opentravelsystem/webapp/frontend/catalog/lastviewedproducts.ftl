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
 *@version    $Rev: 3103 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<table border="0" width="100%" cellpadding="3">
  <tr>
    <td colspan="2">
      <div class="head1">
       ${uiLabelMap.ProductProductsLastViewed}
      </div>
    </td>
  </tr>
</table>

<#if sessionAttributes.lastViewedProducts?exists && sessionAttributes.lastViewedProducts?has_content>
<br>
<center>
  <table width='100%' border='0' cellpadding='0' cellspacing='0'>        
    <#list sessionAttributes.lastViewedProducts as productId>              
      <tr><td><hr class='sepbar'></td></tr>
      <tr>
        <td>
          ${setRequestAttribute("optProductId", productId)}
          ${setRequestAttribute("listIndex", productId_index)}
          ${pages.get("/catalog/productsummary.ftl")}
        </td>
      </tr>
    </#list>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
  </table>
</center>
<#else>
<table border="0" width="100%" cellpadding="2">
  <tr>
    <td colspan="2"><hr class='sepbar'></td>
  </tr>
  <tr>
    <td>
      <div class='tabletext'>${uiLabelMap.ProductNotViewedAnyProducts}.</DIV>
    </td>
  </tr>
</table>
</#if>
