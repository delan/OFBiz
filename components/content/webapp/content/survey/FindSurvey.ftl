<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.4 $
 *@since      3.0
-->

<#if security.hasEntityPermission("CONTENTMGR", "_VIEW", session)>

<#if surveyList?exists>
<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td width="50%"><div class="boxhead">Surveys Found</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <#if 0 < surveyList?size>
                <#if 0 < viewIndex>
                  <a href="<@ofbizUrl>/FindSurvey?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}${paramList}</@ofbizUrl>" class="submenutext">Previous</a>
                <#else>
                  <span class="submenutextdisabled">Previous</span>
                </#if>
                <#if 0 < listSize>
                  <span class="submenutextinfo">${lowIndex+1} - ${highIndex} of ${listSize}</span>
                </#if>
                <#if highIndex < listSize>
                  <a href="<@ofbizUrl>/FindSurvey?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}${paramList}</@ofbizUrl>" class="submenutextright">Next</a>
                <#else>
                  <span class="submenutextrightdisabled">Next</span>
                </#if>
              </#if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <div class="tabletext"><a href="<@ofbizUrl>/EditSurvey</@ofbizUrl>" class="buttontext">[Create New Survey]</a></div>
      <br>
      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
        <tr>
          <td align="left"><div class="tableheadtext">Survey ID</div></td>
          <td align="left"><div class="tableheadtext">Description</div></td>
          <td align="left"><div class="tableheadtext">Anonymous</div></td>
          <td align="left"><div class="tableheadtext">Multiple</div></td>
          <td align="left"><div class="tableheadtext">Update</div></td>
          <td><div class="tableheadtext">&nbsp;</div></td>
        </tr>
        <tr>
          <td colspan="10"><hr class="sepbar"></td>
        </tr>
        <#if surveyList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list surveyList[lowIndex..highIndex-1] as survey>
            <tr class="${rowClass}">
              <td><a href="<@ofbizUrl>/EditSurvey?surveyId=${survey.surveyId}</@ofbizUrl>" class="buttontext">${survey.surveyId}</a></td>
              <td><div class="tabletext">${survey.description?default("No Description")}</div></td>
              <td><div class="tabletext">${survey.isAnonymous?default("&nbsp;")}</div></td>
              <td><div class="tabletext">${survey.allowMultiple?default("&nbsp;")}</div></td>
              <td><div class="tabletext">${survey.allowUpdate?default("&nbsp;")}</div></td>
              <td align="right">
                <a href="<@ofbizUrl>/EditSurvey?surveyId=${survey.surveyId}</@ofbizUrl>" class="buttontext">Edit</a>
              </td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "viewManyTR2">
              <#assign rowClass = "viewManyTR1">
            <#else>
              <#assign rowClass = "viewManyTR2">
            </#if>
          </#list>          
        <#else>
          <tr>
            <td colspan="4"><div class="head3">No surveys found.</div></td>
          </tr>        
        </#if>
      </table>
    </td>
  </tr>
</table>
</#if>

<#else>
  <h3>You do not have permission to view this page. ("CONTENTMGR_VIEW" or "CONTENTMGR_ADMIN" needed)</h3>
</#if>
