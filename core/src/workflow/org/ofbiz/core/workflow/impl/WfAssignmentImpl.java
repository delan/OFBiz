/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import java.util.*;
import org.ofbiz.core.entity.*;
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
    protected GenericValue valueObject;    
    
    /** Creates new WfAssignment
     *@param activity Sets the activity object for this assignment
     *@param resource The WfResource object this is assigned to
     *@throws WfException
     */
    public WfAssignmentImpl(WfActivity activity, WfResource resource) throws WfException {
        this.activity = activity;
        this.resource = resource;      
        this.valueObject = makeAssignment();     
    }
        
    // makes the assignment entity    
    private GenericValue makeAssignment() throws WfException {
        String workEffort = activity.runtimeKey();
        String party = resource.resourcePartyId();
        String role = resource.resourceRoleId();
        
        if ( workEffort == null )
            throw new WfException("WorkEffort could not be found for assignement");
        if ( party == null && role == null )
            throw new WfException("Both party and role type IDs cannot be null");
        role = role == null ? "_NA_" : role;
        GenericValue value = null;
        try {
            Map fields = new HashMap();
            fields.put("workEffortId",activity.runtimeKey());
            fields.put("partyId",party);
            fields.put("roleTypeId",role);
            fields.put("fromDate",new java.sql.Timestamp((new Date()).getTime()));
            GenericValue v = activity.getDelegator().makeValue("WorkEffortPartyAssignment",fields);
            value = activity.getDelegator().create(v);
            
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }        
        if ( value == null )
            throw new WfException("Could not create the assignement!");
        return value;
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
        try {
            valueObject.remove();
        }
        catch ( GenericEntityException e ) {
            throw new WfException("Cannot remove old resource",e);
        }        
        this.resource = newValue;
        this.valueObject = makeAssignment();        
    }
    
}
