/*
 * $Id: PaymentEvents.java,v 1.2 2004/08/15 21:26:42 ajzeneski Exp $
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
package org.ofbiz.pos.event;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.pos.PosTransaction;
import org.ofbiz.pos.component.Input;
import org.ofbiz.pos.screen.PosScreen;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class PaymentEvents {

    public static final String module = PaymentEvents.class.getName();

    public static void payCash(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        try {
            double amount = processAmount(pos, trans.getGrandTotal());
            Debug.log("Processing [Cash] Amount : " + amount, module);

            // add the payment
            trans.addPayment("CASH", amount);
        } catch (GeneralException e) {
            // errors handled
        }

        pos.refresh();
    }

    public static void payCredit(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        Input input = pos.getInput();
        String[] func = input.getLastFunction();

        if ("MSRINFO".equals(func[0])) {
            // make sure we have all necessary data
            int allInfo = validateCreditInfo(func[1]);
            if (allInfo == 1) {
                // missing expiration date
            } else if (allInfo == 0) {
                // missing card number (??)
            } else {
                // all info available add the payment
            }
        } else if ("CREDIT".equals(func[0])) {
            // amount is already set
            //String[] func = input.clearLastFunction(); // will be the last function

        } else {
            // first call; set the amount
            try {
                double amount = processAmount(pos, trans.getGrandTotal());
                Debug.log("Processing [Credit] Amount : " + amount, module);

                // set the CREDIT function
                input.setFunction("CREDIT");
            } catch (GeneralException e) {
                // errors handled
            }
        }
    }

    public static void clearAllPayments(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        trans.clearPayments();
        pos.refresh();
    }

    public static void processSale(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        if (trans.getTotalDue() > 0) {
            pos.showDialog("main/dialog/error/notenoughfunds");
        } else {
            // manual locks (not secured; will be unlocked on clear)
            pos.getInput().setLock(true);
            pos.getButtons().setLock(true);
            pos.getInput().clear();
            pos.getInput().setFunction("PAID");

            // display change
            pos.refresh();

            // process the order
            try {
                trans.processSale();
            } catch (GeneralException e) {
                pos.showDialog("main/dialog/error/testerror");
            }
        }
    }

    private static double processAmount(PosScreen pos, double grandTotal) throws GeneralException {
        Input input = pos.getInput();

        if (input.isFunctionSet("TOTAL")) {
            String amtStr = input.value();
            double amount;
            if (UtilValidate.isNotEmpty(amtStr)) {
                try {
                    amount = Double.parseDouble(amtStr);
                } catch (NumberFormatException e) {
                    Debug.logError("Invalid number for amount : " + amtStr, module);
                    pos.getOutput().print("Invalid Amount!");
                    input.clearInput();
                    throw new GeneralException();
                }
                amount = amount / 100; // convert to decimal
                Debug.log("Set amount / 100 : " + amount, module);
            } else {
                Debug.log("Amount is empty; assumption is full amount : " + grandTotal, module);
                amount = grandTotal;
            }
            return amount;
        } else {
            Debug.log("TOTAL function NOT set", module);
            throw new GeneralException();
        }
    }

    private static int validateCreditInfo(String creditInfo) {
        List cardSplit = StringUtil.split(creditInfo, "|");
        return cardSplit.size();
    }
}
