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
package org.ofbiz.minilang.method.otherops;

import java.util.*;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.w3c.dom.*;
import org.ofbiz.base.util.*;
import org.ofbiz.minilang.*;
import org.ofbiz.minilang.method.*;

/**
 * Calculates a result based on nested calcops.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.0
 */
public class Calculate extends MethodOperation {
    
    public static final String module = Calculate.class.getName();
    
    public static final int TYPE_DOUBLE = 1;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_LONG = 3;
    public static final int TYPE_INTEGER = 4;
    public static final int TYPE_STRING = 5;

    ContextAccessor mapAcsr;
    ContextAccessor fieldAcsr;
    String formatString;
    String typeString;
    Calculate.SubCalc calcops[];

    public Calculate(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        mapAcsr = new ContextAccessor(element.getAttribute("map-name"));
        fieldAcsr = new ContextAccessor(element.getAttribute("field-name"));

        formatString = element.getAttribute("decimal-format");
        typeString = element.getAttribute("type");

        List calcopElements = UtilXml.childElementList(element);
        calcops = new Calculate.SubCalc[calcopElements.size()];
        Iterator calcopIter = calcopElements.iterator();
        int i = 0;

        while (calcopIter.hasNext()) {
            Element calcopElement = (Element) calcopIter.next();
            String nodeName = calcopElement.getNodeName();

            if ("calcop".equals(nodeName)) {
                calcops[i] = new Calculate.CalcOp(calcopElement);
            } else if ("number".equals(nodeName)) {
                calcops[i] = new Calculate.NumberOp(calcopElement);
            } else {
                Debug.logError("Error: calculate operation with type " + nodeName, module);
            }
            // Debug.logInfo("Added operation type " + nodeName + " in position " + i, module);
            i++;
        }
    }

    public boolean exec(MethodContext methodContext) {
        String typeString = methodContext.expandString(this.typeString);
        int type;
        if ("Double".equals(typeString)) {
            type = Calculate.TYPE_DOUBLE;
        } else if ("Float".equals(typeString)) {
            type = Calculate.TYPE_FLOAT;
        } else if ("Long".equals(typeString)) {
            type = Calculate.TYPE_LONG;
        } else if ("Integer".equals(typeString)) {
            type = Calculate.TYPE_INTEGER;
        } else if ("String".equals(typeString)) {
            type = Calculate.TYPE_STRING;
        } else {
            type = Calculate.TYPE_DOUBLE;
        }
        
        double resultValue = 0;

        for (int i = 0; i < calcops.length; i++) {
            resultValue += calcops[i].calcValue(methodContext);
            // Debug.logInfo("main total so far: " + resultValue, module);
        }

        // run the decimal-formatting         
        if (UtilValidate.isNotEmpty(formatString)) {
            DecimalFormat df = new DecimalFormat(formatString);
            try {
                resultValue = ((Double) df.parse(df.format(resultValue))).doubleValue();
            } catch (ParseException e) {
                String errorMessage = "Unable to format [" + formatString + "] result [" + resultValue + "]";
                Debug.logError(e, errorMessage, module);
                if (methodContext.getMethodType() == MethodContext.EVENT) {
                    methodContext.putEnv(simpleMethod.getEventErrorMessageName(), errorMessage);
                } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                    methodContext.putEnv(simpleMethod.getServiceErrorMessageName(), errorMessage);
                }
                return false;
            }
        }

        Object resultObj = null;
        switch (type) {
        case TYPE_DOUBLE:
            resultObj = new Double(resultValue);
            break;
        case TYPE_FLOAT:
            resultObj = new Float(resultValue);
            break;
        case TYPE_LONG:
            resultObj = new Long(Math.round(resultValue));
            break;
        case TYPE_INTEGER:
            resultObj = new Integer((int) Math.round(resultValue));
            break;
        case TYPE_STRING:
            resultObj = new Double(resultValue).toString();
            break;            
        }

        if (!mapAcsr.isEmpty()) {
            Map toMap = (Map) mapAcsr.get(methodContext);
            if (toMap == null) {
                if (Debug.verboseOn()) Debug.logVerbose("Map not found with name " + mapAcsr + ", creating new map", module);
                toMap = new HashMap();
                mapAcsr.put(methodContext, toMap);
            }
            fieldAcsr.put(toMap, resultObj, methodContext);
        } else {
            fieldAcsr.put(methodContext, resultObj);
        }

        return true;
    }

    protected static interface SubCalc {
        public double calcValue(MethodContext methodContext);
    }

    protected static class NumberOp implements SubCalc {
        String valueStr;

        public NumberOp(Element element) {
            valueStr = element.getAttribute("value");
        }

        public double calcValue(MethodContext methodContext) {
            String valueStr = methodContext.expandString(this.valueStr);
            double value;
            try {
                value = Double.parseDouble(valueStr);
            } catch (Exception e) {
                Debug.logError(e, "Could not parse the number string: " + valueStr, module);
                throw new IllegalArgumentException("Could not parse the number string: " + valueStr);
            }
            
            // Debug.logInfo("calcValue number: " + value, module);
            return value;
        }

    }

    protected static class CalcOp implements SubCalc {
        public static final int OPERATOR_ADD = 1;
        public static final int OPERATOR_SUBTRACT = 2;
        public static final int OPERATOR_MULTIPLY = 3;
        public static final int OPERATOR_DIVIDE = 4;
        public static final int OPERATOR_NEGATIVE = 5;

        ContextAccessor mapAcsr;
        ContextAccessor fieldAcsr;
        String operatorStr;
        Calculate.SubCalc calcops[];

        public CalcOp(Element element) {
            mapAcsr = new ContextAccessor(element.getAttribute("map-name"));
            fieldAcsr = new ContextAccessor(element.getAttribute("field-name"));
            operatorStr = element.getAttribute("operator");

            List calcopElements = UtilXml.childElementList(element);
            calcops = new Calculate.SubCalc[calcopElements.size()];
            Iterator calcopIter = calcopElements.iterator();
            int i = 0;

            while (calcopIter.hasNext()) {
                Element calcopElement = (Element) calcopIter.next();
                String nodeName = calcopElement.getNodeName();

                if ("calcop".equals(calcopElement.getNodeName())) {
                    calcops[i] = new Calculate.CalcOp(calcopElement);
                } else if ("number".equals(calcopElement.getNodeName())) {
                    calcops[i] = new Calculate.NumberOp(calcopElement);
                } else {
                    Debug.logError("Error: calculate operation unknown with type " + nodeName, module);
                }
                // Debug.logInfo("Added operation type " + nodeName + " in position " + i, module);
                i++;
            }
        }

        public double calcValue(MethodContext methodContext) {
            String operatorStr = methodContext.expandString(this.operatorStr);
            int operator = CalcOp.OPERATOR_ADD;
            if ("get".equals(operatorStr)) {
                operator = CalcOp.OPERATOR_ADD;
            } else if ("add".equals(operatorStr)) {
                operator = CalcOp.OPERATOR_ADD;
            } else if ("subtract".equals(operatorStr)) {
                operator = CalcOp.OPERATOR_SUBTRACT;
            } else if ("multiply".equals(operatorStr)) {
                operator = CalcOp.OPERATOR_MULTIPLY;
            } else if ("divide".equals(operatorStr)) {
                operator = CalcOp.OPERATOR_DIVIDE;
            } else if ("negative".equals(operatorStr)) {
                operator = CalcOp.OPERATOR_NEGATIVE;
            }
            
            double resultValue = 0;
            boolean isFirst = true;

            // if a fieldAcsr was specified, get the field from the map or result and use it as the initial value
            if (!fieldAcsr.isEmpty()) {
                Object fieldObj = null;

                if (!mapAcsr.isEmpty()) {
                    Map fromMap = (Map) mapAcsr.get(methodContext);
                    if (fromMap == null) {
                        if (Debug.verboseOn()) Debug.logVerbose("Map not found with name " + mapAcsr + ", creating new map", module);
                        fromMap = new HashMap();
                        mapAcsr.put(methodContext, fromMap);
                    }
                    fieldObj = fieldAcsr.get(fromMap, methodContext);
                } else {
                    fieldObj = fieldAcsr.get(methodContext);
                }

                if (fieldObj != null) {
                    if (fieldObj instanceof Double) {
                        resultValue = ((Double) fieldObj).doubleValue();
                    } else if (fieldObj instanceof Long) {
                        resultValue = (double) ((Long) fieldObj).longValue();
                    } else if (fieldObj instanceof Float) {
                        resultValue = (double) ((Float) fieldObj).floatValue();
                    } else if (fieldObj instanceof Integer) {
                        resultValue = (double) ((Integer) fieldObj).intValue();
                    } else if (fieldObj instanceof String) {             
                        resultValue = Double.valueOf((String) fieldObj).doubleValue();                        
                    }
                    if (operator == OPERATOR_NEGATIVE) resultValue = -resultValue;
                    isFirst = false;
                } else {
                    if (Debug.infoOn()) Debug.logInfo("Field not found with field-name " + fieldAcsr + ", and map-name " + mapAcsr + "using a default of 0", module);
                }
            }

            for (int i = 0; i < calcops.length; i++) {
                if (isFirst) {
                    resultValue = calcops[i].calcValue(methodContext);
                    if (operator == OPERATOR_NEGATIVE) resultValue = -resultValue;
                    isFirst = false;
                } else {
                    switch (operator) {
                    case OPERATOR_ADD:
                        resultValue += calcops[i].calcValue(methodContext);
                        break;
                    case OPERATOR_SUBTRACT:
                    case OPERATOR_NEGATIVE:
                        resultValue -= calcops[i].calcValue(methodContext);
                        break;
                    case OPERATOR_MULTIPLY:
                        resultValue *= calcops[i].calcValue(methodContext);
                        break;
                    case OPERATOR_DIVIDE:
                        resultValue /= calcops[i].calcValue(methodContext);
                        break;
                    }
                }
                // Debug.logInfo("sub total so far: " + resultValue, module);
            }
            // Debug.logInfo("calcValue calcop: " + resultValue + "(field=" + fieldAcsr + ", map=" + mapAcsr + ")", module);
            return resultValue;
        }
    }
}
