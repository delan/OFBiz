/*
 * $Id: WfProcessMgrImpl.java,v 1.2 2003/08/19 17:45:18 jonesde Exp $
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
package org.ofbiz.workflow.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.workflow.CannotChangeRequester;
import org.ofbiz.workflow.InvalidRequester;
import org.ofbiz.workflow.NotEnabled;
import org.ofbiz.workflow.RequesterRequired;
import org.ofbiz.workflow.TransitionNotAllowed;
import org.ofbiz.workflow.WfException;
import org.ofbiz.workflow.WfFactory;
import org.ofbiz.workflow.WfProcess;
import org.ofbiz.workflow.WfProcessMgr;
import org.ofbiz.workflow.WfRequester;
import org.ofbiz.workflow.WfUtil;

/**
 * WfProcessMgrImpl - Workflow Process Manager implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     David Ostrovsky (d.ostrovsky@gmx.de)
 * @version    $Revision: 1.2 $
 * @since      2.0
 */
public class WfProcessMgrImpl implements WfProcessMgr {

    public static final String module = WfProcessMgrImpl.class.getName();

    protected GenericValue processDef;
    
    protected String state; // will probably move to a runtime entity for the manager
    protected List processList; // will probably be a related entity to the runtime entity
    
    protected Map contextSignature = null;
    protected Map resultSignature = null;
    protected Map initialContext = null;
    
    /**
     * Method WfProcessMgrImpl.
     * @param delegator
     * @param packageId
     * @param packageVersion
     * @param processId
     * @param processVersion
     * @throws WfException
     */   
    public WfProcessMgrImpl(GenericDelegator delegator, String packageId, String packageVersion,
            String processId, String processVersion) throws WfException {
        Map finder = UtilMisc.toMap("packageId", packageId, "processId", processId);
        List order = UtilMisc.toList("-packageVersion", "-processVersion");

        if (packageVersion != null) finder.put("packageVersion", packageVersion);
        if (processVersion != null) finder.put("processVersion", processVersion);
        try {
            List processes = delegator.findByAnd("WorkflowProcess", finder, order);
            if (processes.size() == 0)
                throw new WfException("No process definition found for the specified processId");
            else
                processDef = EntityUtil.getFirst(processes);
        } catch (GenericEntityException e) {
            throw new WfException("Problems getting the process definition from the WorkflowProcess entity");
        }

        buildSignatures();
        buildInitialContext();
        processList = new ArrayList();
        state = "enabled";
        if (Debug.infoOn()) Debug.logInfo("[WfProcessMgr.init] : Create process manager (" +
                packageId + "[" + packageVersion + "]" + " / " + processId + "[" + processVersion + "]" + ")", module);
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#setProcessMgrState(java.lang.String)
     */
    public void setProcessMgrState(String newState) throws WfException, TransitionNotAllowed {            
        if (!newState.equals("enabled") || !newState.equals("disabled"))
            throw new TransitionNotAllowed();
        this.state = newState;
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#getSequenceProcess(int)
     */
    public List getSequenceProcess(int maxNumber) throws WfException {
        if (maxNumber > 0)
            return new ArrayList(processList.subList(0, maxNumber - 1));
        return processList;
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#createProcess(org.ofbiz.workflow.WfRequester)
     */
    public WfProcess createProcess(WfRequester requester) throws WfException, NotEnabled, 
            InvalidRequester, RequesterRequired {            
        if (state.equals("disabled"))
            throw new NotEnabled();

        if (requester == null)
            throw new RequesterRequired();

        // test if the requestor is OK: how?
        WfProcess process = WfFactory.getWfProcess(processDef, this);

        try {
            process.setRequester(requester);
        } catch (CannotChangeRequester ccr) {
            throw new WfException(ccr.getMessage(), ccr);
        }
        processList.add(process);
        Debug.logVerbose("[WfProcessMgr.createProcess] : Process created.", module);
        return process;
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#contextSignature()
     */   
    public Map contextSignature() throws WfException {
        return this.contextSignature;
    }
    
    /**
     * @see org.ofbiz.workflow.WfProcessMgr#howManyProcess()
     */
    public int howManyProcess() throws WfException {
        return processList.size();
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#processMgrStateType()
     */
    public List processMgrStateType() throws WfException {
        String[] list = {"enabled", "disabled"};
        return Arrays.asList(list);
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#category()
     */
    public String category() throws WfException {
        return processDef.getString("category");
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#version()
     */
    public String version() throws WfException {
        return processDef.getString("version");
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#description()
     */
    public String description() throws WfException {
        return processDef.getString("description");
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#name()
     */
    public String name() throws WfException {
        return processDef.getString("name");
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#resultSignature()
     */
    public Map resultSignature() throws WfException {
        return this.resultSignature;
    }
    
    /**
     * Method getInitialContext.
     * @return Map
     */
    public Map getInitialContext() {
        return initialContext;
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#isMemberOfProcess(org.ofbiz.workflow.WfProcess)
     */
    public boolean isMemberOfProcess(WfProcess member) throws WfException {
        return processList.contains(member);
    }

    /**
     * @see org.ofbiz.workflow.WfProcessMgr#getIteratorProcess()
     */
    public Iterator getIteratorProcess() throws WfException {
        return processList.iterator();
    }

    // Constructs the context/result signatures from the formalParameters
    private void buildSignatures() throws WfException {
        contextSignature = new HashMap();
        resultSignature = new HashMap();
        Collection params = null;

        try {
            Map fields = new HashMap();

            fields.put("packageId", processDef.getString("packageId"));
            fields.put("packageVersion", processDef.getString("packageVersion"));
            fields.put("processId", processDef.getString("processId"));
            fields.put("processVersion", processDef.getString("processVersion"));
            fields.put("applicationId", "_NA_");
            params = processDef.getDelegator().findByAnd("WorkflowFormalParam", fields);

        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        if (params == null)
            return;

        Iterator i = params.iterator();
        while (i.hasNext()) {
            GenericValue param = (GenericValue) i.next();
            String name = param.getString("formalParamId");
            String mode = param.getString("modeEnumId");
            String type = param.getString("dataTypeEnumId");

            if (mode.equals("WPM_IN") || mode.equals("WPM_INOUT"))
                contextSignature.put(name, WfUtil.getJavaType(type));
            else if (mode.equals("WPM_OUT") || mode.equals("WPM_INOUT"))
                resultSignature.put(name, WfUtil.getJavaType(type));
        }
    }
                    
    private void buildInitialContext() throws WfException {
        GenericDelegator delegator = processDef.getDelegator();
        this.initialContext = new HashMap();
        List dataFields = new ArrayList();
        try {
            // make fields
            Map fields = new HashMap();
            fields.put("packageId", processDef.get("packageId"));
            fields.put("packageVersion", processDef.get("packageVersion"));
            
            // first get all package fields
            fields.put("processId", "_NA_");
            fields.put("processVersion", "_NA_");            
            List data1 = delegator.findByAnd("WorkflowDataField", fields);
            dataFields.addAll(data1);
            
            // now get all process fields
            fields.put("processId", processDef.get("processId"));
            fields.put("processVersion", processDef.get("processVersion"));
            List data2 = delegator.findByAnd("WorkflowDataField", fields);
            dataFields.addAll(data2);                
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        if (dataFields == null)
            return;

        Iterator i = dataFields.iterator();
        
        while (i.hasNext()) {
            GenericValue dataField = (GenericValue) i.next();
            String name = dataField.getString("dataFieldName");                    
            String type = dataField.getString("dataTypeEnumId");
            String value = dataField.getString("initialValue");
   
            try {                
                initialContext.put(name, ObjectType.simpleTypeConvert(value, WfUtil.getJavaType(type), null, null));                          
            } catch (GeneralException e) {
                throw new WfException(e.getMessage(), e);
            }            
        }
    }      
}

