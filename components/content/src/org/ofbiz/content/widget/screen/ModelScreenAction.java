/*
 * $Id: ModelScreenAction.java,v 1.6 2004/07/22 05:26:57 jonesde Exp $
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.finder.ByAndFinder;
import org.ofbiz.entity.finder.ByConditionFinder;
import org.ofbiz.entity.finder.PrimaryKeyFinder;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.w3c.dom.Element;

/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.6 $
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
        protected FlexibleStringExpander globalExdr;
        
        public SetField(ModelScreen modelScreen, Element setElement) {
            super (modelScreen, setElement);
            this.field = new FlexibleMapAccessor(setElement.getAttribute("field"));
            this.fromField = UtilValidate.isNotEmpty(setElement.getAttribute("from-field")) ? new FlexibleMapAccessor(setElement.getAttribute("from-field")) : null;
            this.valueExdr = UtilValidate.isNotEmpty(setElement.getAttribute("value")) ? new FlexibleStringExpander(setElement.getAttribute("value")) : null;
            this.globalExdr = new FlexibleStringExpander(setElement.getAttribute("global"));
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
            } else if (this.valueExdr != null) {
                newValue = this.valueExdr.expandString(context);
            }
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

