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
 *@author     Olivier Heintz (olivier.heintz@nereide.biz) 
 *@version    $Rev:$
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if security.hasEntityPermission("SECURITY", "_VIEW", session)>
  ${pages.get("/security/SecurityGroupTabBar.ftl")}
  <div class="head1">${uiLabelMap.PartyPermissionSecurityGroupWithId}&nbsp;"${groupId}"</div>
  <a href="<@ofbizUrl>/EditSecurityGroup</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyNewSecurityGroup}]</a>
  <br>
  <#if securityGroupPermissions?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align='right'>
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>/EditSecurityGroupPermissions?groupId=${groupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} of ${listSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>/EditSecurityGroupPermissions?groupId=${groupId}&VIEW_SIZE${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
            </#if>
          </b>
        </td>
      </tr>
    </table>
  <#else>
    <br>
  </#if>
  <table border="1" cellpadding='2' cellspacing='0' width='100%'>
    <tr>
      <td><div class="tabletext"><b>${uiLabelMap.PartyPermissionId}</b></div></td>
      <td><div class="tabletext"><b>${uiLabelMap.CommonDescription}</b></div></td>
      <td width='1%'><div class="tabletext">&nbsp;</div></td>
    </tr>
    
    <#if securityGroupPermissions?has_content>
    <#list securityGroupPermissions[lowIndex..highIndex-1] as securityGroupPermission>
      <#assign securityPermission = securityGroupPermission.getRelatedOneCache("SecurityPermission")?if_exists>
      <tr valign="middle">
        <td><div class='tabletext'>&nbsp;${securityGroupPermission.permissionId}</div></td>
        <td><div class='tabletext'>&nbsp;${securityPermission.description?default("[" + securityGroupPermission.permissionId + "]")}</div></td>
        <td align="center">
          <a href='<@ofbizUrl>/removeSecurityPermissionFromSecurityGroup?permissionId=${securityGroupPermission.permissionId}&groupId=${groupId}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonDelete}]</a>
        </td>
      </tr>
    </#list>
    </#if>
  </table>  
  <#if securityGroupPermissions?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align='right'>
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>/EditSecurityGroupPermissions?groupId=${groupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} of ${listSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>/EditSecurityGroupPermissions?groupId=${groupId}&VIEW_SIZE${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
            </#if>
          </b>
        </td>
      </tr>
    </table>
  </#if> 
  <br>
  <form method="POST" action="<@ofbizUrl>/addSecurityPermissionToSecurityGroup</@ofbizUrl>" style='margin: 0;'>
    <input type="hidden" name="groupId" value="${groupId}">
    <input type="hidden" name="useValues" value="true">

    <div class='head2'>${uiLabelMap.PartyAddPermissionToSecurityGroup}:</div>
    <div class='tabletext'>
      ${uiLabelMap.PartyPermission}:
      <select name="permissionId" class='selectBox'>
        <#list securityPermissions as securityPermission>
          <option value="${securityPermission.permissionId}">${securityPermission.description}</option>
        </#list>
      </select>
      <input type="submit" class="smallSubmit" value="${uiLabelMap.CommonAdd}">
    </div>
  </form>
  <br>
  <form method="POST" action="<@ofbizUrl>/addSecurityPermissionToSecurityGroup</@ofbizUrl>" style='margin: 0;'>
    <input type="hidden" name="groupId" value="${groupId}">
    <input type="hidden" name="useValues" value="true">

    <div class='head2'>${uiLabelMap.PartyAddPermissionManuallyToSecurityGroup}:</div>
    <div class='tabletext'>
      Permission: <input type='text' class='inputBox' size='60' name='permissionId'>
      <input type="submit" class="smallSubmit" value="${uiLabelMap.CommonAdd}">
    </div>
  </form>
<#else>
  <h3>${uiLabelMap.PartySecurityViewPermissionError}</h3>
</#if>  
