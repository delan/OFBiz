/*
 * $Id: BOMServices.java,v 1.6 2004/05/11 10:51:15 jacopo Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.manufacturing.bom;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


/** Bills of Materials' services implementation.
 * These services are useful when dealing with product's
 * bills of materials.
 * @author <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 */
public class BOMServices {

    public static final String module = BOMServices.class.getName();
    
    /** Returns the product's low level code (llc) i.e. the maximum depth
     * in which the productId can be found in any of the
     * bills of materials of bomType type.
     * If the bomType input field is not passed then the depth is searched for all the bom types and the lowest depth is returned.
     * @param dctx
     * @param context
     * @return
     */    
    public static Map getMaxDepth(DispatchContext dctx, Map context) {

        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String productId = (String) context.get("productId");
        String fromDateStr = (String) context.get("fromDate");
        String bomType = (String) context.get("bomType");
        
        Date fromDate = null;
        if (UtilValidate.isNotEmpty(fromDateStr)) {
            try {
                fromDate = Timestamp.valueOf(fromDateStr);
            } catch (Exception e) {
            }
        }
        if (fromDate == null) {
            fromDate = new Date();
        }
        List bomTypes = new ArrayList();
        if (bomType == null) {
            try {
                List bomTypesValues = delegator.findByAnd("ProductAssocType", UtilMisc.toMap("parentTypeId", "PRODUCT_COMPONENT"));
                Iterator bomTypesValuesIt = bomTypesValues.iterator();
                while (bomTypesValuesIt.hasNext()) {
                    bomTypes.add(((GenericValue)bomTypesValuesIt.next()).getString("productAssocTypeId"));
                }
            } catch(GenericEntityException gee) {
                return ServiceUtil.returnError("Error running max depth algorithm: " + gee.getMessage());
            }
        } else {
            bomTypes.add(bomType);
        }
        
        int depth = 0;
        int maxDepth = 0;
        Iterator bomTypesIt = bomTypes.iterator();
        try {
            while (bomTypesIt.hasNext()) {
                String oneBomType = (String)bomTypesIt.next();
                depth = BOMHelper.getMaxDepth(productId, oneBomType, fromDate, delegator);
                if (depth > maxDepth) {
                    maxDepth = depth;
                }
            }
        } catch(GenericEntityException gee) {
            return ServiceUtil.returnError("Error running max depth algorithm: " + gee.getMessage());
        }
        result.put("depth", new Integer(maxDepth));

        return result;
    }

    /** Updates the product's low level code (llc) 
     * Given a product id, computes and updates the product's low level code (field billOfMaterialLevel in Product entity).
     * It also updates the llc of all the product's descendants.
     * For the llc only the manufacturing bom ("MANUF_COMPONENT") is considered.
     * @param dctx
     * @param context
     * @return
     */    
    public static Map updateLowLevelCode(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String productId = (String) context.get("productId");
        Boolean alsoComponents = (Boolean) context.get("alsoComponents");
        if (alsoComponents == null) {
            alsoComponents = new Boolean(true);
        }

        Integer llc = null;
        try {
            GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
            Map depthResult = dispatcher.runSync("getMaxDepth", UtilMisc.toMap("productId", productId, "bomType", "MANUF_COMPONENT"));
            llc = (Integer)depthResult.get("depth");
            product.set("billOfMaterialLevel", llc);
            product.store();
            if (alsoComponents.booleanValue()) {
                Map treeResult = dispatcher.runSync("getItemConfigurationTree", UtilMisc.toMap("productId", productId, "bomType", "MANUF_COMPONENT"));
                ItemConfigurationTree tree = (ItemConfigurationTree)treeResult.get("tree");
                ArrayList products = new ArrayList();
                tree.print(products, llc.intValue());
                for (int i = 0; i < products.size(); i++) {
                    ItemConfigurationNode oneNode = (ItemConfigurationNode)products.get(i);
                    GenericValue oneProduct = oneNode.getPart();
                    if (oneProduct.getInteger("billOfMaterialLevel").intValue() < oneNode.getDepth()) {
                        oneProduct.set("billOfMaterialLevel", new Integer(oneNode.getDepth()));
                        oneProduct.store();
                    }
                }
            }
            // FIXME: also all the variants llc should be updated?
        } catch (Exception e) {
            return ServiceUtil.returnError("Error running updateLowLevelCode: " + e.getMessage());
        }
        result.put("lowLevelCode", llc);
        return result;
    }

    /** Updates the product's low level code (llc) for all the products in the Product entity.
     * For the llc only the manufacturing bom ("MANUF_COMPONENT") is considered.
     * @param dctx
     * @param context
     * @return
     */    
    public static Map initLowLevelCode(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();

        try {
            List products = delegator.findAll("Product");
            Iterator productsIt = products.iterator();
            Integer zero = new Integer(0);
            while (productsIt.hasNext()) {
                GenericValue product = (GenericValue)productsIt.next();
                product.set("billOfMaterialLevel", zero);
                product.store();
            }
            productsIt = products.iterator();
            List productLLCs = new ArrayList();
            while (productsIt.hasNext()) {
                GenericValue product = (GenericValue)productsIt.next();
                Map depthResult = dispatcher.runSync("updateLowLevelCode", UtilMisc.toMap("productId", product.getString("productId"), "alsoComponents", Boolean.valueOf(false)));
                //System.out.println("PRODUCT: " + product.getString("productId") + " - LLC: " + depthResult.get("lowLevelCode"));
                productLLCs.add(product.getString("productId") + " - " + depthResult.get("lowLevelCode"));
            }
            result.put("productLLCs", productLLCs);
            // FIXME: also all the variants llc should be updated?
        } catch (Exception e) {
            return ServiceUtil.returnError("Error running initLowLevelCode: " + e.getMessage());
        }
        return result;
    }

    /** Returns the ProductAssoc generic value for a duplicate productIdKey
     * ancestor if present, null otherwise.
     * Useful to avoid loops when adding new assocs (components)
     * to a bill of materials.
     * @param dctx
     * @param context
     * @return
     */    
    public static Map searchDuplicatedAncestor(DispatchContext dctx, Map context) {

        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String productId = (String) context.get("productId");
        String productIdKey = (String) context.get("productIdKey");
        String fromDateStr = (String) context.get("fromDate");
        String bomType = (String) context.get("bomType");
        
        Date fromDate = null;
        if (UtilValidate.isNotEmpty(fromDateStr)) {
            try {
                fromDate = Timestamp.valueOf(fromDateStr);
            } catch (Exception e) {
            }
        }
        if (fromDate == null) {
            fromDate = new Date();
        }
        
        GenericValue duplicatedProductAssoc = null;
        try {
            duplicatedProductAssoc = BOMHelper.searchDuplicatedAncestor(productId, productIdKey, bomType, fromDate, delegator, dispatcher);
        } catch(GenericEntityException gee) {
            return ServiceUtil.returnError("Error running duplicated ancestor search: " + gee.getMessage());
        }

        result.put("duplicatedProductAssoc", duplicatedProductAssoc);

        return result;
    }

    /** It reads the product's bill of materials,
     * if necessary configure it, and it returns
     * an object (see {@link ItemConfigurationTree}
     * and {@link ItemConfigurationNode}) that represents a
     * configured bill of material tree.
     * Useful for tree traversal (breakdown, explosion, implosion).
     * @param dctx
     * @param context
     * @return
     */    
    public static Map getItemConfigurationTree(DispatchContext dctx, Map context) {

        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String productId = (String) context.get("productId");
        String fromDateStr = (String) context.get("fromDate");
        String bomType = (String) context.get("bomType");
        Boolean explosion = (Boolean) context.get("explosion");
        if (explosion == null) {
            explosion = new Boolean(true);
        }
        
        Date fromDate = null;
        if (UtilValidate.isNotEmpty(fromDateStr)) {
            try {
                fromDate = Timestamp.valueOf(fromDateStr);
            } catch (Exception e) {
            }
        }
        if (fromDate == null) {
            fromDate = new Date();
        }
        
        ItemConfigurationTree tree = null;
        try {
            tree = new ItemConfigurationTree(productId, bomType, fromDate, explosion.booleanValue(), delegator, dispatcher);
        } catch(GenericEntityException gee) {
            return ServiceUtil.returnError("Error creating bill of materials tree: " + gee.getMessage());
        }

        result.put("tree", tree);

        return result;
    }

}


