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
import org.ofbiz.pos.component.Input;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityConditionList;

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

    public static void openDrawer(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("main/dialog/error/mgrnotloggedin");
        } else {
            DeviceLoader.drawer[0].openDrawer();
            pos.refresh();
        }
    }

    public static void clearCache(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("main/dialog/error/mgrnotloggedin");
        } else {
            UtilCache.clearAllCaches();
            pos.refresh();
        }
    }

    public static void resetXui(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("main/dialog/error/mgrnotloggedin");
        } else {
            XProjectManager.getPageManager().reset();
            pos.refresh();
        }
    }

    public static void shutdown(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("main/dialog/error/mgrnotloggedin");
        } else {
            pos.getOutput().print("Shutting down...");
            System.exit(0);
        }
    }

    public static void totalsReport(PosScreen pos) {
        if (!mgrLoggedIn) {
            pos.showDialog("main/dialog/error/mgrnotloggedin");
            return;
        }

        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
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

        Timestamp dayStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
        Timestamp dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
        if (eli != null) {
            GenericValue ohpp;
            while (((ohpp = (GenericValue) eli.next()) != null)) {
                Timestamp orderDate = ohpp.getTimestamp("orderDate");
                if (orderDate.after(dayStart) && orderDate.before(dayEnd)) {
                    String pmt = ohpp.getString("paymentMethodTypeId");
                    Double amt = ohpp.getDouble("maxAmount");

                    Debug.log("PMT - " + pmt, module);
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
        reportMap.put("cashTitle", UtilFormatOut.padString("CASH:", 20, false, ' '));
        reportMap.put("checkTitle", UtilFormatOut.padString("CHECK:", 20, false, ' '));
        reportMap.put("giftCardTitle", UtilFormatOut.padString("GIFT CARD:", 20, false, ' '));
        reportMap.put("creditCardTitle", UtilFormatOut.padString("CREDIT CARD:", 20, false, ' '));
        reportMap.put("otherTitle", UtilFormatOut.padString("OTHER:", 20, false, ' '));
        reportMap.put("grossSalesTitle", UtilFormatOut.padString("GROSS SALES:", 20, false, ' '));

        reportMap.put("cashTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(cashTotal), 8, false, ' '));
        reportMap.put("checkTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(checkTotal), 8, false, ' '));
        reportMap.put("giftCardTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(gcTotal), 8, false, ' '));
        reportMap.put("creditCardTotalTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(ccTotal), 8, false, ' '));
        reportMap.put("otherTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(othTotal), 8, false, ' '));
        reportMap.put("grossSalesTotal", UtilFormatOut.padString(UtilFormatOut.formatPrice(total), 8, false, ' '));

        Receipt receipt = DeviceLoader.receipt;
        if (receipt.isEnabled()) {
            receipt.printReport(trans, "totals.txt", reportMap);
        }
    }
}
