/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.util;

import java.util.*;
import bsh.*;

/**
 * BshUtil - BeanShell Utilities
 *
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@author     Oswin Ondarza and Manuel Soto
 *@created    Oct 22, 2002
 *@version    $Revision$
 */
public final class BshUtil {

    public static final String module = BshUtil.class.getName();

    /**
     * Evaluate a BSH condition or expression
     * @param expression The expression to evaluate
     * @param context The context to use in evaluation (re-written)
     * @return Object The result of the evaluation 
     * @throws EvalError
     */
    public static final Object eval(String expression, Map context) throws EvalError {
        Interpreter bsh = new Interpreter();
        Object o = null;
        if (expression == null || expression.equals("")) {
            Debug.logError("BSH Evaluation error. Empty expression", module);
            return null;
        }

        if (Debug.verboseOn())
            Debug.logVerbose("Evaluating -- " + expression, module);
        if (Debug.verboseOn())
            Debug.logVerbose("Using Context -- " + context, module);
        try {
            // Set the context for the condition
            if (context != null) {
                Set keySet = context.keySet();
                Iterator i = keySet.iterator();
                while (i.hasNext()) {
                    Object key = i.next();
                    Object value = context.get(key);
                    bsh.set((String) key, value);
                }
            }
            // evaluate the expression
            o = bsh.eval(expression);
            if (Debug.verboseOn())
                Debug.logVerbose("Evaluated to -- " + o, module);

            // read back the context info
            NameSpace ns = bsh.getNameSpace();
            String[] varNames = ns.getVariableNames();
            for (int x = 0; x < varNames.length; x++) {
                context.put(varNames[x], bsh.get(varNames[x]));
            }
        } catch (EvalError e) {
            Debug.logError(e, "BSH Evaluation error.", module);
            throw e;
        }
        return o;
    }
}
