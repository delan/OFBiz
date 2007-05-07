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

import java.sql.*;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Calendar;
import java.net.URL;

/**
 * SpyResultSet
 */
public class SpyResultSet implements ResultSet {
    
    private ResultSet rs;

    public SpyResultSet(ResultSet rs) {
        this.rs = rs;
    }

    public boolean next() throws SQLException {
        return rs.next();
    }

    public void close() throws SQLException {
        rs.close();
    }

    public boolean wasNull() throws SQLException {
        return rs.wasNull();
    }

    public String getString(int i) throws SQLException {
        return rs.getString(i);
    }

    public boolean getBoolean(int i) throws SQLException {
        return rs.getBoolean(i);
    }

    public byte getByte(int i) throws SQLException {
        return rs.getByte(i);
    }

    public short getShort(int i) throws SQLException {
        return rs.getShort(i);
    }

    public int getInt(int i) throws SQLException {
        return rs.getInt(i);
    }

    public long getLong(int i) throws SQLException {
        return rs.getLong(i);
    }

    public float getFloat(int i) throws SQLException {
        return rs.getFloat(i);
    }

    public double getDouble(int i) throws SQLException {
        return rs.getDouble(i);
    }

    @Deprecated
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
        return rs.getBigDecimal(i, i1);
    }

    public byte[] getBytes(int i) throws SQLException {
        return rs.getBytes(1);
    }

    public Date getDate(int i) throws SQLException {
        return rs.getDate(i);
    }

    public Time getTime(int i) throws SQLException {
        return rs.getTime(i);
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        return rs.getTimestamp(i);
    }

    public InputStream getAsciiStream(int i) throws SQLException {
        return rs.getAsciiStream(i);
    }

    @Deprecated
    public InputStream getUnicodeStream(int i) throws SQLException {
        return rs.getUnicodeStream(i);
    }

    public InputStream getBinaryStream(int i) throws SQLException {
        return rs.getBinaryStream(i);
    }

    public String getString(String string) throws SQLException {
        return rs.getString(string);
    }

    public boolean getBoolean(String string) throws SQLException {
        return rs.getBoolean(string);
    }

    public byte getByte(String string) throws SQLException {
        return rs.getByte(string);
    }

    public short getShort(String string) throws SQLException {
        return rs.getShort(string);
    }

    public int getInt(String string) throws SQLException {
        return rs.getInt(string);
    }

    public long getLong(String string) throws SQLException {
        return rs.getLong(string);
    }

    public float getFloat(String string) throws SQLException {
        return rs.getFloat(string);
    }

    public double getDouble(String string) throws SQLException {
        return rs.getDouble(string);
    }

    @Deprecated
    public BigDecimal getBigDecimal(String string, int i) throws SQLException {
        return rs.getBigDecimal(string, i);
    }

    public byte[] getBytes(String string) throws SQLException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Date getDate(String string) throws SQLException {
        return rs.getDate(string);
    }

    public Time getTime(String string) throws SQLException {
        return rs.getTime(string);
    }

    public Timestamp getTimestamp(String string) throws SQLException {
        return rs.getTimestamp(string);
    }

    public InputStream getAsciiStream(String string) throws SQLException {
        return rs.getAsciiStream(string);
    }

    @Deprecated
    public InputStream getUnicodeStream(String string) throws SQLException {
        return rs.getUnicodeStream(string);
    }

    public InputStream getBinaryStream(String string) throws SQLException {
        return rs.getBinaryStream(string);
    }

    public SQLWarning getWarnings() throws SQLException {
        return rs.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        rs.clearWarnings();
    }

    public String getCursorName() throws SQLException {
        return rs.getCursorName();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return rs.getMetaData();
    }

    public Object getObject(int i) throws SQLException {
        return rs.getObject(i);
    }

    public Object getObject(String string) throws SQLException {
        return rs.getObject(string);
    }

    public int findColumn(String string) throws SQLException {
        return rs.findColumn(string);
    }

    public Reader getCharacterStream(int i) throws SQLException {
        return rs.getCharacterStream(i);
    }

    public Reader getCharacterStream(String string) throws SQLException {
        return rs.getCharacterStream(string);
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        return rs.getBigDecimal(i);
    }

    public BigDecimal getBigDecimal(String string) throws SQLException {
        return rs.getBigDecimal(string);
    }

    public boolean isBeforeFirst() throws SQLException {
        return rs.isBeforeFirst();
    }

    public boolean isAfterLast() throws SQLException {
        return rs.isAfterLast();
    }

    public boolean isFirst() throws SQLException {
        return rs.isFirst();
    }

    public boolean isLast() throws SQLException {
        return rs.isLast();
    }

    public void beforeFirst() throws SQLException {
        rs.beforeFirst();
    }

    public void afterLast() throws SQLException {
        rs.afterLast();
    }

    public boolean first() throws SQLException {
        return rs.first();
    }

    public boolean last() throws SQLException {
        return rs.last();
    }

    public int getRow() throws SQLException {
        return rs.getRow();
    }

    public boolean absolute(int i) throws SQLException {
        return rs.absolute(i);
    }

    public boolean relative(int i) throws SQLException {
        return rs.relative(i);
    }

    public boolean previous() throws SQLException {
        return rs.previous();
    }

    public void setFetchDirection(int i) throws SQLException {
        rs.setFetchDirection(i);
    }

    public int getFetchDirection() throws SQLException {
        return rs.getFetchDirection();
    }

    public void setFetchSize(int i) throws SQLException {
        rs.setFetchSize(i);
    }

    public int getFetchSize() throws SQLException {
        return rs.getFetchSize();
    }

    public int getType() throws SQLException {
        return rs.getType();
    }

    public int getConcurrency() throws SQLException {
        return rs.getConcurrency();
    }

    public boolean rowUpdated() throws SQLException {
        return rs.rowUpdated();
    }

    public boolean rowInserted() throws SQLException {
        return rs.rowInserted();
    }

    public boolean rowDeleted() throws SQLException {
        return rs.rowDeleted();
    }

    public void updateNull(int i) throws SQLException {
        rs.updateNull(i);
    }

    public void updateBoolean(int i, boolean b) throws SQLException {
        rs.updateBoolean(i, b);
    }

    public void updateByte(int i, byte b) throws SQLException {
        rs.updateByte(i, b);
    }

    public void updateShort(int i, short i1) throws SQLException {
        rs.updateShort(i, i1);
    }

    public void updateInt(int i, int i1) throws SQLException {
        rs.updateInt(i, i1);
    }

    public void updateLong(int i, long l) throws SQLException {
        rs.updateLong(i, l);
    }

    public void updateFloat(int i, float v) throws SQLException {
        rs.updateFloat(i, v);
    }

    public void updateDouble(int i, double v) throws SQLException {
        rs.updateDouble(i, v);
    }

    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        rs.updateBigDecimal(i, bigDecimal);
    }

    public void updateString(int i, String string) throws SQLException {
        rs.updateString(i, string);
    }

    public void updateBytes(int i, byte[] bytes) throws SQLException {
        rs.updateBytes(i, bytes);
    }

    public void updateDate(int i, Date date) throws SQLException {
        rs.updateDate(i, date);
    }

    public void updateTime(int i, Time time) throws SQLException {
        rs.updateTime(i, time);
    }

    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        rs.updateTimestamp(i, timestamp);
    }

    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        rs.updateAsciiStream(i, inputStream, i1);
    }

    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        rs.updateBinaryStream(i, inputStream, i1);
    }

    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException {
        rs.updateCharacterStream(i, reader, i1);
    }

    public void updateObject(int i, Object object, int i1) throws SQLException {
        rs.updateObject(i, object, i1);
    }

    public void updateObject(int i, Object object) throws SQLException {
        rs.updateObject(i, object);
    }

    public void updateNull(String string) throws SQLException {
        rs.updateNull(string);
    }

    public void updateBoolean(String string, boolean b) throws SQLException {
        rs.updateBoolean(string, b);
    }

    public void updateByte(String string, byte b) throws SQLException {
        rs.updateByte(string, b);
    }

    public void updateShort(String string, short i) throws SQLException {
        rs.updateShort(string, i);
    }

    public void updateInt(String string, int i) throws SQLException {
        rs.updateInt(string, i);
    }

    public void updateLong(String string, long l) throws SQLException {
        rs.updateLong(string, l);
    }

    public void updateFloat(String string, float v) throws SQLException {
        rs.updateFloat(string, v);
    }

    public void updateDouble(String string, double v) throws SQLException {
        rs.updateDouble(string, v);
    }

    public void updateBigDecimal(String string, BigDecimal bigDecimal) throws SQLException {
        rs.updateBigDecimal(string, bigDecimal);
    }

    public void updateString(String string, String string1) throws SQLException {
        rs.updateString(string, string1);
    }

    public void updateBytes(String string, byte[] bytes) throws SQLException {
        rs.updateBytes(string, bytes);
    }

    public void updateDate(String string, Date date) throws SQLException {
        rs.updateDate(string, date);
    }

    public void updateTime(String string, Time time) throws SQLException {
        rs.updateTime(string, time);
    }

    public void updateTimestamp(String string, Timestamp timestamp) throws SQLException {
        rs.updateTimestamp(string, timestamp);
    }

    public void updateAsciiStream(String string, InputStream inputStream, int i) throws SQLException {
        rs.updateAsciiStream(string, inputStream, i);
    }

    public void updateBinaryStream(String string, InputStream inputStream, int i) throws SQLException {
        rs.updateBinaryStream(string, inputStream, i);
    }

    public void updateCharacterStream(String string, Reader reader, int i) throws SQLException {
        rs.updateCharacterStream(string, reader, i);
    }

    public void updateObject(String string, Object object, int i) throws SQLException {
        rs.updateObject(string, object, i);
    }

    public void updateObject(String string, Object object) throws SQLException {
        rs.updateObject(string, object);
    }

    public void insertRow() throws SQLException {
        rs.insertRow();
    }

    public void updateRow() throws SQLException {
        rs.updateRow();
    }

    public void deleteRow() throws SQLException {
        rs.deleteRow();
    }

    public void refreshRow() throws SQLException {
        rs.refreshRow();
    }

    public void cancelRowUpdates() throws SQLException {
        rs.cancelRowUpdates();
    }

    public void moveToInsertRow() throws SQLException {
        rs.moveToInsertRow();
    }

    public void moveToCurrentRow() throws SQLException {
        rs.moveToCurrentRow();
    }

    public Statement getStatement() throws SQLException {
        return rs.getStatement();
    }

    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        return rs.getObject(i, map);
    }

    public Ref getRef(int i) throws SQLException {
        return rs.getRef(i);
    }

    public Blob getBlob(int i) throws SQLException {
        return rs.getBlob(i);
    }

    public Clob getClob(int i) throws SQLException {
        return rs.getClob(i);
    }

    public Array getArray(int i) throws SQLException {
        return rs.getArray(i);
    }

    public Object getObject(String string, Map<String, Class<?>> map) throws SQLException {
        return rs.getObject(string, map);
    }

    public Ref getRef(String string) throws SQLException {
        return rs.getRef(string);
    }

    public Blob getBlob(String string) throws SQLException {
        return rs.getBlob(string);
    }

    public Clob getClob(String string) throws SQLException {
        return rs.getClob(string);
    }

    public Array getArray(String string) throws SQLException {
        return rs.getArray(string);
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        return rs.getDate(i, calendar);
    }

    public Date getDate(String string, Calendar calendar) throws SQLException {
        return rs.getDate(string, calendar);
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        return rs.getTime(i, calendar);
    }

    public Time getTime(String string, Calendar calendar) throws SQLException {
        return rs.getTime(string, calendar);
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        return rs.getTimestamp(i, calendar);
    }

    public Timestamp getTimestamp(String string, Calendar calendar) throws SQLException {
        return rs.getTimestamp(string, calendar);
    }

    public URL getURL(int i) throws SQLException {
        return rs.getURL(i);
    }

    public URL getURL(String string) throws SQLException {
        return rs.getURL(string);
    }

    public void updateRef(int i, Ref ref) throws SQLException {
        rs.updateRef(i, ref);
    }

    public void updateRef(String string, Ref ref) throws SQLException {
        rs.updateRef(string, ref);
    }

    public void updateBlob(int i, Blob blob) throws SQLException {
        rs.updateBlob(i, blob);
    }

    public void updateBlob(String string, Blob blob) throws SQLException {
        rs.updateBlob(string, blob);
    }

    public void updateClob(int i, Clob clob) throws SQLException {
        rs.updateClob(i, clob);
    }

    public void updateClob(String string, Clob clob) throws SQLException {
        rs.updateClob(string, clob);
    }

    public void updateArray(int i, Array array) throws SQLException {
        rs.updateArray(i, array);
    }

    public void updateArray(String string, Array array) throws SQLException {
        rs.updateArray(string, array);
    }
}
