/*
 * $Id$
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.entity;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.WriterAppender;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.objectweb.jotm.Jotm;
import org.objectweb.transaction.jta.TMService;
import org.w3c.dom.Element;

import org.ofbiz.core.entity.config.EntityConfigUtil;
import org.ofbiz.core.entity.jdbc.DatabaseUtil;
import org.ofbiz.core.entity.model.ModelEntity;

/**
 * JotmXaPoolTest - Program For Testing Connections (pooled) and Transactions
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.2
 */
public class JotmXaPoolTest {

    private final static String tableName = "jotm_xapool_test";
    protected Logger logger = null;
    protected Writer writer = null;
         
    protected int counter = 0;
    
    protected GenericDelegator delegator = null;
    protected TMService jotm = null;
    protected StandardXAPoolDataSource pool = null;
    
    public JotmXaPoolTest() throws Exception {
        this.setLogger(null);
               
        // start JOTM
        jotm = new Jotm(true, false);
        logger.info("Started JOTM...");  
        
        // create the datasource
        StandardXADataSource ds = new StandardXADataSource();
        ds.setTransactionManager(jotm.getTransactionManager());
        
        // ---- HSQL Settings ----
        ds.setDriverName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:data/jotmxapooltext");
        ds.setUser("sa");
        ds.setPassword("");
        ds.setDescription("HSQLDB");
        ds.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        
        // ---- PostgreSQL Settings ----
        //ds.setDriverName("org.postgresql.Driver");       
        //ds.setUrl("jdbc:postgresql://localhost/jotmxapooltext");
        //ds.setUser("postgres");
        //ds.setPassword("");
        //ds.setDescription("PostgreSQL");          
        //ds.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        
        // now create the pool
        this.createPool(ds);                                               
    }
        
    public JotmXaPoolTest(GenericDelegator delegator, Level l) throws Exception {
        this.delegator = delegator;
        this.setLogger(l);
        
        GenericHelper helper = delegator.getEntityHelper("JotmXapoolTest");
        EntityConfigUtil.DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helper.getHelperName());
        if (datasourceInfo.inlineJdbcElement == null) {
            throw new Exception("No inline jdbc element found for this helper : " + helper.getHelperName());
        }
        Element jotmJdbcElement = datasourceInfo.inlineJdbcElement;        
        
        // start JOTM
        jotm = new Jotm(true, false);       
        logger.info("Started JOTM...");
                 
        String wrapperClass = jotmJdbcElement.getAttribute("pool-xa-wrapper-class");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class c = loader.loadClass(wrapperClass);
        Object o = c.newInstance();
        
        StandardXADataSource ds = (StandardXADataSource) o;                                                                                                                                
        ds.setDriverName(jotmJdbcElement.getAttribute("jdbc-driver"));
        ds.setUrl(jotmJdbcElement.getAttribute("jdbc-uri"));
        ds.setUser(jotmJdbcElement.getAttribute("jdbc-username"));
        ds.setPassword(jotmJdbcElement.getAttribute("jdbc-password"));
        ds.setDescription(ds.getDriverName());
        ds.setTransactionManager(jotm.getTransactionManager());         
            
        String transIso = jotmJdbcElement.getAttribute("isolation-level");
        if (transIso != null && transIso.length() > 0) {
            if ("Serializable".equals(transIso)) {
                ds.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            } else if ("RepeatableRead".equals(transIso)) {
                ds.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } else if ("ReadUncommitted".equals(transIso)) {
                ds.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            } else if ("ReadCommitted".equals(transIso)) {
                ds.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            } else if ("None".equals(transIso)) {
                ds.setTransactionIsolation(Connection.TRANSACTION_NONE);
            }                                            
        } 
        
        this.createPool(ds);       
    }
        
    private void createPool(StandardXADataSource ds) {
        pool = new StandardXAPoolDataSource();
        pool.setDataSource(ds);
        pool.setDescription(ds.getDescription());
        pool.setUser(ds.getUser());
        pool.setPassword(ds.getPassword());
        pool.setTransactionManager(jotm.getTransactionManager());                   
    }
    
    private void setLogger(Level l) {
        writer = new StringWriter();
        logger = Logger.getLogger("jotm.test");
        if (l != null) {            
            logger.setLevel(l);
        }
        
        // create an appender
        WriterAppender wa = new WriterAppender(new HTMLLayout(), writer);
        logger.addAppender(wa);        
    }
    
    public void dropTest() throws SQLException {        
        Connection con = pool.getConnection();
        Statement s = con.createStatement();
        s.executeUpdate("DROP TABLE " + tableName);
        s.close();
        con.close();
        logger.info("Table '" + tableName + "' dropped.");        
    }
    
    public void createTest() throws SQLException {                
        // get a connection
        if (delegator != null) {
            // create using the delegator so we use valid SQL for the DB
            GenericHelper helper = null;
            try {
                helper = delegator.getEntityHelper("JotmXapoolTest");
            } catch (GenericEntityException e) {
                logger.error("", e);
                throw new SQLException();
            }
            ModelEntity model = delegator.getModelEntity("JotmXapoolTest");
            DatabaseUtil dbUtil = new DatabaseUtil(helper.getHelperName());
            dbUtil.createTable(model, null, false, false, 1, "", false);
        } else {        
            Connection con = pool.getConnection();
        
            // HSQL Create Statement
            String sql = "CREATE TABLE " + tableName + " (idx BIGINT, col_a VARCHAR(), col_b VARCHAR(), stamp TIMESTAMP)";
        
            // PostgreSQL Create Statement
            //String sql = "CREATE TABLE " + tableName + " (idx NUMERIC(18,0), col_1 VARCHAR(60), col_2 VARCHAR(60), stamp TIMESTAMPTZ)";
                       
            Statement s = con.createStatement();
            s.executeUpdate(sql);
            s.close();                               
            con.close();
        }
        logger.info("Table '" + tableName + "' created.");                    
    }
    
    /** Tests a loop of inserts using different connections, same transaction */
    public void insertTest() throws SQLException {
        // start the transaction
        UserTransaction trans = jotm.getUserTransaction();
        try {
            trans.begin();
        } catch (NotSupportedException e1) {
            logger.error("", e1);                       
        } catch (SystemException e1) {
            logger.error("", e1);                        
        }
        
        logger.info("Beginning multiple insert/connection single transaction...");
        for (int i = 0; i < 500; i++) {            
            Connection con = pool.getConnection();
            logger.debug("Got connection.. inserting #" + i);
            try {            
                String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, ++counter);
                ps.setString(2, "insTest" + i);
                ps.setString(3, "Insert Test");
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps.executeUpdate();
                ps.close();                
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e2) {                    
                    logger.error("", e2);
                } catch (SystemException e2) {                    
                    logger.error("", e2);
                }
            } finally {                
                con.close();
            }
        }
        try {        
            trans.commit();
        } catch (Exception e) {
            logger.error("", e);
        }
    }
    
    public void loopTest() throws SQLException {
        // start the transaction
        UserTransaction trans = jotm.getUserTransaction();
        try {
            trans.begin();
        } catch (NotSupportedException e1) {            
            logger.error("", e1);
        } catch (SystemException e1) {            
            logger.error("", e1);
        }
                                
        logger.info("Beginning multiple insert single transaction/connection...");
        
        Connection con = pool.getConnection();        
        logger.debug("Got connection.. ");
        
        for (int i = 0; i < 500; i++) {                       
            logger.debug("Looping.. inserting #" + i);
            try {            
                String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, ++counter);
                ps.setString(2, "loopTest" + i);
                ps.setString(3, "Loop Test");
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps.executeUpdate();
                ps.close();                
            } catch (Exception e) {
                logger.error("", e);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e2) {                    
                    logger.error("", e2);
                } catch (SystemException e2) {                    
                    logger.error("", e2);
                }
            } finally {                
                con.close();
            }
        }
        try {        
            trans.commit();
        } catch (Exception e) {
            logger.error("", e);
        }
    }
    
    /** Tests multiple inserts; suspending before each one, insert in new trans, resuming to continue */
    public void suspendTest() throws SQLException {
        // start the transaction
        UserTransaction trans = jotm.getUserTransaction();
        TransactionManager tm = jotm.getTransactionManager();
        
        // begin the parent transaction
        try {
            trans.begin();
        } catch (NotSupportedException e1) {           
            logger.error("", e1);
        } catch (SystemException e1) {            
            logger.error("", e1);
        }
        
        logger.info("Beginning multiple insert/connection on suspend main transaction...");
        for (int i = 0; i < 500; i++) {            
            // suspend the main transaction                    
            Transaction transaction = null;            
            try {
                transaction = tm.suspend();
            } catch (SystemException e2) {                
                logger.error("", e2);
            }
            logger.debug("Suspended #" + i);
            
            // begin a new transaction
            try {
                trans.begin();
            } catch (NotSupportedException e3) {               
                logger.error("", e3);
            } catch (SystemException e3) {                
                logger.error("", e3);
            }
            
            // do some stuff in the new transaction
            Connection con1 = pool.getConnection();
            try {
                String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?)";
                PreparedStatement ps = con1.prepareStatement(sql);
                ps.setInt(1, ++counter);
                ps.setString(2, "susTest" + i);
                ps.setString(3, "Suspend Test - Main Suspended");
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps.executeUpdate();
                ps.close();                
            } catch (Exception e) {
                logger.error("", e);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e4) {                    
                    logger.error("", e4);
                } catch (SystemException e4) {                    
                    logger.error("", e4);
                }
            } finally {                        
                con1.close();  
                
                // commit the new transaction              
                try {
                    trans.commit();
                } catch (SecurityException e4) {                   
                    logger.error("", e4);
                } catch (IllegalStateException e4) {                    
                    logger.error("", e4);
                } catch (RollbackException e4) {                    
                    logger.error("", e4);
                } catch (HeuristicMixedException e4) {                    
                    logger.error("", e4);
                } catch (HeuristicRollbackException e4) {                    
                    logger.error("", e4);
                } catch (SystemException e4) {                    
                    logger.error("", e4);
                }                                
            }
            
            // resume the main transaction
            try {
                tm.resume(transaction);
            } catch (InvalidTransactionException e4) {                
                logger.error("", e4);
            } catch (IllegalStateException e4) {                
                logger.error("", e4);
            } catch (SystemException e4) {                
                logger.error("", e4);
            }
            logger.debug("Resumed #" + i);
            
            // do some stuff in the main transaction
            Connection con2 = pool.getConnection();            
            try {            
                String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?)";
                PreparedStatement ps = con2.prepareStatement(sql);
                ps.setInt(1, ++counter);
                ps.setString(2, "susTest" + i);
                ps.setString(3, "Suspend Test - Main");
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps.executeUpdate();
                ps.close();
                logger.debug("Inserted main transaction.");
            } catch (Exception e) {
                logger.error("", e);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e5) {                   
                    logger.error("", e5);
                } catch (SystemException e5) {                    
                    logger.error("", e5);
                }
            } finally {
                con2.close();
            }
        }
        
        // commit the main transaction - outside the loop
        try {
            trans.commit();
        } catch (SecurityException e) {            
            logger.error("", e);
        } catch (IllegalStateException e) {            
            logger.error("", e);
        } catch (RollbackException e) {            
            logger.error("", e);
        } catch (HeuristicMixedException e) {            
            logger.error("", e);
        } catch (HeuristicRollbackException e) {            
            logger.error("", e);
        } catch (SystemException e) {            
            logger.error("", e);
        }
    }
    
    public void rollbackTest() throws SQLException { 
        // start the transaction
        UserTransaction trans = jotm.getUserTransaction();
        try {
            trans.begin();
        } catch (NotSupportedException e1) {            
            logger.error("", e1);
        } catch (SystemException e1) {            
            logger.error("", e1);
        }
        
        logger.info("Beginning rollback test...");
        Random rand = new Random();
        int randomInt = rand.nextInt(9);
        
        for (int i = 0; i < 10; i++) {            
            Connection con = pool.getConnection();
            logger.debug("Got connection.. inserting #" + i);
            try {            
                String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, ++counter);
                ps.setString(2, "rollTest" + i);
                ps.setString(3, "Rollback Test - This should not show in selectTest!");
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps.executeUpdate();
                ps.close();                
            } catch (Exception e) {
                logger.error("", e);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e2) {                   
                    logger.error("", e2);
                } catch (SystemException e2) {                    
                    logger.error("", e2);
                }
            } finally {                
                con.close();                
            }
            
            // will set rollback only on some random pass
            if (randomInt == i) {
                logger.info("Setting rollback only on pass #" + i);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e2) {                   
                    logger.error("", e2);
                } catch (SystemException e2) {                    
                    logger.error("", e2);
                }
            }
            
        }
        try {        
            trans.commit();
        } catch (Exception e) {
            // This SHOULD happen!
            logger.info("Commit failed (good), now rolling back.");
            try {
                trans.rollback();
            } catch (Exception e1) {
                logger.error("", e1);
            }
        }
    }            
    
    public void selectTest() throws SQLException {
        Connection con = pool.getConnection();
        Statement s = con.createStatement();
        ResultSet res = s.executeQuery("SELECT * FROM " + tableName + " ORDER BY idx");
        int rowCount = 0;
        while (res.next()) {
            rowCount++;
            logger.debug(res.getString(1) + " : " + res.getString(2) + "[" + res.getString(3) + "] - " + res.getString(4));
        }
        logger.info("Total Rows: " + rowCount + " of 2000");
        if (rowCount == 2000) logger.info("Looks good...");        
    }
    
    public void close() {
        pool.shutdown(true);
        jotm.stop();
        try {
            writer.close();
        } catch (IOException e) {            
            logger.error("", e);
        }
    }
    
    public String write() {
        return writer.toString();
    }
    
    public String writeAndClose() {
        String write = write();
        this.close();
        return write;
    }
    
    public String runTests() {       
        // try to drop the table
        try {       
            dropTest();
        } catch (SQLException e) {            
        }
        
        // try to create the table
        try {        
            createTest();
        } catch (SQLException e) {
            logger.error("Fatal SQL Error", e);
            return writeAndClose();
        }
        
        // test some basic inserts
        try {                       
            insertTest();
        } catch (SQLException e) {
            logger.error("SQL Error", e);
        }
        
        // test looping; same connection
        try {
            loopTest();
        } catch (SQLException e) {
            logger.error("SQL Error", e);            
        }
        
        // test some suspend/resume inserts
        try {        
            suspendTest();
        } catch (SQLException e) {
            logger.error("SQL Error", e);            
        }
        
        // test rollback
        try {
            rollbackTest();
        } catch (SQLException e) {
            logger.error("SQL Error", e);           
        }
        
        // show the results
        try {        
            selectTest();
        } catch (SQLException e) {
            logger.error("SQL Error", e);
        }
        
        // drop the table
        try {       
            dropTest();
        } catch (SQLException e) {
            logger.error("Fatal SQL Error", e);
            return writeAndClose();
        }
        
        return writeAndClose();
    }
    
    public static void main(String[] args) throws Exception {           
        JotmXaPoolTest test = new JotmXaPoolTest();
        test.runTests();
        System.exit(0);
    }
}