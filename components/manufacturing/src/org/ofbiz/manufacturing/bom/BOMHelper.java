/*
 * BOMHelper.java
 *
 * Created on 3 novembre 2003, 16.55
 */

package org.ofbiz.manufacturing.bom;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.base.util.Debug;
import org.ofbiz.security.Security;

/** Helper class containing static method useful when dealing
 * with product's bills of materials.
 * These methods are also available as services (see {@link BOMServices}).
 * @author <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 */
public class BOMHelper {
    
    public static final String module = BOMHelper.class.getName();
    
    /** Creates a new instance of BOMHelper */
    public BOMHelper() {
    }
    
    /** Returns the product's low level code (llc) i.e. the maximum depth
     * in which the productId can be found in any of the
     * bills of materials of bomType type.
     * @return The low level code for the productId. (0 = root, 1 = first level, etc...)
     * @param productId The product id
     * @param bomType The bill of materials type (e.g. manufacturing, engineering,...)
     * @param delegator Validity date (if null, today is used).
     * @param inDate The delegator
     * @throws GenericEntityException If a db problem occurs.
     */
    /*
     * It's implemented as a recursive method that performs the following tasks:
     * 1.given a product id, it selects all the product's parents
     * 2.if no parents are found, the method returns zero (llc = 0, this is a root element)
     * 3.for every parent the method is called recursively, the llc returned is incremented by one and the max of these number is saved as maxDepth
     * 4.the maxDepth value is returned
     */
    public static int getMaxDepth(String productId, String bomType, Date inDate, GenericDelegator delegator) throws GenericEntityException {
        // If the date is null, set it to today.
        if (inDate == null) inDate = new Date();
        int maxDepth = 0;
        List productNodesList = delegator.findByAndCache("ProductAssoc", 
                                         UtilMisc.toMap("productIdTo", productId,
                                         "productAssocTypeId", bomType));
        productNodesList = EntityUtil.filterByDate(productNodesList, inDate);
        GenericValue oneNode = null;
        Iterator nodesIterator = productNodesList.iterator();
        int depth = 0;
        while (nodesIterator.hasNext()) {
            oneNode = (GenericValue)nodesIterator.next();
            depth = 0;
            depth = getMaxDepth(oneNode.getString("productId"), bomType, inDate, delegator);
            depth++;
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }
        
        return maxDepth;
    }

    /** Returns the ProductAssoc generic value for a duplicate productIdKey
     * ancestor if present, null otherwise.
     * Useful to avoid loops when adding new assocs (components)
     * to a bill of materials.
     * @param productId The product to which we want to add a new child.
     * @param productIdKey The new component we want to add to the existing bom.
     * @param bomType The bill of materials type (e.g. manufacturing, engineering).
     * @param inDate Validity date (if null, today is used).
     *
     * @param delegator The delegator used
     * @throws GenericEntityException If a db problem occurs
     * @return the ProductAssoc generic value for a duplicate productIdKey
     * ancestor if present, null otherwise.
     */    
    public static GenericValue searchDuplicatedAncestor(String productId, String productIdKey, String bomType, Date inDate, GenericDelegator delegator, LocalDispatcher dispatcher) throws GenericEntityException {
        return searchDuplicatedAncestor(productId, productIdKey, null, bomType, inDate, delegator, dispatcher);
    }
    
    private static GenericValue searchDuplicatedAncestor(String productId, String productIdKey, ArrayList productIdKeys, String bomType, Date inDate, GenericDelegator delegator, LocalDispatcher dispatcher) throws GenericEntityException {
        // If the date is null, set it to today.
        if (inDate == null) inDate = new Date();
        if (productIdKeys == null) {
            BOMTree tree = new BOMTree(productIdKey, bomType, inDate, delegator, dispatcher);
            productIdKeys = tree.getAllProductsId();
            productIdKeys.add(productIdKey);
        }
        List productNodesList = delegator.findByAndCache("ProductAssoc", 
                                         UtilMisc.toMap("productIdTo", productId,
                                         "productAssocTypeId", bomType));
        productNodesList = EntityUtil.filterByDate(productNodesList, inDate);
        GenericValue oneNode = null;
        GenericValue duplicatedNode = null;
        Iterator nodesIterator = productNodesList.iterator();
        while (nodesIterator.hasNext()) {
            oneNode = (GenericValue)nodesIterator.next();
            for (int i = 0; i < productIdKeys.size(); i++) {
                if (oneNode.getString("productId").equals((String)productIdKeys.get(i))) {
                    return oneNode;
                }
            }
            duplicatedNode = searchDuplicatedAncestor(oneNode.getString("productId"), productIdKey, productIdKeys, bomType, inDate, delegator, dispatcher);
            if (duplicatedNode != null) {
                break;
            }
        }
        return duplicatedNode;
    }

    public static String createProductionRunsForOrders(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");

        String shipmentId = request.getParameter("shipmentId");
        //String orderId = request.getParameter("orderId");
        //String orderItemSeqId = request.getParameter("orderItemSeqId");

        try {
        List shipmentPlans = delegator.findByAnd("ShipmentPlan", UtilMisc.toMap("shipmentId", shipmentId));
        Iterator shipmentPlansIt = shipmentPlans.iterator();
        while (shipmentPlansIt.hasNext()) {
            GenericValue shipmentPlan = (GenericValue)shipmentPlansIt.next();
            GenericValue orderItem = shipmentPlan.getRelatedOne("OrderItem");
    
            List productionRuns = delegator.findByAndCache("WorkOrderItemFulfillment", UtilMisc.toMap("orderId", shipmentPlan.getString("orderId"), "orderItemSeqId", shipmentPlan.getString("orderItemSeqId")));
            if (productionRuns != null && productionRuns.size() > 0) {
                Debug.logError("Production Run for order item (" + orderItem.getString("orderId") + "/" + orderItem.getString("orderItemSeqId") + ") not created.", module);
                continue;
            }
            Map result = dispatcher.runSync("createProductionRunsForOrder", UtilMisc.toMap("productId", orderItem.getString("productId"), "quantity", shipmentPlan.getDouble("quantity"), "orderId", shipmentPlan.getString("orderId"), "orderItemSeqId", shipmentPlan.getString("orderItemSeqId"), "userLogin", userLogin));
        }
        } catch (Exception e) {
            // if there is an exception for either, the other probably wont work
            Debug.logWarning(e, module);
        }

        return "success";
    }

}
