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
import java.awt.AWTEvent;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.pos.PosTransaction;
import org.ofbiz.pos.config.ButtonEventConfig;
import org.ofbiz.pos.component.Input;
import org.ofbiz.pos.component.Journal;
import org.ofbiz.pos.screen.PosScreen;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class MenuEvents {

    public static final String module = MenuEvents.class.getName();

    // extended number events
    public static void triggerClear(PosScreen pos) {
        // clear the components
        String[] totalFunc = pos.getInput().getFunction("TOTAL");
        String[] paidFunc = pos.getInput().getFunction("PAID");
        if (paidFunc != null) {
            pos.getInput().clear();
            pos.showPage("main/pospanel");
        } else {
            if (UtilValidate.isEmpty(pos.getInput().value())) {
                pos.getInput().clear();
            }
            if (totalFunc != null) {
                pos.getInput().setFunction("TOTAL", totalFunc[1]);
            }
        }

        // refresh the current screen
        pos.refresh();

        // clear out the manual locks
        if (!pos.isLocked()) {
            pos.getInput().setLock(false);
            pos.getButtons().setLock(false);
        } else {
            // just re-call set lock
            pos.setLock(true);
        }
    }

    public static void triggerQty(PosScreen pos) {
        pos.getInput().setFunction("QTY");
    }

    public static void triggerEnter(PosScreen pos, AWTEvent event) {
        // enter key maps to various different events; depending on the function
        Input input = pos.getInput();
        String[] lastFunc = input.getLastFunction();
        if (lastFunc != null) {
            if ("MGRLOGIN".equals(lastFunc[0])) {
                SecurityEvents.mgrLogin(pos);
            } else if ("LOGIN".equals(lastFunc[0])) {
                SecurityEvents.login(pos);
            } else if ("REFNUM".equals(lastFunc[0])) {
                PaymentEvents.setRefNum(pos);
            } else if ("CREDIT".equals(lastFunc[0])) {
                PaymentEvents.payCredit(pos);
            } else if ("CHECK".equals(lastFunc[0])) {
                PaymentEvents.payCheck(pos);
            } else if ("GIFTCARD".equals(lastFunc[0])) {
                PaymentEvents.payGiftCard(pos);
            } else if ("MSRINFO".equals(lastFunc[0])) {
                if (input.isFunctionSet("CREDIT")) {
                    PaymentEvents.payCredit(pos);
                } else if (input.isFunctionSet("GIFTCARD")) {
                    PaymentEvents.payGiftCard(pos);
                }
            } else if ("SKU".equals(lastFunc[0])) {
                MenuEvents.addItem(pos, event);
            }
        } else if (input.value().length() > 0) {
            MenuEvents.addItem(pos, event);
        }
    }

    public static void addItem(PosScreen pos, AWTEvent event) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        Input input = pos.getInput();
        String[] func = input.getFunction("QTY");
        String value = input.value();

        // no value; just return
        if (event != null && UtilValidate.isEmpty(value)) {
            String buttonName = ButtonEventConfig.getButtonName(event);
            if (UtilValidate.isNotEmpty(buttonName)) {
                if (buttonName.startsWith("SKU.")) {
                    value = buttonName.substring(4);
                }
            }
            if (UtilValidate.isEmpty(value)) {
                return;
            }
        }

        // check for quantity
        double quantity = 1;
        if (func != null && "QTY".equals(func[0])) {
            try {
                quantity = Double.parseDouble(func[1]);
            } catch (NumberFormatException e) {
                quantity = 1;
            }
        }

        // locate the product ID
        String productId = null;
        try {
            List items = trans.lookupItem(value);
            if (items != null && items.size() == 1) {
                GenericValue product = EntityUtil.getFirst(items);
                productId = product.getString("productId");
            } else if (items != null && items.size() > 0) {
                Debug.logInfo("Multiple products found; need to select one from the list", module);
            }
        } catch (GeneralException e) {
            Debug.logError(e, module);
            pos.showDialog("main/dialog/error/producterror");
        }

        // add the item to the cart; report any errors to the user
        if (productId != null) {
            try {
                trans.addItem(productId, quantity);
            } catch (CartItemModifyException e) {
                Debug.logError(e, module);
                pos.showDialog("main/dialog/error/producterror");
            } catch (ItemNotFoundException e) {
                pos.showDialog("main/dialog/error/productnotfound");
            }
        } else {
            pos.showDialog("main/dialog/error/productnotfound");
        }

        // clear the qty flag
        input.clearFunction("QTY");
        
        // re-calc tax
        trans.calcTax();

        // refresh the other components
        pos.refresh();
    }

    public static void changeQty(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        String sku = null;
        try {
            sku = getSelectedItem(pos);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        if (sku == null) {
            pos.getOutput().print("Invalid Selection!");
            pos.getJournal().refresh(pos);
            pos.getInput().clear();
        }

        Input input = pos.getInput();
        String value = input.value();

        boolean increment = true;
        double quantity = 1;
        if (UtilValidate.isNotEmpty(value)) {
            try {
                quantity = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                quantity = 1;
            }
        } else {
            String[] func = input.getLastFunction();
            if (func != null && "QTY".equals(func[0])) {
                increment = false;
                try {
                    quantity = Double.parseDouble(func[1]);
                } catch (NumberFormatException e) {
                    quantity = trans.getItemQuantity(sku);
                }
            }
        }

        // adjust the quantity
        quantity = (increment ? trans.getItemQuantity(sku) + quantity : quantity);

        try {
            trans.modifyQty(sku, quantity);
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
            pos.showDialog("main/dialog/error/producterror");
        }

        // clear the qty flag
        input.clearFunction("QTY");

        // re-calc tax
        trans.calcTax();

        // refresh the other components
        pos.refresh();
    }

    public static void calcTotal(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        trans.calcTax();

        pos.getInput().setFunction("TOTAL");
        pos.getJournal().refresh(pos);
    }

    public static void voidItem(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        String sku = null;
        try {
            sku = getSelectedItem(pos);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        if (sku == null) {
            pos.getOutput().print("Invalid Selection!");
            pos.getJournal().refresh(pos);
            pos.getInput().clear();
        }

        try {
            trans.voidItem(sku);
        } catch (CartItemModifyException e) {
            pos.getOutput().print(e.getMessage());
        }

        // re-calc tax
        trans.calcTax();
        pos.refresh();
    }

    public static void voidAll(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        trans.voidSale();
        pos.refresh();
    }

    private static String getSelectedItem(PosScreen pos) {
        Journal journal = pos.getJournal();
        return journal.getSelectedSku();
    }
}
