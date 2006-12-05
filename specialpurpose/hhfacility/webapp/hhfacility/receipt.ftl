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

<#assign facility = parameters.facility>

<span class="tabletext">
    <ol>
        <li><a accesskey="1" href="<@ofbizUrl>/poreceipt?facilityId=${facility.facilityId?if_exists}</@ofbizUrl>">PO Receipt</a></li>
        <li><a accesskey="2" href="<@ofbizUrl>/productreceipt?facilityId=${facility.facilityId?if_exists}</@ofbizUrl>">Product Receipt</a></li>
    </ol>
</span>