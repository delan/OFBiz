package org.ofbiz.core.entity.transaction;

import java.net.*;
import java.util.*;
import java.security.*;
import javax.naming.*;
import javax.transaction.*;

import org.ofbiz.core.util.*;
import tyrex.tm.TransactionDomain;

/**
 * <p><b>Title:</b> TyrexFactory.java
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
public class TyrexFactory implements TransactionFactoryInterface {
    protected TransactionDomain td = null;
    
    public TyrexFactory() {
      /* For Tyrex version 0.9.8.5 */
        try {
            String resourceName = "tyrexdomain.xml";
            URL url = UtilURL.fromResource(resourceName);
            if (url != null) {
                td = TransactionDomain.createDomain(url.toString());
                td.recover();
            } else {
                Debug.logError("ERROR: Could not create Tyrex Transaction Domain (resource not found):" + resourceName);
            }
        } catch (tyrex.tm.DomainConfigurationException e) {
            Debug.logError("Could not create Tyrex Transaction Domain (configuration):");
            Debug.logError(e);
        } catch (tyrex.tm.RecoveryException e) {
            Debug.logError("Could not create Tyrex Transaction Domain (recovery):");
            Debug.logError(e);
        }

      /* For Tyrex version 0.9.7.0 * /
        tyrex.resource.ResourceLimits rls = new tyrex.resource.ResourceLimits();
        td = new TransactionDomain("ofbiztx", rls);
       */
    }

    public TransactionManager getTransactionManager() {
        if (td != null)
            return td.getTransactionManager();
        else
            return null;
    }
    
    public UserTransaction getUserTransaction() {
        if (td != null)
            return td.getUserTransaction();
        else
            return null;
    }
}
