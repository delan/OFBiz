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
package org.ofbiz.pos.device.impl;

import jpos.JposException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.pos.device.GenericDevice;
import org.ofbiz.pos.screen.PosScreen;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class CashDrawer extends GenericDevice implements Runnable {

    public static final String module = CashDrawer.class.getName();

    protected boolean waiting = false;
    protected Thread waiter = null;
    protected long startTime = -1;

    public CashDrawer(String deviceName, int timeout) {
        super(deviceName, timeout);
        this.control = new jpos.CashDrawer();
    }

    protected void initialize() throws JposException {
        Debug.logInfo("CashDrawer [" + control.getPhysicalDeviceName() + "] Claimed : " + control.getClaimed(), module);
    }

    public void openDrawer() {
        try {
            ((jpos.CashDrawer) control).openDrawer();
            //this.startWaiter();
        } catch (JposException e) {
            Debug.logError(e, module);
            PosScreen.currentScreen.showDialog("dialog/error/drawererror");
        }
    }

    public boolean isDrawerOpen() {
        try {
            return ((jpos.CashDrawer) control).getDrawerOpened();
        } catch (JposException e) {
            Debug.logError(e, module);
            PosScreen.currentScreen.showDialog("dialog/error/drawererror");
        }
        return false;
    }

    protected synchronized void startWaiter() {
        if (!this.isDrawerOpen()) {
            this.waiter = new Thread(this);
            this.waiter.setDaemon(false);
            this.waiter.setName(this.getClass().getName());
            this.waiting = true;
            this.waiter.start();
        } else {
            Debug.logWarning("Drawer already open!", module);
        }
    }

    public void run() {
        Debug.log("Starting Waiter Thread", module);
        this.startTime = System.currentTimeMillis();
        while (waiting) {
            boolean isOpen = true;
            try {
                isOpen = ((jpos.CashDrawer) control).getDrawerOpened();
            } catch (JposException e) {
                Debug.logError(e, module);
                this.waiting = false;
                PosScreen.currentScreen.showDialog("dialog/error/drawererror");
            }
            if (isOpen) {
                long now = (System.currentTimeMillis() - startTime);
                if ((now > 4499) && (now % 500 == 0)) {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                }
                if ((now > 4499) && (now % 5000 == 0)) {
                    PosScreen.currentScreen.showDialog("dialog/error/draweropen");   
                }
            } else {
                this.waiting = false;
            }
        }
        this.startTime = -1;
        Debug.log("Waiter finished", module);
    }
}

