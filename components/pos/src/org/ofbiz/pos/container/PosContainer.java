/*
 * $Id: PosContainer.java,v 1.2 2004/07/28 23:03:55 ajzeneski Exp $
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
package org.ofbiz.pos.container;

import java.util.Locale;

import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.xui.XuiContainer;
import org.ofbiz.content.xui.XuiSession;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.product.store.ProductStoreWorker;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class PosContainer extends XuiContainer {

    public String getContainerConfigName() {
        return "pos-container";
    }

    public void configure(ContainerConfig.Container cc) throws ContainerException {
        XuiSession session = this.getSession();
        GenericValue productStore = null;
        GenericValue facility = null;

        // get the facility id
        String facilityId = ContainerConfig.getPropertyValue(cc, "facility-id", null);
        if (UtilValidate.isEmpty(facilityId)) {
            throw new ContainerException("No facility-id value set in pos-container!");
        } else {
            try {
                facility = session.getDelegator().findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", facilityId));
            } catch (GenericEntityException e) {
                throw new ContainerException("Invalid facilityId : " + facilityId);
            }
        }

        // verify the facility exists
        if (facility == null) {
            throw new ContainerException("Invalid facility; facility ID not found [" + facilityId + "]");
        }
        session.setAttribute("facility", facility);

        // get the product store id
        String productStoreId = facility.getString("productStoreId");
        if (UtilValidate.isEmpty(productStoreId)) {
            throw new ContainerException("No productStoreId set on facility [" + facilityId + "]!");
        } else {
            productStore = ProductStoreWorker.getProductStore(productStoreId, session.getDelegator());
            if (productStore == null) {
                throw new ContainerException("Invalid productStoreId : " + productStoreId);
            }
        }
        session.setAttribute("productStoreId", productStoreId);

        // get the store locale
        String localeStr = ContainerConfig.getPropertyValue(cc, "locale", null);
        if (UtilValidate.isEmpty(localeStr)) {
            localeStr = productStore.getString("defaultLocaleString");
        }
        if (UtilValidate.isEmpty(localeStr)) {
            throw new ContainerException("Invalid Locale for POS!");
        }
        Locale locale = UtilMisc.parseLocale(localeStr);
        session.setAttribute("locale", locale);

        // get the store currency
        String currencyStr = ContainerConfig.getPropertyValue(cc, "currency", null);
        if (UtilValidate.isEmpty(currencyStr)) {
            currencyStr = productStore.getString("defaultCurrencyUomId");
        }
        if (UtilValidate.isEmpty(currencyStr)) {
            throw new ContainerException("Invalid Currency for POS!");
        }
        session.setAttribute("currency", currencyStr);
    }
}
