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
package org.ofbiz.pos.jpos.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;

import jpos.JposException;
import jpos.ScannerConst;
import jpos.services.EventCallbacks;
import jpos.events.DataEvent;

import org.ofbiz.pos.adaptor.KeyboardAdaptor;
import org.ofbiz.pos.adaptor.KeyboardReceiver;

/**
 * Generic Keyboard Wedge Barcode Scanner
 * 
 * Configure your scanner:
 * 1) Send STX Preamble
 * 2) Send barcode id as prefix
 * 3) Termination char CR
 * 4) Do NOT send ETX Postamble
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class ScannerKybService extends BaseService implements jpos.services.ScannerService17, KeyboardReceiver {

    public static final String module = ScannerKybService.class.getName();
    private static final int TYPELOC_PREFIX = 50;
    private static final int TYPELOC_SUFFIX = 60;
    private static final int TYPELOC_NONE = 99;

    protected Map barcodeIdMap = new HashMap();

    protected byte[] scannedDataLabel = new byte[0];
    protected byte[] scannedData = new byte[0];
    protected String codeId = new String();

    protected boolean decodeData = true;
    protected boolean eventEnabled = true;
    protected boolean autoDisable = false;
    protected int powerState = 1;
    protected int codeLocation = TYPELOC_PREFIX;

    public ScannerKybService() {
        KeyboardAdaptor.getInstance(this, KeyboardAdaptor.SCANNER_DATA);
    }

    public void open(String deviceName, EventCallbacks ecb) throws JposException {
        super.open(deviceName, ecb);
        this.readCodeMap();
        if (entry.hasPropertyWithName("BarcodeTypePosition")) {
            if (entry.getProp("BarcodeTypePosition").getValueAsString().equalsIgnoreCase("suffix")) {
                this.codeLocation = TYPELOC_SUFFIX;
            } else if (entry.getProp("BarcodeTypePosition").getValueAsString().equalsIgnoreCase("prefix")) {
                this.codeLocation = TYPELOC_PREFIX;
            } else {
                this.codeLocation = TYPELOC_NONE;
            }
        }
    }

    // ScannerService12
    public boolean getAutoDisable() throws JposException {
        return this.autoDisable;
    }

    public void setAutoDisable(boolean b) throws JposException {
        this.autoDisable = b;
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
        this.scannedDataLabel = new byte[0];
        this.scannedData = new byte[0];
        this.codeId = new String();
    }

    // ScannerService13
    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    public int getPowerNotify() throws JposException {
        return 0;
    }

    public void setPowerNotify(int i) throws JposException {
    }

    public int getPowerState() throws JposException {
        return 0;
    }    

    // KeyboardReceiver
    public synchronized void receiveData(int[] codes, char[] chars) {
        String dataStr = new String(chars);
        this.parseScannedString(dataStr);

        // fire off the event notification
        DataEvent event = new DataEvent(this, 0);
        this.fireEvent(event);
    }

    private void parseScannedString(String str) {
        if (str == null) {
            return;
        }

        // parse the scanned data
        if (str != null) {
            str = str.trim();        
            this.scannedData = str.getBytes();
            if (this.decodeData) {
                if (this.codeLocation == TYPELOC_PREFIX) {
                    this.codeId = str.substring(0, 1).toUpperCase();
                    this.scannedDataLabel = str.substring(1).getBytes();
                } else if (this.codeLocation == TYPELOC_SUFFIX) {
                    this.codeId = str.substring(str.length() - 1);
                    this.scannedDataLabel = str.substring(0, str.length() - 1).getBytes();
                } else {
                    this.codeId = "";
                    this.scannedDataLabel = str.getBytes();
                }
            }
        }
    }

    private void readCodeMap() {
        if (barcodeIdMap == null) {
            barcodeIdMap = new HashMap();
        }
        if (barcodeIdMap.size() == 0) {
            return;
        }

        Enumeration names = entry.getPropertyNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                if (name.startsWith("CodeType:")) {
                    String codeType = name.substring(name.indexOf(":"));
                    String codeValue = entry.getProp(name).getValueAsString();
                    if ("CODE11".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_OTHER));
                    } else if ("CODE39".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_Code39));
                    } else if ("CODE93".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_Code93));
                    } else if ("CODE128".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_Code128));
                    } else if ("CODABAR".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_Codabar));
                    } else if ("I2OF5".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_OTHER));
                    } else if ("ID2OF5".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_OTHER));
                    } else if ("MSI".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_OTHER));
                    } else if ("UPCA".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_UPCA));
                    } else if ("UPCE".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_UPCE));
                    } else if ("EAN13".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_EAN13));
                    } else if ("EAN8".equals(codeType)) {
                        barcodeIdMap.put(codeValue.toUpperCase(), new Integer(ScannerConst.SCAN_SDT_EAN8));
                    }
                }
            }
        }
    }
}
