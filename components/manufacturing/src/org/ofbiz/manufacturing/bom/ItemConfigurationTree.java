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
    String partBomTypeId;
    
    /** Creates a new instance of ItemConfigurationTree */
    public ItemConfigurationTree(String partId, String partBomTypeId, Date fromDate, GenericDelegator delegator) throws GenericEntityException {
        this(null, partId, partBomTypeId, fromDate, delegator);
    }

    public ItemConfigurationTree(String productId, String partId, String partBomTypeId, Date fromDate, GenericDelegator delegator) throws GenericEntityException {
        List productFeatures = null;
        List productPartRules = null;
        if (productId != null && productId.length() > 0) {
            // The parameter partId is ignored:
            // the partId is retrieved from ProductPartAssoc using the productId.
            partId = null;
            //
            // Part breakdown is performed using the CONFIGURATOR:
            // the VIRTUAL parts are resolved against productFeatures
            // and partVariants.
            // viene recuperata in partId la parte associata dall'entità ProductAttribute
            List parts = delegator.findByAnd("ProductAssoc", 
                                                   UtilMisc.toMap("productId", productId,
                                                                  //"fromDate", fromDate,
                                                                  "productAssocTypeId", "PRODUCT_PART"));
            GenericValue part = null;
            if (parts != null && parts.size() > 0) {
                part = (GenericValue)parts.get(0);
            }
            
            // se non viene trovata la parte:
            //   se productId è un prodotto VARIANTE --> viene recuparata la parte del prodotto VIRTUALE ad esso associato (tramite ProductAssoc)
            if (part == null) {
                List virtualProducts = delegator.findByAnd("ProductAssoc", 
                                                   UtilMisc.toMap("productIdTo", productId,
                                                                  //"fromDate", fromDate,
                                                                  "productAssocTypeId", "PRODUCT_VARIANT"));
                if (virtualProducts != null && virtualProducts.size() > 0) {
                    GenericValue virtualProduct = (GenericValue)virtualProducts.get(0);
                    parts = delegator.findByAnd("ProductAssoc",
                                                   UtilMisc.toMap("productId", (String)virtualProduct.get("productId"),
                                                                  "productAssocTypeId", "PRODUCT_PART"));
                    if (parts != null && parts.size() > 0) {
                        part = (GenericValue)parts.get(0);
                    }

                }
            }
            if (part != null) {
                partId = (String)part.get("productIdTo");
                String productForRules = (String)part.get("productId");
                // vengono recuperate in productFeatures le eventuali FEATURES del prodotto productId
                productFeatures = delegator.findByAnd("ProductFeatureAppl",
                                                       UtilMisc.toMap("productId", productId,
                                                                      "productFeatureApplTypeId", "STANDARD_FEATURE"));

                // vengono recuperate le regole
                productPartRules = delegator.findByAnd("ProductPartRule",
                                                       UtilMisc.toMap("productId", productForRules));
                                                                      //"fromDate", fromDate));
            }
        }
        try {
            if (partId != null && partId.length() > 0) {
                root = new ItemConfigurationNode(partId, delegator);
                root.loadChildren(partBomTypeId, fromDate, productFeatures, productPartRules);
            }
        } catch(GenericEntityException gee) {
            root = null;
        }
        this.partBomTypeId = partBomTypeId;
        this.fromDate = fromDate;
        rootQuantity = 1;
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
    
    /** Getter for property partBomTypeId.
     * @return Value of property partBomTypeId.
     *
     */
    public String getPartBomTypeId() {
        return partBomTypeId;
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
    
}
