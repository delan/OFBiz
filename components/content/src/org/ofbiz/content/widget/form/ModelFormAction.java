/*
 * $Id: ModelFormAction.java 2923 2004-07-31 12:17:42Z jonesde $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.finder.ByAndFinder;
import org.ofbiz.entity.finder.ByConditionFinder;
import org.ofbiz.entity.finder.PrimaryKeyFinder;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import org.w3c.dom.Element;


/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.9 $
 * @since      3.1
 */
public abstract class ModelFormAction {
    public static final String module = ModelFormAction.class.getName();

    protected ModelForm modelForm;

    public ModelFormAction(ModelForm modelForm, Element actionElement) {
        this.modelForm = modelForm;
        if (Debug.verboseOn()) Debug.logVerbose("Reading Screen action with name: " + actionElement.getNodeName(), module);
    }
    
    public abstract void runAction(Map context);
    
    public static List readSubActions(ModelForm modelForm, Element parentElement) {
        List actions = new LinkedList();
        
        List actionElementList = UtilXml.childElementList(parentElement);
        Iterator actionElementIter = actionElementList.iterator();
        while (actionElementIter.hasNext()) {
            Element actionElement = (Element) actionElementIter.next();
            if ("set".equals(actionElement.getNodeName())) {
                actions.add(new SetField(modelForm, actionElement));
            } else if ("property-map".equals(actionElement.getNodeName())) {
                actions.add(new PropertyMap(modelForm, actionElement));
            } else if ("property-to-field".equals(actionElement.getNodeName())) {
                actions.add(new PropertyToField(modelForm, actionElement));
            } else if ("script".equals(actionElement.getNodeName())) {
                actions.add(new Script(modelForm, actionElement));
            } else if ("service".equals(actionElement.getNodeName())) {
                actions.add(new Service(modelForm, actionElement));
            } else if ("entity-one".equals(actionElement.getNodeName())) {
                actions.add(new EntityOne(modelForm, actionElement));
            } else if ("entity-and".equals(actionElement.getNodeName())) {
                actions.add(new EntityAnd(modelForm, actionElement));
            } else if ("entity-condition".equals(actionElement.getNodeName())) {
                actions.add(new EntityCondition(modelForm, actionElement));
            } else {
                throw new IllegalArgumentException("Action element not supported with name: " + actionElement.getNodeName());
            }
        }
        
        return actions;
    }
    
    public static void runSubActions(List actions, Map context) {
        if (actions == null) return;
        
        Iterator actionIter = actions.iterator();
        while (actionIter.hasNext()) {
            ModelFormAction action = (ModelFormAction) actionIter.next();
            if (Debug.verboseOn()) Debug.logVerbose("Running screen action " + action.getClass().getName(), module);
            action.runAction(context);
        }
    }
    
    public static class SetField extends ModelFormAction {
        protected FlexibleMapAccessor field;
        protected FlexibleMapAccessor fromField;
        protected FlexibleStringExpander valueExdr;
        protected FlexibleStringExpander globalExdr;
        protected String type;
        
        public SetField(ModelForm modelForm, Element setElement) {
            super (modelForm, setElement);
            this.field = new FlexibleMapAccessor(setElement.getAttribute("field"));
            this.fromField = UtilValidate.isNotEmpty(setElement.getAttribute("from-field")) ? new FlexibleMapAccessor(setElement.getAttribute("from-field")) : null;
            this.valueExdr = UtilValidate.isNotEmpty(setElement.getAttribute("value")) ? new FlexibleStringExpander(setElement.getAttribute("value")) : null;
            this.globalExdr = new FlexibleStringExpander(setElement.getAttribute("global"));
            this.type = setElement.getAttribute("type");
            if (this.fromField != null && this.valueExdr != null) {
                throw new IllegalArgumentException("Cannot specify a from-field [" + setElement.getAttribute("from-field") + "] and a value [" + setElement.getAttribute("value") + "] on the set action in a screen widget");
            }
        }
        
        public void runAction(Map context) {
            String globalStr = this.globalExdr.expandString(context);
            // default to false
            boolean global = "true".equals(globalStr);
            
            Object newValue = null;
            if (this.fromField != null) {
                newValue = this.fromField.get(context);
                if (Debug.verboseOn()) Debug.logVerbose("In screen getting value for field from [" + this.fromField.getOriginalName() + "]: " + newValue, module);
            } else if (this.valueExdr != null) {
                newValue = this.valueExdr.expandString(context);
            }
            if (UtilValidate.isNotEmpty(this.type)) {
                try {
                    newValue = ObjectType.simpleTypeConvert(newValue, this.type, null, null);
                } catch (GeneralException e) {
                    String errMsg = "Could not convert field value for the field: [" + this.field.getOriginalName() + "] to the [" + this.type + "] type for the value [" + newValue + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new IllegalArgumentException(errMsg);
                }
         
            }
            if (Debug.verboseOn()) Debug.logVerbose("In screen setting field [" + this.field.getOriginalName() + "] to value: " + newValue, module);
            this.field.put(context, newValue);
            
            if (global) {
                Map globalCtx = (Map) context.get("globalContext");
                if (globalCtx != null) {
                    this.field.put(globalCtx, newValue);
                }
            }
            
            // this is a hack for backward compatibility with the JPublish page object
            Map page = (Map) context.get("page");
            if (page != null) {
                this.field.put(page, newValue);
            }
        }
    }
    
    public static class PropertyMap extends ModelFormAction {
        protected FlexibleStringExpander resourceExdr;
        protected FlexibleMapAccessor mapNameAcsr;
        protected FlexibleStringExpander globalExdr;
        
        public PropertyMap(ModelForm modelForm, Element setElement) {
            super (modelForm, setElement);
            this.resourceExdr = new FlexibleStringExpander(setElement.getAttribute("resource"));
            this.mapNameAcsr = new FlexibleMapAccessor(setElement.getAttribute("map-name"));
            this.globalExdr = new FlexibleStringExpander(setElement.getAttribute("global"));
        }
        
        public void runAction(Map context) {
            String globalStr = this.globalExdr.expandString(context);
            // default to false
            boolean global = "true".equals(globalStr);

            Locale locale = (Locale) context.get("locale");
            String resource = this.resourceExdr.expandString(context, locale);
            Map propertyMap = UtilProperties.getResourceBundleMap(resource, locale);
            this.mapNameAcsr.put(context, propertyMap);

            if (global) {
                Map globalCtx = (Map) context.get("globalContext");
                if (globalCtx != null) {
                    this.mapNameAcsr.put(globalCtx, propertyMap);
                }
            }
        }
    }
    
    public static class PropertyToField extends ModelFormAction {
        
        protected FlexibleStringExpander resourceExdr;
        protected FlexibleStringExpander propertyExdr;
        protected FlexibleMapAccessor fieldAcsr;
        protected FlexibleStringExpander defaultExdr;
        protected boolean noLocale;
        protected FlexibleMapAccessor argListAcsr;
        protected FlexibleStringExpander globalExdr;

        public PropertyToField(ModelForm modelForm, Element setElement) {
            super (modelForm, setElement);
            this.resourceExdr = new FlexibleStringExpander(setElement.getAttribute("resource"));
            this.propertyExdr = new FlexibleStringExpander(setElement.getAttribute("property"));
            this.fieldAcsr = new FlexibleMapAccessor(setElement.getAttribute("field"));
            this.defaultExdr = new FlexibleStringExpander(setElement.getAttribute("default"));
            noLocale = "true".equals(setElement.getAttribute("no-locale"));
            this.argListAcsr = new FlexibleMapAccessor(setElement.getAttribute("arg-list-name"));
            this.globalExdr = new FlexibleStringExpander(setElement.getAttribute("global"));
        }
        
        public void runAction(Map context) {
            String globalStr = this.globalExdr.expandString(context);
            // default to false
            boolean global = "true".equals(globalStr);

            Locale locale = (Locale) context.get("locale");
            String resource = this.resourceExdr.expandString(context, locale);
            String property = this.propertyExdr.expandString(context, locale);
            
            String value = null;
            if (noLocale) {
                value = UtilProperties.getPropertyValue(resource, property);
            } else {
                value = UtilProperties.getMessage(resource, property, locale);
            }
            if (value == null || value.length() == 0) {
                value = this.defaultExdr.expandString(context);
            }
            
            // note that expanding the value string here will handle defaultValue and the string from 
            //  the properties file; if we decide later that we don't want the string from the properties 
            //  file to be expanded we should just expand the defaultValue at the beginning of this method.
            value = FlexibleStringExpander.expandString(value, context);

            if (!argListAcsr.isEmpty()) {
                List argList = (List) argListAcsr.get(context);
                if (argList != null && argList.size() > 0) {
                    value = MessageFormat.format(value, argList.toArray());
                }
            }

            fieldAcsr.put(context, value);
        }
    }
    
    public static class Script extends ModelFormAction {
        protected String location;
        
        public Script(ModelForm modelForm, Element scriptElement) {
            super (modelForm, scriptElement);
            this.location = scriptElement.getAttribute("location");
        }
        
        public void runAction(Map context) {
            if (location.endsWith(".bsh")) {
                try {
                    BshUtil.runBshAtLocation(location, context);
                } catch (GeneralException e) {
                    String errMsg = "Error running BSH script at location [" + location + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new IllegalArgumentException(errMsg);
                }
            } else {
                throw new IllegalArgumentException("For screen script actions the script type is not yet support for location:" + location);
            }
        }
    }

    public static class Service extends ModelFormAction {
        protected FlexibleStringExpander serviceNameExdr;
        protected FlexibleMapAccessor resultMapNameAcsr;
        protected FlexibleStringExpander autoFieldMapExdr;
        protected FlexibleStringExpander resultMapListIteratorNameExdr;
        protected FlexibleStringExpander resultMapListNameExdr;
        protected Map fieldMap;
        
        public Service(ModelForm modelForm, Element serviceElement) {
            super (modelForm, serviceElement);
            this.serviceNameExdr = new FlexibleStringExpander(serviceElement.getAttribute("service-name"));
            this.resultMapNameAcsr = UtilValidate.isNotEmpty(serviceElement.getAttribute("result-map-name")) ? new FlexibleMapAccessor(serviceElement.getAttribute("result-map-name")) : null;
            this.autoFieldMapExdr = new FlexibleStringExpander(serviceElement.getAttribute("auto-field-map"));
            this.resultMapListIteratorNameExdr = new FlexibleStringExpander(serviceElement.getAttribute("result-map-list-iterator-name"));
            this.resultMapListNameExdr = new FlexibleStringExpander(serviceElement.getAttribute("result-map-list-name"));
            
            List fieldMapElementList = UtilXml.childElementList(serviceElement, "field-map");
            if (fieldMapElementList.size() > 0) {
                this.fieldMap = new HashMap();
                Iterator fieldMapElementIter = fieldMapElementList.iterator();
                while (fieldMapElementIter.hasNext()) {
                    Element fieldMapElement = (Element) fieldMapElementIter.next();
                    // set the env-name for each field-name, noting that if no field-name is specified it defaults to the env-name
                    this.fieldMap.put(
                            new FlexibleMapAccessor(UtilFormatOut.checkEmpty(fieldMapElement.getAttribute("field-name"), fieldMapElement.getAttribute("env-name"))), 
                            new FlexibleMapAccessor(fieldMapElement.getAttribute("env-name")));
                }
            }
        }
        
        public void runAction(Map context) {
            String serviceNameExpanded = this.serviceNameExdr.expandString(context);
            if (UtilValidate.isEmpty(serviceNameExpanded)) {
                throw new IllegalArgumentException("Service name was empty, expanded from: " + this.serviceNameExdr.getOriginal());
            }
            
            String autoFieldMapString = this.autoFieldMapExdr.expandString(context);
            boolean autoFieldMapBool = !"false".equals(autoFieldMapString);
            
            try {
                Map serviceContext = null;
                if (autoFieldMapBool) {
                    serviceContext = this.modelForm.getDispatcher(context).getDispatchContext().makeValidContext(serviceNameExpanded, ModelService.IN_PARAM, context);
                } else {
                    serviceContext = new HashMap();
                }
                
                if (this.fieldMap != null) {
                    Iterator fieldMapEntryIter = this.fieldMap.entrySet().iterator();
                    while (fieldMapEntryIter.hasNext()) {
                        Map.Entry entry = (Map.Entry) fieldMapEntryIter.next();
                        FlexibleMapAccessor serviceContextFieldAcsr = (FlexibleMapAccessor) entry.getKey();
                        FlexibleMapAccessor contextEnvAcsr = (FlexibleMapAccessor) entry.getValue();
                        serviceContextFieldAcsr.put(serviceContext, contextEnvAcsr.get(context));
                    }
                }
                
                Map result = this.modelForm.getDispatcher(context).runSync(serviceNameExpanded, serviceContext);
                
                if (this.resultMapNameAcsr != null) {
                    this.resultMapNameAcsr.put(context, result);
                } else {
                    context.putAll(result);
                }
                String listIteratorName = resultMapListIteratorNameExdr.expandString(context);
                Object obj = result.get(listIteratorName);
                if (obj != null && obj instanceof EntityListIterator) {
                	context.put("listIteratorName", listIteratorName);
                    context.put(listIteratorName, obj);
                }
                String listName = resultMapListNameExdr.expandString(context);
                List lst = (List)result.get(listName);
                if (lst != null ) {
                	context.put("listName", listName);
                    context.put(listName, lst);
                }
            } catch (GenericServiceException e) {
                String errMsg = "Error calling service with name " + serviceNameExpanded + ": " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    public static class EntityOne extends ModelFormAction {
        protected PrimaryKeyFinder finder;
        
        public EntityOne(ModelForm modelForm, Element entityOneElement) {
            super (modelForm, entityOneElement);
            finder = new PrimaryKeyFinder(entityOneElement);
        }
        
        public void runAction(Map context) {
            try {
                finder.runFind(context, this.modelForm.getDelegator(context));
            } catch (GeneralException e) {
                String errMsg = "Error doing entity query by condition: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    public static class EntityAnd extends ModelFormAction {
        protected ByAndFinder finder;
        protected FlexibleMapAccessor listAcsr;
        
        public EntityAnd(ModelForm modelForm, Element entityAndElement) {
            super (modelForm, entityAndElement);
            this.listAcsr = new FlexibleMapAccessor(entityAndElement.getAttribute("list-name"));
            finder = new ByAndFinder(entityAndElement);
        }
        
        public void runAction(Map context) {
            try {
                finder.runFind(context, this.modelForm.getDelegator(context));
                Object obj = listAcsr.get(context);
                if (obj != null && obj instanceof EntityListIterator) {
                    String iterName = listAcsr.getOriginalName();
                    context.put("listIteratorName", iterName);
                }
            } catch (GeneralException e) {
                String errMsg = "Error doing entity query by condition: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
        
    }

    public static class EntityCondition extends ModelFormAction {
        ByConditionFinder finder;
        protected FlexibleMapAccessor listAcsr;
        
        public EntityCondition(ModelForm modelForm, Element entityConditionElement) {
            super (modelForm, entityConditionElement);
            this.listAcsr = new FlexibleMapAccessor(entityConditionElement.getAttribute("list-name"));
            finder = new ByConditionFinder(entityConditionElement);
        }
        
        public void runAction(Map context) {
            try {
                finder.runFind(context, this.modelForm.getDelegator(context));
                Object obj = listAcsr.get(context);
                if (obj != null && obj instanceof EntityListIterator) {
                    String iterName = listAcsr.getOriginalName();
                    context.put("listIteratorName", iterName);
                }
            } catch (GeneralException e) {
                String errMsg = "Error doing entity query by condition: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }
}

