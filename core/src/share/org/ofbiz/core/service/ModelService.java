/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.util.*;
import java.lang.reflect.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Service Model Class
 * <p><b>Description:</b> None
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
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Oct 20 2001
 *@version    1.0
 */
public class ModelService {
    
  /** The name of this service */
  public String name;
  /** The name of the engine from engine.properties */
  public String engineName;
  /** The package name or location of this service */  
  public String location;
  /** The method or function to invoke for this service */
  public String invoke;
  /** Context Information, a list of parameters required by the service */
  public Map contextInfo;  
  /** Result Information, a list of values returned by the service */
  public Map resultInfo;
  
  /** Validates a map of name, object types to a map of name, objects
   * @param info The map of name, object types
   * @param test The map to test its value types.
   * @returns true if validation is successful
   */
  public static boolean validate(Map info, Map test) {
      // * Validate keys first
      Set testSet = test.keySet();
      Set keySet = info.keySet();
      
      // This test is to see if the keys of the test map match exactly
      if ( !testSet.containsAll(keySet) || !keySet.containsAll(testSet) )
          return false;
      
      // * Validate types next   
      // Warning - the class types MUST be accessible to this classloader            
      String DEFAULT_PACKAGE = "java.lang."; // We will test both the raw value and this + raw value
      Iterator i = keySet.iterator();
      while ( i.hasNext() ) {
          Object key = i.next();
          Object testObject = test.get(key);
          String infoType = (String) info.get(key);   
          Class infoClass = null;
          try {
              infoClass = ObjectType.loadClass(infoType);
          }
          catch ( SecurityException se ) {
              throw new RuntimeException("Problems with classloader");
          }
          catch ( ClassNotFoundException cnf ) {
              try {
                  infoClass = ObjectType.loadClass(DEFAULT_PACKAGE + infoType);
              }
              catch ( SecurityException se2 ) {
                  throw new RuntimeException("Problems with classloader");
              }
              catch ( ClassNotFoundException e ) {
                  throw new RuntimeException("Cannot load the type class of: " + infoType);
              }
          }
          
          if ( infoClass == null )
              throw new RuntimeException("Illegal type found in info map");
          
          if ( !ObjectType.instanceOf(testObject,infoClass) )
              return false;
      }
                
      return true;
  }
}
