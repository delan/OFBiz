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


import javax.naming.*;
import javax.transaction.*;
import org.w3c.dom.Element;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.entity.config.*;
import org.ofbiz.core.util.*;


/**
 * Central source for Tyrex JTA objects from JNDI
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version    1.0
 *@created    July 1, 2001, 5:03 PM
 */
public class JNDIFactory implements TransactionFactoryInterface {
    //Debug module name
    public static final String module = JNDIFactory.class.getName();

    static TransactionManager transactionManager = null;
    static UserTransaction userTransaction = null;

    public TransactionManager getTransactionManager() {
        if (transactionManager == null) {
            synchronized (JNDIFactory.class) {
                //try again inside the synch just in case someone when through while we were waiting
                if (transactionManager == null) {
                    try {
                        String jndiName = EntityConfigUtil.getTxFactoryTxMgrJndiName();
                        String jndiServerName = EntityConfigUtil.getTxFactoryTxMgrJndiServerName();
                        if (jndiName != null && jndiName.length() > 0) {
                            //Debug.logVerbose("[JNDIFactory.getTransactionManager] Trying JNDI name " + jndiName, module);

                            try {
                                InitialContext ic = JNDIContextFactory.getInitialContext(jndiServerName);

                                if (ic != null) {
                                    transactionManager = (TransactionManager) ic.lookup(jndiName);
                                }
                            } catch (NamingException ne) {
                                transactionManager = null;
                            }
                            if (transactionManager == null) {
                                Debug.logWarning("[JNDIFactory.getTransactionManager] Failed to find TransactionManager named " + jndiName + " in JNDI.", module);
                            }
                        }
                    } catch (GeneralException e) {
                        Debug.logError(e);
                        transactionManager = null;
                    }
                }
            }
        }
        return transactionManager;
    }

    public UserTransaction getUserTransaction() {
        if (userTransaction == null) {
            synchronized (JNDIFactory.class) {
                //try again inside the synch just in case someone when through while we were waiting
                if (userTransaction == null) {
                    try {
                        String jndiName = EntityConfigUtil.getTxFactoryUserTxJndiName();
                        String jndiServerName = EntityConfigUtil.getTxFactoryUserTxJndiServerName();
                        if (jndiName != null && jndiName.length() > 0) {
                            //Debug.logVerbose("[JNDIFactory.getTransactionManager] Trying JNDI name " + jndiName, module);

                            try {
                                InitialContext ic = JNDIContextFactory.getInitialContext(jndiServerName);

                                if (ic != null) {
                                    userTransaction = (UserTransaction) ic.lookup(jndiName);
                                }
                            } catch (NamingException ne) {
                                userTransaction = null;
                            }
                            if (userTransaction == null) {
                                Debug.logWarning("[JNDIFactory.getUserTransaction] Failed to find UserTransaction named " + jndiName + " in JNDI.", module);
                            }
                        }
                    } catch (GeneralException e) {
                        Debug.logError(e);
                        transactionManager = null;
                    }
                }
            }
        }
        return userTransaction;
    }
}
