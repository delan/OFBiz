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
 *@version    $Rev:$
 *@since      3.0
-->

<#if additionalFields?has_content>
  <#list additionalFields.keySet() as field>
    <input type="hidden" name="${field}" value="${additionalFields.get(field)}">
  </#list>
</#if>

<#-- update response -->
<#if surveyResponseId?has_content>
  <input type="hidden" name="surveyResponseId" value="${surveyResponseId}">
</#if>

<#-- party ID -->
<#if partyId?has_content>
  <input type="hidden" name="partyId" value="${partyId}">
</#if>

<#-- survey ID -->
<input type="hidden" name="surveyId" value="${survey.surveyId}">

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

    <tr>
      <#-- seperator options -->
      <#if question.surveyQuestionTypeId == "SEPERATOR_TEXT">
        <td colspan="5"><div class="tabletext">${question.question?if_exists}</div></td>
      <#elseif question.surveyQuestionTypeId == "SEPERATOR_LINE">
        <td colspan="5"><hr class="sepbar"></td>
      <#else>
        <#-- standard question options -->
        <td align='right' nowrap>
          <div class="tabletext">${question.question?if_exists}</div>
          <#if question.hint?has_content>
            <div class="tabletext">${question.hint}</div>
          </#if>
        </td>
        <td width='1'>&nbsp;</td>
        <td align="${align}">
          <#if question.surveyQuestionTypeId == "BOOLEAN">
            <#assign selectedOption = (answer.booleanResponse)?default("Y")>
            <select class="selectBox" name="answers_${question.surveyQuestionId}">
              <#if question.requiredField?default("N") != "Y">
                <option value=""></option>
              </#if>
              <option <#if "Y" == selectedOption>SELECTED</#if>>Y</option>
              <option <#if "N" == selectedOption>SELECTED</#if>>N</option>
            </select>
          <#elseif question.surveyQuestionTypeId == "TEXTAREA">
            <textarea class="textAreaBox" cols="40" rows="5" name="answers_${question.surveyQuestionId}">${(answer.textResponse)?if_exists}</textarea>
          <#elseif question.surveyQuestionTypeId == "TEXT_SHORT">
            <input type="text" size="15" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.textResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "TEXT_LONG">
            <input type="text" size="35" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.textResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "EMAIL">
            <input type="text" size="30" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.textResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "URL">
            <input type="text" size="40" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.textResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "DATE">
            <input type="text" size="12" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.textResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "CREDIT_CARD">
            <input type="text" size="20" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.textResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "GIFT_CARD">
            <input type="text" size="20" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.textResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "NUMBER_CURRENCY">
            <input type="text" size="6" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.currencyResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "NUMBER_FLOAT">
            <input type="text" size="6" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.floatResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "NUMBER_LONG">
            <input type="text" size="6" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.numericResponse?string("#"))?if_exists}">
          <#elseif question.surveyQuestionTypeId == "PASSWORD">
            <input type="password" size="30" class="textBox" name="answers_${question.surveyQuestionId}" value="${(answer.textResponse)?if_exists}">
          <#elseif question.surveyQuestionTypeId == "OPTION">
            <#assign options = question.getRelated("SurveyQuestionOption", sequenceSort)?if_exists>
            <#assign selectedOption = (answer.surveyOptionSeqId)?default("_NA_")>
            <select class="selectBox" name="answers_${question.surveyQuestionId}">
              <#if question.requiredField?default("N") != "Y">
                <option value=""></option>
              </#if>
              <#if options?has_content>
                <#list options as option>
                  <option value="${option.surveyOptionSeqId}" <#if option.surveyOptionSeqId == selectedOption>SELECTED</#if>>${option.description?if_exists}</option>
                </#list>
              <#else>
                <option value="">Nothing to choose</option>
              </#if>
            </select>
          <#else>
            <div class="tabletext">Unsupported question type : ${question.surveyQuestionTypeId}</div>
          </#if>
        </td>
        <td nowrap>
          <#if question.requiredField?default("N") == "Y">
            <span class="tabletext">*</span>
          <#else>
            <span class="tabletext">[optional]</span>
          </#if>
        </td>
        <td width="90%">&nbsp;</td>
      </#if>
    </tr>
  </#list>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="2"><input type="submit" value="Submit"></td>
  </tr>
</table>