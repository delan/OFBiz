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
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.*;

/**
 * A complete string process for a given field; contains multiple string operations
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class SimpleMapProcess {
    List simpleMapOperations = new LinkedList();
    String field = "";

    public SimpleMapProcess(Element simpleMapProcessElement) {
        this.field = simpleMapProcessElement.getAttribute("field");
        readOperations(simpleMapProcessElement);
    }

    public String getFieldName() {
        return field;
    }

    public void exec(Map inMap, Map results, List messages, Locale locale, ClassLoader loader) {
        Iterator strOpsIter = simpleMapOperations.iterator();
        while (strOpsIter.hasNext()) {
            SimpleMapOperation simpleMapOperation = (SimpleMapOperation) strOpsIter.next();
            simpleMapOperation.exec(inMap, results, messages, locale, loader);
        }
    }

    void readOperations(Element simpleMapProcessElement) {
        List operationElements = UtilXml.childElementList(simpleMapProcessElement, null);
        if (operationElements != null && operationElements.size() > 0) {
            Iterator operElemIter = operationElements.iterator();
            while (operElemIter.hasNext()) {
                Element curOperElem = (Element) operElemIter.next();
                String nodeName = curOperElem.getNodeName();
                if ("validate-method".equals(nodeName)) {
                    simpleMapOperations.add(new ValidateMethod(curOperElem, this));
                } else if ("compare".equals(nodeName)) {
                    simpleMapOperations.add(new Compare(curOperElem, this));
                } else if ("compare-field".equals(nodeName)) {
                    simpleMapOperations.add(new CompareField(curOperElem, this));
                } else if ("regexp".equals(nodeName)) {
                    simpleMapOperations.add(new Regexp(curOperElem, this));
                } else if ("not-empty".equals(nodeName)) {
                    simpleMapOperations.add(new NotEmpty(curOperElem, this));
                } else if ("copy".equals(nodeName)) {
                    simpleMapOperations.add(new Copy(curOperElem, this));
                } else if ("convert".equals(nodeName)) {
                    simpleMapOperations.add(new Convert(curOperElem, this));
                } else {
                    Debug.logWarning("[SimpleMapProcessor.SimpleMapProcess.readOperations] Operation element \"" + nodeName + "\" not recognized");
                }
            }
        }
    }
}
