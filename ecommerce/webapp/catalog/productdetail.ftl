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

<#-- variable setup -->
<#assign product = requestAttributes.product>
<#assign price = requestAttributes.priceMap>
<#-- end variable setup -->

<table border="0" width="100%" cellpadding="2" cellspacing='0'>
  <#-- Category next/previous -->
  <#if requestAttributes.category?exists>
    <tr>
      <td colspan="2" align="right">
        <#if requestAttributes.previousProductId?exists>
          <a href='<#transform ofbizUrl>/product?category_id=<%=categoryId%>&product_id=<ofbiz:print attribute="previousProductId"/></#transform>' class="buttontext">[Previous]</a>&nbsp;|&nbsp;
        </#if>
        <a href="<ofbiz:url>/category?category_id=<%=categoryId%></ofbiz:url>" class="buttontext"><%EntityField.run("category", "description", pageContext);%></a>
        <#if requestAttributes.nextProductId?exists>
          &nbsp;|&nbsp;<a href='<ofbiz:url>/product?category_id=<%=categoryId%>&product_id=<ofbiz:print attribute="nextProductId"/></ofbiz:url>' class="buttontext">[Next]</a>
        </#if>
      </td>
    </tr>
  </#if>
  
  <tr><td colspan="2"><hr class='sepbar'></td></tr>
  
  <#-- Product image/name/price -->
  <tr>
    <td align="left" valign="top" width="0">
      <#if product.largeImageUrl?exists && product.largeImageUrl != null>
        <img src='<ofbiz:contenturl><%=contentPathPrefix%><%=product.getString("largeImageUrl")%></ofbiz:contenturl>' name='mainImage' vspace='5' hspace='5' border='1' width='200' align=left>
      </#if>
    </td>
    <td align="right" valign="top">
      <div class="head2">${product.productName?if_exists}</div>
      <div class="tabletext">${product.description?if_exists}</div>
      <div class="tabletext"><b>${product.productId?if_exists}</b></div>
      <#-- for prices:
              - if price < listPrice, show
              - if price < defaultPrice and defaultPrice < listPrice, show default
              - if isSale show price with salePrice style and print "On Sale!"
      -->
      <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
        <div class="tabletext">List price: <span class='basePrice'>${price.listPrice?string.currency}</span></div>
      </#if>
      <#if price.listPrice?exists && price.basePrice?exists && price.price?exists && price.price?double < price.defaultPrice?double && price.defaultPrice?double < price.listPrice?double>
        <div class="tabletext">Regular price: <span class='basePrice'>${price.defaultPrice?string.currency}</span></div>
      </#if>     
      <div class="tabletext">
        <b>
          <#if price.isSale?upper_case == "TRUE">
            <span class='salePrice'>On Sale!</span>
            <#assign priceStyle = "salePrice">
          <#else>
            <#assign priceStyle = "regularPrice">
          </#if>
            Your price: <span class='${priceStyle}'>${price.price?string.currency}</span>
        </b>
      </div>
      
      <#-- Included quantities/pieces -->
      <#if product.quantityIncluded?exists && product.quantityIncluded?double != 0>     
        <div class="tabletext">Includes:
          ${product.quantityIncluded?if_exists}
          ${product.quantityUomId?if_exists}
        </div>
      </#if>
      <#if product.piecesIncluded?exists && product.piecesIncluded?long != 0>
        <div class="tabletext">Pieces:
          ${product.piecesIncluded}
        </div>
      </#if>
      
      <p>&nbsp;</p>
      
      <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="addform" style='margin: 0;'>
        <#assign inStock = true>     
        <#-- Variant Selection -->
        <#if product.isVirtual?exists && product.isVirtual?upper_case == "Y">
          <#if requestAttributes.variantTree?exists && 0 < requestAttributes.variantTree?size>
            <#list requestAttributes.featureSet as currentType>
              <div class="tabletext">
                <select name="${currentType}" onChange="getList(this.name, this.options[this.selectedIndex].value)">
                  <option>${requestAttributes.featureTypes.get(currentType)}</option>
                </select>
              </div>
            </#list>
            <input type='hidden' name="product_id" value='${product.productId}'>
            <input type='hidden' name="add_product_id" value='NULL'>
          <#else>
            <input type='hidden' name="product_id" value='${product.productId}'>
            <input type='hidden' name="add_product_id" value='NULL'>
            <div class='tabletext'><b>This item is out of stock.</b></div>
            <#assign inStock = false>
          </#if>
        <#else>          
          <input type='hidden' name="product_id" value='${product.productId}'>
          <input type='hidden' name="add_product_id" value='${product.productId}'>
          <#if !Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].isCatalogInventoryAvailable(request, productId, 1.0)>
            <#if Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].isCatalogInventoryRequired(request, product)> 
              <div class='tabletext'><b>This item is out of stock.</b></div>
              <#assign inStock = false>
            <#else>
              <div class='tabletext'><b>${product.inventoryMessage?if_exists}</b></div>
            </#if>
          </#if>
        </#if>

        <p>&nbsp;</p>
        
        <#-- Check the product's availablity -->
        <#if requestAttributes.introduced?upper_case != "Y">
          <div class='tabletext' style='color: red;'>This product has not yet been made available for sale.</div>
        <#elseif requestAttributes.discontinued?upper_case == "Y">
          <div class='tabletext' style='color: red;'>This product is no longer available for sale.</div>
        <#else>
          <#if inStock>
            <a href="javascript:addItem()" class="buttontext"><nobr>[Add to Cart]</nobr></a>&nbsp;
            <input type="text" size="5" name="quantity" value="1">
          </#if>
          <#if requestParameters.category_id?exists>
            <input type='hidden' name='category_id' value='${requestParameters.category_id}'>
          </#if>
        </#if>
      </form>
       
      <#-- Prefill first select box (virtual products only) -->
      <#if requestAttributes.variantTree?exists && 0 < requestAttributes.variantTree?size>
        <script language="JavaScript">eval("list" + "${requestAttributes.featureOrderFirst}" + "()");</script>
      </#if>
                
      <#-- Swatches (virtual products only) -->
      <#if requestAttributes.variantSample?exists && 0 < requestAttributes.variantSample?size>
        <#assign imageKeys = requestAttributes.variantSample?keys>
        <#assign imageMap = requestAttributes.variantSample>
        <p>&nbsp;</p>
        <table cellspacing="0" cellpadding="0">
          <tr>
            <td>
              <%int ii=0; Iterator imIt=imageSet.iterator();%>
              <#assign indexer = 0>
              <#list imageKeys as key>
                <#assign imageUrl = imageMap[key].getString("smallImageUrl")?if_exists>       
                <#if imageUrl?exists && imageUrl != null>
                  <table cellspacing="0" cellpadding="0">
                    <tr><td><a href="#"><img src="<#transform ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${imageUrl}</#transform>" border="0" width="60" height="60" onclick="javascript:getList('${requestAttributes.featureOrderFirst}','${indexer}',1);"></a></td></tr>
                    <tr><td align="center" valign="top"><span class="tabletext">${key}</span></td></tr>
                  </table>
                </#if>
                <#assign indexer = indexer + 1>                
              </#list>
            </td>
          </tr>
        </table>
        <%}%>
      </#if>    
    </td>
  </tr>
  
  <tr><td colspan="2"><hr class='sepbar'></td></tr>
  
  <#-- Long description of product -->
  <tr>
    <td colspan="2">
      <div class="tabletext">${product.longDescription?if_exists}</div>
    </td>
  </tr>
  
  <tr><td colspan="2"><hr class='sepbar'></td></tr>
  
  <#-- Any attributes/etc may go here -->
</table>

<#-- Upgrades/Up-Sell/Cross-Sell -->
<#macro associated(assocProducts, beforeName, showName, afterName)>
  <#if assocProducts?exists && 0 < assocProducts?size>
    <tr><td>&nbsp;</td></tr> 
    <tr><td colspan="2"><div class="head2">${beforeName?if_exists}<#if nameb4note == "Y">${productValue.productName}</#if>${afterName?if_exists}</div></td></tr>
    <tr><td><hr class='sepbar'></td></tr>    
    <#list assocProducts as productAssoc>
      <tr><td>
        <div class="tabletext">
          <a href='<ofbiz:url>/product?product_id=${productAssoc.productIdTo?if_exists}</ofbiz:url>' class="buttontext">
            ${productAssoc.productIdTo?if_exists}
          </a>
          - <b>${productAssoc.reason?if_exists}</b>
        </div>
      </td></tr>
      <#assign asscProduct = productAssoc.getRelatedOneCache("AssocProduct")>
      ${setRequestAttribute("product", asscProduct)}
      ${setRequestAttribute("listIndex", listIndex)}
      <tr>
        <td>
          ${pages.get("/catalog/productsummary.jsp")}
        </td>
      </tr>
      <#assign listIndex = listIndex + 1>
      <tr><td><hr class='sepbar'></td></tr>
    </#list>           
    ${setRequestAttribute("assocProducts", "")}
  </#if>      
</#macro>
<#assign productValue = product>
<#assign listIndex = 1>
${setRequestAttribute("productValue", productValue)}

<table width='100%'>
  <#-- obsolete -->
  <#call associated(productAttributes.obsoleteProducts, "", "Y", " is made obsolete by these products:")>
  <#-- cross sell -->
  <#call associated(productAttributes.crossSellProducts, "", "N", "You might be interested in these as well:")>
  <#-- up sell -->
  <#call associated(productAttributes.upSellProducts, "Try these instead of ", "Y", ":")>
  <#-- obsolescence -->
  <#call associated(productAttributes.obsolenscenseProducts, "", "Y", " makes these products obsolete:")>
</table>

