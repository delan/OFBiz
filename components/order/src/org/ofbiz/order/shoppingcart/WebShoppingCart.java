/*
 * $Id: WebShoppingCart.java,v 1.6 2004/05/22 20:25:49 ajzeneski Exp $
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
package org.ofbiz.order.shoppingcart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.base.util.UtilHttp;

/**
 * <p><b>Title:</b> WebShoppingCart.java
 * <p><b>Description:</b> This is a very basic
 * extension of the 
 * {@link org.ofbiz.order.shoppingcart.ShoppingCart ShoppingCart}
 * class which provides web presentation layer specific functionality
 * related specifically to user session information. 
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:tristana@twibble.org">Tristan Austin</a>
 * @version    $Revision: 1.6 $
 * @since      2.0
 */
public class WebShoppingCart extends ShoppingCart {

    /** Creates new empty ShoppingCart object. */
    public WebShoppingCart(HttpServletRequest request) {
        super((GenericDelegator)request.getAttribute("delegator"), ProductStoreWorker.getProductStoreId(request),
                CatalogWorker.getWebSiteId(request), UtilHttp.getCurrencyUom(request));
        this.locale = UtilHttp.getLocale(request);

        HttpSession session = request.getSession(true);
        if (session != null) {
            this.webSiteId = (String) session.getAttribute("webSiteId");
            this.userLogin = (GenericValue) session.getAttribute("userLogin");
            this.autoUserLogin = (GenericValue) session.getAttribute("autoUserLogin");

            if (session.getAttribute("orderPartyId") != null) {
                this.orderPartyId = (String) session.getAttribute("orderPartyId");
            }
        } else {
            throw new RuntimeException("Session was null and not created!");
        }
    }

    /** Creates a new cloned ShoppingCart Object. */
    public WebShoppingCart(ShoppingCart cart) {
        super(cart);
    }
}
