/*
 * $Id$
 *
 * Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.base.util.UtilProperties;

/** Bills of Materials' services implementation.
 * These services are useful when dealing with product's
 * bills of materials.
 * @author <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 */
public class BOMServices {

    public static final String module = BOMServices.class.getName();
    public static final String resource = "ManufacturingUiLabels";
    
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
        Boolean alsoVariants = (Boolean) context.get("alsoVariants");
        if (alsoVariants == null) {
            alsoVariants = new Boolean(true);
        }

        Integer llc = null;
        try {
            GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
            Map depthResult = dispatcher.runSync("getMaxDepth", UtilMisc.toMap("productId", productId, "bomType", "MANUF_COMPONENT"));
            llc = (Integer)depthResult.get("depth");
            // If the product is a variant of a virtual, then the billOfMaterialLevel cannot be 
            // lower than the billOfMaterialLevel of the virtual product.
            List virtualProducts = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productIdTo", productId, "productAssocTypeId", "PRODUCT_VARIANT"));
            if (virtualProducts != null) {
                int virtualMaxDepth = 0;
                Iterator virtualProductsIt = virtualProducts.iterator();
                while (virtualProductsIt.hasNext()) {
                    int virtualDepth = 0;
                    GenericValue oneVirtualProductAssoc = (GenericValue)virtualProductsIt.next();
                    GenericValue virtualProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", oneVirtualProductAssoc.getString("productId")));
                    if (virtualProduct.get("billOfMaterialLevel") != null) {
                        virtualDepth = virtualProduct.getLong("billOfMaterialLevel").intValue();
                        if (virtualDepth > virtualMaxDepth) {
                            virtualMaxDepth = virtualDepth;
                        }
                    }
                }
                if (virtualMaxDepth > llc.intValue()) {
                    llc = new Integer(virtualMaxDepth);
                }
            }
            product.set("billOfMaterialLevel", llc);
            product.store();
            if (alsoComponents.booleanValue()) {
                Map treeResult = dispatcher.runSync("getBOMTree", UtilMisc.toMap("productId", productId, "bomType", "MANUF_COMPONENT"));
                BOMTree tree = (BOMTree)treeResult.get("tree");
                ArrayList products = new ArrayList();
                tree.print(products, llc.intValue());
                for (int i = 0; i < products.size(); i++) {
                    BOMNode oneNode = (BOMNode)products.get(i);
                    GenericValue oneProduct = oneNode.getProduct();
                    if (oneProduct.getLong("billOfMaterialLevel").intValue() < oneNode.getDepth()) {
                        oneProduct.set("billOfMaterialLevel", new Integer(oneNode.getDepth()));
                        oneProduct.store();
                    }
                }
            }
            if (alsoVariants.booleanValue()) {
                List variantProducts = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"));
                if (variantProducts != null) {
                    Iterator variantProductsIt = variantProducts.iterator();
                    while (variantProductsIt.hasNext()) {
                        GenericValue oneVariantProductAssoc = (GenericValue)variantProductsIt.next();
                        GenericValue variantProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", oneVariantProductAssoc.getString("productId")));
                        variantProduct.set("billOfMaterialLevel", llc);
                        variantProduct.store();
                    }
                }
            }
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
            List products = delegator.findAll("Product", UtilMisc.toList("isVirtual DESC"));
            Iterator productsIt = products.iterator();
            Integer zero = new Integer(0);
            List allProducts = new ArrayList();
            while (productsIt.hasNext()) {
                GenericValue product = (GenericValue)productsIt.next();
                product.set("billOfMaterialLevel", zero);
                allProducts.add(product);
            }
            delegator.storeAll(allProducts);
                        
            productsIt = products.iterator();
            List productLLCs = new ArrayList();
            while (productsIt.hasNext()) {
                GenericValue product = (GenericValue)productsIt.next();
                try {
                    Map depthResult = dispatcher.runSync("updateLowLevelCode", UtilMisc.toMap("productId", product.getString("productId"), "alsoComponents", Boolean.valueOf(false), "alsoVariants", Boolean.valueOf(false)));
                    productLLCs.add(product.getString("productId") + " - " + depthResult.get("lowLevelCode"));
                } catch(Exception exc) {
                }
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
     * if necessary configures it, and it returns
     * an object (see {@link BOMTree}
     * and {@link BOMNode}) that represents a
     * configured bill of material tree.
     * Useful for tree traversal (breakdown, explosion, implosion).
     * @param dctx
     * @param context
     * @return
     */    
    public static Map getBOMTree(DispatchContext dctx, Map context) {

        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String productId = (String) context.get("productId");
        String fromDateStr = (String) context.get("fromDate");
        String bomType = (String) context.get("bomType");
        Integer type = (Integer) context.get("type");
        if (type == null) {
            type = new Integer(0);
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
        
        BOMTree tree = null;
        try {
            tree = new BOMTree(productId, bomType, fromDate, type.intValue(), delegator, dispatcher);
        } catch(GenericEntityException gee) {
            return ServiceUtil.returnError("Error creating bill of materials tree: " + gee.getMessage());
        }

        result.put("tree", tree);

        return result;
    }

    /** It reads the product's bill of materials,
     * if necessary configures it, and it returns its (possibly configured) components in
     * a List of {@link BOMNode}).
     * @param dctx
     * @param context
     * @return
     */    
    public static Map getManufacturingComponents(DispatchContext dctx, Map context) {

        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        String productId = (String) context.get("productId");
        Double quantity = (Double) context.get("quantity");
        String fromDateStr = (String) context.get("fromDate");
        Boolean excludeWIPs = (Boolean) context.get("excludeWIPs");
        
        if (quantity == null) {
            quantity = new Double(1);
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
        if (excludeWIPs == null) {
            excludeWIPs = new Boolean(true);
        }
        
        //
        // Components
        //
        BOMTree tree = null;
        ArrayList components = new ArrayList();
        try {
            tree = new BOMTree(productId, "MANUF_COMPONENT", fromDate, BOMTree.EXPLOSION_SINGLE_LEVEL, delegator, dispatcher);
            tree.setRootQuantity(quantity.doubleValue());
            tree.print(components, excludeWIPs.booleanValue());
            if (components.size() > 0) components.remove(0);
        } catch(GenericEntityException gee) {
            return ServiceUtil.returnError("Error creating bill of materials tree: " + gee.getMessage());
        }
        //
        // Product routing
        //
        String workEffortId = null;
        try {
            List workEffortProducts = delegator.findByAnd("WorkEffortGoodStandard", UtilMisc.toMap("productId", productId, "statusId", "ROU_PROD_TEMPLATE"));
            workEffortProducts = EntityUtil.filterByDate(workEffortProducts);
            if (UtilValidate.isEmpty(workEffortProducts)) {
                workEffortProducts = delegator.findByAnd("WorkEffortGoodStandard", UtilMisc.toMap("productId", tree.getRoot().getProduct().getString("productId"), "statusId", "ROU_PROD_TEMPLATE"));
                workEffortProducts = EntityUtil.filterByDate(workEffortProducts);
            }
            GenericValue workEffortProduct = EntityUtil.getFirst(workEffortProducts);
            if (workEffortProduct != null) {
                workEffortId = workEffortProduct.getString("workEffortId");
            }
        } catch(GenericEntityException gee) {
            Debug.logWarning(gee.getMessage(), module);
        }

        if (workEffortId != null) {
            result.put("workEffortId", workEffortId);
        }
        result.put("components", components);

        return result;
    }

    public static Map getNotAssembledComponents(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String productId = (String) context.get("productId");
        Double quantity = (Double) context.get("quantity");
        String fromDateStr = (String) context.get("fromDate");
        GenericValue userLogin = (GenericValue)context.get("userLogin");

        if (quantity == null) {
            quantity = new Double(1);
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
        
        BOMTree tree = null;
        ArrayList components = new ArrayList();
        ArrayList notAssembledComponents = new ArrayList();
        try {
            tree = new BOMTree(productId, "MANUF_COMPONENT", fromDate, BOMTree.EXPLOSION_MANUFACTURING, delegator, dispatcher);
            tree.setRootQuantity(quantity.doubleValue());
            tree.print(components);
        } catch(GenericEntityException gee) {
            return ServiceUtil.returnError("Error creating bill of materials tree: " + gee.getMessage());
        }
        Iterator componentsIt = components.iterator();
        while (componentsIt.hasNext()) {
            BOMNode oneComponent = (BOMNode)componentsIt.next();
            if (!oneComponent.isManufactured()) {
                notAssembledComponents.add(oneComponent);
            }
        }
        result.put("notAssembledComponents" , notAssembledComponents);
        return result;
    }
    
    // ---------------------------------------------
    // Service for the Product (Shipment) component
    //
    public static Map createShipmentPackages(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        String shipmentId = (String) context.get("shipmentId");

        try {
            List packages = delegator.findByAnd("ShipmentPackage", UtilMisc.toMap("shipmentId", shipmentId));
            if (!UtilValidate.isEmpty(packages)) {
                return ServiceUtil.returnError("Packages already found.");
            }
        } catch(GenericEntityException gee) {
            return ServiceUtil.returnError("Error loading the ShipmentPackages");
        }
        // ShipmentItems are loaded
        List shipmentItems = null;
        try {
            shipmentItems = delegator.findByAnd("ShipmentItem", UtilMisc.toMap("shipmentId", shipmentId));
        } catch(GenericEntityException gee) {
            return ServiceUtil.returnError("Error loading the ShipmentItems");
        }
        Iterator shipmentItemsIt = shipmentItems.iterator();
        HashMap orderReadHelpers = new HashMap();
        HashMap partyOrderShipments = new HashMap();
        while (shipmentItemsIt.hasNext()) {
            GenericValue shipmentItem = (GenericValue)shipmentItemsIt.next();
            // Get the OrderShipments
            List orderShipments = null;
            try {
                orderShipments = delegator.findByAnd("OrderShipment", UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", shipmentItem.getString("shipmentItemSeqId")));
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingPackageConfiguratorError", locale));
            }
            GenericValue orderShipment = org.ofbiz.entity.util.EntityUtil.getFirst(orderShipments);
            if (!orderReadHelpers.containsKey(orderShipment.getString("orderId"))) {
                orderReadHelpers.put(orderShipment.getString("orderId"), new OrderReadHelper(delegator, orderShipment.getString("orderId")));
            }
            OrderReadHelper orderReadHelper = (OrderReadHelper)orderReadHelpers.get(orderShipment.getString("orderId"));
            Map orderShipmentReadMap = UtilMisc.toMap("orderShipment", orderShipment, "orderReadHelper", orderReadHelper);
            String partyId = (orderReadHelper.getPlacingParty() != null? orderReadHelper.getPlacingParty().getString("partyId"): null); // FIXME: is it the customer?
            if (partyId != null) {
                if (!partyOrderShipments.containsKey(partyId)) {
                    ArrayList orderShipmentReadMapList = new ArrayList();
                    partyOrderShipments.put(partyId, orderShipmentReadMapList);
                }
                ArrayList orderShipmentReadMapList = (ArrayList)partyOrderShipments.get(partyId);
                orderShipmentReadMapList.add(orderShipmentReadMap);
            }
        }
        // For each party: try to expand the shipment item products 
        // (search for components that needs to be packaged).
        Iterator partyOrderShipmentsIt = partyOrderShipments.entrySet().iterator();
        while (partyOrderShipmentsIt.hasNext()) {
            Map.Entry partyOrderShipment = (Map.Entry)partyOrderShipmentsIt.next();
            String partyId = (String)partyOrderShipment.getKey();
            List orderShipmentReadMapList = (List)partyOrderShipment.getValue();
            for (int i = 0; i < orderShipmentReadMapList.size(); i++) {
                Map orderShipmentReadMap = (Map)orderShipmentReadMapList.get(i);
                GenericValue orderShipment = (GenericValue)orderShipmentReadMap.get("orderShipment");
                OrderReadHelper orderReadHelper = (OrderReadHelper)orderShipmentReadMap.get("orderReadHelper");
                GenericValue orderItem = orderReadHelper.getOrderItem(orderShipment.getString("orderItemSeqId"));
                // getProductsInPackages
                Map serviceContext = new HashMap();
                serviceContext.put("productId", orderItem.getString("productId"));
                serviceContext.put("quantity", orderShipment.getDouble("quantity"));
                Map resultService = null;
                try {
                    resultService = dispatcher.runSync("getProductsInPackages", serviceContext);
                } catch (GenericServiceException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingPackageConfiguratorError", locale));
                }
                List productsInPackages = (List)resultService.get("productsInPackages");
                if (productsInPackages.size() == 1) {
                    BOMNode root = (BOMNode)productsInPackages.get(0);
                    String rootProductId = (root.getSubstitutedNode() != null? root.getSubstitutedNode().getProduct().getString("productId"): root.getProduct().getString("productId"));
                    if (orderItem.getString("productId").equals(rootProductId)) {
                        productsInPackages = null;
                    }
                }
                if (productsInPackages != null && productsInPackages.size() == 0) {
                    productsInPackages = null;
                }
                if (productsInPackages != null && productsInPackages.size() > 0) {
                    orderShipmentReadMap.put("productsInPackages", productsInPackages);
                }
            }
        }
        // Group together products and components
        // of the same box type.
        HashMap boxTypes = new HashMap();
        partyOrderShipmentsIt = partyOrderShipments.entrySet().iterator();
        while (partyOrderShipmentsIt.hasNext()) {
            HashMap boxTypeContent = new HashMap();
            Map.Entry partyOrderShipment = (Map.Entry)partyOrderShipmentsIt.next();
            String partyId = (String)partyOrderShipment.getKey();
            List orderShipmentReadMapList = (List)partyOrderShipment.getValue();
            for (int i = 0; i < orderShipmentReadMapList.size(); i++) {
                Map orderShipmentReadMap = (Map)orderShipmentReadMapList.get(i);
                GenericValue orderShipment = (GenericValue)orderShipmentReadMap.get("orderShipment");
                OrderReadHelper orderReadHelper = (OrderReadHelper)orderShipmentReadMap.get("orderReadHelper");
                List productsInPackages = (List)orderShipmentReadMap.get("productsInPackages");
                if (productsInPackages != null) {
                    // there are subcomponents:
                    // this is a multi package shipment item
                    for (int j = 0; j < productsInPackages.size(); j++) {
                        BOMNode component = (BOMNode)productsInPackages.get(j);
                        HashMap boxTypeContentMap = new HashMap();
                        boxTypeContentMap.put("content", orderShipmentReadMap);
                        boxTypeContentMap.put("componentIndex", new Integer(j));
                        GenericValue product = component.getProduct();
                        String boxTypeId = product.getString("shipmentBoxTypeId");
                        if (boxTypeId != null) {
                            if (!boxTypes.containsKey(boxTypeId)) {
                                GenericValue boxType = null;
                                try {
                                    boxType = delegator.findByPrimaryKey("ShipmentBoxType", UtilMisc.toMap("shipmentBoxTypeId", boxTypeId));
                                } catch (GenericEntityException e) {
                                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingPackageConfiguratorError", locale));
                                }
                                boxTypes.put(boxTypeId, boxType);
                                boxTypeContent.put(boxTypeId, new ArrayList());
                            }
                            GenericValue boxType = (GenericValue)boxTypes.get(boxTypeId);
                            List boxTypeContentList = (List)boxTypeContent.get(boxTypeId);
                            boxTypeContentList.add(boxTypeContentMap);
                        }
                    }
                } else {
                    // no subcomponents, the product has its own package:
                    // this is a single package shipment item
                    HashMap boxTypeContentMap = new HashMap();
                    boxTypeContentMap.put("content", orderShipmentReadMap);
                    GenericValue orderItem = orderReadHelper.getOrderItem(orderShipment.getString("orderItemSeqId"));
                    GenericValue product = null;
                    try {
                        product = orderItem.getRelatedOne("Product");
                    } catch (GenericEntityException e) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingPackageConfiguratorError", locale));
                    }
                    String boxTypeId = product.getString("shipmentBoxTypeId");
                    if (boxTypeId != null) {
                        if (!boxTypes.containsKey(boxTypeId)) {
                            GenericValue boxType = null;
                            try {
                                boxType = delegator.findByPrimaryKey("ShipmentBoxType", UtilMisc.toMap("shipmentBoxTypeId", boxTypeId));
                            } catch (GenericEntityException e) {
                                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingPackageConfiguratorError", locale));
                            }

                            boxTypes.put(boxTypeId, boxType);
                            boxTypeContent.put(boxTypeId, new ArrayList());
                        }
                        GenericValue boxType = (GenericValue)boxTypes.get(boxTypeId);
                        List boxTypeContentList = (List)boxTypeContent.get(boxTypeId);
                        boxTypeContentList.add(boxTypeContentMap);
                    }
                }
            }
            // The packages and package contents are created.
            Iterator boxTypeContentIt = boxTypeContent.entrySet().iterator();
            while (boxTypeContentIt.hasNext()) {
                Map.Entry boxTypeContentEntry = (Map.Entry)boxTypeContentIt.next();
                String boxTypeId = (String)boxTypeContentEntry.getKey();
                List contentList = (List)boxTypeContentEntry.getValue();
                GenericValue boxType = (GenericValue)boxTypes.get(boxTypeId);
                Double boxWidth = boxType.getDouble("boxLength");
                double totalWidth = 0;
                double boxWidthDbl = 0;
                if (boxWidth != null) {
                    boxWidthDbl = boxWidth.doubleValue();
                }
                String shipmentPackageSeqId = null;
                for (int i = 0; i < contentList.size(); i++) {
                    Map contentMap = (Map)contentList.get(i);
                    Map content = (Map)contentMap.get("content");
                    OrderReadHelper orderReadHelper = (OrderReadHelper)content.get("orderReadHelper");
                    List productsInPackages = (List)content.get("productsInPackages");
                    GenericValue orderShipment = (GenericValue)content.get("orderShipment");

                    GenericValue product = null;
                    double quantity = 0;
                    boolean subProduct = contentMap.containsKey("componentIndex");
                    if (subProduct) {
                        // multi package
                        Integer index = (Integer)contentMap.get("componentIndex");
                        BOMNode component = (BOMNode)productsInPackages.get(index.intValue());
                        product = component.getProduct();
                        quantity = component.getQuantity();
                    } else {
                        // single package
                        GenericValue orderItem = orderReadHelper.getOrderItem(orderShipment.getString("orderItemSeqId"));
                        try {
                            product = orderItem.getRelatedOne("Product");
                        } catch (GenericEntityException e) {
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingPackageConfiguratorError", locale));
                        }
                        quantity = orderShipment.getDouble("quantity").doubleValue();
                    }

                    Double productDepth = product.getDouble("shippingDepth");
                    if (productDepth == null) {
                        productDepth = product.getDouble("productDepth");
                    }
                    double productDepthDbl = 1;
                    if (productDepth != null) {
                        productDepthDbl = productDepth.doubleValue();
                    }
                    
                    int firstMaxNumOfProducts = (int)((boxWidthDbl - totalWidth) / productDepthDbl);
                    if (firstMaxNumOfProducts == 0) firstMaxNumOfProducts = 1;
                    // 
                    int maxNumOfProducts = (int)(boxWidthDbl / productDepthDbl);
                    if (maxNumOfProducts == 0) maxNumOfProducts = 1;

                    double remQuantity = quantity;
                    boolean isFirst = true;
                    while (remQuantity > 0) {
                        int maxQuantity = 0;
                        if (isFirst) {
                            maxQuantity = firstMaxNumOfProducts;
                            isFirst = false;
                        } else {
                            maxQuantity = maxNumOfProducts;
                        }
                        double qty = (remQuantity < maxQuantity? remQuantity: maxQuantity);
                        // If needed, create the package
                        if (shipmentPackageSeqId == null) {
                            try {
                                Map resultService = dispatcher.runSync("createShipmentPackage", UtilMisc.toMap("shipmentId", orderShipment.getString("shipmentId"), "shipmentBoxTypeId", boxTypeId, "userLogin", userLogin));
                                shipmentPackageSeqId = (String)resultService.get("shipmentPackageSeqId");
                            } catch (GenericServiceException e) {
                                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingPackageConfiguratorError", locale));
                            }
                            totalWidth = 0;
                        }
                        try {
                            Map inputMap = null;
                            if (subProduct) {
                                inputMap = UtilMisc.toMap("shipmentId", orderShipment.getString("shipmentId"),
                                "shipmentPackageSeqId", shipmentPackageSeqId,
                                "shipmentItemSeqId", orderShipment.getString("shipmentItemSeqId"),
                                "subProductId", product.getString("productId"),
                                "userLogin", userLogin,
                                "subProductQuantity", new Double(qty));
                            } else {
                                inputMap = UtilMisc.toMap("shipmentId", orderShipment.getString("shipmentId"),
                                "shipmentPackageSeqId", shipmentPackageSeqId,
                                "shipmentItemSeqId", orderShipment.getString("shipmentItemSeqId"),
                                "userLogin", userLogin,
                                "quantity", new Double(qty));
                            }
                            Map resultService = dispatcher.runSync("createShipmentPackageContent", inputMap);
                        } catch (GenericServiceException e) {
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingPackageConfiguratorError", locale));
                        }
                        totalWidth += qty * productDepthDbl;
                        if (qty == maxQuantity) shipmentPackageSeqId = null;
                        remQuantity = remQuantity - qty;
                    }
                }
            }
        }
        return result;
    }

    /** It reads the product's bill of materials,
     * if necessary configures it, and it returns its (possibly configured) components in
     * a List of {@link BOMNode}).
     * @param dctx
     * @param context
     * @return
     */    
    public static Map getProductsInPackages(DispatchContext dctx, Map context) {

        Map result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        String productId = (String) context.get("productId");
        Double quantity = (Double) context.get("quantity");
        String fromDateStr = (String) context.get("fromDate");
        
        if (quantity == null) {
            quantity = new Double(1);
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
       
        //
        // Components
        //
        BOMTree tree = null;
        ArrayList components = new ArrayList();
        try {
            tree = new BOMTree(productId, "MANUF_COMPONENT", fromDate, BOMTree.EXPLOSION_MANUFACTURING, delegator, dispatcher);
            tree.setRootQuantity(quantity.doubleValue());
            tree.getProductsInPackages(components);
        } catch(GenericEntityException gee) {
            return ServiceUtil.returnError("Error creating bill of materials tree: " + gee.getMessage());
        }
        
        result.put("productsInPackages", components);

        return result;
    }

}
