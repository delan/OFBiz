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

import java.util.*;
import org.w3c.dom.*;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.model.ModelEntity;
import org.ofbiz.core.entity.model.ModelField;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelParam;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.util.*;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Widget Library - Form model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.2
 */
public class ModelForm {
    
    public static final String module = ModelForm.class.getName();
    
    protected GenericDelegator delegator;
    protected LocalDispatcher dispatcher;

    protected String name;
    protected String type;
    protected String target;
    protected String title;
    protected String tooltip;
    protected String listName;
    protected String listEntryName;
    protected FlexibleMapAccessor defaultMapName;
    protected String defaultEntityName;
    protected String defaultServiceName;
    protected String defaultTitleStyle;
    protected String defaultWidgetStyle;
    
    protected List altTargets;
    protected List autoFieldsServices;
    protected List autoFieldsEntities;

    /** This List will contain one copy of each field for each field name in the order
     * they were encountered in the service, entity, or form definition; field definitions
     * with constraints will also be in this list but may appear multiple times for the same
     * field name.
     * 
     * When rendering the form the order in this list should be following and it should not be
     * necessary to use the Map. The Map is used when loading the form definition to keep the
     * list clean and implement the override features for field definitions.
     */    
    protected List fieldList;
    
    /** This Map is keyed with the field name and has a ModelFormField for the value; fields
     * with conditions will not be put in this Map so field definition overrides for fields
     * with conditions is not possible.
     */ 
    protected Map fieldMap;

    // ===== CONSTRUCTORS =====
    /** Default Constructor */
    public ModelForm() {}

    /** XML Constructor */
    public ModelForm(Element formElement, GenericDelegator delegator, LocalDispatcher dispatcher) {
        this.delegator = delegator;
        this.dispatcher = dispatcher;
        
        this.name = formElement.getAttribute("name");
        this.type = formElement.getAttribute("type");
        this.target = formElement.getAttribute("target");
        this.title = formElement.getAttribute("title");
        this.tooltip = formElement.getAttribute("tooltip");
        this.listName = formElement.getAttribute("list-name");
        this.listEntryName = formElement.getAttribute("list-entry-name");
        this.setDefaultMapName(formElement.getAttribute("default-map-name"));
        this.defaultEntityName = formElement.getAttribute("default-entity-name");
        this.defaultServiceName = formElement.getAttribute("default-service-name");
        this.defaultTitleStyle = formElement.getAttribute("default-title-style");
        this.defaultWidgetStyle = formElement.getAttribute("default-widget-style");
        
        // alt-target
        List altTargetElements = UtilXml.childElementList(formElement, "alt-target");
        Iterator altTargetElementIter = altTargetElements.iterator();
        while (altTargetElementIter.hasNext()) {
            Element altTargetElement = (Element) altTargetElementIter.next();
            AltTarget altTarget = new AltTarget(altTargetElement);
            this.addAltTarget(altTarget);
        }

        // auto-fields-service
        List autoFieldsServiceElements = UtilXml.childElementList(formElement, "auto-fields-service");
        Iterator autoFieldsServiceElementIter = autoFieldsServiceElements.iterator();
        while (autoFieldsServiceElementIter.hasNext()) {
            Element autoFieldsServiceElement = (Element) autoFieldsServiceElementIter.next();
            AutoFieldsService autoFieldsService = new AutoFieldsService(autoFieldsServiceElement);
            this.addAutoFieldsFromService(autoFieldsService, dispatcher);
        }

        // auto-fields-entity
        List autoFieldsEntityElements = UtilXml.childElementList(formElement, "auto-fields-entity");
        Iterator autoFieldsEntityElementIter = autoFieldsEntityElements.iterator();
        while (autoFieldsEntityElementIter.hasNext()) {
            Element autoFieldsEntityElement = (Element) autoFieldsEntityElementIter.next();
            AutoFieldsEntity autoFieldsEntity = new AutoFieldsEntity(autoFieldsEntityElement);
            this.addAutoFieldsFromEntity(autoFieldsEntity, delegator);
        }

        // read in add field defs, add/override one by one using the fieldList and fieldMap
        List fieldElements = UtilXml.childElementList(formElement, "field");
        Iterator fieldElementIter = fieldElements.iterator();
        while (fieldElementIter.hasNext()) {
            Element fieldElement = (Element) fieldElementIter.next();
            ModelFormField modelFormField = new ModelFormField(fieldElement, this);
            this.addUpdateField(modelFormField);
        }
    }
    
    /**
     * Renders this form to a String, i.e. in a text format, as defined with the 
     * FormStringRenderer implementation.
     * 
     * @param buffer The StringBuffer that the form text will be written to
     * @param context Map containing the form context; the following are 
     *   reserved words in this context: parameters (Map), isError (Boolean), 
     *   itemIndex (Integer, for lists only, otherwise null), bshInterpreter,
     *   formName (String, optional alternate name for form, defaults to the 
     *   value of the name attribute)
     * @param formStringRenderer An implementation of the FormStringRenderer 
     *   interface that is responsible for the actual text generation for 
     *   different form elements; implementing you own makes it possible to 
     *   use the same form definitions for many types of form UIs
     */
    public void renderFormString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
        // TODO: based on the type of form, render form headers/footers/wrappers and call individual field renderers
        // NOTE: for display and hyperlink with also hidden set to true: iterate through field list, find these and hidden fields and render them
        if ("single".equals(this.type)) {
            
        } else if ("list".equals(this.type)) {
        } else {
            throw new IllegalArgumentException("The type " + this.getType() + " is not supported for form with name " + this.getName());
        }
    }
    
    /** 
     * add/override modelFormField using the fieldList and fieldMap
     * 
     * @return The same ModelFormField, or if merged with an existing field, the existing field.
     */
    public ModelFormField addUpdateField(ModelFormField modelFormField) {
        if (modelFormField.getUseWhen() != null && modelFormField.getUseWhen().length() > 0) {
            // is a conditional field, add to the List but don't worry about the Map
            this.fieldList.add(modelFormField);
            return modelFormField;
        } else {
            // not a conditional field, see if a named field exists in Map
            ModelFormField existingField = (ModelFormField) this.fieldMap.get(modelFormField.getName());
            if (existingField != null) {
                // does exist, update the field by doing a merge/override
                existingField.mergeOverrideModelFormField(modelFormField);
                return existingField; 
            } else {
                // does not exist, add to List and Map
                this.fieldList.add(modelFormField);
                this.fieldMap.put(modelFormField.getName(), modelFormField);
                return modelFormField;
            }
        }
    }
    
    public void addAltTarget(AltTarget altTarget) {
        altTargets.add(altTarget);
    }
    
    public void addAutoFieldsFromService(AutoFieldsService autoFieldsService, LocalDispatcher dispatcher) {
        autoFieldsServices.add(autoFieldsService);
        
        // read service def and auto-create fields
        ModelService modelService = null;
        try {
            modelService = dispatcher.getDispatchContext().getModelService(autoFieldsService.serviceName);
        } catch (GenericServiceException e) {
            String errmsg = "Error finding Service with name " + autoFieldsService.serviceName + " for auto-fields-service in a form widget";
            Debug.logError(e, errmsg);
            throw new IllegalArgumentException(errmsg);
        }
        
        List modelParams = modelService.getInModelParamList();
        Iterator modelParamIter = modelParams.iterator();
        while (modelParamIter.hasNext()) {
            ModelParam modelParam = (ModelParam) modelParamIter.next();
            if (modelParam.formDisplay) {
                ModelFormField modelFormField = this.addFieldFromServiceParam(modelService, modelParam);
                if (UtilValidate.isNotEmpty(autoFieldsService.mapName)) {
                    modelFormField.setMapName(autoFieldsService.mapName);
                }
            }
        }
    }
    
    public ModelFormField addFieldFromServiceParam(ModelService modelService, ModelParam modelParam) {
        // create field def from service param def
        ModelFormField newFormField = new ModelFormField(this);
        newFormField.setName(modelParam.name);
        newFormField.setServiceName(modelService.name);
        newFormField.setAttributeName(modelParam.name);
        newFormField.setFieldInfo(new ModelFormField.TextField(newFormField));
        newFormField.setTitle(modelParam.formLabel);
        
        return this.addUpdateField(newFormField);
    }

    public void addAutoFieldsFromEntity(AutoFieldsEntity autoFieldsEntity, GenericDelegator delegator) {
        autoFieldsEntities.add(autoFieldsEntity);
        // read entity def and auto-create fields
        ModelEntity modelEntity = delegator.getModelEntity(autoFieldsEntity.entityName);
        if (modelEntity == null) {
            throw new IllegalArgumentException("Error finding Entity with name " + autoFieldsEntity.entityName + " for auto-fields-entity in a form widget");
        }
        
        Iterator modelFieldIter = modelEntity.getFieldsIterator();
        while (modelFieldIter.hasNext()) {
            ModelField modelField = (ModelField) modelFieldIter.next();
            ModelFormField modelFormField = this.addFieldFromEntityField(modelEntity, modelField);
            if (UtilValidate.isNotEmpty(autoFieldsEntity.mapName)) {
                modelFormField.setMapName(autoFieldsEntity.mapName);
            }
        }
    }
    
    public ModelFormField addFieldFromEntityField(ModelEntity modelEntity, ModelField modelField) {
        // create field def from entity field def
        ModelFormField newFormField = new ModelFormField(this);
        newFormField.setName(modelField.getName());
        newFormField.setEntityName(modelEntity.getEntityName());
        newFormField.setFieldName(modelField.getName());
        newFormField.setFieldInfo(new ModelFormField.TextField(newFormField));
        
        return this.addUpdateField(newFormField);
    }

    public LocalDispatcher getDispacher() {
        return this.dispatcher;
    }
    
    public GenericDelegator getDelegator() {
        return this.delegator;
    }
    
    /**
     * @return
     */
    public String getDefaultEntityName() {
        return this.defaultEntityName;
    }

    /**
     * @return
     */
    public String getDefaultMapName() {
        return this.defaultMapName.getOriginalName();
    }

    public Map getDefaultMap(Map context) {
        return (Map) this.defaultMapName.get(context);
    }

    /**
     * @return
     */
    public String getDefaultServiceName() {
        return this.defaultServiceName;
    }

    /**
     * @return
     */
    public String getDefaultTitleStyle() {
        return this.defaultTitleStyle;
    }

    /**
     * @return
     */
    public String getDefaultWidgetStyle() {
        return this.defaultWidgetStyle;
    }

    /**
     * @return
     */
    public String getListEntryName() {
        return this.listEntryName;
    }

    /**
     * @return
     */
    public String getListName() {
        return this.listName;
    }

    /**
     * @return
     */
    public String getName() {
        return this.name;
    }
    
    public String getCurrentFormName(Map context) {
        Integer itemIndex = (Integer) context.get("itemIndex");
        String formName = (String) context.get("formName");
        if (UtilValidate.isEmpty(formName)) {
            formName = this.getName();
        }
        
        if (itemIndex != null) {
            return formName + itemIndex.intValue();
        } else {
            return formName;
        }
    }

    /** iterate through altTargets list to see if any should be used, if not return original target
     * @return The target for this Form
     */
    public String getTarget(Map context) {
        try {
            // use the same Interpreter (ie with the same context setup) for all evals
            Interpreter bsh = this.getBshInterpreter(context);
            Iterator altTargetIter = this.altTargets.iterator();
            while (altTargetIter.hasNext()) {
                AltTarget altTarget = (AltTarget) altTargetIter.next();
                Object retVal = bsh.eval(altTarget.useWhen);
                boolean condTrue = false;
                // retVal should be a Boolean, if not something weird is up...
                if (retVal instanceof Boolean) {
                    Boolean boolVal = (Boolean) retVal;
                    condTrue = boolVal.booleanValue();
                } else {
                    throw new IllegalArgumentException("Return value from target condition eval was not a Boolean: " + retVal.getClass().getName() + " [" + retVal + "] of form " + this.name);
                }
                
                if (condTrue) {
                    return altTarget.target;
                }
            }
        } catch (EvalError e) {
            String errmsg = "Error evaluating BeanShell target conditions on form " + this.name;
            Debug.logError(e, errmsg);
            throw new IllegalArgumentException(errmsg);
        }
        
        return target;
    }

    /**
     * @return
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @return
     */
    public String getTooltip() {
        return this.tooltip;
    }

    /**
     * @return
     */
    public String getType() {
        return this.type;
    }
    
    public Interpreter getBshInterpreter(Map context) throws EvalError {
        Interpreter bsh = (Interpreter) context.get("bshInterpreter");
        if (bsh == null) {
            bsh = BshUtil.makeInterpreter(context);
            context.put("bshInterpreter", bsh);
        }
        return bsh;
    }

    /**
     * @param string
     */
    public void setDefaultEntityName(String string) {
        defaultEntityName = string;
    }

    /**
     * @param string
     */
    public void setDefaultMapName(String string) {
        defaultMapName = new FlexibleMapAccessor(string);
    }

    /**
     * @param string
     */
    public void setDefaultServiceName(String string) {
        defaultServiceName = string;
    }

    /**
     * @param string
     */
    public void setDefaultTitleStyle(String string) {
        defaultTitleStyle = string;
    }

    /**
     * @param string
     */
    public void setDefaultWidgetStyle(String string) {
        defaultWidgetStyle = string;
    }

    /**
     * @param string
     */
    public void setListEntryName(String string) {
        listEntryName = string;
    }

    /**
     * @param string
     */
    public void setListName(String string) {
        listName = string;
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
    public void setTarget(String string) {
        target = string;
    }

    /**
     * @param string
     */
    public void setTitle(String string) {
        title = string;
    }

    /**
     * @param string
     */
    public void setTooltip(String string) {
        tooltip = string;
    }

    /**
     * @param string
     */
    public void setType(String string) {
        type = string;
    }
    
    public static class AltTarget {
        public String useWhen;
        public String target;
        public AltTarget(Element altTargetElement) {
            this.useWhen = altTargetElement.getAttribute("use-when");
            this.target = altTargetElement.getAttribute("target");
        }
    }
    
    public static class AutoFieldsService {
        public String serviceName;
        public String mapName;
        public AutoFieldsService(Element element) {
            this.serviceName = element.getAttribute("service-name");
            this.mapName = element.getAttribute("map-name");
        }
    }
    
    public static class AutoFieldsEntity {
        public String entityName;
        public String mapName;
        public AutoFieldsEntity(Element element) {
            this.entityName = element.getAttribute("entity-name");
            this.mapName = element.getAttribute("map-name");
        }
    }
}
