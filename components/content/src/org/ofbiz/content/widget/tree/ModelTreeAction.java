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
package org.ofbiz.content.widget.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.finder.ByAndFinder;
import org.ofbiz.entity.finder.ByConditionFinder;
import org.ofbiz.entity.finder.PrimaryKeyFinder;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Widget Library - Tree model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      3.1
 */
public abstract class ModelTreeAction {
    public static final String module = ModelTreeAction.class.getName();

    protected ModelTree modelTree;
    protected ModelTree.ModelNode modelNode;
    protected ModelTree.ModelNode.ModelSubNode modelSubNode;

    public ModelTreeAction(ModelTree.ModelNode modelNode, Element actionElement) {
        if (Debug.verboseOn()) Debug.logVerbose("Reading Tree action with name: " + actionElement.getNodeName(), module);
        this.modelNode = modelNode;
        this.modelTree = modelNode.getModelTree();
    }
    
    public ModelTreeAction(ModelTree.ModelNode.ModelSubNode modelSubNode, Element actionElement) {
        if (Debug.verboseOn()) Debug.logVerbose("Reading Tree action with name: " + actionElement.getNodeName(), module);
        this.modelSubNode = modelSubNode;
        this.modelNode = this.modelSubNode.getNode();
        this.modelTree = this.modelNode.getModelTree();
    }
    
    public abstract void runAction(Map context);
    
/*
    public static List readSubActions(ModelTree.ModelNode modelNode, Element parentElement) {
        List actions = new LinkedList();
        
        List actionElementList = UtilXml.childElementList(parentElement);
        Iterator actionElementIter = actionElementList.iterator();
        while (actionElementIter.hasNext()) {
            Element actionElement = (Element) actionElementIter.next();
            if ("set".equals(actionElement.getNodeName())) {
                actions.add(new SetField(modelTree, actionElement));
            } else if ("script".equals(actionElement.getNodeName())) {
                actions.add(new Script(modelTree, actionElement));
            } else if ("service".equals(actionElement.getNodeName())) {
                actions.add(new Service(modelTree, actionElement));
            } else if ("entity-one".equals(actionElement.getNodeName())) {
                actions.add(new EntityOne(modelTree, actionElement));
            } else if ("entity-and".equals(actionElement.getNodeName())) {
                actions.add(new EntityAnd(modelTree, actionElement));
            } else if ("entity-condition".equals(actionElement.getNodeName())) {
                actions.add(new EntityCondition(modelTree, actionElement));
            } else {
                throw new IllegalArgumentException("Action element not supported with name: " + actionElement.getNodeName());
            }
        }
        
        return actions;
    }
    */
    
    public static void runSubActions(List actions, Map context) {
        Iterator actionIter = actions.iterator();
        while (actionIter.hasNext()) {
            ModelTreeAction action = (ModelTreeAction) actionIter.next();
            if (Debug.verboseOn()) Debug.logVerbose("Running tree action " + action.getClass().getName(), module);
            action.runAction(context);
        }
    }
    
    public static class SetField extends ModelTreeAction {
        protected FlexibleMapAccessor field;
        protected FlexibleMapAccessor fromField;
        protected FlexibleStringExpander valueExdr;
        protected FlexibleStringExpander globalExdr;
        protected String type;
        
        public SetField(ModelTree.ModelNode modelNode, Element setElement) {
            super (modelNode, setElement);
            this.field = new FlexibleMapAccessor(setElement.getAttribute("field"));
            this.fromField = UtilValidate.isNotEmpty(setElement.getAttribute("from-field")) ? new FlexibleMapAccessor(setElement.getAttribute("from-field")) : null;
            this.valueExdr = UtilValidate.isNotEmpty(setElement.getAttribute("value")) ? new FlexibleStringExpander(setElement.getAttribute("value")) : null;
            this.globalExdr = new FlexibleStringExpander(setElement.getAttribute("global"));
            this.type = setElement.getAttribute("type");
            if (this.fromField != null && this.valueExdr != null) {
                throw new IllegalArgumentException("Cannot specify a from-field [" + setElement.getAttribute("from-field") + "] and a value [" + setElement.getAttribute("value") + "] on the set action in a tree widget");
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
            if (UtilValidate.isNotEmpty(this.type)) {
                try {
                    newValue = ObjectType.simpleTypeConvert(newValue, this.type, null, null);
                } catch (GeneralException e) {
                    String errMsg = "Could not convert field value for the field: [" + this.field.getOriginalName() + "] to the [" + this.type + "] type for the value [" + newValue + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new IllegalArgumentException(errMsg);
                }
         
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
    
    public static class Script extends ModelTreeAction {
        protected String location;
        
        public Script(ModelTree.ModelNode modelNode, Element scriptElement) {
            super (modelNode, scriptElement);
            this.location = scriptElement.getAttribute("location");
        }
        
        public void runAction(Map context) {
            if (location.endsWith(".bsh")) {
                try {
                    context.put("_LIST_ITERATOR_", null);
                    BshUtil.runBshAtLocation(location, context);
                	Object obj = context.get("_LIST_ITERATOR_");
                	if (obj != null && obj instanceof EntityListIterator) {
                    	this.modelSubNode.setListIterator((ListIterator)obj);
                	} else {
                		if (obj instanceof List)
                        	this.modelSubNode.setListIterator(((List)obj).listIterator());
                	}
                } catch (GeneralException e) {
                    String errMsg = "Error running BSH script at location [" + location + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new IllegalArgumentException(errMsg);
                }
            } else {
                throw new IllegalArgumentException("For tree script actions the script type is not yet support for location:" + location);
            }
        }
    }

    public static class Service extends ModelTreeAction {
        protected FlexibleStringExpander serviceNameExdr;
        protected FlexibleMapAccessor resultMapNameAcsr;
        protected FlexibleStringExpander autoFieldMapExdr;
        protected FlexibleStringExpander resultMapListNameExdr;
        protected FlexibleStringExpander resultMapListIteratorNameExdr;
        protected FlexibleStringExpander resultMapValueNameExdr;
        protected FlexibleStringExpander valueNameExdr;
        protected Map fieldMap;
        
        public Service(ModelTree.ModelNode modelNode, Element serviceElement) {
            super (modelNode, serviceElement);
            initService(serviceElement);
        }
        
        public Service(ModelTree.ModelNode.ModelSubNode modelSubNode, Element serviceElement) {
            super (modelSubNode, serviceElement);
            initService(serviceElement);
        }
        
        public void initService( Element serviceElement ) {
            
            this.serviceNameExdr = new FlexibleStringExpander(serviceElement.getAttribute("service-name"));
            this.resultMapNameAcsr = UtilValidate.isNotEmpty(serviceElement.getAttribute("result-map-name")) ? new FlexibleMapAccessor(serviceElement.getAttribute("result-map-name")) : null;
            this.autoFieldMapExdr = new FlexibleStringExpander(serviceElement.getAttribute("auto-field-map"));
            this.resultMapListNameExdr = new FlexibleStringExpander(serviceElement.getAttribute("result-map-list-name"));
            this.resultMapListIteratorNameExdr = new FlexibleStringExpander(serviceElement.getAttribute("result-map-list-iterator-name"));
            this.resultMapValueNameExdr = new FlexibleStringExpander(serviceElement.getAttribute("result-map-value-name"));
            this.valueNameExdr = new FlexibleStringExpander(serviceElement.getAttribute("value-name"));
            
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
                    serviceContext = this.modelTree.getDispatcher().getDispatchContext().makeValidContext(serviceNameExpanded, ModelService.IN_PARAM, context);
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
                
                Map result = this.modelTree.getDispatcher().runSync(serviceNameExpanded, serviceContext);
                
                if (this.resultMapNameAcsr != null) {
                    this.resultMapNameAcsr.put(context, result);
                } else {
                    context.putAll(result);
                }
                String resultMapListName = resultMapListNameExdr.expandString(context);
                String resultMapListIteratorName = resultMapListIteratorNameExdr.expandString(context);
                String resultMapValueName = resultMapValueNameExdr.expandString(context);
                String valueName = valueNameExdr.expandString(context);
                
                if (this.modelSubNode != null) {
                    ListIterator iter = null;
                    if (UtilValidate.isNotEmpty(resultMapListIteratorName)) {
                        this.modelSubNode.setListIterator((ListIterator)result.get(resultMapListIteratorName));
                    } else if (UtilValidate.isNotEmpty(resultMapListName)) {
                	    List lst = (List)result.get(resultMapListName);
                        if (lst != null ) {
                            this.modelSubNode.setListIterator(lst.listIterator());
                        }
                    }
                } else {
                	if (UtilValidate.isNotEmpty(resultMapValueName)) {
                		if (UtilValidate.isNotEmpty(valueName)) {
                			context.put(valueName, result.get(resultMapValueName));
                    	} else {
                    		context.putAll((Map)result.get(resultMapValueName));
                    	}
                	}
                }
            } catch (GenericServiceException e) {
                String errMsg = "Error calling service with name " + serviceNameExpanded + ": " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    public static class EntityOne extends ModelTreeAction {
        protected PrimaryKeyFinder finder;
        
        public EntityOne(ModelTree.ModelNode modelNode, Element entityOneElement) {
            super (modelNode, entityOneElement);
            finder = new PrimaryKeyFinder(entityOneElement);
        }
        
        public void runAction(Map context) {
            try {
                finder.runFind(context, this.modelTree.getDelegator());
            } catch (GeneralException e) {
                String errMsg = "Error doing entity query by condition: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    public static class EntityAnd extends ModelTreeAction {
        protected ByAndFinder finder;
        boolean useCache = false;
        
        public EntityAnd(ModelTree.ModelNode.ModelSubNode modelSubNode, Element entityAndElement) {
            super (modelSubNode, entityAndElement);
            String useCacheString = entityAndElement.getAttribute("use-cache");            
            if ("true".equals(useCacheString)) {
            	useCache = true;
            }
            Document ownerDoc = entityAndElement.getOwnerDocument();
            if (!useCache)
                UtilXml.addChildElement(entityAndElement, "use-iterator", ownerDoc);
            entityAndElement.setAttribute( "list-name", "_LIST_ITERATOR_");
            finder = new ByAndFinder(entityAndElement);
        }
        
        public void runAction(Map context) {
            try {
                context.put("_LIST_ITERATOR_", null);
                finder.runFind(context, this.modelTree.getDelegator());
                Object obj = context.get("_LIST_ITERATOR_");
                if (obj != null && obj instanceof EntityListIterator) {
                    this.modelSubNode.setListIterator((ListIterator)obj);
                } else {
                	if (obj instanceof List)
                        this.modelSubNode.setListIterator(((List)obj).listIterator());
                }
            } catch (GeneralException e) {
                String errMsg = "Error doing entity query by condition: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    public static class EntityCondition extends ModelTreeAction {
        ByConditionFinder finder;
        boolean useCache = false;
        
        public EntityCondition(ModelTree.ModelNode.ModelSubNode modelSubNode, Element entityConditionElement) {
            super (modelSubNode, entityConditionElement);
            Document ownerDoc = entityConditionElement.getOwnerDocument();
            String useCacheString = entityConditionElement.getAttribute("use-cache");            
            if ("true".equals(useCacheString)) {
            	useCache = true;
            }
            if (!useCache)
                UtilXml.addChildElement(entityConditionElement, "use-iterator", ownerDoc);
            entityConditionElement.setAttribute( "list-name", "_LIST_ITERATOR_");
            finder = new ByConditionFinder(entityConditionElement);
        }
        
        public void runAction(Map context) {
            try {
                context.put("_LIST_ITERATOR_", null);
                finder.runFind(context, this.modelTree.getDelegator());
                Object obj = context.get("_LIST_ITERATOR_");
                if (obj != null && obj instanceof EntityListIterator) {
                    this.modelSubNode.setListIterator((ListIterator)obj);
                } else {
                	if (obj instanceof List)
                        this.modelSubNode.setListIterator(((List)obj).listIterator());
                }
            } catch (GeneralException e) {
                String errMsg = "Error doing entity query by condition: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }
}

