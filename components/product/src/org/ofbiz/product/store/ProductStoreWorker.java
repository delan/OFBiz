/*
 * $Id$
 *
 *  Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.product.store;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.common.geo.GeoWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

/**
 * ProductStoreWorker - Worker class for store related functionality
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class ProductStoreWorker {

    public static final String module = ProductStoreWorker.class.getName();

    public static GenericValue getProductStore(String productStoreId, GenericDelegator delegator) {
        if (productStoreId == null || delegator == null) {
            return null;
        }
        GenericValue productStore = null;
        try {
            productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStore entity", module);
        }
        return productStore;
    }

    public static GenericValue getProductStore(ServletRequest request) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        return ProductStoreWorker.getProductStore(productStoreId, delegator);
    }

    public static String getProductStoreId(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession();
        if (session.getAttribute("productStoreId") != null) {
            return (String) session.getAttribute("productStoreId");
        } else {
            GenericValue webSite = CatalogWorker.getWebSite(request);
            if (webSite != null) {
                return webSite.getString("productStoreId");
            }
        }
        return null;
    }

    public static String makeProductStoreOrderId(GenericDelegator delegator, String productStoreId) {
        return makeProductStoreOrderId(delegator, productStoreId, delegator.getNextSeqId("OrderHeader"));
    }

    public static String makeProductStoreOrderId(GenericDelegator delegator, String productStoreId, String orderId) {
        if (UtilValidate.isEmpty(orderId) || UtilValidate.isEmpty(productStoreId) || delegator == null) {
            throw new IllegalArgumentException();
        }

        GenericValue store = getProductStore(productStoreId, delegator);
        String prefix = store.getString("orderNumberPrefix");
        if (!UtilValidate.isEmpty(prefix)) {
            return prefix.trim() + orderId.trim();
        } else {
            return orderId.trim();
        }
    }

    public static boolean autoSaveCart(GenericDelegator delegator, String productStoreId) {
        return autoSaveCart(getProductStore(productStoreId, delegator));    
    }

    public static boolean autoSaveCart(GenericValue productStore) {
        return productStore == null ? false : "Y".equalsIgnoreCase(productStore.getString("autoSaveCart"));
    }

    public static String getProductStorePaymentProperties(ServletRequest request, String paymentMethodTypeId, String paymentServiceTypeEnumId, boolean anyServiceType) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        return ProductStoreWorker.getProductStorePaymentProperties(delegator, productStoreId, paymentMethodTypeId, paymentServiceTypeEnumId, anyServiceType);
    }

    public static String getProductStorePaymentProperties(GenericDelegator delegator, String productStoreId, String paymentMethodTypeId, String paymentServiceTypeEnumId, boolean anyServiceType) {
        GenericValue setting = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStoreId, paymentMethodTypeId, paymentServiceTypeEnumId, anyServiceType);

        String payProps = "payment.properties";
        if (setting != null && setting.get("paymentPropertiesPath") != null) {
            payProps =  setting.getString("paymentPropertiesPath");
        }
        return payProps;
    }

    public static GenericValue getProductStorePaymentSetting(GenericDelegator delegator, String productStoreId, String paymentMethodTypeId, String paymentServiceTypeEnumId, boolean anyServiceType) {
        GenericValue storePayment = null;
        try {
            storePayment = delegator.findByPrimaryKeyCache("ProductStorePaymentSetting", UtilMisc.toMap("productStoreId", productStoreId, "paymentMethodTypeId", paymentMethodTypeId, "paymentServiceTypeEnumId", paymentServiceTypeEnumId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up store payment settings", module);
        }

        if (anyServiceType) {
            if (storePayment == null) {
                try {
                    List storePayments = delegator.findByAnd("ProductStorePaymentSetting", UtilMisc.toMap("productStoreId", productStoreId, "paymentMethodTypeId", paymentMethodTypeId));
                    storePayment = EntityUtil.getFirst(storePayments);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems looking up store payment settings", module);
                }
            }

            if (storePayment == null) {
                try {
                    List storePayments = delegator.findByAnd("ProductStorePaymentSetting", UtilMisc.toMap("productStoreId", productStoreId));
                    storePayment = EntityUtil.getFirst(storePayments);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems looking up store payment settings", module);
                }
            }
        }

        return storePayment;
    }

    public static GenericValue getProductStoreShipmentMethod(GenericDelegator delegator, String productStoreId,
            String shipmentMethodTypeId, String carrierPartyId, String carrierRoleTypeId) {
        // check for an external service call
        Map storeFields = UtilMisc.toMap("productStoreId", productStoreId, "shipmentMethodTypeId", shipmentMethodTypeId,
                "partyId", carrierPartyId, "roleTypeId", carrierRoleTypeId);

        GenericValue storeShipMeth = null;
        try {
            storeShipMeth = delegator.findByPrimaryKeyCache("ProductStoreShipmentMeth", storeFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        return storeShipMeth;
    }

    public static List getAvailableStoreShippingMethods(GenericDelegator delegator, String productStoreId, GenericValue shippingAddress, List itemSizes, Map featureIdMap, double weight, double orderTotal) {
        if (featureIdMap == null) {
            featureIdMap = new HashMap();
        }
        List shippingMethods = null;
        try {
            shippingMethods = delegator.findByAndCache("ProductStoreShipmentMethView", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("sequenceNumber"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get ProductStore shipping methods", module);
            return null;
        }

        // clone the list for concurrent modification
        List returnShippingMethods = new LinkedList(shippingMethods);

        if (shippingMethods != null) {
            Iterator i = shippingMethods.iterator();
            while (i.hasNext()) {
                GenericValue method = (GenericValue) i.next();
                //Debug.logInfo("Checking Shipping Method : " + method.getString("shipmentMethodTypeId"), module);

                // test min/max weight first
                Double minWeight = method.getDouble("minWeight");
                Double maxWeight = method.getDouble("maxWeight");
                if (minWeight != null && minWeight.doubleValue() > 0 && minWeight.doubleValue() > weight) {
                    returnShippingMethods.remove(method);
                    //Debug.logInfo("Removed shipping method due to not enough weight", module);
                    continue;
                }
                if (maxWeight != null && maxWeight.doubleValue() > 0 && maxWeight.doubleValue() < weight) {
                    returnShippingMethods.remove(method);
                    //Debug.logInfo("Removed shipping method due to too much weight", module);
                    continue;
                }

                // test order total
                Double minTotal = method.getDouble("minTotal");
                Double maxTotal = method.getDouble("maxTotal");
                if (minTotal != null && minTotal.doubleValue() > 0 && minTotal.doubleValue() > orderTotal) {
                    returnShippingMethods.remove(method);
                    //Debug.logInfo("Removed shipping method due to not enough order total", module);
                    continue;
                }
                if (maxTotal != null && maxTotal.doubleValue() > 0 && maxTotal.doubleValue() < orderTotal) {
                    returnShippingMethods.remove(method);
                    //Debug.logInfo("Removed shipping method due to too much shipping total", module);
                    continue;
                }

                // test product sizes
                Double minSize = method.getDouble("minSize");
                Double maxSize = method.getDouble("maxSize");
                if (minSize != null && minSize.doubleValue() > 0) {
                    boolean allMatch = false;
                    if (itemSizes != null) {
                        allMatch = true;
                        Iterator isi = itemSizes.iterator();
                        while (isi.hasNext()) {
                            Double size = (Double) isi.next();
                            if (size.doubleValue() < minSize.doubleValue()) {
                                allMatch = false;
                            }
                        }
                    }
                    if (!allMatch) {
                        returnShippingMethods.remove(method);
                        //Debug.logInfo("Removed shipping method because not all products are less then min size", module);
                        continue;
                    }
                }
                if (maxSize != null && maxSize.doubleValue() > 0) {
                    boolean allMatch = false;
                    if (itemSizes != null) {
                        allMatch = true;
                        Iterator isi = itemSizes.iterator();
                        while (isi.hasNext()) {
                            Double size = (Double) isi.next();
                            if (size.doubleValue() > maxSize.doubleValue()) {
                                allMatch = false;
                            }
                        }
                    }
                    if (!allMatch) {
                        returnShippingMethods.remove(method);
                        //Debug.logInfo("Removed shipping method because one or more products were more then max size", module);
                        continue;
                    }
                }

                // check USPS address
                String allowUspsAddr = method.getString("allowUspsAddr");
                String requireUspsAddr = method.getString("requireUspsAddr");
                boolean isUspsAddress = ContactMechWorker.isUspsAddress(shippingAddress);
                if ("N".equals(allowUspsAddr) && isUspsAddress) {
                    returnShippingMethods.remove(method);
                    //Debug.logInfo("Remove shipping method due to USPS address", module);
                    continue;
                }
                if ("Y".equals(requireUspsAddr) && !isUspsAddress) {
                    returnShippingMethods.remove(method);
                    //Debug.logInfo("Removed shipping method due to NON-USPS address", module);
                    continue;
                }

                // check company address
                String companyPartyId = method.getString("companyPartyId");
                String allowCompanyAddr = method.getString("allowCompanyAddr");
                String requireCompanyAddr = method.getString("requireCompanyAddr");
                boolean isCompanyAddress = ContactMechWorker.isCompanyAddress(shippingAddress, companyPartyId);
                if ("N".equals(allowCompanyAddr) && isCompanyAddress) {
                    returnShippingMethods.remove(method);
                    //Debug.logInfo("Removed shipping method due to Company address", module);
                    continue;
                }
                if ("Y".equals(requireCompanyAddr) && !isCompanyAddress) {
                    returnShippingMethods.remove(method);
                    //Debug.logInfo("Removed shipping method due to NON-Company address", module);
                    continue;
                }

                // check the items excluded from shipping
                String includeFreeShipping = method.getString("includeNoChargeItems");
                if (includeFreeShipping != null && "N".equalsIgnoreCase(includeFreeShipping)) {
                    if ((itemSizes == null || itemSizes.size() == 0) && orderTotal == 0) {
                        returnShippingMethods.remove(method);
                        //Debug.logInfo("Removed shipping method due to all items being exempt from shipping", module);
                        continue;
                    }
                }

                // check the geos
                String includeGeoId = method.getString("includeGeoId");
                String excludeGeoId = method.getString("excludeGeoId");
                if ((includeGeoId != null && includeGeoId.length() > 0) || (excludeGeoId != null && excludeGeoId.length() > 0)) {
                    if (shippingAddress == null) {
                        returnShippingMethods.remove(method);
                        //Debug.logInfo("Removed shipping method due to empty shipping adresss (may not have been selected yet)", module);
                        continue;
                    }
                }
                if (includeGeoId != null && includeGeoId.length() > 0) {
                    List includeGeoGroup = GeoWorker.expandGeoGroup(includeGeoId, delegator);
                    if (!GeoWorker.containsGeo(includeGeoGroup, shippingAddress.getString("countryGeoId"), delegator) &&
                            !GeoWorker.containsGeo(includeGeoGroup, shippingAddress.getString("stateProvinceGeoId"), delegator) &&
                            !GeoWorker.containsGeo(includeGeoGroup, shippingAddress.getString("postalCodeGeoId"), delegator)) {
                        // not in required included geos
                        returnShippingMethods.remove(method);
                        //Debug.logInfo("Removed shipping method due to being outside the included GEO", module);
                        continue;
                    }
                }
                if (excludeGeoId != null && excludeGeoId.length() > 0) {
                    List excludeGeoGroup = GeoWorker.expandGeoGroup(excludeGeoId, delegator);
                    if (GeoWorker.containsGeo(excludeGeoGroup, shippingAddress.getString("countryGeoId"), delegator) ||
                            GeoWorker.containsGeo(excludeGeoGroup, shippingAddress.getString("stateProvinceGeoId"), delegator) ||
                            GeoWorker.containsGeo(excludeGeoGroup, shippingAddress.getString("postalCodeGeoId"), delegator)) {
                        // in excluded geos
                        returnShippingMethods.remove(method);
                        //Debug.logInfo("Removed shipping method due to being inside the excluded GEO", module);
                        continue;
                    }
                }

                // check the features
                String includeFeatures = method.getString("includeFeatureGroup");
                String excludeFeatures = method.getString("excludeFeatureGroup");
                if (includeFeatures != null && includeFeatures.length() > 0) {
                    List includedFeatures = null;
                    try {
                        includedFeatures = delegator.findByAndCache("ProductFeatureGroupAppl", UtilMisc.toMap("productFeatureGroupId", includeFeatures));
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Unable to lookup ProductFeatureGroupAppl records for group : " + includeFeatures, module);
                    }
                    if (includedFeatures != null) {
                        boolean foundOne = false;
                        Iterator ifet = includedFeatures.iterator();
                        while (ifet.hasNext()) {
                            GenericValue appl = (GenericValue) ifet.next();
                            if (featureIdMap.containsKey(appl.getString("productFeatureId"))) {
                                foundOne = true;
                                break;
                            }
                        }
                        if (!foundOne) {
                            returnShippingMethods.remove(method);
                            //Debug.logInfo("Removed shipping method due to no required features found", module);
                            continue;
                        }
                    }
                }
                if (excludeFeatures != null && excludeFeatures.length() > 0) {
                    List excludedFeatures = null;
                    try {
                        excludedFeatures = delegator.findByAndCache("ProductFeatureGroupAppl", UtilMisc.toMap("productFeatureGroupId", excludeFeatures));
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Unable to lookup ProductFeatureGroupAppl records for group : " + excludeFeatures, module);
                    }
                    if (excludedFeatures != null) {
                        Iterator ifet = excludedFeatures.iterator();
                        while (ifet.hasNext()) {
                            GenericValue appl = (GenericValue) ifet.next();
                            if (featureIdMap.containsKey(appl.getString("productFeatureId"))) {
                                returnShippingMethods.remove(method);
                                //Debug.logInfo("Removed shipping method due to an exluded feature being found : " + appl.getString("productFeatureId"), module);
                                continue;
                            }
                        }
                    }
                }
            }
        }

        return returnShippingMethods;
    }

    public static ProductStoreSurveyWrapper getRandomSurveyWrapper(ServletRequest request, String groupName) {
        GenericValue productStore = getProductStore(request);
        HttpSession session = ((HttpServletRequest)request).getSession();
        if (productStore == null) {
            return null;
        }

        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (userLogin == null) {
            userLogin = (GenericValue) session.getAttribute("autoUserLogin");
        }

        String partyId = userLogin != null ? userLogin.getString("partyId") : null;
        Map passThruFields = UtilHttp.getParameterMap(((HttpServletRequest)request));

        return getRandomSurveyWrapper(productStore.getDelegator(), productStore.getString("productStoreId"), groupName, partyId, passThruFields);
    }

    public static ProductStoreSurveyWrapper getRandomSurveyWrapper(GenericDelegator delegator, String productStoreId, String groupName, String partyId, Map passThruFields) {
        List randomSurveys = getSurveys(delegator, productStoreId, groupName, null, "RANDOM_POLL");        
        if (!UtilValidate.isEmpty(randomSurveys)) {
            Random rand = new Random();
            int index = rand.nextInt(randomSurveys.size());
            GenericValue appl = (GenericValue) randomSurveys.get(index);
            return new ProductStoreSurveyWrapper(appl, partyId, passThruFields);
        } else {
            return null;
        }
    }

    public static List getProductSurveys(GenericDelegator delegator, String productStoreId, String productId, String surveyApplTypeId) {
        return getSurveys(delegator, productStoreId, null, productId, surveyApplTypeId);
    }

    public static List getSurveys(GenericDelegator delegator, String productStoreId, String groupName, String productId, String surveyApplTypeId) {
        List surveys = new LinkedList();
        List storeSurveys = null;
        try {
            storeSurveys = delegator.findByAndCache("ProductStoreSurveyAppl", UtilMisc.toMap("productStoreId", productStoreId, "surveyApplTypeId", surveyApplTypeId), UtilMisc.toList("sequenceNum"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get ProductStoreSurveyAppl for store : " + productStoreId, module);
        }

        // limit by date
        storeSurveys = EntityUtil.filterByDate(storeSurveys);

        // limit based on group name
        if (!UtilValidate.isEmpty(groupName)) {
            storeSurveys = EntityUtil.filterByAnd(storeSurveys, UtilMisc.toMap("groupName", groupName));
        }

        // limit by product
        if (!UtilValidate.isEmpty(productId) && !UtilValidate.isEmpty(storeSurveys)) {
            Iterator ssi = storeSurveys.iterator();
            while (ssi.hasNext()) {
                GenericValue surveyAppl = (GenericValue) ssi.next();
                if (surveyAppl.get("productId") != null && productId.equals(surveyAppl.get("productId"))) {
                    surveys.add(surveyAppl);
                } else if (surveyAppl.get("productCategoryId") != null) {
                    List categoryMembers = null;
                    try {
                        categoryMembers = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId", surveyAppl.get("productCategoryId")));
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Unable to get ProductCategoryMemebr records for survey application : " + surveyAppl, module);
                    }
                    if (categoryMembers != null) {
                        Iterator cmi = categoryMembers.iterator();
                        while (cmi.hasNext()) {
                            GenericValue member = (GenericValue) cmi.next();
                            if (productId.equals(member.getString("productId"))) {
                                surveys.add(surveyAppl);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            surveys.addAll(storeSurveys);
        }

        return surveys;
    }

    /** Returns the number of responses for this survey by party */
    public static int checkSurveyResponse(HttpServletRequest request, String surveyId) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String productStoreId = getProductStoreId(request);
        if (userLogin == null) {
            return -1;
        }

        return checkSurveyResponse(delegator, userLogin.getString("partyId"), productStoreId, surveyId);
    }

    /** Returns the number of responses for this survey by party */
    public static int checkSurveyResponse(GenericDelegator delegator, String partyId, String productStoreId, String surveyId) {
        if (delegator == null || partyId == null || productStoreId == null) {
            return -1;
        }

        List surveyResponse = null;
        try {
            surveyResponse = delegator.findByAnd("SurveyResponse", UtilMisc.toMap("surveyId", surveyId, "partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return -1;
        }

        if (surveyResponse == null || surveyResponse.size() == 0) {
            return 0;
        } else {
            return surveyResponse.size();
        }
    }

    public static boolean isStoreInventoryRequired(String productStoreId, String productId, GenericDelegator delegator) {
        GenericValue product = null;

        if (productId != null) {
            try {
                product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up product with id " + productId + ", will check the ProdCatalog for inventory required", module);
            }
        }

        return isStoreInventoryRequired(productStoreId, product, delegator);
    }

    public static boolean isStoreInventoryRequired(String productStoreId, GenericValue product, GenericDelegator delegator) {
        // look at the product first since it over-rides the ProductStore setting; if empty or null use the ProductStore setting

        if (product != null && UtilValidate.isNotEmpty(product.getString("requireInventory"))) {
            if ("Y".equals(product.getString("requireInventory"))) {
                return true;
            } else if ("N".equals(product.getString("requireInventory"))) {
                return false;
            }
        }
        // otherwise, check the store...

        GenericValue productStore = getProductStore(productStoreId, delegator);

        if (productStore == null) {
            Debug.logWarning("ProductStore not found with id " + productStoreId + ", returning false for inventory required check", module);
            return false;
        }

        // default to false, so if anything but Y, return false
        return "Y".equals(productStore.getString("requireInventory"));
    }

    public static boolean isStoreInventoryRequired(ServletRequest request, GenericValue product) {
        GenericValue productStore = getProductStore(request);

        if (productStore == null) {
            Debug.logWarning("No ProductStore found, return false for inventory check", module);
            return false;
        }

        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        return isStoreInventoryRequired(productStore.getString("productStoreId"), product, delegator);
    }

    /** check inventory availability for the given catalog, product, quantity, etc */
    public static boolean isStoreInventoryAvailable(String productStoreId, String productId, double quantity, GenericDelegator delegator, LocalDispatcher dispatcher) {
        GenericValue productStore = getProductStore(productStoreId, delegator);

        if (productStore == null) {
            Debug.logWarning("No ProductStore found with id " + productStoreId + ", returning false for inventory available check", module);
            return false;
        }

        // if prodCatalog is set to not check inventory break here
        if ("N".equals(productStore.getString("checkInventory"))) {
            // note: if not set, defaults to yes, check inventory
            if (Debug.verboseOn()) Debug.logVerbose("ProductStore with id " + productStoreId + ", is set to NOT check inventory, returning true for inventory available check", module);
            return true;
        }
        boolean isInventoryAvailable = false;

        if ("Y".equals(productStore.getString("oneInventoryFacility"))) {
            String inventoryFacilityId = productStore.getString("inventoryFacilityId");

            if (UtilValidate.isEmpty(inventoryFacilityId)) {
                Debug.logWarning("ProductStore with id " + productStoreId + " has Y for oneInventoryFacility but inventoryFacilityId is empty, returning false for inventory check", module);
                return false;
            }

            try {
                isInventoryAvailable = ProductWorker.isProductInventoryAvailableByFacility(productId, inventoryFacilityId, quantity, dispatcher);
            } catch (GenericServiceException e) {
                Debug.logWarning(e, "Error invoking isProductInventoryAvailableByFacility in isCatalogInventoryAvailable", module);
                return false;
            }
            return isInventoryAvailable;
            
        } else {
            GenericValue product = null;
            List productFacilities = null;

            try {
                product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Error invoking findByPrimaryKeyCache in isCatalogInventoryAvailable", module);
                return false;
            }
            try {
                productFacilities = delegator.getRelatedCache("ProductFacility", product);
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Error invoking getRelatedCache in isCatalogInventoryAvailable", module);
                return false;
            }

            if (productFacilities != null && productFacilities.size() > 0) {
                Iterator pfIter = productFacilities.iterator();

                while (pfIter.hasNext()) {
                    try {
                        GenericValue pfValue = (GenericValue) pfIter.next();

                        isInventoryAvailable = ProductWorker.isProductInventoryAvailableByFacility(productId, pfValue.getString("facilityId"), quantity, dispatcher);
                        if (isInventoryAvailable == true) {
                            return isInventoryAvailable;
                        }
                    } catch (GenericServiceException e) {
                        Debug.logWarning(e, "Error invoking isProductInventoryAvailableByFacility in isCatalogInventoryAvailable", module);
                        return false;
                    }
                }
            }
            return false;

            /* TODO: must entire quantity be available in one location?
             *  Right now the answer is yes, it only succeeds if one facility has sufficient inventory for the order.
             *  When we get into splitting options it is much more complicated. There are various options like: 
             *  - allow split between facilities
             *  - in split order facilities by highest quantities
             *  - in split order facilities by lowest quantities
             *  - in split order facilities by order in database, ie sequence numbers on facility-store join table
             *  - in split order facilities by nearest locations to customer (not an easy one there...)
             */

            // loop through all facilities attached to this catalog and check for individual or cumulative sufficient inventory
        }
    }

    public static boolean isStoreInventoryAvailable(ServletRequest request, String productId, double quantity) {
        GenericValue productStore = getProductStore(request);

        if (productStore == null) {
            Debug.logWarning("No ProductStore found, return false for inventory check", module);
            return false;
        }

        String productStoreId = productStore.getString("productStoreId");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        return isStoreInventoryAvailable(productStoreId, productId, quantity, delegator, dispatcher);
    }

    /** tries to reserve the specified quantity, if fails returns quantity that it could not reserve or zero if there was an error, otherwise returns null */
    public static Double reserveStoreInventory(String productStoreId, String productId, Double quantity,
            String orderId, String orderItemSeqId, GenericValue userLogin, GenericDelegator delegator, LocalDispatcher dispatcher) {

        GenericValue productStore = getProductStore(productStoreId, delegator);

        if (productStore == null) {
            Debug.logWarning("No ProductStore found with id " + productStoreId + ", not reserving inventory", module);
            return new Double(0.0);
        }

        // if prodCatalog is set to not reserve inventory, break here
        if ("N".equals(productStore.getString("reserveInventory"))) {
            // note: if not set, defaults to yes, reserve inventory
            if (Debug.verboseOn()) Debug.logVerbose("ProductStore with id " + productStoreId + ", is set to NOT reserve inventory, not reserving inventory", module);
            return null;
        }
        String reserveOrderEnumId = productStore.getString("reserveOrderEnumId");
        boolean requireInventory = isStoreInventoryRequired(productStoreId, productId, delegator);
        Double quantityNotReserved = null;

        if ("Y".equals(productStore.getString("oneInventoryFacility"))) {
            String inventoryFacilityId = productStore.getString("inventoryFacilityId");

            if (UtilValidate.isEmpty(inventoryFacilityId)) {
                Debug.logWarning("ProductStore with id " + productStoreId + " has Y for oneInventoryFacility but inventoryFacilityId is empty, not reserving inventory", module);
                return new Double(0.0);
            }

            try {
                quantityNotReserved = ProductWorker.reserveProductInventoryByFacility(productId, quantity, inventoryFacilityId, orderId, reserveOrderEnumId, orderItemSeqId, requireInventory, userLogin, dispatcher);
            } catch (GenericServiceException e) {
                Debug.logWarning(e, "Error invoking reserveProductInventoryByFacility service", module);
                return !requireInventory? null: new Double(0.0);
            }
            return quantityNotReserved;
            
        } else {
            GenericValue product = null;
            List productFacilities = null;

            try {
                product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Error invoking findByPrimaryKeyCache in reserveCatalogInventory", module);
                return new Double(0.0);
            }
            try {
                productFacilities = delegator.getRelatedCache("ProductFacility", product);
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Error invoking getRelatedCache in reserveCatalogInventory", module);
                return new Double(0.0);
            }

            if (productFacilities != null && productFacilities.size() > 0) {
                Iterator pfIter = productFacilities.iterator();

                while (pfIter.hasNext()) {
                    GenericValue pfValue = (GenericValue) pfIter.next();
                    String inventoryFacilityId = pfValue.getString("facilityId");

                    try {
                        // TODO: must entire quantity be available in one location?
                        // Right now the answer is yes, it only succeeds if one facility has sufficient inventory for the order.
                        boolean isAvailable = ProductWorker.isProductInventoryAvailableByFacility(productId, inventoryFacilityId, quantity.doubleValue(), dispatcher);
                        if (!isAvailable) continue;

                        quantityNotReserved = ProductWorker.reserveProductInventoryByFacility(productId, quantity, inventoryFacilityId, orderId, reserveOrderEnumId, orderItemSeqId, requireInventory, userLogin, dispatcher);
                    } catch (GenericServiceException e) {
                        Debug.logWarning(e, "Error invoking reserveProductInventoryByFacility in reserveCatalogInventory", module);
                        return !requireInventory? null: new Double(0.0);
                    }
                    if (quantityNotReserved == null) {
                        return null;
                    }
                }
                return quantityNotReserved;

            }
            return !requireInventory? null: new Double(0.0);
        }
    }
}