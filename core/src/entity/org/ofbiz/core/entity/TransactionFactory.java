/*
 * $Id$
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
 */

package org.ofbiz.core.entity;

import java.util.*;
import javax.naming.*;
import javax.transaction.*;
import org.w3c.dom.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.config.*;
import org.ofbiz.core.entity.transaction.TransactionFactoryInterface;

/**
 * TransactionFactory - central source for JTA objects
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    1.0
 * @created    December 2001
 */
public class TransactionFactory {

    public static TransactionFactoryInterface transactionFactory = null;

    public static TransactionFactoryInterface getTransactionFactory() {
        if (transactionFactory == null) { //don't want to block here
            synchronized (TransactionFactory.class) {
                //must check if null again as one of the blocked threads can still enter
                if (transactionFactory == null) {
                    try {
                        String className = EntityConfigUtil.getTxFactoryClass();
                        if (className == null) {
                            throw new IllegalStateException("Could not find transaction factory class name definition");
                        }
                        Class tfClass = null;
                        if (className != null && className.length() > 0) {
                            try {
                                tfClass = Class.forName(className);
                            } catch (ClassNotFoundException e) {
                                Debug.logWarning(e);
                                throw new IllegalStateException("Error loading TransactionFactory class \"" + className + "\": " + e.getMessage());
                            }
                        }

                        try {
                            transactionFactory = (TransactionFactoryInterface) tfClass.newInstance();
                        } catch (IllegalAccessException e) {
                            Debug.logWarning(e);
                            throw new IllegalStateException("Error loading TransactionFactory class \"" + className + "\": " + e.getMessage());
                        } catch (InstantiationException e) {
                            Debug.logWarning(e);
                            throw new IllegalStateException("Error loading TransactionFactory class \"" + className + "\": " + e.getMessage());
                        }
                    } catch (SecurityException e) {
                        Debug.logError(e);
                        throw new IllegalStateException("Error loading TransactionFactory class: " + e.getMessage());
                    }
                }
            }
        }
        return transactionFactory;
    }

    public static TransactionManager getTransactionManager() {
        return getTransactionFactory().getTransactionManager();
    }

    public static UserTransaction getUserTransaction() {
        return getTransactionFactory().getUserTransaction();
    }
}
