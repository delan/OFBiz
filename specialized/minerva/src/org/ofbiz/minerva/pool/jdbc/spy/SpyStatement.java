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

import java.sql.*;

/**
 * SpyStatement
 */
public class SpyStatement implements Statement {

    private Logger log = Logger.getLogger(SpyPreparedStatement.class);
    private SpyConnection spyCon;
    private Statement s;

    public SpyStatement(SpyConnection spyCon, Statement s) {
        this.spyCon = spyCon;
        this.s = s;       
    }
    
    public ResultSet executeQuery(String string) throws SQLException {
        try {
            return new SpyResultSet(s.executeQuery(string));
        } finally {
            log.debug("execute query : " + string);
        }
    }

    public int executeUpdate(String string) throws SQLException {
        try {
            return s.executeUpdate(string);
        } finally {
            log.debug("execute update : " + string);
        }
    }

    public void close() throws SQLException {
        try {
            s.close();
        } finally {
            log.debug("statement close()");
        }
    }

    public int getMaxFieldSize() throws SQLException {
        return s.getMaxFieldSize();
    }

    public void setMaxFieldSize(int i) throws SQLException {
        s.setMaxFieldSize(i);
    }

    public int getMaxRows() throws SQLException {
        return s.getMaxRows();
    }

    public void setMaxRows(int i) throws SQLException {
        s.setMaxRows(i);
    }

    public void setEscapeProcessing(boolean b) throws SQLException {
        s.setEscapeProcessing(b);
    }

    public int getQueryTimeout() throws SQLException {
        return s.getQueryTimeout();
    }

    public void setQueryTimeout(int i) throws SQLException {
        s.setQueryTimeout(i);
    }

    public void cancel() throws SQLException {
        try {
            s.cancel();
        } finally {
            log.debug("statement cancel()");
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        return s.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        s.clearWarnings();
    }

    public void setCursorName(String string) throws SQLException {
        s.setCursorName(string);
    }

    public boolean execute(String string) throws SQLException {
        return s.execute(string);
    }

    public ResultSet getResultSet() throws SQLException {
        return new SpyResultSet(s.getResultSet());
    }

    public int getUpdateCount() throws SQLException {
        return s.getUpdateCount();
    }

    public boolean getMoreResults() throws SQLException {
        return s.getMoreResults();
    }

    public void setFetchDirection(int i) throws SQLException {
        s.setFetchDirection(i);
    }

    public int getFetchDirection() throws SQLException {
        return s.getFetchDirection();
    }

    public void setFetchSize(int i) throws SQLException {
        s.setFetchSize(i);
    }

    public int getFetchSize() throws SQLException {
        return s.getFetchSize();
    }

    public int getResultSetConcurrency() throws SQLException {
        return s.getResultSetConcurrency();
    }

    public int getResultSetType() throws SQLException {
        return s.getResultSetType();
    }

    public void addBatch(String string) throws SQLException {
        try {
            s.addBatch(string);
        } finally {
            log.debug("batch : " + string);
        }
    }

    public void clearBatch() throws SQLException {
        s.clearBatch();
    }

    public int[] executeBatch() throws SQLException {
        return s.executeBatch();
    }

    public Connection getConnection() throws SQLException {
        return spyCon;
    }

    public boolean getMoreResults(int i) throws SQLException {
        return s.getMoreResults(i);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return s.getGeneratedKeys();
    }

    public int executeUpdate(String string, int i) throws SQLException {
        try {
            return s.executeUpdate(string, i);
        } finally {
            log.debug("execute update : " + string);
        }
    }

    public int executeUpdate(String string, int[] ints) throws SQLException {
        try {
            return s.executeUpdate(string, ints);
        } finally {
            log.debug("execute update : " + string);
        }
    }

    public int executeUpdate(String string, String[] strings) throws SQLException {
        try {
            return s.executeUpdate(string, strings);
        } finally {
            log.debug("execute update : " + string);
        }
    }

    public boolean execute(String string, int i) throws SQLException {
        try {
            return s.execute(string, i);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public boolean execute(String string, int[] ints) throws SQLException {
        try {
            return s.execute(string, ints);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public boolean execute(String string, String[] strings) throws SQLException {
        try {
            return s.execute(string, strings);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public int getResultSetHoldability() throws SQLException {
        return s.getResultSetHoldability();
    }
}
