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
 * An event operation that calls a simple map processor inlined or from a separate file
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class CallSimpleMapProcessor extends MethodOperation {
    String xmlResource;
    String processorName;
    String inMapName;
    String outMapName;
    String errorListName;
    
    MapProcessor inlineMapProcessor = null;

    public CallSimpleMapProcessor(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        xmlResource = element.getAttribute("xml-resource");
        processorName = element.getAttribute("processor-name");
        inMapName = element.getAttribute("in-map-name");
        outMapName = element.getAttribute("out-map-name");

        errorListName = element.getAttribute("error-list-name");
        if (errorListName == null || errorListName.length() == 0)
            errorListName = "error_list";
        
        Element simpleMapProcessorElement = UtilXml.firstChildElement(element, "simple-map-processor");
        if (simpleMapProcessorElement != null) {
            inlineMapProcessor = new MapProcessor(simpleMapProcessorElement);
        }
    }

    public boolean exec(MethodContext methodContext) {
        List messages = (List) methodContext.getEnv(errorListName);
        if (messages == null) {
            messages = new LinkedList();
            methodContext.putEnv(errorListName, messages);
        }

        Map inMap = (Map) methodContext.getEnv(inMapName);
        if (inMap == null) {
            inMap = new HashMap();
            methodContext.putEnv(inMapName, inMap);
        }

        Map outMap = (Map) methodContext.getEnv(outMapName);
        if (outMap == null) {
            outMap = new HashMap();
            methodContext.putEnv(outMapName, outMap);
        }

        //run external map processor first
        if (xmlResource != null && xmlResource.length() > 0 &&
                processorName != null && processorName.length() > 0) {
            try {
                org.ofbiz.core.minilang.SimpleMapProcessor.runSimpleMapProcessor(
                        xmlResource, processorName, inMap, outMap, messages, 
                        (methodContext.getRequest() != null ? methodContext.getRequest().getLocale() : null),
                        methodContext.getLoader());
            } catch (MiniLangException e) {
                messages.add("Error running SimpleMapProcessor in XML file \"" + xmlResource + "\": " + e.toString());
            }
        }

        //run inlined map processor last so it can override the external map processor
        if (inlineMapProcessor != null) {
            inlineMapProcessor.exec(inMap, outMap, messages, 
                    (methodContext.getRequest() != null ? methodContext.getRequest().getLocale() : null),
                    methodContext.getLoader());
        }
        
        return true;
    }
}
