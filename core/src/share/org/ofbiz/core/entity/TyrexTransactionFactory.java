package org.ofbiz.core.entity;

import java.util.*;
import java.security.*;
import javax.naming.*;
import javax.transaction.*;

import org.ofbiz.core.util.*;
import tyrex.tm.Tyrex;
import tyrex.tm.TyrexPermission;
import tyrex.server.Configure;

/**
 * <p><b>Title:</b> TyrexTransactionFactory.java
 * <p><b>Description:</b> TyrexTransactionFactory - central source for Tyrex JTA objects
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
public class TyrexTransactionFactory {
    static {
        /* Any way to do this? doesn't look like it, must go in the java.policy file - pain in the rear
        TyrexPermission perm = new TyrexPermission("server.start");
        PermissionCollection permCol = perm.newPermissionCollection();
        if (permCol == null) permCol = new Permissions();
        permCol.add(perm);

        perm = new TyrexPermission("server.shutdown");
        permCol.add(perm);
        perm = new TyrexPermission("server.meter");
        permCol.add(perm);
        perm = new TyrexPermission("transaction.terminate");
        permCol.add(perm);
        perm = new TyrexPermission("transaction.list");
        permCol.add(perm);
        perm = new TyrexPermission("transaction.manager");
        permCol.add(perm);
        */
        Configure conf = new Configure();
        
        conf.setLogWriter(Debug.getPrintWriter());
        conf.startServer();
    }
    
    public static TransactionManager getTransactionManager() {
        return Tyrex.getTransactionManager();
    }
    
    public static UserTransaction getUserTransaction() {
        return Tyrex.getUserTransaction();
    }
}

