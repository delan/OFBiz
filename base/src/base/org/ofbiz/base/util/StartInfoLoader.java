/*
 * $Id: StartInfoLoader.java,v 1.1 2004/03/30 22:35:09 ajzeneski Exp $
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
package org.ofbiz.base.util;

import org.ofbiz.base.start.StartupLoader;
import org.ofbiz.base.start.Start;
import org.ofbiz.base.start.StartupException;

/**
 * StartInfoLoader - Loader which exposes the startup parameters to the world
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class StartInfoLoader implements StartupLoader {

    public static final String module = StartInfoLoader.class.getName();
    protected static Start.Config config = null;
    protected static String args[] = null;

    /**
     * Load a startup class
     *
     * @param config Startup config
     * @param args   Input arguments
     * @throws org.ofbiz.base.start.StartupException
     *
     */
    public void load(Start.Config config, String args[]) throws StartupException {
        StartInfoLoader.config = config;
        StartInfoLoader.args = args;
        Debug.logInfo("Startup parameters now available via StartInfoLoader.getConfig() / StartInfoLoader.getArgs()", module);
    }

    /**
     * Stop the container
     *
     * @throws org.ofbiz.base.start.StartupException
     *
     */
    public void unload() throws StartupException {
    }

    public static Start.Config getConfig() {
        return StartInfoLoader.config;
    }

    public static String[] getArgs() {
        return StartInfoLoader.args;
    }
}
