<#--
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski
 *@version    $Revision$
 *@since      2.1
-->

<#if requestAttributes.product?exists>
  <#-- variable setup -->
  <#assign product = requestAttributes.product>
  <#assign price = requestAttributes.priceMap>
  <#assign smallImageUrl = product.smallImageUrl?if_exists> 
  <#-- end variable setup -->
            
  <table border="0" width='100%' cellpadding='0' cellspacing='0'>
    <tr>
      <td valign="top">    
          <a href='<@ofbizUrl>/product?<#if requestAttributes.categoryId?exists>category_id=${requestAttributes.categoryId}&</#if>product_id=${product.productId}</@ofbizUrl>'>
            <img src='<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${product.smallImageUrl?default("/images/defaultImage.jpg")}</@ofbizContentUrl>' align="left" height="50" class='imageborder' border='0'> 
          </a>
      </td>
      <td align="left" valign="top" width="100%">
          <div class="tabletext">
            <a href='<@ofbizUrl>/product?<#if requestAttributes.categoryId?exists>category_id=${requestAttributes.categoryId}&</#if>product_id=${product.productId?if_exists}</@ofbizUrl>' class='buttontext'>${product.productName?if_exists}</a>
          </div>
          <div class="tabletext">${product.description?if_exists}</div>
          <div class="tabletext">
            <nobr>
              <b>${product.productId?if_exists}</b>,
                <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
                  List price: <span class='basePrice'>${price.listPrice?string.currency}</span>
                </#if>                                            
                <b>
                  <#if price.isSale>
                    <span class='salePrice'>On Sale!</span>
                  </#if>
                  Your price: <span class='<#if price.isSale>salePrice<#else>normalPrice</#if>'>${price.price?string.currency}</span>
                </b>
            </nobr>
          </div>
      </td>
      <td valign=center align=right>
          <#-- check to see if introductionDate hasn't passed yet -->
          <#if product.introductionDate?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().before(product.introductionDate)>
            <div class='tabletext' style='color: red;'>Not Yet Available</div>
          <#-- check to see if salesDiscontinuationDate has passed -->
          <#elseif product.salesDiscontinuationDate?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().before(product.salesDiscontinuationDate)>
            <div class='tabletext' style='color: red;'>No Longer Available</div>          
          <#-- check to see if the product is a virtual product -->
          <#elseif product.isVirtual?exists && product.isVirtual == "Y">
            <a href='<@ofbizUrl>/product?<#if requestAttributes.categoryId?exists>category_id=${requestAttributes.categoryId}&</#if>product_id=${product.productId}</@ofbizUrl>' class="buttontext"><nobr>[Choose Variation...]</nobr></a>                                                          
          <#else>
            <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="the${requestAttributes.listIndex?if_exists}form" style='margin: 0;'>
              <input type='hidden' name='add_product_id' value='${product.productId}'>
              <input type="text" class='inputBox' size="5" name="quantity" value="1">              
              <#if requestParameters.product_id?exists><input type='hidden' name='product_id' value='${requestParameters.product_id}'></#if>
              <#if requestParameters.category_id?exists><input type='hidden' name='category_id' value='${requestParameters.category_id}'></#if>
              <#if requestParameters.VIEW_INDEX?exists><input type='hidden' name='VIEW_INDEX' value='${requestParameters.VIEW_INDEX}'></#if>
              <#if requestParameters.SEARCH_STRING?exists><input type='hidden' name='SEARCH_STRING' value='${requestParameters.SEARCH_STRING}'></#if>
              <#if requestParameters.SEARCH_CATEGORY_ID?exists><input type='hidden' name='SEARCH_CATEGORY_ID' value='${requestParameters.SEARCH_CATEGORY_ID}'></#if>                            
              <br><a href="javascript:document.the${requestAttributes.listIndex?if_exists}form.submit()" class="buttontext"><nobr>[Add to Cart]</nobr></a>
            </form>
            
            <#if requestAttributes.productCategoryMemeber?exists>
			  <#assign prodCatMem = requestAttributes.productCategoryMember>
			  <#if prodCatMem?exists && prodCatMem != null && prodCatMem.quantity?exists && 0.00 < prodCatMem.quantity?double>
                <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="the${requestAttributes.listIndex?if_exists}defaultform" style='margin: 0;'>
                  <input type='hidden' name='add_product_id' value='${prodCatMem.productId?if_exists}'>
                  <input type='hidden' name="quantity" value='${prodCatMem.quantity?if_exists}'>                  
                  <#if requestParameters.product_id?exists><input type='hidden' name='product_id' value='${requestParameters.product_id}'></#if>
                  <#if requestParameters.category_id?exists><input type='hidden' name='category_id' value='${requestParameters.category_id}'></#if>
                  <#if requestParameters.VIEW_INDEX?exists><input type='hidden' name='VIEW_INDEX' value='${requestParameters.VIEW_INDEX}'></#if>
                  <#if requestParameters.SEARCH_STRING?exists><input type='hidden' name='SEARCH_STRING' value='${requestParameters.SEARCH_STRING}'></#if>
                  <#if requestParameters.SEARCH_CATEGORY_ID?exists><input type='hidden' name='SEARCH_CATEGORY_ID' value='${requestParameters.SEARCH_CATEGORY_ID}'></#if>                                              
                  <a href="javascript:document.the<%=UtilFormatOut.formatQuantity(listIndex)%>defaultform.submit()" class="buttontext"><nobr>[Add Default(<%EntityField.run("productCategoryMember", "quantity", pageContext);%>) to Cart]</nobr></a>
                </form>
              </#if>
            </#if>
          </#if>
      </td>
    </tr>
  </table>
<#else>
&nbsp;ERROR: Product not found.<br>
</#if>