/*
 * $Id$
 * $Log$
 * Revision 1.2  2001/07/18 22:22:53  jonesde
 * A few small changes to use the Debug class for logging instead of straight
 * System.out. Also added conditional logging for info, warning, and error messages
 * which are controlled through the debug.properties file.
 *
 * Revision 1.1  2001/07/16 14:45:48  azeneski
 * Added the missing 'core' directory into the module.
 *
 * Revision 1.1  2001/07/15 16:36:18  azeneski
 * Initial Import
 *
 */

package org.ofbiz.core.util;

/**
 * Licences stuff
 */

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Helper class to find the initial context
 *
 * @author Thierry Janaudy <janaudy@yahoo.com>
 * @date Sunday, February 20, 2000
 * @modified Friday, April 07, 2000
 */
public final class JNDIContext 
{
  String providerUrl;
  String contextFactory;
  String pkgPrefix;
  
  /** Defult Constructor */
  public JNDIContext() 
  {
    providerUrl = UtilProperties.getPropertyValue("servers", "default.ejb.context.provider.url", "127.0.0.1\\:1099");
    contextFactory = UtilProperties.getPropertyValue("servers", "default.ejb.initial.context.factory", "org.jnp.interfaces.NamingContextFactory");
    pkgPrefix = UtilProperties.getPropertyValue("servers", "default.ejb.url.pkg.prefixes", "org.jnp.interfaces");
  }

  /** Constructor user to specify the server name */
  public JNDIContext(String serverName)
  {
    this.providerUrl = UtilProperties.getPropertyValue("servers", serverName + ".ejb.context.provider.url", "127.0.0.1\\:1099");
    contextFactory = UtilProperties.getPropertyValue("servers", serverName + ".ejb.initial.context.factory", "org.jnp.interfaces.NamingContextFactory");
    pkgPrefix = UtilProperties.getPropertyValue("servers", serverName + ".ejb.url.pkg.prefixes", "org.jnp.interfaces");
  }
  
  /**
   * Return the initial context
   *
   * @return the JNDI initial context
   */
  public InitialContext getInitialContext() 
  {
    InitialContext ic =  null;

    try 
    {
      Hashtable h = new Hashtable();
      h.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
      h.put(Context.PROVIDER_URL, providerUrl);
      h.put(Context.URL_PKG_PREFIXES, pkgPrefix);

      ic = new InitialContext(h);
    } 
    catch (Exception e) 
    {
      Debug.logWarning(e);
    }

    return ic;
  }
}
