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
    protected int loops = 100;
    
    protected GenericDelegator delegator = null;
    protected TMService jotm = null;
    protected StandardXAPoolDataSource pool = null;
            
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
            String msg = dbUtil.createTable(model, null, false, false, 1, "", false);
            if (msg == null) {
                logger.info("Table '" + tableName + "' created.");    
            } else {
                logger.info(msg);
            }
        } else {        
            throw new SQLException("No delegator available; cannot create");
        }                           
    }
    
    /** Tests a loop of inserts using different connections and transaction */
    public void insertTest() {
        // start the transaction
        UserTransaction trans = jotm.getUserTransaction();                   
        logger.info("Beginning multiple insert with unique transactions/connections...");
                                
        for (int i = 0; i < loops; i++) { 
            try {
                trans.begin();            
            } catch (NotSupportedException e1) {            
                logger.error("Exception", e1);
            } catch (SystemException e1) {            
                logger.error("Exception", e1);
            }
            
            Connection con = null;
            try {
                con = pool.getConnection();
            } catch (SQLException e) {            
                logger.error("Problems getting new connection - Test Failed!", e);
                return;
            }    
            if (con == null) {
                logger.error("Pool returned null connection with no exception - Test Failed!");
                return;    
            }            
                                                       
            logger.debug("[A] Looping.. inserting #" + i);
            try {
                // insert item            
                String sql1 = "INSERT INTO " + tableName + " VALUES(?,?,?,?)";
                PreparedStatement ps1 = con.prepareStatement(sql1);
                ps1.setInt(1, ++counter);
                ps1.setString(2, "insTest" + i);
                ps1.setString(3, "Insert Test");
                ps1.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps1.executeUpdate();
                ps1.close();   
                // select it back
                String sql2 = "SELECT * FROM " + tableName + " WHERE idx = ?";
                PreparedStatement ps2 = con.prepareStatement(sql2);
                ps2.setInt(1, counter);
                ResultSet res = ps2.executeQuery();
                if (res == null || !res.next()) {
                    logger.error("Could not get inserted item back from select!");
                } else {
                    logger.debug(res.getString(1) + " : " + res.getString(2) + "[" + res.getString(3) + "] - " + res.getString(4));
                }
                res.close();
                ps2.close();                 
            } catch (Exception e) {
                logger.error("Exception", e);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e2) {                    
                    logger.error("Exception", e2);
                } catch (SystemException e2) {                    
                    logger.error("Exception", e2);
                }                
            } finally {
                // close the connection
                try {
                    con.close();
                } catch (SQLException e2) {            
                    logger.error("Exception", e2);
                }                
        
                // commit the transaction
                try {        
                    trans.commit();            
                } catch (Exception e) {
                    logger.error("Exception", e);
                }                           
            }
        }                        
    }
    
    /** Tests a loop of inserts using different connections, same transaction */
    public void connectionTest() {
        // start the transaction
        UserTransaction trans = jotm.getUserTransaction();
        try {
            trans.begin();
        } catch (NotSupportedException e1) {
            logger.error("Exception", e1);                       
        } catch (SystemException e1) {
            logger.error("Exception", e1);                        
        }
        
        logger.info("Beginning multiple insert/connection single transaction...");
        for (int i = 0; i < loops; i++) {            
            Connection con = null;
            try {
                con = pool.getConnection();
            } catch (SQLException e) {            
                logger.error("Problems getting new connection - Test Failed!", e);
                return;
            }    
            if (con == null) {
                logger.error("Pool returned null connection with no exception - Test Failed!");
                return;    
            }
            
            logger.debug("Got connection.. inserting #" + i);
            try {            
                String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, ++counter);
                ps.setString(2, "conTest" + i);
                ps.setString(3, "Connection Test");
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps.executeUpdate();
                ps.close();                
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e2) {                    
                    logger.error("Exception", e2);
                } catch (SystemException e2) {                    
                    logger.error("Exception", e2);
                }
            } finally {                
                try {
                    con.close();
                } catch (SQLException e2) {                    
                    logger.error("Exception", e2);
                }
            }
        }
        try {        
            trans.commit();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    
    /** Tests a loop of inserts using same connection & transaction */
    public void loopTest() {
        // start the transaction
        UserTransaction trans = jotm.getUserTransaction();
        try {
            trans.begin();            
        } catch (NotSupportedException e1) {            
            logger.error("Exception", e1);
        } catch (SystemException e1) {            
            logger.error("Exception", e1);
        }
                                
        logger.info("Beginning multiple insert single transaction/connection...");
        
        Connection con = null;
        try {
            con = pool.getConnection();
        } catch (SQLException e) {            
            logger.error("Problems getting new connection - Test Failed!", e);
            return;
        }    
        if (con == null) {
            logger.error("Pool returned null connection with no exception - Test Failed!");
            return;    
        }
        
        logger.debug("Got connection.. ");
        
        for (int i = 0; i < loops; i++) {                       
            logger.debug("[B] Looping.. inserting #" + i);
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
                logger.error("Exception", e);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e2) {                    
                    logger.error("Exception", e2);
                } catch (SystemException e2) {                    
                    logger.error("Exception", e2);
                }
            }            
        }
        
        // close the connection
        try {
            con.close();
        } catch (SQLException e2) {            
            logger.error("Exception", e2);
        }
        logger.debug("Closed connection..");
        
        // commit the transaction
        try {        
            trans.commit();            
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    
    /** Tests multiple inserts; suspending before each one, insert in new trans, resuming to continue */
    public void suspendTest() {
        // start the transaction
        UserTransaction trans = jotm.getUserTransaction();
        TransactionManager tm = jotm.getTransactionManager();
        
        // set the timeout to something reasonable
        try {    
            trans.setTransactionTimeout(300);
        } catch (SystemException e) {
            logger.error("Exception", e);
            return;
        }
                
        // begin the parent transaction
        try {
            trans.begin();
        } catch (NotSupportedException e1) {           
            logger.error("Exception", e1);
        } catch (SystemException e1) {            
            logger.error("Exception", e1);
        }
        
        logger.info("Beginning multiple insert/connection on suspend main transaction...");
        for (int i = 0; i < loops; i++) {            
            // suspend the main transaction                    
            Transaction transaction = null;            
            try {
                transaction = tm.suspend();
            } catch (SystemException e2) {                
                logger.error("Exception", e2);
            }
            logger.debug("Suspended #" + i);
            
            // begin a new transaction
            try {
                trans.begin();
            } catch (NotSupportedException e3) {               
                logger.error("Exception", e3);
            } catch (SystemException e3) {                
                logger.error("Exception", e3);
            }
            logger.debug("Began new transaction.");
            
            // do some stuff in the new transaction
            Connection con1 = null;
            try {
                con1 = pool.getConnection();
            } catch (SQLException e) {               
                logger.error("Problems getting new (sub) connection - Test Failed!", e);
                return;
            }
            if (con1 == null) {
                logger.error("Pool returned null connection with no exception - Test Failed!");
                return;
            }
            logger.debug("Got connection.");
            
            try {
                // insert item            
                String sql1 = "INSERT INTO " + tableName + " VALUES(?,?,?,?)";
                PreparedStatement ps1 = con1.prepareStatement(sql1);
                ps1.setInt(1, ++counter);
                ps1.setString(2, "susTest" + i);
                ps1.setString(3, "Suspend Test - Main Suspended");
                ps1.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps1.executeUpdate();
                ps1.close();                              
            } catch (Exception e) {
                logger.error("Exception", e);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e4) {                    
                    logger.error("Exception", e4);
                } catch (SystemException e4) {                    
                    logger.error("Exception", e4);
                }
            } finally {                        
                try {
                    con1.close();
                } catch (SQLException e5) {                   
                    logger.error("Exception", e5);
                }  
                
                // commit the new transaction              
                try {
                    trans.commit();
                } catch (SecurityException e4) {                   
                    logger.error("Exception", e4);
                } catch (IllegalStateException e4) {                    
                    logger.error("Exception", e4);
                } catch (RollbackException e4) {                    
                    logger.error("Exception", e4);
                } catch (HeuristicMixedException e4) {                    
                    logger.error("Exception", e4);
                } catch (HeuristicRollbackException e4) {                    
                    logger.error("Exception", e4);
                } catch (SystemException e4) {                    
                    logger.error("Exception", e4);
                }                                
            }
            logger.debug("Inserted record.");
            
            // resume the main transaction
            try {
                tm.resume(transaction);
            } catch (InvalidTransactionException e4) {                
                logger.error("Exception", e4);
            } catch (IllegalStateException e4) {                
                logger.error("Exception", e4);
            } catch (SystemException e4) {                
                logger.error("Exception", e4);
            }
            logger.debug("Resumed #" + i);
            
            // do some stuff in the main transaction
            Connection con2 = null;
            try {
                con2 = pool.getConnection();
            } catch (SQLException e) {
                logger.error("Problems getting new (main) connection - Test Failed!", e);
                return;              
            }          
            if (con2 == null) {
                logger.error("Pool returned null connection with no exception - Test Failed!");
                return;  
            }
            
            try {            
                String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?)";
                PreparedStatement ps1 = con2.prepareStatement(sql);
                ps1.setInt(1, ++counter);
                ps1.setString(2, "susTest" + i);
                ps1.setString(3, "Suspend Test - Main");
                ps1.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps1.executeUpdate();
                ps1.close();                
                logger.debug("Inserted main transaction.");
                // select it back
                String sql2 = "SELECT * FROM " + tableName + " WHERE idx = ?";
                PreparedStatement ps2 = con2.prepareStatement(sql2);
                ps2.setInt(1, counter);
                ResultSet res = ps2.executeQuery();
                if (res == null || !res.next()) {
                    logger.error("Could not get inserted item back from select!");
                } else {
                    logger.debug(res.getString(1) + " : " + res.getString(2) + "[" + res.getString(3) + "] - " + res.getString(4));
                }       
                res.close();
                ps2.close();                                    
            } catch (Exception e) {
                logger.error("Exception", e);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e5) {                   
                    logger.error("Exception", e5);
                } catch (SystemException e5) {                    
                    logger.error("Exception", e5);
                }
            } finally {
                try {
                    con2.close();
                } catch (SQLException e5) {                    
                    logger.error("Exception", e5);
                }
            }
        }
        
        // commit the main transaction - outside the loop
        try {
            trans.commit();
        } catch (SecurityException e) {            
            logger.error("Exception", e);
        } catch (IllegalStateException e) {            
            logger.error("Exception", e);
        } catch (RollbackException e) {            
            logger.error("Exception", e);
        } catch (HeuristicMixedException e) {            
            logger.error("Exception", e);
        } catch (HeuristicRollbackException e) {            
            logger.error("Exception", e);
        } catch (SystemException e) {            
            logger.error("Exception", e);
        }
    }
        
    public void rollbackOnlyTest() { 
        // start the transaction
        UserTransaction trans = jotm.getUserTransaction();
        try {
            trans.begin();
        } catch (NotSupportedException e1) {            
            logger.error("Exception", e1);
        } catch (SystemException e1) {            
            logger.error("Exception", e1);
        }
        
        logger.info("Beginning rollback test...");
        Random rand = new Random();
        int randomInt = rand.nextInt(9);
        
        for (int i = 0; i < 10; i++) {            
            Connection con = null;
            try {
                con = pool.getConnection();                
            } catch (SQLException e) {                
                logger.error("Problems getting connection - rolling back now.", e);
                try {
                    trans.rollback();
                } catch (IllegalStateException e2) {                  
                    logger.error("Exception", e2);
                } catch (SecurityException e2) {                    
                    logger.error("Exception", e2);
                } catch (SystemException e2) {                    
                    logger.error("Exception", e2);
                }
            }
            if (con == null) {
                logger.error("Pool returned a null connection w/ no exception! Test Failed!");
                return;
            }
            
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
                logger.error("Exception", e);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e2) {                   
                    logger.error("Exception", e2);
                } catch (SystemException e2) {                    
                    logger.error("Exception", e2);
                }
            } finally {                
                try {
                    con.close();
                } catch (SQLException e2) {                    
                    logger.error("Exception", e2);
                }                
            }
            
            // will set rollback only on some random pass
            if (randomInt == i) {
                logger.info("Setting rollback only on pass #" + i);
                try {
                    trans.setRollbackOnly();
                } catch (IllegalStateException e2) {                   
                    logger.error("Exception", e2);
                } catch (SystemException e2) {                    
                    logger.error("Exception", e2);
                }
            }
            
        }
        
        try {        
            trans.commit();        
        } catch (SystemException e) {
            logger.error("Commit failed; RollbackException not thrown!", e);
        } catch (SecurityException e) {
            logger.error("Commit failed; RollbackException not thrown!", e);
        } catch (IllegalStateException e) {            
            logger.error("Commit failed; RollbackException not thrown!", e);                        
        } catch (RollbackException re) {                        
            // This SHOULD happen!
            logger.info("Commit failed (good), transaction rolled back by commit().");                                    
        } catch (HeuristicMixedException e) {            
            logger.error("Commit failed; RollbackException not thrown!", e);
        } catch (HeuristicRollbackException e) {          
            logger.error("Commit failed; RollbackException not thrown!", e);
        }
    }  
    
    public void timeoutTest() {
        // get the transaction
        UserTransaction trans = jotm.getUserTransaction();
                                   
        logger.info("Beginning timeout test...");
        Random rand = new Random();
        int randomInt = rand.nextInt(60);
        randomInt++;
        
        logger.info("Setting timeout to: " + randomInt);
        try {            
            trans.setTransactionTimeout(randomInt); // set to new value
        } catch (SystemException e) {            
            logger.error("Exception", e);
        }
        
        // begin the transaction
        try {
            trans.begin();
        } catch (NotSupportedException e1) {            
            logger.error("Exception", e1);
        } catch (SystemException e1) {            
            logger.error("Exception", e1);
        }
        logger.info("Began transaction; now waiting...");           
        
        // now wait a few seconds
        long wait = new Date().getTime() + ((randomInt + 2) * 1000);
        long now = 0;
        while ((now = new Date().getTime()) < wait) {
            //logger.info(now + " != " + wait);                                        
        }        
        
        // attempt to commit the transaction; should fail
        try {
            trans.commit();
            logger.info("Transaction commited; shouldn't have happened!");
        } catch (SecurityException e) {           
            logger.error("Exception", e);
        } catch (IllegalStateException e) {            
            logger.error("Exception", e);            
        } catch (RollbackException e) {            
            logger.info("RollBackException caught! Good! The transaction was rolled back.");            
        } catch (HeuristicMixedException e) {            
            logger.error("Exception", e);
        } catch (HeuristicRollbackException e) {            
            logger.error("Exception", e);            
        } catch (SystemException e) {            
            logger.error("Exception", e);
        }        
    }              
    
    public void selectTest() throws SQLException {
        logger.info("Beginning select test.. We should have exactly " + (loops * 5) + " records.");
        Connection con = pool.getConnection();
        Statement s = con.createStatement();        
        ResultSet res = s.executeQuery("SELECT * FROM " + tableName + " ORDER BY idx");
        int rowCount = 0;
        while (res.next()) {
            rowCount++;
            logger.debug(res.getString(1) + " : " + res.getString(2) + "[" + res.getString(3) + "] - " + res.getString(4));
        }
        res.close();
        con.close();
        logger.info("Total Rows: " + rowCount + " of " + (loops * 5));
        if (rowCount == (loops * 5)) logger.info("Looks good...");        
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
        insertTest(); 
               
        connectionTest();
        
        // test looping; same connection        
        loopTest();
        
        // test some suspend/resume inserts               
        suspendTest();        
        
        // test rollback        
        rollbackOnlyTest();        
        
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
}