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

<#-- Party IDs -->
<#assign partyId = requestParameters.partyId?if_exists>
<#assign partyIdTo = requestParameters.partyIdTo?if_exists>

<br/>
<#if hasUpdatePermission>

<TABLE border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">${uiLabelMap.PartyLink}</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width="100%" >
      <center>
      <table border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <#if partyTo?has_content && partyFrom?has_content>
          <form name="linkparty" method="post" action="<@ofbizUrl>setPartyLink</@ofbizUrl>">
          <tr>
            <td colspan="2" align="center">
              <div class="head1">
                <font color="red">
                    ${uiLabelMap.PartyLinkMessage1}
                </font>
              </div>
            </td>
          </tr>
          <tr><td colspan="2">&nbsp;</td></tr>
          <tr>
            <td align="right"><span class="tabletext">&nbsp;${uiLabelMap.PartyLink}:&nbsp;</span></td>
            <td>
              <input type="hidden" name="partyId" value="${partyFrom.partyId}"/>
              <span class="tabletext">
                  <#if personFrom?has_content>
                    ${personFrom.lastName}, ${personFrom.firstName}
                  <#elseif groupFrom?has_content>
                    ${groupFrom.groupName}
                  <#else>
                    [${uiLabelMap.PartyUnknown}]
                  </#if>
                  &nbsp;<b>[${partyFrom.partyId}]</b>
              </span>
            </td>
          </tr>
          <tr>
            <td align="right"><span class="tabletext">&nbsp;${uiLabelMap.CommonTo}:&nbsp;</span></td>
            <td>
              <input type="hidden" name="partyIdTo" value="${partyTo.partyId}"/>
              <span class="tabletext">
                  <#if personTo?has_content>
                    ${personTo.lastName}, ${personTo.firstName}
                  <#elseif groupTo?has_content>
                    ${groupTo.groupName}
                  <#else>
                    [${uiLabelMap.PartyUnknown}]
                  </#if>
                  &nbsp;<b>[${partyTo.partyId}]</b>
              </span>
            </td>
          </tr>
          <tr><td colspan="2">&nbsp;</td></tr>
          <tr>
            <td colspan="2" align="center">
              <a href="javascript:document.linkparty.submit()" class="buttontext">${uiLabelMap.CommonConfirm}</a>&nbsp;&nbsp;
            </td>
        </tr>
        </form>
        <#else>
          <form name="linkpartycnf" method="post" action="<@ofbizUrl>linkparty</@ofbizUrl>">
          <tr>
            <td><span class="tabletext">&nbsp;${uiLabelMap.PartyLink}:&nbsp;</span></td>
            <td><input type="text" class="inputBox" name="partyId" value="${partyId?if_exists}"></td>
            <td><span class="tabletext">&nbsp;${uiLabelMap.CommonTo}:&nbsp;</span></td>
            <td><input type="text" class="inputBox" name="partyIdTo" value="${partyIdTo?if_exists}"></td>
            <td>
              <a href="javascript:document.linkpartycnf.submit()" class="buttontext">${uiLabelMap.CommonLink}</a>&nbsp;&nbsp;
            </td>
          </tr>
          </form>
        </#if>
      </table>
      </center>
    </TD>
  </TR>

</TABLE>
</#if>
