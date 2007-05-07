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
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.net.URL;

/**
 * SpyPreparedStatement
 */
public class SpyPreparedStatement implements PreparedStatement {

    private Logger log = Logger.getLogger(SpyPreparedStatement.class);
    private SpyConnection spyCon;
    private PreparedStatement ps;
    private String query;

    public SpyPreparedStatement(SpyConnection spyCon, PreparedStatement ps, String query) {
        this.spyCon = spyCon;
        this.ps = ps;
        this.query = query;
    }
       
    public ResultSet executeQuery() throws SQLException {
        try {
            return ps.executeQuery();
        } finally {
            log.debug("execute query : " + query);
        }
    }

    public int executeUpdate() throws SQLException {
        try {
            return ps.executeUpdate();
        } finally {
            log.debug("execute update : " + query);
        }
    }

    public void setNull(int i, int i1) throws SQLException {
        ps.setNull(i, i1);
    }

    public void setBoolean(int i, boolean b) throws SQLException {
        ps.setBoolean(i, b);
    }

    public void setByte(int i, byte b) throws SQLException {
        ps.setByte(i, b);
    }

    public void setShort(int i, short i1) throws SQLException {
        ps.setShort(i, i1);
    }

    public void setInt(int i, int i1) throws SQLException {
        ps.setInt(i, i1);
    }

    public void setLong(int i, long l) throws SQLException {
        ps.setLong(i, l);
    }

    public void setFloat(int i, float v) throws SQLException {
        ps.setFloat(i, v);
    }

    public void setDouble(int i, double v) throws SQLException {
        ps.setDouble(i, v);
    }

    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        ps.setBigDecimal(i, bigDecimal);
    }

    public void setString(int i, String string) throws SQLException {
        ps.setString(i, string);
    }

    public void setBytes(int i, byte[] bytes) throws SQLException {
        ps.setBytes(i, bytes);
    }

    public void setDate(int i, Date date) throws SQLException {
        ps.setDate(i, date);
    }

    public void setTime(int i, Time time) throws SQLException {
        ps.setTime(i, time);
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        ps.setTimestamp(i, timestamp);
    }

    public void setAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        ps.setAsciiStream(i, inputStream, i1);
    }

    @Deprecated
    public void setUnicodeStream(int i, InputStream inputStream, int i1) throws SQLException {
        ps.setUnicodeStream(i, inputStream, i1);
    }

    public void setBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        ps.setBinaryStream(i, inputStream, i1);
    }

    public void clearParameters() throws SQLException {
        ps.clearParameters();
    }

    public void setObject(int i, Object object, int i1, int i2) throws SQLException {
        ps.setObject(i, object, i1, i2);
    }

    public void setObject(int i, Object object, int i1) throws SQLException {
        ps.setObject(i, object, i1);
    }

    public void setObject(int i, Object object) throws SQLException {
        ps.setObject(i, object);
    }

    public boolean execute() throws SQLException {
        return ps.execute();
    }

    public void addBatch() throws SQLException {
        ps.addBatch();
    }

    public void setCharacterStream(int i, Reader reader, int i1) throws SQLException {
        ps.setCharacterStream(i, reader, i1);
    }

    public void setRef(int i, Ref ref) throws SQLException {
        ps.setRef(i, ref);
    }

    public void setBlob(int i, Blob blob) throws SQLException {
        ps.setBlob(i, blob);
    }

    public void setClob(int i, Clob clob) throws SQLException {
        ps.setClob(i, clob);
    }

    public void setArray(int i, Array array) throws SQLException {
        ps.setArray(i, array);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return ps.getMetaData();
    }

    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        ps.setDate(i, date, calendar);
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        ps.setTime(i, time, calendar);
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        ps.setTimestamp(i, timestamp, calendar);
    }

    public void setNull(int i, int i1, String string) throws SQLException {
        ps.setNull(i, i1, string);
    }

    public void setURL(int i, URL url) throws SQLException {
        ps.setURL(i, url);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return ps.getParameterMetaData();
    }

    public ResultSet executeQuery(String string) throws SQLException {
        try {
            return new SpyResultSet(ps.executeQuery(string));
        } finally {
            log.debug("execute query : " + query + "/ " + string);
        }
    }

    public int executeUpdate(String string) throws SQLException {
        try {
            return ps.executeUpdate(string);
        } finally {
            log.debug("execute update : " + query);
        }
    }

    public void close() throws SQLException {
        ps.close();
    }

    public int getMaxFieldSize() throws SQLException {
        return ps.getMaxFieldSize();
    }

    public void setMaxFieldSize(int i) throws SQLException {
        ps.setMaxFieldSize(i);
    }

    public int getMaxRows() throws SQLException {
        return ps.getMaxRows();
    }

    public void setMaxRows(int i) throws SQLException {
        ps.setMaxRows(i);
    }

    public void setEscapeProcessing(boolean b) throws SQLException {
        ps.setEscapeProcessing(b);
    }

    public int getQueryTimeout() throws SQLException {
        return ps.getQueryTimeout();
    }

    public void setQueryTimeout(int i) throws SQLException {
        ps.setQueryTimeout(i);
    }

    public void cancel() throws SQLException {
        try {
            ps.cancel();
        } finally {
            log.debug("prepared statment : cancel()");
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        return ps.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        ps.clearWarnings();
    }

    public void setCursorName(String string) throws SQLException {
        ps.setCursorName(string);
    }

    public boolean execute(String string) throws SQLException {
        try {
            return ps.execute(string);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public ResultSet getResultSet() throws SQLException {
        return new SpyResultSet(ps.getResultSet());
    }

    public int getUpdateCount() throws SQLException {
        return ps.getUpdateCount();
    }

    public boolean getMoreResults() throws SQLException {
        return ps.getMoreResults();
    }

    public void setFetchDirection(int i) throws SQLException {
        ps.setFetchDirection(i);
    }

    public int getFetchDirection() throws SQLException {
        return ps.getFetchDirection();
    }

    public void setFetchSize(int i) throws SQLException {
        ps.setFetchSize(i);
    }

    public int getFetchSize() throws SQLException {
        return ps.getFetchSize();
    }

    public int getResultSetConcurrency() throws SQLException {
        return ps.getResultSetConcurrency();
    }

    public int getResultSetType() throws SQLException {
        return ps.getResultSetType();
    }

    public void addBatch(String string) throws SQLException {
        ps.addBatch(string);
    }

    public void clearBatch() throws SQLException {
        ps.clearBatch();
    }

    public int[] executeBatch() throws SQLException {
        return ps.executeBatch();
    }

    public Connection getConnection() throws SQLException {
        return spyCon;
    }

    public boolean getMoreResults(int i) throws SQLException {
        return ps.getMoreResults(i);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return new SpyResultSet(ps.getGeneratedKeys());
    }

    public int executeUpdate(String string, int i) throws SQLException {
        try {
            return ps.executeUpdate(string, i);
        } finally {
            log.debug("execute update : " + query + " / " + string);
        }
    }

    public int executeUpdate(String string, int[] ints) throws SQLException {
        return ps.executeUpdate(string, ints);
    }

    public int executeUpdate(String string, String[] strings) throws SQLException {
        return ps.executeUpdate(string, strings);
    }

    public boolean execute(String string, int i) throws SQLException {
        return ps.execute(string, i);
    }

    public boolean execute(String string, int[] ints) throws SQLException {
        return ps.execute(string, ints);
    }

    public boolean execute(String string, String[] strings) throws SQLException {
        return ps.execute(string, strings);
    }

    public int getResultSetHoldability() throws SQLException {
        return ps.getResultSetHoldability();
    }
}
