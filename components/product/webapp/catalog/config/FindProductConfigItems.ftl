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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Rev:$
 *@since      3.0
-->

<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if security.hasEntityPermission("CONTENTMGR", "_VIEW", session)>

<#if itemsList?exists>
<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td width="50%"><div class="boxhead">Config Items Found</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <#if 0 < itemsList?size>
                <#if 0 < viewIndex>
                  <a href="<@ofbizUrl>/FindProductConfigItems?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}${paramList}</@ofbizUrl>" class="submenutext">Previous</a>
                <#else>
                  <span class="submenutextdisabled">Previous</span>
                </#if>
                <#if 0 < listSize>
                  <span class="submenutextinfo">${lowIndex+1} - ${highIndex} of ${listSize}</span>
                </#if>
                <#if highIndex < listSize>
                  <a href="<@ofbizUrl>/FindProductConfigItems?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}${paramList}</@ofbizUrl>" class="submenutextright">Next</a>
                <#else>
                  <span class="submenutextrightdisabled">Next</span>
                </#if>
              </#if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <div class="tabletext"><a href="<@ofbizUrl>/EditProductConfigItem</@ofbizUrl>" class="buttontext">[Create New Config Item]</a></div>
      <br>
      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
        <tr>
          <td align="left"><div class="tableheadtext">Config Item</div></td>
          <td align="left"><div class="tableheadtext">Type</div></td>
          <td align="left"><div class="tableheadtext">Description</div></td>
          <td><div class="tableheadtext">&nbsp;</div></td>
        </tr>
        <tr>
          <td colspan="10"><hr class="sepbar"></td>
        </tr>
        <#if itemsList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list itemsList[lowIndex..highIndex-1] as item>
            <tr class="${rowClass}">
              <td><div class="tabletext">${item.configItemId} - ${item.configItemName?default("")}</div></td>
              <td><div class="tabletext">
                <#if item.configItemTypeId?if_exists == "SINGLE">${uiLabelMap.ProductSingleChoice}<#else>${uiLabelMap.ProductMultiChoice}</#if>
              </div></td>
              <td><div class="tabletext">${item.description?default("No Description")}</div></td>
              <td align="right">
                <a href="<@ofbizUrl>/EditProductConfigItem?configItemId=${item.configItemId}</@ofbizUrl>" class="buttontext">Edit</a>
              </td>
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
            <td colspan="4"><div class="head3">No Config Items found.</div></td>
          </tr>        
        </#if>
      </table>
    </td>
  </tr>
</table>
</#if>

<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
