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
package org.ofbiz.entityext;

import java.util.HashMap;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;

/**
 * EntityEcaUtil
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev:$
 * @since      2.1
 */
public class EntityServiceFactory {

    public static final String module = EntityServiceFactory.class.getName();

    public static HashMap delegatorDispatchers = new HashMap();    
    
    public static LocalDispatcher getLocalDispatcher(GenericDelegator delegator) {
        String delegatorName = delegator.getDelegatorName();
        GenericDispatcher dispatcher = (GenericDispatcher) delegatorDispatchers.get(delegatorName);
        if (dispatcher == null) {
            synchronized (EntityServiceFactory.class) {
                dispatcher = (GenericDispatcher) delegatorDispatchers.get(delegatorName);
                if (dispatcher == null) {
                    dispatcher = new GenericDispatcher("entity-" + delegatorName, delegator);
                    delegatorDispatchers.put(delegatorName, dispatcher);
                }
            }
        }
        return dispatcher;
    }
    
    public static DispatchContext getDispatchContext(GenericDelegator delegator) {
        LocalDispatcher dispatcher = getLocalDispatcher(delegator);
        if (dispatcher == null) return null;
        return dispatcher.getDispatchContext();
    }
}
