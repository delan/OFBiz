/*
 * $Id$
 * $Log$
 *
 */

package org.ofbiz.core.entity;

import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> JNDIContextFactory.java
 * <p><b>Description:</b> JNDIContextFactory - central source for JNDI Contexts by helper name
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
 * Created on Sep 21, 2001
 */
public final class JNDIContextFactory {
  static Map contexts = new Hashtable();
  
  /** Return the initial context according to the servers.properties parameters that correspond to the given helper name
   * @return the JNDI initial context
   */
  public static InitialContext getInitialContext(String helperName) {
    InitialContext ic = (InitialContext)contexts.get(helperName);
    
    if(ic == null) {
      String providerUrl;
      String contextFactory;
      String pkgPrefix;

      providerUrl = UtilProperties.getPropertyValue("servers", helperName + ".context.provider.url", "127.0.0.1:1099");
      contextFactory = UtilProperties.getPropertyValue("servers", helperName + ".initial.context.factory", "com.sun.jndi.rmi.registry.RegistryContextFactory");
      pkgPrefix = UtilProperties.getPropertyValue("servers", helperName + ".url.pkg.prefixes", "java.naming.rmi.security.manager");

      try {
        Hashtable h = new Hashtable();
        h.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        h.put(Context.PROVIDER_URL, providerUrl);
        h.put(Context.URL_PKG_PREFIXES, pkgPrefix);

        ic = new InitialContext(h);
      }
      catch (Exception e) {
        Debug.logWarning(e);
      }
      if(ic != null) contexts.put(helperName, ic);
    }
    
    return ic;
  }
}
