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
 *@version    $Revision: 1.2 $
 *@since      2.2
-->

<#if hasPermission>

${pages.get("/facility/FacilityTabBar.ftl")}
    
    <div class="head1">Find Locations <span class="head2">for&nbsp;<#if facility?exists>${(facility.facilityName)?if_exists}</#if> [ID:${facilityId?if_exists}]</span></div>
    <a href="<@ofbizUrl>/EditFacility</@ofbizUrl>" class="buttontext">[New Facility]</a>
    <a href="<@ofbizUrl>/EditFacilityLocation?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="buttontext">[New Facility Location]</a>
        
    <form action="<@ofbizUrl>/FindFacilityLocation</@ofbizUrl>" method="GET" style="margin: 0;">
        <table border="0" cellpadding="2" cellspacing="0">
        <#if !(facilityId?exists)>
            <tr>
                <td width="26%" align=right><div class="tabletext">Facility</div></td>
                <td>&nbsp;</td>
                <td width="74%"><input type="text" class="inputBox" value="" size="19" maxlength="20"></td>
            </tr>
        <#else>
            <input type="hidden" name="facilityId" value="${facilityId}">
        </#if>
        <tr>
            <td width="26%" align=right><div class="tabletext">Location SeqID</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="locationSeqId" value="" size="19" maxlength="20"></td>
        </tr>
        <tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Area</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="areaId" value="" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Aisle</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="aisleId" value="" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Section</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="sectionId" value="" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Level</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="levelId" value="" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">Position</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="positionId" value="" size="19" maxlength="20"></td>
        </tr>             
        <tr>
            <td colspan="2">&nbsp;</td>
            <td colspan="1" align=left><input type="submit" name="look_up" value="Find"></td>
        </tr>
        </table>
    </form>
    
    <#if foundLocations?exists>
        <br>
        <span class="head1">Found:&nbsp;</span><span class="head2"><b>${foundLocations.size()}</b>&nbsp;Location(s) for&nbsp;<#if facility?exists>${(facility.facilityName)?if_exists}</#if> [ID:${facilityId?if_exists}]</span>
        <table border="1" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Facility</b></div></td>
            <td><div class="tabletext"><b>SeqID</b></div></td>
            <td><div class="tabletext"><b>Type</b></div></td>
            <td><div class="tabletext"><b>Area</b></div></td>
            <td><div class="tabletext"><b>Aisle</b></div></td>
            <td><div class="tabletext"><b>Section</b></div></td>
            <td><div class="tabletext"><b>Level</b></div></td>
            <td><div class="tabletext"><b>Position</b></div></td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <#if itemId?exists>
                <td>&nbsp;</td>
            </#if>
        </tr>
        <#list foundLocations as location>
        <#assign locationTypeEnum = location.getRelatedOneCache("TypeEnumeration")?if_exists>
        <tr>
            <td><div class="tabletext"><a href="<@ofbizUrl>/EditFacility?facilityId=${(location.facilityId)?if_exists}</@ofbizUrl>" class="buttontext">&nbsp;${(location.facilityId)?if_exists}</a></div></td>
            <td><div class="tabletext">&nbsp;<a href="<@ofbizUrl>/EditFacilityLocation?facilityId=${facilityId}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>" class="buttontext">${(location.locationSeqId)?if_exists}</a></div></td>
            <td><div class="tabletext">&nbsp;${(locationTypeEnum.description)?default(location.locationTypeEnumId?if_exists)}</div></td>
            <td><div class="tabletext">&nbsp;${(location.areaId)?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;${(location.aisleId)?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;${(location.sectionId)?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;${(location.levelId)?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;${(location.positionId)?if_exists}</div></td>       
            <td>
            <a href="<@ofbizUrl>/EditInventoryItem?facilityId=${(location.facilityId)?if_exists}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>" class="buttontext">[New Inventory Item]</a>
            </td>
            <#if itemId?exists>
                <td>
                <a href="<@ofbizUrl>/UpdateInventoryItem?inventoryItemId=${itemId}&facilityId=${facilityId}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>" class="buttontext">[Set Item ${itemId}]</a>
                </td>
            </#if>   
            <td>          
            <a href="<@ofbizUrl>/EditFacilityLocation?facilityId=${(location.facilityId)?if_exists}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>" class="buttontext">[Edit]</a>
            </td>     
        </tr>
        </#list>
        </table>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
