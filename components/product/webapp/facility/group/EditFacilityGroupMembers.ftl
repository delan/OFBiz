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

${pages.get("/group/FacilityGroupTabBar.ftl")}
    
    <div class="head1">Facilities <span class="head2">for&nbsp;<#if facilityGroup?exists>${(facilityGroup.facilityGroupName)?if_exists}</#if> [ID:${facilityGroupId?if_exists}]</span></div>
    <a href="<@ofbizUrl>/EditFacilityGroup</@ofbizUrl>" class="buttontext">[New Group]</a>
    <#if (activeOnly) >
        <a href="<@ofbizUrl>/EditFacilityGroupMembers?facilityGroupId=${facilityGroupId}&activeOnly=false</@ofbizUrl>" class="buttontext">[Active and Inactive]</a>
    <#else>
        <a href="<@ofbizUrl>/EditFacilityGroupMembers?facilityGroupId=${facilityGroupId}&activeOnly=true</@ofbizUrl>" class="buttontext">[Active Only]</a>
    </#if>
    <p>
    
    <#if facilityGroupId?exists && facilityGroup?exists>
        <p class="head2">Facility-Group Member Maintenance</p>
        
        <#if (facilityGroupMembers.size() > 0)>
            <table border="0" width="100%" cellpadding="2">
                <tr>
                <td align=right>
                    <b>
                    <#if (viewIndex > 0)>
                    <a href="<@ofbizUrl>/EditCategoryProducts?facilityGroupId=${facilityGroupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${(viewIndex-1)}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[Previous]</a> |
                    </#if>
                    <#if (listSize > 0)>
                        ${lowIndex+1} - ${highIndex+1} of ${listSize}
                    </#if>
                    <#if (listSize > highIndex+1)>
                        | <a href="<@ofbizUrl>/EditCategoryProducts?facilityGroupId=${facilityGroupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${(viewIndex+1)}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[Next]</a>
                    </#if>
                    </b>
                </td>
                </tr>
            </table>
        </#if>
        
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Facility Name [ID]</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time&nbsp;&amp;&nbsp;Sequence</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#if (listSize > 0)>
            <#assign line = 0>
            <#list facilityGroupMembers[lowIndex..highIndex] as facilityGroupMember>
            <#assign line = line + 1>
            <#assign facility = facilityGroupMember.getRelatedOne("Facility")>
            <tr valign="middle">
                <td><a href="<@ofbizUrl>/EditFacility?facilityId=${(facilityGroupMember)?if_exists}</@ofbizUrl>" class="buttontext"><#if facility?exists>${(facility.facilityName)?if_exists}</#if> [${(facilityGroupMember.facilityId)?if_exists}]</a></td>
                <td>
                    <#assign hasntStarted = false>
                    <#if (facilityGroupMember.getTimestamp("fromDate"))?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().before(facilityGroupMember.getTimestamp("fromDate"))> <#assign hasntStarted = true></#if>
                    <div class="tabletext"<#if hasntStarted> style="color: red;"</#if>>
                            ${(facilityGroupMember.fromDate)?if_exists}
                    </div>
                </td>
                <td align="center">
                    <#assign hasExpired = false>
                    <#if (facilityGroupMember.getTimestamp("thruDate"))?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(facilityGroupMember.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                    <FORM method=POST action="<@ofbizUrl>/updateFacilityToGroup?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex}</@ofbizUrl>" name="lineForm${line}">
                        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
                        <input type=hidden name="facilityId" value="${(facilityGroupMember.facilityId)?if_exists}">
                        <input type=hidden name="facilityGroupId" value="${(facilityGroupMember.facilityGroupId)?if_exists}">
                        <input type=hidden name="fromDate" value="${(facilityGroupMember.fromDate)?if_exists}">
                        <input type=text size="25" name="thruDate" value="${(facilityGroupMember.thruDate)?if_exists}" class="inputBox" <#if hasExpired>style="color: red;"</#if>>
                        <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(facilityGroupMember.thruDate)?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                        <input type=text size="5" name="sequenceNum" value="${(facilityGroupMember.sequenceNum)?if_exists}" class="inputBox">           
                        <INPUT type=submit value="Update" style="font-size: x-small;">
                    </FORM>
                </td>
                <td align="center">
                <a href="<@ofbizUrl>/removeFacilityFromGroup?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex}&facilityId=${(facilityGroupMember)?if_exists}&facilityGroupId=${(facilityGroupMember.facilityGroupId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(facilityGroupMember.getTimestamp("fromDate").toString())}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">
                [Delete]</a>
                </td>
            </tr>
            </#list>
        </#if>
        </table>
        
        <#if (facilityGroupMembers.size() > 0)>
            <table border="0" width="100%" cellpadding="2">
                <tr>
                <td align=right>
                    <b>
                    <#if (viewIndex > 0)>
                    <a href="<@ofbizUrl>/EditCategoryProducts?facilityGroupId=${facilityGroupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${(viewIndex-1)}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[Previous]</a> |
                    </#if>
                    <#if (listSize > 0) >
                        ${lowIndex+1} - ${highIndex+1} of ${listSize}
                    </#if>
                    <#if (listSize > highIndex+1)>
                        | <a href="<@ofbizUrl>/EditCategoryProducts?facilityGroupId=${facilityGroupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${(viewIndex+1)}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[Next]</a>
                    </#if>
                    </b>
                </td>
                </tr>
            </table>
        </#if>
        
        <br>
        <form method="POST" action="<@ofbizUrl>/addFacilityToGroup</@ofbizUrl>" style="margin: 0;" name="addFacilityGroupMemberForm">
        <input type="hidden" name="facilityGroupId" value="${facilityGroupId}">
        <input type="hidden" name="useValues" value="true">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">Add FacilityGroupMember:</div>
        <div class="tabletext">
            Facility ID: <input type=text size="20" class="inputBox" name="facilityId">
            From Date: <input type=text size="25" class="inputBox" name="fromDate">
            <a href="javascript:call_cal(document.addFacilityGroupMemberForm.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
            <input type="submit" value="Add">
        </div>
        </form>
        
        <!-- TO DO IMPLEMENT THIS
        <br>
        <form method="POST" action="<@ofbizUrl>/expireAllFacilityGroupMembers</@ofbizUrl>" style="margin: 0;">
        <input type="hidden" name="facilityGroupId" value="${facilityGroupId}">
        <input type="hidden" name="useValues" value="true">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">Expire All Facility Members:</div>
        <div class="tabletext">
            Optional Expiration Date: <input type=text size="20" name="thruDate">
            <input type="submit" value="Expire All">
        </div>
        </form>
        <br>
        <form method="POST" action="<@ofbizUrl>/removeExpiredFacilityGroupMembers</@ofbizUrl>" style="margin: 0;">
        <input type="hidden" name="facilityGroupId" value="${facilityGroupId}">
        <input type="hidden" name="useValues" value="true">
        <input type=hidden name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">Remove Expired Facility Members:</div>
        <div class="tabletext">
            Optional Expired Before Date: <input type=text size="20" name="validDate">
            <input type="submit" value="Remove Expired">
        </div>
        </form>
        -->
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
