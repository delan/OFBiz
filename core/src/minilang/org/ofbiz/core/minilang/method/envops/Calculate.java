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

package org.ofbiz.core.minilang.method.envops;


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
 *@created    February 19, 2002
 *@version    1.0
 */
public class Calculate extends MethodOperation {
    public static final int TYPE_DOUBLE = 1;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_LONG = 3;
    public static final int TYPE_INTEGER = 4;

    String mapName;
    String fieldName;
    int type;
    Calculate.SubCalc calcops[];

    public Calculate(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        mapName = element.getAttribute("map-name");
        fieldName = element.getAttribute("field-name");

        String typeString = element.getAttribute("type");

        if ("Double".equals(typeString)) {
            type = this.TYPE_DOUBLE;
        } else if ("Float".equals(typeString)) {
            type = this.TYPE_FLOAT;
        } else if ("Long".equals(typeString)) {
            type = this.TYPE_LONG;
        } else if ("Integer".equals(typeString)) {
            type = this.TYPE_INTEGER;
        } else {
            type = this.TYPE_DOUBLE;
        }

        List calcopElements = UtilXml.childElementList(element, null);

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
                Debug.logError("Error: calculate operation with type " + nodeName);
            }
            // Debug.logInfo("Added operation type " + nodeName + " in position " + i);
            i++;
        }
    }

    public boolean exec(MethodContext methodContext) {
        double resultValue = 0;

        for (int i = 0; i < calcops.length; i++) {
            resultValue += calcops[i].calcValue(methodContext);
            // Debug.logInfo("main total so far: " + resultValue);
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
        }

        if (mapName != null && mapName.length() > 0) {
            Map toMap = (Map) methodContext.getEnv(mapName);

            if (toMap == null) {
                if (Debug.verboseOn()) Debug.logVerbose("Map not found with name " + mapName + ", creating new map");
                toMap = new HashMap();
                methodContext.putEnv(mapName, toMap);
            }
            toMap.put(fieldName, resultObj);
        } else {
            methodContext.putEnv(fieldName, resultObj);
        }

        return true;
    }

    protected static interface SubCalc {
        public double calcValue(MethodContext methodContext);
    }


    protected static class NumberOp implements SubCalc {
        double value;

        public NumberOp(Element element) {
            String valueStr = element.getAttribute("value");

            try {
                value = Double.parseDouble(valueStr);
            } catch (Exception e) {
                Debug.logError(e, "Could not parse the number string: " + valueStr);
                throw new IllegalArgumentException("Could not parse the number string: " + valueStr);
            }
        }

        public double calcValue(MethodContext methodContext) {
            // Debug.logInfo("calcValue number: " + value);
            return value;
        }

    }


    protected static class CalcOp implements SubCalc {
        public static final int OPERATOR_ADD = 1;
        public static final int OPERATOR_SUBTRACT = 2;
        public static final int OPERATOR_MULTIPLY = 3;
        public static final int OPERATOR_DIVIDE = 4;
        public static final int OPERATOR_NEGATIVE = 5;

        String mapName;
        String fieldName;
        int operator;
        Calculate.SubCalc calcops[];

        public CalcOp(Element element) {
            mapName = element.getAttribute("map-name");
            fieldName = element.getAttribute("field-name");

            String operatorStr = element.getAttribute("operator");

            if ("get".equals(operatorStr)) {
                operator = this.OPERATOR_ADD;
            } else if ("add".equals(operatorStr)) {
                operator = this.OPERATOR_ADD;
            } else if ("subtract".equals(operatorStr)) {
                operator = this.OPERATOR_SUBTRACT;
            } else if ("multiply".equals(operatorStr)) {
                operator = this.OPERATOR_MULTIPLY;
            } else if ("divide".equals(operatorStr)) {
                operator = this.OPERATOR_DIVIDE;
            } else if ("negative".equals(operatorStr)) {
                operator = this.OPERATOR_NEGATIVE;
            }

            List calcopElements = UtilXml.childElementList(element, null);

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
                    Debug.logError("Error: calculate operation with type " + nodeName);
                }
                // Debug.logInfo("Added operation type " + nodeName + " in position " + i);
                i++;
            }
        }

        public double calcValue(MethodContext methodContext) {
            double resultValue = 0;
            boolean isFirst = true;

            // if a fieldName was specified, get the field from the map or result and use it as the initial value
            if (fieldName != null && fieldName.length() > 0) {
                Object fieldObj = null;

                if (mapName != null && mapName.length() > 0) {
                    Map fromMap = (Map) methodContext.getEnv(mapName);

                    if (fromMap == null) {
                        if (Debug.verboseOn()) Debug.logVerbose("Map not found with name " + mapName + ", creating new map");
                        fromMap = new HashMap();
                        methodContext.putEnv(mapName, fromMap);
                    }
                    fieldObj = fromMap.get(fieldName);
                } else {
                    fieldObj = methodContext.getEnv(fieldName);
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
                    }
                    if (operator == OPERATOR_NEGATIVE) resultValue = -resultValue;
                    isFirst = false;
                } else {
                    if (Debug.infoOn()) Debug.logInfo("Field not found with field-name " + fieldName + ", and map-name " + mapName + "using a default of 0");
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
                // Debug.logInfo("sub total so far: " + resultValue);
            }
            // Debug.logInfo("calcValue calcop: " + resultValue + "(field=" + fieldName + ", map=" + mapName + ")");
            return resultValue;
        }
    }
}
