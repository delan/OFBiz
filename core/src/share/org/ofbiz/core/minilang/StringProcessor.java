package org.ofbiz.core.minilang;

import java.net.*;
import java.text.*;
import java.util.*;
import java.lang.reflect.*;

import org.w3c.dom.*;
import org.apache.oro.text.regex.*;
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
        runStringProcessor(xmlResource, strings, results, messages, null);
    }

    public static void runStringProcessor(String xmlResource, Map strings, Map results, List messages, Class contextClass) throws MiniLangException {
        URL xmlURL = UtilURL.fromResource(contextClass, xmlResource);
        if (xmlURL == null) {
            throw new MiniLangException("Could not find StringProcessor XML document in resource: " + xmlResource);
        }
                    
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
                    
                    if (document == null) {
                        throw new MiniLangException("Could not find StringProcessor XML document: " + xmlURL.toString());
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
        
        public String getFieldName() {
            return field;
        }
        
        public void exec(Map strings, Map results, List messages, Class contextClass) {
            String fieldValue = (java.lang.String) strings.get(field);
            
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
                    String nodeName = curOperElem.getNodeName();
                    if ("validate-method".equals(nodeName)) {
                        stringOperations.add(new StringProcessor.ValidateMethod(curOperElem, this));
                    } else if ("compare".equals(nodeName)) {
                        stringOperations.add(new StringProcessor.Compare(curOperElem, this));
                    } else if ("regexp".equals(nodeName)) {
                        stringOperations.add(new StringProcessor.Regexp(curOperElem, this));
                    } else if ("not-empty".equals(nodeName)) {
                        stringOperations.add(new StringProcessor.NotEmpty(curOperElem, this));
                    } else if ("copy".equals(nodeName)) {
                        stringOperations.add(new StringProcessor.Copy(curOperElem, this));
                    } else if ("convert".equals(nodeName)) {
                        stringOperations.add(new StringProcessor.Convert(curOperElem, this));
                    } else {
                        Debug.logWarning("[StringProcessor.StringProcess.readOperations] Operation element \"" + nodeName + "\" no recognized");
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
        StringProcess stringProcess;
        String fieldName;
        
        public StringOperation(Element element, StringProcess stringProcess) {
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
            
            this.stringProcess = stringProcess;
            this.fieldName = stringProcess.getFieldName();
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
        
        public ValidateMethod(Element element, StringProcess stringProcess) {
            super(element, stringProcess);
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

    public static class Compare extends StringOperation {
        String operator;
        String value;
        String type;
        String format;
        
        public Compare(Element element, StringProcess stringProcess) {
            super(element, stringProcess);
            this.value = element.getAttribute("value");
            this.operator = element.getAttribute("operator");
            this.type = element.getAttribute("type");
            this.format = element.getAttribute("format");
            if (this.format == null || this.format.length() == 0) {
                if ("Date".equals(type)) {
                    this.format = "yyyy-MM-dd";
                } else if ("Time".equals(type)) {
                    this.format = "HH:mm:ss";
                } else if ("Timestamp".equals(type)) {
                    this.format = "yyyy-MM-dd HH:mm:ss";
                }
            }
        }
        
        public void exec(String fieldValue, Map results, List messages, Class contextClass) {
            if (value == null)
                return;
            
            if ("contains".equals(operator)) {
                if ("String".equals(type)) {
                    messages.add("Error in string-processor file: cannot do a contains compare with a non-String type");
                    return;
                }
                
                if (value.indexOf(fieldValue) < 0)
                    addMessage(messages, contextClass);
            }
            
            int result = 0;            
            if ("String".equals(type)) {
                result = value.compareTo(fieldValue);
            } else if ("Number".equals(type)) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                Number tempNum = null;
                try {
                    tempNum = nf.parse(value);
                } catch (ParseException e) {
                    messages.add("Could not parse comparison value \"" + value + "\" for validation: " + e.getMessage());
                    return;
                }
                double valueDouble = tempNum.doubleValue();

                try {
                    tempNum = nf.parse(fieldValue);
                } catch (ParseException e) {
                    messages.add("Could not parse field value \"" + fieldValue + "\" for validation: " + e.getMessage());
                    return;
                }
                double fieldDouble = tempNum.doubleValue();

                if (valueDouble < fieldDouble)
                    result = -1;
                else if (valueDouble < fieldDouble)
                    result = 1;
                else
                    result = 0;
            } else if ("Date".equals(type) || "Time".equals(type) || "Timestamp".equals(type)) {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                java.util.Date valueDate = null;
                try {
                    valueDate = sdf.parse(value);
                } catch (ParseException e) {
                    messages.add("Could not parse comparison value \"" + value + "\" for validation: " + e.getMessage());
                    return;
                }
                
                java.util.Date fieldDate = null;
                try {
                    fieldDate = sdf.parse(fieldValue);
                } catch (ParseException e) {
                    messages.add("Could not parse field value \"" + fieldValue + "\" for validation: " + e.getMessage());
                    return;
                }
                result = valueDate.compareTo(fieldDate);
            } else {
                messages.add("Specified compare conversion type \"" + type + "\" not known.");
            }
            
            if ("less".equals(operator)) {
                if (result >= 0)
                    addMessage(messages, contextClass);
            } else if ("greater".equals(operator)) {
                if (result <= 0)
                    addMessage(messages, contextClass);
            } else if ("less-equals".equals(operator)) {
                if (result > 0)
                    addMessage(messages, contextClass);
            } else if ("greater-equals".equals(operator)) {
                if (result < 0)
                    addMessage(messages, contextClass);
            } else if ("equals".equals(operator)) {
                if (result != 0)
                    addMessage(messages, contextClass);
            } else if ("not-equals".equals(operator)) {
                if (result == 0)
                    addMessage(messages, contextClass);
            } else {
                messages.add("Specified compare operator \"" + operator + "\" not known.");
            }
        }
    }

    public static class Regexp extends StringOperation {
        static PatternMatcher matcher = new Perl5Matcher();
        static PatternCompiler compiler = new Perl5Compiler();
        Pattern pattern = null;
        String expr;
        
        public Regexp(Element element, StringProcess stringProcess) {
            super(element, stringProcess);
            expr = element.getAttribute("expr");
            try {
                pattern = compiler.compile(expr);
            } catch (MalformedPatternException e) {
                Debug.logError(e);
            }
        }
        
        public void exec(String fieldValue, Map results, List messages, Class contextClass) {
            if (pattern == null) {
                messages.add("Could not compile regular expression \"" + expr + "\" for validation");
                return;
            }
            
            if (!matcher.matches(fieldValue, pattern)) {
                addMessage(messages, contextClass);
            }
        }
    }

    public static class NotEmpty extends StringOperation {
        public NotEmpty(Element element, StringProcess stringProcess) {
            super(element, stringProcess);
        }
        
        public void exec(String fieldValue, Map results, List messages, Class contextClass) {
            if (!UtilValidate.isNotEmpty(fieldValue)) {
                addMessage(messages, contextClass);
            }
        }
    }

    public static class Copy extends StringOperation {
        boolean replace = true;
        String toField;
        
        public Copy(Element element, StringProcess stringProcess) {
            super(element, stringProcess);
            toField = element.getAttribute("to-field");
            if (this.toField == null || this.toField.length() == 0) {
                this.toField = this.fieldName;
            }
            
            replace = "true".equals(element.getAttribute("replace"));
        }
        
        public void exec(String fieldValue, Map results, List messages, Class contextClass) {
            if (fieldValue == null)
                return;
            
            if (replace) {
                results.put(toField, fieldValue);
                //Debug.logInfo("[StringProcessor.Copy.exec] Copied \"" + fieldValue + "\" to field \"" + toField + "\"");
            } else {
                if (results.containsKey(toField)) {
                    //do nothing
                } else {
                    results.put(toField, fieldValue);
                    //Debug.logInfo("[StringProcessor.Copy.exec] Copied \"" + fieldValue + "\" to field \"" + toField + "\"");
                }
            }
        }
    }

    public static class Convert extends StringOperation {
        String toField;
        String type;
        boolean replace = true;
        String format;
        
        public Convert(Element element, StringProcess stringProcess) {
            super(element, stringProcess);
            this.toField = element.getAttribute("to-field");
            if (this.toField == null || this.toField.length() == 0) {
                this.toField = this.fieldName;
            }
            
            this.type = element.getAttribute("type");
            this.replace = "true".equals(element.getAttribute("replace"));

            this.format = element.getAttribute("format");
            if (this.format == null || this.format.length() == 0) {
                if ("Date".equals(type)) {
                    this.format = "yyyy-MM-dd";
                } else if ("Time".equals(type)) {
                    this.format = "HH:mm:ss";
                } else if ("Timestamp".equals(type)) {
                    this.format = "yyyy-MM-dd HH:mm:ss";
                }
            }
        }
        
        public void exec(String fieldValue, Map results, List messages, Class contextClass) {
            Object fieldObject = null;
            
            if (fieldValue == null || fieldValue.length() == 0) {
                return;
            }
            
            if ("String".equals(type)) {
                fieldObject = fieldValue;
            } else if ("Double".equals(type)) {
                try {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    Number tempNum = nf.parse(fieldValue);
                    fieldObject = new Double(tempNum.doubleValue());
                } catch (ParseException e) {
                    addMessage(messages, contextClass);
                    return;
                }
            } else if ("Float".equals(type)) {
                try {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    Number tempNum = nf.parse(fieldValue);
                    fieldObject = new Float(tempNum.floatValue());
                } catch (ParseException e) {
                    addMessage(messages, contextClass);
                    return;
                }
            } else if ("Long".equals(type)) {
                try {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(0);
                    Number tempNum = nf.parse(fieldValue);
                    fieldObject = new Long(tempNum.longValue());
                } catch (ParseException e) {
                    addMessage(messages, contextClass);
                    return;
                }
            } else if ("Integer".equals(type)) {
                try {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(0);
                    Number tempNum = nf.parse(fieldValue);
                    fieldObject = new Integer(tempNum.intValue());
                } catch (ParseException e) {
                    addMessage(messages, contextClass);
                    return;
                }
            } else if ("Date".equals(type)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    java.util.Date fieldDate = sdf.parse(fieldValue);
                    fieldObject = new java.sql.Date(fieldDate.getTime());
                } catch (ParseException e) {
                    addMessage(messages, contextClass);
                    return;
                }
            } else if ("Time".equals(type)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    java.util.Date fieldDate = sdf.parse(fieldValue);
                    fieldObject = new java.sql.Time(fieldDate.getTime());
                } catch (ParseException e) {
                    addMessage(messages, contextClass);
                    return;
                }
            } else if ("Timestamp".equals(type)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    java.util.Date fieldDate = sdf.parse(fieldValue);
                    fieldObject = new java.sql.Timestamp(fieldDate.getTime());
                } catch (ParseException e) {
                    addMessage(messages, contextClass);
                    return;
                }
            } else {
                messages.add("Specified type \"" + type + "\" not known in conversion operation.");
            }
            
            if (fieldObject == null)
                return;
            
            if (replace) {
                results.put(toField, fieldObject);
                //Debug.logInfo("[StringProcessor.Converted.exec] Put converted value \"" + fieldObject + "\" in field \"" + toField + "\"");
            } else {
                if (results.containsKey(toField)) {
                    //do nothing
                } else {
                    results.put(toField, fieldObject);
                    //Debug.logInfo("[StringProcessor.Converted.exec] Put converted value \"" + fieldObject + "\" in field \"" + toField + "\"");
                }
            }
        }
    }
}
