
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#-- generic address information -->
<#assign toName = (postalFields.toName)?if_exists>
<#if !toName?has_content && person?exists && person?has_content>
  <#assign toName = "">
  <#if person.personalTitle?has_content><#assign toName = person.personalTitle + " "></#if>
  <#assign toName = toName + person.firstName + " ">
  <#if person.middleName?has_content><#assign toName = toName + person.middleName + " "></#if>
  <#assign toName = toName + person.lastName>
  <#if person.suffix?has_content><#assign toName = toName + " " + person.suffix></#if>
</#if>

<tr>
  <td width="26%" align=right valign=middle><div class="tabletext">${uiLabelMap.PartyToName}</div></td>
  <td width="5">&nbsp;</td>
  <td width="74%">
    <input type="text" class="inputBox" size="30" maxlength="60" name="toName" value="${toName}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
  </td>
</tr>
<tr>
  <td width="26%" align=right valign=middle><div class="tabletext">${uiLabelMap.PartyAttentionName}</div></td>
  <td width="5">&nbsp;</td>
  <td width="74%">
    <input type="text" class="inputBox" size="30" maxlength="60" name="attnName" value="${(postalFields.attnName)?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
  </td>
</tr>
<tr>
  <td width="26%" align=right valign=middle><div class="tabletext">${uiLabelMap.PartyAddressLine1}</div></td>
  <td width="5">&nbsp;</td>
  <td width="74%">
    <input type="text" class="inputBox" size="30" maxlength="30" name="address1" value="${(postalFields.address1)?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
  *</td>
</tr>
<tr>
  <td width="26%" align=right valign=middle><div class="tabletext">${uiLabelMap.PartyAddressLine2}</div></td>
  <td width="5">&nbsp;</td>
  <td width="74%">
    <input type="text" class="inputBox" size="30" maxlength="30" name="address2" value="${(postalFields.address2)?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
  </td>
</tr>
<tr>
  <td width="26%" align=right valign=middle><div class="tabletext">${uiLabelMap.PartyCity}</div></td>
  <td width="5">&nbsp;</td>
  <td width="74%">
    <input type="text" class="inputBox" size="30" maxlength="30" name="city" value="${(postalFields.city)?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
  *</td>
</tr>
<tr>
  <td width="26%" align=right valign=middle><div class="tabletext">${uiLabelMap.PartyState}</div></td>
  <td width="5">&nbsp;</td>
  <td width="74%">
    <select name="stateProvinceGeoId" class="selectBox" <#if requestParameters.useShipAddr?exists>disabled</#if>>
      <#if (postalFields.stateProvinceGeoId)?exists>
        <option>${postalFields.stateProvinceGeoId}</option>
        <option value="${postalFields.stateProvinceGeoId}">---</option>
      <#else>
        <option value="">${uiLabelMap.PartyNoState}</option>
      </#if>
      ${pages.get("/includes/states.ftl")}
    </select>
  </td>
</tr>
<tr>
  <td width="26%" align=right valign=middle><div class="tabletext">${uiLabelMap.PartyZipCode}</div></td>
  <td width="5">&nbsp;</td>
  <td width="74%">
    <input type="text" class="inputBox" size="12" maxlength="10" name="postalCode" value="${(postalFields.postalCode)?if_exists}" <#if requestParameters.useShipAddr?exists>disabled</#if>>
  *</td>
</tr>
<tr>
  <td width="26%" align=right valign=middle><div class="tabletext">${uiLabelMap.PartyCountry}</div></td>
  <td width="5">&nbsp;</td>
  <td width="74%">
    <select name="countryGeoId" class="selectBox" <#if requestParameters.useShipAddr?exists>disabled</#if>>
      <#if (postalFields.countryGeoId)?exists>
        <option>${postalFields.countryGeoId}</option>
        <option value="${postalFields.countryGeoId}">---</option>
      </#if>
      ${pages.get("/includes/countries.ftl")}
    </select>
  *</td>
</tr>
<tr>
  <td width="26%" align=right valign=middle><div class="tabletext">${uiLabelMap.PartyAllowSolicitation}?</div></td>
  <td width="5">&nbsp;</td>
  <td width="74%">
    <select name="allowSolicitation" class='selectBox' <#if requestParameters.useShipAddr?exists>disabled</#if>>
      <#if (partyContactMech.allowSolicitation)?exists>
        <option>${partyContactMech.allowSolicitation}</option>
        <option value="${partyContactMech.allowSolicitation}">---</option>
      </#if>
      <option>Y</option><option>N</option>
    </select>
  </td>
</tr>
