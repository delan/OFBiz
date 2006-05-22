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

<div class="head1">
    ${uiLabelMap.ManufacturingEditCalendarExceptionWeekFor}&nbsp; 
    <span class='head2'>
        <#if (techDataCalendar.description)?has_content>"${(techDataCalendar.get("description",locale))}"</#if> 
        [${uiLabelMap.CommonId}:${techDataCalendar.calendarId?if_exists}]
    </span>
</div>
<br/>
<#if techDataCalendar?has_content>
    ${listCalendarExceptionWeekWrapper.renderFormString(context)}
    <br/>
    <hr class="sepbar">
    <#if calendarExceptionWeek?has_content>
        ${updateCalendarExceptionWeekWrapper.renderFormString(context)}
        <br/>
        <hr class="sepbar">
    </#if>
    ${addCalendarExceptionWeekWrapper.renderFormString(context)}
</#if>
<br/>