/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.view;

import java.sql.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import jimm.datavision.*;
import jimm.datavision.sql.*;

/**
 * Custom DataVision Database class for OFBiz connections
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    July 9, 2002
 *@version    1.0
 */
public class DataVisionDatabase extends jimm.datavision.sql.Database {
    protected String datasourceName;
    
    /** Creates a new instance of DataVisionDatabase */
    public DataVisionDatabase(String datasourceName, Report report)
    throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, UserCancellationException {
        super("", "", report, "", "");
        this.datasourceName = datasourceName;
        if (Debug.infoOn()) Debug.logVerbose("For DataVision using datasourceName: " + this.datasourceName);

        if (conn != null) {
            conn.close();
            conn = null;
        }
        initializeConnection();
        //not loading all tables, not needed for just running a report
        //loadAllTables();
    }
    
    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }
    
    /**
     * Initializes the connection to the database.
     *
     * @return a connection to the database
     */
    public void initializeConnection()
    throws ClassNotFoundException, InstantiationException, IllegalAccessException, UserCancellationException {
        
        if (Debug.infoOn()) Debug.logVerbose("For DataVision using datasourceName: " + this.datasourceName);
        //very simple method for this inherited class: just call get connection with the datasourceName
        try {
            this.conn = ConnectionFactory.getConnection(this.datasourceName);
        } catch (SQLException e) {
            Debug.logError(e, "Error getting database connection for DataVision report");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting database connection for DataVision report");
        }
    }
    
    /**
     * Loads information about all tables in the database. If no tables are
     * found when using the database schema name, try again with a
     * <code>null</code> schema name.
     */
    protected void loadAllTables() throws SQLException {
        if (datasourceName == null) {
            return;
        } else {
            Debug.logInfo("Getting all table info");
            super.loadAllTables();
        }
    }
}
