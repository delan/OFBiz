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
 * Begins a transaction if one is not already in place; if does begin one puts true in the began-transaction-name env variable, otherwise it returns false.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class TransactionBegin extends MethodOperation {
    
    String beganTransactionName;

    public TransactionBegin(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        beganTransactionName = element.getAttribute("began-transaction-name");
        if (UtilValidate.isEmpty(beganTransactionName)) {
            beganTransactionName = "beganTransaction";
        }
    }

    public boolean exec(MethodContext methodContext) {
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
        } catch (GenericTransactionException e) {
            Debug.logError(e, "Could not begin transaction in simple-method, returning error.");
            
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [error beginning a transaction: " + e.getMessage() + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        }
        
        methodContext.putEnv(beganTransactionName, new Boolean(beganTransaction));
        return true;
    }
}
