/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.service.group;

import java.util.Iterator;
import java.util.List;

import org.ofbiz.core.config.GenericConfigException;
import org.ofbiz.core.config.MainResourceHandler;
import org.ofbiz.core.config.ResourceHandler;
import org.ofbiz.core.service.config.ServiceConfigUtil;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilCache;
import org.ofbiz.core.util.UtilXml;
import org.w3c.dom.Element;

/**
 * ServiceGroupReader.java
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ServiceGroupReader {
    
    public static final String module = ServiceGroupReader.class.getName();

    public static UtilCache groupsCache = new UtilCache("service.ServiceGroups", 0, 0, false);
    
    public static void readConfig() {
        Element rootElement = null;

        try {
            rootElement = ServiceConfigUtil.getXmlRootElement();
        } catch (GenericConfigException e) {
            Debug.logError(e, "Error getting Service Engine XML root element", module);
            return;
        }

        List serviceGroupElements = UtilXml.childElementList(rootElement, "service-groups");
        Iterator groupsIter = serviceGroupElements.iterator();

        while (groupsIter.hasNext()) {
            Element serviceGroupElement = (Element) groupsIter.next();
            ResourceHandler handler = new MainResourceHandler(ServiceConfigUtil.SERVICE_ENGINE_XML_FILENAME, serviceGroupElement);
            addGroupDefinitions(handler);
        }
    }    
    
    public static void addGroupDefinitions(ResourceHandler handler) {
        Element rootElement = null;

        try {
            rootElement = handler.getDocument().getDocumentElement();
        } catch (GenericConfigException e) {
            Debug.logError(e, module);
            return;
        }
        List groupList = UtilXml.childElementList(rootElement, "group");
        Iterator groupIt = groupList.iterator();
        int numDefs = 0;

        while (groupIt.hasNext()) {
            Element group = (Element) groupIt.next();
            String groupName = group.getAttribute("name");
            groupsCache.put(groupName, new GroupModel(group));        
            numDefs++;
        }
        Debug.logImportant("Loaded " + numDefs + " Group definitions from " + handler.getLocation() + " in loader " + handler.getLoaderName(), module);
    }

    public static GroupModel getGroupModel(String serviceName) {
        if (groupsCache.size() == 0)
            ServiceGroupReader.readConfig();
        return (GroupModel) groupsCache.get(serviceName);
    }
}
