/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 15, 2001
 *@version    1.0
 */

public class WfResourceImpl implements WfResource {

    protected GenericDelegator delegator;
    protected String resourceKey;
    protected String resourceName;
    protected String description;
    protected String partyId;
    protected String roleTypeId;
    protected String type;


    /** Creates a new WfResource
     * @param resourceKey Uniquely identifies the resource
     * @param resourceName The name of the resource
     * @param partyId The partyID of this resource
     * @param roleTypeId The roleTypeId of this resource
     * @param fromDate The fromDate of this resource
     */
    public WfResourceImpl(GenericDelegator delegator, String resourceKey,
                          String resourceName, String partyId, String roleTypeId) {
        this.delegator = delegator;
        this.resourceKey = resourceKey;
        this.resourceName = resourceName;
        this.description = null;
        this.partyId = partyId;
        this.roleTypeId = roleTypeId;
        this.type = "HUMAN";
    }

    /** Creates a new WfResource
     * @param valueObject The GenericValue object of the WorkflowParticipant
     */
    public WfResourceImpl(GenericValue valueObject) {
        this.delegator = valueObject.getDelegator();
        this.resourceKey = valueObject.getString("participantId");
        this.resourceName = valueObject.getString("participantName");
        this.description = valueObject.getString("description");
        this.partyId = valueObject.getString("partyId");
        this.roleTypeId = valueObject.getString("roleTypeId");
        this.type = valueObject.getString("participantTypeId");
        if (partyId == null)
            partyId = "_NA_";
        if (roleTypeId == null)
            roleTypeId = "_NA_";
    }

    /** Gets the number of work items
     * @throws WfException
     * @return Count of work items
     */
    public int howManyWorkItem() throws WfException {
        return workItems().size();
    }

    /** Gets an iterator of work items
     * @throws WfException
     * @return Iterator of work items
     */
    public Iterator getIteratorWorkItem() throws WfException {
        return workItems().iterator();
    }

    /** Gets the work items
     * @param maxNumber
     * @throws WfException
     * @return List of WfAssignment objects.
     */
    public List getSequenceWorkItem(int maxNumber) throws WfException {
        if (maxNumber > 0)
            return workItems().subList(0, (maxNumber - 1));
        return workItems();
    }

    /** Checks if an assignment object is associated with this resource
     * @param member The assignment object to check
     * @throws WfException
     * @return true if assignment is part of the work list
     */
    public boolean isMemberOfWorkItems(WfAssignment member) throws WfException {
        return workItems().contains(member);
    }

    /** Gets the resource key.
     * @throws WfException
     * @return String of the resouce key.
     */
    public String resourceKey() throws WfException {
        return resourceKey;
    }

    /** Gets the resource name
     * @throws WfException
     * @return String of the resource name
     */
    public String resourceName() throws WfException {
        return resourceName;
    }

    /** Gets the role id of this resource
     * @throws WfException
     * @return String role id of this participant or null if none
     */
    public String resourceRoleId() throws WfException {
        return roleTypeId;
    }

    /** Gets the party id of this resource
     * @throws WfException
     * @return String party id of this participant or null if none
     */
    public String resourcePartyId() throws WfException {
        return partyId;
    }

    /** Release the resouce from the assignement
     * @param fromAssigment
     * @param releaseInfo
     * @throws WfException
     * @throws NotAssigned
     */
    public void release(WfAssignment fromAssignment,
                        String releaseInfo) throws WfException, NotAssigned {
        if (!workItems().contains(fromAssignment))
            throw new NotAssigned();
        //workItems.remove(fromAssignment);
        // log the transaction
    }

    private List workItems() throws WfException {
        List workList = new ArrayList();
        Collection c = null;
        try {
            Map fields = UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId);
            c = delegator.findByAnd("WorkEffortPartyAssignment", fields);
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }

        if (c != null) {
            Iterator i = c.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                WfActivity a = null;
                try {
                    a = WfFactory.getWfActivity(delegator, v.getString("workEffortId"));
                } catch (RuntimeException e) {
                    throw new WfException(e.getMessage(), e);
                }
                if (a != null)
                    workList.add(a);
            }
        }
        return workList;
    }

}




