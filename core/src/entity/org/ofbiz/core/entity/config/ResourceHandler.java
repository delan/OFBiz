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
 * Contains resource information and provides for loading data
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    March 1, 2002
 *@version    1.0
 */
public class ResourceHandler {
    
    protected String loaderName;
    protected String location;
    
    public ResourceHandler(Element element) {
        this.loaderName = element.getAttribute("loader");
        this.location = element.getAttribute("location");
    }
    
    public ResourceHandler(String loaderName, String location) {
        this.loaderName = loaderName;
        this.location = location;
    }
    
    public String getLoaderName() { return this.loaderName; }
    public String getLocation() { return this.location; }
    
    public Document getDocument() throws GenericEntityConfException {
        try {
            return UtilXml.readXmlDocument(this.getStream());
        } catch (org.xml.sax.SAXException e) {
            throw new GenericEntityConfException("Error reading " + this.toString(), e);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new GenericEntityConfException("Error reading " + this.toString(), e);
        } catch (java.io.IOException e) {
            throw new GenericEntityConfException("Error reading " + this.toString(), e);
        }
    }
    
    public InputStream getStream() throws GenericEntityConfException {
        return ResourceLoader.loadResource(this.location, this.loaderName);
    }
    
    public boolean isFileResource() throws GenericEntityConfException {
        ResourceLoader loader = ResourceLoader.getLoader(loaderName);
        if (loader instanceof FileLoader) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getFullLocation() throws GenericEntityConfException {
        ResourceLoader loader = ResourceLoader.getLoader(loaderName);
        return loader.fullLocation(location);
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof ResourceHandler) {
            ResourceHandler other = (ResourceHandler) obj;
            if (this.loaderName.equals(other.loaderName) &&
                    this.location.equals(other.location)) {
                return true;
            }
        }
        return false;
    }
    
    public int hashCode() {
        return (this.loaderName.hashCode() + this.location.hashCode()) >> 1;
    }
    
    public String toString() {
        return "ResourceHandler with loaderName [" + loaderName + "] and location [" + location + "]";
    }
}
