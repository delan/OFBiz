/*
 * $Id$
 * $Log$
 *
 */

package org.ofbiz.core.entity;

import java.io.*;

/**
 * <p><b>Title:</b> EventHandlerException.java
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
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created Sep 17, 2001
 *@version 1.0
 */
public class GenericEntityException extends Exception {
  Throwable nested = null;
  
  public GenericEntityException() {
    super();
  }

  public GenericEntityException(String str) {
    super(str);
  }

  public GenericEntityException(String str, Throwable nested) {
    super(str);
    this.nested = nested;
  }
  
  /** Returns the detail message, including the message from the nested exception if there is one. */
  public String getMessage() {
    return this.getMessage() + " (" + nested.getMessage() + ")";
  }
  
  /** Prints the composite message to System.err. */
  public void printStackTrace() {
    this.printStackTrace();
    nested.printStackTrace();
  }

  /** Prints the composite message and the embedded stack trace to the specified stream ps. */
  public void printStackTrace(PrintStream ps) {
    this.printStackTrace(ps);
    nested.printStackTrace(ps);
  }

  /** Prints the composite message and the embedded stack trace to the specified print writer pw. */
  public void printStackTrace(PrintWriter pw) {
    this.printStackTrace(pw);
    nested.printStackTrace(pw);
  }
}
