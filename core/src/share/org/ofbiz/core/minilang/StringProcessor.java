package org.ofbiz.core.minilang;

import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> StringProcessor Mini Language
 * <p><b>Description:</b> None
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
 *@created    December 29, 2001
 *@version    1.0
 */
public class StringProcessor {
    protected static UtilCache stringProcessors = new UtilCache("StringProcessors", 0, 0);
    
    public static void runStringProcessor(String xmlResource, Map strings, Map results, List messages) {
        runStringProcessor(xmlResource, strings, results, messages, StringProcessor.class);
    }

    public static void runStringProcessor(String xmlResource, Map strings, Map results, List messages, Class contextClass) {
        URL xmlURL = UtilURL.fromResource(contextClass, xmlResource);
        runStringProcessor(xmlURL, strings, results, messages, contextClass);
    }

    public static void runStringProcessor(URL xmlURL, Map strings, Map results, List messages, Class contextClass) {
        List stringProcesses = getStringProcesses(xmlURL);
        if (stringProcesses != null && stringProcesses.size() > 0) {
            Iterator strPrsIter = stringProcesses.iterator();
            while (strPrsIter.hasNext()) {
                StringProcess stringProcess = (StringProcess) strPrsIter.next();
                stringProcess.exec(strings, results, messages, contextClass);
            }
        }
    }

    protected static List getStringProcesses(URL xmlURL) {
        List stringProcesses = (List) stringProcessors.get(xmlURL);
        if (stringProcesses == null) {
            synchronized (StringProcessor.class) {
                stringProcesses = (List) stringProcessors.get(xmlURL);
                if (stringProcesses == null) {
                    //read in the file
                    
                    //put it in the cache
                    stringProcessors.put(xmlURL, stringProcesses);
                }
            }
        }
        
        return stringProcesses;
    }
    
    /** A complete string process for a given field; contains multiple string operations */
    public class StringProcess {
        List stringOperations = new LinkedList();
        String field = "";
        
        public StringProcess(String field) {
            this.field = field;
        }
        
        public void exec(Map strings, Map results, List messages, Class contextClass) {
            String fieldValue = (String) strings.get(field);
            
            Iterator strOpsIter = stringOperations.iterator();
            while (strOpsIter.hasNext()) {
                StringOperation stringOperation = (StringOperation) strOpsIter.next();
                stringOperation.exec(fieldValue, results, messages, contextClass);
            }
        }
    }
    
    /** A single string operation, does the specified operation on the given field */
    public abstract class StringOperation {
        public String message = null;
        public String propertyResource = null;
        public String propertyName = null;
        
        public StringOperation() { }
        
        public abstract void exec(String fieldValue, Map results, List messages, Class contextClass);
        
        public void addMessage(List messages, Class contextClass) {
            if (message != null) {
                messages.add(message);
            } else if (propertyResource != null && propertyName != null) {
                String propMsg = UtilProperties.getPropertyValue(UtilURL.fromResource(contextClass, propertyResource), propertyName);
            }
        }
    }
    
    public class ValidateMethod extends StringOperation {
        String methodName;
        String className;
        public void exec(String fieldValue, Map results, List messages, Class contextClass) {
            Class[] paramTypes = new Class[] {String.class};
            Object[] params = new Object[] {fieldValue};

            Class valClass;
            try {
                valClass = contextClass.forName(className);
            } catch(ClassNotFoundException cnfe) {
                String msg = "Could not find validation class: " + className;
                messages.add(msg);
                Debug.logError("[ValidateMethod.exec] " + msg);
                return;
            }

            Method valMethod;
            try {
                valMethod = valClass.getMethod(methodName, paramTypes);
            } catch(NoSuchMethodException cnfe) {
                String msg = "Could not find validation method: " + methodName + " of class " + className;
                messages.add(msg);
                Debug.logError("[ValidateMethod.exec] " + msg);
                return;
            }

            Boolean resultBool = Boolean.FALSE;
            try {
                resultBool = (Boolean)valMethod.invoke(null,params);
            } catch(Exception e) {
                String msg = "Error in validation method " + methodName + " of class " + className + ": " + e.getMessage();
                messages.add(msg);
                Debug.logError("[ValidateMethod.exec] " + msg);
                return;
            }

            if(!resultBool.booleanValue()) {
                addMessage(messages, contextClass);
            }
        }
    }
}
