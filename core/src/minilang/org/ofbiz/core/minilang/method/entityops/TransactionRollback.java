/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.minilang.method.entityops;

import org.w3c.dom.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Rolls back a transaction if beganTransaction is true, otherwise tries to do a setRollbackOnly.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class TransactionRollback extends MethodOperation {
    
    String beganTransactionName;

    public TransactionRollback(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        beganTransactionName = element.getAttribute("began-transaction-name");
        if (UtilValidate.isEmpty(beganTransactionName)) {
            beganTransactionName = "beganTransaction";
        }
    }

    public boolean exec(MethodContext methodContext) {
        boolean beganTransaction = false;
        
        Boolean beganTransactionBoolean = (Boolean) methodContext.getEnv(beganTransactionName);
        if (beganTransactionBoolean != null) {
            beganTransaction = beganTransactionBoolean.booleanValue();
        }
        
        try {
            TransactionUtil.rollback(beganTransaction);
        } catch (GenericTransactionException e) {
            Debug.logError(e, "Could not rollback transaction in simple-method, returning error.");
            
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [error rolling back a transaction: " + e.getMessage() + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        }
        
        methodContext.removeEnv(beganTransactionName);
        return true;
    }
}
