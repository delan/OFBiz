/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/07/23 18:38:14  azeneski
 * Added in new HttpClient. Makes behind the scenes HTTP request (GET/POST)
 * and returns the output as a string.
 *
 */

package org.ofbiz.core.util;

import java.io.*;

/**
 * <p><b>Title:</b> HttpClientException.java
 * <p><b>Description:</b> HttpClient Exception.
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on July 21, 2001, 1:08 PM
 */
public class HttpClientException extends java.lang.Exception {
  Throwable nested = null;
  
  public HttpClientException() {
    super();
  }

  public HttpClientException(String str) {
    super(str);
  }

  public HttpClientException(String str, Throwable nested) {
    super(str);
    this.nested = nested;
  }
  
  /** Returns the detail message, including the message from the nested exception if there is one. */
  public String getMessage() {
    if(nested != null) return super.getMessage() + " (" + nested.getMessage() + ")";
    else return super.getMessage();
  }
  
  /** Prints the composite message to System.err. */
  public void printStackTrace() {
    super.printStackTrace();
    if(nested != null) nested.printStackTrace();
  }

  /** Prints the composite message and the embedded stack trace to the specified stream ps. */
  public void printStackTrace(PrintStream ps) {
    super.printStackTrace(ps);
    if(nested != null) nested.printStackTrace(ps);
  }

  /** Prints the composite message and the embedded stack trace to the specified print writer pw. */
  public void printStackTrace(PrintWriter pw) {
    super.printStackTrace(pw);
    if(nested != null) nested.printStackTrace(pw);
  }
}


