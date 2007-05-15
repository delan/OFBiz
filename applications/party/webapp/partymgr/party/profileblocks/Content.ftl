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

  <div id="partyContent" class="screenlet">
    <div class="screenlet-title-bar">
      <h3>${uiLabelMap.PartyContent}</h3>
    </div>
    <div class="screenlet-body">
      <#if partyContent?has_content>
        <table class="basic-table" cellspacing="0">
          <#list partyContent as pContent>
            <#assign content = pContent.getRelatedOne("Content")>
            <#assign contentType = content.getRelatedOneCache("ContentType")>
            <#assign mimeType = content.getRelatedOneCache("MimeType")?if_exists>
            <#assign status = content.getRelatedOneCache("StatusItem")>
            <#assign pcPurpose = pContent.getRelatedOne("Enumeration")>
            <tr>
              <td class="button-col"><a href="<@ofbizUrl>EditPartyContents?contentId=${pContent.contentId}&partyId=${pContent.partyId}</@ofbizUrl>">${content.contentId}</a></td>
              <td>${pcPurpose.description?if_exists}</td>
              <td>${content.contentName?if_exists}</td>
              <td>${(contentType.get("description",locale))?if_exists}</td>
              <td>${(mimeType.description)?if_exists}</td>
              <td>${(status.get("description",locale))?if_exists}</td>
              <#-- <td>${contentRole.fromDate?if_exists}</td> -->
              <td class="button-col">
                <a href="<@ofbizUrl>img/${content.contentName}?imgId=${content.dataResourceId}</@ofbizUrl>">${uiLabelMap.CommonView}</a>
                <a href="<@ofbizUrl>removePartyContent/viewprofile?contentId=${pContent.contentId}&partyId=${pContent.partyId}</@ofbizUrl>">${uiLabelMap.CommonRemove}</a>
              </td>
            </tr>
          </#list>
        </table>
      <#else>
        ${uiLabelMap.PartyNoContent}
      </#if>
      <hr/>
      <div class="label">${uiLabelMap.PartyAttachContent}</div>
      <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>uploadPartyContent</@ofbizUrl>">
        <input type="hidden" name="dataCategoryId" value="PERSONAL"/>
        <input type="hidden" name="contentTypeId" value="DOCUMENT"/>
        <input type="hidden" name="statusId" value="CTNT_PUBLISHED"/>
        <input type="hidden" name="partyId" value="${partyId}"/>
        <input type="file" name="uploadedFile" size="20"/>
        <select name="contentPurposeEnumId">
          <option value="">${uiLabelMap.PartySelectPurpose}</option>
          <#list contentPurposes as contentPurpose>
            <option value="${contentPurpose.enumId}">${contentPurpose.get("description", locale)?default(contentPurpose.enumId)}</option>
          </#list>
        </select>
        <select name="roleTypeId">
          <option value="">${uiLabelMap.PartySelectRole}</option>
          <#list roles as role>
            <option value="${role.roleTypeId}">${role.get("description", locale)?default(role.roleTypeId)}</option>
          </#list>
        </select>
        <select name="mimeTypeId">
          <option value="">${uiLabelMap.PartySelectMimeType}</option>
          <#list mimeTypes as mimeType>
            <option value="${mimeType.mimeTypeId}">${mimeType.get("description", locale)?default(mimeType.mimeTypeId)}</option>
          </#list>
        </select>
        <input type="submit" value="${uiLabelMap.CommonUpload}"/>
      </form>
    </div>
  </div>
  