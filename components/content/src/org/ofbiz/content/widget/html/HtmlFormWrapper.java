/*
 * $Id$
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.content.widget.html;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.widget.form.FormFactory;
import org.ofbiz.content.widget.form.FormStringRenderer;
import org.ofbiz.content.widget.form.ModelForm;

import org.xml.sax.SAXException;


/**
 * Widget Library - HTML Form Wrapper class - makes it easy to do the setup and render of a form
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.2
 */
public class HtmlFormWrapper {
    
    public static final String module = HtmlFormWrapper.class.getName();
    
    protected String resourceName;
    protected String formName;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected ModelForm modelForm;
    protected FormStringRenderer renderer;
    protected Map context;

    protected HtmlFormWrapper() {}

    public HtmlFormWrapper(String resourceName, String formName, HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SAXException, ParserConfigurationException {
        this.resourceName = resourceName;
        this.formName = formName;
        this.request = request;
        this.response = response;
        
        this.modelForm = FormFactory.getFormFromWebappContext(resourceName, formName, request);

        this.renderer = new HtmlFormRenderer(request, response);
        
        this.context = new HashMap();
        Map parameterMap = UtilHttp.getParameterMap(request);
        context.put("parameters", parameterMap);
        
        //make sure the locale is in the context
        context.put("locale", UtilHttp.getLocale(request));
        
        // if there was an error message, this is an error
        if (UtilValidate.isNotEmpty((String) request.getAttribute("_ERROR_MESSAGE_"))) {
            context.put("isError", Boolean.TRUE);
        } else {
            context.put("isError", Boolean.FALSE);
        }
        
        // if a parameter was passed saying this is an error, it is an error
        if ("true".equals((String) parameterMap.get("isError"))) {
            context.put("isError", Boolean.TRUE);
        }
        
        Map uiLabelMap = (Map) request.getAttribute("uiLabelMap");
        Debug.logInfo("Got uiLabelMap: " + uiLabelMap, module);
        if (uiLabelMap != null && uiLabelMap.size() > 0 && context.get("uiLabelMap") == null) {
            context.put("uiLabelMap", uiLabelMap);
        }
    }
    
    public String renderFormString() {
        StringBuffer buffer = new StringBuffer();
        modelForm.renderFormString(buffer, context, renderer);
        return buffer.toString();
    }

    /** 
     * Tells the form library whether this is a response to an error or not.
     * Defaults on initialization according to the presense of an errorMessage
     * in the request or if an isError parameter was passed to the page with 
     * the value "true". If true then the prefilled values will come from the
     * parameters Map instead of the value Map. 
     */
    public void setIsError(boolean isError) {
        this.context.put("isError", new Boolean(isError));
    }
    
    public boolean getIsError() {
        Boolean isErrorBoolean = (Boolean) this.context.get("isError");
        if (isErrorBoolean == null) {
            return false;
        } else {
            return isErrorBoolean.booleanValue();
        }
    }
    
    /**
     * The "useRequestParameters" value in the form context tells the form library
     * to use the request parameters to fill in values instead of the value map.
     * This is generally used when it is an empty form to pre-set inital values.
     * This is automatically set to false for list and multi forms. For related
     * functionality see the setIsError method.
     * 
     * @param useRequestParameters
     */
    public void setUseRequestParameters(boolean useRequestParameters) {
        this.context.put("useRequestParameters", new Boolean(useRequestParameters));
    }
    
    public boolean getUseRequestParameters() {
        Boolean useRequestParametersBoolean = (Boolean) this.context.get("useRequestParameters");
        if (useRequestParametersBoolean == null) {
            return false;
        } else {
            return useRequestParametersBoolean.booleanValue();
        }
    }
    
    public void setFormOverrideName(String formName) {
        this.context.put("formName", formName);
    }
    
    public void putInContext(String name, Object value) {
        this.context.put(name, value);
    }
    
    public Object getFromContext(String name) {
        return this.context.get(name);
    }
    
    public ModelForm getModelForm() {
        return modelForm;
    }

    public FormStringRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(FormStringRenderer renderer) {
        this.renderer = renderer;
    }
}
