<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Revision: 1.7 $
 *@since      3.0
-->

<#if security.hasEntityPermission("CATALOG", "_VIEW", session)>

  ${pages.get("/config/ConfigItemTabBar.ftl")}

  <div class="head1">Config Options - <span class="head2">ID: ${requestParameters.configItemId?if_exists}<#if configItem?exists> - ${configItem.description}</#if></div>
  <br><br>
  <table border="1" cellpadding='2' cellspacing='0'>
    <tr>
      <td><div class="tableheadtext">Name</div></td>
      <td><div class="tableheadtext">Seq #</div></td>
      <td><div class="tableheadtext">Description</div></td>
      <td><div class="tableheadtext">&nbsp;</div></td>
      <td><div class="tableheadtext">&nbsp;</div></td>
      <td><div class="tableheadtext">&nbsp;</div></td>
    </tr>

    <#list configOptionList as question>

      <form method="post" action="<@ofbizUrl>/updateProductConfigOption</@ofbizUrl>">
        <input type="hidden" name="configItemId" value="${question.configItemId}">
        <input type="hidden" name="configOptionId" value="${question.configOptionId}">
        <tr valign="middle">
          <td><div class="tabletext">${question.configOptionId} - ${question.configOptionName?if_exists}</div></td>
          <td><input type="text" name="sequenceNum" size="3" class="textBox" value="${question.sequenceNum?if_exists}">
          <td><div class="tabletext">${question.description?if_exists}</div></td>
          <td><input type="submit" value="Update">
          <td><a href="<@ofbizUrl>/EditProductConfigOptions?configItemId=${requestParameters.configItemId}&configOptionId=${question.configOptionId}#edit</@ofbizUrl>" class="buttontext">[Edit]</a>
          <td><a href="<@ofbizUrl>/deleteProductConfigOption?configItemId=${question.configItemId}&configOptionId=${question.configOptionId}</@ofbizUrl>" class="buttontext">[Remove]</a>
        </tr>
      </form>
    </#list>
  </table>
  <br>

  <hr class="sepbar">
  <a name="edit">
  <#-- new question / category -->

    <#if configOptionId?has_content>
      <div class="head2">Edit Config Option:</div>
      <a href="<@ofbizUrl>/EditProductConfigOptions?configItemId=${requestParameters.configItemId}</@ofbizUrl>" class="buttontext">[New Option]</a>
    <#else>
      <div class="head2">Create New Config Option</div>
    </#if>
    <br><br>
    ${createConfigOptionWrapper.renderFormString()}

  <#if (configOption?has_content)>
    <br>
    <hr class="sepbar">
    <br>
    <div class="head1">Components - <span class="head2">ID: ${configOption.configOptionId?if_exists} - ${configOption.description?if_exists}</div>
    <br><br>
    <table border="1" cellpadding='2' cellspacing='0'>
      <tr>
        <td><div class="tableheadtext">Seq #</div></td>
        <td><div class="tableheadtext">Product</div></td>
        <td><div class="tableheadtext">Quantity</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
      </tr>

      <#list configProducts as component>
        <#assign product = component.getRelatedOne("ProductProduct")>
        <tr valign="middle">
          <td><div class="tabletext">${component.sequenceNum?if_exists}</div></td>
          <td><div class="tabletext">${component.productId?if_exists} - ${product.description?if_exists}</div></td>
          <td><div class="tabletext">${component.quantity?if_exists}</div></td>
          <td><a href="<@ofbizUrl>/EditProductConfigOptions?configItemId=${requestParameters.configItemId}&configOptionId=${component.configOptionId}&productId=${component.productId}</@ofbizUrl>" class="buttontext">[Edit]</a>
          <td><a href="<@ofbizUrl>/deleteProductConfigProduct?configItemId=${requestParameters.configItemId}&configOptionId=${component.configOptionId}&productId=${component.productId}</@ofbizUrl>" class="buttontext">[Remove]</a>
        </tr>
      </#list>
    </table>

    <br>

    <#if !configProduct?has_content>
      <div class="head2">Add a ConfigProduct:</div>
    <#else>
      <div class="head2">Edit a ConfigProduct:</div>
      <a href="<@ofbizUrl>/EditProductConfigOptions?configItemId=${requestParameters.configItemId}&configOptionId=${configProduct.configOptionId}</@ofbizUrl>" class="buttontext">[New ConfigProduct]</a>
    </#if>
    ${createConfigProductWrapper.renderFormString()}
  </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>

