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
 *@author     David E. Jones
 *@author     Brad Steiner
 *@version    $Revision: 1.1 $
 *@since      2.2
-->

<#if hasPermission>

${pages.get("/group/FacilityGroupTabBar.ftl")}

<div class="head1">Roles <span class="head2">for&nbsp;"${(facilityGroup.facilityGroupName)?if_exists}" [ID:${facilityGroupId?if_exists}]</span></div>
<a href="<@ofbizUrl>/EditFacilityGroup</@ofbizUrl>" class="buttontext">[New Group]</a>
<br>
<br>

<table border="1" cellpadding="2" cellspacing="0">
  <tr>
    <td><div class="tabletext"><b>Party ID</b></div></td>
    <td><div class="tabletext"><b>Role Type</b></div></td>  
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>

<#list facilityRoles as facilityGroupRole>
  <#assign roleType = facilityGroupRole.getRelatedOne("RoleType")?if_exists>
  <tr valign="middle">
    <td><a href="/partymgr/control/viewprofile?party_id=${facilityGroupRole.partyId}" class="buttontext">${facilityGroupRole.partyId}</a></td>    
    <td><div class="tabletext">${roleType.description}</div></td>
    <td align="center">
      <a href="<@ofbizUrl>/removePartyFromFacilityGroup?facilityGroupId=${facilityGroupRole.facilityGroupId}&partyId=${facilityGroupRole.partyId}&roleTypeId=${facilityGroupRole.roleTypeId}</@ofbizUrl>" class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</#list>
</table>

<br>
<form method="POST" action="<@ofbizUrl>/addPartyToFacilityGroup</@ofbizUrl>" style="margin: 0;">
  <input type="hidden" name="facilityGroupId" value="${facilityGroupId}">  
  <div class="head2">Add FacilityGroup Party Role:</div>
  <div class="tabletext">
    Party ID: <input type="text" class="inputBox" size="20" name="partyId">
    Role Type:
    <select name="roleTypeId" class="selectBox"><option></option>
      <#list roles as role>
        <option value="${role.roleTypeId}">${role.description}</option>
      </#list>
    </select>
    <input type="submit" value="Add">
  </div>
</form>

<br>
<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
