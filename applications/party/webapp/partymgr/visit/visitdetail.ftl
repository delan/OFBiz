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

  <div class="head1">${uiLabelMap.PartyVisitDetail}</div>
  <br/>

  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyVisitIDSessionID}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">${visit.visitId?if_exists} / ${visit.sessionId?if_exists}</div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyVisitorId}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">
          ${visit.visitorId?default("${uiLabelMap.CommonNot} ${uiLabelMap.CommonFound}")}
        </div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyPartyIDUserLoginID}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">
          <a href="<@ofbizUrl>viewprofile?partyId=${visit.partyId?if_exists}</@ofbizUrl>" class="buttontext">${visit.partyId?if_exists}</a> / <a href="<@ofbizUrl>viewprofile?partyId=${visit.partyId?if_exists}</@ofbizUrl>" class="buttontext">${visit.userLoginId?if_exists}</a>
        </div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyUserCreated}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">${visit.userCreated?if_exists}</div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyWebApp}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">${visit.webappName?if_exists}</div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyServer}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">
          <a href="http://uptime.netcraft.com/up/graph/?site=${visit.serverIpAddress?if_exists}" class="buttontext" target="_blank">${visit.serverIpAddress?if_exists}</a> / <a href="http://uptime.netcraft.com/up/graph/?site=${visit.serverIpAddress?if_exists}" class="buttontext" target="_blank">${visit.serverHostName?if_exists}</a>
        </div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyClient}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">
          <a href="http://ws.arin.net/cgi-bin/whois.pl?queryinput=${visit.clientIpAddress?if_exists}" class="buttontext" target="_blank">${visit.clientIpAddress?if_exists}</a> / <a href="http://www.networksolutions.com/cgi-bin/whois/whois?STRING=${visit.clientHostName?if_exists}&SearchType=do" class="buttontext" target="_blank">${visit.clientHostName?if_exists}</a>
        </div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyClientUser}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">${visit.clientUser?if_exists}</div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyInitialLocale}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">${visit.initialLocale?if_exists}</div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyInitialRequest}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <a href="${visit.initialRequest?if_exists}" class="buttontext">${visit.initialRequest?if_exists}</a>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyInitialReferer}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <a href="${visit.initialReferrer?if_exists}" class="buttontext">${visit.initialReferrer?if_exists}</a>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyInitialUserAgent}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">${visit.initialUserAgent?if_exists}</div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.PartyCookie}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">${visit.cookie?if_exists}</div>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tableheadtext">${uiLabelMap.CommonFromDateThruDate}</div></td>
      <td width="5">:&nbsp;</td>
      <td width="74%" align="left">
        <div class="tabletext">${(visit.fromDate?string)?if_exists} / ${(visit.thruDate?string)?default("["+uiLabelMap.PartyStillActive+"]")}</div>
      </td>
    </tr>
  </table>

  <br/>
  <div class="head1">${uiLabelMap.PartyHitTracker}</div>
  <br/>

  <#if serverHits?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align="right">
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>visitdetail?visitId=${visitId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>visitdetail?visitId=${visitId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
            </#if>
          </b>
        </td>
      </tr>
    </table>
  </#if>

  <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td><div class="tableheadtext">${uiLabelMap.PartyContentId}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.PartyType}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.PartySize}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.PartyStartTime}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.PartyTime}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.PartyURI}</div></td>
    </tr>
    <tr>
      <td colspan="6"><hr class="sepbar"></td>
    </tr>
    <#-- set initial row color -->
    <#assign rowClass = "viewManyTR2">
    <#list serverHits[lowIndex..highIndex-1] as hit>
          <#assign serverHitType = hit.getRelatedOne("ServerHitType")?if_exists>      
      <tr class="${rowClass}">
        <td><div class="tabletext">${hit.contentId?if_exists}</div></td>
        <td><div class="tabletext">${serverHitType.get("description",locale)?if_exists}</div></td>
        <td><div class="tabletext">&nbsp;&nbsp;${hit.numOfBytes?default("?")}</div></td>
        <td><div class="tabletext">${hit.hitStartDateTime?string?if_exists}</div></td>
        <td><div class="tabletext">&nbsp;&nbsp;${hit.runningTimeMillis?if_exists}</div></td>
        <td>
          <#assign url = (hit.requestUrl)?if_exists>
          <#if url?exists>
            <#assign len = url?length>
            <#if 45 < len>
              <#assign url = url[0..45] + "...">
            </#if>
          </#if>
          <a href="${hit.requestUrl?if_exists}" class="buttontext" target="_blank">${url}</a>
        </td>
      </tr>
      <#-- toggle the row color -->
      <#if rowClass == "viewManyTR2">
        <#assign rowClass = "viewManyTR1">
      <#else>
        <#assign rowClass = "viewManyTR2">
      </#if>
    </#list>
  </table>

  <#if serverHits?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align="right">
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>visitdetail?visitId=${visitId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>visitdetail?visitId=${visitId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
            </#if>
          </b>
        </td>
      </tr>
    </table>
  </#if>

  <#if security.hasPermission("SEND_CONTROL_APPLET", session)>
    <br/>
    <div class="head1">${uiLabelMap.PartyPagePushFollowing}</div>
    <br/>

    <table border="0" cellpadding="5" cellspacing="5">
      <form name="pushPage" method="get" action="<@ofbizUrl>pushPage</@ofbizUrl>">
        <tr>
          <td><div class="tableheadtext">${uiLabelMap.PartyPushURL}</div></td>
          <td>
            <input type="hidden" name="followerSid" value="${visit.sessionId}">
            <input type="hidden" name="visitId" value="${visit.visitId}">
            <input type="input" name="pageUrl" class="inputBox">
          </td>
          <td><input type="submit" value="${uiLabelMap.CommonSubmit}" class="smallSubmit"></td>
        </tr>
        <tr>
          <td colspan="3"><hr class="sepbar"></td>
        </tr>
      </form>
      <form name="setFollower" method="get" action="<@ofbizUrl>setAppletFollower</@ofbizUrl>">
        <tr>
          <td><div class="tableheadtext">${uiLabelMap.PartyFollowSession}</div></td>
          <td>
            <input type="hidden" name="followerSid" value="${visit.sessionId}">
            <input type="hidden" name="visitId" value="${visit.visitId}">
            <input type="text" name="followSid" class="inputBox">
          </td>
          <td><input type="submit" value="${uiLabelMap.CommonSubmit}" class="smallSubmit"></td>
        </tr>
      </form>
    </table>
  </#if>
