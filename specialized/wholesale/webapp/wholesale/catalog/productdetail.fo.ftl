<#--
 *  Copyright (c) 2003-5 The Open For Business Project - www.ofbiz.org
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
 *@author     Si Chen
 *@version    $Revision$
-->
<?xml version="1.0" encoding="UTF-8" ?>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign product = requestAttributes.product?if_exists>
<#assign price = requestAttributes.priceMap?if_exists>
	<fo:layout-master-set>
		<fo:simple-page-master margin-right="2.0cm" margin-left="1.0cm" margin-bottom="1.0cm" margin-top="0cm" page-width="22cm" page-height="20cm" master-name="first">
			<fo:region-before extent="1.5cm"/>
			<fo:region-body margin-bottom="1.5cm" margin-top="1.5cm"/>
			<fo:region-after extent="1.0cm"/>
		</fo:simple-page-master>
	</fo:layout-master-set>
<fo:page-sequence master-reference="first" language="en" hyphenate="true">
	<fo:static-content flow-name="xsl-region-after">
		<fo:block line-height="12pt" font-size="10pt" space-before.optimum="1.5pt" space-after.optimum="1.5pt" keep-together="always" text-align="center">
To order, call us at our toll free number: 1-888-828-8888
		</fo:block>
	</fo:static-content>
	<fo:flow flow-name="xsl-region-body">
	<fo:table text-align="left" table-layout="fixed">
	       	<fo:table-column column-width="7cm"/>
			<fo:table-column column-width="9.30cm"/>
			<fo:table-body>
   				<fo:table-row>
      				 <fo:table-cell>
						<fo:external-graphic  width="252pt" height="72pt" overflow="hidden"  src="http://127.0.0.1:8080/images/demo_wholesale_logo.gif" />
					</fo:table-cell>
               	<fo:table-cell>
<fo:block  space-before.optimum="1.5pt" space-after.optimum="1.5pt" keep-together="always" text-align="center"> 

<fo:inline white-space-collapse="false">
</fo:inline>


<fo:inline color="#000099" font-size="12pt" font-weight="bold">The Demo Company</fo:inline>
<fo:inline white-space-collapse="false">
</fo:inline>


<fo:inline color="#330099"   font-size="9pt" >Wholesale Catalog</fo:inline>

 </fo:block></fo:table-cell></fo:table-row></fo:table-body></fo:table>






	<fo:block space-before.optimum="1.5pt" space-after.optimum="1.5pt" keep-together="always"  line-height="14pt" font-size="10pt">
<fo:inline white-space-collapse="false">
</fo:inline>

	</fo:block>




<fo:table text-align="left" table-layout="fixed">
			         	<fo:table-column column-width="7cm"/>
                                         <fo:table-column column-width="0.30cm"/>
					<fo:table-column column-width="11cm"/>

		<fo:table-body>
   			<fo:table-row>
      			 <fo:table-cell>
      <#if product.largeImageUrl?exists>
		<fo:external-graphic text-align="start"  overflow="hidden"  src="http://127.0.0.1:8080${product.largeImageUrl}" />
	  </#if>
			</fo:table-cell>

<fo:table-cell>
</fo:table-cell>


<fo:table-cell>
<fo:block  space-before.optimum="1.5pt" space-after.optimum="1.5pt" keep-together="always" > 


<fo:inline  text-align="left" font-size="12pt" font-weight="bold">${product.productName?if_exists}
</fo:inline>
<fo:inline white-space-collapse="false">
</fo:inline>


<fo:inline  font-size="8pt" >${product.longDescription?if_exists}</fo:inline>
<fo:inline white-space-collapse="false">
</fo:inline>
<fo:inline white-space-collapse="false">
</fo:inline>

<#if daysToShip?exists>
<fo:inline  font-size="9pt" font-weight="bold">${uiLabelMap.ProductUsuallyShipsIn}</fo:inline>
 <fo:inline  font-size="9pt" font-weight="bold" color="red">${daysToShip}</fo:inline>
 <fo:inline  font-size="9pt" font-weight="bold">${uiLabelMap.CommonDays}</fo:inline>
</#if>

<fo:inline white-space-collapse="false">
</fo:inline>

<fo:inline  font-size="9pt" font-weight="bold">Item: ${product.productId?if_exists} 
      <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
        ${uiLabelMap.ProductListPrice}: <@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed/>
      </#if>
      <#if price.listPrice?exists && price.basePrice?exists && price.price?exists && price.price?double < price.defaultPrice?double && price.defaultPrice?double < price.listPrice?double>
        ${uiLabelMap.ProductRegularPrice}: <@ofbizCurrency amount=price.defaultPrice isoCode=price.currencyUsed/>
      </#if>
            ${uiLabelMap.EcommerceYourPrice}: <@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>

</fo:inline>

<fo:inline white-space-collapse="false">
</fo:inline>

 
  <#macro associated assocProducts beforeName showName afterName formNamePrefix targetRequestName>
  <#assign targetRequest = "product">
  <#if targetRequestName?has_content>
    <#assign targetRequest = targetRequestName>
  </#if>
  <#if assocProducts?has_content>
	<fo:leader leader-length="4.3in" leader-pattern="rule"  alignment-baseline="middle"
    	         rule-thickness="0.5pt" color="black"/>
	<fo:inline  font-size="11pt" font-weight="bold">${beforeName?if_exists}<#if showName == "Y">${productValue.productName}</#if>${afterName?if_exists}
	</fo:inline>
<fo:inline white-space-collapse="false">
</fo:inline>
    <#list assocProducts as productAssoc>
      ${setRequestAttribute("optProductId", productAssoc.productIdTo)}
      ${setRequestAttribute("listIndex", listIndex)}
      ${setRequestAttribute("formNamePrefix", formNamePrefix)}
      <#if targetRequestName?has_content>
        ${setRequestAttribute("targetRequestName", targetRequestName)}
      </#if>
	<fo:leader leader-length="0.1in"
             leader-pattern="rule"
             alignment-baseline="middle"
             rule-thickness="0.5pt" color="white"/>
         <#if pages?exists>${pages.get("/catalog/productshortsum.fo.ftl")}</#if>
         <#if screens?exists>${screens.render("component://wholesale/widget/CatalogScreens.xml#productshortsum.fo")}</#if>
      <#local listIndex = listIndex + 1>
    </#list>
    ${setRequestAttribute("optProductId", "")}
    ${setRequestAttribute("formNamePrefix", "")}
    ${setRequestAttribute("targetRequestName", "")}
  </#if>
</#macro>
<#assign productValue = product>
<#assign listIndex = 1>
${setRequestAttribute("productValue", productValue)}

  <#-- obsolete -->
  <@associated assocProducts=requestAttributes.obsoleteProducts beforeName="" showName="Y" afterName=" is made obsolete by these products:" formNamePrefix="obs" targetRequestName=""/>
  <#-- cross sell -->
  <@associated assocProducts=requestAttributes.crossSellProducts beforeName="" showName="N" afterName="Cross-sell suggestions:" formNamePrefix="cssl" targetRequestName="crosssell"/>
  <#-- up sell -->
  <@associated assocProducts=requestAttributes.upSellProducts beforeName="Upsell suggestions:" showName="N" afterName="" formNamePrefix="upsl" targetRequestName="upsell"/>


 </fo:block>				 
		   </fo:table-cell>
         </fo:table-row>
		</fo:table-body>
	</fo:table>




	
	<fo:block space-before.optimum="1.5pt" space-after.optimum="1.5pt" keep-together="always" id="LastPage" line-height="1pt" font-size="1pt">
	</fo:block>
	
</fo:flow>










</fo:page-sequence>

</fo:root>


