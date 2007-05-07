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

import org.ofbiz.minerva.pool.jdbc.xa.XAConnectionFactory;
import org.ofbiz.minerva.pool.jdbc.xa.wrapper.XAConnectionImpl;
import org.apache.log4j.Logger;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.SQLException;
import java.io.PrintWriter;

/**
 * SpyDataSource
 */
public class SpyDataSource implements XADataSource {

    private Logger log = Logger.getLogger(SpyDataSource.class);
    private XAConnectionFactory factory;
    private XADataSource ds;

    public SpyDataSource(XAConnectionFactory factory, XADataSource ds) {
        this.factory = factory;
        this.ds = ds;
    }

    public XAConnection getXAConnection() throws SQLException {
        XAConnection xac = ds.getXAConnection();
        if (xac instanceof XAConnectionImpl) {
            log.debug("returning new SpyXAConnection");
            return new SpyXAConnection((XAConnectionImpl) xac, factory);
        } else {
            return xac;
        }        
    }

    public XAConnection getXAConnection(String string, String string1) throws SQLException {
        XAConnection xac = ds.getXAConnection(string, string1);
        if (xac instanceof XAConnectionImpl) {
            log.debug("returning new SpyXAConnection");
            return new SpyXAConnection((XAConnectionImpl) xac, factory);
        } else {
            return xac;
        }            
    }

    public PrintWriter getLogWriter() throws SQLException {
        return ds.getLogWriter();
    }

    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        ds.setLogWriter(printWriter);
    }

    public void setLoginTimeout(int i) throws SQLException {
        ds.setLoginTimeout(i);
    }

    public int getLoginTimeout() throws SQLException {
        return ds.getLoginTimeout();
    }
}
