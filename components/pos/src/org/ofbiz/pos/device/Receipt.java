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
import jpos.POSPrinterConst;
import jpos.POSPrinter;

import org.ofbiz.base.util.Debug;
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
            // check the printer's state
            if (!checkState(printer)) {
                return;
            }

            // transaction mode causes all output to be buffered
            printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_TRANSACTION);

            // print a LF
            printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, LF);

            // print the receipt
            this.printHeader();
            this.printItems(trans);
            this.printTotal(trans);
            this.printBarcode(trans.getOrderId());
            this.printFooter();

            // terminate the transaction causing all of the above buffered data to be sent to the printer
            printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);

        } catch (JposException e) {
            Debug.logError(e, module);
        }
    }

    private void printHeader() {
        this.println(ALIGN_CENTER + TEXT_DOUBLE_HEIGHT + TEXT_BOLD + "Company XYZ");
        this.println(ALIGN_CENTER + TEXT_BOLD + "7 E. 8th St #308");
        this.println(ALIGN_CENTER + TEXT_BOLD + "New York, NY 10003");
        this.println(ALIGN_CENTER + TEXT_BOLD + "212.655.3052");
        this.println();
    }

    private void printItems(PosTransaction trans) {
        trans.isEmpty();
    }

    private void printTotal(PosTransaction trans) {
        trans.isEmpty();
    }

    private void printFooter() {
        // print the thank-you message
        this.println(ALIGN_CENTER + TEXT_DOUBLE_HEIGHT + TEXT_BOLD + "Thank You");

        // send the cut paper signal
        try {
            ((POSPrinter) control).printNormal(POSPrinterConst.PTR_S_RECEIPT, PAPER_CUT);
        } catch (JposException e) {
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
