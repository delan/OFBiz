/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.service.engine;

import java.util.*;
import java.lang.reflect.*;
import org.w3c.dom.Element;

import org.ofbiz.core.config.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.config.*;
import org.ofbiz.core.service.*;

/**
 * Generic Engine Factory
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class GenericEngineFactory {

    protected static Map engines = new HashMap();

    /** 
     * Gets the GenericEngine instance that corresponds to given the name
     *@param engineName Name of the engine
     *@return GenericEngine that corresponds to the engineName
     */
    public static GenericEngine getGenericEngine(String engineName, ServiceDispatcher dispatcher)
        throws GenericServiceException {

        Element rootElement = null;

        try {
            rootElement = ServiceConfigUtil.getXmlRootElement();
        } catch (GenericConfigException e) {
            throw new GenericServiceException("Error getting Service Engine XML root element", e);
        }
        Element engineElement = UtilXml.firstChildElement(rootElement, "engine", "name", engineName);

        if (engineElement == null) {
            throw new GenericServiceException("Cannot find an engine definition for the engine name [" + engineName + "] in the serviceengine.xml file");
        }

        String className = engineElement.getAttribute("class");

        GenericEngine engine = (GenericEngine) engines.get(engineName);

        if (engine == null) {
            synchronized (GenericEngineFactory.class) {
                engine = (GenericEngine) engines.get(engineName);
                if (engine == null) {
                    Class[] paramTypes = new Class[] {ServiceDispatcher.class};
                    Object[] params = new Object[] {dispatcher};

                    try {
                        Class c = Class.forName(className);
                        Constructor cn = c.getConstructor(paramTypes);
                        engine = (GenericEngine) cn.newInstance(params);
                    } catch (Exception e) {
                        throw new GenericServiceException(e.getMessage(), e);
                    }
                    if (engine != null) {
                        engines.put(engineName, engine);
                    }
                }
            }
        }

        return engine;
    }
}

