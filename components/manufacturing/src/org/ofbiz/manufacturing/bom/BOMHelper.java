/*
 * BOMHelper.java
 *
 * Created on 3 novembre 2003, 16.55
 */

package org.ofbiz.manufacturing.bom;

import java.util.Date;
import java.util.List;
import java.util.Iterator;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;

/**
 *
 * @author  Jacopo Cappellato
 */
public class BOMHelper {
    
    /** Creates a new instance of BOMHelper */
    public BOMHelper() {
    }
    
    /* 
     *  Returns the maximum depth in which the productId can be found in the 
     *  bill of materials of bomType type.
     */
    public static int getMaxDepth(String productId, String bomType, Date fromDate, GenericDelegator delegator) throws GenericEntityException {
        int maxDepth = 0;
        List productNodesList = delegator.findByAndCache("ProductAssoc", 
                                         UtilMisc.toMap("productIdTo", productId,
                                         //"fromDate", fromDate,
                                         "productAssocTypeId", bomType));
        GenericValue oneNode = null;
        Iterator nodesIterator = productNodesList.iterator();
        int depth = 0;
        while (nodesIterator.hasNext()) {
            oneNode = (GenericValue)nodesIterator.next();
            depth = 0;
            depth = getMaxDepth(oneNode.getString("productId"), bomType, fromDate, delegator);
            depth++;
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }
        
        return maxDepth;
    }

    /* 
     *  Returns the ProductAssoc generic value for a duplicate productIdKey ancestor if present, null otherwise
     *  Useful to avoid loops when adding new assocs to a bill of materials.
     */
    public static GenericValue searchDuplicatedAncestor(String productId, String productIdKey, String bomType, Date fromDate, GenericDelegator delegator) throws GenericEntityException {
        List productNodesList = delegator.findByAndCache("ProductAssoc", 
                                         UtilMisc.toMap("productIdTo", productId,
                                         //"fromDate", fromDate,
                                         "productAssocTypeId", bomType));
        GenericValue oneNode = null;
        GenericValue duplicatedNode = null;
        Iterator nodesIterator = productNodesList.iterator();
        while (nodesIterator.hasNext()) {
            oneNode = (GenericValue)nodesIterator.next();
            if (oneNode.getString("productId").equals(productIdKey)) {
                return oneNode;
            }
            duplicatedNode = searchDuplicatedAncestor(oneNode.getString("productId"), productIdKey, bomType, fromDate, delegator);
            if (duplicatedNode != null) {
                break;
            }
        }
        return duplicatedNode;
    }

}
