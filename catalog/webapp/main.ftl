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
 *@version    $Revision$
 *@since      2.1
-->

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='90%' >
            <div class='boxhead'>&nbsp;Catalog Administration Main Page</div>
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
            <#if !sessionAttributes.userLogin?exists>
              <div class='tabletext'>For something interesting make sure you are logged in, try username:admin, password:ofbiz.</div>
            </#if>
            <br>
            <#if security.hasEntityPermission("CATALOG", "_VIEW", session)>
              <div class='tabletext'>Edit Catalog with Catalog ID:</div>
              <form method=POST action='<@ofbizUrl>/EditProdCatalog</@ofbizUrl>' style='margin: 0;'>
                <input type='text' size='20' maxlength='20' name='prodCatalogId' class='inputBox' value=''>                
                <input type='submit' value='Edit Catalog' class='standardSubmit'>
              </form>
              <div class='tabletext'>OR: <A href='<@ofbizUrl>/EditProdCatalog</@ofbizUrl>' class='buttontext'>Create New Catalog</A></div>
            <br>            
              <div class='tabletext'>Edit Category with Category ID:</div>
              <form method=POST action='<@ofbizUrl>/EditCategory</@ofbizUrl>' style='margin: 0;'>
                <input type='text' size='20' maxlength='20' name='productCategoryId' class='inputBox' value=''>
                <input type='submit' value='Edit Category' class='standardSubmit'>
              </form>
              <div class='tabletext'>OR: <A href='<@ofbizUrl>/EditCategory</@ofbizUrl>' class='buttontext'>Create New Category</A></div>
            <br>
              <div class='tabletext'>Edit Product with Product ID:</div>
              <form method=POST action='<@ofbizUrl>/EditProduct</@ofbizUrl>' style='margin: 0;'>
                <input type='text' size='20' maxlength='20' name='productId' class='inputBox' value=''>
                <input type='submit' value='Edit Product' class='standardSubmit'>
              </form>
              <div class='tabletext'>OR: <A href='<@ofbizUrl>/EditProduct</@ofbizUrl>' class='buttontext'>Create New Product</A></div>
            <br>
              <div class='tabletext'>Find Product with ID Value:</div>
              <form method=POST action='<@ofbizUrl>/FindProductById</@ofbizUrl>' style='margin: 0;'>
                <input type='text' size='20' maxlength='20' name='idValue' class='inputBox' value=''>
                <input type='submit' value='Find Product' class='standardSubmit'>
              </form>
            <br>
            <br>
            <div><A href='<@ofbizUrl>/UpdateAllKeywords</@ofbizUrl>' class='buttontext'>Auto-Create Keywords for All Products</A></div>
            <div><A href='<@ofbizUrl>/FastLoadCache</@ofbizUrl>' class='buttontext'>Fast-Load Catalog into Cache</A></div>
            <br>
            </#if>
            <div class='tabletext'>This application is primarily intended for those repsonsible for the maintenance of product catalog related information.</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
