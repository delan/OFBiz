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
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if requestAttributes.product?exists>
<#-- variable setup -->
<#assign product = requestAttributes.product>
<#assign price = requestAttributes.priceMap>
<#assign targetRequestName = "product">
<#if requestAttributes.targetRequestName?has_content>
    <#assign targetRequestName = requestAttributes.targetRequestName>
</#if>

<fo:external-graphic text-align="start"  overflow="hidden"  src="http://127.0.0.1:8080${product.smallImageUrl?if_exists}" />

<fo:inline white-space-collapse="false">
</fo:inline>
	<fo:leader leader-length="0.1in"
             leader-pattern="rule"
             alignment-baseline="middle"
             rule-thickness="0.5pt" color="white"/>
<fo:inline  font-size="9pt">${product.productName?if_exists} <@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></fo:inline>
<fo:inline white-space-collapse="false">
</fo:inline>
<#else>
&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br>
</#if>
