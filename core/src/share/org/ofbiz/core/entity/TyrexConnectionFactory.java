package org.ofbiz.core.entity;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;

import org.ofbiz.core.util.*;

// For Tyrex 0.9.8.5
import tyrex.resource.jdbc.xa.*;
// For Tyrex 0.9.7.0
//import tyrex.jdbc.xa.*;

/**
 * <p><b>Title:</b> TyrexConnectionFactory.java
 * <p><b>Description:</b> Tyrex ConnectionFactory - central source for JDBC connections from Tyrex
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on Dec 18, 2001, 5:03 PM
 */
public class TyrexConnectionFactory {
    static UtilCache dsCache = new UtilCache("TyrexDataSources", 0, 0);

    public static Connection getConnection(String helperName) throws SQLException, GenericEntityException {
        boolean usingTyrex = true;
        try {
            // For Tyrex 0.9.8.5
            Class.forName("tyrex.resource.jdbc.xa.EnabledDataSource").newInstance();
            // For Tyrex 0.9.7.0
            //Class.forName("tyrex.jdbc.xa.EnabledDataSource").newInstance();
            //Debug.logInfo("Found Tyrex Driver...");
        } catch (Exception ex) {
            usingTyrex = false;
        }

        if (usingTyrex) {
            EnabledDataSource ds;

            //try once
            ds = (EnabledDataSource) dsCache.get(helperName);
            if (ds != null) {
                return TransactionUtil.enlistConnection(ds.getXAConnection());
            }

            synchronized (TyrexConnectionFactory.class) {
                //try again inside the synch just in case someone when through while we were waiting
                ds = (EnabledDataSource) dsCache.get(helperName);
                if (ds != null) {
                    return TransactionUtil.enlistConnection(ds.getXAConnection());
                }

                ds = new EnabledDataSource();
                ds.setDriverClassName(UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.driver"));
                ds.setDriverName(UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.uri"));
                ds.setUser(UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.username"));
                ds.setPassword(UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.password"));
                ds.setDescription(helperName);

                String transIso = UtilProperties.getPropertyValue("entityengine", helperName + ".isolation.level");
                if (transIso != null && transIso.length() > 0)
                    ds.setIsolationLevel(transIso);
                    
                ds.setLogWriter(Debug.getPrintWriter());

                dsCache.put(helperName, ds);
                return TransactionUtil.enlistConnection(ds.getXAConnection());
            }
        }

        return null;
    }
}



