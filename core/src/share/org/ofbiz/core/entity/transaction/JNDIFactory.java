
package org.ofbiz.core.entity.transaction;

import javax.naming.*;
import javax.transaction.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Central source for Tyrex JTA objects from JNDI
 * <p><b>Description:</b> 
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
                    String jndiName = UtilProperties.getPropertyValue("entityengine", "transaction.factory.TransactionManager.jndi.name");
                    if (jndiName != null && jndiName.length() > 0) {
                        //Debug.logVerbose("[JNDIFactory.getTransactionManager] Trying JNDI name " + jndiName, module);

                        try {
                            InitialContext ic = JNDIContextFactory.getInitialContext("transaction.factory");
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
                    String jndiName = UtilProperties.getPropertyValue("entityengine", "transaction.factory.UserTransaction.jndi.name");
                    if (jndiName != null && jndiName.length() > 0) {
                        //Debug.logVerbose("[JNDIFactory.getTransactionManager] Trying JNDI name " + jndiName, module);

                        try {
                            InitialContext ic = JNDIContextFactory.getInitialContext("transaction.factory");
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
                }
            }
        }
        return userTransaction;
    }
}
