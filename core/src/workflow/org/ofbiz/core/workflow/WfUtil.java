/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.workflow;

import java.util.*;
import org.ofbiz.core.util.*;

/**
 * WorkflowUtil - Workflow Engine Utilities
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public final class WfUtil {
    
    private static final Map typeMap = UtilMisc.toMap("WDT_BOOLEAN", "java.lang.Boolean",
        "WDT_STRING", "java.lang.String", "WDT_INTEGER", "java.lang.Long", 
        "WDT_FLOAT", "java.lang.Double", "WDT_DATETIME", "java.sql.Timestamp");
                      
    /**
     * Gets the Java type from a XPDL datatype
     * @param xpdlType XPDL data type to be translated
     * @return Java Class name equivalence to the XPDL data type
     */
    public static final String getJavaType(String xpdlType) {        
        if (typeMap.containsKey(xpdlType))
            return (String) typeMap.get(xpdlType);
        else
            return "java.lang.Object";
    }
    
}
