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

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.pos.adaptor.DataEventAdaptor;
import org.ofbiz.pos.adaptor.ErrorEventAdaptor;
import org.ofbiz.pos.screen.PosScreen;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class Msr extends GenericDevice {

    public static final String module = Msr.class.getName();
    public static final int MSR_CREDIT_CARD = 100;
    public static final int MSR_GIFT_CARD = 101;
    public static final int MSR_ATM_CARD = 102;
    public static final int MSR_CUST_CARD = 701;
    public static final int MSR_CLERK_CARD = 801;
    public static final int MSR_UNKNOWN = 999;

    public Msr(String deviceName, int timeout) {
        super(deviceName, timeout);
        this.control = new jpos.MSR();
    }

    protected void initialize() throws JposException {
        Debug.logInfo("MSR [" + control.getPhysicalDeviceName() + "] Claimed : " + control.getClaimed(), module);
        final jpos.MSR msr = (jpos.MSR) control;
        msr.setDecodeData(true);
        msr.setTracksToRead(2);

        // create the data listner
        msr.addDataListener(new DataEventAdaptor() {

            public void dataOccurred(jpos.events.DataEvent event) {
                String[] decodedData = new String[7];
                byte[] track1 = null;
                byte[] track2 = null;

                try {
                    // get the raw track data
                    track1 = msr.getTrack1Data();
                    track2 = msr.getTrack2Data();

                    // get the decoded data
                    decodedData[0] = msr.getTitle();
                    decodedData[1] = msr.getFirstName();
                    decodedData[2] = msr.getMiddleInitial();
                    decodedData[3] = msr.getSurname();
                    decodedData[4] = msr.getSuffix();
                    decodedData[5] = msr.getAccountNumber();

                    // verify the acct num exists
                    if (UtilValidate.isEmpty(decodedData[5])) {
                        PosScreen.currentScreen.showDialog("main/dialog/error/cardreaderror");
                        msr.clearInput();
                        return;
                    }

                    // fix expDate (reversed)
                    if (msr.getExpirationDate() != null && msr.getExpirationDate().length() > 3) {
                        decodedData[6] = msr.getExpirationDate().substring(2) + msr.getExpirationDate().substring(0, 2);
                    } else {
                        PosScreen.currentScreen.showDialog("main/dialog/error/cardreaderror");
                        msr.clearInput();
                        return;
                    }

                    msr.clearInput();
                } catch (jpos.JposException e) {
                    Debug.logError(e, module);
                }

                processMsrData(decodedData, track1, track2);
            }
        });

        // create the error listener
        msr.addErrorListener(new ErrorEventAdaptor() {

            public void errorOccurred(jpos.events.ErrorEvent event) {
                Debug.log("Error Occurred : " + event.getErrorCodeExtended(), module);
                PosScreen.currentScreen.showDialog("main/dialog/error/cardreaderror");
                try {
                    msr.clearInput();
                } catch (jpos.JposException e) {
                    Debug.logError(e, module);
                }
            }
        });
    }

    protected void processMsrData(String[] decodedData, byte[] track1, byte[] track2) {
        StringBuffer msrStr = new StringBuffer();
        msrStr.append(decodedData[5]);
        msrStr.append("|");
        msrStr.append(decodedData[6]);
        msrStr.append("|");
        msrStr.append(decodedData[1]);
        msrStr.append("|");
        msrStr.append(decodedData[3]);
        Debug.log("Msr Info : " + msrStr.toString(), module);

        // implemented validation
        int msrType = MSR_UNKNOWN;
        try {
            if (UtilValidate.isAnyCard(decodedData[5])) {
                msrType = MSR_CREDIT_CARD;
            } else if (UtilValidate.isGiftCard(decodedData[5])) {
                msrType = MSR_GIFT_CARD;
            }
        } catch (NumberFormatException e) {            
        }

        // make sure we are on the POS pay screen
        if (!"main/paypanel".equals(PosScreen.currentScreen.getName())) {
            PosScreen pos = PosScreen.currentScreen.showPage("main/paypanel");
            pos.getInput().setFunction("TOTAL", "");
            Debug.log("Switched to paypanel.xml; triggered TOTAL function", module);
        }

        // all implemented types
        switch (msrType) {
            case MSR_CREDIT_CARD:
                String[] credInfo = PosScreen.currentScreen.getInput().getFunction("CREDIT");
                if (credInfo == null) {
                    PosScreen.currentScreen.getInput().setFunction("CREDIT", "");
                }
                PosScreen.currentScreen.getInput().setFunction("MSRINFO", msrStr.toString());
                PosScreen.currentScreen.getOutput().print("Credit Card Read");
                PosScreen.currentScreen.getInput().clearInput();
                this.callEnter();
                break;
            case MSR_GIFT_CARD:
                PosScreen.currentScreen.getInput().setFunction("MSRINFO", msrStr.toString());
                PosScreen.currentScreen.getOutput().print("Gift Card Read");
                PosScreen.currentScreen.getInput().clearInput();
                this.callEnter();
                break;
            case MSR_UNKNOWN:
                PosScreen.currentScreen.showDialog("main/dialog/error/unknowncardtype");
                break;
        }
    }
}
