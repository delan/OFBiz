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
package org.ofbiz.order.shoppingcart;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * Shopping Cart Services
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */
public class ShoppingCartServices {

    public static final String module = ShoppingCartServices.class.getName();
    public static final String resource = "OrderUiLabels";

    public static Map assignItemShipGroup(DispatchContext dctx, Map context) {
        ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
        Integer fromGroupIndex = (Integer) context.get("fromGroupIndex");
        Integer toGroupIndex = (Integer) context.get("toGroupIndex");
        Integer itemIndex = (Integer) context.get("itemIndex");
        Double quantity = (Double) context.get("quantity");

        Debug.log("From Group - " + fromGroupIndex + " To Group - " + toGroupIndex + "Item - " + itemIndex + "(" + quantity + ")", module);
        if (fromGroupIndex.equals(toGroupIndex)) {
            // nothing to do
            return ServiceUtil.returnSuccess();
        }

        cart.positionItemToGroup(itemIndex.intValue(), quantity.doubleValue(),
                fromGroupIndex.intValue(), toGroupIndex.intValue());
        Debug.log("Called cart.positionItemToGroup()", module);
        
        return ServiceUtil.returnSuccess();
    }

    public static Map setShippingOptions(DispatchContext dctx, Map context) {
        ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
        Integer groupIndex = (Integer) context.get("groupIndex");
        String shippingContactMechId = (String) context.get("shippingContactMechId");
        String shipmentMethodString = (String) context.get("shipmentMethodString");
        String shippingInstructions = (String) context.get("shippingInstructions");
        String giftMessage = (String) context.get("giftMessage");
        Boolean maySplit = (Boolean) context.get("maySplit");
        Boolean isGift = (Boolean) context.get("isGift");

        ShoppingCart.CartShipInfo csi = cart.getShipInfo(groupIndex.intValue());
        if (csi != null) {
            int idx = groupIndex.intValue();

            if (UtilValidate.isNotEmpty(shipmentMethodString)) {
                int delimiterPos = shipmentMethodString.indexOf('@');
                String shipmentMethodTypeId = null;
                String carrierPartyId = null;

                if (delimiterPos > 0) {
                    shipmentMethodTypeId = shipmentMethodString.substring(0, delimiterPos);
                    carrierPartyId = shipmentMethodString.substring(delimiterPos + 1);
                 }

                cart.setShipmentMethodTypeId(idx, shipmentMethodTypeId);
                cart.setCarrierPartyId(idx, carrierPartyId);
            }

            cart.setShippingInstructions(idx, shippingInstructions);
            cart.setShippingContactMechId(idx, shippingContactMechId);
            cart.setGiftMessage(idx, giftMessage);

            if (maySplit != null) {
                cart.setMaySplit(idx, maySplit);
            }
            if (isGift != null) {
                cart.setIsGift(idx, isGift);
            }
        } else {
            return ServiceUtil.returnError("Cart ship group not found [" + groupIndex + "]");
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map setPaymentOptions(DispatchContext dctx, Map context) {
        return ServiceUtil.returnError("Service not yet implemented");
    }

    public static Map setOtherOptions(DispatchContext dctx, Map context) {
        ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
        String orderAdditionalEmails = (String) context.get("orderAdditionalEmails");
        String correspondingPoId = (String) context.get("correspondingPoId");

        cart.setOrderAdditionalEmails(orderAdditionalEmails);
        if (UtilValidate.isNotEmpty(correspondingPoId)) {
            cart.setPoNumber(correspondingPoId);
        } else {
            cart.setPoNumber("(none)");
        }

        return ServiceUtil.returnSuccess();
    }
}
