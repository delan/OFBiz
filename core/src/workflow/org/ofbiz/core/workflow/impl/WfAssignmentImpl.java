/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import org.ofbiz.core.workflow.*;

/**
 * <p><b>Title:</b> WfAssignmentImpl
 * <p><b>Description:</b> Workflow Assignment Object implementation
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
 *@created    November 15, 2001
 *@version    1.0
 */
public class WfAssignmentImpl implements WfAssignment {
    
    protected WfActivity activity;
    protected WfResource resource;
    
    /** Creates new WfAssignment
     *@param activity Sets the activity object for this assignment
     *@param resource Sets the resource object for this assignment
     */
    public WfAssignmentImpl(WfActivity activity, WfResource resource) {
        this.activity = activity;
        this.resource = resource;
    }
    
    /** Gets the activity object of this assignment.
     * @return WfActivity The activity object of this assignment
     * @throws WfException
     */
    public WfActivity activity() throws WfException {
        return this.activity;
    }
    
    /** Gets the assignee (resource) of this assignment
     * @return WfResource The assignee of this assignment
     * @throws WfException
     */
    public WfResource assignee() throws WfException {                   
        return this.resource;
    }
    
    /** Sets the assignee of this assignment
     * @param newValue
     * @throws WfException
     * @throws InvalidResource
     */
    public void setAssignee(WfResource newValue) throws WfException, InvalidResource {
        // test the validity of this WfResource object and throw exception if necessary
        this.resource = newValue;
    }
    
}
