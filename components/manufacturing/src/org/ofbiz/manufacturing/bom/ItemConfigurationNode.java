/*
 * ProductNode.java
 *
 * Created on 1 ottobre 2003, 16.10
 */

package org.ofbiz.manufacturing.bom;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.sql.Timestamp;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.manufacturing.techdata.ProductHelper;

/** An ItemCoinfigurationNode represents a component in a bill of materials.
 * @author <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 */

public class ItemConfigurationNode {

    private ItemConfigurationNode parentNode; // the parent node (null if it's not present)
    private ItemConfigurationNode substitutedNode; // The virtual node (if any) that this instance substitutes
    private GenericValue ruleApplied; // The rule (if any) that that has been applied to configure the current node
    private String productForRules;
    private GenericValue product; // the current product (from Product entity)
    private GenericValue productAssoc; // the product assoc record (from ProductAssoc entity) in which the current product is in productIdTo
    private ArrayList children; // current node's children (ProductAssocs)
    private ArrayList childrenNodes; // current node's children nodes (ItemConfigurationNode)
    private double quantityMultiplier; // the necessary quantity as declared in the bom (from ProductAssocs or ProductManufacturingRule)
    private double scrapFactor; // the scrap factor as declared in the bom (from ProductAssocs)
    // Runtime fields
    private int depth; // the depth of this node in the current tree
    private double quantity; // the quantity of this node in the current tree
    private String bomTypeId; // the type of the current tree
   
    public ItemConfigurationNode(GenericValue product) {
        this.product = product;
        children = new ArrayList();
        childrenNodes = new ArrayList();
        parentNode = null;
        productForRules = null;
        bomTypeId = null;
        quantityMultiplier = 1;
        scrapFactor = 1;
        // Now we initialize the fields used in breakdowns
        depth = 0;
        quantity = 0;
    }

    public ItemConfigurationNode(String partId, GenericDelegator delegator) throws GenericEntityException {
        this(delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", partId)));
    }

    protected void loadChildren(String partBomTypeId, Date inDate, List productFeatures, int type, LocalDispatcher dispatcher) throws GenericEntityException {
        if (product == null) {
            throw new GenericEntityException("product is null");
        }
        // If the date is null, set it to today.
        if (inDate == null) inDate = new Date();
        bomTypeId = partBomTypeId;
        GenericDelegator delegator = product.getDelegator();
        List rows = delegator.findByAnd("ProductAssoc", 
                                            UtilMisc.toMap("productId", product.get("productId"), 
                                                       "productAssocTypeId", partBomTypeId),
                                            UtilMisc.toList("sequenceNum"));
        rows = EntityUtil.filterByDate(rows, inDate);
        if ((rows == null || rows.size() == 0) && substitutedNode != null) {
            // If no child is found and this is a substituted node
            // we try to search for substituted node's children.
            rows = delegator.findByAnd("ProductAssoc", 
                                        UtilMisc.toMap("productId", substitutedNode.getProduct().get("productId"), 
                                                       "productAssocTypeId", partBomTypeId),
                                        UtilMisc.toList("sequenceNum"));
            rows = EntityUtil.filterByDate(rows, inDate);
        }
        children = new ArrayList(rows);
        childrenNodes = new ArrayList();
        Iterator childrenIterator = children.iterator();
        GenericValue oneChild = null;
        ItemConfigurationNode oneChildNode = null;
        while(childrenIterator.hasNext()) {
            oneChild = (GenericValue)childrenIterator.next();
            // Configurator
            oneChildNode = configurator(oneChild, productFeatures, getRootNode().getProductForRules(), inDate, delegator, dispatcher);
            // If the node is null this means that the node has been discarded by the rules.
            if (oneChildNode != null) {
                oneChildNode.setParentNode(this);
                switch (type) {
                    case ItemConfigurationTree.EXPLOSION:
                        oneChildNode.loadChildren(partBomTypeId, inDate, productFeatures, ItemConfigurationTree.EXPLOSION, dispatcher);
                    break;
                    case ItemConfigurationTree.EXPLOSION_MANUFACTURING:
                        if (!oneChildNode.isPurchased()) {
                            oneChildNode.loadChildren(partBomTypeId, inDate, productFeatures, type, dispatcher);
                        }
                    break;
                }
            }
            childrenNodes.add(oneChildNode);
        }
    }

    private ItemConfigurationNode substituteNode(ItemConfigurationNode oneChildNode, List productFeatures, List productPartRules, GenericDelegator delegator) throws GenericEntityException {
        if (productPartRules != null) {
            GenericValue rule = null;
            for (int i = 0; i < productPartRules.size(); i++) {
                rule = (GenericValue)productPartRules.get(i);
                String ruleCondition = (String)rule.get("productFeature");
                String ruleOperator = (String)rule.get("ruleOperator");
                String newPart = (String)rule.get("productIdInSubst");
                double ruleQuantity = 0;
                try {
                    ruleQuantity = rule.getDouble("quantity").doubleValue();
                } catch(Exception exc) {
                    ruleQuantity = 0;
                }

                GenericValue feature = null;
                boolean ruleSatisfied = false;
                if (ruleCondition == null || ruleCondition.equals("")) {
                    ruleSatisfied = true;
                } else {
                    if (productFeatures != null) {
                        for (int j = 0; j < productFeatures.size(); j++) {
                            feature = (GenericValue)productFeatures.get(j);
                            if (ruleCondition.equals((String)feature.get("productFeatureId"))) {
                                ruleSatisfied = true;
                                break;
                            }
                        }
                    }
                }
                if (ruleSatisfied && ruleOperator.equals("OR")) {
                    ItemConfigurationNode tmpNode = oneChildNode;
                    if (newPart == null || newPart.equals("")) {
                        oneChildNode = null;
                    } else {
                        ItemConfigurationNode origNode = oneChildNode;
                        oneChildNode = new ItemConfigurationNode(newPart, delegator);
                        oneChildNode.setSubstitutedNode(tmpNode);
                        oneChildNode.setRuleApplied(rule);
                        oneChildNode.setProductAssoc(origNode.getProductAssoc());
                        oneChildNode.setScrapFactor(origNode.getScrapFactor());
                        if (ruleQuantity > 0) {
                            oneChildNode.setQuantityMultiplier(ruleQuantity);
                        } else {
                            oneChildNode.setQuantityMultiplier(origNode.getQuantityMultiplier());
                        }
                    }
                    break;
                }
                // FIXME: AND operator still not implemented
            } // end of for
            
        }
        return oneChildNode;
    }
    
    private ItemConfigurationNode configurator(GenericValue node, List productFeatures, String productIdForRules, Date inDate, GenericDelegator delegator, LocalDispatcher dispatcher) throws GenericEntityException {
        ItemConfigurationNode oneChildNode = new ItemConfigurationNode((String)node.get("productIdTo"), delegator);
        oneChildNode.setProductAssoc(node);
        try {
            oneChildNode.setQuantityMultiplier(node.getDouble("quantity").doubleValue());
        } catch(Exception nfe) {
            oneChildNode.setQuantityMultiplier(1);
        }
        try {
            double percScrapFactor = node.getDouble("scrapFactor").doubleValue();
            if (percScrapFactor >= 0 && percScrapFactor < 100) {
                percScrapFactor = 1 - percScrapFactor / 100;
            } else {
                percScrapFactor = 1;
            }
            oneChildNode.setScrapFactor(percScrapFactor);
        } catch(Exception nfe) {
            oneChildNode.setScrapFactor(1);
        }
        ItemConfigurationNode newNode = oneChildNode;
        // CONFIGURATOR
        if (oneChildNode.isVirtual()) {
            // If the part is VIRTUAL and
            // productFeatures and productPartRules are not null
            // we have to substitute the part with the right part's variant
            List productPartRules = delegator.findByAnd("ProductManufacturingRule",
                                                    UtilMisc.toMap("productId", productIdForRules,
                                                    "productIdFor", node.get("productId"),
                                                    "productIdIn", node.get("productIdTo")));
            if (substitutedNode != null) {
                productPartRules.addAll(delegator.findByAnd("ProductManufacturingRule",
                                                    UtilMisc.toMap("productId", productIdForRules,
                                                    "productIdFor", substitutedNode.getProduct().getString("productId"),
                                                    "productIdIn", node.get("productIdTo"))));
            }
            productPartRules = EntityUtil.filterByDate(productPartRules, inDate);
            newNode = substituteNode(oneChildNode, productFeatures, productPartRules, delegator);
            if (newNode == oneChildNode) {
                // If no substitution has been done (no valid rule applied),
                // we try to search for a generic link-rule
                List genericLinkRules = delegator.findByAnd("ProductManufacturingRule",
                                                        UtilMisc.toMap("productIdFor", node.get("productId"),
                                                        "productIdIn", node.get("productIdTo")));
                if (substitutedNode != null) {
                    genericLinkRules.addAll(delegator.findByAnd("ProductManufacturingRule",
                                                        UtilMisc.toMap("productIdFor", substitutedNode.getProduct().getString("productId"),
                                                        "productIdIn", node.get("productIdTo"))));
                }
                genericLinkRules = EntityUtil.filterByDate(genericLinkRules, inDate);
                newNode = null;
                newNode = substituteNode(oneChildNode, productFeatures, genericLinkRules, delegator);
                if (newNode == oneChildNode) {
                    // If no substitution has been done (no valid rule applied),
                    // we try to search for a generic node-rule
                    List genericNodeRules = delegator.findByAnd("ProductManufacturingRule",
                                                            UtilMisc.toMap("productIdIn", node.get("productIdTo")),
                                                            UtilMisc.toList("ruleSeqId"));
                    genericNodeRules = EntityUtil.filterByDate(genericNodeRules, inDate);
                    newNode = null;
                    newNode = substituteNode(oneChildNode, productFeatures, genericNodeRules, delegator);
                    if (newNode == oneChildNode) {
                        // If no substitution has been done (no valid rule applied),
                        // we try to set the default (first) node-substitution
                        if (genericNodeRules != null && genericNodeRules.size() > 0) {
                            // FIXME
                            //...
                        }
                        // -----------------------------------------------------------
                        // We try to apply directly the selected features
                        if (newNode == oneChildNode) {
                            Map selectedFeatures = new HashMap();
                            if (productFeatures != null) {
                                GenericValue feature = null;
                                for (int j = 0; j < productFeatures.size(); j++) {
                                    feature = (GenericValue)productFeatures.get(j);
                                    selectedFeatures.put((String)feature.get("productFeatureTypeId"), (String)feature.get("productFeatureId")); // FIXME
                                }
                            }

                            if (selectedFeatures.size() > 0) {
                                Map context = new HashMap();
                                context.put("productId", node.get("productIdTo"));
                                context.put("selectedFeatures", selectedFeatures);
                                Map storeResult = null;
                                GenericValue variantProduct = null;
                                try {
                                    storeResult = dispatcher.runSync("getProductVariant", context);
                                    List variantProducts = (List) storeResult.get("products");
                                    if (variantProducts.size() == 1) {
                                        variantProduct = (GenericValue)variantProducts.get(0);
                                    }
                                } catch (GenericServiceException e) {
                                    String service = e.getMessage();
                                    System.out.println("ItemConfigurationNode.configurator(...): " + service);
                                }
                                if (variantProduct != null) {
                                    newNode = new ItemConfigurationNode(variantProduct);
                                    newNode.setSubstitutedNode(oneChildNode);
                                    newNode.setQuantityMultiplier(oneChildNode.getQuantityMultiplier());
                                    newNode.setScrapFactor(oneChildNode.getScrapFactor());
                                    newNode.setProductAssoc(oneChildNode.getProductAssoc());
                                }
                            }

                        }
                        // -----------------------------------------------------------
                    }
                }
            }
        } // end of if (isVirtual())
        return newNode;
    }

    protected void loadParents(String partBomTypeId, Date inDate, List productFeatures) throws GenericEntityException {
        if (product == null) {
            throw new GenericEntityException("product is null");
        }
        // If the date is null, set it to today.
        if (inDate == null) inDate = new Date();

        bomTypeId = partBomTypeId;
        GenericDelegator delegator = product.getDelegator();
        List rows = delegator.findByAnd("ProductAssoc", 
                                            UtilMisc.toMap("productIdTo", product.get("productId"), 
                                                       "productAssocTypeId", partBomTypeId),
                                            UtilMisc.toList("sequenceNum"));
        rows = EntityUtil.filterByDate(rows, inDate);
        if ((rows == null || rows.size() == 0) && substitutedNode != null) {
            // If no parent is found and this is a substituted node
            // we try to search for substituted node's parents.
            rows = delegator.findByAnd("ProductAssoc", 
                                        UtilMisc.toMap("productIdTo", substitutedNode.getProduct().get("productId"), 
                                                       "productAssocTypeId", partBomTypeId),
                                        UtilMisc.toList("sequenceNum"));
            rows = EntityUtil.filterByDate(rows, inDate);
        }
        children = new ArrayList(rows);
        childrenNodes = new ArrayList();
        Iterator childrenIterator = children.iterator();
        GenericValue oneChild = null;
        ItemConfigurationNode oneChildNode = null;
        while(childrenIterator.hasNext()) {
            oneChild = (GenericValue)childrenIterator.next();
            oneChildNode = new ItemConfigurationNode(oneChild.getString("productId"), delegator);
            // Configurator
            //oneChildNode = configurator(oneChild, productFeatures, getRootNode().getProductForRules(), delegator);
            // If the node is null this means that the node has been discarded by the rules.
            if (oneChildNode != null) {
                oneChildNode.setParentNode(this);
                oneChildNode.loadParents(partBomTypeId, inDate, productFeatures);
            }
            childrenNodes.add(oneChildNode);
        }
    }

    
    /** Getter for property parentNode.
     * @return Value of property parentNode.
     *
     */
    public ItemConfigurationNode getParentNode() {
        return parentNode;
    }

    public ItemConfigurationNode getRootNode() {
        return (parentNode != null? getParentNode(): this);
    }
    /** Setter for property parentNode.
     * @param parentNode New value of property parentNode.
     *
     */
    public void setParentNode(ItemConfigurationNode parentNode) {
        this.parentNode = parentNode;
    }
    // ------------------------------------
    // Method used for TEST and DEBUG purposes
    public void print(StringBuffer sb, double quantity, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append("<b>&nbsp;*&nbsp;</b>");
        }
        sb.append(product.get("productId"));
        sb.append(" - ");
        sb.append("" + quantity);
        GenericValue oneChild = null;
        ItemConfigurationNode oneChildNode = null;
        depth++;
        for (int i = 0; i < children.size(); i++) {
            oneChild = (GenericValue)children.get(i);
            double bomQuantity = 0;
            try {
                bomQuantity = oneChild.getDouble("quantity").doubleValue();
            } catch(Exception exc) {
                bomQuantity = 1;
            }
            oneChildNode = (ItemConfigurationNode)childrenNodes.get(i);
            sb.append("<br>");
            if (oneChildNode != null) {
                oneChildNode.print(sb, (quantity * bomQuantity), depth);
            }
        }
    }

    public void print(ArrayList arr, double quantity, int depth, boolean excludePhantoms) {
        // Now we set the depth and quantity of the current node
        // in this breakdown.
        this.depth = depth;
        //this.quantity = Math.floor(quantity * quantityMultiplier / scrapFactor + 0.5);
        this.quantity = quantity * quantityMultiplier / scrapFactor;
        // First of all we visit the current node.
        arr.add(this);
        // Now (recursively) we visit the children.
        GenericValue oneChild = null;
        ItemConfigurationNode oneChildNode = null;
        depth++;
        for (int i = 0; i < children.size(); i++) {
            oneChild = (GenericValue)children.get(i);
            oneChildNode = (ItemConfigurationNode)childrenNodes.get(i);
            // FIXME: phantom flag?
            if (excludePhantoms && "PHANTOM".equals(oneChildNode.getProduct().getString("productTypeId"))) {
                continue;
            }
            if (oneChildNode != null) {
                oneChildNode.print(arr, this.quantity, depth, excludePhantoms);
            }
        }
    }

    public void sumQuantity(HashMap nodes) {
        // First of all, we try to fetch a node with the same partId
        ItemConfigurationNode sameNode = (ItemConfigurationNode)nodes.get(product.getString("productId"));
        // If the node is not found we create a new node for the current product
        if (sameNode == null) {
            sameNode = new ItemConfigurationNode(product);
            nodes.put(product.getString("productId"), sameNode);
        }
        // Now we add the current quantity to the node
        sameNode.setQuantity(sameNode.getQuantity() + quantity);
        // Now (recursively) we visit the children.
        ItemConfigurationNode oneChildNode = null;
        for (int i = 0; i < childrenNodes.size(); i++) {
            oneChildNode = (ItemConfigurationNode)childrenNodes.get(i);
            if (oneChildNode != null) {
                oneChildNode.sumQuantity(nodes);
            }
        }
    }

    public void createManufacturingOrder(String workEffortId, String orderId, String orderItemSeqId, String shipmentId, String facilityId, Date date, boolean useSubstitute, GenericDelegator delegator, LocalDispatcher dispatcher, GenericValue userLogin) throws GenericEntityException {
        if (isManufactured()) {
            String productionRunId = null;
            Timestamp startDate = UtilDateTime.toTimestamp(UtilDateTime.toDateTimeString(date));
            Map serviceContext = new HashMap();
            if (!useSubstitute) {
                serviceContext.put("productId", getProduct().getString("productId"));
                serviceContext.put("facilityId", getProduct().getString("facilityId"));
            } else {
                serviceContext.put("productId", getSubstitutedNode().getProduct().getString("productId"));
                serviceContext.put("facilityId", getSubstitutedNode().getProduct().getString("facilityId"));
            }
            serviceContext.put("pRQuantity", new Double(getQuantity()));
            serviceContext.put("startDate", startDate);
            serviceContext.put("userLogin", userLogin);
            Map resultService = null;
            try {
                resultService = dispatcher.runSync("createProductionRun", serviceContext);
                productionRunId = (String)resultService.get("productionRunId");
            } catch (GenericServiceException e) {
                //Debug.logError(e, "Problem calling the getManufacturingComponents service", module);
            }
            System.out.println("Production run #" + productionRunId + " created for " + getProduct().getString("productId"));
            try {
                if (productionRunId != null && orderId != null && orderItemSeqId != null) {
                    delegator.create("WorkOrderItemFulfillment", UtilMisc.toMap("workEffortId", productionRunId, "orderId", orderId, "orderItemSeqId", orderItemSeqId));
                }
                if (productionRunId != null && workEffortId != null) {
                    delegator.create("WorkEffortAssoc", UtilMisc.toMap("workEffortIdFrom", productionRunId, "workEffortIdTo", workEffortId, "workEffortAssocTypeId", "WORK_EFF_PRECEDENCY"));
                }
            } catch (GenericEntityException e) {
                //Debug.logError(e, "Problem calling the getManufacturingComponents service", module);
            }
            ItemConfigurationNode oneChildNode = null;
            for (int i = 0; i < childrenNodes.size(); i++) {
                oneChildNode = (ItemConfigurationNode)childrenNodes.get(i);
                if (oneChildNode != null) {
                    oneChildNode.createManufacturingOrder(productionRunId, null, null, shipmentId, facilityId, date, false, delegator, dispatcher, userLogin);
                }
            }
        }
    }

    public boolean isPurchased() {
        boolean isPurchased = false;
        try {
            if ("PHANTOM".equals(getProduct().getString("productTypeId"))) {
                return false;
            }
            List pfs = getProduct().getRelatedCache("ProductFacility");
            Iterator pfsIt = pfs.iterator();
            GenericValue pf = null;
            boolean found = false;
            while(pfsIt.hasNext()) {
                found = true;
                pf = (GenericValue)pfsIt.next();
                if (pf.getDouble("minimumStock") != null && pf.getDouble("minimumStock").doubleValue() > 0) {
                    isPurchased = true;
                }
            }
            // If no records are found, we try to search for substituted node's records
            if (!found && getSubstitutedNode() != null) {
                pfs = getSubstitutedNode().getProduct().getRelatedCache("ProductFacility");
                pfsIt = pfs.iterator();
                pf = null;
                while(pfsIt.hasNext()) {
                    pf = (GenericValue)pfsIt.next();
                    if (pf.getDouble("minimumStock") != null && pf.getDouble("minimumStock").doubleValue() > 0) {
                        isPurchased = true;
                    }
                }
            }
        } catch(GenericEntityException gee) {
            System.out.println("Error in ItemConfigurationNode.isPurchased() " + gee);
        }
        return isPurchased;
    }

    public boolean isManufactured() {
        return childrenNodes.size() > 0 && !isPurchased();
    }
    
    public boolean isVirtual() {
        return (product.get("isVirtual") != null? product.get("isVirtual").equals("Y"): false);
    }

    public void isConfigured(ArrayList arr) {
        // First of all we visit the current node.
        if (isVirtual()) {
            arr.add(this);
        }
        // Now (recursively) we visit the children.
        GenericValue oneChild = null;
        ItemConfigurationNode oneChildNode = null;
        for (int i = 0; i < children.size(); i++) {
            oneChild = (GenericValue)children.get(i);
            oneChildNode = (ItemConfigurationNode)childrenNodes.get(i);
            if (oneChildNode != null) {
                oneChildNode.isConfigured(arr);
            }
        }
    }
   

    /** Getter for property quantity.
     * @return Value of property quantity.
     *
     */
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /** Getter for property depth.
     * @return Value of property depth.
     *
     */

    public int getDepth() {
        return depth;
    }
  
    public GenericValue getProduct() {
        return product;
    }
    
    /** Getter for property substitutedNode.
     * @return Value of property substitutedNode.
     *
     */
    public ItemConfigurationNode getSubstitutedNode() {
        return substitutedNode;
    }
  
    /** Setter for property substitutedNode.
     * @param substitutedNode New value of property substitutedNode.
     *
     */
    public void setSubstitutedNode(ItemConfigurationNode substitutedNode) {
        this.substitutedNode = substitutedNode;
    }
 
    public String getRootProductForRules() {
        return getParentNode().getProductForRules();
    }
    
    /** Getter for property productForRules.
     * @return Value of property productForRules.
     *
     */
    public String getProductForRules() {
        return productForRules;
    }
    
    /** Setter for property productForRules.
     * @param productForRules New value of property productForRules.
     *
     */
    public void setProductForRules(String productForRules) {
        this.productForRules = productForRules;
    }
    
    /** Getter for property bomTypeId.
     * @return Value of property bomTypeId.
     *
     */
    public java.lang.String getBomTypeId() {
        return bomTypeId;
    }
    
    /** Getter for property quantityMultiplier.
     * @return Value of property quantityMultiplier.
     *
     */
    public double getQuantityMultiplier() {
        return quantityMultiplier;
    }    
    
    /** Setter for property quantityMultiplier.
     * @param quantityMultiplier New value of property quantityMultiplier.
     *
     */
    public void setQuantityMultiplier(double quantityMultiplier) {
        this.quantityMultiplier = quantityMultiplier;
    }
    
    /** Getter for property ruleApplied.
     * @return Value of property ruleApplied.
     *
     */
    public org.ofbiz.entity.GenericValue getRuleApplied() {
        return ruleApplied;
    }
    
    /** Setter for property ruleApplied.
     * @param ruleApplied New value of property ruleApplied.
     *
     */
    public void setRuleApplied(org.ofbiz.entity.GenericValue ruleApplied) {
        this.ruleApplied = ruleApplied;
    }
    
    /** Getter for property scrapFactor.
     * @return Value of property scrapFactor.
     *
     */
    public double getScrapFactor() {
        return scrapFactor;
    }
    
    /** Setter for property scrapFactor.
     * @param scrapFactor New value of property scrapFactor.
     *
     */
    public void setScrapFactor(double scrapFactor) {
        this.scrapFactor = scrapFactor;
    }
    
    /** Getter for property childrenNodes.
     * @return Value of property childrenNodes.
     *
     */
    public java.util.ArrayList getChildrenNodes() {
        return childrenNodes;
    }
    
    /** Setter for property childrenNodes.
     * @param childrenNodes New value of property childrenNodes.
     *
     */
    public void setChildrenNodes(java.util.ArrayList childrenNodes) {
        this.childrenNodes = childrenNodes;
    }
    
    /** Getter for property productAssoc.
     * @return Value of property productAssoc.
     *
     */
    public org.ofbiz.entity.GenericValue getProductAssoc() {
        return productAssoc;
    }
    
    /** Setter for property productAssoc.
     * @param productAssoc New value of property productAssoc.
     *
     */
    public void setProductAssoc(org.ofbiz.entity.GenericValue productAssoc) {
        this.productAssoc = productAssoc;
    }
    
}

