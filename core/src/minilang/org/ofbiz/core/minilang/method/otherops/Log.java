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

package org.ofbiz.core.minilang.method.otherops;


import java.net.*;
import java.text.*;
import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;


/**
 * Calculates a result based on nested calcops.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    October 4, 2002
 *@version    1.0
 */
public class Log extends MethodOperation {
    public static final String module = Log.class.getName();

    int level;
    String message;
    List methodStrings = null;
    
    public Log(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        message = element.getAttribute("message");
        String levelStr = element.getAttribute("level");
        Integer levelInt = Debug.getLevelFromString(levelStr);
        if (levelInt == null) {
            Debug.logWarning("Specified level [" + levelStr + "] was not valid, using INFO");
            level = Debug.INFO;
        } else {
            level = levelInt.intValue();
        }

        List methodStringElements = UtilXml.childElementList(element, null);
        if (methodStringElements.size() > 0) {
            methodStrings = new LinkedList();
            
            Iterator methodStringIter = methodStringElements.iterator();
            while (methodStringIter.hasNext()) {
                Element methodStringElement = (Element) methodStringIter.next();
                if ("string".equals(methodStringElement.getNodeName())) {
                    methodStrings.add(new StringString(methodStringElement, simpleMethod)); 
                } else if ("field".equals(methodStringElement.getNodeName())) {
                    methodStrings.add(new FieldString(methodStringElement, simpleMethod)); 
                } else {
                    //whoops, invalid tag here, print warning
                    Debug.logWarning("Found an unsupported tag under the log tag: " + methodStringElement.getNodeName() + "; ignoring");
                }
            }
        }
    }

    public boolean exec(MethodContext methodContext) {
        //bail out quick if the logging level isn't on, ie don't even create string
        if (!Debug.isOn(level)) {
            return true;
        }
        
        StringBuffer buf = new StringBuffer();
        
        if (message != null) buf.append(message);
        
        if (methodStrings != null) {
            Iterator methodStringsIter = methodStrings.iterator();
            while (methodStringsIter.hasNext()) {
                MethodString methodString = (MethodString) methodStringsIter.next();
                String strValue = methodString.getString(methodContext);
                if (strValue != null) buf.append(strValue);
            }
        }        

        Debug.log(level, null, buf.toString(), module);
        
        return true;
    }
}
