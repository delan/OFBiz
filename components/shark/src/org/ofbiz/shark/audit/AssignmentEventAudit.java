/*
 * $Id$
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.shark.audit;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import org.enhydra.shark.api.internal.eventaudit.AssignmentEventAuditPersistenceInterface;

/**
 * Persistance Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class AssignmentEventAudit extends EventAudit implements AssignmentEventAuditPersistenceInterface {

    public static final String module = AssignmentEventAudit.class.getName();
    protected GenericValue assignmentEventAudit = null;
    private boolean newValue = false;

    protected AssignmentEventAudit() {}

    public AssignmentEventAudit(GenericDelegator delegator, String eventAuditId) {
        super(delegator, eventAuditId);
        if (this.delegator != null) {
            try {
                this.assignmentEventAudit = delegator.findByPrimaryKey("WfAssignmentEventAudit", UtilMisc.toMap("eventAuditId", eventAuditId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    public AssignmentEventAudit(GenericDelegator delegator) {
        super(delegator);
        this.newValue = true;
        this.assignmentEventAudit = delegator.makeValue("WfAssignmentEventAudit", UtilMisc.toMap("eventAuditId", this.eventAuditId));
    }

    public AssignmentEventAudit(GenericValue assignmentEventAudit) {
        super(assignmentEventAudit.getDelegator(), assignmentEventAudit.getString("eventAuditId"));
        this.assignmentEventAudit = assignmentEventAudit;
    }

    public void setOldResourceUsername(String un) {
        assignmentEventAudit.set("oldUserName", un);
    }

    public String getOldResourceUsername() {
        return assignmentEventAudit.getString("oldUserName");
    }

    public void setOldResourceName(String nm) {
        assignmentEventAudit.set("oldName", nm);
    }

    public String getOldResourceName() {
        return assignmentEventAudit.getString("oldName");
    }

    public void setNewResourceUsername(String un) {
        assignmentEventAudit.set("newUserName", un);
    }

    public String getNewResourceUsername() {
        return assignmentEventAudit.getString("newUserName");
    }

    public void setNewResourceName(String nm) {
        assignmentEventAudit.set("newName", nm);
    }

    public String getNewResourceName() {
        return assignmentEventAudit.getString("newName");
    }

    public void setIsAccepted(boolean acc) {
        assignmentEventAudit.set("isAccepted", new Boolean(acc));
    }

    public boolean getIsAccepted() {
        return assignmentEventAudit.getBoolean("isAccepted").booleanValue();
    }

    public void store() throws GenericEntityException {
        super.store();
        if (newValue) {
            newValue = false;
            delegator.createOrStore(assignmentEventAudit);
        } else {
            delegator.store(assignmentEventAudit);
        }
    }

    public void reload() throws GenericEntityException {
        super.reload();
        if (!newValue) {
            assignmentEventAudit.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        super.remove();
        if (!newValue) {
            delegator.removeValue(assignmentEventAudit);
        }
    }
}
