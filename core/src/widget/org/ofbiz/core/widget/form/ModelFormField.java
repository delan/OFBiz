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
package org.ofbiz.core.widget.form;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.FlexibleMapAccessor;
import org.ofbiz.core.util.FlexibleStringExpander;
import org.ofbiz.core.util.UtilDateTime;
import org.ofbiz.core.util.UtilValidate;
import org.ofbiz.core.util.UtilXml;
import org.w3c.dom.Element;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Widget Library - Form model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.2
 */
public class ModelFormField {
    
    public static final String module = ModelFormField.class.getName();

    protected ModelForm modelForm;

    protected String name;
    protected FlexibleMapAccessor mapAcsr;
    protected String entityName;
    protected String serviceName;
    protected FlexibleMapAccessor entryAcsr;
    protected String parameterName;
    protected String fieldName;
    protected String attributeName;
    protected FlexibleStringExpander title;
    protected FlexibleStringExpander tooltip;
    protected String titleStyle;
    protected String widgetStyle;
    protected Integer position = null;
    protected String redWhen;
    protected String useWhen;
    
    protected FieldInfo fieldInfo = null;

    // ===== CONSTRUCTORS =====
    /** Default Constructor */
    public ModelFormField(ModelForm modelForm) {
        this.modelForm = modelForm;
    }

    /** XML Constructor */
    public ModelFormField(Element fieldElement, ModelForm modelForm) {
        this.modelForm = modelForm;
        this.name = fieldElement.getAttribute("name");
        this.setMapName(fieldElement.getAttribute("map-name"));
        this.entityName = fieldElement.getAttribute("entity-name");
        this.serviceName = fieldElement.getAttribute("service-name");
        this.setEntryName(UtilXml.checkEmpty(fieldElement.getAttribute("entry-name"), this.name));
        this.parameterName = UtilXml.checkEmpty(fieldElement.getAttribute("parameter-name"), this.name);
        this.fieldName = UtilXml.checkEmpty(fieldElement.getAttribute("field-name"), this.name);
        this.attributeName = UtilXml.checkEmpty(fieldElement.getAttribute("attribute-name"), this.name);
        this.setTitle(fieldElement.getAttribute("title"));
        this.setTooltip(fieldElement.getAttribute("tooltip"));
        this.titleStyle = fieldElement.getAttribute("title-style");
        this.widgetStyle = fieldElement.getAttribute("widget-style");
        this.redWhen = fieldElement.getAttribute("red-when");
        this.useWhen = fieldElement.getAttribute("use-when");
        
        String positionStr = fieldElement.getAttribute("position");
        try {
            if (positionStr != null && positionStr.length() > 0) {
                position = Integer.valueOf(positionStr);
            }
        } catch (Exception e) {
            Debug.logError(e, "Could not convert position attribute of the field element to an integer: [" + positionStr + "], using the default of the form renderer");
        }
        
        // get sub-element and set fieldInfo
        Element subElement = UtilXml.firstChildElement(fieldElement, null);
        if (subElement != null) {
            String subElementName = subElement.getTagName();
            if (Debug.verboseOn()) Debug.logVerbose("Processing field " + this.name + " with type info tag " + subElementName);
            
            if (UtilValidate.isEmpty(subElementName)) {
                this.fieldInfo = null;
            } else if ("display".equals(subElementName)) {
                this.fieldInfo = new DisplayField(subElement, this);
            } else if ("hyperlink".equals(subElementName)) {
                this.fieldInfo = new HyperlinkField(subElement, this);
            } else if ("text".equals(subElementName)) {
                this.fieldInfo = new TextField(subElement, this);
            } else if ("textarea".equals(subElementName)) {
                this.fieldInfo = new TextareaField(subElement, this);
            } else if ("date-time".equals(subElementName)) {
                this.fieldInfo = new DateTimeField(subElement, this);
            } else if ("drop-down".equals(subElementName)) {
                this.fieldInfo = new DropDownField(subElement, this);
            } else if ("check".equals(subElementName)) {
                this.fieldInfo = new CheckField(subElement, this);
            } else if ("radio".equals(subElementName)) {
                this.fieldInfo = new RadioField(subElement, this);
            } else if ("submit".equals(subElementName)) {
                this.fieldInfo = new SubmitField(subElement, this);
            } else if ("reset".equals(subElementName)) {
                this.fieldInfo = new ResetField(subElement, this);
            } else if ("hidden".equals(subElementName)) {
                this.fieldInfo = new HiddenField(subElement, this);
            } else if ("ignored".equals(subElementName)) {
                this.fieldInfo = new IgnoredField(subElement, this);
            } else {
                throw new IllegalArgumentException("The field sub-element with name " + subElementName + " is not supported");
            }
        }
    }
    
    public void mergeOverrideModelFormField(ModelFormField overrideFormField) {
        // incorporate updates for values that are not empty in the overrideFormField
        if (UtilValidate.isNotEmpty(overrideFormField.name)) this.name = overrideFormField.name;
        if (overrideFormField.mapAcsr != null && !overrideFormField.mapAcsr.isEmpty()) this.mapAcsr = overrideFormField.mapAcsr;
        if (UtilValidate.isNotEmpty(overrideFormField.entityName)) this.entityName = overrideFormField.entityName;
        if (UtilValidate.isNotEmpty(overrideFormField.serviceName)) this.serviceName = overrideFormField.serviceName;
        if (overrideFormField.entryAcsr != null && !overrideFormField.entryAcsr.isEmpty()) this.entryAcsr = overrideFormField.entryAcsr;
        if (UtilValidate.isNotEmpty(overrideFormField.parameterName)) this.parameterName = overrideFormField.parameterName;
        if (UtilValidate.isNotEmpty(overrideFormField.fieldName)) this.fieldName = overrideFormField.fieldName;
        if (UtilValidate.isNotEmpty(overrideFormField.attributeName)) this.attributeName = overrideFormField.attributeName;
        if (overrideFormField.title != null && !overrideFormField.title.isEmpty()) this.title = overrideFormField.title;
        if (overrideFormField.tooltip != null && !overrideFormField.tooltip.isEmpty()) this.tooltip = overrideFormField.tooltip;
        if (UtilValidate.isNotEmpty(overrideFormField.titleStyle)) this.titleStyle = overrideFormField.titleStyle;
        if (UtilValidate.isNotEmpty(overrideFormField.widgetStyle)) this.widgetStyle = overrideFormField.widgetStyle;
        if (overrideFormField.position != null) this.position = overrideFormField.position;
        if (UtilValidate.isNotEmpty(overrideFormField.redWhen)) this.redWhen = overrideFormField.redWhen;
        if (UtilValidate.isNotEmpty(overrideFormField.useWhen)) this.useWhen = overrideFormField.useWhen;
        if (overrideFormField.fieldInfo != null) this.fieldInfo = overrideFormField.fieldInfo;        
    }

    public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
        this.fieldInfo.renderFieldString(buffer, context, formStringRenderer);
    }

    /**
     * @return
     */
    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    /**
     * @return
     */
    public ModelForm getModelForm() {
        return modelForm;
    }

    /**
     * @param fieldInfo
     */
    public void setFieldInfo(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
    }
    
    /** 
     * Gets the name of the Service Attribute (aka Parameter) that corresponds 
     * with this field. This can be used to get additional information about the field.
     * Use the getServiceName() method to get the Entity name that the field is in.
     *  
     * @return
     */
    public String getAttributeName() {
        if (UtilValidate.isNotEmpty(this.attributeName)) {
            return this.attributeName;
        } else {
            return this.name;
        }
    }

    /**
     * @return
     */
    public String getEntityName() {
        if (UtilValidate.isNotEmpty(this.entityName)) {
            return this.entityName;
        } else {
            return this.modelForm.getDefaultEntityName();
        }
    }

    /**
     * @see org.ofbiz.core.widget.form.ModelFormField.getMapName()
     * @return
     */
    public String getEntryName() {
        if (!entryAcsr.isEmpty()) {
            return entryAcsr.getOriginalName();
        } else {
            return this.name;
        }
    }

    /**
     * Gets the entry from the context that corresponds to this field; if this
     * form is being rendered in an error condition (ie isError in the context
     * is true) then the value will be retreived from the parameters Map in 
     * the context.
     * 
     * @param context
     * @return
     */
    public String getEntry(Map context) {
        return this.getEntry(context, "");
    }

    public String getEntry(Map context, String defaultValue) {
        Boolean isError = (Boolean) context.get("isError");
        if (isError != null && isError.booleanValue()) {
            Map parameters = (Map) context.get("parameters");
            if (parameters != null) {
                return (String) parameters.get(this.getParameterName());
            } else {
                return defaultValue;
            }
        } else {
            Map dataMap = this.getMap(context);
            if (dataMap == null) {
                dataMap = context;
            }
            Object retVal = null;
            if (!this.entryAcsr.isEmpty()) {
                retVal = this.entryAcsr.get(dataMap);
            } else {
                // if no extry name was specified, use the field's name
                retVal = dataMap.get(this.name);
            }
        
            if (retVal != null) {
                return retVal.toString();
            } else {
                return defaultValue;
            }
        }
    }
    
    public Map getMap(Map context) {
        if (this.mapAcsr.isEmpty()) {
            return this.modelForm.getDefaultMap(context);
        } else {
            return (Map) mapAcsr.get(context);
        }
    }

    /**
     * Gets the name of the Entity Field that corresponds 
     * with this field. This can be used to get additional information about the field.
     * Use the getEntityName() method to get the Entity name that the field is in.
     * 
     * @return
     */
    public String getFieldName() {
        if (UtilValidate.isNotEmpty(this.fieldName)) {
            return this.fieldName;
        } else {
            return this.name;
        }
    }

    /** Get the name of the Map in the form context that contains the entry, 
     * available from the getEntryName() method. This entry is used to 
     * pre-populate the field widget when not in an error condition. In an
     * error condition the parameter name is used to get the value from the
     * parameters Map.
     * 
     * @return
     */
    public String getMapName() {
        if (!this.mapAcsr.isEmpty()) {
            return this.mapAcsr.getOriginalName();
        } else {
            return this.modelForm.getDefaultMapName();
        }
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the name to use for the parameter for this field in the form interpreter.
     * For HTML forms this is the request parameter name.
     * 
     * @return 
     */
    public String getParameterName() {
        if (UtilValidate.isNotEmpty(this.parameterName)) {
            return this.parameterName;
        } else {
            return this.name;
        }
    }

    /**
     * @return
     */
    public int getPosition() {
        if (this.position == null) {
            return 1;
        } else {
            return position.intValue();
        }
    }

    /**
     * @return
     */
    public String getRedWhen() {
        return redWhen;
    }
    
    /**
     * the widget/interaction part will be red if the date value is
     *  before-now (for ex. thruDate), after-now (for ex. fromDate), or by-name (if the
     *  field's name or entry-name or fromDate or thruDate the corresponding 
     *  action will be done); only applicable when the field is a timestamp
     * 
     * @param context
     * @return
     */
    public boolean shouldBeRed(Map context) {
        // red-when ( never | before-now | after-now | by-name ) "by-name"
        
        String redCondition = this.redWhen;
        
        if ("never".equals(redCondition)) {
            return false;
        }
        
        // for performance resaons we check this first, most fields will be eliminated here and the valueOfs will not be necessary
        if (UtilValidate.isEmpty(redCondition) || "by-name".equals(redCondition)) {
            if ("fromDate".equals(this.name) || "fromDate".equals(this.entryAcsr.getOriginalName())) {
                redCondition = "after-now";
            } else if ("thruDate".equals(this.name) || "thruDate".equals(this.entryAcsr.getOriginalName())) {
                redCondition = "before-now";
            } else {
                return false;
            }
        }
        
        boolean isBeforeNow = false;
        if ("before-now".equals(redCondition)) {
            isBeforeNow = true;
        } else if ("after-now".equals(redCondition)) {
            isBeforeNow = false;
        } else {
            return false;
        }

        
        java.sql.Date dateVal = null;
        java.sql.Time timeVal = null;
        java.sql.Timestamp timestampVal = null;
        
        //now before going on, check to see if the current entry is a valid date and/or time and get the value
        String value = this.getEntry(context);
        try {
            timestampVal = java.sql.Timestamp.valueOf(value);
        } catch (Exception e) {
            // okay, not a timestamp...
        }
        
        if (timestampVal == null) {
            try {
                dateVal = java.sql.Date.valueOf(value);
            } catch (Exception e) {
                // okay, not a date...
            }
        }
        
        if (timestampVal == null && dateVal == null) {
            try {
                timeVal = java.sql.Time.valueOf(value);
            } catch (Exception e) {
                // okay, not a time...
            }
        }
        
        if (timestampVal == null && dateVal == null && timeVal == null) {
            return false;
        }        
        
        long nowMillis = System.currentTimeMillis();
        if (timestampVal != null) {
            java.sql.Timestamp nowStamp = new java.sql.Timestamp(nowMillis);
            if (!timestampVal.equals(nowStamp)) {
                if (isBeforeNow) {
                    if (timestampVal.before(nowStamp)) {
                        return true; 
                    }
                } else {
                    if (timestampVal.after(nowStamp)) {
                        return true; 
                    }
                }
            }
        } else if (dateVal != null) {
            java.sql.Date nowDate = new java.sql.Date(nowMillis);
            if (!dateVal.equals(nowDate)) {
                if (isBeforeNow) {
                    if (dateVal.before(nowDate)) {
                        return true; 
                    }
                } else {
                    if (dateVal.after(nowDate)) {
                        return true; 
                    }
                }
            }
        } else if (timeVal != null) {
            java.sql.Time nowTime = new java.sql.Time(nowMillis);
            if (!timeVal.equals(nowTime)) {
                if (isBeforeNow) {
                    if (timeVal.before(nowTime)) {
                        return true; 
                    }
                } else {
                    if (timeVal.after(nowTime)) {
                        return true; 
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * @return
     */
    public String getServiceName() {
        if (UtilValidate.isNotEmpty(this.serviceName)) {
            return this.serviceName;
        } else {
            return this.modelForm.getDefaultServiceName();
        }
    }

    /**
     * @return
     */
    public String getTitle(Map context) {
        if (!title.isEmpty()) {
            return title.expandString(context);
        } else {
            // create a title from the name of this field; expecting a Java method/field style name, ie productName or productCategoryId
            if (this.name == null || this.name.length() == 0) {
                // this should never happen, ie name is required
                return "";
            }
            
            StringBuffer autoTitleBuffer = new StringBuffer();
            
            // always use upper case first letter...
            autoTitleBuffer.append(Character.toUpperCase(this.name.charAt(0)));

            // just put spaces before the upper case letters            
            for (int i = 1; i < this.name.length(); i++) {
                char curChar = this.name.charAt(i);
                if (Character.isUpperCase(curChar)) {
                    autoTitleBuffer.append(' ');
                }
                autoTitleBuffer.append(curChar);
            }
            
            return autoTitleBuffer.toString();
        }
    }

    /**
     * @return
     */
    public String getTitleStyle() {
        if (UtilValidate.isNotEmpty(this.titleStyle)) {
            return this.titleStyle;
        } else {
            return this.modelForm.getDefaultTitleStyle();
        }
    }

    /**
     * @return
     */
    public String getTooltip(Map context) {
        return tooltip.expandString(context);
    }

    /**
     * @return
     */
    public String getUseWhen() {
        return useWhen;
    }
    
    public boolean shouldUse(Map context) {
        if (UtilValidate.isEmpty(useWhen)) {
            return true;
        } else {
            try {
                Interpreter bsh = this.modelForm.getBshInterpreter(context);
                Object retVal = bsh.eval(this.useWhen);
                boolean condTrue = false;
                // retVal should be a Boolean, if not something weird is up...
                if (retVal instanceof Boolean) {
                    Boolean boolVal = (Boolean) retVal;
                    condTrue = boolVal.booleanValue();
                } else {
                    throw new IllegalArgumentException("Return value from use-when condition eval was not a Boolean: " + retVal.getClass().getName() + " [" + retVal + "] on the field " + this.name + " of form " + this.modelForm.name);
                }
                
                return condTrue;
            } catch (EvalError e) {
                String errMsg = "Error evaluating BeanShell use-when condition [" + this.useWhen + "] on the field " + this.name + " of form " + this.modelForm.name + ": " + e.toString();
                Debug.logError(e, errMsg);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    /**
     * @return
     */
    public String getWidgetStyle() {
        if (UtilValidate.isNotEmpty(this.widgetStyle)) {
            return this.widgetStyle;
        } else {
            return this.modelForm.getDefaultWidgetStyle();
        }
    }

    /**
     * @param string
     */
    public void setAttributeName(String string) {
        attributeName = string;
    }

    /**
     * @param string
     */
    public void setEntityName(String string) {
        entityName = string;
    }

    /**
     * @param string
     */
    public void setEntryName(String string) {
        entryAcsr = new FlexibleMapAccessor(string);
    }

    /**
     * @param string
     */
    public void setFieldName(String string) {
        fieldName = string;
    }

    /**
     * @param string
     */
    public void setMapName(String string) {
        mapAcsr = new FlexibleMapAccessor(string);
    }

    /**
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    /**
     * @param string
     */
    public void setParameterName(String string) {
        parameterName = string;
    }

    /**
     * @param i
     */
    public void setPosition(int i) {
        position = new Integer(i);
    }

    /**
     * @param string
     */
    public void setRedWhen(String string) {
        redWhen = string;
    }

    /**
     * @param string
     */
    public void setServiceName(String string) {
        serviceName = string;
    }

    /**
     * @param string
     */
    public void setTitle(String string) {
        this.title = new FlexibleStringExpander(string);
    }

    /**
     * @param string
     */
    public void setTitleStyle(String string) {
        titleStyle = string;
    }

    /**
     * @param string
     */
    public void setTooltip(String string) {
        tooltip = new FlexibleStringExpander(string);
    }

    /**
     * @param string
     */
    public void setUseWhen(String string) {
        useWhen = string;
    }

    /**
     * @param string
     */
    public void setWidgetStyle(String string) {
        widgetStyle = string;
    }
    
    public static abstract class FieldInfo {
        
        public static final int DISPLAY = 1;
        public static final int HYPERLINK = 2;
        public static final int TEXT = 3;
        public static final int TEXTAREA = 4;
        public static final int DATE_TIME = 5;
        public static final int DROP_DOWN = 6;
        public static final int CHECK = 7;
        public static final int RADIO = 8;
        public static final int SUBMIT = 9;
        public static final int RESET = 10;
        public static final int HIDDEN = 11;
        public static final int IGNORED = 12;
        
        public static Map fieldTypeByName = new HashMap();
        
        static {
            fieldTypeByName.put("display", new Integer(1));
            fieldTypeByName.put("hyperlink", new Integer(2));
            fieldTypeByName.put("text", new Integer(3));
            fieldTypeByName.put("textarea", new Integer(4));
            fieldTypeByName.put("date-time", new Integer(5));
            fieldTypeByName.put("drop-down", new Integer(6));
            fieldTypeByName.put("check", new Integer(7));
            fieldTypeByName.put("radio", new Integer(8));
            fieldTypeByName.put("submit", new Integer(9));
            fieldTypeByName.put("reset", new Integer(10));
            fieldTypeByName.put("hidden", new Integer(11));
            fieldTypeByName.put("ignored", new Integer(12));
        }
        
        protected int fieldType;
        protected ModelFormField modelFormField;
        
        /** Don't allow the Default Constructor */
        protected FieldInfo() {}

        /** Value Constructor */        
        public FieldInfo(int fieldType, ModelFormField modelFormField) {
            this.fieldType = fieldType;
            this.modelFormField = modelFormField;
        }
        
        /** XML Constructor */        
        public FieldInfo(Element element, ModelFormField modelFormField) {
            this.fieldType = findFieldTypeFromName(element.getTagName());
            this.modelFormField = modelFormField;
        }
        
        /**
         * @return
         */
        public ModelFormField getModelFormField() {
            return modelFormField;
        }


        /**
         * @return
         */
        public int getFieldType() {
            return fieldType;
        }

        /**
         * @param i
         */
        public void setFieldType(int i) {
            fieldType = i;
        }

        public static int findFieldTypeFromName(String name) {
            Integer fieldTypeInt = (Integer) FieldInfo.fieldTypeByName.get(name);
            if (fieldTypeInt != null) {
                return fieldTypeInt.intValue();
            } else {
                throw new IllegalArgumentException("Could not get fieldType for field type name " + name);
            }
        }

        public abstract void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer);
    }
    
    public static abstract class FieldInfoWithOptions extends FieldInfo {
        protected FieldInfoWithOptions() { super(); }
        
        protected List optionSources = new LinkedList();

        public FieldInfoWithOptions(int fieldType, ModelFormField modelFormField) {
            super(fieldType, modelFormField);
        }

        public FieldInfoWithOptions(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            
            // read all option and entity-options sub-elements, maintaining order
            List childElements = UtilXml.childElementList(element, null);
            Iterator childElementIter = childElements.iterator();
            while (childElementIter.hasNext()) {
                Element childElement = (Element) childElementIter.next();
                if ("option".equals(childElement.getTagName())) {
                    optionSources.add(new SingleOption(childElement, this));
                } else if ("entity-options".equals(childElement.getTagName())) {
                    optionSources.add(new EntityOptions(childElement, this));
                }
            }
        }

        public List getAllOptionValues(Map context, GenericDelegator delegator) {
            List optionValues = new LinkedList();
            
            Iterator optionSourceIter = this.optionSources.iterator();
            while (optionSourceIter.hasNext()) {
                OptionSource optionSource = (OptionSource) optionSourceIter.next();
                optionSource.addOptionValues(optionValues, context, delegator);
            }
                  
            return optionValues;
        }
        
        public static String getDescriptionForOptionKey(String key, List allOptionValues) {
            if (UtilValidate.isEmpty(key)) {
                return "";
            }
            
            if (UtilValidate.isEmpty(allOptionValues)) {
                return key;
            }
            
            Iterator optionValueIter = allOptionValues.iterator();
            while (optionValueIter.hasNext()) {
                OptionValue optionValue = (OptionValue) optionValueIter.next();
                if (key.equals(optionValue.getKey())) {
                    return optionValue.getDescription();
                }
            }
            
            // if we get here we didn't find a match, just return the key
            return key;
        }
    }
    
    public static class OptionValue {
        protected String key;
        protected String description;
        
        public OptionValue(String key, String description) {
            this.key = key;
            this.description = description;
        }

        public String getKey() {
            return key;
        }

        public String getDescription() {
            return description;
        }
    }
    
    public static abstract class OptionSource {
        protected FieldInfo fieldInfo;
        
        public abstract void addOptionValues(List optionValues, Map context, GenericDelegator delegator);
    }
    
    public static class SingleOption extends OptionSource {
        protected FlexibleStringExpander key;
        protected FlexibleStringExpander description;
        
        public SingleOption(String key, String description, FieldInfo fieldInfo) {
            this.key = new FlexibleStringExpander(key);
            this.description = new FlexibleStringExpander(UtilXml.checkEmpty(description, key));
            this.fieldInfo = fieldInfo;
        }
        
        public SingleOption(Element optionElement, FieldInfo fieldInfo) {
            this.key = new FlexibleStringExpander(optionElement.getAttribute("key"));
            this.description = new FlexibleStringExpander(UtilXml.checkEmpty(optionElement.getAttribute("description"), optionElement.getAttribute("key")));
            this.fieldInfo = fieldInfo;
        }
        
        public void addOptionValues(List optionValues, Map context, GenericDelegator delegator) {
            optionValues.add(new OptionValue(key.expandString(context), description.expandString(context)));
        }
    }
    
    public static class EntityOptions extends OptionSource {
        protected String entityName;
        protected String keyFieldName;
        protected FlexibleStringExpander description;
        protected boolean cache = true;
        
        protected Map constraintMap = null;
        protected List orderByList = null; 
        
        public EntityOptions(FieldInfo fieldInfo) {
            this.fieldInfo = fieldInfo;
        }
        
        public EntityOptions(Element entityOptionsElement, FieldInfo fieldInfo) {
            this.entityName = entityOptionsElement.getAttribute("entity-name");
            this.keyFieldName = entityOptionsElement.getAttribute("key-field-name");
            this.description = new FlexibleStringExpander(entityOptionsElement.getAttribute("description"));
            this.cache = !"false".equals(entityOptionsElement.getAttribute("cache"));
            
            List constraintElements = UtilXml.childElementList(entityOptionsElement, "entity-constraint");
            Iterator constraintElementIter = constraintElements.iterator();
            while (constraintElementIter.hasNext()) {
                Element constraintElement = (Element) constraintElementIter.next();
                constraintMap.put(constraintElement.getAttribute("name"), constraintElement.getAttribute("value"));
            }

            List orderByElements = UtilXml.childElementList(entityOptionsElement, "entity-order-by");
            Iterator orderByElementIter = constraintElements.iterator();
            while (orderByElementIter.hasNext()) {
                Element orderByElement = (Element) orderByElementIter.next();
                orderByList.add(orderByElement.getAttribute("field-name"));
            }
            
            this.fieldInfo = fieldInfo;
        }
        
        public String getKeyFieldName() {
            if (UtilValidate.isNotEmpty(this.keyFieldName)) {
                return this.keyFieldName;
            } else {
                // get the modelFormField fieldName
                return this.fieldInfo.getModelFormField().getFieldName();
            }
        }
        
        public void addOptionValues(List optionValues, Map context, GenericDelegator delegator) {
            // add key and description with string expansion, ie expanding ${} stuff
            try {
                List values = null;
                if (this.cache) {
                    values = delegator.findByAndCache(this.entityName, this.constraintMap, this.orderByList);
                } else {
                    values = delegator.findByAnd(this.entityName, this.constraintMap, this.orderByList);
                }
                Iterator valueIter = values.iterator();
                while (valueIter.hasNext()) {
                    GenericValue value = (GenericValue) valueIter.next();
                    optionValues.add(new OptionValue(value.get(this.getKeyFieldName()).toString(), this.description.expandString(value)));
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting entity options in form");
            }
        }
    }
    
    public static class DisplayField extends FieldInfo {
        protected boolean alsoHidden = true;
        protected FlexibleStringExpander description;
        
        protected DisplayField() { super(); }

        public DisplayField(ModelFormField modelFormField) {
            super(FieldInfo.DISPLAY, modelFormField);
        }

        public DisplayField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.setDescription(element.getAttribute("description"));
            this.alsoHidden = !"false".equals(element.getAttribute("also-hidden"));
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderDisplayField(buffer, context, this);
        }
        
        /**
         * @return
         */
        public boolean getAlsoHidden() {
            return alsoHidden;
        }

        /**
         * @return
         */
        public String getDescription(Map context) {
            if (!this.description.isEmpty()) {
                return this.description.expandString(context);
            } else {
                return modelFormField.getEntry(context);
            }
        }

        /**
         * @param b
         */
        public void setAlsoHidden(boolean b) {
            alsoHidden = b;
        }

        /**
         * @param string
         */
        public void setDescription(String string) {
            description = new FlexibleStringExpander(string);
        }
    }
    
    public static class HyperlinkField extends FieldInfo {
        protected boolean alsoHidden = true;
        protected FlexibleStringExpander target;
        protected FlexibleStringExpander description;
        
        protected HyperlinkField() { super(); }

        public HyperlinkField(ModelFormField modelFormField) {
            super(FieldInfo.HYPERLINK, modelFormField);
        }

        public HyperlinkField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            
            this.setDescription(element.getAttribute("description"));
            this.setTarget(element.getAttribute("target"));
            this.alsoHidden = !"false".equals(element.getAttribute("also-hidden"));
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderHyperlinkField(buffer, context, this);
        }
        
        /**
         * @return
         */
        public boolean getAlsoHidden() {
            return alsoHidden;
        }

        /**
         * @return
         */
        public String getDescription(Map context) {
            return description.expandString(context);
        }

        /**
         * @return
         */
        public String getTarget(Map context) {
            return target.expandString(context);
        }

        /**
         * @param b
         */
        public void setAlsoHidden(boolean b) {
            alsoHidden = b;
        }

        /**
         * @param string
         */
        public void setDescription(String string) {
            description = new FlexibleStringExpander(string);
        }

        /**
         * @param string
         */
        public void setTarget(String string) {
            target = new FlexibleStringExpander(string);
        }
    }
    
    public static class TextField extends FieldInfo {
        protected int size = 25;
        protected Integer maxlength;
        
        protected TextField() { super(); }

        public TextField(ModelFormField modelFormField) {
            super(FieldInfo.TEXT, modelFormField);
        }

        public TextField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            
            String sizeStr = element.getAttribute("size");
            try {
                size = Integer.parseInt(sizeStr);
            } catch (Exception e) {
                if (sizeStr != null && sizeStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + sizeStr + "], setting to the default of " + size);
                }
            }
            
            String maxlengthStr = element.getAttribute("maxlength");
            try {
                maxlength = Integer.valueOf(maxlengthStr);
            } catch (Exception e) {
                maxlength = null;
                if (maxlengthStr != null && maxlengthStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + sizeStr + "], setting to null; default of no maxlength will be used");
                }
            }
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderTextField(buffer, context, this);
        }
        
        /**
         * @return
         */
        public Integer getMaxlength() {
            return maxlength;
        }

        /**
         * @return
         */
        public int getSize() {
            return size;
        }

        /**
         * @param integer
         */
        public void setMaxlength(Integer integer) {
            maxlength = integer;
        }

        /**
         * @param i
         */
        public void setSize(int i) {
            size = i;
        }
    }
    
    public static class TextareaField extends FieldInfo {
        protected int cols = 60;
        protected int rows = 2;
        
        protected TextareaField() { super(); }

        public TextareaField(ModelFormField modelFormField) {
            super(FieldInfo.TEXTAREA, modelFormField);
        }

        public TextareaField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            
            String colsStr = element.getAttribute("cols");
            try {
                cols = Integer.parseInt(colsStr);
            } catch (Exception e) {
                if (colsStr != null && colsStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + colsStr + "], setting to default of " + cols);
                }
            }
            
            String rowsStr = element.getAttribute("rows");
            try {
                rows = Integer.parseInt(rowsStr);
            } catch (Exception e) {
                if (rowsStr != null && rowsStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + rowsStr + "], setting to default of " + rows);
                }
            }
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderTextareaField(buffer, context, this);
        }
        
        /**
         * @return
         */
        public int getCols() {
            return cols;
        }

        /**
         * @return
         */
        public int getRows() {
            return rows;
        }

        /**
         * @param i
         */
        public void setCols(int i) {
            cols = i;
        }

        /**
         * @param i
         */
        public void setRows(int i) {
            rows = i;
        }
    }

    public static class DateTimeField extends FieldInfo {
        protected String type;
        
        protected DateTimeField() { super(); }

        public DateTimeField(ModelFormField modelFormField) {
            super(FieldInfo.DATE_TIME, modelFormField);
        }

        public DateTimeField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            type = element.getAttribute("type");
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderDateTimeField(buffer, context, this);
        }
        
        /**
         * @return
         */
        public String getType() {
            return type;
        }

        /**
         * @param string
         */
        public void setType(String string) {
            type = string;
        }

        public String getDefaultDateTimeString(Map context) {
            if ("date".equals(this.type)) {
                return (new java.sql.Date(System.currentTimeMillis())).toString();
            } else if ("time".equals(this.type)) {
                return (new java.sql.Time(System.currentTimeMillis())).toString();
            } else {
                return UtilDateTime.nowTimestamp().toString();
            }
        }
    }

    public static class DropDownField extends FieldInfoWithOptions {
        protected boolean allowEmpty = false;
        protected String current;
        
        protected DropDownField() { super(); }

        public DropDownField(ModelFormField modelFormField) {
            super(FieldInfo.DROP_DOWN, modelFormField);
        }

        public DropDownField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            
            this.current = element.getAttribute("current");
            this.allowEmpty = "true".equals(element.getAttribute("allow-empty"));
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderDropDownField(buffer, context, this);
        }
        
        /**
         * @return
         */
        public boolean isAllowEmpty() {
            return allowEmpty;
        }

        /**
         * @return
         */
        public String getCurrent() {
            if (UtilValidate.isEmpty(this.current)) {
                return "first-in-list";
            } else {
                return this.current;
            }
        }

        /**
         * @param b
         */
        public void setAllowEmpty(boolean b) {
            allowEmpty = b;
        }

        /**
         * @param string
         */
        public void setCurrent(String string) {
            current = string;
        }
    }

    public static class CheckField extends FieldInfoWithOptions {
        protected CheckField() { super(); }

        public CheckField(ModelFormField modelFormField) {
            super(FieldInfo.CHECK, modelFormField);
        }

        public CheckField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderCheckField(buffer, context, this);
        }
    }

    public static class RadioField extends FieldInfoWithOptions {
        protected RadioField() { super(); }

        public RadioField(ModelFormField modelFormField) {
            super(FieldInfo.RADIO, modelFormField);
        }

        public RadioField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderRadioField(buffer, context, this);
        }
    }

    public static class SubmitField extends FieldInfo {
        protected String buttonType;
        protected String imageLocation;
        
        protected SubmitField() { super(); }

        public SubmitField(ModelFormField modelFormField) {
            super(FieldInfo.SUBMIT, modelFormField);
        }

        public SubmitField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.buttonType = element.getAttribute("button-type");
            this.imageLocation = element.getAttribute("image-location");
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderSubmitField(buffer, context, this);
        }
        
        /**
         * @return
         */
        public String getButtonType() {
            return buttonType;
        }

        /**
         * @return
         */
        public String getImageLocation() {
            return imageLocation;
        }

        /**
         * @param string
         */
        public void setButtonType(String string) {
            buttonType = string;
        }

        /**
         * @param string
         */
        public void setImageLocation(String string) {
            imageLocation = string;
        }
    }

    public static class ResetField extends FieldInfo {
        protected ResetField() { super(); }

        public ResetField(ModelFormField modelFormField) {
            super(FieldInfo.RESET, modelFormField);
        }

        public ResetField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderResetField(buffer, context, this);
        }
    }

    public static class HiddenField extends FieldInfo {
        protected FlexibleStringExpander value;

        protected HiddenField() { super(); }

        public HiddenField(ModelFormField modelFormField) {
            super(FieldInfo.HIDDEN, modelFormField);
        }

        public HiddenField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.setValue(element.getAttribute("value"));
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderHiddenField(buffer, context, this);
        }

        public String getValue(Map context) {
            if (!this.value.isEmpty()) {
                return this.value.expandString(context);
            } else {
                return modelFormField.getEntry(context);
            }
        }

        public void setValue(String string) {
            this.value = new FlexibleStringExpander(string);
        }
    }

    public static class IgnoredField extends FieldInfo {
        protected IgnoredField() { super(); }

        public IgnoredField(ModelFormField modelFormField) {
            super(FieldInfo.IGNORED, modelFormField);
        }

        public IgnoredField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
        }

        public void renderFieldString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderIgnoredField(buffer, context, this);
        }
    }
}
