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
import java.util.Date;
import java.text.SimpleDateFormat;

import jpos.JposException;
import jpos.POSPrinterConst;
import jpos.POSPrinter;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilFormatOut;
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

    protected static final String ESC = ((char) 0x1b) + "";
    protected static final String LF = ((char) 0x0a) + "";

    protected static final String ALIGN_CENTER = ESC + "|cA";
    protected static final String ALIGN_RIGHT = ESC + "|rA";

    protected static final String TEXT_DOUBLE_HEIGHT = ESC + "|4C";
    protected static final String TEXT_UNDERLINE = ESC + "|uC";
    protected static final String TEXT_BOLD = ESC + "|bC";

    protected static final String PAPER_CUT = ESC + "|100fP";

    protected SimpleDateFormat[] dateFormat = null;
    protected String[] storeReceiptTmpl = null;
    protected String[] custReceiptTmpl = null;

    protected String[] dateFmtStr = { "EEE, d MMM yyyy HH:mm:ss z", "EEE, d MMM yyyy HH:mm:ss z" };
    protected int[] priceLength = { 7, 7 };
    protected int[] qtyLength = { 5, 5 };
    protected int[] descLength = { 25, 25 };
    protected int[] pridLength = { 25, 25 };
    protected int[] infoLength = { 34, 34 };

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
        this.reprintReceipt(false);
    }

    public void reprintReceipt(boolean reprintStoreCopy) {
        this.printReceipt(lastTransaction, reprintStoreCopy);
    }

    public void printReceipt(PosTransaction trans, boolean printStoreCopy) {
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

        if (printStoreCopy) {
            String[] storeReceipt = this.readStoreTemplate();
            int payments = trans.getNumberOfPayments();
            for (int i = 0; i < payments; i++) {
                Map info = trans.getPaymentInfo(i);
                if (info.containsKey("cardNumber")) {
                    this.printReceipt(trans, storeReceipt, 1, info);
                }
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                }
            }                        
        }

        // print the customer receipt
        String[] custReceipt = this.readCustomerTemplate();
        this.printReceipt(trans, custReceipt, 0, null);
    }

    private void printReceipt(PosTransaction trans, String[] template, int type, Map payInfo) {
        if (template != null) {
            for (int i = 0; i < template.length; i++) {
                if (template[i] != null) {
                    if ("[ORDER_BARCODE]".equals(template[i])) {
                        this.printBarcode(trans.getOrderId());
                    } else if (template[i].startsWith("[DLOOP]")) {
                        this.printDetail(template[i], trans, type);
                    } else if (template[i].startsWith("[PLOOP]")) {
                        this.printPayInfo(template[i], trans, type);
                    } else if (payInfo != null) {
                        this.printPayInfo(template[i], trans, type, payInfo);
                    } else {
                        this.printInfo(template[i], trans, type);
                    }
                }
            }

            this.println();
            this.println();
            this.println(PAPER_CUT);
        }
    }

    private synchronized String[] readStoreTemplate() {
        if (this.storeReceiptTmpl == null) {
            this.storeReceiptTmpl = new String[7];
            this.readCustomerTemplate(storeReceiptTmpl, "storereceipt.txt", 1);
        }

        return this.storeReceiptTmpl;
    }

    private synchronized String[] readCustomerTemplate() {
        if (this.custReceiptTmpl == null) {
            this.custReceiptTmpl = new String[7];
            this.readCustomerTemplate(custReceiptTmpl, "custreceipt.txt", 0);
        }

        return this.custReceiptTmpl;
    }

    private String[] readCustomerTemplate(String[] template, String resource, int type) {
        int currentPart = 0;

        URL fileUrl = UtilURL.fromResource(resource);
        StringBuffer buf = new StringBuffer();

        try {
            InputStream in = fileUrl.openStream();
            BufferedReader dis = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = dis.readLine()) != null) {
                if (line.trim().startsWith("#")) {
                    String[] code = line.trim().split("\\=");
                    if ("#description.length".equals(code[0])) {
                        try {
                            this.descLength[type] = Integer.parseInt(code[1]);
                        } catch (NumberFormatException e) {
                            Debug.logWarning(e, module);
                        }
                    } else if ("#productId.length".equals(code[0])) {
                        try {
                            this.pridLength[type] = Integer.parseInt(code[1]);
                        } catch (NumberFormatException e) {
                            Debug.logWarning(e, module);
                        }
                    } else if ("#price.length".equals(code[0])) {
                        try {
                            this.priceLength[type] = Integer.parseInt(code[1]);
                        } catch (NumberFormatException e) {
                            Debug.logWarning(e, module);
                        }
                    } else if ("#quantity.length".equals(code[0])) {
                        try {
                            this.qtyLength[type] = Integer.parseInt(code[1]);
                        } catch (NumberFormatException e) {
                            Debug.logWarning(e, module);
                        }
                    } else if ("#infoString.length".equals(code[0])) {
                        try {
                            this.infoLength[type] = Integer.parseInt(code[1]);
                        } catch (NumberFormatException e) {
                            Debug.logWarning(e, module);
                        }
                    } else if ("#dateFormat".equals(code[0])) {
                        this.dateFmtStr[type] = code[1];
                    }
                } else if (line.trim().startsWith("[BEGIN ITEM LOOP]")) {
                    template[currentPart++] = buf.toString();
                    buf = new StringBuffer();
                    buf.append("[DLOOP]");
                } else if (line.trim().startsWith("[END ITEM LOOP]")) {
                    template[currentPart++] = buf.toString();
                    buf = new StringBuffer();
                } else if (line.trim().startsWith("[BEGIN PAY LOOP]")) {
                    template[currentPart++] = buf.toString();
                    buf = new StringBuffer();
                    buf.append("[PLOOP]");
                } else if (line.trim().startsWith("[END PAY LOOP]")) {
                    template[currentPart++] = buf.toString();
                    buf = new StringBuffer();
                } else if (line.trim().startsWith("[ORDER BARCODE]")) {
                    template[currentPart++] = buf.toString();
                    template[currentPart++] = "[ORDER_BARCODE]";
                    buf = new StringBuffer();
                } else {
                    if (UtilValidate.isEmpty(line)) {
                        line = " ";
                    }
                    buf.append(line + "\n");
                }
            }
            in.close();
        } catch (IOException e) {
            Debug.logError(e, "Unable to open receipt template", module);
        }

        template[currentPart] = buf.toString();
        return template;
    }

    private synchronized SimpleDateFormat getDateFormat(int type) {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat[2];
        }
        if (dateFormat[type] == null) {
            dateFormat[type] = new SimpleDateFormat(this.dateFmtStr[type]);
        }
        return dateFormat[type];
    }

    private void printInfo(String template, PosTransaction trans, int type) {
        Map expandMap = this.makeCodeExpandMap(trans, type);
        String toPrint = FlexibleStringExpander.expandString(template, expandMap);
        String[] lines = toPrint.split("\\n");
        for (int i = 0; i < lines.length; i++) {
            this.println(lines[i]);
        }
    }

    private void printDetail(String loop, PosTransaction trans, int type) {
        String loopStr = loop.substring(7);
        int size = trans.size();
        for (int i = 0; i < size; i++) {
            Map expandMap = this.makeCodeExpandMap(trans, type);
            expandMap.putAll(trans.getItemInfo(i));
            // adjust the padding
            expandMap.put("description", padString((String) expandMap.get("description"), descLength[type], true));
            expandMap.put("productId", padString((String) expandMap.get("productId"), pridLength[type], true));
            expandMap.put("basePrice", padString((String) expandMap.get("basePrice"), priceLength[type], false));
            expandMap.put("subtotal", padString((String) expandMap.get("subtotal"), priceLength[type], false));
            expandMap.put("quantity", padString((String) expandMap.get("quantity"), qtyLength[type], false));
            expandMap.put("adjustments", padString((String) expandMap.get("adjustments"), priceLength[type], false));
            String toPrint = FlexibleStringExpander.expandString(loopStr, expandMap);
            if (toPrint.indexOf("\\n") > -1) {
                String[] lines = toPrint.split("\\n");
                for (int x = 0; x < lines.length; x++) {
                    this.println(lines[x]);
                }
            } else {
                this.println(toPrint);
            }
        }
    }

    private void printPayInfo(String loop, PosTransaction trans, int type) {
        String loopStr = loop.substring(7);
        int size = trans.getNumberOfPayments();
        for (int i = 0; i < size; i++) {
            Map payInfoMap = trans.getPaymentInfo(i);
            this.printPayInfo(loopStr, trans, type, payInfoMap);
        }
    }

    private void printPayInfo(String template, PosTransaction trans, int type, Map payInfo) {
        Map expandMap = this.makeCodeExpandMap(trans, type);
        expandMap.putAll(payInfo);
        // adjust the padding
        expandMap.put("authInfoString", padString((String) expandMap.get("authInfoString"), infoLength[type], false));
        expandMap.put("infoString", padString((String) expandMap.get("infoString"), infoLength[type], false));
        expandMap.put("amount", padString((String) expandMap.get("amount"), priceLength[type], false));
        String toPrint = FlexibleStringExpander.expandString(template, expandMap);
        if (toPrint.indexOf("\\n") > -1) {
            String[] lines = toPrint.split("\\n");
            for (int x = 0; x < lines.length; x++) {
                this.println(lines[x]);
            }
        } else {
            this.println(toPrint);
        }
    }

    private Map makeCodeExpandMap(PosTransaction trans, int type) {
        Map expandMap = new HashMap();
        SimpleDateFormat fmt = this.getDateFormat(type);
        String dateString = fmt.format(new Date());

        expandMap.put("DOUBLE_HEIGHT", TEXT_DOUBLE_HEIGHT);
        expandMap.put("CENTER", ALIGN_CENTER);
        expandMap.put("BOLD", TEXT_BOLD);
        expandMap.put("LF", LF);
        expandMap.put("transactionId", trans.getTransactionId());
        expandMap.put("userId", trans.getUserId());
        expandMap.put("orderId", trans.getOrderId());
        expandMap.put("dateStamp", dateString);
        expandMap.put("drawerNo", new Integer(trans.getDrawerNumber()).toString());
        expandMap.put("taxTotal", this.padString(UtilFormatOut.formatPrice(trans.getTaxTotal()), priceLength[type], false));
        expandMap.put("grandTotal", this.padString(UtilFormatOut.formatPrice(trans.getGrandTotal()), priceLength[type], false));
        expandMap.put("totalPayments", this.padString(UtilFormatOut.formatPrice(trans.getPaymentTotal()), priceLength[type], false));
        expandMap.put("change", this.padString((trans.getTotalDue() < 0 ?
                UtilFormatOut.formatPrice(trans.getTotalDue() * -1) : "0.00"), priceLength[type], false));

        return expandMap;
    }

    private String padString(String str, int setLen, boolean padEnd) {
        if (str == null) {
            return null;
        }
        if (setLen == 0) {
            return str;
        }
        int stringLen = str.length();
        int diff = setLen - stringLen;
        if (diff < 0) {
            return str.substring(0, setLen);
        } else {
            String newString = new String();
            if (padEnd) {
                newString = newString + str;
            }
            for (int i = 0; i < diff; i++) {
                newString = newString + " ";
            }
            if (!padEnd) {
                newString = newString + str;
            }
            return newString;
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
