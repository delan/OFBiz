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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.1 $
 *@since      2.2
-->

<#if security.hasEntityPermission("SECURITY", "_VIEW", session)>
  <div class="head1">SecurityGroups List</div>
  <div><a href='<@ofbizUrl>/EditSecurityGroup</@ofbizUrl>' class="buttontext">[Create New SecurityGroup]</a></div>
  
  <#if securityGroups?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align='right'>
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>/FindSecurityGroup?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[Previous]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} of ${listSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>/FindSecurityGroup?VIEW_SIZE${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[Next]</a>
            </#if>
          </b>
        </td>
      </tr>
    </table>
  </#if>
  
  <table border="1" cellpadding='2' cellspacing='0' width='100%'>
    <tr>
      <td><div class="tabletext"><b>SecurityGroup&nbsp;ID</b></div></td>
      <td><div class="tabletext"><b>Description</b></div></td>
      <td><div class="tabletext">&nbsp;</div></td>
    </tr>
    <#list securityGroups[lowIndex..highIndex-1] as securityGroup>    
      <tr valign="middle">
        <td><div class='tabletext'>&nbsp;${securityGroup.groupId}</div></td>
        <td><div class='tabletext'>&nbsp;${securityGroup.description?if_exists}</div></td>
        <td align="center">
          <a href='<@ofbizUrl>/EditSecurityGroup?groupId=${securityGroup.groupId}</@ofbizUrl>' class="buttontext">[Edit]</a>
        </td>
      </tr>
    </#list>
  </table>
  
  <#if securityGroups?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align='right'>
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>/FindSecurityGroup?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[Previous]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} of ${listSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>/FindSecurityGroup?VIEW_SIZE${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[Next]</a>
            </#if>
          </b>
        </td>
      </tr>
    </table>
  </#if>
<#else>
  <h3>You do not have permission to view this page. ("SECURITY_VIEW" or "SECURITY_ADMIN" needed)</h3>
</#if>