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
<#assign unselectedClassName = "buttontext">
<#assign selectedClassMap = {page.scheduleTabButtonItem?default("void") : "buttontext"}>

<#if facilityId?has_content>
  <div class='tabContainer'>
    <a href="<@ofbizUrl>ScheduleShipmentRouteSegment?facilityId=${facilityId}</@ofbizUrl>" class="${selectedClassMap.ScheduleTabButton?default(unselectedClassName)}">${uiLabelMap.ProductSchedule}</a>
    <a href="<@ofbizUrl>Labels?facilityId=${facilityId}</@ofbizUrl>" class="${selectedClassMap.LabelsTabButton?default(unselectedClassName)}">${uiLabelMap.ProductLabels}</a>
  </div>
</#if>
