<#--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski
 *@version    $Revision$
 *@since      2.0
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

<div class="head1">Facility Group List</div>

<div><a href='<@ofbizUrl>/EditFacilityGroup</@ofbizUrl>' class="buttontext">[New Group]</a></div>
<br>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Facility&nbsp;Group&nbsp;Name&nbsp;[ID]</b></div></td>
    <td><div class="tabletext"><b>Facility&nbsp;Group&nbsp;Type</b></div></td>   
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<#list facilityGroups as facilityGroup>
  <#if facilityGroup.facilityGroupId?exists && facilityGroup.facilityGroupId != "_NA_">
    <#assign facilityGroupType = facilityGroup.getRelatedOne("FacilityGroupType")?if_exists>
    <tr valign="middle">
      <td><div class='tabletext'>&nbsp;<a href='<@ofbizUrl>/EditFacilityGroup?facilityGroupId=${facilityGroup.facilityGroupId?if_exists}</@ofbizUrl>' class="buttontext">${facilityGroup.facilityGroupName?if_exists} [${facilityGroup.facilityGroupId?if_exists}]</a></div></td>
      <td><div class='tabletext'>&nbsp;${facilityGroupType.description?if_exists}</div></td>
      <td><div class='tabletext'>&nbsp;${facilityGroup.description?if_exists}</div></td>
      <td>
        <a href='<@ofbizUrl>/EditFacilityGroup?facilityGroupId=${facilityGroup.facilityGroupId?if_exists}</@ofbizUrl>' class="buttontext">
        [Edit]</a>
      </td>
    </tr>
  </#if>
</#list>
</table>
<br>

<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>

