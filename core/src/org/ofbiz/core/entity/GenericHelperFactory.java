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
 *@author     David E. Jones
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public class GenericHelperFactory
{
  static UtilCache helperCache = new UtilCache("GenericHelper", 0, 0);
  public static GenericHelper getDefaultHelper()
  {
    String defaultName = UtilProperties.getPropertyValue("servers", "server.default.name", "default");
    return getDefaultHelper(defaultName);
  }

  public static GenericHelper getDefaultHelper(String serverName)
  {
    if(UtilProperties.propertyValueEqualsIgnoreCase("servers", "server.default.type", "ejb"))
      return getEJBHelper(serverName);
    //if no default type is specified, go with jdbc
    return getJDBCHelper(serverName);
  }

  public static GenericHelper getJDBCHelper() 
  {
    return getJDBCHelper(UtilProperties.getPropertyValue("servers", "server.default.name", "default")); 
  }
  public static GenericHelper getJDBCHelper(String serverName) 
  { 
    GenericHelper helper = (GenericHelper)helperCache.get(serverName + "JDBC");
    if(helper == null)
    {
      helper = new GenericHelperDAO(serverName);
      helperCache.put(serverName + "JDBC", helper);
    }
    return helper;
  }

  public static GenericHelper getEJBHelper() 
  { 
    return getEJBHelper(UtilProperties.getPropertyValue("servers", "server.default.name", "default")); 
  }
  public static GenericHelper getEJBHelper(String serverName) 
  { 
    GenericHelper helper = (GenericHelper)helperCache.get(serverName + "EJB");
    if(helper == null)
    {
      helper = new GenericHelperEJB(serverName);
      helperCache.put(serverName + "JDBC", helper);
    }
    return helper;
  }
}
