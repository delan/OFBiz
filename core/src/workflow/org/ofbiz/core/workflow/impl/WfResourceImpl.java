/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import java.util.*;
import org.ofbiz.core.workflow.*;

/**
 * <p><b>Title:</b> WfResourceImpl
 * <p><b>Description:</b> Workflow Resource Object implementation
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

public class WfResourceImpl implements WfResource {
    
    protected String resourceName;
    protected String resourceKey;
    protected List workItems;
    
    /** Creates a new WfResource
     * @param resourceName The name of the resource
     * @param resourceKey Uniquely identifies the resource
     * @param workItems Assignments associated with this resource
     */
    public WfResourceImpl(String resourceName, String resourceKey, List workItems) {
        this.resourceName = resourceName;
        this.resourceKey = resourceKey;
        this.workItems = workItems;
    }
    
    /** Gets the number of work items
     * @throws WfException
     * @return Count of work items
     */
    public int howManyWorkItem() throws WfException {
        return workItems.size();
    }
    
    /** Gets an iterator of work items
     * @throws WfException
     * @return Iterator of work items
     */
    public Iterator getIteratorWorkItem() throws WfException {
        return workItems.iterator();
    }
    
    /** Gets the work items
     * @param maxNumber
     * @throws WfException
     * @return List of WfAssignment objects.
     */
    public List getSequenceWorkItem(int maxNumber) throws WfException {
        if ( maxNumber > 0 )
            return workItems.subList(0,(maxNumber-1));
        return workItems;
    }
    
    /** Checks if an assignment object is associated with this resource
     * @param member The assignment object to check
     * @throws WfException
     * @return true if assignment is part of the work list
     */
    public boolean isMemberOfWorkItems(WfAssignment member) throws WfException {
        return workItems.contains(member);            
    }
    
    /** Gets the resource key.
     * @throws WfException
     * @return String of the resouce key.
     */
    public String resourceKey() throws WfException {
        return this.resourceKey;
    }
    
    /** Gets the resource name
     * @throws WfException
     * @return String of the resource name
     */
    public String resourceName() throws WfException {
        return this.resourceName;
    }
    
    /** Release the resouce from the assignement
     * @param fromAssigment
     * @param releaseInfo
     * @throws WfException
     * @throws NotAssigned
     */
    public void release(WfAssignment fromAssignment, String releaseInfo) throws WfException, NotAssigned {
        if ( !workItems.contains(fromAssignment) )
            throw new NotAssigned();
        workItems.remove(fromAssignment);
        // log the transaction
    }
}



