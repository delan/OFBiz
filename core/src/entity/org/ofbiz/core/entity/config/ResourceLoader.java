/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.ofbiz.core.entity.config;

import java.util.*;
import java.net.*;
import java.io.*;
import org.w3c.dom.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * Loads resources using dynamically specified resource loader classes
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    March 1, 2002
 *@version    1.0
 */
public abstract class ResourceLoader {
    protected static UtilCache loaderCache = new UtilCache("ResourceLoaders", 0, 0);

    protected String name;
    protected String prefix;
    protected String envName;
    
    public static InputStream loadResource(String location, String loaderName) throws GenericEntityConfException {
        ResourceLoader loader = getLoader(loaderName);
        if (loader == null) throw new IllegalArgumentException("ResourceLoader not found with name [" + loaderName + "] in entityengine.xml");
        return loader.loadResource(location);
    }
    
    public static ResourceLoader getLoader(String loaderName) throws GenericEntityConfException {
        ResourceLoader loader = (ResourceLoader) loaderCache.get(loaderName);
        if (loader == null) {
            synchronized (ResourceLoader.class) {
                loader = (ResourceLoader) loaderCache.get(loaderName);
                if (loader == null) {
                    Element rootElement = EntityConfigUtil.getXmlRootElement();

                    Element loaderElement = UtilXml.firstChildElement(rootElement, "resource-loader", "name", loaderName);
                    loader = makeLoader(loaderElement);

                    if (loader != null) {
                        loaderCache.put(loaderName, loader);
                    }
                }
            }
        }
        
        return loader;
    }
    
    public static ResourceLoader makeLoader(Element loaderElement) throws GenericEntityConfException {
        if (loaderElement == null)
            return null;
        
        String loaderName = loaderElement.getAttribute("name");
        String className = loaderElement.getAttribute("class");
        ResourceLoader loader = null;
        
        try {
            Class lClass = null;
            if (className != null && className.length() > 0) {
                try {
                    lClass = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new GenericEntityConfException("Error loading Resource Loader class \"" + className + "\"", e);
                }
            }

            try {
                loader = (ResourceLoader) lClass.newInstance();
            } catch (IllegalAccessException e) {
                throw new GenericEntityConfException("Error loading Resource Loader class \"" + className + "\"", e);
            } catch (InstantiationException e) {
                throw new GenericEntityConfException("Error loading Resource Loader class \"" + className + "\"", e);
            }
        } catch (SecurityException e) {
            throw new GenericEntityConfException("Error loading Resource Loader class \"" + className + "\"", e);
        }

        if (loader != null) {
            loader.init(loaderName, loaderElement.getAttribute("prefix"), loaderElement.getAttribute("prepend-env"));
        }
        
        return loader;
    }
    
    public ResourceLoader() { }
    
    public void init(String name, String prefix, String envName) {
        this.name = name;
        this.prefix = prefix;
        this.envName = envName;
    }

    /** Just a utility method to be used in loadResource by the implementing class */
    public String fullLocation(String location) {
        StringBuffer buf = new StringBuffer();
        if (envName != null && envName.length() > 0) {
            buf.append(System.getProperty(envName));
        }
        if (prefix != null && prefix.length() > 0) {
            buf.append(prefix);
        }
        buf.append(location);
        return buf.toString();
    }
    
    public abstract InputStream loadResource(String location) throws GenericEntityConfException;
}
