/*
 * $Id: ModelForm.java,v 1.4 2003/11/15 21:52:15 jonesde Exp $
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
package org.ofbiz.content.widget.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FlexibleMapAccessor;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.w3c.dom.Element;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Widget Library - Form model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.4 $
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
    protected String paginateTarget;

    protected List altTargets = new LinkedList();
    protected List autoFieldsServices = new LinkedList();
    protected List autoFieldsEntities = new LinkedList();
    protected List sortOrderFields = new LinkedList();

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

        // check if there is a parent form to inherit from
        String parentResource = formElement.getAttribute("extends-resource");
        String parentForm = formElement.getAttribute("extends");
        //TODO: Modify this to allow for extending a form with the same name but different resource
        if (parentForm.length() > 0 && !parentForm.equals(formElement.getAttribute("name"))) {
            ModelForm parent = null;
            // check if we have a resource name (part of the string before the ?)
            if (parentResource.length() > 0) {
                try {
                    parent = FormFactory.getFormFromClass(parentResource, parentForm, delegator, dispatcher);
                } catch (Exception e) {
                    Debug.logError(e, "Failed to load parent form definition '" + parentForm + "' at resource '" + parentResource + "'", module);
                }
            } else {
                // try to find a form definition in the same file
                Element rootElement = formElement.getOwnerDocument().getDocumentElement();
                List formElements = UtilXml.childElementList(rootElement, "form");
                //Uncomment below to add support for abstract forms
                //formElements.addAll(UtilXml.childElementList(rootElement, "abstract-form"));
                Iterator formElementIter = formElements.iterator();
                while (formElementIter.hasNext()) {
                    Element formElementEntry = (Element) formElementIter.next();
                    if (formElementEntry.getAttribute("name").equals(parentForm)) {
                        parent = new ModelForm(formElementEntry, delegator, dispatcher);
                        break;
                    }
                }
                if (parent == null) {
                    Debug.logError("Failed to find parent form defenition '" + parentForm + "' in same document.", module);
                }
            }

            if (parent != null) {
                this.type = parent.type;
                this.target = parent.target;
                this.title = parent.title;
                this.tooltip = parent.tooltip;
                this.listName = parent.listName;
                this.listEntryName = parent.listEntryName;
                this.tooltip = parent.tooltip;
                this.defaultEntityName = parent.defaultEntityName;
                this.defaultServiceName = parent.defaultServiceName;
                this.defaultTitleStyle = parent.defaultTitleStyle;
                this.defaultWidgetStyle = parent.defaultWidgetStyle;
                this.defaultTooltipStyle = parent.defaultTooltipStyle;
                this.itemIndexSeparator = parent.itemIndexSeparator;
                this.fieldList = parent.fieldList;
                this.fieldMap = parent.fieldMap;
            }
        }

        this.name = formElement.getAttribute("name");
        if (this.type == null || formElement.hasAttribute("type"))
            this.type = formElement.getAttribute("type");
        if (this.target == null || formElement.hasAttribute("target"))
            this.target = formElement.getAttribute("target");
        if (this.title == null || formElement.hasAttribute("title"))
            this.title = formElement.getAttribute("title");
        if (this.tooltip == null || formElement.hasAttribute("tooltip"))
            this.tooltip = formElement.getAttribute("tooltip");
        if (this.listName == null || formElement.hasAttribute("listName"))
            this.listName = formElement.getAttribute("list-name");
        if (this.listEntryName == null || formElement.hasAttribute("listEntryName"))
            this.listEntryName = formElement.getAttribute("list-entry-name");
        if (this.defaultMapName == null || formElement.hasAttribute("default-map-name"))
            this.setDefaultMapName(formElement.getAttribute("default-map-name"));
        if (this.defaultEntityName == null || formElement.hasAttribute("defaultEntityName"))
            this.defaultEntityName = formElement.getAttribute("default-entity-name");
        if (this.defaultServiceName == null || formElement.hasAttribute("defaultServiceName"))
            this.defaultServiceName = formElement.getAttribute("default-service-name");
        if (this.defaultTitleStyle == null || formElement.hasAttribute("defaultTitleStyle"))
            this.defaultTitleStyle = formElement.getAttribute("default-title-style");
        if (this.defaultWidgetStyle == null || formElement.hasAttribute("defaultWidgetStyle"))
            this.defaultWidgetStyle = formElement.getAttribute("default-widget-style");
        if (this.defaultTooltipStyle == null || formElement.hasAttribute("defaultTooltipStyle"))
            this.defaultTooltipStyle = formElement.getAttribute("default-tooltip-style");
        if (this.itemIndexSeparator == null || formElement.hasAttribute("itemIndexSeparator"))
            this.itemIndexSeparator = formElement.getAttribute("item-index-separator");
        if (this.paginateTarget == null || formElement.hasAttribute("paginateTarget"))
            this.paginateTarget = formElement.getAttribute("paginate-target");

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
            //Debug.logInfo("Added field " + modelFormField.getName() + " from def, mapName=" + modelFormField.getMapName(), module);
        }

        // get the sort-order
        Element sortOrderElement = UtilXml.firstChildElement(formElement, "sort-order");
        if (sortOrderElement != null) {
            // read in sort-field
            List sortFieldElements = UtilXml.childElementList(sortOrderElement, "sort-field");
            Iterator sortFieldElementIter = sortFieldElements.iterator();
            while (sortFieldElementIter.hasNext()) {
                Element sortFieldElement = (Element) sortFieldElementIter.next();
                this.sortOrderFields.add(sortFieldElement.getAttribute("name"));
            }
        }

        // reorder fields according to sort order
        if (sortOrderFields.size() > 0) {
            List sortedFields = new ArrayList(this.fieldList.size());
            Iterator sortOrderFieldIter = this.sortOrderFields.iterator();
            while (sortOrderFieldIter.hasNext()) {
                String fieldName = (String) sortOrderFieldIter.next();
                if (UtilValidate.isEmpty(fieldName)) {
                    continue;
                }

                // get all fields with the given name from the existing list and put them in the sorted list
                Iterator fieldIter = this.fieldList.iterator();
                while (fieldIter.hasNext()) {
                    ModelFormField modelFormField = (ModelFormField) fieldIter.next();
                    if (fieldName.equals(modelFormField.getName())) {
                        // matched the name; remove from the original last and add to the sorted list
                        fieldIter.remove();
                        sortedFields.add(modelFormField);
                    }
                }
            }
            // now add all of the rest of the fields from fieldList, ie those that were not explicitly listed in the sort order
            sortedFields.addAll(this.fieldList);
            // sortedFields all done, set fieldList
            this.fieldList = sortedFields;
        }
    }

    /**
     * add/override modelFormField using the fieldList and fieldMap
     *
     * @return The same ModelFormField, or if merged with an existing field, the existing field.
     */
    public ModelFormField addUpdateField(ModelFormField modelFormField) {
        if (!modelFormField.isUseWhenEmpty()) {
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
            Debug.logError(e, errmsg, module);
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
                            ModelFormField modelFormField = this.addFieldFromEntityField(modelEntity, modelField, autoFieldsService.defaultFieldType);
                            if (UtilValidate.isNotEmpty(autoFieldsService.mapName)) {
                                modelFormField.setMapName(autoFieldsService.mapName);
                            }

                            // continue to skip creating based on service param
                            continue;
                        }
                    }
                }

                ModelFormField modelFormField = this.addFieldFromServiceParam(modelService, modelParam, autoFieldsService.defaultFieldType);
                if (UtilValidate.isNotEmpty(autoFieldsService.mapName)) {
                    modelFormField.setMapName(autoFieldsService.mapName);
                }
            }
        }
    }

    public ModelFormField addFieldFromServiceParam(ModelService modelService, ModelParam modelParam, String defaultFieldType) {
        // create field def from service param def
        ModelFormField newFormField = new ModelFormField(this);
        newFormField.setName(modelParam.name);
        newFormField.setServiceName(modelService.name);
        newFormField.setAttributeName(modelParam.name);
        newFormField.setTitle(modelParam.formLabel);
        newFormField.induceFieldInfoFromServiceParam(modelService, modelParam, defaultFieldType);
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
            ModelFormField modelFormField = this.addFieldFromEntityField(modelEntity, modelField, autoFieldsEntity.defaultFieldType);
            if (UtilValidate.isNotEmpty(autoFieldsEntity.mapName)) {
                modelFormField.setMapName(autoFieldsEntity.mapName);
            }
        }
    }

    public ModelFormField addFieldFromEntityField(ModelEntity modelEntity, ModelField modelField, String defaultFieldType) {
        // create field def from entity field def
        ModelFormField newFormField = new ModelFormField(this);
        newFormField.setName(modelField.getName());
        newFormField.setEntityName(modelEntity.getEntityName());
        newFormField.setFieldName(modelField.getName());
        newFormField.induceFieldInfoFromEntityField(modelEntity, modelField, defaultFieldType);
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
        // if this is a list form, don't useRequestParameters
        if ("list".equals(this.type) || "multi".equals(this.type)) {
            context.put("useRequestParameters", Boolean.FALSE);
        }

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
        } else if ("upload".equals(this.type)) {
            this.renderSingleFormString(buffer, context, formStringRenderer, positions);
        } else {
            throw new IllegalArgumentException("The type " + this.getType() + " is not supported for form with name " + this.getName());
        }
    }

    public void renderSingleFormString(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer, int positions) {
        Iterator fieldIter = null;

        Set alreadyRendered = new TreeSet();

        // render form open
        formStringRenderer.renderFormOpen(buffer, context, this);

        // render all hidden & ignored fields
        this.renderHiddenIgnoredFields(buffer, context, formStringRenderer, alreadyRendered);

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
            //Debug.logInfo("In single form evaluating use-when for field " + currentFormField.getName() + ": " + currentFormField.getUseWhen(), module);
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
                this.renderHiddenIgnoredFields(buffer, localContext, formStringRenderer, null);

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

    public void renderHiddenIgnoredFields(StringBuffer buffer, Map context, FormStringRenderer formStringRenderer, Set alreadyRendered) {
        Iterator fieldIter = this.fieldList.iterator();
        while (fieldIter.hasNext()) {
            ModelFormField modelFormField = (ModelFormField) fieldIter.next();
            ModelFormField.FieldInfo fieldInfo = modelFormField.getFieldInfo();

            // render hidden/ignored field widget
            switch (fieldInfo.getFieldType()) {
                case ModelFormField.FieldInfo.HIDDEN :
                case ModelFormField.FieldInfo.IGNORED :
                    if (modelFormField.shouldUse(context)) {
                        modelFormField.renderFieldString(buffer, context, formStringRenderer);
                        if (alreadyRendered != null)
                            alreadyRendered.add(modelFormField.getName());
                    }
                    break;

                case ModelFormField.FieldInfo.DISPLAY :
                    ModelFormField.DisplayField displayField = (ModelFormField.DisplayField) fieldInfo;
                    if (displayField.getAlsoHidden() && modelFormField.shouldUse(context)) {
                        formStringRenderer.renderHiddenField(buffer, context, modelFormField, modelFormField.getEntry(context));
                        // don't add to already rendered here, or the display won't ger rendered: if (alreadyRendered != null) alreadyRendered.add(modelFormField.getName());
                    }
                    break;

                case ModelFormField.FieldInfo.HYPERLINK :
                    ModelFormField.HyperlinkField hyperlinkField = (ModelFormField.HyperlinkField) fieldInfo;
                    if (hyperlinkField.getAlsoHidden() && modelFormField.shouldUse(context)) {
                        formStringRenderer.renderHiddenField(buffer, context, modelFormField, modelFormField.getEntry(context));
                        // don't add to already rendered here, or the hyperlink won't ger rendered: if (alreadyRendered != null) alreadyRendered.add(modelFormField.getName());
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
                    throw new IllegalArgumentException(
                        "Return value from target condition eval was not a Boolean: " + retVal.getClass().getName() + " [" + retVal + "] of form " + this.name);
                }

                if (condTrue) {
                    return altTarget.target;
                }
            }
        } catch (EvalError e) {
            String errmsg = "Error evaluating BeanShell target conditions on form " + this.name;
            Debug.logError(e, errmsg, module);
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

    /**
     * @return
     */
    public String getPaginateTarget() {
        return this.paginateTarget;
    }

    /**
     * @param string
     */
    public void setPaginateTarget(String string) {
        this.paginateTarget = string;
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
        public String defaultFieldType;
        public AutoFieldsService(Element element) {
            this.serviceName = element.getAttribute("service-name");
            this.mapName = element.getAttribute("map-name");
            this.defaultFieldType = element.getAttribute("default-field-type");
        }
    }

    public static class AutoFieldsEntity {
        public String entityName;
        public String mapName;
        public String defaultFieldType;
        public AutoFieldsEntity(Element element) {
            this.entityName = element.getAttribute("entity-name");
            this.mapName = element.getAttribute("map-name");
            this.defaultFieldType = element.getAttribute("default-field-type");
        }
    }

}
