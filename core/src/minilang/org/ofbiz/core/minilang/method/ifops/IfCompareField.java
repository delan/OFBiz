/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

import org.ofbiz.core.minilang.operation.*;

/**
 * Iff the comparison between the specified field and the other field is true process sub-operations
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 19, 2002
 *@version    1.0
 */
public class IfCompareField extends MethodOperation {
    
    List subOps = new LinkedList();
    List elseSubOps = null;
    
    String mapName;
    String fieldName;
    String toMapName;
    String toFieldName;

    String operator;
    String type;
    String format;
    
    public IfCompareField(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.mapName = element.getAttribute("map-name");
        this.fieldName = element.getAttribute("field-name");
        this.toMapName = element.getAttribute("to-map-name");
        this.toFieldName = element.getAttribute("to-field-name");

        this.operator = element.getAttribute("operator");
        this.type = element.getAttribute("type");
        this.format = element.getAttribute("format");
        
        SimpleMethod.readOperations(element, subOps, simpleMethod);
        
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement != null) {
            elseSubOps = new LinkedList();
            SimpleMethod.readOperations(elseElement, elseSubOps, simpleMethod);
        }
    }

    public boolean exec(MethodContext methodContext) {
        //if conditions fails, always return true; if a sub-op returns false 
        // return false and stop, otherwise return true

        Object fieldVal1 = null;
        Object fieldVal2 = null;
        
        if (mapName != null && mapName.length() > 0) {
            Map fromMap = (Map) methodContext.getEnv(mapName);
            if (fromMap == null) {
                if (Debug.infoOn()) Debug.logVerbose("Map not found with name " + mapName + ", using null for comparison");
            } else {
                fieldVal1 = fromMap.get(fieldName);
            }
        } else {
            //no map name, try the env
            fieldVal1 = methodContext.getEnv(fieldName);
        }
        
        if (toMapName != null && toMapName.length() > 0) {
            Map toMap = (Map) methodContext.getEnv(toMapName);
            if (toMap == null) {
                if (Debug.infoOn()) Debug.logVerbose("To Map not found with name " + toMapName + ", using null for comparison");
            } else {
                fieldVal2 = toMap.get(toFieldName);
            }
        } else {
            //no map name, try the env
            fieldVal2 = methodContext.getEnv(toFieldName);
        }
                
        List messages = new LinkedList();
        Boolean resultBool = BaseCompare.doRealCompare(fieldVal1, fieldVal2, this.operator, this.type, this.format, messages, null, methodContext.getLoader());

        if (messages.size() > 0) {
            Iterator miter = messages.iterator();
            while (miter.hasNext()) {
                Debug.logWarning("Error with comparison: " + miter.next());
            }
        }
        
        if (resultBool != null && resultBool.booleanValue()) {
            return SimpleMethod.runSubOps(subOps, methodContext);
        } else {
            if (elseSubOps != null) {
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            } else {
                return true;
            }
        }
    }
}
