/*
 * $Id$
 *
 * Copyright (c) 2002-2003 The Open For Business Project - www.ofbiz.org
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.config.GenericConfigException;
import org.ofbiz.core.config.ResourceHandler;
import org.ofbiz.core.service.DispatchContext;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.config.ServiceConfigUtil;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilXml;
import org.w3c.dom.Element;

/**
 * ServiceEcaUtil
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ServiceEcaUtil {

    public static final String module = ServiceEcaUtil.class.getName();
    
    //NOTE: the Service ECA cache is a Map instead of a UtilCache because there is no way to 
    // reload ecas for inidividual services if their cache line is cleared, leading to potential 
    // problems; the symantics of UtilCaches dictate that the user of the cache should know how
    // to reload ANY cleared cache line because a user could muck up the system pretty bad otherwise 
    public static Map ecaCache = null;
    //public static UtilCache ecaCache = new UtilCache("service.ServiceECAs", 0, 0, false);

    public static void reloadConfig() {
        ServiceEcaUtil.ecaCache = null;
        readConfig();
    }

    public static void readConfig() {
        ServiceEcaUtil.ecaCache = new HashMap();
        
        Element rootElement = null;
        try {
            rootElement = ServiceConfigUtil.getXmlRootElement();
        } catch (GenericConfigException e) {
            Debug.logError(e, "Error getting Service Engine XML root element", module);
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
                ecaCache.put(serviceName, eventMap);
                eventMap.put(eventName, rules);
            } else {
                rules = (List) eventMap.get(eventName);
                if (rules == null) {
                    rules = new LinkedList();
                    eventMap.put(eventName, rules);
                }
            }
            rules.add(new ServiceEcaRule(e));
            numDefs++;
        }
        Debug.logImportant("Loaded " + numDefs + " Service ECA definitions from " + handler.getLocation() + " in loader " + handler.getLoaderName(), module);
    }

    public static Map getServiceEventMap(String serviceName) {
        if (ServiceEcaUtil.ecaCache == null) ServiceEcaUtil.readConfig();
        return (Map) ServiceEcaUtil.ecaCache.get(serviceName);
    }

    public static void evalRules(String serviceName, Map eventMap, String event, DispatchContext dctx, Map context, Map result, boolean isError) throws GenericServiceException {
        // if the eventMap is passed we save a HashMap lookup, but if not that's okay we'll just look it up now
        if (eventMap == null) eventMap = getServiceEventMap(serviceName);
        if (eventMap == null || eventMap.size() == 0) {
            return;
        }

        List rules = (List) eventMap.get(event);
        if (rules == null || rules.size() == 0) {
            return;
        }

        Iterator i = rules.iterator();
        if (i.hasNext() && Debug.verboseOn()) Debug.logVerbose("Running ECA (" + event + ").", module);
        while (i.hasNext()) {
            ServiceEcaRule eca = (ServiceEcaRule) i.next();
            eca.eval(serviceName, dctx, context, result, isError);
        }
    }
}
