
<#assign countries = Static["org.ofbiz.commonapp.common.CommonWorkers"].getCountryList(delegator)>
<#list countries as country>
    <option value='${country.geoId}'>${country.geoName?default(country.geoId)}</option>
</#list>

