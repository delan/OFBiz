/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.ofbiz.core.service.eca;

import java.util.*;
import org.w3c.dom.*;

import org.ofbiz.core.config.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.config.*;

/**
 * ECAUtil
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Jul 27, 2002
 * @version    1.0
 */
public class ECAUtil {

    public static final String module = ECAUtil.class.getName();
    
    //for now just use a Map because we aren't doing reloading right now anyway...
    //public static UtilCache ecaCache = new UtilCache("service.ServiceECAs", 0, 0, false);
    public static Map ecaCache = new HashMap();

    public static void readConfig() {
        Element rootElement = null;
        try {
            rootElement = ServiceConfigUtil.getXmlRootElement();
        } catch (GenericConfigException e) {
            Debug.logError(e, "Error getting Service Engine XML root element");
            return;
        }
        
        List serviceEcasElements = UtilXml.childElementList(rootElement, "service-ecas");
        Iterator secasIter = serviceEcasElements.iterator();
        while (secasIter.hasNext()) {
            Element serviceEcasElement = (Element) secasIter.next();
            ResourceHandler handler = new ResourceHandler(ServiceConfigUtil.SERVICE_ENGINE_XML_FILENAME, serviceEcasElement);
            addEcaDefinitions(handler);
        }
    }
    
    public static void addEcaDefinitions(ResourceHandler handler) {
        Element rootElement = null;
        try {
            rootElement = handler.getDocument().getDocumentElement();
        } catch (GenericConfigException e) {
            Debug.logError(e);
            return;
        }
        List ecaList = UtilXml.childElementList(rootElement, "eca");
        Iterator ecaIt = ecaList.iterator();
        int numDefs = 0;
        while (ecaIt.hasNext()) {
            Element e = (Element) ecaIt.next();
            String serviceName = e.getAttribute("service");
            String eventName = e.getAttribute("event");
            Map eventMap = (Map) ecaCache.get(serviceName);
            List rules = null;
            if (eventMap == null) {
                eventMap = new HashMap();
                rules = new LinkedList();
            } else {
                rules = (List) eventMap.get(eventName);
                if (rules == null)
                    rules = new LinkedList();
            }
            rules.add(new EventConditionAction(e));
            eventMap.put(eventName, rules);
            ecaCache.put(serviceName, eventMap);
            numDefs++;
        }
        Debug.logImportant("Loaded " + numDefs + " ECA definitions from " + handler.getLocation() + " in loader " + handler.getLoaderName());
    }

    public static Map getServiceEventMap(String serviceName) {
        return (Map) ecaCache.get(serviceName);
    }
    
    public static void evalConditions(String serviceName, Map eventMap, String event, DispatchContext dctx, Map context, Map result, boolean isError) throws GenericServiceException {
        //if the eventMap is passed we save a HashMap lookup, but if not that's okay we'll just look it up now
        if (eventMap == null) eventMap = getServiceEventMap(serviceName);
        if (eventMap == null || eventMap.size() == 0) {
            return;
        }

        List conditions = (List) eventMap.get(event);
        if (conditions == null || conditions.size() == 0) {
            return;
        }

        Iterator i = conditions.iterator();
        if (i.hasNext() && Debug.verboseOn()) Debug.logVerbose("Running ECA (" + event + ").");
        while (i.hasNext()) {
            EventConditionAction eca = (EventConditionAction) i.next();
            eca.eval(serviceName, dctx, context, result, isError);
        }
    }
}
