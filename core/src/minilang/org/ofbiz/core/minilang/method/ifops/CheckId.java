/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.minilang.method.ifops;


import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;


/**
 * Iff the given ID field is not valid the fail-message 
 * or fail-property sub-elements are used to add a message to the error-list.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version    1.0
 */
public class CheckId extends MethodOperation {
    String message = null;
    String propertyResource = null;
    boolean isProperty = false;

    String fieldName;
    String mapName;
    String errorListName;

    public CheckId(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.fieldName = element.getAttribute("field-name");
        this.mapName = element.getAttribute("map-name");

        errorListName = element.getAttribute("error-list-name");
        if (errorListName == null || errorListName.length() == 0) {
            errorListName = "error_list";
        }

        //note: if no fail-message or fail-property then message will be null
        Element failMessage = UtilXml.firstChildElement(element, "fail-message");
        Element failProperty = UtilXml.firstChildElement(element, "fail-property");

        if (failMessage != null) {
            this.message = failMessage.getAttribute("message");
            this.isProperty = false;
        } else if (failProperty != null) {
            this.propertyResource = failProperty.getAttribute("resource");
            this.message = failProperty.getAttribute("property");
            this.isProperty = true;
        }
    }

    public boolean exec(MethodContext methodContext) {
        boolean isValid = true;

        List messages = (List) methodContext.getEnv(errorListName);

        if (messages == null) {
            messages = new LinkedList();
            methodContext.putEnv(errorListName, messages);
        }

        Object fieldVal = null;

        if (mapName != null && mapName.length() > 0) {
            Map fromMap = (Map) methodContext.getEnv(mapName);

            if (fromMap == null) {
                if (Debug.infoOn()) Debug.logInfo("Map not found with name " + mapName + ", running operations");
            } else {
                fieldVal = fromMap.get(fieldName);
            }
        } else {
            // no map name, try the env
            fieldVal = methodContext.getEnv(fieldName);
        }
        
        String fieldStr = fieldVal.toString();
        StringBuffer errorDetails = new StringBuffer();
        
        //check various illegal characters, etc for ids
        if (fieldStr.indexOf(' ') >= 0) {
            isValid = false;
            errorDetails.append("[space found at position " + (fieldStr.indexOf(' ') + 1) + "]");
        }
        if (fieldStr.indexOf('"') >= 0) {
            isValid = false;
            errorDetails.append("[double-quote found at position " + (fieldStr.indexOf('"') + 1) + "]");
        }
        if (fieldStr.indexOf('\'') >= 0) {
            isValid = false;
            errorDetails.append("[single-quote found at position " + (fieldStr.indexOf('\'') + 1) + "]");
        }
        if (fieldStr.indexOf('&') >= 0) {
            isValid = false;
            errorDetails.append("[ampersand found at position " + (fieldStr.indexOf('&') + 1) + "]");
        }
        if (fieldStr.indexOf('?') >= 0) {
            isValid = false;
            errorDetails.append("[question mark found at position " + (fieldStr.indexOf('?') + 1) + "]");
        }
        if (fieldStr.indexOf('<') >= 0) {
            isValid = false;
            errorDetails.append("[less-than sign found at position " + (fieldStr.indexOf('<') + 1) + "]");
        }
        if (fieldStr.indexOf('>') >= 0) {
            isValid = false;
            errorDetails.append("[greater-than sign found at position " + (fieldStr.indexOf('>') + 1) + "]");
        }

        if (!isValid) {
            this.addMessage(messages, methodContext.getLoader(), "The ID value in the field [" + fieldName + "] was not valid", ": " + errorDetails.toString());
        }

        return true;
    }

    public void addMessage(List messages, ClassLoader loader, String defaultMessage, String errorDetails) {
        if (!isProperty && message != null) {
            messages.add(message + errorDetails);
        } else if (isProperty && propertyResource != null && message != null) {
            String propMsg = UtilProperties.getPropertyValue(UtilURL.fromResource(propertyResource, loader), message);

            if (propMsg == null || propMsg.length() == 0)
                messages.add(defaultMessage + errorDetails);
            else
                messages.add(propMsg + errorDetails);
        } else {
            messages.add(defaultMessage + errorDetails);
        }
    }
}
