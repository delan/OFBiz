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


import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;

import org.ofbiz.base.util.UtilMisc;

/**
 *
 * @author  jacopo
 */
public class ItemConfigurationNode {

    private ItemConfigurationNode parentNode; // the parent node (null if it's not present)
    private ItemConfigurationNode substitutedNode; // The virtual node (if any) that this instance substitutes
    private GenericValue part; // the current part (from Part entity)
    private ArrayList children; // part's children (from PartBom entity)
    private ArrayList childrenNodes; // part's children nodes (ItemConfigurationNode)
    private int depth;
    private float quantity;
    
    public ItemConfigurationNode(GenericValue part) {
        this.part = part;
        children = new ArrayList();
        childrenNodes = new ArrayList();
        parentNode = null;
        // Now we initialize the fields used in breakdowns
        depth = 0;
        quantity = 0;
    }

    public ItemConfigurationNode(String partId, GenericDelegator delegator) throws GenericEntityException {
        this(delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", partId)));
    }
    

    protected void loadChildren(String partBomTypeId, Date fromDate, List productFeatures, List productPartRules) throws GenericEntityException {
        if (part == null) {
            throw new GenericEntityException("Part is null");
        }
        GenericDelegator delegator = part.getDelegator();
        List rows = delegator.findByAnd("ProductAssoc", 
                                        UtilMisc.toMap("productId", part.get("productId"), 
                                                  //     "fromDate", fromDate,
                                                       "productAssocTypeId", partBomTypeId),
                                        UtilMisc.toList("sequenceNum"));
        if ((rows == null || rows.size() == 0) && substitutedNode != null) {
            // If no child is found and this is a substituted node
            // we try to search for substituted node's children.
            rows = delegator.findByAnd("ProductAssoc", 
                                        UtilMisc.toMap("productId", substitutedNode.getPart().get("productId"), 
                                                  //     "fromDate", fromDate,
                                                       "productAssocTypeId", partBomTypeId),
                                        UtilMisc.toList("sequenceNum"));
        }
        children = new ArrayList(rows);
        childrenNodes = new ArrayList();
        Iterator childrenIterator = children.iterator();
        GenericValue oneChild = null;
        ItemConfigurationNode oneChildNode = null;
        while(childrenIterator.hasNext()) {
            oneChild = (GenericValue)childrenIterator.next();
            // Configurator
            oneChildNode = configurator(oneChild, productFeatures, productPartRules, delegator);

            // If the node is null this means that the node has been discarded by the rules.
            if (oneChildNode != null) {
                oneChildNode.setParentNode(this);
                oneChildNode.loadChildren(partBomTypeId, fromDate, productFeatures, productPartRules);
            }
            childrenNodes.add(oneChildNode);
        }
    }
    
    private ItemConfigurationNode configurator(GenericValue node, List productFeatures, List productPartRules, GenericDelegator delegator) throws GenericEntityException {
        ItemConfigurationNode oneChildNode = new ItemConfigurationNode((String)node.get("productIdTo"), delegator);
        // CONFIGURATOR
        if (oneChildNode.isVirtual()) {
            if (productFeatures != null && productPartRules != null) {
                // If the part is VIRTUAL and
                // productFeatures and productPartRules are not null
                // we have to substitute the part with the right part's variant
                Iterator rules = productPartRules.iterator();
                ArrayList linkRules = new ArrayList();
                GenericValue rule = null;
                // First of all we select all the rules that apply to this link
                while (rules.hasNext()) {
                    rule = (GenericValue)rules.next();
                    if (((String)rule.get("partIdFor")).equals((String)part.get("productId")) &&
                    ((String)rule.get("partIdIn")).equals((String)oneChildNode.getPart().get("productId"))) {
                        // The rule concerns this link
                        linkRules.add(rule);
                    } else {
                        // We try to see if this rule applies to the substituted node (if any)
                        if (substitutedNode != null) {
                            if (((String)rule.get("partIdFor")).equals((String)substitutedNode.getPart().get("productId")) &&
                                ((String)rule.get("partIdIn")).equals((String)oneChildNode.getPart().get("productId"))) {
                                // The rule concerns this link
                                linkRules.add(rule);
                            }       
                        }
                    }
                }
                for (int i = 0; i < linkRules.size(); i++) {
                    rule = (GenericValue)linkRules.get(i);
                    String ruleCondition = (String)rule.get("productFeature");
                    String ruleOperator = (String)rule.get("ruleOperator");
                    String newPart = (String)rule.get("partIdInSubst");
                    GenericValue feature = null;
                    boolean ruleSatisfied = false;
                    for (int j = 0; j < productFeatures.size(); j++) {
                        feature = (GenericValue)productFeatures.get(j);
                        if (ruleCondition == null || ruleCondition.equals("") || ruleCondition.equals((String)feature.get("productFeatureId"))) {
                            ruleSatisfied = true;
                            break;
                        }
                    }
                    if (ruleSatisfied && ruleOperator.equals("OR")) {
                        ItemConfigurationNode tmpNode = oneChildNode;
                        if (newPart == null || newPart.equals("")) {
                            oneChildNode = null;
                        } else {
                            oneChildNode = new ItemConfigurationNode(newPart, delegator);
                            oneChildNode.setSubstitutedNode(tmpNode);
                        }
                        break;
                    }
                    // FIXME: implementare AND
                }
            }
        }
        return oneChildNode;
    }
    /** Getter for property parentNode.
     * @return Value of property parentNode.
     *
     */
    public ItemConfigurationNode getParentNode() {
        return parentNode;
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

    // Method used for TEST and DEBUG purposes
    public void print(ArrayList arr, float quantity, int depth) {
        // Now we set the depth and quantity of the current node
        // in this breakdown.
        this.depth = depth;
        this.quantity = quantity;
        // First of all we visit the corrent node.
        arr.add(this);
        // Now (recursively) we visit the children.
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
            if (oneChildNode != null) {
                oneChildNode.print(arr, (quantity * bomQuantity), depth);
            }
        }
        
    }

    // Method used for TEST and DEBUG purposes
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

    protected boolean isVirtual() {
        return part.get("productTypeId").equals("VIRTUAL");
    }
    
    public void isConfigured(ArrayList arr) {
        // First of all we visit the corrent node.
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
    
}
