/*
 * $Id: ClassLoaderContainer.java,v 1.2 2004/05/22 20:59:38 ajzeneski Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.base.container;

import org.ofbiz.base.util.CachedClassLoader;
import org.ofbiz.base.start.Classpath;

import java.net.URL;

/**
 * ClassLoader Container; Created a CachedClassLoader for use by all following containers
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.0
 */
public class ClassLoaderContainer implements Container {

    protected static CachedClassLoader cl = null;

    /**
     * @see org.ofbiz.base.container.Container#start(java.lang.String)
     */
    public boolean start(String configFileLocation) throws ContainerException {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = Classpath.class.getClassLoader();
        }
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }

        cl = new CachedClassLoader(new URL[0], parent);
        Thread.currentThread().setContextClassLoader(cl);
        return true;
    }

    /**
     * @see org.ofbiz.base.container.Container#stop()
     */
    public void stop() throws ContainerException {
    }

    public static ClassLoader getClassLoader() {
        if (cl != null) {
            return cl;
        } else {
            return ClassLoader.getSystemClassLoader();
        }
    }
}
