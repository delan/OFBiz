/*
 * $Id$
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.accounting.invoice;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * InvoiceWorker - Worker methods of invoices
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Rev:$
 * @since      2.1
 */
public class InvoiceWorker {
    
    public static String module = InvoiceWorker.class.getName();
    
    /**
     * Method to return the total amount of an invoice
     * @param invoice GenericValue object of the Invoice
     * @return the invoice total as double
     */
    public static double getInvoiceTotal(GenericDelegator delegator, String invoiceId) {
        if (delegator == null) {
            throw new IllegalArgumentException("Null delegator is not allowed in this method");
        }
        
        GenericValue invoice = null;
        try {
            invoice = delegator.findByPrimaryKey("Invoice", UtilMisc.toMap("invoiceId", invoiceId));    
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Invoice", module);
        }
        
        if (invoice == null) {
            throw new IllegalArgumentException("The invoiceId passed does not match an existing invoice");
        }
        
        return getInvoiceTotal(invoice);
    }
    
    /**
     * Method to return the total amount of an invoice
     * @param invoice GenericValue object of the Invoice
     * @return the invoice total as double
     */
    public static double getInvoiceTotal(GenericValue invoice) {
        double invoiceTotal = 0.00;
        List invoiceItems = null;
        try {
            invoiceItems = invoice.getRelated("InvoiceItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting InvoiceItem list", module);            
        }
        if (invoiceItems != null && invoiceItems.size() > 0) {
            Iterator invoiceItemsIter = invoiceItems.iterator();
            while (invoiceItemsIter.hasNext()) {
                GenericValue invoiceItem = (GenericValue) invoiceItemsIter.next();
                Double amount = invoiceItem.getDouble("amount");
                Double quantity = invoiceItem.getDouble("quantity");
                if (amount == null)
                    amount = new Double(0.00);
                if (quantity == null)
                    quantity = new Double(1);
                invoiceTotal += amount.doubleValue() * quantity.doubleValue();
            }
        }

        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);
        String invoiceTotalString = formatter.format(invoiceTotal);
        Double formattedTotal = null;
        try {
            formattedTotal = new Double(formatter.parse(invoiceTotalString).doubleValue());
        } catch (ParseException e) {
            Debug.logError(e, "Problem getting parsed tax amount; using the primitive value", module);
            formattedTotal = new Double(invoiceTotal);
        }
        
        return formattedTotal.doubleValue();        
    }
    
    /**
     * Method to obtain the bill to party for an invoice
     * @param invoice GenericValue object of the Invoice
     * @return GenericValue object of the Party
     */
    public static GenericValue getBillToParty(GenericValue invoice) {
        List billToRoles = null;
        try {
            billToRoles = invoice.getRelated("InvoiceRole", UtilMisc.toMap("roleTypeId", "BILL_TO_CUSTOMER"), 
                UtilMisc.toList("-datetimePerformed"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting InvoiceRole list", module);            
        }
        
        if (billToRoles != null) {        
            GenericValue role = EntityUtil.getFirst(billToRoles);
            GenericValue party = null;
            try {
                party = role.getRelatedOne("Party");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting Party from InvoiceRole", module);
            }
            if (party != null)
                return party;
        }            
        return null;        
    }
    
    /**
      * Method to obtain the send from party for an invoice
      * @param invoice GenericValue object of the Invoice
      * @return GenericValue object of the Party
      */    
    public static GenericValue getSendFromParty(GenericValue invoice) {
        List sendFromRoles = null;
        try {
            sendFromRoles = invoice.getRelated("InvoiceRole", UtilMisc.toMap("roleTypeId", "BILL_FROM_VENDOR"), 
                UtilMisc.toList("-datetimePerformed"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting InvoiceRole list", module);            
        }
        
        if (sendFromRoles != null) {        
            GenericValue role = EntityUtil.getFirst(sendFromRoles);
            GenericValue party = null;
            try {
                party = role.getRelatedOne("Party");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting Party from InvoiceRole", module);
            }
            if (party != null)
                return party;
        }            
        return null;        
    }
    
    /**
      * Method to obtain the billing address for an invoice
      * @param invoice GenericValue object of the Invoice
      * @return GenericValue object of the PostalAddress
      */        
    public static GenericValue getBillToAddress(GenericValue invoice) {
        return getInvoiceAddressByType(invoice, "BILLING_LOCATION");        
    }
    
    /**
      * Method to obtain the sending address for an invoice
      * @param invoice GenericValue object of the Invoice
      * @return GenericValue object of the PostalAddress
      */        
    public static GenericValue getSendFromAddress(GenericValue invoice) {
        return getInvoiceAddressByType(invoice, "PAYMENT_LOCATION");                
    }
    
    public static GenericValue getInvoiceAddressByType(GenericValue invoice, String contactMechPurposeTypeId) {
        GenericDelegator delegator = invoice.getDelegator();
        List locations = null;
        try {
            locations = invoice.getRelated("InvoiceContactMech", UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId), null);
        } catch (GenericEntityException e) {
            Debug.logError("Touble getting InvoiceContactMech entity list", module);           
        }
        
        GenericValue postalAddress = null;
        if (locations != null && locations.size() > 0) {
            GenericValue purpose = EntityUtil.getFirst(locations);                      
            try {
                postalAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", purpose.getString("contactMechId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting PostalAddress for contactMechId: " + purpose.getString("contactMechId"), module);
            }
        }
        
        return postalAddress;
    }    
    
    private static GenericValue getAddressFromParty(GenericValue party, String purposeTypeId) {
        if (party == null) return null;
        
        GenericValue contactMech = null;
        GenericValue postalAddress = null;
        try {
            List mecs = party.getRelated("PartyContactMechPurpose", 
                UtilMisc.toMap("contactMechPurposeTypeId", purposeTypeId), null);
            if (mecs != null) {            
                List filteredMecs = EntityUtil.filterByDate(mecs);
                GenericValue mecPurpose = EntityUtil.getFirst(filteredMecs);
                if (mecPurpose != null)
                    contactMech = mecPurpose.getRelatedOne("ContactMech");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting current ContactMech for Party/Purpose", module);
        }
        
        if (contactMech != null) {
            if (contactMech.getString("contactMechTypeId").equals("POSTAL_ADDRESS")) {
                try {
                    postalAddress = contactMech.getRelatedOne("PostalAddress");                               
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Trouble getting PostalAddress from ContactMech", module);
                }
            }
        }
        
        if (postalAddress != null)
            return postalAddress;
        return null;        
    }
    
    
}
