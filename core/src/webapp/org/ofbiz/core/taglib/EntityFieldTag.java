/*
 * EntityFieldTag.java
 *
 * Created on October 2, 2001, 12:15 PM
 */

package org.ofbiz.core.taglib;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.model.ModelEntity;
import org.ofbiz.core.entity.model.ModelField;
import org.ofbiz.core.entity.model.ModelFieldType;
import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> Tag to Print Localized Entity Fields
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project (www.ofbiz.org) and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version 1.0
 *@created 
 */
public class EntityFieldTag extends TagSupport {

    String field = null;
    String type = null;
    String attribute = null;
    String defaultStr = "";
    String prefix = null;
    String suffix = null;
    
    public String getAttribute() {
        return attribute;
    }
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDefault() {
        return defaultStr;
    }
    public void setDefault(String defaultStr) {
        this.defaultStr = defaultStr;
    }

    public int doStartTag() throws JspTagException {
        String javaType = null;
        String fieldObjectType = null;
        Object fieldObject = null;

        /* TYPE and FIELD should not be used together. TYPE defines the type of an object.
         *  When FIELD is defined, type is assumed to be 'ValueObject'.
         */

        // We should be a ValueObject
        if (type == null) {
            // Get the ValueObject from PageContext.
            GenericValue valueObject = (GenericValue) pageContext.findAttribute(attribute);
            if (valueObject == null) {
                fieldObject = defaultStr;
                fieldObjectType = "comment"; // Default for NULL objects.
                javaType = "java.lang.String";
            } else {
                // Get the Delegator from the ValueObject.
                GenericDelegator delegator = null;
                try {
                    delegator = valueObject.getDelegator();
                } catch (IllegalStateException e) {
                    throw new JspTagException("Delegator not found in ValueObject.");
                }

                // Get the Entity Model from the ValueObject
                ModelEntity entityModel = null;
                try {
                    entityModel = valueObject.getModelEntity();
                } catch (IllegalStateException e) {
                    throw new JspTagException("ModelEntity not found in ValueObject.");
                }

                // Get the field as an object.
                fieldObject = valueObject.get(field);

                // Get the Object Type.
                if (fieldObject != null) {
                    ModelField fieldModel = entityModel.getField(field);
                    fieldObjectType = fieldModel.type;
                    // Try to get the Field Type
                    try {
                        ModelFieldType fieldType = delegator.getEntityFieldType(entityModel, fieldObjectType);
                        javaType = fieldType.javaType;
                    } catch (GenericEntityException e) {
                        throw new JspTagException("[EntityFieldTag] : Cannot get the ModelFieldType from the Delegator.");
                    }
                } else {
                    //Debug.logWarning("[EntityFieldTag] : Null ValueObject passed.");
                    fieldObject = defaultStr;
                    fieldObjectType = "comment"; // Default for NULL objects.
                    javaType = "java.lang.String";
                }
            }
        }
        // We should be either a 'currency' or a java type.
        else {
            fieldObject = pageContext.findAttribute(attribute);
            javaType = type;
            // Set a default for NULL objects.
            if (fieldObject == null) {
                //Debug.logWarning("[EntityFieldTag] : Null Object passed.");
                fieldObject = defaultStr;
                javaType = "java.lang.String";
            }
            if (javaType.equalsIgnoreCase("currency")) {
                // Convert the String to a Double for standard processing.
                if (fieldObject instanceof String) {
                    try {
                        String objStr = (String) fieldObject;
                        fieldObject = new Double(objStr);
                    } catch (NumberFormatException nfe) {
                        throw new JspTagException("[EntityFieldTag] : String not a number.");
                    }
                }
                // The default type for currency is Double.
                javaType = "java.lang.Double";
                fieldObjectType = "currency-amount";
            }
        }

        // Get the Locale from the Request object.
        Locale userLocale = pageContext.getRequest().getLocale();
        if (userLocale == null)
            userLocale = Locale.getDefault();

        // Format the Object based on its type.
        String fieldString = null;
        if (javaType.equals("java.lang.Object") || javaType.equals("Object")) {
            fieldString = fieldObject.toString();
        } else if (javaType.equals("java.lang.String") || javaType.equals("String")) {
            fieldString = (String) fieldObject;
        } else if (javaType.equals("java.lang.Double") || javaType.equals("Double")) {
            Double doubleValue = (Double) fieldObject;
            NumberFormat nf = null;
            if (fieldObjectType.equals("currency-amount")) {
                //TODO: convert currency to current Locale
                nf = NumberFormat.getCurrencyInstance(userLocale);
            } else {
                nf = NumberFormat.getNumberInstance(userLocale);
            }
            fieldString = nf.format(doubleValue);
        } else if (javaType.equals("java.lang.Long") || javaType.equals("Long")) {
            Long longValue = (Long) fieldObject;
            NumberFormat nf = NumberFormat.getNumberInstance(userLocale);
            fieldString = nf.format(longValue);
        } else if (javaType.equals("java.lang.Boolean") || javaType.equals("Boolean")) {
            Boolean booleanValue = (Boolean) fieldObject;
            if (booleanValue.booleanValue())
                fieldString = "Yes";
            else
                fieldString = "No";
        } else if (javaType.equals("java.sql.Timestamp")) {
            Date dateValue = (Date) fieldObject;
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, userLocale);
            fieldString = df.format(dateValue);
        } else if (javaType.equals("java.sql.Time")) {
            Date dateValue = (Date) fieldObject;
            DateFormat df = DateFormat.getTimeInstance(DateFormat.FULL, userLocale);
            fieldString = df.format(dateValue);
        } else if (javaType.equals("java.sql.Date")) {
            Date dateValue = (Date) fieldObject;
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, userLocale);
            fieldString = df.format(dateValue);
        }

        try {
            JspWriter out = pageContext.getOut();
            if (fieldString.length() > 0) {
                if (prefix != null) out.print(prefix);
                out.print(fieldString);
                if (suffix != null) out.print(suffix);
            }
        } catch (IOException e) {
            throw new JspTagException(e.getMessage());
        }

        defaultStr = "";
        prefix = "";
        suffix = "";
        return (SKIP_BODY);
    }
}
