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
package org.ofbiz.core.widgetimpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.core.util.UtilHttp;
import org.ofbiz.core.widget.form.FormFactory;
import org.ofbiz.core.widget.form.FormStringRenderer;
import org.ofbiz.core.widget.form.ModelForm;
import org.xml.sax.SAXException;


/**
 * Widget Library - HTML Form Wrapper class - makes it easy to do the setup and render of a form
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
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
        context.put("parameters", UtilHttp.getParameterMap(request));
        context.put("isError", Boolean.FALSE);
    }
    
    public String renderFormString() {
        StringBuffer buffer = new StringBuffer();
        modelForm.renderFormString(buffer, context, renderer);
        return buffer.toString();
    }

    public void setIsError(boolean isError) {
        this.context.put("isError", new Boolean(isError));
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
