/*
 * $Id: ProductConfigWorker.java 3204 2004-08-30 11:13:25Z jonesde $
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
package org.ofbiz.product.config;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.service.LocalDispatcher;

/**
 * Product Config Worker class to reduce code in templates.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 *
 */
public class ProductConfigWorker {
    
    public static final String module = ProductConfigWorker.class.getName();
    public static final String resource = "ProductUiLabels";

    public static ProductConfigWrapper getProductConfigWrapper(String productId, String currencyUomId, HttpServletRequest request) {
        ProductConfigWrapper configWrapper = null;
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        String webSiteId = CatalogWorker.getWebSiteId(request);
        GenericValue autoUserLogin = (GenericValue)request.getSession().getAttribute("autoUserLogin");
        try {
            configWrapper = new ProductConfigWrapper((GenericDelegator)request.getAttribute("delegator"),
                                                     (LocalDispatcher)request.getAttribute("dispatcher"),
                                                     productId, catalogId, webSiteId,
                                                     currencyUomId, UtilHttp.getLocale(request),
                                                     autoUserLogin);
        } catch(ProductConfigWrapperException we) {
            configWrapper = null;
        } catch(Exception e) {
            Debug.logWarning(e.getMessage(), module);
        }
        return configWrapper;
    }
    
    public static void fillProductConfigWrapper(ProductConfigWrapper configWrapper, HttpServletRequest request) {
        int numOfQuestions = configWrapper.getQuestions().size();
        for (int k = 0; k < numOfQuestions; k++) {
            String[] opts = request.getParameterValues("" + k);
            if (opts == null) {
                continue;
            }
            for (int h = 0; h < opts.length; h++) {
                int cnt = -1;
                try {
                    cnt = Integer.parseInt(opts[h]);
                    configWrapper.setSelected(k, cnt);
                } catch(Exception e) {
                    Debug.logWarning(e.getMessage(), module);
                }
            }
        }
    }
}

