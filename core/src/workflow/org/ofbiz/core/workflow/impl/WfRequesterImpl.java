/*
 * $Id$
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
package org.ofbiz.core.workflow.impl;

import java.util.*;

import org.ofbiz.core.service.*;
import org.ofbiz.core.workflow.*;

/**
 * WfRequesterImpl - Workflow Requester implementation
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class WfRequesterImpl implements WfRequester {
    
    public static final String module = WfRequesterImpl.class.getName();

    protected Map performers = null;
      
    /**
     * Method WfRequesterImpl.
     */
    public WfRequesterImpl() {
        this.performers = new HashMap();
    }

    /**
     * @see org.ofbiz.core.workflow.WfRequester#registerProcess(org.ofbiz.core.workflow.WfProcess, java.util.Map, org.ofbiz.core.service.GenericRequester)
     */  
    public void registerProcess(WfProcess process, Map context, GenericRequester requester) throws WfException {
        if (process == null)
            throw new WfException("Process cannot be null");
        if (context == null)
            throw new WfException("Context should not be null");

        performers.put(process, requester);
        WfProcessMgr mgr = process.manager();

        // Validate the process context w/ what was passed.
        try {
            ModelService.validate(mgr.contextSignature(), context, true);
        } catch (GenericServiceException e) {
            throw new WfException("Context passed does not validate against defined signature: ", e);
        }

        // Set the context w/ the process        
        Map localContext = new HashMap(context);
        localContext.putAll(mgr.getInitialContext());
        process.setProcessContext(localContext);       
    }

    /**
     * @see org.ofbiz.core.workflow.WfRequester#howManyPerformer()
     */    
    public int howManyPerformer() throws WfException {
        return performers.size();
    }
  
    /**
     * @see org.ofbiz.core.workflow.WfRequester#getIteratorPerformer()
     */
    public Iterator getIteratorPerformer() throws WfException {
        return performers.keySet().iterator();
    }
   
    /**
     * @see org.ofbiz.core.workflow.WfRequester#getSequencePerformer(int)
     */
    public List getSequencePerformer(int maxNumber) throws WfException {
        if (maxNumber > 0)
            return new ArrayList(performers.keySet()).subList(0, (maxNumber - 1));
        return new ArrayList(performers.keySet());
    }
  
    /**
     * @see org.ofbiz.core.workflow.WfRequester#isMemberOfPerformer(org.ofbiz.core.workflow.WfProcess)
     */
    public boolean isMemberOfPerformer(WfProcess member) throws WfException {
        return performers.containsKey(member);
    }
   
    /**
     * @see org.ofbiz.core.workflow.WfRequester#receiveEvent(org.ofbiz.core.workflow.WfEventAudit)
     */
    public synchronized void receiveEvent(WfEventAudit event) throws WfException, InvalidPerformer {
        // Should the source of the audit come from the process? if so use this.
        WfProcess process = null;

        try {
            process = (WfProcess) event.source();
        } catch (SourceNotAvailable sna) {
            throw new InvalidPerformer("Could not get the performer", sna);
        } catch (ClassCastException cce) {
            throw new InvalidPerformer("Not a valid process object", cce);
        }
        if (process == null)
            throw new InvalidPerformer("No performer specified");
        if (!performers.containsKey(process))
            throw new InvalidPerformer("Performer not assigned to this requester");

        GenericRequester req = null;

        if (performers.containsKey(process))
            req = (GenericRequester) performers.get(process);
        if (req != null)
            req.receiveResult(process.result());
    }
}

