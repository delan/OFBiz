/*
 * $Id$
 * $Log$
 * Revision 1.1.1.1  2001/08/24 01:01:44  azeneski
 * Initial Import
 *
 */

package org.ofbiz.ecommerce.checkout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.core.util.UtilMisc;

/**
 * <p><b>Title:</b> CheckOutEvents.java
 * <p><b>Description:</b> Events used for processing checkout and orders.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on August 23, 2001, 7:58 PM
 */
public class CheckOutEvents {
    
    public static String setAddressInfo(HttpServletRequest request, HttpServletResponse response) {
        String[] notRequired = { "street2", "address_type","same_billing" };
        List notRequiredList = Arrays.asList(notRequired);
        HashMap address = new HashMap(UtilMisc.getParameterMap(request));
        boolean errorsFound = false;
        StringBuffer errorMessage = new StringBuffer();
        Set keySet = address.keySet();
        Iterator i = keySet.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            String name = (String) o;
            String value = (String) address.get(o);
            if ( value == null || value.length() < 2 ) {
                if ( !notRequiredList.contains(name) ) {
                    errorMessage.append(name.toUpperCase() + " does not contain a valid value.<br>");
                    errorsFound = true;
                }
            }
        }
        
        HttpSession session = request.getSession(true);
        String addressType = (String) address.get("address_type");
        String sameBilling = (String) address.get("same_billing");
        if ( addressType == null ) {
            errorMessage.append("There is a problem with this JSP form!<br>");
            errorsFound = true;
        }
        
        if ( errorsFound ) {
            request.setAttribute("ERROR_MESSAGE",errorMessage.toString());
            return "error";
        }
        else {            
            if ( sameBilling != null || addressType.equals("O") || addressType.equals("S") )            
                session.setAttribute("SHIPPING_LOCATION",address);
            if ( sameBilling != null || addressType.equals("O") || addressType.equals("B") )            
                session.setAttribute("BILLING_LOCATION",address);            
            return "success";
        }
    }
        
    public static String setPaymentInfo(HttpServletRequest request, HttpServletResponse response) {
        return "success";
    }
    
}