/*
 * BOMHelper.java
 *
 * Created on 3 novembre 2003, 16.55
 */

package org.ofbiz.manufacturing.bom;

import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.ofbiz.entity.util.EntityUtil;
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

    /* 
     *  Returns the ProductAssoc generic value for a duplicate productIdKey ancestor if present, null otherwise
     *  Useful to avoid loops when adding new assocs to a bill of materials.
     */
    public static GenericValue searchDuplicatedAncestor(String productId, String productIdKey, String bomType, Date inDate, GenericDelegator delegator) throws GenericEntityException {
        return searchDuplicatedAncestor(productId, productIdKey, null, bomType, inDate, delegator);
    }
    
    public static GenericValue searchDuplicatedAncestor(String productId, String productIdKey, ArrayList productIdKeys, String bomType, Date inDate, GenericDelegator delegator) throws GenericEntityException {
        // If the date is null, set it to today.
        if (inDate == null) inDate = new Date();
        if (productIdKeys == null) {
            ItemConfigurationTree tree = new ItemConfigurationTree(productIdKey, bomType, inDate, delegator);
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
            duplicatedNode = searchDuplicatedAncestor(oneNode.getString("productId"), productIdKey, productIdKeys, bomType, inDate, delegator);
            if (duplicatedNode != null) {
                break;
            }
        }
        return duplicatedNode;
    }

}
