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

import java.net.URL;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jpos.JposException;
import jpos.POSPrinterConst;
import jpos.POSPrinter;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.pos.PosTransaction;
import org.ofbiz.pos.screen.DialogCallback;
import org.ofbiz.pos.screen.PosDialog;
import org.ofbiz.pos.screen.PosScreen;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class Receipt extends GenericDevice implements DialogCallback {

    public static final String module = Receipt.class.getName();

    protected static final String SPACES = "                                                                      ";
    protected static final String ESC = ((char) 0x1b) + "";
    protected static final String LF = ((char) 0x0a) + "";

    protected static final String ALIGN_CENTER = ESC + "|cA";
    protected static final String ALIGN_RIGHT = ESC + "|rA";

    protected static final String TEXT_DOUBLE_HEIGHT = ESC + "|4C";
    protected static final String TEXT_UNDERLINE = ESC + "|uC";
    protected static final String TEXT_BOLD = ESC + "|bC";

    protected static final String PAPER_CUT = ESC + "|100fP";

    protected PosTransaction lastTransaction = null;

    public Receipt(String deviceName, int timeout) {
        super(deviceName, timeout);
        this.control = new jpos.POSPrinter();
    }

    protected void initialize() throws JposException {
        Debug.logInfo("Receipt [" + control.getPhysicalDeviceName() + "] Claimed : " + control.getClaimed(), module);
        // set map mode to metric - all dimensions specified in 1/100mm units
        // unit = 1/100 mm - i.e. 1 cm = 10 mm = 10 * 100 units
        ((jpos.POSPrinter) control).setMapMode(POSPrinterConst.PTR_MM_METRIC);
    }

    public void println() {
        this.println("");
    }

    public void println(String p) {
        try {
            ((POSPrinter) control).printNormal(POSPrinterConst.PTR_S_RECEIPT, p + LF);
        } catch (jpos.JposException e) {
            Debug.logError(e, module);
        }
    }

    public void printBarcode(String barcode) {
        // print the orderId bar code (Code 3 of 9) centered (1cm tall, 6cm wide)
        try {
            ((POSPrinter) control).printBarCode(POSPrinterConst.PTR_S_RECEIPT, barcode, POSPrinterConst.PTR_BCS_Code39,
                    10 * 100, 60 * 100, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_NONE);
        } catch (JposException e) {
            Debug.logError(e, module);
        }
    }

    public void reprintReceipt() {
        this.printReceipt(lastTransaction);
    }

    public void printReceipt(PosTransaction trans) {
        Debug.log("Print Receipt Requested : " + trans.getTransactionId(), module);
        POSPrinter printer = (POSPrinter) control;
        this.lastTransaction = trans;

        try {
            if (!checkState(printer)) {
                return;
            }
        } catch (JposException e) {
            Debug.logError(e, module);
        }

        String[] receiptTemplate = this.readTemplate();

        if (receiptTemplate != null) {
            for (int i = 0; i < receiptTemplate.length; i++) {
                if (receiptTemplate[i] != null) {
                    if ("[ORDER_BARCODE]".equals(receiptTemplate[i])) {
                        this.printBarcode(trans.getOrderId());
                    } else if (receiptTemplate[i].startsWith("[LOOP]")) {                    
                        // print item loop - minus the LOOP flag
                    } else {
                        this.printInfo(receiptTemplate[i]);
                    }
                }
            }
        }
    }

    private String[] readTemplate() {
        String[] templateString = new String[4];
        int currentPart = 0;

        URL fileUrl = UtilURL.fromResource("receipt.txt");
        StringBuffer buf = new StringBuffer();

        try {
            InputStream in = fileUrl.openStream();
            BufferedReader dis = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = dis.readLine()) != null) {
                if (line.trim().startsWith("[BEGIN LOOP]")) {
                    templateString[currentPart++] = buf.toString();
                    buf = new StringBuffer();
                    buf.append("[LOOP]");
                } else if (line.trim().startsWith("[END LOOP]")) {
                    templateString[currentPart++] = buf.toString();
                    buf = new StringBuffer();
                } else if (line.trim().startsWith("[ORDER BARCODE]")) {
                    templateString[currentPart++] = buf.toString();
                    templateString[currentPart++] = "[ORDER_BARCODE]";
                    buf = new StringBuffer();
                } else {
                    buf.append(line);
                }
            }
            in.close();
        } catch (IOException e) {
            Debug.logError(e, "Unable to open receipt template", module);
        }

        templateString[currentPart] = buf.toString();
        return templateString;
    }

    private void printInfo(String template) {
        Map expandMap = new HashMap();
        expandMap.put("DOUBLE_HEIGHT", TEXT_DOUBLE_HEIGHT);
        expandMap.put("CENTER", ALIGN_CENTER);
        expandMap.put("BOLD", TEXT_BOLD);
        expandMap.put("LF", LF);
        String toPrint = FlexibleStringExpander.expandString(template, expandMap);
        try {
            ((POSPrinter) control).printNormal(POSPrinterConst.PTR_S_RECEIPT, toPrint);
        } catch (jpos.JposException e) {
            Debug.logError(e, module);
        }
    }

    private boolean checkState(POSPrinter printer) throws JposException {
        if (printer.getCoverOpen() == true) {
            // printer is not ready
            PosScreen.currentScreen.showDialog("main/dialog/error/printernotready", this);
            return false;
        }

        return true;
    }

    public void receiveDialogCb(PosDialog dialog) {
        PosScreen.currentScreen.refresh();
        this.reprintReceipt();
    }
}
