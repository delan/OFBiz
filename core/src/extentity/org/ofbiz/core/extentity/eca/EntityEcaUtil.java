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
package org.ofbiz.core.extentity.eca;

import java.util.*;
import org.w3c.dom.*;

import org.ofbiz.core.config.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.entity.config.*;
import org.ofbiz.core.extentity.*;

/**
 * EntityEcaUtil
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class EntityEcaUtil {

    public static final String module = EntityEcaUtil.class.getName();

    public static UtilCache entityEcaReaders = new UtilCache("entity.EcaReaders", 0, 0, false);
    
    public static Map getEntityEcaCache(String entityEcaReaderName) {
        Map ecaCache = (Map) entityEcaReaders.get(entityEcaReaderName);
        if (ecaCache == null) {
            ecaCache = new HashMap();
            readConfig(entityEcaReaderName, ecaCache);
            entityEcaReaders.put(entityEcaReaderName, ecaCache);
        }
        return ecaCache;
    }
    
    public static String getEntityEcaReaderName(String delegatorName) {
        EntityConfigUtil.DelegatorInfo delegatorInfo = EntityConfigUtil.getDelegatorInfo(delegatorName);
        if (delegatorInfo == null) {
            Debug.logError("BAD ERROR: Could not find delegator config with name: " + delegatorName);
            return null;
        }
        return delegatorInfo.entityEcaReader;
    }
    
    protected static void readConfig(String entityEcaReaderName, Map ecaCache) {
        EntityConfigUtil.EntityEcaReaderInfo entityEcaReaderInfo = EntityConfigUtil.getEntityEcaReaderInfo(entityEcaReaderName);
        if (entityEcaReaderInfo == null) {
            Debug.logError("BAD ERROR: Could not find entity-eca-reader config with name: " + entityEcaReaderName);
            return;
        }
        
        Iterator eecaResourceIter = entityEcaReaderInfo.resourceElements.iterator();
        while (eecaResourceIter.hasNext()) {
            Element eecaResourceElement = (Element) eecaResourceIter.next();
            ResourceHandler handler = new ResourceHandler(EntityConfigUtil.ENTITY_ENGINE_XML_FILENAME, eecaResourceElement);
            addEcaDefinitions(handler, ecaCache);
        }
    }

    protected static void addEcaDefinitions(ResourceHandler handler, Map ecaCache) {
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
            String entityName = e.getAttribute("entity");
            String eventName = e.getAttribute("event");
            Map eventMap = (Map) ecaCache.get(entityName);
            List rules = null;
            if (eventMap == null) {
                eventMap = new HashMap();
                rules = new LinkedList();
                ecaCache.put(entityName, eventMap);
            } else {
                rules = (List) eventMap.get(eventName);
                if (rules == null) {
                    rules = new LinkedList();
                    eventMap.put(eventName, rules);
                }
            }
            rules.add(new EntityEcaRule(e));
            numDefs++;
        }
        Debug.logImportant("Loaded " + numDefs + " Entity ECA definitions from " + handler.getLocation() + " in loader " + handler.getLoaderName(), module);
    }
}
