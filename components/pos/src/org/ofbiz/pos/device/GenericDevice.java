/*
 * $Id: GenericDevice.java,v 1.1 2004/08/06 20:55:10 ajzeneski Exp $
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
package org.ofbiz.pos.device;

import jpos.BaseControl;
import jpos.JposException;

import org.ofbiz.pos.screen.PosScreen;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.2
 */
public abstract class GenericDevice implements JposDevice {

    public static final String module = GenericDevice.class.getName();

    protected PosScreen screen = null;
    protected BaseControl control = null;
    protected String deviceName = null;
    protected int timeout = -1;

    public GenericDevice(String deviceName, int timeout, PosScreen screen) {
        this.deviceName = deviceName;
        this.timeout = timeout;
        this.screen = screen;
    }

    public void open() throws JposException {
        control.open(deviceName);
        control.claim(timeout);
        control.setDeviceEnabled(true);        
        this.initialize();
    }

    public void close() throws JposException {
        control.release();
        control.close();
        control = null;
    }

    protected abstract void initialize() throws JposException;
}
