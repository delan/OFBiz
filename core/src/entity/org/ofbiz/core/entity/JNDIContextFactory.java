/*
 * $Id$
 *
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
 */

package org.ofbiz.core.entity;

import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.ofbiz.core.util.*;

/**
 * JNDIContextFactory - central source for JNDI Contexts by helper name
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on Sep 21, 2001
 */
public class JNDIContextFactory {
    static UtilCache contexts = new UtilCache("JNDIContexts", 0, 0);

    /** Return the initial context according to the entityengine.properties parameters that correspond to the given prefix
     * @return the JNDI initial context
     */
    public static InitialContext getInitialContext(String prefix) {
        InitialContext ic = (InitialContext) contexts.get(prefix);
        
        if (ic == null) {
            synchronized (JNDIContextFactory.class) {
                ic = (InitialContext) contexts.get(prefix);

                if (ic == null) {
                    String providerUrl = UtilProperties.getPropertyValue("entityengine", prefix + ".context.provider.url");
                    String contextFactory = UtilProperties.getPropertyValue("entityengine", prefix + ".initial.context.factory");
                    String pkgPrefix = UtilProperties.getPropertyValue("entityengine", prefix + ".url.pkg.prefixes");

                    String secPrincipal = UtilProperties.getPropertyValue("entityengine", prefix + ".security.principal");
                    String secCred = UtilProperties.getPropertyValue("entityengine", prefix + ".security.credentials");

                    try {
                        if (providerUrl == null || providerUrl.length() == 0) {
                            ic = new InitialContext();
                        } else {
                            Hashtable h = new Hashtable();
                            h.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
                            h.put(Context.PROVIDER_URL, providerUrl);
                            if (pkgPrefix != null && pkgPrefix.length() > 0)
                                h.put(Context.URL_PKG_PREFIXES, pkgPrefix);

                            if (secPrincipal != null && secPrincipal.length() > 0)
                                h.put(Context.SECURITY_PRINCIPAL, secPrincipal);
                            if (secCred != null && secCred.length() > 0)
                                h.put(Context.SECURITY_CREDENTIALS, secCred);

                            ic = new InitialContext(h);
                        }
                    } catch (Exception e) {
                        Debug.logError(e);
                    }

                    if (ic != null) {
                        contexts.put(prefix, ic);
                    }
                }
            }
        }

        return ic;
    }
}
