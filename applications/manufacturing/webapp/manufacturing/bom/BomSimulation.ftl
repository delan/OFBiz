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
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *@version    $Rev$
 *@since      3.0
-->
<#if (requestAttributes.uiLabelMap)?exists>
    <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>

<#if requestParameters.lookupFlag?default("N") == "Y">

<#if selectedFeatures?has_content>
<hr>
<div class="tableheadtext">${uiLabelMap.ManufacturingSelectedFeatures}</div>
<#list selectedFeatures as selectedFeature>
    <div class="tabletext">${selectedFeature.productFeatureTypeId} = ${selectedFeature.description?if_exists} [${selectedFeature.productFeatureId}]</div>
</#list>
</#if>
<hr>
      <table border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td width="10%" align="left"><div class="tableheadtext">${uiLabelMap.ManufacturingProductLevel}</div></td>
          <td width="20%" align="left"><div class="tableheadtext">${uiLabelMap.ProductProductId}</div></td>
          <td width="10%" align="left"><div class="tableheadtext">---</div></td>
          <td width="40%" align="left"><div class="tableheadtext">${uiLabelMap.ProductProductName}</div></td>
          <td width="20%" align="right"><div class="tableheadtext">${uiLabelMap.CommonQuantity}</div></td>
        </tr>
        <tr>
          <td colspan='5'><hr class='sepbar'></td>
        </tr>
        <#if tree?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list tree as node>            
            <tr class='${rowClass}'>
              <td><img src='/manufacturing/images/depth${node.depth}.gif' height='16' border='0' alt='Depth'></td>
              <td><a href="<@ofbizUrl>/EditProductBom?productId=${(node.product.productId)?if_exists}&productAssocTypeId=${(node.bomTypeId)?if_exists}</@ofbizUrl>" class="buttontext">${node.product.productId}</a></td>
              <td>
                <#if node.product.isVirtual?default("N") == "Y">
                    Virtual
                </#if>
                ${(node.ruleApplied.ruleId)?if_exists}
              </td>
              <td>${node.product.internalName?default("&nbsp;")}</td>
              <td align="right">${node.quantity}</td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "viewManyTR2">
              <#assign rowClass = "viewManyTR1">
            <#else>
              <#assign rowClass = "viewManyTR2">
            </#if>
          </#list>          
        <#else>
          <tr>
            <td colspan='4'><div class='head3'>${uiLabelMap.CommonNoElementFound}.</div></td>
          </tr>        
        </#if>
      </table>
<hr>
<hr>
      <table border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td width="20%" align="left"><div class="tableheadtext">${uiLabelMap.ProductProductId}</div></td>
          <td width="40%" align="left"><div class="tableheadtext">${uiLabelMap.ProductProductName}</div></td>
          <td width="40%" align="right"><div class="tableheadtext">${uiLabelMap.CommonQuantity}</div></td>
        </tr>
        <tr>
          <td colspan='3'><hr class='sepbar'></td>
        </tr>
        <#if treeQty?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list treeQty as nodeQty>            
            <tr class='${rowClass}'>
              <td>${nodeQty.product.productId}</td>
              <td>${nodeQty.product.internalName?default("&nbsp;")}</td>
              <td align="right">${nodeQty.quantity}</td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "viewManyTR2">
              <#assign rowClass = "viewManyTR1">
            <#else>
              <#assign rowClass = "viewManyTR2">
            </#if>
          </#list>          
        <#else>
          <tr>
            <td colspan='4'><div class='head3'>${uiLabelMap.CommonNoElementFound}.</div></td>
          </tr>        
        </#if>
      </table>
</#if>
