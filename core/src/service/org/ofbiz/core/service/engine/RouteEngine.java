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
package org.ofbiz.core.service.engine;

import java.util.*;

import org.ofbiz.core.service.*;

/**
 * RouteEngine.java
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class RouteEngine implements GenericEngine {
    
    public RouteEngine(ServiceDispatcher dispatcher) { }

    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runSync(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map)
     */    
    public Map runSync(String localName, ModelService modelService, Map context) throws GenericServiceException {
        return new HashMap();
    }

    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runSyncIgnore(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map)
     */
    public void runSyncIgnore(String localName, ModelService modelService, Map context) throws GenericServiceException {
        return;
    }

    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runAsync(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map, org.ofbiz.core.service.GenericRequester, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map context, GenericRequester requester, boolean persist) throws GenericServiceException {
        requester.receiveResult(new HashMap());
    }

    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runAsync(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map context, boolean persist) throws GenericServiceException {
        return;
    }
}
