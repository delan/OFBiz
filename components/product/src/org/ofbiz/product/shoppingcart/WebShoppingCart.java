/*
 * $Id: WebShoppingCart.java,v 1.1 2003/08/18 03:51:15 ajzeneski Exp $
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
package org.ofbiz.product.shoppingcart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;

/**
 * <p><b>Title:</b> WebShoppingCart.java
 * <p><b>Description:</b> This is a very basic
 * extension of the 
 * {@link org.ofbiz.commonapp.order.shoppingcart.ShoppingCart ShoppingCart}
 * class which provides web presentation layer specific functionality
 * related specifically to user session information. 
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:tristana@twibble.org">Tristan Austin</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public class WebShoppingCart extends ShoppingCart {
    private HttpSession session;

    /** Creates a new cloned ShoppingCart Object. */
    public WebShoppingCart(ShoppingCart cart, HttpSession session) {
        super(cart);
        this.session = session;
    }    

    /** Creates new empty ShoppingCart object. */
    public WebShoppingCart(HttpServletRequest request) {
        super((GenericDelegator)request.getAttribute("delegator"), ProductStoreWorker.getProductStoreId(request), CatalogWorker.getWebSiteId(request));
        this.session = request.getSession();
    }
    
    /** Gets the userLogin from the session; may be null */
    public GenericValue getUserLogin() {
        return (GenericValue) this.session.getAttribute("userLogin");
    }

    public GenericValue getAutoUserLogin() {
        return (GenericValue) this.session.getAttribute("autoUserLogin");
    }
    
    public String getWebSiteId() {
        return (String) session.getAttribute("webSiteId");
    }
    
    public String getPartyId() {
        String partyId = (String) session.getAttribute("orderPartyId");
        if (partyId == null && getUserLogin() != null)
            partyId = getUserLogin().getString("partyId");
        if (partyId == null && getAutoUserLogin() != null)
            partyId = getAutoUserLogin().getString("partyId");
        return partyId;
    }
}
