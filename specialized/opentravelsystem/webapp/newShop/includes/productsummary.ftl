<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Rev: 6358 $
 *@since      2.1
-->
<#if product?exists>
    <#-- variable setup -->
    <#assign targetRequestName = "product">
    <#if requestAttributes.targetRequestName?has_content>
        <#assign targetRequestName = requestAttributes.targetRequestName>
    </#if>
    <#assign smallImageUrl = productContentWrapper.get("SMALL_IMAGE_URL")?if_exists>
    <#if !smallImageUrl?has_content><#assign smallImageUrl = "/shop/html/images/image_tba.gif"></#if>
    <#-- end variable setup -->


<a href="<@ofbizUrl>${targetRequestName}/<#if categoryId?exists>~category_id=${categoryId}/</#if>~product_id=${product.productId}</@ofbizUrl>">
<table border="0" cellpadding="2" cellspacing="0" width="260">
 <tbody><tr><td colspan="2" class="blk">${productContentWrapper.get("DESCRIPTION")?if_exists}</td></tr>
 <tr><td valign="top">
         <div class="smallimage">
                <img src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Small Image" border="0"/>
        </div>
 </td>
 <td valign="bottom" ><font color="white">More about this fly</font></td>
 </tr>
 <tr><td colspan="2" class="sizes">
           <#if sizeProductFeatureAndAppls?has_content>
            <div class="tabletext">
              <#if (sizeProductFeatureAndAppls?size == 1)>
                Size:
              <#else>
                Sizes Available:
              </#if>
              <#list sizeProductFeatureAndAppls as sizeProductFeatureAndAppl>
                ${sizeProductFeatureAndAppl.abbrev?default(sizeProductFeatureAndAppl.description?default(sizeProductFeatureAndAppl.productFeatureId))}<#if sizeProductFeatureAndAppl_has_next>,</#if>
              </#list>
            </div>
          </#if>

</td></tr>
</tbody></table>
</a>

<#else>
&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br/>
</#if>
