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
 *@version    $Revision: 1.1 $
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#if (shoppingCartSize > 0)>
  <#include "/entry/promoUseDetailsInline.ftl"/>
  <br/>
</#if>

  <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.EcommerceSpecialOffers}</div>
            </td>
            <#--<td valign="middle" align="right">&nbsp;</td>-->
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td width="100%">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
          <tr>
            <td>
                <table width="100%" cellspacing="0" cellpadding="1" border="0">
                  <#-- show promotions text -->
                  <#list productPromosAllShowable as productPromo>
                    <tr>
                      <td>
                        <div class="tabletext"><a href="<@ofbizUrl>/showPromotionDetails?productPromoId=${productPromo.productPromoId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonDetails}]</a> ${productPromo.promoText}</div>
                      </td>
                    </tr>
                    <#if productPromo_has_next>
                      <tr><td><hr class="sepbar"></td></tr>
                    </#if>
                  </#list>
                </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>

