/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 */

package org.ofbiz.core.entity.jdbc;

import java.util.*;
import java.sql.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * <p>Title: SQLProcessor</p>
 * <p>Description: provides utitlity functions to ease database access</p>
 * @author  <a href="mailto:jdonnerstag@eds.de">Juergen Donnerstag</a>
 * @version 1.0
 */
public class SQLProcessor {
    /** Module Name Used for debugging */
    public static final String module = SQLProcessor.class.getName();
    
    /** The datasource helper (see entityengine.xml <datasource name="..">) */
    private String helperName;
    
    /// The database resources to be used
    private Connection _connection = null;
    
    /// The database resources to be used
    private PreparedStatement _ps = null;
    
    /// The database resources to be used
    private Statement _stmt = null;
    
    /// The database resources to be used
    private ResultSet _rs = null;
    
    /// The SQL String used. Use for debugging only
    private String _sql;
    
    /// Index to be used with preparedStatement.setValue(_ind, ...)
    private int _ind;
    
    /// true in case of manual transactions
    private boolean _manualTX;
    
    /// true in case the connection shall be closed.
    private boolean _bDeleteConnection = false;
    
    /**
     * Construct an object based on the helper/datasource
     *
     * @param helperName  The datasource helper (see entityengine.xml &lt;datasource name=".."&gt;)
     */
    public SQLProcessor(String helperName) {
        this.helperName = helperName;
        this._manualTX = true;
    }
    
    /**
     * Construct an object with an connection given. The connection will not
     * be closed by this SQLProcessor, but may be by some other.
     *
     * @param helperName  The datasource helper (see entityengine.xml &lt;datasource name=".."&gt;)
     * @param connection  The connection to be used
     */
    public SQLProcessor(String helperName, Connection connection) {
        this.helperName  = helperName;
        this._connection = connection;
        
        // Do not commit while closing
        if (_connection != null) {
            _manualTX = false;
        }
    }
    
    /**
     * Commit all modifications
     *
     * @throws GenericDataSourceException
     */
    public void commit() throws GenericDataSourceException {
        if (_connection == null) {
            return;
        }
        
        try {
            if (_manualTX) {
                _connection.commit();
            }
        } catch (SQLException sqle) {
            rollback();
            throw new GenericDataSourceException("SQL Exception occured on commit", sqle);
        }
    }
    
    /**
     * Rollback all modifications
     */
    public void rollback() throws GenericDataSourceException {
        if (_connection == null) {
            return;
        }
        
        try {
            if (_manualTX) {
                _connection.rollback();
            } else {
                try {
                    TransactionUtil.setRollbackOnly();
                } catch (GenericTransactionException e) {
                    Debug.logError(e, "Error setting rollback only");
                    throw new GenericDataSourceException("Error setting rollback only", e);
                }
            }
        } catch (SQLException sqle2) {
            Debug.logWarning("[SQLProcessor.rollback]: SQL Exception while rolling back insert. Error was:" + sqle2, module);
            Debug.logWarning(sqle2, module);
        }
    }
    
    /**
     * Commit if required and remove all allocated resources
     *
     * @throws GenericDataSourceException
     */
    public void close() throws GenericDataSourceException {
        if (_manualTX) {
            commit();
        }
        
        _sql = null;
        
        if (_rs != null) {
            try {
                _rs.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            
            _rs = null;
        }
        
        if (_ps != null) {
            try {
                _ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            
            _ps = null;
        }
        
        if (_stmt != null) {
            try {
                _stmt.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            
            _stmt = null;
        }
        
        if ((_connection != null) && (_bDeleteConnection == true)) {
            try {
                _connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            
            _connection = null;
        }
    }
    
    /**
     * Get a connection from the ConnectionFactory
     *
     * @return  The connection created
     *
     * @throws GenericDataSourceException
     * @throws GenericEntityException
     */
    public Connection getConnection() throws GenericDataSourceException, GenericEntityException {
        if (_connection != null)
            return _connection;
        
        _manualTX = true;
        
        try {
            _connection = ConnectionFactory.getConnection(helperName);
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        //NOTE: the fancy ethernet type stuff is for the case where transactions not available
        try {
            _connection.setAutoCommit(false);
        } catch (SQLException sqle) {
            _manualTX = false;
        }
        
        try {
            if (TransactionUtil.getStatus() == TransactionUtil.STATUS_ACTIVE) {
                _manualTX = false;
            }
        } catch (GenericTransactionException e) {
            //nevermind, don't worry about it, but print the exc anyway
            Debug.logWarning("[SQLProcessor.getConnection]: Exception was thrown trying to check " +
            "transaction status: " + e.toString(), module);
        }
        
        _bDeleteConnection = true;
        return _connection;
    }
    
    /**
     * Prepare a statement. In case no connection has been given, allocate a
     * new one.
     *
     * @param sql  The SQL statement to be executed
     *
     * @throws GenericDataSourceException
     * @throws GenericEntityException
     */
    public void prepareStatement(String sql) throws GenericDataSourceException, GenericEntityException {
        this.prepareStatement(sql, false, 0, 0);
    }
    
    /**
     * Prepare a statement. In case no connection has been given, allocate a
     * new one.
     *
     * @param sql  The SQL statement to be executed
     *
     * @throws GenericDataSourceException
     * @throws GenericEntityException
     */
    public void prepareStatement(String sql, boolean specifyTypeAndConcur, int resultSetType, int resultSetConcurrency) throws GenericDataSourceException, GenericEntityException {
        if (Debug.verboseOn()) Debug.logVerbose("[SQLProcessor.prepareStatement] sql=" + sql, module);
        
        if (_connection == null) {
            getConnection();
        }
        
        try {
            _sql = sql;
            _ind = 1;
            if (specifyTypeAndConcur) {
                _ps = _connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
            } else {
                _ps = _connection.prepareStatement(sql);
            }
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        }
    }
    
    /**
     * Execute a query based on the prepared statement
     *
     * @param fkt
     * @return The result set of the query
     * @throws GenericDataSourceException
     */
    public ResultSet executeQuery() throws GenericDataSourceException {
        try {
            //if (Debug.verboseOn()) Debug.logVerbose("[SQLProcessor.executeQuery] ps=" + _ps.toString(), module);
            _rs = _ps.executeQuery();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("SQL Exception while executing the following:" + _sql, sqle);
        }
        
        return _rs;
    }
    
    /**
     * Execute a query baed ont SQL string given
     *
     * @param sql  The SQL string to be executed
     * @return  The result set of the query
     * @throws GenericEntityException
     * @throws GenericDataSourceException
     */
    public ResultSet executeQuery(String sql) throws GenericDataSourceException, GenericEntityException {
        prepareStatement(sql);
        return executeQuery();
    }
    
    /**
     * Execute updates
     *
     * @return  The number of rows updated
     * @throws GenericDataSourceException
     */
    public int executeUpdate() throws GenericDataSourceException {
        try {
            //if (Debug.verboseOn()) Debug.logVerbose("[SQLProcessor.executeUpdate] ps=" + _ps.toString(), module);
            return _ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("SQL Exception while executing the following:" + _sql, sqle);
        }
    }
    
    /**
     * Execute update based on the SQL statement given
     *
     * @param sql  SQL statement to be executed
     * @throws GenericDataSourceException
     */
    public int executeUpdate(String sql) throws GenericDataSourceException {
        Statement stmt = null;
        
        try {
            stmt = _connection.createStatement();
            return stmt.executeUpdate(sql);
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("SQL Exception while executing the following:" + _sql, sqle);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqle) {
                    Debug.logWarning("Unable to close 'statement': " + sqle.getMessage(), module);
                }
            }
        }
    }
    
    /**
     * Test if there more records available
     *
     * @return true, if there more records available
     *
     * @throws GenericDataSourceException
     */
    public boolean next() throws GenericDataSourceException {
        try {
            return _rs.next();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("SQL Exception while executing the following:" + _sql, sqle);
        }
    }
    
    /**
     * Getter: get the currently activ ResultSet
     *
     * @return ResultSet
     */
    public ResultSet getResultSet() {
        return _rs;
    }
    
    /**
     * Getter: get the prepared statement
     *
     * @return PreparedStatement
     */
    public PreparedStatement getPreparedStatement() {
        return _ps;
    }
    
    /**
     * Execute a query based on the SQL string given. For each record
     * of the ResultSet return, execute a callback function
     *
     * @param sql       The SQL string to be executed
     * @param aListener The callback function object
     *
     * @throws GenericEntityException
     */
    public void execQuery(String sql, ExecQueryCallbackFunctionIF aListener) throws GenericEntityException {
        if (_connection == null) {
            getConnection();
        }
        
        try {
            if (Debug.verboseOn()) Debug.logVerbose("[SQLProcessor.execQuery]: " + sql, module);
            executeQuery(sql);
            
            // process the results by calling the listener for
            // each row...
            boolean keepGoing = true;
            while(keepGoing && _rs.next()) {
                keepGoing = aListener.processNextRow(_rs);
            }
            
            if (_manualTX) {
                _connection.commit();
            }
            
        } catch (SQLException sqle) {
            Debug.logWarning("[SQLProcessor.execQuery]: SQL Exception while executing the following:\n" +
            sql + "\nError was:", module);
            Debug.logWarning(sqle.getMessage(), module);
            throw new GenericEntityException("SQL Exception while executing the following:" + _sql, sqle);
        } finally {
            close();
        }
    }
    
    /**
     * Set the next binding variable of the currently active prepared statement.
     *
     * @param field
     *
     * @throws SQLException
     */
    public void setValue(String field) throws SQLException {
        if (field != null)
            _ps.setString(_ind, field);
        else
            _ps.setNull(_ind, Types.VARCHAR);
        
        _ind ++;
    }
    
    /**
     * Set the next binding variable of the currently active prepared statement.
     *
     * @param field
     *
     * @throws SQLException
     */
    public void setValue(java.sql.Timestamp field) throws SQLException {
        if (field != null)
            _ps.setTimestamp(_ind, field);
        else
            _ps.setNull(_ind, Types.TIMESTAMP);
        
        _ind ++;
    }
    
    /**
     * Set the next binding variable of the currently active prepared statement.
     *
     * @param field
     *
     * @throws SQLException
     */
    public void setValue(java.sql.Time field) throws SQLException {
        if (field != null)
            _ps.setTime(_ind, field);
        else
            _ps.setNull(_ind, Types.TIME);
        
        _ind ++;
    }
    
    /**
     * Set the next binding variable of the currently active prepared statement.
     *
     * @param field
     *
     * @throws SQLException
     */
    public void setValue(java.sql.Date field) throws SQLException {
        if (field != null)
            _ps.setDate(_ind, field);
        else
            _ps.setNull(_ind, Types.DATE);
        
        _ind ++;
    }
    
    /**
     * Set the next binding variable of the currently active prepared statement.
     *
     * @param field
     *
     * @throws SQLException
     */
    public void setValue(Integer field) throws SQLException {
        if (field != null)
            _ps.setInt(_ind, field.intValue());
        else
            _ps.setNull(_ind, Types.NUMERIC);
        
        _ind ++;
    }
    
    /**
     * Set the next binding variable of the currently active prepared statement.
     *
     * @param field
     *
     * @throws SQLException
     */
    public void setValue(Long field) throws SQLException {
        if (field != null)
            _ps.setLong(_ind, field.longValue());
        else
            _ps.setNull(_ind, Types.NUMERIC);
        
        _ind ++;
    }
    
    /**
     * Set the next binding variable of the currently active prepared statement.
     *
     * @param field
     *
     * @throws SQLException
     */
    public void setValue(Float field) throws SQLException {
        if (field != null)
            _ps.setFloat(_ind, field.floatValue());
        else
            _ps.setNull(_ind, Types.NUMERIC);
        
        _ind ++;
    }
    
    /**
     * Set the next binding variable of the currently active prepared statement.
     *
     * @param field
     *
     * @throws SQLException
     */
    public void setValue(Double field) throws SQLException {
        if (field != null)
            _ps.setDouble(_ind, field.doubleValue());
        else
            _ps.setNull(_ind, Types.NUMERIC);
        
        _ind ++;
    }

    /**
     * Set the next binding variable of the currently active prepared statement.
     *
     * @param field
     *
     * @throws SQLException
     */
    public void setValue(Boolean field) throws SQLException {
        if (field != null)
            _ps.setBoolean(_ind, field.booleanValue());
        else
            _ps.setNull(_ind, Types.NULL); //TODO: really should be Types.BOOLEAN, but that wasn't introduced until Java 1.4... hmmm what to do?
        
        _ind ++;
    }
    
    protected void finalize() throws Throwable {
        try {
            this.close();
        } catch (Exception e) {
            Debug.logError(e, "Error closing the result, connection, etc in finalize EntityListIterator");
        }
        super.finalize();
    }
}
