/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.core.minilang.method.eventops;

import java.net.*;
import java.util.*;
import javax.servlet.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Copies a property value from a properties file in a ServletContext resource to a field
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class WebappPropertyToField extends MethodOperation {
    
    String resource;
    String property;
    String defaultVal;
    ContextAccessor mapAcsr;
    ContextAccessor fieldAcsr;

    public WebappPropertyToField(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        resource = element.getAttribute("resource");
        property = element.getAttribute("property");
        defaultVal = element.getAttribute("default");
        mapAcsr = new ContextAccessor(element.getAttribute("map-name"));
        fieldAcsr = new ContextAccessor(element.getAttribute("field-name"));
    }

    public boolean exec(MethodContext methodContext) {
        String resource = methodContext.expandString(this.resource);
        String property = methodContext.expandString(this.property);
        String defaultVal = methodContext.expandString(this.defaultVal);
                
        String fieldVal = null;

        // only run this if it is in an EVENT context
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            ServletContext servletContext = (ServletContext) methodContext.getRequest().getAttribute("servletContext");
            URL propsUrl = null;

            try {
                propsUrl = servletContext.getResource(resource);
            } catch (java.net.MalformedURLException e) {
                Debug.logWarning(e, "Error finding webapp resource (properties file) not found with name " + resource);
            }

            if (propsUrl == null) {
                Debug.logWarning("Webapp resource (properties file) not found with name " + resource);
            } else {
                fieldVal = UtilProperties.getPropertyValue(propsUrl, property);
                if (fieldVal == null || fieldVal.length() == 0) {
                    Debug.logWarning("Webapp resource property value not found with name " + property + " in resource " + resource);
                }
            }
        }

        // if fieldVal is null, or is a String and has zero length, use defaultVal
        if (fieldVal == null) {
            fieldVal = defaultVal;
        } else if (fieldVal instanceof String) {
            String strVal = (String) fieldVal;

            if (strVal.length() == 0) {
                fieldVal = defaultVal;
            }
        }

        if (!mapAcsr.isEmpty()) {
            Map fromMap = (Map) mapAcsr.get(methodContext);

            if (fromMap == null) {
                Debug.logWarning("Map not found with name " + mapAcsr + " creating a new map");
                fromMap = new HashMap();
                mapAcsr.put(methodContext, fromMap);
            }

            fieldAcsr.put(fromMap, fieldVal, methodContext);
        } else {
            fieldAcsr.put(methodContext, fieldVal);
        }
        return true;
    }
}
