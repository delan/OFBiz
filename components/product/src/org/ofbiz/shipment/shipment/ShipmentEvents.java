/*
 * $Id: ShipmentEvents.java,v 1.3 2004/04/10 13:55:27 ajzeneski Exp $
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
package org.ofbiz.shipment.shipment;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.ByteWrapper;

/**
 * ShippingEvents - Events used for processing shipping fees
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.3 $
 * @since      2.2
 */
public class ShipmentEvents {
    
    public static final String module = ShipmentEvents.class.getName();

    public static String viewShipmentPackageRouteSegLabelImage(HttpServletRequest request, HttpServletResponse response) {
        
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        
        String shipmentId = request.getParameter("shipmentId");
        String shipmentRouteSegmentId = request.getParameter("shipmentRouteSegmentId");
        String shipmentPackageSeqId = request.getParameter("shipmentPackageSeqId");
        
        GenericValue shipmentPackageRouteSeg = null;
        try {
            shipmentPackageRouteSeg = delegator.findByPrimaryKey("ShipmentPackageRouteSeg", UtilMisc.toMap("shipmentId", shipmentId, "shipmentRouteSegmentId", shipmentRouteSegmentId, "shipmentPackageSeqId", shipmentPackageSeqId));
        } catch (GenericEntityException e) {
            String errorMsg = "Error looking up ShipmentPackageRouteSeg: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }
        
        if (shipmentPackageRouteSeg == null) {
            request.setAttribute("_ERROR_MESSAGE_", "Could not find ShipmentPackageRouteSeg where shipmentId=[" + shipmentId + "], shipmentRouteSegmentId=[" + shipmentRouteSegmentId + "], shipmentPackageSeqId=[" + shipmentPackageSeqId + "]");
            return "error";
        }
        
        ByteWrapper byteWrapper = (ByteWrapper) shipmentPackageRouteSeg.get("labelImage");
        if (byteWrapper == null || byteWrapper.getBytes().length == 0) {
            request.setAttribute("_ERROR_MESSAGE_", "The ShipmentPackageRouteSeg was found where shipmentId=[" + shipmentId + "], shipmentRouteSegmentId=[" + shipmentRouteSegmentId + "], shipmentPackageSeqId=[" + shipmentPackageSeqId + "], but there was no labelImage on the value.");
            return "error";
        }
        
        try {
            UtilHttp.streamContentToBrowser(response, byteWrapper.getBytes(), "image/gif");
        } catch (IOException e) {
            String errorMsg = "Error writing labelImage to OutputStream: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }
        
        return "success";                                                
    }
}

