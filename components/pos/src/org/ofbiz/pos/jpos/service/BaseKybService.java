/*
 * $Id: BaseKybService.java,v 1.1 2004/08/06 20:55:12 ajzeneski Exp $
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
package org.ofbiz.pos.jpos.service;

import jpos.services.BaseService;
import jpos.services.EventCallbacks;
import jpos.JposException;
import jpos.JposConst;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;


/**
 * JPOS BaseService Implementation for Keyboard Wedge Services
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.2
 */
public class BaseKybService implements BaseService, JposServiceInstance {

    public static final String module = BaseKybService.class.getName();

    protected static boolean claimed = false;
    protected EventCallbacks ecb = null;
    protected JposEntry entry = null;

    protected boolean freezeEvents = false;
    protected boolean enabled = false;

    protected String deviceName = null;
    protected String healthText = null;
    protected String physicalName = null;
    protected String physicalDesc = null;
    protected String serviceDesc = null;

    protected int serviceVer = 1008000;
    protected int state = JposConst.JPOS_S_CLOSED;

    // open/close methods
    public void open(String deviceName, EventCallbacks ecb) throws JposException {
        this.deviceName = deviceName;
        this.ecb = ecb;
        this.healthText = "OK";
        this.state = JposConst.JPOS_S_IDLE;
        this.serviceDesc = entry.getProp(JposEntry.DEVICE_CATEGORY_PROP_NAME).getValueAsString();
        this.physicalDesc = entry.getProp(JposEntry.PRODUCT_DESCRIPTION_PROP_NAME).getValueAsString();
        this.physicalName = entry.getProp(JposEntry.PRODUCT_NAME_PROP_NAME).getValueAsString();
    }

    public void claim(int i) throws JposException {
        BaseKybService.claimed = true;
    }

    public void release() throws JposException {
        BaseKybService.claimed = false;
    }

    public void close() throws JposException {
        BaseKybService.claimed = false;
        this.freezeEvents = false;
        this.enabled = false;
        this.ecb = null;
        this.healthText = "CLOSED";
        this.state = JposConst.JPOS_S_CLOSED;
    }

    // field methods
    public String getCheckHealthText() throws JposException {
        return this.healthText;
    }

    public boolean getClaimed() throws JposException {
        return BaseKybService.claimed;
    }

    public boolean getDeviceEnabled() throws JposException {
        return this.enabled;
    }

    public void setDeviceEnabled(boolean b) throws JposException {
        this.enabled = b;
    }

    public String getDeviceServiceDescription() throws JposException {
        return this.serviceDesc;
    }

    public int getDeviceServiceVersion() throws JposException {
        return this.serviceVer;
    }

    public boolean getFreezeEvents() throws JposException {
        return this.freezeEvents;
    }

    public void setFreezeEvents(boolean b) throws JposException {
        this.freezeEvents = b;
    }

    public String getPhysicalDeviceDescription() throws JposException {
        return this.physicalDesc;
    }

    public String getPhysicalDeviceName() throws JposException {
        return this.physicalName;
    }

    public int getState() throws JposException {
        return this.state;
    }

    public void checkHealth(int i) throws JposException {
        // This method is not used since there is no physical device to check
    }

    public void directIO(int i, int[] ints, Object o) throws JposException {
        // This method is not used since there is no physical IO to be performed
    }

    public void setEntry(JposEntry entry) {
        this.entry = entry;
    }

    // JposServiceInstance
    public void deleteInstance() throws JposException {
        // TODO: Implement Me!
    }
}
