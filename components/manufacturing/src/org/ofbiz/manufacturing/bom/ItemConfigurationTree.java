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
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;


    /** It represents an (in-memory) bill of materials (in which each
     * component is an ItemConfigurationNode)
     * Useful for tree traversal (breakdown, explosion, implosion).
     * @author <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
     */    
 
public class ItemConfigurationTree {
    public static final int EXPLOSION = 0;
    public static final int EXPLOSION_SINGLE_LEVEL = 1;
    public static final int EXPLOSION_MANUFACTURING = 2;
    public static final int IMPLOSION = 3;
    
    ItemConfigurationNode root;
    double rootQuantity;
    Date inDate;
    String bomTypeId;

    /** Creates a new instance of ItemConfigurationTree by reading downward
     * the productId's bill of materials (explosion).
     * If virtual products are found, it tries to configure them by running
     * the Product Configurator.
     * @param productId The product for which we want to get the bom.
     * @param bomTypeId The bill of materials type (e.g. manufacturing, engineering, ...)
     * @param inDate Validity date (if null, today is used).
     *
     * @param delegator The delegator used.
     * @throws GenericEntityException If a db problem occurs.
     *
     */
    public ItemConfigurationTree(String productId, String bomTypeId, Date inDate, GenericDelegator delegator, LocalDispatcher dispatcher) throws GenericEntityException {
        this(productId, bomTypeId, inDate, EXPLOSION, delegator, dispatcher);
    }
    
    /** Creates a new instance of ItemConfigurationTree by reading
     * the productId's bill of materials (upward or downward).
     * If virtual products are found, it tries to configure them by running
     * the Product Configurator.
     * @param productId The product for which we want to get the bom.
     * @param bomTypeId The bill of materials type (e.g. manufacturing, engineering, ...)
     * @param inDate Validity date (if null, today is used).
     *
     * @param type if equals to EXPLOSION, a downward visit is performed (explosion);
     * if equals to EXPLOSION_SINGLE_LEVEL, a single level explosion is performed;
     * if equals to EXPLOSION_MANUFACTURING, a downward visit is performed (explosion), including only the product that needs manufacturing;
     * if equals to IMPLOSION an upward visit is done (implosion);
     *
     * @param delegator The delegator used.
     * @throws GenericEntityException If a db problem occurs.
     *
     */
    public ItemConfigurationTree(String productId, String bomTypeId, Date inDate, int type, GenericDelegator delegator, LocalDispatcher dispatcher) throws GenericEntityException {
        // If the parameters are not valid, return.
        if (productId == null || bomTypeId == null || delegator == null || dispatcher == null) return;
        // If the date is null, set it to today.
        if (inDate == null) inDate = new Date();
        
        String productIdForRules = productId;
        // The selected product features are loaded
        List productFeaturesAppl = delegator.findByAnd("ProductFeatureAppl",
                                              UtilMisc.toMap("productId", productId,
                                              "productFeatureApplTypeId", "STANDARD_FEATURE"));
        List productFeatures = new ArrayList();
        GenericValue oneProductFeatureAppl = null;
        for (int i = 0; i < productFeaturesAppl.size(); i++) {
            oneProductFeatureAppl = (GenericValue)productFeaturesAppl.get(i);
            productFeatures.add(delegator.findByPrimaryKey("ProductFeature", 
                                       UtilMisc.toMap("productFeatureId", oneProductFeatureAppl.getString("productFeatureId"))));
                               
        }
        // If the product is manufactured as a different product,
        // load the new product
        GenericValue manufacturedAsProduct = manufacturedAsProduct(productId, inDate, delegator);
        // We load the information about the product that needs to be manufactured
        // from Product entity
        GenericValue product = delegator.findByPrimaryKey("Product", 
                                            UtilMisc.toMap("productId", 
                                            (manufacturedAsProduct != null? manufacturedAsProduct.getString("productIdTo"): productId)));
        if (product == null) return;
        ItemConfigurationNode originalNode = new ItemConfigurationNode(product);
        // If the product hasn't a bill of materials we try to retrieve
        // the bill of materials of its virtual product (if the current
        // product is variant).
        if (!hasBom(product, inDate)) {
            List virtualProducts = product.getRelatedByAnd("AssocProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT"));
            if (virtualProducts != null && virtualProducts.size() > 0) {
                virtualProducts = EntityUtil.filterByDate(virtualProducts, inDate);
                if (virtualProducts != null && virtualProducts.size() > 0) {
                    GenericValue virtualProduct = (GenericValue)virtualProducts.get(0);
                    // If the virtual product is manufactured as a different product,
                    // load the new product
                    productIdForRules = virtualProduct.getString("productId");
                    manufacturedAsProduct = manufacturedAsProduct(virtualProduct.getString("productId"), inDate, delegator);
                    product = delegator.findByPrimaryKey("Product", 
                                                UtilMisc.toMap("productId", 
                                                (manufacturedAsProduct != null? manufacturedAsProduct.getString("productIdTo"): virtualProduct.get("productId"))));
                }
            }
        }
        if (product == null) return;
        try {
            root = new ItemConfigurationNode(product);
            root.setProductForRules(productIdForRules);
            root.setSubstitutedNode(originalNode);
            if (type == IMPLOSION) {
                root.loadParents(bomTypeId, inDate, productFeatures);
            } else {
                root.loadChildren(bomTypeId, inDate, productFeatures, type, dispatcher);
            }
        } catch(GenericEntityException gee) {
            root = null;
        }
        this.bomTypeId = bomTypeId;
        this.inDate = inDate;
        rootQuantity = 1;
    }

    private GenericValue manufacturedAsProduct(String productId, Date inDate, GenericDelegator delegator) throws GenericEntityException {
        List manufacturedAsProducts = delegator.findByAnd("ProductAssoc", 
                                         UtilMisc.toMap("productId", productId,
                                         "productAssocTypeId", "PRODUCT_MANUFACTURED"));
        manufacturedAsProducts = EntityUtil.filterByDate(manufacturedAsProducts, inDate);
        GenericValue manufacturedAsProduct = null;
        if (manufacturedAsProducts != null && manufacturedAsProducts.size() > 0) {
            manufacturedAsProduct = (GenericValue)manufacturedAsProducts.get(0);
        }
        return manufacturedAsProduct;
    }
    
    private boolean hasBom(GenericValue product, Date inDate) throws GenericEntityException {
        List children = product.getRelatedByAnd("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", bomTypeId));
        children = EntityUtil.filterByDate(children, inDate);
        return (children != null && children.size() > 0);
    }

    /** It tells if the current (in-memory) tree representing
     * a product's bill of materials is completely configured
     * or not.
     * @return true if no virtual nodes (products) are present in the tree.
     *
     */    
    public boolean isConfigured() {
        ArrayList notConfiguredParts = new ArrayList();
        root.isConfigured(notConfiguredParts);
        return (notConfiguredParts.size() == 0);
    }
    
    /** Getter for property rootQuantity.
     * @return Value of property rootQuantity.
     *
     */
    public double getRootQuantity() {
        return rootQuantity;
    }
    
    /** Setter for property rootQuantity.
     * @param rootQuantity New value of property rootQuantity.
     *
     */
    public void setRootQuantity(double rootQuantity) {
        this.rootQuantity = rootQuantity;
    }

    
    /** Getter for property root.
     * @return Value of property root.
     *
     */
    public ItemConfigurationNode getRoot() {
        return root;
    }    
    
    /** Getter for property inDate.
     * @return Value of property inDate.
     *
     */
    public Date getInDate() {
        return inDate;
    }
    
    /** Getter for property bomTypeId.
     * @return Value of property bomTypeId.
     *
     */
    public String getBomTypeId() {
        return bomTypeId;
    }
    
    /** It visits the in-memory tree that represents a bill of materials
     * and it collects info of its nodes in the StringBuffer.
     * Method used for debug purposes.
     * @param sb The StringBuffer used to collect tree info.
     */    
    public void print(StringBuffer sb) {
        if (root != null) {
            root.print(sb, getRootQuantity(), 0);
        }
    }

    /** It visits the in-memory tree that represents a bill of materials
     * and it collects info of its nodes in the ArrayList.
     * Method used for bom breakdown (explosion/implosion).
     * @param arr The ArrayList used to collect tree info.
     * @param initialDepth The depth of the root node.
     */    
    public void print(ArrayList arr, int initialDepth) {
        print(arr, initialDepth, true);
    }
    
    public void print(ArrayList arr, int initialDepth, boolean excludePhantoms) {
        if (root != null) {
            root.print(arr, getRootQuantity(), initialDepth, excludePhantoms);
        }
    }
    
    /** It visits the in-memory tree that represents a bill of materials
     * and it collects info of its nodes in the ArrayList.
     * Method used for bom breakdown (explosion/implosion).
     * @param arr The ArrayList used to collect tree info.
     */    
    public void print(ArrayList arr) {
        print(arr, 0);
    }

    public void print(ArrayList arr, boolean excludePhantoms) {
        print(arr, 0, excludePhantoms);
    }

    /** It visits the in-memory tree that represents a bill of materials
     * and it collects info of its nodes in the HashMap.
     * Method used for bom summarized explosion.
     * @param quantityPerNode The HashMap that will contain the summarized quantities per productId.
     */    
    public void sumQuantities(HashMap quantityPerNode) {
        if (root != null) {
            root.sumQuantity(quantityPerNode);
        }
    }
    
    /** It visits the in-memory tree that represents a bill of materials
     * and it collects all the productId it contains.
     * @return ArrayLsit conatining all the tree's productId.
     */    
    public ArrayList getAllProductsId() {
        ArrayList nodeArr = new ArrayList();
        ArrayList productsId = new ArrayList();
        print(nodeArr);
        for (int i = 0; i < nodeArr.size(); i++) {
            productsId.add(((ItemConfigurationNode)nodeArr.get(i)).getProduct().getString("productId"));
        }
        return productsId;
    }
    
    /** It visits the in-memory tree that represents a bill of materials
     * and it creates a manufacturing order for each of the nodes that needs
     * to be manufactured.
     * @param orderId The (sales) order id for which the manufacturing orders are created. If specified (together with orderItemSeqId) a link between the two order lines is created in the OrderItemAssociation entity. If null, no link is created.
     * @param orderItemSeqId
     * @param delegator The delegator used.
     * @throws GenericEntityException If a db problem occurs.
     */    
    public void createManufacturingOrders(String orderId, String orderItemSeqId, String shipmentId, Date date, GenericDelegator delegator, LocalDispatcher dispatcher, GenericValue userLogin)  throws GenericEntityException {
        if (root != null) {
            String facilityId = null;
            if (orderId != null) {
                GenericValue order = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                facilityId = order.getString("originFacilityId");
            }
            if (facilityId == null && shipmentId != null) {
                GenericValue shipment = delegator.findByPrimaryKey("Shipment", UtilMisc.toMap("shipmentId", shipmentId));
                facilityId = shipment.getString("originFacilityId");
            }
            root.createManufacturingOrder(null, orderId, orderItemSeqId, shipmentId, facilityId, date, true, delegator, dispatcher, userLogin);
        }
    }

}
