/*
 * $Id: XuiContainer.java,v 1.2 2004/07/09 03:13:14 ajzeneski Exp $
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
package org.ofbiz.content.xui;

import javax.swing.JFrame;

import net.xoetrope.swing.XApplet;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.GenericDelegator;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class XuiContainer implements Container {

    public static final String module = XuiContainer.class.getName();

    protected static GenericDelegator delegator = null;
    protected static LocalDispatcher dispatcher = null;

    protected XuiScreen initial = null;
    protected String startup = null;


    public void init(String[] args) throws ContainerException {
    }

    public boolean start(String configFileLocation) throws ContainerException {
        // get the container config
        ContainerConfig.Container cc = ContainerConfig.getContainer("xui-container", configFileLocation);
        if (cc == null) {
            throw new ContainerException("No xui-container configuration found in container config!");
        }

        // get the delegator
        String delegatorName = ContainerConfig.getPropertyValue(cc, "delegator-name", "default");
        delegator = GenericDelegator.getGenericDelegator(delegatorName);

        // get the dispatcher
        String dispatcherName = ContainerConfig.getPropertyValue(cc, "dispatcher-name", "xui-dispatcher");
        try {
            dispatcher = GenericDispatcher.getLocalDispatcher(dispatcherName, delegator);
        } catch (GenericServiceException e) {
            throw new ContainerException(e);
        }

        // load the XUI and render the initial screen
        this.startup = ContainerConfig.getPropertyValue(cc, "startup-file", "xui.properties");
        this.initial = new XuiScreen();
        this.initial.setup(this.startup);

        return true;
    }

    public void stop() throws ContainerException {
    }

    public static GenericDelegator getGenericDelegator() {
        return delegator;
    }

    public static LocalDispatcher getLocalDispatcher() {
        return dispatcher;
    }

    class XuiScreen extends XApplet {

        public void setup(String startupFile) {
            JFrame frame = new JFrame();
            frame.getContentPane().add(this);
            this.setup(frame, new String[] { startupFile });
        }
    }
}
