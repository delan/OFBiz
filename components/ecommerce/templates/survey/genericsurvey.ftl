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
 *@version    $Revision: 1.5 $
 *@since      3.0
-->

<#if additionalFields?has_content>
  <#list additionalFields.keySet() as field>
    <input type="hidden" name="${field}" value="${additionalFields.get(field)}">
  </#list>
</#if>
<input type="hidden" name="surveyId" value="${survey.surveyId}">
<div class="head2">${survey.description?if_exists}</div>
<br>

<table border="0" cellpadding="2" cellspacing="0">
  <#list surveyQuestions as question>
    <tr>
      <td align='right'><div class="tabletext">${question.question}</div></td>
      <td width='1'>&nbsp;</td>
      <td>
        <#if question.surveyQuestionTypeId == "BOOLEAN">
          <select class="selectBox" name="answers_${question.surveyQuestionId}">
            <option>Y</option>
            <option>N</option>
          </select>
        <#elseif question.surveyQuestionTypeId == "TEXTAREA">
          <textarea class="textAreaBox" cols="40" rows="5" name="answers_${question.surveyQuestionId}"></textarea>
        <#elseif question.surveyQuestionTypeId == "TEXT_SHORT">
          <input type="text" size="15" class="textBox" name="answers_${question.surveyQuestionId}">
        <#elseif question.surveyQuestionTypeId == "TEXT_LONG">
          <input type="text" size="35" class="textBox" name="answers_${question.surveyQuestionId}">
        <#elseif question.surveyQuestionTypeId == "EMAIL">
          <input type="text" size="30" class="textBox" name="answers_${question.surveyQuestionId}">
        <#elseif question.surveyQuestionTypeId == "URL">
          <input type="text" size="40" class="textBox" name="answers_${question.surveyQuestionId}">
        <#elseif question.surveyQuestionTypeId == "DATE">
          <input type="text" size="12" class="textBox" name="answers_${question.surveyQuestionId}">
        <#elseif question.surveyQuestionTypeId == "CREDIT_CARD">
          <input type="text" size="20" class="textBox" name="answers_${question.surveyQuestionId}">
        <#elseif question.surveyQuestionTypeId == "GIFT_CARD">
          <input type="text" size="20" class="textBox" name="answers_${question.surveyQuestionId}">
        <#elseif question.surveyQuestionTypeId == "NUMBER_CURRENCY">
          <input type="text" size="6" class="textBox" name="answers_${question.surveyQuestionId}">
          <#elseif question.surveyQuestionTypeId == "NUMBER_FLOAT">
          <input type="text" size="6" class="textBox" name="answers_${question.surveyQuestionId}">
          <#elseif question.surveyQuestionTypeId == "NUMBER_LONG">
          <input type="text" size="6" class="textBox" name="answers_${question.surveyQuestionId}">
        <#elseif question.surveyQuestionTypeId == "PASSWORD">
          <input type="password" size="30" class="textBox" name="answers_${question.surveyQuestionId}">
        <#elseif question.surveyQuestionTypeId == "OPTION">
          <div class="tabletext">Question type OPTION is not yet supported</div>
        <#else>
          <div class="tabletext">Unsupported question type : ${question.surveyQuestionTypeId}</div>
        </#if>
        <#if question.requiredField?default("N") == "Y">
          <span class="tabletext">*</span>
        <#else>
          <span class="tabletext">[optional]</span>
        </#if>
      </td>
    </tr>
  </#list>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td><input type="submit" value="Submit"></td>
  </tr>
</table>