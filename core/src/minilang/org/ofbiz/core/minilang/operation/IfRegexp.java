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

package org.ofbiz.core.minilang.operation;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.apache.oro.text.regex.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;

/**
 * Iff the specified field complies with the pattern specified by the regular expression, process sub-operations
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 19, 2002
 *@version    1.0
 */
public class IfRegexp extends MethodOperation {
    
    static PatternMatcher matcher = new Perl5Matcher();
    static PatternCompiler compiler = new Perl5Compiler();

    List subOps = new LinkedList();
    
    String mapName;
    String fieldName;

    Pattern pattern = null;
    String expr;

    public IfRegexp(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.mapName = element.getAttribute("map-name");
        this.fieldName = element.getAttribute("field-name");
        
        this.expr = element.getAttribute("expr");
        try {
            pattern = compiler.compile(expr);
        } catch (MalformedPatternException e) {
            Debug.logError(e);
        }

        SimpleMethod.readOperations(element, subOps, simpleMethod);
    }

    public boolean exec(MethodContext methodContext) {
        //if conditions fails, always return true; if a sub-op returns false 
        // return false and stop, otherwise return true

        String fieldString = null;
        Map fromMap = (Map) methodContext.getEnv(mapName);
        if (fromMap == null) {
            Debug.logInfo("Map not found with name " + mapName + ", using empty string for comparison");
        } else {
            Object fieldVal = fromMap.get(fieldName);

            if (fieldVal != null) {
                try {
                    fieldString = (String) ObjectType.simpleTypeConvert(fieldVal, "String", null, null);
                } catch (GeneralException e) {
                    Debug.logError(e, "Could not convert object to String, using empty String");
                }

            }
        }
        
        //always use an empty string by default
        if (fieldString == null)
            fieldString = "";
        

        if (matcher.matches(fieldString, pattern)) {
            return SimpleMethod.runSubOps(subOps, methodContext);
        } else {
            return true;
        }
    }
}
