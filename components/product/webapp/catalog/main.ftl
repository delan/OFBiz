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
 *@author     Olivier Heintz (olivier.heintz@nereide.biz)
 *@version    $Rev$
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td align=left width='90%' >
            <div class='boxhead'>&nbsp;${uiLabelMap.ProductCatalogAdministrationMainPage}</div>
          </td>          
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td>
            <#if !sessionAttributes.userLogin?exists>
              <div class='tabletext'> ${uiLabelMap.ProductGeneralMessage}.</div>
            </#if>
            <br>
            <#if security.hasEntityPermission("CATALOG", "_VIEW", session)>
              <div class="tabletext"> ${uiLabelMap.ProductEditCatalogWithCatalogId}:</div>
              <form method=POST action="<@ofbizUrl>/EditProdCatalog</@ofbizUrl>" style="margin: 0;">
                <input type="text" size="20" maxlength="20" name="prodCatalogId" class="inputBox" value="">                
                <input type="submit" value=" ${uiLabelMap.ProductEditCatalog}" class="smallSubmit">
              </form>
              <div class="tabletext"> ${uiLabelMap.CommonOr}: <a href="<@ofbizUrl>/EditProdCatalog</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductCreateNewCatalog}</a></div>
            <br>            
              <div class="tabletext"> ${uiLabelMap.ProductEditCategoryWithCategoryId}:</div>
              <form method=POST action="<@ofbizUrl>/EditCategory</@ofbizUrl>" style="margin: 0;">
                <input type="text" size="20" maxlength="20" name="productCategoryId" class="inputBox" value="">
                <input type="submit" value="${uiLabelMap.ProductEditCategory}" class="smallSubmit">
              </form>
              <div class="tabletext"> ${uiLabelMap.CommonOr}: <a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductCreateNewCategory}</a></div>
            <br>
              <div class="tabletext"> ${uiLabelMap.ProductEditProductWithProductId}:</div>
              <form method=POST action="<@ofbizUrl>/EditProduct</@ofbizUrl>" style="margin: 0;">
                <input type="text" size="20" maxlength="20" name="productId" class="inputBox" value="">
                <input type="submit" value=" ${uiLabelMap.ProductEditProduct}" class="smallSubmit">
              </form>
              <div class="tabletext"> ${uiLabelMap.CommonOr}: <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductCreateNewProduct}</a></div>
              <div class="tabletext"> ${uiLabelMap.CommonOr}: <a href="<@ofbizUrl>/CreateVirtualWithVariantsForm</@ofbizUrl>" class="buttontext">Quick Create Virtual from Variants</a></div>
            <br>
              <div class="tabletext"> ${uiLabelMap.ProductFindProductWithIdValue}:</div>
              <form method=POST action="<@ofbizUrl>/FindProductById</@ofbizUrl>" style="margin: 0;">
                <input type="text" size="20" maxlength="20" name="idValue" class="inputBox" value="">
                <input type="submit" value=" ${uiLabelMap.ProductFindProduct}" class="smallSubmit">
              </form>
            <br>
            <br>
            <div><a href="<@ofbizUrl>/UpdateAllKeywords</@ofbizUrl>" class="buttontext"> ${uiLabelMap.ProductAutoCreateKeywordsForAllProducts}</a></div>
            <div><a href="<@ofbizUrl>/FastLoadCache</@ofbizUrl>" class="buttontext"> ${uiLabelMap.ProductFastLoadCatalogIntoCache}</a></div>
            <br>
            </#if>
            <div class="tabletext"> ${uiLabelMap.ProductCatalogManagerIsFor}.</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
