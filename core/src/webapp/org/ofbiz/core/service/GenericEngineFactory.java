/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.util.*;
import java.lang.reflect.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Engine Factory Class
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Oct 20 2001
 *@version    1.0
 */
public class GenericEngineFactory {
    
    /** Gets the GenericEngine instance that corresponds to given the name
     *@param engineName Name of the engine
     *@return GenericEngine that corresponds to the engineName
     */
    public static GenericEngine getGenericEngine(String engineName, ServiceDispatcher dispatcher) throws GenericServiceException {
        String className = UtilProperties.getPropertyValue("org.ofbiz.core.service.engine.properties",engineName + ".engine","org.ofbiz.core.service.StandardJavaEngine");
        Class[] paramTypes = new Class[] { ServiceDispatcher.class };
        Object[] params = new Object[] { dispatcher };
        GenericEngine engine = null;
        try {
            Class c = Class.forName(className);
            Constructor cn = c.getConstructor(paramTypes);
            engine = (GenericEngine) cn.newInstance(params);
        }
        catch ( Exception e ) {
            throw new GenericServiceException(e.getMessage(),e);
        }
        return engine;
    }
}
