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
  public static GenericHelper getDefaultHelper()
  {
    if(UtilProperties.propertyValueEqualsIgnoreCase("servers", "jdbc.uri", "jdbc"))
      return getJDBCHelper();
    else
      return getEJBHelper();
  }

  public static GenericHelper getJDBCHelper() { return new GenericHelperDAO(); }
  public static GenericHelper getJDBCHelper(String serverName) { return new GenericHelperDAO(serverName); }

  public static GenericHelper getEJBHelper() { return new GenericHelperEJB(); }
  public static GenericHelper getEJBHelper(String serverName) { return new GenericHelperEJB(serverName); }
}
