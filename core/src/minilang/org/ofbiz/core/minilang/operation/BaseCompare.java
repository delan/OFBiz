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

import org.w3c.dom.*;
import org.ofbiz.core.util.*;

import org.ofbiz.core.minilang.*;

/**
 * Abstract class providing functionality for the compare SimpleMapOperations
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public abstract class BaseCompare extends SimpleMapOperation {
    String operator;
    String type;
    String format;

    public BaseCompare(Element element, SimpleMapProcess simpleMapProcess) {
        super(element, simpleMapProcess);
        this.operator = element.getAttribute("operator");
        this.type = element.getAttribute("type");
        this.format = element.getAttribute("format");
        
        /* -- Let ObjectType handle the default --
            if (this.format == null || this.format.length() == 0) {
                if ("Date".equals(type)) {
                    this.format = "yyyy-MM-dd";
                } else if ("Time".equals(type)) {
                    this.format = "HH:mm:ss";
                } else if ("Timestamp".equals(type)) {
                    this.format = "yyyy-MM-dd HH:mm:ss";
                }
            }
        */
    }

    public void doCompare(Object value1, Object value2, List messages, Locale locale, ClassLoader loader) {
        Boolean success = BaseCompare.doRealCompare(value1, value2, this.operator, this.type, this.format, messages, locale, loader);
        if (success != null && success.booleanValue() == false) {
            addMessage(messages, loader);
        }
    }

    public void exec(Map inMap, Map results, List messages, Locale locale, ClassLoader loader) {
    }
    
    public static Boolean doRealCompare(Object value1, Object value2, String operator, String type, String format, 
            List messages, Locale locale, ClassLoader loader) {
        Debug.logVerbose("[BaseCompare.doCompare] Comparing value1: \"" + value1 + "\", value2:\"" + value2 + "\"");

        int result = 0;

        Object convertedValue1 = null;
        try {
            convertedValue1 = ObjectType.simpleTypeConvert(value1, type, format, locale);
        } catch (GeneralException e) {
            messages.add("Could not convert value1 for comparison: " + e.getMessage());
            return null;
        }

        Object convertedValue2 = null;
        try {
            convertedValue2 = ObjectType.simpleTypeConvert(value2, type, format, locale);
        } catch (GeneralException e) {
            messages.add("Could not convert value2 for comparison: " + e.getMessage());
            return null;
        }

        if (convertedValue1 == null) {
            Debug.logVerbose("Value1 was null, cannot complete comparison");
            return null;
        }
        if (convertedValue2 == null) {
            Debug.logVerbose("Value2 was null, cannot complete comparison");
            return null;
        }

        if ("contains".equals(operator)) {
            if (!"String".equals(type)) {
                messages.add("Error in MiniLang XML file: cannot do a contains compare with a non-String type");
                return null;
            }

            String str1 = (String) convertedValue1;
            String str2 = (String) convertedValue2;
            if (str1.indexOf(str2) < 0) {
                return Boolean.FALSE;
            }
        }

        if ("String".equals(type)) {
            String str1 = (String) convertedValue1;
            String str2 = (String) convertedValue2;
            if (str1.length() == 0 || str2.length() == 0) {
                return null;
            }
            result = str1.compareTo(str2);
        } else if ("Double".equals(type) || "Float".equals(type) || "Long".equals(type) || "Integer".equals(type)) {
            Number tempNum = (Number) convertedValue1;
            double value1Double = tempNum.doubleValue();

            tempNum = (Number) convertedValue2;
            double value2Double = tempNum.doubleValue();

            if (value1Double < value2Double)
                result = -1;
            else if (value1Double < value2Double)
                result = 1;
            else
                result = 0;
        } else if ("Date".equals(type)) {
            java.sql.Date value1Date = (java.sql.Date) convertedValue1;
            java.sql.Date value2Date = (java.sql.Date) convertedValue2;
            result = value1Date.compareTo(value2Date);
        } else if ("Time".equals(type)) {
            java.sql.Time value1Time = (java.sql.Time) convertedValue1;
            java.sql.Time value2Time = (java.sql.Time) convertedValue2;
            result = value1Time.compareTo(value2Time);
        } else if ("Timestamp".equals(type)) {
            java.sql.Timestamp value1Timestamp = (java.sql.Timestamp) convertedValue1;
            java.sql.Timestamp value2Timestamp = (java.sql.Timestamp) convertedValue2;
            result = value1Timestamp.compareTo(value2Timestamp);
        } else {
            messages.add("Type \"" + type + "\" specified for compare not supported.");
            return null;
        }

        //Debug.logInfo("[BaseCompare.doCompare] Got Compare result: " + result + ", operator: " + operator);
        if ("less".equals(operator)) {
            if (result >= 0)
                return Boolean.FALSE;
        } else if ("greater".equals(operator)) {
            if (result <= 0)
                return Boolean.FALSE;
        } else if ("less-equals".equals(operator)) {
            if (result > 0)
                return Boolean.FALSE;
        } else if ("greater-equals".equals(operator)) {
            if (result < 0)
                return Boolean.FALSE;
        } else if ("equals".equals(operator)) {
            if (result != 0)
                return Boolean.FALSE;
        } else if ("not-equals".equals(operator)) {
            if (result == 0)
                return Boolean.FALSE;
        } else {
            messages.add("Specified compare operator \"" + operator + "\" not known.");
            return null;
        }
        
        return Boolean.TRUE;
    }
}
