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
<#if geoChart?has_content>
    <#if geoChart.dataSourceId?has_content>
      <#if geoChart.dataSourceId == "GEOPT_GOOGLE">
        <div id="<#if geoChart.id?has_content>${geoChart.id}<#else>map_canvas</#if>" style="border:1px solid #979797; background-color:#e5e3df; width:${geoChart.width}px; height:${geoChart.height}px; margin:2em auto;">
          <div style="padding:1em; color:gray;">${uiLabelMap.CommonLoading}</div>
        </div>
        <#assign defaultUrl = "https." + request.getServerName()>
        <#assign defaultGogleMapKey = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", defaultUrl)>
        <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=${defaultGogleMapKey}" type="text/javascript"></script>
        <script type="text/javascript">
          if (GBrowserIsCompatible()) {
            var map = new GMap2(document.getElementById("<#if geoChart.id?has_content>${geoChart.id}<#else>map_canvas</#if>"));
            <#if geoChart.center?has_content>
              map.setCenter(new GLatLng(${geoChart.center.lat?c}, ${geoChart.center.lon?c}), ${geoChart.center.zoom});
            <#else>
              map.setCenter(new GLatLng(37.4419, -122.1419), 12);
            </#if>
            map.setUIToDefault();
            <#list geoChart.points as point>            
              map.addOverlay(new GMarker(new GLatLng(${point.lat?c}, ${point.lon?c})));             
            </#list>
          }
        </script>
      <#elseif  geoChart.dataSourceId == "GEOPT_YAHOO">
      <#elseif  geoChart.dataSourceId == "GEOPT_MICROSOFT">
      <#elseif  geoChart.dataSourceId == "GEOPT_MAPTP">
      </#if>
    </#if>
<#else>
  <h2>${uiLabelMap.CommonNoGeolocationAvailable}</h2>
</#if>
