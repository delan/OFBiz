/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/10/13 22:37:37  jonesde
 * Added UtilURL and changed FlexibleProperties and UtilProperties to be URL-centric
 *
 *
 */

package org.ofbiz.core.util;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * <p><b>Title:</b> URL Utilities
 * <p><b>Description:</b> Simple Class for flexibly working with properties files
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
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Sep 22, 2001
 *@version    1.0
 */
public class UtilURL {
  public static URL fromClass(Class contextClass) {
    String resourceName=contextClass.getName();
    int dotIndex = resourceName.lastIndexOf('.');
    if(dotIndex!=-1) resourceName = resourceName.substring(0,dotIndex);
    resourceName += ".properties";

    return fromResource(contextClass, resourceName);
  }
  
  public static URL fromResource(String resourceName) {
    return fromResource(null, resourceName);
  }
  
  public static URL fromResource(Class contextClass, String resourceName) {
    URL url = null;
    if(contextClass != null && url == null) url = contextClass.getResource(resourceName);
    if(contextClass != null && url == null) url = contextClass.getResource(resourceName + ".properties");

    UtilURL utilURL = new UtilURL();
    Class utilURLClass = utilURL.getClass();
    
    if(url == null) url = utilURLClass.getClassLoader().getResource(resourceName);
    if(url == null) url = utilURLClass.getClassLoader().getResource(resourceName + ".properties");

    if(url == null) url = ClassLoader.getSystemResource(resourceName);
    if(url == null) url = ClassLoader.getSystemResource(resourceName + ".properties");

    if(url == null) url = fromFilename(resourceName);
    
    //Debug.log("[fromResource] got URL " + url + " from resourceName " + resourceName);
    return url;
  }
  
  public static URL fromFilename(String filename) {
    if(filename == null) return null;
    File file = new File(filename);
    URL url = null;
    try { if(file.exists()) url = file.toURL(); }
    catch(java.net.MalformedURLException e) { Debug.log(e); url = null; }
    return url;
  }
}
