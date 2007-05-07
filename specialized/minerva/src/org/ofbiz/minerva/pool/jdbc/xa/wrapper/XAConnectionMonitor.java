/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package org.ofbiz.minerva.pool.jdbc.xa.wrapper;

import org.apache.log4j.Logger;

import javax.sql.XAConnection;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionEvent;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAException;
import javax.transaction.*;
import java.util.Date;

/**
 * XAConnectionMonitor
 */
public class XAConnectionMonitor implements XAResource, ConnectionEventListener {

    private TransactionManager mgr;
    private XAConnection xaCon;
    private long monStart;
    private Xid xid;
    private int timeout;
    private boolean active = false;

    private Logger log = Logger.getLogger(XAConnectionMonitor.class);

    public XAConnectionMonitor(TransactionManager mgr, XAConnection xaCon) {
        this.xaCon = xaCon;
        this.mgr = mgr;
        this.xaCon.addConnectionEventListener(this);
    }

    public TransactionManager getTransactionManager() {
        return this.mgr;
    }

    public XAConnection getXAConnection() {
        return this.xaCon;
    }

    /**
     * Enlists this resource in the current transaction
     * @throws XAException
     */
    public void enlist() throws XAException {
        try {
            if (mgr != null && mgr.getStatus() == Status.STATUS_ACTIVE) {
                Transaction tx = mgr.getTransaction();
                if (tx != null) {
                    tx.enlistResource(this);
                } else {
                    throw new XAException(XAException.XAER_NOTA);
                }
            } else {
                throw new XAException("No transaction manager or invalid status");
            }
        } catch (SystemException e) {
            throw new XAException("Unable to get transaction status");
        } catch (RollbackException e) {
            throw new XAException("Unable to enlist resource with transaction");
        }
    }


    /**
     * @see javax.transaction.xa.XAResource#start(javax.transaction.xa.Xid xid, int flag)
     */
    public void start(Xid xid, int flag) throws XAException {
        if (this.active) {
            if (this.xid != null && this.xid.equals(xid)) {
                throw new XAException(XAException.XAER_DUPID);
            } else {
                throw new XAException(XAException.XAER_PROTO);
            }
        }
        if (this.xid != null && !this.xid.equals(xid)) {
            throw new XAException(XAException.XAER_NOTA);
        }

        this.xid = xid;
        this.active = true;
        this.monStart = new Date().getTime();
        log.debug("transaction started : " + monStart + " - " + xid.toString());
    }

    /**
     * @see javax.transaction.xa.XAResource#end(javax.transaction.xa.Xid xid, int flag)
     */
    public void end(Xid xid, int flag) throws XAException {
        if (!this.active) {
            throw new XAException(XAException.XAER_PROTO);
        }

        if (this.xid == null || !this.xid.equals(xid)) {
            throw new XAException(XAException.XAER_NOTA);
        }
        this.active = false;

        long monEnd = new Date().getTime();
        log.debug("transaction ended : " + monStart + " # " + monEnd + " - " + xid.toString());
    }

    /**
     * @see javax.transaction.xa.XAResource#forget(javax.transaction.xa.Xid xid)
     */
    public void forget(Xid xid) throws XAException {
        if (this.xid == null || !this.xid.equals(xid)) {
            throw new XAException(XAException.XAER_NOTA);
        }
        this.xid = null;
        if (active) {
            // non-fatal
            log.warn("forget() called without end()");
        }
    }

    /**
     * @see javax.transaction.xa.XAResource#prepare(javax.transaction.xa.Xid xid)
     */
    public int prepare(Xid xid) throws XAException {
        if (this.xid == null || !this.xid.equals(xid)) {
            throw new XAException(XAException.XAER_NOTA);
        }
        return XA_OK;
    }

    /**
     * @see javax.transaction.xa.XAResource#recover(int flag)
     */
    public Xid[] recover(int flag) throws XAException {
        if (this.xid == null) {
            return new Xid[0];
        } else {
            return new Xid[] { this.xid };
        }
    }

    /**
     * @see javax.transaction.xa.XAResource#isSameRM(javax.transaction.xa.XAResource xaResource)
     */
    public boolean isSameRM(XAResource xaResource) throws XAException {
        return xaResource == this;
    }

    /**
     * @see javax.transaction.xa.XAResource#getTransactionTimeout()
     */
    public int getTransactionTimeout() throws XAException {
        return this.timeout;
    }

    /**
     * @see javax.transaction.xa.XAResource#setTransactionTimeout(int seconds)
     * Note: the valus is saved but in the current implementation this is not used.
     */
    public boolean setTransactionTimeout(int seconds) throws XAException {
        this.timeout = seconds;
        return true;
    }

    public Xid getXid() {
        return this.xid;
    }

    /**
     * @see javax.transaction.xa.XAResource#commit(javax.transaction.xa.Xid xid, boolean onePhase)
     */
    public void commit(Xid xid, boolean onePhase) throws XAException {
        log.debug("Xid : " + xid.toString() + " cleared [commit]");
    }

    /**
     * @see javax.transaction.xa.XAResource#rollback(javax.transaction.xa.Xid xid)
     */
    public void rollback(Xid xid) throws XAException {
        log.debug("Xid : " + xid.toString() + " cleared [rollback]");
    }

    // connection event listener methods
    public void connectionClosed(ConnectionEvent event) {
        //Object source = event.getSource();
        //log.debug("connection closed : " + source);
    }

    public void connectionErrorOccurred(ConnectionEvent event) {
        Object source = event.getSource();
        Exception e = event.getSQLException();
        log.warn("connection error : " + source + " : ", e);
    }
}
