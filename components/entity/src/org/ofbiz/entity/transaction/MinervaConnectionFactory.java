/*
 * $Id: MinervaConnectionFactory.java,v 1.3 2004/04/30 22:28:51 ajzeneski Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *
 */
package org.ofbiz.entity.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.minerva.pool.jdbc.xa.XAPoolDataSource;
import org.ofbiz.minerva.pool.jdbc.xa.wrapper.XADataSourceImpl;
import org.w3c.dom.Element;

/**
 * MinervaConnectionFactory - Central source for Minerva JDBC Objects
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.3 $
 * @since      3.0
 */
public class MinervaConnectionFactory {
        
    public static final String module = JotmConnectionFactory.class.getName();                
        
    protected static Map dsCache = new HashMap();
    
    public static Connection getConnection(String helperName, Element jotmJdbcElement) throws SQLException, GenericEntityException {                               
        XAPoolDataSource pds = (XAPoolDataSource) dsCache.get(helperName);        
        if (pds != null) {                                  
            return TransactionFactory.getCursorConnection(helperName, pds.getConnection());
        }
        
        synchronized (JotmConnectionFactory.class) {            
            pds = (XAPoolDataSource) dsCache.get(helperName);
            if (pds != null) {                           
                return pds.getConnection();
            } else {
                pds = new XAPoolDataSource();
                pds.setPoolName(helperName);
            }

            XADataSourceImpl ds = new XADataSourceImpl();

            if (ds == null)
                throw new GenericEntityException("XADataSource was not created, big problem!");
            
            ds.setDriver(jotmJdbcElement.getAttribute("jdbc-driver"));
            ds.setURL(jotmJdbcElement.getAttribute("jdbc-uri"));
            
            String transIso = jotmJdbcElement.getAttribute("isolation-level");
            if (transIso != null && transIso.length() > 0) {
                if ("Serializable".equals(transIso)) {
                    pds.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                } else if ("RepeatableRead".equals(transIso)) {
                    pds.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                } else if ("ReadUncommitted".equals(transIso)) {
                    pds.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                } else if ("ReadCommitted".equals(transIso)) {
                    pds.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                } else if ("None".equals(transIso)) {
                    pds.setTransactionIsolation(Connection.TRANSACTION_NONE);
                }                                            
            }
            
            // set the datasource in the pool            
            pds.setDataSource(ds);
            pds.setJDBCUser(jotmJdbcElement.getAttribute("jdbc-username"));
            pds.setJDBCPassword(jotmJdbcElement.getAttribute("jdbc-password"));
            
            // set the transaction manager in the pool
            pds.setTransactionManager(TransactionFactory.getTransactionManager());
            
            // configure the pool settings           
            try {            
                pds.setMaxSize(new Integer(jotmJdbcElement.getAttribute("pool-maxsize")).intValue());
                pds.setMinSize(new Integer(jotmJdbcElement.getAttribute("pool-minsize")).intValue());
            } catch (NumberFormatException nfe) {
                Debug.logError(nfe, "Problems with pool settings; the values MUST be numbers, using defaults.", module);
            } catch (Exception e) {
                Debug.logError(e, "Problems with pool settings", module);
            }
                                  
            // cache the pool
            dsCache.put(helperName, pds);        
                                                      
            return TransactionFactory.getCursorConnection(helperName, pds.getConnection());
        }                
    }
    
    public static void closeAll() {
        Set cacheKeys = dsCache.keySet();
        Iterator i = cacheKeys.iterator();
        while (i.hasNext()) {
            String helperName = (String) i.next();
            XAPoolDataSource pds = (XAPoolDataSource) dsCache.remove(helperName);
            pds.close();   
        }                                                                             
    }
}
