/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.workflow.*;

/**
 * <p><b>Title:</b> WfRequesterImpl
 * <p><b>Description:</b> Workflow Requester implementation
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
 *@author     Andy Zeneski (jaz@zsolv.com)
 *@created    November 15, 2001
 *@version    1.0
 */

public class WfRequesterImpl implements WfRequester {
    
    protected List performers;
    protected List contexts;
    protected List waiters;
    
    /** Create a new WfRequester
     */
    public WfRequesterImpl() {
        this.performers = new ArrayList();
        this.contexts = new ArrayList();
        this.waiters = new ArrayList();
    }
    
    /** Sets up a new process
     * @param context of the process   
     */
    public void processInfo(Map context) {
        processInfo(context,null);        
    }
    
    /** Sets up a new process
     * @param context of the process
     * @param requester GenericRequester for the service
     */
    public void processInfo(Map context, GenericRequester requester) {
        this.contexts.add(context);
        this.waiters.add(requester);
    }
    
    /** Registers a process with this requester; starts the process.
     *@param WfProcess to register
     *@throws WfException
     */
    public void registerProcess(WfProcess process) throws WfException {
        performers.add(process);
        if ( performers.size() != context.size() || waiters.size() != performers.size() )
            throw new WfException("Cannot match context/waiter to process.");
        process.setProcessContext(contextList.get(contextList.size() -1));
        process.start();
    }
    
    /** Gets the number of processes.
     * @throws WfException
     * @return Count of the number of workflow processes
     */
    public int howManyPerformer() throws WfException {
        return performers.size();
    }
    
    /** Gets an iterator of processes.
     * @throws WfException
     * @return Iterator of workflow processes.
     */
    public Iterator getIteratorPerformer() throws WfException {
        return performers.iterator();
    }
    
    /** A list of processes
     * @param maxNumber
     * @throws WfException
     * @return List of WfProcess objects.
     */
    public List getSequencePerformer(int maxNumber) throws WfException {
        if ( maxNumber > 0 )
            return performers.subList(0,(maxNumber-1));
        return performers;
    }
    
    /** Checks if a WfProcess is associated with this requester object
     * @param member
     * @throws WfException
     * @return true if the process is found.
     */
    public boolean isMemberOfPerformer(WfProcess member) throws WfException {
        return performers.contains(member);
    }
    
    /** Receives notice of event status changes
     * @param event
     * @throws WfException
     * @throws InvalidPerformer
     */
    public synchronized void receiveEvent(WfEventAudit event) throws WfException, InvalidPerformer {     
        // Should the source of the audit come from the process? if so use this.
        WfProcess process = null;
        try {
            process = (WfProcess) event.source();
        } 
        catch ( SourceNotAvailable sna ) {
            throw new InvalidPerformer("Could not get the performer.",sna);
        }
        catch ( ClassCastException cce ) {
            throw new InvalidPerformer("Not a valid process object.",cce);
        }
        if ( process == null )
            throw new InvalidPerformer("No performer specified.");
        if ( !performers.contains(process) )
            throw new InvalidPerformer("Performer not assigned to this requester.");
        
        
        // Implement me
    }
    
}
