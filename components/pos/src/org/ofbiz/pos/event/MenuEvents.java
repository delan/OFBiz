/*
 * $Id: MenuEvents.java,v 1.3 2004/08/17 19:51:26 ajzeneski Exp $
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

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.pos.PosTransaction;
import org.ofbiz.pos.component.Input;
import org.ofbiz.pos.component.Journal;
import org.ofbiz.pos.screen.PosScreen;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.3 $
 * @since      3.1
 */
public class MenuEvents {

    public static final String module = MenuEvents.class.getName();

    public static void addItem(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        Input input = pos.getInput();
        String[] func = input.getFunction("QTY");
        String value = input.value();

        // no value; just return
        if (UtilValidate.isEmpty(value)) {
            return;
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

        // add the item to the cart; report any errors to the user
        try {
            trans.addItem(value, quantity);
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
            pos.showDialog("main/dialog/error/producterror");
        } catch (ItemNotFoundException e) {
            pos.showDialog("main/dialog/error/productnotfound");
        }

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

        pos.getJournal().refresh(pos);
        pos.getInput().clear();
    }

    public static void voidAll(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        trans.voidSale();

        pos.getJournal().refresh(pos);
        pos.getInput().clear();
    }

    private static String getSelectedItem(PosScreen pos) {
        Journal journal = pos.getJournal();
        return journal.getSelectedSku();
    }
}
