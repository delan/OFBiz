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
package org.ofbiz.pos.event;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.sql.Timestamp;

import net.xoetrope.xui.XProjectManager;

import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.pos.device.DeviceLoader;
import org.ofbiz.pos.device.impl.Receipt;
import org.ofbiz.pos.screen.PosScreen;
import org.ofbiz.pos.PosTransaction;
import org.ofbiz.pos.adaptor.SyncCallbackAdaptor;
import org.ofbiz.pos.component.Input;
import org.ofbiz.pos.component.Output;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.content.xui.XuiSession;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class ManagerEvents {

    public static final String module = ManagerEvents.class.getName();
    public static boolean mgrLoggedIn = false;

    public static void modifyPrice(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        String sku = null;
        try {
            sku = MenuEvents.getSelectedItem(pos);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        if (sku == null) {
            pos.getOutput().print("Invalid Selection!");
            pos.getJournal().refresh(pos);
            pos.getInput().clear();
        }

        Input input = pos.getInput();
        String value = input.value();
        if (UtilValidate.isNotEmpty(value)) {
            double price = 0.00;
            boolean parsed = false;
            try {
                price = Double.parseDouble(value);
                parsed = true;
            } catch (NumberFormatException e) {
            }

            if (parsed) {
                price = price / 100;
                trans.modifyPrice(sku, price);

                // re-calc tax
                trans.calcTax();
            }
        }

        // refresh the other components
        pos.refresh();
    }

    public static void openTerminal(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("dialog/error/mgrnotloggedin");
            return;
        }

        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        Input input = pos.getInput();
        if (!trans.isOpen()) {
            if (input.isFunctionSet("OPEN")) {
                String amountStr = input.value();
                Double amount = null;
                if (UtilValidate.isNotEmpty(amountStr)) {
                    try {
                        double amt = Double.parseDouble(amountStr);
                        amt = amt / 100;
                        amount = new Double(amt);
                    } catch (NumberFormatException e) {
                        Debug.logError(e, module);
                    }
                }
                GenericValue state = pos.getSession().getDelegator().makeValue("PosTerminalState", null);
                state.set("posTerminalId", pos.getSession().getId());
                state.set("openedDate", UtilDateTime.nowTimestamp());
                state.set("openedByUserLoginId", pos.getSession().getUserId());
                state.set("startingTxId", trans.getTransactionId());
                state.set("startingDrawerAmount", amount);
                try {
                    state.create();
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    pos.showDialog("dialog/error/exception", e.getMessage());
                }
                NavagationEvents.showPosScreen(pos);
            } else {
                input.clear();
                input.setFunction("OPEN");
                pos.getOutput().print(Output.OPDRAM);
                return;
            }
        } else {
            pos.showPage("pospanel");
        }
    }

    public static void closeTerminal(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("dialog/error/mgrnotloggedin");
            return;
        }

        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        if (!trans.isOpen()) {
            pos.showDialog("dialog/error/terminalclosed");
            return;
        }

        Output output = pos.getOutput();
        Input input = pos.getInput();
        if (input.isFunctionSet("CLOSE")) {
            String[] func = input.getFunction("CLOSE");
            String lastValue = input.value();
            if (UtilValidate.isNotEmpty(lastValue)) {
                try {
                    double dbl = Double.parseDouble(lastValue);
                    dbl = dbl / 100;
                    lastValue = UtilFormatOut.formatPrice(dbl);
                } catch (NumberFormatException e) {
                    Debug.logError(e, module);
                }
                if (UtilValidate.isNotEmpty(func[1])) {
                    func[1] = func[1] + "|";
                }
                func[1] = func[1] + lastValue;
                input.setFunction("CLOSE", func[1]);
            }

            String[] closeInfo = new String[0];
            if (UtilValidate.isNotEmpty(func[1])) {
                closeInfo = func[1].split("\\|");
            }
            switch (closeInfo.length) {
                case 0:
                    output.print(Output.ENTCAS);
                    break;
                case 1:
                    output.print(Output.ENTCHK);
                    break;
                case 2:
                    output.print(Output.ENTCRC);
                    break;
                case 3:
                    output.print(Output.ENTGFC);
                    break;
                case 4:
                    output.print(Output.ENTOTH);
                    break;
                case 5:
                    GenericValue state = trans.getTerminalState();
                    state.set("closedDate", UtilDateTime.nowTimestamp());
                    state.set("closedByUserLoginId", pos.getSession().getUserId());
                    state.set("actualEndingCash", new Double(closeInfo[0]));
                    state.set("actualEndingCheck", new Double(closeInfo[1]));
                    state.set("actualEndingCc", new Double(closeInfo[2]));
                    state.set("actualEndingGc", new Double(closeInfo[3]));
                    state.set("actualEndingOther", new Double(closeInfo[4]));
                    state.set("endingTxId", trans.getTransactionId());
                    Debug.log("Updated State - " + state, module);
                    try {
                        state.store();
                        state.refresh();
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        pos.showDialog("dialog/error/exception", e.getMessage());
                    }

                    // print the totals report
                    printTotals(pos, state, true);

                    // lock the terminal for the moment
                    output.print("Waiting for final sales data transmission...");
                    pos.getInput().setLock(true);
                    pos.getButtons().setLock(true);
                    pos.refresh(false);

                    // transmit final data to server
                    GenericValue terminal = null;
                    try {
                        terminal = state.getRelatedOne("PosTerminal");
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        pos.showDialog("dialog/error/exception", e.getMessage());
                    }
                    if (terminal != null && terminal.get("pushEntitySyncId") != null) {
                        String syncId = terminal.getString("pushEntitySyncId");
                        SyncCallbackAdaptor cb = new SyncCallbackAdaptor(pos, syncId, state.getTimestamp("lastUpdatedTxStamp"));
                        pos.getSession().getDispatcher().registerCallback("runEntitySync", cb);
                    } else {
                        // no sync setting; just logout
                        pos.showDialog("dialog/error/terminalclosed");
                        SecurityEvents.logout(pos);
                    }
            }
        } else {
            trans.popDrawer();
            input.clear();
            input.setFunction("CLOSE");
            output.print(Output.ENTCAS);
        }

    }

    public static void voidOrder(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("dialog/error/mgrnotloggedin");
            return;
        }

        XuiSession session = pos.getSession();
        PosTransaction trans = PosTransaction.getCurrentTx(session);
        if (!trans.isOpen()) {
            pos.showDialog("dialog/error/terminalclosed");
            return;
        }

        Output output = pos.getOutput();
        Input input = pos.getInput();
        boolean lookup = false;

        if (input.isFunctionSet("VOID")) {
            lookup = true;
        } else if (UtilValidate.isNotEmpty(input.value())) {
            lookup = true;
        }

        if (lookup) {
            GenericValue state = trans.getTerminalState();
            Timestamp openDate = state.getTimestamp("openedDate");

            String orderId = input.value();
            GenericValue orderHeader = null;
            try {
                orderHeader = session.getDelegator().findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            if (orderHeader == null) {
                input.clear();
                pos.showDialog("dialog/error/ordernotfound");
                return;
            } else {
                Timestamp orderDate = orderHeader.getTimestamp("orderDate");
                if (orderDate.after(openDate)) {
                    LocalDispatcher dispatcher = session.getDispatcher();
                    try {
                        dispatcher.runSyncIgnore("quickReturnOrder", UtilMisc.toMap("orderId", orderId, "userLogin", session.getUserLogin()));
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        pos.showDialog("dialog/error/exception", e.getMessage());
                    }
                    // todo print void receipt

                    input.clear();
                    pos.showDialog("dialog/error/salevoided");
                    pos.refresh();
                } else {
                    input.clear();
                    pos.showDialog("dialog/error/ordernotfound");
                    return;
                }
            }
        } else {
            input.setFunction("VOID");
            output.print("Enter Order Number To Void:");
        }
    }

    public static void reprintLastTx(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("dialog/error/mgrnotloggedin");
            return;
        }
        DeviceLoader.receipt.reprintReceipt(true);
        pos.refresh();
    }

    public static void popDrawer(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("dialog/error/mgrnotloggedin");
        } else {
            PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
            trans.popDrawer();
            pos.refresh();
        }
    }

    public static void clearCache(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("dialog/error/mgrnotloggedin");
        } else {
            UtilCache.clearAllCaches();
            pos.refresh();
        }
    }

    public static void resetXui(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("dialog/error/mgrnotloggedin");
        } else {
            XProjectManager.getPageManager().reset();
            pos.refresh();
        }
    }

    public static void shutdown(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("dialog/error/mgrnotloggedin");
        } else {
            pos.getOutput().print("Shutting down...");
            System.exit(0);
        }
    }

    public static void totalsReport(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("dialog/error/mgrnotloggedin");
            return;
        }
        printTotals(pos, null, false);
    }

    private static void printTotals(PosScreen pos, GenericValue state, boolean runBalance) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        if (!trans.isOpen()) {
            pos.showDialog("dialog/error/terminalclosed");
            return;
        }
        if (state == null) {
            state = trans.getTerminalState();
        }

        double checkTotal = 0.00;
        double cashTotal = 0.00;
        double gcTotal = 0.00;
        double ccTotal = 0.00;
        double othTotal = 0.00;
        double total = 0.00;

        GenericDelegator delegator = pos.getSession().getDelegator();
        List exprs = UtilMisc.toList(new EntityExpr("originFacilityId", EntityOperator.EQUALS, trans.getFacilityId()),
                new EntityExpr("terminalId", EntityOperator.EQUALS, trans.getTerminalId()));
        EntityListIterator eli = null;

        try {
            eli = delegator.findListIteratorByCondition("OrderHeaderAndPaymentPref", new EntityConditionList(exprs, EntityOperator.AND), null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        Timestamp dayStart = state.getTimestamp("openedDate");
        Timestamp dayEnd = state.getTimestamp("closedDate");
        if (dayEnd == null) {
            dayEnd = UtilDateTime.nowTimestamp();
        }

        if (eli != null) {
            GenericValue ohpp;
            while (((ohpp = (GenericValue) eli.next()) != null)) {
                Timestamp orderDate = ohpp.getTimestamp("orderDate");
                if (orderDate.after(dayStart) && orderDate.before(dayEnd)) {
                    String pmt = ohpp.getString("paymentMethodTypeId");
                    Double amt = ohpp.getDouble("maxAmount");

                    if ("CASH".equals(pmt)) {
                        cashTotal += amt.doubleValue();
                    } else  if ("CHECK".equals(pmt)) {
                        checkTotal += amt.doubleValue();
                    } else if ("GIFT_CARD".equals(pmt)) {
                        gcTotal += amt.doubleValue();
                    } else if ("CREDIT_CARD".equals(pmt)) {
                        ccTotal += amt.doubleValue();
                    } else {
                        othTotal += amt.doubleValue();
                    }
                    total += amt.doubleValue();
                }
            }

            try {
                eli.close();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Trouble closing ELI", module);
            }
        }

        Map reportMap = new HashMap();
        String reportTemplate = "totals.txt";

        // titles
        reportMap.put("cashTitle", UtilFormatOut.padString("CASH:", 20, false, ' '));
        reportMap.put("checkTitle", UtilFormatOut.padString("CHECK:", 20, false, ' '));
        reportMap.put("giftCardTitle", UtilFormatOut.padString("GIFT CARD:", 20, false, ' '));
        reportMap.put("creditCardTitle", UtilFormatOut.padString("CREDIT CARD:", 20, false, ' '));
        reportMap.put("otherTitle", UtilFormatOut.padString("OTHER:", 20, false, ' '));
        reportMap.put("grossSalesTitle", UtilFormatOut.padString("GROSS SALES:", 20, false, ' '));
        reportMap.put("+/-", UtilFormatOut.padString("+/-", 20, false, ' '));
        reportMap.put("spacer", UtilFormatOut.padString("", 20, false, ' '));

        // logged
        reportMap.put("cashTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(cashTotal), 8, false, ' '));
        reportMap.put("checkTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(checkTotal), 8, false, ' '));
        reportMap.put("ccTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(ccTotal), 8, false, ' '));
        reportMap.put("gcTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(gcTotal), 8, false, ' '));
        reportMap.put("otherTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(othTotal), 8, false, ' '));
        reportMap.put("grossTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(total), 8, false, ' '));

        if (runBalance) {
            // actuals
            double cashEnd = state.getDouble("actualEndingCash").doubleValue();
            double checkEnd = state.getDouble("actualEndingCheck").doubleValue();
            double ccEnd = state.getDouble("actualEndingCc").doubleValue();
            double gcEnd = state.getDouble("actualEndingGc").doubleValue();
            double othEnd = state.getDouble("actualEndingOther").doubleValue();
            double grossEnd = cashEnd + checkEnd + ccEnd + gcEnd + othEnd;

            reportMap.put("cashEnd", UtilFormatOut.padString(UtilFormatOut.formatPrice(cashEnd), 8, false, ' '));
            reportMap.put("checkEnd", UtilFormatOut.padString(UtilFormatOut.formatPrice(checkEnd), 8, false, ' '));
            reportMap.put("ccEnd", UtilFormatOut.padString(UtilFormatOut.formatPrice(ccEnd), 8, false, ' '));
            reportMap.put("gcEnd", UtilFormatOut.padString(UtilFormatOut.formatPrice(gcEnd), 8, false, ' '));
            reportMap.put("otherEnd", UtilFormatOut.padString(UtilFormatOut.formatPrice(othEnd), 8, false, ' '));
            reportMap.put("grossEnd", UtilFormatOut.padString(UtilFormatOut.formatPrice(grossEnd), 8, false, ' '));

            // diffs
            double cashDiff = cashEnd - cashTotal;
            double checkDiff = checkEnd - checkTotal;
            double ccDiff = ccEnd - ccTotal;
            double gcDiff = gcEnd - gcTotal;
            double othDiff = othEnd - othTotal;
            double grossDiff = cashDiff + checkDiff + ccDiff + gcDiff + othDiff;

            reportMap.put("cashDiff", UtilFormatOut.padString(UtilFormatOut.formatPrice(cashDiff), 8, false, ' '));
            reportMap.put("checkDiff", UtilFormatOut.padString(UtilFormatOut.formatPrice(checkDiff), 8, false, ' '));
            reportMap.put("ccDiff", UtilFormatOut.padString(UtilFormatOut.formatPrice(ccDiff), 8, false, ' '));
            reportMap.put("gcDiff", UtilFormatOut.padString(UtilFormatOut.formatPrice(gcDiff), 8, false, ' '));
            reportMap.put("otherDiff", UtilFormatOut.padString(UtilFormatOut.formatPrice(othDiff), 8, false, ' '));
            reportMap.put("grossDiff", UtilFormatOut.padString(UtilFormatOut.formatPrice(grossDiff), 8, false, ' '));

            // set the report template
            reportTemplate = "balance.txt";
        }

        Receipt receipt = DeviceLoader.receipt;
        if (receipt.isEnabled()) {
            receipt.printReport(trans, reportTemplate, reportMap);
        }
    }
}
