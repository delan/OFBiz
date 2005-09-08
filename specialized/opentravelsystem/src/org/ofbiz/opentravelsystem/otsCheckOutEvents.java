/*
 * $Id: CheckOutEvents.java 5462 2005-08-05 18:35:48Z jonesde $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.opentravelsystem;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.control.RequestHandler;
import org.ofbiz.webapp.control.RequestManager;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.*;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.event.EventHandlerException;
import org.ofbiz.party.contact.*;


/**
 * Events used for processing checkout specific for the Open travel system.
 *
 * @author <a href="mailto:h.bakker@antwebsystesm.com">Hans Bakker</a>
 * @version $Rev: 0 $
 */
public class otsCheckOutEvents {

    public static final String module = CheckOutEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";

    public static String removePreviousRequest(HttpServletRequest request, HttpServletResponse response) {
    	
    	String prevRequest = (String) request.getSession().getAttribute("_PREVIOUS_REQUEST_"); 
    	request.getSession().removeAttribute("_PREVIOUS_REQUEST_");
    	Debug.logInfo("Previous request: " + prevRequest + " removed.",module);
    	
    	prevRequest = (String) request.getSession().getAttribute("_POST_CHAIN_VIEW_"); 
    	request.getSession().removeAttribute("_POST_CHAIN_VIEW_");
    	Debug.logInfo("Previous _POST_CHAIN_VIEW_ removed: " + prevRequest + " removed.",module);
    	
       	return "success";
    }
    
    
    
    
    /**
     * Event to check out with as less information as possible. The user needs not to be logged in.
     * it still however use the all the existing services and processes and do not create any of its own,
     * except for this event Derived from org.ofbiz.order.shoppingcart.CheckOutEvents.finalizeOrderEntry
     * @param request
     * @param response
     * @return resultString: error or success
     */
    public static String onePageCheckout(HttpServletRequest request, HttpServletResponse response) {
    	HttpSession session = request.getSession();
  
        ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        
    	// remove auto-login fields
    	session.removeAttribute("autoUserLogin");
    	session.removeAttribute("autoName");
    	// clear out the login fields from the cart
    	try {
    		cart.setAutoUserLogin(null, dispatcher);
    	} catch (CartItemModifyException e) {
    		Debug.logError(e, module);
    	}

        
        
        return "success";
        
    }
}