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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Revision: 1.5 $
 *@since      3.0
-->

<#if security.hasEntityPermission("CONTENTMGR", "_VIEW", session)>

  ${pages.get("/survey/SurveyTabBar.ftl")}
  <div class="head1">Survey Questions - <span class="head2">ID: ${requestParameters.surveyId?if_exists}</div>
  <br><br>
  <table border="1" cellpadding='2' cellspacing='0'>
    <tr>
      <td><div class="tableheadtext">ID</div></td>
      <td><div class="tableheadtext">Type</div></td>
      <td><div class="tableheadtext">Category</div></td>
      <td><div class="tableheadtext">Description</div></td>
      <td><div class="tableheadtext">Question</div></td>
      <td><div class="tableheadtext">Required</div></td>
      <td><div class="tableheadtext">Seq #</div></td>
      <td><div class="tableheadtext">&nbsp;</div></td>
      <td><div class="tableheadtext">&nbsp;</div></td>
      <td><div class="tableheadtext">&nbsp;</div></td>
    </tr>

    <#list surveyQuestionList as question>
      <#assign questionType = question.getRelatedOne("SurveyQuestionType")>
      <#assign questionCat = question.getRelatedOne("SurveyQuestionCategory")>
      <form method="post" action="<@ofbizUrl>/updateSurveyQuestionAppl</@ofbizUrl>">
        <input type="hidden" name="surveyId" value="${question.surveyId}">
        <input type="hidden" name="surveyQuestionId" value="${question.surveyQuestionId}">
        <input type="hidden" name="fromDate" value="${question.fromDate}">
        <tr valign="middle">
          <td><div class="tabletext">${question.surveyQuestionId}</div></td>
          <td><div class="tabletext">${questionType.description}</div></td>
          <td><div class="tabletext">${questionCat.description}</div></td>
          <td><div class="tabletext">${question.description?if_exists}</div></td>
          <td><div class="tabletext">${question.question?if_exists}</div></td>
          <td>
            <select class="selectBox" name="requiredField">
              <option>${question.requiredField?default("N")}</option>
              <option value="N">----</option>
              <option>Y</option>
              <option>N</option>
            </select>
          </td>
          <td><input type="text" name="sequenceNum" size="5" class="textBox" value="${question.sequenceNum?if_exists}">
          <td><input type="submit" value="Update">
          <td><a href="<@ofbizUrl>/EditSurveyQuestions?surveyId=${requestParameters.surveyId}&surveyQuestionId=${question.surveyQuestionId}</@ofbizUrl>" class="buttontext">[Edit]</a>
          <td><a href="<@ofbizUrl>/removeSurveyQuestionAppl?surveyId=${question.surveyId}&surveyQuestionId=${question.surveyQuestionId}&fromDate=${question.fromDate}</@ofbizUrl>" class="buttontext">[Remove]</a>
        </tr>
      </form>
    </#list>
  </table>
  <br>

  <#if requestParameters.newCategory?default("N") == "Y">
    <div class="head2">Create Question Category:</div>
    <a href="<@ofbizUrl>/EditSurveyQuestions?surveyId=${requestParameters.surveyId}</@ofbizUrl>" class="buttontext">[New Question]</a>
    <br><br>
    ${createSurveyQuestionCategoryWrapper.renderFormString()}
  <#else>
    <#if surveyQuestionId?has_content>
      <div class="head2">Edit Question:</div>
      <a href="<@ofbizUrl>/EditSurveyQuestions?surveyId=${requestParameters.surveyId}</@ofbizUrl>" class="buttontext">[New Question]</a>
    <#else>
      <div class="head2">Create New Question:</div>
    </#if>
    <a href="<@ofbizUrl>/EditSurveyQuestions?surveyId=${requestParameters.surveyId}&newCategory=Y</@ofbizUrl>" class="buttontext">[New Question Category]</a>
    <br><br>
    ${createSurveyQuestionWrapper.renderFormString()}
  </#if>

  <#if (surveyQuestion?has_content && surveyQuestion.surveyQuestionTypeId?default("") == "OPTION")>
    <br>
    <hr class="sepbar">
    <br>
    <div class="head1">Survey Options - <span class="head2">ID: ${surveyQuestion.surveyQuestionId?if_exists}</div>
    <br><br>
    <table border="1" cellpadding='2' cellspacing='0'>
      <tr>
        <td><div class="tableheadtext">Description</div></td>
        <td><div class="tableheadtext">Seq #</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
      </tr>

      <#list questionOptions as option>
        <tr valign="middle">
          <td><div class="tabletext">${option.description?if_exists}</div></td>
          <td><div class="tabletext">${option.sequenceNum?if_exists}</div></td>
          <td><a href="<@ofbizUrl>/EditSurveyQuestions?surveyId=${requestParameters.surveyId}&surveyQuestionId=${option.surveyQuestionId}&surveyOptionSeqId=${option.surveyOptionSeqId}</@ofbizUrl>" class="buttontext">[Edit]</a>
          <td><a href="<@ofbizUrl>/removeSurveyQuestionAppl?surveyId=${requestParameters.surveyId}&surveyQuestionId=${option.surveyQuestionId}&surveyOptionSeqId=${option.surveyOptionSeqId}</@ofbizUrl>" class="buttontext">[Remove]</a>
        </tr>
      </#list>
    </table>
    <br>
    <#if !surveyQuestionOption?has_content>
      <div class="head2">Create Question Option:</div>
    <#else>
      <div class="head2">Edit Question Option:</div>
      <a href="<@ofbizUrl>/EditSurveyQuestions?surveyId=${requestParameters.surveyId}&surveyQuestionId=${surveyQuestionOption.surveyQuestionId}</@ofbizUrl>" class="buttontext">[New Option]</a>
    </#if>
    ${createSurveyOptionWrapper.renderFormString()}
  </#if>
<#else>
  <h3>You do not have permission to view this page. ("CONTENTMGR_VIEW" or "CONTENTMGR_ADMIN" needed)</h3>
</#if>
