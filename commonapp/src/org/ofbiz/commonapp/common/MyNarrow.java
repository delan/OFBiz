package org.ofbiz.commonapp.common;

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
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("Looking up initial context "+ ic +" with JNDI name " + jndiName);
    return ic.lookup(jndiName);
  }

  /**
   * narrow stuff
   */
  public static Object narrow(Object obj, Class c) throws Exception
  {
    //if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("Narrowing object "+ obj +" of class " + c);
    return obj;
  }
}
