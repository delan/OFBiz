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
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.model.ModelEntity;
import org.ofbiz.core.entity.model.ModelField;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelParam;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.util.BshUtil;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.FlexibleMapAccessor;
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
    protected String defaultTooltipStyle;
    protected String itemIndexSeparator;
    
    protected List altTargets = new LinkedList();
    protected List autoFieldsServices = new LinkedList();
    protected List autoFieldsEntities = new LinkedList();

    /** This List will contain one copy of each field for each field name in the order
     * they were encountered in the service, entity, or form definition; field definitions
     * with constraints will also be in this list but may appear multiple times for the same
     * field name.
     * 
     * When rendering the form the order in this list should be following and it should not be
     * necessary to use the Map. The Map is used when loading the form definition to keep the
     * list clean and implement the override features for field definitions.
     */    
    protected List fieldList = new LinkedList();
    
    /** This Map is keyed with the field name and has a ModelFormField for the value; fields
     * with conditions will not be put in this Map so field definition overrides for fields
     * with conditions is not possible.
     */ 
    protected Map fieldMap = new HashMap();

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
        this.defaultTooltipStyle = formElement.getAttribute("default-tooltip-style");
        this.itemIndexSeparator = formElement.getAttribute("item-index-separator");
        
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
            modelFormField = this.addUpdateField(modelFormField);
            //Debug.logInfo("Added field " + modelFormField.getName() + " from def, mapName=" + modelFormField.getMapName());
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
            //for adding to list, see if there is another field with that name in the list and if so, put it before that one
            boolean inserted = false;
            for (int i = 0; i < this.fieldList.size(); i++) {
                ModelFormField curField = (ModelFormField) this.fieldList.get(i);
                if (curField.getName() != null && curField.getName().equals(modelFormField.getName())) {
                    this.fieldList.add(i, modelFormField);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                this.fieldList.add(modelFormField);
            }
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
            // skip auto params that the service engine populates...
            if ("userLogin".equals(modelParam.name) || "locale".equals(modelParam.name)) {
                continue;
            }
            if (modelParam.formDisplay) {
                if (UtilValidate.isNotEmpty(modelParam.entityName) && UtilValidate.isNotEmpty(modelParam.fieldName)) {
                    ModelEntity modelEntity = delegator.getModelEntity(modelParam.entityName);
                    if (modelEntity != null) {
                        ModelField modelField = modelEntity.getField(modelParam.fieldName);
                        if (modelField != null) {
                            // okay, populate using the entity field info...
                            ModelFormField modelFormField = this.addFieldFromEntityField(modelEntity, modelField);
                            if (UtilValidate.isNotEmpty(autoFieldsService.mapName)) {
                                modelFormField.setMapName(autoFieldsService.mapName);
                            }

                            // continue to skip creating based on service param                            
                            continue;
                        }
                    }
                }
                
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
        
        if (modelParam.type.indexOf("Double") != -1 || modelParam.type.indexOf("Float") != -1 || modelParam.type.indexOf("Long") != -1 || modelParam.type.indexOf("Integer") != -1) {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, newFormField);
            textField.setSize(6);
            newFormField.setFieldInfo(textField);
        } else if (modelParam.type.indexOf("Timestamp") != -1) {
            ModelFormField.DateTimeField dateTimeField = new ModelFormField.DateTimeField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, newFormField);
            dateTimeField.setType("timestamp");
            newFormField.setFieldInfo(dateTimeField);
        } else if (modelParam.type.indexOf("Date") != -1) {
            ModelFormField.DateTimeField dateTimeField = new ModelFormField.DateTimeField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, newFormField);
            dateTimeField.setType("date");
            newFormField.setFieldInfo(dateTimeField);
        } else if (modelParam.type.indexOf("Time") != -1) {
            ModelFormField.DateTimeField dateTimeField = new ModelFormField.DateTimeField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, newFormField);
            dateTimeField.setType("time");
            newFormField.setFieldInfo(dateTimeField);
        } else {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, newFormField);
            newFormField.setFieldInfo(textField);
        }
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
        
        if ("id".equals(modelField.getType()) || "id-ne".equals(modelField.getType())) {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            textField.setSize(20);
            textField.setMaxlength(new Integer(20));
            newFormField.setFieldInfo(textField);
        } else if ("id-long".equals(modelField.getType()) || "id-long-ne".equals(modelField.getType())) {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            textField.setSize(40);
            textField.setMaxlength(new Integer(60));
            newFormField.setFieldInfo(textField);
        } else if ("id-vlong".equals(modelField.getType()) || "id-vlong-ne".equals(modelField.getType())) {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            textField.setSize(60);
            textField.setMaxlength(new Integer(250));
            newFormField.setFieldInfo(textField);
        } else if ("indicator".equals(modelField.getType())) {
            ModelFormField.DropDownField dropDownField = new ModelFormField.DropDownField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            dropDownField.setAllowEmpty(false);
            dropDownField.addOptionSource(new ModelFormField.SingleOption("Y", null, dropDownField));
            dropDownField.addOptionSource(new ModelFormField.SingleOption("N", null, dropDownField));
            //ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            //textField.setSize(1);
            //textField.setMaxlength(new Integer(1));
            //newFormField.setFieldInfo(textField);
        } else if ("very-short".equals(modelField.getType())) {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            textField.setSize(6);
            textField.setMaxlength(new Integer(10));
            newFormField.setFieldInfo(textField);
        } else if ("very-long".equals(modelField.getType())) {
            ModelFormField.TextareaField textareaField = new ModelFormField.TextareaField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            textareaField.setCols(60);
            textareaField.setRows(2);
            newFormField.setFieldInfo(textareaField);
        } else if ("name".equals(modelField.getType()) || "short-varchar".equals(modelField.getType())) {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            textField.setSize(40);
            textField.setMaxlength(new Integer(60));
            newFormField.setFieldInfo(textField);
        } else if ("value".equals(modelField.getType()) || "comment".equals(modelField.getType()) || 
                "description".equals(modelField.getType()) || "long-varchar".equals(modelField.getType()) ||
                "url".equals(modelField.getType()) || "email".equals(modelField.getType())) {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            textField.setSize(60);
            textField.setMaxlength(new Integer(250));
            newFormField.setFieldInfo(textField);
        } else if ("floating-point".equals(modelField.getType()) || "currency".equals(modelField.getType()) || "numeric".equals(modelField.getType())) {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            textField.setSize(6);
            newFormField.setFieldInfo(textField);
        } else if ("date-time".equals(modelField.getType()) || "date".equals(modelField.getType()) || "time".equals(modelField.getType())) {
            ModelFormField.DateTimeField dateTimeField = new ModelFormField.DateTimeField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            if ("date-time".equals(modelField.getType())) {
                dateTimeField.setType("timestamp");
            } else if ("date".equals(modelField.getType())) {
                dateTimeField.setType("date");
            } else if ("time".equals(modelField.getType())) {
                dateTimeField.setType("time");
            }
            newFormField.setFieldInfo(dateTimeField);
        } else {
            ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, newFormField);
            newFormField.setFieldInfo(textField);
        }
        
        return this.addUpdateField(newFormField);
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
        // find the highest position number to get the max positions used
        int positions = 1;
        Iterator fieldIter = this.fieldList.iterator();
        while (fieldIter.hasNext()) {
            ModelFormField modelFormField = (ModelFormField) fieldIter.next();
            int curPos = modelFormField.getPosition();
            if (curPos > positions) {
                positions = curPos;
            }
        }
            
        if ("single".equals(this.type)) {
            this.renderSingleFormString(buffer, context, formStringRenderer, positions);
        } else if ("list".equals(this.type)) {
            this.renderListFormString(buffer, context, formStringRenderer, positions);
        } else if ("multi".equals(this.type)) {
            this.renderMultiFormString(buffer, context, formStringRenderer, positions);
        } else {
            throw new IllegalArgumentException("The type " + this.getType() + " is not supported for form with name " + this.getName());
        }
    }

    public void renderSingleFormString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer, int positions) {
        Iterator fieldIter = null;
        
        // render form open
        formStringRenderer.renderFormOpen(buffer, context, this);
            
        // render all hidden & ignored fields
        this.renderHiddenIgnoredFields(buffer, context, formStringRenderer);
            
        // render formatting wrapper open
        formStringRenderer.renderFormatSingleWrapperOpen(buffer, context, this);
            
        // render each field row, except hidden & ignored rows
        fieldIter = this.fieldList.iterator();
        ModelFormField lastFormField = null;
        ModelFormField currentFormField = null;
        ModelFormField nextFormField = null;
        if (fieldIter.hasNext()) {
            currentFormField = (ModelFormField) fieldIter.next();
        }
        if (fieldIter.hasNext()) {
            nextFormField = (ModelFormField) fieldIter.next();
        }
        
        Set alreadyRendered = new TreeSet();

        boolean isFirstPass = true;            
        while (currentFormField != null) {
            // do the check/get next stuff at the beginning so we can still use the continue stuff easily
            // don't do it on the first pass though...
            if (isFirstPass) {
                isFirstPass = false;
            } else {
                if (fieldIter.hasNext()) {
                    // at least two loops left
                    lastFormField = currentFormField;
                    currentFormField = nextFormField;
                    nextFormField = (ModelFormField) fieldIter.next();
                } else if (nextFormField != null) {
                    // okay, just one loop left
                    lastFormField = currentFormField;
                    currentFormField = nextFormField;
                    nextFormField = null;
                } else {
                    // at the end...
                    lastFormField = currentFormField;
                    currentFormField = null;
                    // nextFormField is already null
                    break;
                }
            }
                
            ModelFormField.FieldInfo fieldInfo = currentFormField.getFieldInfo();
                
            if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.HIDDEN || fieldInfo.getFieldType() == ModelFormField.FieldInfo.IGNORED) {
                continue; 
            }
            
            if (alreadyRendered.contains(currentFormField.getName())) {
                continue;
            }

            //Debug.logInfo("In single form evaluating use-when for field " + currentFormField.getName() + ": " + currentFormField.getUseWhen());
            if (!currentFormField.shouldUse(context)) {
                continue;
            }
            
            alreadyRendered.add(currentFormField.getName());
                
            boolean stayingOnRow = false;
            if (lastFormField != null) {
                if (lastFormField.getPosition() >= currentFormField.getPosition()) {
                    // moving to next row
                    stayingOnRow = false;
                } else {
                    // staying on same row
                    stayingOnRow = true;
                }
            }

            int positionSpan = 1;
            Integer nextPositionInRow = null;
            if (nextFormField != null) {
                if (nextFormField.getPosition() > currentFormField.getPosition()) {
                    positionSpan = nextFormField.getPosition() - currentFormField.getPosition() - 1;
                    nextPositionInRow = new Integer(nextFormField.getPosition());
                } else {
                    positionSpan = positions - currentFormField.getPosition();
                    if (!stayingOnRow && nextFormField.getPosition() > 1) {
                        // TODO: here is a weird case where it is setup such 
                        //that the first position(s) in the row are skipped
                        // not sure what to do about this right now...
                    }
                }
            }
                
            if (stayingOnRow) {
                // no spacer cell, might add later though...
                //formStringRenderer.renderFormatFieldRowSpacerCell(buffer, context, currentFormField);
            } else {
                if (lastFormField != null) {
                    // render row formatting close
                    formStringRenderer.renderFormatFieldRowClose(buffer, context, this);
                }
                    
                // render row formatting open
                formStringRenderer.renderFormatFieldRowOpen(buffer, context, this);
            }
                
            // render title formatting open
            formStringRenderer.renderFormatFieldRowTitleCellOpen(buffer, context, currentFormField);
                
            // render title (unless this is a submit or a reset field)
            if (fieldInfo.getFieldType() != ModelFormField.FieldInfo.SUBMIT && fieldInfo.getFieldType() != ModelFormField.FieldInfo.RESET) {
                formStringRenderer.renderFieldTitle(buffer, context, currentFormField);
            } else {
                formStringRenderer.renderFormatEmptySpace(buffer, context, this);
            }
                
            // render title formatting close
            formStringRenderer.renderFormatFieldRowTitleCellClose(buffer, context, currentFormField);
                
            // render separator
            formStringRenderer.renderFormatFieldRowSpacerCell(buffer, context, currentFormField);
                                
            // render widget formatting open
            formStringRenderer.renderFormatFieldRowWidgetCellOpen(buffer, context, currentFormField, positions, positionSpan, nextPositionInRow);
                
            // render widget
            currentFormField.renderFieldString(buffer, context, formStringRenderer);
                
            // render widget formatting close
            formStringRenderer.renderFormatFieldRowWidgetCellClose(buffer, context, currentFormField, positions, positionSpan, nextPositionInRow);

        }
        // always render row formatting close after the end
        formStringRenderer.renderFormatFieldRowClose(buffer, context, this);
            
        // render formatting wrapper close
        formStringRenderer.renderFormatSingleWrapperClose(buffer, context, this);
            
        // render form close
        formStringRenderer.renderFormClose(buffer, context, this);
    }
    
    public void renderListFormString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer, int positions) {
        // render list/tabular type forms
            
        // render formatting wrapper open
        formStringRenderer.renderFormatListWrapperOpen(buffer, context, this);
            
        // ===== render header row =====
        this.renderHeaderRow(buffer, context, formStringRenderer);    
            
        // ===== render the item rows =====
        this.renderItemRows(buffer, context, formStringRenderer, true);
            
        // render formatting wrapper close
        formStringRenderer.renderFormatListWrapperClose(buffer, context, this);
    }

    public void renderMultiFormString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer, int positions) {
        formStringRenderer.renderFormOpen(buffer, context, this);

        // render formatting wrapper open
        formStringRenderer.renderFormatListWrapperOpen(buffer, context, this);
            
        // ===== render header row =====
        this.renderHeaderRow(buffer, context, formStringRenderer);    
            
        // ===== render the item rows =====
        this.renderItemRows(buffer, context, formStringRenderer, false);
            
        // render formatting wrapper close
        formStringRenderer.renderFormatListWrapperClose(buffer, context, this);

        formStringRenderer.renderFormClose(buffer, context, this);
    }
    
    public void renderHeaderRow(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
        formStringRenderer.renderFormatHeaderRowOpen(buffer, context, this);
        
        // render title for each field, except hidden & ignored, etc
            
        // start by rendering all display and hyperlink fields, until we
        //get to a field that should go into the form cell, then render
        //the form cell with all non-display and non-hyperlink fields, then
        //do a start after the first form input field and
        //render all display and hyperlink fields after the form
            
        // do the first part of display and hyperlink fields 
        Iterator displayHyperlinkFieldIter = this.fieldList.iterator();
        while (displayHyperlinkFieldIter.hasNext()) {
            ModelFormField modelFormField = (ModelFormField) displayHyperlinkFieldIter.next();
            ModelFormField.FieldInfo fieldInfo = modelFormField.getFieldInfo();
                
            // don't do any header for hidden or ignored fields
            if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.HIDDEN || fieldInfo.getFieldType() == ModelFormField.FieldInfo.IGNORED) {
                continue; 
            }

            if (fieldInfo.getFieldType() != ModelFormField.FieldInfo.DISPLAY && fieldInfo.getFieldType() != ModelFormField.FieldInfo.HYPERLINK) {
                // okay, now do the form cell
                break;
            }

            if (!modelFormField.shouldUse(context)) {
                continue;
            }

            formStringRenderer.renderFormatHeaderRowCellOpen(buffer, context, this, modelFormField);

            formStringRenderer.renderFieldTitle(buffer, context, modelFormField);
                
            formStringRenderer.renderFormatHeaderRowCellClose(buffer, context, this, modelFormField);
        }
            
            
        List headerFormFields = new LinkedList();
        Iterator formFieldIter = this.fieldList.iterator();
        boolean isFirstFormHeader = true;
        while (formFieldIter.hasNext()) {
            ModelFormField modelFormField = (ModelFormField) formFieldIter.next();
            ModelFormField.FieldInfo fieldInfo = modelFormField.getFieldInfo();
                
            // don't do any header for hidden or ignored fields
            if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.HIDDEN || fieldInfo.getFieldType() == ModelFormField.FieldInfo.IGNORED) {
                continue; 
            }

            // skip all of the display/hyperlink fields
            if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.DISPLAY || fieldInfo.getFieldType() == ModelFormField.FieldInfo.HYPERLINK) {
                continue;
            }

            // skip all of the submit/reset fields
            if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.SUBMIT || fieldInfo.getFieldType() == ModelFormField.FieldInfo.RESET) {
                continue;
            }

            if (!modelFormField.shouldUse(context)) {
                continue;
            }
                
            headerFormFields.add(modelFormField);
        }
            
        // render the "form" cell 
        formStringRenderer.renderFormatHeaderRowFormCellOpen(buffer, context, this);
            
        Iterator headerFormFieldIter = headerFormFields.iterator();
        while (headerFormFieldIter.hasNext()) {
            ModelFormField modelFormField = (ModelFormField) headerFormFieldIter.next();
            ModelFormField.FieldInfo fieldInfo = modelFormField.getFieldInfo();
                
            // render title (unless this is a submit or a reset field)
            formStringRenderer.renderFieldTitle(buffer, context, modelFormField);

            if (headerFormFieldIter.hasNext()) {
                // TODO: determine somehow if this is the last one... how?
                formStringRenderer.renderFormatHeaderRowFormCellTitleSeparator(buffer, context, this, modelFormField, false);
            }
        }

        formStringRenderer.renderFormatHeaderRowFormCellClose(buffer, context, this);
            
        // render the rest of the display/hyperlink fields 
        while (displayHyperlinkFieldIter.hasNext()) {
            ModelFormField modelFormField = (ModelFormField) displayHyperlinkFieldIter.next();
            ModelFormField.FieldInfo fieldInfo = modelFormField.getFieldInfo();
                
            // don't do any header for hidden or ignored fields
            if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.HIDDEN || fieldInfo.getFieldType() == ModelFormField.FieldInfo.IGNORED) {
                continue; 
            }

            // skip all non-display and non-hyperlink fields
            if (fieldInfo.getFieldType() != ModelFormField.FieldInfo.DISPLAY && fieldInfo.getFieldType() != ModelFormField.FieldInfo.HYPERLINK) {
                continue;
            }

            if (!modelFormField.shouldUse(context)) {
                continue;
            }

            formStringRenderer.renderFormatHeaderRowCellOpen(buffer, context, this, modelFormField);

            formStringRenderer.renderFieldTitle(buffer, context, modelFormField);
                
            formStringRenderer.renderFormatHeaderRowCellClose(buffer, context, this, modelFormField);
        }
            
        formStringRenderer.renderFormatHeaderRowClose(buffer, context, this);
    }

    public void renderItemRows(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer, boolean formPerItem) {
        // if list is empty, do not render rows
        List items = (List) context.get(this.getListName());
        if (items == null || items.size() == 0) {
            // do nothing; we could show an simple box with a message here
        } else {
            // render item rows
            Iterator itemIter = items.iterator();
            int itemIndex = 0;
            while (itemIter.hasNext()) {
                itemIndex++;
                Map localContext = new HashMap(context);
                Object item = itemIter.next();
                if (UtilValidate.isNotEmpty(this.getListEntryName())) {
                    localContext.put(this.getListEntryName(), item);
                } else {
                    Map itemMap = (Map) item;
                    localContext.putAll(itemMap);
                }
                localContext.put("itemIndex", new Integer(itemIndex));
                
                // render row formatting open
                formStringRenderer.renderFormatItemRowOpen(buffer, localContext, this);
                    
                // do the first part of display and hyperlink fields 
                Iterator innerDisplayHyperlinkFieldIter = this.fieldList.iterator();
                while (innerDisplayHyperlinkFieldIter.hasNext()) {
                    ModelFormField modelFormField = (ModelFormField) innerDisplayHyperlinkFieldIter.next();
                    ModelFormField.FieldInfo fieldInfo = modelFormField.getFieldInfo();
                
                    // don't do any header for hidden or ignored fields
                    if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.HIDDEN || fieldInfo.getFieldType() == ModelFormField.FieldInfo.IGNORED) {
                        continue; 
                    }

                    if (fieldInfo.getFieldType() != ModelFormField.FieldInfo.DISPLAY && fieldInfo.getFieldType() != ModelFormField.FieldInfo.HYPERLINK) {
                        // okay, now do the form cell
                        break;
                    }

                    if (!modelFormField.shouldUse(localContext)) {
                        continue;
                    }

                    formStringRenderer.renderFormatItemRowCellOpen(buffer, localContext, this, modelFormField);

                    modelFormField.renderFieldString(buffer, localContext, formStringRenderer);
                
                    formStringRenderer.renderFormatItemRowCellClose(buffer, localContext, this, modelFormField);
                }

                // render the "form" cell 
                formStringRenderer.renderFormatItemRowFormCellOpen(buffer, localContext, this);

                if (formPerItem) {            
                    formStringRenderer.renderFormOpen(buffer, localContext, this);
                }
                    
                // do all of the hidden fields...
                this.renderHiddenIgnoredFields(buffer, localContext, formStringRenderer);
            
                Iterator innerFormFieldIter = this.fieldList.iterator();
                while (innerFormFieldIter.hasNext()) {
                    ModelFormField modelFormField = (ModelFormField) innerFormFieldIter.next();
                    ModelFormField.FieldInfo fieldInfo = modelFormField.getFieldInfo();
                
                    // don't do any header for hidden or ignored fields
                    if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.HIDDEN || fieldInfo.getFieldType() == ModelFormField.FieldInfo.IGNORED) {
                        continue; 
                    }

                    // skip all of the display/hyperlink fields
                    if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.DISPLAY || fieldInfo.getFieldType() == ModelFormField.FieldInfo.HYPERLINK) {
                        continue;
                    }

                    if (!modelFormField.shouldUse(localContext)) {
                        continue;
                    }

                    // render field widget
                    modelFormField.renderFieldString(buffer, localContext, formStringRenderer);
                }

                if (formPerItem) {            
                    formStringRenderer.renderFormClose(buffer, localContext, this);
                }
            
                formStringRenderer.renderFormatItemRowFormCellClose(buffer, localContext, this);
            
                // render the rest of the display/hyperlink fields 
                while (innerDisplayHyperlinkFieldIter.hasNext()) {
                    ModelFormField modelFormField = (ModelFormField) innerDisplayHyperlinkFieldIter.next();
                    ModelFormField.FieldInfo fieldInfo = modelFormField.getFieldInfo();
                
                    // don't do any header for hidden or ignored fields
                    if (fieldInfo.getFieldType() == ModelFormField.FieldInfo.HIDDEN || fieldInfo.getFieldType() == ModelFormField.FieldInfo.IGNORED) {
                        continue; 
                    }

                    // skip all non-display and non-hyperlink fields
                    if (fieldInfo.getFieldType() != ModelFormField.FieldInfo.DISPLAY && fieldInfo.getFieldType() != ModelFormField.FieldInfo.HYPERLINK) {
                        continue;
                    }

                    if (!modelFormField.shouldUse(localContext)) {
                        continue;
                    }

                    formStringRenderer.renderFormatItemRowCellOpen(buffer, localContext, this, modelFormField);

                    modelFormField.renderFieldString(buffer, localContext, formStringRenderer);
                
                    formStringRenderer.renderFormatItemRowCellClose(buffer, localContext, this, modelFormField);
                }

                // render row formatting close
                formStringRenderer.renderFormatItemRowClose(buffer, localContext, this);
            }
        }
    }
    
    public void renderHiddenIgnoredFields(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer) {
        Iterator fieldIter = this.fieldList.iterator();
        while (fieldIter.hasNext()) {
            ModelFormField modelFormField = (ModelFormField) fieldIter.next();
            ModelFormField.FieldInfo fieldInfo = modelFormField.getFieldInfo();

            // render hidden/ignored field widget
            switch (fieldInfo.getFieldType()) {
                case ModelFormField.FieldInfo.HIDDEN:
                case ModelFormField.FieldInfo.IGNORED:
                if (modelFormField.shouldUse(context)) {
                    modelFormField.renderFieldString(buffer, context, formStringRenderer);
                }
                break;
                    
                case ModelFormField.FieldInfo.DISPLAY:
                ModelFormField.DisplayField displayField = (ModelFormField.DisplayField) fieldInfo;
                if (displayField.getAlsoHidden() && modelFormField.shouldUse(context)) {
                    formStringRenderer.renderHiddenField(buffer, context, modelFormField, modelFormField.getEntry(context));
                }
                break;
                    
                case ModelFormField.FieldInfo.HYPERLINK:
                ModelFormField.HyperlinkField hyperlinkField = (ModelFormField.HyperlinkField) fieldInfo;
                if (hyperlinkField.getAlsoHidden() && modelFormField.shouldUse(context)) {
                    formStringRenderer.renderHiddenField(buffer, context, modelFormField, modelFormField.getEntry(context));
                }
                break;
            }
        }
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
    public String getDefaultTooltipStyle() {
        return this.defaultTooltipStyle;
    }

    /**
     * @return
     */
    public String getItemIndexSeparator() {
        if (UtilValidate.isNotEmpty(this.itemIndexSeparator)) {
            return this.itemIndexSeparator;
        } else {
            return "_o_";
        }
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
        
        if (itemIndex != null && "list".equals(this.getType())) {
            return formName + this.getItemIndexSeparator() + itemIndex.intValue();
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
        this.defaultEntityName = string;
    }

    /**
     * @param string
     */
    public void setDefaultMapName(String string) {
        this.defaultMapName = new FlexibleMapAccessor(string);
    }

    /**
     * @param string
     */
    public void setDefaultServiceName(String string) {
        this.defaultServiceName = string;
    }

    /**
     * @param string
     */
    public void setDefaultTitleStyle(String string) {
        this.defaultTitleStyle = string;
    }

    /**
     * @param string
     */
    public void setDefaultWidgetStyle(String string) {
        this.defaultWidgetStyle = string;
    }

    /**
     * @param string
     */
    public void setDefaultTooltipStyle(String string) {
        this.defaultTooltipStyle = string;
    }

    /**
     * @param string
     */
    public void setItemIndexSeparator(String string) {
        this.itemIndexSeparator = string;
    }

    /**
     * @param string
     */
    public void setListEntryName(String string) {
        this.listEntryName = string;
    }

    /**
     * @param string
     */
    public void setListName(String string) {
        this.listName = string;
    }

    /**
     * @param string
     */
    public void setName(String string) {
        this.name = string;
    }

    /**
     * @param string
     */
    public void setTarget(String string) {
        this.target = string;
    }

    /**
     * @param string
     */
    public void setTitle(String string) {
        this.title = string;
    }

    /**
     * @param string
     */
    public void setTooltip(String string) {
        this.tooltip = string;
    }

    /**
     * @param string
     */
    public void setType(String string) {
        this.type = string;
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
