/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.Iterator;
import java.util.List;

/**
 * <p><b>Title:</b> WfResource.java
 * <p><b>Description:</b> Workflow Resource Interface
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
 *@created    October 29, 2001
 *@version    1.0
 */

public interface WfResource  {
    
    /** Gets the number of work items
     * @throws WfException
     * @return Count of work items
     */
    public int howManyWorkItem() throws WfException;
    
    /** Gets an iterator of work items
     * @throws WfException
     * @return Iterator of work items
     */
    public Iterator getIteratorWorkItem() throws WfException;
    
    /** Gets the work items
     * @param maxNumber
     * @throws WfException
     * @return List of WfAssignment objects.
     */
    public List getSequenceWorkItem(int maxNumber) throws WfException;
    
    /** Checks if an assignment object is associated with this resource
     * @param member The assignment object to check
     * @throws WfException
     * @return true if assignment is part of the work list
     */
    public boolean isMemberOfWorkItems(WfAssignment member) throws WfException;
    
   /** Gets the resource key.
     * @throws WfException
     * @return String of the resouce key.
     */
    public String resourceKey() throws WfException;
    
    /** Gets the resource name
     * @throws WfException
     * @return String of the resource name
     */
    public String resourceName() throws WfException;
    
    /** Gets the role id of this resource
     * @throws WfException
     * @return String role id of this participant or null if none
     */
    public String resourceRoleId() throws WfException;
    
    /** Gets the party id of this resource
     * @throws WfException
     * @return String party id of this participant or null if none
     */
    public String resourcePartyId() throws WfException;
    
    /** Release the resouce from the assignement
     * @param fromAssigment
     * @param releaseInfo
     * @throws WfException
     * @throws NotAssigned
     */
    public void release(WfAssignment fromAssignment, String releaseInfo) throws WfException, NotAssigned;    
    
} // interface WfResourceOperations
