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
package org.ofbiz.pos;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

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
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.pos.component.Journal;
import org.ofbiz.pos.device.DeviceLoader;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
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
    protected String orderId = null;
    protected String partyId = null;
    protected Locale locale = null;
    protected boolean isMgr = false;
    protected int drawerIdx = 0;

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

    public String getUserId() {
        return session.getUserId();
    }

    public int getDrawerNumber() {
        return drawerIdx + 1;
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

    public String getOrderId() {
        return this.orderId;
    }

    public double getTaxTotal() {
        return cart.getTotalSalesTax();
    }
    
    public double getGrandTotal() {
        return UtilFormatOut.formatPriceNumber(cart.getGrandTotal()).doubleValue();
    }

    public double getPaymentTotal() {
        return UtilFormatOut.formatPriceNumber(cart.getPaymentTotal()).doubleValue();
    }

    public double getTotalDue() {
        double grandTotal = this.getGrandTotal();
        double paymentAmt = this.getPaymentTotal();
        return (grandTotal - paymentAmt);
    }

    public int size() {
        return cart.size();
    }

    public Map getItemInfo(int index) {
        ShoppingCartItem item = cart.findCartItem(index);
        Map itemInfo = new HashMap();
        itemInfo.put("productId", item.getProductId());
        itemInfo.put("description", item.getDescription());
        itemInfo.put("quantity", UtilFormatOut.formatQuantity(item.getQuantity()));
        itemInfo.put("basePrice", UtilFormatOut.formatPrice(item.getBasePrice()));
        itemInfo.put("subtotal", UtilFormatOut.formatPrice(item.getItemSubTotal()));
        itemInfo.put("isTaxable", item.taxApplies() ? "T" : " ");
        itemInfo.put("adjustments", item.getOtherAdjustments() > 0 ?
                UtilFormatOut.formatPrice(item.getOtherAdjustments()) : "");

        return itemInfo;
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
        
    public void addItem(String productId, double quantity) throws CartItemModifyException, ItemNotFoundException {
        trace("add item", productId + "/" + quantity);
        try {
            cart.addOrIncreaseItem(productId, quantity, session.getDispatcher());
        } catch (ItemNotFoundException e) {
            trace("item not found", e);
            throw e;
        } catch (CartItemModifyException e) {
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
        currentTx = null;
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

    public double processSale() throws GeneralException {
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

        if (orderRes != null && ServiceUtil.isError(orderRes)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(orderRes));
        } else if (orderRes != null) {
            this.orderId = (String) orderRes.get("orderId");
        }

        // process the payment(s)
        Map payRes = null;
        try {
            payRes = ch.processPayment(ProductStoreWorker.getProductStore(productStoreId, session.getDelegator()), session.getUserLogin(), true);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            throw e;
        }
        Debug.log("Process Payment Resp : " + payRes, module);

        if (payRes != null && ServiceUtil.isError(payRes)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(payRes));
        }

        // get the change due
        double change = (grandTotal - paymentAmt);

        // open the drawer (only supports 1 drawer for now)
        DeviceLoader.drawer[drawerIdx].openDrawer();

        // print the receipt
        DeviceLoader.receipt.printReceipt(this);

        // clear the tx
        cart.clear();
        currentTx = null;

        return change;
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
            int paymentInfoSize = cart.selectedPayments();
            for (int i = 0; i < paymentInfoSize; i++) {
                ShoppingCart.CartPaymentInfo inf = (ShoppingCart.CartPaymentInfo) cart.getPaymentInfo(i);
                GenericValue paymentInfoObj = inf.getValueObject(session.getDelegator());

                GenericValue paymentMethodType = null;
                GenericValue paymentMethod = null;
                if ("PaymentMethod".equals(paymentInfoObj.getEntityName())) {
                    paymentMethod = paymentInfoObj;
                    try {
                        paymentMethodType = paymentMethod.getRelatedOne("PaymentMethodType");
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                    }
                } else {
                    paymentMethodType = paymentInfoObj;
                }

                String desc = paymentMethodType != null ? paymentMethodType.getString("description") : "??";
                double amount = 0;
                if (inf.amount == null) {
                    amount = cart.getGrandTotal() - cart.getPaymentTotal();
                } else {
                    amount = inf.amount.doubleValue();
                }

                XModel paymentLine = Journal.appendNode(model, "tr", "", "");
                Journal.appendNode(paymentLine, "td", "sku", "");
                Journal.appendNode(paymentLine, "td", "desc", desc);
                Journal.appendNode(paymentLine, "td", "qty", "-");
                Journal.appendNode(paymentLine, "td", "price", UtilFormatOut.formatPrice(-1 * amount));
            }            
        }
    }

    public void appendChangeDataModel(XModel model) {
        if (cart != null) {
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

    public String makeCreditCardVo(String cardNumber, String expDate, String firstName, String lastName) {
        LocalDispatcher dispatcher = session.getDispatcher();
        String expMonth = expDate.substring(0, 2);
        String expYear = expDate.substring(2);
        // two digit year check -- may want to re-think this
        if (expYear.length() == 2) {
            expYear = "20" + expYear;
        }

        Map svcCtx = new HashMap();
        svcCtx.put("userLogin", session.getUserLogin());
        svcCtx.put("partyId", partyId);
        svcCtx.put("cardNumber", cardNumber);
        svcCtx.put("firstNameOnCard", firstName == null ? "" : firstName);
        svcCtx.put("lastNameOnCard", lastName == null ? "" : lastName);
        svcCtx.put("expMonth", expMonth);
        svcCtx.put("expYear", expYear);
        svcCtx.put("cardType", UtilValidate.getCardType(cardNumber));

        Debug.log("Create CC : " + svcCtx, module);
        Map svcRes = null;
        try {
            svcRes = dispatcher.runSync("createCreditCard", svcCtx);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return null;
        }
        if (ServiceUtil.isError(svcRes)) {
            Debug.logError(ServiceUtil.getErrorMessage(svcRes) + " - " + svcRes, module);
            return null;
        } else {
            return (String) svcRes.get("paymentMethodId");
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
