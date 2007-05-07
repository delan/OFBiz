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
import java.util.Map;
import java.util.Calendar;
import java.net.URL;
import java.io.InputStream;
import java.io.Reader;

/**
 * SpyCallableStatement
 */
public class SpyCallableStatement implements CallableStatement {

    private Logger log = Logger.getLogger(SpyPreparedStatement.class);
    private SpyConnection spyCon;
    private CallableStatement cs;
    private String query;

    public SpyCallableStatement(SpyConnection spyCon, CallableStatement cs, String query) {
        this.spyCon = spyCon;
        this.cs = cs;
        this.query = query;
    }
    
    public void registerOutParameter(int i, int i1) throws SQLException {
        cs.registerOutParameter(i, i1);
    }

    public void registerOutParameter(int i, int i1, int i2) throws SQLException {
        cs.registerOutParameter(i, i1, i2);
    }

    public boolean wasNull() throws SQLException {
        return cs.wasNull();
    }

    public String getString(int i) throws SQLException {
        return cs.getString(i);
    }

    public boolean getBoolean(int i) throws SQLException {
        return cs.getBoolean(i);
    }

    public byte getByte(int i) throws SQLException {
        return cs.getByte(i);
    }

    public short getShort(int i) throws SQLException {
        return cs.getShort(i);
    }

    public int getInt(int i) throws SQLException {
        return cs.getInt(i);
    }

    public long getLong(int i) throws SQLException {
        return cs.getLong(i);
    }

    public float getFloat(int i) throws SQLException {
        return cs.getFloat(i);
    }

    public double getDouble(int i) throws SQLException {
        return cs.getDouble(i);
    }

    @Deprecated
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
        return cs.getBigDecimal(i, i1);
    }

    public byte[] getBytes(int i) throws SQLException {
        return cs.getBytes(i);
    }

    public Date getDate(int i) throws SQLException {
        return cs.getDate(i);
    }

    public Time getTime(int i) throws SQLException {
        return cs.getTime(i);
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        return cs.getTimestamp(i);
    }

    public Object getObject(int i) throws SQLException {
        return cs.getObject(i);
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        return cs.getBigDecimal(i);
    }

    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        return cs.getObject(i, map);
    }

    public Ref getRef(int i) throws SQLException {
        return cs.getRef(i);
    }

    public Blob getBlob(int i) throws SQLException {
        return cs.getBlob(i);
    }

    public Clob getClob(int i) throws SQLException {
        return cs.getClob(i);
    }

    public Array getArray(int i) throws SQLException {
        return cs.getArray(i);
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        return cs.getDate(i, calendar);
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        return cs.getTime(i, calendar);
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        return cs.getTimestamp(i, calendar);
    }

    public void registerOutParameter(int i, int i1, String string) throws SQLException {
        cs.registerOutParameter(i, i1, string);
    }

    public void registerOutParameter(String string, int i) throws SQLException {
        cs.registerOutParameter(string, i);
    }

    public void registerOutParameter(String string, int i, int i1) throws SQLException {
        cs.registerOutParameter(string, i, i1);
    }

    public void registerOutParameter(String string, int i, String string1) throws SQLException {
        cs.registerOutParameter(string, i, string1);
    }

    public URL getURL(int i) throws SQLException {
        return cs.getURL(i);
    }

    public void setURL(String string, URL url) throws SQLException {
        cs.setURL(string, url);
    }

    public void setNull(String string, int i) throws SQLException {
        cs.setNull(string, i);
    }

    public void setBoolean(String string, boolean b) throws SQLException {
        cs.setBoolean(string, b);
    }

    public void setByte(String string, byte b) throws SQLException {
        cs.setByte(string, b);
    }

    public void setShort(String string, short i) throws SQLException {
        cs.setShort(string, i);
    }

    public void setInt(String string, int i) throws SQLException {
        cs.setInt(string, i);
    }

    public void setLong(String string, long l) throws SQLException {
        cs.setLong(string, l);
    }

    public void setFloat(String string, float v) throws SQLException {
        cs.setFloat(string, v);
    }

    public void setDouble(String string, double v) throws SQLException {
        cs.setDouble(string, v);
    }

    public void setBigDecimal(String string, BigDecimal bigDecimal) throws SQLException {
        cs.setBigDecimal(string, bigDecimal);
    }

    public void setString(String string, String string1) throws SQLException {
        cs.setString(string, string1);
    }

    public void setBytes(String string, byte[] bytes) throws SQLException {
        cs.setBytes(string, bytes);
    }

    public void setDate(String string, Date date) throws SQLException {
        cs.setDate(string, date);
    }

    public void setTime(String string, Time time) throws SQLException {
        cs.setTime(string, time);
    }

    public void setTimestamp(String string, Timestamp timestamp) throws SQLException {
        cs.setTimestamp(string, timestamp);
    }

    public void setAsciiStream(String string, InputStream inputStream, int i) throws SQLException {
        cs.setAsciiStream(string, inputStream, i);
    }

    public void setBinaryStream(String string, InputStream inputStream, int i) throws SQLException {
        cs.setBinaryStream(string, inputStream, i);
    }

    public void setObject(String string, Object object, int i, int i1) throws SQLException {
        cs.setObject(string, object, i, i1);
    }

    public void setObject(String string, Object object, int i) throws SQLException {
        cs.setObject(string, object, i);
    }

    public void setObject(String string, Object object) throws SQLException {
        cs.setObject(string, object);
    }

    public void setCharacterStream(String string, Reader reader, int i) throws SQLException {
        cs.setCharacterStream(string, reader, i);
    }

    public void setDate(String string, Date date, Calendar calendar) throws SQLException {
        cs.setDate(string, date, calendar);
    }

    public void setTime(String string, Time time, Calendar calendar) throws SQLException {
        cs.setTime(string, time, calendar);
    }

    public void setTimestamp(String string, Timestamp timestamp, Calendar calendar) throws SQLException {
        cs.setTimestamp(string, timestamp, calendar);
    }

    public void setNull(String string, int i, String string1) throws SQLException {
        cs.setNull(string, i, string1);
    }

    public String getString(String string) throws SQLException {
        return cs.getString(string);
    }

    public boolean getBoolean(String string) throws SQLException {
        return cs.getBoolean(string);
    }

    public byte getByte(String string) throws SQLException {
        return cs.getByte(string);
    }

    public short getShort(String string) throws SQLException {
        return cs.getShort(string);
    }

    public int getInt(String string) throws SQLException {
        return cs.getInt(string);
    }

    public long getLong(String string) throws SQLException {
        return cs.getLong(string);
    }

    public float getFloat(String string) throws SQLException {
        return cs.getFloat(string);
    }

    public double getDouble(String string) throws SQLException {
        return cs.getDouble(string);
    }

    public byte[] getBytes(String string) throws SQLException {
        return cs.getBytes(string);
    }

    public Date getDate(String string) throws SQLException {
        return cs.getDate(string);
    }

    public Time getTime(String string) throws SQLException {
        return cs.getTime(string);
    }

    public Timestamp getTimestamp(String string) throws SQLException {
        return cs.getTimestamp(string);
    }

    public Object getObject(String string) throws SQLException {
        return cs.getObject(string);
    }

    public BigDecimal getBigDecimal(String string) throws SQLException {
        return cs.getBigDecimal(string);
    }

    public Object getObject(String string, Map<String, Class<?>> map) throws SQLException {
        return cs.getObject(string, map);
    }

    public Ref getRef(String string) throws SQLException {
        return cs.getRef(string);
    }

    public Blob getBlob(String string) throws SQLException {
        return cs.getBlob(string);
    }

    public Clob getClob(String string) throws SQLException {
        return cs.getClob(string);
    }

    public Array getArray(String string) throws SQLException {
        return cs.getArray(string);
    }

    public Date getDate(String string, Calendar calendar) throws SQLException {
        return cs.getDate(string, calendar);
    }

    public Time getTime(String string, Calendar calendar) throws SQLException {
        return cs.getTime(string, calendar);
    }

    public Timestamp getTimestamp(String string, Calendar calendar) throws SQLException {
        return cs.getTimestamp(string, calendar);
    }

    public URL getURL(String string) throws SQLException {
        return cs.getURL(string);
    }

    public ResultSet executeQuery() throws SQLException {
        try {
            return new SpyResultSet(cs.executeQuery());
        } finally {
            log.debug("execute query : " + query);
        }
    }

    public int executeUpdate() throws SQLException {
        try {
            return cs.executeUpdate();
        } finally {
            log.debug("execute update : " + query);
        }
    }

    public void setNull(int i, int i1) throws SQLException {
        cs.setNull(i, i1);
    }

    public void setBoolean(int i, boolean b) throws SQLException {
        cs.setBoolean(i, b);
    }

    public void setByte(int i, byte b) throws SQLException {
        cs.setByte(i, b);
    }

    public void setShort(int i, short i1) throws SQLException {
        cs.setShort(i, i1);
    }

    public void setInt(int i, int i1) throws SQLException {
        cs.setInt(i, i1);
    }

    public void setLong(int i, long l) throws SQLException {
        cs.setLong(i, l);
    }

    public void setFloat(int i, float v) throws SQLException {
        cs.setFloat(i, v);
    }

    public void setDouble(int i, double v) throws SQLException {
        cs.setDouble(i, v);
    }

    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        cs.setBigDecimal(i, bigDecimal);
    }

    public void setString(int i, String string) throws SQLException {
        cs.setString(i, string);
    }

    public void setBytes(int i, byte[] bytes) throws SQLException {
        cs.setBytes(i, bytes);
    }

    public void setDate(int i, Date date) throws SQLException {
        cs.setDate(i, date);
    }

    public void setTime(int i, Time time) throws SQLException {
        cs.setTime(i, time);
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        cs.setTimestamp(i, timestamp);
    }

    public void setAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        cs.setAsciiStream(i, inputStream, i1);
    }

    @Deprecated
    public void setUnicodeStream(int i, InputStream inputStream, int i1) throws SQLException {
        cs.setUnicodeStream(i, inputStream, i1);
    }

    public void setBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        cs.setBinaryStream(i, inputStream, i1);
    }

    public void clearParameters() throws SQLException {
        cs.clearParameters();
    }

    public void setObject(int i, Object object, int i1, int i2) throws SQLException {
        cs.setObject(i, object, i1, i2);
    }

    public void setObject(int i, Object object, int i1) throws SQLException {
        cs.setObject(i, object, i1);
    }

    public void setObject(int i, Object object) throws SQLException {
        cs.setObject(i, object);
    }

    public boolean execute() throws SQLException {
        try {
            return cs.execute();
        } finally {
            log.debug("execute : " + query);
        }
    }

    public void addBatch() throws SQLException {
        cs.addBatch();
    }

    public void setCharacterStream(int i, Reader reader, int i1) throws SQLException {
        cs.setCharacterStream(i, reader, i1);
    }

    public void setRef(int i, Ref ref) throws SQLException {
        cs.setRef(i, ref);
    }

    public void setBlob(int i, Blob blob) throws SQLException {
        cs.setBlob(i, blob);
    }

    public void setClob(int i, Clob clob) throws SQLException {
        cs.setClob(i, clob);
    }

    public void setArray(int i, Array array) throws SQLException {
        cs.setArray(i, array);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return cs.getMetaData();
    }

    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        cs.setDate(i, date, calendar);
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        cs.setTime(i, time, calendar);
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        cs.setTimestamp(i, timestamp, calendar);
    }

    public void setNull(int i, int i1, String string) throws SQLException {
        cs.setNull(i, i1, string);
    }

    public void setURL(int i, URL url) throws SQLException {
        cs.setURL(i, url);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return cs.getParameterMetaData();
    }

    public ResultSet executeQuery(String string) throws SQLException {
        try {
            return new SpyResultSet(cs.executeQuery(string));
        } finally {
            log.debug("execute query : " + string);
        }
    }

    public int executeUpdate(String string) throws SQLException {
        try {
            return cs.executeUpdate(string);
        } finally {
            log.debug("execute update : " + string);
        }
    }

    public void close() throws SQLException {
        cs.close();
    }

    public int getMaxFieldSize() throws SQLException {
        return cs.getMaxFieldSize();
    }

    public void setMaxFieldSize(int i) throws SQLException {
        cs.setMaxFieldSize(i);
    }

    public int getMaxRows() throws SQLException {
        return cs.getMaxRows();
    }

    public void setMaxRows(int i) throws SQLException {
        cs.setMaxRows(i);
    }

    public void setEscapeProcessing(boolean b) throws SQLException {
        cs.setEscapeProcessing(b);
    }

    public int getQueryTimeout() throws SQLException {
        return cs.getQueryTimeout();
    }

    public void setQueryTimeout(int i) throws SQLException {
        cs.setQueryTimeout(i);
    }

    public void cancel() throws SQLException {
        cs.cancel();
    }

    public SQLWarning getWarnings() throws SQLException {
        return cs.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        cs.clearWarnings();
    }

    public void setCursorName(String string) throws SQLException {
        cs.setCursorName(string);
    }

    public boolean execute(String string) throws SQLException {
        try {
            return cs.execute(string);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public ResultSet getResultSet() throws SQLException {
        return new SpyResultSet(cs.getResultSet());
    }

    public int getUpdateCount() throws SQLException {
        return cs.getUpdateCount();
    }

    public boolean getMoreResults() throws SQLException {
        return cs.getMoreResults();
    }

    public void setFetchDirection(int i) throws SQLException {
        cs.setFetchDirection(i);
    }

    public int getFetchDirection() throws SQLException {
        return cs.getFetchDirection();
    }

    public void setFetchSize(int i) throws SQLException {
        cs.setFetchSize(i);
    }

    public int getFetchSize() throws SQLException {
        return cs.getFetchSize();
    }

    public int getResultSetConcurrency() throws SQLException {
        return cs.getResultSetConcurrency();
    }

    public int getResultSetType() throws SQLException {
        return cs.getResultSetType();
    }

    public void addBatch(String string) throws SQLException {
        try {
            cs.addBatch(string);
        } finally {
            log.debug("batch : " + string);
        }
    }

    public void clearBatch() throws SQLException {
        cs.clearBatch();
    }

    public int[] executeBatch() throws SQLException {
        return cs.executeBatch();
    }

    public Connection getConnection() throws SQLException {
        return spyCon;
    }

    public boolean getMoreResults(int i) throws SQLException {
        return cs.getMoreResults(i);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return cs.getGeneratedKeys();
    }

    public int executeUpdate(String string, int i) throws SQLException {
        try {
            return cs.executeUpdate(string, i);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public int executeUpdate(String string, int[] ints) throws SQLException {
        try {
            return cs.executeUpdate(string, ints);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public int executeUpdate(String string, String[] strings) throws SQLException {
        try {
            return cs.executeUpdate(string, strings);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public boolean execute(String string, int i) throws SQLException {
        try {
            return cs.execute(string, i);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public boolean execute(String string, int[] ints) throws SQLException {
        try {
            return cs.execute(string, ints);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public boolean execute(String string, String[] strings) throws SQLException {
        try {
            return cs.execute(string, strings);
        } finally {
            log.debug("execute : " + string);
        }
    }

    public int getResultSetHoldability() throws SQLException {
        return cs.getResultSetHoldability();
    }
}
