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
 *@author     Andy Zeneski
 *@version    $Revision$
 *@since      2.2
-->


<#if hasPermission>

<div class="head1">Global Price Rules</div>
<a href="<@ofbizUrl>/EditProductPriceRules</@ofbizUrl>" class="buttontext">[Create Rule]</a>
<#if activeOnly>
    <a href="<@ofbizUrl>/FindProductPriceRules?activeOnly=false</@ofbizUrl>" class="buttontext">[Active and Inactive]</a>
<#else>
    <a href="<@ofbizUrl>/FindProductPriceRules</@ofbizUrl>" class="buttontext">[Active Only]</a>
</#if>
<br>
<br>
<#if productPriceRules?has_content>
  <table border="1" cellpadding="2" cellspacing="0">
    <tr>
      <td><div class="tabletext"><b>Price&nbsp;Rule&nbsp;Name&nbsp;[ID]</b></div></td>
      <td><div class="tabletext"><b>Sale&nbsp;Rule?</b></div></td>
      <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
      <td><div class="tabletext"><b>Thru&nbsp;Date</b></div></td>
      <td><div class="tabletext">&nbsp;</div></td>
    </tr>
    <#list productPriceRules as rule>
    <tr>
      <td><div class="tabletext">&nbsp;<a href="<@ofbizUrl>/EditProductPriceRules?productPriceRuleId=${rule.productPriceRuleId}</@ofbizUrl>" class="buttontext">${rule.ruleName?if_exists} [${rule.productPriceRuleId}]</a></div></td>
      <td><div class="tabletext">&nbsp;${rule.isSale?if_exists}</div></td>
      <td>
        <#assign hasntStarted = false>
        <#if rule.fromDate?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().before(rule.getTimestamp("fromDate"))><#assign hasntStarted = true></#if>
        <div class="tabletext"<#if hasntStarted> style="color: red;"</#if>>
            &nbsp;${rule.fromDate?if_exists}
        </div>
      </td>
      <td>
        <#assign hasExpired = false>
        <#if rule.thruDate?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().after(rule.getTimestamp("thruDate"))><#assign hasExpired = true></#if>
        <div class="tabletext"<#if hasExpired> style="color: red;"</#if>>
            &nbsp;${rule.thruDate?if_exists}
        </div>
      </td>
      <td align="center">
        <a href="<@ofbizUrl>/EditProductPriceRules?productPriceRuleId=${rule.productPriceRuleId}</@ofbizUrl>" class="buttontext">[Edit]</a>
      </td>
    </tr>
    </#list>
  </table>
<#else>
    <h3>No price rules found.</h3>
</#if>

<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
