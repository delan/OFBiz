package org.ofbiz.core.minilang;

import java.net.*;
import java.util.*;
import java.lang.reflect.*;

import org.w3c.dom.*;
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
    
    public static void runStringProcessor(String xmlResource, Map strings, Map results, List messages) throws MiniLangException {
        runStringProcessor(xmlResource, strings, results, messages, StringProcessor.class);
    }

    public static void runStringProcessor(String xmlResource, Map strings, Map results, List messages, Class contextClass) throws MiniLangException {
        URL xmlURL = UtilURL.fromResource(contextClass, xmlResource);
        runStringProcessor(xmlURL, strings, results, messages, contextClass);
    }

    public static void runStringProcessor(URL xmlURL, Map strings, Map results, List messages, Class contextClass) throws MiniLangException {
        List stringProcesses = getStringProcesses(xmlURL);
        if (stringProcesses != null && stringProcesses.size() > 0) {
            Iterator strPrsIter = stringProcesses.iterator();
            while (strPrsIter.hasNext()) {
                StringProcess stringProcess = (StringProcess) strPrsIter.next();
                stringProcess.exec(strings, results, messages, contextClass);
            }
        }
    }

    protected static List getStringProcesses(URL xmlURL) throws MiniLangException {
        List stringProcesses = (List) stringProcessors.get(xmlURL);
        if (stringProcesses == null) {
            synchronized (StringProcessor.class) {
                stringProcesses = (List) stringProcessors.get(xmlURL);
                if (stringProcesses == null) {
                    stringProcesses = new LinkedList();
                    
                    //read in the file
                    Document document = null;
                    try {
                        document = UtilXml.readXmlDocument(xmlURL, true);
                    } catch (java.io.IOException e) {
                        throw new MiniLangException("Could not read XML file", e);
                    } catch (org.xml.sax.SAXException e) {
                        throw new MiniLangException("Could not parse XML file", e);
                    } catch (javax.xml.parsers.ParserConfigurationException e) {
                        throw new MiniLangException("XML parser not setup correctly", e);
                    }
                    
                    Element rootElement = document.getDocumentElement();
                    List stringProcessElements = UtilXml.childElementList(rootElement, "string-process");
                    
                    Iterator strProcIter = stringProcessElements.iterator();
                    while (strProcIter.hasNext()) {
                        Element stringProcessElement = (Element) strProcIter.next();
                        StringProcessor.StringProcess strProc = new StringProcessor.StringProcess(stringProcessElement);
                        stringProcesses.add(strProc);
                    }
                    
                    //put it in the cache
                    stringProcessors.put(xmlURL, stringProcesses);
                }
            }
        }
        
        return stringProcesses;
    }
    
    /** A complete string process for a given field; contains multiple string operations */
    public static class StringProcess {
        List stringOperations = new LinkedList();
        String field = "";
        
        public StringProcess(Element stringProcessElement) {
            this.field = stringProcessElement.getAttribute("field");
            readOperations(stringProcessElement);
        }
        
        public void exec(Map strings, Map results, List messages, Class contextClass) {
            String fieldValue = (String) strings.get(field);
            
            Iterator strOpsIter = stringOperations.iterator();
            while (strOpsIter.hasNext()) {
                StringOperation stringOperation = (StringOperation) strOpsIter.next();
                stringOperation.exec(fieldValue, results, messages, contextClass);
            }
        }
        
        void readOperations(Element stringProcessElement) {
            List operationElements = UtilXml.childElementList(stringProcessElement, null);
            if (operationElements != null && operationElements.size() > 0) {
                Iterator operElemIter = operationElements.iterator();
                while (operElemIter.hasNext()) {
                    Element curOperElem = (Element) operElemIter.next();
                    if ("validate-method".equals(curOperElem.getNodeName())) {
                        stringOperations.add(new StringProcessor.ValidateMethod(curOperElem));
                    } else if ("compare".equals(curOperElem.getNodeName())) {
                        stringOperations.add(new StringProcessor.Compare(curOperElem));
                    } else if ("regexp".equals(curOperElem.getNodeName())) {
                        //stringOperations.add(new StringProcessor.Regexp(curOperElem));
                    } else if ("not-empty".equals(curOperElem.getNodeName())) {
                        //stringOperations.add(new StringProcessor.NotEmpty(curOperElem));
                    } else if ("equals".equals(curOperElem.getNodeName())) {
                        //stringOperations.add(new StringProcessor.Equals(curOperElem));
                    } else if ("copy".equals(curOperElem.getNodeName())) {
                        //stringOperations.add(new StringProcessor.Copy(curOperElem));
                    } else if ("convert".equals(curOperElem.getNodeName())) {
                        //stringOperations.add(new StringProcessor.Convert(curOperElem));
                    } else {
                        //for now ignore it if unknown...
                    }
                }
            }
            
        }
    }
    
    /** A single string operation, does the specified operation on the given field */
    public static abstract class StringOperation {
        String message = null;
        String propertyResource = null;
        boolean isProperty = false;
        
        public StringOperation() { }
        
        public StringOperation(String message) {
            this.message = message;
            this.isProperty = false;
        }
        
        public StringOperation(String propertyResource, String propertyName) {
            this.propertyResource = propertyResource;
            this.message = propertyName;
            this.isProperty = true;
        }
        
        public StringOperation(Element element) {
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
        
        public abstract void exec(String fieldValue, Map results, List messages, Class contextClass);
        
        public void addMessage(List messages, Class contextClass) {
            if (isProperty && message != null) {
                messages.add(message);
            } else if (propertyResource != null && message != null) {
                String propMsg = UtilProperties.getPropertyValue(UtilURL.fromResource(contextClass, propertyResource), message);
            }
        }
    }
    
    /* ==================================================================== */
    /* All of the StringOperations...
    /* ==================================================================== */

    /** A string operation that calls a validation method */
    public static class ValidateMethod extends StringOperation {
        String methodName;
        String className;
        
        public ValidateMethod(String methodName, String className) {
            this.methodName = methodName;
            this.className = className;
        }
        
        public ValidateMethod(Element element) {
            this.methodName = element.getAttribute("method");
            this.className = element.getAttribute("class");
        }
        
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
/*
<!ATTLIST compare
    operator CDATA #REQUIRED
    value CDATA #REQUIRED
    type ( String | Double | Float | Long | Integer | Date | Time | Timestamp ) "String"
    format CDATA #IMPLIED
>*/
    public static class Compare extends StringOperation {
        String methodName;
        String className;
        
        public Compare(String methodName, String className) {
            this.methodName = methodName;
            this.className = className;
        }
        
        public Compare(Element element) {
            super(element);
            this.methodName = element.getAttribute("method");
            this.className = element.getAttribute("class");
        }
        
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
