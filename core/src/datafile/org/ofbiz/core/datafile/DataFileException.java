/*
 * $Id$
 * $Log$
 * Revision 1.1  2002/02/14 05:16:58  jonesde
 * Moved datafile and minilang to separate src trees and jars
 *
 * Revision 1.2  2002/02/02 19:50:30  azeneski
 * formatting changes (120cols)
 *
 * Revision 1.1  2001/11/16 14:12:02  jonesde
 * Initial checkin of datafile stuff
 *
 *
 */

package org.ofbiz.core.datafile;


import java.io.*;

import org.ofbiz.core.util.GeneralException;


/**
 * <p><b>Title:</b> DataFileException
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
public class DataFileException extends GeneralException {

    public DataFileException() {
        super();
    }

    public DataFileException(String str) {
        super(str);
    }

    public DataFileException(String str, Throwable nested) {
        super(str, nested);
    }
}
