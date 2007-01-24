<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#assign unselectedLeftClassName = "headerButtonLeft">
<#assign unselectedRightClassName = "headerButtonRight">
<#assign selectedLeftClassMap = {headerItem?default("void") : "headerButtonLeftSelected"}>
<#assign selectedRightClassMap = {headerItem?default("void") : "headerButtonRightSelected"}>

<div class="apptitle">${uiLabelMap.ExampleApplication}</div>
<div class="row">
    <div class="col"><a href="<@ofbizUrl>main</@ofbizUrl>" class="${selectedLeftClassMap.main?default(unselectedLeftClassName)}">${uiLabelMap.CommonMain}</a></div>
    <div class="col"><a href="<@ofbizUrl>FindExample</@ofbizUrl>" class="${selectedLeftClassMap.Example?default(unselectedLeftClassName)}">${uiLabelMap.ExampleExample}</a></div>
    <div class="col"><a href="<@ofbizUrl>FindExampleFeature</@ofbizUrl>" class="${selectedLeftClassMap.ExampleFeature?default(unselectedLeftClassName)}">${uiLabelMap.ExampleFeature}</a></div>

    <#if userLogin?has_content>
        <div class="col-right"><a href="<@ofbizUrl>logout</@ofbizUrl>" class="${selectedRightClassMap.logout?default(unselectedRightClassName)}">${uiLabelMap.CommonLogout}</a></div>
    <#else>
        <div class="col-right"><a href='<@ofbizUrl>${checkLoginUrl?if_exists}</@ofbizUrl>' class='${selectedRightClassMap.login?default(unselectedRightClassName)}'>${uiLabelMap.CommonLogin}</a></div>
    </#if>
    <div class="col-fill">&nbsp;</div>
</div>
