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

<#if requestAttributes.uiLabelMap?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#assign previousParams = sessionAttributes._PREVIOUS_PARAMS_?if_exists>
<#if previousParams?has_content>
  <#assign previousParams = "?" + previousParams>
</#if>

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#if username != "">
  <#assign focusName = false>
<#else>
  <#assign focusName = true>
</#if>

<table width='300' border='0' cellpadding='0' cellspacing='0' align='center'>
  <tr>    
    <td width='100%' valign='top'>
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign='middle' align='center'>
                  <div class="boxhead">${uiLabelMap.CommonRegistered}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td align="center" valign="middle" width='100%'>
                  <form method="post" action="<@ofbizUrl>login${previousParams?if_exists}</@ofbizUrl>" name="loginform" style='margin: 0;'>
                    <table width='100%' border='0' cellpadding='0' cellspacing='2'>
                      <tr>
                        <td align="right">
                          <span class="tabletext">${uiLabelMap.CommonUsername}&nbsp;</span>
                        </td>
                        <td align="left">
                          <input type="text" class="inputBox" name="USERNAME" value="${username}" size="20"/>
                        </td>
                      </tr>
                      <tr>
                        <td align="right">
                          <span class="tabletext">${uiLabelMap.CommonPassword}&nbsp;</span>
                        </td>
                        <td align="left">
                          <input type="password" class="inputBox" name="PASSWORD" value="" size="20"/>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2" align="center">
                          <!--<a href="javascript:document.loginform.submit()" class="buttontext">[Login]</a>-->
                          <input type="submit" value="${uiLabelMap.CommonLogin}" class="loginButton"/>
                        </td>
                      </tr>
                    </table>
                  </form>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<script language="JavaScript" type="text/javascript">
<!--
  <#if focusName>
    document.loginform.USERNAME.focus();
  <#else>
    document.loginform.PASSWORD.focus();
  </#if>
//-->
</script>
