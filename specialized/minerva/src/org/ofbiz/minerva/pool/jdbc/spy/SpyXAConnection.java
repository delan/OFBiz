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

package org.ofbiz.minerva.pool.jdbc.spy;

import org.ofbiz.minerva.pool.jdbc.xa.XAConnectionFactory;
import org.ofbiz.minerva.pool.jdbc.xa.wrapper.*;
import org.ofbiz.minerva.pool.PoolEvent;
import org.ofbiz.minerva.pool.PoolEventListener;
import org.ofbiz.minerva.pool.PooledObject;
import org.apache.log4j.Logger;

import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionEvent;
import javax.transaction.xa.XAResource;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Vector;
import java.util.ArrayList;

/**
 * SpyXAConnection
 */
public class SpyXAConnection implements XAConnectionExt, PooledObject {

    private static Logger log = Logger.getLogger(SpyXAConnection.class);
    private XAConnectionFactory factory;
    private XAConnectionImpl xaCon;

    private Vector listeners, poolListeners;
    private ArrayList clientConnections;
    private TransactionListener transListener;

    public SpyXAConnection(XAConnectionImpl xaCon, XAConnectionFactory factory) {
        this.factory = factory;
        this.xaCon = xaCon;
        this.listeners = new Vector();
        this.poolListeners = new Vector();
        this.clientConnections = new ArrayList();


        XAResourceImpl res = this.xaCon.getXAResourceImpl();
        res.setXAConnection(this, true);        
    }

    public XAResource getXAResource() throws SQLException {
        return xaCon.getXAResource();
    }

    public synchronized Connection getConnection() {
        XAClientConnection client = new XAClientConnection(this, xaCon.getConnection(), true);
        client.setPSCacheSize(xaCon.getPSCacheSize());
        clientConnections.add(client);

        if (log.isTraceEnabled())
            log.trace("new SpyXAConnection created; added to clientConnections size: " + clientConnections.size());
        
        return new SpyConnection(factory, this, client);
    }

    public void close() throws SQLException {
        xaCon.close();
    }

    public void addPoolEventListener(PoolEventListener listener) {
        poolListeners.addElement(listener);
    }

    public void removePoolEventListener(PoolEventListener listener) {
        poolListeners.removeElement(listener);
    }

    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.addElement(listener);
    }

    public void removeConnectionEventListener(ConnectionEventListener listener) {
        if (!listeners.remove(listener))
            throw new IllegalArgumentException();
    }

    public String getPassword() {
        return xaCon.getPassword();
    }

    public String getUser() {
        return xaCon.getUser();
    }

    public void rollback() throws SQLException {
        xaCon.rollback();
    }

    public void transactionFinished() {
        if (transListener != null)
            transListener.transactionFinished(this);
    }

    public void transactionFailed() {
        if (transListener != null)
            transListener.transactionFailed(this);
    }

    public void setTransactionIsolation(int iso) throws SQLException {
        xaCon.setTransactionIsolation(iso);
    }

    public void setPSCacheSize(int maxSize) {
        xaCon.setPSCacheSize(maxSize);
    }

    public int getPSCacheSize() {
        return xaCon.getPSCacheSize();
    }

    public void setTransactionListener(TransactionListener tl) {
        this.transListener = tl;
    }

    public void clearTransactionListener() {
        this.transListener = null;
    }

    public void forceClientConnectionsClose() {
        xaCon.forceClientConnectionsClose();
    }

    public void setConnectionError(SQLException e) {
        Vector local = (Vector) listeners.clone();
        for (int i = local.size() - 1; i >= 0; i--) {
            try {
                ((ConnectionEventListener) local.elementAt(i)).connectionErrorOccurred(new ConnectionEvent(this, e));
            } catch (RuntimeException ex) {
                // there can be thrown an induced exception,
                // but we must report to client the original one, right?
                log.error(ex);
            }
        }
    }

    public XAResourceImpl getXAResourceImpl() {
        return xaCon.getXAResourceImpl();
    }

    public synchronized void clientConnectionClosed(XAClientConnection clientCon) {
        clientConnections.remove(clientCon);

        if (clientConnections.size() > 0)
            return;  // Only take action if the last connection referring to this is closed

        Vector local = (Vector) listeners.clone();
        for (int i = local.size() - 1; i >= 0; i--)
            ((ConnectionEventListener) local.elementAt(i)).connectionClosed(new ConnectionEvent(this));
    }

    public void firePoolEvent(PoolEvent evt) {
        Vector local = (Vector) poolListeners.clone();
        for (int i = local.size() - 1; i >= 0; i--)
            if (evt.getType() == PoolEvent.OBJECT_CLOSED)
                ((PoolEventListener) local.elementAt(i)).objectClosed(evt);
            else if (evt.getType() == PoolEvent.OBJECT_ERROR)
                ((PoolEventListener) local.elementAt(i)).objectError(evt);
            else
                ((PoolEventListener) local.elementAt(i)).objectUsed(evt);
    }
}
