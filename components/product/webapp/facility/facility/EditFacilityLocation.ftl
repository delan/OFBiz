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

${pages.get("/facility/FacilityTabBar.ftl")}
    
    <div class="head1">Location <span class="head2">for&nbsp;<#if facility?exists>${(facility.facilityName)?if_exists}</#if> [ID:${facilityId?if_exists}]</span></div>
    <a href="<@ofbizUrl>/EditFacility</@ofbizUrl>" class="buttontext">[New Facility]</a>
    <a href="<@ofbizUrl>/EditFacilityLocation</@ofbizUrl>" class="buttontext">[New Facility Location]</a>
    <#if facilityId?exists && locationSeqId?exists>
        <a href="<@ofbizUrl>/EditInventoryItem?facilityId=${facilityId}&locationSeqId=${locationSeqId}</@ofbizUrl>" class="buttontext">[New Inventory Item]</a>
    </#if>
    
    <#if facilityId?exists && !(facilityLocation?exists)> 
        <form action="<@ofbizUrl>/CreateFacilityLocation</@ofbizUrl>" method=POST style="margin: 0;">
        <table border="0" cellpadding="2" cellspacing="0">
        <input type="hidden" name="facilityId" value="${facilityId}">  
    <#elseif facilityLocation?exists>
        <form action="<@ofbizUrl>/UpdateFacilityLocation</@ofbizUrl>" method=POST style="margin: 0;">
        <table border="0" cellpadding="2" cellspacing="0">
        <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
        <input type="hidden" name="locationSeqId" value="${locationSeqId}">
        <tr>
            <td align=right><div class="tabletext">Facility ID</div></td>
            <td>&nbsp;</td>
            <td>
            <b>${facilityId?if_exists}</b>
            </td>
        </tr>
        <tr>
            <td align="right"><div class="tabletext">Location SeqID</div></td>
            <td>&nbsp;</td>
            <td>
            <b>${locationSeqId}</b>
            </td>
        </tr>
    <#else>
        <div class="head1">Cannot create location without facilityId</div>
    </#if>
    
    <#if facilityId?exists>      
        <tr>
            <td width="26%" align=right><div class="tabletext">Area</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="areaId" value="${(facilityLocation.areaId)?if_exists}" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Aisle</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="aisleId" value="${(facilityLocation.aisleId)?if_exists}" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Section</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="sectionId" value="${(facilityLocation.sectionId)?if_exists}" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Level</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="levelId" value="${(facilityLocation.levelId)?if_exists}" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Position</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="positionId" value="${(facilityLocation.positionId)?if_exists}" size="19" maxlength="20"></td>
        </tr>    
        <tr>
            <td colspan="2">&nbsp;</td>
            <td colspan="1" align=left><input type="submit" value="Update"></td>
        </tr>
    </table>
    </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
