/*
 * $Id: BOMEvents.java,v 1.3 2004/02/16 11:20:04 jacopo Exp $
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
package org.ofbiz.manufacturing.bom;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.security.Security;

/**
 * Product Information Related Events
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.3 $
 * @since      2.0
 */
public class BOMEvents {
    
    public static final String module = BOMEvents.class.getName();

    /** Updates ProductAssoc information according to UPDATE_MODE parameter
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateProductBom(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");

        String updateMode = request.getParameter("UPDATE_MODE");

        if (updateMode == null || updateMode.length() <= 0) {
            request.setAttribute("_ERROR_MESSAGE_", "Update Mode was not specified, but is required.");
            Debug.logWarning("[BOMEvents.updateProductBom] Update Mode was not specified, but is required", module);
            return "error";
        }

        // check permissions before moving on...
        if (!security.hasEntityPermission("MANUFACTURING", "_" + updateMode, request.getSession())) {
            request.setAttribute("_ERROR_MESSAGE_", "You do not have sufficient permissions to " + updateMode + " MANUFACTURING (MANUFACTURING_" + updateMode + " or MANUFACTURING__ADMIN needed).");
            return "error";
        }

        String productId = request.getParameter("PRODUCT_ID");
        String productIdTo = request.getParameter("PRODUCT_ID_TO");
        String productAssocTypeId = request.getParameter("PRODUCT_ASSOC_TYPE_ID");
        String fromDateStr = request.getParameter("FROM_DATE");
        Timestamp fromDate = null;

        try {
            if (delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId)) == null)
                errMsg += "<li>Product with id " + productId + " not found.";
            if (delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productIdTo)) == null)
                errMsg += "<li>Product To with id " + productIdTo + " not found.";
        } catch (GenericEntityException e) {
            // if there is an exception for either, the other probably wont work
            Debug.logWarning(e, module);
        }

        if (UtilValidate.isNotEmpty(fromDateStr)) {
            try {
                fromDate = Timestamp.valueOf(fromDateStr);
            } catch (Exception e) {
                errMsg += "<li>From Date not formatted correctly.";
            }
        }
        if (!UtilValidate.isNotEmpty(productId))
            errMsg += "<li>Product ID is missing.";
        if (!UtilValidate.isNotEmpty(productIdTo))
            errMsg += "<li>Product ID To is missing.";
        if (!UtilValidate.isNotEmpty(productAssocTypeId))
            errMsg += "<li>Association Type ID is missing.";
        // from date is only required if update mode is not CREATE
        if (!updateMode.equals("CREATE") && !UtilValidate.isNotEmpty(fromDateStr))
            errMsg += "<li>From Date is missing.";
        // Will the new node create loops in the bill of materials tree?
        try {
            // FIXME: fromDate should be provided instead of null
            GenericValue dupAncestor = BOMHelper.searchDuplicatedAncestor(productId, productIdTo, productAssocTypeId, null, delegator);
            if (dupAncestor != null) {
                errMsg += "<li>The link could cause conflicts because of the following link: " + dupAncestor.getString("productId") + " --> " + dupAncestor.getString("productIdTo");
            }
        } catch (GenericEntityException e) {
            // if there is an exception for either, the other probably wont work
            Debug.logWarning(e, module);
        }
        
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occurred:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // clear some cache entries
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productId", productId));
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", productAssocTypeId));

        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productIdTo", productIdTo));
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId));

        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productAssocTypeId", productAssocTypeId));
        delegator.clearCacheLine("ProductAssoc",
            UtilMisc.toMap("productId", productId, "productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId, "fromDate", fromDate));

        GenericValue tempProductAssoc = delegator.makeValue("ProductAssoc",
                UtilMisc.toMap("productId", productId, "productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId, "fromDate", fromDate));

        if (updateMode.equals("DELETE")) {
            GenericValue productAssoc = null;

            try {
                productAssoc = delegator.findByPrimaryKey(tempProductAssoc.getPrimaryKey());
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                productAssoc = null;
            }
            if (productAssoc == null) {
                request.setAttribute("_ERROR_MESSAGE_", "Could not remove product association (does not exist)");
                return "error";
            }
            try {
                productAssoc.remove();
            } catch (GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_", "Could not remove product association (write error)");
                Debug.logWarning("[BOMEvents.updateProductBom] Could not remove product association (write error); message: " + e.getMessage(), module);
                return "error";
            }
            return "success";
        }

        String thruDateStr = request.getParameter("THRU_DATE");
        String reason = request.getParameter("REASON");
        String instruction = request.getParameter("INSTRUCTION");
        String quantityStr = request.getParameter("QUANTITY");
        String scrapFactorStr = request.getParameter("SCRAP_FACTOR");
        String routingWorkEffortId = request.getParameter("WORK_EFFORT_ID");
        String sequenceNumStr = request.getParameter("SEQUENCE_NUM");
        Timestamp thruDate = null;
        Double quantity = null;
        Double scrapFactor = null;
        Long sequenceNum = null;

        if (UtilValidate.isNotEmpty(thruDateStr)) {
            try {
                thruDate = Timestamp.valueOf(thruDateStr);
            } catch (Exception e) {
                errMsg += "<li>Thru Date not formatted correctly.";
            }
        }
        if (UtilValidate.isNotEmpty(quantityStr)) {
            try {
                quantity = Double.valueOf(quantityStr);
            } catch (Exception e) {
                errMsg += "<li>Quantity not formatted correctly.";
            }
        }
        if (UtilValidate.isNotEmpty(scrapFactorStr)) {
            try {
                scrapFactor = Double.valueOf(scrapFactorStr);
            } catch (Exception e) {
                errMsg += "<li>Scrap Factor not formatted correctly.";
            }
        }
        if (UtilValidate.isNotEmpty(sequenceNumStr)) {
            try {
                sequenceNum = Long.valueOf(sequenceNumStr);
            } catch (Exception e) {
                errMsg += "<li>SequenceNum not formatted correctly.";
            }
        }
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occurred:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        tempProductAssoc.set("thruDate", thruDate);
        tempProductAssoc.set("reason", reason);
        tempProductAssoc.set("instruction", instruction);
        tempProductAssoc.set("quantity", quantity);
        tempProductAssoc.set("sequenceNum", sequenceNum);
        tempProductAssoc.set("scrapFactor", scrapFactor);
//        tempProductAssoc.set("routingWorkEffortId", routingWorkEffortId);

        if (updateMode.equals("CREATE")) {
            // if no from date specified, set to now
            if (fromDate == null) {
                fromDate = new Timestamp(new java.util.Date().getTime());
                tempProductAssoc.set("fromDate", fromDate);
                request.setAttribute("ProductAssocCreateFromDate", fromDate);
            }

            GenericValue productAssoc = null;

            try {
                productAssoc = delegator.findByPrimaryKey(tempProductAssoc.getPrimaryKey());
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                productAssoc = null;
            }
            if (productAssoc != null) {
                request.setAttribute("_ERROR_MESSAGE_", "Could not create product association (already exists)");
                return "error";
            }
            try {
                productAssoc = tempProductAssoc.create();
            } catch (GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_", "Could not create product association (write error)");
                Debug.logWarning("[BOMEvents.updateProductBom] Could not create product association (write error); message: " + e.getMessage(), module);
                return "error";
            }
        } else if (updateMode.equals("UPDATE")) {
            try {
                tempProductAssoc.store();
            } catch (GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_", "Could not update product association (write error)");
                Debug.logWarning("[BOMEvents.updateProductBom] Could not update product association (write error); message: " + e.getMessage(), module);
                return "error";
            }
        } else {
            request.setAttribute("_ERROR_MESSAGE_", "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }

        return "success";
    }
}
