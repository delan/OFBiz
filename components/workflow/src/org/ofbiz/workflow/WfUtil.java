/*
 * $Id: WfUtil.java,v 1.1 2003/08/17 09:29:33 ajzeneski Exp $
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
package org.ofbiz.workflow;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;

/**
 * WorkflowUtil - Workflow Engine Utilities
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
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
    
    /**
     * Returns the OFB status code which refers to the passed OMG status code
     * @param state
     * @return String
     */
    public static String getOFBStatus(String state) {
        String statesArr[] = {"open.running", "open.not_running.not_started", "open.not_running.suspended",
                "closed.completed", "closed.terminated", "closed.aborted"};
        String entityArr[] = {"WF_RUNNING", "WF_NOT_STARTED", "WF_SUSPENDED", "WF_COMPLETED",
                "WF_TERMINATED", "WF_ABORTED"};

        for (int i = 0; i < statesArr.length; i++) {
            if (statesArr[i].equals(state))
                return entityArr[i];
        }
        return null;
    }

    /**
     * Returns the OMG status code which refers to the passed OFB status code
     * @param state
     * @return String
     */
    public static String getOMGStatus(String state) {
        String statesArr[] = {"open.running", "open.not_running.not_started", "open.not_running.suspended",
                "closed.completed", "closed.terminated", "closed.aborted"};
        String entityArr[] = {"WF_RUNNING", "WF_NOT_STARTED", "WF_SUSPENDED", "WF_COMPLETED",
                "WF_TERMINATED", "WF_ABORTED"};

        for (int i = 0; i < entityArr.length; i++) {
            if (entityArr[i].equals(state))
                return statesArr[i];
        }
        return null;
    }
    
    
}
