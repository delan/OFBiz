package org.ofbiz.commonapp.common;

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
  /**
   * Constructor
   */
  public JNDIContext () {}

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
      h.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
      h.put(Context.PROVIDER_URL, UtilProperties.getPropertyValue("ejbserver", "jndi.context.provider.url", "127.0.0.1\\:1099"));
      h.put(Context.URL_PKG_PREFIXES, "org.jnp.interfaces");

      ic = new InitialContext(h);
    } 
    catch (Exception e) 
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true")) e.printStackTrace();
    }

    return ic;
  }
}
