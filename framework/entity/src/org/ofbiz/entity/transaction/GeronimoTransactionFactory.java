/*
 * $Id: $
 *
 * Copyright 2006-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.entity.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.geronimo.transaction.context.TransactionContextManager;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.config.DatasourceInfo;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.jdbc.ConnectionFactory;

/**
 * GeronimoTransactionFactory
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @since      3.5
 */
public class GeronimoTransactionFactory implements TransactionFactoryInterface {

    public static final String module = GeronimoTransactionFactory.class.getName();        
    
    protected static TransactionContextManager geronimoTcm = null;

    static {    
        // creates an instance of Geronimo with a local transaction factory which is not bound to a registry            
        geronimoTcm = new TransactionContextManager();
    }

    /*
     * @see org.ofbiz.entity.transaction.TransactionFactoryInterface#getTransactionManager()
     */
    public TransactionManager getTransactionManager() {  
        if (geronimoTcm != null) {
            // TODO: this is not working; not sure how to initialize or use the Geronimo API properly, have not found any valid examples
            Debug.logError("Got TransactionManager from geronimoTcm: " + geronimoTcm.getTransactionManager(), module);
            
            return geronimoTcm.getTransactionManager();
        } else {
            Debug.logError("Cannot get TransactionManager, geronimoTcm object is null", module);
            return null;
        }
    }

    /*
     * @see org.ofbiz.entity.transaction.TransactionFactoryInterface#getUserTransaction()
     */
    public UserTransaction getUserTransaction() {  
        if (geronimoTcm != null) {           
            // TODO: any way to get a UserTransaction or will this do?
            return (UserTransaction) geronimoTcm.getTransactionManager();
        } else {
            Debug.logError("Cannot get UserTransaction, geronimoTcm object is null", module);
            return null;
        }
    }                
    
    public String getTxMgrName() {
        return "geronimo";
    }
    
    public Connection getConnection(String helperName) throws SQLException, GenericEntityException {
        DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperName);

        if (datasourceInfo != null && datasourceInfo.inlineJdbcElement != null) {
            try {
                Connection con = MinervaConnectionFactory.getConnection(helperName, datasourceInfo.inlineJdbcElement);
                if (con != null) return con;
            } catch (Exception ex) {
                Debug.logError(ex, "Geronimo is the configured transaction manager but there was an error getting a database Connection through Geronimo for the " + helperName + " datasource. Please check your configuration, class path, etc.", module);
            }
        
            Connection otherCon = ConnectionFactory.tryGenericConnectionSources(helperName, datasourceInfo.inlineJdbcElement);
            return otherCon;
        } else {            
            Debug.logError("Geronimo is the configured transaction manager but no inline-jdbc element was specified in the " + helperName + " datasource. Please check your configuration", module);
            return null;
        }
    }
    
    public void shutdown() {
        MinervaConnectionFactory.closeAll();
        if (geronimoTcm != null) {
            // TODO: need to do anything for this?
            geronimoTcm = null;
        }           
    }
}
