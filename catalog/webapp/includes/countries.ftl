
<#assign geoFindMap = Static["org.ofbiz.core.util.UtilMisc"].toMap("geoTypeId", "COUNTRY")>
<#assign geoOrderList = Static["org.ofbiz.core.util.UtilMisc"].toList("geoName")>
<#assign countries = delegator.findByAndCache("Geo", geoFindMap, geoOrderList)>
<#list countries as country>
    <option value='${country.geoId}'>${country.geoName?default(country.geoId)}</option>
</#list>

