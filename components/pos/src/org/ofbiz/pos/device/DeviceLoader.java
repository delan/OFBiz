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

import java.util.Map;

import jpos.JposException;

import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.pos.device.impl.CashDrawer;
import org.ofbiz.pos.device.impl.CheckScanner;
import org.ofbiz.pos.device.impl.Journal;
import org.ofbiz.pos.device.impl.Keyboard;
import org.ofbiz.pos.device.impl.LineDisplay;
import org.ofbiz.pos.device.impl.Msr;
import org.ofbiz.pos.device.impl.PinPad;
import org.ofbiz.pos.device.impl.Receipt;
import org.ofbiz.pos.device.impl.Scanner;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class DeviceLoader {

    public static final String module = DeviceLoader.class.getName();

    public static CashDrawer[] drawer = null;
    public static CheckScanner check = null;
    public static Journal journal = null;
    public static Keyboard keyboard = null;
    public static LineDisplay ldisplay = null;
    public static Msr msr = null;
    public static PinPad pinpad = null;
    public static Receipt receipt = null;
    public static Scanner scanner = null;

    public static void load(Map devices) throws GeneralException {
        // load the keyboard
        if (devices.get("Keyboard") != null) {
            String keyboardDevice = ((ContainerConfig.Container.Property) devices.get("Keyboard")).value;
            if (UtilValidate.isNotEmpty(keyboardDevice) && !"[NOT IMPLEMENTED]".equals(keyboardDevice)) {
                keyboard = new Keyboard(keyboardDevice, -1);
                try {
                    keyboard.open();
                } catch (jpos.JposException jpe) {
                    Debug.logError(jpe, "JPOS Exception", module);
                    throw new GeneralException(jpe.getOrigException());
                }
            }
        }

        // load the scanner
        if (devices.get("Scanner") != null) {
            String scannerDevice = ((ContainerConfig.Container.Property) devices.get("Scanner")).value;
            if (UtilValidate.isNotEmpty(scannerDevice) && !"[NOT IMPLEMENTED]".equals(scannerDevice)) {
                scanner = new Scanner(scannerDevice, -1);
                try {
                    scanner.open();
                } catch (jpos.JposException jpe) {
                    Debug.logError(jpe, "JPOS Exception", module);
                    throw new GeneralException(jpe.getOrigException());
                }
            }
        }

        // load the check reader
        if (devices.get("CheckScanner") != null) {
            String checkScannerDevice = ((ContainerConfig.Container.Property) devices.get("CheckScanner")).value;
            if (UtilValidate.isNotEmpty(checkScannerDevice) && !"[NOT IMPLEMENTED]".equals(checkScannerDevice)) {
                check = new CheckScanner(checkScannerDevice, -1);
                try {
                    check.open();
                } catch (jpos.JposException jpe) {
                    Debug.logError(jpe, "JPOS Exception", module);
                    throw new GeneralException(jpe.getOrigException());
                }
            }
        }

        // load the msr
        if (devices.get("Msr") != null) {
            String msrDevice = ((ContainerConfig.Container.Property) devices.get("Msr")).value;
            if (UtilValidate.isNotEmpty(msrDevice) && !"[NOT IMPLEMENTED]".equals(msrDevice)) {
                msr = new Msr(msrDevice, -1);
                try {
                    msr.open();
                } catch (jpos.JposException jpe) {
                    Debug.logError(jpe, "JPOS Exception", module);
                    throw new GeneralException(jpe.getOrigException());
                }
            }
        }

        // load the receipt printer
        if (devices.get("Receipt") != null) {
            String receiptDevice = ((ContainerConfig.Container.Property) devices.get("Receipt")).value;
            if (UtilValidate.isNotEmpty(receiptDevice) && !"[NOT IMPLEMENTED]".equals(receiptDevice)) {
                receipt = new Receipt(receiptDevice, -1);
                try {
                    receipt.open();
                } catch (jpos.JposException jpe) {
                    Debug.logError(jpe, "JPOS Exception", module);
                    throw new GeneralException(jpe.getOrigException());
                }
            }
        }

        // load the journal printer
        if (devices.get("Journal") != null) {
            String journalDevice = ((ContainerConfig.Container.Property) devices.get("Journal")).value;
            if (UtilValidate.isNotEmpty(journalDevice) && !"[NOT IMPLEMENTED]".equals(journalDevice)) {
                journal = new Journal(journalDevice, -1);
                try {
                    journal.open();
                } catch (jpos.JposException jpe) {
                    Debug.logError(jpe, "JPOS Exception", module);
                    throw new GeneralException(jpe.getOrigException());
                }
            }
        }

        // load the line display
        if (devices.get("LineDisplay") != null) {
            String lineDisplayDevice = ((ContainerConfig.Container.Property) devices.get("LineDisplay")).value;
            if (UtilValidate.isNotEmpty(lineDisplayDevice) && !"[NOT IMPLEMENTED]".equals(lineDisplayDevice)) {
                ldisplay = new LineDisplay(lineDisplayDevice, -1);
                try {
                    ldisplay.open();
                } catch (jpos.JposException jpe) {
                    Debug.logError(jpe, "JPOS Exception", module);
                    throw new GeneralException(jpe.getOrigException());
                }
            }
        }

        // load the cash drawer(s) -- Currently only supports one drawer per terminal
        for (int i = 1; i < 10; i++) { // more than 10 cash drawers on a terminal??
            String idName = "CashDrawer." + i;
            if (devices.get(idName) != null) {
                String cashDrawerDevice = ((ContainerConfig.Container.Property) devices.get(idName)).value;
                if (UtilValidate.isNotEmpty(cashDrawerDevice) && !"[NOT IMPLEMENTED]".equals(cashDrawerDevice)) {
                    if (drawer == null) {
                        drawer = new CashDrawer[10];
                    }

                    // create the instance
                    drawer[i-1] = new CashDrawer(cashDrawerDevice, -1);
                    try {
                        drawer[i-1].open();
                    } catch (jpos.JposException jpe) {
                        Debug.logError(jpe, "JPOS Exception", module);
                        throw new GeneralException(jpe.getOrigException());
                    }
                }
            }
        }
    }

    public static void enable(boolean enable) {
        if (keyboard != null) {
            keyboard.enable(enable);
        }
        if (scanner != null) {
            scanner.enable(enable);
        }
        if (msr != null) {
            msr.enable(enable);
        }
        if (check != null) {
            check.enable(enable);
        }
        if (ldisplay != null) {
            ldisplay.enable(enable);
        }
        if (pinpad != null) {
            pinpad.enable(enable);
        }
        if (receipt != null) {
            receipt.enable(enable);
        }
        
        // cash drawers and journal printer are
        // never able to be disabled so we can
        // notify when the drawer is open and
        // print any information needed to the
        // journal
    }

    public static void stop() throws GeneralException {
        try {
            if (keyboard != null) {
                keyboard.close();
            }
            if (scanner != null) {
                scanner.close();
            }
            if (msr != null) {
                msr.close();
            }
            if (check != null) {
                check.close();
            }
            if (ldisplay != null) {
                ldisplay.close();
            }
            if (pinpad != null) {
                pinpad.close();
            }

            if (drawer != null) {
                for (int i = 0; i < drawer.length; i++) {
                    if (drawer[i] != null) {
                        drawer[i].close();
                    }
                }
            }

            if (receipt != null) {
                receipt.close();
            }
            if (journal != null) {
                journal.close();
            }
        } catch (JposException jpe) {
            Debug.logError(jpe, "JPOS Exception", module);
            throw new GeneralException(jpe.getOrigException());
        }
    }
}
