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
package org.ofbiz.pos.device;

import jpos.JposException;
import jpos.JposConst;

import org.ofbiz.base.util.Debug;
import org.ofbiz.pos.adaptor.DataEventAdaptor;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.2
 */
public class Keyboard extends GenericDevice {

    public static final String module = CashDrawer.class.getName();

    public Keyboard(String deviceName, int timeout) {
        super(deviceName, timeout);
        this.control = new jpos.POSKeyboard();
    }

    protected void initialize() throws JposException {
        Debug.logInfo("Keyboard [" + control.getPhysicalDeviceName() + "] Claimed : " + control.getClaimed(), module);
        final jpos.POSKeyboard keyboard = (jpos.POSKeyboard) control;

        keyboard.addDataListener(new DataEventAdaptor() {
            public void dataOccurred(jpos.events.DataEvent event) {
                Debug.log("Event Source - " + event.getSource().getClass().getName(), module);    
            }
        });
    }
}
