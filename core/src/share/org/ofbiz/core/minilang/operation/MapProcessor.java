
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
 * <p><b>Title:</b> Map Processor Main Class
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
public class MapProcessor {
    String name;
    List makeInStrings = new LinkedList();
    List simpleMapProcesses = new LinkedList();

    public MapProcessor(Element simpleMapProcessorElement) {
        name = simpleMapProcessorElement.getAttribute("name");

        List makeInStringElements = UtilXml.childElementList(simpleMapProcessorElement, "make-in-string");
        Iterator misIter = makeInStringElements.iterator();
        while (misIter.hasNext()) {
            Element makeInStringElement = (Element) misIter.next();
            MakeInString makeInString = new MakeInString(makeInStringElement);
            makeInStrings.add(makeInString);
        }

        List simpleMapProcessElements = UtilXml.childElementList(simpleMapProcessorElement, "process");
        Iterator strProcIter = simpleMapProcessElements.iterator();
        while (strProcIter.hasNext()) {
            Element simpleMapProcessElement = (Element) strProcIter.next();
            SimpleMapProcess strProc = new SimpleMapProcess(simpleMapProcessElement);
            simpleMapProcesses.add(strProc);
        }
    }

    public String getName() {
        return name;
    }

    public void exec(Map inMap, Map results, List messages, Locale locale, ClassLoader loader) {
        if (makeInStrings != null && makeInStrings.size() > 0) {
            Iterator misIter = makeInStrings.iterator();
            while (misIter.hasNext()) {
                MakeInString makeInString = (MakeInString) misIter.next();
                makeInString.exec(inMap, results, messages, locale, loader);
            }
        }

        if (simpleMapProcesses != null && simpleMapProcesses.size() > 0) {
            Iterator strPrsIter = simpleMapProcesses.iterator();
            while (strPrsIter.hasNext()) {
                SimpleMapProcess simpleMapProcess = (SimpleMapProcess) strPrsIter.next();
                simpleMapProcess.exec(inMap, results, messages, locale, loader);
            }
        }
    }
}
