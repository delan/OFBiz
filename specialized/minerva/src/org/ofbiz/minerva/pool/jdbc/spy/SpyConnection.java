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

import org.apache.log4j.Logger;
import org.ofbiz.minerva.pool.jdbc.xa.wrapper.XAConnectionMonitor;
import org.ofbiz.minerva.pool.jdbc.xa.XAConnectionFactory;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import java.sql.*;
import java.util.Map;

/**
 * SpyConnection
 */
public class SpyConnection implements Connection {

    private Logger log = Logger.getLogger(SpyConnection.class);
    private Connection c;

    public SpyConnection(XAConnectionFactory factory, XAConnection xaCon, Connection c) {
        this.c = c;

        XAConnectionMonitor mon = new XAConnectionMonitor(factory.getTransactionManager(), xaCon);        
        try {
            mon.enlist();
        } catch (XAException e) {
            log.warn(e);
        } finally {
            log.debug("SpyConnection instance; building connection monitor");
        }
    }

    public Statement createStatement() throws SQLException {
        return new SpyStatement(this, c.createStatement());
    }

    public PreparedStatement prepareStatement(String string) throws SQLException {
        return new SpyPreparedStatement(this, c.prepareStatement(string), string);
    }

    public CallableStatement prepareCall(String string) throws SQLException {
        return new SpyCallableStatement(this, c.prepareCall(string), string);
    }

    public String nativeSQL(String string) throws SQLException {
        return c.nativeSQL(string);
    }

    public void setAutoCommit(boolean b) throws SQLException {
        c.setAutoCommit(b);
    }

    public boolean getAutoCommit() throws SQLException {
        return c.getAutoCommit();
    }

    public void commit() throws SQLException {
        log.debug("connection commit()");
        c.commit();
    }

    public void rollback() throws SQLException {
        log.debug("connection rollback()");
        c.rollback();
    }

    public void close() throws SQLException {
        log.debug("connection close()");
        c.close();
    }

    public boolean isClosed() throws SQLException {
        return c.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return c.getMetaData();
    }

    public void setReadOnly(boolean b) throws SQLException {
        c.setReadOnly(b);
    }

    public boolean isReadOnly() throws SQLException {
        return c.isReadOnly();
    }

    public void setCatalog(String string) throws SQLException {
        c.setCatalog(string);
    }

    public String getCatalog() throws SQLException {
        return c.getCatalog();
    }

    public void setTransactionIsolation(int i) throws SQLException {
        log.debug("setting connection isolation level to : " + i);
        c.setTransactionIsolation(i);
    }

    public int getTransactionIsolation() throws SQLException {
        return c.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return c.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        c.clearWarnings();
    }

    public Statement createStatement(int i, int i1) throws SQLException {
        return new SpyStatement(this, c.createStatement(i, i1));
    }

    public PreparedStatement prepareStatement(String string, int i, int i1) throws SQLException {
        return new SpyPreparedStatement(this, c.prepareStatement(string, i, i1), string);
    }

    public CallableStatement prepareCall(String string, int i, int i1) throws SQLException {
        return new SpyCallableStatement(this, c.prepareCall(string, i, i1), string);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return c.getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        c.setTypeMap(map);
    }

    public void setHoldability(int i) throws SQLException {
        c.setHoldability(i);
    }

    public int getHoldability() throws SQLException {
        return c.getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return c.setSavepoint();
    }

    public Savepoint setSavepoint(String string) throws SQLException {
        return c.setSavepoint(string);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        log.debug("conection rollback(savepoint)");
        c.rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        c.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int i, int i1, int i2) throws SQLException {
        return new SpyStatement(this, c.createStatement(i, i1, i2));
    }

    public PreparedStatement prepareStatement(String string, int i, int i1, int i2) throws SQLException {
        return new SpyPreparedStatement(this, c.prepareStatement(string, i, i1, i2), string);
    }

    public CallableStatement prepareCall(String string, int i, int i1, int i2) throws SQLException {
        return new SpyCallableStatement(this, c.prepareCall(string, i, i1, i2), string);
    }

    public PreparedStatement prepareStatement(String string, int i) throws SQLException {
        return new SpyPreparedStatement(this, c.prepareStatement(string, i), string);
    }

    public PreparedStatement prepareStatement(String string, int[] ints) throws SQLException {
        return new SpyPreparedStatement(this, c.prepareStatement(string, ints), string);
    }

    public PreparedStatement prepareStatement(String string, String[] strings) throws SQLException {
        return new SpyPreparedStatement(this, c.prepareStatement(string, strings), string);
    }
}
