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
  ${pages.get("/security/SecurityGroupTabBar.ftl")}
  <div class="head1">Edit SecurityGroup with ID "${groupId}"</div>
  <a href="<@ofbizUrl>/EditSecurityGroup</@ofbizUrl>" class="buttontext">[New Security Group]</a>
  <br>
  <br>
  
  <#if securityGroup?has_content>
    <form action="<@ofbizUrl>/updateSecurityGroup</@ofbizUrl>" method=POST style='margin: 0;'>
      <input type="hidden" name="groupId" value="${groupId}">
      <table border='0' cellpadding='2' cellspacing='0'>    
        <tr>
          <td align='right'><div class="tabletext">SecurityGroup ID</div></td>
          <td>&nbsp;</td>
          <td>
            <b>${groupId}</b> <span class='tabletext'>(This cannot be changed without re-creating the securityGroup.)</span>
          </td>
        </tr>
  <#else>  
    <form action="<@ofbizUrl>/createSecurityGroup</@ofbizUrl>" method=POST style='margin: 0;'>
      <table border='0' cellpadding='2' cellspacing='0'>
        <tr>
          <td align='right'><div class="tabletext">SecurityGroup ID</div></td>
          <td>&nbsp;</td>
          <td>
            <input type="text" name='groupId' class="inputBox" value='' size='20'>
          </td>
        </tr>
  </#if>
      <tr>
        <td width="26%" align=right><div class="tabletext">Description</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="description" value="${(securityGroup.description)?if_exists}" size="60" maxlength="250"></td>
      </tr>

      <tr>
        <td colspan='1' align=right><input type="submit" class="smallSubmit" value="Update"></td>
        <td colspan='2'>&nbsp;</td>
      </tr>
    </table>
  </form>
<#else>
  <h3>You do not have permission to view this page. ("SECURITY_VIEW" or "SECURITY_ADMIN" needed)</h3>
</#if>