/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * <p><b>Title:</b> WfExecutionObjectImpl
 * <p><b>Description:</b> Workflow Execution Object implementation
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     David Ostrovsky (d.ostrovsky@gmx.de)
 *@created    November 19, 2001
 *@version    1.0
 */

public class WfProcessMgrImpl implements WfProcessMgr {
    
    protected GenericValue processDef;    
    protected String state;        // will probably move to a runtime entity for the manager
    protected List processList; // will probably be a related entity to the runtime entity    
    protected Map contextSignature;
    protected Map resultSignature;
    
    /** Creates new WfProcessMgrImpl
     * @param delegator The GenericDelegator to use for the process definitions.
     * @param processId The unique key of the process definition.
     * @throws WfException
     */
    public WfProcessMgrImpl(GenericDelegator delegator, String packageId, String processId) throws WfException {
        try {
            Map finder = UtilMisc.toMap("packageId",packageId,"processId",processId);
            Collection processes = delegator.findByAnd("WorkflowProcess",finder);
            if ( processes.size() > 1 )
                throw new WfException("Unique processId does not exist. Entity value error");
            if ( processes.size() == 0 )
                throw new WfException("No process definition found for the specified processId");
            processDef = (GenericValue) processes.iterator().next();
        }
        catch ( GenericEntityException e ) {
            throw new WfException("Problems getting the process definition from the WorkflowProcess entity");
        }
                
        buildSignatures();        
        processList = new ArrayList();
        state = "enabled";
    }
    
    /**
     * @param newState
     * @throws WfException
     * @throws TransitionNotAllowed
     */
    public void setProcessMgrState(String newState) throws WfException,
    TransitionNotAllowed {
        if ( !newState.equals("enabled") || !newState.equals("disabled") )
            throw new TransitionNotAllowed();
        this.state = newState;
    }
    
    /**
     * @param maxNumber
     * @throws WfException
     * @return List of WfProcess objects.
     */
    public List getSequenceProcess(int maxNumber) throws WfException {
        if (maxNumber > 0)
            return new ArrayList(processList.subList(0, maxNumber - 1));
        return processList;
    }
    
    /**
     * Create a WfProcess object
     * @param requester
     * @throws WfException
     * @throws NotEnabled
     * @throws InvalidRequester
     * @throws RequesterRequired
     * @return WfProcess created
     */
    public WfProcess createProcess(WfRequester requester)
    throws WfException, NotEnabled, InvalidRequester, RequesterRequired {
        if ( state.equals("disabled") )
            throw new NotEnabled();
        
        if (requester == null)
            throw new RequesterRequired();
        
        // test if the requestor is OK: how?        
        WfProcess process = WfFactory.newWfProcess(processDef,this);
        
        try {
            process.setRequester(requester);
        }
        catch (CannotChangeRequester ccr) {
            throw new WfException(ccr.getMessage(),ccr);
        }
        return process;
    }
    
    /**
     * @throws WfException
     * @return
     */
    public Map contextSignature() throws WfException {
        return this.contextSignature;
    }
    
    /**
     * @throws WfException
     * @return
     */
    public int howManyProcess() throws WfException {
        return processList.size();
    }
    
    /**
     * @throws WfException
     * @return
     */
    public List processMgrStateType() throws WfException {
        String[] list = { "enabled", "disabled" };
        return Arrays.asList(list);
    }
    
    /**
     * @throws WfException
     * @return
     */
    public String category() throws WfException {
        return processDef.getString("category");
    }
    
    /**
     * @throws WfException
     * @return
     */
    public String version() throws WfException {
        return processDef.getString("version");
    }
    
    /**
     * @throws WfException
     * @return
     */
    public String description() throws WfException {
        return processDef.getString("description");
    }
    
    /**
     * @throws WfException
     * @return
     */
    public String name() throws WfException {
        return processDef.getString("name");
    }
    
    /**
     * @throws WfException
     * @return
     */
    public Map resultSignature() throws WfException {
        return this.resultSignature;
    }
    
    /**
     * @param member
     * @throws WfException
     * @return
     */
    public boolean isMemberOfProcess(WfProcess member) throws WfException {
        return processList.contains(member);
    }
    
    /**
     * @throws WfException
     * @return
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
            params = processDef.getRelated("WorkflowFormalParamsView");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        if ( params == null )
            return;
        
        Iterator i = params.iterator();
        while ( i.hasNext() ) {
            GenericValue param = (GenericValue) i.next();
            String name = param.getString("formalParamId");
            String mode = param.getString("modeEnumId");
            String type = param.getString("dataTypeEnumId");
            if ( mode.equals("WPM_IN") || mode.equals("WPM_INOUT") )
                contextSignature.put(name,getJavaType(type));
            else if ( mode.equals("WPM_OUT") || mode.equals("WPM_INOUT") )
                resultSignature.put(name,getJavaType(type));
        }                                    
    }
     
    // Gets the Java type from a XPDL datatype
    private String getJavaType(String xpdlType) {
        Map typeMap = new HashMap();
        typeMap.put("WDT_BOOLEAN","java.lang.Boolean");
        typeMap.put("WDT_STRING","java.lang.String");
        typeMap.put("WDT_INTEGER","java.lang.Long");
        typeMap.put("WDT_FLOAT","java.lang.Double");
        typeMap.put("WDT_DATETIME","java.sql.Timestamp");        
        if ( typeMap.containsKey(xpdlType) )
            return (String) typeMap.get(xpdlType);
        else
            return "java.lang.Object";
    }
}
