/*
 * $Id: StateEventAudit.java,v 1.1 2004/04/22 15:41:03 ajzeneski Exp $
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
package org.ofbiz.shark.instance;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;

import org.enhydra.shark.api.internal.instancepersistence.*;

/**
 * Persistance Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class StateEventAudit extends EventAudit implements StateEventAuditPersistenceInterface {

    public static final String module = AssignmentEventAudit.class.getName();
    protected GenericValue stateEventAudit = null;
    private boolean newValue = false;

    protected StateEventAudit() {}

    public StateEventAudit(GenericDelegator delegator, String eventAuditId) {
        super(delegator, eventAuditId);
        if (this.delegator != null) {
            try {
                this.stateEventAudit = delegator.findByPrimaryKey("WfStateEventAudit", UtilMisc.toMap("eventAuditId", eventAuditId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    public StateEventAudit(GenericDelegator delegator) {
        super(delegator);
        this.newValue = true;
        this.stateEventAudit = delegator.makeValue("WfStateEventAudit", UtilMisc.toMap("eventAuditId", this.eventAuditId));
    }

    public StateEventAudit(GenericValue stateEventAudit) {
        super(stateEventAudit.getDelegator(), stateEventAudit.getString("eventAuditId"));
        this.stateEventAudit = stateEventAudit;
    }

    public void setOldState(String os) {
        stateEventAudit.set("oldState", os);
    }

    public String getOldState() {
        return stateEventAudit.getString("oldState");
    }

    public void setNewState(String ns) {
        stateEventAudit.set("newState", ns);
    }

    public String getNewState() {
        return stateEventAudit.getString("newState");
    }

    public void store() throws GenericEntityException {
        super.store();
        if (newValue) {
            newValue = false;
            delegator.createOrStore(stateEventAudit);
        } else {
            delegator.store(stateEventAudit);
        }
    }

    public void reload() throws GenericEntityException {
        super.reload();
        if (!newValue) {
            stateEventAudit.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        super.remove();
        if (!newValue) {
            delegator.removeValue(stateEventAudit);
        }
    }
}
