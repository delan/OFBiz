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

package org.ofbiz.core.util;


import javax.naming.*;


public final class MyNarrow {

    /**
     * instance
     */
    private static final MyNarrow instance = new MyNarrow();

    /**
     * Private constructor
     */
    private MyNarrow() {}

    /**
     * Return the instance
     * @return instance
     */
    public static MyNarrow getInstance() {
        return instance;
    }

    /**
     * Get back the lookup object
     * @param ic InitialContext
     * @param jndiname
     * @return the Object
     */
    public static Object lookup(InitialContext ic, String jndiName) throws Exception {
        if (Debug.infoOn()) Debug.logInfo("Looking up initial context " + ic + " with JNDI name " + jndiName);
        return ic.lookup(jndiName);
    }

    /**
     * narrow stuff
     */
    public static Object narrow(Object obj, Class c) throws Exception {
        // if (Debug.infoOn()) Debug.logInfo("Narrowing object "+ obj +" of class " + c);
        return obj;
    }
}
