/*
 * $Id: SurveyWrapper.java,v 1.2 2003/11/19 21:48:50 ajzeneski Exp $
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
 * @version    $Revision: 1.2 $
 * @since      3.0
 */
public class SurveyWrapper {

    public static final String module = SurveyWrapper.class.getName();

    protected GenericDelegator delegator = null;
    protected String surveyId = null;
    protected String templatePath = null;
    protected Map passThru = null;

    protected SurveyWrapper() {}

    public SurveyWrapper(GenericDelegator delegator, String surveyId, String templatePath, Map passThru) {
        this.delegator = delegator;
        this.surveyId = surveyId;
        this.templatePath = templatePath;
        if (passThru != null) {
            this.passThru = new HashMap(passThru);
        }
    }

    public String renderSurvey(String formAction, String formName) throws SurveyWrapperException {
        GenericValue survey = this.getSurvey();
        List questions = this.getQuestions();
        Map templateContext = new HashMap();
        templateContext.put("formAction", formAction);
        templateContext.put("formName", formName);
        templateContext.put("survey", survey);
        templateContext.put("surveyQuestions", questions);
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
        return writer.toString();
    }

    protected GenericValue getSurvey() {
        GenericValue survey = null;
        try {
            survey = delegator.findByPrimaryKey("Survey", UtilMisc.toMap("surveyId", surveyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get Survey : " + surveyId, module);
        }
        return survey;
    }

    protected List getQuestions() {
        GenericValue survey = this.getSurvey();
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
