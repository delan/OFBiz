package org.ofbiz.core.entity;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Entity Helper Factory Class
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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public class GenericHelperFactory {
  static UtilCache helperCache = new UtilCache("GenericHelpers", 0, 0);
  
  public static GenericHelper getHelper(String helperName) {
    GenericHelper helper = (GenericHelper)helperCache.get(helperName);
    if(helper == null) //don't want to block here
    {
      synchronized(GenericHelperFactory.class) {
        //must check if null again as one of the blocked threads can still enter
        helper = (GenericHelper)helperCache.get(helperName);
        if(helper == null) {
          try {
            String helperClassName = UtilProperties.getPropertyValue("entityengine", helperName + ".helper.class", "org.ofbiz.core.entity.GenericHelperDAO");
            Class helperClass = null;
            if(helperClassName != null && helperClassName.length() > 0) {
              try { helperClass = Class.forName(helperClassName); }
              catch(ClassNotFoundException e) { Debug.logWarning(e); return null; }
            }
            
            Class[] paramTypes = new Class[] {String.class};
            Object[] params = new Object[] {helperName};
            
            java.lang.reflect.Constructor helperConstructor = null;
            if(helperClass != null) {
              try { helperConstructor = helperClass.getConstructor(paramTypes); }
              catch(NoSuchMethodException e) { Debug.logWarning(e); return null; }
            }
            try { helper = (GenericHelper)helperConstructor.newInstance(params); }
            catch(IllegalAccessException e) { Debug.logWarning(e); return null; }
            catch(InstantiationException e) { Debug.logWarning(e); return null; }
            catch(java.lang.reflect.InvocationTargetException e) { Debug.logWarning(e); return null; }
            
            if(helper != null) helperCache.put(helperName, helper);
          }
          catch(SecurityException e) { Debug.logError(e); }
        }
      }
    }
    return helper;
  }
}
