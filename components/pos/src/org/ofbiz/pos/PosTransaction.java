/*
 * $Id: PosTransaction.java,v 1.1 2004/07/27 18:37:36 ajzeneski Exp $
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
package org.ofbiz.pos;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.xoetrope.xui.data.XModel;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.Log4jLoggerWriter;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.collections.LifoSet;
import org.ofbiz.content.xui.XuiSession;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.pos.component.Journal;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.ServiceUtil;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class PosTransaction {

    public static final String module = PosTransaction.class.getName();

    private static PrintWriter defaultPrintWriter = new Log4jLoggerWriter(Debug.getLogger(module));
    private static PosTransaction currentTx = null;
    private static LifoSet savedTx = new LifoSet();

    protected XuiSession session = null;
    protected ShoppingCart cart = null;
    protected CheckOutHelper ch = null;
    protected PrintWriter trace = null;

    protected String productStoreId = null;
    protected String transactionId = null;
    protected String terminalId = null;
    protected String currency = null;
    protected String partyId = null;
    protected Locale locale = null;
    protected boolean isMgr = false;

    private GenericValue shipAddress = null;

    public PosTransaction(XuiSession session) {
        this.session = session;
        this.terminalId = session.getId();
        this.partyId = "_NA_";
        this.trace = defaultPrintWriter;

        this.productStoreId = (String) session.getAttribute("productStoreId");
        this.currency = (String) session.getAttribute("currency");
        this.locale = (Locale) session.getAttribute("locale");

        this.cart = new ShoppingCart(session.getDelegator(), productStoreId, locale, currency);
        this.transactionId = session.getDelegator().getNextSeqId("PosTransaction");
        this.ch = new CheckOutHelper(session.getDispatcher(), session.getDelegator(), cart);
        currentTx = this;
        trace("transaction created");
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public boolean isMgr() {
        return this.isMgr;
    }

    public boolean isEmpty() {
        return (cart == null || cart.size() == 0);
    }

    public List lookupItem(String productId) {
        trace("item lookup", productId);
        return null;
    }

    public double getGrandTotal() {
        return new Double(UtilFormatOut.formatPrice(cart.getGrandTotal())).doubleValue();
    }

    public double getPaymentTotal() {
        return new Double(UtilFormatOut.formatPrice(cart.getPaymentTotal())).doubleValue();
    }

    public double getTotalDue() {
        double grandTotal = this.getGrandTotal();
        double paymentAmt = this.getPaymentTotal();
        return (grandTotal - paymentAmt);
    }

    public double getItemQuantity(String productId) {
        trace("request item quantity", productId);
        ShoppingCartItem item = cart.findCartItem(productId, null, null, null, 0.00);
        if (item != null) {
            return item.getQuantity();
        } else {
            trace("item not found", productId);
            return 0;
        }
    }

    public void addItem(String productId, double quantity) throws CartItemModifyException {
        trace("add item", productId + "/" + quantity);
        try {
            cart.addOrIncreaseItem(productId, quantity, session.getDispatcher());
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
            trace("add item error", e);
            throw e;
        }
    }

    public void modifyQty(String productId, double quantity) throws CartItemModifyException {
        trace("modify item quantity", productId + "/" + quantity);
        ShoppingCartItem item = cart.findCartItem(productId, null, null, null, 0.00);
        if (item != null) {
            try {
                item.setQuantity(quantity, session.getDispatcher(), cart, true);
            } catch (CartItemModifyException e) {
                Debug.logError(e, module);
                trace("modify item error", e);
                throw e;
            }
        } else {
            trace("item not found", productId);
        }
    }

    public void modifyPrice(String productId, double price) {
        trace("modify item price", productId + "/" + price);
        ShoppingCartItem item = cart.findCartItem(productId, null, null, null, 0.00);
        if (item != null) {
            item.setBasePrice(price);
        } else {
            trace("item not found", productId);
        }
    }

    public void voidItem(String productId) throws CartItemModifyException {
        trace("void item", productId);
        ShoppingCartItem item = cart.findCartItem(productId, null, null, null, 0.00);
        if (item != null) {
            try {
                int itemIdx = cart.getItemIndex(item);
                cart.removeCartItem(itemIdx, session.getDispatcher());
            } catch (CartItemModifyException e) {
                Debug.logError(e, module);
                trace("void item error", productId, e);
                throw e;
            }
        } else {
            trace("item not found", productId);
        }
    }

    public void voidSale() {
        trace("void sale");
        cart.clear();
    }

    public void calcTax() {
        try {
            ch.calcAndAddTax(this.getStoreOrgAddress());
        } catch (GeneralException e) {
            Debug.logError(e, module);
        }
    }

    public void clearTax() {
        cart.removeAdjustmentByType("SALES_TAX");
    }

    public double addPayment(String id, double amount) {
        trace("added payment", id + "/" + amount);
        Double currentAmt = cart.getPaymentAmount(id);
        if (currentAmt != null) {
            amount += currentAmt.doubleValue();
        }
        cart.addPaymentAmount(id, amount, true);
        return this.getTotalDue();
    }

    public void clearPayments() {
        cart.clearPayments();
    }

    public int selectedPayments() {
        return cart.selectedPayments();
    }

    public double processSale() {
        double grandTotal = this.getGrandTotal();
        double paymentAmt = this.getPaymentTotal();
        if (grandTotal > paymentAmt) {
            throw new IllegalStateException();
        }

        // attach the party ID to the cart
        cart.setOrderPartyId(partyId);

        // store the "order"
        Map orderRes = ch.createOrder(session.getUserLogin());
        Debug.log("Create Order Resp : " + orderRes, module);

        // process the payment(s)
        Map payRes = null;
        try {
            payRes = ch.processPayment(ProductStoreWorker.getProductStore(productStoreId, session.getDelegator()), session.getUserLogin(), true);
        } catch (GeneralException e) {
            Debug.logError(e, module);
        }
        Debug.log("Process Payment Resp : " + payRes, module);

        if (payRes == null || ServiceUtil.isError(payRes)) {
            // handle the error
        } else {
            cart.clear();
            currentTx = null;
        }

        return (grandTotal - paymentAmt);
    }

    private synchronized GenericValue getStoreOrgAddress() {
        if (this.shipAddress == null) {
            // locate the store's physical address - use this for tax
            GenericValue facility = (GenericValue) session.getAttribute("facility");
            if (facility == null) {
                return null;
            }

            List fcp = null;
            try {
                fcp = facility.getRelatedByAnd("FacilityContactMechPurpose", UtilMisc.toMap("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION"));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            fcp = EntityUtil.filterByDate(fcp);
            GenericValue purp = EntityUtil.getFirst(fcp);
            if (purp != null) {
                try {
                    this.shipAddress = session.getDelegator().findByPrimaryKey("PostalAddress",
                            UtilMisc.toMap("contactMechId", purp.getString("contactMechId")));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            }
        }
        return this.shipAddress;
    }

    public void saveTx() {
        savedTx.push(this);
        currentTx = null;
        trace("transaction saved");
    }

    public void appendItemDataModel(XModel model) {
        if (cart != null) {
            Iterator i = cart.iterator();
            while (i.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) i.next();
                double quantity = item.getQuantity();
                double unitPrice = item.getBasePrice();
                double subTotal = unitPrice * quantity;
                double adjustment = item.getOtherAdjustments();

                XModel line = Journal.appendNode(model, "tr", "", "");
                Journal.appendNode(line, "td", "sku", item.getProductId());
                Journal.appendNode(line, "td", "desc", item.getDescription());
                Journal.appendNode(line, "td", "qty", UtilFormatOut.formatQuantity(quantity));
                Journal.appendNode(line, "td", "price", UtilFormatOut.formatPrice(subTotal));
                if (adjustment != 0) {
                    // append the promo info
                    XModel promo = Journal.appendNode(model, "tr", "", "");
                    Journal.appendNode(promo, "td", "sku", "");
                    Journal.appendNode(promo, "td", "desc", "(adjustment)");
                    Journal.appendNode(promo, "td", "qty", "-");
                    Journal.appendNode(promo, "td", "price", UtilFormatOut.formatPrice(adjustment));
                }
            }
        }
    }

    public void appendTotalDataModel(XModel model) {
        if (cart != null) {
            double taxAmount = cart.getTotalSalesTax();
            double total = cart.getGrandTotal();

            XModel taxLine = Journal.appendNode(model, "tr", "", "");
            Journal.appendNode(taxLine, "td", "sku", "");
            Journal.appendNode(taxLine, "td", "desc", "Sales Tax");
            Journal.appendNode(taxLine, "td", "qty", "-");
            Journal.appendNode(taxLine, "td", "price", UtilFormatOut.formatPrice(taxAmount));

            XModel totalLine = Journal.appendNode(model, "tr", "", "");
            Journal.appendNode(totalLine, "td", "sku", "");
            Journal.appendNode(totalLine, "td", "desc", "Grand Total");
            Journal.appendNode(totalLine, "td", "qty", "-");
            Journal.appendNode(totalLine, "td", "price", UtilFormatOut.formatPrice(total));
        }
    }

    public void appendPaymentDataModel(XModel model) {
        if (cart != null) {
            Iterator pm = cart.getPaymentMethods().iterator();
            while (pm.hasNext()) {

            }

            Iterator pt = cart.getPaymentMethodTypes().iterator();
            while (pt.hasNext()) {
                GenericValue paymentMethodType = (GenericValue) pt.next();
                String paymentId = paymentMethodType.getString("paymentMethodTypeId");
                String desc = paymentMethodType.getString("description");
                double amount = cart.getPaymentAmount(paymentId).doubleValue();

                XModel paymentLine = Journal.appendNode(model, "tr", "", "");
                Journal.appendNode(paymentLine, "td", "sku", "");
                Journal.appendNode(paymentLine, "td", "desc", desc);
                Journal.appendNode(paymentLine, "td", "qty", "-");
                Journal.appendNode(paymentLine, "td", "price", UtilFormatOut.formatPrice(-1 * amount));
            }

            double changeDue = (-1 * this.getTotalDue());
            if (changeDue >= 0) {
                XModel changeLine = Journal.appendNode(model, "tr", "", "");
                Journal.appendNode(changeLine, "td", "sku", "");
                Journal.appendNode(changeLine, "td", "desc", "Change");
                Journal.appendNode(changeLine, "td", "qty", "-");
                Journal.appendNode(changeLine, "td", "price", UtilFormatOut.formatPrice(changeDue));
            }
        }
    }

    public void setPrintWriter(PrintWriter writer) {
        this.trace = writer;
    }

    private void trace(String s) {
        trace(s, null, null);
    }

    private void trace(String s, Throwable t) {
        trace(s, null, t);
    }

    private void trace(String s1, String s2) {
        trace(s1, s2, null);
    }

    private void trace(String s1, String s2, Throwable t) {
        if (trace != null) {
            String msg = s1;
            if (UtilValidate.isNotEmpty(s2)) {
                msg = msg + "(" + s2 + ")";
            }
            if (t != null) {
                msg = msg + " : " + t.getMessage();
            }

            // print the trace line
            trace.println("[POS @ " + terminalId + " TX:" + transactionId + "] - " + msg);
            trace.flush();
        }
    }

    public static synchronized PosTransaction getCurrentTx(XuiSession session) {
        if (currentTx == null) {
            currentTx = new PosTransaction(session);
        }
        return currentTx;
    }
}
