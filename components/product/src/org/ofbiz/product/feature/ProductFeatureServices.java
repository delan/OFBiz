package org.ofbiz.product.feature;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;


/**
 * Services for product features
 *
 * @author     <a href="mailto:schen@graciousstyle.com">Si Chen</a>
 * @version    $Revision$
 * @since      3.0
 */

public class ProductFeatureServices {

    public static final String module = ProductFeatureServices.class.getName();
    public static final String resource = "ProductUiLabels";
    
    /*
     * Parameters: productFeatureCategoryId, productId, productFeatureApplTypeId
     * Result: productFeaturesByType, a Map of all product features from productFeatureCategoryId, group by productFeatureType -> List of productFeatures
     * If the parameter were productFeatureCategoryId, the results are from ProductFeatures.  If productFeatureCategoryId were null and there were a productId,
     * the results are from ProductFeatureAndAppl.
     * The optional productFeatureApplTypeId causes results to be filtered by this parameter--only used in conjunction with productId.
     */
    public static Map getProductFeaturesByType(DispatchContext dctx, Map context) {
		Map results = new HashMap();
		Map featuresByType = new LinkedHashMap();		// a LinkedHashMap preserves order of keys and hence sequence of featureTypeIds
		GenericDelegator delegator = dctx.getDelegator();

		/* because we might need to search either for product features or for product features of a product, the search code has to be generic.
		 * we will determine which entity and field to search on based on what the user has supplied us with.
		 */
		String valueToSearch = (String) context.get("productFeatureCategoryId");
		String productFeatureApplTypeId = (String) context.get("productFeatureApplTypeId");
		
		String entityToSearch = "ProductFeature";
		String fieldToSearch = "productFeatureCategoryId";
		List orderBy = UtilMisc.toList("productFeatureTypeId", "description");
		
		if (valueToSearch == null) {
			entityToSearch = "ProductFeatureAndAppl";
			fieldToSearch = "productId";
			valueToSearch = (String) context.get("productId");
			orderBy = UtilMisc.toList("sequenceNum", "productFeatureApplTypeId", "productFeatureTypeId", "description");
		}
		
		if (valueToSearch == null)
			return ServiceUtil.returnError("This service requires either a productId or a productFeatureCategoryId to run.");
		
		try {
			// get all product features in this feature category
			List allFeatures = delegator.findByAnd(entityToSearch, UtilMisc.toMap(fieldToSearch, valueToSearch), orderBy);
		
			if (entityToSearch.equals("ProductFeatureAndAppl") && productFeatureApplTypeId != null)
				allFeatures = EntityUtil.filterByAnd(allFeatures, UtilMisc.toMap("productFeatureApplTypeId", productFeatureApplTypeId));
				
			/* Returns an iterator for distinct productFeaturetypeIds.  Find from ProductFeature where productFeatureCategoryId = <productFeatureCategoryId
			 * The List specifies field to retrieve.  The first null is for "having condition," the second one for "order by."  The EntityFindOptions's
			 * last parameter is for distinct.
			 */
			EntityListIterator featureTypesIterator = delegator.findListIteratorByCondition(entityToSearch, 
					new EntityExpr(fieldToSearch, EntityOperator.EQUALS, valueToSearch),
					null, UtilMisc.toList("productFeatureTypeId"), UtilMisc.toList("productFeatureTypeId"),
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
			
			// loop through each product feature type and get those features which are of this type.  Put it into the map with this feature type id.
			// only do this for feature types which have some features (this checking is only meaningful for productId and productFeatureApplTypeId parameters)
			
			while (featureTypesIterator.hasNext()) {
				String nextFeatureType = ((GenericEntity) featureTypesIterator.next()).getString("productFeatureTypeId");
				List possibleFeatures = EntityUtil.filterByAnd(allFeatures, UtilMisc.toMap("productFeatureTypeId", nextFeatureType));
				if (possibleFeatures != null && possibleFeatures.size() > 0)
					featuresByType.put(nextFeatureType, possibleFeatures);
			}
			
			featureTypesIterator.close(); 	// it has a database connection which should be closed when done
			results = ServiceUtil.returnSuccess();
			results.put("productFeaturesByType", featuresByType);
		} catch (GenericEntityException ex) {
			Debug.logError(ex, ex.getMessage(), module);
            return ServiceUtil.returnError(ex.getMessage());
		}
		return results;
    }
    
    /*
     * Parameter: productId, productFeatureAppls (a List of ProductFeatureAndAppl entities of features applied to productId)
     * Result: variantProductIds: a List of productIds of variants with those features
     */
    public static Map getAllExistingVariants(DispatchContext dctx, Map context) {
		Map results = new HashMap();
		Map featuresByType = new HashMap();
		GenericDelegator delegator = dctx.getDelegator();

		String productId = (String) context.get("productId");
		List curProductFeatureAndAppls = (List) context.get("productFeatureAppls");
		List existingVariantProductIds = new ArrayList();
		
		try {
			/*
			 * get a list of all products which are associated with the current one as PRODUCT_VARIANT and for each one, 
			 * see if it has every single feature in the list of productFeatureAppls as a STANDARD_FEATURE.  If so, then 
			 * it qualifies and add it to the list of existingVariantProductIds.
			 */
			List productAssocs = EntityUtil.filterByDate(delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT")), true);
		    if (productAssocs != null && productAssocs.size() > 0) {
		        Iterator productAssocIter = productAssocs.iterator();
		        while (productAssocIter.hasNext()) {
		            GenericEntity productAssoc = (GenericEntity) productAssocIter.next();
		            
		            //for each associated product, if it has all standard features, display it's productId
		            boolean hasAllFeatures = true;
		            Iterator curProductFeatureAndApplIter = curProductFeatureAndAppls.iterator();
		            while (curProductFeatureAndApplIter.hasNext()) {
		                GenericEntity productFeatureAndAppl = (GenericEntity) curProductFeatureAndApplIter.next();
		                Map findByMap = UtilMisc.toMap("productId", productAssoc.getString("productIdTo"), 
		                        "productFeatureTypeId", productFeatureAndAppl.get("productFeatureTypeId"),
		                        "description", productFeatureAndAppl.get("description"),
		                        "productFeatureApplTypeId", "STANDARD_FEATURE");
		                //Debug.log("Using findByMap: " + findByMap);

		                List standardProductFeatureAndAppls = EntityUtil.filterByDate(delegator.findByAnd("ProductFeatureAndAppl", findByMap), true);
		                if (standardProductFeatureAndAppls == null || standardProductFeatureAndAppls.size() == 0) {
		                    // Debug.log("Does NOT have this standard feature");
		                    hasAllFeatures = false;
		                    break;
		                } else {
		                    // Debug.log("DOES have this standard feature");
		                }
		            }

		            if (hasAllFeatures) {
		            	// add to list of existing variants: productId=productAssoc.productIdTo
		            	existingVariantProductIds.add(productAssoc.get("productIdTo"));
		            }
		        }
		    }
		    results = ServiceUtil.returnSuccess();
		    results.put("variantProductIds", existingVariantProductIds);
		} catch (GenericEntityException ex) {
			Debug.logError(ex, ex.getMessage(), module);
            return ServiceUtil.returnError(ex.getMessage());
		}
	return results;
    }

    /*
     * Parameter: productId (of the parent product which has SELECTABLE features)
     * Result: featureCombinations, a List of Maps containing, for each possible variant of the productid: 
     * {defaultVariantProductId: id of this variant; curProductFeatureAndAppls: features applied to this variant; existingVariantProductIds: List of productIds which are already variants with these features }
     */
    public static Map getVariantCombinations(DispatchContext dctx, Map context) {
		Map results = new HashMap();
		Map featuresByType = new HashMap();
		GenericDelegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		
		String productId = (String) context.get("productId");
		
		try {
			Map featuresResults = dispatcher.runSync("getProductFeaturesByType", UtilMisc.toMap("productId", productId));
			Map features = new HashMap();
			
			if (featuresResults.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS))
				features = (Map) featuresResults.get("productFeaturesByType"); 
			else
				return ServiceUtil.returnError((String) featuresResults.get(ModelService.ERROR_MESSAGE_LIST));

			// need to keep 2 lists, oldCombinations and newCombinations, and keep swapping them after each looping.  Otherwise, you'll get a 
			// concurrent modification exception
			List oldCombinations = new LinkedList();

			// loop through each feature type
			for (Iterator fi = features.keySet().iterator(); fi.hasNext(); ) {
				String currentFeatureType = (String) fi.next();
				List currentFeatures = (List) features.get(currentFeatureType);

				List newCombinations = new LinkedList();
				List combinations;

				// start with either existing combinations or from scratch
				if (oldCombinations.size() > 0) 
					combinations = oldCombinations;
				else
					combinations = new LinkedList();

				// in both cases, use each feature of current feature type's idCode and
				// product feature and add it to the id code and product feature applications
				// of the next variant.  just a matter of whether we're starting with an
				// existing list of features and id code or from scratch.
				if (combinations.size()==0) {
				   for (Iterator cFi = currentFeatures.iterator(); cFi.hasNext(); ) {
				   		GenericEntity currentFeature = (GenericEntity) cFi.next();
				   		if (currentFeature.getString("productFeatureApplTypeId").equals("SELECTABLE_FEATURE")) {
				   			Map newCombination = new HashMap();
				   			List newFeatures = new LinkedList();
				   			if (currentFeature.getString("idCode") != null)
								newCombination.put("defaultVariantProductId", productId + currentFeature.getString("idCode"));
							else
								newCombination.put("defaultVariantProductId", productId);
							newFeatures.add(currentFeature);
							newCombination.put("curProductFeatureAndAppls", newFeatures);
							newCombinations.add(newCombination);
					   }
				   }
				} else {
			      for (Iterator comboIt = combinations.iterator(); comboIt.hasNext(); ) {
				      	Map combination = (Map) comboIt.next();
					  	for (Iterator cFi = currentFeatures.iterator(); cFi.hasNext(); ) {
					  		GenericEntity currentFeature = (GenericEntity) cFi.next();
					  		if (currentFeature.getString("productFeatureApplTypeId").equals("SELECTABLE_FEATURE")) {
							      Map newCombination = new HashMap();
							      // .clone() is important, or you'll keep adding to the same List for all the variants
							      // have to cast twice: once from get() and once from clone()
							      List newFeatures = ((List) ((LinkedList) combination.get("curProductFeatureAndAppls")).clone());
							      if (currentFeature.getString("idCode") != null)
							      		newCombination.put("defaultVariantProductId", combination.get("defaultVariantProductId") + currentFeature.getString("idCode"));
							      else
							      		newCombination.put("defaultVariantProductId", combination.get("defaultVariantProductId"));
							      newFeatures.add(currentFeature);
							      newCombination.put("curProductFeatureAndAppls", newFeatures);
							      newCombinations.add(newCombination);
						      }
						  }
				      }
			    }
				if (newCombinations.size() >= oldCombinations.size())
					oldCombinations = newCombinations;	// save the newly expanded list as oldCombinations
			}
			
			// now figure out which of these combinations already have productIds associated with them
			for (Iterator fCi = oldCombinations.iterator(); fCi.hasNext(); ) {
			    Map combination = (Map) fCi.next();
			    results = dispatcher.runSync("getAllExistingVariants", UtilMisc.toMap("productId", productId, 
											"productFeatureAppls", combination.get("curProductFeatureAndAppls")));
			    combination.put("existingVariantProductIds", results.get("variantProductIds"));
			}
		    results = ServiceUtil.returnSuccess();
		    results.put("featureCombinations", oldCombinations);
		} catch (GenericServiceException ex) {
			Debug.logError(ex, ex.getMessage(), module);
            return ServiceUtil.returnError(ex.getMessage());
		}
		
		return results;
    }
}