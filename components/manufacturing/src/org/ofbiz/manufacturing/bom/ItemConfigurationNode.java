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

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;


/** An ItemCoinfigurationNode represents a component in a bill of materials.
 * @author <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 */

public class ItemConfigurationNode {

    private ItemConfigurationNode parentNode; // the parent node (null if it's not present)
    private ItemConfigurationNode substitutedNode; // The virtual node (if any) that this instance substitutes
    private GenericValue ruleApplied; // The rule (if any) that that has been applied to configure the current node
    private String productForRules;
    private GenericValue part; // the current product (from Product entity)
    private ArrayList children; // current node's children (ProductAssocs)
    private ArrayList childrenNodes; // current node's children nodes (ItemConfigurationNode)
    private float quantityMultiplier; // the necessary quantity as declared in the bom (from ProductAssocs or ProductManufacturingRule)
    private float scrapFactor; // the scrap factor as declared in the bom (from ProductAssocs)
    // Runtime fields
    private int depth; // the depth of this node in the current tree
    private float quantity; // the quantity of this node in the current tree
    private String bomTypeId; // the type of the current tree
   
    public ItemConfigurationNode(GenericValue part) {
        this.part = part;
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

    protected void loadChildren(String partBomTypeId, Date inDate, List productFeatures, LocalDispatcher dispatcher) throws GenericEntityException {
        if (part == null) {
            throw new GenericEntityException("Part is null");
        }
        // If the date is null, set it to today.
        if (inDate == null) inDate = new Date();

        bomTypeId = partBomTypeId;
        GenericDelegator delegator = part.getDelegator();
        List rows = delegator.findByAnd("ProductAssoc", 
                                            UtilMisc.toMap("productId", part.get("productId"), 
                                                       "productAssocTypeId", partBomTypeId),
                                            UtilMisc.toList("sequenceNum"));
        rows = EntityUtil.filterByDate(rows, inDate);
        if ((rows == null || rows.size() == 0) && substitutedNode != null) {
            // If no child is found and this is a substituted node
            // we try to search for substituted node's children.
            rows = delegator.findByAnd("ProductAssoc", 
                                        UtilMisc.toMap("productId", substitutedNode.getPart().get("productId"), 
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
                oneChildNode.loadChildren(partBomTypeId, inDate, productFeatures, dispatcher);
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
                float ruleQuantity = 0;
                try {
                    ruleQuantity = rule.getDouble("quantity").floatValue();
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
                        oneChildNode = new ItemConfigurationNode(newPart, delegator);
                        oneChildNode.setSubstitutedNode(tmpNode);
                        oneChildNode.setRuleApplied(rule);
                        if (ruleQuantity > 0) {
                            oneChildNode.setQuantityMultiplier(ruleQuantity);
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
        try {
            oneChildNode.setQuantityMultiplier(node.getDouble("quantity").floatValue());
        } catch(NumberFormatException nfe) {
            oneChildNode.setQuantityMultiplier(1);
        }
        try {
            float percScrapFactor = node.getDouble("scrapFactor").floatValue();
            if (percScrapFactor != 0) {
                percScrapFactor = percScrapFactor / 100;
            } else {
                percScrapFactor = 1;
            }
            oneChildNode.setScrapFactor(percScrapFactor);
        } catch(NumberFormatException nfe) {
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
                                                    "productIdFor", substitutedNode.getPart().getString("productId"),
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
                                                        UtilMisc.toMap("productIdFor", substitutedNode.getPart().getString("productId"),
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

                            Map context = new HashMap();
                            context.put("productId", node.get("productIdTo"));
                            context.put("selectedFeatures", selectedFeatures);
                            Map storeResult = null;
                            GenericValue variantProduct = null;
                            try {
                                storeResult = dispatcher.runSync("getProductVariant", context);
                                List variantProducts = (List) storeResult.get("products");
                                if (variantProducts.size() > 0) {
                                    variantProduct = (GenericValue)variantProducts.get(0);
                                }
                            } catch (GenericServiceException e) {
                                String service = e.getMessage();
                                System.out.println("ItemConfigurationNode.configurator(...): " + service);
                            }
                            if (variantProduct != null) {
                                newNode = new ItemConfigurationNode(variantProduct);
                                newNode.setSubstitutedNode(oneChildNode);
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
        if (part == null) {
            throw new GenericEntityException("Part is null");
        }
        // If the date is null, set it to today.
        if (inDate == null) inDate = new Date();

        bomTypeId = partBomTypeId;
        GenericDelegator delegator = part.getDelegator();
        List rows = delegator.findByAnd("ProductAssoc", 
                                            UtilMisc.toMap("productIdTo", part.get("productId"), 
                                                       "productAssocTypeId", partBomTypeId),
                                            UtilMisc.toList("sequenceNum"));
        rows = EntityUtil.filterByDate(rows, inDate);
        if ((rows == null || rows.size() == 0) && substitutedNode != null) {
            // If no parent is found and this is a substituted node
            // we try to search for substituted node's parents.
            rows = delegator.findByAnd("ProductAssoc", 
                                        UtilMisc.toMap("productIdTo", substitutedNode.getPart().get("productId"), 
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
    public void print(StringBuffer sb, float quantity, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append("<b>&nbsp;*&nbsp;</b>");
        }
        sb.append(part.get("productId"));
        sb.append(" - ");
        sb.append("" + quantity);
        GenericValue oneChild = null;
        ItemConfigurationNode oneChildNode = null;
        depth++;
        for (int i = 0; i < children.size(); i++) {
            oneChild = (GenericValue)children.get(i);
            float bomQuantity = 0;
            try {
                bomQuantity = oneChild.getDouble("quantity").floatValue();
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

    public void print(ArrayList arr, float quantity, int depth) {
        // Now we set the depth and quantity of the current node
        // in this breakdown.
        this.depth = depth;
        this.quantity = quantity * quantityMultiplier * scrapFactor;
        // First of all we visit the corrent node.
        arr.add(this);
        // Now (recursively) we visit the children.
        GenericValue oneChild = null;
        ItemConfigurationNode oneChildNode = null;
        depth++;
        for (int i = 0; i < children.size(); i++) {
            oneChild = (GenericValue)children.get(i);
            oneChildNode = (ItemConfigurationNode)childrenNodes.get(i);
            if (oneChildNode != null) {
                oneChildNode.print(arr, this.quantity, depth);
            }
        }
    }

    public void sumQuantity(HashMap nodes) {
        // First of all, we try to fetch a node with the same partId
        ItemConfigurationNode sameNode = (ItemConfigurationNode)nodes.get(part.getString("productId"));
        // If the node is not found we create a new node for the current part
        if (sameNode == null) {
            sameNode = new ItemConfigurationNode(part);
            nodes.put(part.getString("productId"), sameNode);
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

    public void createManufacturingOrder(String orderId, String orderItemSeqId, GenericDelegator delegator, LocalDispatcher dispatcher, GenericValue userLogin) throws GenericEntityException {
        // FIXME: Not still implemented
    }

    // FIXME: at now we create manufacturing orders for all the non leaf components.
    protected boolean needsManufacturing() {
        return children.size() > 0; 
    }
    
    protected boolean isVirtual() {
        return (part.get("isVirtual") != null? part.get("isVirtual").equals("Y"): false);
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
    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    /** Getter for property depth.
     * @return Value of property depth.
     *
     */

    public int getDepth() {
        return depth;
    }
  
    public GenericValue getPart() {
        return part;
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
    public float getQuantityMultiplier() {
        return quantityMultiplier;
    }    
    
    /** Setter for property quantityMultiplier.
     * @param quantityMultiplier New value of property quantityMultiplier.
     *
     */
    public void setQuantityMultiplier(float quantityMultiplier) {
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
    public float getScrapFactor() {
        return scrapFactor;
    }
    
    /** Setter for property scrapFactor.
     * @param scrapFactor New value of property scrapFactor.
     *
     */
    public void setScrapFactor(float scrapFactor) {
        this.scrapFactor = scrapFactor;
    }
    
}

