
<#assign states = Static["org.ofbiz.commonapp.common.CommonWorkers"].getStateList(delegator)>
<#list states as state>
    <option value='${state.geoId}'>${state.geoName?default(state.geoId)}</option>
</#list>

