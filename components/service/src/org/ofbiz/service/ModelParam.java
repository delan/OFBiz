/*
 * $Id: ModelParam.java,v 1.3 2004/07/27 18:11:35 ajzeneski Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 *
 */
package org.ofbiz.service;

import java.util.List;

/**
 * Generic Service Model Parameter
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jon</a>
 * @version    $Revision: 1.3 $
 * @since      2.0
 */
public class ModelParam {

    /** Parameter name */
    public String name;

    /** Paramater type */
    public String type;

    /** Parameter mode (IN/OUT/INOUT) */
    public String mode;
    
    /** The form label */
    public String formLabel;
    
    /** The entity name */
    public String entityName;
    
    /** The entity field name */
    public String fieldName;

    /** Parameter prefix for creating an attribute Map */
    public String stringMapPrefix;

    /** Parameter suffix for creating an attribute List */
    public String stringListSuffix;

    /** Validation methods */
    public List validators;

    /** Is this Parameter required or optional? Default to false, or required */
    public boolean optional = false;
    public boolean overrideOptional = false;

    /** Is this parameter to be displayed via the form tool? */
    public boolean formDisplay = true;
    public boolean overrideFormDisplay = false;
    
    /** Is this Parameter set internally? */
    public boolean internal = false;
    
    public ModelParam() {}
    
    public ModelParam(ModelParam param) {
        this.name = param.name;
        this.type = param.type;
        this.mode = param.mode;
        this.formLabel = param.formLabel;
        this.entityName = param.entityName;
        this.fieldName = param.fieldName;
        this.stringMapPrefix = param.stringMapPrefix;
        this.stringListSuffix = param.stringListSuffix;
        this.validators = param.validators;
        this.optional = param.optional;
        this.overrideOptional = param.overrideOptional;
        this.formDisplay = param.formDisplay;
        this.overrideFormDisplay = param.overrideFormDisplay;
        this.internal = param.internal;
    }
        
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(name + "::");
        buf.append(type + "::");
        buf.append(mode + "::");
        buf.append(formLabel + "::");
        buf.append(entityName + "::");
        buf.append(fieldName + "::");
        buf.append(stringMapPrefix + "::");
        buf.append(stringListSuffix + "::");
        buf.append(validators.toString() + "::");
        buf.append(optional + "::");
        buf.append(overrideOptional + "::");
        buf.append(formDisplay + "::");
        buf.append(overrideFormDisplay + "::");
        buf.append(internal);
        return buf.toString();
    }
    
    public boolean equals(ModelParam model) {
        if (model.name.equals(this.name))
            return true;
        return false;
    }

    class ModelParamValidator {

        protected String className;
        protected String methodName;

        public ModelParamValidator(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public String toString() {
            return className + "::" + methodName;
        }
    }
}

