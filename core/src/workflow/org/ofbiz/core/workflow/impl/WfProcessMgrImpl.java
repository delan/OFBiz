/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import java.util.*;
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
 *@author     David Ostrovsky (d.ostrovsky@gmx.de)
 *@created    November 19, 2001
 *@version    1.0
 */

public class WfProcessMgrImpl implements WfProcessMgr
{
    // Process Manager state types
    public static final int PROCESS_MGR_DISABLED = 10;
    public static final int PROCESS_MGR_ENABLED = 11;
    
    protected String name;
    protected String description;
    protected String version;
    protected String category;
    protected int state;
    protected List processList;
    
    /** Creates new WfProcessMgrImpl 
     * @param pName Initial value for attribute 'name'
     * @param pDescription Initial value for attribute 'description'
     * @param pCategory Initial value for attribute 'category'
     * @param pVersion Initial value for attribute 'version'
     */
    public WfProcessMgrImpl(String pName, String pDescription, 
                            String pCategory, String pVersion) {
        name = pName;
        description = pDescription;
        version = pVersion;
        
        processList = new ArrayList();
        state = PROCESS_MGR_DISABLED;
    }

    /**
     * @param newState
     * @throws WfException
     * @throws TransitionNotAllowed
     */
    public void setProcessMgrState(int newState) throws WfException, 
    TransitionNotAllowed {
        if (newState != PROCESS_MGR_DISABLED &&
            newState != PROCESS_MGR_ENABLED)
            throw new TransitionNotAllowed("TransitionNotAllowed Exception");
        state = newState;
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
        if (state == PROCESS_MGR_DISABLED)
            throw new NotEnabled("Process Manager not enabled");
        
        if (requester == null)
            throw new RequesterRequired("REquestor is null");
        
        // test if the requestor is OK: how?
        String key = null;  // work on this...
        WfProcess process = WfFactory.newWfProcess(null); // TODO: FIXME!
        
        try {
            process.setRequester(requester);
        } catch (CannotChangeRequester ccr) {
            throw new WfException("CannotChangeRequester Exception"); 
        }
        
        return process;
    }
    
    /**
     * @throws WfException
     * @return
     */
    public Map contextSignature() throws WfException {
        return new HashMap();
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
        return category;
    }
    
    /**
     * @throws WfException
     * @return
     */
    public String version() throws WfException {
        return version;
    }
    
    /**
     * @throws WfException
     * @return
     */
    public String description() throws WfException {
        return description;
    }
    
    /**
     * @throws WfException
     * @return
     */
    public String name() throws WfException {
        return name;
    }
    
    /**
     * @throws WfException
     * @return
     */
    public Map resultSignature() throws WfException {
        return new HashMap();
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
    
}
