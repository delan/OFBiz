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
package org.ofbiz.commonapp.accounting.invoice;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * InvoiceWorker - Worker methods of invoices
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.1
 */
public class InvoiceWorker {
    
    public static String module = InvoiceWorker.class.getName();
    
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
                invoiceTotal = amount.doubleValue() * quantity.doubleValue();
            }
        }
        return invoiceTotal;        
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
            sendFromRoles = invoice.getRelated("InvoiceRole", UtilMisc.toMap("roleTypeId", "SEND_FROM_COMPANY"), 
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
        return getAddressFromParty(getBillToParty(invoice), "BILLING_LOCATION");        
    }
    
    /**
      * Method to obtain the sending address for an invoice
      * @param invoice GenericValue object of the Invoice
      * @return GenericValue object of the PostalAddress
      */        
    public static GenericValue getSendFromAddress(GenericValue invoice) {
        return getAddressFromParty(getSendFromParty(invoice), "PAYRECEIVE_LOCATION");
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
