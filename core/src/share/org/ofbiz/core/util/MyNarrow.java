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

/**
 * Usefull class to swap from WLS 5.1 to jBoss
 *
 * @author Thierry Janaudy
 * @date April, 3rd, 2000
 */

//
// There is no need for Portable RemoteObject for jBoss
//import javax.rmi.PortableRemoteObject;
import javax.naming.*;

public final class MyNarrow 
{
  /**
   * Using ejboss
   */
  public static final boolean jBoss = true;

  /**
   * instance
   */
  private static final MyNarrow instance = new MyNarrow();

  /**
   * Private constructor
   */
  private MyNarrow() {
  }

  /**
   * Return the instance
   * @return instance
   */
  public static MyNarrow getInstance() {
    return instance;
  }

  /**
   * Get back the lookup object
   * @param ic InitialContext
   * @param jndiname
   * @return the Object
   */
  public static Object lookup(InitialContext ic, String jndiName) throws Exception
  {
    Debug.logInfo("Looking up initial context "+ ic +" with JNDI name " + jndiName);
    return ic.lookup(jndiName);
  }

  /**
   * narrow stuff
   */
  public static Object narrow(Object obj, Class c) throws Exception
  {
    //Debug.logInfo("Narrowing object "+ obj +" of class " + c);
    return obj;
  }
}
