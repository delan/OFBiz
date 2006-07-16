<#--

Copyright 2001-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->
   
    <div class="head1">${uiLabelMap.ProductFacilities} <span class="head2">${uiLabelMap.CommonFor}&nbsp;<#if facilityGroup?exists>${(facilityGroup.facilityGroupName)?if_exists}</#if> [${uiLabelMap.CommonId}:${facilityGroupId?if_exists}]</span></div>
    <a href="<@ofbizUrl>EditFacilityGroup</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewGroup}]</a>
    <#if (activeOnly) >
        <a href="<@ofbizUrl>EditFacilityGroupMembers?facilityGroupId=${facilityGroupId}&activeOnly=false</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonActiveInactive}]</a>
    <#else>
        <a href="<@ofbizUrl>EditFacilityGroupMembers?facilityGroupId=${facilityGroupId}&activeOnly=true</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonActiveOnly}]</a>
    </#if>
    <p>
    
    <#if facilityGroupId?exists && facilityGroup?exists>
        <p class="head2">${uiLabelMap.ProductFacilityGroupMemberMaintenance}</p>
        
        <#if (facilityGroupMembers.size() > 0)>
            <table border="0" width="100%" cellpadding="2">
                <tr>
                <td align="right">
                    <b>
                    <#if (viewIndex > 0)>
                    <a href="<@ofbizUrl>EditCategoryProducts?facilityGroupId=${facilityGroupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${(viewIndex-1)}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
                    </#if>
                    <#if (listSize > 0)>
                        ${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}
                    </#if>
                    <#if (listSize > highIndex)>
                        | <a href="<@ofbizUrl>EditCategoryProducts?facilityGroupId=${facilityGroupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${(viewIndex+1)}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
                    </#if>
                    </b>
                </td>
                </tr>
            </table>
        </#if>
        
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>${uiLabelMap.ProductFacilityNameId}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonFromDateTime}</b></div></td>
            <td align="center"><div class="tabletext"><b>${uiLabelMap.ProductThruDateTimeSequence}</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#if (listSize > 0)>
            <#assign line = 0>
            <#list facilityGroupMembers[lowIndex..highIndex-1] as facilityGroupMember>
            <#assign line = line + 1>
            <#assign facility = facilityGroupMember.getRelatedOne("Facility")>
            <tr valign="middle">
                <td><a href="<@ofbizUrl>EditFacility?facilityId=${(facilityGroupMember.facilityId)?if_exists}</@ofbizUrl>" class="buttontext"><#if facility?exists>${(facility.facilityName)?if_exists}</#if> [${(facilityGroupMember.facilityId)?if_exists}]</a></td>
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
                    <FORM method="post" action="<@ofbizUrl>updateFacilityToGroup?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex}</@ofbizUrl>" name="lineForm${line}">
                        <input type="hidden" name="activeOnly" value="${activeOnly.toString()}">
                        <input type="hidden" name="facilityId" value="${(facilityGroupMember.facilityId)?if_exists}">
                        <input type="hidden" name="facilityGroupId" value="${(facilityGroupMember.facilityGroupId)?if_exists}">
                        <input type="hidden" name="fromDate" value="${(facilityGroupMember.fromDate)?if_exists}">
                        <input type="text" size="25" name="thruDate" value="${(facilityGroupMember.thruDate)?if_exists}" class="inputBox" <#if hasExpired>style="color: red;"</#if>>
                        <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(facilityGroupMember.thruDate)?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                        <input type="text" size="5" name="sequenceNum" value="${(facilityGroupMember.sequenceNum)?if_exists}" class="inputBox">           
                        <INPUT type="submit" value="${uiLabelMap.CommonUpdate}" style="font-size: x-small;">
                    </FORM>
                </td>
                <td align="center">
                <a href="<@ofbizUrl>removeFacilityFromGroup?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex}&facilityId=${(facilityGroupMember.facilityId)?if_exists}&facilityGroupId=${(facilityGroupMember.facilityGroupId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(facilityGroupMember.getTimestamp("fromDate").toString())}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">
                [${uiLabelMap.CommonDelete}]</a>
                </td>
            </tr>
            </#list>
        </#if>
        </table>
        
        <#if (facilityGroupMembers.size() > 0)>
            <table border="0" width="100%" cellpadding="2">
                <tr>
                <td align="right">
                    <b>
                    <#if (viewIndex > 0)>
                    <a href="<@ofbizUrl>EditCategoryProducts?facilityGroupId=${facilityGroupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${(viewIndex-1)}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
                    </#if>
                    <#if (listSize > 0) >
                        ${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}
                    </#if>
                    <#if (listSize > highIndex)>
                        | <a href="<@ofbizUrl>EditCategoryProducts?facilityGroupId=${facilityGroupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${(viewIndex+1)}&activeOnly=${activeOnly.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
                    </#if>
                    </b>
                </td>
                </tr>
            </table>
        </#if>
        
        <br/>
        <form method="post" action="<@ofbizUrl>addFacilityToGroup</@ofbizUrl>" style="margin: 0;" name="addFacilityGroupMemberForm">
        <input type="hidden" name="facilityGroupId" value="${facilityGroupId}">
        <input type="hidden" name="useValues" value="true">
        <input type="hidden" name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">${uiLabelMap.ProductAddFacilityGroupMember}:</div>
        <div class="tabletext">
            ${uiLabelMap.ProductFacilityId} : <input type="text" size="20" class="inputBox" name="facilityId">
            ${uiLabelMap.CommonFromDate} : <input type="text" size="25" class="inputBox" name="fromDate">
            <a href="javascript:call_cal(document.addFacilityGroupMemberForm.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
            <input type="submit" value="${uiLabelMap.CommonAdd}">
        </div>
        </form>
        
        <!-- TO DO IMPLEMENT THIS
        <br/>
        <form method="post" action="<@ofbizUrl>expireAllFacilityGroupMembers</@ofbizUrl>" style="margin: 0;">
        <input type="hidden" name="facilityGroupId" value="${facilityGroupId}">
        <input type="hidden" name="useValues" value="true">
        <input type="hidden" name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">Expire All Facility Members:</div>
        <div class="tabletext">
            Optional Expiration Date: <input type="text" size="20" name="thruDate">
            <input type="submit" value="Expire All">
        </div>
        </form>
        <br/>
        <form method="post" action="<@ofbizUrl>removeExpiredFacilityGroupMembers</@ofbizUrl>" style="margin: 0;">
        <input type="hidden" name="facilityGroupId" value="${facilityGroupId}">
        <input type="hidden" name="useValues" value="true">
        <input type="hidden" name="activeOnly" value="${activeOnly.toString()}">
        
        <div class="head2">Remove Expired Facility Members:</div>
        <div class="tabletext">
            Optional Expired Before Date: <input type="text" size="20" name="validDate">
            <input type="submit" value="Remove Expired">
        </div>
        </form>
        -->
    </#if>
