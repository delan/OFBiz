/*
 * $Id: DeviceLoader.java,v 1.2 2004/08/06 23:45:31 ajzeneski Exp $
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

import org.ofbiz.pos.screen.PosScreen;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.Debug;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.2
 */
public class DeviceLoader {

    public static final String module = DeviceLoader.class.getName();

    public static CashDrawer[] drawer = null;
    //public static ClerkDisplay clerk = null;
    //public static CustDisplay cust = null;
    public static Scanner scanner = null;
    public static Receipt receipt = null;
    //public static JLog journal = null;
    public static Msr msr = null;
    //public static CheckReader check = null;


    public static void load(PosScreen screen) throws GeneralException {
        // TODO get device names from some configuration -- THIS WILL CHANGE!

        // load the scanner
        scanner = new Scanner("KeyboardScanner", -1, screen);
        try {
            scanner.open();
        } catch (jpos.JposException jpe) {
            Debug.logError(jpe, "JPOS Exception", module);
            throw new GeneralException(jpe.getOrigException());
        }

        // load the check reader

        // load the msr
        msr = new Msr("KeyboardMsr", -1, screen);
        try {
            msr.open();
        } catch (jpos.JposException jpe) {
            Debug.logError(jpe, "JPOS Exception", module);
            throw new GeneralException(jpe.getOrigException());
        }

        // load the keyboard

        // load the receipt printer

        // load the journal printer

        // load the customer display

        // load the clerk display

        // load the cash drawers
    }
}
