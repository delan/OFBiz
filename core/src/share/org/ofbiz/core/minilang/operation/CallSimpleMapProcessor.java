
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
 * <p><b>Title:</b> An event operation that calls a simple map processor minilang file
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
public class CallSimpleMapProcessor extends EventOperation {
    String xmlResource;
    String processorName;
    String inMapName;
    String outMapName;
    String errorListName;

    public CallSimpleMapProcessor(Element element, SimpleEvent simpleEvent) {
        super(element, simpleEvent);
        xmlResource = element.getAttribute("xml-resource");
        processorName = element.getAttribute("processor-name");
        inMapName = element.getAttribute("in-map-name");
        outMapName = element.getAttribute("out-map-name");
        errorListName = element.getAttribute("error-list-name");
        if (errorListName == null || errorListName.length() == 0)
            errorListName = "_error_list_";
    }

    public boolean exec(Map env, HttpServletRequest request, ClassLoader loader) {
        List messages = (List) env.get(errorListName);
        if (messages == null) {
            messages = new LinkedList();
            env.put(errorListName, messages);
        }

        Map inMap = (Map) env.get(inMapName);
        if (inMap == null) {
            inMap = new HashMap();
            env.put(inMapName, inMap);
        }

        Map outMap = (Map) env.get(outMapName);
        if (outMap == null) {
            outMap = new HashMap();
            env.put(outMapName, outMap);
        }

        try {
            org.ofbiz.core.minilang.SimpleMapProcessor.runSimpleMapProcessor(xmlResource, processorName, inMap, outMap, messages, request.getLocale());
        } catch (MiniLangException e) {
            messages.add("Error running SimpleMapProcessor in XML file \"" + xmlResource + "\": " + e.toString());
        }

        return true;
    }
}
