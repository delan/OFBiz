/*
 * $Id: ModelService.java,v 1.6 2004/02/11 16:49:36 ajzeneski Exp $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.service.group.GroupModel;
import org.ofbiz.service.group.GroupServiceModel;
import org.ofbiz.service.group.ServiceGroupReader;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.OrderedSet;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;

/**
 * Generic Service Model Class
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.6 $
 * @since      2.0
 */
public class ModelService {

    public static final String module = ModelService.class.getName();

    public static final String OUT_PARAM = "OUT";
    public static final String IN_PARAM = "IN";

    public static final String RESPONSE_MESSAGE = "responseMessage";
    public static final String RESPOND_SUCCESS = "success";
    public static final String RESPOND_ERROR = "error";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String ERROR_MESSAGE_LIST = "errorMessageList";
    public static final String ERROR_MESSAGE_MAP = "errorMessageMap";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String SUCCESS_MESSAGE_LIST = "successMessageList";
       
    /** The name of this service */
    public String name;

    /** The description of this service */
    public String description;

    /** The name of the service engine */
    public String engineName;

    /** The namespace of this service */
    public String nameSpace;

    /** The package name or location of this service */
    public String location;

    /** The method or function to invoke for this service */
    public String invoke;
    
    /** The default Entity to use for auto-attributes */
    public String defaultEntityName;
    
    /** Does this service require authorization */
    public boolean auth;

    /** Can this service be exported via RPC, RMI, SOAP, etc */
    public boolean export;
    
    /** Enable verbose debugging when calling this service */
    public boolean debug;

    /** Validate the context info for this service */
    public boolean validate;

    /** Create a transaction for this service (if one is not already in place...)? */
    public boolean useTransaction;
    
    /** Require a new transaction for this service */
    public boolean requireNewTransaction;
    
    /** Override the default transaction timeout, only works if we start the transaction */
    public int transactionTimeout;
    
    /** Set of services this service implements */
    public Set implServices = new OrderedSet();  
    
    /** Set of override parameters */
    public Set overrideParameters = new OrderedSet();

    /** List of permission groups for service invocation */
    public List permissionGroups = new LinkedList();

    /** Context Information, a list of parameters used by the service, contains ModelParam objects */
    protected Map contextInfo = new HashMap();

    /** Context Information, a list of parameters used by the service, contains ModelParam objects */
    protected List contextParamList = new LinkedList();
    
    /** Flag to say if we have pulled in our addition parameters from our implemented service(s) */
    protected boolean inheritedParameters = false;
    
    public ModelService() {}
    
    public ModelService(ModelService model) {
        this.name = model.name;
        this.description = model.description;
        this.engineName = model.engineName;
        this.nameSpace = model.nameSpace;
        this.location = model.location;
        this.invoke = model.invoke;
        this.defaultEntityName = model.defaultEntityName;
        this.auth = model.auth;
        this.export = model.export;
        this.validate = model.validate;
        this.useTransaction = model.useTransaction || true;
        this.requireNewTransaction = model.requireNewTransaction || false;
        this.transactionTimeout = model.transactionTimeout;
        this.implServices = model.implServices;
        this.overrideParameters = model.overrideParameters;
        this.inheritedParameters = model.inheritedParameters();
        
        List modelParamList = model.getModelParamList();
        Iterator i = modelParamList.iterator();
        while (i.hasNext()) {        
            this.addParamClone((ModelParam) i.next());
        }                
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(name + "::");
        buf.append(description + "::");
        buf.append(engineName + "::");
        buf.append(nameSpace + "::");
        buf.append(location + "::");
        buf.append(invoke + "::");
        buf.append(defaultEntityName + "::");
        buf.append(auth + "::");
        buf.append(export + "::");
        buf.append(validate + "::");
        buf.append(useTransaction + "::");
        buf.append(requireNewTransaction + "::");
        buf.append(transactionTimeout + "::");
        buf.append(implServices + "::");
        buf.append(overrideParameters + "::");
        buf.append(contextInfo + "::");
        buf.append(contextParamList + "::");
        buf.append(inheritedParameters + "::");        
        return buf.toString();
    }

    public String debugInfo() {
        if (debug || Debug.verboseOn()) {
            return " [" + this.toString() + "]";
        }
        return "";
    }

    /**
     * Test if we have already inherited our interface parameters
     * @return boolean
     */
    public boolean inheritedParameters() {
        return this.inheritedParameters;
    }
    
    /** 
     * Gets the ModelParam by name
     * @param name The name of the parameter to get
     * @return ModelParam object with the specified name
     */
    public ModelParam getParam(String name) {        
        return (ModelParam) contextInfo.get(name);
    }

    /**
     * Adds a parameter definition to this service; puts on list in order added
     * then sorts by order if specified.
     */
    public void addParam(ModelParam param) {
        if (param != null) {        
            contextInfo.put(param.name, param);
            contextParamList.add(param);
        }        
    }
        
    private void copyParams(Collection params) {
        if (params != null) {
            Iterator i = params.iterator();
            while (i.hasNext()) {
                ModelParam param = (ModelParam) i.next();       
                addParam(param);
            }
        }
    }
    
    /**
     * Adds a clone of a parameter definition to this service     
     */
    public void addParamClone(ModelParam param) {
        if (param != null) {        
            ModelParam newParam = new ModelParam(param);          
            addParam(newParam);
        }
    }   
    
    private void copyParamsAndClone(Collection params) {        
        if (params != null) {
            Iterator i = params.iterator();
            while (i.hasNext()) {            
                ModelParam param = (ModelParam) i.next();
                addParamClone(param);
            }            
        }        
    }

    public Set getAllParamNames() {        
        Set nameList = new TreeSet();
        Iterator i = this.contextParamList.iterator();

        while (i.hasNext()) {
            ModelParam p = (ModelParam) i.next();
            nameList.add(p.name);
        }
        return nameList;
    }

    public Set getInParamNames() {        
        Set nameList = new TreeSet();        
        Iterator i = this.contextParamList.iterator();

        while (i.hasNext()) {
            ModelParam p = (ModelParam) i.next();
            // don't include OUT parameters in this list, only IN and INOUT
            if ("OUT".equals(p.mode)) continue;
            nameList.add(p.name);
        }
        return nameList;
    }
    
    public Set getOutParamNames() {
        Set nameList = new TreeSet();        
        Iterator i = this.contextParamList.iterator();

        while (i.hasNext()) {
            ModelParam p = (ModelParam) i.next();
            // don't include IN parameters in this list, only OUT and INOUT
            if ("IN".equals(p.mode)) continue;
            nameList.add(p.name);
        }
        return nameList;        
    }

    /**
     * Validates a Map against the IN or OUT parameter information
     * @param test The Map object to test
     * @param mode Test either mode IN or mode OUT
     */
    public void validate(Map test, String mode) throws ServiceValidationException {        
        Map requiredInfo = new HashMap();
        Map optionalInfo = new HashMap();
        boolean verboseOn = Debug.verboseOn();

        if (verboseOn) Debug.logVerbose("[ModelService.validate] : {" + name + "} : Validating context - " + test, module);

        // do not validate results with errors
        if (mode.equals(OUT_PARAM) && test != null && test.containsKey(RESPONSE_MESSAGE) &&
            test.get(RESPONSE_MESSAGE).equals(RESPOND_ERROR)) {
            if (verboseOn) Debug.logVerbose("[ModelService.validate] : {" + name + "} : response was an error, not validating.", module);
            return;
        }

        // get the info values
        Collection values = contextInfo.values();
        Iterator i = values.iterator();

        while (i.hasNext()) {
            ModelParam p = (ModelParam) i.next();

            if (p.mode.equals("INOUT") || p.mode.equals(mode)) {
                if (!p.optional)
                    requiredInfo.put(p.name, p.type);
                else
                    optionalInfo.put(p.name, p.type);
            }
        }

        // get the test values
        Map requiredTest = new HashMap();
        Map optionalTest = new HashMap();

        if (test == null) test = new HashMap();
        requiredTest.putAll(test);
        
        List requiredButNull = new ArrayList();
        if (requiredTest != null) {
            List keyList = new ArrayList(requiredTest.keySet());
            Iterator t = keyList.iterator();

            while (t.hasNext()) {
                Object key = t.next();
                Object value = requiredTest.get(key);

                if (!requiredInfo.containsKey(key)) {
                    requiredTest.remove(key);
                    optionalTest.put(key, value);
                } else if (value == null) {
                    requiredButNull.add(key);
                }
            }
        }
        
        // check for requiredButNull fields and return an error since null values are not allowed for required fields
        if (requiredButNull.size() > 0) {
            String missing = "";
            Iterator rbni = requiredButNull.iterator();
            while (rbni.hasNext()) {
                String missingKey = (String) rbni.next();
                missing = missing + missingKey;
                if (rbni.hasNext()) {
                    missing = missing + ", ";
                }               
            }
            throw new ServiceValidationException("The following required parameters found null (not allowed): " + missing);
        }

        if (verboseOn) {
            String requiredNames = "";
            Iterator requiredIter = requiredInfo.keySet().iterator();
            while (requiredIter.hasNext()) {            
                requiredNames = requiredNames + requiredIter.next();
                if (requiredIter.hasNext()) {
                    requiredNames = requiredNames + ", ";                    
                }
            }
            Debug.logVerbose("[ModelService.validate] : required fields - " + requiredNames, module);
            
            Debug.logVerbose("[ModelService.validate] : {" + name + "} : (" + mode + ") Required - " +
                requiredTest.size() + " / " + requiredInfo.size(), module);
            Debug.logVerbose("[ModelService.validate] : {" + name + "} : (" + mode + ") Optional - " +
                optionalTest.size() + " / " + optionalInfo.size(), module);
        }

        try {
            validate(requiredInfo, requiredTest, true, this.name);
            validate(optionalInfo, optionalTest, false, this.name);
        } catch (ServiceValidationException e) {
            Debug.logError("[ModelService.validate] : {" + name + "} : (" + mode + ") Required test error: " + e.toString(), module);
            throw e;
        }
    }

    /**
     * Validates a map of name, object types to a map of name, objects
     * @param info The map of name, object types
     * @param test The map to test its value types.
     * @param reverse Test the maps in reverse.
     */
    public static void validate(Map info, Map test, boolean reverse, String serviceName) throws ServiceValidationException {
        if (info == null || test == null) {
            throw new ServiceValidationException("Cannot validate NULL maps");
        }
        
        String serviceNameMessage = "";
        if (serviceName != null) {
            serviceNameMessage = "For service [" + serviceName + "] ";
        }

        // * Validate keys first
        Set testSet = test.keySet();
        Set keySet = info.keySet();

        // Quick check for sizes
        if (info.size() == 0 && test.size() == 0) return;
        // This is to see if the test set contains all from the info set (reverse)
        if (reverse && !testSet.containsAll(keySet)) {
            Set missing = new TreeSet(keySet);

            missing.removeAll(testSet);
            String missingStr = "";
            Iterator iter = missing.iterator();

            while (iter.hasNext()) {
                missingStr += (String) iter.next();
                if (iter.hasNext()) {
                    missingStr += ", ";
                }
            }

            throw new ServiceValidationException(serviceNameMessage + "the following required parameters are missing: " + missingStr);
        }
        // This is to see if the info set contains all from the test set
        if (!keySet.containsAll(testSet)) {
            Set extra = new TreeSet(testSet);

            extra.removeAll(keySet);
            String extraStr = "";
            Iterator iter = extra.iterator();

            while (iter.hasNext()) {
                extraStr += (String) iter.next();
                if (iter.hasNext()) {
                    extraStr += ", ";
                }
            }
            throw new ServiceValidationException(serviceNameMessage + "unknown parameters found: " + extraStr);
        }

        // * Validate types next
        Iterator i = testSet.iterator();

        while (i.hasNext()) {
            Object key = i.next();
            Object testObject = test.get(key);
            String infoType = (String) info.get(key);

            if (!ObjectType.instanceOf(testObject, infoType, null)) {
                String testType = testObject == null ? "null" : testObject.getClass().getName();
                throw new ServiceValidationException(serviceNameMessage + "type check failed for field " + key + "; expected type is " +
                        infoType + "; actual type is: " + testType);
            }
        }
    }

    /**
     * Gets the parameter names of the specified mode (IN/OUT/INOUT). The 
     * parameters will be returned in the order specified in the file.
     * Note: IN and OUT will also contains INOUT parameters.
     * @param mode The mode (IN/OUT/INOUT)
     * @param optional True if to include optional parameters
     * @return List of parameter names
     */
    public List getParameterNames(String mode, boolean optional) {        
        List names = new ArrayList();

        if (!"IN".equals(mode) && !"OUT".equals(mode) && !"INOUT".equals(mode)) {
            return names;
        }
        if (contextInfo.size() == 0) {
            return names;
        }
        Iterator i = contextParamList.iterator();

        while (i.hasNext()) {
            ModelParam param = (ModelParam) i.next();

            if (param.mode.equals("INOUT") || param.mode.equals(mode)) {
                if (optional || (!optional && !param.optional)) {
                    names.add(param.name);
                }
            }
        }
        return names;
    }

    /**
     * Creates a new Map based from an existing map with just valid parameters. 
     * Tries to convert parameters to required type.
     * @param source The source map
     * @param mode The mode which to build the new map
     */
    public Map makeValid(Map source, String mode) {
        return makeValid(source, mode, true);
    }
    
    /**
     * Creates a new Map based from an existing map with just valid parameters. 
     * Tries to convert parameters to required type.
     * @param source The source map
     * @param mode The mode which to build the new map
     * @param includeInternal When false will exclude internal fields
     */    
    public Map makeValid(Map source, String mode, boolean includeInternal) {        
        Map target = new HashMap();

        if (source == null) {
            return target;
        }
        if (!"IN".equals(mode) && !"OUT".equals(mode) && !"INOUT".equals(mode)) {
            return target;
        }
        if (contextInfo.size() == 0) {
            return target;
        }
        Iterator i = contextParamList.iterator();

        while (i.hasNext()) {
            ModelParam param = (ModelParam) i.next();
            boolean internalParam = param.internal;

            if (param.mode.equals("INOUT") || param.mode.equals(mode)) {
                Object key = param.name;

                // internal map of strings
                if (param.stringMapPrefix != null && param.stringMapPrefix.length() > 0 && !source.containsKey(key)) {
                    Map paramMap = this.makePrefixMap(source, param);
                    if (paramMap != null && paramMap.size() > 0) {
                        target.put(key, paramMap);
                    }
                // internal list of strings
                } else if (param.stringListSuffix != null && param.stringListSuffix.length() > 0 && !source.containsKey(key)) {
                    List paramList = this.makeSuffixList(source, param);
                    if (paramList != null && paramList.size() > 0) {
                        target.put(key, paramList);
                    }
                // other attributes
                } else {
                    if (source.containsKey(key)) {
                        if ((param.internal && includeInternal) || (!param.internal)) {
                            Object value = source.get(key);
    
                            try {
                                value = ObjectType.simpleTypeConvert(value, param.type, null, null);
                            } catch (GeneralException e) {
                                Debug.logWarning("[ModelService.makeValid] : Simple type conversion of param " +
                                    key + " failed: " + e.toString(), module);
                            }
                            target.put(key, value);
                        }
                    }
                }
            }
        }
        return target;
    }

    private Map makePrefixMap(Map source, ModelParam param) {
        Map paramMap = new HashMap();
        Set sourceSet = source.keySet();
        Iterator i = sourceSet.iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            if (key.startsWith(param.stringMapPrefix)) {
                paramMap.put(key, source.get(key));
            }
        }
        return paramMap;
    }

    private List makeSuffixList(Map source, ModelParam param) {
        List paramList = new ArrayList();
        Set sourceSet = source.keySet();
        Iterator i = sourceSet.iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            if (key.endsWith(param.stringListSuffix)) {
                paramList.add(source.get(key));
            }
        }
        return paramList;
    }

    public boolean containsPermissions() {
        if (this.permissionGroups != null && this.permissionGroups.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Evaluates permissions for a service.
     * @param security The security object to use for permission checking
     * @param userLogin The logged in user's value object
     * @return true if all permissions evaluate true.
     */
    public boolean evalPermissions(Security security, GenericValue userLogin) {
        if (this.containsPermissions()) {
            Iterator i = this.permissionGroups.iterator();
            while (i.hasNext()) {
                ModelPermGroup group = (ModelPermGroup) i.next();
                if (!group.evalPermissions(security, userLogin)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * Gets a list of required IN parameters in sequence.
     * @return A list of required IN parameters in the order which they were defined.
     */
    public List getInParameterSequence(Map source) {
        List target = new LinkedList();

        if (source == null) {
            return target;
        }
        if (contextInfo == null || contextInfo.size() == 0) {
            return target;
        }
        Iterator i = this.contextParamList.iterator();

        while (i.hasNext()) {
            ModelParam p = (ModelParam) i.next();

            // don't include OUT parameters in this list, only IN and INOUT
            if ("OUT".equals(p.mode)) continue;

            Object srcObject = source.get(p.name);

            if (srcObject != null) {
                target.add(srcObject);
            }
        }
        return target;
    }

    /** 
     * Returns a list of ModelParam objects in the order they were defined when 
     * the service was created.
     */
    public List getModelParamList() {
        return new LinkedList(this.contextParamList);
    }

    /** 
     * Returns a list of ModelParam objects in the order they were defined when 
     * the service was created.
     */
    public List getInModelParamList() {
        List inList = new LinkedList();
        Iterator i = this.contextParamList.iterator();

        while (i.hasNext()) {
            ModelParam p = (ModelParam) i.next();

            // don't include OUT parameters in this list, only IN and INOUT
            if ("OUT".equals(p.mode)) continue;
            inList.add(p);
        }
        return inList;
    }
        
    /**
     * Run the interface update and inherit all interface parameters
     * @param dctx The DispatchContext to use for service lookups
     */
    public synchronized void interfaceUpdate(DispatchContext dctx) throws GenericServiceException {                       
        if (!inheritedParameters) {            
            // services w/ engine 'group' auto-implement the grouped services
            if (this.engineName.equals("group") && implServices.size() == 0) {
                GroupModel group = ServiceGroupReader.getGroupModel(this.location);
                if (group != null) {
                    List groupedServices = group.getServices();
                    Iterator i = groupedServices.iterator();
                    while (i.hasNext()) {
                        GroupServiceModel sm = (GroupServiceModel) i.next();
                        implServices.add(sm.getName());                        
                        if (Debug.verboseOn()) Debug.logVerbose("Adding service [" + sm.getName() + "] as interface of: [" + this.name + "]", module);
                    }
                }                
            }
            
            // handle interfaces
            if (implServices != null && implServices.size() > 0 && dctx != null) {                 
                // backup the old info                 
                List oldParams = this.contextParamList;
                              
                // reset the fields
                this.contextInfo = new HashMap();
                this.contextParamList = new LinkedList();
                                              
                Iterator implIter = implServices.iterator();
                while (implIter.hasNext()) {
                    String serviceName = (String) implIter.next();
                    ModelService model = dctx.getModelService(serviceName);
                    if (model != null) {                                                                                                                                             
                        copyParamsAndClone(model.contextInfo.values());                                                                           
                    } else {
                        Debug.logWarning("Inherited model [" + serviceName + "] not found for [" + this.name + "]", module);
                    }
                }                          
                
                // put the old values back on top
                copyParams(oldParams);
            }                           
                  
            // handle any override parameters
            if (overrideParameters != null && overrideParameters.size() > 0) {                                                                   
                Iterator keySetIter = overrideParameters.iterator();
                while (keySetIter.hasNext()) {                    
                    ModelParam overrideParam = (ModelParam) keySetIter.next();                    
                    ModelParam existingParam = (ModelParam) contextInfo.get(overrideParam.name);
                                                           
                    // keep the list clean, remove it then add it back
                    contextParamList.remove(existingParam);                  
                    
                    if (existingParam != null) {                                                
                        // now re-write the parameters
                        if (overrideParam.type != null && overrideParam.type.length() > 0) {
                            existingParam.type = overrideParam.type;                                   
                        }
                        if (overrideParam.mode != null && overrideParam.mode.length() > 0) {
                            existingParam.mode = overrideParam.mode;
                        }
                        if (overrideParam.entityName != null && overrideParam.entityName.length() > 0) {
                            existingParam.entityName = overrideParam.entityName;
                        }
                        if (overrideParam.fieldName != null && overrideParam.fieldName.length() > 0) {
                            existingParam.fieldName = overrideParam.fieldName;
                        }
                        if (overrideParam.formLabel != null && overrideParam.formLabel.length() > 0) {
                            existingParam.formLabel = overrideParam.formLabel;
                        }
                        if (overrideParam.overrideFormDisplay) {
                            existingParam.formDisplay = overrideParam.formDisplay;
                        }
                        if (overrideParam.overrideOptional) {
                            existingParam.optional = overrideParam.optional;
                        }                        
                        addParam(existingParam);                        
                    } else {
                        Debug.logWarning("Override param found but no parameter existing; ignoring: " + overrideParam.name, module);                   
                    }
                }                                                       
            }
            
            // set the flag so we don't do this again
            this.inheritedParameters = true;
        }
    }            
}
