<#--
 *  Copyright (c) 2003-2006 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Rev$
 *@since      3.0
-->

<#macro renderSurveyQuestionText surveyQuestionAndAppl>
  <div class="tabletext">${surveyQuestionAndAppl.question?if_exists}</div>
  <#if surveyQuestionAndAppl.hint?has_content>
    <div class="tabletext">${surveyQuestionAndAppl.hint}</div>
  </#if>
</#macro>

<#macro renderSurveyQuestionRequired surveyQuestionAndAppl>
  <#if surveyQuestionAndAppl.requiredField?default("N") == "Y">
    <span class="tabletext">*[required]</span>
  <#else/>
    <span class="tabletext">[optional]</span>
  </#if>
</#macro>

<#macro renderSurveyQuestionInput surveyQuestionAndAppl questionFieldName>
  <#if surveyQuestionAndAppl.surveyQuestionTypeId == "BOOLEAN">
    <#assign selectedOption = (answer.booleanResponse)?default("Y")>
    <select class="selectBox" name="${questionFieldName}">
      <#if surveyQuestionAndAppl.requiredField?default("N") != "Y">
        <option value=""></option>
      </#if>
      <option <#if "Y" == selectedOption>selected="selected"</#if>>Y</option>
      <option <#if "N" == selectedOption>selected="selected"</#if>>N</option>
    </select>
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "TEXTAREA"/>
    <textarea class="textAreaBox" cols="40" rows="5" name="${questionFieldName}">${(answer.textResponse)?if_exists}</textarea>
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "TEXT_SHORT"/>
    <input type="text" size="15" class="inputBox" name="${questionFieldName}" value="${(answer.textResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "TEXT_LONG"/>
    <input type="text" size="35" class="inputBox" name="${questionFieldName}" value="${(answer.textResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "EMAIL"/>
    <input type="text" size="30" class="inputBox" name="${questionFieldName}" value="${(answer.textResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "URL"/>
    <input type="text" size="40" class="inputBox" name="${questionFieldName}" value="${(answer.textResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "DATE"/>
    <input type="text" size="12" class="inputBox" name="${questionFieldName}" value="${(answer.textResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "CREDIT_CARD"/>
    <input type="text" size="20" class="inputBox" name="${questionFieldName}" value="${(answer.textResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "GIFT_CARD"/>
    <input type="text" size="20" class="inputBox" name="${questionFieldName}" value="${(answer.textResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "NUMBER_CURRENCY"/>
    <input type="text" size="6" class="inputBox" name="${questionFieldName}" value="${(answer.currencyResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "NUMBER_FLOAT"/>
    <input type="text" size="6" class="inputBox" name="${questionFieldName}" value="${(answer.floatResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "NUMBER_LONG"/>
    <input type="text" size="6" class="inputBox" name="${questionFieldName}" value="${(answer.numericResponse?string("#"))?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "PASSWORD"/>
    <input type="password" size="30" class="textBox" name="${questionFieldName}" value="${(answer.textResponse)?if_exists}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "CONTENT"/>
     <#if (answer.contentId)?has_content>
      <#assign content = answer.getRelatedOne("Content")>
      <a href="/content/control/img?imgId=${content.dataResourceId}" class="buttontext">${answer.contentId}</a>&nbsp;-&nbsp;${content.contentName?if_exists}&nbsp;&nbsp;&nbsp;
    </#if>
    <input type="file" size="15" name="${questionFieldName}">
  <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "OPTION"/>
    <#assign options = surveyQuestionAndAppl.getRelated("SurveyQuestionOption", sequenceSort)?if_exists/>
    <#assign selectedOption = (answer.surveyOptionSeqId)?default("_NA_")/>
    <select class="selectBox" name="${questionFieldName}">
      <#if surveyQuestionAndAppl.requiredField?default("N") != "Y">
        <option value=""></option>
      </#if>
      <#if options?has_content>
        <#list options as option>
          <option value="${option.surveyOptionSeqId}" <#if option.surveyOptionSeqId == selectedOption>selected="selected"</#if>>${option.description?if_exists}</option>
        </#list>
      <#else>
        <option value="">Nothing to choose</option>
      </#if>
    </select>
  <#else/>
    <div class="tabletext">Unsupported question type : ${surveyQuestionAndAppl.surveyQuestionTypeId}</div>
  </#if>
</#macro>

<#if additionalFields?has_content>
  <#list additionalFields.keySet() as field>
    <input type="hidden" name="${field}" value="${additionalFields.get(field)}"/>
  </#list>
</#if>

<#-- update response -->
<#if surveyResponseId?has_content>
  <input type="hidden" name="surveyResponseId" value="${surveyResponseId}"/>
</#if>

<#-- party ID -->
<#if partyId?has_content>
  <input type="hidden" name="partyId" value="${partyId}"/>
</#if>

<#-- survey ID -->
<input type="hidden" name="surveyId" value="${survey.surveyId}"/>

<div class="head1">${survey.description?if_exists}</div>
<br/>

<#if survey.comments?has_content>
<div class="tabletext">${survey.comments}</div>
<br/>
</#if>

<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <#assign lastSurveyMultiRespId = ""/>
  <#list surveyQuestionAndAppls as surveyQuestionAndAppl>
    <#-- Get and setup MultiResp info for this question -->
    <#assign openMultiRespHeader = false/>
    <#assign closeMultiRespHeader = false/>
    <#assign surveyMultiResp = surveyQuestionAndAppl.getRelatedOneCache("SurveyMultiResp")?if_exists/>
    <#if surveyMultiResp?has_content>
      <#assign surveyMultiRespColumnList = surveyMultiResp.getRelatedCache("SurveyMultiRespColumn", Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum"))/>
    
      <#if lastSurveyMultiRespId == "">
        <#assign openMultiRespHeader = true/>
      <#elseif lastSurveyMultiRespId != surveyMultiResp.surveyMultiRespId>
        <#assign openMultiRespHeader = true/>
        <#assign closeMultiRespHeader = true/>
      </#if>
      <#assign lastSurveyMultiRespId = surveyMultiResp.surveyMultiRespId/>
    <#else/>
      <#if lastSurveyMultiRespId?has_content><#assign closeMultiRespHeader = true/></#if>
      <#assign lastSurveyMultiRespId = ""/>
    </#if>
    
    <#-- this is before the rest because it will be done if the current row is not a MultiResp (or is different MultiResp) but the last row was... -->
    <#if closeMultiRespHeader>
          </table>
        </td>
      </tr>
    </#if>
  
    <#-- -->
    <#if openMultiRespHeader>
      <tr width="100%">
        <td colspan="5" width="100%">
          <table width="100%" border="1" cellpadding="1" cellspacing="0">
            <tr>
              <td align="left">
                <div class="tableheadtext">${surveyMultiResp.multiRespTitle?if_exists}</div>
              </td>
              <#list surveyMultiRespColumnList as surveyMultiRespColumn>
                <td align="center">
                  <div class="tableheadtext">${surveyMultiRespColumn.columnTitle?if_exists}</div>
                </td>
              </#list>
              <td><div class="tableheadtext">Required?</div></td><#-- placeholder for required/optional column -->
            </tr>
    </#if>
  
  <#if surveyMultiResp?has_content>
    <tr>
      <td align="left">
          <@renderSurveyQuestionText surveyQuestionAndAppl=surveyQuestionAndAppl/>
      </td>
      <#list surveyMultiRespColumnList as surveyMultiRespColumn>
        <td align="center">
          <#assign questionFieldName = "answers_" + surveyQuestionAndAppl.surveyQuestionId + "_" + surveyMultiRespColumn.surveyMultiRespColId/>
          <@renderSurveyQuestionInput surveyQuestionAndAppl=surveyQuestionAndAppl questionFieldName=questionFieldName/>
        </td>
      </#list>
      <td>
        <@renderSurveyQuestionRequired surveyQuestionAndAppl=surveyQuestionAndAppl/>
      </td>
    </tr>
  <#else/>
    <#-- special formatting for select boxes -->
    <#assign align = "left"/>
    <#if surveyQuestionAndAppl?exists && surveyQuestionAndAppl.surveyQuestionTypeId?has_content>
    	<#if (surveyQuestionAndAppl.surveyQuestionTypeId == "BOOLEAN" || surveyQuestionAndAppl.surveyQuestionTypeId == "CONTENT" || surveyQuestionAndAppl.surveyQuestionTypeId == "OPTION")>
      		<#assign align = "right"/>
    	</#if>
    </#if>
    <#-- get an answer from the answerMap -->
    <#if surveyAnswers?has_content>
      <#assign answer = surveyAnswers.get(surveyQuestionAndAppl.surveyQuestionId)?if_exists/>
    </#if>

    <tr>
    <#if surveyQuestionAndAppl?exists && surveyQuestionAndAppl.surveyQuestionTypeId?has_content>
      <#-- seperator options -->
      <#if surveyQuestionAndAppl.surveyQuestionTypeId == "SEPERATOR_TEXT">
        <td colspan="5"><div class="tabletext">${surveyQuestionAndAppl.question?if_exists}</div></td>
      <#elseif surveyQuestionAndAppl.surveyQuestionTypeId == "SEPERATOR_LINE"/>
        <td colspan="5"><hr class="sepbar"/></td>
      <#else/>
        <#-- standard question options -->
        <td align="right">
          <@renderSurveyQuestionText surveyQuestionAndAppl=surveyQuestionAndAppl/>
        </td>
        <td width="1">&nbsp;</td>
        <td align="${align}">
          <#assign questionFieldName = "answers_" + surveyQuestionAndAppl.surveyQuestionId/>
          <@renderSurveyQuestionInput surveyQuestionAndAppl=surveyQuestionAndAppl questionFieldName=questionFieldName/>
        </td>
        <td>
          <@renderSurveyQuestionRequired surveyQuestionAndAppl=surveyQuestionAndAppl/>
        </td>
        <td width="20%">&nbsp;</td>
      </#if>
    </#if>
    
    </tr>
  </#if>
  </#list>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="2"><input type="submit" value="<#if survey.submitCaption?has_content>${survey.submitCaption}<#else/>Submit</#if>"/></td>
  </tr>
</table>
