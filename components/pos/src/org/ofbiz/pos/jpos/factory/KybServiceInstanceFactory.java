/*
 * $Id$
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
package org.ofbiz.pos.jpos.factory;

import java.util.HashMap;
import java.util.Map;

import jpos.JposConst;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;

import org.ofbiz.base.util.ObjectType;
import org.ofbiz.pos.jpos.service.BaseKybService;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class KybServiceInstanceFactory extends Object implements JposServiceInstanceFactory {

    public static final String module = KybServiceInstanceFactory.class.getName();
    private static Map serviceMap = new HashMap();

    public JposServiceInstance createInstance(String logicalName, JposEntry entry) throws JposException {
        // check to see if we have a service class property
        if (!entry.hasPropertyWithName(JposEntry.SERVICE_CLASS_PROP_NAME)) {
            throw new JposException(JposConst.JPOS_E_NOSERVICE, "serviceClass property not found!");
        }

        String className = (String) entry.getPropertyValue(JposEntry.SERVICE_CLASS_PROP_NAME);
        BaseKybService service = (BaseKybService) serviceMap.get(className);

        if (service != null) {
            service.setEntry(entry);
        } else {
            try {
                Object obj = ObjectType.getInstance(className);
                if (obj == null) {
                    throw new JposException(JposConst.JPOS_E_NOEXIST, "unable to locate serviceClass");
                }                

                if (!(obj instanceof JposServiceInstance)) {
                    throw new JposException(JposConst.JPOS_E_NOSERVICE, "serviceClass is not an instance of JposServiceInstance");
                } else if (!(obj instanceof BaseKybService)) {
                    throw new JposException(JposConst.JPOS_E_NOSERVICE, "serviceClass is not an instance of BaseKybService");
                } else {
                    service = (BaseKybService) obj;
                    service.setEntry(entry);
                    serviceMap.put(className, service);
                }
            } catch (Exception e) {
                throw new JposException(JposConst.JPOS_E_NOSERVICE, "Error creating the service instance [" + className + "]", e);
            }
        }

        return service;
    }
}
