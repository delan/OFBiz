package org.ofbiz.product.supplier;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Services for suppliers of products
 *
 * @author     <a href="mailto:schen@graciousstyle.com">Si Chen</a>
 * @version    $Revision$
 * @since      3.0
 */
public class SupplierProductServices {

    public static final String module = SupplierProductServices.class.getName();
    public static final String resource = "ProductUiLabels";
    
/*
 * Parameters: productId, partyId
 * Result: a List of SupplierProduct entities for productId, filtered by date and optionally by partyId
 */
    public static Map getSuppliersForProduct(DispatchContext dctx, Map context) {
		Map results = new HashMap();
		GenericDelegator delegator = dctx.getDelegator();
        
		GenericValue product = null;
		String productId = (String) context.get("productId");
		String partyId = (String) context.get("partyId");
		
		try {
			product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
			List supplierProducts = product.getRelatedCache("SupplierProduct");

			// if there were no related SupplierProduct entities and the item is a variant, then get the SupplierProducts of the virtual parent product
			if (supplierProducts.size() == 0 && product.getString("isVariant") != null && product.getString("isVariant").equals("Y")) {
				String virtualProductId = ProductWorker.getVariantVirtualId(product);
				GenericValue virtualProduct = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", virtualProductId));
				if (virtualProduct != null) {
				supplierProducts = virtualProduct.getRelatedCache("SupplierProduct");
			}
			}
			
			// filter the list down by the partyId if one is provided
			if (partyId != null) {
				supplierProducts = EntityUtil.filterByAnd(supplierProducts, UtilMisc.toMap("partyId", partyId));
			}
			
			// filter the list down again by date before returning it
			results = ServiceUtil.returnSuccess();
			results.put("supplierProducts", EntityUtil.filterByDate(supplierProducts, UtilDateTime.nowTimestamp(), "availableFromDate", "availableThruDate", true));
		} catch (GenericEntityException ex) {
            Debug.logError(ex, ex.getMessage(), module);
            return ServiceUtil.returnError(ex.getMessage());
		}
    	return results;
    }
}
