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
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#if productPromo?has_content>
  <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.EcommercePromotionDetails}:</div>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="4" class="boxbottom">
          <tr>
            <td>
                <div class="tabletext">${productPromo.promoText}</div>
                <div class="tabletext">TODO: put long auto text here</div>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>

  <br/>

<#if productIds?has_content>
  <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.EcommerceProductsForPromotion}:</div>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
          <tr>
            <td><div class="tableheadtext">${uiLabelMap.CommonQualifier}</div></td>
            <td><div class="tableheadtext">${uiLabelMap.CommonBenefit}</div></td>
            <td><div class="tableheadtext">&nbsp;</div></td>
          </tr>
          <#list productIds as productId>
              <tr>
                <td><div class="tabletext">[<#if productIdsCond.contains(productId)>x<#else>&nbsp;</#if>]</div></td>
                <td><div class="tabletext">[<#if productIdsAction.contains(productId)>x<#else>&nbsp;</#if>]</div></td>
                <td>
                  ${setRequestAttribute("optProductId", productId)}
                  ${setRequestAttribute("listIndex", productId_index)}
                  ${pages.get("/catalog/productsummary.ftl")}
                </td>
              </tr>
          </#list>
        </table>
      </td>
    </tr>
  </table>
</#if>
<#else>
    <div class="head2">${uiLabelMap.EcommerceErrorNoPromotionFoundWithID} [${productPromoId?if_exists}]</div>
</#if>