/*
 * $Id: Deadline.java,v 1.1 2004/07/11 23:26:26 ajzeneski Exp $
 *
 */
package org.ofbiz.shark.instance;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.internal.instancepersistence.DeadlinePersistenceInterface;
import org.enhydra.shark.api.internal.instancepersistence.PersistenceException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      Jul 11, 2004
 */
public class Deadline implements DeadlinePersistenceInterface {

    public static final String module = Deadline.class.getName();

    protected GenericDelegator delegator = null;
    protected GenericValue deadline = null;
    protected boolean newValue = false;

    protected Deadline() {}

    protected Deadline(GenericDelegator delegator, String deadlineId) throws PersistenceException {
        this.delegator = delegator;
        if (this.delegator != null) {
            try {
                this.deadline = delegator.findByPrimaryKey("WfDeadline", UtilMisc.toMap("deadlineId", deadlineId));
            } catch (GenericEntityException e) {
                throw new PersistenceException(e);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    protected Deadline(GenericValue deadline) {
        this.deadline = deadline;
        this.delegator = deadline.getDelegator();
    }

    public Deadline(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;
        this.deadline = delegator.makeValue("WfDeadline", UtilMisc.toMap("deadlineId", delegator.getNextSeqId("WfDeadline")));
    }

    public static Deadline getInstance(GenericValue deadlineV) {
        Deadline deadline = new Deadline(deadlineV);
        if (deadline.isLoaded()) {
            return deadline;
        }
        return null;
    }

    public static Deadline getInstance(String deadlineId) throws PersistenceException {
        Deadline deadline = new Deadline(SharkContainer.getDelegator(), deadlineId);
        if (deadline.isLoaded()) {
            return deadline;
        }
        return null;
    }

    public boolean isLoaded() {
        if (deadline == null) {
            return false;
        }
        return true;
    }

    public void setProcessId(String procId) {
        this.deadline.set("processId", procId);
    }

    public String getProcessId() {
        return this.deadline.getString("processId");
    }

    public void setActivityId(String actId) {
        this.deadline.set("activityId", actId);
    }

    public String getActivityId() {
        return this.deadline.getString("activityId");
    }

    public void setTimeLimit(long timeLimit) {
        this.deadline.set("timeLimit", new Long(timeLimit));
    }

    public long getTimeLimit() {
        if (this.deadline.get("timeLimit") != null) {
            return this.deadline.getLong("timeLimit").longValue();
        } else {
            return -1;
        }
    }

    public void setExceptionName(String exceptionName) {
        this.deadline.set("exceptionName", exceptionName);
    }

    public String getExceptionName() {
        return this.deadline.getString("exceptionName");
    }

    public void setSynchronous(boolean sync) {
        this.deadline.set("isSync", sync ? "Y" : "N");
    }

    public boolean isSynchronous() {
        return (this.deadline.get("isSync") == null ? false : "Y".equalsIgnoreCase(this.deadline.getString("isSync")));
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            newValue = false;
            delegator.createOrStore(deadline);
        } else {
            delegator.store(deadline);
        }
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            deadline.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(deadline);
        }
    }
}
