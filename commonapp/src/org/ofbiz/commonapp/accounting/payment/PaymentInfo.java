/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.commonapp.accounting.payment;

import java.util.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.commonapp.order.order.*;
import org.ofbiz.commonapp.party.contact.*;

/**
 * PaymentInfo.java
 *
 * @author     cnelson
 * @version    1.0
 * @created    August 23, 2002
 */
public class PaymentInfo {

    /** Holds value of property address. */
    private GenericValue address;

    /** Holds value of property billToPerson. */
    private GenericValue billToPerson;

    /** Holds value of property emailAddress. */
    private String emailAddress;

    /** Holds value of property paymentPreference. */
    private GenericValue paymentPreference;

    /** Holds value of property amount. */
    private double amount;

    /** Holds value of property orderHeader. */
    private GenericValue orderHeader;

    /** Holds value of property currency. */
    private String currency;

    /** Creates a new instance of PaymentInfo */
    public PaymentInfo() {
    }

    public void fill(GenericValue address, double amount, GenericValue billToPerson,
            String emailAddress, GenericValue orderHeader, GenericValue paymentPreference) {
        this.address = address;
        this.billToPerson = billToPerson;
        this.currency = currency;
        this.amount = amount;
        this.emailAddress = emailAddress;
        this.orderHeader = orderHeader;
        this.paymentPreference = paymentPreference;
    }

    /** Getter for property address.
     * @return Value of property address.
     */
    public GenericValue getAddress() {
        return this.address;
    }

    /** Setter for property address.
     * @param address New value of property address.
     */
    public void setAddress(GenericValue address) {
        this.address = address;
    }

    /** Getter for property billToPerson.
     * @return Value of property billToPerson.
     */
    public GenericValue getBillToPerson() {
        return this.billToPerson;
    }

    /** Setter for property billToPerson.
     * @param billToPerson New value of property billToPerson.
     */
    public void setBillToPerson(GenericValue billToPerson) {
        this.billToPerson = billToPerson;
    }

    /** Getter for property emailAddress.
     * @return Value of property emailAddress.
     */
    public String getEmailAddress() {
        return this.emailAddress;
    }

    /** Setter for property emailAddress.
     * @param emailAddress New value of property emailAddress.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /** Getter for property paymentPreference.
     * @return Value of property paymentPreference.
     */
    public GenericValue getPaymentPreference() {
        return this.paymentPreference;
    }

    /** Setter for property paymentPreference.
     * @param paymentPreference New value of property paymentPreference.
     */
    public void setPaymentPreference(GenericValue paymentPreference) {
        this.paymentPreference = paymentPreference;
    }

    /** Getter for property amount.
     * @return Value of property amount.
     */
    public double getAmount() {
        return this.amount;
    }

    /** Setter for property amount.
     * @param amount New value of property amount.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /** Getter for property orderHeader.
     * @return Value of property orderHeader.
     */
    public GenericValue getOrderHeader() {
        return this.orderHeader;
    }

    /** Setter for property orderHeader.
     * @param orderHeader New value of property orderHeader.
     */
    public void setOrderHeader(GenericValue orderHeader) {
        this.orderHeader = orderHeader;
    }

    /** Getter for property currency.
     * @return Value of property currency.
     */
    public String getCurrency() {
        return this.currency;
    }

    /** Setter for property currency.
     * @param currency New value of property currency.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }


    /**
     * Get all the payments for a given order that have a given status
     * @param orderHeader -- the OrderHeader of the order
     * @payam statusId -- which status to use
     */
    public static List getPaymentsForOrder(GenericValue orderHeader, String statusId)
            throws GenericEntityException {
        LinkedList payments = new LinkedList();
        String currency = UtilProperties.getPropertyValue("payment.properties", "defaultCurrency", "USD");
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        GenericValue billToPerson = orh.getBillToPerson();
        String email = "";

        // Contact Info
        Collection emails = ContactHelper.getContactMech(billToPerson.getRelatedOne("Party"), "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
        if (emails != null && emails.size() > 0) {
            GenericValue em = (GenericValue) emails.iterator().next();
            email = em.getString("infoString");
        }

        Collection paymentPreferences = orderHeader.getRelatedByAnd("OrderPaymentPreference",
                UtilMisc.toMap("statusId", statusId));
        for (Iterator iter = paymentPreferences.iterator(); iter.hasNext();) {
            PaymentInfo payment = null;
            GenericValue paymentPreference = (GenericValue) iter.next();
            GenericValue paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");
            GenericValue address = null;
            if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD")) {
                GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");
                address = creditCard.getRelatedOne("PostalAddress");
                payment = new CreditCardPaymentInfo(creditCard);
            } else if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("EFT_ACCOUNT")) {
                GenericValue eftAcct = paymentMethod.getRelatedOne("EFT_ACCOUNT");
                address = eftAcct.getRelatedOne("PostalAddress");
                payment = new EFTPaymentInfo(eftAcct);

            }
            payment.setCurrency(currency);
            payment.fill(address,
                    orh.getOrderGrandTotal(),
                    billToPerson,
                    email,
                    orderHeader,
                    paymentPreference);
            payments.add(payment);
        }
        return payments;

    }

}
