/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.io.PrintWriter;
import java.io.PrintStream;

/**
 * <p><b>Title:</b> WfException.java
 * <p><b>Description:</b> Generic Workflow Exception
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
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    October 29, 2001
 *@version    1.0
 */

public class WfException extends Exception {

    Throwable nested = null;

    /**
     * Creates new <code>WfException</code> without detail message.
     */
    public WfException() {
        super();
    }

    /**
     * Constructs an <code>WfException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public WfException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>WfException</code> with the specified detail message and nested exception.
     * @param msg the detail message.
     */
    public WfException(String msg, Throwable nested) {
        super(msg);
        this.nested = nested;
    }

    /** Returns the detail message, including the message from the nested exception if there is one. */
    public String getMessage() {
        if (nested != null)
            return super.getMessage() + " (" + nested.getMessage() + ")";
        else
            return super.getMessage();
    }

    /** Prints the composite message to System.err. */
    public void printStackTrace() {
        super.printStackTrace();
        if (nested != null)
            nested.printStackTrace();
    }

    /** Prints the composite message and the embedded stack trace to the specified stream ps. */
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (nested != null)
            nested.printStackTrace(ps);
    }

    /** Prints the composite message and the embedded stack trace to the specified print writer pw. */
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (nested != null)
            nested.printStackTrace(pw);
    }
}

