/*
 * $Id: SurveyWrapper.java,v 1.7 2003/12/06 00:50:20 ajzeneski Exp $
 *
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
 */
package org.ofbiz.content.survey;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilURL;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;

import freemarker.template.Template;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.ext.beans.BeansWrapper;

/**
 * Survey Wrapper - Class to render survey forms
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.7 $
 * @since      3.0
 */
public class SurveyWrapper {

    public static final String module = SurveyWrapper.class.getName();

    protected GenericDelegator delegator = null;
    protected String partyId = null;
    protected String surveyId = null;
    protected String templatePath = null;
    protected Map passThru = null;

    protected SurveyWrapper() {}

    public SurveyWrapper(GenericDelegator delegator, String partyId, String surveyId, String templatePath, Map passThru) {
        this.delegator = delegator;
        this.partyId = partyId;
        this.surveyId = surveyId;
        this.templatePath = templatePath;
        if (passThru != null) {
            this.passThru = new HashMap(passThru);
        }
        this.checkParameters();
    }

    protected void checkParameters() {
        if (delegator == null || surveyId == null || templatePath == null) {
            throw new IllegalArgumentException("Missing one or more required parameters (delegator, surveyId, templatePath");
        }
    }

    /**
     * Renders the Survey
     * @return Writer object from the parsed Freemarker Tempalte
     * @throws SurveyWrapperException
     */
    public Writer renderSurvey() throws SurveyWrapperException {
        GenericValue survey = this.getSurvey();
        List questions = this.getQuestions();
        Map templateContext = new HashMap();
        templateContext.put("partyId", partyId);
        templateContext.put("survey", survey);
        templateContext.put("surveyQuestions", questions);
        templateContext.put("surveyAnswers", this.getAnswers());
        templateContext.put("surveyResponseId", this.getResponseId());
        templateContext.put("sequenceSort", UtilMisc.toList("sequenceNum"));
        templateContext.put("additionalFields", passThru);

        Template template = this.getTemplate();
        Writer writer = new StringWriter();
        try {
            template.process(templateContext, writer);
        } catch (TemplateException e) {
            Debug.logError(e, module);
        } catch (IOException e) {
            Debug.logError(e, module);
        }
        return writer;
    }

    // returns the GenericValue object for the current Survey
    protected GenericValue getSurvey() {
        GenericValue survey = null;
        try {
            survey = delegator.findByPrimaryKey("Survey", UtilMisc.toMap("surveyId", surveyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get Survey : " + surveyId, module);
        }
        return survey;
    }

    // returns a list of SurveyQuestions (in order by sequence number) for the current Survey
    protected List getQuestions() {
        List surveyQuestions = null;
        try {
            Map fields = UtilMisc.toMap("surveyId", surveyId);
            List order = UtilMisc.toList("sequenceNum");
            surveyQuestions = delegator.findByAnd("SurveyQuestionAndAppl", fields, order);
            if (surveyQuestions != null) {
                surveyQuestions = EntityUtil.filterByDate(surveyQuestions);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get questions for survey : " + surveyId, module);
        }
        return surveyQuestions;
    }

    // returns the FTL Template object
    protected Template getTemplate() {
        URL templateUrl = UtilURL.fromResource(templatePath);

        if (templateUrl == null) {
            Debug.logError("Problem getting the template URL: " + templatePath + " not found", module);
        }

        Configuration config = Configuration.getDefaultConfiguration();
        config.setObjectWrapper(BeansWrapper.getDefaultInstance());
        try {
            config.setSetting("datetime_format", "yyyy-MM-dd HH:mm:ss.SSS");
        } catch (TemplateException e) {
            Debug.logError(e, module);
        }

        Template template = null;
        try {
            InputStreamReader templateReader = new InputStreamReader(templateUrl.openStream());
            template = new Template(templateUrl.toExternalForm(), templateReader, config);
        } catch (IOException e) {
            Debug.logError(e, "Unable to get template from URL :" + templatePath, module);
        }
        return template;
    }

    // returns the most current SurveyResponse ID for an updateable survey; null if no party or survey cannot be updated
    protected String getResponseId() {
        if (partyId == null) {
            return null;
        }

        GenericValue survey = this.getSurvey();
        if (!"Y".equals(survey.getString("allowMultiple")) || !"Y".equals(survey.getString("allowUpdate"))) {
            // can only update if multiple responses is true and the updateable flag is set
            return null;
        }

        String responseId = null;
        List responses = null;
        try {
            responses = delegator.findByAnd("SurveyResponse", UtilMisc.toMap("surveyId", surveyId, "partyId", partyId), UtilMisc.toList("-lastModifiedDate"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        if (responses != null && responses.size() > 0) {
            GenericValue response = EntityUtil.getFirst(responses);
            responseId = response.getString("surveyResponseId");
            if (responses.size() > 1) {
                Debug.logWarning("More then one response found for survey : " + surveyId + " by party : " + partyId + " using most current", module);
            }
        }

        return responseId;
    }

    // returns a Map of answers keyed on SurveyQuestion ID from the most current SurveyResponse ID
    protected Map getAnswers() {
        Map answerMap = new HashMap();
        String responseId = this.getResponseId();

        if (responseId != null) {
            List answers = null;
            try {
                answers = delegator.findByAnd("SurveyResponseAnswer", UtilMisc.toMap("surveyResponseId", responseId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }

            if (answers != null && answers.size() > 0) {
                Iterator i = answers.iterator();
                while (i.hasNext()) {
                    GenericValue answer = (GenericValue) i.next();
                    answerMap.put(answer.get("surveyQuestionId"), answer);
                }
            }
        }

        // get the pass-thru (posted form data)
        if (passThru != null && passThru.size() > 0) {
            Iterator i = passThru.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                if (key.toUpperCase().startsWith("ANSWERS_")) {
                    int splitIndex = key.indexOf('_');
                    String questionId = key.substring(splitIndex+1);
                    Map thisAnswer = new HashMap();
                    String answer = (String) passThru.remove(key);
                    thisAnswer.put("booleanResponse", answer);
                    thisAnswer.put("currencyResponse", answer);
                    thisAnswer.put("floatResponse", answer);
                    thisAnswer.put("numericResponse", answer);
                    thisAnswer.put("textResponse", answer);
                    thisAnswer.put("surveyOptionSeqId", answer);
                    // this is okay since only one will be looked at
                    answerMap.put(questionId, thisAnswer);
                }
            }
        }

        return answerMap;
    }

    class SurveyWrapperException extends GeneralException {

        public SurveyWrapperException() {
            super();
        }

        public SurveyWrapperException(String str) {
            super(str);
        }

        public SurveyWrapperException(String str, Throwable nested) {
            super(str, nested);
        }

        public SurveyWrapperException(Throwable nested) {
            super(nested);
        }
    }
}
