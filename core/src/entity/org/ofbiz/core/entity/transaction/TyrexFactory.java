/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.entity.transaction;


import java.net.*;
import java.util.*;
import java.security.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import javax.transaction.*;

import org.ofbiz.core.util.*;
import tyrex.tm.*;
import tyrex.resource.*;


/**
 * TyrexTransactionFactory - central source for Tyrex JTA objects
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version    1.0
 *@created    July 1, 2001, 5:03 PM
 */
public class TyrexFactory implements TransactionFactoryInterface {

    protected static TransactionDomain td = null;
    protected static String DOMAIN_NAME = "default";

    static {

        td = TransactionDomain.getDomain(DOMAIN_NAME);
        
        if (td == null) {
            //probably because there was no tyrexdomain.xml file, try another method:
            
            /* For Tyrex version 0.9.8.5 */
            try {
                String resourceName = "tyrexdomain.xml";
                URL url = UtilURL.fromResource(resourceName);

                if (url != null) {
                    td = TransactionDomain.createDomain(url.toString());
                } else {
                    Debug.logError("ERROR: Could not create Tyrex Transaction Domain (resource not found):" + resourceName);
                }
            } catch (tyrex.tm.DomainConfigurationException e) {
                Debug.logError("Could not create Tyrex Transaction Domain (configuration):");
                Debug.logError(e);
            }
            
            if (td != null) {
                Debug.logImportant("Got TyrexDomain from classpath (NO tyrex.config file found)");
            }
        } else {
            Debug.logImportant("Got TyrexDomain from tyrex.config location");
        }
        
        if (td != null) {
            try {
                td.recover();
            } catch (tyrex.tm.RecoveryException e) {
                Debug.logError("Could not complete recovery phase of Tyrex TransactionDomain creation");
                Debug.logError(e);
            }
        } else {
            Debug.logError("Could not get Tyrex TransactionDomain for domain " +  DOMAIN_NAME);
        }
        /* For Tyrex version 0.9.7.0 * /
         tyrex.resource.ResourceLimits rls = new tyrex.resource.ResourceLimits();
         td = new TransactionDomain("ofbiztx", rls);
         */
    }

    public static Resources getResources() {
        if (td != null) {
            return td.getResources();
        } else {
            Debug.logWarning("No Tyrex TransactionDomain, not returning resources");
            return null;
        }
    }
    
    public static DataSource getDataSource(String dsName) {
        Resources resources = getResources();
        if (resources != null) {
            try {
                return (DataSource) resources.getResource(dsName);
            } catch (tyrex.resource.ResourceException e) {
                Debug.logError(e, "Could not get tyrex dataSource resource with name " + dsName);
                return null;
            }
        } else {
            return null;
        }
    }
    
    public TransactionManager getTransactionManager() {
        if (td != null) {
            return td.getTransactionManager();
        } else {
            Debug.logWarning("No Tyrex TransactionDomain, not returning TransactionManager");
            return null;
        }
    }

    public UserTransaction getUserTransaction() {
        if (td != null) {
            return td.getUserTransaction();
        } else {
            Debug.logWarning("No Tyrex TransactionDomain, not returning UserTransaction");
            return null;
        }
    }
}
