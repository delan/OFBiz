/*
 * $Id$
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
package org.ofbiz.content.widget.screen;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.finder.ByAndFinder;
import org.ofbiz.entity.finder.ByConditionFinder;
import org.ofbiz.entity.finder.PrimaryKeyFinder;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import org.w3c.dom.Element;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      3.1
 */
public abstract class ModelScreenAction {
    public static final String module = ModelScreenAction.class.getName();

    protected ModelScreen modelScreen;

    public ModelScreenAction(ModelScreen modelScreen, Element actionElement) {
        this.modelScreen = modelScreen;
        if (Debug.verboseOn()) Debug.logVerbose("Reading Screen action with name: " + actionElement.getNodeName(), module);
    }
    
    public abstract void runAction(Map context);
    
    public static List readSubActions(ModelScreen modelScreen, Element parentElement) {
        List actions = new LinkedList();
        
        List actionElementList = UtilXml.childElementList(parentElement);
        Iterator actionElementIter = actionElementList.iterator();
        while (actionElementIter.hasNext()) {
            Element actionElement = (Element) actionElementIter.next();
            if ("set".equals(actionElement.getNodeName())) {
                actions.add(new SetField(modelScreen, actionElement));
            } else if ("property-map".equals(actionElement.getNodeName())) {
                actions.add(new PropertyMap(modelScreen, actionElement));
            } else if ("property-to-field".equals(actionElement.getNodeName())) {
                actions.add(new PropertyToField(modelScreen, actionElement));
            } else if ("script".equals(actionElement.getNodeName())) {
                actions.add(new Script(modelScreen, actionElement));
            } else if ("service".equals(actionElement.getNodeName())) {
                actions.add(new Service(modelScreen, actionElement));
            } else if ("entity-one".equals(actionElement.getNodeName())) {
                actions.add(new EntityOne(modelScreen, actionElement));
            } else if ("entity-and".equals(actionElement.getNodeName())) {
                actions.add(new EntityAnd(modelScreen, actionElement));
            } else if ("entity-condition".equals(actionElement.getNodeName())) {
                actions.add(new EntityCondition(modelScreen, actionElement));
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
            ModelScreenAction action = (ModelScreenAction) actionIter.next();
            if (Debug.verboseOn()) Debug.logVerbose("Running screen action " + action.getClass().getName(), module);
            action.runAction(context);
        }
    }
    
    public static class SetField extends ModelScreenAction {
        protected FlexibleMapAccessor field;
        protected FlexibleMapAccessor fromField;
        protected FlexibleStringExpander valueExdr;
        protected FlexibleStringExpander defaultExdr;
        protected FlexibleStringExpander globalExdr;
        protected String type;
        protected String toScope;
        protected String fromScope;
        
        public SetField(ModelScreen modelScreen, Element setElement) {
            super (modelScreen, setElement);
            this.field = new FlexibleMapAccessor(setElement.getAttribute("field"));
            this.fromField = UtilValidate.isNotEmpty(setElement.getAttribute("from-field")) ? new FlexibleMapAccessor(setElement.getAttribute("from-field")) : null;
            this.valueExdr = UtilValidate.isNotEmpty(setElement.getAttribute("value")) ? new FlexibleStringExpander(setElement.getAttribute("value")) : null;
            this.defaultExdr = UtilValidate.isNotEmpty(setElement.getAttribute("default-value")) ? new FlexibleStringExpander(setElement.getAttribute("default-value")) : null;
            this.globalExdr = new FlexibleStringExpander(setElement.getAttribute("global"));
            this.type = setElement.getAttribute("type");
            this.toScope = setElement.getAttribute("to-scope");
            this.fromScope = setElement.getAttribute("from-scope");
            if (this.fromField != null && this.valueExdr != null) {
                throw new IllegalArgumentException("Cannot specify a from-field [" + setElement.getAttribute("from-field") + "] and a value [" + setElement.getAttribute("value") + "] on the set action in a screen widget");
            }
        }
        
        public void runAction(Map context) {
            String globalStr = this.globalExdr.expandString(context);
            // default to false
            boolean global = "true".equals(globalStr);
            
            Object newValue = null;
            if (this.fromScope != null && this.fromScope.equals("user")) {
                if (this.fromField != null) {
                    HttpSession session = (HttpSession)context.get("session");
                	newValue = getInMemoryPersistedFromField(session, context);
                    if (Debug.verboseOn()) Debug.logVerbose("In user getting value for field from [" + this.fromField.getOriginalName() + "]: " + newValue, module);
                } else if (this.valueExdr != null) {
                    newValue = this.valueExdr.expandString(context);
                }
                
            } else if (this.fromScope != null && this.fromScope.equals("application")) {
                if (this.fromField != null) {
                    ServletContext servletContext = (ServletContext)context.get("application");
                	newValue = getInMemoryPersistedFromField(servletContext, context);
                    if (Debug.verboseOn()) Debug.logVerbose("In application getting value for field from [" + this.fromField.getOriginalName() + "]: " + newValue, module);
                } else if (this.valueExdr != null) {
                    newValue = this.valueExdr.expandString(context);
                }
                
            } else {
                if (this.fromField != null) {
                    newValue = this.fromField.get(context);
                    if (Debug.verboseOn()) Debug.logVerbose("In screen getting value for field from [" + this.fromField.getOriginalName() + "]: " + newValue, module);
                } else if (this.valueExdr != null) {
                    newValue = this.valueExdr.expandString(context);
                }
            }

            // If newValue is still empty, use the default value
           	if (this.defaultExdr != null) {
           		if (ObjectType.isEmpty(newValue)) {
            		newValue = this.defaultExdr.expandString(context);
               	}
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
            if (this.toScope != null && this.toScope.equals("user")) {
                    String originalName = this.field.getOriginalName();
                    List currentWidgetTrail = (List)context.get("_WIDGETTRAIL_");
                    String newKey = "";
                    if (currentWidgetTrail != null)
                        newKey = StringUtil.join(currentWidgetTrail, "|");
                    if (UtilValidate.isNotEmpty(newKey))
                        newKey += "|";
                    newKey += originalName;
                    HttpSession session = (HttpSession)context.get("session");
                    session.setAttribute(newKey, newValue);
                    if (Debug.verboseOn()) Debug.logVerbose("In user setting value for field from [" + this.field.getOriginalName() + "]: " + newValue, module);
                
            } else if (this.toScope != null && this.toScope.equals("application")) {
                    String originalName = this.field.getOriginalName();
                    List currentWidgetTrail = (List)context.get("_WIDGETTRAIL_");
                    String newKey = "";
                    if (currentWidgetTrail != null)
                        newKey = StringUtil.join(currentWidgetTrail, "|");
                    if (UtilValidate.isNotEmpty(newKey))
                        newKey += "|";
                    newKey += originalName;
                    ServletContext servletContext = (ServletContext)context.get("application");
                    servletContext.setAttribute(newKey, newValue);
                    if (Debug.verboseOn()) Debug.logVerbose("In application setting value for field from [" + this.field.getOriginalName() + "]: " + newValue, module);
                
            } else {
            	if (Debug.verboseOn()) Debug.logVerbose("In screen setting field [" + this.field.getOriginalName() + "] to value: " + newValue, module);
                this.field.put(context, newValue);
            }
            
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
    	
    	public Object getInMemoryPersistedFromField( Object storeAgent, Map context) {
    	            
                    Object newValue = null;
                    String originalName = this.fromField.getOriginalName();
                    List currentWidgetTrail = (List)context.get("_WIDGETTRAIL_");
                    List trailList = new ArrayList();
                    if (currentWidgetTrail != null)
                        trailList.addAll(currentWidgetTrail);
                    
                    for (int i=trailList.size(); i >= 0; i--) {
                    	List subTrail = trailList.subList(0,i);
                    	String newKey = null;
                    	if (subTrail.size() > 0)
                    	    newKey = StringUtil.join(subTrail, "|") + "|" + originalName;
                    	else
                    	    newKey = originalName;
                        
                    	if (storeAgent instanceof ServletContext)
                    		newValue = ((ServletContext)storeAgent).getAttribute(newKey);
                    	else if (storeAgent instanceof HttpSession)
                    		newValue = ((HttpSession)storeAgent).getAttribute(newKey);
                    	if (newValue != null)
                            break;
                    }
                    return newValue;
        }
    }
    
    
    public static class PropertyMap extends ModelScreenAction {
        protected FlexibleStringExpander resourceExdr;
        protected FlexibleMapAccessor mapNameAcsr;
        protected FlexibleStringExpander globalExdr;
        
        public PropertyMap(ModelScreen modelScreen, Element setElement) {
            super (modelScreen, setElement);
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
    
    public static class PropertyToField extends ModelScreenAction {
        
        protected FlexibleStringExpander resourceExdr;
        protected FlexibleStringExpander propertyExdr;
        protected FlexibleMapAccessor fieldAcsr;
        protected FlexibleStringExpander defaultExdr;
        protected boolean noLocale;
        protected FlexibleMapAccessor argListAcsr;
        protected FlexibleStringExpander globalExdr;

        public PropertyToField(ModelScreen modelScreen, Element setElement) {
            super (modelScreen, setElement);
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
    
    public static class Script extends ModelScreenAction {
        protected String location;
        
        public Script(ModelScreen modelScreen, Element scriptElement) {
            super (modelScreen, scriptElement);
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

    public static class Service extends ModelScreenAction {
        protected FlexibleStringExpander serviceNameExdr;
        protected FlexibleMapAccessor resultMapNameAcsr;
        protected FlexibleStringExpander autoFieldMapExdr;
        protected Map fieldMap;
        
        public Service(ModelScreen modelScreen, Element serviceElement) {
            super (modelScreen, serviceElement);
            this.serviceNameExdr = new FlexibleStringExpander(serviceElement.getAttribute("service-name"));
            this.resultMapNameAcsr = UtilValidate.isNotEmpty(serviceElement.getAttribute("result-map-name")) ? new FlexibleMapAccessor(serviceElement.getAttribute("result-map-name")) : null;
            this.autoFieldMapExdr = new FlexibleStringExpander(serviceElement.getAttribute("auto-field-map"));
            
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
                    serviceContext = this.modelScreen.getDispatcher(context).getDispatchContext().makeValidContext(serviceNameExpanded, ModelService.IN_PARAM, context);
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
                
                Map result = this.modelScreen.getDispatcher(context).runSync(serviceNameExpanded, serviceContext);
                
                if (this.resultMapNameAcsr != null) {
                    this.resultMapNameAcsr.put(context, result);
                } else {
                    context.putAll(result);
                }
            } catch (GenericServiceException e) {
                String errMsg = "Error calling service with name " + serviceNameExpanded + ": " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    public static class EntityOne extends ModelScreenAction {
        protected PrimaryKeyFinder finder;
        
        public EntityOne(ModelScreen modelScreen, Element entityOneElement) {
            super (modelScreen, entityOneElement);
            finder = new PrimaryKeyFinder(entityOneElement);
        }
        
        public void runAction(Map context) {
            try {
                finder.runFind(context, this.modelScreen.getDelegator(context));
            } catch (GeneralException e) {
                String errMsg = "Error doing entity query by condition: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    public static class EntityAnd extends ModelScreenAction {
        protected ByAndFinder finder;
        
        public EntityAnd(ModelScreen modelScreen, Element entityAndElement) {
            super (modelScreen, entityAndElement);
            finder = new ByAndFinder(entityAndElement);
        }
        
        public void runAction(Map context) {
            try {
                finder.runFind(context, this.modelScreen.getDelegator(context));
            } catch (GeneralException e) {
                String errMsg = "Error doing entity query by condition: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    public static class EntityCondition extends ModelScreenAction {
        ByConditionFinder finder;
        
        public EntityCondition(ModelScreen modelScreen, Element entityConditionElement) {
            super (modelScreen, entityConditionElement);
            finder = new ByConditionFinder(entityConditionElement);
        }
        
        public void runAction(Map context) {
            try {
                finder.runFind(context, this.modelScreen.getDelegator(context));
            } catch (GeneralException e) {
                String errMsg = "Error doing entity query by condition: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }
}

