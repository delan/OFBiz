<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@version    $Revision: 1.2 $
 *@since      3.1
-->

<div class="head1">${survey.description?if_exists}</div>
<br>

<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <#list surveyQuestions as question>

    <#-- special formatting for select boxes -->
    <#assign align = "left">
    <#if (question.surveyQuestionTypeId == "BOOLEAN" || question.surveyQuestionTypeId == "OPTION")>
      <#assign align = "right">
    </#if>

    <#-- get an answer from the answerMap -->
    <#if surveyAnswers?has_content>
      <#assign answer = surveyAnswers.get(question.surveyQuestionId)?if_exists>
    </#if>

    <#-- get the question results -->
    <#if surveyResults?has_content>
      <#assign results = surveyResults.get(question.surveyQuestionId)?if_exists>
    </#if>

    <tr>

      <#-- seperator options -->
      <#if question.surveyQuestionTypeId == "SEPERATOR_TEXT">
        <td colspan="5"><div class="tabletext">${question.question?if_exists}</div></td>
      <#elseif question.surveyQuestionTypeId == "SEPERATOR_LINE">
        <td colspan="5"><hr class="sepbar"></td>
      <#else>

        <#-- standard question options -->
        <td align='right' nowrap>
          <#assign answerString = "answers">
          <#if (results._total?default(0) == 1)>
             <#assign answerString = "answer">
          </#if>
          <div class="tabletext">${question.question?if_exists} (${results._total?default(0)?string.number} ${answerString})</div>
          <#if question.hint?has_content>
            <div class="tabletext">${question.hint}</div>
          </#if>
        </td>
        <td width='1'>&nbsp;</td>

        <td align="${align}">
          <#if question.surveyQuestionTypeId == "BOOLEAN">
            <#assign selectedOption = (answer.booleanResponse)?default("Y")>
            <div class="tabletext"><nobr>
              <#if "Y" == selectedOption><b>==>&nbsp;<font color="red"></#if>Y<#if "Y" == selectedOption></font></b></#if>&nbsp;[${results._yes_total?default(0)?string("#")} / ${results._yes_percent?default(0)?string("#")}%]
            </nobr></div>
            <div class="tabletext"><nobr>
              <#if "N" == selectedOption><b>==>&nbsp;<font color="red"></#if>N<#if "N" == selectedOption></font></b></#if>&nbsp;[${results._no_total?default(0)?string("#")} / ${results._no_percent?default(0)?string("#")}%]
            </nobr></div>
          <#elseif question.surveyQuestionTypeId == "TEXTAREA">
            <div class="tabletext">${(answer.textResponse)?if_exists}</div>
          <#elseif question.surveyQuestionTypeId == "TEXT_SHORT">
            <div class="tabletext">${(answer.textResponse)?if_exists}</div>
          <#elseif question.surveyQuestionTypeId == "TEXT_LONG">
            <div class="tabletext">${(answer.textResponse)?if_exists}</div>
          <#elseif question.surveyQuestionTypeId == "EMAIL">
            <div class="tabletext">${(answer.textResponse)?if_exists}</div>
          <#elseif question.surveyQuestionTypeId == "URL">
            <div class="tabletext">${(answer.textResponse)?if_exists}</div>
          <#elseif question.surveyQuestionTypeId == "DATE">
            <div class="tabletext">${(answer.textResponse)?if_exists}</div>
          <#elseif question.surveyQuestionTypeId == "CREDIT_CARD">
            <div class="tabletext">${(answer.textResponse)?if_exists}</div>
          <#elseif question.surveyQuestionTypeId == "GIFT_CARD">
            <div class="tabletext">${(answer.textResponse)?if_exists}</div>
          <#elseif question.surveyQuestionTypeId == "NUMBER_CURRENCY">
            <div class="tabletext">${answer.currencyResponse?number?default(0)?string.currency}</div>
          <#elseif question.surveyQuestionTypeId == "NUMBER_FLOAT">
            <div class="tabletext">${answer.floatResponse?number?default(0)?string("#")}</div>
          <#elseif question.surveyQuestionTypeId == "NUMBER_LONG">
            <div class="tabletext">${answer.numericResponse?number?default(0)?string("#")}&nbsp;[Tally: ${results._tally?default(0)?string("#")} / Average: ${results._average?default(0)?string("#")}]</div>
          <#elseif question.surveyQuestionTypeId == "PASSWORD">
            <div class="tabletext">[Not Shown]</div>

          <#elseif question.surveyQuestionTypeId == "OPTION">
            <#assign options = question.getRelated("SurveyQuestionOption", sequenceSort)?if_exists>
            <#assign selectedOption = (answer.surveyOptionSeqId)?default("_NA_")>
            <#if options?has_content>
              <#list options as option>
                <#assign optionResults = results.get(option.surveyOptionSeqId)?if_exists>
                  <div class="tabletext"><nobr>
                    <#if option.surveyOptionSeqId == selectedOption><b>==>&nbsp;<font color="red"></#if>
                    ${option.description?if_exists}
                    <#if option.surveyOptionSeqId == selectedOption></font></b></#if>
                    &nbsp;[${optionResults._total?default(0)?string("#")} / ${optionResults._percent?default(0?string("#"))}%]
                  </nobr></div>
              </#list>
            </#if>
          <#else>
            <div class="tabletext">Unsupported question type : ${question.surveyQuestionTypeId}</div>
          </#if>
        </td>
        <td width="90%">&nbsp;</td>
      </#if>
    </tr>
  </#list>
</table>