package org.ofbiz.core.entity;

import java.util.*;
import javax.naming.*;
import javax.transaction.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> TransactionFactory.java
 * <p><b>Description:</b> TransactionFactory - central source for JTA objects
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
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on July 1, 2001, 5:03 PM
 */
public class TransactionFactory {
    public static TransactionManager getTransactionManager() {
        return TyrexTransactionFactory.getTransactionManager();
    }
    
    public static UserTransaction getUserTransaction() {
        return TyrexTransactionFactory.getUserTransaction();
    }
}

