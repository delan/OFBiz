/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.net.*;
import java.util.*;

/**
 * <p><b>Title:</b> Global Service Dispatcher
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
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on November 8, 2001
 */
public class ServiceContext {
    
    protected Map modelServices;
    protected Collection readers;
    protected ClassLoader loader;
    
    /** Creates new ServiceContext */
    public ServiceContext(Collection readers, ClassLoader loader) {
        this.readers = readers;
        this.loader = loader;
        this.addReaders(readers);        
    }
    
    /** Gets the classloader of this context
     *@return ClassLoader of the context
     */
    public ClassLoader getClassLoader() {
        return this.loader;
    }

   /** Gets the GenericServiceModel instance that corresponds to given the name
     *@param serviceName Name of the service
     *@return GenericServiceModel that corresponds to the serviceName
     */
    public ModelService getModelService(String serviceName) throws GenericServiceException {
        if ( !modelServices.containsKey(serviceName) )
            throw new GenericServiceException("Illegal service name.");
        return (ModelService)modelServices.get(serviceName);
    }
        
    public void addReaders(Collection readerURLs) {
        if(readerURLs == null) return;
        Iterator urlIter = readerURLs.iterator();
        while(urlIter.hasNext()) {
            URL readerURL = (URL)urlIter.next();
            this.addReader(readerURL);
        }
    }
    
    public void addReader(URL readerURL) {
        ModelServiceReader reader = ModelServiceReader.getModelServiceReader(readerURL);
        modelServices.putAll(reader.getModelServices());
    }
    
    
}
