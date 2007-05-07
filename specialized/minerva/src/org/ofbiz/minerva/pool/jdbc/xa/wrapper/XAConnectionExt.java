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

package org.ofbiz.minerva.pool.jdbc.xa.wrapper;

import org.ofbiz.minerva.pool.PoolEvent;

import javax.sql.XAConnection;
import java.sql.SQLException;

/**
 * XAConnectionExt
 */
public interface XAConnectionExt extends XAConnection {

    public java.lang.String getPassword();
    public java.lang.String getUser();
    
    public void rollback() throws SQLException;
            
    public void transactionFinished();
    public void transactionFailed();

    public void setTransactionIsolation(int iso) throws SQLException;
    public void setPSCacheSize(int maxSize);
    public int getPSCacheSize();
    
    public void setTransactionListener(TransactionListener tl);
    public void clearTransactionListener();
    public void forceClientConnectionsClose();   
    public void setConnectionError(SQLException e);

    public XAResourceImpl getXAResourceImpl();

    public void clientConnectionClosed(XAClientConnection clientCon);    
    public void firePoolEvent(PoolEvent evt);
}
