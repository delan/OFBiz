package org.ofbiz.core.entity;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> ConnectionFactory.java
 * <p><b>Description:</b> ConnectionFactory - central source for JDBC connections
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
 * Created on July 1, 2001, 5:03 PM
 */
public class ConnectionFactory {
    static UtilCache dsCache = new UtilCache("JNDIDataSources", 0, 0);

    public static Connection getConnection(String helperName) throws SQLException {

        String jndiName = UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.jndi.name");
        if (jndiName != null && jndiName.length() > 0) {
            //Debug.logInfo("[ConnectionFactory.getConnection] Trying JNDI name " + jndiName);
            DataSource ds;
            ds = (DataSource) dsCache.get(jndiName);
            if (ds != null)
                return ds.getConnection();

            synchronized (ConnectionFactory.class) {
                //try again inside the synch just in case someone when through while we were waiting
                ds = (DataSource) dsCache.get(jndiName);
                if (ds != null)
                    return ds.getConnection();

                try {
                    InitialContext ic = JNDIContextFactory.getInitialContext(helperName);
                    if (ic != null)
                        ds = (DataSource) ic.lookup(jndiName);
                    if (ds != null) {
                        dsCache.put(jndiName, ds);
                        Connection con = ds.getConnection();
                        //Debug.logInfo("[ConnectionFactory.getConnection] Got JNDI connection with catalog: " + con.getCatalog());
                        return con;
                    }
                } catch (NamingException ne) {
                    // Debug.logWarning("[ConnectionFactory.getConnection] Failed to find DataSource named " + jndiName + " in JNDI. Trying normal database.");
                }
            }
        }

        // Try to use PoolMan Connection Pool.
        String poolManName = UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.poolman");
        //Debug.logInfo("[ConnectionFactory.getConnection] Attempting to connect to PoolMan pool with name '"+poolManName+"'");
        boolean usingPoolMan = false;
        if (poolManName != null && poolManName.length() > 0) {
            usingPoolMan = true;
            try {
                Class.forName("com.codestudio.sql.PoolMan").newInstance();
                //Debug.logInfo("Found PoolMan Driver...");
            } catch (Exception ex) {
                usingPoolMan = false;
            }

            if (usingPoolMan) {
                Connection con = DriverManager.getConnection("jdbc:poolman://" + poolManName);
                if (con != null) {
                    //Debug.logInfo("Connection to PoolMan established.");
                    return con;
                }
            }
            usingPoolMan = false;
        }

        //If not PoolMan or JNDI sources are specified, or found, try Tyrex
        Connection con = TyrexConnectionFactory.getConnection(helperName);
        if (con != null)
            return con;

        // Default to plain JDBC.
        String driverClassName = UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.driver");
        if (driverClassName != null && driverClassName.length() > 0) {
            try {
                Class.forName(driverClassName);
            } catch (ClassNotFoundException cnfe) {
                Debug.logWarning("Could not find JDBC driver class named " + driverClassName + ".\n");
                Debug.logWarning(cnfe);
                return null;
            }
            return DriverManager.getConnection(UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.uri"),
                    UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.username"),
                    UtilProperties.getPropertyValue("entityengine", helperName + ".jdbc.password"));
        }

        Debug.log("******* ERROR: No database connection found for helperName \"" + helperName + "\"");
        return null;
    }
}

