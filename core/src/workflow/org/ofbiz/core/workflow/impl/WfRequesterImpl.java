/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.*;
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
    
    protected String name;
    protected List performers;
    
    // TODO : I believe this is the starting point for a workflow. We need to add in an API to start a process.
    
    /** Create a new WfRequester
     * @param name of this requester
     */
    public WfRequesterImpl(String name) {
        this.name = name;
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
    public void receiveEvent(WfEventAudit event) throws WfException, InvalidPerformer {
        // Implement this
    }
    
}
