/*
 * ItemConfigurationTree.java
 *
 * Created on 1 ottobre 2003, 17.16
 */

package org.ofbiz.manufacturing.bom;

import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import org.ofbiz.base.util.UtilMisc;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

/**
 *
 * @author  jacopo
 */
public class ItemConfigurationTree {
    
    ItemConfigurationNode root;
    float rootQuantity;
    Date fromDate;
    String bomTypeId;
    
    public ItemConfigurationTree(String productId, String bomTypeId, Date fromDate, GenericDelegator delegator) throws GenericEntityException {
        this(productId, bomTypeId, fromDate, true, delegator);
    }
    
    /** Creates a new instance of ItemConfigurationTree */
    public ItemConfigurationTree(String productId, String bomTypeId, Date fromDate, boolean explosion, GenericDelegator delegator) throws GenericEntityException {
        // If the parameters are not valid, return.
        if (productId == null || bomTypeId == null || fromDate == null || delegator == null) return;
        String productIdForRules = productId;
        // The selected product features are loaded
        List productFeatures = delegator.findByAnd("ProductFeatureAppl",
                                              UtilMisc.toMap("productId", productId,
                                              "productFeatureApplTypeId", "STANDARD_FEATURE"));

        // If the product is manufactured as a different product,
        // load the new product
        GenericValue manufacturedAsProduct = manufacturedAsProduct(productId, delegator);
        // We load the information about the product that needs to be manufactured
        // from Product entity
        GenericValue product = delegator.findByPrimaryKey("Product", 
                                            UtilMisc.toMap("productId", 
                                            (manufacturedAsProduct != null? manufacturedAsProduct.getString("productIdTo"): productId)));
        if (product == null) return;
        // If the product hasn't a bill of materials we try to retrieve
        // the bill of materials of its virtual product (if the current
        // product is variant).
        if (!hasBom(product) && product.get("isVariant")!=null && 
                product.getString("isVariant").equals("Y")) {
            List virtualProducts = product.getRelatedByAnd("AssocProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT"));
            if (virtualProducts != null && virtualProducts.size() > 0) {
                GenericValue virtualProduct = (GenericValue)virtualProducts.get(0);
                // If the virtual product is manufactured as a different product,
                // load the new product
                productIdForRules = virtualProduct.getString("productId");
                manufacturedAsProduct = manufacturedAsProduct(virtualProduct.getString("productId"), delegator);
                product = delegator.findByPrimaryKey("Product", 
                                            UtilMisc.toMap("productId", 
                                            (manufacturedAsProduct != null? manufacturedAsProduct.getString("productIdTo"): virtualProduct.get("productId"))));
            }
        }
        if (product == null) return;
        try {
            root = new ItemConfigurationNode(product);
            root.setProductForRules(productIdForRules);
            if (explosion) {
                root.loadChildren(bomTypeId, fromDate, productFeatures);
            } else {
                root.loadParents(bomTypeId, fromDate, productFeatures);
            }
        } catch(GenericEntityException gee) {
            root = null;
        }
        this.bomTypeId = bomTypeId;
        this.fromDate = fromDate;
        rootQuantity = 1;
    }

    private GenericValue manufacturedAsProduct(String productId, GenericDelegator delegator) throws GenericEntityException {
        List manufacturedAsProducts = delegator.findByAnd("ProductAssoc", 
                                         UtilMisc.toMap("productId", productId,
                                         //"fromDate", fromDate,
                                         "productAssocTypeId", "PRODUCT_MANUFACTURED"));
        GenericValue manufacturedAsProduct = null;
        if (manufacturedAsProducts != null && manufacturedAsProducts.size() > 0) {
            manufacturedAsProduct = (GenericValue)manufacturedAsProducts.get(0);
        }
        return manufacturedAsProduct;
    }
    
    private boolean hasBom(GenericValue product) throws GenericEntityException {
        List children = product.getRelatedByAnd("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", bomTypeId));
        return (children != null && children.size() > 0);
    }

    public boolean isConfigured() {
        ArrayList notConfiguredParts = new ArrayList();
        root.isConfigured(notConfiguredParts);
        return (notConfiguredParts.size() == 0);
    }
    
    /** Getter for property rootQuantity.
     * @return Value of property rootQuantity.
     *
     */
    public float getRootQuantity() {
        return rootQuantity;
    }
    
    /** Setter for property rootQuantity.
     * @param rootQuantity New value of property rootQuantity.
     *
     */
    public void setRootQuantity(float rootQuantity) {
        this.rootQuantity = rootQuantity;
    }

    
    /** Getter for property root.
     * @return Value of property root.
     *
     */
    public ItemConfigurationNode getRoot() {
        return root;
    }    
    
    /** Getter for property fromDate.
     * @return Value of property fromDate.
     *
     */
    public Date getFromDate() {
        return fromDate;
    }
    
    /** Getter for property bomTypeId.
     * @return Value of property bomTypeId.
     *
     */
    public String getBomTypeId() {
        return bomTypeId;
    }
    
    // ------------------------------------
    // Method used for TEST and DEBUG purposes
    public void print(StringBuffer sb) {
        if (root != null) {
            root.print(sb, getRootQuantity(), 0);
        }
    }

    // Method used for TEST and DEBUG purposes
    public void print(ArrayList arr) {
        if (root != null) {
            root.print(arr, getRootQuantity(), 0);
        }
    }

    // Method used for TEST and DEBUG purposes
    public void sumQuantities(HashMap quantityPerNode) {
        if (root != null) {
            root.sumQuantity(quantityPerNode);
        }
    }
    
    public ArrayList getAllProductsId() {
        ArrayList nodeArr = new ArrayList();
        ArrayList productsId = new ArrayList();
        print(nodeArr);
        for (int i = 0; i < nodeArr.size(); i++) {
            productsId.add(((ItemConfigurationNode)nodeArr.get(i)).getPart().getString("productId"));
        }
        return productsId;
    }
}
