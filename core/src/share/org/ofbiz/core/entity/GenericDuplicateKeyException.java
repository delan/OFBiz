/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/09/20 14:56:20  jonesde
 * Added a bunch of improved error handling code.
 *
 *
 */

package org.ofbiz.core.entity;

import java.io.*;

/**
 * <p><b>Title:</b> GenericDuplicateKeyException.java
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
public class GenericDuplicateKeyException extends GenericEntityException {
  public GenericDuplicateKeyException() {
    super();
  }

  public GenericDuplicateKeyException(String str) {
    super(str);
  }

  public GenericDuplicateKeyException(String str, Throwable nested) {
    super(str, nested);
  }
}
