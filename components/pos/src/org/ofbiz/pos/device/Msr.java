/*
 * $Id: Msr.java,v 1.4 2004/08/10 18:58:56 ajzeneski Exp $
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
import org.ofbiz.pos.adaptor.DataEventAdaptor;
import org.ofbiz.pos.adaptor.ErrorEventAdaptor;
import org.ofbiz.pos.screen.PosScreen;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.4 $
 * @since      3.2
 */
public class Msr extends GenericDevice {

    public static final String module = Msr.class.getName();

    public Msr(String deviceName, int timeout, PosScreen screen) {
        super(deviceName, timeout, screen);
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
                    decodedData[6] = msr.getExpirationDate();

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
                screen.showDialog("main/dialog/error/cardreaderror.xml");
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
        screen.getInput().setFunction("CREDITINFO", msrStr.toString());
        screen.getOutput().print("Credit Card Read");
    }
}
