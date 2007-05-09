/*
 * Licensed under the X license (see http://www.x.org/terms.htm)
 */
package org.ofbiz.minerva.pool;

import org.ofbiz.minerva.pool.jdbc.xa.wrapper.XAConnectionExt;

import javax.transaction.xa.Xid;
import java.util.Date;
import java.util.ConcurrentModificationException;

/**
 * Stores the properties of an object in a pool.
 *
 * @author Aaron Mulder (ammulder@alumni.princeton.edu)
 */
public class ObjectRecord {

    private long created;
    private long lastUsed;
    private Thread thread;
    private Object object;
    private Object clientObject;
    private boolean inUse;

    /**
     * Created a new record for the specified pooled object.  Objects default to
     * being in use when created, so that they can't be stolen away from the
     * creator by another thread.
     */
    public ObjectRecord(Object ob) {
        this(ob, true);
    }

    /**
     * Created a new record for the specified pooled object.  Sets the initial
     * state to in use or not.
     */
    public ObjectRecord(Object ob, boolean inUse) {
        created = lastUsed = System.currentTimeMillis();
        object = ob;
        this.inUse = inUse;
        if (inUse)
            thread = Thread.currentThread();
    }

    /**
     * Gets the transaction used with this connection
     */
    public int getTransactionTimeout() {
        int timeout = -1;
        if (object instanceof XAConnectionExt) {
            try {
                timeout = ((XAConnectionExt) object).getXAResource().getTransactionTimeout();
            } catch (Throwable e) {
                // do nothing
            }
        }
        return timeout;
    }

    /**
     * Gets the current transaction XID     
     */
    public Xid getCurrentXid() {
        Xid xid = null;
        if (object instanceof XAConnectionExt) {
            try {
                xid = ((XAConnectionExt) object).getXAResourceImpl().getCurrent();
            } catch (Throwable e) {
                // do nothing
            }
        }
        return xid;
    }

    /**
     * Gets the date when this connection was originally opened.
     */
    public Date getCreationDate() {
        return new Date(created);
    }

    /**
     * Gets the date when this connection was last used.
     */
    public Date getLastUsedDate() {
        return new Date(lastUsed);
    }

    /**
     * Gets the time (in milliseconds) since this connection was last used.
     */
    public long getMillisSinceLastUse() {
        return System.currentTimeMillis() - lastUsed;
    }

    /**
     * Tells whether this connection is currently in use.  This is not
     * synchronized since you probably want to synchronize at a higher level
     * (if not in use, do something), etc.
     */
    public boolean isInUse() {
        return inUse;
    }

    /**
     * Sets whether this connection is currently in use.
     * @throws java.util.ConcurrentModificationException
     *          Occurs when the connection is already in use and it is set to be
     *          in use, or it is not in use and it is set to be not in use.
     */
    public synchronized void setInUse(boolean inUse) throws ConcurrentModificationException {
        if (this.inUse == inUse)
            throw new ConcurrentModificationException();
        this.inUse = inUse;
        lastUsed = System.currentTimeMillis();
        if (!inUse) {
            clientObject = null;
            thread = null;
        } else {
            thread = Thread.currentThread();
        }
    }

    /**
     * Sets the last used time to the current time.
     */
    public void setLastUsed() {
        lastUsed = System.currentTimeMillis();
    }

    /**
     * Gets the pooled object associated with this record.
     */
    public Object getObject() {
        return object;
    }

    /**
     * Sets the client object associated with this object.  Not always used.
     */
    public void setClientObject(Object o) {
        clientObject = o;
    }

    /**
     * Gets the client object associated with this object.  If there is none,
     * returns the normal object (which is the default).
     */
    public Object getClientObject() {
        return clientObject == null ? object : clientObject;
    }

    /**
     * Shuts down this object - it will be useless thereafter.
     */
    public void close() {
        object = null;
        clientObject = null;
        created = lastUsed = Long.MAX_VALUE;
        inUse = true;
    }

    /**
     * Gets the thread currently associated with this object     
     */
    public Thread getThread() {
        return this.thread;
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(object.toString());
        buf.append(" : created : [").append(created);
        buf.append("] : last used : [").append(lastUsed);
        buf.append("] : thread : [").append(thread.getName());
        buf.append("] : in use : [").append(inUse);
        buf.append("]");

        return buf.toString();
    }
}
