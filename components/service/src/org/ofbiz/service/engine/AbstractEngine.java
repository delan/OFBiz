/*
 * $Id: AbstractEngine.java,v 1.1 2004/07/01 15:27:13 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.service.engine;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

import org.ofbiz.service.ServiceDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.config.ServiceConfigUtil;
import org.ofbiz.base.config.GenericConfigException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilXml;

import org.w3c.dom.Element;

/**
 * Abstract Service Engine
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public abstract class AbstractEngine implements GenericEngine {

    public static final String module = AbstractEngine.class.getName();
    protected static Map locationMap = null;

    protected ServiceDispatcher dispatcher = null;

    protected AbstractEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        initLocations();
    }

    // creates the location alias map
    protected synchronized void initLocations() {
        if (locationMap == null) {
            locationMap = new HashMap();

            Element root = null;
            try {
                root = ServiceConfigUtil.getXmlRootElement();
            } catch (GenericConfigException e) {
                Debug.logError(e, module);
            }

            if (root != null) {
                List locationElements = UtilXml.childElementList(root, "service-location");
                if (locationElements != null) {
                    Iterator i = locationElements.iterator();
                    while (i.hasNext()) {
                        Element e = (Element) i.next();
                        locationMap.put(e.getAttribute("name"), e.getAttribute("location"));
                    }
                }
            }
            Debug.logInfo("Loaded Service Locations : " + locationMap, module);
        }
    }

    // uses the lookup map to determin if the location has been aliased in serviceconfig.xml
    protected String getLocation(ModelService model) {
        if (locationMap.containsKey(model.location)) {
            return (String) locationMap.get(model.location);
        } else {
            return model.location;
        }
    }
}
