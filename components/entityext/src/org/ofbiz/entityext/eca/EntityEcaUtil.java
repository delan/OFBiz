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
package org.ofbiz.entityext.eca;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.config.GenericConfigException;
import org.ofbiz.base.config.MainResourceHandler;
import org.ofbiz.base.config.ResourceHandler;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilCache;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.config.DelegatorInfo;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.config.EntityEcaReaderInfo;
import org.w3c.dom.Element;

/**
 * EntityEcaUtil
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.1
 */
public class EntityEcaUtil {

    public static final String module = EntityEcaUtil.class.getName();

    public static UtilCache entityEcaReaders = new UtilCache("entity.EcaReaders", 0, 0, false);
    
    public static Map getEntityEcaCache(String entityEcaReaderName) {
        Map ecaCache = (Map) entityEcaReaders.get(entityEcaReaderName);
        if (ecaCache == null) {
            synchronized (EntityEcaUtil.class) {
                ecaCache = (Map) entityEcaReaders.get(entityEcaReaderName);
                if (ecaCache == null) {
                    ecaCache = new HashMap();
                    readConfig(entityEcaReaderName, ecaCache);
                    entityEcaReaders.put(entityEcaReaderName, ecaCache);
                }
            }
        }
        return ecaCache;
    }
    
    public static String getEntityEcaReaderName(String delegatorName) {
        DelegatorInfo delegatorInfo = EntityConfigUtil.getDelegatorInfo(delegatorName);
        if (delegatorInfo == null) {
            Debug.logError("BAD ERROR: Could not find delegator config with name: " + delegatorName, module);
            return null;
        }
        return delegatorInfo.entityEcaReader;
    }
    
    protected static void readConfig(String entityEcaReaderName, Map ecaCache) {
        EntityEcaReaderInfo entityEcaReaderInfo = EntityConfigUtil.getEntityEcaReaderInfo(entityEcaReaderName);
        if (entityEcaReaderInfo == null) {
            Debug.logError("BAD ERROR: Could not find entity-eca-reader config with name: " + entityEcaReaderName, module);
            return;
        }
        
        Iterator eecaResourceIter = entityEcaReaderInfo.resourceElements.iterator();
        while (eecaResourceIter.hasNext()) {
            Element eecaResourceElement = (Element) eecaResourceIter.next();
            ResourceHandler handler = new MainResourceHandler(EntityConfigUtil.ENTITY_ENGINE_XML_FILENAME, eecaResourceElement);
            addEcaDefinitions(handler, ecaCache);
        }

        // get all of the component resource eca stuff, ie specified in each ofbiz-component.xml file
        List componentResourceInfos = ComponentConfig.getAllEntityResourceInfos("eca");
        Iterator componentResourceInfoIter = componentResourceInfos.iterator();
        while (componentResourceInfoIter.hasNext()) {
            ComponentConfig.EntityResourceInfo componentResourceInfo = (ComponentConfig.EntityResourceInfo) componentResourceInfoIter.next();
            if (entityEcaReaderName.equals(componentResourceInfo.readerName)) {
                addEcaDefinitions(componentResourceInfo.createResourceHandler(), ecaCache);
            }
        }
    }

    protected static void addEcaDefinitions(ResourceHandler handler, Map ecaCache) {
        Element rootElement = null;
        try {
            rootElement = handler.getDocument().getDocumentElement();
        } catch (GenericConfigException e) {
            Debug.logError(e, module);
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
                eventMap.put(eventName, rules);
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
