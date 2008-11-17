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

<#if currentPortalPage?has_content>

<div id="manage-portal-toolbar">
  <ul>
    <#if currentPortalPage.portalName?has_content>
      <li id="portal-page-name">
        ${currentPortalPage.portalName}
      </li>
    </#if>

    <#if (portalPages.size() > 1)>
      <li id="portal-page-select">
        <select name="selectPortal" onchange="window.location=this[this.selectedIndex].value;">
          <option>${uiLabelMap.CommonSelectPortalPage}</option>
          <#list portalPages as portalPage>
            <#if (currentPortalPage.portalPageId != portalPage.portalPageId)>
              <option value="<@ofbizUrl>dashboard?portalPageId=${portalPage.portalPageId}</@ofbizUrl>">${portalPage.portalName}</option>
            </#if>
          </#list>                          
        </select>
      </li>
    </#if>
    
    <li id="manage-portal-page">
      <a href="<@ofbizUrl>ManagePortalPages?portalPageId=${currentPortalPage.portalPageId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonManagePortalPages}</a>
    </li>

    <li id="config-on-off">
     ${uiLabelMap.CommonConfigure}
     <#if configurePortalPage?has_content>
         ON | <a href="<@ofbizUrl>dashboard?portalPageId=${currentPortalPage.portalPageId}</@ofbizUrl>" class="buttontext">OFF</a>
     <#else>
         <a href="<@ofbizUrl>dashboard?portalPageId=${currentPortalPage.portalPageId}&configurePortalPage=true</@ofbizUrl>" class="buttontext">ON</a> | OFF
     </#if>
    </li>

    <#if configurePortalPage?has_content>
      <li id="add-portlet">
        <a href="<@ofbizUrl>AddPortlet?portalPageId=${currentPortalPage.portalPageId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonAddAPortlet}</a>
      </li>
    </#if>

  </ul>
  <br class="clear"/>
</div>

<table width="100%">
  <tr>
    <#list portalPageColumnList?if_exists as portalPageColumn>
      <td style="vertical-align: top;"<#if portalPageColumn.columnWidthPixels?has_content> width="${portalPageColumn.columnWidthPixels}"</#if>>
      <#assign firstInColumn = true/>
      <#list portalPagePortletViewList as portlet>
        <#if (!portlet.columnSeqId?has_content && portalPageColumn_index == 0) || (portlet.columnSeqId?if_exists == portalPageColumn.columnSeqId)>
          <#if portlet.screenName?has_content>
            <#if configurePortalPage?has_content>
              <#assign portletUrlLink = "portalPageId="+currentPortalPage.portalPageId+"&amp;portalPortletId="+portlet.portalPortletId+"&amp;portletSeqId="+portlet.portletSeqId+"&amp;configurePortalPage=true" />
      
              <div class="portlet-config">
              <div class="portlet-config-title-bar">
                <ul>
                  <li class="title">Portlet : ${portlet.portletName}</li>
                  <li class="remove"><a href="<@ofbizUrl>deletePortalPagePortlet?${portletUrlLink}</@ofbizUrl>" title="${uiLabelMap.CommonRemovePortlet}">&nbsp;&nbsp;&nbsp;</a></li>

                  <#if (portlet.editFormName?has_content && portlet.editFormLocation?has_content)>
                    <li class="edit"><a href="<@ofbizUrl>EditPortlet?${portletUrlLink}</@ofbizUrl>" title="edit">&nbsp;&nbsp;&nbsp;</a></li>
                  </#if>  

                  <#if portlet_has_next> <#-- TODO: this doesn't take into account that later items in the list might be in a different column -->
                    <li class="move-down"><a href="<@ofbizUrl>updatePortalPagePortlet?${portletUrlLink}&amp;sequenceNum=${portlet.sequenceNum?default(0) + 1}</@ofbizUrl>" title="${uiLabelMap.CommonMovePortletDown}">&nbsp;&nbsp;&nbsp;</a></li>
                  </#if>  
                  <#if !firstInColumn>
                    <li class="move-up"><a href="<@ofbizUrl>updatePortalPagePortlet?${portletUrlLink}&amp;sequenceNum=${portlet.sequenceNum?default(1)-1}</@ofbizUrl>" title="${uiLabelMap.CommonMovePortletUp}">&nbsp;&nbsp;&nbsp;</a></li>
                  </#if>  
                  <#if portalPageColumn_has_next>
                    <li class="move-right"><a href="<@ofbizUrl>updatePortalPagePortlet?${portletUrlLink}&amp;columnSeqId=${portalPageColumnList[portalPageColumn_index+1].columnSeqId}</@ofbizUrl>" title="${uiLabelMap.CommonMovePortletRight}">&nbsp;&nbsp;&nbsp;</a></li>
                  </#if>  
                  <#if (portalPageColumn_index > 0)>
                    <li class="move-left"><a href="<@ofbizUrl>updatePortalPagePortlet?${portletUrlLink}&amp;columnSeqId=${portalPageColumnList[portalPageColumn_index-1].columnSeqId}</@ofbizUrl>" title="${uiLabelMap.CommonMovePortletLeft}">&nbsp;&nbsp;&nbsp;</a></li>
                  </#if>  
                  <#if !firstInColumn>
                    <li class="move-top"><a href="<@ofbizUrl>updatePortalPagePortlet?${portletUrlLink}&amp;sequenceNum=0</@ofbizUrl>" title="${uiLabelMap.CommonMovePortletTop}">&nbsp;&nbsp;&nbsp;</a></li>
                  </#if>  
                  <#if portlet_has_next> <#-- TODO: this doesn't take into account that later items in the list might be in a different column -->
                    <li class="move-bottom"><a href="<@ofbizUrl>updatePortalPagePortlet?${portletUrlLink}&amp;sequenceNum=${portalPagePortletViewList.size()}</@ofbizUrl>" title="${uiLabelMap.CommonMovePortletBottom}">&nbsp;&nbsp;&nbsp;</a></li>
                  </#if>  
                  <#if (portalPages.size() > 1)>
                    <li>
                    <select name="moveToPortal" onchange="window.location=this[this.selectedIndex].value;">
                      <option value="">${uiLabelMap.CommonMoveToPortalPage}</option>
  
                      <#list portalPages as portalPage>
                        <#if (currentPortalPage.portalName != portalPage.portalName)> 
                          <option value="<@ofbizUrl>movePortletToPortalPage?${portletUrlLink}&amp;newPortalPageId=${portalPage.portalPageId}</@ofbizUrl>">${portalPage.portalName}</option>
                        </#if>
                      </#list>                          
                    </select>
                    </li>
                  </#if>
                </ul>
                <br class="clear"/>
              </div> 
              <div class="screenlet-body">
            </#if>
        
            <#assign screenFileName = portlet.screenLocation + "#" + portlet.screenName/>
            <div>
            ${setRequestAttribute("portalPageId", currentPortalPage.portalPageId)}
            ${setRequestAttribute("portalPortletId", portlet.portalPortletId)}
            ${setRequestAttribute("portletSeqId", portlet.portletSeqId)}
            
            ${screens.render(screenFileName)}
            </div>
          
            <#if configurePortalPage?has_content>
                </div>
              </div>
            </#if>
          </#if>
          <#assign firstInColumn = false/>
        </#if>
      </#list>
      <#if portalPageColumn_has_next>
        <td width="5px">&nbsp;</td>
      </#if>
    </#list>
  </tr>
</table>

<#else/>
<h2>No portal page data found. You may not have the necessary seed or other data for it.</h2>
</#if>
