/*
 * $Id: ScannerKybService.java,v 1.1 2004/08/06 20:55:12 ajzeneski Exp $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jpos.JposException;
import jpos.ScannerConst;
import jpos.events.DataEvent;

import org.ofbiz.pos.adaptor.KeyboardAdaptor;
import org.ofbiz.pos.adaptor.KeyboardReceiver;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.2
 */
public class ScannerKybService extends BaseKybService implements jpos.services.ScannerService18, KeyboardReceiver {

    public static final String module = ScannerKybService.class.getName();

    protected List dataEventQueue = new ArrayList();
    protected Map barcodeIdMap = new HashMap();

    protected byte[] scannedDataLabel = null;
    protected byte[] scannedData = null;
    protected String codeId = null;

    protected boolean decodeData = true;
    protected boolean eventEnabled = true;
    protected boolean autoDisable = false;
    protected int powerState = 1;

    public ScannerKybService() {
        KeyboardAdaptor.getInstance(this, KeyboardAdaptor.SCANNER_DATA);
    }

    // ScannerService18
    public boolean getCapStatisticsReporting() throws JposException {
        return false;
    }

    public boolean getCapUpdateStatistics() throws JposException {
        return false;
    }

    public void resetStatistics(String s) throws JposException {
        // not used
    }

    public void retrieveStatistics(String[] strings) throws JposException {
        // not used
    }

    public void updateStatistics(String s) throws JposException {
        // not used
    }

    // ScannerService13
    public int getCapPowerReporting() throws JposException {
        return 0; // not used
    }

    public int getPowerNotify() throws JposException {
        return 0; // not used
    }

    public void setPowerNotify(int i) throws JposException {
        // not used
    }

    public int getPowerState() throws JposException {
        return this.powerState;
    }

    // ScannerService12
    public boolean getAutoDisable() throws JposException {
        return this.autoDisable;
    }

    public void setAutoDisable(boolean b) throws JposException {
        this.autoDisable = b;
    }

    public int getDataCount() throws JposException {
        return this.scannedData.length;
    }

    public boolean getDataEventEnabled() throws JposException {
        return this.eventEnabled;
    }

    public void setDataEventEnabled(boolean b) throws JposException {
        this.eventEnabled = b;
    }

    public boolean getDecodeData() throws JposException {
        return this.decodeData;
    }

    public void setDecodeData(boolean b) throws JposException {       
        this.decodeData = b;
    }

    public byte[] getScanData() throws JposException {
        return this.scannedData;
    }

    public byte[] getScanDataLabel() throws JposException {
        if (this.decodeData) {
            return this.scannedDataLabel;
        } else {
            return new byte[0];
        }
    }

    public int getScanDataType() throws JposException {
        if (codeId != null && barcodeIdMap.containsKey(codeId)) {
            return ((Integer) barcodeIdMap.get(codeId)).intValue();
        }
        return ScannerConst.SCAN_SDT_UNKNOWN;
    }

    public void clearInput() throws JposException {
        this.scannedDataLabel = null;
        this.scannedData = null;
        this.codeId = null;
    }

    // KeyboardReceiver
    public void receiveData(int[] codes, char[] chars) {
        String dataStr = new String(chars);
        this.parseScannedString(dataStr);

        // fire off the event notification
        DataEvent event = new DataEvent(this, 0);
        ecb.fireDataEvent(event);
    }

    private void parseScannedString(String str) {
        if (str == null) {
            return;
        }

        // parse the scanned data
        this.scannedData = str.trim().getBytes();
        if (this.decodeData) {
            this.codeId = str.substring(0, 1).toUpperCase();
            this.scannedDataLabel = str.substring(1).getBytes();
        }
    }
}
